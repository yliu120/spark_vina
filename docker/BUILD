load(
    "@io_bazel_rules_docker//container:container.bzl",
    "container_pull",
)
load(
    "@io_bazel_rules_docker//docker/util:run.bzl", "container_run_and_extract",
)

container_pull(
  name = "java_base",
  registry = "gcr.io",
  repository = "distroless/java",
  # 'tag' is also supported, but digest is encouraged for reproducibility.
  digest = "sha256:deadbeef",
)

container_run_and_extract(
    name = "BuildSparkVinaJar",
    commands = [
        "..."
    ],
    extract_file = "",
    image = "",
)