# Home Operating System

The Home Operating System is a full stacked smart home control endpoint written in Java. It could run on Java Virtual 
Machines (1.6 and higher). 

## How to build

### Requirements

- Java SDK (at least 1.6)
- Maven 3

### Preperation

After checkout and before the first build, please make sure you've a copy of JWave library and libcul as well in your local
Maven repository (usually it's the folder .m2/repository` in your user's home folder ).If you do not have the needed libraries,
 no problem. The needed versions are available as resources within this project. Continue related to your operating system as follows
 
 
#### Linux and Mac
Linux and Mac user could easily run `sh install_libs_to_local_repository.sh` in a Terminal to copy the libraries to the local
Maven repopsitory.

#### Windows
Please take a look at the folders `driver/cul` and `driver/zwave`. You'll find two sh-files that include a maven call. Copy the
Maven call and use it in your windows terminal. Do not forget to change the "/" to "\\" for path declarations.

### Build

After preparation has successfully finished, simply type `mvn clean install` in your root project folder. Maven will complie
everything and creates a folder "deployment" wherein the complete runnable project will be placed. When the build ends with 
success, go into the deployment folder (`cd deployment`) and run either the GUI-Version or the normal version of the HomeOS.

If you do not have changed one of the pom-files, the generated deployment folder will include a complete runnable version 
of the HomeOS including a driver that simulates numerous sensors for temperature and humidity. 

#### Build additional drivers

Take a look at the pom-file in the project root folder. You'll see, that several lines will be ignored due to the comment 
statement (`<!-- -->`). These lines will copy the driver files into the folder `deployment/driver`. Remove the comment 
  statements to let maven copy the files after a successful build, or copy them manually. Do not forget to rename the 
  files according to the pom entries. 

## The config file

comming soon

## Setting up a raspberry with `Wheezy Debian`

- configure wheezy with the setup at first start or type : sudo raspi-config
- Setting locale to de-de
- Setting Time-Zone to Berlin
- setting keyboard / keymap to german
- setting password but do not forget!
- enable ssh    
- reboot
- login and type `sudo apt-get update` and `sudo apt-get upgrade`

### Required Packages
The following packages are required for HomeOS (make use of "sudo apt-get install <package-name>"):


- mysql-server-5.5: MySQL Server -> you will be asked for password during setup -> use "m1st2r!"
- avahi-daemon: ZeroConf / Bonjour daemon, advertising the HomeOS service for Clients in the LAN. For debugging purposes the package avahi-util may also be installed.
- ntpdate: Hardware Clock will set to internet time automatically.
- openjdk-7-jdk if no java is already installed


### Installation & Configuration

Install MySQL
- install database `sudo apt-get install mysql-server-5.5
- when called for password type `passwort m1st2r!    (if you want to set another password -> remember to change it also when granting privileges)

Configure MySQL
- open mysql config file `sudo nano /etc/mysql/my.cnf`
- change line with `bind-address = 127.0.0.0` into `bind-address = 0.0.0.0

Create Database
    sudo mysql -p
    CREATE DATABASE home;
    GRANT ALL PRIVILEGES ON home.* TO root@'%' IDENTIFIED BY "m1st2r!";

Install Java
- check wether Java is installed `java -version`
- in case Java is not installed `sudo apt-get install openjdk-8-jdk`
``
Setup ntpdate
- install package `apt-get install ntpdate`
- stop ntp service `/etc/init.d/ntp stop`
- set ntp server `ntpdate ptbtime1.ptb.de`
- start ntp service `/etc/init.d/ntp start

Install smahoo HomeOS
- copy the file `domo` to `/etc/init.d/domo`
- change mode `sudo chmod 777 domo`
- set startup call `sudo update-rc.d domo defaults`
- copy all other files to /home/pi/homeos

Install zeroConf `sudo apt-get install avahi-daemonsudsudo`and copy files `copy setup/etc/avahi/* to /etc/avahi/*`
    
Change Hostname according to config file (systemid) `edit the name in /etc/hostname

### Ports
The following ports are used by HomeOS:

- 2020: HomeOS main server
- 2021: HomeOS Callback server



