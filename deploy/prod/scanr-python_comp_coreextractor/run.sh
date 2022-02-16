#!/bin/bash
# this script can be executed as is, or docker commands copied from it and executed independently as required

for container in `docker ps -a -f "name=scanr-python-comp-coreextractor" --format "{{.ID}}"`; do
    echo "Destroying previously created container" ${container}
    docker stop ${container} || true
    docker rm -f ${container}
done

#sudo docker build -f Dockerfile.pythonplugin --build-arg ARTIFACT=comp_coreextractor -t python/comp_coreextractor:1.74 .
sudo docker create -v /var/deploy/scanr-python_comp_coreextractor/config/config.json:/application/config.json --log-opt max-size=200m --log-opt max-file=5 --hostname scanr-python_comp_coreextractor --name scanr-python_comp_coreextractor --restart=unless-stopped -v /var/log/scanr-python_comp_coreextractor:/logs python/comp_coreextractor:1.74 --proc 4
sudo docker network connect scanr-private scanr-python_comp_coreextractor
sudo docker start scanr-python_comp_coreextractor
# check :
sudo docker logs -f scanr-python_comp_coreextractor