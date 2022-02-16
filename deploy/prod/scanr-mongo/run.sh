#!/bin/bash
# this script can be executed as is, or docker commands copied from it and executed independently as required

for container in `docker ps -a -f "name=scanr-mongo" --format "{{.ID}}"`; do
    echo "Destroying previously created container" ${container}
    docker stop ${container} || true
    docker rm -f ${container}
done

docker network create scanr-private
# to not require 3GB (smallfiles) ex. when in a VM : sudo /var/deploy/scanr-mongo/run.sh --smallfiles
docker create -p 27017:27017 -v /var/db/scanr-mongo:/data/db -v /var/log/scanr-mongo:/var/log/mongodb --net=scanr-private --log-opt max-size=200m --log-opt max-file=5 --hostname scanr-mongo --name scanr-mongo --restart=unless-stopped mongo:3.0 $*
docker start scanr-mongo
# check :
docker logs -f scanr-mongo