package codi.drive.conductor.chiclayo.entity;

/**
 * By: el-bryant
 */

public class SolicitudTaxi {
    private String idSolicitudTaxi;
    private String estado;
    private Double tarifa;
    private Double pagoFinal;
    private String apellidosPasajero;
    private String nombresPasajero;
    private String telefonoPasajero;
    private String fotoPasajero;
    private Double latitudRecojo;
    private Double longitudRecojo;
    private Double latitudDestino;
    private Double longitudDestino;
    private String direccionOrigen;
    private String direccionDestino;
    private String codigo;
    private String referencia;
    private String fechaHora;

    public SolicitudTaxi(String idSolicitudTaxi, String estado, Double tarifa, Double pagoFinal, String apellidosPasajero, String nombresPasajero, String telefonoPasajero, String fotoPasajero, Double latitudRecojo, Double longitudRecojo, Double latitudDestino, Double longitudDestino, String direccionOrigen, String direccionDestino, String codigo, String referencia, String fechaHora) {
        this.idSolicitudTaxi = idSolicitudTaxi;
        this.estado = estado;
        this.tarifa = tarifa;
        this.pagoFinal = pagoFinal;
        this.apellidosPasajero = apellidosPasajero;
        this.nombresPasajero = nombresPasajero;
        this.telefonoPasajero = telefonoPasajero;
        this.fotoPasajero = fotoPasajero;
        this.latitudRecojo = latitudRecojo;
        this.longitudRecojo = longitudRecojo;
        this.latitudDestino = latitudDestino;
        this.longitudDestino = longitudDestino;
        this.direccionOrigen = direccionOrigen;
        this.direccionDestino = direccionDestino;
        this.codigo = codigo;
        this.referencia = referencia;
        this.fechaHora = fechaHora;
    }

    public String getIdSolicitudTaxi() {
        return idSolicitudTaxi;
    }

    public void setIdSolicitudTaxi(String idSolicitudTaxi) {
        this.idSolicitudTaxi = idSolicitudTaxi;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getTarifa() {
        return tarifa;
    }

    public void setTarifa(Double tarifa) {
        this.tarifa = tarifa;
    }

    public Double getPagoFinal() {
        return pagoFinal;
    }

    public void setPagoFinal(Double pagoFinal) {
        this.pagoFinal = pagoFinal;
    }

    public String getApellidosPasajero() {
        return apellidosPasajero;
    }

    public void setApellidosPasajero(String apellidosPasajero) {
        this.apellidosPasajero = apellidosPasajero;
    }

    public String getNombresPasajero() {
        return nombresPasajero;
    }

    public void setNombresPasajero(String nombresPasajero) {
        this.nombresPasajero = nombresPasajero;
    }

    public String getTelefonoPasajero() {
        return telefonoPasajero;
    }

    public void setTelefonoPasajero(String telefonoPasajero) {
        this.telefonoPasajero = telefonoPasajero;
    }

    public String getFotoPasajero() {
        return fotoPasajero;
    }

    public void setFotoPasajero(String fotoPasajero) {
        this.fotoPasajero = fotoPasajero;
    }

    public Double getLatitudRecojo() {
        return latitudRecojo;
    }

    public void setLatitudRecojo(Double latitudRecojo) {
        this.latitudRecojo = latitudRecojo;
    }

    public Double getLongitudRecojo() {
        return longitudRecojo;
    }

    public void setLongitudRecojo(Double longitudRecojo) {
        this.longitudRecojo = longitudRecojo;
    }

    public Double getLatitudDestino() {
        return latitudDestino;
    }

    public void setLatitudDestino(Double latitudDestino) {
        this.latitudDestino = latitudDestino;
    }

    public Double getLongitudDestino() {
        return longitudDestino;
    }

    public void setLongitudDestino(Double longitudDestino) {
        this.longitudDestino = longitudDestino;
    }

    public String getDireccionOrigen() {
        return direccionOrigen;
    }

    public void setDireccionOrigen(String direccionOrigen) {
        this.direccionOrigen = direccionOrigen;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public void setDireccionDestino(String direccionDestino) {
        this.direccionDestino = direccionDestino;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }
}
