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
  enum class TaskType : char {
    kSentinel = 0,
    kCompute = 1,
  };
  enum class TaskStatus : char {
    kNotStart = 0,
    kDone = 1,
  };
  virtual void Run() {}
  virtual TaskType Type() const = 0;
  virtual bool RequireExecutionContext() { return false; }
  virtual void SetExecutionContext(const ExecutionContext* context) {}

  void MarkDone() { task_status_ = TaskStatus::kDone; }
  TaskStatus Status() const { return task_status_; }

 private:
  TaskStatus task_status_ = TaskStatus::kNotStart;
};

class SentinelTask : public Task {
 public:
  explicit SentinelTask() {}
  TaskType Type() const final { return TaskType::kSentinel; }
};

class DockTask : public Task {
 public:
  explicit DockTask(const model& model, int seed);

  DockTask(const DockTask&) = delete;
  void operator=(const DockTask&&) = delete;

  void Run() final;
  TaskType Type() const final { return TaskType::kCompute; }

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