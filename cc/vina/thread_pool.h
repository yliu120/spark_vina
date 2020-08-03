#ifndef CC_VINA_THREAD_POOL_H_
#define CC_VINA_THREAD_POOL_H_

#include <memory>
#include <queue>
#include <thread>
#include <vector>

#include "absl/base/thread_annotations.h"
#include "absl/synchronization/mutex.h"
#include "cc/vina/execution_context.h"
#include "cc/vina/task.h"

namespace vina {

// A trivial fixed size thread pool.
class ThreadPool {
 public:
  explicit ThreadPool(const ExecutionContext* context, int num_threads);
  explicit ThreadPool(int num_threads);

  ThreadPool(const ThreadPool&) = delete;
  ThreadPool& operator=(const ThreadPool&) = delete;

  ~ThreadPool();

  void SetExecutionContext(const ExecutionContext* context) {
    execution_context_ = context;
  }
  void Schedule(Task&& task);

 private:
  bool WorkAvailable() const ABSL_SHARED_LOCKS_REQUIRED(mu_);
  void WorkLoop();

  const ExecutionContext* execution_context_ = nullptr;
  absl::Mutex mu_;
  std::queue<Task> queue_ ABSL_GUARDED_BY(mu_);
  std::vector<std::thread> threads_;
};

}  // namespace vina

#endif  // CC_VINA_THREAD_POOL_H_