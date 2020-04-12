// Convenient tools on parsing pdbqt or pdbqt.gz to vina models.
// Author: Yunlong Liu

#ifndef CC_PARSE_PDBQT_H_
#define CC_PARSE_PDBQT_H_

#include <string>
#include <utility>
#include <vector>

#include "third_party/vina/lib/model.h"

namespace spark_vina {

// Parse a receptor pdbqt string to model.
// can throw parse_error
model parse_receptor_pdbqt(const path& receptor_path);

// Parse ligand strs to a vector of ligand models.
// can throw parse_error
std::vector<std::pair<int, model>> parse_ligand_pdbqt(
    const std::vector<std::string>& ligand_strs);

// Splitting ligands in a file to ligand strings.
std::vector<std::string> split_multiple_ligands(const path& ligand_path);

}  // namespace spark_vina

#endif  // CC_PARSE_PDBQT_H_
