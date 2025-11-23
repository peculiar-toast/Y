CREATE TABLE Users (
       id SERIAL PRIMARY KEY,
       username VARCHAR(255),
       password VARCHAR(255),
       role VARCHAR(255)
);

CREATE TABLE Posts (
       id SERIAL PRIMARY KEY,
       title VARCHAR(255),
       content TEXT
);