# chat-app
## Start the services(MySQL and Spring Boot application) with Docker Compose
```bash
docker-compose up
```
## Log in or sign up
To get started with the application you need to log in:

Сreate a new user:
```bash
GET localhost:8080/registration
```
Log in with existing credentials:
```bash
GET localhost:8080/login
```
