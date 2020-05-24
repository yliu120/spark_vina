#include "cc/zinc/utils.h"

#include "glog/logging.h"
#include "openbabel/mol.h"
#include "openbabel/obconversion.h"

namespace zinc {
namespace {

using ::OpenBabel::OBConversion;

OBConversion* GetOBConversion() {
  static OBConversion* const obconversion = []() {
    OBConversion* obconversion = new OBConversion();
    if (!obconversion->SetInAndOutFormats("mol2", "pdbqt")) {
      LOG(FATAL) << "Unable to get the staticly registered type mol2 and pdbqt";
    }
    return obconversion;
  }();
  return obconversion;
}

}  // namespace

Compound ConvertMol2StringToPdbqtCompound(absl::string_view mol2_string) {
  Compound compound;
  OBConversion* obconversion = GetOBConversion();
  OpenBabel::OBMol molecule;

  if (!obconversion->ReadString(&molecule, std::string(mol2_string))) {
    LOG(ERROR) << "Unable to read mol2 string: " << mol2_string;
    return compound;
  }
  compound.set_name(molecule.GetTitle());
  compound.set_num_atoms(molecule.NumAtoms());
  compound.set_num_bonds(molecule.NumBonds());
  compound.set_original_pdbqt(obconversion->WriteString(&molecule));

  if (compound.original_pdbqt().empty()) {
    LOG(ERROR) << "Unable to convert MOL2 string: " << mol2_string;
  }
  return compound;
}

}  // namespace zinc