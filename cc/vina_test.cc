// An c++ example of running VinaDock wrapper class
// Author: Yunlong Liu (davislong198833@gmail.com)
//

#include "cc/vina.h"

#include "gtest/gtest.h"

namespace {

TEST(VinaDockTest, SplitLigandsInFile) {
  std::string ligand_path =
      "data/ligands/HB/AAMM/HBAAMM.xaa.pdbqt.gz";
  std::vector<std::string> ligand_strs = read_ligand_to_strings(ligand_path);
  EXPECT_EQ(ligand_strs.size(), 2);
}

TEST(VinaDockTest, VinaDock) {
  std::string receptor_path = "data/protein/4ZPH-docking.pdb.pdbqt";
  std::string ligand_path =
      "data/ligands/HB/AAMM/HBAAMM.xaa.pdbqt.gz";

  std::vector<std::string> ligand_strs = read_ligand_to_strings(ligand_path);
  EXPECT_EQ(ligand_strs.size(), 2);

  VinaDock vina_dock(receptor_path, 0, 0, 0, 30, 30, 30, 4, 8);
  std::vector<VinaResult> affinities = vina_dock.vina_fit(ligand_strs, 1.0);
  EXPECT_EQ(affinities.size(), 2);
}

}  // namespace
