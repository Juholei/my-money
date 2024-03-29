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

-- :name save-savings! :! :n
-- :doc Save array of savings recipients
INSERT INTO savings
(user_id, recipients)
VALUES (:user-id, :recipients)
ON CONFLICT (user_id) DO UPDATE
SET recipients = :recipients

-- :name get-savings :? :1
-- :doc retrieve array of savings recipients for the the user-id
SELECT recipients FROM savings
WHERE user_id = :user-id

--:name get-users-newest-event-date :? :1
SELECT created FROM events
WHERE user_id = (SELECT id FROM users WHERE username = :username)
ORDER BY created DESC LIMIT 1;