#!/bin/sh
mvn clean package && docker build -t com.lexor/qbsa .
docker rm -f qbsa || true && docker run -d -p 9080:9080 -p 9443:9443 --name qbsa com.lexor/qbsa