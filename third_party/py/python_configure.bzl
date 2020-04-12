# -*- Python -*-
"""Repository rule for Python autoconfiguration.
"""

def _tpl(repository_ctx, tpl, substitutions={}, out=None):
  if not out:
    out = tpl
  repository_ctx.template(
      out,
      Label("//third_party/py:%s.tpl" % tpl),
      substitutions)

def _read_dir(repository_ctx, src_dir):
  """Returns a string with all files in a directory.
  Finds all files inside a directory, traversing subfolders and following
  symlinks. The returned string contains the full path of all files
  separated by line breaks.
  """
  find_result = _execute(
      repository_ctx, ["find", src_dir, "-follow", "-type", "f"],
      empty_stdout_fine=True)
  result = find_result.stdout
  return result

def _genrule(src_dir, genrule_name, command, outs):
  """Returns a string with a genrule.
  Genrule executes the given command and produces the given outputs.
  """
  return (
      'genrule(\n' +
      '    name = "' +
      genrule_name + '",\n' +
      '    outs = [\n' +
      outs +
      '\n    ],\n' +
      '    cmd = """\n' +
      command +
      '\n   """,\n' +
      ')\n'
  )

def _norm_path(path):
  """Returns a path with '/' and remove the trailing slash."""
  path = path.replace("\\", "/")
  if path[-1] == "/":
    path = path[:-1]
  return path

def _python_configure_warning(msg):
  """Output warning message during auto configuration."""
  yellow = "\033[1;33m"
  no_color = "\033[0m"
  print("%sPython Configuration Warning:%s %s" % (yellow, no_color, msg))

def _python_configure_fail(msg):
  """Output failure message when auto configuration fails."""
  red = "\033[0;31m"
  no_color = "\033[0m"
  fail("%sPython Configuration Error:%s %s\n" % (red, no_color, msg))

def _execute(repository_ctx, cmdline, error_msg=None, error_details=None,
             empty_stdout_fine=False):
  """Executes an arbitrary shell command.
  Args:
    repository_ctx: the repository_ctx object
    cmdline: list of strings, the command to execute
    error_msg: string, a summary of the error if the command fails
    error_details: string, details about the error or steps to fix it
    empty_stdout_fine: bool, if True, an empty stdout result is fine, otherwise
      it's an error
  Return:
    the result of repository_ctx.execute(cmdline)
  """
  result = repository_ctx.execute(cmdline)
  if result.stderr or not (empty_stdout_fine or result.stdout):
    _python_configure_fail(
        "\n".join([
            error_msg.strip() if error_msg else "Repository command failed",
            result.stderr.strip(),
            error_details if error_details else ""]))
  return result

def _get_python_include(repository_ctx):
  result = _execute(repository_ctx,
                    ["python3", "-c",
                     'from __future__ import print_function;' +
                     'from distutils import sysconfig;' +
                     'print(sysconfig.get_python_inc())'],
                    error_msg="Problem getting python include path.",
                    error_details=("Cannot find python3 include path."))
  return result.stdout.splitlines()[0]

# later on we may need numpy include rules
def _symlink_genrule_for_dir(repository_ctx, src_dir, dest_dir, genrule_name,
    src_files = [], dest_files = []):
  """Returns a genrule to symlink(or copy if on Windows) a set of files.
  If src_dir is passed, files will be read from the given directory; otherwise
  we assume files are in src_files and dest_files
  """
  if src_dir != None:
    src_dir = _norm_path(src_dir)
    dest_dir = _norm_path(dest_dir)
    files = _read_dir(repository_ctx, src_dir)
    # Create a list with the src_dir stripped to use for outputs.
    dest_files = files.replace(src_dir, '').splitlines()
    src_files = files.splitlines()
  command = []
  outs = []
  for i in range(len(dest_files)):
    if dest_files[i] != "":
      # If we have only one file to link we do not want to use the dest_dir, as
      # $(@D) will include the full path to the file.
      dest = '$(@D)/' + dest_dir + dest_files[i] if len(dest_files) != 1 else '$(@D)/' + dest_files[i]
      # On Windows, symlink is not supported, so we just copy all the files.
      cmd = 'ln -s'
      command.append(cmd + ' "%s" "%s"' % (src_files[i] , dest))
      outs.append('        "' + dest_dir + dest_files[i] + '",')
  genrule = _genrule(src_dir, genrule_name, " && ".join(command),
                     "\n".join(outs))
  return genrule

def _create_local_python_repository(repository_ctx):
  """Creates the repository containing files set up to build with Python."""
  python_include = _get_python_include(repository_ctx)
  python_include_rule = _symlink_genrule_for_dir(
      repository_ctx, python_include, 'python_include', 'python_include')
  _tpl(repository_ctx, "BUILD", {
      "%{PYTHON_INCLUDE_GENRULE}": python_include_rule,
  })

def _python_autoconf_impl(repository_ctx):
  """Implementation of the python_autoconf repository rule."""
  _create_local_python_repository(repository_ctx)

python_configure = repository_rule(
    implementation = _python_autoconf_impl,
)

"""Detects and configures the local Python.
Add the following to your WORKSPACE FILE:
```python
python_configure(name = "local_config_python")
```
Args:
  name: A unique name for this workspace rule.
"""