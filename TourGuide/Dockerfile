###########################
#TOURGUIDE API
###########################
# 1- Alpine Linux with OpenJDK JRE
# 2- Add folder
# 3- Copy JAR into image
# 4- Run java app
FROM openjdk:8-jdk-alpine
RUN mkdir /tmp/app
COPY ./build/libs/tourguide-1.0.0.jar /tmp/app
CMD java -jar /tmp/app/tourguide-1.0.0.jar