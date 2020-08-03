#ifndef CC_VINA_EXECUTION_CONTEXT_H_
#define CC_VINA_EXECUTION_CONTEXT_H_

#include "third_party/vina/lib/common.h"
#include "third_party/vina/lib/precalculate.h"
#include "third_party/vina/lib/monte_carlo.h"
#include "third_party/vina/lib/igrid.h"

namespace vina {

struct ExecutionContextInput {
  const monte_carlo* mc = nullptr;
  const precalculate* p = nullptr;
  const precalculate* p_widened = nullptr;
  const igrid* g = nullptr;
  const igrid* ig_widened = nullptr;
  const vec* corner1 = nullptr;
  const vec* corner2 = nullptr;
};

// A shared context for task execution.
class ExecutionContext {
 public:
  explicit ExecutionContext(ExecutionContextInput&& input) : input_(input) {}

  ExecutionContext(const ExecutionContext&) = delete;
  void operator=(const ExecutionContext&&) = delete;

  const monte_carlo* GetMonteCarlo() const { return input_.mc; }

  const precalculate* GetPreCalculate() const { return input_.p; }

  const precalculate* GetPreCalculateWidened() const {
    return input_.p_widened;
  }

  const igrid* GetIGrid() const { return input_.g; }

  const igrid* GetIGridWidened() const { return input_.ig_widened; }

  const vec* GetCorner1() const { return input_.corner1; }
  const vec* GetCorner2() const { return input_.corner2; }

 private:
  ExecutionContextInput input_;
};

}  // namespace vina

#endif  // CC_VINA_EXECUTION_CONTEXT_H_