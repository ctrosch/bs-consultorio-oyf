/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.web;

import bs.facturacion.modelo.CircuitoFacturacion;
import bs.facturacion.modelo.CodigoCircuitoFacturacion;
import bs.facturacion.modelo.ItemCircuitoFacturacion;
import bs.facturacion.modelo.ItemCircuitoStock;
import bs.facturacion.modelo.ItemCircuitoTesoreria;
import bs.facturacion.modelo.ItemCircuitoVenta;
import bs.facturacion.rn.CircuitoFacturacionRN;
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
import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.endpoint.DotEndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class CircuitoFacturacionBean extends GenericBean implements Serializable {

    @EJB
    private CircuitoFacturacionRN circuitoRN;

    private CircuitoFacturacion circuito;
    private List<CircuitoFacturacion> lista;

    private String CIRCOM;
    private String CIRAPL;

    private DefaultDiagramModel diagrama;
//    private int xDiagrama = 6;
    private int yDiagrama = 6;

    // Variables para filtros
    private String actualizaFacturacion;
    private String actualizaVenta;
    private String actualizaTesoreria;
    private String actualizaStock;
    private String comprometeStock;
    private CodigoCircuitoFacturacion circuitoInicio;
    private CodigoCircuitoFacturacion circuitoAplicado;

    public CircuitoFacturacionBean() {

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
                    seleccionar(circuitoRN.getCircuito(CIRCOM, CIRAPL));
                }

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        esNuevo = true;
        modoVista = "D";
        circuito = new CircuitoFacturacion();
    }

    public void guardar(boolean nuevo) {

        try {
            if (circuito != null) {
                circuito = circuitoRN.guardar(circuito, esNuevo);
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
            circuito = circuitoRN.guardar(circuito, false);
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

        cargarFiltroBusqueda();
        lista = circuitoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (actualizaFacturacion != null && !actualizaFacturacion.isEmpty()) {

            filtro.put("actualizaFacturacion = ", "'" + actualizaFacturacion + "'");
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

        if (comprometeStock != null && !comprometeStock.isEmpty()) {

            filtro.put("comprometeStock = ", "'" + comprometeStock + "'");
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

        actualizaFacturacion = "";
        actualizaVenta = "";
        actualizaStock = "";
        actualizaTesoreria = "";
        comprometeStock = "";
        circuitoInicio = null;
        circuitoAplicado = null;

        buscar();

    }

    public List<CircuitoFacturacion> complete(String query) {
        try {

            lista = circuitoRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CircuitoFacturacion>();
        }
    }

    public void onSelect(SelectEvent event) {
        circuito = (CircuitoFacturacion) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void onCircuitoInicioChange() {

        if (circuito.getCircuitoComienzo() != null) {
            circuito.setCircom(circuito.getCircuitoComienzo().getCodigo());
        }
    }

    public void onCircuitoAplicacionChange() {

        if (circuito.getCircuitoAplicacion() != null) {
            circuito.setCirapl(circuito.getCircuitoAplicacion().getCodigo());
        }
    }

    public void seleccionar(CircuitoFacturacion c) {

        if (c == null) {
            return;
        }

        circuito = c;
        esNuevo = false;
        modoVista = "D";
    }

    public void validarModulosActualizarFC() {

        if (circuito.getActualizaFacturacion().equals("S")) {

            circuito.setActualizaTesoreria("N");
            circuito.setActualizaStock("N");
            circuito.setActualizaVenta("N");
            return;
        }
    }

    public void validarModulosActualizarVT() {

        if (circuito.getActualizaVenta().equals("S")) {

            circuito.setActualizaFacturacion("N");
            return;
        }
    }

    public void validarModulosActualizarCJ() {

        if (circuito.getActualizaTesoreria().equals("S")) {

            circuito.setActualizaFacturacion("N");
            circuito.setActualizaVenta("S");
        }
    }

    public void validarModulosActualizarST() {

        if (circuito.getActualizaStock().equals("S")) {

            circuito.setActualizaFacturacion("N");
            return;
        }
    }

    public void nuevoItemCircuitoFacturacion() {

        try {
            circuitoRN.nuevoItemCircuitoFacturacion(circuito);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item circuito facturacion: " + ex);
        }
    }

    public void eliminarItemCircuitoFacturacion(ItemCircuitoFacturacion itemCircuito) {

        try {
            circuitoRN.eliminarItemCircuitoFacturacion(circuito, itemCircuito);
            JsfUtil.addWarningMessage("Se ha borrado el item comprobante " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + " " + ex);
        }
    }

    public void nuevoItemCircuitoVenta() {

        try {
            circuitoRN.nuevoItemCircuitoVenta(circuito);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item circuito venta: " + ex);
        }
    }

    public void eliminarItemCircuitoVenta(ItemCircuitoVenta itemCircuito) {

        try {
            circuitoRN.eliminarItemCircuitoVenta(circuito, itemCircuito);
            JsfUtil.addWarningMessage("Se ha borrado el item comprobante " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + " " + ex);
        }
    }

    public void nuevoItemCircuitoTesoreria() {

        try {
            circuitoRN.nuevoItemCircuitoTesoreria(circuito);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item circuito tesoreria: " + ex);
        }
    }

    public void eliminarItemCircuitoTesoreria(ItemCircuitoTesoreria itemCircuito) {

        try {
            circuitoRN.eliminarItemCircuitoTesoreria(circuito, itemCircuito);
            JsfUtil.addWarningMessage("Se ha borrado el item comprobante " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + " " + ex);
        }
    }

    public void nuevoItemCircuitoStock() {

        try {
            circuitoRN.nuevoItemCircuitoStock(circuito);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item circuito tesoreria: " + ex);
        }
    }

    public void eliminarItemCircuitoStock(ItemCircuitoStock itemCircuito) {

        try {
            circuitoRN.eliminarItemCircuitoStock(circuito, itemCircuito);
            JsfUtil.addWarningMessage("Se ha borrado el item comprobante " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemCircuito.getComprobante() != null ? itemCircuito.getComprobante().getDescripcion() : "") + " " + ex);
        }
    }

    public void iniciarDiagrama() {

        diagrama = new DefaultDiagramModel();
        diagrama.setMaxConnections(-1);

        CircuitoFacturacion circuitoInicial = circuitoRN.getCircuito("0100", "0100");

        yDiagrama = 6;

        Element elementA = new Element(circuitoInicial.getDescripcion(), 60 + "em", yDiagrama + "em");
        elementA.addEndPoint(new DotEndPoint(EndPointAnchor.BOTTOM));

        diagrama.addElement(elementA);

        generarDiagrama(circuitoInicial, elementA);
    }

    private void generarDiagrama(CircuitoFacturacion circuitoFac, Element raiz) {

        if (circuitoFac != null) {

            filtro.clear();
            filtro.put("cirapl = ", "'" + circuitoFac.getCircom() + "'");
            filtro.put("circom <> ", "'" + circuitoFac.getCircom() + "'");

            circuitoFac.setCircuitosRelacionados(circuitoRN.getListaByBusqueda(filtro, "", true));

//            System.err.println("********" + circuitoFac.getCircuitosRelacionados());
            if (circuitoFac.getCircuitosRelacionados() != null) {

                yDiagrama = yDiagrama + 6;
                int xDiagrama = 20;

                for (CircuitoFacturacion cr : circuitoFac.getCircuitosRelacionados()) {

//                    System.err.println("circuito relacionado " + cr.getDescripcion());
                    Element nuevoElemento = new Element(cr.getDescripcion(), xDiagrama + "em", yDiagrama + "em");
                    nuevoElemento.addEndPoint(new DotEndPoint(EndPointAnchor.TOP));

                    diagrama.addElement(nuevoElemento);
                    diagrama.connect(new Connection(raiz.getEndPoints().get(0), nuevoElemento.getEndPoints().get(0)));

                    xDiagrama = xDiagrama + 20;

                    generarDiagrama(cr, nuevoElemento);
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    public CircuitoFacturacion getCircuito() {
        return circuito;
    }

    public void setCircuito(CircuitoFacturacion circuito) {
        this.circuito = circuito;
    }

    public List<CircuitoFacturacion> getLista() {
        return lista;
    }

    public void setLista(List<CircuitoFacturacion> lista) {
        this.lista = lista;
    }

    public DefaultDiagramModel getDiagrama() {
        return diagrama;
    }

    public void setDiagrama(DefaultDiagramModel diagrama) {
        this.diagrama = diagrama;
    }

    public CircuitoFacturacionRN getCircuitoRN() {
        return circuitoRN;
    }

    public void setCircuitoRN(CircuitoFacturacionRN circuitoRN) {
        this.circuitoRN = circuitoRN;
    }

    public int getyDiagrama() {
        return yDiagrama;
    }

    public void setyDiagrama(int yDiagrama) {
        this.yDiagrama = yDiagrama;
    }

    public String getActualizaFacturacion() {
        return actualizaFacturacion;
    }

    public void setActualizaFacturacion(String actualizaFacturacion) {
        this.actualizaFacturacion = actualizaFacturacion;
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

    public String getComprometeStock() {
        return comprometeStock;
    }

    public void setComprometeStock(String comprometeStock) {
        this.comprometeStock = comprometeStock;
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

    public CodigoCircuitoFacturacion getCircuitoInicio() {
        return circuitoInicio;
    }

    public void setCircuitoInicio(CodigoCircuitoFacturacion circuitoInicio) {
        this.circuitoInicio = circuitoInicio;
    }

    public CodigoCircuitoFacturacion getCircuitoAplicado() {
        return circuitoAplicado;
    }

    public void setCircuitoAplicado(CodigoCircuitoFacturacion circuitoAplicado) {
        this.circuitoAplicado = circuitoAplicado;
    }

}
