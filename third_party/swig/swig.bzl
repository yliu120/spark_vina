# Bazel rules for building swig files.
# These files are partially copying the tensorflow.bzl in tensorflow repo
# of Google.

def _get_repository_roots(ctx, files):
  """Returns abnormal root directories under which files reside.
  When running a ctx.action, source files within the main repository are all
  relative to the current directory; however, files that are generated or exist
  in remote repositories will have their root directory be a subdirectory,
  e.g. bazel-out/local-fastbuild/genfiles/external/jpeg_archive. This function
  returns the set of these devious directories, ranked and sorted by popularity
  in order to hopefully minimize the number of I/O system calls within the
  compiler, because includes have quadratic complexity.
  """
  result = {}
  for f in files:
    root = f.root.path
    if root:
      if root not in result:
        result[root] = 0
      result[root] -= 1
    work = f.owner.workspace_root
    if work:
      if root:
        root += "/"
      root += work
    if root:
      if root not in result:
        result[root] = 0
      result[root] -= 1
  return [k for v, k in sorted([(v, k) for k, v in result.items()])]

def _py_wrap_cc_impl(ctx):
  srcs = ctx.files.srcs
  if len(srcs) != 1:
    fail("Exactly one SWIG source file label must be specified.", "srcs")
  module_name = ctx.attr.module_name
  src = ctx.files.srcs[0]
  inputs = depset([src]).to_list()
  inputs += ctx.files.swig_includes
  for dep in ctx.attr.deps:
    inputs += dep[CcInfo].compilation_context.headers.to_list()
  inputs += ctx.files._swiglib
  inputs += ctx.files.toolchain_deps
  swig_include_dirs = depset(_get_repository_roots(ctx, inputs)).to_list()
  # swig_include_dirs += sorted([f.dirname for f in ctx.files._swiglib])
  args = ["-c++"] if ctx.attr.cpp else []
  args += [
      "-python", "-module", module_name,
      "-o", ctx.outputs.cc_out.path,
      "-outdir", ctx.outputs.py_out.dirname
  ]
  args += ["-l" + f.path for f in ctx.files.swig_includes]
  args += ["-I" + i for i in swig_include_dirs]
  args += [src.path]
  outputs = [ctx.outputs.cc_out, ctx.outputs.py_out]
  ctx.actions.run(
      executable=ctx.executable._swig,
      arguments=args,
      inputs=list(inputs),
      outputs=outputs,
      mnemonic="PythonSwig",
      use_default_shell_env=True,
      progress_message="SWIGing " + src.path)
  return struct(files=depset(outputs))

def _output_func(module_name, py_module_name, cpp):
  return {"py_out": "%{py_module_name}.py",
          "cc_out": "%{module_name}.cc" if cpp else "%{module_name}.c",}

_py_wrap_cc = rule(
    attrs = {
        "srcs": attr.label_list(
            mandatory = True,
            allow_files = True,
        ),
        "swig_includes": attr.label_list(
            cfg = "target",
            allow_files = True,
        ),
        "deps": attr.label_list(
            allow_files = True,
            providers = [CcInfo]
        ),
        "toolchain_deps": attr.label_list(
            allow_files = True,
        ),
        "cpp": attr.bool(default = True),
        "module_name": attr.string(mandatory = True),
        "py_module_name": attr.string(mandatory = True),
        "_swig": attr.label(
            default = Label("@swig//:swig"),
            executable = True,
            cfg = "host",
        ),
        "_swiglib": attr.label(
            default = Label("@swig//:templates"),
            allow_files = True,
        ),
    },
    outputs = _output_func,
    implementation = _py_wrap_cc_impl,
)

def py_wrap_cc(name,
               srcs,
               swig_includes=[],
               deps=[],
               copts=[],
               cpp=True,
               **kwargs):
  module_name = name.split("/")[-1]
  # Convert a rule name such as foo/bar/baz to foo/bar/_baz.so
  # and use that as the name for the rule producing the .so file.
  cc_library_name = "/".join(name.split("/")[:-1] + ["_" + module_name + ".so"])
  cc_library_pyd_name = "/".join(
      name.split("/")[:-1] + ["_" + module_name + ".pyd"])
  _py_wrap_cc(
      name=name + "_py_wrap",
      srcs=srcs,
      swig_includes=swig_includes,
      deps=deps,
      toolchain_deps=[],
      module_name=module_name,
      py_module_name=name,
      cpp=cpp)

  src = module_name
  src += ".cc" if cpp else ".c"

  native.cc_binary(
      name=cc_library_name,
      srcs=[src],
      deps=deps,
      linkshared = 1,
      linkstatic = 1,
      linkopts=[],
      **kwargs)

  native.genrule(
      name="gen_" + cc_library_pyd_name,
      srcs=[":" + cc_library_name],
      outs=[cc_library_pyd_name],
      cmd="cp $< $@",)

  native.py_library(
      name=name,
      srcs=[":" + name + ".py"],
      data=[":" + cc_library_name])