// This file is the header file for building vina's python module.
// Author: Yunlong Liu (davislong198833@gmail.com)
//
#ifndef SPARK_VINA_VINA_H_
#define SPARK_VINA_VINA_H_

#include <string>
#include <vector>

class VinaResult {
 public:
  VinaResult() {}
  explicit VinaResult(const std::string& ligand_str, double affinity)
      : ligand_str(ligand_str), affinity(affinity) {}

  std::string ligand_str;
  double affinity;
};

// This is a util method that read ligand files to a list of strings.
// This is just an interface to expose the split method in parse_pdbqt.
std::vector<std::string> read_ligand_to_strings(const std::string& ligand_path);

// This class provides a simple and light-weighted wrapper class
// for AutoDock Vina library. This class will serve the interface
// between python3 and the original vina's c++ libraries.
class VinaDock {
 public:
  explicit VinaDock(const std::string& receptor_path,
                    // We don't support flex_name for big virtual screening.
                    double center_x, double center_y, double center_z,
                    double size_x, double size_y, double size_z, int cpu,
                    int num_modes);

  std::vector<VinaResult> vina_fit(const std::vector<std::string>& ligand_strs,
                                   double filter_limit);

 private:
  // All private fields are named as the original vina/main/main.cpp
  std::string rigid_name_;

  double center_x_;
  double center_y_;
  double center_z_;
  double size_x_;
  double size_y_;
  double size_z_;
  int cpu_;
  int num_modes_;
};

#endif  // SPARK_VINA_VINA_H_
