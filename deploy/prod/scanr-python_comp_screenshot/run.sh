#!/bin/bash
# this script can be executed as is, or docker commands copied from it and executed independently as required

for container in `docker ps -a -f "name=scanr-python_comp_screenshot" --format "{{.ID}}"`; do
    echo "Destroying previously created container" ${container}
    docker stop ${container} || true
    docker rm -f ${container}
done

#sudo docker build -f Dockerfile.screenshot --build-arg ARTIFACT=comp_screenshot -t python/comp_screenshot:0.7-0.10.23 .
sudo docker create -v /var/deploy/scanr-python_comp_screenshot/config/config.json:/application/config.json --log-opt max-size=200m --log-opt max-file=5 --hostname scanr-python-comp-screenshot --name scanr-python_comp_screenshot --restart=unless-stopped -v /var/log/scanr-python_comp_screenshot:/logs python/comp_screenshot:0.7-0.10.23 --proc 2
sudo docker network connect scanr-private scanr-python_comp_screenshot
sudo docker start scanr-python_comp_screenshot
# check :
sudo docker logs -f scanr-python_comp_screenshot
