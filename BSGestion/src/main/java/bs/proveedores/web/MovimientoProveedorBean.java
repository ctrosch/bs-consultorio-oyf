/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.web;

import bs.compra.rn.CircuitoCompraRN;
import bs.contabilidad.modelo.CuentaContable;
import bs.contabilidad.rn.CuentaContableRN;
import bs.entidad.modelo.EntidadComercial;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.rn.ComprobanteRN;
import bs.global.rn.FormularioPorSituacionIVARN;
import bs.global.rn.PuntoVentaRN;
import bs.global.util.JsfUtil;
import bs.global.util.ReportFactory;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.MailFactory;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.proveedores.rn.ListaCostoRN;
import bs.proveedores.rn.MovimientoProveedorRN;
import bs.seguridad.web.UsuarioSessionBean;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.tesoreria.modelo.ItemSaldoPendienteCuentaTesoreria;
import bs.tesoreria.rn.MovimientoTesoreriaRN;
import bs.tesoreria.web.informe.ConsultaValoresBean;
import java.io.Serializable;
import java.math.RoundingMode;
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

/**
 * @author ctrosch
 */
@ManagedBean
@ViewScoped
public class MovimientoProveedorBean extends GenericBean implements Serializable {

    protected @EJB
    MovimientoProveedorRN proveedorRN;
    protected @EJB
    CircuitoCompraRN circuitoRN;
    protected @EJB
    ListaCostoRN listaCostoRN;
    @EJB
    protected MovimientoTesoreriaRN tesoreriaRN;
    @EJB
    protected ComprobanteRN comprobanteRN;
    protected @EJB
    PuntoVentaRN puntoVentaRN;
    protected @EJB
    FormularioPorSituacionIVARN formularioPorSituacionIVARN;
    @EJB
    private CuentaContableRN cuentaContableRN;

    @EJB
    private MailFactory mailFactoryBean;

    protected String SUCPV = "";
    protected String CODPV = "";
    protected String SUCCJ = "";
    protected String CODCJ = "";

    protected boolean seleccionaComprobante;
    protected boolean solicitaDatos;

    // VARIABLES PARA BUSQUEDA DE COMPROBANTES
    protected EntidadComercial proveedor;
    protected List<MovimientoProveedor> movimientos;
    protected Integer numeroFormularioOriginalDesde;
    protected Integer numeroFormularioOriginalHasta;
    //--------------------------------------------------

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    @ManagedProperty(value = "#{consultaValoresBean}")
    protected ConsultaValoresBean consultaValoresBean;

    protected MovimientoProveedor m;
    protected MovimientoProveedor mReversion;
    protected MovimientoProveedor mBusqueda;

    /**
     * Creates a new instance of MovimientoCompra
     */
    public MovimientoProveedorBean() {

        titulo = "Movimiento de Proveedor";
        muestraReporte = false;
        solicitaDatos = false;
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                iniciarMovimiento();
                modoVista = "D";
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void iniciarMovimiento() {

        try {
            nombreArchivo = "";
            solicitaDatos = false;
            buscaMovimiento = false;
            muestraReporte = false;
            mBusqueda = null;
            mReversion = null;

            m = proveedorRN.nuevoMovimiento(CODPV, SUCPV, CODCJ, SUCCJ);

            comprobante = m.getComprobante();
            formulario = m.getFormulario();
            puntoVenta = m.getPuntoVenta();
            proveedor = m.getProveedor();

        } catch (ExcepcionGeneralSistema ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("iniciarMovimiento: " + ex);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nuevo() {

        iniciarMovimiento();
        esNuevo = true;
        modoVista = "D";
    }

    public void guardar(boolean nuevo) {

        try {
            m = proveedorRN.guardar(m);

            if (m.getComprobante().getEsComprobanteReversion().equals("S")) {
                mReversion.setMovimientoReversion(m);
                proveedorRN.guardar(mReversion);
            }

            JsfUtil.addInfoMessage("El comprobante " + m.getFormulario().getDescripcion() + " " + m.getPuntoVentaOriginal() + "-" + m.getNumeroOriginal() + " se guardó correctamente, Nro de carga " + m.getNumeroFormulario(), "");

            if (nuevo) {
                nuevo();
            }

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("guardar: " + ex);
//            ex.printStackTrace();
        }
    }

    public void seleccionar(MovimientoProveedor m) {

        if (m == null) {
            return;
        }

        this.m = m;
    }

    public void procesarProveedor() {

        if (m != null && m.getProveedor() != null) {

            try {
                proveedorRN.asignarProveedor(m, m.getProveedor());

                if (m.getProveedor().getSoloContado().equals("S") || m.getProveedor().getEntidadComodin().equals("S")) {
                    solicitaDatos = true;
                }
            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar proveedor " + ex);
            }
        }
    }

//    public void procesarDireccionEntrega() {
//
//        if (direccionEntregaBean.getDireccionEntrega() != null && m != null) {
//
//            DireccionEntregaEntidad de = direccionEntregaBean.getDireccionEntrega();
//
//            m.setProvincia(de.getProvincia());
//            m.setLocalidad(de.getLocalidad());
//            m.setBarrio(de.getBarrio());
//            m.setDireccion(de.getDireccion());
//            m.setNumero(de.getNumero());
//            m.setDepartamentoPiso(de.getDepartamentoPiso());
//            m.setDepartamentoNumero(de.getDepartamentoNumero());
//
//        }
//    }
//    public void procesarLocalidad() {
//
//        if (localidadBean.getLocalidad() != null && m != null) {
//
//            m.setLocalidad(localidadBean.getLocalidad());
//            m.setProvincia(localidadBean.getLocalidad().getProvincia());
//        }
//    }
    public void calcularImportes(boolean calculaIVA) {

        try {
            proveedorRN.calcularImportes(m, calculaIVA);
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para calcular totales del comprobante");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void calcularImportesTesoreria() {
        try {
            tesoreriaRN.calcularImportes(m.getMovimientoTesoreria());
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para calcular totales del comprobante de tesorería");
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

    public void imprimir(MovimientoProveedor movimiento) {

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

            nombreArchivo = m.getFormulario().getCodigo() + "-" + m.getNumeroFormulario();
            reportFactory.exportReportToPdfFile(m.getFormulario().getReporte(), nombreArchivo, parameters);
            muestraReporte = true;

        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage("No se encontró el archivo: " + m.getFormulario().getReporte().getPath());
            muestraReporte = false;
        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e);
            muestraReporte = false;
        }
    }

    private void aplicarDatosPorDefecto() {

        if (m != null) {
            m.setFechaRecepcion(fechaMovimientoDesde);
        }
    }

    public void nuevaBusqueda() {

//        resetParametros();
        if (m != null) {
            formulario = m.getFormulario();
        }
        buscaMovimiento = true;
        modoVista = "B";
    }

    public void limpiarFiltroBusqueda() {

        proveedor = null;
        formulario = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        numeroFormularioOriginalDesde = null;
        numeroFormularioOriginalHasta = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        muestraReporte = false;

        movimientos = null;

    }

    public void buscar() {

        if (!validarParametros()) {
            return;
        }

        cargarFiltroBusqueda();

        movimientos = proveedorRN.getListaByBusqueda(filtro, cantidadRegistros);

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

    public void seleccionarMovimiento(MovimientoProveedor mSel) {

        m = mSel;
        tesoreriaRN.cargarItemsMovimiento(m.getMovimientoTesoreria());
        calcularImportes(false);
        modoVista = "D";
    }

    public void revertirMovimiento(MovimientoProveedor mSel) {
        try {

            mReversion = mSel;
            m = proveedorRN.revertirMovimiento(mReversion);

            JsfUtil.addInfoMessage("El comprobante " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            JsfUtil.addInfoMessage("Se ha revertido el comprobante " + m.getMovimientoReversion().getComprobante().getDescripcion() + "-" + m.getMovimientoReversion().getPuntoVentaOriginal() + "-" + m.getMovimientoReversion().getNumeroOriginal(), "");

            buscar();
            nuevo();

            buscaMovimiento = true;

        } catch (Exception e) {
            JsfUtil.addErrorMessage("Problemas para generar movimiento de reversión " + e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<Formulario> completeFormulario(String query) {
        try {

            Comprobante comprobante = null;

            if (m.getComprobante() != null) {
                comprobante = m.getComprobante();
            } else {
                comprobante = comprobanteRN.getComprobante("PV", CODPV);
            }

            return formularioPorSituacionIVARN.getFormularioByComprobantePuntoVenta(comprobante, SUCPV);

            //return formularioRN.getFormularioByBusqueda(modulo,puntoVenta, query, false);
        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Formulario>();
        }
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

    public void asignarTotal(ItemMovimientoTesoreria item) {

        item.setImporte(m.getImporteTotal());
        item.setImporteMonedaSecundaria(m.getImporteTotal().divide(m.getCotizacion(), 4, RoundingMode.HALF_UP));
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

                calcularImportesTesoreria();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public List<CuentaContable> completeCuentaContable(String query) {
        try {
            filtro.clear();
            filtro.put("imputacionCompras = ", "'S'");

            return cuentaContableRN.getListaByBusqueda(query, "S", filtro, false, 15);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CuentaContable>();
        }
    }

    public void procesarLocalidad() {

        if (m != null && m.getLocalidad() != null) {

            m.setProvincia(m.getLocalidad().getProvincia());
        }
    }

    //------------------------------------------------------------------------------------------------------------
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

    public String getSUCPV() {
        return SUCPV;
    }

    public void setSUCPV(String SUCPV) {
        this.SUCPV = SUCPV;
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

    public String getCODPV() {
        return CODPV;
    }

    public void setCODPV(String CODPV) {
        this.CODPV = CODPV;
    }

    public EntidadComercial getProveedor() {
        return proveedor;
    }

    public void setProveedor(EntidadComercial proveedor) {
        this.proveedor = proveedor;
    }

    public List<MovimientoProveedor> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoProveedor> movimientos) {
        this.movimientos = movimientos;
    }

    public MovimientoProveedor getM() {
        return m;
    }

    public void setM(MovimientoProveedor m) {
        this.m = m;
    }

    public MovimientoProveedor getmReversion() {
        return mReversion;
    }

    public void setmReversion(MovimientoProveedor mReversion) {
        this.mReversion = mReversion;
    }

    public MovimientoProveedor getmBusqueda() {
        return mBusqueda;
    }

    public void setmBusqueda(MovimientoProveedor mBusqueda) {
        this.mBusqueda = mBusqueda;
    }

    public Integer getNumeroFormularioOriginalDesde() {
        return numeroFormularioOriginalDesde;
    }

    public void setNumeroFormularioOriginalDesde(Integer numeroFormularioOriginalDesde) {
        this.numeroFormularioOriginalDesde = numeroFormularioOriginalDesde;
    }

    public Integer getNumeroFormularioOriginalHasta() {
        return numeroFormularioOriginalHasta;
    }

    public void setNumeroFormularioOriginalHasta(Integer numeroFormularioOriginalHasta) {
        this.numeroFormularioOriginalHasta = numeroFormularioOriginalHasta;
    }

    public ConsultaValoresBean getConsultaValoresBean() {
        return consultaValoresBean;
    }

    public void setConsultaValoresBean(ConsultaValoresBean consultaValoresBean) {
        this.consultaValoresBean = consultaValoresBean;
    }
}
