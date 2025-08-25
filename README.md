Инструкция по запуску:
1. git clone https://github.com/Evgesha-thunder/user-service.git
2. Пройти в рабочую дерикторию проекта
3. docker-compose up -d --build

OPERATIONS:
- Create new user - POST http://localhost:8080/users
- Get all users - GET http://localhost:8080/users
- Get user by id - GET http://localhost:8080/users/{id}
- Update user by id - PUT http://localhost:8080/users/{id}
- Delete user by id - DELETE http://localhost:8080/users/{id}
