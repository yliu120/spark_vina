load("@rules_java//java:defs.bzl", "java_binary", "java_library", "java_test")
load("@bazel_tools//tools/build_defs/pkg:pkg.bzl", "pkg_tar")
# load("@io_bazel_rules_docker//container:image.bzl", "container_image")

package(default_visibility = ["//visibility:public"])

licenses(["notice"])

filegroup(
    name = "srcs",
    srcs = glob(
        ["*"],
        exclude = [
            "bazel-*",  # convenience symlinks
            "out",  # IntelliJ with setup-intellij.sh
            "data",  # output of compile.sh
            ".*",  # mainly .git* files
            "README.md",
        ],
    ) + [
        "//cc:srcs",
        "//third_party:srcs",
        "//protos:srcs",
        "//java/jni:srcs",
        "//java/org/spark/tools:srcs",
    ] + [".bazelrc"] + glob(["java/**/*"]),
)

pkg_tar(
    name = "spark_vina_srcs",
    srcs = [":srcs"],
    mode = "0755",
    # Adds strip_prefix to preserve the original directory structure.
    strip_prefix = "./",
)

java_library(
    name = "spark_vina_lib",
    srcs = glob(
        ["java/org/spark_vina/*.java"],
        exclude = [
            "java/org/spark_vina/SparkVinaMain.java",
        ],
    ),
    resources = [
        "//java/jni:vina_jni_all",
    ],
    deps = [
        "//java/jni:vina_jni_all",
        "//java/org/spark/tools:library_loader",
        "//protos:vina_java_proto",
        "@com_google_protobuf//:protobuf_java",
        "@maven//:com_google_guava_guava",
        "@maven//:org_apache_spark_spark_catalyst_2_13",
        "@maven//:org_apache_spark_spark_core_2_13",
        "@maven//:org_slf4j_slf4j_api",
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
        "@maven//:org_hamcrest_hamcrest",
    ],
)

java_binary(
    name = "spark_vina_main",
    srcs = ["java/org/spark_vina/SparkVinaMain.java"],
    main_class = "org.spark_vina.SparkVinaMain",
    resources = [
        "log4j.properties",
    ],
    deps = [
        ":spark_vina_lib",
        "//protos:vina_java_proto",
        "@maven//:com_google_guava_guava",
        "@maven//:commons_cli_commons_cli_1_4",
        "@maven//:org_apache_spark_spark_catalyst_2_13",
        "@maven//:org_apache_spark_spark_core_2_13",
        "@maven//:org_apache_spark_spark_sql_2_13",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

# container_image(
#    name = "spark_vina_image",
#    base = "@java_base_modified//image:dockerfile_image.tar",
#    entrypoint = [
#        "java",
#        "-jar",
#        "/spark_vina_main_deploy.jar",
#    ],
#    files = ["//docker:spark_vina_main_deploy.jar"],
#    ports = [
#        # Ports for Spark WebUI
#        "4040",
#    ],
#    repository = "spark_vina/spark_vina",
#    stamp = 1,
#    volumes = [
#        "/workspace",
#    ],
#)

#container_image(
#    name = "spark_vina_k8s",
#    base = "@spark_base//image",
#    data_path = ".",
#    files = [
#        "//data:test_data",
#        "//docker:spark_vina_main_deploy.jar",
#    ],
#    repository = "spark_vina/spark_vina",
#    stamp = 1,
#    volumes = [
#        "/workspace",
#    ],
#)
