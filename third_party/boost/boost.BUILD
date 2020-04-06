load("@fiesta3//third_party/boost:boost.bzl", "boost_library")

config_setting(
    name = "mac",
    values = {"cpu": "darwin"},
)

boost_library(
    name = "algorithm",
    deps = [
        ":function",
        ":iterator",
        ":range",
    ],
)

boost_library(
    name = "align",
)

boost_library(
    name = "any",
    deps = [
        ":config",
        ":mpl",
        ":static_assert",
        ":throw_exception",
        ":type_index",
        ":type_traits",
        ":utility",
    ],
)

boost_library(
    name = "atomic",
    hdrs = [
        "boost/memory_order.hpp",
    ],
    deps = [
        ":assert",
        ":config",
        ":cstdint",
        ":type_traits",
    ],
)

boost_library(
    name = "archive",
    deps = [
        ":assert",
        ":cstdint",
        ":integer",
        ":io",
        ":iterator",
        ":mpl",
        ":noncopyable",
        ":smart_ptr",
        ":spirit",
    ],
)

boost_library(
    name = "array",
    deps = [
        ":functional",
        ":swap",
    ],
)

boost_library(
    name = "asio",
    deps = [
        ":bind",
        ":date_time",
        ":regex",
    ],
)

boost_library(
    name = "assert",
)

boost_library(
    name = "bind",
    deps = [
        ":get_pointer",
        ":is_placeholder",
        ":mem_fn",
        ":ref",
        ":type",
        ":visit_each",
    ],
)

boost_library(
    name = "call_traits",
)

boost_library(
    name = "cerrno",
)

boost_library(
    name = "checked_delete",
)

boost_library(
    name = "chrono",
    deps = [
        ":config",
        ":mpl",
        ":operators",
        ":predef",
        ":ratio",
        ":system",
        ":throw_exception",
        ":type_traits",
    ],
)

boost_library(
    name = "circular_buffer",
    deps = [
        ":call_traits",
        ":concept_check",
        ":config",
        ":container",
        ":detail",
        ":iterator",
        ":limits",
        ":move",
        ":static_assert",
        ":throw_exception",
        ":type_traits",
        ":utility",
    ],
)

boost_library(
    name = "concept_archetype",
    deps = [
        ":config",
        ":iterator",
        ":mpl",
    ],
)

boost_library(
    name = "concept_check",
    deps = [
        ":concept",
        ":concept_archetype",
    ],
)

boost_library(
    name = "config",
    deps = [
        ":version",
    ],
)

boost_library(
    name = "concept",
)

boost_library(
    name = "container",
    srcs = [
        "libs/container/src/dlmalloc_ext_2_8_6.c",
    ],
    hdrs = [
        "libs/container/src/dlmalloc_2_8_6.c",
    ],
    deps = [
        ":config",
        ":core",
        ":intrusive",
        ":move",
    ],
)

boost_library(
    name = "conversion",
)

boost_library(
    name = "core",
    srcs = [
        "boost/checked_delete.hpp",
    ],
)

boost_library(
    name = "cstdint",
)

boost_library(
    name = "current_function",
)

boost_library(
    name = "date_time",
    deps = [
        ":algorithm",
        ":io",
        ":lexical_cast",
        ":mpl",
        ":operators",
        ":smart_ptr",
        ":static_assert",
        ":tokenizer",
        ":type_traits",
    ],
)

boost_library(
    name = "detail",
    deps = [
        ":limits",
    ],
)

boost_library(
    name = "dynamic_bitset",
)

boost_library(
    name = "enable_shared_from_this",
)

boost_library(
    name = "exception",
    hdrs = [
        "boost/exception_ptr.hpp",
    ],
    deps = [
        ":config",
        ":detail",
    ],
)

boost_library(
    name = "exception_ptr",
    deps = [
        ":config",
    ],
)

boost_library(
    name = "filesystem",
    copts = [
        "-Wno-deprecated-declarations",
    ],
    deps = [
        ":config",
        ":functional",
        ":io",
        ":iterator",
        ":range",
        ":smart_ptr",
        ":system",
        ":type_traits",
    ],
)

boost_library(
    name = "foreach",
)

boost_library(
    name = "format",
    deps = [
        ":assert",
        ":config",
        ":detail",
        ":limits",
        ":optional",
        ":smart_ptr",
        ":throw_exception",
        ":timer",
        ":utility",
    ],
)

boost_library(
    name = "function",
    hdrs = [
        "boost/function_equal.hpp",
    ],
    deps = [
        ":bind",
        ":integer",
        ":type_index",
    ],
)

boost_library(
    name = "function_types",
)

boost_library(
    name = "functional",
    deps = [
        ":detail",
        ":integer",
    ],
)

boost_library(
    name = "fusion",
    deps = [
        ":call_traits",
        ":config",
        ":core",
        ":detail",
        ":function_types",
        ":functional",
        ":get_pointer",
        ":mpl",
        ":preprocessor",
        ":ref",
        ":static_assert",
        ":tuple",
        ":type_traits",
        ":typeof",
        ":utility",
    ],
)

boost_library(
    name = "property_tree",
    deps = [
        ":any",
        ":bind",
        ":format",
        ":multi_index",
        ":optional",
        ":range",
        ":ref",
        ":throw_exception",
        ":utility",
    ],
)

boost_library(
    name = "get_pointer",
)

boost_library(
    name = "heap",
    deps = [
        ":parameter",
    ],
)

boost_library(
    name = "is_placeholder",
)

boost_library(
    name = "integer",
    hdrs = [
        "boost/cstdint.hpp",
        "boost/integer_traits.hpp",
        "boost/pending/integer_log2.hpp",
    ],
    deps = [
        ":static_assert",
    ],
)

boost_library(
    name = "interprocess",
    deps = [
        ":assert",
        ":checked_delete",
        ":config",
        ":container",
        ":core",
        ":date_time",
        ":detail",
        ":integer",
        ":intrusive",
        ":limits",
        ":move",
        ":static_assert",
        ":type_traits",
        ":unordered",
        ":utility",
    ],
)

boost_library(
    name = "iterator",
    hdrs = [
        "boost/function_output_iterator.hpp",
        "boost/pointee.hpp",
    ],
    deps = [
        ":detail",
        ":static_assert",
        ":type_traits",
        ":utility",
    ],
)

boost_library(
    name = "intrusive",
    deps = [
        ":assert",
        ":cstdint",
        ":noncopyable",
        ":static_assert",
    ],
)

boost_library(
    name = "intrusive_ptr",
    deps = [
        ":assert",
        ":detail",
        ":smart_ptr",
    ],
)

boost_library(
    name = "io",
)

boost_library(
    name = "iostreams",
    deps = [
        ":assert",
        ":bind",
        ":call_traits",
        ":checked_delete",
        ":config",
        ":detail",
        ":function",
        ":integer",
        ":mpl",
        ":noncopyable",
        ":preprocessor",
        ":random",
        ":range",
        ":ref",
        ":regex",
        ":shared_ptr",
        ":static_assert",
        ":throw_exception",
        ":type",
        ":type_traits",
        ":utility",
        "@bzip2//:bz2lib",
        "@zlib//:zlib",
    ],
)

boost_library(
    name = "lexical_cast",
    deps = [
        ":array",
        ":chrono",
        ":config",
        ":container",
        ":detail",
        ":integer",
        ":limits",
        ":math",
        ":mpl",
        ":noncopyable",
        ":numeric_conversion",
        ":range",
        ":static_assert",
        ":throw_exception",
        ":type_traits",
    ],
)

boost_library(
    name = "limits",
)

boost_library(
    name = "math",
    hdrs = [
        "boost/cstdint.hpp",
    ],
)

boost_library(
    name = "mem_fn",
)

boost_library(
    name = "move",
    deps = [
        ":assert",
        ":detail",
        ":static_assert",
    ],
)

boost_library(
    name = "mpl",
    deps = [
        ":move",
        ":preprocessor",
    ],
)

boost_library(
    name = "multi_index",
    hdrs = [
        "boost/multi_index_container.hpp",
        "boost/multi_index_container_fwd.hpp",
    ],
    deps = [
        ":foreach",
        ":serialization",
        ":static_assert",
        ":tuple",
    ],
)

boost_library(
    name = "noncopyable",
)

boost_library(
    name = "none",
    hdrs = [
        "boost/none_t.hpp",
    ],
)

boost_library(
    name = "numeric_conversion",
    hdrs = glob([
        "boost/numeric/conversion/**/*.hpp",
    ]),
    deps = [
        ":config",
        ":detail",
        ":integer",
        ":limits",
        ":mpl",
        ":throw_exception",
        ":type",
        ":type_traits",
    ],
)

boost_library(
    name = "numeric_ublas",
    hdrs = glob([
        "boost/numeric/ublas/**/*.hpp",
    ]),
    deps = [
        ":concept_check",
        ":config",
        ":core",
        ":iterator",
        ":mpl",
        ":noncopyable",
        ":numeric",
        ":range",
        ":serialization",
        ":shared_array",
        ":static_assert",
        ":timer",
        ":type_traits",
        ":typeof",
        ":utility",
    ],
)

boost_library(
    name = "operators",
)

boost_library(
    name = "optional",
    deps = [
        ":none",
        ":type",
    ],
)

boost_library(
    name = "parameter",
)

boost_library(
    name = "predef",
)

boost_library(
    name = "preprocessor",
)

boost_library(
    name = "program_options",
    deps = [
        ":any",
        ":bind",
        ":config",
        ":detail",
        ":function",
        ":iterator",
        ":lexical_cast",
        ":limits",
        ":noncopyable",
        ":shared_ptr",
        ":static_assert",
        ":throw_exception",
        ":tokenizer",
        ":type_traits",
        ":version",
    ],
)

boost_library(
    name = "progress",
    deps = [
        ":cstdint",
        ":noncopyable",
        ":timer",
    ],
)

boost_library(
    name = "ptr_container",
    hdrs = ["boost/pointee.hpp"],
    deps = [
        ":assert",
        ":circular_buffer",
        ":config",
        ":core",
        ":iterator",
        ":mpl",
        ":range",
        ":static_assert",
        ":type_traits",
        ":utility",
    ],
)

boost_library(
    name = "random",
    deps = [
        ":assert",
        ":config",
        ":detail",
        ":foreach",
        ":integer",
        ":lexical_cast",
        ":limits",
        ":math",
        ":mpl",
        ":multi_index",
        ":noncopyable",
        ":operators",
        ":range",
        ":regex",
        ":shared_ptr",
        ":static_assert",
        ":system",
        ":test",
        ":throw_exception",
        ":timer",
        ":tuple",
        ":type_traits",
        ":utility",
    ],
)

boost_library(
    name = "range",
    deps = [
        ":array",
        ":assert",
        ":concept_check",
        ":config",
        ":detail",
        ":functional",
        ":integer",
        ":iterator",
        ":mpl",
        ":noncopyable",
        ":optional",
        ":preprocessor",
        ":ref",
        ":regex",
        ":static_assert",
        ":tuple",
        ":type_traits",
        ":utility",
    ],
)

boost_library(
    name = "ratio",
    deps = [
        ":integer",
    ],
)

boost_library(
    name = "ref",
    deps = [
        ":config",
        ":core",
        ":detail",
        ":utility",
    ],
)

boost_library(
    name = "regex",
    hdrs = [
        "boost/cregex.hpp",
    ],
    defines = [
        "BOOST_FALLTHROUGH",
    ],
    deps = [
        ":assert",
        ":config",
        ":cstdint",
        ":detail",
        ":exception",
        ":functional",
        ":integer",
        ":limits",
        ":mpl",
        ":predef",
        ":ref",
        ":smart_ptr",
        ":throw_exception",
        ":type_traits",
    ],
)

boost_library(
    name = "scope_exit",
    deps = [
        ":config",
        ":detail",
        ":function",
        ":mpl",
        ":preprocessor",
        ":type_traits",
        ":typeof",
        ":utility",
    ],
)

boost_library(
    name = "scoped_array",
    deps = [
        ":checked_delete",
    ],
)

boost_library(
    name = "scoped_ptr",
    deps = [
        ":checked_delete",
    ],
)

boost_library(
    name = "shared_ptr",
    deps = [
        ":checked_delete",
    ],
)

boost_library(
    name = "shared_array",
    deps = [
        ":checked_delete",
    ],
)

boost_library(
    name = "signals2",
    deps = [
        ":assert",
        ":bind",
        ":checked_delete",
        ":config",
        ":core",
        ":detail",
        ":function",
        ":iterator",
        ":mpl",
        ":multi_index",
        ":noncopyable",
        ":optional",
        ":parameter",
        ":predef",
        ":preprocessor",
        ":ref",
        ":scoped_ptr",
        ":shared_ptr",
        ":smart_ptr",
        ":swap",
        ":throw_exception",
        ":tuple",
        ":type_traits",
        ":utility",
        ":variant",
        ":visit_each",
    ],
)

boost_library(
    name = "serialization",
    deps = [
        ":archive",
        ":array",
        ":call_traits",
        ":config",
        ":detail",
        ":function",
        ":operators",
        ":type_traits",
    ],
)

boost_library(
    name = "smart_ptr",
    hdrs = [
        "boost/enable_shared_from_this.hpp",
        "boost/make_shared.hpp",
        "boost/pointer_to_other.hpp",
        "boost/weak_ptr.hpp",
    ],
    deps = [
        ":align",
        ":core",
        ":predef",
        ":scoped_array",
        ":scoped_ptr",
        ":shared_array",
        ":shared_ptr",
        ":throw_exception",
        ":utility",
    ],
)

boost_library(
    name = "spirit",
    deps = [
        ":optional",
        ":ref",
        ":utility",
    ],
)

boost_library(
    name = "static_assert",
)

boost_library(
    name = "system",
    deps = [
        ":assert",
        ":cerrno",
        ":config",
        ":core",
        ":cstdint",
        ":noncopyable",
        ":predef",
        ":utility",
    ],
)

boost_library(
    name = "swap",
)

boost_library(
    name = "throw_exception",
    deps = [
        ":current_function",
        ":detail",
        ":exception",
    ],
)

boost_library(
    name = "thread",
    srcs = [
        "libs/thread/src/pthread/once.cpp",
        "libs/thread/src/pthread/thread.cpp",
    ],
    hdrs = [
        "libs/thread/src/pthread/once_atomic.cpp",
    ],
    linkopts = select({
        ":mac": [
            "-lpthread",
        ],
        "//conditions:default": [
            "-lrt",
            "-lpthread",
        ],
    }),
    deps = [
        ":algorithm",
        ":atomic",
        ":bind",
        ":chrono",
        ":config",
        ":core",
        ":date_time",
        ":detail",
        ":enable_shared_from_this",
        ":exception",
        ":function",
        ":io",
        ":lexical_cast",
        ":smart_ptr",
        ":system",
        ":tuple",
        ":type_traits",
    ],
)

boost_library(
    name = "tokenizer",
    hdrs = [
        "boost/token_functions.hpp",
        "boost/token_iterator.hpp",
    ],
    deps = [
        ":assert",
        ":config",
        ":detail",
        ":iterator",
        ":mpl",
        ":throw_exception",
    ],
)

boost_library(
    name = "type",
    deps = [
        ":core",
    ],
)

boost_library(
    name = "type_index",
    deps = [
        ":core",
        ":functional",
        ":throw_exception",
    ],
)

boost_library(
    name = "type_traits",
    hdrs = [
        "boost/aligned_storage.hpp",
    ],
    deps = [
        ":core",
        ":mpl",
        ":static_assert",
    ],
)

boost_library(
    name = "typeof",
    deps = [
        ":config",
        ":detail",
        ":mpl",
        ":preprocessor",
        ":type_traits",
        ":utility",
    ],
)

boost_library(
    name = "tuple",
    deps = [
        ":ref",
    ],
)

boost_library(
    name = "tr1",
    defines = [
        "BOOST_FALLTHROUGH",
    ],
    deps = [
        ":config",
        ":unordered",
    ],
)

boost_library(
    name = "unordered",
    hdrs = [
        "boost/unordered_map.hpp",
        "boost/unordered_set.hpp",
    ],
    deps = [
        ":assert",
        ":config",
        ":container",
        ":detail",
        ":functional",
        ":iterator",
        ":limits",
        ":move",
        ":preprocessor",
        ":smart_ptr",
        ":swap",
        ":throw_exception",
        ":tuple",
        ":type_traits",
        ":utility",
    ],
)

boost_library(
    name = "utility",
    hdrs = [
        "boost/compressed_pair.hpp",
        "boost/next_prior.hpp",
    ],
    deps = [
        ":config",
        ":detail",
        ":swap",
    ],
)

boost_library(
    name = "blank",
)

boost_library(
    name = "variant",
    deps = [
        ":blank",
        ":call_traits",
        ":config",
        ":detail",
        ":functional",
        ":math",
        ":static_assert",
        ":type_index",
        ":type_traits",
        ":utility",
    ],
)

boost_library(
    name = "version",
)

boost_library(
    name = "visit_each",
)

boost_library(
    name = "cstdlib",
)

boost_library(
    name = "timer",
    deps = [
        ":cerrno",
        ":chrono",
        ":config",
        ":cstdint",
        ":io",
        ":limits",
        ":system",
        ":throw_exception",
    ],
)

boost_library(
    name = "numeric",
)

boost_library(
    name = "test",
    exclude_src = [
        "libs/test/src/unit_test_main.cpp",
        "libs/test/src/test_main.cpp",
        "libs/test/src/cpp_main.cpp",
    ],
    deps = [
        ":algorithm",
        ":assert",
        ":bind",
        ":call_traits",
        ":config",
        ":core",
        ":cstdlib",
        ":current_function",
        ":detail",
        ":exception",
        ":function",
        ":io",
        ":iterator",
        ":limits",
        ":mpl",
        ":numeric_conversion",
        ":optional",
        ":preprocessor",
        ":smart_ptr",
        ":static_assert",
        ":timer",
        ":type",
        ":type_traits",
        ":utility",
        ":version",
    ],
)
