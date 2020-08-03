#include "cc/vina/thread_pool.h"

namespace vina {

ThreadPool::ThreadPool(const ExecutionContext* context, int num_threads)
    : execution_context_(context) {
  for (int i = 0; i < num_threads; ++i) {
    threads_.push_back(std::thread(&ThreadPool::WorkLoop, this));
  }
}

ThreadPool::ThreadPool(int num_threads)
    : ThreadPool(/*context=*/nullptr, num_threads) {}

ThreadPool::~ThreadPool() {
  {
    absl::MutexLock l(&mu_);
    for (size_t i = 0; i < threads_.size(); i++) {
      queue_.push(SentinelTask());  // Shutdown signal.
    }
  }
  for (auto& t : threads_) {
    t.join();
  }
}

ThreadPool::Schedule(Task&& task) {
  absl::MutexLock lock(&mu_);
  queue_.push(task);
}

bool ThreadPool::WorkAvailable() const { return !queue_.empty(); }

void ThreadPool::WorkLoop() {
  while (true) {
    Task task_to_run;
    {
      absl::MutexLock l(&mu_);
      mu_.Await(absl::Condition(this, &ThreadPool::WorkAvailable));
      task_to_run = std::move(queue_.front());
      queue_.pop();
    }
    if (task_to_run.Type() == Task::TaskType::kSentinel) {  // Shutdown signal.
      break;
    }
    if (task_to_run.RequireExecutionContext()) {
      task_to_run.SetExecutionContext(execution_context_);
    }
    task_to_run.Run();
  }
}

}  // namespace vina