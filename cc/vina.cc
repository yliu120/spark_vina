/*

   Copyright (c) 2006-2010, The Scripps Research Institute

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Author: Dr. Oleg Trott <ot14@columbia.edu>,
           The Olson Lab,
           The Scripps Research Institute

           Yunlong Liu <davislong198833@gmail.com>
           Amzel Lab,
           JHU
*/

#include "cc/vina.h"

#include <boost/thread/thread.hpp>  // hardware_concurrency // FIXME rm ?
#include <cmath>                    // for ceila
#include <exception>
#include <iostream>
#include <string>
#include <utility>
#include <vector>  // ligand paths

#include "cc/parse_pdbqt.h"
#include "glog/logging.h"
#include "third_party/vina/lib/cache.h"
#include "third_party/vina/lib/coords.h"  // add_to_output_container
#include "third_party/vina/lib/current_weights.h"
#include "third_party/vina/lib/everything.h"
#include "third_party/vina/lib/file.h"
#include "third_party/vina/lib/non_cache.h"
#include "third_party/vina/lib/parallel_mc.h"
#include "third_party/vina/lib/quasi_newton.h"
#include "third_party/vina/lib/tee.h"
#include "third_party/vina/lib/weighted_terms.h"

namespace {

// -0.035579, -0.005156, 0.840245, -0.035069, -0.587439, 0.05846
// These constants are copied from the original vina/main/main.cpp
const double kWeightGauss1 = -0.035579;
const double kWeightGauss2 = -0.005156;
const double kWeightRepulsion = 0.840245;
const double kWeightHydrophobic = -0.035069;
const double kWeightHydrogen = -0.587439;
const double kWeightRot = 0.05846;
const double kGranularity = 0.375;

void refine_structure(model& m, const precalculate& prec, non_cache& nc,
                      output_type& out, const vec& cap, sz max_steps = 1000) {
  change g(m.get_size());
  quasi_newton quasi_newton_par;
  quasi_newton_par.max_steps = max_steps;
  const fl slope_orig = nc.slope;
  VINA_FOR(p, 5) {
    nc.slope = 100 * std::pow(10.0, 2.0 * p);
    quasi_newton_par(m, prec, nc, out, g, cap);
    m.set(out.c);  // just to be sure
    if (nc.within(m)) break;
  }
  out.coords = m.get_heavy_atom_movable_coords();
  if (!nc.within(m)) out.e = max_fl;
  nc.slope = slope_orig;
}

output_container remove_redundant(const output_container& in, fl min_rmsd) {
  output_container tmp;
  VINA_FOR_IN(i, in)
  add_to_output_container(tmp, in[i], min_rmsd, in.size());
  return tmp;
}

VinaResult do_search(model& m, const boost::optional<model>& ref,
                     const scoring_function& sf, const precalculate& prec,
                     const igrid& ig, const precalculate& prec_widened,
                     const igrid& ig_widened,
                     non_cache& nc,  // nc.slope is changed
                     const std::string& out_name, const vec& corner1,
                     const vec& corner2, const parallel_mc& par,
                     fl energy_range, sz num_modes,
                     std::optional<int> seed, const terms& t,
                     const flv& weights) {
  conf_size s = m.get_size();
  conf c = m.get_initial_conf();
  const vec authentic_v(1000, 1000, 1000);

  rng generator(seed.has_value() ? static_cast<rng::result_type>(*seed)
                                 : static_cast<rng::result_type>(
                                       std::random_device("/dev/urandom")()));
  output_container out_cont;
  par(m, out_cont, prec, ig, prec_widened, ig_widened, corner1, corner2,
      generator);

  VINA_FOR_IN(i, out_cont)
  refine_structure(m, prec, nc, out_cont[i], authentic_v, par.mc.ssd_par.evals);

  if (!out_cont.empty()) {
    out_cont.sort();
    const fl best_mode_intramolecular_energy =
        m.eval_intramolecular(prec, authentic_v, out_cont[0].c);

    VINA_FOR_IN(i, out_cont)
    if (not_max(out_cont[i].e))
      out_cont[i].e = m.eval_adjusted(sf, prec, nc, authentic_v, out_cont[i].c,
                                      best_mode_intramolecular_energy);
    // the order must not change because of non-decreasing g (see paper), but
    // we'll re-sort in case g is non strictly increasing
    out_cont.sort();
  }

  const fl out_min_rmsd = 1;
  out_cont = remove_redundant(out_cont, out_min_rmsd);

  model best_mode_model = m;
  if (!out_cont.empty()) best_mode_model.set(out_cont.front().c);

  sz how_many = 0;
  VINA_FOR_IN(i, out_cont) {
    if (how_many >= num_modes || !not_max(out_cont[i].e) ||
        out_cont[i].e > out_cont[0].e + energy_range)
      break;  // check energy_range sanity FIXME
    ++how_many;
  }

  if (out_cont.size() < how_many) {
    how_many = out_cont.size();
  }

  VinaResult result;
  VINA_FOR(i, how_many) {
    m.set(out_cont[i].c);
    VinaResult::Model* model = result.add_models();
    model->set_affinity(out_cont[i].e);
    model->set_docked_pdbqt(m.model_to_string());
  }
  return result;
}

VinaResult main_procedure(
    model& m, const boost::optional<model>& ref,  // m is non-const (FIXME?)
    const std::string& out_name, const grid_dims& gd, int exhaustiveness,
    const flv& weights, int cpu, std::optional<int> seed, sz num_modes, fl energy_range) {
  everything t;
  VINA_CHECK(weights.size() == 6);

  weighted_terms wt(&t, weights);
  precalculate prec(wt);
  const fl left = 0.25;
  const fl right = 0.25;
  precalculate prec_widened(prec);
  prec_widened.widen(left, right);

  vec corner1(gd[0].begin, gd[1].begin, gd[2].begin);
  vec corner2(gd[0].end, gd[1].end, gd[2].end);

  parallel_mc par;
  sz heuristic =
      m.num_movable_atoms() + 10 * m.get_size().num_degrees_of_freedom();
  par.mc.num_steps =
      unsigned(70 * 3 * (50 + heuristic) / 2);  // 2 * 70 -> 8 * 20 // FIXME
  par.mc.ssd_par.evals = unsigned((25 + m.num_movable_atoms()) / 3);
  par.mc.min_rmsd = 1.0;
  par.mc.num_saved_mins = 20;
  par.mc.hunt_cap = vec(10, 10, 10);
  par.num_tasks = exhaustiveness;
  par.num_threads = cpu;

  const fl slope = 1e6;  // FIXME: too large? used to be 100
  non_cache nc(m, gd, &prec,
               slope);  // if gd has 0 n's, this will not constrain anything
  cache c("scoring_function_version001", gd, slope, atom_type::XS);
  c.populate(m, prec, m.get_movable_atom_types(prec.atom_typing_used()));
  return do_search(m, ref, wt, prec, c, prec, c, nc, out_name, corner1, corner2,
                   par, energy_range, num_modes, seed, t, weights);
}

}  // anonymous namespace

std::vector<std::string> read_ligand_to_strings(
    const std::string& ligand_path) {
  return spark_vina::split_multiple_ligands(path(ligand_path));
}

VinaDock::VinaDock(const std::string& receptor_path, double center_x,
                   double center_y, double center_z, double size_x,
                   double size_y, double size_z, int cpu, int num_modes)
    : rigid_name_(receptor_path),
      center_x_(center_x),
      center_y_(center_y),
      center_z_(center_z),
      size_x_(size_x),
      size_y_(size_y),
      size_z_(size_z),
      cpu_(cpu),
      num_modes_(num_modes) {}

std::vector<VinaResult> VinaDock::vina_fit(
    const std::vector<std::string>& ligand_strs, double filter_limit) {
  // Some non-const parameters
  int exhaustiveness = 8.0;
  double energy_range = 3.0;

  // Initialize grid
  grid_dims gd;
  vec span(size_x_, size_y_, size_z_);
  vec center(center_x_, center_y_, center_z_);
  VINA_FOR_IN(i, gd) {
    gd[i].n = sz(std::ceil(span[i] / kGranularity));
    fl real_span = kGranularity * gd[i].n;
    gd[i].begin = center[i] - real_span / 2;
    gd[i].end = gd[i].begin + real_span;
  }

  // Weight Space, original syntax used.
  flv weights;
  weights.push_back(kWeightGauss1);
  weights.push_back(kWeightGauss2);
  weights.push_back(kWeightRepulsion);
  weights.push_back(kWeightHydrophobic);
  weights.push_back(kWeightHydrogen);
  weights.push_back(5.0 * kWeightRot / 0.1 - 1.0);

  // Initialize cpu
  if (cpu_ <= 0) {
    cpu_ = 1;
  }

  std::vector<VinaResult> results;

  // Keep the try...catch... logic even though it is stupid.
  // Since a lot of error is thrown in the libs of vina.
  try {
    model receptor = spark_vina::parse_receptor_pdbqt(path(rigid_name_));
    std::vector<std::pair<int, model>> ligand_models =
        spark_vina::parse_ligand_pdbqt(ligand_strs);

    for (const auto& ligand_model : ligand_models) {
      model system = receptor;
      system.append(ligand_model.second);
      boost::optional<model> ref;

      VinaResult result;
      try {
        result =
            main_procedure(system, ref, "", gd, exhaustiveness, weights, cpu_,
                           seed_, static_cast<sz>(num_modes_), energy_range);
      } catch (...) {
        // For any kinds of failure, we just give up this ligand.
        continue;
      }

      if (result.models().empty()) {
        continue;
      }

      // filter_limit should always be negative as the Vina score should be
      // negative.
      if (result.models(0).affinity() < filter_limit) {
        result.set_original_pdbqt(ligand_strs[ligand_model.first]);
        results.push_back(std::move(result));
      }
    }
  } catch (...) {
    // In principle, this shouldn't happen.
    std::cerr << "\n\nAn unknown error occurred. Most likely cannot read "
                 "receptor pdbqt.\n";
  }
  return results;
}
