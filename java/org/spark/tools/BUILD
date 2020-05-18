load("@rules_java//java:defs.bzl", "java_binary")

java_binary(
    name = "zinc_files_to_parquet_main",
    srcs = ["ZincFilesToParquetMain.java"],
    main_class = "org.spark.tools.ZincFilesToParquetMain",
    resources = [
        "//:log4j.properties",
    ],
    deps = [
        "//:spark_vina_lib",
        "//java/org/spark_vina/util:pdbqt_parser_helper",
        "//java/org/spark_vina/util:zinc_helper",
        "//protos:compound_java_proto",
        "@maven//:com_google_guava_guava_29_0_jre",
        "@maven//:commons_cli_commons_cli_1_4",
        "@maven//:org_apache_spark_spark_catalyst_2_11_2_4_5",
        "@maven//:org_apache_spark_spark_core_2_11_2_4_5",
        "@maven//:org_apache_spark_spark_sql_2_11_2_4_5",
        "@maven//:org_slf4j_slf4j_api_1_7_30",
    ],
)
