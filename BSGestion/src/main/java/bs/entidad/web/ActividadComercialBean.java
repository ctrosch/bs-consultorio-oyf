/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.web;

import bs.entidad.modelo.ActividadComercial;
import bs.entidad.modelo.TipoEntidad;
import bs.entidad.rn.ActividadComercialRN;
import bs.entidad.rn.TipoEntidadRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author GUILLERMO
 */
public class ActividadComercialBean extends GenericBean implements Serializable {

    @EJB
    private ActividadComercialRN actividadComercialRN;
    @EJB
    private TipoEntidadRN tipoEntidadRN;

    private ActividadComercial actividadComercial;
    private List<ActividadComercial> lista;
    protected String CODTIP = "-";

    private TipoEntidad tipoEntidad;
    private List<TipoEntidad> tipos;

    // Variables para filtros
    ///
    public ActividadComercialBean() {

    }

    public void nuevo() {
        esNuevo = true;
        modoVista = "D";

        actividadComercial = new ActividadComercial();

        tipoEntidad = tipoEntidadRN.getTipoEntidad(CODTIP);
        actividadComercial.setCodtip(CODTIP);
        actividadComercial.setTipoEntidad(tipoEntidad);

    }

    public void guardar(boolean nuevo) {

        try {
            if (actividadComercial != null) {
                actividadComercial = actividadComercialRN.guardar(actividadComercial, esNuevo);
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
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            actividadComercial.getAuditoria().setDebaja(habDes);
            actividadComercial = actividadComercialRN.guardar(actividadComercial, false);
            JsfUtil.addInfoMessage("Los datos se deshabilitaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible deshabilitar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            actividadComercialRN.eliminar(actividadComercial);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = actividadComercialRN.getActividadComercialByBusqueda(filtro, txtBusqueda, tipoEntidadRN.getTipoEntidad(CODTIP), mostrarDebaja, cantidadRegistros);
        modoVista = "B";

    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;

        buscar();

    }

    public List<ActividadComercial> complete(String query) {
        try {

            lista = actividadComercialRN.getActividadComercialByBusqueda(query, tipoEntidadRN.getTipoEntidad(CODTIP), false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<ActividadComercial>();
        }
    }

    public void onSelect(SelectEvent event) {
        actividadComercial = (ActividadComercial) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(ActividadComercial a) {

        if (a == null) {
            return;
        }

        actividadComercial = a;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (actividadComercial == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public ActividadComercial getActividadComercial() {
        return actividadComercial;
    }

    public void setActividadComercial(ActividadComercial actividadComercial) {
        this.actividadComercial = actividadComercial;
    }

    public List<ActividadComercial> getLista() {
        return lista;
    }

    public void setLista(List<ActividadComercial> lista) {
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

    public ActividadComercialRN getActividadComercialRN() {
        return actividadComercialRN;
    }

}
