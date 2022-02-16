#!/bin/bash
# this script can be executed as is, or docker commands copied from it and executed independently as required

for container in `docker ps -a -f "name=scanr-python-oaifetcher" --format "{{.ID}}"`; do
    echo "Destroying previously created container" ${container}
    docker stop ${container} || true
    docker rm -f ${container}
done

# (--net=scanr)
#sudo docker build -f Dockerfile.pythonplugin --build-arg ARTIFACT=oaifetcher -t python/oaifetcher:0.16 .
sudo docker create -v /var/deploy/scanr-python_oaifetcher/config/config.json:/application/config.json --net=scanr --log-opt max-size=200m --log-opt max-file=5 --hostname scanr-python-oaifetcher --name scanr-python-oaifetcher --restart=unless-stopped -v /var/log/scanr-python_oaifetcher:/logs python/oaifetcher:0.16 --proc 2
# and NOT datapublica-obsolete/pyoaifetcher:oaifetcher-0.14
sudo docker start scanr-python-oaifetcher
# check :
sudo docker logs -f scanr-python-oaifetcher