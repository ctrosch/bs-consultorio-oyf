/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.web;

import bs.entidad.modelo.TipoEntidad;
import bs.entidad.rn.TipoEntidadRN;
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

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class tipoEntidadBean extends GenericBean implements Serializable {

    @EJB
    private TipoEntidadRN tipoEntidadRN;

    private TipoEntidad tipoEntidad;
    private List<TipoEntidad> lista;

    public tipoEntidadBean() {
        mostrarDebaja = false;

    }

    @PostConstruct
    public void init() {

    }

    public void buscar() {
        lista = tipoEntidadRN.getListaByBusqueda(txtBusqueda, mostrarDebaja);
    }

    public List<TipoEntidad> complete(String query) {
        try {
            lista = tipoEntidadRN.getListaByBusqueda(query, mostrarDebaja);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TipoEntidad>();
        }
    }

    public TipoEntidad getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(TipoEntidad tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public List<TipoEntidad> getLista() {
        return lista;
    }

    public void setLista(List<TipoEntidad> lista) {
        this.lista = lista;
    }

}
