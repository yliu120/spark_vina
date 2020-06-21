package(default_visibility = ["//visibility:public"])

filegroup(
    name = "srcs",
    srcs = glob(["*"], exclude=["*test.cc",]),
)

cc_library(
    name = "parse_pdbqt",
    srcs = ["parse_pdbqt.cc"],
    hdrs = ["parse_pdbqt.h"],
    deps = [
        "//third_party/vina/lib:vina_libs",
        "@boost//:iostreams",
    ],
)

cc_test(
    name = "parse_pdbqt_test",
    srcs = ["parse_pdbqt_test.cc"],
    data = [
        "//data:test_data",
    ],
    deps = [
        ":parse_pdbqt",
        "@com_google_googletest//:gtest",
        "@com_google_googletest//:gtest_main",
    ],
)

cc_library(
    name = "vina",
    srcs = ["vina.cc"],
    hdrs = ["vina.h"],
    deps = [
        ":parse_pdbqt",
        "//protos:vina_cc_proto",
        "//third_party/vina/lib:vina_libs",
        "@boost//:filesystem",
        "@boost//:program_options",
        "@boost//:thread",
        "@glog//:glog",
    ],
)

cc_test(
    name = "vina_test",
    srcs = ["vina_test.cc"],
    data = [
        "//data:test_data",
    ],
    linkopts = [
        "-lprofiler",
    ],
    deps = [
        ":vina",
        "@com_google_googletest//:gtest",
        "@com_google_googletest//:gtest_main",
    ],
)
