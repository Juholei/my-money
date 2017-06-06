ALTER TABLE events RENAME id TO transaction_id;
ALTER TABLE events ADD id BIGSERIAL;
ALTER TABLE events DROP CONSTRAINT events_pkey;
ALTER TABLE events ADD PRIMARY KEY(transaction_id, user_id, transaction_date);
