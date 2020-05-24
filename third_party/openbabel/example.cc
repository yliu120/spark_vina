#include <string>

#include "glog/logging.h"
#include "openbabel/obconversion.h"
#include "openbabel/mol.h"

constexpr char kMol2String[] = R"(
@<TRIPOS>MOLECULE
benzene
   12 12 1  0   0
SMALL
NO_CHARGES
@<TRIPOS>ATOM
   1   C1  1.207   2.091   0.000   C.ar    1   BENZENE 0.000
   2   C2  2.414   1.394   0.000   C.ar    1   BENZENE 0.000
   3   C3  2.414   0.000   0.000   C.ar    1   BENZENE 0.000
   4   C4  1.207   -0.697  0.000   C.ar    1   BENZENE 0.000
   5   C5  0.000   0.000   0.000   C.ar    1   BENZENE 0.000
   6   C6  0.000   1.394   0.000   C.ar    1   BENZENE 0.000
   7   H1  1.207   3.175   0.000   H   1   BENZENE 0.000
   8   H2  3.353   1.936   0.000   H   1   BENZENE 0.000
   9   H3  3.353   -0.542  0.000   H   1   BENZENE 0.000
   10  H4  1.207   -1.781  0.000   H   1   BENZENE 0.000
   11  H5  -0.939  -0.542  0.000   H   1   BENZENE 0.000
   12  H6  -0.939  1.936   0.000   H   1   BENZENE 0.000
@<TRIPOS>BOND
   1   1   2   ar
   2   1   6   ar
   3   2   3   ar
   4   3   4   ar
   5   4   5   ar
   6   5   6   ar
   7   1   7   1
   8   2   8   1
   9   3   9   1
   10  4   10  1
   11  5   11  1
   12  6   12  1
@<TRIPOS>SUBSTRUCTURE
   1   BENZENE 1   PERM    0   ****    ****    0   ROOT
)";

int main(int argc, char* argv[]) {
  google::InitGoogleLogging(argv[0]);

  OpenBabel::OBConversion conv;
  if (!conv.SetInAndOutFormats("mol2", "pdbqt")) {
    LOG(ERROR) << "Unable to load formats: mol2, pdbqt.";
    return 1;
  }

  OpenBabel::OBMol molecule;

  if (!conv.ReadString(&molecule, kMol2String)) {
    LOG(ERROR) << "Unable to read mol2 string: " << kMol2String;
    return 1;
  }
  LOG(INFO) << "Read molecule: " << molecule.GetTitle() << " with " << molecule.NumAtoms()
            << " atoms.";
  const std::string result = conv.WriteString(&molecule);
  if (result.empty()) {
    LOG(ERROR) << "Unable to write pdbqt string.";
    return 1;
  }

  LOG(INFO) << "\nReceived mol2 string: " << kMol2String;
  LOG(INFO) << "\nConvert to pdbqt string: " << result;
  return 0;
}