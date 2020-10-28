package bs.administracion.web;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import bs.administracion.modelo.Parametro;
import bs.administracion.rn.ParametrosRN;
import bs.global.util.JsfUtil;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.MailFactory;
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
public class ParametroBean extends GenericBean implements Serializable {

    private Parametro parametro;

    @ManagedProperty(value = "#{aplicacionBean}")
    private AplicacionBean aplicacionBean;

    @EJB
    private MailFactory mailFactory;

    /**
     * Creates a new instance of ParametrosBean
     */
    public ParametroBean() {

    }

    @PostConstruct
    private void init() {
        try {
            parametro = parametrosRN.getParametro(aplicacionBean.getOrganizacion().getCodigo());
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String verParametros() {
        return "../administracion/tablas/parametros.xhtml";
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

    public void envioMailPruebaHTML() {
        try {
            mailFactory.envioPruebaHTML();
            JsfUtil.addInfoMessage("Se ha enviado un correo electrónico a la casilla confugarada");
        } catch (Exception e) {
            JsfUtil.addErrorMessage("No es posible enviar el correo de pruebas: " + e);
        }
    }

    public void eliminarItemBanner() {

    }

    public Parametro getParametro() {
        return parametro;
    }

    public void setParametro(Parametro parametro) {
        this.parametro = parametro;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

}
