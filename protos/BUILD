load("@rules_proto//proto:defs.bzl", "proto_library")
load("@rules_cc//cc:defs.bzl", "cc_proto_library")
load("@rules_java//java:defs.bzl", "java_proto_library")
load("@com_google_protobuf//:protobuf.bzl", "py_proto_library")

package(default_visibility = ["//visibility:public"])

filegroup(
    name = "srcs",
    srcs = glob(["*"]),
)

proto_library(
    name = "vina_proto",
    srcs = ["vina.proto"],
)

cc_proto_library(
    name = "vina_cc_proto",
    deps = [":vina_proto"],
)

java_proto_library(
    name = "vina_java_proto",
    deps = [":vina_proto"],
)

py_proto_library(
    name = "vina_proto_py",
    srcs = ["vina.proto"],
)