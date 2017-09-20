-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(username, password)
VALUES (:username, :password)

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
(transaction_id, user_id, transaction_date, amount, recipient, type)
VALUES (:transaction-id, :user-id, :transaction-date, :amount, :recipient, :type)
ON CONFLICT DO NOTHING

-- :name get-events :? :*
-- :doc Get entries with the given user id
SELECT * FROM events
WHERE user_id = :user-id
ORDER BY transaction_date ASC

-- :name get-recurring-expenses :? :*
SELECT A.*
FROM events A
INNER JOIN (SELECT recipient, amount
            FROM events
            WHERE user_id = :user-id
            GROUP BY recipient, amount
            HAVING COUNT(*) > 1 AND amount < 0) B
ON A.recipient = B.recipient AND A.amount = B.amount
WHERE user_id = :user-id
ORDER By transaction_date DESC

-- :name update-starting-amount! :! :n
UPDATE users
SET starting_amount = :starting-amount
where id = :user-id
