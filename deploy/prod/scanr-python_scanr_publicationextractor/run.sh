#!/bin/bash
# this script can be executed as is, or docker commands copied from it and executed independently as required

for container in `docker ps -a -f "name=scanr-python-scanr-publicationextractor" --format "{{.ID}}"`; do
    echo "Destroying previously created container" ${container}
    docker stop ${container} || true
    docker rm -f ${container}
done

#sudo docker build -f Dockerfile.pythonplugin --build-arg ARTIFACT=scanr_publicationextractor -t python/scanr_publicationextractor:0.6 .
sudo docker create -v /var/deploy/scanr-python_scanr_publicationextractor/config/config.json:/application/config.json --log-opt max-size=200m --log-opt max-file=5 --hostname scanr-python_scanr_publicationextractor --name scanr-python_scanr_publicationextractor --restart=unless-stopped -v /var/log/scanr-python_scanr_publicationextractor:/logs python/scanr_publicationextractor:0.6 --proc 2
sudo docker network connect scanr-private scanr-python_scanr_publicationextractor
sudo docker start scanr-python_scanr_publicationextractor
# check :
sudo docker logs -f scanr-python_scanr_publicationextractor