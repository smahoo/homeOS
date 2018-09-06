# HomeOs Readme

## Releases
New HomeOS releases are deployed to the [AC Setup Box Folder](https://app.box.com/s/ksb03locs6s5lqwkbmqo) for easy updating of AirControl Boxes.

Ensure that the HomeOS installation on the [AC Setup Box Folder](https://app.box.com/s/ksb03locs6s5lqwkbmqo) contains required services (e.g. DataUploaderService) and drivers, that it is available as an unpacked directory with the name of __homeos__ and that it can be used with the ACUpdater script in the same box folder.

## AirControl Box Setup for HomeOs
locale setzen de-de

    apt-get update
    apt-get dist-upgrade

1. Datum setzen und Datumservice installieren

    apt-get install ntpdate

        /etc/init.d/ntp stop
        ntpdate ptbtime1.ptb.de
        /etc/init.d/ntp start

2. Java installieren

        sudo apt-get install openjdk-7-jdk

3. Serielle Erweiterung für Java installieren

        sudo apt-get install librxtx-java

4. MySQL installieren

        sudo apt-get install mysql-server-5.5
        passwort m1st2r!

        configure MySQL
             sudo nano /etc/mysql/my.cnf
         change line with "bind-address = 127.0.0.0" to "bind-address = 0.0.0.0"

5. Datenbank home anlegen
    CREATE DATABASE home;
    GRANT ALL PRIVILEGES ON home.* TO root@'%' IDENTIFIED BY "m1st2r!";


6. DomoSystem installieren

        domo script kopieren
        sudo chmod 777 domo
        sudo update-rc.d domo defaults

7. ZeroConf installieren

    sudo apt-get install avahi-daemon
    Dienstkonfiguration aus dem DomoBox Repository einspielen (siehe dortiges DomoBoxSetup.txt)