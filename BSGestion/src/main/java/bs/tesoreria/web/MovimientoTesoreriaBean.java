/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.web;

import bs.administracion.modelo.CorreoElectronico;
import bs.contabilidad.modelo.CentroCosto;
import bs.contabilidad.modelo.SubCuenta;
import bs.contabilidad.rn.SubCuentaRN;
import bs.educacion.rn.NotificacionesEducacionRN;
import bs.entidad.modelo.EntidadComercial;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.TipoConcepto;
import bs.global.rn.FormularioPorSituacionIVARN;
import bs.global.rn.FormularioRN;
import bs.global.rn.TipoConceptoRN;
import bs.global.util.JsfUtil;
import bs.global.util.ReportFactory;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.MailFactory;
import bs.prestamo.modelo.Prestamo;
import bs.proveedores.rn.NotificacionesProveedorRN;
import bs.seguridad.web.UsuarioSessionBean;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.tesoreria.modelo.ItemMovimientoTesoreriaCentroCosto;
import bs.tesoreria.modelo.ItemMovimientoTesoreriaSubcuenta;
import bs.tesoreria.modelo.ItemSaldoPendienteCuentaTesoreria;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.tesoreria.rn.CuentaTesoreriaRN;
import bs.tesoreria.rn.MovimientoTesoreriaRN;
import bs.tesoreria.web.informe.ConsultaValoresBean;
import bs.ventas.rn.NotificacionesVentaRN;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.primefaces.PrimeFaces;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.ToggleEvent;

/**
 * @author ctrosch El movimiento de rendición permite generar varios recibos de
 * una sola vez de manera de ahorrar tiempo en la cobranza.
 *
 * CONCEPTO DEBE, LAS CAJAS DE LA PERSONA QUE RECIBE CONCEPTO HABER,
 *
 *
 */
@ManagedBean
@ViewScoped
public class MovimientoTesoreriaBean extends GenericBean implements Serializable {

    @EJB
    MovimientoTesoreriaRN tesoreriaRN;
    @EJB
    FormularioRN formularioRN;
    @EJB
    NotificacionesVentaRN notificacionesVentaRN;
    @EJB
    NotificacionesProveedorRN notificacionesProveedorRN;
    @EJB
    NotificacionesEducacionRN notificacionesEducacionRN;
    @EJB
    MailFactory mailFactoryBean;
    @EJB
    FormularioPorSituacionIVARN formularioPorSituacionIVARN;
    @EJB
    private TipoConceptoRN tipoConceptoRN;
    @EJB
    private SubCuentaRN subCuentaRN;
    @EJB
    private CuentaTesoreriaRN cuentaTesoreriaRN;

    protected String CODCJ = "";
    protected String SUCCJ = "";
    protected String CODVT = "";
    protected String CODPV = "";
    protected String CODPR = "";
    protected String CODED = "";

    protected String CTAVAL = "";

    private Integer numeroFormulario;

    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{conceptoTesoreriaBean}")
    protected ConceptoTesoreriaBean conceptoBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    @ManagedProperty(value = "#{formularioTesoreriaBean}")
    protected FormularioTesoreriaBean formularioTesoreriaBean;

    @ManagedProperty(value = "#{consultaValoresBean}")
    protected ConsultaValoresBean consultaValoresBean;

    protected MovimientoTesoreria m;
    protected ItemMovimientoTesoreria itemMovimiento;
    protected ItemMovimientoTesoreria itemMovimientoDebe;
    protected ItemMovimientoTesoreria itemMovimientoHaber;
    protected MovimientoTesoreria mBusqueda;
    protected MovimientoTesoreria mReversion;
    private List<ItemSaldoPendienteCuentaTesoreria> saldosPendiente;

    //Variable para busqueda
    private Prestamo prestamo;
    private EntidadComercial entidad;
    private CentroCosto centroCosto;
    private String referencia;

    protected double totalComision;
    protected double totalIntereses;

    protected List<MovimientoTesoreria> movimientos;

    /**
     * Creates a new instance of MovimientoFacturacion
     */
    public MovimientoTesoreriaBean() {
        muestraReporte = false;
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (SUCCJ == null) {
                    SUCCJ = "";
                }

                if (CODCJ == null) {
                    CODCJ = "";
                }

                if (CODVT == null) {
                    CODVT = "";
                }

                if (CODPV == null) {
                    CODPV = "";
                }

                if (CODPR == null) {
                    CODPR = "";
                }

                if (CODED == null) {
                    CODED = "";
                }

                if (CTAVAL == null) {
                    CTAVAL = "";
                }

                iniciarMovimiento();
                modoVista = "D";
                beanIniciado = true;

            }
        } catch (Exception e) {

            JsfUtil.addErrorMessage("Error al iniciar el bean " + e.getMessage());
        }
    }

    //@PostConstruct
    public void iniciarMovimiento() {

        try {

            nombreArchivo = "";
            buscaMovimiento = false;
            muestraReporte = false;
            mBusqueda = null;
            mReversion = null;

            totalComision = 0;
            totalIntereses = 0;

            m = tesoreriaRN.nuevoMovimiento(CODCJ, CODVT, CODPV, CODPR, CODED, SUCCJ);
            esNuevo = true;

            if (m.getComprobante().getTipoComprobante().equals("T") && m.getComprobante().getRegisracionManual().equals("A")) {
                seleccionaPendiente = true;
                JsfUtil.addInfoMessage("Seleccione la cuenta de tesoreria de egreso");
            }

            cargarFormulariosBusqueda();

            comprobante = m.getComprobante();
            puntoVenta = m.getPuntoVenta();

        } catch (Exception ex) {
//            ex.printStackTrace();
            JsfUtil.addErrorMessage("ERROR iniciarMovimiento" + ex);
        }
    }

    public void nuevo() {

        m = null;
        iniciarMovimiento();
        esNuevo = true;
        modoVista = "D";

        TabView a = (TabView) FacesContext.getCurrentInstance().getViewRoot().findComponent("formulario").findComponent("tb");

        if (a != null) {
            a.setTabindex("0");
        }

    }

    public void guardar(boolean nuevo) {

        try {

            if (!puedoGuardar()) {
                return;
            }

            m = tesoreriaRN.guardar(m);

            if (m.getComprobante().getTipoComprobante().equals("R")) {

                mBusqueda.setMovimientoReversion(m);
                tesoreriaRN.guardar(mBusqueda);
            }

            JsfUtil.addInfoMessage("El documento " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            muestraReporte = false;
            esNuevo = false;

            if (nuevo) {
                nuevo();
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible guardar el comprobante " + ex.getMessage());
        }
    }

    public boolean puedoGuardar() {

        return true;
    }

    public void filtrarTipoConcepto(String codTipo) {

        TipoConcepto tipo = tipoConceptoRN.getTipoConcepto("CJ", codTipo);

        conceptoBean.setTipoConcepto(tipo);
        conceptoBean.buscar();
    }

    public void procesarEntidad() {

        if (m != null && m.getEntidad() != null) {

            tesoreriaRN.procesarEntidad(m);
        }
    }

    public void buscarValores(ItemMovimientoTesoreria i) {

        itemMovimiento = i;
        consultaValoresBean.init(i.getCuentaTesoreria().getCodigo());
    }

    public void procesarValor() {

        if (m != null && itemMovimiento != null && consultaValoresBean.getItemPendiente() != null) {
            try {

                tesoreriaRN.procesarValor(itemMovimiento, consultaValoresBean.getItemPendiente());
                consultaValoresBean.verSaldosPendiente();
                tesoreriaRN.calcularImportes(m);

            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void calcularImportes() {
        try {
            tesoreriaRN.calcularImportes(m);
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para calcular totales del comprobante");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String procesoSeleccionPendientes(FlowEvent event) {

        if (event.getNewStep().equals("cuenta_haber")) {
            JsfUtil.addInfoMessage("Seleccione la cuenta de tesoreria de egreso");
        }

        if (event.getNewStep().equals("pendientes")) {
            try {
                if (itemMovimientoHaber == null || itemMovimientoHaber.getCuentaTesoreria() == null) {
                    JsfUtil.addWarningMessage("Seleccione la cuenta de tesoreria de egreso");
                    return event.getOldStep();
                }

                saldosPendiente = cuentaTesoreriaRN.getSaldosPendienteByCuenta(itemMovimientoHaber.getCuentaTesoreria().getCodigo());

                if (saldosPendiente == null || saldosPendiente.isEmpty()) {
                    JsfUtil.addWarningMessage("No se han encontrado saldos pendiente para la cuenta " + itemMovimientoHaber.getCuentaTesoreria().getDescripcion());
                    return event.getOldStep();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JsfUtil.addErrorMessage("No es posible procesar pendientes: " + ex);
            }
        }

        if (event.getNewStep().equals("cuenta_debe")) {

            boolean tengoSeleccionado = false;

            for (ItemSaldoPendienteCuentaTesoreria saldo : saldosPendiente) {
                if (saldo.isSeleccionado()) {
                    tengoSeleccionado = true;
                    break;
                }
            }

            if (!tengoSeleccionado) {
                JsfUtil.addWarningMessage("No ha seleccionado ningún item");
                return event.getOldStep();
            }

            JsfUtil.addInfoMessage("Seleccione la cuenta de tesoreria de ingreso");
        }

        return event.getNewStep();
    }

    public void finalizarProcesoSeleccionPendiente() {

        try {

            if (itemMovimientoDebe == null || itemMovimientoDebe.getCuentaTesoreria() == null) {
                JsfUtil.addWarningMessage("No se encontró la cuenta de tesorería de ingreso");
                return;
            }

            tesoreriaRN.asignarItemsFromPendiente(m, itemMovimientoDebe, itemMovimientoHaber, saldosPendiente);

            PrimeFaces.current().executeScript("PF('dlgPendiente').hide();");

            saldosPendiente = null;
            itemMovimiento = null;
            modoVista = "D";

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al generar comprobante", "- " + ex);
            log.log(Level.SEVERE, "Error al generar comprobante", ex);
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
        }
    }

    public void imprimir() {

        generarReporte();

        PrimeFaces.current().ajax().update("formulario");

        if (muestraReporte) {
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
        }
    }

    public void imprimir(MovimientoTesoreria movimiento) {

        m = movimiento;
        imprimir();

    }

    public void generarReporte() {

        try {

            Map parameters = new HashMap();

            if (m == null) {
                throw new ExcepcionGeneralSistema("No se puede imprimir - No existe movimiento seleccionado");
            }

            if (m.getFormulario().getReporte() == null) {
                throw new ExcepcionGeneralSistema("No se puede imprimir - El formulario no tienen reporte asociado");
            }

            if (m.getPuntoVenta().getUnidadNegocio().getLogo() != null && !m.getPuntoVenta().getUnidadNegocio().getLogo().isEmpty()) {
                parameters.put("LOGO", FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getResourceAsStream("/resources/image/" + m.getPuntoVenta().getUnidadNegocio().getLogo()));
            }

            parameters.put("ID", m.getId());
            parameters.put("CANT_COPIAS", m.getComprobante().getCopias());

            if (m.getMovimientoVenta() != null || m.getMovimientoEducacion() != null || m.getMovimientoPrestamo() != null) {

                if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {

                    parameters.put("EN_LETRAS", JsfUtil.generarEnLetras(m.getImporteTotalDebe()));
                }

                if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {

                    parameters.put("EN_LETRAS", JsfUtil.generarEnLetras(m.getImporteTotalDebeSecundario()));
                }
            }

            if (m.getMovimientoProveedor() != null) {

                if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {

                    parameters.put("EN_LETRAS", JsfUtil.generarEnLetras(m.getImporteTotalHaber()));
                }

                if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {

                    parameters.put("EN_LETRAS", JsfUtil.generarEnLetras(m.getImporteTotalHaberSecundario()));
                }
            }

            nombreArchivo = m.getFormulario().getCodigo() + "-" + m.getNumeroFormulario();
            reportFactory.exportReportToPdfFile(m.getFormulario().getReporte(), nombreArchivo, parameters);
            muestraReporte = true;

        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage("No se encontró el archivo: " + m.getFormulario().getReporte().getPath());
            muestraReporte = false;

        } catch (Exception e) {
            muestraReporte = false;
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e.getMessage());
        }
    }

    public void procesarFormulario() {

        if (formularioTesoreriaBean.getFormulario() != null) {
            formulario = formularioTesoreriaBean.getFormulario();
        }
    }

    public void buscar() {

        if (!validarParametros()) {
            return;
        }

        cargarFiltroBusqueda();

        movimientos = tesoreriaRN.getListaByBusqueda(filtro, cantidadRegistros);

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

        if (comprobante != null) {

            filtro.put("comprobante.modulo = ", "'" + comprobante.getModulo() + "'");
            filtro.put("comprobante.codigo = ", "'" + comprobante.getCodigo() + "'");
        }

        if (puntoVenta != null) {

            filtro.put("puntoVenta.codigo = ", "'" + puntoVenta.getCodigo() + "'");
        }

        filtro.put("movimientoReversion", " IS NULL");

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

        if (entidad != null) {
            filtro.put("entidad.nrocta = ", "'" + entidad.getNrocta() + "'");
        }

        if (prestamo != null) {
            filtro.put("prestamo.id = ", prestamo.getId() + "");
        }

        if (referencia != null) {
            filtro.put("referencia LIKE ", "'%" + referencia + "%'");
        }

//        System.err.println("filtro " + filtro);
    }

    public void seleccionarMovimiento(MovimientoTesoreria mSel) {

        m = mSel;
        tesoreriaRN.cargarItemsMovimiento(m);
        calcularImportes();
        modoVista = "D";
    }

    public void revertirMovimiento(MovimientoTesoreria mSel) {
        try {

            mReversion = mSel;
            m = tesoreriaRN.revertirMovimiento(mReversion);

            JsfUtil.addInfoMessage("El comprobante " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            JsfUtil.addInfoMessage("Se ha revertido el comprobante " + mReversion.getComprobante().getDescripcion() + "-" + mReversion.getPuntoVenta().getCodigo() + "-" + mReversion.getNumeroFormulario(), "");

            buscar();

        } catch (Exception e) {
            JsfUtil.addErrorMessage("Problemas para generar movimiento de reversión " + e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public void limpiarFiltroBusqueda() {

        mBusqueda = null;
        //formulario = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;

        numeroFormulario = null;
        entidad = null;
        prestamo = null;

        buscar();
    }

    public void agregarConcepto(List<ItemMovimientoTesoreria> items, ItemMovimientoTesoreria itemCopiar) {

        try {
            ItemMovimientoTesoreria item = tesoreriaRN.nuevoItemMovimiento(m, itemCopiar);
            items.add(itemCopiar.getNroItem(), item);
            tesoreriaRN.reorganizarNroItem(m);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar concepto " + ex);
        }
    }

    public List<SubCuenta> completeSubCuenta(String query) {

        try {
            return subCuentaRN.getListaByBusqueda(centroCosto, query, false, 10);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<SubCuenta>();
        }

    }

    public void nuevoItemSubCuenta(ItemMovimientoTesoreriaCentroCosto cc) {

        try {
            tesoreriaRN.nuevoItemMovimientoSubCuenta(cc);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item Sub Cuenta " + ex);
        }
    }

    public void eliminarItemSubCuenta(ItemMovimientoTesoreriaCentroCosto itemCentroCosto, ItemMovimientoTesoreriaSubcuenta itemSubCuenta) {

        try {
            tesoreriaRN.eliminarItemSubCuenta(itemCentroCosto, itemSubCuenta);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible quitar item Sub Cuenta " + ex);
        }
    }

    public void preparoEnvioNotificaciones() {

        if (m == null) {
            JsfUtil.addWarningMessage("No hay comprobante seleccionado para enviar por e-mail");
            return;
        }

//        if (m.getComprobanteVenta() == null && m.getComprobanteProveedor() == null) {
//            JsfUtil.addWarningMessage("Comprobante no habilitado para enviar por correo");
//            return;
//        }
        solicitaEmail = true;
        emailEnvioComprobante = "";
        informacionAdicional = "";

        if (m.getEntidad() != null && m.getEntidad().getEmail() != null) {
            emailEnvioComprobante = m.getEntidad().getEmail();
        }
        JsfUtil.addWarningMessage("Separe las direcciones de entrega con punto y coma(;) si desea enviar a más de un destinatario");
    }

    public void enviarNotificaciones() {

        try {

            if (aplicacionBean.getParametro().getEnviaNotificaciones() == 'S') {

                muestraReporte = false;
                generarReporte();

                CorreoElectronico ce = null;

                if (m.getComprobanteVenta() != null) {
                    ce = notificacionesVentaRN.generarCorreoElectronico(m.getMovimientoVenta(), emailEnvioComprobante, informacionAdicional);
                }

                if (m.getComprobanteProveedor() != null) {
                    ce = notificacionesProveedorRN.generarCorreoElectronico(m.getMovimientoProveedor(), emailEnvioComprobante, informacionAdicional);
                }

                if (m.getMovimientoEducacion() != null & m.getMovimientoEducacion().getId() != null) {
                    ce = notificacionesEducacionRN.generarCorreoElectronico(m.getMovimientoEducacion(), emailEnvioComprobante, informacionAdicional);
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

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "enviarNotificaciones", e);
            JsfUtil.addErrorMessage("No es posible enviar la notificación", (e.getMessage() == null ? "Error desconocido" : e.getMessage()));
            solicitaEmail = true;
        }
    }

    /**
     * Cargamos en la lista de formularios disponibles los que tienen relación
     * con el circuito que estamos ejecutando.
     */
    protected void cargarFormulariosBusqueda() {

        if (!m.getComprobante().getEsComprobanteReversion().equals("R")) {
            formularioTesoreriaBean.setLista(formularioPorSituacionIVARN.getFormularioByComprobante(m.getComprobante()));
        }

        if (formularioTesoreriaBean.getLista().size() == 1) {
            formulario = formularioTesoreriaBean.getLista().get(0);
        }
    }

    public void onRowToggle(ToggleEvent event) {

        centroCosto = ((ItemMovimientoTesoreriaCentroCosto) event.getData()).getCentroCosto();

    }

    public void asignarTotal(ItemMovimientoTesoreria item) {

        if (item.getDebeHaber().equals("D")) {
            m.getItemsDebe().forEach(d -> d.setImporte(BigDecimal.ZERO));
            item.setImporte(m.getImporteTotalHaber());
        } else {
            m.getItemsHaber().forEach(h -> h.setImporte(BigDecimal.ZERO));
            item.setImporte(m.getImporteTotalDebe());
        }

        calcularImportes();
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

    //------------------------------------------------------------------------------------------------------------
    public String getCODCJ() {
        return CODCJ;
    }

    public void setCODCJ(String CODCJ) {
        this.CODCJ = CODCJ;
    }

    public MovimientoTesoreria getM() {
        return m;
    }

    public void setM(MovimientoTesoreria m) {
        this.m = m;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public ConceptoTesoreriaBean getConceptoBean() {
        return conceptoBean;
    }

    public void setConceptoBean(ConceptoTesoreriaBean conceptoBean) {
        this.conceptoBean = conceptoBean;
    }

    public ReportFactory getReportFactory() {
        return reportFactory;
    }

    public void setReportFactory(ReportFactory reportFactory) {
        this.reportFactory = reportFactory;
    }

    public String getSUCCJ() {
        return SUCCJ;
    }

    public void setSUCCJ(String SUCCJ) {
        this.SUCCJ = SUCCJ;
    }

    public String getCODPR() {
        return CODPR;
    }

    public void setCODPR(String CODPR) {
        this.CODPR = CODPR;
    }

    public FormularioTesoreriaBean getFormularioTesoreriaBean() {
        return formularioTesoreriaBean;
    }

    public void setFormularioTesoreriaBean(FormularioTesoreriaBean formularioTesoreriaBean) {
        this.formularioTesoreriaBean = formularioTesoreriaBean;
    }

    public Integer getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(Integer numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
    }

    public MovimientoTesoreria getmBusqueda() {
        return mBusqueda;
    }

    public void setmBusqueda(MovimientoTesoreria mBusqueda) {
        this.mBusqueda = mBusqueda;
    }

    public String getCODVT() {
        return CODVT;
    }

    public void setCODVT(String CODVT) {
        this.CODVT = CODVT;
    }

    public String getCODPV() {
        return CODPV;
    }

    public void setCODPV(String CODPV) {
        this.CODPV = CODPV;
    }

    public ConsultaValoresBean getConsultaValoresBean() {
        return consultaValoresBean;
    }

    public void setConsultaValoresBean(ConsultaValoresBean consultaValoresBean) {
        this.consultaValoresBean = consultaValoresBean;
    }

    public String getCTAVAL() {
        return CTAVAL;
    }

    public void setCTAVAL(String CTAVAL) {
        this.CTAVAL = CTAVAL;
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

    public MovimientoTesoreria getmReversion() {
        return mReversion;
    }

    public void setmReversion(MovimientoTesoreria mReversion) {
        this.mReversion = mReversion;
    }

    public List<MovimientoTesoreria> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoTesoreria> movimientos) {
        this.movimientos = movimientos;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public EntidadComercial getEntidad() {
        return entidad;
    }

    public void setEntidad(EntidadComercial entidad) {
        this.entidad = entidad;
    }

    public ItemMovimientoTesoreria getItemMovimiento() {
        return itemMovimiento;
    }

    public void setItemMovimiento(ItemMovimientoTesoreria itemMovimiento) {
        this.itemMovimiento = itemMovimiento;
    }

    public CentroCosto getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(CentroCosto centroCosto) {
        this.centroCosto = centroCosto;
    }

    public List<ItemSaldoPendienteCuentaTesoreria> getSaldosPendiente() {
        return saldosPendiente;
    }

    public void setSaldosPendiente(List<ItemSaldoPendienteCuentaTesoreria> saldosPendiente) {
        this.saldosPendiente = saldosPendiente;
    }

    public ItemMovimientoTesoreria getItemMovimientoDebe() {
        return itemMovimientoDebe;
    }

    public void setItemMovimientoDebe(ItemMovimientoTesoreria itemMovimientoDebe) {
        this.itemMovimientoDebe = itemMovimientoDebe;
    }

    public ItemMovimientoTesoreria getItemMovimientoHaber() {
        return itemMovimientoHaber;
    }

    public void setItemMovimientoHaber(ItemMovimientoTesoreria itemMovimientoHaber) {
        this.itemMovimientoHaber = itemMovimientoHaber;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getCODED() {
        return CODED;
    }

    public void setCODED(String CODED) {
        this.CODED = CODED;
    }

    public double getTotalComision() {
        return totalComision;
    }

    public void setTotalComision(double totalComision) {
        this.totalComision = totalComision;
    }

    public double getTotalIntereses() {
        return totalIntereses;
    }

    public void setTotalIntereses(double totalIntereses) {
        this.totalIntereses = totalIntereses;
    }

}
