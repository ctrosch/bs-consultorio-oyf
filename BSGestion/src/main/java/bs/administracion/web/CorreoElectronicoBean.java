/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.web;

import bs.administracion.modelo.CorreoElectronico;
import bs.administracion.rn.CorreoElectronicoRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.global.web.MailFactory;
import bs.seguridad.web.UsuarioSessionBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;

/**
 *
 * @author ctrosch
 */
@ManagedBean
@SessionScoped
public class CorreoElectronicoBean extends GenericBean implements Serializable {

    @EJB
    private CorreoElectronicoRN correoElectronicoRN;
    @EJB
    MailFactory mailFactoryBean;

    @Inject
    private UsuarioSessionBean usuarioSessionBean;

    private List<CorreoElectronico> lista;
    private CorreoElectronico correo;

    private String enviado;

    /**
     * Creates a new instance of BannerBean
     */
    public CorreoElectronicoBean() {

    }

    @PostConstruct
    private void init() {

        enviado = "N";
        lista = correoElectronicoRN.getCorreosNoEnviados();
    }

    public void enviarCorreo(CorreoElectronico ce) {

        List<CorreoElectronico> correos = new ArrayList<CorreoElectronico>();
        correos.add(ce);

        try {
            //Enviamos los correos con un nuevo hilo...
            mailFactoryBean.enviarCorreosElectronicos(correos);
            JsfUtil.addInfoMessage("Enviando mail....");
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("Error: enviarCorreo" + ex);
            Logger.getLogger(CorreoElectronicoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void enviarTodo() {

        try {
            //Enviamos los correos con un nuevo hilo...
            mailFactoryBean.enviarCorreosElectronicos(lista);
            JsfUtil.addInfoMessage("Enviando mails...");
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("Error: enviarCorreo" + ex);
            Logger.getLogger(CorreoElectronicoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscar() {
        lista = correoElectronicoRN.getListaByBusqueda(txtBusqueda, enviado, cantidadRegistros);
    }

    public void actualizarLista() {
        lista = correoElectronicoRN.getCorreosNoEnviados();
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public List<CorreoElectronico> getLista() {
        return lista;
    }

    public void setLista(List<CorreoElectronico> lista) {
        this.lista = lista;
    }

    public CorreoElectronico getCorreo() {
        return correo;
    }

    public void setCorreo(CorreoElectronico correo) {
        this.correo = correo;
    }

    public String getEnviado() {
        return enviado;
    }

    public void setEnviado(String enviado) {
        this.enviado = enviado;
    }

}
