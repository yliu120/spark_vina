workspace(name = "spark_vina")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
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

maven_jar(
    name = "spark_core",
    artifact = "org.apache.spark:spark-core_2.11:2.2.0",
)

maven_jar(
    name = "spark_sql",
    artifact = "org.apache.spark:spark-sql_2.11:2.2.0",
)

maven_jar(
    name = "spark_mllib",
    artifact = "org.apache.spark:spark-mllib_2.11:2.2.0",
)

maven_jar(
    name = "spark_tags",
    artifact = "org.apache.spark:spark-tags_2.11:2.2.0",
)

maven_jar(
    name = "spark_catalyst",
    artifact = "org.apache.spark:spark-catalyst_2.11:2.2.0",
)
