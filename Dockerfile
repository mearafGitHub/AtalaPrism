FROM azul/zulu-openjdk:17.0.5
# Unpack from our ./gradlew distTar into the docker image
# RUN set -x \
RUN mkdir -p /app
ADD build/libs/PrismIntegration-0.1-runner.jar /app

EXPOSE 8080
WORKDIR /app
# Run the app
CMD ["java", "-jar", "PrismIntegration-0.1-runner.jar"]


# docker build -t prism_integration .
# docker run --rm -it -p 8080:8080 prism_integration



