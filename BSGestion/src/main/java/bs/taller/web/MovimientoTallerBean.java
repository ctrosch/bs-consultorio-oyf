
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.web;

import bs.administracion.modelo.CorreoElectronico;
import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.rn.EntidadRN;
import bs.entidad.web.DireccionEntregaBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Formulario;
import bs.global.modelo.Localidad;
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
import bs.stock.web.informe.ConsultaStock;
import bs.taller.modelo.CircuitoTaller;
import bs.taller.modelo.ItemMovimientoTaller;
import bs.taller.modelo.ItemProductoTaller;
import bs.taller.modelo.ItemServicioTaller;
import bs.taller.modelo.MovimientoTaller;
import bs.taller.rn.CircuitoTallerRN;
import bs.taller.rn.TallerRN;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.primefaces.PrimeFaces;
import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.component.wizard.Wizard;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;

/**
 * @author ctrosch
 */
@ManagedBean
@ViewScoped
public class MovimientoTallerBean extends GenericBean implements Serializable {

    protected @EJB
    TallerRN tallerRN;
    protected @EJB
    CircuitoTallerRN circuitoRN;
    protected @EJB
    FormularioPorSituacionIVARN formularioPorSituacionIVARN;
    protected @EJB
    ProductoRN productoRN;
    protected @EJB
    EntidadRN entidadRN;

    @EJB
    private MailFactory mailFactoryBean;

    //Datos inicializacion registracion
    protected String CIRCOM = "";
    protected String CIRAPL = "";

    protected String CODTL = "";
    protected String SUCTL = "";

    protected String CODFC = "";
    protected String SUCFC = "";

    protected String CODST = "";
    protected String SUCST = "";

    protected String NROCTA = "";
    protected String TIPPRO = "";
    protected String TIPSER = "";

    protected MovimientoTaller m;
    protected ItemServicioTaller itemServicio;
    protected ItemProductoTaller itemProducto;
    protected CircuitoTaller circuito;

    //Datos para generar movimientos aplicados
    protected List<MovimientoTaller> movimientosPendientes;
    protected MovimientoTaller movimientoPendiente;

    // VARIABLES PARA BUSQUEDA DE COMPROBANTES
    protected EntidadComercial cliente;
    protected List<MovimientoTaller> movimientos;

    //--------------------------------------------------
    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    @ManagedProperty(value = "#{consultaStock}")
    protected ConsultaStock consultaStock;

    @ManagedProperty(value = "#{direccionEntregaBean}")
    protected DireccionEntregaBean direccionEntregaBean;

    @ManagedProperty(value = "#{localidadBean}")
    protected LocalidadBean localidadBean;

    @ManagedProperty(value = "#{formularioTallerBean}")
    protected FormularioTallerBean formularioTallerBean;

    /**
     * Creates a new instance of MovimientoTaller
     */
    public MovimientoTallerBean() {

        titulo = "Movimiento de Taller";
        muestraReporte = false;
        numeroFormularioDesde = 1;
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                formulario = new Formulario();
                iniciarMovimiento();
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

//  @PostConstruct
    public void iniciarMovimiento() {

        super.iniciar();

        try {
            movimientoPendiente = null;
            movimientosPendientes = null;

            muestraReporte = false;
            solicitaEmail = false;
            buscaMovimiento = false;

            nombreArchivo = "";
            esAnulacion = false;

            cliente = null;
            numeroFormularioDesde = null;
            numeroFormularioHasta = null;
            fechaMovimientoDesde = null;
            fechaMovimientoHasta = null;

            circuito = circuitoRN.iniciarCircuito(CIRCOM, CIRAPL, CODTL, CODFC, CODST);
            seleccionaPendiente = (!CIRCOM.equals(CIRAPL));

            if (!seleccionaPendiente) {
                m = tallerRN.nuevoMovimiento(circuito, CODTL, SUCTL, CODFC, SUCFC, CODST, SUCST);
            }

            cargarFormulariosBusqueda();

            if (NROCTA != null && !NROCTA.isEmpty()) {

                m.setCliente(entidadRN.getEntidad(NROCTA));
                procesarCliente();
            }

            if (circuito.getTipoProductoProducto() != null) {
                TIPPRO = circuito.getTipoProductoProducto().getCodigo();
            }

            if (circuito.getTipoProductoServicio() != null) {
                TIPSER = circuito.getTipoProductoServicio().getCodigo();
            }

            if (usuarioSessionBean.getUsuario().getTecnico() != null) {
                m.setTecnico(usuarioSessionBean.getUsuario().getTecnico());
            }

        } catch (ExcepcionGeneralSistema ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("iniciarMovimiento: " + ex);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nuevoMovimiento() {

        m = null;
        iniciarMovimiento();

        if (seleccionaPendiente) {
            Wizard w = (Wizard) FacesContext.getCurrentInstance().getViewRoot().findComponent("fPendiente").findComponent("wizPendiente");
            w.setStep("filtro");

            AccordionPanel a = (AccordionPanel) FacesContext.getCurrentInstance().getViewRoot().findComponent("fPendiente").findComponent("accordion");
            a.setActiveIndex("0");
        }
    }

    public void nuevoItemServicio() {

        if (m.getCircuito().getPermiteAgregarItems().equals("N")) {
            JsfUtil.addErrorMessage("El circuito no permite agregar items");
            return;
        }

        try {

            if (m.getItemsServicio().size() > 0) {

                ItemServicioTaller ultimoItem = m.getItemsServicio().get(m.getItemsServicio().size() - 1);

                if (!puedoAgregarItemServicio(ultimoItem)) {
                    return;
                }
            }
            //Cargarmos un nuevo item en blanco
            m.getItemsServicio().add(tallerRN.nuevoItemServicio(m));

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("No es posible agregar un nuevo item " + ex);
        }
    }

    public void nuevoItemProducto() {

        if (m.getCircuito().getPermiteAgregarItems().equals("N")) {
            JsfUtil.addErrorMessage("El circuito no permite agregar items");
            return;
        }

        try {

            if (m.getItemsProducto().size() > 0) {

                ItemProductoTaller ultimoItem = m.getItemsProducto().get(m.getItemsProducto().size() - 1);

                if (!puedoAgregarItemProducto(ultimoItem)) {
                    return;
                }
            }
            //Cargarmos un nuevo item en blanco
            m.getItemsProducto().add(tallerRN.nuevoItemProducto(m));

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("No es posible agregar un nuevo item " + ex);
        }
    }

    public void guardar(boolean nuevo) {

        try {
            m = tallerRN.guardar(m);

            if (m.getComprobante().getModulo().equals("TL")) {

                JsfUtil.addInfoMessage("El documento " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            } else {

                if (m.getMovimientoFacturacion() != null) {
                    JsfUtil.addInfoMessage("El documento " + m.getMovimientoFacturacion().getFormulario().getDescripcion() + "-" + m.getMovimientoFacturacion().getNumeroFormulario() + " se guardó correctamente", "");
                }

                if (m.getMovimientoStock() != null) {
                    JsfUtil.addInfoMessage("El documento " + m.getMovimientoStock().getFormulario().getDescripcion() + "-" + m.getMovimientoStock().getNumeroFormulario() + " se guardó correctamente", "");
                }
            }
            muestraReporte = false;

            if (nuevo) {
                nuevoMovimiento();
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al guardar: " + ex);
        }
    }

    public void eliminarItem(ItemProductoTaller nItem) {

        try {
            tallerRN.eliminarItemProducto(m, nItem);
            JsfUtil.addWarningMessage("Se ha borrado el item " + nItem.getProducto().getDescripcion() + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + nItem.getProducto().getDescripcion() + " " + ex);
        }
    }

    public boolean puedoAgregarItemServicio(ItemServicioTaller itemAnterior) {

        if (itemAnterior == null) {
            JsfUtil.addErrorMessage("No se ha creado el item");
            return false;
        }

        if (m.getMonedaSecundaria() == null) {
            JsfUtil.addErrorMessage("La moneda secundaria no puede estar vacía");
            return false;
        }

        if (itemAnterior.getCotizacion() == null || itemAnterior.getCotizacion().compareTo(BigDecimal.ONE) < 0) {
            JsfUtil.addErrorMessage("La cotización del comprobante no puede se nula o menor a 1");
            return false;
        }

        /*
        if((circuito.getTipoProductoUnico()!=null) && (m.getItemsProducto().size() > 1)){
            JsfUtil.addErrorMessage("Ha superado la cantidad máxima de items, no puede continuar agregando");
            return false;
        }*/
        if (itemAnterior.getCantidad() == null || itemAnterior.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            JsfUtil.addErrorMessage("Ingrese un valor de cantidad válido. Mayor a 0");
            return false;
        }

        if (itemAnterior.getPrecio() == null || itemAnterior.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            JsfUtil.addErrorMessage("Ingrese un valor de precio válido. Mayor a 0");
            return false;
        }

        if (itemAnterior.getProducto() == null) {
            JsfUtil.addErrorMessage("Seleccione un producto para agregar al comprobante");
            return false;
        }

        return true;
    }

    public boolean puedoAgregarItemProducto(ItemProductoTaller itemAnterior) {

        if (itemAnterior == null) {
            JsfUtil.addErrorMessage("No se ha creado el item");
            return false;
        }

        if (m.getMonedaSecundaria() == null) {
            JsfUtil.addErrorMessage("La moneda secundaria no puede estar vacía");
            return false;
        }

        if (itemAnterior.getCotizacion() == null || itemAnterior.getCotizacion().compareTo(BigDecimal.ONE) < 0) {
            JsfUtil.addErrorMessage("La cotización del comprobante no puede se nula o menor a 1");
            return false;
        }

        /*
        if((circuito.getTipoProductoUnico()!=null) && (m.getItemsProducto().size() > 1)){
            JsfUtil.addErrorMessage("Ha superado la cantidad máxima de items, no puede continuar agregando");
            return false;
        }*/
        if (itemAnterior.getCantidad() == null || itemAnterior.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            JsfUtil.addErrorMessage("Ingrese un valor de cantidad válido. Mayor a 0");
            return false;
        }

        if (itemAnterior.getPrecio() == null || itemAnterior.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            JsfUtil.addErrorMessage("Ingrese un valor de precio válido. Mayor a 0");
            return false;
        }

        if (itemAnterior.getProducto() == null) {
            JsfUtil.addErrorMessage("Seleccione un producto para agregar al comprobante");
            return false;
        }

        return true;
    }

    public void seleccionar(MovimientoTaller m) {

        if (m == null) {
            return;
        }

        this.m = m;
    }

    public void verPendientes(CircuitoTaller c) {

        try {
            circuito = circuitoRN.iniciarCircuito(c.getCircom(), c.getCirapl(), CODTL, CODFC, CODST);
            seleccionaPendiente = (!c.getCircom().equals(c.getCirapl()));
            m = null;

            if (seleccionaPendiente) {
                Wizard w = (Wizard) FacesContext.getCurrentInstance().getViewRoot().findComponent("fPendiente").findComponent("wizPendiente");
                w.setStep("filtro");
            }

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
                if (m != null) {

                    JsfUtil.addInfoMessage("El comprobante fue generado, no puede regresar al paso anterior");
                    return event.getOldStep();
                }

                seleccionarMovimientosPendientes();

                if (movimientosPendientes == null || movimientosPendientes.isEmpty()) {
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
        movimientosPendientes = tallerRN.getListaByBusqueda(filtroGrupo, seleccionaPendiente, 0);

        if (movimientosPendientes == null || movimientosPendientes.isEmpty()) {
            JsfUtil.addWarningMessage("No se han encontrado pendientes para este circuito");
        }
    }

    public void finalizarProcesoSeleccionPendiente(MovimientoTaller movimientoPendiente) {

        try {

            if (movimientoPendiente == null) {
                JsfUtil.addErrorMessage("El movimiento pendiente no puede ser nulo");
                return;
            }

            m = tallerRN.nuevoMovimiento(circuito, CODTL, SUCTL, CODFC, SUCFC, CODST, SUCST, movimientoPendiente);
            aplicarDatosPorDefecto();

            PrimeFaces.current().executeScript("PF('dlgPendiente').hide()");
            buscaMovimiento = false;

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al generar comprobante", "- " + ex);
            log.log(Level.SEVERE, "Error al generar comprobante", ex);
            PrimeFaces.current().executeScript("PF('dlgPendiente').show()");
        }
    }

    public void cargarFiltroGrupo() {

        filtroGrupo.clear();

        filtroGrupo.put("circuito.circom = ", "'" + circuito.getCirapl() + "'");
        filtroGrupo.put("movimientoAplicacion ", " IS NULL ");

        if (cliente != null) {
            filtroGrupo.put("nrocta =", "'" + cliente.getNrocta() + "'");
        }

    }

    public void actualizarCantidades(ItemProductoTaller item) {

        tallerRN.actualizarCantidades(m, item);

    }

    public void procesarCliente() {

        if (m != null && m.getCliente() != null) {

            try {
                tallerRN.asignarCliente(m, m.getCliente());
                direccionEntregaBean.setEntidad(cliente);
                direccionEntregaBean.buscar();
            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar cliente " + ex);
            }
        }
    }

    public void procesarContacto() {

        if (m != null && m.getContacto() != null) {

            try {
                tallerRN.asignarContacto(m, m.getContacto());

            } catch (ExcepcionGeneralSistema ex) {
                JsfUtil.addErrorMessage("No es posible asignar contacto " + ex);
            }
        }
    }

    public void procesarEquipo() {

        if (m != null && m.getEquipo() != null) {

            try {
                tallerRN.asignarEquipo(m, m.getEquipo());
            } catch (ExcepcionGeneralSistema ex) {
                JsfUtil.addErrorMessage("No es posible asignar contacto " + ex);
            }
        }
    }

    public void procesarDireccionEntrega() {

        if (direccionEntregaBean.getDireccionEntrega() != null && m != null) {

            DireccionEntregaEntidad de = direccionEntregaBean.getDireccionEntrega();
            m.setProvincia(de.getProvincia());
            m.setLocalidad(de.getLocalidad());
            m.setBarrio(de.getBarrio());
            m.setDireccion(de.getDireccion());
        }
    }

    public void procesarLocalidad() {

        if (m != null && m.getLocalidad() != null) {

            m.setProvincia(m.getLocalidad().getProvincia());
        }
    }

    public void procesarServicio(ItemServicioTaller i) {

        if (m != null && i != null && i.getProducto() != null) {

            try {
                tallerRN.asignarServicio(i, i.getProducto());

            } catch (ExcepcionGeneralSistema ex) {

                JsfUtil.addErrorMessage("No es posible asignar producto al item " + ex);
            }
        }
    }

    public void procesarProducto(ItemProductoTaller i) {

        if (m != null && i != null && i.getProducto() != null) {

            try {
                tallerRN.asignarProducto(i, i.getProducto());

            } catch (ExcepcionGeneralSistema ex) {

                JsfUtil.addErrorMessage("No es posible asignar producto al item " + ex);
            }
        }
    }

    public void procesarStock() {

        if (consultaStock.getItemStock() != null && m != null && itemProducto != null) {

            Stock s = consultaStock.getItemStock();

            itemProducto.setAtributo1(s.getAtributo1());
            itemProducto.setAtributo2(s.getAtributo2());
            itemProducto.setAtributo3(s.getAtributo3());
            itemProducto.setAtributo4(s.getAtributo4());
            itemProducto.setAtributo5(s.getAtributo5());
            itemProducto.setAtributo6(s.getAtributo6());
            itemProducto.setAtributo7(s.getAtributo7());
        }
    }

    public void onChangeCotizacion() {

        for (ItemProductoTaller ip : m.getItemsProducto()) {

            ip.setCotizacion(m.getCotizacion());
        }
    }

    public void onSelectLocalidad(SelectEvent event) {

        Localidad cp = ((Localidad) event.getObject());
//        m.setProvincia(cp.getProvincia());
    }

    public void imprimir(String modulo) {

        generarReporte(modulo);

        PrimeFaces.current().ajax().update("formulario");

        if (muestraReporte) {
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
        }
    }

    public void imprimir(MovimientoTaller movimiento, String modulo) {

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

            if (modulo.equals("TL") && m != null) {

                if (m.getFormulario().getReporte() == null) {
                    throw new ExcepcionGeneralSistema("No se puede imprimir - El formulario de no tienen reporte asociado");
                }

                parameters.put("ID", m.getId());
                parameters.put("CANT_COPIAS", m.getComprobante().getCopias());

                nombreArchivo = m.getFormulario().getCodigo() + "-" + m.getNumeroFormulario();
                reportFactory.exportReportToPdfFile(m.getFormulario().getReporte(), nombreArchivo, parameters);
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

                if (m.getFormulario().getModfor().equals("ST")) {
                    JsfUtil.addWarningMessage("Comprobante no habilitado para enviar por correo");
                    return;
                }

                muestraReporte = false;
                CorreoElectronico ce = null;

//                if (m.getFormulario().getModfor().equals("FC")) {
//                    generarReporte("FC");
//                    ce = notificacionesTallerRN.generarCorreoElectronicoCliente(m, emailEnvioComprobante, informacionAdicional);
//                }
//
//                if (m.getFormulario().getModfor().equals("VT")) {
//                    generarReporte("VT");
//                    ce = notificacionesVentaRN.generarCorreoElectronicoCliente(m.getMovimientoVenta(), emailEnvioComprobante, informacionAdicional);
//                }
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
        buscaMovimiento = true;
    }

    public void resetParametros() {

        cliente = null;
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

    public void buscarMovimiento() {

        if (!validarParametros()) {
            return;
        }
        cargarFiltroBusqueda();
        movimientos = tallerRN.getListaByBusqueda(filtro, seleccionaPendiente, cantidadRegistros);

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

        if (cliente != null) {

            filtro.put("cliente.nrocta =", "'" + cliente.getNrocta() + "'");
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

    public void seleccionarMovimiento(MovimientoTaller mSel) {

        try {
            m = tallerRN.seleccionarMovimiento(mSel, circuito);
            buscaMovimiento = false;
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible seleccionar movimiento " + ex);
        }
    }

    public void procesarFormulario() {

        if (formularioTallerBean.getFormulario() != null) {
            formulario = formularioTallerBean.getFormulario();
        }
    }

    /**
     * Cargamos en la lista de formularios disponibles los que tienen relación
     * con el circuito que estamos ejecutando.
     */
    private void cargarFormulariosBusqueda() {

        if (m.getCircuito().getActualizaTaller().equals("S") && CODTL != null) {

            formularioTallerBean.setLista(formularioPorSituacionIVARN.getFormularioByComprobante(m.getCircuito().getComprobanteTaller()));

        } else if (m.getCircuito().getActualizaFacturacion().equals("S") && CODFC != null) {

            formularioTallerBean.setLista(formularioPorSituacionIVARN.getFormularioByComprobante(m.getCircuito().getComprobanteFacturacion()));

        } else if (m.getCircuito().getActualizaStock().equals("S") && CODST != null) {

            formularioTallerBean.setLista(formularioPorSituacionIVARN.getFormularioByComprobante(m.getCircuito().getComprobanteStock()));
        }

        if (formularioTallerBean.getLista().size() == 1) {
            formulario = formularioTallerBean.getLista().get(0);
        }
    }

    public List<Producto> completeServicio(String query) {
        try {
            String codTipo = "";

            if (circuito.getTipoProductoServicio() != null) {
                codTipo = circuito.getTipoProductoServicio().getCodigo();
            }

            return productoRN.getListaByBusqueda(codTipo, query, false, 10);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Producto>();
        }
    }

    public List<Producto> completeProducto(String query) {
        try {
            String codTipo = "";

            if (circuito.getTipoProductoProducto() != null) {
                codTipo = circuito.getTipoProductoProducto().getCodigo();
            }

            return productoRN.getListaByBusqueda(codTipo, query, false, 10);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Producto>();
        }
    }

    private void aplicarDatosPorDefecto() {

    }

    public void calcularImportes(ItemMovimientoTaller item) {

        tallerRN.calcularImportesLineaByPrecio(item, false);

    }

    public void calcularImportesConImpuesto(ItemMovimientoTaller item) {

        tallerRN.calcularImportesLineaByPrecio(item, true);
    }

    //------------------------------------------------------------------------------------------------------------
    public TallerRN getTallerRN() {
        return tallerRN;
    }

    public void setTallerRN(TallerRN tallerRN) {
        this.tallerRN = tallerRN;
    }

    public CircuitoTallerRN getCircuitoRN() {
        return circuitoRN;
    }

    public void setCircuitoRN(CircuitoTallerRN circuitoRN) {
        this.circuitoRN = circuitoRN;
    }

    public FormularioPorSituacionIVARN getFormularioPorSituacionIVARN() {
        return formularioPorSituacionIVARN;
    }

    public void setFormularioPorSituacionIVARN(FormularioPorSituacionIVARN formularioPorSituacionIVARN) {
        this.formularioPorSituacionIVARN = formularioPorSituacionIVARN;
    }

    public ProductoRN getProductoRN() {
        return productoRN;
    }

    public void setProductoRN(ProductoRN productoRN) {
        this.productoRN = productoRN;
    }

    public EntidadRN getEntidadRN() {
        return entidadRN;
    }

    public void setEntidadRN(EntidadRN entidadRN) {
        this.entidadRN = entidadRN;
    }

    public MailFactory getMailFactoryBean() {
        return mailFactoryBean;
    }

    public void setMailFactoryBean(MailFactory mailFactoryBean) {
        this.mailFactoryBean = mailFactoryBean;
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

    public String getCODTL() {
        return CODTL;
    }

    public void setCODTL(String CODTL) {
        this.CODTL = CODTL;
    }

    public String getSUCTL() {
        return SUCTL;
    }

    public void setSUCTL(String SUCTL) {
        this.SUCTL = SUCTL;
    }

    public String getCODFC() {
        return CODFC;
    }

    public void setCODFC(String CODFC) {
        this.CODFC = CODFC;
    }

    public String getSUCFC() {
        return SUCFC;
    }

    public void setSUCFC(String SUCFC) {
        this.SUCFC = SUCFC;
    }

    public String getCODST() {
        return CODST;
    }

    public void setCODST(String CODST) {
        this.CODST = CODST;
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

    public List<MovimientoTaller> getMovimientosPendientes() {
        return movimientosPendientes;
    }

    public void setMovimientosPendientes(List<MovimientoTaller> movimientosPendientes) {
        this.movimientosPendientes = movimientosPendientes;
    }

    public MovimientoTaller getMovimientoPendiente() {
        return movimientoPendiente;
    }

    public void setMovimientoPendiente(MovimientoTaller movimientoPendiente) {
        this.movimientoPendiente = movimientoPendiente;
    }

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
    }

    public List<MovimientoTaller> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoTaller> movimientos) {
        this.movimientos = movimientos;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
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

    public ConsultaStock getConsultaStock() {
        return consultaStock;
    }

    public void setConsultaStock(ConsultaStock consultaStock) {
        this.consultaStock = consultaStock;
    }

    public DireccionEntregaBean getDireccionEntregaBean() {
        return direccionEntregaBean;
    }

    public void setDireccionEntregaBean(DireccionEntregaBean direccionEntregaBean) {
        this.direccionEntregaBean = direccionEntregaBean;
    }

    public LocalidadBean getLocalidadBean() {
        return localidadBean;
    }

    public void setLocalidadBean(LocalidadBean localidadBean) {
        this.localidadBean = localidadBean;
    }

    public FormularioTallerBean getFormularioTallerBean() {
        return formularioTallerBean;
    }

    public void setFormularioTallerBean(FormularioTallerBean formularioTallerBean) {
        this.formularioTallerBean = formularioTallerBean;
    }

    public MovimientoTaller getM() {
        return m;
    }

    public void setM(MovimientoTaller m) {
        this.m = m;
    }

    public ItemServicioTaller getItemServicio() {
        return itemServicio;
    }

    public void setItemServicio(ItemServicioTaller itemServicio) {
        this.itemServicio = itemServicio;
    }

    public ItemProductoTaller getItemProducto() {
        return itemProducto;
    }

    public void setItemProducto(ItemProductoTaller itemProducto) {
        this.itemProducto = itemProducto;
    }

    public CircuitoTaller getCircuito() {
        return circuito;
    }

    public void setCircuito(CircuitoTaller circuito) {
        this.circuito = circuito;
    }

    public String getTIPPRO() {
        return TIPPRO;
    }

    public void setTIPPRO(String TIPPRO) {
        this.TIPPRO = TIPPRO;
    }

    public String getTIPSER() {
        return TIPSER;
    }

    public void setTIPSER(String TIPSER) {
        this.TIPSER = TIPSER;
    }

}
