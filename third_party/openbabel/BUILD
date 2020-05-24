package(default_visibility = ["//visibility:public"])

load("@rules_cc//cc:defs.bzl", "cc_binary")

cc_binary(
    name = "example",
    srcs = ["example.cc"],
    deps = [
        "@glog//:glog",
        "@openbabel//:core",
        "@openbabel//:formats",
    ]
)