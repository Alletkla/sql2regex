FROM eclipse-temurin:17-jdk-focal
LABEL maintainer="M.Förster"
WORKDIR /app
COPY mvn/ ./mvn
COPY mvnw ./
COPY pom.xml ./
COPY package.json ./
RUN chmod +x mvnw
RUN ls -l
COPY src ./src
# RUN ./mvnw clean install
RUN ./mvnw dependency:go-offline
CMD ["./mvnw", "spring-boot:run"]
# COPY target/sqltoregex-1.0.0.jar sqltoregex-1.0.0.jar
# ENTRYPOINT ["java","-jar","/sqltoregex-1.0.0.jar"]