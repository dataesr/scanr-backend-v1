#!/bin/bash
# this script can be executed as is, or docker commands copied from it and executed independently as required

for container in `docker ps -a -f "name=scanr-frontend" --format "{{.ID}}"`; do
    echo "Destroying previously created container" ${container}
    docker stop ${container} || true
    docker rm -f ${container}
done

# frontend :
#cd ~/dev/scanesr/front/scanr-frontend
# NB. npm start and NOT merely npm install, else ERROR in ./index.ts Module build failed: A file specified in tsconfig.json could not be found: /home/menesr/dev/front/scanr-frontend-admin/typings/main.d.ts
# with option --no-audit else critial vulnerability and breaks build (TODO AFTER security audit and solve front vulnerability)
#rm -rf node_module && npm start --no-audit
# NB. npm run-script build and NOT merely npm build
#npm run-script build
#cd ~/dev/scanesr/front/docker/
#rm -rf dist && cp -rf ~/dev/scanesr/front/scanr-frontend/dist .
#sudo docker build -f Dockerfile.frontend -t front/scanr-frontend:0.0.1-pre .
sudo docker create -e HOST=scanr.local --volumes-from nginx -e ALIASES=scanr.enseignementsup-recherche.gouv.fr -e VIRTUAL_HOST=local --log-opt max-size=200m --log-opt max-file=5 --hostname scanr-frontend --name scanr-frontend  -v /var/log/scanr-frontend:/logs -v /var/deploy/scanr-frontend/config/root.conf:/root.conf -v /var/deploy/scanr-frontend/config/root_location.conf:/root_location.conf front/scanr-frontend:0.0.1-pre
sudo docker start scanr-frontend
# check :
sudo docker logs -f scanr-frontend
