USE todolist_db;

-- Insertar 2 Usuarios 
INSERT INTO users (username, email, password_hash) VALUES 
('daniel_admin', 'daniel@universidad.edu', SHA2('123456', 256)),
('usuario_prueba', 'prueba@universidad.edu', SHA2('123456', 256));

-- Insertar Categorías para el usuario 1
INSERT INTO categories (name, id_user, is_default) VALUES 
('General', 1, TRUE),
('Universidad', 1, FALSE),
('Personal', 1, FALSE);

-- Insertar Tareas
INSERT INTO tasks (title, description, status, due_date, id_category) VALUES 
('Terminar modelo ER', 'Revisar relaciones de base de datos', 'COMPLETADA', '2026-10-20 23:59:00', 2),
('Programar DAOs en Java', 'Hacer las sentencias preparadas para MySQL', 'EN_PROGRESO', '2026-10-25 18:00:00', 2),
('Comprar monitor', 'Buscar opciones de monitores externos', 'NUEVA', '2026-11-01 10:00:00', 3);

-- Insertar Subtareas para la tarea 2 
INSERT INTO subtasks (title, is_completed, id_task) VALUES 
('Crear UserDAO', TRUE, 2),
('Crear TaskDAO', FALSE, 2),
('Testear conexión', FALSE, 2);

-- Insertar Etiquetas
INSERT INTO tags (name, id_user) VALUES 
('Urgente', 1),
('Proyecto Final', 1);

-- Asignar etiquetas a las tareas
INSERT INTO task_tags (id_task, id_tag) VALUES 
(2, 1), -- La tarea de DAOs es Urgente
(2, 2); -- La tarea de DAOs es del Proyecto Final