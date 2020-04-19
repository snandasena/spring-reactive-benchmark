#!/bin/bash

mvn clean install

docker stop spring-reactive-example 2>>/dev/null

docker system prune -f

docker build -t sajith/spring-reactive-example -f Dockerfile .

docker run -d --network=host --name spring-reactive-example sajith/spring-reactive-example:latest
