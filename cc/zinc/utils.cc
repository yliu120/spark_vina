#include "cc/zinc/utils.h"

#include <cmath>

#include "glog/logging.h"
#include "openbabel/mol.h"
#include "openbabel/obconversion.h"

namespace zinc {
namespace {

using ::OpenBabel::OBConversion;
using ::OpenBabel::OBMol;

OBConversion* GetMol2ToPdbqtOBConversion() {
  static OBConversion* const obconversion = []() {
    OBConversion* obconversion = new OBConversion();
    if (!obconversion->SetInAndOutFormats("mol2", "pdbqt")) {
      LOG(FATAL)
          << "Unable to get the staticly registered type mol2 and pdbqt.";
    }
    return obconversion;
  }();
  return obconversion;
}

OBConversion* GetSmilesOBConversion() {
  static OBConversion* const obconversion = []() {
    OBConversion* obconversion = new OBConversion();
    if (!obconversion->SetInFormat("smi")) {
      LOG(FATAL) << "Unable to get the staticly registered type smile.";
    }
    return obconversion;
  }();
  return obconversion;
}

int ComputeMolecularWeightMillis(double raw_mol_weight) {
  return static_cast<int>(std::nearbyint(raw_mol_weight * 100.0)) * 10;
}
}  // namespace

std::string ConvertMol2StringToPdbqtString(absl::string_view mol2_string) {
  Compound compound;
  OBMol molecule;
  if (!GetMol2ToPdbqtOBConversion()->ReadString(&molecule,
                                                std::string(mol2_string))) {
    LOG(ERROR) << "Unable to read mol2 string: " << mol2_string;
    return "";
  }

  std::string result = GetMol2ToPdbqtOBConversion()->WriteString(&molecule);
  if (result.empty()) {
    LOG(ERROR) << "Unable to convert MOL2 string: " << mol2_string;
  }
  return result;
}

Compound GetMetadataFromSmileString(absl::string_view smile_string) {
  Compound compound;
  OBMol molecule;

  if (!GetSmilesOBConversion()->ReadString(&molecule, std::string(smile_string))) {
    LOG(ERROR) << "Unable to read smile string: " << smile_string;
    return compound;
  }
  molecule.AddHydrogens();

  compound.set_num_atoms(molecule.NumAtoms());
  compound.set_num_bonds(molecule.NumBonds());
  compound.set_molecular_weight_millis(ComputeMolecularWeightMillis(molecule.GetMolWt()));
  compound.set_net_charge(molecule.GetTotalCharge());
  return compound;
}

Compound GetMetadataFromMol2String(absl::string_view mol2_string) {
  Compound compound;
  OBMol molecule;
  if (!GetMol2ToPdbqtOBConversion()->ReadString(&molecule,
                                                std::string(mol2_string))) {
    LOG(ERROR) << "Unable to read mol2 string: " << mol2_string;
    return compound;
  }
  compound.set_name(molecule.GetTitle());
  compound.set_num_atoms(molecule.NumAtoms());
  compound.set_num_bonds(molecule.NumBonds());
  compound.set_molecular_weight_millis(ComputeMolecularWeightMillis(molecule.GetMolWt()));
  compound.set_net_charge(molecule.GetTotalCharge());
  return compound;
}

}  // namespace zinc