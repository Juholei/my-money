CREATE TABLE users
(id SERIAL PRIMARY KEY,
 username VARCHAR(30) UNIQUE,
 created TIMESTAMP DEFAULT current_timestamp NOT NULL);
