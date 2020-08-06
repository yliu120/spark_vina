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

*/

#include <memory>
#include <vector>

#include "execution_context.h"
#include "lock_free_thread_pool.h"
#include "parallel_mc.h"
#include "task.h"
#include "coords.h"

#include "absl/algorithm/container.h"
#include "absl/memory/memory.h"

using ::vina::DockTask;
using ::vina::ExecutionContext;
using ::vina::ExecutionContextInput;
using ::vina::LockFreeThreadPool;
using ::vina::Task;

struct parallel_mc_task {
	model m;
	output_container out;
	rng generator;
	parallel_mc_task(const model& m_, int seed) : m(m_), generator(static_cast<rng::result_type>(seed)) {}
};

struct parallel_mc_aux {
  const monte_carlo* mc;
  const precalculate* p;
  const igrid* ig;
  const precalculate* p_widened;
  const igrid* ig_widened;
  const vec* corner1;
  const vec* corner2;
  parallel_mc_aux(const monte_carlo* mc_, const precalculate* p_,
                  const igrid* ig_, const precalculate* p_widened_,
                  const igrid* ig_widened_, const vec* corner1_,
                  const vec* corner2_)
      : mc(mc_),
        p(p_),
        ig(ig_),
        p_widened(p_widened_),
        ig_widened(ig_widened_),
        corner1(corner1_),
        corner2(corner2_) {}
  void operator()(parallel_mc_task& t) const {
    (*mc)(t.m, t.out, *p, *ig, *p_widened, *ig_widened, *corner1, *corner2,
          t.generator);
  }
};

void merge_output_containers(const output_container& in, output_container& out,
                             fl min_rmsd, sz max_size) {
  for (const auto& in_element : in) {
    add_to_output_container(out, *in_element, min_rmsd, max_size);
  }
}

void merge_output_containers(const std::vector<DockTask>& many,
                             output_container& out, fl min_rmsd, sz max_size) {
  min_rmsd = 2;  // FIXME? perhaps it's necessary to separate min_rmsd during
                 // search and during output?
  for (const auto& task : many) {
    merge_output_containers(task.Output(), out, min_rmsd, max_size);
  }
  absl::c_sort(
      out, [](const std::unique_ptr<output_type>& a,
              const std::unique_ptr<output_type>& b) { return a->e < b->e; });
}

void parallel_mc::operator()(const model& m, output_container& out,
                             const precalculate& p, const igrid& ig,
                             const precalculate& p_widened,
                             const igrid& ig_widened, const vec& corner1,
                             const vec& corner2, rng& generator) const {
  const ExecutionContext context(
      ExecutionContextInput{.mc = &mc,
                            .p = &p,
                            .p_widened = &p_widened,
                            .g = &ig,
                            .ig_widened = &ig_widened,
                            .corner1 = &corner1,
                            .corner2 = &corner2});
  std::vector<DockTask> dock_task_storage;
  std::vector<Task*> dock_tasks;
  // By calling reserve(), memory will be fixed as well as
  dock_task_storage.reserve(num_tasks);
  dock_tasks.reserve(num_tasks);
  for (int i = 0; i < num_tasks; i++) {
    auto& task =
        dock_task_storage.emplace_back(m, random_int(0, 1000000, generator));
    dock_tasks.push_back(&task);
  }

  LockFreeThreadPool thread_pool(&context, num_threads);
  thread_pool.RunTasks(absl::MakeSpan(dock_tasks));

  merge_output_containers(dock_task_storage, out, mc.min_rmsd,
                          mc.num_saved_mins);
}
