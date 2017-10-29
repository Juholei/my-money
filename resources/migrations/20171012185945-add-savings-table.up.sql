CREATE TABLE savings
(user_id INTEGER REFERENCES users (id) PRIMARY KEY,
 recipients text[],
 created TIMESTAMP DEFAULT current_timestamp NOT NULL);
