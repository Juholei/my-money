-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(username)
VALUES (:username)

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- :name get-user-by-username :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE username = :username

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE id = :id

-- :name create-event! :! :n
-- :doc creates a new user record
INSERT INTO events
(id, user_id, transaction_date, amount, recipient, type)
VALUES (:id, :user-id, :transaction-date, :amount, :recipient, :type)

-- :name get-events :? :*
-- :doc Get entries with the given user id
SELECT * FROM events
WHERE user_id = :user-id
