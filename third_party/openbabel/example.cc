#include <string>

#include "gflags/gflags.h"
#include "glog/logging.h"
#include "openbabel/mol.h"
#include "openbabel/obconversion.h"

constexpr char kMol2String[] = R"(
@<TRIPOS>MOLECULE
ZINC000001531008
  24   23    0    0    0
SMALL
USER_CHARGES
NO_NAME
@<TRIPOS>ATOM
    1 O1        0.0021    -0.0041     0.0020 O.co2  1 <0>      -0.6900
    2 C2       -0.0145     1.2150     0.0087 C.2    1 <0>       0.4800
    3 O3        1.0330     1.8389     0.0021 O.co2  1 <0>      -0.6900
    4 C4       -1.3296     1.9507     0.0190 C.3    1 <0>       0.0100
    5 H5       -1.3550     2.6379     0.8646 H      1 <0>       0.0900
    6 O6       -2.4000     1.0113     0.1364 O.3    1 <0>      -0.5400
    7 C7       -1.4833     2.7388    -1.2834 C.3    1 <0>       0.0700
    8 H8       -0.7192     3.5147    -1.3309 H      1 <0>       0.1200
    9 O9       -1.3338     1.8544    -2.3958 O.3    1 <0>      -0.5400
   10 C10      -2.8696     3.3847    -1.3281 C.3    1 <0>       0.0500
   11 H11      -2.9515     4.1247    -0.5319 H      1 <0>       0.1000
   12 O12      -3.8694     2.3796    -1.1490 O.3    1 <0>      -0.5300
   13 C13      -3.0703     4.0682    -2.6822 C.3    1 <0>       0.0800
   14 H14      -2.9884     3.3283    -3.4783 H      1 <0>       0.1100
   15 O15      -2.0705     5.0733    -2.8613 O.3    1 <0>      -0.5400
   16 C16      -4.4566     4.7141    -2.7269 C.3    1 <0>       0.0500
   17 O17      -4.6899     5.2514    -4.0303 O.3    1 <0>      -0.5700
   18 H18      -2.4389     0.3673    -0.5840 H      1 <0>       0.3600
   19 H19      -1.9821     1.1373    -2.4181 H      1 <0>       0.3600
   20 H20      -3.8556     1.6866    -1.8233 H      1 <0>       0.3700
   21 H21      -2.0842     5.7664    -2.1870 H      1 <0>       0.3700
   22 H22      -5.2145     3.9637    -2.5022 H      1 <0>       0.0600
   23 H23      -4.5083     5.5149    -1.9892 H      1 <0>       0.0500
   24 H24      -5.5521     5.6775    -4.1315 H      1 <0>       0.3800
@<TRIPOS>BOND
   1    1    2 ar
   2    2    3 ar
   3    2    4 1
   4    4    5 1
   5    4    6 1
   6    4    7 1
   7    6   18 1
   8    7    8 1
   9    7    9 1
  10    7   10 1
  11    9   19 1
  12   10   11 1
  13   10   12 1
  14   10   13 1
  15   12   20 1
  16   13   14 1
  17   13   15 1
  18   13   16 1
  19   15   21 1
  20   16   17 1
  21   16   22 1
  22   16   23 1
  23   17   24 1
)";

int main(int argc, char* argv[]) {
  google::InitGoogleLogging(argv[0]);
  google::ParseCommandLineFlags(&argc, &argv, true);

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
  LOG(INFO) << "Read molecule: " << molecule.GetTitle() << " with "
            << molecule.NumAtoms() << " atoms."
            << " (MW: " << molecule.GetMolWt()
            << " Net Charge: " << molecule.GetTotalCharge() << ")\n";
  LOG(INFO) << "\nReceived mol2 string: " << kMol2String;

  const std::string pdbqt_string = conv.WriteString(&molecule);
  if (pdbqt_string.empty()) {
    LOG(ERROR) << "Unable to write pdbqt string.";
    return 1;
  }
  LOG(INFO) << "\nConvert to pdbqt string: " << pdbqt_string;
  return 0;
}