FROM busybox as pre-build-app
WORKDIR /src/
COPY . .
RUN mkdir -p /poms && find . -name pom.xml -exec cp --parents {} /poms \;

FROM maven:3.8.4-openjdk-17-slim as build-app
WORKDIR /build
COPY --from=pre-build-app /poms/ ./
RUN mvn dependency:go-offline dependency:resolve-plugins -B
ADD . .
RUN mvn clean package

FROM openjdk:17-jdk-oracle as app
ENV JAVA_OPTS="-Xms1G -Xmx2G"
COPY --from=build-app /build/app/target/*.jar /app.jar
CMD ["sh", "-c", "eval exec java -server ${JAVA_OPTS} -jar app.jar"]
