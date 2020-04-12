load("@rules_cc//cc:defs.bzl", "cc_proto_library")
load("@rules_java//java:defs.bzl", "java_proto_library")
load("@rules_java//java:defs.bzl", "java_test")
load("@rules_proto//proto:defs.bzl", "proto_library")

package(default_visibility = ["//visibility:public"])
licenses(["notice"])

java_library(
    name = "spark_vina_lib",
    srcs = glob(["java/org/spark_vina/*.java"],
                exclude = [
                    "java/org/spark_vina/SparkVinaMain.java",
                ]),
    resources = [
        "//java/jni:vina_jni_all",
    ],
    deps = [
        "//protos:vina_java_proto",
        "//java/jni:vina_jni_all",
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
        ":spark_vina_lib",
        "//protos:vina_java_proto",
        "@maven//:junit_junit",
        "@maven//:org_hamcrest_hamcrest_library",
    ],
    jvm_flags = [
        "-Dorg.spark_vina.LibraryLoader.DEBUG=1",
    ],
)