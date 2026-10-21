package edu.programacion.models;

public class Subtask {

    private int idSubtask;
    private String title;
    private boolean completed;
    private int idTask;

    public Subtask() {
    }

    // Constructor para una subtarea nueva
    public Subtask(String title, int idTask) {
        this.title = title;
        this.idTask = idTask;
        this.completed = false;
    }

    // Constructor para una subtarea existente
    public Subtask(int idSubtask, String title,
                   boolean completed, int idTask) {
        this.idSubtask = idSubtask;
        this.title = title;
        this.completed = completed;
        this.idTask = idTask;
    }

    public int getIdSubtask() {
        return idSubtask;
    }

    public void setIdSubtask(int idSubtask) {
        this.idSubtask = idSubtask;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    @Override
    public String toString() {
        return "Subtask{" + "idSubtask=" + idSubtask + ", title='" + title + '\'' + ", completed=" + completed + ", idTask=" + idTask + '}';
    }
}
