#!/bin/bash
# this script can be executed as is, or docker commands copied from it and executed independently as required

for container in `docker ps -a -f "name=scanr-rabbit" --format "{{.ID}}"`; do
    echo "Destroying previously created container" ${container}
    docker stop ${container} || true
    docker rm -f ${container}
done

docker network create scanr-private
docker create -p 5672:5672 -p 15672:15672 -v /var/db/scanr-rabbit:/var/lib/rabbitmq/mnesia/ -v /var/log/scanr-rabbit:/var/lib/rabbitmq/log/ --net=scanr-private --log-opt max-size=200m --log-opt max-file=5 --hostname scanr-rabbit --name scanr-rabbit --restart=unless-stopped rabbitmq:3-management $*
docker start scanr-rabbit
# check :
docker logs -f scanr-rabbit