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
ON CONFLICT DO NOTHING

-- :name get-events :? :*
-- :doc Get entries with the given user id
SELECT * FROM events
WHERE user_id = :user-id

-- :name get-recurring-events :? :*
SELECT A.*
FROM events A
INNER JOIN (SELECT recipient, amount
            FROM events
            GROUP BY recipient, amount
            HAVING COUNT(*) > 1) B
ON A.recipient = B.recipient AND A.amount = B.amount
ORDER By recipient
