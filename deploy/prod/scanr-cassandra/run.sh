#!/bin/bash
export LOG_BASEDIR=/var/log/scanr-cassandra
# this script can be executed as is, or docker commands copied from it and executed independently as required

for container in `docker ps -a -f "name=scanr-cassandra" --format "{{.ID}}"`; do
    echo "Destroying previously created container" ${container}
    docker stop ${container} || true
    docker rm -f ${container}
done

docker network create scanr-private
docker create -p 7000:7000 -p 7001:7001 -p 7199:7199 -p 9042:9042 -p 9160:9160 -v /var/db/scanr-cassandra:/var/lib/cassandra -v /var/log/scanr-cassandra:/var/log/cassandra --net=scanr-private --log-opt max-size=200m --log-opt max-file=5 --hostname scanr-cassandra --name scanr-cassandra --restart=unless-stopped cassandra:2.1 $*
docker start scanr-cassandra
# check :
docker logs -f scanr-cassandra