#!/bin/bash
# this script can be executed as is, or docker commands copied from it and executed independently as required

for container in `docker ps -a -f "name=scanr-python-scanr-entityextractor" --format "{{.ID}}"`; do
    echo "Destroying previously created container" ${container}
    docker stop ${container} || true
    docker rm -f ${container}
done

sudo docker build -f Dockerfile.pythonplugin --build-arg ARTIFACT=scanr_entityextractor -t python/scanr_entityextractor:0.20 .
sudo docker create -v /var/deploy/scanr-python_scanr_entityextractor/config/config.json:/application/config.json --log-opt max-size=200m --log-opt max-file=5 --hostname scanr-python_scanr_entityextractor --name scanr-python_scanr_entityextractor --restart=unless-stopped -v /var/log/scanr-python_scanr_entityextractor:/logs python/scanr_entityextractor:0.20 --proc 2
sudo docker network connect scanr-private scanr-python_scanr_entityextractor
sudo docker start scanr-python_scanr_entityextractor
# check :
sudo docker logs -f scanr-python_scanr_entityextractor