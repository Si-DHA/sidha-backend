FROM openjdk:17-alpine
ARG JAR_FILE_1=build/libs/sidha-backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE_1} app1.jar
EXPOSE 9099
ENTRYPOINT ["java","-jar","/app1.jar", "-Duser.timezone=Asia/Jakarta"]
