#ifndef CC_ZINC_UTILS_H_
#define CC_ZINC_UTILS_H_

#include <string>
#include <vector>

#include "absl/strings/string_view.h"
#include "protos/compound.pb.h"

namespace zinc {

Compound ConvertMol2StringToPdbqtCompound(absl::string_view mol2_string);

}  // namespace zinc

#endif  // CC_ZINC_UTILS_H_