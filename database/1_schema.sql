CREATE DATABASE IF NOT EXISTS todolist_db;
USE todolist_db;

-- 1. Tabla de Usuarios
CREATE TABLE IF NOT EXISTS users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabla de Categorías
CREATE TABLE IF NOT EXISTS categories (
    id_category INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    id_user INT NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_user) REFERENCES users(id_user) ON DELETE CASCADE
);

-- 3. Tabla de Tareas
CREATE TABLE IF NOT EXISTS tasks (
    id_task INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status ENUM('NUEVA', 'EN_PROGRESO', 'PENDIENTE', 'COMPLETADA') DEFAULT 'NUEVA',
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date DATETIME,
    id_category INT NOT NULL,
    FOREIGN KEY (id_category) REFERENCES categories(id_category) ON DELETE CASCADE
);

-- 4. Tabla de Subtareas (Pasos de una tarea)
CREATE TABLE IF NOT EXISTS subtasks (
    id_subtask INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    id_task INT NOT NULL,
    FOREIGN KEY (id_task) REFERENCES tasks(id_task) ON DELETE CASCADE
);

-- 5. Tabla de Etiquetas
CREATE TABLE IF NOT EXISTS tags (
    id_tag INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    id_user INT NOT NULL,
    FOREIGN KEY (id_user) REFERENCES users(id_user) ON DELETE CASCADE
);

-- 6. Tabla Intermedia Tareas_Etiquetas (Relación N:M)
CREATE TABLE IF NOT EXISTS task_tags (
    id_task INT NOT NULL,
    id_tag INT NOT NULL,
    PRIMARY KEY (id_task, id_tag),
    FOREIGN KEY (id_task) REFERENCES tasks(id_task) ON DELETE CASCADE,
    FOREIGN KEY (id_tag) REFERENCES tags(id_tag) ON DELETE CASCADE
);

-- 7. Tabla de Auditoría (Para el Trigger)
CREATE TABLE IF NOT EXISTS task_audit (
    id_audit INT AUTO_INCREMENT PRIMARY KEY,
    id_task INT NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);