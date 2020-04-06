// Simple unit tests for cc module parse_pdbqt

#include "parse_pdbqt.h"

#include "gtest/gtest.h"

namespace spark_vina {
namespace {

TEST(ParseReceptorTest, GeneralUsages) {
  model receptor_model = parse_receptor_pdbqt(
      path("data/protein/4ZPH-docking.pdb.pdbqt"));
  EXPECT_EQ(receptor_model.num_ligands(), 0);
}

TEST(ParseLigandsTest, ParseCompressedLigands) {
  std::vector<std::string> ligand_strs = split_multiple_ligands(
      path("data/ligands/HB/AAMM/HBAAMM.xaa.pdbqt.gz"));
  EXPECT_EQ(ligand_strs.size(), 2);
}

TEST(ParseLigandsTest, ParseUncompressedLigands) {
  std::vector<std::string> ligand_strs = split_multiple_ligands(
      path("data/ligands/HB/AAMM/HBAAMM.xaa.pdbqt"));
  EXPECT_EQ(ligand_strs.size(), 2);
}

}  // namespace
}  // namespace spark_vina
