CREATE TABLE events
(id VARCHAR(30) PRIMARY KEY,
 transaction_date DATE,
 amount MONEY,
 recipient VARCHAR(50),
 type VARCHAR(30),
 created TIMESTAMP DEFAULT current_timestamp NOT NULL);
