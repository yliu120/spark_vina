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
        "@maven//:com_google_guava_guava_29_0_jre",
        "@maven//:org_apache_spark_spark_core_2_12_2_4_5",
        "@maven//:org_slf4j_slf4j_api_1_7_30",
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
        "@maven//:org_hamcrest_hamcrest_library_2_2",
        "@maven//:junit_junit_4_13",
    ],
    jvm_flags = [
        "-Dorg.spark_vina.LibraryLoader.DEBUG=1",
    ],
)

java_binary(
    name = "spark_vina_main",
    srcs = ["java/org/spark_vina/SparkVinaMain.java"],
    resources = [
        "log4j.properties",
    ],
    main_class = "org.spark_vina.SparkVinaMain",
    deps = [
        ":spark_vina_lib",
        "//protos:vina_java_proto",
        "@maven//:commons_cli_commons_cli_1_4",
        "@maven//:com_google_guava_guava_29_0_jre",
        "@maven//:org_apache_spark_spark_core_2_12_2_4_5",
        "@maven//:org_apache_spark_spark_sql_2_12_2_4_5",
        "@maven//:org_slf4j_slf4j_api_1_7_30",
    ]
)