/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.taller.modelo.TipoMantenimiento2;
import bs.taller.rn.TipoMantenimiento2RN;
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
public class TipoMantenimiento2Bean extends GenericBean implements Serializable {

    private TipoMantenimiento2 tipoMantenimiento;
    private List<TipoMantenimiento2> lista;

    @EJB
    private TipoMantenimiento2RN tipoMantenimientoRN;

    public TipoMantenimiento2Bean() {

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
        tipoMantenimiento = new TipoMantenimiento2();
    }

    public void guardar(boolean nuevo) {

        try {
            if (tipoMantenimiento != null) {

                tipoMantenimientoRN.guardar(tipoMantenimiento, esNuevo);
                esNuevo = false;
                buscaMovimiento = false;
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
            tipoMantenimiento.getAuditoria().setDebaja(habDes);
            tipoMantenimientoRN.guardar(tipoMantenimiento, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            tipoMantenimientoRN.eliminar(tipoMantenimiento);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        lista = tipoMantenimientoRN.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<TipoMantenimiento2> complete(String query) {
        try {
            lista = tipoMantenimientoRN.getListaByBusqueda(query, false, 5);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TipoMantenimiento2>();
        }
    }

    public void onSelect(SelectEvent event) {
        tipoMantenimiento = (TipoMantenimiento2) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(TipoMantenimiento2 u) {

        if (u == null) {
            return;
        }

        tipoMantenimiento = u;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void imprimir() {

        if (tipoMantenimiento == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public TipoMantenimiento2 getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(TipoMantenimiento2 unidadMedida) {
        this.tipoMantenimiento = unidadMedida;
    }

    public List<TipoMantenimiento2> getLista() {
        return lista;
    }

    public void setLista(List<TipoMantenimiento2> lista) {
        this.lista = lista;
    }
}
