FROM azul/zulu-openjdk:17.0.5

RUN mkdir -p /app

WORKDIR /prism_integration

COPY build/libs /prism_integration/build/libs

COPY build/classes /prism_integration/build/classes
COPY build/resources /prism_integration/build/resources
COPY build/libs /prism_integration/build/libs
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/build/libs/PrismIntegration-0.1-runner.jar.jar"]
