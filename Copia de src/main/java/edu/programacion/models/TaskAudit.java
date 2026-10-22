package edu.programacion.models;

import java.sql.Timestamp;

public class TaskAudit {
    private int idAudit;
    private int idTask;
    private String taskTitle;
    private String oldStatus;
    private String newStatus;
    private Timestamp changedAt;

    public TaskAudit() {
    }

    public TaskAudit(int idAudit, int idTask, String taskTitle, String oldStatus, String newStatus, Timestamp changedAt) {
        this.idAudit = idAudit;
        this.idTask = idTask;
        this.taskTitle = taskTitle;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
    }

    public int getIdAudit() {
        return idAudit;
    }

    public void setIdAudit(int idAudit) {
        this.idAudit = idAudit;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public Timestamp getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Timestamp changedAt) {
        this.changedAt = changedAt;
    }

    @Override
    public String toString() {
        return "Auditoría #" + idAudit + " | Tarea #" + idTask + " (\"" + taskTitle + "\") | "
                + oldStatus + " -> " + newStatus + " | Fecha: " + changedAt;
    }
}
