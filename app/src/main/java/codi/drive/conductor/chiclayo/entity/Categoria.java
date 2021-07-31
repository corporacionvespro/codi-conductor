package codi.drive.conductor.chiclayo.entity;

/**
 * By: el-bryant
 */

public class Categoria {
    private String idCategoria;
    private String nombre;
    private String imagen;

    public Categoria(String idCategoria, String nombre, String imagen) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.imagen = imagen;
    }

    public Categoria() {
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
