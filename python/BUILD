load("//third_party/swig:swig.bzl", "py_wrap_cc")

package(default_visibility = ["//visibility:public"])

alias(
    name = "python_headers",
    actual = "@local_config_python//:python_headers",
)

py_wrap_cc(
    name = "vina_wrap",
    srcs = ["vina.swig"],
    deps = [
        ":python_headers",
        "//cc:vina",
        "//protos:vina_cc_proto",
    ],
    module_name = "vina",
    py_deps = [
        "//protos:vina_proto_py",
    ]
)

py_binary(
    name = "vina_example_py",
    srcs = ["vina_example.py"],
    data = ["//data:test_data"],
    main = "vina_example.py",
    deps = [
        ":vina_wrap",
    ],
)

py_binary(
    name = "spark_vina_app",
    srcs = ["spark_vina_app.py"],
    deps = [
        ":vina_wrap",
    ],
)