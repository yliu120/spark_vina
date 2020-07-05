# Util functions used in parsing ZINC helpers.

load("@rules_cc//cc:defs.bzl", "cc_library", "cc_test")

package(default_visibility = ["//visibility:public"])

cc_library(
    name = "utils",
    srcs = ["utils.cc"],
    hdrs = ["utils.h"],
    deps = [
        "//protos:compound_cc_proto",
        "@absl//absl/strings",
        "@glog",
        "@openbabel//:supported_formats",
    ],
)

cc_test(
    name = "utils_test",
    srcs = ["utils_test.cc"],
    deps = [
        ":utils",
        "@com_google_googletest//:gtest_main",
    ],
)
