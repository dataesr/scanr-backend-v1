#!/bin/bash
# this script can be executed as is, or docker commands copied from it and executed independently as required

for container in `docker ps -a -f "name=scanr-filebeat" --format "{{.ID}}"`; do
    echo "Destroying previously created container" ${container}
    docker stop ${container} || true
    docker rm -f ${container}
done

# Backend
docker run -d \
--name scanr-filebeat-backend \
--hostname scanr-filebeat-backend \
--network scanr-private \
-v /var/deploy/scanr-filebeat/filebeat-backend.yml:/usr/share/filebeat/filebeat.yml \
-v /var/deploy/scanr-elasticsearch/certificates:/usr/share/filebeat/config/certs \
-v /var/log/scanr-backend_app:/var/log/scanr-backend_app \
-v /var/log/scanr-backend_workflow:/var/log/scanr-backend_workflow \
--log-opt max-size=200m --log-opt max-file=5 \
--restart=unless-stopped \
docker.elastic.co/beats/filebeat:7.13.1

# Nginx
docker run -d \
--name scanr-filebeat-nginx \
--hostname scanr-filebeat-nginx \
--network scanr-private \
-v /var/deploy/scanr-filebeat/filebeat-nginx.yml:/usr/share/filebeat/filebeat.yml \
-v /var/deploy/scanr-elasticsearch/certificates:/usr/share/filebeat/config/certs \
-v /var/log/scanr-nginx:/var/log/nginx \
--log-opt max-size=200m --log-opt max-file=5 \
--restart=unless-stopped \
docker.elastic.co/beats/filebeat:7.13.1