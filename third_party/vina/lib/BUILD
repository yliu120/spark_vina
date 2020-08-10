load("@rules_cc//cc:defs.bzl", "cc_library", "cc_test")

licenses(["notice"])

cc_library(
    name = "vina_libs",
    srcs = glob(["*.cpp"]) + glob(
        ["*.cc"],
        exclude = ["quaternion_test.cc"],
    ),
    hdrs = glob(["*.h"]),
    visibility = ["//visibility:public"],
    deps = [
        "@absl//absl/algorithm:container",
        "@absl//absl/base",
        "@absl//absl/memory",
        "@absl//absl/strings",
        "@absl//absl/types:span",
        "@eigen//:eigen_minimal",
        "@glog",
    ],
)

cc_test(
    name = "quaternion_test",
    srcs = ["quaternion_test.cc"],
    deps = [
        ":vina_libs",
        "@com_google_googletest//:gtest_main",
    ],
)
