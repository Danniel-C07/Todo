package edu.programacion.models;

public class Tag {

    private int idTag;
    private String name;
    private int idUser;

    public Tag() {
    }

    // Constructor para una etiqueta nueva
    public Tag(String name, int idUser) {
        this.name = name;
        this.idUser = idUser;
    }

    // Constructor para una etiqueta existente
    public Tag(int idTag, String name, int idUser) {
        this.idTag = idTag;
        this.name = name;
        this.idUser = idUser;
    }

    public int getIdTag() {
        return idTag;
    }

    public void setIdTag(int idTag) {
        this.idTag = idTag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    @Override
    public String toString() {
        return "Tag{" + "idTag=" + idTag + ", name='" + name + '\'' + ", idUser=" + idUser + '}';
    }
}
