DROP TABLE IF EXISTS todo_items;
DROP TABLE IF EXISTS user_info;

CREATE TABLE user_info (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(150) NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(250) DEFAULT NULL,
  roles VARCHAR(250)
);

CREATE TABLE todo_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  done INT NOT NULL DEFAULT 0,
  text_value VARCHAR(250),
  description VARCHAR(500),
  creation_date DATE,
  expiration_date DATE,
  FOREIGN KEY (user_id) REFERENCES user_info(id)
);

INSERT INTO user_info (username, password, email, roles) VALUES
    ('Admin', 'c1c224b03cd9bc7b6a86d77f5dace40191766c485cd55dc48caf9ac873335d6f', 'admin@billet.com', 'ROLE_ADMIN'),
    ('eee', '928b84e6e430c53f4b9292afd132b86c625198757c4299d81a441f6f633c0221', 'eee@eee.com', 'ROLE_USER'),
    ('aaa', '9834876dcfb05cb167a5c24953eba58c4ac89b1adf57f28f2f9d09af107ee8f0', 'aaa@aaa.com', 'ROLE_USER');
INSERT INTO todo_items (user_id, done, text_value, description, creation_date, expiration_date) VALUES 
  (1, 1, 'Sacar la basura', 'Fusce elementum augue vitae volutpat condimentum. Pellentesque turpis sem, pretium in quam ac, pulvinar facilisis est', '2022-12-31', '2026-12-31');
  -- (1, 0, 'hacer prueba de todo list', 'Sed convallis, libero quis tincidunt laoreet, metus ex maximus tortor, at pellentesque nisl magna eget urna.', '2022-12-31', '2026-12-31'),
  -- (1, 0, 'limpiar codigo', 'Cras aliquam pulvinar tristique. Nulla facilisi. Maecenas sagittis faucibus aliquet. Integer nec est eu orci cursus condimentum ut vitae lectus', '2022-12-31', '2026-12-31'),
  -- (1, 0, 'hacer pruebas unitarias', 'Lorem Ipsum description', '2022-12-31', '2025-12-31'),
  -- (2, 0, 'ver una pelcula', 'Lorem Ipsum description', '2022-12-31', '2025-12-31')

  -- (3, 0, 'limpiar codigo', 'Cras aliquam pulvinar tristique. Nulla facilisi. Maecenas sagittis faucibus aliquet. Integer nec est eu orci cursus condimentum ut vitae lectus', '2022-12-31', '2026-12-31'),
  -- (3, 0, 'hacer pruebas unitarias', 'Lorem Ipsum description', '2022-12-31', '2025-12-31'),
  -- (3, 0, 'ver una pelcula', 'Lorem Ipsum description', '2022-12-31', '2025-12-31')

