USE todolist_db;

-- =====================================================================
-- 1. VISTAS (VIEWS)
-- =====================================================================

-- Vista 1: Resumen completo de tareas con usuario, categoría, subtareas y etiquetas
CREATE OR REPLACE VIEW v_resumen_tareas AS
SELECT 
    t.id_task,
    t.title AS tarea_titulo,
    t.description AS tarea_descripcion,
    t.status AS estado,
    t.creation_date AS fecha_creacion,
    t.due_date AS fecha_limite,
    c.id_category,
    c.name AS categoria_nombre,
    u.id_user,
    u.username AS usuario_propietario,
    COUNT(DISTINCT s.id_subtask) AS total_subtareas,
    SUM(CASE WHEN s.is_completed = 1 THEN 1 ELSE 0 END) AS subtareas_completadas,
    COUNT(DISTINCT tt.id_tag) AS total_etiquetas
FROM tasks t
INNER JOIN categories c ON t.id_category = c.id_category
INNER JOIN users u ON c.id_user = u.id_user
LEFT JOIN subtasks s ON t.id_task = s.id_task
LEFT JOIN task_tags tt ON t.id_task = tt.id_task
GROUP BY t.id_task, c.id_category, u.id_user;

-- =====================================================================
-- 2. PROCEDIMIENTOS ALMACENADOS (STORED PROCEDURES)
-- =====================================================================

DELIMITER //

-- Procedimiento 1: Marcar tarea como completada validando propiedad
DROP PROCEDURE IF EXISTS sp_marcar_tarea_completada //
CREATE PROCEDURE sp_marcar_tarea_completada(
    IN p_id_task INT,
    IN p_id_user INT,
    OUT p_filas_afectadas INT
)
BEGIN
    UPDATE tasks t
    INNER JOIN categories c ON c.id_category = t.id_category
    SET t.status = 'COMPLETADA'
    WHERE t.id_task = p_id_task 
      AND c.id_user = p_id_user 
      AND t.status <> 'COMPLETADA';

    SET p_filas_afectadas = ROW_COUNT();
END //

-- Procedimiento 2: Resumen de estadísticas de tareas por usuario
DROP PROCEDURE IF EXISTS sp_estadisticas_usuario //
CREATE PROCEDURE sp_estadisticas_usuario(
    IN p_id_user INT
)
BEGIN
    SELECT 
        COUNT(*) AS total_tareas,
        SUM(CASE WHEN t.status = 'COMPLETADA' THEN 1 ELSE 0 END) AS completadas,
        SUM(CASE WHEN t.status = 'EN_PROGRESO' THEN 1 ELSE 0 END) AS en_progreso,
        SUM(CASE WHEN t.status = 'PENDIENTE' THEN 1 ELSE 0 END) AS pendientes,
        SUM(CASE WHEN t.status = 'NUEVA' THEN 1 ELSE 0 END) AS nuevas
    FROM tasks t
    INNER JOIN categories c ON c.id_category = t.id_category
    WHERE c.id_user = p_id_user;
END //

-- =====================================================================
-- 3. FUNCIONES ALMACENADAS (STORED FUNCTIONS)
-- =====================================================================

-- Función 1: Calcular días restantes hasta la fecha límite
DROP FUNCTION IF EXISTS fn_dias_restantes //
CREATE FUNCTION fn_dias_restantes(p_fecha_limite DATETIME) 
RETURNS INT
DETERMINISTIC
NO SQL
BEGIN
    IF p_fecha_limite IS NULL THEN
        RETURN NULL;
    END IF;
    RETURN DATEDIFF(p_fecha_limite, NOW());
END //

DELIMITER ;
