# Дипломный проект “Облачное хранилище”

## Описание проекта

* Spring Boot Application "Cloud Storage" - REST-сервис - предоставляет REST интерфейс для возможности загрузки файлов и вывода списка уже загруженных файлов пользователя по заранее описанной спецификации. 
* Заранее подготовленное веб-приложение (FRONT) подключается к разработанному сервису (BACK) и использует его функционал  для авторизации, загрузки и вывода списка файлов пользователя.
* Информация о пользователях сервиса и сами файлы хранятся в базе данных MySQl.
* Все запросы к сервису авторизованы. FRONT приложение использует header `auth-token`, в котором отправляется токен (ключ-строка) для идентификации пользователя на BACKEND. Для получения токена нужно пройти авторизацию на BACKEND и отправить на метод /login пару логин и пароль, в случае успешной проверки в ответ BACKEND должен вернуть json объект
с полем `auth-token` и значением токена. Все дальнейшие запросы с FRONTEND, кроме метода /login отправляются с этим header. Для выхода из приложения нужно вызвать метод BACKEND /logout, который удалит/деактивирует токен и последующие запросы с этим токеном будут не авторизованы и возвращать код 401.


Спецификация:
https://github.com/netology-code/jd-homeworks/blob/master/diploma/CloudServiceSpecification.yaml

Front доступен по адресу:
https://github.com/netology-code/jd-homeworks/tree/master/diploma/netology-diplom-frontend

## Возможности приложения:
  1. Вывод списка файлов
  2. Добавление файла
  3. Изменение имени файла
  4. Удаление файла
  5. Авторизация

## Сделано:
* разработана архитектура
* использован сборщик пакетов Maven
* реализован функционал в соответствии со спецификацией
* подключен Spring Security и реализован функционал авторизации путем генерации токена с помощью библиотеки JSON Web Token
* протестировано с помощью Postman

## В планах:
* покрытие кода  unit тестами с использованием Mockito
* написание dockerfile, docker-compose файлов
* добавление интеграционных тестов с использованием TestContainers
