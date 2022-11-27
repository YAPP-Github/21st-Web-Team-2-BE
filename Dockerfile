FROM openjdk:17.0.2-slim
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY ${JAR_FILE:-build/libs/*.jar} app.jar
ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=prod","/app.jar"]
