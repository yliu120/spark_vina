licenses(["notice"])

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
