/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.web;

import bs.administracion.modelo.Reporte;
import bs.compra.web.MovimientoCompraBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Formulario;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.FormularioPorSituacionIVARN;
import bs.global.rn.PuntoVentaRN;
import bs.global.util.JsfUtil;
import bs.global.util.ReportFactory;
import bs.global.web.GenericBean;
import bs.produccion.modelo.CircuitoProduccion;
import bs.produccion.modelo.DepartamentoProduccion;
import bs.produccion.modelo.ItemDetalleMovimientoProduccion;
import bs.produccion.modelo.ItemMovimientoProduccion;
import bs.produccion.modelo.MovimientoProduccion;
import bs.produccion.modelo.TipoMovimientoProduccion;
import bs.produccion.rn.CircuitoProduccionRN;
import bs.produccion.rn.ProduccionRN;
import bs.produccion.vista.PendienteProduccionDetalle;
import bs.produccion.vista.PendienteProduccionGrupo;
import bs.seguridad.web.UsuarioSessionBean;
import bs.stock.modelo.Producto;
import bs.stock.rn.ProductoRN;
import bs.stock.web.FormulaBean;
import bs.stock.web.ProductoBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.component.wizard.Wizard;
import org.primefaces.event.FlowEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class MovimientoProduccionBean extends GenericBean implements Serializable {

    @EJB
    private ProduccionRN produccionRN;
    @EJB
    private ProductoRN productoRN;
    @EJB
    private CircuitoProduccionRN circuitoRN;
    @EJB
    private PuntoVentaRN puntoVentaRN;

    protected @EJB
    FormularioPorSituacionIVARN formularioPorSituacionIVARN;

    protected MovimientoProduccion m;
    protected ItemMovimientoProduccion item;
    protected List<MovimientoProduccion> movimientos;
    protected CircuitoProduccion circuito;

    protected String CIRCOM = "";
    protected String CIRAPL = "";

    protected String SUCPD = "";
    protected String MODPD = "";
    protected String CODPD = "";

    protected String SUCST = "";
    protected String MODST = "";
    protected String CODST = "";

    protected String SUCPR = "";
    protected String MODPR = "";
    protected String CODPR = "";

    protected String SUCVC = "";
    protected String MODVC = "";
    protected String CODVC = "";

    @ManagedProperty(value = "#{usuarioSessionBean}")
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;

    @ManagedProperty(value = "#{formulaBean}")
    protected FormulaBean formulaBean;

    @ManagedProperty(value = "#{formularioProduccionBean}")
    protected FormularioProduccionBean formularioProduccionBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    //Fecha para control
    protected Date fechaMinina;

    //Datos para generar movimientos aplicados
    protected List<PendienteProduccionGrupo> movimientosPendientes;
    protected List<PendienteProduccionDetalle> itemsPendiente;
    protected PendienteProduccionGrupo movimientoPendiente;

    protected PuntoVenta puntoVenta;
    protected PuntoVenta puntoVentaStock;
    protected PuntoVenta puntoVentaParteProceso;
    protected PuntoVenta puntoVentaValeConsumo;

    //Atributos para valores por defecto
    DepartamentoProduccion departamento;
    String prioridad = "";

    /**
     * Creates a new instance of RequerimientoProduccionBean
     */
    /**
     * Creates a new instance of OrdenProduccionBean
     */
    public MovimientoProduccionBean() {

        fechaMinina = new Date();
        titulo = "Movimiento de Producción";
        muestraReporte = false;
        numeroFormularioDesde = 1;
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                formulario = new Formulario();
                iniciarMovimiento();
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void iniciarMovimiento() {

        super.iniciar();

        try {

            movimientoPendiente = new PendienteProduccionGrupo();
            movimientosPendientes = null;
            itemsPendiente = new ArrayList<PendienteProduccionDetalle>();

            muestraReporte = false;
            solicitaEmail = false;
            buscaMovimiento = false;

            nombreArchivo = "";
            esAnulacion = false;

            numeroFormularioDesde = null;
            numeroFormularioHasta = null;
            fechaMovimientoDesde = null;
            fechaMovimientoHasta = null;

            puntoVenta = puntoVentaRN.getPuntoVenta(SUCPD);
            puntoVentaStock = puntoVentaRN.getPuntoVenta(SUCST);
            puntoVentaParteProceso = puntoVentaRN.getPuntoVenta(SUCPR);
            puntoVentaValeConsumo = puntoVentaRN.getPuntoVenta(SUCVC);

            if (puntoVentaStock == null) {
                puntoVentaStock = puntoVenta;
            }

            if (puntoVentaParteProceso == null) {
                puntoVentaParteProceso = puntoVenta;
            }

            if (puntoVentaValeConsumo == null) {
                puntoVentaValeConsumo = puntoVenta;
            }

            if (puntoVenta == null) {
                puntoVenta = puntoVentaStock;
            }

            iniciarCircuito(CIRCOM, CIRAPL);

            if (!seleccionaPendiente) {
                m = produccionRN.nuevoMovimiento(circuito, puntoVenta, puntoVentaStock);
            }

            cargarFormulariosBusqueda();
            aplicarDatosPorDefecto();

        } catch (ExcepcionGeneralSistema ex) {

            Logger.getLogger(MovimientoCompraBean.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("iniciarMovimiento: " + ex);

        } catch (Exception ex) {
            Logger.getLogger(MovimientoCompraBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void iniciarCircuito(String circom, String cirapl) throws ExcepcionGeneralSistema {

        if (circom != null && cirapl != null) {
            circuito = circuitoRN.getCircuito(circom, cirapl);
        }

        if (circuito == null) {
            JsfUtil.addErrorMessage("No se encontró circuito " + circom + "-" + cirapl);
            return;
        }

        circuitoRN.cargarCircuitosRelacionados(circuito);

        if (circuito.getActualizaProduccion().equals("S") && MODPD != null && CODPD != null) {

            circuito.setComprobanteProduccion(circuitoRN.getComprobanteProduccion(circom, cirapl, MODPD, CODPD));

        } else if (circuito.getActualizaStock().equals("S") && MODST != null && CODST != null) {

            circuito.setComprobanteStock(circuitoRN.getComprobanteStock(circom, cirapl, MODST, CODST));
        }

        if (circuito.getAutomatizaParteProduccion().equals("S")) {

            circuito.setComprobanteParteProceso(circuitoRN.getComprobanteProduccion(circom, cirapl, MODPR, CODPR));
            circuito.setComprobanteValeConsumo(circuitoRN.getComprobanteStock(circom, cirapl, MODVC, CODVC));
        }

        if (circom == null) {
            circom = "";
        }
        seleccionaPendiente = (!circom.equals(cirapl));
    }

    public void nuevo() {

        m = null;

        if (seleccionaPendiente) {
            Wizard w = (Wizard) FacesContext.getCurrentInstance().getViewRoot().findComponent("fPendiente").findComponent("wizPendiente");
            w.setStep("filtro");

            AccordionPanel a = (AccordionPanel) FacesContext.getCurrentInstance().getViewRoot().findComponent("fPendiente").findComponent("accordion");
            a.setActiveIndex("0");
        }
        iniciarMovimiento();
    }

    public void guardar(boolean nuevo) {

        try {
            m = produccionRN.guardar(m);

            if (m.getComprobante().getModulo().equals("PD")) {
                JsfUtil.addInfoMessage("El documento " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            } else if (m.getMovimientoStock() != null) {
                JsfUtil.addInfoMessage("El documento " + m.getMovimientoStock().getFormulario().getDescripcion() + "-" + m.getMovimientoStock().getNumeroFormulario() + " se guardó correctamente", "");
            }

            if (m.getValeConsumo() != null && m.getValeConsumo().isPersistido()) {
                JsfUtil.addInfoMessage("El documento " + m.getValeConsumo().getComprobante().getDescripcion() + "-" + m.getValeConsumo().getNumeroFormulario() + " se guardó correctamente", "");
            }

            if (m.getParteProceso() != null && m.getParteProceso().isPersistido()) {
                JsfUtil.addInfoMessage("El documento " + m.getParteProceso().getComprobante().getDescripcion() + "-" + m.getParteProceso().getNumeroFormulario() + " se guardó correctamente", "");
            }

            if (nuevo) {
                nuevo();
            }

        } catch (Exception ex) {

            JsfUtil.addErrorMessage(ex.getMessage());
        }
    }

    public void procesarProducto() {

        if (productoBean.getProducto() != null && m != null && item != null) {

            try {
                produccionRN.asignarProducto(item, productoBean.getProducto());

            } catch (ExcepcionGeneralSistema ex) {

                JsfUtil.addErrorMessage("No es posible asignar producto al item " + ex);
            }

            if (productoBean.getProducto() != null && m != null && item != null) {

            }
        }
    }

    public void procesarFormula() {

        if (formulaBean.getFormula() != null && m != null && item != null) {

            try {
                produccionRN.asignarFormula(item, formulaBean.getFormula());

            } catch (ExcepcionGeneralSistema ex) {

                JsfUtil.addErrorMessage("No es posible agregar componentes y procesos " + ex);
            }
        }
    }

    public void nuevoItem() {

        try {

            productoBean.setProducto(null);
            //Cargarmos un nuevo item en blanco
            m.getItemsMovimiento().add(produccionRN.nuevoItem(m, "P"));

            item = m.getItemsMovimiento().get(m.getItemsProducto().size() - 1);
            PrimeFaces.current().executeScript("PF('dlgProducto').show()");

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("No es posible agregar un nuevo item " + ex);
        }
    }

    public void nuevoItemHorario() {

        try {

            productoBean.setProducto(null);
            //Cargarmos un nuevo item en blanco

            item = produccionRN.nuevoItem(m, "H");
            m.getItemsMovimiento().add(item);

            Producto pHorario = productoRN.getProducto("080001");

            produccionRN.asignarProducto(item, pHorario);

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("No es posible agregar un nuevo item " + ex);
        }
    }

    public void agregarItem(ItemMovimientoProduccion item, String tipitm) {
        try {

            if (!puedoAgregarItem(item)) {
                return;
            }

            item.setCantidadStock(item.getCantidad());
            item.setCantidadOriginal(item.getCantidad());

            for (ItemDetalleMovimientoProduccion id : item.getItemDetalle()) {

                id.setCantidad(item.getCantidad());
                id.setUnidadMedida(item.getUnidadMedida());

                id.setAtributo1(item.getAtributo1());
                id.setAtributo2(item.getAtributo2());
                id.setAtributo3(item.getAtributo3());
                id.setAtributo4(item.getAtributo4());
                id.setAtributo5(item.getAtributo5());
                id.setAtributo6(item.getAtributo6());
                id.setAtributo7(item.getAtributo7());
            }

            //Si es comprobante de Orden de producción, agregamos los componentes
            if (circuito.getTipoMovimiento() == TipoMovimientoProduccion.OP) {

                produccionRN.agregarComponentesYProcesos(m, item);
            }

            //Cargarmos un nuevo item en blanco
            item.setTodoOk(true);
            m.getItemsMovimiento().add(produccionRN.nuevoItem(m, tipitm));
            productoBean.setProducto(null);

        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage(ex.getMessage());
        }
    }

    public void agregarItemDetalle(ItemMovimientoProduccion nItem) {
        try {

            produccionRN.agregarItemDetalle(nItem);

        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage(ex.getMessage());
        }
    }

    public boolean puedoAgregarItem(ItemMovimientoProduccion nItem) {

        if ((circuito.getItemUnico().equals("S")) && (m.getItemsMovimiento().size() > 1)) {
            JsfUtil.addErrorMessage("Ha superado la cantidad máxima de items, no puede continuar agregando");
            return false;
        }

        if (nItem.getCantidad() == null || nItem.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            JsfUtil.addErrorMessage("Ingrese un valor de cantidad válido. Mayor a 0");
            return false;
        }

        if (productoBean.getProducto() == null) {
            JsfUtil.addErrorMessage("Seleccione un producto para agregar al comprobante");
            return false;
        }

        return true;
    }

    public void eliminarItem(ItemMovimientoProduccion nItem) {

        if (produccionRN.eliminarItem(m, nItem)) {
            JsfUtil.addWarningMessage("Se ha borrado el item " + (nItem.getProducto() != null ? nItem.getProducto().getDescripcion() : "") + "");
        } else {
            JsfUtil.addErrorMessage("Error " + (nItem.getProducto() != null ? nItem.getProducto().getDescripcion() : "") + "");
        }
    }

    public void eliminarItemDetalle(ItemMovimientoProduccion ip, ItemDetalleMovimientoProduccion nItem) {

        try {
            if (produccionRN.eliminarItemDetalle(ip, nItem)) {

                JsfUtil.addWarningMessage("Se ha borrado el item " + nItem.getAtributo1() + "");
            } else {

                JsfUtil.addInfoMessage("No se encontró el item a borrar");
            }
        } catch (ExcepcionGeneralSistema e) {
            JsfUtil.addErrorMessage(e.getMessage());
        }
    }

    public void actualizarCantidades() {

        try {
            produccionRN.actualizarCantidades(m);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("Error: " + ex);
        }
    }

    public void verPendientes(CircuitoProduccion c) {

        try {
            iniciarCircuito(c.getCircom(), c.getCirapl());
            m = null;

            if (seleccionaPendiente) {
                Wizard w = (Wizard) FacesContext.getCurrentInstance().getViewRoot().findComponent("fPendiente").findComponent("wizPendiente");
                w.setStep("filtro");

                AccordionPanel a = (AccordionPanel) FacesContext.getCurrentInstance().getViewRoot().findComponent("fPendiente").findComponent("accordion");
                a.setActiveIndex("0");
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible seleccionar pendientes");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String procesoSeleccionPendientes(FlowEvent event) {

        if (event.getNewStep().equals("filtro")) {
            JsfUtil.addInfoMessage("Aplique los filtros que considere");
        }

        if (event.getNewStep().equals("pendiente_grupo")) {
            try {
                if (m != null) {

                    JsfUtil.addInfoMessage("El comprobante fue generado, no puede regresar al paso anterior");
                    return event.getOldStep();
                }

                seleccionarMovimientosPendientes();

                if (movimientosPendientes == null || movimientosPendientes.isEmpty()) {
                    JsfUtil.addInfoMessage("No se encontraron pendientes para este circuito");
                    return event.getOldStep();
                }

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible procesar pendientes: " + ex);
            }
        }

        return event.getNewStep();
    }

    public void seleccionarMovimientosPendientes() throws ExcepcionGeneralSistema {

        cargarFiltroGrupo();

        movimientoPendiente = new PendienteProduccionGrupo();
        movimientosPendientes = produccionRN.getMovimientosPendiente(filtroGrupo);

        itemsPendiente = new ArrayList<PendienteProduccionDetalle>();
        seleccionaTodo = false;

        if (movimientosPendientes != null && !movimientosPendientes.isEmpty()) {
            JsfUtil.addInfoMessage("Seleccione un comprobante para ver los pendientes");
        }
    }

    public void seleccionarItemPendiente(PendienteProduccionGrupo pg, boolean muestraMensaje) {

        movimientoPendiente = pg;
        cargarFiltroDetalle();
        itemsPendiente = produccionRN.getItemsPendiente(filtroDetalle);

        if (itemsPendiente.isEmpty() && muestraMensaje) {
            JsfUtil.addWarningMessage("No se han encontrado items pendientes");
        }
    }

    //Seleccionar todos los items pendientes
    public void seleccionarTodo() {

        produccionRN.seleccionarTodo(itemsPendiente, seleccionaTodo);

    }

//        try {
//                if (itemsPendiente == null) {
//            JsfUtil.addErrorMessage("No existen items pendientes");
//            return event.getOldStep();
//        }
//
//        if (itemsPendiente.isEmpty()) {
//            JsfUtil.addErrorMessage("No existen items pendientes");
//            return event.getOldStep();
//        }
//
//        m = produccionRN.nuevoMovimiento(circuito, puntoVenta, puntoVentaStock);
//        aplicarDatosPorDefecto();
//        produccionRN.genetems(m, itemsPendiente);
//
//        //Generamos automaticamente el comprobante
//        if (m.getValeConsumo() != null) {
//
//            //Genera y carga filtro para seleccionar pendientes de vale de consumo
//            Map<String, String> filtroDetalleVC = new HashMap<String, String>();
//            //filtroDetalleVC.put("modapl=", "'" + movimientoPendiente.getModapl() + "'");
//            //filtroDetalleVC.put("codapl=", "'" + movimientoPendiente.getCodapl() + "'");
//            //filtroDetalleVC.put("nroapl=", "" + movimientoPendiente.getNroapl());
//            filtroDetalleVC.put("circom = ", "'" + CIRAPL + "'");
//            filtroDetalleVC.put("expapl > ", "0");
//            filtroDetalleVC.put("producto.tippro=", "'MAT'");
//
//            //Seleccionamos los items pendientes
//            List<PendienteProduccionDetalle> itemsPendienteVC = produccionRN.getItemPendiente(filtroDetalleVC);
//            //Marca todos los items como seleccionados
//            produccionRN.seleccionarTodo(itemsPendienteVC, true);
//            //Genera los items para el comprobante
//            if (itemsPendienteVC != null) {
//
//                if (itemsPendienteVC.isEmpty()) {
//                    m.setValeConsumo(null);
//                } else {
//                    produccionRN.generarItems(m.getValeConsumo(), itemsPendienteVC);
//                }
//            }
//        }
//
//        //Generamos automaticamente el comprobante parte proceso
//        if (m.getParteProceso() != null) {
//
//            //Genera y carga filtro para seleccionar pendientes de parte proceso
//            Map<String, String> filtroDetallePR = new HashMap<String, String>();
////                    filtroDetallePR.put("modapl=", "'" + movimientoPendiente.getModapl() + "'");
////                    filtroDetallePR.put("codapl=", "'" + movimientoPendiente.getCodapl() + "'");
////                    filtroDetallePR.put("nroapl=", "" + movimientoPendiente.getNroapl());
//            filtroDetallePR.put("circom = ", "'" + CIRAPL + "'");
//            filtroDetallePR.put("expapl > ", "0");
//            filtroDetallePR.put("producto.tippro=", "'PRC'");
//
//            //Seleccionamos los items pendientes
//            List<PendienteProduccionDetalle> itemsPendientePR = produccionRN.getItemPendiente(filtroDetallePR);
//            //Marca todos los items como seleccionados
//            produccionRN.seleccionarTodo(itemsPendientePR, true);
//            //Genera los items para el comprobante
//            if (itemsPendientePR != null) {
//
//                if (itemsPendientePR.isEmpty()) {
//                    m.setParteProceso(null);
//                } else {
//                    produccionRN.generarItems(m.getParteProceso(), itemsPendientePR);
//                }
//            }
//        }
//
//    }
//    catch (Exception e
//
//
//        ) {
//                e.printStackTrace();
//        JsfUtil.addErrorMessage("Error al generar comprobante", e.getMessage());
//        return event.getOldStep();
//    }
    public void finalizarProcesoSeleccionPendiente() {

        try {
            if (!produccionRN.tengoItemsSeleccionados(itemsPendiente)) {
                JsfUtil.addErrorMessage("No existen items pendientes seleccionados para generar el movimiento");
                return;
            }

            m = produccionRN.nuevoMovimientoFromPendiente(circuito, puntoVenta, puntoVentaStock, movimientoPendiente, itemsPendiente);
            aplicarDatosPorDefecto();

            movimientoPendiente = null;
            itemsPendiente = null;

            //PrimeFaces.current().ajax().update("fPendiente :formulario");
            PrimeFaces.current().executeScript("PF('dlg_pendiente').hide()");

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al generar comprobante", "- " + ex);
            log.log(Level.SEVERE, "Error al generar comprobante", ex);
        }
    }

    public void generarMovimientoFromItems(List<ItemMovimientoProduccion> items) throws Exception {

        m = produccionRN.nuevoMovimientoFromItems(circuito, puntoVenta, puntoVentaStock, items);
        aplicarDatosPorDefecto();

    }

    public void cargarFiltroGrupo() {

        filtroGrupo.clear();

        if (numeroFormularioDesde != null) {
            if (numeroFormularioDesde > 0) {
                filtroGrupo.put("nrofor = ", "" + numeroFormularioDesde);
            }
        }

        filtroGrupo.put("circom = ", "'" + circuito.getCirapl() + "'");

        if (circuito.getTipoMovimiento().equals(TipoMovimientoProduccion.VC)) {
            filtroGrupo.put("tipitm = ", "'C'");
        }

        if (circuito.getTipoMovimiento().equals(TipoMovimientoProduccion.PP)) {
            filtroGrupo.put("tipitm = ", "'P'");
        }

        if (circuito.getTipoMovimiento().equals(TipoMovimientoProduccion.PR)) {
            filtroGrupo.put("tipitm = ", "'R'");
        }

        if (circuito.getTipoMovimiento().equals(TipoMovimientoProduccion.PH)) {
            filtroGrupo.put("tipitm = ", "'H'");
        }
    }

    public void cargarFiltroDetalle() {

        filtroDetalle.clear();

        if (movimientoPendiente == null) {
            return;
        }

        filtroDetalle.put("modfor=", "'" + movimientoPendiente.getModfor() + "'");
        filtroDetalle.put("codfor=", "'" + movimientoPendiente.getCodfor() + "'");
        filtroDetalle.put("nrofor=", "" + movimientoPendiente.getNrofor());

        if (circuito.getTipoMovimiento().equals(TipoMovimientoProduccion.VC)) {

            filtroDetalle.put("tipitm = ", "'C'");
        }

        if (circuito.getTipoMovimiento().equals(TipoMovimientoProduccion.PP)) {
            filtroDetalle.put("tipitm = ", "'P'");
        }

        if (circuito.getTipoMovimiento().equals(TipoMovimientoProduccion.PR)) {
            filtroDetalle.put("tipitm = ", "'R'");
        }

        if (circuito.getTipoMovimiento().equals(TipoMovimientoProduccion.PH)) {
            filtroDetalle.put("tipitm = ", "'H'");
        }
    }

    public void generarReporteEtiquetas() {

        try {

            Map parameters = new HashMap();

            if (m == null) {
                throw new ExcepcionGeneralSistema("No se puede imprimir - No existe movimiento seleccionado");
            }

            if (m.getTipoMovimiento().equals(TipoMovimientoProduccion.PP)) {

                parameters.put("ID", m.getId());
                parameters.put("CANT_COPIAS", m.getMovimientoStock().getComprobante().getCopias());

//                System.err.println("m.getId() " + m.getId());
                nombreArchivo = "ETIQUETAS";

                Reporte reporte = reporteRN.getReporte("SIS_00149");

                reportFactory.exportReportToPdfFile(reporte, nombreArchivo, parameters);
            }

            muestraReporte = true;

        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage("No se encontró el archivo: " + m.getFormulario().getReporte().getPath());
            muestraReporte = false;

        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + ex);
            muestraReporte = false;
        }
    }

    public void imprimirEtiquetas() {

        generarReporteEtiquetas();

        PrimeFaces.current().ajax().update("formulario");

        if (muestraReporte) {
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
        }
    }

    public void imprimir(String modulo) {

        generarReporte(modulo);

        PrimeFaces.current().ajax().update("formulario");

        if (muestraReporte) {
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
        }
    }

    public void generarReporte(String modulo) {

        try {

            Map parameters = new HashMap();

            if (modulo.equals("PD") && m != null) {

                if (m.getFormulario().getReporte() == null) {
                    throw new ExcepcionGeneralSistema("No se puede imprimir - El formulario de producción no tiene reporte asociado");
                }

                parameters.put("ID", m.getId());
                parameters.put("CANT_COPIAS", m.getComprobante().getCopias());

                nombreArchivo = m.getFormulario().getCodigo() + "-" + m.getNumeroFormulario();
                reportFactory.exportReportToPdfFile(m.getFormulario().getReporte(), nombreArchivo, parameters);
            }

            if (modulo.equals("ST") && m.getMovimientoStock() != null) {

                if (m.getMovimientoStock().getFormulario().getReporte() == null) {
                    throw new ExcepcionGeneralSistema("No se puede imprimir - El formulario de stock no tiene reporte asociado");
                }

                parameters.put("ID", m.getMovimientoStock().getId());
                parameters.put("CANT_COPIAS", m.getMovimientoStock().getComprobante().getCopias());

                nombreArchivo = m.getMovimientoStock().getFormulario().getCodigo() + "-" + m.getMovimientoStock().getNumeroFormulario();
                reportFactory.exportReportToPdfFile(m.getMovimientoStock().getFormulario().getReporte(), nombreArchivo, parameters);
            }

            muestraReporte = true;

        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage("No se encontró el archivo: " + m.getFormulario().getReporte().getPath());
            muestraReporte = false;

        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + ex);
            muestraReporte = false;
        }
    }

    public void nuevaBusqueda() {

        if (m != null && m.getFormulario() != null) {
            formulario = m.getFormulario();
        }

        buscaMovimiento = true;
    }

    public void buscarMovimiento() {

        if (!validarParametros()) {
            return;
        }
        cargarFiltroBusqueda();
        movimientos = produccionRN.getListaByBusqueda(filtro, seleccionaPendiente, cantidadRegistros);

        if (movimientos == null || movimientos.isEmpty()) {
            JsfUtil.addWarningMessage("No se han encontrado movimientos");
        }
    }

    public boolean validarParametros() {

        if (formulario == null) {
            JsfUtil.addWarningMessage("Seleccione un formulario");
            return false;
        }

        if (fechaMovimientoDesde != null && fechaMovimientoHasta != null) {
            if (fechaMovimientoHasta.before(fechaMovimientoDesde)) {
                JsfUtil.addWarningMessage("La fecha de desde, no puede ser mayor a la fecha hasta.");
                return false;
            }
        }

        if (numeroFormularioDesde != null && numeroFormularioHasta != null) {
            if (numeroFormularioDesde > numeroFormularioHasta) {
                JsfUtil.addWarningMessage("Número de formulario desde es mayor al número de formulario hasta");
                return false;
            }
        }

        return true;
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        filtro.put("circuito.circom =", "'" + CIRCOM + "'");

        if (formulario != null) {
            filtro.put("formulario.codigo = ", "'" + formulario.getCodigo() + "'");
        }

        if (numeroFormularioDesde != null) {

            filtro.put("numeroFormulario >=", String.valueOf(numeroFormularioDesde));
        }

        if (numeroFormularioHasta != null) {

            filtro.put("numeroFormulario <=", String.valueOf(numeroFormularioHasta));
        }

        if (fechaMovimientoDesde != null) {

            filtro.put("fechaMovimiento >= ", JsfUtil.getFechaSQL(fechaMovimientoDesde));
        }

        if (fechaMovimientoHasta != null) {

            filtro.put("fechaMovimiento <= ", JsfUtil.getFechaSQL(fechaMovimientoHasta));
        }

        if (seleccionaPendiente) {
            //filtro.put("itemsProducto.")
        }
    }

    public void resetParametros() {

//        formulario = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        muestraReporte = false;
        solicitaEmail = false;
        movimientos = null;
        indiceWizard = 0;
    }

    public void seleccionarMovimiento(MovimientoProduccion mSel) {

        if (m.getId() == mSel.getId()) {
            return;
        }

        m = mSel;
        buscaMovimiento = false;
    }

    protected void aplicarDatosPorDefecto() {

        if (departamento != null) {
            m.setDepartamento(departamento);
        }

        if (!prioridad.isEmpty()) {
            m.setPrioridad(prioridad);
        }
    }

    private void cargarFormulariosBusqueda() {

        if (circuito.getActualizaProduccion().equals("S") && MODPD != null && CODPD != null) {

            formularioProduccionBean.setLista(formularioPorSituacionIVARN.getFormularioByComprobante(circuito.getComprobanteProduccion()));
        }

        if (circuito.getActualizaStock().equals("S") && MODST != null && CODST != null) {

            formularioProduccionBean.setLista(formularioPorSituacionIVARN.getFormularioByComprobante(circuito.getComprobanteStock()));
        }

        if (formularioProduccionBean.getLista().size() == 1) {
            formulario = formularioProduccionBean.getLista().get(0);
        }
    }

    //--------------------------------------------------------------------------
    public MovimientoProduccion getM() {
        return m;
    }

    public void setM(MovimientoProduccion m) {
        this.m = m;
    }

    public CircuitoProduccion getCircuito() {
        return circuito;
    }

    public void setCircuito(CircuitoProduccion circuito) {
        this.circuito = circuito;
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

    public String getSUCPD() {
        return SUCPD;
    }

    public void setSUCPD(String SUCPD) {
        this.SUCPD = SUCPD;
    }

    public String getMODPD() {
        return MODPD;
    }

    public void setMODPD(String MODPD) {
        this.MODPD = MODPD;
    }

    public String getCODPD() {
        return CODPD;
    }

    public void setCODPD(String CODPD) {
        this.CODPD = CODPD;
    }

    public String getSUCST() {
        return SUCST;
    }

    public void setSUCST(String SUCST) {
        this.SUCST = SUCST;
    }

    public String getMODST() {
        return MODST;
    }

    public void setMODST(String MODST) {
        this.MODST = MODST;
    }

    public String getCODST() {
        return CODST;
    }

    public void setCODST(String CODST) {
        this.CODST = CODST;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public ProductoBean getProductoBean() {
        return productoBean;
    }

    public void setProductoBean(ProductoBean productoBean) {
        this.productoBean = productoBean;
    }

    public FormularioProduccionBean getFormularioProduccionBean() {
        return formularioProduccionBean;
    }

    public void setFormularioProduccionBean(FormularioProduccionBean formularioProduccionBean) {
        this.formularioProduccionBean = formularioProduccionBean;
    }

    public Date getFechaMinina() {
        return fechaMinina;
    }

    public void setFechaMinina(Date fechaMinina) {
        this.fechaMinina = fechaMinina;
    }

    public List<PendienteProduccionGrupo> getMovimientosPendientes() {
        return movimientosPendientes;
    }

    public void setMovimientosPendientes(List<PendienteProduccionGrupo> movimientosPendientes) {
        this.movimientosPendientes = movimientosPendientes;
    }

    public List<PendienteProduccionDetalle> getItemsPendiente() {
        return itemsPendiente;
    }

    public void setItemsPendiente(List<PendienteProduccionDetalle> itemsPendiente) {
        this.itemsPendiente = itemsPendiente;
    }

    public PendienteProduccionGrupo getMovimientoPendiente() {
        return movimientoPendiente;
    }

    public void setMovimientoPendiente(PendienteProduccionGrupo movimientoPendiente) {
        this.movimientoPendiente = movimientoPendiente;
    }

    public PuntoVenta getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(PuntoVenta puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public PuntoVenta getPuntoVentaStock() {
        return puntoVentaStock;
    }

    public void setPuntoVentaStock(PuntoVenta puntoVentaStock) {
        this.puntoVentaStock = puntoVentaStock;
    }

    public PuntoVenta getPuntoVentaParteProceso() {
        return puntoVentaParteProceso;
    }

    public void setPuntoVentaParteProceso(PuntoVenta puntoVentaParteProceso) {
        this.puntoVentaParteProceso = puntoVentaParteProceso;
    }

    public PuntoVenta getPuntoVentaValeConsumo() {
        return puntoVentaValeConsumo;
    }

    public void setPuntoVentaValeConsumo(PuntoVenta puntoVentaValeConsumo) {
        this.puntoVentaValeConsumo = puntoVentaValeConsumo;
    }

    public DepartamentoProduccion getDepartamento() {
        return departamento;
    }

    public void setDepartamento(DepartamentoProduccion departamento) {
        this.departamento = departamento;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public List<MovimientoProduccion> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoProduccion> movimientos) {
        this.movimientos = movimientos;
    }

    public String getSUCPR() {
        return SUCPR;
    }

    public void setSUCPR(String SUCPR) {
        this.SUCPR = SUCPR;
    }

    public String getMODPR() {
        return MODPR;
    }

    public void setMODPR(String MODPR) {
        this.MODPR = MODPR;
    }

    public String getCODPR() {
        return CODPR;
    }

    public void setCODPR(String CODPR) {
        this.CODPR = CODPR;
    }

    public String getSUCVC() {
        return SUCVC;
    }

    public void setSUCVC(String SUCVC) {
        this.SUCVC = SUCVC;
    }

    public String getMODVC() {
        return MODVC;
    }

    public void setMODVC(String MODVC) {
        this.MODVC = MODVC;
    }

    public String getCODVC() {
        return CODVC;
    }

    public void setCODVC(String CODVC) {
        this.CODVC = CODVC;
    }

    public ReportFactory getReportFactory() {
        return reportFactory;
    }

    public void setReportFactory(ReportFactory reportFactory) {
        this.reportFactory = reportFactory;
    }

    public ItemMovimientoProduccion getItem() {
        return item;
    }

    public void setItem(ItemMovimientoProduccion item) {
        this.item = item;
    }

    public FormulaBean getFormulaBean() {
        return formulaBean;
    }

    public void setFormulaBean(FormulaBean formulaBean) {
        this.formulaBean = formulaBean;
    }

}
