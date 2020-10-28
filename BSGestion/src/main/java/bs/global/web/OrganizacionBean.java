/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.global.modelo.Organizacion;
import bs.global.rn.OrganizacionRN;
import bs.global.util.JsfUtil;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class OrganizacionBean extends GenericBean implements Serializable {

    @EJB
    private OrganizacionRN organizacionRN;

    private Organizacion organizacion;

    @ManagedProperty(value = "#{aplicacionBean}")
    private AplicacionBean aplicacionBean;

    /**
     * Creates a new instance of ParametrosBean
     */
    public OrganizacionBean() {

    }

    @PostConstruct
    private void init() {
        try {
            organizacion = organizacionRN.getOrganizacion("01");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guardar() {
        try {

            organizacionRN.guardar(organizacion);
            aplicacionBean.actualizarDatos();
            JsfUtil.addInfoMessage("Los datos se guardaron correctamente");
        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al guardar datos", ex);
        }
    }

    public Organizacion getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(Organizacion organizacion) {
        this.organizacion = organizacion;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

}
