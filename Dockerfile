FROM azul/zulu-openjdk:17.0.5
# RUN set -x \
RUN mkdir -p /app
ADD build/libs/PrismIntegration-0.1-all.jar /app

EXPOSE 8080
WORKDIR /app
# Run the app
CMD ["java", "-jar", "app/build/libs/PrismIntegration-0.1-all.jar"]


# docker build -t prism_integration .
# docker run --rm -it -p 8080:8080 prism_integration
