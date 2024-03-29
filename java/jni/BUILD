load("@rules_cc//cc:defs.bzl", "cc_library")
load("//:spark_vina.bzl", "sv_cc_shared_library")

package(default_visibility = ["//visibility:public"])

filegroup(
    name = "srcs",
    srcs = glob(["*"]),
)

# Inspired from:
# https://github.com/bazelbuild/bazel/blob/f99a0543f8d97339d32075c7176b79f35be84606/src/main/native/BUILD
# but hopefully there is a simpler alternative to this.
genrule(
    name = "copy_jni_h",
    srcs = ["@bazel_tools//tools/jdk:jni_header"],
    outs = ["jni.h"],
    cmd = "cp -f $< $@",
)

genrule(
    name = "copy_jni_md_h",
    srcs = select({
        "@bazel_tools//src/conditions:darwin": [
            "@bazel_tools//tools/jdk:jni_md_header-darwin",
        ],
        "@bazel_tools//src/conditions:darwin_x86_64": [
            "@bazel_tools//tools/jdk:jni_md_header-darwin",
        ],
        "//conditions:default": ["@bazel_tools//tools/jdk:jni_md_header-linux"],
    }),
    outs = ["jni_md.h"],
    cmd = "cp -f $< $@",
)

filegroup(
    name = "jni_headers",
    srcs = [
        ":jni.h",
        ":jni_md.h",
    ],
)

cc_library(
    name = "jni_utils",
    srcs = ["jni_utils.cc"],
    hdrs = [
        "jni_utils.h",
        ":jni_headers",
    ],
    includes = ["."],
    deps = [],
)

cc_library(
    name = "vina_jni",
    srcs = [
        "vina_jni.cc",
        "vina_tools_jni.cc",
    ],
    hdrs = [
        ":jni_headers",
    ],
    includes = ["."],
    deps = [
        ":jni_utils",
        "//cc:vina",
        "//protos:vina_cc_proto",
    ],
    alwayslink = 1,
)

cc_library(
    name = "zinc_utils_jni",
    srcs = ["zinc_utils_jni.cc"],
    hdrs = [
        ":jni_headers",
    ],
    includes = ["."],
    deps = [
        ":jni_utils",
        "//cc/zinc:utils",
        "//protos:compound_cc_proto",
    ],
    alwayslink = 1,
)

sv_cc_shared_library(
    name = "vina_jni_all",
    deps = [
        ":vina_jni",
        "//cc:vina",
        "//protos:vina_cc_proto",
    ],
)

sv_cc_shared_library(
    name = "zinc_utils_jni_all",
    deps = [
        ":zinc_utils_jni",
        "//cc/zinc:utils",
        "//protos:compound_cc_proto",
    ],
)
