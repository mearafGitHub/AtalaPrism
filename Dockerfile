# We are only running a pre-compiled app; so select a small JRE
FROM azul/zulu-openjdk:17.0.5
# Unpack from our `./gradlew distTar` into the docker image
# RUN set -x \
RUN mkdir -p /app
ADD build/libs/prism_integration-1.0-SNAPSHOT-all.jar /app

EXPOSE 8080
WORKDIR /app
# Run the app
CMD ["kotlin", "-jar", "prism_integration-1.0-SNAPSHOT-all.jar"]


# docker build -t prism_integration .

# docker run --rm -it -p 8080:8080 prism_integration
