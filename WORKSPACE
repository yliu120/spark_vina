workspace(name = "spark_vina")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")
load("@bazel_tools//tools/build_defs/repo:maven_rules.bzl", "maven_jar")

http_archive(
    name = "com_google_googletest",
    url = "https://github.com/google/googletest/archive/release-1.10.0.zip",
    sha256 = "94c634d499558a76fa649edb13721dce6e98fb1e7018dfaeba3cd7a083945e91",
    strip_prefix = "googletest-release-1.10.0",
)

http_archive(
    name = "zlib",
    build_file = "//third_party:zlib.BUILD",
    sha256 = "c3e5e9fdd5004dcb542feda5ee4f0ff0744628baf8ed2dd5d66f8ca1197cb1a1",
    strip_prefix = "zlib-1.2.11",
    url = "https://zlib.net/zlib-1.2.11.tar.gz",
)

http_archive(
    name = "bzip2",
    build_file = "//third_party:bzip2.BUILD",
    sha256 = "a2848f34fcd5d6cf47def00461fcb528a0484d8edef8208d6d2e2909dc61d9cd",
    strip_prefix = "bzip2-1.0.6",
    url = "https://downloads.sourceforge.net/project/bzip2/bzip2-1.0.6.tar.gz",
)

http_archive(
    name = "boost",
    build_file = "//third_party/boost:boost.BUILD",
    sha256 = "fe34a4e119798e10b8cc9e565b3b0284e9fd3977ec8a1b19586ad1dec397088b",
    strip_prefix = "boost_1_63_0",
    url = "https://dl.bintray.com/boostorg/release/1.63.0/source/boost_1_63_0.tar.gz",
)

http_archive(
    name = "pcre",
    build_file = "//third_party:pcre.BUILD",
    sha256 = "ccdf7e788769838f8285b3ee672ed573358202305ee361cfec7a4a4fb005bbc7",
    strip_prefix = "pcre-8.39",
    urls = [
        "https://mirror.bazel.build/ftp.exim.org/pub/pcre/pcre-8.39.tar.gz",
        "http://ftp.exim.org/pub/pcre/pcre-8.39.tar.gz",
    ],
)

http_archive(
    name = "six",
    build_file = "//third_party:six.BUILD",
    sha256 = "d16a0141ec1a18405cd4ce8b4613101da75da0e9a7aec5bdd4fa804d0e0eba73",
    strip_prefix = "six-1.12.0",
    urls = [
        "https://storage.googleapis.com/mirror.tensorflow.org/pypi.python.org/packages/source/s/six/six-1.12.0.tar.gz",
        "https://pypi.python.org/packages/source/s/six/six-1.12.0.tar.gz",
    ],
)

http_archive(
    name = "swig",
    build_file = "//third_party/swig:swig.BUILD",
    strip_prefix = "swig-3.0.12",
    urls = [
        "https://downloads.sourceforge.net/project/swig/swig/swig-3.0.12/swig-3.0.12.tar.gz",
    ],
)

git_repository(
    name = "subpar",
    remote = "https://github.com/google/subpar",
    tag = "1.0.0",
)

# rules_cc defines rules for generating C++ code from Protocol Buffers.
http_archive(
    name = "rules_cc",
    sha256 = "35f2fb4ea0b3e61ad64a369de284e4fbbdcdba71836a5555abb5e194cf119509",
    strip_prefix = "rules_cc-624b5d59dfb45672d4239422fa1e3de1822ee110",
    urls = [
        "https://github.com/bazelbuild/rules_cc/archive/624b5d59dfb45672d4239422fa1e3de1822ee110.tar.gz",
    ],
)

# rules_java defines rules for generating Java code from Protocol Buffers.
http_archive(
    name = "rules_java",
    sha256 = "ccf00372878d141f7d5568cedc4c42ad4811ba367ea3e26bc7c43445bbc52895",
    strip_prefix = "rules_java-d7bf804c8731edd232cb061cb2a9fe003a85d8ee",
    urls = [
        "https://github.com/bazelbuild/rules_java/archive/d7bf804c8731edd232cb061cb2a9fe003a85d8ee.tar.gz",
    ],
)

http_archive(
    name = "rules_proto",
    sha256 = "602e7161d9195e50246177e7c55b2f39950a9cf7366f74ed5f22fd45750cd208",
    strip_prefix = "rules_proto-97d8af4dc474595af3900dd85cb3a29ad28cc313",
    urls = [
        "https://github.com/bazelbuild/rules_proto/archive/97d8af4dc474595af3900dd85cb3a29ad28cc313.tar.gz",
    ],
)

http_archive(
    name = "rules_python",
    url = "https://github.com/bazelbuild/rules_python/releases/download/0.0.1/rules_python-0.0.1.tar.gz",
    sha256 = "aa96a691d3a8177f3215b14b0edc9641787abaaa30363a080165d06ab65e1161",
)
load("@rules_python//python:repositories.bzl", "py_repositories")
py_repositories()

http_archive(
    name = "com_google_protobuf",
    sha256 = "cf754718b0aa945b00550ed7962ddc167167bd922b842199eeb6505e6f344852",
    strip_prefix = "protobuf-3.11.3",
    urls = [
        "https://mirror.bazel.build/github.com/protocolbuffers/protobuf/archive/v3.11.3.tar.gz",
        "https://github.com/protocolbuffers/protobuf/archive/v3.11.3.tar.gz",
    ],
)

load("@rules_cc//cc:repositories.bzl", "rules_cc_dependencies")
rules_cc_dependencies()

load("@rules_java//java:repositories.bzl", "rules_java_dependencies", "rules_java_toolchains")
rules_java_dependencies()
rules_java_toolchains()

load("@rules_proto//proto:repositories.bzl", "rules_proto_dependencies", "rules_proto_toolchains")
rules_proto_dependencies()
rules_proto_toolchains()

load("//third_party/py:python_configure.bzl", "python_configure")
python_configure(name = "local_config_python")

bind(
    name = "python_headers",
    actual = "@local_config_python//:python_headers",
)

RULES_JVM_EXTERNAL_TAG = "3.0"
RULES_JVM_EXTERNAL_SHA = "62133c125bf4109dfd9d2af64830208356ce4ef8b165a6ef15bbff7460b35c3a"

http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    sha256 = RULES_JVM_EXTERNAL_SHA,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "junit:junit:4.13",
        "org.hamcrest:hamcrest-library:1.3",
    ],
    repositories = [
        "https://maven.google.com",
        "https://repo1.maven.org/maven2",
    ],
)
