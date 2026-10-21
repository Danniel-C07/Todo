package edu.programacion.models;
import edu.programacion.enums.Status;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Task {

    private int idTask;
    private String title;
    private String description;
    private Status status;
    private Timestamp creationDate;
    private LocalDateTime dueDate;
    private int idCategory;

    public Task() {
    }

    // Constructor para crear una tarea nueva
    public Task(String title, String description, LocalDateTime dueDate, int idCategory) {
        this.title = title;
        this.description = description;
        this.status = Status.NUEVA;
        this.dueDate = dueDate;
        this.idCategory = idCategory;
    }

    // Constructor para una tarea que ya existe
    public Task(int idTask, String title, String description, Status status, Timestamp creationDate, LocalDateTime dueDate, int idCategory) {
        this.idTask = idTask;
        this.title = title;
        this.description = description;
        this.status = status;
        this.creationDate = creationDate;
        this.dueDate = dueDate;
        this.idCategory = idCategory;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    @Override
    public String toString() {
        return "Task{" + "idTask=" + idTask + ", title='" + title + '\'' + ", description='" + description + '\'' + ", status=" + status + ", creationDate=" + creationDate + ", dueDate=" + dueDate + ", idCategory=" + idCategory + '}';
    }
}
