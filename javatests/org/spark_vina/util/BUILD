load("@rules_java//java:defs.bzl", "java_test")

package(default_visibility = ["//visibility:public"])

java_test(
    name = "pdbqt_parser_helper_test",
    srcs = ["PdbqtParserHelperTest.java"],
    test_class = "org.spark_vina.util.PdbqtParserHelperTest",
    deps = [
        "//java/org/spark_vina/util:pdbqt_parser_helper",
        "//protos:compound_java_proto",
        "@maven//:com_google_guava_guava_29_0_jre",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:com_google_truth_extensions_truth_proto_extension_1_0_1",
        "@maven//:com_google_truth_truth_1_0_1",
        "@maven//:junit_junit_4_13",
    ],
)
