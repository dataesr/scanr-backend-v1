#!/bin/bash
# this script can be executed as is, or docker commands copied from it and executed independently as required

for container in `docker ps -a -f "name=scanr-python-scanr-doiresolver" --format "{{.ID}}"`; do
    echo "Destroying previously created container" ${container}
    docker stop ${container} || true
    docker rm -f ${container}
done

# --net=scanr 
#sudo docker build -f Dockerfile.pythonplugin --build-arg ARTIFACT=scanr_doiresolver -t python/scanr_doiresolver:0.9 .
sudo docker create -v /var/deploy/scanr-python_scanr_doiresolver/config/config.json:/application/config.json --net=scanr --log-opt max-size=200m --log-opt max-file=5 --hostname scanr-python_scanr_doiresolver --name scanr-python_scanr_doiresolver --restart=unless-stopped -v /var/log/scanr-python_scanr_doiresolver:/logs python/scanr_doiresolver:0.9 --proc 1
sudo docker start scanr-python_scanr_doiresolver
# check :
sudo docker logs -f scanr-python_scanr_doiresolver