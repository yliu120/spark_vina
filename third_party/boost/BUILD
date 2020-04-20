package(default_visibility = ["//visibility:public"])

filegroup(
    name = "srcs",
    srcs = glob(["*"]),
)

cc_library(
    name = "progress",
    hdrs = ["progress.hpp"],
    deps = [
        "@boost//:cstdint",
        "@boost//:noncopyable",
        "@boost//:timer",
    ],
)