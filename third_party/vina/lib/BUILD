licenses(["notice"])

cc_library(
    name = "vina_libs",
    srcs = glob(["*.cpp"]),
    hdrs = glob(["*.h"]),
    visibility = ["//visibility:public"],
    deps = [
        "@absl//absl/strings",
        "@boost//:array",
        "@boost//:math",
        "@boost//:optional",
        # TODO: Change backs to "@boost//:progress" after rules_boost supports
        # the progress lib.
        "//third_party/boost:progress",
        "@boost//:ptr_container",
        "@boost//:random",
        "@boost//:serialization",
        "@boost//:static_assert",
        "@boost//:thread",
        "@boost//:utility",
    ],
)
