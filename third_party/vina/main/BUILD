licenses(["notice"])

cc_binary(
    name = "vina",
    srcs = ["main.cpp"],
    copts = select({
        "//:marcc": [
            "-march=sandybridge",
            "-mtune=haswell",
        ],
        "//conditions:default": [],
    }),
    deps = [
        "//third_party/vina/lib:vina_libs",
        "@boost//:filesystem",
        "@boost//:program_options",
        "@boost//:thread",
    ],
)
