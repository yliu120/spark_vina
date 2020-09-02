#include <cstdio>
#include <cstdlib>
#include <filesystem>
#include <fstream>
#include <memory>

// Low-level C headers
#include <fcntl.h>
#include <sys/mman.h>
#include <unistd.h>

#include "benchmark/benchmark.h"
#include "glog/logging.h"

namespace experimental {
namespace {

const int kPageSize = getpagesize();
const int kDefaultBufferSize = 2 * kPageSize;

// Size has to be a multiply of 32 bytes.
void WriteTestDataFile(const std::filesystem::path& path, int size) {
  // Modulus 32
  LOG_IF(FATAL, (size & (32 - 1)) != 0)
      << "Requested file size is not a multiply of 32! ";

  std::ofstream ofs;
  ofs.open(path, std::ofstream::out);

  for (int i = 0; i < (size >> 5); i++) {
    ofs << "TESTTESTTESTTESTTESTTESTTESTTEST";
  }
  ofs.close();
}

void ReadFileWithSyscall(const std::filesystem::path& path, int file_size) {
  int fd = open(path.c_str(), O_RDONLY);
  LOG_IF(FATAL, fd == -1) << "ReadFileWithSyscall cannot open file!";
  auto buffer = std::make_unique<char[]>(kDefaultBufferSize);

  ssize_t bytes_read = read(fd, buffer.get(), kDefaultBufferSize);
  ssize_t total_bytes_read = bytes_read;
  while (bytes_read == kDefaultBufferSize) {
    bytes_read = read(fd, buffer.get(), kDefaultBufferSize);
    total_bytes_read += bytes_read;
  }

  LOG_IF(FATAL, total_bytes_read != file_size)
      << "ReadFileWithSyscall Read failed";
  LOG_IF(FATAL, close(fd) == -1) << "ReadFileWithSyscall cannot close file!";
}

void ReadFileWithFread(const std::filesystem::path& path, int file_size) {
  std::FILE* fp = std::fopen(path.c_str(), "r");
  LOG_IF(FATAL, fp == nullptr) << "ReadFileWithFread cannot open file!";
  auto buffer = std::make_unique<char[]>(kDefaultBufferSize);

  ssize_t bytes_read =
      std::fread(buffer.get(), sizeof(buffer[0]), kDefaultBufferSize, fp);
  ssize_t total_bytes_read = bytes_read;
  while (bytes_read == kDefaultBufferSize) {
    bytes_read =
        std::fread(buffer.get(), sizeof(buffer[0]), kDefaultBufferSize, fp);
    total_bytes_read += bytes_read;
  }

  LOG_IF(FATAL, total_bytes_read != file_size)
      << "ReadFileWithFread Read failed";
  LOG_IF(FATAL, std::ferror(fp)) << "ReadFileWithFread read I/O error!";
  std::fclose(fp);
}

void ReadFileWithIfstream(const std::filesystem::path& path, int file_size) {
  std::ifstream stream(path, std::ios::in);
  auto buffer = std::make_unique<char[]>(kDefaultBufferSize);

  ssize_t total_bytes_read = 0;
  while (stream.read(buffer.get(), kDefaultBufferSize)) {
    total_bytes_read += stream.gcount();
  }
  total_bytes_read += stream.gcount();
  LOG_IF(FATAL, total_bytes_read != file_size)
      << "ReadFileWithIfstream Read failed";
}

void ReadFileWithMMap(const std::filesystem::path& path, int file_size) {
  int fd = open(path.c_str(), O_RDONLY);
  LOG_IF(FATAL, fd == -1) << "ReadFileWithMMap cannot open file!";

  char* mapped = static_cast<char*>(
      mmap(nullptr, file_size, PROT_READ, MAP_PRIVATE, fd, /*offset=*/0));
  // Mimics memory read access to each page.
  char c;
  for (int i = 0; i < file_size; i += kPageSize) {
    benchmark::DoNotOptimize(c = mapped[i]);
  }

  LOG_IF(FATAL, mapped == MAP_FAILED)
      << "ReadFileWithMMap failed to map file.";
  LOG_IF(FATAL, munmap(mapped, file_size) == -1)
      << "ReadFileWithMMap failed to unmap.";
  LOG_IF(FATAL, close(fd) == -1) << "ReadFileWithMMap cannot close file!";
}
}  // namespace

class LowLevelIOFixture : public benchmark::Fixture {
 public:
  static constexpr char kTestFileName[] = "test_data_file";
  void SetUp(const ::benchmark::State& state) {
    path_ = kTestFileName;
    WriteTestDataFile(path_, state.range(0));
  }

  void TearDown(const ::benchmark::State& state) {
    if (std::filesystem::exists(path_)) {
      LOG_IF(FATAL, !std::filesystem::remove(path_))
          << "Unable to remove the existing file: " << path_;
    }
  }

  std::filesystem::path path_;
};

BENCHMARK_DEFINE_F(LowLevelIOFixture, ReadFileWithSyscallTest)
(benchmark::State& st) {
  for (auto _ : st) {
    ReadFileWithSyscall(path_, st.range(0));
  }
}

// File sizes: 1K, 32K, 1M, 32M, 1G.
BENCHMARK_REGISTER_F(LowLevelIOFixture, ReadFileWithSyscallTest)
    ->Arg(1 << 10)
    ->Arg(1 << 15)
    ->Arg(1 << 20)
    ->Arg(1 << 25)
    ->Arg(1 << 30);

BENCHMARK_DEFINE_F(LowLevelIOFixture, ReadFileWithFreadTest)
(benchmark::State& st) {
  for (auto _ : st) {
    ReadFileWithFread(path_, st.range(0));
  }
}

// File sizes: 1K, 32K, 1M, 32M, 1G.
BENCHMARK_REGISTER_F(LowLevelIOFixture, ReadFileWithFreadTest)
    ->Arg(1 << 10)
    ->Arg(1 << 15)
    ->Arg(1 << 20)
    ->Arg(1 << 25)
    ->Arg(1 << 30);

BENCHMARK_DEFINE_F(LowLevelIOFixture, ReadFileWithFStreamTest)
(benchmark::State& st) {
  for (auto _ : st) {
    ReadFileWithIfstream(path_, st.range(0));
  }
}

// File sizes: 1K, 32K, 1M, 32M, 1G.
BENCHMARK_REGISTER_F(LowLevelIOFixture, ReadFileWithFStreamTest)
    ->Arg(1 << 10)
    ->Arg(1 << 15)
    ->Arg(1 << 20)
    ->Arg(1 << 25)
    ->Arg(1 << 30);

BENCHMARK_DEFINE_F(LowLevelIOFixture, ReadFileWithMMapTest)
(benchmark::State& st) {
  for (auto _ : st) {
    ReadFileWithMMap(path_, st.range(0));
  }
}

// File sizes: 1K, 32K, 1M, 32M, 1G.
BENCHMARK_REGISTER_F(LowLevelIOFixture, ReadFileWithMMapTest)
    ->Arg(1 << 10)
    ->Arg(1 << 15)
    ->Arg(1 << 20)
    ->Arg(1 << 25)
    ->Arg(1 << 30);

}  // namespace experimental

BENCHMARK_MAIN();