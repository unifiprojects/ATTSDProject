[![Build Status](https://travis-ci.org/unifiprojects/ATTSDProject.svg?branch=master)](https://travis-ci.org/unifiprojects/ATTSDProject)
[![Coverage Status](https://coveralls.io/repos/github/unifiprojects/ATTSDProject/badge.svg?branch=master)](https://coveralls.io/github/unifiprojects/ATTSDProject?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.maurosalani.project.attsd%3AATTSDProject&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.maurosalani.project.attsd%3AATTSDProject)

# Social Network for Videogames

Taking inspiration from other "specific-topic" social networks, our web site is dedicated for people who love videogames.

The web application uses MySQL v.8.0.16, you can create a container with docker (make sure to launch it before the application):

```
docker run -d -p 3306:3306 --name mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=attsd_database -e MYSQL_USER=springuser -e MYSQL_PASSWORD=springuser mysql:8.0.16
```

## Built With

* [Spring Boot 2.1.6](https://spring.io/projects/spring-boot) - The web framework used
* [Maven 3.6.1](https://maven.apache.org/) - Dependency Management
* [Docker](https://www.docker.com/) - Software containers
* [Travis CI](https://travis-ci.org/unifiprojects/ATTSDProject) - Continuous Integration
* [Sonar Cloud](https://sonarcloud.io/dashboard?id=com.maurosalani.project.attsd%3AATTSDProject) - Code Quality
* [GitKraken](https://www.gitkraken.com/) - Git Client
* [MySQL 8.0.16](https://www.mysql.com/it/) - Database
