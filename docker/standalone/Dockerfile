FROM gcr.io/distroless/java-debian11
COPY --from=debian:11 /bin/rm /bin/rm
COPY --from=debian:11 /bin/chmod /bin/chmod

ARG JAR
ADD $JAR /

EXPOSE 4040
VOLUME /workspace
ENTRYPOINT ["java", "-jar", "/spark_vina_main_deploy.jar"]
