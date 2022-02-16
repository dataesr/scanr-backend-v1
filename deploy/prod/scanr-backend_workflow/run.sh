#!/bin/bash
# this script can be executed as is, or docker commands copied from it and executed independently as required

for container in `docker ps -a -f "name=scanr-backend-workflow" --format "{{.ID}}"`; do
    echo "Destroying previously created container" ${container}
    docker stop ${container} || true
    docker rm -f ${container}
done

# workflow :
#cd ~/dev/scanesr/java
#mvn clean install
# Before app because creates ES settings and mapping
#cd ~/dev/scanesr/java/docker/
#cp -rf ~/dev/scanesr/java/scanr-backend/workflow/target/workflow-3.0-SNAPSHOT.jar .
#sudo docker build -f Dockerfile.springboot --build-arg JAR_FILE=workflow-3.0-SNAPSHOT.jar -t fr.gouv.recherche.scanr/workflow:3.0-SNAPSHOT .
docker network create scanr

docker create \
--name scanr-backend-workflow \
--hostname scanr-backend-workflow \
--net=scanr \
-v /var/db/scanr-screenshots:/var/db/scanr-screenshots \
-v /var/log/scanr-backend_workflow:/logs \
-v /var/deploy/scanr-backend_workflow/config/application.properties:/application.properties \
-v /home/sword/menesr-upload:/mnt/menesr-upload:ro \
-v /var/deploy/scanr-elasticsearch/certificates/generated/ca/ca.crt:/ca.crt \
--expose 8080 -e VIRTUAL_HOST=scanr-admin.local \
-e LETSENCRYPT_HOST=scanr-admin.local \
-e LETSENCRYPT_EMAIL=mail@mail.com \
-e VIRTUAL_PATH=/api \
--log-opt max-size=200m --log-opt max-file=5 \
--restart=unless-stopped \
fr.gouv.recherche.scanr/workflow:3.0-SNAPSHOT

docker network connect scanr-private scanr-backend-workflow
docker start scanr-backend-workflow

# check :
docker logs -f scanr-backend-workflow
# may have error but _settings & mapping are actually OK & disappears at next start : failed to load elasticsearch nodes : org.elasticsearch.index.mapper.MapperParsingException: analyzer [text] not found for field [description]
# further manual check :
#sudo docker exec -it scanr-backend-workflow /bin/bash
#wget http://localhost:8080/api/swagger-ui.html