#include "lock_free_thread_pool.h"

namespace vina {
namespace {
void WorkLoop(absl::Span<Task*> tasks, const ExecutionContext* context,
              std::atomic_int* next_task) {
  while (true) {
    int next_id = next_task->fetch_add(1, std::memory_order_relaxed);
    if (next_id >= tasks.size()) {
      // Terminates as all tasks are done.
      break;
    }
    auto& task_to_run = tasks[next_id];
    if (task_to_run->Type() == Task::TaskType::kSentinel ||
        task_to_run->Status() == Task::TaskStatus::kDone) {
      // In the lock free version, Sentinel tasks are not necessary.
      // This will simply ignore all sentinel tasks.
      continue;
    }
    if (task_to_run->RequireExecutionContext()) {
      task_to_run->SetExecutionContext(context);
    }
    task_to_run->Run();
    task_to_run->MarkDone();
  }
}
}  // namespace

LockFreeThreadPool::LockFreeThreadPool(const ExecutionContext* context,
                                       int num_threads)
    : execution_context_(context), num_threads_(num_threads) {}

LockFreeThreadPool::LockFreeThreadPool(int num_threads)
    : LockFreeThreadPool(/*context=*/nullptr, num_threads) {}

void LockFreeThreadPool::RunTasks(absl::Span<Task*> tasks) {
  if (tasks.empty()) {
    return;
  }

  std::atomic_int next_task{0};
  std::vector<std::thread> threads;
  for (int i = 0; i < num_threads_; i++) {
    threads.emplace_back(&WorkLoop, tasks, execution_context_, &next_task);
  }

  for (auto& t : threads) {
    if (t.joinable()) {
      t.join();
    }
  }
}
}  // namespace vina