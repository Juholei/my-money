ALTER TABLE events ALTER COLUMN transaction_date TYPE date USING to_date(transaction_date, 'DD.MM.YYYY');
