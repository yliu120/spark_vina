load("@rules_cc//cc:defs.bzl", "cc_proto_library")
load("@rules_java//java:defs.bzl", "java_proto_library")
load("@rules_java//java:defs.bzl", "java_test")
load("@rules_proto//proto:defs.bzl", "proto_library")

package(default_visibility = ["//visibility:public"])
licenses(["notice"])

config_setting(
    name = "macos",
    constraint_values = ["@bazel_tools//platforms:osx"],
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
        ":vina_cc_proto",
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

java_library(
    name = "spark_vina_lib",
    srcs = glob(["java/org/spark_vina/*.java"]),
    resources = [
        "//java/jni:libvina_jni_all.so",
    ],
    deps = [
        ":vina_java_proto",
        "//java/jni:libvina_jni_all.so",
        "@com_google_protobuf//:protobuf_java",
    ],
)

java_test(
    name = "spark_vina_lib_test",
    srcs = ["javatests/org/spark_vina/VinaDockTest.java"],
    data = [
        "//data:test_data",
    ],
    test_class = "org.spark_vina.VinaDockTest",
    deps = [
        ":vina_java_proto",
        ":spark_vina_lib",
        "@maven//:junit_junit",
        "@maven//:org_hamcrest_hamcrest_library",
    ],
    jvm_flags = [
        "-Dorg.spark_vina.LibraryLoader.DEBUG=1",
    ],
)