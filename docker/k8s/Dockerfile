FROM debian:11 AS base

WORKDIR /build
RUN apt-get update && apt-get install -y --no-install-recommends wget \
    # Currently, we stick to JDK 11 as other versions might not be
    # compatible with spark.
    git build-essential openjdk-11-jre openjdk-11-jdk

# Install bazel. We don't directly use the official bazel image as it is hard to use.
COPY --from=gcr.io/bazel-public/bazel:latest /usr/local/bin/bazel /usr/local/bin/bazel

RUN git clone https://github.com/yliu120/spark_vina.git && \
    cd spark_vina && \
    bazel build -c opt //:spark_vina_main_deploy.jar && \
    bazel build -c opt //java/org/spark/tools:zinc_files_to_parquet_main_deploy.jar && \
    bazel build -c opt //java/org/spark/tools:zinc_files_to_parquet_v2_main_deploy.jar

RUN cp spark_vina/bazel-bin/spark_vina_main_deploy.jar /build/ && \
    cp spark_vina/bazel-bin/java/org/spark/tools/zinc_files_to_parquet_v2_main_deploy.jar /build/ && \
    cp spark_vina/bazel-bin/java/org/spark/tools/zinc_files_to_parquet_main_deploy.jar /build/

# We need to stick to 3.2.4 as spark 3.4 won't work well.
FROM apache/spark:v3.2.4
COPY --from=base /build/spark_vina_main_deploy.jar /opt/spark/jars/
COPY --from=base /build/zinc_files_to_parquet_v2_main_deploy.jar /opt/spark/jars/
COPY --from=base /build/zinc_files_to_parquet_main_deploy.jar /opt/spark/jars/

EXPOSE 4040
