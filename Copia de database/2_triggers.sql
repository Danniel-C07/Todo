USE todolist_db;

DELIMITER //

CREATE TRIGGER after_task_status_update
AFTER UPDATE ON tasks
FOR EACH ROW
BEGIN
    -- Validar si el estado cambió y si el nuevo estado es COMPLETADA
    IF OLD.status != NEW.status AND NEW.status = 'COMPLETADA' THEN
        INSERT INTO task_audit (id_task, old_status, new_status, changed_at)
        VALUES (NEW.id_task, OLD.status, NEW.status, NOW());
    END IF;
END //

DELIMITER ;