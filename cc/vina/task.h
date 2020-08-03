#ifndef CC_VINA_TASK_H_
#define CC_VINA_TASK_H_

#include <memory>
#include <vector>

#include "cc/vina/execution_context.h"
#include "third_party/vina/lib/model.h"
#include "third_party/vina/lib/random.h"

namespace vina {

class Task {
 public:
  enum class TaskType {
    kSentinel = 0,
    kDock = 1,
  };
  virtual void Run() {}
  virtual TaskType Type() = 0;
  virtual bool RequireExecutionContext() { return false; }
  virtual void SetExecutionContext(const ExecutionContext* context) {}
};

class SentinelTask : public Task {
 public:
  explicit SentinelTask() {}
  TaskType Type() final { return TaskType::kSentinel; }
};

class DockTask : public Task {
 public:
  explicit DockTask(const model& model, int seed);

  DockTask(const DockTask&) = delete;
  void operator=(const DockTask&&) = delete;

  void Run() final;
  TaskType Type() final { return TaskType::kDock; }

  bool RequireExecutionContext() { return true; }
  void SetExecutionContext(const ExecutionContext* context) {
    context_ = context;
  }

 private:
  const ExecutionContext* context_ = nullptr;  // Not Owned
  model model_;
  rng generator_;
  std::vector<std::unique_ptr<output_type>> outputs_;
};

}  // namespace vina

#endif  // CC_VINA_TASK_H_