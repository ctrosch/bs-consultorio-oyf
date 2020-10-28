package bs.educacion.web;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import bs.educacion.modelo.ParametroEducacion;
import bs.educacion.rn.ParametroEducacionRN;
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
 *
 * @author ctrosch
 */
@ManagedBean
@ViewScoped
public class ParametroEducacionBean extends GenericBean implements Serializable {

    @EJB
    private ParametroEducacionRN parametrosRN;

    private ParametroEducacion parametro;

    @ManagedProperty(value = "#{aplicacionBean}")
    private AplicacionBean aplicacionBean;

    /**
     * Creates a new instance of ParametrosBean
     */
    public ParametroEducacionBean() {

    }

    @PostConstruct
    private void init() {
        try {
            parametro = parametrosRN.getParametro();
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guardar() {
        try {
            parametrosRN.guardar(parametro);
            aplicacionBean.actualizarDatos();
            JsfUtil.addInfoMessage("Lo datos se guardaron correctamente");
        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al guardar datos", ex);
        }
    }

    public ParametroEducacion getParametro() {
        return parametro;
    }

    public void setParametro(ParametroEducacion parametro) {
        this.parametro = parametro;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

}
