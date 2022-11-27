FROM openjdk:17.0.2-slim
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ARG JAR_FILE=build/libs/weekand-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=prod","/app.jar"]
