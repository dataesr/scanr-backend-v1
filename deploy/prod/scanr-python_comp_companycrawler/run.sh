#!/bin/bash
# this script can be executed as is, or docker commands copied from it and executed independently as required

for container in `docker ps -a -f "name=scanr-python-comp-companycrawler" --format "{{.ID}}"`; do
    echo "Destroying previously created container" ${container}
    docker stop ${container} || true
    docker rm -f ${container}
done

#sudo docker build -f Dockerfile.pythonplugin --build-arg ARTIFACT=comp_companycrawler -t python/comp_companycrawler:1.51 .
sudo docker create -v /var/deploy/scanr-python_comp_companycrawler/config/config.json:/application/config.json --log-opt max-size=200m --log-opt max-file=5 --hostname scanr-python-comp-companycrawler --name scanr-python-comp-companycrawler --restart=unless-stopped -v /var/log/scanr-python-comp-companycrawler:/logs python/comp_companycrawler:1.51 --proc 8
sudo docker network connect scanr-private scanr-python-comp-companycrawler
sudo docker start scanr-python-comp-companycrawler
# check :
sudo docker logs -f scanr-python-comp-companycrawler