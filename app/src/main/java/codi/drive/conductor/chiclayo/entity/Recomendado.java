package codi.drive.conductor.chiclayo.entity;

/**
 * By: el-bryant
 */

public class Recomendado {
    private String idRecomendado;
    private String nombre;
    private Double latitud;
    private Double longitud;
    private String foto;
    private String direccion;

    public Recomendado() {
    }

    public Recomendado(String idRecomendado, String nombre, Double latitud, Double longitud, String foto, String direccion) {
        this.idRecomendado = idRecomendado;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.foto = foto;
        this.direccion = direccion;
    }

    public String getIdRecomendado() {
        return idRecomendado;
    }

    public void setIdRecomendado(String idRecomendado) {
        this.idRecomendado = idRecomendado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
