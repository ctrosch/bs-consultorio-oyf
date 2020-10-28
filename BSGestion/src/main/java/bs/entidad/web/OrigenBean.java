/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.web;

import bs.entidad.modelo.Origen;
import bs.entidad.modelo.TipoEntidad;
import bs.entidad.rn.OrigenRN;
import bs.entidad.rn.TipoEntidadRN;
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
public class OrigenBean extends GenericBean implements Serializable {

    @EJB
    private OrigenRN origenRN;
    @EJB
    private TipoEntidadRN tipoEntidadRN;

    private Origen origen;
    private List<Origen> lista;

    private TipoEntidad tipoEntidad;
    private List<TipoEntidad> tipos;

    public OrigenBean() {

    }

    @PostConstruct
    public void init() {

        txtBusqueda = "";
        mostrarDebaja = false;

        tipos = tipoEntidadRN.getListaByBusqueda(false);
        if (!tipos.isEmpty()) {
            tipoEntidad = tipos.get(0);
        }

        nuevo();
        buscar();
    }

    public void nuevo() {
        esNuevo = true;
        buscaMovimiento = false;
        origen = new Origen();
    }

    public void guardar(boolean nuevo) {

        try {
            if (origen != null) {
                origen = origenRN.guardar(origen, esNuevo);
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
            origen.getAuditoria().setDebaja(habDes);
            origen = origenRN.guardar(origen, false);
            JsfUtil.addInfoMessage("Los datos se deshabilitaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible deshabilitar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            origenRN.eliminar(origen);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        lista = origenRN.getOrigenByBusqueda(txtBusqueda, tipoEntidad, mostrarDebaja);
    }

    public List<Origen> complete(String query) {
        try {
            lista = origenRN.getOrigenByBusqueda(query, tipoEntidad, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Origen>();
        }
    }

    public void onSelect(SelectEvent event) {
        origen = (Origen) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(Origen c) {

        if (c == null) {
            return;
        }

        origen = c;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void imprimir() {

        if (origen == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Origen getOrigen() {
        return origen;
    }

    public void setOrigen(Origen origen) {
        this.origen = origen;
    }

    public List<Origen> getLista() {
        return lista;
    }

    public void setLista(List<Origen> lista) {
        this.lista = lista;
    }

    public TipoEntidad getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(TipoEntidad tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public List<TipoEntidad> getTipos() {
        return tipos;
    }

    public void setTipos(List<TipoEntidad> tipos) {
        this.tipos = tipos;
    }

}
