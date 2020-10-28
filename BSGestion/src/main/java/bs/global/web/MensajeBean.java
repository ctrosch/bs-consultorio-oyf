/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.estadistica.web.DashboardBean;
import bs.global.modelo.Mensaje;
import bs.global.rn.MensajeRN;
import bs.global.util.JsfUtil;
import bs.seguridad.web.UsuarioSessionBean;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Agustin
 */
@ManagedBean
@ViewScoped
public class MensajeBean extends GenericBean implements Serializable {

    private Mensaje mensaje;
    private List<Mensaje> mensajesSinEnviar;
    private List<Mensaje> mensajesEnviados;
    private List<Mensaje> mensajesRecibidos;

    @EJB
    private MensajeRN mensajeRN;

    @ManagedProperty(value = "#{usuarioSessionBean}")
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{dashboardBean}")
    protected DashboardBean dashboardBean;

    public MensajeBean() {

    }

    @PostConstruct
    public void init() {
        mostrarDebaja = false;
        nuevo();
        txtBusqueda = "";
        buscarEnviados();
        buscarRecibidos();
    }

    public void nuevo() {

        esNuevo = true;
        buscaMovimiento = false;
        mensaje = new Mensaje();
        mensaje.setRemitente(usuarioSessionBean.getUsuario());
    }

    public void enviarMensaje() {

        mensaje.setEstado("B");
        guardar(true);
        buscarEnviados();
    }

    public void guardar(boolean nuevo) {

        try {
            if (mensaje != null) {
                mensajeRN.guardar(mensaje, esNuevo);
                esNuevo = false;
                JsfUtil.addInfoMessage("Mensaje enviado");

//                dashboardBean.actualizarMensajes();
                if (nuevo) {
                    nuevo();
                }
            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(MensajeBean.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible enviar el mensaje en estos momentos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            mensaje.getAuditoria().setDebaja(habDes);
            mensajeRN.guardar(mensaje, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(MensajeBean.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            mensajeRN.eliminar(mensaje);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(MensajeBean.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscarEnviados() {

        filtro.clear();
        filtro.put("estado = ", "'B'");
        filtro.put("remitente.id = ", "" + usuarioSessionBean.getUsuario().getId());
        mensajesEnviados = mensajeRN.getListaByBusqueda(txtBusqueda, filtro, mostrarDebaja, cantidadRegistros);
    }

    public void buscarRecibidos() {

        filtro.clear();
        filtro.put("estado IN ", "('B','C')");
        filtro.put("destinatario.id = ", "" + usuarioSessionBean.getUsuario().getId() + "");

        mensajesRecibidos = mensajeRN.getListaByBusqueda(txtBusqueda, filtro, mostrarDebaja, cantidadRegistros);
    }

    public void onSelect(SelectEvent event) {
        mensaje = (Mensaje) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;

        if (mensaje.getLeido().equals("N")) {
            mensaje.setLeido("S");
            try {
                mensajeRN.guardar(mensaje, esNuevo);
            } catch (Exception ex) {
                Logger.getLogger(MensajeBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            buscarRecibidos();
        }
    }

    public void seleccionar(Mensaje z) {

        if (z == null) {
            return;
        }

        mensaje = z;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void imprimir() {

        if (mensaje == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Mensaje getMensaje() {
        return mensaje;
    }

    public void setMensaje(Mensaje mensaje) {
        this.mensaje = mensaje;
    }

    public List<Mensaje> getMensajesSinEnviar() {
        return mensajesSinEnviar;
    }

    public void setMensajesSinEnviar(List<Mensaje> mensajesSinEnviar) {
        this.mensajesSinEnviar = mensajesSinEnviar;
    }

    public List<Mensaje> getMensajesEnviados() {
        return mensajesEnviados;
    }

    public void setMensajesEnviados(List<Mensaje> mensajesEnviados) {
        this.mensajesEnviados = mensajesEnviados;
    }

    public List<Mensaje> getMensajesRecibidos() {
        return mensajesRecibidos;
    }

    public void setMensajesRecibidos(List<Mensaje> mensajesRecibidos) {
        this.mensajesRecibidos = mensajesRecibidos;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public DashboardBean getDashboardBean() {
        return dashboardBean;
    }

    public void setDashboardBean(DashboardBean dashboardBean) {
        this.dashboardBean = dashboardBean;
    }

}
