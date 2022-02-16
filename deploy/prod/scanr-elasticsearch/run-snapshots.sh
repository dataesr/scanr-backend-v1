#!/bin/bash

URL_REQ="http://localhost:9200/_snapshot/backup"
TIMESTAMP=`date +%Y%m%d`

#specify the index to backup
#include_global_state: to prevent the cluster global state to be stored as part of the snapshot

curl -XPUT "$URL_REQ/$TIMESTAMP-persons?wait_for_completion=true" -H 'Content-Type: application/json' -d '{"indices": "persons",
 "ignore_unavailable": true,
 "include_global_state": false
}'
curl -XPUT "$URL_REQ/$TIMESTAMP-projects?wait_for_completion=true" -H 'Content-Type: application/json' -d '{"indices": "projects",
 "ignore_unavailable": true,
 "include_global_state": false
}'
curl -XPUT "$URL_REQ/$TIMESTAMP-publications?wait_for_completion=true" -H 'Content-Type: application/json' -d '{"indices": "publications",
 "ignore_unavailable": true,
 "include_global_state": false
}'
curl -XPUT "$URL_REQ/$TIMESTAMP-structures?wait_for_completion=true" -H 'Content-Type: application/json' -d '{"indices": "structures",
 "ignore_unavailable": true,
 "include_global_state": false
}'

echo "Done!"
