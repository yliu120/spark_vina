load("@rules_cc//cc:defs.bzl", "cc_library", "cc_test")

package(default_visibility = ["//visibility:public"])

filegroup(
    name = "srcs",
    srcs = glob(
        ["*"],
        exclude = ["*test.cc"],
    ),
)

cc_library(
    name = "execution_context",
    hdrs = ["execution_context.h"],
    deps = [
        "//third_party/vina/lib:vina_libs",
    ],
)

cc_library(
    name = "task",
    srcs = ["task.cc"],
    hdrs = ["task.h"],
    deps = [
        ":execution_context",
        "//third_party/vina/lib:vina_libs",
        "@glog",
    ],
)

cc_library(
    name = "thread_pool",
    srcs = ["thread_pool.cc"],
    hdrs = ["thread_pool.h"],
    deps = [
        ":execution_context",
        ":task",
        "@absl//absl/base",
    ]
)

cc_library(
    name = "lock_free_thread_pool",
    srcs = ["lock_free_thread_pool.cc"],
    hdrs = ["lock_free_thread_pool.h"],
    deps = [
        ":execution_context",
        ":task",
        "@absl//absl/types:span",
    ]
)

cc_test(
    name = "lock_free_thread_pool_test",
    srcs = ["lock_free_thread_pool_test.cc"],
    deps = [
        ":lock_free_thread_pool",
        ":task",
        "@absl//absl/types:span",
        "@com_google_googletest//:gtest_main",
        "@glog",
    ]
)


