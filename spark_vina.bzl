# JNI specifically needs a .dylib to be loaded as a shared library though
# Bazel builds .so libraries on Darwin platform.
def sv_cc_shared_library(name,
                         srcs=[],
                         deps=[],
                         copts=[],
                         **kwargs):
  base_name = "lib" + name
  native.cc_binary(
      name = base_name + ".so",
      srcs = srcs,
      deps = deps,
      linkshared = 1,
      linkstatic = 1,
      **kwargs)
  native.cc_binary(
      name = base_name + ".dylib",
      srcs = srcs,
      deps = deps,
      linkshared = 1,
      linkstatic = 1,
      **kwargs)

  native.alias(
      name = name,
      actual = select({
          "@bazel_tools//src/conditions:darwin_x86_64": base_name + ".dylib",
          "@bazel_tools//src/conditions:darwin": base_name + ".dylib",
          "//conditions:default": base_name + ".so",
      }),
  )