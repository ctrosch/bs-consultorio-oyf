/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.web;

import bs.administracion.modelo.CorreoElectronico;
import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.rn.DireccionEntregaRN;
import bs.entidad.rn.EntidadRN;
import bs.facturacion.modelo.CircuitoFacturacion;
import bs.facturacion.modelo.ItemMovimientoFacturacion;
import bs.facturacion.modelo.ItemMovimientoFacturacionDetalle;
import bs.facturacion.modelo.ItemMovimientoFacturacionKit;
import bs.facturacion.modelo.MovimientoFacturacion;
import bs.facturacion.rn.CircuitoFacturacionRN;
import bs.facturacion.rn.FacturacionRN;
import bs.facturacion.rn.NotificacionesFacturacionRN;
import bs.facturacion.vista.PendienteFacturacionDetalle;
import bs.facturacion.vista.PendienteFacturacionGrupo;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.ConceptoComprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.Localidad;
import bs.global.modelo.Sucursal;
import bs.global.modelo.Zona;
import bs.global.rn.FormularioPorSituacionIVARN;
import bs.global.util.JsfUtil;
import bs.global.util.ReportFactory;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.LocalidadBean;
import bs.global.web.MailFactory;
import bs.seguridad.web.UsuarioSessionBean;
import bs.stock.modelo.Producto;
import bs.stock.modelo.Stock;
import bs.stock.rn.ProductoRN;
import bs.stock.rn.StockRN;
import bs.stock.web.ProductoBean;
import bs.stock.web.informe.ConsultaStock;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.tesoreria.rn.MovimientoTesoreriaRN;
import bs.tesoreria.web.informe.ConsultaValoresBean;
import bs.ventas.modelo.CanalVenta;
import bs.ventas.modelo.Repartidor;
import bs.ventas.modelo.Vendedor;
import bs.ventas.rn.ListaPrecioVentaRN;
import bs.ventas.rn.MovimientoVentaRN;
import bs.ventas.rn.NotificacionesVentaRN;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.PrimeFaces;
import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.chart.MeterGaugeChartModel;

/**
 * @author ctrosch
 */
@ManagedBean
@ViewScoped
public class MovimientoFacturacionBean extends GenericBean implements Serializable {

    protected @EJB
    FacturacionRN facturacionRN;
    protected @EJB
    MovimientoVentaRN ventaRN;
    protected @EJB
    MovimientoTesoreriaRN tesoreriaRN;
    protected @EJB
    NotificacionesVentaRN notificacionesVentaRN;
    protected @EJB
    NotificacionesFacturacionRN notificacionesFacturacionRN;
    protected @EJB
    CircuitoFacturacionRN circuitoRN;
    protected @EJB
    ListaPrecioVentaRN listaPrecioRN;
    protected @EJB
    FormularioPorSituacionIVARN formularioPorSituacionIVARN;
    protected @EJB
    ProductoRN productoRN;
    protected @EJB
    EntidadRN entidadRN;
    @EJB
    private StockRN stockRN;
    @EJB
    private DireccionEntregaRN direccionEntregaRN;
    @EJB
    private MailFactory mailFactoryBean;

    //Datos inicializacion registracion
    protected String CIRCOM = "";
    protected String CIRAPL = "";
    protected String SUCURS = "";

    protected String CODFC = "";
    protected String SUCFC = "";
    protected String CODVT = "";
    protected String SUCVT = "";
    protected String CODCJ = "";
    protected String SUCCJ = "";
    protected String CODST = "";
    protected String SUCST = "";
    protected String NROCTA = "";

    //Datos para generar movimientos aplicados
    protected List<PendienteFacturacionGrupo> movimientosPendientes;
    protected List<PendienteFacturacionDetalle> itemsPendiente;
    protected PendienteFacturacionGrupo movimientoPendiente;
    protected PendienteFacturacionDetalle itemPendiente;

    // VARIABLES PARA BUSQUEDA DE COMPROBANTES
    protected EntidadComercial cliente;
    protected Repartidor repartidor;
    protected CanalVenta canalVenta;
    protected Sucursal sucursal;
    protected Zona zona;
    protected Vendedor vendedor;
    protected boolean congelaPrecio;
    protected boolean congelaCotizacion;
    protected String razonSocial;
    protected List<MovimientoFacturacion> movimientos;
    protected boolean mantieneCotizacionOriginal;

    private boolean solicitaDatos;
    private boolean solicitaDatosRemitente;
    private boolean solicitaDatosDestinatario;
    private boolean existeProducto;
    private String codigoBarra;
    private String codigoBarraPendiente;

    //--------------------------------------------------
    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    @ManagedProperty(value = "#{productoFacturacionBean}")
    protected ProductoFacturacionBean productoFacturacionBean;

    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;

    @ManagedProperty(value = "#{consultaStock}")
    protected ConsultaStock consultaStock;

    @ManagedProperty(value = "#{consultaValoresBean}")
    protected ConsultaValoresBean consultaValoresBean;

    @ManagedProperty(value = "#{localidadBean}")
    protected LocalidadBean localidadBean;

    protected MovimientoFacturacion m;
    protected ItemMovimientoFacturacion item;
    protected ItemMovimientoFacturacionDetalle itemDetalle;
    protected CircuitoFacturacion circuito;

    public MovimientoFacturacionBean() {

        titulo = "Movimiento de Facturación";
        muestraReporte = false;
        solicitaDatos = false;
        mantieneCotizacionOriginal = false;
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                super.iniciar();
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
            movimientoPendiente = new PendienteFacturacionGrupo();
            movimientosPendientes = null;
            itemsPendiente = new ArrayList<>();

            circuito = circuitoRN.iniciarCircuito(CIRCOM, CIRAPL, CODFC, CODVT, CODST, CODCJ);
            seleccionaPendiente = (!circuito.getCircom().equals(circuito.getCirapl()));
            congelaPrecio = (circuito.getCongelaPrecio() != null && circuito.getCongelaPrecio().equals("S"));
            congelaCotizacion = (circuito.getCongelaCotizacion() != null && circuito.getCongelaCotizacion().equals("S"));

            m = facturacionRN.nuevoMovimiento(circuito, CODFC, SUCFC, CODVT, SUCVT, CODST, SUCST, CODCJ, SUCCJ);

            if (NROCTA != null && !NROCTA.isEmpty()) {
                m.setCliente(entidadRN.getEntidad(NROCTA));
                procesarCliente();
            }

            comprobante = m.getComprobante();
            puntoVenta = m.getPuntoVenta();
            cliente = m.getCliente();

            modoVista = (seleccionaPendiente ? "P" : "D");

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Iniciar Movimiento: " + ex);
        }
    }

    public void nuevo() {

        m = null;
        iniciarMovimiento();

        if (seleccionaPendiente) {
//            Wizard w = (Wizard) FacesContext.getCurrentInstance().getViewRoot().findComponent("fPendiente").findComponent("wizPendiente");
//            w.setStep("filtro");

            AccordionPanel a = (AccordionPanel) FacesContext.getCurrentInstance().getViewRoot().findComponent("formulario").findComponent("accordion");
            a.setActiveIndex("0");
        }
        esNuevo = true;
        modoVista = (seleccionaPendiente ? "P" : "D");
    }

    public void nuevoItem() {

        try {
            calcularImportes();
            productoFacturacionBean.setTxtBusqueda("");
            productoFacturacionBean.setProducto(null);
            productoFacturacionBean.setCotizacion(m.getCotizacion());
            //Cargarmos un nuevo item en blanco
            facturacionRN.agregarItem(m);

            item = m.getItemsProducto().get(m.getItemsProducto().size() - 1);
            PrimeFaces.current().ajax().update("fBuscadorProducto");
            PrimeFaces.current().executeScript("PF('dlgProducto').show()");

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("No es posible agregar un nuevo item " + ex);
        }
    }

    public void nuevoItem(Producto p) {

        try {

            existeProducto = false;

            m.getItemsProducto().forEach(i -> {
                if (i.getProducto().equals(p)) {

                    i.setCantidad(i.getCantidad().add(BigDecimal.ONE));
                    existeProducto = true;
                }
            });

            if (!existeProducto) {
                facturacionRN.agregarItem(m);
                item = m.getItemsProducto().get(m.getItemsProducto().size() - 1);
                facturacionRN.asignarProducto(item, p);
            }

            calcularImportes();

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("No es posible agregar un nuevo item " + ex);
        }
    }

    public void nuevoItemDetalle(ItemMovimientoFacturacion itemProducto) {

        try {

            facturacionRN.nuevoItemDetalle(itemProducto);

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("No es posible agregar un nuevo item detalle de atributos " + ex);
        }
    }

    public void nuevoItemKit(ItemMovimientoFacturacion itemProducto) {

        try {

            facturacionRN.nuevoItemKit(itemProducto);

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("No es posible agregar un nuevo item kit " + ex);
        }
    }

    public void guardar(boolean nuevo) {

        try {
            m = facturacionRN.guardar(m);

            if (m.getComprobante().getModulo().equals("FC")) {

                JsfUtil.addInfoMessage("El documento " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            } else {

                if (m.getMovimientoVenta() != null) {
                    JsfUtil.addInfoMessage("El documento de venta " + m.getMovimientoVenta().getFormulario().getDescripcion() + "-" + m.getMovimientoVenta().getNumeroFormulario() + " se guardó correctamente", "");
                }

                if (m.getMovimientoStock() != null) {
                    JsfUtil.addInfoMessage("El documento de stock " + m.getMovimientoStock().getFormulario().getDescripcion() + "-" + m.getMovimientoStock().getNumeroFormulario() + " se guardó correctamente", "");
                }

                if (m.getMovimientoTesoreria() != null) {
                    JsfUtil.addInfoMessage("El documento tesorería " + m.getMovimientoTesoreria().getFormulario().getDescripcion() + "-" + m.getMovimientoTesoreria().getNumeroFormulario() + " se guardó correctamente", "");
                }
            }

            productoFacturacionBean.setProducto(null);
            muestraReporte = false;

            if (nuevo) {
                nuevo();
            }

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("Error al guardar: " + ex);
        }
    }

    public void guardar(boolean nuevo, boolean imprimir) {

        guardar(nuevo);

        if (m.getId() != null) {
            imprimir("VT");
        }

    }

    public void eliminarItem(ItemMovimientoFacturacion nItem) {

        try {
            facturacionRN.eliminarItemProducto(m, nItem);
            JsfUtil.addWarningMessage("Se ha borrado el item " + (nItem.getProducto() == null ? "" : nItem.getProducto().getDescripcion()) + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible borrar el item " + (nItem.getProducto() == null ? "" : nItem.getProducto().getDescripcion()) + "");
        }
    }

    public void eliminarItemDetalle(ItemMovimientoFacturacion itemProducto, ItemMovimientoFacturacionDetalle itemDetalle) {

        try {
            facturacionRN.eliminarItemDetalle(m, itemProducto, itemDetalle);
            JsfUtil.addWarningMessage("Se ha borrado el item detalle de atributos " + (itemProducto.getProducto() != null ? itemProducto.getProducto().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + itemProducto.getProducto().getDescripcion() + " " + ex);
        }
    }

    public void eliminarItemKit(ItemMovimientoFacturacionKit itemKit) {

        try {
            facturacionRN.eliminarItemKit(itemKit);
            JsfUtil.addWarningMessage("Se ha borrado el item kit " + (itemKit.getProducto() != null ? itemKit.getProducto().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + itemKit.getProducto().getDescripcion() + " " + ex);
        }
    }

    public void seleccionar(MovimientoFacturacion m) {

        if (m == null) {
            return;
        }

        this.m = m;

        if (m.getMovimientoTesoreria() != null) {
            tesoreriaRN.cargarItemsMovimiento(m.getMovimientoTesoreria());
        }
    }

    public void verPendientes(CircuitoFacturacion c) {

        try {
            circuito = circuitoRN.iniciarCircuito(c.getCircom(), c.getCirapl(), CODFC, CODVT, CODST, CODCJ);

            seleccionaPendiente = (!c.getCircom().equals(c.getCirapl()));
            congelaPrecio = (circuito.getCongelaPrecio() != null && circuito.getCongelaPrecio().equals("S"));
            congelaCotizacion = (circuito.getCongelaCotizacion() != null && circuito.getCongelaCotizacion().equals("S"));

            m = null;

//            if (seleccionaPendiente) {
//                Wizard w = (Wizard) FacesContext.getCurrentInstance().getViewRoot().findComponent("fPendiente").findComponent("wizPendiente");
//                w.setStep("filtro");
//
            AccordionPanel a = (AccordionPanel) FacesContext.getCurrentInstance().getViewRoot().findComponent("formulario").findComponent("accordion");

            if (a != null) {
                a.setActiveIndex("0");
            }

            modoVista = "P";
//            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible seleccionar pendientes " + ex);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void verDetalleKit(ItemMovimientoFacturacion i) {

        item = i;

        if (m.getDeposito() != null && item != null && item.getItemsKit() != null) {

            item.getItemsKit().forEach(k -> {

                if (k.getProducto() != null) {
                    k.setStock(stockRN.getCantidadStockTotalByProducto(k.getProducto(), m.getDeposito()));
                }
            });
        }
    }

    public String procesoSeleccionPendientes(FlowEvent event) {

        if (event.getNewStep().equals("filtro")) {
            JsfUtil.addInfoMessage("Aplique los filtros que considere");
        }

        if (event.getNewStep().equals("pendiente_grupo")) {
            try {
//                if (m != null) {
//
//                    JsfUtil.addInfoMessage("El comprobante fue generado, no puede regresar al paso anterior");
//                    return event.getOldStep();
//                }

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

        try {
            cargarFiltroGrupo();

            movimientoPendiente = new PendienteFacturacionGrupo();

            String queryGrupo = reportFactory.generarQuerySQL(circuito.getReporteGrupo(), filtroGrupo);

            movimientosPendientes = facturacionRN.getPendienteGrupo(queryGrupo);
            itemsPendiente = new ArrayList<>();
            seleccionaTodo = false;

            if (movimientosPendientes != null && !movimientosPendientes.isEmpty()) {
                JsfUtil.addInfoMessage("Seleccione un comprobante para ver los pendientes");
            }
        } catch (JRException ex) {
            JsfUtil.addErrorMessage("Problemas con el reporte de grupo");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void seleccionarItemPendiente(PendienteFacturacionGrupo m, boolean muestraMensaje) {

        try {
            movimientoPendiente = m;
            cargarFiltroDetalle();
            String queryDetalle = reportFactory.generarQuerySQL(circuito.getReporteDetalle(), filtroDetalle);
            itemsPendiente = facturacionRN.getPendienteDetalle(queryDetalle);

//            System.err.println("queryDetalle " + queryDetalle );
            if (itemsPendiente.isEmpty() && muestraMensaje) {
                JsfUtil.addWarningMessage("No se han encontrado items pendientes");
            } else {
                indiceWizard = 2;
                seleccionaTodo = false;
            }
        } catch (JRException ex) {
            JsfUtil.addErrorMessage("Problemas con el reporte de detalle");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Seleccionar todos los items pendientes
    public void seleccionarTodo() {

        facturacionRN.seleccionarTodo(itemsPendiente, seleccionaTodo);
    }

    public void seleccionarItemPendiente(PendienteFacturacionDetalle p) {

        if (p.isSeleccionado()) {
            p.setCantidadSeleccionada(p.getCantidad());
        } else {
            p.setCantidadSeleccionada(0);
        }
    }

    public void modificarCantidadSeleccionada(PendienteFacturacionDetalle p) {

        p.setSeleccionado((p.getCantidadSeleccionada() > 0));

    }

    public void finalizarProcesoSeleccionPendiente() {

        try {

            m = facturacionRN.nuevoMovimiento(circuito, CODFC, SUCFC, CODVT, SUCVT, CODST, SUCST, CODCJ, SUCCJ, movimientoPendiente, itemsPendiente, congelaCotizacion);
            aplicarDatosPorDefecto();
            calcularImportes();

            PrimeFaces.current().ajax().addCallbackParam("todoOk", true);

            if (m.getCliente().getSoloContado().equals("S") || m.getCliente().getEntidadComodin().equals("S")) {
                solicitaDatos = true;
            }

            movimientoPendiente = null;
            itemsPendiente = null;
            modoVista = "D";
            seleccionaPendiente = false;

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al generar comprobante", "- " + ex);
            log.log(Level.SEVERE, "Error al generar comprobante", ex);
            PrimeFaces.current().ajax().addCallbackParam("todoOk", false);
        }
    }

    public void procesarCliente() {

        if (m != null && m.getCliente() != null) {

            try {
                facturacionRN.asignarCliente(m, m.getCliente());

                if (m.getCliente().getSoloContado().equals("S") || m.getCliente().getEntidadComodin().equals("S")) {
                    solicitaDatos = true;
                }

                if (m.getCliente().getEstado().getCodigo().equals("P")) {
                    JsfUtil.addWarningMessage("El cliente seleccionado es un potencial, deberá completar todos los datos al momento de guardar un comprobante de venta");
                }

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar cliente " + ex);
            }
        }
    }

    public void procesarRemitente() {

        if (m != null && m.getRemitenteCliente() != null) {

            try {
                facturacionRN.asignarRemitente(m, m.getRemitenteCliente());
                solicitaDatosRemitente = m.getRemitenteCliente().getSoloContado().equals("S");
            } catch (ExcepcionGeneralSistema ex) {
                JsfUtil.addErrorMessage("No es posible asingar remitente " + ex);
            }
        }
    }

    public void procesarDestinatario() {

        if (m != null && m.getDestinatarioCliente() != null) {

            try {
                facturacionRN.asignarDestinatario(m, m.getDestinatarioCliente());
                solicitaDatosDestinatario = m.getDestinatarioCliente().getSoloContado().equals("S");
            } catch (ExcepcionGeneralSistema ex) {

                JsfUtil.addErrorMessage("No es posible asignar destinatario " + ex);
            }
        }

    }

    public void procesarDireccionEntrega() {

        if (m != null && m.getDireccionEntrega() != null) {

            facturacionRN.asignarDireccionEntrega(m, m.getDireccionEntrega());
        }
    }

    public void procesarDireccionEntregaRemitente() {

        if (m != null && m.getDireccionEntrega() != null) {

            try {
                facturacionRN.asignarDireccionEntregaRemitente(m, m.getDireccionEntrega());
                facturacionRN.definirCliente(m);

            } catch (ExcepcionGeneralSistema ex) {

                JsfUtil.addErrorMessage("No es posible asignar dirección de entrega a remitente");
            }
        }
    }

    public void procesarDireccionEntregaDestinatario() {

        if (m != null && m.getDireccionEntrega() != null) {

            try {
                facturacionRN.asignarDireccionEntregaDestinatario(m, m.getDireccionEntrega());
                facturacionRN.definirCliente(m);
            } catch (ExcepcionGeneralSistema ex) {
                JsfUtil.addErrorMessage("No es posible asignar dirección de entrega a destinatario");
            }
        }
    }

    public void procesarLocalidad() {

        if (m != null && m.getLocalidad() != null) {

            m.setProvincia(m.getLocalidad().getProvincia());
        }
    }

    public void procesarLocalidadDlg() {

        if (m != null && localidadBean.getLocalidad() != null) {

            m.setLocalidad(localidadBean.getLocalidad());
            m.setProvincia(m.getLocalidad().getProvincia());
        }
    }

    public void procesarListaPrecios() {

        facturacionRN.asignarListaPrecios(m);
    }

    public void agregarConceptoValoresHaber() {

        for (ConceptoComprobante cc : m.getComprobanteTesoreria().getConceptos()) {

            if (cc.getConcepto().getCuentaTesoreria() != null) {

                if (cc.getConcepto().getCuentaTesoreria().getTipoCuenta().getCodigo().equals("2")) {

                    ItemMovimientoTesoreria id = new ItemMovimientoTesoreria();
                    id.setNroItem(m.getItemsValoresHaber().size() + 1);
                    id.setDebeHaber(cc.getDebeHaber());
                    //id.setMovimiento(m);
                    id.setConcepto(cc.getConcepto());
                    id.setMonedaSecundaria(m.getMonedaSecundaria());
                    id.setCotizacion(m.getCotizacion());
                    id.setCuentaTesoreria(cc.getConcepto().getCuentaTesoreria());

                    m.getItemsValoresHaber().add(id);
                }
            }
        }
    }

    public void procesarProducto() {

        if (productoFacturacionBean.getProducto() != null && m != null && item != null) {

            try {

                Producto producto = productoRN.getProducto(productoFacturacionBean.getProducto().getCodigo());
                facturacionRN.asignarProducto(item, producto);

            } catch (ExcepcionGeneralSistema ex) {

                JsfUtil.addErrorMessage("No es posible asignar producto al item " + ex);
            }
        }
    }

    public void preparoBusquedaProductos() {

        if (m != null && m.getItemsProducto() != null && !m.getItemsProducto().isEmpty()) {

            productoBean.getProductosSeleccionados().clear();

            m.getItemsProducto().forEach(i -> {

                productoBean.getProductosSeleccionados().add(i.getProducto());

            });
        }

    }

    public void procesarProductos() {

        if (productoBean.getProductosSeleccionados() != null && m != null) {

            try {

                for (Producto p : productoBean.getProductosSeleccionados()) {

                    if (m.getItemsProducto().isEmpty()) {

                        facturacionRN.agregarItem(m);
                        item = m.getItemsProducto().get(m.getItemsProducto().size() - 1);
                        facturacionRN.asignarProducto(item, p);
                    }

                    if (m.getItemsProducto()
                            .stream()
                            .filter(i -> i.getProducto() != null && i.getProducto().equals(p)).count() == 0) {

                        facturacionRN.agregarItem(m);
                        item = m.getItemsProducto().get(m.getItemsProducto().size() - 1);
                        facturacionRN.asignarProducto(item, p);

                    }
                }

            } catch (ExcepcionGeneralSistema ex) {

                JsfUtil.addErrorMessage("No es posible procesar los productos " + ex);
            }
        }
    }

    public void procesarProductoComplete() {

        if (productoFacturacionBean.getProducto() != null && m != null && item != null) {

            try {

                Producto producto = productoRN.getProducto(productoFacturacionBean.getProducto().getCodigo());
                facturacionRN.asignarProducto(item, producto);

            } catch (ExcepcionGeneralSistema ex) {

                JsfUtil.addErrorMessage("No es posible asignar producto al item " + ex);
            }
        }
    }

    public void procesarProductoKit() {

        if (m != null && item != null) {

            try {

                facturacionRN.procesarProductoKit(item);

            } catch (ExcepcionGeneralSistema ex) {

                JsfUtil.addErrorMessage("No es posible asignar producto al item kit " + ex);
            }
        }
    }

    public void setearStockPorProducto(ItemMovimientoFacturacion itemProducto, ItemMovimientoFacturacionDetalle itemDetall) {

        if (itemProducto.getProducto() == null) {
            JsfUtil.addWarningMessage("Seleccione un producto para ver el stock");
            return;
        }

        if (m.getDeposito() == null) {
            JsfUtil.addWarningMessage("Seleccione un depósito para ver el stock");
            return;
        }

        item = itemProducto;
        itemDetalle = itemDetall;

        consultaStock.verStockPorProductoDeposito(item.getProducto(), m.getDeposito());
    }

    public void procesarStock() {

        //Primero asignamos los atributos al detalle ya que es ahí donde se guardan los datos.
        if (consultaStock.getItemStock() != null && m != null && itemDetalle != null) {

            Stock s = consultaStock.getItemStock();

            item.setAtributo1("");
            item.setAtributo2("");
            item.setAtributo3("");
            item.setAtributo4("");
            item.setAtributo5("");
            item.setAtributo6("");
            item.setAtributo7("");

            itemDetalle.setAtributo1(s.getAtributo1());
            itemDetalle.setAtributo2(s.getAtributo2());
            itemDetalle.setAtributo3(s.getAtributo3());
            itemDetalle.setAtributo4(s.getAtributo4());
            itemDetalle.setAtributo5(s.getAtributo5());
            itemDetalle.setAtributo6(s.getAtributo6());
            itemDetalle.setAtributo7(s.getAtributo7());
            return;
        }

        if (consultaStock.getItemStock() != null && m != null && item != null) {

            Stock s = consultaStock.getItemStock();

            item.setAtributo1(s.getAtributo1());
            item.setAtributo2(s.getAtributo2());
            item.setAtributo3(s.getAtributo3());
            item.setAtributo4(s.getAtributo4());
            item.setAtributo5(s.getAtributo5());
            item.setAtributo6(s.getAtributo6());
            item.setAtributo7(s.getAtributo7());
        }
    }

    public List<DireccionEntregaEntidad> completeDireccionEntrega(String query) {
        try {
            String nroCuenta = "";
            if (m.getCliente() == null) {
                return new ArrayList<DireccionEntregaEntidad>();
            }
            nroCuenta = m.getCliente().getNrocta();

            return direccionEntregaRN.getListaByBusqueda(query, nroCuenta, false);
        } catch (Exception ex) {
            return new ArrayList<DireccionEntregaEntidad>();
        }
    }

    public List<Formulario> completeFormulario(String query) {
        try {

            if (circuito == null) {
                return null;
            }

            Comprobante comprobante = null;
            String puntoVenta = SUCFC;

            if (circuito.getActualizaFacturacion().equals("S") && CODFC != null) {

                comprobante = circuito.getComprobanteFacturacion();
                puntoVenta = SUCFC;

            } else if (circuito.getActualizaVenta().equals("S") && CODVT != null) {

                comprobante = circuito.getComprobanteVenta();
                puntoVenta = SUCFC;

            } else if (circuito.getActualizaStock().equals("S") && CODST != null) {

                comprobante = circuito.getComprobanteStock();
                puntoVenta = SUCST;
            }

            return formularioPorSituacionIVARN.getFormularioByComprobantePuntoVenta(comprobante, puntoVenta);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Formulario>();
        }
    }

    public void onChageFechaMovimiento(SelectEvent event) {
        m.setFechaVencimiento(m.getFechaMovimiento());
    }

    public void onChageFechaVencimiento(SelectEvent event) {

        calcularImportes();

    }

    public void onChangeCotizacion() {

        for (ItemMovimientoFacturacion ip : m.getItemsProducto()) {

            ip.setCotizacion(m.getCotizacion());
        }

        calcularImportes();
        productoFacturacionBean.setCotizacion(m.getCotizacion());

    }

    public void onSelectLocalidad(SelectEvent event) {

        Localidad cp = ((Localidad) event.getObject());
        m.setProvincia(cp.getProvincia());
    }

    public void onSelectLocalidadRemitente(SelectEvent event) {

        Localidad cp = ((Localidad) event.getObject());
        m.setRemitenteProvincia(cp.getProvincia());
    }

    public void onSelectLocalidadDestinatario(SelectEvent event) {

        Localidad cp = ((Localidad) event.getObject());
        m.setDestinatarioProvincia(cp.getProvincia());
    }

    public void modificarAtributo(ItemMovimientoFacturacion itemProducto) {
        facturacionRN.asignarProductoItemDetalle(itemProducto);
    }

    public void modificarCantidad(ItemMovimientoFacturacion itemProducto) {

        facturacionRN.actualizarCantidades(itemProducto);

    }

    public void modificarCantidadItemKit(ItemMovimientoFacturacionKit itemKit) {

        facturacionRN.actualizarCantidades(itemKit.getItemProducto());

    }

    public void calcularImportes() {

        facturacionRN.calcularImportes(m, "U", false);
    }

    public void calcularImportesConImpuesto() {

        facturacionRN.calcularImportes(m, "U", true);
    }

    public void calcularImportesTesoreria() {
        try {
            tesoreriaRN.calcularImportes(m.getMovimientoTesoreria());
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para calcular totales del comprobante de tesorería");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void imprimir(String modulo) {

        generarReporte(modulo);

        PrimeFaces.current().ajax().update("formulario");

        if (muestraReporte) {
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
        }
    }

    public void imprimir(MovimientoFacturacion movimiento, String modulo) {

        m = movimiento;
        imprimir(modulo);
    }

    public void generarReporte(String modulo) {

        try {

            Map parameters = new HashMap();

            if (m == null) {
                throw new ExcepcionGeneralSistema("No se puede imprimir - No existe movimiento seleccionado");
            }

            if (m.getPuntoVenta().getUnidadNegocio().getLogo() != null && !m.getPuntoVenta().getUnidadNegocio().getLogo().isEmpty()) {
                parameters.put("LOGO", FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getResourceAsStream("/resources/image/" + m.getPuntoVenta().getUnidadNegocio().getLogo()));
            }

            if (modulo.equals("FC") && m != null) {

                if (m.getFormulario().getReporte() == null) {
                    throw new ExcepcionGeneralSistema("No se puede imprimir - El formulario de facturación no tienen reporte asociado");
                }

                parameters.put("ID", m.getId());
                parameters.put("EN_LETRAS", "Son pesos " + JsfUtil.generarEnLetras(m.getImporteTotal()));
                parameters.put("CANT_COPIAS", m.getComprobante().getCopias());

                nombreArchivo = m.getFormulario().getCodigo() + "-" + m.getNumeroFormulario();
                reportFactory.exportReportToPdfFile(m.getFormulario().getReporte(), nombreArchivo, parameters);
            }

            if (modulo.equals("VT") && m.getMovimientoVenta() != null) {

                if (m.getMovimientoVenta().getFormulario().getReporte() == null) {
                    throw new ExcepcionGeneralSistema("No se puede imprimir - El formulario de venta no tienen reporte asociado");
                }

                parameters.put("ID", m.getMovimientoVenta().getId());
                parameters.put("CODIGO_BARRA", ventaRN.generarCodigoBarra(m.getMovimientoVenta()));
                parameters.put("EN_LETRAS", "Son pesos " + JsfUtil.generarEnLetras(m.getMovimientoVenta().getItemTotal().get(0).getImporte()));
                parameters.put("CANT_COPIAS", m.getMovimientoVenta().getComprobante().getCopias());

                nombreArchivo = m.getMovimientoVenta().getFormulario().getCodigo() + "-" + m.getMovimientoVenta().getNumeroFormulario();
                reportFactory.exportReportToPdfFile(m.getMovimientoVenta().getFormulario().getReporte(), nombreArchivo, parameters);
            }

            if (modulo.equals("ST") && m.getMovimientoStock() != null) {

                if (m.getMovimientoStock().getFormulario().getReporte() == null) {
                    throw new ExcepcionGeneralSistema("No se puede imprimir - El formulario de stock no tienen reporte asociado");
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

    public void preparoEnvioNotificaciones(MovimientoFacturacion movimiento) {

        m = movimiento;
        preparoEnvioNotificaciones();
    }

    public void preparoEnvioNotificaciones() {

        if (m == null) {
            JsfUtil.addWarningMessage("No hay comprobante seleccionado para enviar por e-mail");
            return;
        }

        solicitaEmail = true;
        emailEnvioComprobante = "";
        informacionAdicional = "";

        if (m.getCliente().getEmail() != null) {
            emailEnvioComprobante = m.getCliente().getEmail();
        }

        JsfUtil.addWarningMessage("Separe las direcciones de entrega con punto y coma(;) si desea enviar a más de un destinatario");

    }

    public void enviarNotificaciones() {

        try {

            if (aplicacionBean.getParametro().getEnviaNotificaciones() == 'S') {

//                if (m.getFormulario().getModfor().equals("ST")) {
//                    JsfUtil.addWarningMessage("Comprobante no habilitado para enviar por correo");
//                    return;
//                }
                muestraReporte = false;
                CorreoElectronico ce = null;

                if (m.getFormulario().getModfor().equals("FC")) {
                    generarReporte("FC");
                    ce = notificacionesFacturacionRN.generarCorreoElectronicoCliente(m, emailEnvioComprobante, informacionAdicional);
                }

                if (m.getFormulario().getModfor().equals("VT")) {
                    generarReporte("VT");
                    ce = notificacionesVentaRN.generarCorreoElectronicoCliente(m.getMovimientoVenta(), emailEnvioComprobante, informacionAdicional);
                }

                if (ce == null) {
                    JsfUtil.addWarningMessage("No se ha generado el correo electrónico");
                    return;
                }

                String pathPDF = reportFactory.getPathTemporales() + nombreArchivo + ".pdf";
                ce.setPathArchivo(pathPDF);
                ce.setMovimientoFacturacion(m);

                List<CorreoElectronico> correos = new ArrayList<>();
                correos.add(ce);

                //Enviamos los correos con un nuevo hilo...
                mailFactoryBean.enviarCorreosElectronicos(correos);

                m.getCorreos().add(ce);

                JsfUtil.addInfoMessage("El comprobante fue enviado existosamente a " + emailEnvioComprobante);

            } else {
                JsfUtil.addWarningMessage("No tiene permiso para enviar notificaciones");
            }

            solicitaEmail = false;
            PrimeFaces.current().executeScript("PF('dlgEnvioComprobante').hide()");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "enviarNotificaciones", ex);
            JsfUtil.addErrorMessage("No es posible enviar la notificación", "enviarNotificaciones: " + ex);
            solicitaEmail = true;
        }
    }

    public void onFleteChange() {

        try {
            facturacionRN.definirCliente(m);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible definir cliente según flete");
        }

    }

    public void buscar() {

        if (!validarParametros()) {
            return;
        }
        cargarFiltroBusqueda();

        movimientos = facturacionRN.getListaByBusqueda(filtro, mostrarSoloPendiente, cantidadRegistros);

        if (movimientos == null || movimientos.isEmpty()) {
            JsfUtil.addWarningMessage("No se han encontrado movimientos");
        }
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        // Filtramos movimientos de acuerdo a la sucursal asignada al usuario
        String sFiltroSuc = usuarioSessionBean.getStringInSucursal();

        if (sFiltroSuc != null && !sFiltroSuc.isEmpty()) {
            filtro.put("sucursal.codigo IN", "(" + sFiltroSuc + ")");
        }

        filtro.put("circuito.circom =", "'" + CIRCOM + "'");

        if (comprobante != null) {

            filtro.put("comprobante.modulo = ", "'" + comprobante.getModulo() + "'");
            filtro.put("comprobante.codigo = ", "'" + comprobante.getCodigo() + "'");
        }

        if (sucursal != null) {
            filtro.put("sucursal.codigo = ", "'" + sucursal.getCodigo() + "'");
        }

        if (canalVenta != null) {
            filtro.put("canalVenta.codigo = ", "'" + canalVenta.getCodigo() + "'");
        }

        if (puntoVenta != null) {
            filtro.put("puntoVenta.codigo = ", "'" + puntoVenta.getCodigo() + "'");
        }

        if (cliente != null) {

            filtro.put("cliente.nrocta =", "'" + cliente.getNrocta() + "'");
        }

        if (razonSocial != null && !razonSocial.isEmpty()) {

            filtro.put("razonSocial LIKE ", "'%" + razonSocial + "%'");
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

//        System.err.println("filtro busqueda " + filtro);
    }

    public void cargarFiltroGrupo() {

        filtroGrupo.clear();

        filtroGrupo.put("CIRCOM", circuito.getCirapl());
        filtroGrupo.put("CONCOT", (congelaCotizacion ? "S" : "N"));

        if (sucursal != null) {
            filtroGrupo.put("CODSUC", sucursal.getCodigo());
        } else {
            filtroGrupo.put("CODSUC", "");
        }

        if (canalVenta != null) {
            filtroGrupo.put("CANVTA", canalVenta.getCodigo());
        } else {
            filtroGrupo.put("CANVTA", "");
        }

        if (cliente != null) {
            filtroGrupo.put("NROCTA", cliente.getNrocta());
        } else {
            filtroGrupo.put("NROCTA", "");
        }

        if (numeroFormularioDesde != null) {
            filtroGrupo.put("NRODES", numeroFormularioDesde);
        } else {
            filtroGrupo.put("NRODES", "0");
        }

        if (numeroFormularioHasta != null) {
            filtroGrupo.put("NROHAS", numeroFormularioHasta);
        } else {
            filtroGrupo.put("NROHAS", "999999");
        }

        if (fechaMovimientoDesde != null) {
            filtroGrupo.put("FCHDES", fechaMovimientoDesde);
        } else {
            filtroGrupo.put("FCHDES", new Date(0));
        }

        if (fechaMovimientoHasta != null) {
            filtroGrupo.put("FCHHAS", fechaMovimientoHasta);
        } else {
            filtroGrupo.put("FCHHAS", new Date(4102518877364L));
        }

        if (circuito.getComprobanteFacturacion() != null) {

            filtroGrupo.put("MONREG", circuito.getComprobanteFacturacion().getMonedaRegistracion().getCodigo());

        } else if (circuito.getComprobanteVenta() != null) {

            filtroGrupo.put("MONREG", circuito.getComprobanteVenta().getMonedaRegistracion().getCodigo());

        } else if (circuito.getComprobanteStock() != null) {

            filtroGrupo.put("MONREG", circuito.getComprobanteStock().getMonedaRegistracion().getCodigo());
        }

        if (vendedor != null) {
            filtroGrupo.put("VNDDOR", vendedor.getCodigo());
        } else {
            filtroGrupo.put("VNDDOR", "");
        }

        if (repartidor != null) {
            filtroGrupo.put("REPDOR", repartidor.getCodigo());
        } else {
            filtroGrupo.put("REPDOR", "");
        }

        if (zona != null) {
            filtroGrupo.put("CODZON", zona.getCodigo());
        } else {
            filtroGrupo.put("CODZON", "");
        }

    }

    public void cargarFiltroDetalle() {

        filtroDetalle.clear();

        if (movimientoPendiente == null) {
            return;
        }

        filtroDetalle.put("CIRCOM", movimientoPendiente.getCircom());
        filtroDetalle.put("CODSUC", movimientoPendiente.getCodsuc());
        filtroDetalle.put("CANVTA", movimientoPendiente.getCanvta());

        filtroDetalle.put("NROCTA", movimientoPendiente.getNrocta());
        filtroDetalle.put("MONREG", movimientoPendiente.getMonreg());
        filtroDetalle.put("CNDPAG", movimientoPendiente.getCndpag());

        filtroDetalle.put("CONCOT", (congelaCotizacion ? "S" : "N"));
        filtroDetalle.put("COTIZA", (congelaCotizacion ? movimientoPendiente.getCotizacion() : 1));

        if (numeroFormularioDesde != null) {
            filtroDetalle.put("NRODES", numeroFormularioDesde);
        } else {
            filtroDetalle.put("NRODES", "0");
        }

        if (numeroFormularioHasta != null) {
            filtroDetalle.put("NROHAS", numeroFormularioHasta);
        } else {
            filtroDetalle.put("NROHAS", "999999");
        }

        if (fechaMovimientoDesde != null) {
            filtroDetalle.put("FCHDES", fechaMovimientoDesde);
        } else {
            filtroDetalle.put("FCHDES", new Date(0));
        }

        if (fechaMovimientoHasta != null) {
            filtroDetalle.put("FCHHAS", fechaMovimientoHasta);
        } else {
            filtroDetalle.put("FCHHAS", new Date(4102518877364L));
        }
    }

    public void aplicarAccionEnLotes() {

        if (movimientos == null) {
            JsfUtil.addWarningMessage("No se encontraron movimientos");
            return;
        }

        if (accionEnLote == null || accionEnLote.isEmpty()) {
            JsfUtil.addWarningMessage("Ningúna acción seleccionada");
            return;
        }

        movimientos.stream()
                .filter(i -> i.isSeleccionado())
                .collect(Collectors.toList()).forEach((me) -> {
            try {

                if (accionEnLote.equals("A")) {
//                    facturacionRN.generarComprobanteReincripcion(me);
                }

                if (accionEnLote.equals("I")) {
//                    educacionRN.revertirMovimiento(me);
                }

                if (accionEnLote.equals("E")) {
//                    educacionRN.revertirMovimiento(me);
                }

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("Problemas para generar acción en lote");
            }
        });

    }

    public boolean validarParametros() {

        if (comprobante == null) {
            JsfUtil.addWarningMessage("Comprobante vacío no es posible buscar");
            return false;
        }

        if (puntoVenta == null) {
            JsfUtil.addWarningMessage("Punto de venta vacío no es posible buscar");
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

    public void limpiarFiltroBusqueda() {

        cliente = null;
        vendedor = null;
        repartidor = null;
        canalVenta = null;
        zona = null;

        razonSocial = "";
//        formulario = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        muestraReporte = false;
        solicitaEmail = false;
        movimientos = null;
        indiceWizard = 0;

        buscar();
    }

    public void limpiarFiltroPendiente() {

        cliente = null;
        vendedor = null;
        repartidor = null;
        canalVenta = null;
        zona = null;

        razonSocial = "";
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        muestraReporte = false;
        solicitaEmail = false;
        movimientos = null;
        indiceWizard = 0;

    }

    public void seleccionarMovimiento(MovimientoFacturacion mSel) {

        try {
            m = facturacionRN.seleccionarMovimiento(mSel, circuito);

            if (m.getCliente().getSoloContado().equals("S") || m.getCliente().getEntidadComodin().equals("S")) {
                solicitaDatos = true;
            }

            modoVista = "D";
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible seleccionar movimiento " + ex);
        }
    }

    public void agregarConceptoValoresDebe() {

        for (ConceptoComprobante cc : m.getComprobanteTesoreria().getConceptos()) {

            if (cc.getConcepto().getCuentaTesoreria() != null) {

                if (cc.getConcepto().getCuentaTesoreria().getTipoCuenta().getCodigo().equals("2")) {

                    ItemMovimientoTesoreria id = new ItemMovimientoTesoreria();
                    id.setNroItem(m.getItemsValoresDebe().size() + 1);
                    id.setDebeHaber(cc.getDebeHaber());
                    id.setConcepto(cc.getConcepto());
                    id.setMonedaSecundaria(m.getMonedaSecundaria());
                    id.setCotizacion(m.getCotizacion());
                    id.setCuentaTesoreria(cc.getConcepto().getCuentaTesoreria());

                    m.getItemsValoresDebe().add(id);
                }
            }
        }
    }

    /**
     *
     * @throws Exception El código de barra puede recibir diferentes datos
     * separados por espacio.
     *
     * 1 - Codigo de producto 2 - Cantidad 3 - Precio
     *
     * Ej: 0001 2 155.30
     *
     */
    public void procesarCodigoBarra() throws Exception {

        ItemMovimientoFacturacion ip = null;

        try {

            if (codigoBarra == null || codigoBarra.isEmpty()) {
                return;
            }

            String[] datos = codigoBarra.split(" ");

            Producto producto = productoRN.getProductoByCodigoBarra(datos[0]);

            if (producto == null) {
                JsfUtil.addErrorMessage("No se ha encontrado el producto");
                return;
            }
            ip = facturacionRN.nuevoItemProducto(m);

            m.getItemsProducto().add(ip);
            facturacionRN.asignarProducto(ip, producto);

            if (datos.length >= 2 && datos[1] != null) {
                ip.setCantidad(new BigDecimal(datos[1]));
            } else {
                ip.setCantidad(BigDecimal.ONE);
            }

            if (datos.length == 3 && datos[2] != null) {
                ip.setPrecioConImpuesto(new BigDecimal(datos[2]));
            }

            calcularImportes();

            codigoBarra = "";

            JsfUtil.addInfoMessage("Producto agregado");
        } catch (ExcepcionGeneralSistema ex) {
            facturacionRN.eliminarItemProducto(m, ip);
            JsfUtil.addErrorMessage("No es posible agregar el producto " + ex);
        }

    }

    public void procesarCodigoBarraPendiente() throws Exception {

        if (itemsPendiente == null || itemsPendiente.isEmpty()) {
            return;
        }

        if (codigoBarraPendiente == null || codigoBarraPendiente.isEmpty()) {
            return;
        }

        String[] datos = codigoBarraPendiente.split(" ");

        Producto producto = productoRN.getProductoByCodigoBarra(datos[0]);

        if (producto == null) {
            JsfUtil.addErrorMessage("No se ha encontrado el producto");
            return;
        }

        for (PendienteFacturacionDetalle p : itemsPendiente) {

            if (!p.isSeleccionado() && p.getProducto().equals(producto)) {

                if (datos.length >= 2 && datos[1] != null) {
                    p.setCantidadSeleccionada(Double.valueOf(datos[1]));
                } else {
                    p.setCantidadSeleccionada(p.getCantidad());
                }

                if (p.getCantidadSeleccionada() > p.getCantidad()) {
                    p.setCantidadSeleccionada(p.getCantidad());
                }

                p.setSeleccionado(p.getCantidadSeleccionada() > 0);
                itemPendiente = p;
            }
        }

        if (itemPendiente != null
                && circuito.getDialogoCantidadSeleccionPendiente().equals("S")) {

            PrimeFaces.current().ajax().update("dlgCantidad");
            PrimeFaces.current().executeScript("PF('dlgCantidad').show()");
        }

        codigoBarraPendiente = "";
        JsfUtil.addInfoMessage("Producto seleccionado");

    }

    private void aplicarDatosPorDefecto() {

    }

    public void asignarTotal(ItemMovimientoTesoreria item) {

        item.setImporte(m.getImporteTotal());
        item.setImporteMonedaSecundaria(m.getImporteTotal().divide(m.getCotizacion(), 4, RoundingMode.HALF_UP));
    }

    public void agregarConcepto(List<ItemMovimientoTesoreria> items, ItemMovimientoTesoreria itemCopiar) {

        try {
            ItemMovimientoTesoreria item = tesoreriaRN.nuevoItemMovimiento(m.getMovimientoTesoreria(), itemCopiar);
            items.add(itemCopiar.getNroItem(), item);
            tesoreriaRN.reorganizarNroItem(m.getMovimientoTesoreria());
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar concepto " + ex);
        }
    }

    public void buscarValores(ItemMovimientoTesoreria i) {
        consultaValoresBean.init(i.getCuentaTesoreria().getCodigo());
    }

    public void procesarValor(ItemMovimientoTesoreria i) {

        if (consultaValoresBean.getItemPendiente() != null && m != null) {
            try {

                tesoreriaRN.procesarValor(i, consultaValoresBean.getItemPendiente());
                consultaValoresBean.verSaldosPendiente();

            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void calcularVuelto(ItemMovimientoTesoreria item) {

        item.setImporte(m.getImporteTotal());
    }

    private MeterGaugeChartModel initMeterGaugeModel() {
        List<Number> intervals = new ArrayList<Number>() {
            {
                add(25);
                add(50);
                add(75);
                add(100);
            }
        };

        MeterGaugeChartModel meterGaugeModel1 = new MeterGaugeChartModel(m.getImporteTotal(), intervals);
        meterGaugeModel1.setTitle("Contribución Marginal");
        meterGaugeModel1.setGaugeLabel("$");
        meterGaugeModel1.setGaugeLabelPosition("bottom");

        return new MeterGaugeChartModel(m.getImporteTotal(), intervals);
    }

    //------------------------------------------------------------------------------------------------------------
    public String getCIRAPL() {
        return CIRAPL;
    }

    public void setCIRAPL(String CIRAPL) {
        this.CIRAPL = CIRAPL;
    }

    public String getCIRCOM() {
        return CIRCOM;
    }

    public void setCIRCOM(String CIRCOM) {
        this.CIRCOM = CIRCOM;
    }

    public String getCODFC() {
        return CODFC;
    }

    public void setCODFC(String CODFC) {
        this.CODFC = CODFC;
    }

    public String getCODVT() {
        return CODVT;
    }

    public void setCODVT(String CODVT) {
        this.CODVT = CODVT;
    }

    public String getCODST() {
        return CODST;
    }

    public void setCODST(String CODST) {
        this.CODST = CODST;
    }

    public String getCODCJ() {
        return CODCJ;
    }

    public void setCODCJ(String CODCJ) {
        this.CODCJ = CODCJ;
    }

    public ProductoFacturacionBean getProductoFacturacionBean() {
        return productoFacturacionBean;
    }

    public void setProductoFacturacionBean(ProductoFacturacionBean productoFacturacionBean) {
        this.productoFacturacionBean = productoFacturacionBean;
    }

    public List<PendienteFacturacionDetalle> getItemsPendiente() {
        return itemsPendiente;
    }

    public void setItemsPendiente(List<PendienteFacturacionDetalle> itemsPendiente) {
        this.itemsPendiente = itemsPendiente;
    }

    public MovimientoFacturacion getM() {
        return m;
    }

    public void setM(MovimientoFacturacion m) {
        this.m = m;
    }

    public PendienteFacturacionGrupo getMovimientoPendiente() {
        return movimientoPendiente;
    }

    public void setMovimientoPendiente(PendienteFacturacionGrupo movimientoPendiente) {
        this.movimientoPendiente = movimientoPendiente;
    }

    public List<PendienteFacturacionGrupo> getMovimientosPendientes() {
        return movimientosPendientes;
    }

    public void setMovimientosPendientes(List<PendienteFacturacionGrupo> movimientosPendientes) {
        this.movimientosPendientes = movimientosPendientes;
    }

    public boolean isSolicitaDatos() {
        return solicitaDatos;
    }

    public void setSolicitaDatos(boolean solicitaDatos) {
        this.solicitaDatos = solicitaDatos;
    }

    public boolean isSolicitaDatosRemitente() {
        return solicitaDatosRemitente;
    }

    public void setSolicitaDatosRemitente(boolean solicitaDatosRemitente) {
        this.solicitaDatosRemitente = solicitaDatosRemitente;
    }

    public boolean isSolicitaDatosDestinatario() {
        return solicitaDatosDestinatario;
    }

    public void setSolicitaDatosDestinatario(boolean solicitaDatosDestinatario) {
        this.solicitaDatosDestinatario = solicitaDatosDestinatario;
    }

    public String getSUCURS() {
        return SUCURS;
    }

    public void setSUCURS(String SUCURS) {
        this.SUCURS = SUCURS;
    }

    @Override
    public boolean isDetalleVacio() {

        if (m == null) {
            detalleVacio = true;
            return detalleVacio;
        }

        if (m.getItemsProducto() != null) {

            detalleVacio = (m.getItemsProducto().size() <= 0);

//            if (m.getCircuito().getCircom().equals(m.getCircuito().getCirapl())) {
//                detalleVacio = (m.getItemsProducto().size() <= 1);
//            } else {
//                detalleVacio = (m.getItemsProducto().size() <= 0);
//            }
        } else {
            detalleVacio = true;
        }

        return detalleVacio;
    }

    public void setDetalleVacio(boolean detalleVacio) {
        this.detalleVacio = detalleVacio;
    }

    public MailFactory getMailFactoryBean() {
        return mailFactoryBean;
    }

    public void setMailFactoryBean(MailFactory mailFactoryBean) {
        this.mailFactoryBean = mailFactoryBean;
    }

    public List<MovimientoFacturacion> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoFacturacion> movimientos) {
        this.movimientos = movimientos;
    }

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
    }

    public ConsultaValoresBean getConsultaValoresBean() {
        return consultaValoresBean;
    }

    public void setConsultaValoresBean(ConsultaValoresBean consultaValoresBean) {
        this.consultaValoresBean = consultaValoresBean;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public ReportFactory getReportFactory() {
        return reportFactory;
    }

    public void setReportFactory(ReportFactory reportFactory) {
        this.reportFactory = reportFactory;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public ConsultaStock getConsultaStock() {
        return consultaStock;
    }

    public void setConsultaStock(ConsultaStock consultaStock) {
        this.consultaStock = consultaStock;
    }

    public ItemMovimientoFacturacion getItem() {
        return item;
    }

    public void setItem(ItemMovimientoFacturacion item) {
        this.item = item;
    }

    public boolean isMantieneCotizacionOriginal() {
        return mantieneCotizacionOriginal;
    }

    public void setMantieneCotizacionOriginal(boolean mantieneCotizacionOriginal) {
        this.mantieneCotizacionOriginal = mantieneCotizacionOriginal;
    }

    public String getSUCST() {
        return SUCST;
    }

    public void setSUCST(String SUCST) {
        this.SUCST = SUCST;
    }

    public String getNROCTA() {
        return NROCTA;
    }

    public void setNROCTA(String NROCTA) {
        this.NROCTA = NROCTA;
    }

    public String getSUCFC() {
        return SUCFC;
    }

    public void setSUCFC(String SUCFC) {
        this.SUCFC = SUCFC;
    }

    public String getSUCVT() {
        return SUCVT;
    }

    public void setSUCVT(String SUCVT) {
        this.SUCVT = SUCVT;
    }

    public String getSUCCJ() {
        return SUCCJ;
    }

    public void setSUCCJ(String SUCCJ) {
        this.SUCCJ = SUCCJ;
    }

    public CircuitoFacturacion getCircuito() {
        return circuito;
    }

    public void setCircuito(CircuitoFacturacion circuito) {
        this.circuito = circuito;
    }

    public ItemMovimientoFacturacionDetalle getItemDetalle() {
        return itemDetalle;
    }

    public void setItemDetalle(ItemMovimientoFacturacionDetalle itemDetalle) {
        this.itemDetalle = itemDetalle;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public LocalidadBean getLocalidadBean() {
        return localidadBean;
    }

    public void setLocalidadBean(LocalidadBean localidadBean) {
        this.localidadBean = localidadBean;
    }

    public boolean isCongelaPrecio() {
        return congelaPrecio;
    }

    public void setCongelaPrecio(boolean congelaPrecio) {
        this.congelaPrecio = congelaPrecio;
    }

    public boolean isCongelaCotizacion() {
        return congelaCotizacion;
    }

    public void setCongelaCotizacion(boolean congelaCotizacion) {
        this.congelaCotizacion = congelaCotizacion;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public boolean isExisteProducto() {
        return existeProducto;
    }

    public void setExisteProducto(boolean existeProducto) {
        this.existeProducto = existeProducto;
    }

    public ProductoBean getProductoBean() {
        return productoBean;
    }

    public void setProductoBean(ProductoBean productoBean) {
        this.productoBean = productoBean;
    }

    public CanalVenta getCanalVenta() {
        return canalVenta;
    }

    public void setCanalVenta(CanalVenta canalVenta) {
        this.canalVenta = canalVenta;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public String getCodigoBarraPendiente() {
        return codigoBarraPendiente;
    }

    public void setCodigoBarraPendiente(String codigoBarraPendiente) {
        this.codigoBarraPendiente = codigoBarraPendiente;
    }

    public PendienteFacturacionDetalle getItemPendiente() {
        return itemPendiente;
    }

    public void setItemPendiente(PendienteFacturacionDetalle itemPendiente) {
        this.itemPendiente = itemPendiente;
    }

}
