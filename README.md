<img src="https://scanr.enseignementsup-recherche.gouv.fr/app/assets/img/home/scanR_H.png" width="332" height="93">
# scanR : Moteur de recherche de la recherche et de l'innovation française

https://scanr.enseignementsup-recherche.gouv.fr/  

Contact: scanr@recherche.gouv.fr  

Licences: MIT, APACHE 2.0  

Copyright (c) MESRI

## A propos de scanR

scanR est le moteur de recherche de la recherche et de l'innovation française.

scanR est une application web d’aide à la caractérisation des acteurs de la recherche et de l'innovation qui comprend aussi bien des organisations publiques (unité de recherche de tous types, institutions publiques) que privées (entreprises). scanR est un agrégateur de sources. Il n'est donc pas exhaustif mais tente de rassembler le plus d'information possible afin de pouvoir caractériser ses objets le plus finement possible.

## Fonctionnalités

- Récupération et indexation de données structurées sur les différents objets
    - structures de recherche francaises (source : répertoire national des structures de recherche, répertoire https://appliweb.dgri.education.fr/rnsr/, répertoire SIRENE https://www.sirene.fr)
    - projets de recherche (ANR, PCRDT, H2020, PHRC, etc.)
    - productions scientifiques (thèses, brevets)
    - personnes / participants

- Exploration des site web :
    - récupération des données textuelles
    - récupération des comptes sociaux détectés
    - récupération de logo et/ou de screenshots de l'index du site

- API de recherche :
    - pour chaque type d'objet (structures, projets, publications, personnes)
    - via un moteur elasticsearch
    - possibilité de filtres
    - swagger disponible : https://scanr.enseignementsup-recherche.gouv.fr/api/swagger-ui.html

- Interface web :
    - interface de recherche et de recherche avancée avec filtres
    - outils d'analyse visuelle des résultats de recherche (statistiques descriptives des résultats)
    - présentation des données des structures :
        - informations sur l'identité de la structure (nom, acronyme, logo, type de structure, responsables, relations statutaires avec d'autres structures, thématiques)
        - informations sur les projets menés par la structure
        - informations sur les productions scientifiques de la structure (publications, thèses, brevets)
        - informations sur les relations scientifiques entre entités

## Team

- Département d'outils d'aide à la décision, Sous-direction des Systèmes d'information et études statistiques (SIES) du ministère de l'Enseignement supérieur, de la Recherche et de l'Innovation.
- SideTrade, anciennement C-Radar (entre mars 2016 et avril 2018)
- Coexya (ex: Sword-group) (depuis mars 2018)

## Installation

### Environnement de développement

En production, scanR se déploie dans une Debian 9 (LTS) avec Docker.

L'environnement de développment consiste en un déploiement de production, mais dans une VM locale, et dont on peut arrêter le conteneur Docker d'un composant Java ou Python pour plutôt exécuter celui-ci directement dans la VM afin de faciliter son redéploiement et son debug, et dont le code source est synchronisé depuis l'hôte Windows pour pouvoir développer dans Windows.

Voici donc comment développer scanR sous Windows 10 en tout en l'exécutant dans une machine virtuelle locale Debian Jessie, en s'aidant de Vagrant et chocolatey.

#### Développement du code source sous Windows :

Récupérer le code source :
````
cd C:/dev/menesr/vm/rsync
git@github.com:dataesr/scanr-backend.git
````

et committer et développer là sous Windows avec
[Eclipse Java](https://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/photonr),
[PyCharm](https://www.jetbrains.com/pycharm/download),
[Notepad++](https://notepad-plus-plus.org/download),
[ConEmu](https://conemu.github.io/en/Downloads.html)...
après y avoir configuré encoding UTF-8 et fin de ligne Unix (dans Eclipse, dans les propriétés de tous les projets et pas seulement de la racine scanesr).

NB. éviter de committer dans la VM, ou penser à faire un git pull dans Windows après.
NB. git est configuré pour garder les fichiers source au format Linux et non Windows, vu que ce code est ensuite exécuté directement dans la VM Linux (par rsync). L'équivalent en configuration manuelle est :
````
git config core.autocrlf false
git config --list
````

#### Installer Vagrant :

à l'aide de chocolatey (ou manuellement) : (comme dit à https://chocolatey.org/install)
````
dans une ligne de commande Windows en tant qu'administrateur (search "cmd" > clic droit "Run as admin"), exécuter
@"%SystemRoot%\System32\WindowsPowerShell\v1.0\powershell.exe" -NoProfile -InputFormat None -ExecutionPolicy Bypass -Command "iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))" && SET "PATH=%PATH%;%ALLUSERSPROFILE%\chocolatey\bin"

choco install vagrant
````

#### Déployer dans une VM locale Debian 8 ayant Java et Docker :

Dans une ligne de commande Windows, démarrer la VM et y synchroniser le code source :

````
cd C:/dev/menesr/vm
cp rsync/scanesr/Vagrantfile rsync/scanesr/*sh .
vagrant up
vagrant rsync-auto
# Attention, il peut être nécessaire de le redémarrer, notamment après un commit depuis la VM et non Windows
````

Ceci créé une VM (IP 192.168.56.35, disque 10GB additionnel monté sous ~/dev), la démarre et installe aussi les prérequis : Maven 3, Java 8, Python 3, Docker. Sinon pour plutôt déployer manuellement dans une Debian 8, installer les prérequis avec les ligne de commandes shell au début du [Vagrantfile](Vagrantfile).

Puis dans une autre ligne de commande Windows, se connecter à la VM pour builder et déployer :
````
cd C:/dev/menesr/vm
vagrant ssh
cd /home/vagrant/dev/scanesr

# DEPLOY CONF, BUILD ALL AND DEPLOY DOCKER CONTAINERS

# this script can be executed as is, or shell commands copied from it and executed independently as required
# IMPORTANT do CTRL-C to stop logging a docker container and start the next one
./buildAndStartAll.sh <ENV>
# where ENV can be : preprod (else prod by default)
# WARNING the first time, building the pythondp docker image (and subsequent python images)
# may fail. In this case, execute this script again.

# try it out :
# in c:\Windows\System32\Drivers\etc\hosts :
<your.vm.ip.here> scanr.local scanr-admin.local
# admin : login with CHANGEME/CHANGEME at http://scanr-admin.local
# front : search ex. occiware at http://scanr.local

# Clean up docker (to gain disk size) - stop and remove all containers and images :
docker stop $(sudo docker ps -aq)
docker rm $(sudo docker ps -aq)
docker rmi $(sudo docker images -q)
````

#### Importer les données (2Gb ram minimum):

(requires 2gb ram) :
Go to the Swagger API doc UI by clicking on SWAGGER in the admin UI toolbar
(or by going to http://<your.vm.ip.here>:8080/api/swagger-ui.html or http://scanr-admin.local/api/swagger-ui.html)
there in "import menesr" call GET /admin/import/all
and see increasing data size in "count" by calling GET /services/counts .
BEWARE, this adds 3-200GB, so if disk becomes full in development environment,
increase main disk size in VirtualBox and check that Elasticsearch and MongoDB work again
(ex. restart Elasticsearch if it has been put in read-only).

Dans Swagger UI, cliquer sur le bouton "Try it out" de l'opération GET /admin/import/all
Et voyez la taille des données augmenter dans SwaggerUI dans "count" en appelant l'opération GET /services/counts

#### Planifier les tâches d'extraction :

Dans l'onglet "QUEUES" de l'UI d'administration, cliquer sur chaque composant pour voir
sa planification, forcer son démarrage ou le déplanifier.
Ou bien utiliser la ScheduleApi directement depuis Swagger API DOC UI.

### Déploiement en intégration

Ressources minimum conseillées en intégration : 1 VM avec 4 CPU, 16go RAM, 500go disque
(20-30go mongodb, 23-60go elasticsearch, 70-200go cassandra depending on indexing phase).
Pas besoin de partitionner (car pas besoin de faire de snapshots ; sinon, /var/lib/docker a besoin de 10-15go
et /vagrant/dev de 3go).

Et voir le déploiement en préproduction.

### Déploiement en (pré)production

````bash
# create debian 8 jessie
# create lvm volumes (refer to the original df -h)

# SETUP SOURCES AND PREREQS (do this only once)

# Adding user menesr
sudo adduser menesr
sudo usermod -a -G sudo menesr

# Switch to user menesr
su - menesr

# Creating work folders in home
mkdir -p install dl save dev

# Prepare sources : put sources (front, java, python) in dev
# depends on your deployment method : git / rsync / scp / ...
sudo apt-get install git
cd ~/dev
git clone https://github.com/dataesr/scanr-backend.git
# if you have a gitlab account, rather generate a key and copy-paste it to your gitlab account :
ssh-keygen -o -t rsa -C "your.email@example.com" -b 4096
git clone git@github.com:dataesr/scanr-backend.git

# Install prereqs
cd scanesr
chmod +x install_prereqs.sh
sudo ./install_prereqs.sh

# UPDATE SOURCES (do this everytime these scripts change in source code)

cd ~/dev/scanesr
git pull

# configure certificates
# TODO
sudo mkdir -p /etc/nginx/certs
# and put your certs in /etc/nginx/certs

# BUILD ALL AND DEPLOY DOCKER CONTAINERS

cd ~/dev/scanesr
chmod +x buildAndStartAll.sh
sudo ./buildAndStartAll.sh
# or do manually what is in this script.
# if MongoDB fails because of not enough space (esp. in VM), do: sudo /var/deploy/scanr-mongo/run.sh --smallfiles
# look in /var/deploy/*/run.sh how to check deployments

# try it out :
# admin : login using CHANGEME/CHANGEME at http://scanr-admin.local/api/swagger-ui.html and do import menesr : GET /admin/import/all (BEWARE adds 3GB)
# front : search ex. occiware at http://scanr.local
````


## Development

### Démarrer dans l'IDE au-dessus des composants d'une VM :

Dans Eclipse, créer une nouvelle configuration d'exécution avec les paramètres :
- Project : 
- Main Class : fr.gouv.recherche.scanr.Application
- Program arguments : --spring.config.location=file:///${git_work_tree}/deploy/prod/scanr-backend_app/config/application.properties
(resp. ...deploy/prod/scanr-backend_workflow/config/application.properties)

### Alternative - Déployer et débugger des composants Java ou Python dans la VM locale :

Pour déployer des composants Java ou Python en développement, typiquement à des fins de debugging :
- arrêter leur conteneur Docker,
- les aliaser à localhost dans /etc/hosts,
- les builder et démarrer en mode debug directement dans la VM

Par exemple, pour le workflow côté java et le CoreExtractor côté python :
````bash
vi /etc/hosts
127.0.0.1 localhost scanr-backend_workflow scanr-python_comp_coreextractor

# worfklow
sudo docker stop scanr-backend_workflow
# start workflow with default conf (scanesr's "brain", will conf everything : ES, rabbitmq, cassandra)
vi application.properties
cd ~/dev/scanesr/java/scanr-backend/workflow && mvn clean install && java -Xmx2048m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8002 -Dspring.config.location=/var/deploy/scanr-backend_app/config/application.properties -jar target/*.jar
# then connect an Eclipse remote debugging session to <your.vm.ip.here>:8002

# CoreExtractor
sudo docker stop scanr-python_comp_coreextractor
cd ~/dev/scanesr/python/comp_coreextractor-* && source tools/setup_venv.sh && ./tools/deps.sh && python comp_coreextractor/main.py --conf /var/deploy/scanr-python_comp_coreextractor/config.json --proc 1&
````

#### Alternative development deployment of front :

start dev server (won't work without backend, BEWARE conflicts with the java processes' 8080 port)
````bash
cd ~/dev/scanesr/front/scanr-frontend
npm start
browse to http://<your.vm.ip.here>:8080/
# OR
#npm run dev
````

### Development FAQ

- how to introspect Elasticsearch data :
````
sudo docker exec -it scanr-elasticsearch /bin/bash
curl -X GET "localhost:9200/_stats"
curl -X GET "localhost:9200/scanr/_search?q=inria"
````
or from host :
````
sudo docker inspect scanr-elasticsearch
curl -X GET "172.18.0.3:9200/_stats"
````

- how to introspect MongoDB data :
````
sudo docker exec -it scanr-mongo /bin/bash
mongo scanr
db.stats()
show collections
db.structure.find({ "label" : /.*INRIA.*/ })
mongodump --db scanr
ls -l /dump/scanr/
````
- how to introspect Cassandra data :
````
sudo docker exec -it scanr-cassandra /bin/bash
nodetool cfstats
cqlsh crawl_store
SELECT * FROM crawl_info limit 2;
````

- how to introspect RabbitMQ data :
````
sudo docker exec -it scanr-rabbit /bin/bash
rabbitmqadmin list queues
rabbitmqadmin get queue=CORE_EXTRACTOR requeue=false
````
RabbitMQ Management : http://{hostname}:15672

- Error building the pythondp docker image (breaking subsequent python images build) :
````
Step 8/25 : RUN set -ex         && buildDeps='          gcc             libbz2-dev
...
2019-01-02 17:19:50 (5.30 MB/s) - ‘python.tar.xz.asc’ saved [836/836]

+ mktemp -d
+ export GNUPGHOME=/tmp/tmp.03FursnPZ9
+ gpg --keyserver ha.pool.sks-keyservers.net --recv-keys 97FC712E4C024BBEA48A61ED3A5CA953F73C700D
gpg: keyring `/tmp/tmp.03FursnPZ9/secring.gpg' created
gpg: keyring `/tmp/tmp.03FursnPZ9/pubring.gpg' created
gpg: requesting key F73C700D from hkp server ha.pool.sks-keyservers.net
gpgkeys: key 97FC712E4C024BBEA48A61ED3A5CA953F73C700D can't be retrieved
gpg: no valid OpenPGP data found.
gpg: Total number processed: 0
The command '/bin/sh -c set -ex         && buildDeps='          gcc             libbz2-dev
... && rm -rf /usr/src/python ~/.cache' returned a non-zero code: 2
Unable to find image 'pythondp:latest' locally
docker: Error response from daemon: pull access denied for pythondp, repository does not exist or may require 'docker login'.
...
Step 1/14 : FROM pythondp
pull access denied for pythondp, repository does not exist or may require 'docker login'
Unable to find image 'python/oaifetcher:0.16' locally
````
=> because gpg key server times out (or is down). Executing the build script again solves it
(````gpg: key F73C700D: public key "Larry Hastings <larry@hastings.org>" imported````).
https://stackoverflow.com/questions/34760945/key-server-times-out-while-installing-docker-on-ubuntu-14-04

- Java build warning
````
16:43:07.235 [main] DEBUG org.springframework.core.type.classreading.AnnotationAttributesReadingVisitor - Failed to class-load type while reading annotation metadata. This is a non-fatal error, but certain annotation metadata may be unavailable.
java.lang.ClassNotFoundException: javax.annotation.Nullable
        at java.net.URLClassLoader.findClass(URLClassLoader.java:381)
16:43:07.237 [main] DEBUG org.springframework.data.repository.config.RepositoryConfigurationDelegate$LenientAssignableTypeFilter - Could not read super class [com.mysema.query.mongodb.MongodbSerializer] of type-filtered class [org.springframework.data.mongodb.repository.support.SpringDataMongodbSerializer]
````
=> bug connu de spring data mongo, sans impact, le mettre à jour devrait le résoudre https://jira.spring.io/browse/DATAMONGO-1512 https://stackoverflow.com/questions/40158730/java-lang-classnotfoundexception-javax-annotation-nullable 

- Python build warning
````
debconf: delaying package configuration, since apt-utils is not installed
````
=> not a bug, safe to ignore https://stackoverflow.com/questions/51023312/docker-having-issues-installing-apt-utils

- Python build warning
````
  Running setup.py (path:/home/vagrant/dev/scanesr/python/companies_plugin-1.21/virtualenv/build/coverage/setup.py) egg_info for package coverage
    /home/vagrant/local/scanr/lib/python3.4/distutils/dist.py:260: UserWarning: Unknown distribution option: 'python_requires'
````
=> try to use setuptools instead of distutils https://stackoverflow.com/questions/8295644/pypi-userwarning-unknown-distribution-option-install-requires

- Python build warning
````
*** Error compiling '/home/vagrant/dev/scanesr/python/companies_plugin-1.21/virtualenv/build/Jinja2/jinja2/asyncfilters.py'...
  File "/home/vagrant/dev/scanesr/python/companies_plugin-1.21/virtualenv/build/Jinja2/jinja2/asyncfilters.py", line 7
    async def auto_to_seq(value):
            ^
SyntaxError: invalid syntax
````
=> because python 3.4 would need import asyncio @asyncio.coroutine => try python 3.5 https://stackoverflow.com/questions/43948454/python-invalid-syntax-with-async-def

## Develoment - models

JSON ->
Structure (by ImportMenesrApi which cleans it) ->
FullStructure (Mongo + API get) ->
FullStructureIndex (ES + API search)