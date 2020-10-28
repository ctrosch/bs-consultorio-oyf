/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.compra.web;

import bs.compra.modelo.CircuitoCompra;
import bs.compra.modelo.CodigoCircuitoCompra;
import bs.compra.modelo.ItemCircuitoCompraCompra;
import bs.compra.modelo.ItemCircuitoCompraProveedor;
import bs.compra.modelo.ItemCircuitoCompraStock;
import bs.compra.modelo.ItemCircuitoCompraTesoreria;
import bs.compra.rn.CircuitoCompraRN;
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
public class CircuitoCompraBean extends GenericBean implements Serializable {

    @EJB
    private CircuitoCompraRN circuitoCompraRN;

    private CircuitoCompra circuito;
    private List<CircuitoCompra> lista;
    private String CIRCOM;
    private String CIRAPL;

    private boolean mostrarItemCircuitoCompraDeBaja;
    private boolean mostrarItemCircuitoProveedorDeBaja;
    private boolean mostrarItemCircuitoTesoreriaDeBaja;
    private boolean mostrarItemCircuitoStockDeBaja;

    // Variables para filtros
    private String actualizaCompra;
    private String actualizaProveedor;
    private String actualizaTesoreria;
    private String actualizaStock;
    private String pendienteIngreso;
    private CodigoCircuitoCompra circuitoInicio;
    private CodigoCircuitoCompra circuitoAplicado;

    public CircuitoCompraBean() {

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
                    seleccionar(circuitoCompraRN.getCircuito(CIRCOM, CIRAPL));
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
        circuito = new CircuitoCompra();
    }

    public void guardar(boolean nuevo) {

        try {
            if (circuito != null) {
                circuito = circuitoCompraRN.guardar(circuito, esNuevo);
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
            circuito = circuitoCompraRN.guardar(circuito, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizaron los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            circuitoCompraRN.eliminar(circuito);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();
        lista = circuitoCompraRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";

    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (actualizaCompra != null && !actualizaCompra.isEmpty()) {

            filtro.put("actualizaCompra = ", "'" + actualizaCompra + "'");
        }

        if (actualizaProveedor != null && !actualizaProveedor.isEmpty()) {

            filtro.put("actualizaProveedor = ", "'" + actualizaProveedor + "'");
        }

        if (actualizaStock != null && !actualizaStock.isEmpty()) {

            filtro.put("actualizaStock = ", "'" + actualizaStock + "'");
        }

        if (actualizaTesoreria != null && !actualizaTesoreria.isEmpty()) {

            filtro.put("actualizaTesoreria = ", "'" + actualizaTesoreria + "'");
        }

        if (pendienteIngreso != null && !pendienteIngreso.isEmpty()) {

            filtro.put("pendienteIngreso = ", "'" + pendienteIngreso + "'");
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
        actualizaCompra = "";
        actualizaProveedor = "";
        actualizaStock = "";
        actualizaTesoreria = "";
        pendienteIngreso = "";
        circuitoInicio = null;
        circuitoAplicado = null;

        buscar();

    }

    public List<CircuitoCompra> complete(String query) {
        try {
            lista = circuitoCompraRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CircuitoCompra>();
        }
    }

    public void onSelect(SelectEvent event) {
        circuito = (CircuitoCompra) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(CircuitoCompra c) {

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

            circuito = circuitoCompraRN.duplicar(circuito);
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void validarModulosActualizarCO() {

        if (circuito.getActualizaCompra().equals("S")) {

            circuito.setActualizaTesoreria("N");
            circuito.setActualizaStock("N");
            circuito.setActualizaProveedor("N");
            return;
        }
    }

    public void validarModulosActualizarPV() {

        if (circuito.getActualizaProveedor().equals("S")) {

            circuito.setActualizaCompra("N");
            return;
        }
    }

    public void validarModulosActualizarCJ() {

        if (circuito.getActualizaTesoreria().equals("S")) {

            circuito.setActualizaCompra("N");
            circuito.setActualizaProveedor("S");
        }
    }

    public void validarModulosActualizarST() {

        if (circuito.getActualizaStock().equals("S")) {

            circuito.setActualizaCompra("N");
            return;
        }
    }

    public void imprimir() {

        if (circuito == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void nuevoItemCircuitoCompra() {
        try {
            circuitoCompraRN.nuevoItemCircuitoCompra(circuito);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item circuito compra: " + ex);
        }
    }

    public void seleccionarItemCircuitoCompra(ItemCircuitoCompraCompra ic) {

    }

    public void sincronizarItemCircuitoCompra() {

    }

    public void nuevoItemCircuitoProveedor() {
        try {
            circuitoCompraRN.nuevoItemCircuitoProveedor(circuito);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item circuito proveedor: " + ex);
        }

    }

    public void seleccionarItemCircuitoProveedor(ItemCircuitoCompraProveedor ic) {

    }

    public void sincronizarItemCircuitoProveedor() {

    }

    public void nuevoItemCircuitoTesoreria() {
        try {
            circuitoCompraRN.nuevoItemCircuitoTesoreria(circuito);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item circuito proveedor: " + ex);
        }

    }

    public void seleccionarItemCircuitoTesoreria(ItemCircuitoCompraTesoreria ic) {

    }

    public void sincronizarItemCircuitoTesoreria() {

    }

    public void nuevoItemCircuitoStock() {
        try {
            circuitoCompraRN.nuevoItemCircuitoStock(circuito);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item circuito proveedor: " + ex);
        }

    }

    public void seleccionarItemCircuitoStock(ItemCircuitoCompraStock ic) {

    }

    public void sincronizarItemCircuitoStock() {

    }

    public void eliminarItemCircuitoCompra(ItemCircuitoCompraCompra itemCircuito) {

        try {
            circuitoCompraRN.eliminarItemCircuitoCompra(circuito, itemCircuito);
            JsfUtil.addWarningMessage("Se ha borrado el item comprobante " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + " " + ex);
        }
    }

    public void eliminarItemCircuitoProveedor(ItemCircuitoCompraProveedor itemCircuito) {

        try {
            circuitoCompraRN.eliminarItemCircuitoProveedor(circuito, itemCircuito);
            JsfUtil.addWarningMessage("Se ha borrado el item comprobante " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + " " + ex);
        }
    }

    public void eliminarItemCircuitoTesoreria(ItemCircuitoCompraTesoreria itemCircuito) {

        try {
            circuitoCompraRN.eliminarItemCircuitoTesoreria(circuito, itemCircuito);
            JsfUtil.addWarningMessage("Se ha borrado el item comprobante " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + " " + ex);
        }
    }

    public void eliminarItemCircuitoStock(ItemCircuitoCompraStock itemCircuito) {

        try {
            circuitoCompraRN.eliminarItemCircuitoStock(circuito, itemCircuito);
            JsfUtil.addWarningMessage("Se ha borrado el item comprobante " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + " " + ex);
        }
    }

    //-------------------------------------------------------------------------
    public CircuitoCompra getCircuito() {
        return circuito;
    }

    public void setCircuito(CircuitoCompra circuito) {
        this.circuito = circuito;
    }

    public List<CircuitoCompra> getLista() {
        return lista;
    }

    public void setLista(List<CircuitoCompra> lista) {
        this.lista = lista;
    }

    public boolean isMostrarItemCircuitoCompraDeBaja() {
        return mostrarItemCircuitoCompraDeBaja;
    }

    public void setMostrarItemCircuitoCompraDeBaja(boolean mostrarItemCircuitoCompraDeBaja) {
        this.mostrarItemCircuitoCompraDeBaja = mostrarItemCircuitoCompraDeBaja;
    }

    public boolean isMostrarItemCircuitoProveedorDeBaja() {
        return mostrarItemCircuitoProveedorDeBaja;
    }

    public void setMostrarItemCircuitoProveedorDeBaja(boolean mostrarItemCircuitoProveedorDeBaja) {
        this.mostrarItemCircuitoProveedorDeBaja = mostrarItemCircuitoProveedorDeBaja;
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

    public String getActualizaCompra() {
        return actualizaCompra;
    }

    public void setActualizaCompra(String actualizaCompra) {
        this.actualizaCompra = actualizaCompra;
    }

    public String getActualizaProveedor() {
        return actualizaProveedor;
    }

    public void setActualizaProveedor(String actualizaProveedor) {
        this.actualizaProveedor = actualizaProveedor;
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

    public String getPendienteIngreso() {
        return pendienteIngreso;
    }

    public void setPendienteIngreso(String pendienteIngreso) {
        this.pendienteIngreso = pendienteIngreso;
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

    public CodigoCircuitoCompra getCircuitoInicio() {
        return circuitoInicio;
    }

    public void setCircuitoInicio(CodigoCircuitoCompra circuitoInicio) {
        this.circuitoInicio = circuitoInicio;
    }

    public CodigoCircuitoCompra getCircuitoAplicado() {
        return circuitoAplicado;
    }

    public void setCircuitoAplicado(CodigoCircuitoCompra circuitoAplicado) {
        this.circuitoAplicado = circuitoAplicado;
    }

}
