/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.web;

import bs.entidad.modelo.EstadoEntidad;
import bs.entidad.rn.EstadoEntidadRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
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
public class EstadoEntidadBean extends GenericBean implements Serializable {

    private EstadoEntidad estadoEntidad;
    private List<EstadoEntidad> lista;

    @EJB
    private EstadoEntidadRN estadoEntidadRN;

    public EstadoEntidadBean() {

    }

    @PostConstruct
    public void init() {
        txtBusqueda = "";
        mostrarDebaja = false;
        nuevo();
        buscar();
    }

    public void nuevo() {

        esNuevo = true;
        buscaMovimiento = false;
        estadoEntidad = new EstadoEntidad();
    }

    public void guardar(boolean nuevo) {

        try {
            if (estadoEntidad != null) {
                estadoEntidad = estadoEntidad = estadoEntidadRN.guardar(estadoEntidad, esNuevo);
                esNuevo = false;
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitarDeshabilitar(String habDes) {

        try {
            estadoEntidad.getAuditoria().setDebaja(habDes);
            estadoEntidadRN.guardar(estadoEntidad, esNuevo);
            JsfUtil.addInfoMessage("Los datos se deshabilitaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible habilitar/deshabilitar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            estadoEntidadRN.eliminar(estadoEntidad);
            estadoEntidad = new EstadoEntidad();
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        lista = estadoEntidadRN.getEstadoEntidadByBusqueda(txtBusqueda, false);
    }

    public void onSelect(SelectEvent event) {
        estadoEntidad = (EstadoEntidad) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(EstadoEntidad e) {
        if (e == null) {
            return;
        }
        estadoEntidad = e;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public List<EstadoEntidad> complete(String query) {
        try {
            lista = estadoEntidadRN.getEstadoEntidadByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<EstadoEntidad>();
        }
    }

    public EstadoEntidad getEstadoEntidad() {
        return estadoEntidad;
    }

    public void setEstadoEntidad(EstadoEntidad estadoEntidad) {
        this.estadoEntidad = estadoEntidad;
    }

    public List<EstadoEntidad> getLista() {
        return lista;
    }

    public void setLista(List<EstadoEntidad> lista) {
        this.lista = lista;
    }

}
