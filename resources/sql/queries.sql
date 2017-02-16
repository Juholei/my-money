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
