
# TOUR GUIDE
```Author: Geoffrey Aulombard - Java web-application developer student at OpenClassRooms```

## Description

description

## Getting started

- this app runs on port ``8080``, then address is "localhost:8080"


## How it works
### Technologies

This is a Spring Boot Application, working with (check the ``build.gradle`` file to see all dependencies and versions)

- Java 11
- Gradle 6.9.1
- Docker version 18.09.0, build 4d60db4

### Architecture

- this application use the MVC pattern.


### Run the app

use the following command to run the app:

cd /path/to/directory/project

git clone https://

cd dossier

 ```bash
 gradle clean bootJar
```

 ```bash
 docker-compose up -d --build 
```
 ```bash
 docker-compose down
```


### Run the tests

use the following command to run the app:

 ```bash
 gradle
```

## API
### Documentation

http://localhost:8080/v2/api-docs

http://localhost:8080/swagger-ui.html#/

http://localhost:8081/swagger-ui.html#/

http://localhost:8082/swagger-ui.html#/

http://localhost:8083/swagger-ui.html#/

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

