#!/bin/bash
# this script can be executed as is, or docker commands copied from it and executed independently as required

for container in `docker ps -a -f "name=scanr-backend-app" --format "{{.ID}}"`; do
    echo "Destroying previously created container" ${container}
    docker stop ${container} || true
    docker rm -f ${container}
done

# app :
#cd ~/dev/scanesr/java
#mvn clean install
#cp -rf ~/dev/scanesr/java/scanr-backend/app/target/app-3.0-SNAPSHOT.jar .
#docker build -f Dockerfile.springboot --build-arg JAR_FILE=app-3.0-SNAPSHOT.jar -tfr.gouv.recherche.scanr/app:3.0-SNAPSHOT .
docker network create scanr

docker create \
--name scanr-backend-app \
--net=scanr \
--hostname scanr-backend-app \
-v /var/db/scanr-screenshots:/var/db/scanr-screenshots \
-v /var/log/scanr-backend_app:/logs \
-v /var/deploy/scanr-backend_app/config/application.properties:/application.properties \
-v /var/deploy/scanr-elasticsearch/certificates/generated/ca/ca.crt:/ca.crt \
-p 88:8080 \
-e VIRTUAL_HOST=scanr.local \
-e LETSENCRYPT_HOST=scanr.local \
-e LETSENCRYPT_EMAIL=mail@mail.com \
-e VIRTUAL_PATH=/api \
--log-opt max-size=200m --log-opt max-file=5 \
--restart=unless-stopped \
fr.gouv.recherche.scanr/app:3.0-SNAPSHOT

docker network connect scanr-private scanr-backend-app
docker start scanr-backend-app
# check :
docker logs -f scanr-backend-app
# further manual check :
#docker exec -it scanr-backend-app /bin/bash
#wget http://localhost:8080
# => 404
