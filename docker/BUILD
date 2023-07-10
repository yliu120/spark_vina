package(default_visibility = ["//visibility:public"])

genrule(
    name = "copy_srcs",
    srcs = [
        "//:spark_vina_srcs.tar",
    ],
    outs = ["tmpdir.txt"],
    cmd = " && ".join([
        "SPARK_VINA_TMP_DIR=$$(mktemp -d)",
        "tar -C $$SPARK_VINA_TMP_DIR -xf $(locations //:spark_vina_srcs.tar)",
        "echo $$SPARK_VINA_TMP_DIR > $@",
    ]),
    message = "UNTARing srcs files to a tmp dir.",
)

genrule(
    name = "build_spark_vina_jar",
    srcs = [
        ":tmpdir.txt",
    ],
    outs = ["spark_vina_main_deploy.jar"],
    cmd = " ".join([
        "docker run",
        "-v $$(cat $(locations :tmpdir.txt)):/src/workspace",
        "-v /tmp/build_output:/tmp/build_output",
        "-w /src/workspace",
        "l.gcr.io/google/bazel:latest",
        "--output_user_root=/tmp/build_output build -s -c opt",
        "--config=avx2 -- //:spark_vina_main_deploy.jar",
        " && ",
        "cp $$(cat $(locations :tmpdir.txt))/bazel-bin/spark_vina_main_deploy.jar $@",
    ]),
    message = "Build SparkVina inside Bazel Container...",
)
