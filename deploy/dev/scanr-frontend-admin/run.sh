#!/bin/bash
# this script can be executed as is, or docker commands copied from it and executed independently as required

for container in `docker ps -a -f "name=scanr-frontend-admin" --format "{{.ID}}"`; do
    echo "Destroying previously created container" ${container}
    docker stop ${container} || true
    docker rm -f ${container}
done

# admin :
#cd ~/dev/scanesr/front/scanr-frontend-admin
# NB. npm start and NOT merely npm install, else ERROR in ./index.ts Module build failed: A file specified in tsconfig.json could not be found: /home/sword/dev/front/scanr-frontend-admin/typings/main.d.ts
# with option --no-audit else critial vulnerability and breaks build (TODO AFTER security audit and solve front vulnerability)
#rm -rf node_module && npm start --no-audit
# NB. npm run-script build and NOT merely npm build
#npm run-script build
#cd ~/dev/scanesr/front/docker/
#rm -rf dist && cp -rf ~/dev/scanesr/front/scanr-frontend-admin/dist .
#sudo docker build -f Dockerfile.frontend -t front/scanr-admin:0.0.1-pre .
sudo docker create -e HOST=scanr-admin.local --volumes-from nginx -e VIRTUAL_HOST=scanr-admin.local --log-opt max-size=200m --log-opt max-file=5 --hostname scanr-frontend-admin --name scanr-frontend-admin  -v /var/log/scanr-frontend-admin:/logs -v /var/deploy/scanr-frontend/config/root.conf:/root.conf -v /var/deploy/scanr-frontend/config/root_location.conf:/root_location.conf front/scanr-admin:0.0.1-pre
sudo docker start scanr-frontend-admin
# check :
sudo docker logs -f scanr-frontend-admin
