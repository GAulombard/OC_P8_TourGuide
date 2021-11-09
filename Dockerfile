###########################
#GPSUTIL API
###########################
FROM openjdk:17-jdk-alpine AS gpsutil
RUN apk --no-cache add curl
RUN mkdir /tmp/app
COPY ./gpsUtil/build/libs/gpsapi-1.0.0.jar /tmp/app
CMD java -jar /tmp/app/gpsapi-1.0.0.jar

###########################
#REWARDCENTRAL API
###########################
FROM openjdk:17-jdk-alpine AS rewardcentral
RUN apk --no-cache add curl
RUN mkdir /tmp/app
COPY ./RewardCentral/build/libs/rewardcentralapi-1.0.0.jar /tmp/app
CMD java -jar /tmp/app/rewardcentralapi-1.0.0.jar

###########################
#TRIPPRICER API
###########################
FROM openjdk:17-jdk-alpine AS trippricer
RUN apk --no-cache add curl
RUN mkdir /tmp/app
COPY ./TripPricer/build/libs/trippricerapi-1.0.0.jar /tmp/app
CMD java -jar /tmp/app/trippricerapi-1.0.0.jar

###########################
#TOURGUIDE
###########################
FROM openjdk:17-jdk-alpine AS tourguide
RUN apk --no-cache add curl
RUN mkdir /tmp/app
COPY ./TourGuide/build/libs/tourguide-1.0.0.jar /tmp/app
CMD java -jar /tmp/app/tourguide-1.0.0.jar