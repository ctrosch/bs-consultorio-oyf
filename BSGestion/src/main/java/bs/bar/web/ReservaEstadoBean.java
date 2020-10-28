/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.web;

import bs.bar.modelo.ReservaEstado;
import bs.bar.rn.ReservaEstadoRN;
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
public class ReservaEstadoBean extends GenericBean implements Serializable {

    private ReservaEstado reservaEstado;
    private List<ReservaEstado> lista;

    @EJB
    private ReservaEstadoRN reservaEstadoRN;

    public ReservaEstadoBean() {

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
        reservaEstado = new ReservaEstado();
    }

    public void guardar(boolean nuevo) {

        try {
            if (reservaEstado != null) {

                reservaEstado = reservaEstadoRN.guardar(reservaEstado, esNuevo);
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
            reservaEstado.getAuditoria().setDebaja(habDes);
            reservaEstado = reservaEstadoRN.guardar(reservaEstado, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            reservaEstadoRN.eliminar(reservaEstado);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        lista = reservaEstadoRN.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<ReservaEstado> complete(String query) {
        try {
            lista = reservaEstadoRN.getListaByBusqueda(query, false, cantidadRegistros);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<ReservaEstado>();
        }
    }

    public void onSelect(SelectEvent event) {
        reservaEstado = (ReservaEstado) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(ReservaEstado u) {

        if (u == null) {
            return;
        }

        reservaEstado = u;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void imprimir() {

        if (reservaEstado == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public ReservaEstado getReservaEstado() {
        return reservaEstado;
    }

    public void setReservaEstado(ReservaEstado reservaEstado) {
        this.reservaEstado = reservaEstado;
    }

    public List<ReservaEstado> getLista() {
        return lista;
    }

    public void setLista(List<ReservaEstado> lista) {
        this.lista = lista;
    }

}
