filegroup(
    name = "srcs",
    srcs = [
        "split.cpp",
        "BUILD",
    ],
    visibility = ["//third_party:__pkg__"],
)

cc_binary(
    name = "vina_split",
    srcs = ["split.cpp"],
    deps = [
        "//third_party/vina/lib:vina_libs",
        "@boost//:filesystem",
        "@boost//:program_options",
    ],
)
