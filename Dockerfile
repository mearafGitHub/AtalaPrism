# FROM azul/zulu-openjdk:17.0.5
# # Unpack from our ./gradlew distTar into the docker image
# # RUN set -x \
# RUN mkdir -p /app
# ADD build/libs/PrismIntegration-0.1-runner.jar /app

# EXPOSE 8080
# WORKDIR /app
# # Run the app
# CMD ["java", "-jar", "PrismIntegration-0.1-runner.jar"]


# docker build -t prism_integration .
# docker run --rm -it -p 8080:8080 prism_integration



# FROM gradle
# RUN mkdir -p /app
# COPY . /app
# WORKDIR /app
# ENV PRISM_SDK_PASSWORD=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
# # RUN /app/gradlew shadowJar
# EXPOSE 8080
# CMD ["java", "-jar", "/app/build/PrismIntegration-0.1-runner.jar"]


FROM gradle
ARG PRISM_SDK_PASSWORD
RUN mkdir -p /app
COPY . /app/
WORKDIR /app
ENV PRISM_SDK_PASSWORD=${PRISM_SDK_PASSWORD}
RUN /app/gradlew --info shadowJar
EXPOSE 8080
CMD ["java", "-jar", "/app/build/libs/PrismIntegration-0.1-all.jar"]




