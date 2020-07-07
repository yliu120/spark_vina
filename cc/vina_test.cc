// An c++ example of running VinaDock wrapper class
// Author: Yunlong Liu (davislong198833@gmail.com)
//

#include "cc/vina.h"

#include "gtest/gtest.h"

namespace {

constexpr int kRandomSeed = 12345;

TEST(VinaDockTest, SplitLigandsInFile) {
  std::string ligand_path = "data/ligands/HB/AAMM/HBAAMM.xaa.pdbqt.gz";
  std::vector<std::string> ligand_strs = read_ligand_to_strings(ligand_path);
  EXPECT_EQ(ligand_strs.size(), 2);
}

TEST(VinaDockTest, VinaDock) {
  std::string receptor_path = "data/protein/4ZPH-docking.pdb.pdbqt";
  std::string ligand_path = "data/ligands/HB/AAMM/HBAAMM.xaa.pdbqt.gz";

  std::vector<std::string> ligand_strs = read_ligand_to_strings(ligand_path);
  ASSERT_EQ(ligand_strs.size(), 2);

  VinaDock vina_dock(receptor_path, 170.0, -110.0, -110.0, 10, 10, 10, 4, 4);
  vina_dock.SetRandomSeed(kRandomSeed);
  std::vector<VinaResult> affinities = vina_dock.vina_fit(ligand_strs, 1.0);
  ASSERT_EQ(affinities.size(), 2);

  // Compares the affinities of the first two models of each docking is
  // sufficient for guarding the correctness.
  EXPECT_NEAR(affinities[0].models(0).affinity(), -5.824148, 1e-5);
  EXPECT_NEAR(affinities[0].models(1).affinity(), -5.445939, 1e-5);
  EXPECT_NEAR(affinities[1].models(0).affinity(), -5.583360, 1e-5);
  EXPECT_NEAR(affinities[1].models(1).affinity(), -5.568257, 1e-5);
}

}  // namespace
