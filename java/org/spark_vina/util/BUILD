load("@rules_java//java:defs.bzl", "java_library")

package(default_visibility = ["//visibility:public"])

java_library(
    name = "pdbqt_parser_helper",
    srcs = ["PdbqtParserHelper.java"],
    deps = [
        "//protos:compound_java_proto",
        "@maven//:com_google_guava_guava_29_0_jre",
        "@maven//:org_slf4j_slf4j_api_1_7_30",
    ],
)

java_library(
    name = "zinc_helper",
    srcs = ["ZincHelper.java"],
    deps = [
        "@maven//:com_google_guava_guava_29_0_jre",
    ],
)
