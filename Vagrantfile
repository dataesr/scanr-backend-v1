# Debian 8 Jessie VM with 2CPUs, 4GB RAM & additional disks :
# - additional 20GB disk at /var/lib/docker (and not ~/dev rsync'd)
# - ScanR prereqs install

# Configuration
# workflow importAll requires 2gb ram
VAGRANT_MEMORY_MB = 4096
# install in Docker PLUS env dev requires more than 10GB
VAGRANT_DISK_SIZE = "10GB" # KO because still needs internal repartioning
VAGRANT_CPUS = 2 # else 1
VAGRANT_IP = "192.168.56.35"
# synced folder. See below how to override it ex. to move everything except source to d: if c: is too small
VAGRANT_SYNCED_FOLDER = "rsync"
#VAGRANT_SYNCED_FOLDER = "C:\\dev\\menesr\\vm2\\rsync"

$USER_INSTALL_SCRIPT = <<-SHELL

# as non-privileged (though sudoer) vagrant user
# (else created dirs are owned by root even if sudo su - vagrant)

export DEBIAN_FRONTEND=noninteractive
# else error default: dpkg-preconfigure: unable to re-open stdin: No such file or directory https://serverfault.com/questions/500764/dpkg-reconfigure-unable-to-re-open-stdin-no-file-or-directory

# SETUP SOURCES

# NB. vagrant creates user and rsyncs sources to ~/dev/scanesr

# Creating work folders in home
mkdir -p dl dev install save
ls -la

# build java & python then docker images

# Start docker containers using deploy/prod/*/run.sh scripts from there
SHELL

Vagrant.configure("2") do |config|
 
  config.vm.box = "debian/jessie64"
 
  # Prevent custom SSH key generation for each Vagrant image
  config.ssh.insert_key = false
 
  # Install VirtualBox Guest and update
  config.vbguest.auto_update = true
  
  # default host mount, else none
  config.vm.synced_folder ".", "/vagrant", id: "vagrant", type: "virtualbox"
  # also using rsync, else no symlink support since NTFS ; requires manually : vagrant rsync, or once per startup vagrant rsync-auto
  config.vm.synced_folder VAGRANT_SYNCED_FOLDER, "/home/vagrant/dev", id: "dev", type: "rsync",
    rsync__args: [
      "--verbose", "--archive", "-z", "--copy-links" # defaults
      # override defaults, else deletes existing files in target that are not in source ex. build output https://github.com/hashicorp/vagrant/issues/7036
      # and NOT --ignore-existing else doesn't update file after first sync
  ]
 
  # Private network to enable communication between machines and host
  config.vm.network "private_network", ip: VAGRANT_IP
 
  # ALTERNATIVELY Port forwarding : add yours but beware conflicts, so prefer using private network IP
  #config.vm.network "forwarded_port", guest: 8080, host: 8080
  
  # RAM & CPU
  config.vm.provider "virtualbox" do |vb|
    vb.memory = VAGRANT_MEMORY_MB
    vb.cpus = VAGRANT_CPUS
    
    # Disk - add another disk (way simpler than resizing default one) :
    file_to_disk = File.realpath( "." ).to_s + "/disk.vdi"
    if ARGV[0] == "up" && ! File.exist?(file_to_disk) 
      puts "Creating 20GB disk #{file_to_disk}."
      vb.customize [
          'createhd', 
          '--filename', file_to_disk, 
          '--format', 'VDI', 
          '--size', 20000 # 20 GB
          ] 
      vb.customize [
          'storageattach', :id, 
          '--storagectl', 'SATA Controller', 
          '--port', 1, '--device', 0, 
          '--type', 'hdd', '--medium', 
          file_to_disk
          ]
    end
  end
  
  # use and mount added disk at ~/dev :
  config.vm.provision "add_disk",
    type: "shell",
    name: "root",
    privileged: true,
    inline: <<-SHELL
set -e
set -x

if [ -f /etc/disk_added_date ]
then
   echo "disk already added so exiting."
   exit 0
fi

sudo fdisk -u /dev/sdb <<EOF
n
p
1


t
8e
w
EOF

#pvcreate /dev/sdb1
#vgextend VolGroup /dev/sdb1
#lvextend /dev/VolGroup/lv_root
#resize2fs /dev/VolGroup/lv_root
mkfs.ext4 /dev/sdb1
#mkdir -p /home/vagrant/dev
#mount -t ext4 /dev/sdb1 /home/vagrant/dev
mkdir -p /var/lib/docker
mount -t ext4 /dev/sdb1 /var/lib/docker

date > /etc/disk_added_date
SHELL
  
  # Disk - alternatively resizes the default disk, BUT not used because disk has still to be resized from within the VM
  # Disk (requires auto-installing vagrant-disksize plugin, see https://unix.stackexchange.com/questions/176687/set-storage-size-on-creation-of-vm-virtualbox )
  # disk has to be resized from within the VM :
  # on debian apt-get install lvm2 parted then https://medium.com/@phirschybar/resize-your-vagrant-virtualbox-disk-3c0fbc607817
  # else https://gist.github.com/christopher-hopper/9755310
  required_plugins = %w( vagrant-vbguest vagrant-disksize )
  _retry = false
  required_plugins.each do |plugin|
    unless Vagrant.has_plugin? plugin
      system "vagrant plugin install #{plugin}"
      _retry=true
    end
  end
  if (_retry)
    exec "vagrant " + ARGV.join(' ')
  end
  config.disksize.size = VAGRANT_DISK_SIZE
  config.vm.provision "resize",
    type: "shell",
    name: "root",
    privileged: true,
    inline: <<-SHELL
resize2fs /dev/sda1
SHELL
  
  config.vm.provision "root_install",
    type: "shell",
    name: "root",
    privileged: true, # avoids too many sudo DEBIAN_FRONTEND=noninteractive apt-get -yq install x
    path: "install_prereqs.sh"
 
  config.vm.provision "user_install",
    type: "shell",
    name: "vagrant",
    privileged: false, # else created dirs are owned by root even if sudo su - vagrant
    inline: $USER_INSTALL_SCRIPT
 
end