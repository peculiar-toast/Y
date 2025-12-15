#!/bin/bash

# Build the project using Maven
mvn clean package

docker compose down -v app

docker compose up --build app
