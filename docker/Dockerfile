# Copy chmod & rm into our java image.
FROM gcr.io/distroless/java-debian10
COPY --from=debian:10 /bin/rm /bin/rm
COPY --from=debian:10 /bin/chmod /bin/chmod