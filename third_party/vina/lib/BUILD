licenses(["notice"])

cc_library(
    name = "vina_libs",
    srcs = glob(["*.cpp"]),
    hdrs = glob(["*.h"]),
    visibility = ["//visibility:public"],
    deps = [
        "@absl//absl/strings",
        "@boost//:ptr_container",
        "@boost//:serialization",
        "@boost//:thread",
    ],
)
