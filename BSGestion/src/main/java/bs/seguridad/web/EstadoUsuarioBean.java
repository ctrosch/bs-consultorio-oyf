/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.seguridad.modelo.EstadoUsuario;
import bs.seguridad.rn.EstadoUsuarioRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class EstadoUsuarioBean extends GenericBean implements Serializable {

    private EstadoUsuario estado;
    private List<EstadoUsuario> lista;

    @EJB
    private EstadoUsuarioRN estadoRN;

    public EstadoUsuarioBean() {

    }

    @PostConstruct
    public void init() {

        mostrarDebaja = false;
        txtBusqueda = "";
        nuevo();
        buscar();
    }

    public void nuevo() {

        esNuevo = true;
        estado = new EstadoUsuario();
    }

    public void guardar(boolean nuevo) {

        try {
            if (estado != null) {
                estadoRN.guardar(estado, esNuevo);
                esNuevo = false;
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            estado.getAuditoria().setDebaja(habDes);
            estadoRN.guardar(estado, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            estadoRN.eliminar(estado);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        lista = estadoRN.getUsuarioByBusqueda(txtBusqueda, mostrarDebaja);
    }

    public List<EstadoUsuario> complete(String query) {
        try {
            lista = estadoRN.getUsuarioByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<EstadoUsuario>();
        }
    }

    public void onSelect(SelectEvent event) {
        estado = (EstadoUsuario) event.getObject();
        esNuevo = false;
    }

    public void seleccionar(EstadoUsuario u) {

        if (u == null) {
            return;
        }

        estado = u;
        esNuevo = false;
    }

    public void imprimir() {

        if (estado == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public List<EstadoUsuario> getLista() {
        return lista;
    }

    public void setLista(List<EstadoUsuario> lista) {
        this.lista = lista;
    }

}
