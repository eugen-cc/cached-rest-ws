FROM openjdk:17-slim
LABEL authors="Eugen Gross"

EXPOSE 8080

COPY target/cached-rest-proxy-*.jar .

ENTRYPOINT ["java", "-jar", "cached-rest-proxy-0.0.1-SNAPeSHOT.jar"]