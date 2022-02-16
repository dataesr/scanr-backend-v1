#!/bin/bash
# this script can be executed as is, or shell commands copied from it and executed independently as required
# IMPORTANT do CTRL-C to stop logging a docker container and start the next one

# (puts nginx in /etc and everything else in /var/deploy)
sudo mkdir -p /var/deploy
sudo cp -rf ~/dev/scanesr/deploy/prod/* /var/deploy/
ENV=$1
if [ "$ENV" != "" ]; then
echo Copying also $ENV configuration files
sudo cp -rf ~/dev/scanesr/deploy/$ENV/* /var/deploy/
fi
sudo cp -rf /var/deploy/etc/* /etc/

read -p "start dependencies ? [y|N]" START_DEPENDENCIES
if [ "$START_DEPENDENCIES" == "y" ]; then
sudo /var/deploy/scanr-cassandra/run.sh
sudo /var/deploy/scanr-mongo/run.sh
# OR to not require 3GB (smallfiles) ex. when in a VM : sudo /var/deploy/scanr-mongo/run.sh --smallfiles
sudo /var/deploy/scanr-rabbit/run.sh
sudo docker-compose -f /var/deploy/scanr-elasticsearch/docker-compose-elasticsearch-prod.yml up -d
fi
sudo docker ps -a

# nginx (will auto listen to java backend apps connecting to scanr network and vhost them) :
sudo /var/deploy/scanr-nginx/run.sh

# java processes :
cd ~/dev/scanesr
mvn clean install

# workflow :
# Before app because creates ES settings and mapping
# NB not using run.sh to see the output & because error when taking application.properties : " character not allowed in -v
cd ~/dev/scanesr/java/docker/
cp -rf ~/dev/scanesr/java/scanr-backend/workflow/target/workflow-3.0-SNAPSHOT.jar .
sudo docker build -f Dockerfile.springboot --build-arg JAR_FILE=workflow-3.0-SNAPSHOT.jar -t fr.gouv.recherche.scanr/workflow:3.0-SNAPSHOT .
sudo /var/deploy/scanr-backend_workflow/run.sh

# app :
cp -rf ~/dev/scanesr/java/scanr-backend/app/target/app-3.0-SNAPSHOT.jar .
sudo docker build -f Dockerfile.springboot --build-arg JAR_FILE=app-3.0-SNAPSHOT.jar -t fr.gouv.recherche.scanr/app:3.0-SNAPSHOT .
sudo /var/deploy/scanr-backend_app/run.sh

# build and start front & admin :

# frontend :
##cd ~/dev/scanesr/front/scanr-frontend
# NB. in webpack.config.js, comment config.devtool = 'source-map'; else errors in files .styl : ERR_INVALID_ARG_TYPE The "path" argument must be of type string. Received type undefined
# NB. npm start and NOT merely npm install, else ERROR in ./index.ts Module build failed: A file specified in tsconfig.json could not be found: /home/menesr/dev/front/scanr-frontend-admin/typings/main.d.ts
# run bootstrap instead of start which starts a local server, with option --no-audit else critical vulnerability and breaks build (TODO AFTER security audit and solve front vulnerability)
##rm -rf node_module && npm run bootstrap --no-audit
# NB. npm run-script build and NOT merely npm build
##npm run-script build
##cd ~/dev/scanesr/front/docker/
##rm -rf dist && cp -rf ~/dev/scanesr/front/scanr-frontend/dist .
##sudo docker build -f Dockerfile.frontend -t front/scanr-frontend:0.0.1-pre .
##sudo /var/deploy/scanr-frontend/run.sh

# admin :
cd ~/dev/scanesr/front/scanr-frontend-admin
# NB. in webpack.config.js, comment config.devtool = 'source-map'; else errors in files .styl : ERR_INVALID_ARG_TYPE The "path" argument must be of type string. Received type undefined
# NB. npm start and NOT merely npm install, else ERROR in ./index.ts Module build failed: A file specified in tsconfig.json could not be found: /home/menesr/dev/front/scanr-frontend-admin/typings/main.d.ts
# run bootstrap instead of start which starts a local server, with option --no-audit else critical vulnerability and breaks build (TODO AFTER security audit and solve front vulnerability)
rm -rf node_module && npm run bootstrap --no-audit
# NB. npm run-script build and NOT merely npm build
npm run-script build
cd ~/dev/scanesr/front/docker/
rm -rf dist && cp -rf ~/dev/scanesr/front/scanr-frontend-admin/dist .
sudo docker build -f Dockerfile.frontend -t front/scanr-admin:0.0.1-pre .
sudo /var/deploy/scanr-frontend-admin/run.sh

# check nginx front :
#sudo su -
# NB. beware, then sudo triggers message : sudo: unable to resolve host => change /etc/hostname accordingly https://ubuntuforums.org/showthread.php?t=1754106
# in c:\Windows\System32\Drivers\etc\hosts :
# admin : login with CHANGEME/CHANGEME at http://scanr-admin.local/api/swagger-ui.html and do import menesr : GET /admin/import/all (BEWARE adds 3GB)
# front : search ex. occiware at http://scanr.local

# python :
cd ~/dev/scanesr/python

# build python plugin dependencies :
cd companies_plugin-* && source tools/setup_venv.sh && ./tools/deps.sh && tools/install.sh && tools/build.sh && cd -
cd entities_extractor* && source tools/setup_venv.sh && ./tools/deps.sh && tools/install.sh && tools/build.sh && cd -
cd fastmatch* && source tools/setup_venv.sh && ./tools/deps.sh && tools/install.sh && tools/build.sh && cd -
cd cstore_api* && source tools/setup_venv.sh && ./tools/deps.sh && tools/install.sh && tools/build.sh && cd -
cd textmining* && source tools/setup_venv.sh && ./tools/deps.sh && tools/install.sh && tools/build.sh && cd -
cd webmining* && source tools/setup_venv.sh && ./tools/deps.sh && tools/install.sh && tools/build.sh && cd -

# build python plugins :
#cd oaifetcher-* && source tools/setup_venv.sh && ./tools/deps.sh && tools/install.sh && tools/build.sh && cd -
cd comp_companycrawler-* && source tools/setup_venv.sh && ./tools/deps.sh && tools/install.sh && tools/build.sh && cd -
cd comp_coreextractor-* && source tools/setup_venv.sh && ./tools/deps.sh && tools/install.sh && tools/build.sh && cd -
cd comp_screenshot-* && source tools/setup_venv.sh && ./tools/deps.sh && tools/install.sh && tools/build.sh && cd -
#cd scanr_doiresolver-* && source tools/setup_venv.sh && ./tools/deps.sh && tools/install.sh && tools/build.sh && cd -
#cd scanr_entityextractor-* && source tools/setup_venv.sh && ./tools/deps.sh && tools/install.sh && tools/build.sh && cd -
#cd scanr_publicationextractor* && source tools/setup_venv.sh && ./tools/deps.sh && tools/install.sh && tools/build.sh && cd -

# building python base image, using [menesr] additions & correction :
cd ~/dev/scanesr/python/docker
cp -rf ~/dev/scanesr/python/*/dist/*gz .
sudo docker build -f Dockerfile.pythondp -t pythondp .
# checking installed deps :
sudo docker run --rm pythondp sh -c ". /appenv/bin/activate && pip freeze"

# building, creating and starting each python process :

# oaifetcher
#sudo docker build -f Dockerfile.pythonplugin --build-arg ARTIFACT=oaifetcher -t python/oaifetcher:0.16 .
#sudo /var/deploy/scanr-python_oaifetcher/run.sh

# companycrawler
sudo docker build -f Dockerfile.pythonplugin --build-arg ARTIFACT=comp_companycrawler -t python/comp_companycrawler:1.51 .
sudo /var/deploy/scanr-python_comp_companycrawler/run.sh

# comp_coreextractor
sudo docker build -f Dockerfile.pythonplugin --build-arg ARTIFACT=comp_coreextractor -t python/comp_coreextractor:1.74 .
sudo /var/deploy/scanr-python_comp_coreextractor/run.sh

# scanr_screenshot
sudo docker build -f Dockerfile.screenshot --build-arg ARTIFACT=comp_screenshot -t python/comp_screenshot:0.7-0.10.23 .
sudo /var/deploy/scanr-python_comp_screenshot/run.sh

# scanr_doiresolver
#sudo docker build -f Dockerfile.pythonplugin --build-arg ARTIFACT=scanr_doiresolver -t python/scanr_doiresolver:0.9 .
#sudo /var/deploy/scanr-python_scanr_doiresolver/run.sh

# scanr_entityextractor
#sudo docker build -f Dockerfile.pythonplugin --build-arg ARTIFACT=scanr_entityextractor -t python/scanr_entityextractor:0.20 .
#sudo /var/deploy/scanr-python_scanr_entityextractor/run.sh

# scanr_publicationextractor
#sudo docker build -f Dockerfile.pythonplugin --build-arg ARTIFACT=scanr_publicationextractor -t python/scanr_publicationextractor:0.6 .
#sudo /var/deploy/scanr-python_scanr_publicationextractor/run.sh
