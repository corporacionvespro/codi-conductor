package codi.drive.conductor.chiclayo.entity;

/**
 * By: el-bryant
 */

public class AcercaApp {
    private String idAcercaApp;
    private String nombre;
    private String contenido;
    private String PC;

    public AcercaApp(String idAcercaApp, String nombre, String contenido, String PC) {
        this.idAcercaApp = idAcercaApp;
        this.nombre = nombre;
        this.contenido = contenido;
        this.PC = PC;
    }

    public String getIdAcercaApp() {
        return idAcercaApp;
    }

    public void setIdAcercaApp(String idAcercaApp) {
        this.idAcercaApp = idAcercaApp;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getPC() {
        return PC;
    }

    public void setPC(String PC) {
        this.PC = PC;
    }
}
