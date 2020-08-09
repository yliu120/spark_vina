#include "task.h"

#include "absl/base/macros.h"
#include "glog/logging.h"

namespace vina {

DockTask::DockTask(const model& model, int seed)
    : model_(model), generator_(static_cast<rng::result_type>(seed)){};

void DockTask::Run() {
  if (context_ == nullptr) {
    LOG(FATAL) << "Dock task with no context initialized.";
  }
  (*context_->GetMonteCarlo())(
      model_, outputs_, *context_->GetPreCalculate(), *context_->GetIGrid(),
      *context_->GetPreCalculateWidened(), *context_->GetIGridWidened(),
      *context_->GetCorner1(), *context_->GetCorner2(), generator_);
}

}  // namespace vina