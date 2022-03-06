# Cloud Storage App
## Table of Contents
* [General Info](#general-information)
* [Features](#features)
* [How it works](#how-it-works)
* [Security](#security)
* [Database](#database)
* [Technologies used](#technologies-used)
* [Building the project](#building-the-project)
* [Try it out with FRONT](#try-it-out-with-front)
* [Tests](#tests)

## General Information
Cloud Storage App is a Spring Boot application that provides REST interface for cloud file storage with the functionalities such as uploading, saving and downloading files. 
This project is a backend service which can be connected to the frontend web application and interact with it according to [specification](https://github.com/netology-code/jd-homeworks/blob/9ee0a1532b55f42b61bad641ccb167ad1f0ff386/diploma/CloudServiceSpecification.yaml).

## Features
* User authorization
* Listing the files
* Uploading a file
* Changing the file name
* Downloading a file
* Deleting a file

## How it works
The application uses Spring Boot Starters.
And the code is organized as this:
1.	**controller** is the web layer implemented by Spring Boot Starter Web, which handles a request from the moment when it is intercepted to the generation of the response and its transmission;
2.	**service** is the service layer, encapsulating the business logic of application and centralizing data access;
3.	**repository** is the DAO layer, which provides a CRUD interface for each entity;
4.	**model** is the model layer, including entities;
5.	**dto** is the (de)serialization layer, which carries data between processes and represents the model sent from or to the API client;
6.	**configuration** contains security configuration class;
7.	**security** contains JWT token filter class;
8.	**exception** contains exception classes.

## Security
The application has integration with Spring Security and add another filter for JWT token process.
All requests are authorized. Frontend application uses the header “auth-token”, which is sent to identify the user on the backend service. In order to get a token, it’s necessary to go through authorization on backend service and send a login and password pair to the /login method. In case of successful verification, backend service should return a json object with the auth-token field and the value of the token. All further requests from frontend application are sent with this header. To log out from the application, you need to call the /logout method. Token has an expiration period during which it is active.

## Database
The application uses MySQL database. Users’ and files’ data are stored in database.

## Technologies used
* Java – version 11
* Spring Boot Starters (web, data-jpa, validation, security, test, security-test) – version 2.6.2
* Hibernate Validator – version 6.1.0.Final
* MySQL Connector Java – version 8.0.27
* JJWT – version 0.11.1
* Log4j – version 1.2.17
* Testcontainers – version 1.16.3
* Junit – version 4.13.2
* Lombok – version 1.18.22

## Building the project
**You will need:**
* Java JDK 11 or higher
* Maven 3 or higher
* Git
* MySQL image
* Docker
* IntelliJ IDEA Ultimate

**Steps to run (for example with IDEA) from the command line:**
1.	Clone the project from GitHub
2.	Build a jar file using `mvn clean install`
3.	Run it using `mvn spring-boot:run`
4.	Connect it to the database using `docker run -e MYSQL_ROOT_PASSWORD=root MYSQL_DATABASE=cloudstorage -p 3306:3306 mysql`

**Steps to run with Docker Compose from the command line:**
1.	Clone the project from GitHub
2.	Build a jar file using `mvn clean install`
3.	Build the project using `docker-compose build`
4.	Run it using `docker-compose up`
5.	Stop it using `docker-compose down`

## Try it out with FRONT
* The entry point address of the backend API is reachable by URL at `http://localhost:8888`
* Default FRONT is reachable by URL at `http://localhost:8080`

**Steps to run FRONT:**
1.	Install NodeJS (version 14.15.0 or higher) on your computer following the [instructions](https://nodejs.org/en/download/)
2.	Download [FRONT (JavaScript)]( https://github.com/netology-code/jd-homeworks/tree/master/diploma/netology-diplom-frontend)
3.	Go to the FRONT folder of the application and run all the commands from it
4.	Change URL to call backend service: in the .env FRONT file (located at the root of the project) `VUE_APP_BASE_URL=http://localhost:8888`
5.	Rebuild FRONT again: `npm run build`
6.	Run FRONT following the README.md FRONT description

## Tests
The repository contains a lot of unit test cases (with using JUnit, Mockito and Testcontainers libraries) to cover service and DAO layers.





