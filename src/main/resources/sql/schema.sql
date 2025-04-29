/* This line will create the database if it doesn't exist and grant all privileges to the user */

CREATE DATABASE sirregloginBank;
GRANT ALL PRIVILEGES ON sirregloginBank.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;

/* This line will update the database schema automatically when the application starts

 spring.jpa.hibernate.ddl-auto=update */
