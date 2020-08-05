#include "cc/vina/lock_free_thread_pool.h"

#include "absl/types/span.h"
#include "cc/vina/task.h"
#include "gtest/gtest.h"

namespace vina {

// A simplest add task for test.
class AddTask : public Task {
 public:
  explicit AddTask(int a, int b) : a_(a), b_(b), result_(0) {}
  void Run() final { result_ = a_ + b_; }
  TaskType Type() const final { return TaskType::kCompute; }

  int a() const { return a_; }
  int b() const { return b_; }
  int GetResult() const {
    EXPECT_EQ(Status(), TaskStatus::kDone);
    return result_;
  }

 private:
  const int a_;
  const int b_;
  int result_;
};

TEST(LockFreeThreadPoolTest, RunAddTasks) {
  std::vector<AddTask> task_storage;
  std::vector<Task*> tasks;

  for (int i = 0; i < 1000; i++) {
    task_storage.emplace_back(i, 1000);
  }
  for (auto& task : task_storage) {
    tasks.push_back(&task);
  }

  LockFreeThreadPool thread_pool(/*num_threads=*/4);
  thread_pool.RunTasks(absl::MakeSpan(tasks));

  for (const auto& task : task_storage) {
    EXPECT_EQ(task.GetResult(), task.a() + 1000);
  }
}

}  // namespace vina