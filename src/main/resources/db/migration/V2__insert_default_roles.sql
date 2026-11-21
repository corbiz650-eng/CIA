
INSERT INTO roles (name, description, active, created_at, updated_at)
VALUES
    ('ADMIN', 'System administrator', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CITIZEN', 'Citizen user', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('OFFICER', 'Municipal officer', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
