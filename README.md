## Micronaut 3.7.3 Documentation

- [User Guide](https://docs.micronaut.io/3.7.3/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.7.3/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.7.3/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)
## Feature http-client documentation

- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)


# HOW TO RUN

Place the PRISM_SDK_PASSWORD in a .env file on the root of the repository. Then run

            docker-compose up

You can also build the image directly

            docker build --build-arg PRISM_SDK_PASSWORD=$PRISM_SDK_PASSWORD .
    