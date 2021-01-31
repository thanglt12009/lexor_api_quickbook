@echo off
call mvn clean package
call docker build -t com.lexor/qbsa .
call docker rm -f qbsa
call docker run -d -p 9080:9080 -p 9443:9443 --name qbsa com.lexor/qbsa