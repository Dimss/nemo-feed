#!/usr/bin/env bash
if [ -z "$1" ]
then
      echo "Version is missing. Usage example: ./deploy.sh 0.3 "
      exit
fi
export APP_VERSION=$1
export PROJECT_NAME=feed
export PROJECT_NS=nemo
export DOCKER_REGISTRY=172.30.1.1:5000
mvn versions:set -DnewVersion=${APP_VERSION}
mvn package -Dspring.profiles.active=dev
docker build --build-arg VERSION=${APP_VERSION} -t ${DOCKER_REGISTRY}/${PROJECT_NS}/${PROJECT_NAME}:${APP_VERSION} .
docker push ${DOCKER_REGISTRY}/${PROJECT_NS}/${PROJECT_NAME}:${APP_VERSION}
