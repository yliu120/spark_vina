workspace(name = "spark_vina")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

http_archive(
    name = "com_google_googletest",
    sha256 = "94c634d499558a76fa649edb13721dce6e98fb1e7018dfaeba3cd7a083945e91",
    strip_prefix = "googletest-release-1.10.0",
    url = "https://github.com/google/googletest/archive/release-1.10.0.zip",
)

http_archive(
    name = "zlib",
    build_file = "//third_party:zlib.BUILD",
    sha256 = "b3a24de97a8fdbc835b9833169501030b8977031bcb54b3b3ac13740f846ab30",
    strip_prefix = "zlib-1.2.13",
    url = "https://zlib.net/zlib-1.2.13.tar.gz",
)

http_archive(
    name = "bzip2",
    build_file = "//third_party:bzip2.BUILD",
    sha256 = "a2848f34fcd5d6cf47def00461fcb528a0484d8edef8208d6d2e2909dc61d9cd",
    strip_prefix = "bzip2-1.0.6",
    url = "https://downloads.sourceforge.net/project/bzip2/bzip2-1.0.6.tar.gz",
)

http_archive(
    name = "com_github_nelhage_rules_boost",
    url = "https://github.com/nelhage/rules_boost/archive/96e9b631f104b43a53c21c87b01ac538ad6f3b48.tar.gz",
    strip_prefix = "rules_boost-96e9b631f104b43a53c21c87b01ac538ad6f3b48",
)
load("@com_github_nelhage_rules_boost//:boost/boost.bzl", "boost_deps")
boost_deps()

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
    name = "eigen",
    build_file = "//third_party:eigen.BUILD",
    sha256 = "d56fbad95abf993f8af608484729e3d87ef611dd85b3380a8bad1d5cbc373a57",
    strip_prefix = "eigen-3.3.7",
    urls = [
        "https://yunlongl-mirror.oss-cn-zhangjiakou.aliyuncs.com/eigen-3.3.7.tar.gz",
    ],
)

http_archive(
    name = "swig",
    build_file = "//third_party/swig:swig.BUILD",
    sha256 = "7cf9f447ae7ed1c51722efc45e7f14418d15d7a1e143ac9f09a668999f4fc94d",
    strip_prefix = "swig-3.0.12",
    urls = [
        "https://downloads.sourceforge.net/project/swig/swig/swig-3.0.12/swig-3.0.12.tar.gz",
    ],
)

http_archive(
    name = "com_github_gflags_gflags",
    sha256 = "34af2f15cf7367513b352bdcd2493ab14ce43692d2dcd9dfc499492966c64dcf",
    strip_prefix = "gflags-2.2.2",
    url = "https://github.com/gflags/gflags/archive/v2.2.2.tar.gz",
)

http_archive(
    name = "glog",
    sha256 = "f28359aeba12f30d73d9e4711ef356dc842886968112162bc73002645139c39c",
    strip_prefix = "glog-0.4.0",
    url = "https://github.com/google/glog/archive/v0.4.0.tar.gz",
)

http_archive(
    name = "absl",
    sha256 = "5366d7e7fa7ba0d915014d387b66d0d002c03236448e1ba9ef98122c13b35c36",
    strip_prefix = "abseil-cpp-20230125.3",
    urls = [
        "https://github.com/abseil/abseil-cpp/archive/refs/tags/20230125.3.tar.gz"
    ],
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
    name = "rules_pkg",
    urls = [
        "https://mirror.bazel.build/github.com/bazelbuild/rules_pkg/releases/download/0.9.1/rules_pkg-0.9.1.tar.gz",
        "https://github.com/bazelbuild/rules_pkg/releases/download/0.9.1/rules_pkg-0.9.1.tar.gz",
    ],
    sha256 = "8f9ee2dc10c1ae514ee599a8b42ed99fa262b757058f65ad3c384289ff70c4b8",
)
load("@rules_pkg//:deps.bzl", "rules_pkg_dependencies")
rules_pkg_dependencies()

http_archive(
    name = "com_google_protobuf",
    sha256 = "a700a49470d301f1190a487a923b5095bf60f08f4ae4cac9f5f7c36883d17971",
    strip_prefix = "protobuf-23.4",
    urls = [
        "https://github.com/protocolbuffers/protobuf/archive/refs/tags/v23.4.tar.gz",
    ],
)

http_archive(
    name = "openbabel",
    build_file = "//third_party/openbabel:openbabel.BUILD",
    patch_args = ["-p1"],
    patches = [
        "//third_party/openbabel:openbabel.patch",
    ],
    sha256 = "c97023ac6300d26176c97d4ef39957f06e68848d64f1a04b0b284ccff2744f02",
    strip_prefix = "openbabel-openbabel-3-1-1",
    urls = [
	"https://yunlongl-mirror.oss-cn-zhangjiakou.aliyuncs.com/openbabel-3-1-1.tar.gz",
        "https://github.com/openbabel/openbabel/archive/openbabel-3-1-1.tar.gz",
    ],
)

http_archive(
    name = "com_google_benchmark",
    sha256 = "23082937d1663a53b90cb5b61df4bcc312f6dee7018da78ba00dd6bd669dfef2",
    strip_prefix = "benchmark-1.5.1",
    urls = [
        "https://github.com/google/benchmark/archive/v1.5.1.tar.gz",
    ],
)

load("@com_google_protobuf//:protobuf_deps.bzl", "protobuf_deps")

protobuf_deps()

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

RULES_JVM_EXTERNAL_TAG = "4.5"
RULES_JVM_EXTERNAL_SHA = "b17d7388feb9bfa7f2fa09031b32707df529f26c91ab9e5d909eb1676badd9a6"

http_archive(
    name = "rules_jvm_external",
    sha256 = RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    urls = [
        "https://yunlongl-mirror.oss-cn-zhangjiakou.aliyuncs.com/rules_external_jvm-%s.zip" % RULES_JVM_EXTERNAL_TAG,
        "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
    ],
)

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "commons-cli:commons-cli:1.5.0",
        "com.google.truth:truth:1.1.5",
        "com.google.guava:guava:32.1.1-jre",
        "com.google.truth.extensions:truth-proto-extension:1.1.5",
        "junit:junit:4.13.2",
        "org.apache.hadoop:hadoop-mapreduce-client-core:3.3.6",
        "org.apache.hadoop:hadoop-mapreduce-client-common:3.3.6",
        "org.apache.hadoop:hadoop-mapreduce-client-app:3.3.6,",
        "org.apache.hadoop:hadoop-common:3.3.6",
        "org.apache.spark:spark-catalyst_2.13:3.4.1",
        "org.apache.spark:spark-core_2.13:3.4.1",
        "org.apache.spark:spark-sql_2.13:3.4.1",
        "org.hamcrest:hamcrest:2.2",
        "org.scala-lang:scala-library:2.13.8",
        "org.slf4j:slf4j-api:2.0.6",
    ],
    repositories = [
	#"https://maven.aliyun.com/repository/public",
	#"https://maven.aliyun.com/repository/google",
	#"https://maven.aliyun.com/repository/apache-snapshots",
        "https://maven.google.com",
        "https://repo1.maven.org/maven2",
    ],
)

#container_pull(
#    name = "java_base",
#    digest = "sha256:e99eb6cf88ca2df69e99bf853d65f125066730e3e9f7a233bd1b7e3523c144cb",
#    registry = "gcr.io",
#    repository = "distroless/java",
#)

#container_pull(
#    name = "spark_base",
#    digest = "sha256:0d2c7d9d66fb83a0311442f0d2830280dcaba601244d1d8c1704d72f5806cc4c",
#    registry = "gcr.io",
#    repository = "spark-operator/spark",
#)

#load("@io_bazel_rules_docker//contrib:dockerfile_build.bzl", "dockerfile_image")
#
#dockerfile_image(
#    name = "java_base_modified",
#    dockerfile = "//docker:Dockerfile",
#)
