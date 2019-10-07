FROM openjdk:8
COPY ./target/uberjar/nubank-authorizer-0.1.0-SNAPSHOT-standalone.jar /
ENTRYPOINT ["java", "-jar", "/nubank-authorizer-0.1.0-SNAPSHOT-standalone.jar"]
# CMD ["-jar /nubank-authorizer-0.1.0-SNAPSHOT-standalone.jar"]