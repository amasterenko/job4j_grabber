### Vacancies Parser  
[![Build Status](https://travis-ci.org/amasterenko/job4j_grabber.svg?branch=master)](https://travis-ci.org/amasterenko/job4j_grabber)

---
Parser for Sql.ru site (https://www.sql.ru/forum/job-offers/).  
It searches topics with Java Developer vacancies, parses them and saves to the database.  

#### Technologies  

* Java Core
* Jsoup
* Maven
* Quartz-scheduler
* JDBC, PostgreSQL
* Travis CI

#### Features    

* Job schedule  
* Parsing and filtering the web-pages  
* Simple Socket Server to provide the parsed vacancies via HTTP  

#### Usage  

1. Create a DB and run _create.sql_.  
2. Specify your database settings and the schedule interval in _app.properties_.  
3. Build the project: ```mvn clean package```.  
4. Run the app: ```java -jar target/grabber.jar```.  
5. Get results from the DB or from the Socket Server (http://localhost:21555 by default).  



