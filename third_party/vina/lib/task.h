#ifndef VINA_TASK_H_
#define VINA_TASK_H_

#include <memory>
#include <vector>

#include "execution_context.h"
#include "model.h"
#include "random.h"

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
  DockTask() = default;
  explicit DockTask(const model& model, int seed);

  DockTask(const DockTask&) = delete;
  void operator=(const DockTask&&) = delete;

  DockTask(DockTask&&) = default;
  DockTask& operator=(DockTask&&) = default;

  void Run() final;
  TaskType Type() const final { return TaskType::kCompute; }

  bool RequireExecutionContext() { return true; }
  void SetExecutionContext(const ExecutionContext* context) {
    context_ = context;
  }

  const std::vector<std::unique_ptr<output_type>>& Output() const {
    return outputs_;
  }

 private:
  const ExecutionContext* context_ = nullptr;  // Not Owned
  model model_;
  rng generator_;
  std::vector<std::unique_ptr<output_type>> outputs_;
};

}  // namespace vina

#endif  // VINA_TASK_H_