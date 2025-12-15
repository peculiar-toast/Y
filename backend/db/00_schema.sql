CREATE TABLE Users (
       id SERIAL PRIMARY KEY,
       username VARCHAR(255),
       password VARCHAR(255),
       role VARCHAR(255)
);

CREATE TABLE Posts (
       id SERIAL PRIMARY KEY,
       title VARCHAR(255) NOT NULL,
       user_id INTEGER REFERENCES Users(id) ON DELETE CASCADE
);

CREATE TABLE AudioData (
       id SERIAL PRIMARY KEY,
       filename VARCHAR(255),
       storage_path VARCHAR(255),
       mime_type VARCHAR(100),
       post_id INTEGER REFERENCES Posts(id) ON DELETE CASCADE
);