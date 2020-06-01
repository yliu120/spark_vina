load("@rules_cc//cc:defs.bzl", "cc_binary")

package(default_visibility = ["//visibility:public"])

cc_binary(
    name = "example",
    srcs = ["example.cc"],
    deps = [
        "@absl//absl/strings",
        "@com_github_gflags_gflags//:gflags",
        "@glog",
        "@openbabel//:supported_formats",
    ],
)
