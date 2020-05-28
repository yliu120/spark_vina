load("@rules_java//java:defs.bzl", "java_library")

package(default_visibility = ["//visibility:public"])

java_library(
    name = "pdbqt_parser_helper",
    srcs = ["PdbqtParserHelper.java"],
    deps = [
        "//protos:compound_java_proto",
        "@maven//:com_google_guava_guava",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

java_library(
    name = "zinc_helper",
    srcs = ["ZincHelper.java"],
    deps = [
        "@maven//:com_google_guava_guava",
    ],
)
