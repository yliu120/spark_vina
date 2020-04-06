workspace(name = "fiesta3")

git_repository(
    name = "com_google_googletest",
    commit = "247a3d8e5e5d403f7fcacdb8ccc71e5059f15daa",
    remote = "https://github.com/google/googletest.git",
)

bind(
    name = "gtest",
    actual = "@com_google_googletest//:gtest",
)

bind(
    name = "gtest_main",
    actual = "@com_google_googletest//:gtest_main",
)

new_http_archive(
    name = "zlib",
    build_file = "//third_party:zlib.BUILD",
    sha256 = "c3e5e9fdd5004dcb542feda5ee4f0ff0744628baf8ed2dd5d66f8ca1197cb1a1",
    strip_prefix = "zlib-1.2.11",
    url = "https://zlib.net/zlib-1.2.11.tar.gz",
)

new_http_archive(
    name = "bzip2",
    build_file = "//third_party:bzip2.BUILD",
    sha256 = "a2848f34fcd5d6cf47def00461fcb528a0484d8edef8208d6d2e2909dc61d9cd",
    strip_prefix = "bzip2-1.0.6",
    url = "http://www.bzip.org/1.0.6/bzip2-1.0.6.tar.gz",
)

new_http_archive(
    name = "boost",
    build_file = "//third_party/boost:boost.BUILD",
    sha256 = "fe34a4e119798e10b8cc9e565b3b0284e9fd3977ec8a1b19586ad1dec397088b",
    strip_prefix = "boost_1_63_0",
    url = "https://dl.bintray.com/boostorg/release/1.63.0/source/boost_1_63_0.tar.gz",
)

new_http_archive(
    name = "pcre",
    build_file = "//third_party:pcre.BUILD",
    sha256 = "ccdf7e788769838f8285b3ee672ed573358202305ee361cfec7a4a4fb005bbc7",
    strip_prefix = "pcre-8.39",
    urls = [
        "https://mirror.bazel.build/ftp.exim.org/pub/pcre/pcre-8.39.tar.gz",
        "http://ftp.exim.org/pub/pcre/pcre-8.39.tar.gz",
    ],
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

maven_jar(
    name = "scala_library",
    artifact = "org.scala-lang:scala-library:2.11.8",
)

maven_jar(
    name = "hadoop_common",
    artifact = "org.apache.hadoop:hadoop-common:2.7.2",
)

maven_jar(
    name = "jackson_annotations",
    artifact = "com.fasterxml.jackson.core:jackson-annotations:2.9.0",
)
