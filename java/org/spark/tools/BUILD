load("@rules_java//java:defs.bzl", "java_binary", "java_library")

package(default_visibility = ["//visibility:public"])

filegroup(
    name = "srcs",
    srcs = glob(["**"]),
)

java_library(
    name = "library_loader",
    srcs = ["LibraryLoader.java"],
    deps = [
        "@maven//:org_slf4j_slf4j_api",
    ],
)

java_library(
    name = "zinc_utils",
    srcs = ["ZincUtils.java"],
    resources = [
        "//java/jni:zinc_utils_jni_all",
    ],
    deps = [
        ":library_loader",
        "//java/jni:zinc_utils_jni_all",
        "//protos:compound_java_proto",
        "@com_google_protobuf//:protobuf_java",
        "@maven//:com_google_guava_guava",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

java_binary(
    name = "zinc_files_to_parquet_main",
    srcs = [
        "CompoundMetadata.java",
        "PhLevel.java",
        "ZincFilesToParquetMain.java",
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
        "@maven//:commons_cli_commons_cli_1_5_0",
        "@maven//:org_apache_spark_spark_catalyst_2_12",
        "@maven//:org_apache_spark_spark_core_2_12",
        "@maven//:org_apache_spark_spark_sql_2_12",
        "@maven//:org_scala_lang_scala_library",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

java_binary(
    name = "zinc_files_to_parquet_v2_main",
    srcs = [
        "CompoundMetadata.java",
        "PhLevel.java",
        "ZincFilesToParquetV2Main.java",
    ],
    main_class = "org.spark.tools.ZincFilesToParquetV2Main",
    resources = [
         "//:log4j.properties",
    ],
    deps = [
        ":zinc_utils",
        "//:spark_vina_lib",
        "//protos:compound_java_proto",
        "@maven//:com_google_guava_guava",
        "@maven//:commons_cli_commons_cli_1_5_0",
#        "@maven//:org_apache_spark_spark_catalyst_2_12",
#        "@maven//:org_apache_spark_spark_core_2_12",
#        "@maven//:org_apache_spark_spark_sql_2_12",
#        "@maven//:org_scala_lang_scala_library",
        "@maven//:org_slf4j_slf4j_api",
    ],
)
