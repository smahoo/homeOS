# Install on Raspian

## Install Java

        sudo apt-get install oracle-java7-jdk


## Install MySQL Server

        sudo apt-get install mysql-server-5.5

Set and remember root passwort during installation. Configure external access to database by 
 
        sudo nano /etc/mysql/my.cnf
 
Change line  ```"bind-address = 127.0.0.1"``` to ```"bind-address = 0.0.0.0"```

Afterwards start mysql, create database ```home``` and grant privileges.

    mysql u-root -p
    CREATE DATABASE home;
    GRANT ALL PRIVILEGES ON home.* TO root@'%' IDENTIFIED BY <your_password>
