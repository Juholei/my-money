CREATE TABLE events
(id VARCHAR(30) PRIMARY KEY,
 user_id INTEGER REFERENCES users (id),
 transaction_date VARCHAR(10),
 amount NUMERIC,
 recipient VARCHAR(50),
 type VARCHAR(30),
 created TIMESTAMP DEFAULT current_timestamp NOT NULL);
