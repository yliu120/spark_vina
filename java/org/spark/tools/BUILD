package(default_visibility = ["//visibility:public"])

load("@rules_java//java:defs.bzl", "java_library", "java_binary")

java_library(
    name = "library_loader",
    srcs = ["LibraryLoader.java"],
    deps = [
        "@maven//:org_slf4j_slf4j_api",
    ]
)

java_library(
    name = "zinc_utils",
    srcs = ["ZincUtils.java"],
    deps = [
        ":library_loader",
        "//java/jni:zinc_utils_jni_all",
        "//protos:compound_java_proto",
        "@com_google_protobuf//:protobuf_java",
        "@maven//:com_google_guava_guava",
        "@maven//:org_slf4j_slf4j_api",
    ]
)

java_binary(
    name = "zinc_files_to_parquet_main",
    srcs = [
        "PhLevel.java",
        "CompoundMetadata.java",
        "ZincFilesToParquetMain.java"
    ],
    main_class = "org.spark.tools.ZincFilesToParquetMain",
    resources = [
        "//:log4j.properties",
    ],
    deps = [
        ":zinc_utils",
        "//:spark_vina_lib",
        "//protos:compound_java_proto",
        "@maven//:com_google_guava_guava",
        "@maven//:commons_cli_commons_cli_1_4",
        "@maven//:org_apache_spark_spark_catalyst_2_12",
        "@maven//:org_apache_spark_spark_core_2_12",
        "@maven//:org_apache_spark_spark_sql_2_12",
        "@maven//:org_scala_lang_scala_library",
        "@maven//:org_slf4j_slf4j_api",
    ],
)
