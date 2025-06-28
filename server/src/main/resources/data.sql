INSERT INTO users (user_id,first_name, last_name, email) VALUES
                                                     ('JD123','John', 'Doe', 'john.doe@example.com'),
                                                     ('JS334','Jane', 'Smith', 'jane.smith@example.com'),
                                                     ('BJ345','Bob', 'Johnson', 'bob.johnson@example.com');



INSERT INTO transactions (
    type,
    amount,
    transaction_date,
    transaction_description,
    debit_account,
    credit_account,
    status,
    last_updated,
    currency,
    submitted_by,
    submitted_at,
    approved_by,
    approved_at
) VALUES (
    'PAYMENT',
    1000.00,
    '2024-06-01 00:00:00',
    'Transaction Description Example',
    '1001',
    '2001',
    'COMPLETED',
    '2024-06-01 10:00:00',
    'CNY',
    'user01',
    '2024-06-01 09:00:00',
    'admin01',
    '2024-06-01 11:00:00'
    );