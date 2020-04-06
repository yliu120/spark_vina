// Simple unit tests for cc module parse_pdbqt

#include "spark_vina/parse_pdbqt.h"

#include "gtest/gtest.h"

namespace spark_vina {
namespace {

TEST(ParseReceptorTest, GeneralUsages) {
  model receptor_model = parse_receptor_pdbqt(
      path("spark_vina/data/protein/4ZPH-docking.pdb.pdbqt"));
  EXPECT_EQ(0, receptor_model.num_ligands());
}

TEST(ParseLigandsTest, ParseCompressedLigands) {
  std::vector<std::string> ligand_strs = split_multiple_ligands(
      path("spark_vina/data/ligands/HB/AAMM/HBAAMM.xaa.pdbqt.gz"));
  EXPECT_EQ(2, ligand_strs.size());
}

TEST(ParseLigandsTest, ParseUncompressedLigands) {
  std::vector<std::string> ligand_strs = split_multiple_ligands(
      path("spark_vina/data/ligands/HB/AAMM/HBAAMM.xaa.pdbqt"));
  EXPECT_EQ(2, ligand_strs.size());
}

}  // namespace
}  // namespace spark_vina
