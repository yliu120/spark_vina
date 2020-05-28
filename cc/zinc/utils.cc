#include "cc/zinc/utils.h"

#include "glog/logging.h"
#include "openbabel/mol.h"
#include "openbabel/obconversion.h"

namespace zinc {
namespace {

using ::OpenBabel::OBConversion;

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

}  // namespace

Compound ConvertMol2StringToPdbqtCompound(absl::string_view mol2_string) {
  Compound compound;
  OBConversion* obconversion = GetMol2ToPdbqtOBConversion();
  OpenBabel::OBMol molecule;

  if (!obconversion->ReadString(&molecule, std::string(mol2_string))) {
    LOG(ERROR) << "Unable to read mol2 string: " << mol2_string;
    return compound;
  }
  compound.set_name(molecule.GetTitle());
  compound.set_num_atoms(molecule.NumAtoms());
  compound.set_num_bonds(molecule.NumBonds());
  compound.set_molecular_weight(molecule.GetMolWt());
  compound.set_net_charge(molecule.GetTotalCharge());
  compound.set_original_pdbqt(obconversion->WriteString(&molecule));

  if (compound.original_pdbqt().empty()) {
    LOG(ERROR) << "Unable to convert MOL2 string: " << mol2_string;
  }
  return compound;
}

Compound GetMetadataFromSmileString(absl::string_view smile_string) {
  Compound compound;
  OBConversion* obconversion = GetSmilesOBConversion();
  OpenBabel::OBMol molecule;

  if (!obconversion->ReadString(&molecule, std::string(smile_string))) {
    LOG(ERROR) << "Unable to read smile string: " << smile_string;
    return compound;
  }
  molecule.AddHydrogens();

  compound.set_num_atoms(molecule.NumAtoms());
  compound.set_num_bonds(molecule.NumBonds());
  compound.set_molecular_weight(molecule.GetMolWt());
  compound.set_net_charge(molecule.GetTotalCharge());
  return compound;
}

}  // namespace zinc