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
        "//third_party/vina/lib:vina_libs",
        "@boost//:filesystem",
        "@boost//:program_options",
        "@boost//:thread",
    ],
)

py_binary(
    name = "vina_example_py",
    srcs = ["vina_example.py"],
    data = ["//data:test_data"],
    main = "vina_example.py",
    deps = [":vina_wrap"],
)

cc_test(
    name = "vina_test",
    srcs = ["vina_test.cc"],
    data = [
        "//data:test_data",
    ],
    deps = [
        ":vina",
        "@com_google_googletest//:gtest",
        "@com_google_googletest//:gtest_main",
    ],
)
