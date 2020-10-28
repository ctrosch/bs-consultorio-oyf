/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.web;

import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.web.ClienteBean;
import bs.entidad.web.DireccionEntregaBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.rn.FormularioPorSituacionIVARN;
import bs.global.rn.PuntoVentaRN;
import bs.global.util.JsfUtil;
import bs.global.util.ReportFactory;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.LocalidadBean;
import bs.global.web.MailFactory;
import bs.seguridad.web.UsuarioSessionBean;
import bs.stock.web.ProductoBean;
import bs.ventas.modelo.MovimientoVenta;
import bs.ventas.rn.CondicionPagoVentaRN;
import bs.ventas.rn.ListaPrecioVentaRN;
import bs.ventas.rn.MovimientoVentaRN;
import java.io.Serializable;
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
public class MovimientoVentaBean extends GenericBean implements Serializable {

    protected @EJB
    MovimientoVentaRN ventaRN;
    protected @EJB
    ListaPrecioVentaRN listaPrecioRN;
    protected @EJB
    CondicionPagoVentaRN condicionPagoVentaRN;
    protected @EJB
    PuntoVentaRN puntoVentaRN;
    protected @EJB
    FormularioPorSituacionIVARN formularioPorSituacionIVARN;

    @EJB
    private MailFactory mailFactoryBean;

    protected String SUCURS = "";
    protected String MODVT = "";
    protected String CODVT = "";

    protected int indiceWizard;
    protected boolean solicitaDatos;

    // VARIABLES PARA BUSQUEDA DE COMPROBANTES
    protected EntidadComercial cliente;
    protected List<MovimientoVenta> movimientos;
    //--------------------------------------------------

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;

    @ManagedProperty(value = "#{clienteBean}")
    protected ClienteBean clienteBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    @ManagedProperty(value = "#{direccionEntregaBean}")
    protected DireccionEntregaBean direccionEntregaBean;

    @ManagedProperty(value = "#{localidadBean}")
    protected LocalidadBean localidadBean;

    @ManagedProperty(value = "#{formularioVentaBean}")
    protected FormularioVentaBean formularioVentaBean;

    protected MovimientoVenta m;
    protected MovimientoVenta mReversion;
    protected MovimientoVenta mBusqueda;

    /**
     * Creates a new instance of MovimientoVentaBean
     */
    public MovimientoVentaBean() {

        titulo = "Movimiento de Venta";
        muestraReporte = false;
        solicitaDatos = false;
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                iniciarMovimiento();
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

//    @PostConstruct
    public void iniciarVariables(String SUCURS, String MODVT, String CODVT) {

        try {
            if (!beanIniciado) {

                this.SUCURS = (SUCURS == null ? "" : SUCURS);
                this.MODVT = (MODVT == null ? "" : MODVT);
                this.CODVT = (CODVT == null ? "" : CODVT);

                iniciarMovimiento();
                beanIniciado = true;
            }
        } catch (Exception ex) {
//            e.printStackTrace();
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

//    @PostConstruct
    public void iniciarMovimiento() {

        try {
            nombreArchivo = "";
            solicitaDatos = false;
            buscaMovimiento = false;
            muestraReporte = false;
            mBusqueda = null;
            mReversion = null;

            m = ventaRN.nuevoMovimiento(CODVT, SUCURS);

            cargarFormulariosBusqueda();

            clienteBean.setEntidad(null);
            productoBean.setProducto(null);

        } catch (ExcepcionGeneralSistema ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("iniciarMovimiento " + ex);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nuevoMovimiento() {

        iniciarMovimiento();
    }

    public void guardar(boolean nuevo) {

        try {
            m = ventaRN.guardar(m);

            if (m.getComprobante().getEsComprobanteReversion().equals("S")) {
                mReversion.setMovimientoReversion(m);
                mReversion = ventaRN.guardar(mReversion);
            }

            JsfUtil.addInfoMessage("El comprobante " + m.getFormulario().getDescripcion() + " " + m.getPuntoVenta().getCodigo() + "-" + m.getNumeroFormulario() + " se guardó correctamente, Nro de carga " + m.getNumeroFormulario(), "");

            if (nuevo) {
                nuevoMovimiento();
            }

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("guardar" + ex);
//            ex.printStackTrace();
        }
    }

    public void seleccionar(MovimientoVenta m) {

        if (m == null) {
            return;
        }

        this.m = m;
    }

    public void procesarCliente() {

        if (clienteBean.getEntidad() != null) {
            cliente = clienteBean.getEntidad();
        }

        if (m != null && cliente != null) {

            m.setCliente(cliente);
            m.setClienteCuentaCorriente(cliente);
            m.setRazonSocial(cliente.getRazonSocial());
            m.setNrodoc(cliente.getNrodoc());
            m.setTipoDocumento(cliente.getTipoDocumento());

            m.setProvincia(cliente.getProvincia());
            m.setLocalidad(cliente.getLocalidad());
            m.setBarrio(cliente.getBarrio());
            m.setDireccion(cliente.getDireccion());
//            m.setNumero(cliente.getNumero());
//            m.setDepartamentoPiso(cliente.getDepartamentoPiso());
//            m.setDepartamentoNumero(cliente.getDepartamentoNumero());

            m.setCondicionDeIva(cliente.getCondicionDeIva());

            if (m.getComprobante() != null && m.getImputacionCuentaCorriente().equals("CO")) {
                m.setCondicionDePago(condicionPagoVentaRN.getCondicionDePagoVenta("A"));
            } else {
                m.setCondicionDePago(cliente.getCondicionDePagoVentas());
            }

            m.setListaDePrecio(cliente.getListaDePrecioVenta());
            m.setMonedaListaPrecio(cliente.getListaDePrecioVenta().getMoneda());
            m.setVendedor(cliente.getVendedor());

            try {
                ventaRN.asignarFormulario(m);
            } catch (ExcepcionGeneralSistema ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage("procesarCliente" + ex);
            }
            solicitaDatos = cliente.getSoloContado().equals("S");

            //Filtramos las direcciones de entrega del cliente
            direccionEntregaBean.setEntidad(cliente);
            direccionEntregaBean.buscar();
        }
    }

    public void procesarDireccionEntrega() {

        if (direccionEntregaBean.getDireccionEntrega() != null && m != null) {

            DireccionEntregaEntidad de = direccionEntregaBean.getDireccionEntrega();

            m.setProvincia(de.getProvincia());
            m.setLocalidad(de.getLocalidad());
            m.setBarrio(de.getBarrio());
            m.setDireccion(de.getDireccion());
//            m.setNumero(de.getNumero());
//            m.setDepartamentoPiso(de.getDepartamentoPiso());
//            m.setDepartamentoNumero(de.getDepartamentoNumero());

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
            ventaRN.calcularImportes(m);
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

            parameters.put("ID", m.getId());
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
    }

    public void resetParametros() {

        cliente = null;
        formulario = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        muestraReporte = false;

        movimientos = null;

    }

    public void buscarMovimiento() {

        if (!validarParametros()) {
            return;
        }

        cargarFiltroBusqueda();

        movimientos = ventaRN.getListaByBusqueda(filtro, cantidadRegistros);

        if (movimientos == null || movimientos.isEmpty()) {
            JsfUtil.addWarningMessage("No se han encontrado movimientos");
        }
    }

    public boolean validarParametros() {

        if (formulario == null) {
            JsfUtil.addWarningMessage("Seleccione un formulario");
            return false;
        }

        if (numeroFormularioDesde == null || numeroFormularioDesde <= 0) {
            JsfUtil.addWarningMessage("Ingrese un número de formulario desde válido");
            return false;
        }

        if (numeroFormularioHasta == null || numeroFormularioHasta <= 0) {
            JsfUtil.addWarningMessage("Ingrese un número de formulario hasta válido");
            return false;
        }

        if (numeroFormularioDesde > numeroFormularioHasta) {
            JsfUtil.addWarningMessage("Número de formulario desde es mayor al número de formulario hasta");
            return false;
        }

//        if(fechaMovimientoDesde==null){
//            JsfUtil.addWarningMessage("Ingrese una fecha de movimiento desde válida");
//            return false;
//        }
//
//        if(fechaMovimientoHasta==null){
//            JsfUtil.addWarningMessage("Ingrese una fecha de movimiento hasta válida");
//            return false;
//        }
//        if(fechaMovimientoDesde.after(fechaMovimientoHasta)){
//            JsfUtil.addWarningMessage("La fecha de movimiento desde es mayor a fecha de movimiento hasta");
//            return false;
//        }
        return true;
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        filtro.put("movimientoReversion", " IS NULL");

        if (formulario != null) {
            filtro.put("formulario.codigo = ", "'" + formulario.getCodigo() + "'");
        }

        if (cliente != null) {
            filtro.put("venta.nrocta =", "'" + cliente.getNrocta() + "'");
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

    public void seleccionarMovimiento(MovimientoVenta mSel) {

        m = mSel;
        calcularImportes();
        buscaMovimiento = false;
    }

    public void revertirMovimiento(MovimientoVenta mSel) {
        try {

            mReversion = mSel;
            m = ventaRN.revertirMovimiento(mReversion);
            m = ventaRN.guardar(m);

            mReversion.setMovimientoReversion(m);
            ventaRN.guardar(mReversion);

            JsfUtil.addInfoMessage("El comprobante " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            JsfUtil.addInfoMessage("Se ha revertido el comprobante " + m.getMovimientoReversion().getComprobante().getDescripcion() + "-" + m.getPuntoVenta().getCodigo() + "-" + m.getNumeroFormulario(), "");

            buscarMovimiento();
            nuevoMovimiento();

            buscaMovimiento = true;

        } catch (Exception e) {
            JsfUtil.addErrorMessage("Problemas para generar movimiento de reversión" + (e.getMessage() == null ? e.getCause() : ""));
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public void procesarFormulario() {

        if (formularioVentaBean.getFormulario() != null) {
            formulario = formularioVentaBean.getFormulario();
        }
    }

    /**
     * Cargamos en la lista de formularios disponibles los que tienen relación
     * con el circuito que estamos ejecutando.
     */
    private void cargarFormulariosBusqueda() {

        if (!m.getComprobante().getEsComprobanteReversion().equals("R")) {
            formularioVentaBean.setLista(formularioPorSituacionIVARN.getFormularioByComprobante(m.getComprobante()));
        }

        if (formularioVentaBean.getLista().size() == 1) {
            formulario = formularioVentaBean.getLista().get(0);
        }
    }

    //------------------------------------------------------------------------------------------------------------
    public ProductoBean getProductoBean() {
        return productoBean;
    }

    public void setProductoBean(ProductoBean productoBean) {
        this.productoBean = productoBean;
    }

    public UsuarioSessionBean getUsuarioSessionBean() {
        return usuarioSessionBean;
    }

    public void setUsuarioSessionBean(UsuarioSessionBean usuarioSessionBean) {
        this.usuarioSessionBean = usuarioSessionBean;
    }

    public ClienteBean getEntidadComercialBean() {
        return clienteBean;
    }

    public void setEntidadComercialBean(ClienteBean clienteBean) {
        this.clienteBean = clienteBean;
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

    public String getSUCURS() {
        return SUCURS;
    }

    public void setSUCURS(String SUCURS) {
        this.SUCURS = SUCURS;
    }

    public int getIndiceWizard() {
        return indiceWizard;
    }

    public void setIndiceWizard(int indiceWizard) {
        this.indiceWizard = indiceWizard;
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

    public List<MovimientoVenta> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoVenta> movimientos) {
        this.movimientos = movimientos;
    }

    public FormularioVentaBean getFormularioVentaBean() {
        return formularioVentaBean;
    }

    public void setFormularioVentaBean(FormularioVentaBean formularioVentaBean) {
        this.formularioVentaBean = formularioVentaBean;
    }

    public MovimientoVenta getM() {
        return m;
    }

    public void setM(MovimientoVenta m) {
        this.m = m;
    }

    public MovimientoVenta getmReversion() {
        return mReversion;
    }

    public void setmReversion(MovimientoVenta mReversion) {
        this.mReversion = mReversion;
    }

    public MovimientoVenta getmBusqueda() {
        return mBusqueda;
    }

    public void setmBusqueda(MovimientoVenta mBusqueda) {
        this.mBusqueda = mBusqueda;
    }

    public String getMODVT() {
        return MODVT;
    }

    public void setMODVT(String MODVT) {
        this.MODVT = MODVT;
    }

    public String getCODVT() {
        return CODVT;
    }

    public void setCODVT(String CODVT) {
        this.CODVT = CODVT;
    }

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
    }

}
