#!/bin/bash
# Générer des certificats et un CA pour Elasticsearch et Kibana

cd "$(dirname "$0")"

docker run --rm \
--name create_certs \
-u "0" \
--workdir="/usr/share/elasticsearch" \
-v $(pwd)/generated:/certs \
-v $(pwd):/usr/share/elasticsearch/config/certificates \
docker.elastic.co/elasticsearch/elasticsearch:7.13.1 \
bash -c '
if [[ ! -f /certs/bundle.zip ]]; then
  bin/elasticsearch-certutil cert --silent --pem --days 3650 --in config/certificates/instances.yml -out /certs/bundle.zip;
  unzip /certs/bundle.zip -d /certs; 
fi;
chown -R 1000:0 /certs
'