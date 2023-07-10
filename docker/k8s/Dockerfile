FROM gcr.io/spark-operator/spark:v3.1.1

ARG JAR
COPY ../../data /data
COPY $JAR /spark_vina_main_deploy.jar

EXPOSE 4040
VOLUME /workspace

