#!/bin/bash
# this script is used by Vagrant but can also be executed as is, or shell commands copied from it and executed independently as required

# as root (to avoid in Vagrant too many sudo DEBIAN_FRONTEND=noninteractive apt-get -yq install x)

export DEBIAN_FRONTEND=noninteractive
# else error default: dpkg-preconfigure: unable to re-open stdin: No such file or directory https://serverfault.com/questions/500764/dpkg-reconfigure-unable-to-re-open-stdin-no-file-or-directory

# Install generic packages
apt-get update
apt-get install -y git
apt-get install -y sudo vim curl byobu htop

# Install Docker : (see https://docs.docker.com/install/linux/docker-ce/debian/#install-using-the-repository)
apt-get install -y --no-install-recommends apt-transport-https ca-certificates curl gnupg2 software-properties-common
curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
#apt-key fingerprint 0EBFCD88
add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable"
apt-get update
apt-get install -y docker-ce

# Configure ElasticSearch :
# Virtual Memory : 
# else ERROR: [1] bootstrap checks failed [1]: max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]
# see https://www.elastic.co/guide/en/elasticsearch/reference/current/vm-max-map-count.html https://github.com/docker-library/elasticsearch/issues/98
# NB. sysctl -w vm.max_map_count=262144 not permanent
echo 'vm.max_map_count=262144' | tee --append /etc/sysctl.conf
# open files limit :
# else java.nio.file.FileSystemException: ...: Too many open files
# NB. ulimit -n 65536 not permanent
echo '* hard nofile 64000' | tee --append /etc/security/limits.conf

# BUILD ENVIRONMENT

# Install java 8 JRE openjdk using backports : (see https://xmoexdev.com/wordpress/installing-openjdk-8-debian-jessie/)
echo "deb http://http.debian.net/debian jessie-backports main" | tee /etc/apt/sources.list.d/jessie-backports.list # .list else Ignoring file ... as it has no filename extension ; NOT ftp.debian.org else E: Failed to fetch http://ftp.debian.org/debian/pool/main/o/openjdk-8/openjdk-8-jdk-headless_8u171-b11-1~bpo8+1_amd64.deb  Connection failed
apt-get update
apt-get install -y --no-install-recommends -t jessie-backports openjdk-8-jdk

# Install Maven 3 : (using backports else brings openjdk-7-jre-headless)
apt-get install -y --no-install-recommends -t jessie-backports maven

# install node : (see https://nodejs.org/en/download/package-manager/#debian-and-ubuntu-based-linux-distributions)
curl -sL https://deb.nodesource.com/setup_10.x | sudo -E bash -
apt-get install -y --no-install-recommends nodejs

# Install Python 3 : (requires sources ; see https://github.com/yyuu/pyenv/wiki/Common-build-problems)
apt-get install -y --no-install-recommends make build-essential libssl-dev zlib1g-dev libbz2-dev libreadline-dev libsqlite3-dev wget curl llvm libncurses5-dev libncursesw5-dev xz-utils tk-dev
USER_DIR=`ls /home | head -n 1`
echo "Finding out user dir : $USER_DIR"
cd /home/$USER_DIR
pwd
cd dev/scanesr/python/companies_plugin-*
chmod +x ../*/tools/*sh
sudo -H -u $USER_DIR bash -c "tools/install_python3.sh" 2>> /dev/null # as user because goes in ~/local/scanr ; redirect error else download status takes up kilometers
# then source tools/setup_venv.sh allows to use python3 in virtualenv