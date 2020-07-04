#include "benchmark/benchmark.h"
#include "cc/vina.h"

namespace {
constexpr int kRandomSeed = 12345;
}  // namespace

// Benchmark guards for VinaDock. This doesn't aim at obtaining
// microbenchmark numbers. It is only used for getting ideas how much
// time the current implementation takes to run docking.
static void BM_VinaDock(benchmark::State& state) {
  std::string receptor_path = "data/protein/4ZPH-docking.pdb.pdbqt";
  std::string ligand_path = "data/ligands/HB/AAMM/HBAAMM.xaa.pdbqt.gz";

  std::vector<std::string> ligand_strs = read_ligand_to_strings(ligand_path);
  int num_cpus = state.range(0);
  VinaDock vina_dock(receptor_path, 170.0, -110.0, -110.0, 10, 10, 10, num_cpus,
                     4);
  vina_dock.SetRandomSeed(kRandomSeed);
  for (auto _ : state) {
    benchmark::DoNotOptimize(vina_dock.vina_fit(ligand_strs, 1.0));
  };
}

BENCHMARK(BM_VinaDock)->Arg(1)->Arg(2)->Arg(4);

BENCHMARK_MAIN();