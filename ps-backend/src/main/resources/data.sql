INSERT INTO roles (role_id, name)
VALUES (1, 'ADMIN'),
       (2, 'KITCHEN'),
       (3, 'COSTUMER')
ON CONFLICT (role_id) DO NOTHING;
