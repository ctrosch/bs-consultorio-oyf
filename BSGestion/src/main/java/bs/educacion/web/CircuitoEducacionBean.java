/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.web;

import bs.educacion.modelo.CircuitoEducacion;
import bs.educacion.modelo.CodigoCircuitoEducacion;
import bs.educacion.modelo.ItemCircuitoEducacionEducacion;
import bs.educacion.modelo.ItemCircuitoEducacionStock;
import bs.educacion.modelo.ItemCircuitoEducacionTesoreria;
import bs.educacion.modelo.ItemCircuitoEducacionVenta;
import bs.educacion.rn.CircuitoEducacionRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
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
public class CircuitoEducacionBean extends GenericBean implements Serializable {

    @EJB
    private CircuitoEducacionRN circuitoEducacionRN;

    private CircuitoEducacion circuito;
    private List<CircuitoEducacion> lista;
    private String CIRCOM;
    private String CIRAPL;

    private boolean mostrarItemCircuitoEducacionDeBaja;
    private boolean mostrarItemCircuitoVentaDeBaja;
    private boolean mostrarItemCircuitoTesoreriaDeBaja;
    private boolean mostrarItemCircuitoStockDeBaja;

    // Variables para filtros
    private String actualizaEducacion;
    private String actualizaVenta;
    private String actualizaTesoreria;
    private String actualizaStock;
    private CodigoCircuitoEducacion circuitoInicio;
    private CodigoCircuitoEducacion circuitoAplicado;

    public CircuitoEducacionBean() {

    }

    @PostConstruct
    public void init() {

        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (CIRCOM != null && !CIRCOM.isEmpty() && CIRAPL != null && !CIRAPL.isEmpty()) {
                    seleccionar(circuitoEducacionRN.getCircuito(CIRCOM, CIRAPL));
                }
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {
        modoVista = "D";
        esNuevo = true;
        buscaMovimiento = false;
        circuito = new CircuitoEducacion();
    }

    public void guardar(boolean nuevo) {

        try {
            if (circuito != null) {
                circuito = circuitoEducacionRN.guardar(circuito, esNuevo);
                esNuevo = false;
                modoVista = "D";
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
            circuito = circuitoEducacionRN.guardar(circuito, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizaron los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            circuitoEducacionRN.eliminar(circuito);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();
        lista = circuitoEducacionRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";

    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (actualizaEducacion != null && !actualizaEducacion.isEmpty()) {

            filtro.put("actualizaEducacion = ", "'" + actualizaEducacion + "'");
        }

        if (actualizaVenta != null && !actualizaVenta.isEmpty()) {

            filtro.put("actualizaVenta = ", "'" + actualizaVenta + "'");
        }

        if (actualizaStock != null && !actualizaStock.isEmpty()) {

            filtro.put("actualizaStock = ", "'" + actualizaStock + "'");
        }

        if (actualizaTesoreria != null && !actualizaTesoreria.isEmpty()) {

            filtro.put("actualizaTesoreria = ", "'" + actualizaTesoreria + "'");
        }

        if (circuitoInicio != null) {

            filtro.put("circom = ", "'" + circuitoInicio.getCodigo() + "'");
        }

        if (circuitoAplicado != null) {

            filtro.put("cirapl = ", "'" + circuitoAplicado.getCodigo() + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        actualizaVenta = "";
        actualizaStock = "";
        actualizaTesoreria = "";
        actualizaEducacion = "";
        circuitoInicio = null;
        circuitoAplicado = null;

        buscar();

    }

    public List<CircuitoEducacion> complete(String query) {
        try {
            lista = circuitoEducacionRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CircuitoEducacion>();
        }
    }

    public void onSelect(SelectEvent event) {
        circuito = (CircuitoEducacion) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(CircuitoEducacion c) {

        if (c == null) {
            return;
        }

        circuito = c;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void duplicar() {

        try {
            if (circuito == null) {
                JsfUtil.addErrorMessage("No hay circuito activo");
                return;
            }

            circuito = circuitoEducacionRN.duplicar(circuito);
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void validarModulosActualizarVT() {

        if (circuito.getActualizaVenta().equals("S")) {

            circuito.setActualizaEducacion("N");
            return;
        }
    }

    public void validarModulosActualizarED() {

        if (circuito.getActualizaEducacion().equals("S")) {

            circuito.setActualizaVenta("N");
            circuito.setActualizaTesoreria("N");
            circuito.setActualizaStock("N");
            return;
        }
    }

    public void validarModulosActualizarCJ() {

        if (circuito.getActualizaTesoreria().equals("S")) {

            circuito.setActualizaEducacion("N");

        }
    }

    public void validarModulosActualizarST() {

        if (circuito.getActualizaStock().equals("S")) {

            circuito.setActualizaEducacion("N");
            return;
        }
    }

    public void imprimir() {

        if (circuito == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void nuevoItemCircuitoEducacion() {
        try {
            circuitoEducacionRN.nuevoItemCircuitoEducacion(circuito);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item circuito Educación: " + ex);
        }
    }

    public void seleccionarItemCircuitoEducacion(ItemCircuitoEducacionEducacion ic) {

    }

    public void sincronizarItemCircuitoEducacion() {

    }

    public void nuevoItemCircuitoVenta() {
        try {
            circuitoEducacionRN.nuevoItemCircuitoVenta(circuito);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item circuito venta: " + ex);
        }

    }

    public void seleccionarItemCircuitoVenta(ItemCircuitoEducacionVenta ic) {

    }

    public void sincronizarItemCircuitoVenta() {

    }

    public void nuevoItemCircuitoTesoreria() {
        try {
            circuitoEducacionRN.nuevoItemCircuitoTesoreria(circuito);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item circuito tesoreria: " + ex);
        }

    }

    public void seleccionarItemCircuitoTesoreria(ItemCircuitoEducacionTesoreria ic) {

    }

    public void sincronizarItemCircuitoTesoreria() {

    }

    public void nuevoItemCircuitoStock() {
        try {
            circuitoEducacionRN.nuevoItemCircuitoStock(circuito);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item circuito stock: " + ex);
        }

    }

    public void seleccionarItemCircuitoStock(ItemCircuitoEducacionStock ic) {

    }

    public void sincronizarItemCircuitoStock() {

    }

    public void eliminarItemCircuitoEducacion(ItemCircuitoEducacionEducacion itemCircuito) {

        try {
            circuitoEducacionRN.eliminarItemCircuitoEducacion(circuito, itemCircuito);
            JsfUtil.addWarningMessage("Se ha borrado el item comprobante " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + " " + ex);
        }
    }

    public void eliminarItemCircuitoVenta(ItemCircuitoEducacionVenta itemCircuito) {

        try {
            circuitoEducacionRN.eliminarItemCircuitoVenta(circuito, itemCircuito);
            JsfUtil.addWarningMessage("Se ha borrado el item comprobante " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + " " + ex);
        }
    }

    public void eliminarItemCircuitoTesoreria(ItemCircuitoEducacionTesoreria itemCircuito) {

        try {
            circuitoEducacionRN.eliminarItemCircuitoTesoreria(circuito, itemCircuito);
            JsfUtil.addWarningMessage("Se ha borrado el item comprobante " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + " " + ex);
        }
    }

    public void eliminarItemCircuitoStock(ItemCircuitoEducacionStock itemCircuito) {

        try {
            circuitoEducacionRN.eliminarItemCircuitoStock(circuito, itemCircuito);
            JsfUtil.addWarningMessage("Se ha borrado el item comprobante " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + " " + ex);
        }
    }

    //-------------------------------------------------------------------------
    public CircuitoEducacion getCircuito() {
        return circuito;
    }

    public void setCircuito(CircuitoEducacion circuito) {
        this.circuito = circuito;
    }

    public List<CircuitoEducacion> getLista() {
        return lista;
    }

    public void setLista(List<CircuitoEducacion> lista) {
        this.lista = lista;
    }

    public boolean isMostrarItemCircuitoCompraDeBaja() {
        return mostrarItemCircuitoEducacionDeBaja;
    }

    public void setMostrarItemCircuitoEducacionDeBaja(boolean mostrarItemCircuitoEducacionDeBaja) {
        this.mostrarItemCircuitoEducacionDeBaja = mostrarItemCircuitoEducacionDeBaja;
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

    public String getActualizaEducacion() {
        return actualizaEducacion;
    }

    public void setActualizaEducacion(String actualizaEducacion) {
        this.actualizaEducacion = actualizaEducacion;
    }

    public String getActualizaVenta() {
        return actualizaVenta;
    }

    public void setActualizaVenta(String actualizaVenta) {
        this.actualizaVenta = actualizaVenta;
    }

    public String getActualizaTesoreria() {
        return actualizaTesoreria;
    }

    public void setActualizaTesoreria(String actualizaTesoreria) {
        this.actualizaTesoreria = actualizaTesoreria;
    }

    public String getActualizaStock() {
        return actualizaStock;
    }

    public void setActualizaStock(String actualizaStock) {
        this.actualizaStock = actualizaStock;
    }

    public String getCIRCOM() {
        return CIRCOM;
    }

    public void setCIRCOM(String CIRCOM) {
        this.CIRCOM = CIRCOM;
    }

    public String getCIRAPL() {
        return CIRAPL;
    }

    public void setCIRAPL(String CIRAPL) {
        this.CIRAPL = CIRAPL;
    }

    public CodigoCircuitoEducacion getCircuitoInicio() {
        return circuitoInicio;
    }

    public void setCircuitoInicio(CodigoCircuitoEducacion circuitoInicio) {
        this.circuitoInicio = circuitoInicio;
    }

    public CodigoCircuitoEducacion getCircuitoAplicado() {
        return circuitoAplicado;
    }

    public void setCircuitoAplicado(CodigoCircuitoEducacion circuitoAplicado) {
        this.circuitoAplicado = circuitoAplicado;
    }

}
