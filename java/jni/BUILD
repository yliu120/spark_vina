package(default_visibility = ["//visibility:public"])

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
        "//:macos": ["@bazel_tools//tools/jdk:jni_md_header-darwin"],
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
    deps = []
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
        "//:vina",
        "//:vina_cc_proto",
    ],
    alwayslink = 1,
)

cc_binary(
    name = "libvina_jni_all.so",
    deps = [
        ":vina_jni",
        "//:vina",
        "//:vina_cc_proto",
    ],
    linkstatic = 1,
    linkshared = 1,
)