package edu.programacion.models;

public class Category {

    private int idCategory;
    private String name;
    private int idUser;
    private boolean isDefault;

    // Constructor vacío
    public Category() {
    }

    // Constructor para crear una categoría nueva
    public Category(String name, int idUser, boolean isDefault) {
        this.name = name;
        this.idUser = idUser;
        this.isDefault = isDefault;
    }

    // Constructor para una categoría que ya existe en la base de datos
    public Category(int idCategory, String name, int idUser, boolean isDefault) {
        this.idCategory = idCategory;
        this.name = name;
        this.idUser = idUser;
        this.isDefault = isDefault;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
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

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return "Category{" + "idCategory=" + idCategory + ", name='" + name + '\'' + ", idUser=" + idUser + ", isDefault=" + isDefault + '}';
    }
}
