load("@subpar//:subpar.bzl", "par_binary")
load("//third_party/swig:swig.bzl", "fiesta_py_wrap_cc")

cc_library(
    name = "parse_pdbqt",
    srcs = ["parse_pdbqt.cc"],
    hdrs = ["parse_pdbqt.h"],
    copts = select({
        "//:marcc": [
            "-march=sandybridge",
            "-mtune=haswell",
        ],
        "//conditions:default": [],
    }),
    deps = [
        "//third_party/vina/lib:vina_libs",
        "@boost//:iostreams",
    ],
)

cc_test(
    name = "parse_pdbqt_test",
    srcs = ["parse_pdbqt_test.cc"],
    data = [
        "//spark_vina/data:test_data",
    ],
    deps = [
        ":parse_pdbqt",
        "//external:gtest",
        "//external:gtest_main",
    ],
)

cc_library(
    name = "vina",
    srcs = ["vina.cc"],
    hdrs = ["vina.h"],
    copts = select({
        "//:marcc": [
            "-march=sandybridge",
            "-mtune=haswell",
        ],
        "//conditions:default": [],
    }),
    deps = [
        ":parse_pdbqt",
        "//third_party/vina/lib:vina_libs",
        "@boost//:filesystem",
        "@boost//:program_options",
        "@boost//:thread",
    ],
)

fiesta_py_wrap_cc(
    name = "vina_wrap",
    srcs = ["vina.i"],
    deps = [
        ":vina",
        "//util/python:python_headers",
    ],
)

py_binary(
    name = "vina_example_py",
    srcs = ["vina_example.py"],
    data = ["//spark_vina/data:test_data"],
    main = "vina_example.py",
    deps = [":vina_wrap"],
)

cc_test(
    name = "vina_test",
    srcs = ["vina_test.cc"],
    data = [
        "//spark_vina/data:test_data",
    ],
    deps = [
        ":vina",
        "//external:gtest",
        "//external:gtest_main",
    ],
)

par_binary(
    name = "spark_vina_app",
    srcs = ["spark_vina_app.py"],
    deps = [
        ":vina_wrap",
    ],
)
