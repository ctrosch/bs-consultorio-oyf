package bs.salud.web;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import bs.global.util.JsfUtil;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.salud.modelo.ParametroSalud;
import bs.salud.rn.ParametroSaludRN;
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
 * @author Guillermo
 */
@ManagedBean
@ViewScoped
public class ParametroSaludBean extends GenericBean implements Serializable {

    @EJB
    private ParametroSaludRN parametrosRN;

    private ParametroSalud parametro;

    @ManagedProperty(value = "#{aplicacionBean}")
    private AplicacionBean aplicacionBean;

    /**
     * Creates a new instance of ParametrosBean
     */
    public ParametroSaludBean() {

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

    public ParametroSalud getParametro() {
        return parametro;
    }

    public void setParametro(ParametroSalud parametro) {
        this.parametro = parametro;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

}
