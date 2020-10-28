/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.compra.web;

import bs.administracion.modelo.CorreoElectronico;
import bs.compra.modelo.CircuitoCompra;
import bs.compra.modelo.ItemDetalleItemMovimientoCompra;
import bs.compra.modelo.ItemMovimientoCompra;
import bs.compra.modelo.MovimientoCompra;
import bs.compra.rn.CircuitoCompraRN;
import bs.compra.rn.CompraRN;
import bs.compra.vista.PendienteCompraDetalle;
import bs.compra.vista.PendienteCompraGrupo;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.rn.EntidadRN;
import bs.entidad.web.DireccionEntregaBean;
import bs.entidad.web.TransporteBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.FormularioPorSituacionIVARN;
import bs.global.rn.PuntoVentaRN;
import bs.global.util.JsfUtil;
import bs.global.util.ReportFactory;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.MailFactory;
import bs.proveedores.web.CompradorBean;
import bs.proveedores.web.CondicionPagoProveedorBean;
import bs.proveedores.web.ListaPrecioCostoBean;
import bs.seguridad.web.UsuarioSessionBean;
import bs.stock.modelo.Producto;
import bs.stock.modelo.Stock;
import bs.stock.rn.ProductoRN;
import bs.stock.web.ProductoBean;
import bs.stock.web.informe.ConsultaStock;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.tesoreria.modelo.ItemSaldoPendienteCuentaTesoreria;
import bs.tesoreria.rn.MovimientoTesoreriaRN;
import bs.tesoreria.web.informe.ConsultaValoresBean;
import java.io.Serializable;
import java.math.RoundingMode;
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
import javax.inject.Inject;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.PrimeFaces;
import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.event.FlowEvent;

/**
 * @author ctrosch
 */
@ManagedBean
@ViewScoped
public class MovimientoCompraBean extends GenericBean implements Serializable {

    @EJB
    protected CompraRN compraRN;
    protected @EJB
    MovimientoTesoreriaRN tesoreriaRN;
    @EJB
    protected CircuitoCompraRN circuitoRN;
    @EJB
    protected PuntoVentaRN puntoVentaRN;
    @EJB
    protected FormularioPorSituacionIVARN formularioPorSituacionIVARN;
    @EJB
    protected EntidadRN entidadRN;
    @EJB
    private MailFactory mailFactoryBean;
    protected @EJB
    ProductoRN productoRN;

    //Datos inicializacion registracion
    protected String CIRCOM = "";
    protected String CIRAPL = "";

//    protected String MODCO = "";
    protected String CODCO = "";
    protected String SUCCO = "";

//    protected String MODPV = "";
    protected String CODPV = "";
    protected String SUCPV = "";

//    protected String MODCJ = "";
    protected String CODCJ = "";
    protected String SUCCJ = "";

//    protected String MODST = "";
    protected String CODST = "";
    protected String SUCST = "";

    protected String NROCTA = "";

    //Datos para generar movimientos aplicados
    protected List<PendienteCompraGrupo> movimientosPendientes;
    protected List<PendienteCompraDetalle> itemsPendiente;
    protected PendienteCompraGrupo movimientoPendiente;
    protected PendienteCompraDetalle itemPendiente;

    protected boolean seleccionaComprobante;

    // VARIABLES PARA BUSQUEDA DE COMPROBANTES
    protected EntidadComercial proveedor;
    protected String congelaPrecio;
    protected boolean congelaCotizacion;
    protected List<MovimientoCompra> movimientos;
    protected boolean mantieneCotizacionOriginal;
    //--------------------------------------------------

    private boolean solicitaDatos;
    private String codigoBarra;
    private String codigoBarraPendiente;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    @ManagedProperty(value = "#{direccionEntregaBean}")
    protected DireccionEntregaBean direccionEntregaBean;

    @ManagedProperty(value = "#{formularioCompraBean}")
    protected FormularioCompraBean formularioCompraBean;

    @ManagedProperty(value = "#{compradorBean}")
    protected CompradorBean compradorBean;

    @ManagedProperty(value = "#{condicionPagoProveedorBean}")
    protected CondicionPagoProveedorBean condicionPagoProveedorBean;

    @ManagedProperty(value = "#{listaPrecioCostoBean}")
    protected ListaPrecioCostoBean listaPrecioCostoBean;

    @ManagedProperty(value = "#{transporteBean}")
    protected TransporteBean transporteBean;

    @ManagedProperty(value = "#{consultaStock}")
    protected ConsultaStock consultaStock;

    @ManagedProperty(value = "#{consultaValoresBean}")
    protected ConsultaValoresBean consultaValoresBean;

    protected MovimientoCompra m;
    protected ItemMovimientoCompra item;
    protected ItemDetalleItemMovimientoCompra itemDetalle;
    protected MovimientoCompra mReversion;
    protected CircuitoCompra circuito;

    protected PuntoVenta puntoVenta;
    protected PuntoVenta puntoVentaStock;

    public MovimientoCompraBean() {

        titulo = "Movimiento de Compras";
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

            movimientoPendiente = new PendienteCompraGrupo();
            movimientosPendientes = null;
            itemsPendiente = new ArrayList<>();

            circuito = circuitoRN.iniciarCircuito(CIRCOM, CIRAPL, CODCO, CODPV, CODST, CODCJ);
            seleccionaPendiente = (!CIRCOM.equals(CIRAPL));
            congelaPrecio = circuito.getCongelaPrecio();
            congelaCotizacion = (circuito.getCongelaCotizacion() != null && circuito.getCongelaCotizacion().equals("S"));

            m = compraRN.nuevoMovimiento(circuito, CODCO, SUCCO, CODPV, SUCPV, CODST, SUCST, CODCJ, SUCCJ);

            if (NROCTA != null && !NROCTA.isEmpty()) {

                m.setProveedor(entidadRN.getEntidad(NROCTA));
                procesarProveedor();
            }

            comprobante = m.getComprobante();
            puntoVenta = m.getPuntoVenta();
            proveedor = m.getProveedor();

            modoVista = (seleccionaPendiente ? "P" : "D");

        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage("iniciarMovimiento: " + ex);
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
            productoBean.setProducto(null);
            //Cargarmos un nuevo item en blanco
            compraRN.agregarItem(m);

            item = m.getItemsProducto().get(m.getItemsProducto().size() - 1);
            PrimeFaces.current().executeScript("PF('dlgProducto').show()");

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("No es posible agregar un nuevo item " + ex);
        }
    }

    public void nuevoItemDetalle(ItemMovimientoCompra itemProducto) {

        try {

            compraRN.nuevoItemDetalle(itemProducto);

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("No es posible agregar un nuevo item detalle de atributos " + ex);
        }
    }

    public void guardar(boolean nuevo) {

        try {
            m = compraRN.guardar(m);

            if (m.getComprobante().getModulo().equals("CO")) {
                JsfUtil.addInfoMessage("El documento " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            } else {

                if (m.getMovimientoProveedor() != null) {
                    JsfUtil.addInfoMessage("El documento de proveedor " + m.getMovimientoProveedor().getFormulario().getDescripcion() + "-" + m.getMovimientoProveedor().getNumeroFormulario() + " se guardó correctamente", "");
                }

                if (m.getMovimientoStock() != null) {
                    JsfUtil.addInfoMessage("El documento de stock " + m.getMovimientoStock().getFormulario().getDescripcion() + "-" + m.getMovimientoStock().getNumeroFormulario() + " se guardó correctamente", "");
                }

                if (m.getMovimientoTesoreria() != null) {
                    JsfUtil.addInfoMessage("El documento de tesorería " + m.getMovimientoTesoreria().getFormulario().getDescripcion() + "-" + m.getMovimientoTesoreria().getNumeroFormulario() + " se guardó correctamente", "");
                }
            }

            productoBean.setProducto(null);
            muestraReporte = false;

            if (nuevo) {
                nuevo();
            }

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("guardar: " + ex);
//            ex.printStackTrace();
        }
    }

    public void guardar(boolean nuevo, boolean imprimir) {

        guardar(nuevo);

        if (m.getId() != null && imprimir) {
            imprimir("PV");
        }

    }

    public void eliminarItem(ItemMovimientoCompra nItem) {

        try {
            compraRN.eliminarItemProducto(m, nItem);
            JsfUtil.addWarningMessage("Se ha borrado el item " + (nItem.getProducto() != null ? nItem.getProducto().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + nItem.getProducto().getDescripcion() + " " + ex);
        }
    }

    public void eliminarItemDetalle(ItemMovimientoCompra itemProducto, ItemDetalleItemMovimientoCompra itemDetalle) {

        try {
            compraRN.eliminarItemDetalle(m, itemProducto, itemDetalle);
            JsfUtil.addWarningMessage("Se ha borrado el item detalle de atributos " + (itemProducto.getProducto() != null ? itemProducto.getProducto().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + itemProducto.getProducto().getDescripcion() + " " + ex);
        }
    }

    public void seleccionar(MovimientoCompra m) {
        if (m == null) {
            return;
        }

        this.m = m;
    }

    public void verPendientes(CircuitoCompra c) {

        try {
            circuito = circuitoRN.iniciarCircuito(c.getCircom(), c.getCirapl(), CODCO, CODPV, CODST, CODCJ);

            seleccionaPendiente = (!c.getCircom().equals(c.getCirapl()));
            congelaPrecio = circuito.getCongelaPrecio();
            congelaCotizacion = (circuito.getCongelaCotizacion() != null && circuito.getCongelaCotizacion().equals("S"));

            m = null;

//            if (seleccionaPendiente) {
//                Wizard w = (Wizard) FacesContext.getCurrentInstance().getViewRoot().findComponent("fPendiente").findComponent("wizPendiente");
//                w.setStep("filtro");
//
//                AccordionPanel a = (AccordionPanel) FacesContext.getCurrentInstance().getViewRoot().findComponent("fPendiente").findComponent("accordion");
//                a.setActiveIndex("0");
//            }
            AccordionPanel a = (AccordionPanel) FacesContext.getCurrentInstance().getViewRoot().findComponent("formulario").findComponent("accordion");

            if (a != null) {
                a.setActiveIndex("0");
            }

            modoVista = "P";

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible seleccionar pendientes " + ex);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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

            movimientoPendiente = new PendienteCompraGrupo();

            String queryGrupo = reportFactory.generarQuerySQL(circuito.getReporteGrupo(), filtroGrupo);
            movimientosPendientes = compraRN.getPendienteGrupo(queryGrupo);

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

    public void seleccionarItemPendiente(PendienteCompraGrupo m, boolean muestraMensaje) {

        try {
            movimientoPendiente = m;
            cargarFiltroDetalle();
            String queryDetalle = reportFactory.generarQuerySQL(circuito.getReporteDetalle(), filtroDetalle);
            itemsPendiente = compraRN.getPendienteDetalle(queryDetalle);

            System.err.println("queryDetalle " + queryDetalle);

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

        compraRN.seleccionarTodo(itemsPendiente, seleccionaTodo);
    }

    public void seleccionarItemPendiente(PendienteCompraDetalle p) {

        if (p.isSeleccionado()) {
            p.setCantidadSeleccionada(p.getCantidad());
        } else {
            p.setCantidadSeleccionada(0);
        }
    }

    public void modificarCantidadSeleccionada(PendienteCompraDetalle p) {

        p.setSeleccionado((p.getCantidadSeleccionada() > 0));

    }

    public void finalizarProcesoSeleccionPendiente() {

        try {
            if (!compraRN.tengoItemsSeleccionados(itemsPendiente)) {
                JsfUtil.addErrorMessage("No existen items pendientes seleccionados para generar el movimiento");
                PrimeFaces.current().ajax().addCallbackParam("todoOk", false);
                return;
            }

            m = compraRN.nuevoMovimiento(circuito, CODCO, SUCCO, CODPV, SUCPV, CODST, SUCST, CODCJ, SUCCJ, movimientoPendiente, itemsPendiente);
            calcularImportes();

            PrimeFaces.current().ajax().addCallbackParam("todoOk", true);

            movimientoPendiente = null;
            itemsPendiente = null;
            modoVista = "D";

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al generar comprobante", "- " + ex);
            log.log(Level.SEVERE, "Error al generar comprobante", ex);
            PrimeFaces.current().ajax().addCallbackParam("todoOk", false);
        }
    }

    public void cargarFiltroGrupo() {

        filtroGrupo.clear();

        filtroGrupo.put("CIRCOM", circuito.getCirapl());
        filtroGrupo.put("CONCOT", (congelaCotizacion ? "S" : "N"));

        if (proveedor != null) {
            filtroGrupo.put("NROCTA", proveedor.getNrocta());
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

        if (circuito.getComprobanteCompra() != null) {

            filtroGrupo.put("MONREG", circuito.getComprobanteCompra().getMonedaRegistracion().getCodigo());

        } else if (circuito.getComprobanteProveedor() != null) {

            filtroGrupo.put("MONREG", circuito.getComprobanteProveedor().getMonedaRegistracion().getCodigo());

        } else if (circuito.getComprobanteStock() != null) {

            filtroGrupo.put("MONREG", circuito.getComprobanteStock().getMonedaRegistracion().getCodigo());
        }

    }

    public void cargarFiltroDetalle() {

        filtroDetalle.clear();

        if (movimientoPendiente == null) {
            return;
        }

        filtroDetalle.put("CIRCOM", movimientoPendiente.getCircom());
        filtroDetalle.put("NROCTA", movimientoPendiente.getNrocta());
        filtroDetalle.put("MONREG", movimientoPendiente.getMonreg());
        filtroDetalle.put("COTIZA", movimientoPendiente.getCotizacion());

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

    public void procesarProveedor() {

        if (m != null && m.getProveedor() != null) {

            try {
                compraRN.asignarProveeedor(m, m.getProveedor());

                if (m.getProveedor().getSoloContado().equals("S") || m.getProveedor().getEntidadComodin().equals("S")) {
                    solicitaDatos = true;
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JsfUtil.addErrorMessage("No es posible asignar proveedor " + ex);
            }
        }
    }

    public void procesarDireccionEntrega() {

        if (m != null && m.getDireccionEntrega() != null) {

            compraRN.asignarDireccionEntrega(m, m.getDireccionEntrega());
        }
    }

    public void procesarLocalidad() {

        if (m != null && m.getLocalidad() != null) {

            m.setProvincia(m.getLocalidad().getProvincia());
        }
    }

    public void procesarTransporte() {

        if (transporteBean.getEntidad() != null && m != null) {

            m.setTransporte(transporteBean.getEntidad());
        }
    }

    public void procesarProducto() {

        if (productoBean.getProducto() != null && m != null && item != null) {

            try {
                compraRN.asignarProducto(item, productoBean.getProducto());

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

                        compraRN.agregarItem(m);
                        item = m.getItemsProducto().get(m.getItemsProducto().size() - 1);
                        compraRN.asignarProducto(item, p);
                    }

                    if (m.getItemsProducto()
                            .stream()
                            .filter(i -> i.getProducto() != null && i.getProducto().equals(p)).count() == 0) {

                        compraRN.agregarItem(m);
                        item = m.getItemsProducto().get(m.getItemsProducto().size() - 1);
                        compraRN.asignarProducto(item, p);

                    }
                }

            } catch (ExcepcionGeneralSistema ex) {

                JsfUtil.addErrorMessage("No es posible procesar los productos " + ex);
            }
        }
    }

    public void onChangeFechaEntrega() {

        if (m == null || m.getFechaEntrega() == null || m.getItemsProducto() == null) {
            return;
        }

        m.getItemsProducto().forEach(i -> {
            i.setFechaEntregaDesde(m.getFechaEntrega());
            i.setFechaEntregaHasta(m.getFechaEntrega());

        });

    }

    public void setearStockPorProducto(ItemMovimientoCompra itemProducto, ItemDetalleItemMovimientoCompra itemDetall) {

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

        if (m.getComprobanteStock() != null && m.getComprobanteStock().getTipoMovimiento().equals("T")) {
            consultaStock.verStockPorProductoDeposito(item.getProducto(), m.getDepositoTransferencia());
        } else {
            consultaStock.verStockPorProductoDeposito(item.getProducto(), m.getDeposito());
        }

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

    public void onChangeCotizacion() {

        for (ItemMovimientoCompra ip : m.getItemsProducto()) {

            ip.setCotizacion(m.getCotizacion());
        }
    }

    public void modificarAtributo() {
        compraRN.asignarProductoItemDetalle(m);
    }

    public void modificarAtributo(ItemMovimientoCompra itemProducto) {
        compraRN.asignarProductoItemDetalle(itemProducto);
    }

    public void modificarCantidad(ItemMovimientoCompra itemProducto) {

        compraRN.actualizarCantidades(itemProducto);
        compraRN.calcularImportes(m, "U");
    }

    public void calcularImportes() {

        compraRN.calcularImportes(m, "U");
    }

    public void calcularImportesTesoreria() {
        try {
            tesoreriaRN.calcularImportes(m.getMovimientoTesoreria());
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para calcular totales del comprobante de tesorería");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void calcularImportesLineaByPrecio(ItemMovimientoCompra item) {

        compraRN.calcularImportesLineaByPrecio(item);
    }

    public void calcularImportesLineaByTotal(ItemMovimientoCompra item) {

        compraRN.calcularImportesLineaByTotal(item);
    }

    public void imprimir(String modulo) {

        generarReporte(modulo);

        PrimeFaces.current().ajax().update("formulario");

        if (muestraReporte) {
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
        }
    }

    public void imprimir(MovimientoCompra movimiento, String modulo) {

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

            if (modulo.equals("CO") && m != null) {

                if (m.getFormulario().getReporte() == null) {
                    throw new ExcepcionGeneralSistema("No se puede imprimir - El formulario de compras no tienen reporte asociado");
                }

                parameters.put("ID", m.getId());
                parameters.put("EN_LETRAS", "Son pesos " + JsfUtil.generarEnLetras(m.getImporteTotal()));
                parameters.put("CANT_COPIAS", m.getComprobante().getCopias());

                nombreArchivo = m.getFormulario().getCodigo() + "-" + m.getNumeroFormulario();
                reportFactory.exportReportToPdfFile(m.getFormulario().getReporte(), nombreArchivo, parameters);
            }

            if (modulo.equals("PV") && m.getMovimientoProveedor() != null) {

                if (m.getMovimientoProveedor().getFormulario().getReporte() == null) {
                    throw new ExcepcionGeneralSistema("No se puede imprimir - El formulario de proveedor no tienen reporte asociado");
                }

                parameters.put("ID", m.getMovimientoProveedor().getId());
                parameters.put("EN_LETRAS", "Son pesos " + JsfUtil.generarEnLetras(m.getMovimientoProveedor().getItemTotal().get(0).getImporte()));
                parameters.put("CANT_COPIAS", m.getMovimientoProveedor().getComprobante().getCopias());

                nombreArchivo = m.getMovimientoProveedor().getFormulario().getCodigo() + "-" + m.getMovimientoProveedor().getNumeroFormulario();
                reportFactory.exportReportToPdfFile(m.getMovimientoProveedor().getFormulario().getReporte(), nombreArchivo, parameters);
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
        } catch (ExcepcionGeneralSistema e) {
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e);
            muestraReporte = false;
            PrimeFaces.current().ajax().update("formulario");
        } catch (JRException e) {
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e);
            muestraReporte = false;
            PrimeFaces.current().ajax().update("formulario");
        } catch (Exception e) {
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e);
            muestraReporte = false;
            PrimeFaces.current().ajax().update("formulario");
        }
    }

    public void preparoEnvioNotificaciones() {

        if (m == null) {
            JsfUtil.addWarningMessage("No hay comprobante seleccionado para enviar por e-mail");
            return;
        }

        solicitaEmail = true;
        emailEnvioComprobante = "";
        informacionAdicional = "";

        if (m.getProveedor().getEmail() != null) {
            emailEnvioComprobante = m.getProveedor().getEmail();
        }
        JsfUtil.addWarningMessage("Separe las direcciones de entrega con punto y coma(;) si desea enviar a más de un destinatario");
    }

    public void enviarNotificaciones() {

        try {

            if (aplicacionBean.getParametro().getEnviaNotificaciones() == 'S') {

                if (m.getFormulario().getModfor().equals("ST")) {
                    JsfUtil.addWarningMessage("Comprobante no habilitado para enviar por correo");
                    return;
                }

                muestraReporte = false;

                CorreoElectronico ce = null;

                if (m.getFormulario().getModfor().equals("CO")) {
                    generarReporte("CO");
//                    ce = notificacionesFacturacionRN.generarCorreoElectronicoCliente(m, emailEnvioComprobante,informacionAdicional);
                }

                if (m.getFormulario().getModfor().equals("PV")) {
                    generarReporte("PV");
//                    ce = notificacionesVentaRN.generarCorreoElectronicoCliente(m.getMovimientoVenta(), emailEnvioComprobante,informacionAdicional);
                }

                if (ce == null) {
                    JsfUtil.addWarningMessage("No se ha generado el correo electrónico");
                    return;
                }

                String pathPDF = reportFactory.getPathTemporales() + nombreArchivo + ".pdf";
                ce.setPathArchivo(pathPDF);

                List<CorreoElectronico> correos = new ArrayList<CorreoElectronico>();
                correos.add(ce);

                mailFactoryBean.enviarCorreosElectronicos(correos);

                JsfUtil.addInfoMessage("El comprobante fue enviado existosamente a " + emailEnvioComprobante);

            } else {
                JsfUtil.addWarningMessage("No tiene permiso para enviar notificaciones");
            }

            solicitaEmail = false;

        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "enviarNotificaciones", ex);
            JsfUtil.addErrorMessage("No es posible enviar la notificación", "enviarNotificaciones: " + ex);
            solicitaEmail = true;
        }
    }

    public void nuevaBusqueda() {

        if (m != null && m.getFormulario() != null) {
            formulario = m.getFormulario();
        }
        modoVista = "B";
    }

    public void limpiarFiltroBusqueda() {

        proveedor = null;

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

        proveedor = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        muestraReporte = false;
        solicitaEmail = false;
        movimientos = null;
        indiceWizard = 0;

    }

    public void buscar() {

        if (!validarParametros()) {
            return;
        }
        cargarFiltroBusqueda();

        movimientos = compraRN.getListaByBusqueda(filtro, mostrarSoloPendiente, cantidadRegistros);

        if (movimientos == null || movimientos.isEmpty()) {
            JsfUtil.addWarningMessage("No se han encontrado movimientos");
        }
        modoVista = "B";
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

        if (puntoVenta != null) {

            filtro.put("puntoVenta.codigo = ", "'" + puntoVenta.getCodigo() + "'");
        }

        if (proveedor != null) {
            filtro.put("proveedor.nrocta =", "'" + proveedor.getNrocta() + "'");
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
    }

    public void seleccionarMovimiento(MovimientoCompra mSel) {

        try {
            m = compraRN.seleccionarMovimiento(mSel, circuito);
            modoVista = "D";
        } catch (ExcepcionGeneralSistema excepcionGeneralSistema) {

            JsfUtil.addErrorMessage("No es posible seleccionar movimiento " + excepcionGeneralSistema);
        }
    }

    /**
     * Cargamos en la lista de formularios disponibles los que tienen relación
     * con el circuito que estamos ejecutando.
     *
     * @param query
     * @return
     */
    public List<Formulario> completeFormulario(String query) {
        try {

            if (circuito == null) {
                return null;
            }

            Comprobante comprobante = null;
            String puntoVenta = SUCCO;

            if (circuito.getActualizaCompra().equals("S") && CODCO != null) {

                comprobante = circuito.getComprobanteCompra();
                puntoVenta = SUCCO;

            } else if (circuito.getActualizaProveedor().equals("S") && CODPV != null) {

                comprobante = circuito.getComprobanteProveedor();
                puntoVenta = SUCCO;

            } else if (circuito.getActualizaStock().equals("S") && CODST != null) {

                comprobante = circuito.getComprobanteStock();
                puntoVenta = SUCST;
            }

            return formularioPorSituacionIVARN.getFormularioByComprobantePuntoVenta(comprobante, puntoVenta);

            //return formularioRN.getFormularioByBusqueda(modulo,puntoVenta, query, false);
        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Formulario>();
        }
    }

    public String verSeguimientoComprobante(MovimientoCompra mSel) {

        return "../compra/informe/seguimientoComprobante.jsf?TITULO=Seguimiento de Comprobantes&MODFOR=" + m.getFormulario().getModfor() + "CODFOR=" + m.getFormulario().getCodigo() + "NROFOR=" + m.getNumeroOriginal() + "&redirect=true";
    }

    public void procesarFormulario() {

        if (formularioCompraBean.getFormulario() != null) {
            formulario = formularioCompraBean.getFormulario();
        }
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
                ItemSaldoPendienteCuentaTesoreria v = consultaValoresBean.getItemPendiente();

                i.setCheque(v.getCheque());
                i.setFirmanteDocumento(v.getFirmante());
                i.setNombreEntidad(v.getNombre());
                i.setFechaCheque(v.getFechaCheque());
                i.setFechaVencimiento(v.getFechaVencimiento());
                i.setBanco(v.getBanco());
                i.setImporte(v.getImporte());
                i.setNumeroDocumento(v.getNumeroDocumento());
                i.setCategoriaCheque(v.getCategoria());

                consultaValoresBean.verSaldosPendiente();

                calcularImportes();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
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

        for (PendienteCompraDetalle p : itemsPendiente) {

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

    public ProductoBean getProductoBean() {
        return productoBean;
    }

    public void setProductoBean(ProductoBean productoBean) {
        this.productoBean = productoBean;
    }

    public PendienteCompraGrupo getMovimientoPendiente() {
        return movimientoPendiente;
    }

    public void setMovimientoPendiente(PendienteCompraGrupo movimientoPendiente) {
        this.movimientoPendiente = movimientoPendiente;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public boolean isSeleccionaComprobante() {
        return seleccionaComprobante;
    }

    public void setSeleccionaComprobante(boolean seleccionaComprobante) {
        this.seleccionaComprobante = seleccionaComprobante;
    }

    public ReportFactory getReportFactory() {
        return reportFactory;
    }

    public void setReportFactory(ReportFactory reportFactory) {
        this.reportFactory = reportFactory;
    }

    public boolean isSolicitaDatos() {
        return solicitaDatos;
    }

    public void setSolicitaDatos(boolean solicitaDatos) {
        this.solicitaDatos = solicitaDatos;
    }

    public String getSUCCO() {
        return SUCCO;
    }

    public void setSUCCO(String SUCCO) {
        this.SUCCO = SUCCO;
    }

    public String getSUCPV() {
        return SUCPV;
    }

    public void setSUCPV(String SUCPV) {
        this.SUCPV = SUCPV;
    }

    public String getSUCCJ() {
        return SUCCJ;
    }

    public void setSUCCJ(String SUCCJ) {
        this.SUCCJ = SUCCJ;
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

    @Override
    public boolean isDetalleVacio() {

        if (m == null) {
            detalleVacio = true;
            return detalleVacio;
        }

        if (m.getItemsProducto() != null) {

            if (circuito.getCircom().equals(circuito.getCirapl())) {
                detalleVacio = (m.getItemsProducto().size() <= 1);
            } else {
                detalleVacio = (m.getItemsProducto().size() <= 0);
            }
        } else {
            detalleVacio = true;
        }

        return detalleVacio;
    }

    public MailFactory getMailFactoryBean() {
        return mailFactoryBean;
    }

    public void setMailFactoryBean(MailFactory mailFactoryBean) {
        this.mailFactoryBean = mailFactoryBean;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

    public DireccionEntregaBean getDireccionEntregaBean() {
        return direccionEntregaBean;
    }

    public void setDireccionEntregaBean(DireccionEntregaBean direccionEntregaBean) {
        this.direccionEntregaBean = direccionEntregaBean;
    }

    public String getCODCO() {
        return CODCO;
    }

    public void setCODCO(String CODCO) {
        this.CODCO = CODCO;
    }

    public String getCODPV() {
        return CODPV;
    }

    public void setCODPV(String CODPV) {
        this.CODPV = CODPV;
    }

    public List<PendienteCompraGrupo> getMovimientosPendientes() {
        return movimientosPendientes;
    }

    public void setMovimientosPendientes(List<PendienteCompraGrupo> movimientosPendientes) {
        this.movimientosPendientes = movimientosPendientes;
    }

    public List<PendienteCompraDetalle> getItemsPendiente() {
        return itemsPendiente;
    }

    public void setItemsPendiente(List<PendienteCompraDetalle> itemsPendiente) {
        this.itemsPendiente = itemsPendiente;
    }

    public MovimientoCompra getM() {
        return m;
    }

    public void setM(MovimientoCompra m) {
        this.m = m;
    }

    public CircuitoCompra getCircuito() {
        return circuito;
    }

    public void setCircuito(CircuitoCompra circuito) {
        this.circuito = circuito;
    }

    public EntidadComercial getProveedor() {
        return proveedor;
    }

    public void setProveedor(EntidadComercial proveedor) {
        this.proveedor = proveedor;
    }

    public List<MovimientoCompra> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoCompra> movimientos) {
        this.movimientos = movimientos;
    }

    public FormularioCompraBean getFormularioCompraBean() {
        return formularioCompraBean;
    }

    public void setFormularioCompraBean(FormularioCompraBean formularioCompraBean) {
        this.formularioCompraBean = formularioCompraBean;
    }

    public MovimientoCompra getmReversion() {
        return mReversion;
    }

    public void setmReversion(MovimientoCompra mReversion) {
        this.mReversion = mReversion;
    }

    public CompradorBean getCompradorBean() {
        return compradorBean;
    }

    public void setCompradorBean(CompradorBean compradorBean) {
        this.compradorBean = compradorBean;
    }

    public CondicionPagoProveedorBean getCondicionPagoProveedorBean() {
        return condicionPagoProveedorBean;
    }

    public void setCondicionPagoProveedorBean(CondicionPagoProveedorBean condicionPagoProveedorBean) {
        this.condicionPagoProveedorBean = condicionPagoProveedorBean;
    }

    public ListaPrecioCostoBean getListaPrecioCostoBean() {
        return listaPrecioCostoBean;
    }

    public void setListaPrecioCostoBean(ListaPrecioCostoBean listaPrecioCostoBean) {
        this.listaPrecioCostoBean = listaPrecioCostoBean;
    }

    public ItemMovimientoCompra getItem() {
        return item;
    }

    public void setItem(ItemMovimientoCompra item) {
        this.item = item;
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

    public boolean isMantieneCotizacionOriginal() {
        return mantieneCotizacionOriginal;
    }

    public void setMantieneCotizacionOriginal(boolean mantieneCotizacionOriginal) {
        this.mantieneCotizacionOriginal = mantieneCotizacionOriginal;
    }

    public TransporteBean getTransporteBean() {
        return transporteBean;
    }

    public void setTransporteBean(TransporteBean transporteBean) {
        this.transporteBean = transporteBean;
    }

    public ConsultaStock getConsultaStock() {
        return consultaStock;
    }

    public void setConsultaStock(ConsultaStock consultaStock) {
        this.consultaStock = consultaStock;
    }

    public ItemDetalleItemMovimientoCompra getItemDetalle() {
        return itemDetalle;
    }

    public void setItemDetalle(ItemDetalleItemMovimientoCompra itemDetalle) {
        this.itemDetalle = itemDetalle;
    }

    public ConsultaValoresBean getConsultaValoresBean() {
        return consultaValoresBean;
    }

    public void setConsultaValoresBean(ConsultaValoresBean consultaValoresBean) {
        this.consultaValoresBean = consultaValoresBean;
    }

    public String getCongelaPrecio() {
        return congelaPrecio;
    }

    public void setCongelaPrecio(String congelaPrecio) {
        this.congelaPrecio = congelaPrecio;
    }

    public boolean getCongelaCotizacion() {
        return congelaCotizacion;
    }

    public void setCongelaCotizacion(boolean congelaCotizacion) {
        this.congelaCotizacion = congelaCotizacion;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public String getCodigoBarraPendiente() {
        return codigoBarraPendiente;
    }

    public void setCodigoBarraPendiente(String codigoBarraPendiente) {
        this.codigoBarraPendiente = codigoBarraPendiente;
    }

    public PendienteCompraDetalle getItemPendiente() {
        return itemPendiente;
    }

    public void setItemPendiente(PendienteCompraDetalle itemPendiente) {
        this.itemPendiente = itemPendiente;
    }

}
