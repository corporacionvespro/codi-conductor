package codi.drive.conductor.chiclayo.entity;

/**
 * By: el-bryant
 */

public class PromocionesLista {
    String nombreEmpresa;
    String logo;

    public PromocionesLista(String nombreEmpresa, String logo) {
        this.nombreEmpresa = nombreEmpresa;
        this.logo = logo;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
