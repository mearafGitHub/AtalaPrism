FROM openjdk:17-alpine
WORKDIR /prism_integration

COPY build/libs /prism_integration/build/libs

COPY build/classes /prism_integration/build/classes
COPY build/resources /prism_integration/build/resources
COPY build/libs/prism_integration.jar /prism_integration/build/libs/prism_integration-1.0-SNAPSHOT-all.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/Users/mearaftiranchie/Documents/Developer/prism_integration/prism_integration/build/libs/prism_integration-1.0-SNAPSHOT-all.jar"]
