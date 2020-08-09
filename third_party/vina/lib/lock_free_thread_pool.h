#ifndef VINA_LOCK_FREE_THREAD_POOL_H_
#define VINA_LOCK_FREE_THREAD_POOL_H_

#include <atomic>
#include <memory>
#include <queue>
#include <thread>
#include <vector>

#include "absl/types/span.h"
#include "execution_context.h"
#include "task.h"

namespace vina {

// A thread pool executor processing a fixed size of work items
// with a fixed number of workers.
class LockFreeThreadPool {
 public:
  explicit LockFreeThreadPool(const ExecutionContext* context, int num_threads);
  explicit LockFreeThreadPool(int num_threads);

  LockFreeThreadPool(const LockFreeThreadPool&) = delete;
  LockFreeThreadPool& operator=(const LockFreeThreadPool&) = delete;

  void SetExecutionContext(const ExecutionContext* context) {
    execution_context_ = context;
  }
  // Takes ownership of all tasks.
  void RunTasks(absl::Span<Task*> tasks);

 private:
  const ExecutionContext* execution_context_ = nullptr;
  int num_threads_;
};

}  // namespace vina

#endif  // VINA_LOCK_FREE_THREAD_POOL_H_