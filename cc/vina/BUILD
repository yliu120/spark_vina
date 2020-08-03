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


