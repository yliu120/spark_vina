#include <string>

#include "absl/strings/string_view.h"
#include "gflags/gflags.h"
#include "glog/logging.h"
#include "openbabel/mol.h"
#include "openbabel/obconversion.h"

constexpr char kMol2String[] = R"(
@<TRIPOS>MOLECULE
ZINC000004428529
  53   56    0    0    0
SMALL
USER_CHARGES
NO_NAME
@<TRIPOS>ATOM
    1 C1        0.6436     0.8656     1.4458 C.3    1 <0>      -0.2100
    2 C2        0.2423     0.3022     0.1070 C.2    1 <0>       0.3600
    3 O3        0.9910     0.3945    -0.8360 O.2    1 <0>      -0.4500
    4 C4       -1.0934    -0.3765    -0.0551 C.3    1 <0>      -0.1300
    5 H5       -1.8451     0.1186     0.5596 H      1 <0>       0.1000
    6 C6       -1.5243    -0.3648    -1.5430 C.3    1 <0>      -0.1200
    7 C7       -1.7376    -1.8487    -1.9489 C.3    1 <0>      -0.1200
    8 C8       -2.0249    -2.5333    -0.6079 C.3    1 <0>      -0.0800
    9 H9       -3.0387    -2.3084    -0.2767 H      1 <0>       0.0800
   10 C10      -1.7758    -4.0299    -0.5642 C.3    1 <0>      -0.0800
   11 H11      -0.7442    -4.2442    -0.8436 H      1 <0>       0.0900
   12 C12      -2.7360    -4.7423    -1.5212 C.3    1 <0>      -0.1100
   13 C13      -2.4262    -6.2408    -1.5195 C.3    1 <0>      -0.1000
   14 C14      -2.4940    -6.7522    -0.0921 C.2    1 <0>      -0.0200
   15 C15      -3.2035    -7.8571     0.1368 C.2    1 <0>      -0.2400
   16 C16      -3.2909    -8.4056     1.4945 C.2    1 <0>       0.3900
   17 O17      -4.1912    -9.1480     1.8266 O.2    1 <0>      -0.4600
   18 C18      -2.1971    -7.9956     2.4610 C.3    1 <0>      -0.1700
   19 C19      -2.0681    -6.4748     2.3870 C.3    1 <0>      -0.1000
   20 C20      -1.7572    -6.0053     0.9765 C.3    1 <0>      -0.0300
   21 C21      -0.2576    -6.1898     0.7359 C.3    1 <0>      -0.1500
   22 C22      -2.0393    -4.5019     0.8730 C.3    1 <0>      -0.0700
   23 H23      -3.0904    -4.3221     1.0987 H      1 <0>       0.0700
   24 C24      -1.1787    -3.7609     1.8922 C.3    1 <0>      -0.1200
   25 C25      -1.3232    -2.2368     1.7579 C.3    1 <0>      -0.1000
   26 C26      -0.9870    -1.8500     0.3246 C.3    1 <0>      -0.0500
   27 C27       0.3972    -2.3860    -0.0462 C.3    1 <0>      -0.1600
   28 H28       1.6341     1.3141     1.3691 H      1 <0>       0.0800
   29 H29      -0.0764     1.6250     1.7510 H      1 <0>       0.0900
   30 H30       0.6633     0.0654     2.1856 H      1 <0>       0.0900
   31 H31      -2.4541     0.1921    -1.6588 H      1 <0>       0.0700
   32 H32      -0.7413     0.0818    -2.1558 H      1 <0>       0.0800
   33 H33      -2.5896    -1.9458    -2.6218 H      1 <0>       0.0700
   34 H34      -0.8359    -2.2572    -2.4051 H      1 <0>       0.0700
   35 H35      -3.7630    -4.5822    -1.1930 H      1 <0>       0.0700
   36 H36      -2.6082    -4.3450    -2.5282 H      1 <0>       0.0700
   37 H37      -3.1596    -6.7662    -2.1311 H      1 <0>       0.0800
   38 H38      -1.4263    -6.4076    -1.9203 H      1 <0>       0.0800
   39 H39      -3.7140    -8.3491    -0.6779 H      1 <0>       0.1300
   40 H40      -2.4659    -8.2957     3.4738 H      1 <0>       0.0900
   41 H41      -1.2547    -8.4617     2.1734 H      1 <0>       0.1000
   42 H42      -3.0042    -6.0224     2.7143 H      1 <0>       0.0700
   43 H43      -1.2675    -6.1532     3.0531 H      1 <0>       0.0800
   44 H44      -0.0183    -7.2532     0.7325 H      1 <0>       0.0600
   45 H45       0.0125    -5.7535    -0.2258 H      1 <0>       0.0600
   46 H46       0.3014    -5.6941     1.5296 H      1 <0>       0.0700
   47 H47      -1.4805    -4.0594     2.8962 H      1 <0>       0.0700
   48 H48      -0.1343    -4.0336     1.7407 H      1 <0>       0.0700
   49 H49      -2.3548    -1.9431     1.9518 H      1 <0>       0.0700
   50 H50      -0.6557    -1.7373     2.4601 H      1 <0>       0.0700
   51 H51       0.4317    -3.4601     0.1364 H      1 <0>       0.0700
   52 H52       0.5922    -2.1902    -1.1006 H      1 <0>       0.0600
   53 H53       1.1538    -1.8897     0.5615 H      1 <0>       0.0500
@<TRIPOS>BOND
   1    1    2 1
   2    1   28 1
   3    1   29 1
   4    1   30 1
   5    2    3 2
   6    2    4 1
   7    4    5 1
   8    4   26 1
   9    4    6 1
  10    6    7 1
  11    6   31 1
  12    6   32 1
  13    7    8 1
  14    7   33 1
  15    7   34 1
  16    8    9 1
  17    8   26 1
  18    8   10 1
  19   10   11 1
  20   10   22 1
  21   10   12 1
  22   12   13 1
  23   12   35 1
  24   12   36 1
  25   13   14 1
  26   13   37 1
  27   13   38 1
  28   14   20 1
  29   14   15 2
  30   15   16 1
  31   15   39 1
  32   16   17 2
  33   16   18 1
  34   18   19 1
  35   18   40 1
  36   18   41 1
  37   19   20 1
  38   19   42 1
  39   19   43 1
  40   20   21 1
  41   20   22 1
  42   21   44 1
  43   21   45 1
  44   21   46 1
  45   22   23 1
  46   22   24 1
  47   24   25 1
  48   24   47 1
  49   24   48 1
  50   25   26 1
  51   25   49 1
  52   25   50 1
  53   26   27 1
  54   27   51 1
  55   27   52 1
  56   27   53 1
)";

constexpr const char* const kSmilesStrings[] = {
    "O=C(Nc1ccc(Cl)cc1)Nc1ccc(Cl)c(Cl)c1",
    "OC[C@H]1O[C@@H](Oc2ccc(O)cc2)[C@H](O)[C@@H](O)[C@@H]1O",
    "COc1ccc(-c2cc(=O)c3c(O)cc([O-])cc3o2)cc1O",
    "C=C1CC[C@H]2[C@@H](/"
    "C=C(\C)C(=O)[C@@]3(OC(C)=O)C[C@H](C)[C@H](OC(=O)c4ccccc4)[C@@H]3[C@H]1OC("
    "C)=O)C2(C)C",
    "O=C(OCCO)c1ccccc1O",
    "CCOc1cc2ncc(C#N)c(Nc3ccc(OCc4ccccn4)c(Cl)c3)c2cc1NC(=O)/C=C/CN(C)C",
    "Nc1nc(-n2cc(C(=O)[O-])c(=O)c3cc(F)c(N4CC(O)C4)c(Cl)c32)c(F)cc1F",
    "COC(=O)[C@H]1[C@@H](c2ccc(I)cc2)C[C@@H]2CC[C@H]1[N@@H+]2CCCF",
    "CO[C@@H]1[C@H](N(C)C(=O)c2ccccc2)C[C@H]2O[C@]1(C)n1c3ccccc3c3c4c("
    "c5c6ccccc6n2c5c31)C(=O)NC4",
    "C[NH2+]CCC=C1c2ccccc2CCc2ccccc21",
    "COC(=O)[C@H]1[C@H]2C[C@@H]3c4[nH]c5cc(OC)ccc5c4CCN3C[C@H]2C[C@@H](OC(=O)"
    "c2cc(OC)c(OC)c(OC)c2)[C@@H]1OC",
    "Cn1nc(C(=O)NC2C[C@@H]3CCC[C@H](C2)[N@@H+]3C)c2ccccc21",
    "Cc1cc2nc3c(=O)[n-]c(=O)nc-3n(C[C@H](O)[C@H](O)[C@H](O)CO)c2cc1C"};

void ParseSmileString(absl::string_view smile_string) {
  OpenBabel::OBConversion conv;
  OpenBabel::OBMol molecule;
  if (!conv.SetInFormat("smi")) {
    LOG(ERROR) << "Unable to load formats: smi.";
    return;
  }
  if (!conv.ReadString(&molecule, std::string(smile_string))) {
    LOG(ERROR) << "Unable to read smi string: " << smile_string;
    return;
  }
  molecule.AddHydrogens();
  LOG(INFO) << "Read molecule: " << molecule.NumAtoms() << " atoms & "
            << molecule.NumBonds() << " bonds. "
            << " (MW: " << molecule.GetMolWt()
            << " Net Charge: " << molecule.GetTotalCharge() << ")\n";
}

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
            << molecule.NumAtoms() << " atoms & " << molecule.NumBonds()
            << " bonds. "
            << " (MW: " << molecule.GetMolWt()
            << " Net Charge: " << molecule.GetTotalCharge() << ")\n";
  LOG(INFO) << "\nReceived mol2 string: " << kMol2String;

  const std::string pdbqt_string = conv.WriteString(&molecule);
  if (pdbqt_string.empty()) {
    LOG(ERROR) << "Unable to write pdbqt string.";
    return 1;
  }
  LOG(INFO) << "\nConvert to pdbqt string: " << pdbqt_string;

  for (const auto& smile_string : kSmilesStrings) {
    ParseSmileString(smile_string);
  }
  return 0;
}