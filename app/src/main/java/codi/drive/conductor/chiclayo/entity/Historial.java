package codi.drive.conductor.chiclayo.entity;

/**
 * By: el-bryant
 */

public class Historial {
    private String idSolicitud;
    private String foto;
    private String nombres;
    private String apellidos;
    private float valoracion;
    private String telefono;
    private String direccion_origen;
    private String direccion_destino;
    private int numdia;
    private int nummes;
    private int numfecha;
    private String hora;
    private double pagofinal;

    public Historial(String idSolicitud, String foto, String nombres, String apellidos, float valoracion, String telefono, String direccion_origen, String direccion_destino, int numdia, int nummes, int numfecha, String hora, double pagofinal) {
        this.idSolicitud = idSolicitud;
        this.foto = foto;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.valoracion = valoracion;
        this.telefono = telefono;
        this.direccion_origen = direccion_origen;
        this.direccion_destino = direccion_destino;
        this.numdia = numdia;
        this.nummes = nummes;
        this.numfecha = numfecha;
        this.hora = hora;
        this.pagofinal = pagofinal;
    }

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(String idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public float getValoracion() {
        return valoracion;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion_origen() {
        return direccion_origen;
    }

    public void setDireccion_origen(String direccion_origen) {
        this.direccion_origen = direccion_origen;
    }

    public String getDireccion_destino() {
        return direccion_destino;
    }

    public void setDireccion_destino(String direccion_destino) {
        this.direccion_destino = direccion_destino;
    }

    public int getNumdia() {
        return numdia;
    }

    public void setNumdia(int numdia) {
        this.numdia = numdia;
    }

    public int getNummes() {
        return nummes;
    }

    public void setNummes(int nummes) {
        this.nummes = nummes;
    }

    public int getNumfecha() {
        return numfecha;
    }

    public void setNumfecha(int numfecha) {
        this.numfecha = numfecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public double getPagofinal() {
        return pagofinal;
    }

    public void setPagofinal(double pagofinal) {
        this.pagofinal = pagofinal;
    }
}
