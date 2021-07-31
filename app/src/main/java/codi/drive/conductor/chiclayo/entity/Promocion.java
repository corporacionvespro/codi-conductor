package codi.drive.conductor.chiclayo.entity;

/**
 * By: el-bryant
 */

public class Promocion {
    String empresa;
    String descripcion;
    String logo;

    public Promocion(String empresa, String descripcion, String logo) {
        this.empresa = empresa;
        this.descripcion = descripcion;
        this.logo = logo;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
