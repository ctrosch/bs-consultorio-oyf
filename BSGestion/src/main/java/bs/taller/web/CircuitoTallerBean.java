/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.web;

import bs.facturacion.modelo.ItemCircuitoStock;
import bs.facturacion.modelo.ItemCircuitoTesoreria;
import bs.facturacion.modelo.ItemCircuitoVenta;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.taller.modelo.CircuitoTaller;
import bs.taller.modelo.ItemCircuitoTaller;
import bs.taller.rn.CircuitoTallerRN;
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
public class CircuitoTallerBean extends GenericBean implements Serializable {

    @EJB
    private CircuitoTallerRN circuitoRN;

    private CircuitoTaller circuito;
    private List<CircuitoTaller> lista;

    private boolean mostrarItemCircuitoServiceDeBaja;
    private boolean mostrarItemCircuitoVentaDeBaja;
    private boolean mostrarItemCircuitoTesoreriaDeBaja;
    private boolean mostrarItemCircuitoStockDeBaja;

    public CircuitoTallerBean() {

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
        circuito = new CircuitoTaller();
    }

    public void guardar(boolean nuevo) {

        try {
            if (circuito != null) {
                circuitoRN.guardar(circuito, esNuevo);
                esNuevo = false;
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            circuito.getAuditoria().setDebaja(habDes);
            circuitoRN.guardar(circuito, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizaron los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            circuitoRN.eliminar(circuito);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        lista = circuitoRN.getListaByBusqueda(txtBusqueda, mostrarDebaja);
    }

    public List<CircuitoTaller> complete(String query) {
        try {
            lista = circuitoRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CircuitoTaller>();
        }
    }

    public void onSelect(SelectEvent event) {
        circuito = (CircuitoTaller) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(CircuitoTaller c) {

        if (c == null) {
            return;
        }

        circuito = c;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void validarModulosActualizar() {

        if (circuito.getActualizaTaller().equals("S")) {

            circuito.setActualizaStock("N");
            return;
        }

        if (circuito.getActualizaStock().equals("S")) {
            circuito.setActualizaTaller("N");
        }

    }

    public void nuevoItemCircuitoService() {

    }

    public void seleccionarItemCircuitoService(ItemCircuitoTaller ic) {

    }

    public void sincronizarItemCircuitoService() {

    }

    public void nuevoItemCircuitoVenta() {

    }

    public void seleccionarItemCircuitoVenta(ItemCircuitoVenta ic) {

    }

    public void sincronizarItemCircuitoVenta() {

    }

    public void nuevoItemCircuitoTesoreria() {

    }

    public void seleccionarItemCircuitoTesoreria(ItemCircuitoTesoreria ic) {

    }

    public void sincronizarItemCircuitoTesoreria() {

    }

    public void nuevoItemCircuitoStock() {

    }

    public void seleccionarItemCircuitoStock(ItemCircuitoStock ic) {

    }

    public void sincronizarItemCircuitoStock() {

    }

    //--------------------------------------------------------------------------
    public CircuitoTaller getCircuito() {
        return circuito;
    }

    public void setCircuito(CircuitoTaller circuito) {
        this.circuito = circuito;
    }

    public List<CircuitoTaller> getLista() {
        return lista;
    }

    public void setLista(List<CircuitoTaller> lista) {
        this.lista = lista;
    }

    public boolean isMostrarItemCircuitoServiceDeBaja() {
        return mostrarItemCircuitoServiceDeBaja;
    }

    public void setMostrarItemCircuitoServiceDeBaja(boolean mostrarItemCircuitoServiceDeBaja) {
        this.mostrarItemCircuitoServiceDeBaja = mostrarItemCircuitoServiceDeBaja;
    }

    public boolean isMostrarItemCircuitoVentaDeBaja() {
        return mostrarItemCircuitoVentaDeBaja;
    }

    public void setMostrarItemCircuitoVentaDeBaja(boolean mostrarItemCircuitoVentaDeBaja) {
        this.mostrarItemCircuitoVentaDeBaja = mostrarItemCircuitoVentaDeBaja;
    }

    public boolean isMostrarItemCircuitoTesoreriaDeBaja() {
        return mostrarItemCircuitoTesoreriaDeBaja;
    }

    public void setMostrarItemCircuitoTesoreriaDeBaja(boolean mostrarItemCircuitoTesoreriaDeBaja) {
        this.mostrarItemCircuitoTesoreriaDeBaja = mostrarItemCircuitoTesoreriaDeBaja;
    }

    public boolean isMostrarItemCircuitoStockDeBaja() {
        return mostrarItemCircuitoStockDeBaja;
    }

    public void setMostrarItemCircuitoStockDeBaja(boolean mostrarItemCircuitoStockDeBaja) {
        this.mostrarItemCircuitoStockDeBaja = mostrarItemCircuitoStockDeBaja;
    }

}
