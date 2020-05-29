#ifndef CC_ZINC_UTILS_H_
#define CC_ZINC_UTILS_H_

#include <string>
#include <vector>

#include "absl/strings/string_view.h"
#include "protos/compound.pb.h"

namespace zinc {

Compound ConvertMol2StringToPdbqtCompound(absl::string_view mol2_string);

Compound GetMetadataFromSmileString(absl::string_view smile_string);

// This is unfortunate as OBMol is not natively serializable. In this case,
// using free function to wrap the logic seems to be reasonable.
Compound GetMetadataFromMol2String(absl::string_view mol2_string);

}  // namespace zinc

#endif  // CC_ZINC_UTILS_H_