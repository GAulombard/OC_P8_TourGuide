###########################
#GPSUTIL API
###########################
FROM openjdk:17-jdk-alpine AS gpsapi
RUN apk --no-cache add curl
RUN mkdir /tmp/app
COPY /libs/gpsUtil.jar /tmp/app
CMD java -jar /tmp/app/gpsUtil.jar

###########################
#TRIPPRICER API
###########################
FROM openjdk:17-jdk-alpine AS trippricerapi
RUN apk --no-cache add curl
RUN mkdir /tmp/app
COPY /libs/TripPricer.jar /tmp/app
CMD java -jar /tmp/app/TripPricer.jar

###########################
#REWARDCENTRAL API
###########################
FROM openjdk:17-jdk-alpine AS rewardcentralapi
RUN apk --no-cache add curl
RUN mkdir /tmp/app
COPY /libs/RewardCentral.jar /tmp/app
CMD java -jar /tmp/app/RewardCentral.jar