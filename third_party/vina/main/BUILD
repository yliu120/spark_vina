filegroup(
    name = "srcs",
    srcs = [
        "main.cpp",
        "BUILD",
    ],
    visibility = ["//third_party:__pkg__"],
)

cc_binary(
    name = "vina",
    srcs = ["main.cpp"],
    deps = [
        "//third_party/vina/lib:vina_libs",
        "@boost//:filesystem",
        "@boost//:program_options",
        "@boost//:thread",
    ],
)
