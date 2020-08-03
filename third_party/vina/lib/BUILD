licenses(["notice"])

cc_library(
    name = "vina_libs",
    srcs = glob(["*.cpp"]),
    hdrs = glob(["*.h"]),
    visibility = ["//visibility:public"],
    deps = [
        "@absl//absl/algorithm:container",
        "@absl//absl/memory:memory",
        "@absl//absl/strings",
        "@boost//:serialization",
        "@boost//:thread",
    ],
)
