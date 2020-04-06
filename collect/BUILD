load("@io_bazel_rules_go//go:def.bzl", "go_binary", "go_library", "go_test")

go_library(
    name = "process",
    srcs = [
        "process.go",
    ],
    importpath = "spark_vina/collect/process",
    visibility = ["//visibility:public"],
)

go_test(
    name = "process_test",
    srcs = ["process_test.go"],
    data = ["//spark_vina/data:pdbqt"],
    importpath = "spark_vina/collect/process_test",
    deps = [":process"],
)

go_binary(
    name = "collect",
    srcs = [
        "collect.go",
    ],
    importpath = "spark_vina/collect/collect",
    visibility = ["//visibility:public"],
    deps = [":process"],
)
