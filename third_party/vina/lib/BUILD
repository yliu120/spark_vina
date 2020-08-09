licenses(["notice"])

cc_library(
    name = "vina_libs",
    srcs = glob(["*.cpp"]) + glob(["*.cc"]),
    hdrs = glob(["*.h"]),
    visibility = ["//visibility:public"],
    deps = [
        "@absl//absl/algorithm:container",
        "@absl//absl/base",
        "@absl//absl/memory:memory",
        "@absl//absl/strings",
        "@absl//absl/types:span",
        "@eigen//:eigen_minimal",
        "@glog",
    ],
)
