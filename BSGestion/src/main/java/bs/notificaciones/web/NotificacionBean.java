/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.notificaciones.web;

import bs.administracion.modelo.Modulo;
import bs.administracion.modelo.Reporte;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.notificaciones.modelo.ItemNotificacionGrupo;
import bs.notificaciones.modelo.ItemNotificacionUsuario;
import bs.notificaciones.modelo.Notificacion;
import bs.notificaciones.rn.NotificacionRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author GUILLERMO
 */
@ManagedBean
@ViewScoped
public class NotificacionBean extends GenericBean implements Serializable {

    private String codigo;
    private Notificacion notificacion;
    private List<Notificacion> lista;
    private String MODULO = "";

    //variables para filtros
    private Reporte reporte;
    private Modulo modulo;
    private String email;
    private String whatsapp;
    private String mensajePush;

    @EJB
    private NotificacionRN notificacionRN;

    public NotificacionBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(notificacionRN.getNotificacion(codigo));
                } else {
                    buscar();
                }

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        esNuevo = true;
        modoVista = "D";
        notificacion = new Notificacion();

    }

    public void guardar(boolean nuevo) {

        try {
            if (notificacion != null) {

                notificacion = notificacionRN.guardar(notificacion, esNuevo);
                esNuevo = false;
                modoVista = "D";
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            notificacion.getAuditoria().setDebaja(habDes);
            notificacionRN.guardar(notificacion, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            notificacionRN.eliminar(notificacion);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        lista = notificacionRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (email != null && !email.isEmpty()) {

            filtro.put("email = ", "'" + email + "'");
        }

        if (mensajePush != null && !mensajePush.isEmpty()) {

            filtro.put("mensajePush = ", "'" + mensajePush + "'");
        }

        if (whatsapp != null && !whatsapp.isEmpty()) {

            filtro.put("whatsapp = ", "'" + whatsapp + "'");
        }
        if (modulo != null) {

            filtro.put("modulo.codigo = ", "'" + modulo.getCodigo() + "'");
        }

        if (reporte != null) {

            filtro.put("reporte.codigo = ", "'" + reporte.getCodigo() + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;

        modulo = null;
        reporte = null;
        email = "";
        mensajePush = "";
        whatsapp = "";

        buscar();

    }

    public List<Notificacion> complete(String query) {
        try {

            lista = notificacionRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Notificacion>();
        }
    }

    public void onSelect(SelectEvent event) {
        notificacion = (Notificacion) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Notificacion n) {

        if (n == null) {
            return;
        }

        notificacion = n;
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (notificacion == null) {
                JsfUtil.addErrorMessage("No hay Notificación activa");
                return;
            }

            notificacion = notificacionRN.duplicar(notificacion);
            esNuevo = true;
            modoVista = "D";

        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public List<Reporte> completeReporte(String query) {
        try {
            if (!MODULO.isEmpty() || MODULO != null) {
                filtro.clear();
                filtro.put("modulo.codigo = ", "'" + MODULO + "'");

                return reporteRN.getListaByBusqueda(filtro, query, false);

            } else {
                return new ArrayList<>();
            }

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public void asignarModulo() {

        MODULO = notificacionRN.asignarCodigoModulo(notificacion);
        notificacion = notificacionRN.asignarModulo(notificacion);

    }

    public void imprimir() {

        if (notificacion == null) {
            JsfUtil.addErrorMessage("No hay Notificación seleccionada, nada para imprimir");
        }
    }

    public List<Notificacion> getLista() {
        return lista;
    }

    public void setLista(List<Notificacion> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Notificacion getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(Notificacion notificacion) {
        this.notificacion = notificacion;
    }

    public void nuevoItemUsuario() {

        try {
            notificacionRN.nuevoItemUsuario(notificacion);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar usuario: " + ex);
        }
    }

    public void eliminarItemUsuario(ItemNotificacionUsuario item) {

        try {
            notificacionRN.eliminarItemUsuario(notificacion, item);
            JsfUtil.addWarningMessage("Se ha borrado el item usuario " + (item.getUsuario() != null ? item.getUsuario().getNombre() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (item.getUsuario() != null ? item.getUsuario().getNombre() : "") + " " + ex);
        }
    }

    public void nuevoItemGrupo() {

        try {
            notificacionRN.nuevoItemGrupo(notificacion);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar usuario: " + ex);
        }
    }

    public void eliminarItemGrupo(ItemNotificacionGrupo item) {

        try {
            notificacionRN.eliminarItemGrupo(notificacion, item);
            JsfUtil.addWarningMessage("Se ha borrado el item grupo " + (item.getGrupo() != null ? item.getGrupo().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (item.getGrupo() != null ? item.getGrupo().getDescripcion() : "") + " " + ex);
        }
    }

    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getMensajePush() {
        return mensajePush;
    }

    public void setMensajePush(String mensajePush) {
        this.mensajePush = mensajePush;
    }

    public String getMODULO() {
        return MODULO;
    }

    public void setMODULO(String MODULO) {
        this.MODULO = MODULO;
    }

}
