/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.web;

import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.web.DireccionEntregaBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.rn.FormularioPorSituacionIVARN;
import bs.global.rn.PuntoVentaRN;
import bs.global.util.JsfUtil;
import bs.global.util.Numeros;
import bs.global.util.ReportFactory;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.LocalidadBean;
import bs.global.web.MailFactory;
import bs.prestamo.modelo.ItemPendienteCuentaCorrientePrestamo;
import bs.prestamo.modelo.MovimientoPrestamo;
import bs.prestamo.modelo.Prestamo;
import bs.prestamo.rn.MovimientoPrestamoRN;
import bs.prestamo.rn.PrestamoRN;
import bs.seguridad.web.UsuarioSessionBean;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.tesoreria.rn.MovimientoTesoreriaRN;
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
import javax.inject.Inject;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.PrimeFaces;

/**
 * @author ctrosch
 */
@ManagedBean
@ViewScoped
public class MovimientoPrestamoBean extends GenericBean implements Serializable {

    protected @EJB
    MovimientoPrestamoRN movimientoRN;
    protected @EJB
    PrestamoRN prestamoRN;
    protected @EJB
    PuntoVentaRN puntoVentaRN;
    protected @EJB
    FormularioPorSituacionIVARN formularioPorSituacionIVARN;
    @EJB
    MovimientoTesoreriaRN tesoreriaRN;
    @EJB
    private MailFactory mailFactoryBean;

    protected String SUCPR = "";
    protected String CODPR = "";
    protected String SUCCJ = "";
    protected String CODCJ = "";

    // VARIABLES PARA BUSQUEDA DE COMPROBANTES
    protected EntidadComercial destinatario;
    protected List<MovimientoPrestamo> movimientos;
    protected List<Prestamo> prestamos;
    //--------------------------------------------------

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    @ManagedProperty(value = "#{direccionEntregaBean}")
    protected DireccionEntregaBean direccionEntregaBean;

    @ManagedProperty(value = "#{localidadBean}")
    protected LocalidadBean localidadBean;

    protected MovimientoPrestamo m;
    protected MovimientoPrestamo mReversion;
    protected MovimientoPrestamo mBusqueda;

    private List<ItemPendienteCuentaCorrientePrestamo> debitos;
    private BigDecimal totalDebe;

    /**
     * Creates a new instance of MovimientoPrestamoBean
     */
    public MovimientoPrestamoBean() {

        titulo = "Movimiento de Prestamo";
        muestraReporte = false;
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                this.CODPR = (CODPR == null ? "" : CODPR);
                this.SUCPR = (SUCPR == null ? "" : SUCPR);
                this.CODCJ = (CODCJ == null ? "" : CODCJ);
                this.SUCCJ = (SUCCJ == null ? "" : SUCCJ);

                iniciarMovimiento();
                modoVista = "D";
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

//    @PostConstruct
    public void iniciarMovimiento() {

        try {
            nombreArchivo = "";
            buscaMovimiento = false;
            muestraReporte = false;
            mBusqueda = null;
            mReversion = null;

            m = movimientoRN.nuevoMovimiento(CODPR, SUCPR, CODCJ, SUCCJ);

            comprobante = m.getComprobante();
            puntoVenta = m.getPuntoVenta();

            esNuevo = true;

        } catch (ExcepcionGeneralSistema ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("iniciarMovimiento " + ex);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nuevo() {

        m = null;
        iniciarMovimiento();
        esNuevo = true;
        modoVista = "D";
    }

    public void guardar(boolean nuevo) {

        try {
            m = movimientoRN.guardar(m);

            if (m.getComprobante().getEsComprobanteReversion().equals("S")) {
//                mReversion.setMovimientoReversion(m);
                movimientoRN.guardar(mReversion);
            }

            JsfUtil.addInfoMessage("El comprobante " + m.getFormulario().getDescripcion() + " " + m.getPuntoVenta().getCodigo() + "-" + m.getNumeroFormulario() + " se guardó correctamente, Nro de carga " + m.getNumeroFormulario(), "");

            if (nuevo) {
                nuevo();
            }

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("guardar" + ex);
//            ex.printStackTrace();
        }
    }

    public void generarItemsCuentaCoorriente() {

        try {

            movimientoRN.generarItemsCuentaCorriente(m);
            JsfUtil.addInfoMessage("Los items se han generado correctamente");
        } catch (ExcepcionGeneralSistema ex) {
            Logger.getLogger(MovimientoPrestamoBean.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("Error : " + ex);
        }

    }

    public void generarItemsMovimiento() {

        try {

            movimientoRN.generarItemsMovimientoMantenimiento(m);
            JsfUtil.addInfoMessage("Los items se han generado correctamente");
        } catch (ExcepcionGeneralSistema ex) {
            Logger.getLogger(MovimientoPrestamoBean.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("Error : " + ex);
        } catch (Exception ex) {
            Logger.getLogger(MovimientoPrestamoBean.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("Error : " + ex);
        }

    }

    public void seleccionar(MovimientoPrestamo m) {

        if (m == null) {
            return;
        }

        this.m = m;
        modoVista = "D";
    }

    public List<Prestamo> completePrestamo(String query) {
        try {
            filtro.clear();

            filtro.put("estado.codigo = ", "'" + m.getComprobante().getEstadoPrestamoBusqueda() + "'");
            filtro.put("sucursal.codigo = ", "'" + m.getPuntoVenta().getSucursal().getCodigo() + "'");

            if (m.getComprobante().getTipoComprobante().equals("LQ")) {
                filtro.put("fechaEntrega <= ", JsfUtil.getFechaSQL(m.getFechaMovimiento()));
            }

            prestamos = prestamoRN.getListaByBusqueda(query, filtro, 10);
            return prestamos;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public void procesarPrestamo() {

        if (m != null && m.getPrestamo() != null) {

            try {
                movimientoRN.asignarPrestamo(m, m.getPrestamo());

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar destinatario " + ex);
            }
        }
    }

    public void generarCuotasReprogramacion() {

        try {
            prestamoRN.generarCuota(m.getPrestamoReprogramado());
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para generar cuotas " + ex);
        }
    }

    public void procesarDestinatario() {

        if (m != null && m.getDestinatario() != null) {

            try {
                movimientoRN.asignarDestinatario(m);

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar destinatario " + ex);
            }
        }
    }

    public void modificaDebito(ItemPendienteCuentaCorrientePrestamo i) {

        prestamoRN.calcularMoraYDescuento(m.getPrestamo(), m.getDebitosPrestamo(), m.getFechaMovimiento(), true, true);

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

        if (localidadBean.getLocalidad() != null && m != null) {

            m.setLocalidad(localidadBean.getLocalidad());
            m.setProvincia(localidadBean.getLocalidad().getProvincia());
        }
    }

    public void calcularImportes() {

        try {
//            movimientoRN.calcularImportes(m);
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para calcular totales del comprobante");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void imprimir() {

        generarReporte();

        PrimeFaces.current().ajax().update("formulario");

        if (muestraReporte) {
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
        }
    }

    public void imprimir(MovimientoPrestamo movimiento) {

        m = movimiento;
        imprimir();
    }

    public void generarReporte() {

        try {

            if (m.getFormulario().getReporte() == null) {
                throw new ExcepcionGeneralSistema("No se puede imprimir - El formulario no tienen reporte asociado");
            }

            Map parameters = new HashMap();

            if (m.getPuntoVenta().getUnidadNegocio().getLogo() != null && !m.getPuntoVenta().getUnidadNegocio().getLogo().isEmpty()) {
                parameters.put("LOGO", FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getResourceAsStream("/resources/image/" + m.getPuntoVenta().getUnidadNegocio().getLogo()));
            }

            if (m.getMovimientoTesoreria() != null) {
                parameters.put("ID", m.getMovimientoTesoreria().getId());
                parameters.put("EN_LETRAS", "Son pesos " + JsfUtil.generarEnLetras(m.getMovimientoTesoreria().getImporteTotalDebe()));
            } else {
                parameters.put("ID", m.getId());
            }

            parameters.put("CANT_COPIAS", m.getComprobante().getCopias());

            nombreArchivo = m.getFormulario().getCodigo() + "-" + m.getNumeroFormulario();
            reportFactory.exportReportToPdfFile(m.getFormulario().getReporte(), nombreArchivo, parameters);
            muestraReporte = true;

        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage("No se encontró el archivo: " + m.getFormulario().getReporte().getPath());
            muestraReporte = false;
        } catch (ExcepcionGeneralSistema e) {
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e);
            muestraReporte = false;
        } catch (JRException e) {
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e);
            muestraReporte = false;
        } catch (Exception e) {
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e);
            muestraReporte = false;
        }
    }

    private void aplicarDatosPorDefecto() {

        if (m != null) {
            m.setFechaMovimiento(new Date());
        }
    }

    public void nuevaBusqueda() {

//        resetParametros();
        if (m != null) {
            formulario = m.getFormulario();
        }
        buscaMovimiento = true;
        esNuevo = false;
    }

    public void limpiarFiltroBusqueda() {

        destinatario = null;
        formulario = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        muestraReporte = false;

        movimientos = null;

        buscar();
    }

    public void buscar() {

        if (!validarParametros()) {
            return;
        }

        cargarFiltroBusqueda();

        movimientos = movimientoRN.getListaByBusqueda(filtro, cantidadRegistros);

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

        filtro.put("movimientoReversion", " IS NULL");

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

        if (destinatario != null) {
            filtro.put("destinatario.nrocta =", "'" + destinatario.getNrocta() + "'");
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

    public void seleccionarMovimiento(MovimientoPrestamo mSel) {

        m = mSel;
        tesoreriaRN.cargarItemsMovimiento(m.getMovimientoTesoreria());
        calcularImportes();
        buscaMovimiento = false;
    }

    public void asignarTotal(ItemMovimientoTesoreria item) {

        item.setImporte(Numeros.toBigDecimal(m.getImporteCapital()));
        calcularImportes();
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

    public void revertirMovimiento(MovimientoPrestamo mSel) {
        try {

            mReversion = mSel;
            m = movimientoRN.revertirMovimiento(mReversion);
            m = movimientoRN.guardar(m);

//            mReversion.setMovimientoReversion(m);
            movimientoRN.guardar(mReversion);

            JsfUtil.addInfoMessage("El comprobante " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
//            JsfUtil.addInfoMessage("Se ha revertido el comprobante " + m.getMovimientoReversion().getComprobante().getDescripcion() + "-" + m.getPuntoVenta().getCodigo() + "-" + m.getNumeroFormulario(), "");

            buscar();
            //nuevo();

        } catch (Exception e) {
            JsfUtil.addErrorMessage("Problemas para generar movimiento de reversión" + (e.getMessage() == null ? e.getCause() : ""));
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    //------------------------------------------------------------------------------------------------------------
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

    public String getSUCPR() {
        return SUCPR;
    }

    public void setSUCPR(String SUCPR) {
        this.SUCPR = SUCPR;
    }

    public String getCODPR() {
        return CODPR;
    }

    public void setCODPR(String CODPR) {
        this.CODPR = CODPR;
    }

    public EntidadComercial getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(EntidadComercial destinatario) {
        this.destinatario = destinatario;
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

    public LocalidadBean getLocalidadBean() {
        return localidadBean;
    }

    public void setLocalidadBean(LocalidadBean localidadBean) {
        this.localidadBean = localidadBean;
    }

    public List<MovimientoPrestamo> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoPrestamo> movimientos) {
        this.movimientos = movimientos;
    }

    public MovimientoPrestamo getM() {
        return m;
    }

    public void setM(MovimientoPrestamo m) {
        this.m = m;
    }

    public MovimientoPrestamo getmReversion() {
        return mReversion;
    }

    public void setmReversion(MovimientoPrestamo mReversion) {
        this.mReversion = mReversion;
    }

    public MovimientoPrestamo getmBusqueda() {
        return mBusqueda;
    }

    public void setmBusqueda(MovimientoPrestamo mBusqueda) {
        this.mBusqueda = mBusqueda;
    }

    public String getSUCCJ() {
        return SUCCJ;
    }

    public void setSUCCJ(String SUCCJ) {
        this.SUCCJ = SUCCJ;
    }

    public String getCODCJ() {
        return CODCJ;
    }

    public void setCODCJ(String CODCJ) {
        this.CODCJ = CODCJ;
    }

    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(List<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

    public List<ItemPendienteCuentaCorrientePrestamo> getDebitos() {
        return debitos;
    }

    public void setDebitos(List<ItemPendienteCuentaCorrientePrestamo> debitos) {
        this.debitos = debitos;
    }

    public BigDecimal getTotalDebe() {
        return totalDebe;
    }

    public void setTotalDebe(BigDecimal totalDebe) {
        this.totalDebe = totalDebe;
    }

}
