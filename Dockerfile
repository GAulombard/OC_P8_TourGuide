###########################
#GPSUTIL API
###########################
FROM openjdk:17-jdk-alpine AS gpsapi
RUN apk --no-cache add curl
RUN mkdir /tmp/app
COPY ./gpsUtil/build/libs/gpsapi-1.0.0.jar /tmp/app
EXPOSE 8081
CMD java -jar /tmp/app/gpsapi-1.0.0.jar

###########################
#TRIPPRICER API
###########################
FROM openjdk:17-jdk-alpine AS trippricerapi
RUN apk --no-cache add curl
RUN mkdir /tmp/app
COPY ./TripPricer/build/libs/trippricerapi-1.0.0.jar /tmp/app
EXPOSE 8083
CMD java -jar /tmp/app/trippricerapi-1.0.0.jar

###########################
#REWARDCENTRAL API
###########################
FROM openjdk:17-jdk-alpine AS rewardcentralapi
RUN apk --no-cache add curl
RUN mkdir /tmp/app
COPY ./RewardCentral/build/libs/rewardcentralapi-1.0.0.jar /tmp/app
EXPOSE 8082
CMD java -jar /tmp/app/rewardcentralapi-1.0.0.jar

###########################
#TOURGUIDE
###########################
FROM openjdk:17-jdk-alpine AS tourguide
RUN apk --no-cache add curl
RUN mkdir /tmp/app
COPY ./TourGuide/build/libs/tourguide-1.0.0.jar /tmp/app
EXPOSE 9000
CMD java -jar /tmp/app/tourguide-1.0.0.jar