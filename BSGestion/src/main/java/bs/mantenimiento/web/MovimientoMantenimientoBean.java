/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.web;

import bs.educacion.modelo.MovimientoEducacion;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.rn.FormularioPorSituacionIVARN;
import bs.global.util.JsfUtil;
import bs.global.util.ReportFactory;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.MailFactory;
import bs.mantenimiento.modelo.ItemMovimientoMantenimientoActividad;
import bs.mantenimiento.modelo.ItemMovimientoMantenimientoRecurso;
import bs.mantenimiento.modelo.MovimientoMantenimiento;
import bs.mantenimiento.rn.MantenimientoRN;
import bs.seguridad.modelo.Usuario;
import bs.seguridad.rn.UsuarioRN;
import bs.seguridad.web.UsuarioSessionBean;
import bs.stock.modelo.Producto;
import bs.stock.rn.ProductoRN;
import java.io.Serializable;
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
import javax.inject.Inject;
import org.primefaces.PrimeFaces;

/**
 * @author ctrosch
 */
@ManagedBean
@ViewScoped
public class MovimientoMantenimientoBean extends GenericBean implements Serializable {

    @EJB
    private MantenimientoRN mantenimientoRN;

    @EJB
    private FormularioPorSituacionIVARN formularioPorSituacionIVARN;
    @EJB
    private ProductoRN productoRN;
    @EJB
    private UsuarioRN usuarioRN;
    @EJB
    private MailFactory mailFactoryBean;

    //Datos inicializacion registracion
    protected String PTOMT = "";
    protected String CODMT = "";

    //Atributos para filtros
    private Producto bienUso;

    // VARIABLES PARA BUSQUEDA DE COMPROBANTES
    //--------------------------------------------------
    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    @ManagedProperty(value = "#{actividadBean}")
    protected ActividadBean actividadBean;

    @ManagedProperty(value = "#{formularioMantenimientoBean}")
    protected FormularioMantenimientoBean formularioMantenimientoBean;

    protected MovimientoMantenimiento m;
    protected List<MovimientoMantenimiento> pendientes;
    protected List<MovimientoMantenimiento> movimientos;
    protected MovimientoMantenimiento mReversion;
    protected MovimientoMantenimiento mBusqueda;
    protected MovimientoEducacion mPendiente;
    protected ItemMovimientoMantenimientoActividad itemActividad;
    protected ItemMovimientoMantenimientoRecurso itemRecurso;

    public MovimientoMantenimientoBean() {

        titulo = "Movimiento de Mantenimiento";
        muestraReporte = false;
        numeroFormularioDesde = 1;
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                formulario = new Formulario();
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
            muestraReporte = false;
            mBusqueda = null;
            mReversion = null;

            m = mantenimientoRN.nuevoMovimiento(CODMT, PTOMT);
            cargarFormulariosBusqueda();

        } catch (ExcepcionGeneralSistema ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("Error: iniciarMovimiento: " + ex);

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error: iniciarMovimiento: " + ex);
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
            m = mantenimientoRN.guardar(m);

            if (m.getComprobante().getTipoComprobante().equals("R")) {

                mBusqueda.setMovimientoReversion(m);
                mantenimientoRN.guardar(mBusqueda);
            }

            JsfUtil.addInfoMessage("El documento " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");

            muestraReporte = false;

            if (nuevo) {
                nuevo();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage("Error al guardar: " + ex);
        }
    }

    public void imprimir() {

        generarReporte();

        PrimeFaces.current().ajax().update("formulario");

        if (muestraReporte) {
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
        }
    }

    public void imprimir(MovimientoMantenimiento movimiento) {

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
                throw new ExcepcionGeneralSistema("No se puede imprimir - El formulario de contabilidad no tienen reporte asociado");
            }

            parameters.put("ID", m.getId());
            parameters.put("CANT_COPIAS", m.getComprobante().getCopias());

            nombreArchivo = m.getFormulario().getCodigo() + "-" + m.getNumeroFormulario();
            reportFactory.exportReportToPdfFile(m.getFormulario().getReporte(), nombreArchivo, parameters);

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

        JsfUtil.addWarningMessage("Separe las direcciones de entrega con punto y coma(;) si desea enviar a más de un destinatario");
    }

    public void revertirMovimiento(MovimientoMantenimiento mSel) {
        try {

            mReversion = mSel;
            m = mantenimientoRN.revertirMovimiento(mReversion);

            JsfUtil.addInfoMessage("El comprobante " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            JsfUtil.addInfoMessage("Se ha revertido el comprobante " + mReversion.getComprobante().getDescripcion() + "-" + mReversion.getPuntoVenta().getCodigo() + "-" + mReversion.getNumeroFormulario(), "");

            buscar();
            nuevo();

        } catch (Exception e) {
            JsfUtil.addErrorMessage("Problemas para generar movimiento de reversión " + e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public void limpiarFiltroBusqueda() {

//        formulario = null;
        mBusqueda = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        muestraReporte = false;
        solicitaEmail = false;
        movimientos = null;
        bienUso = null;
        indiceWizard = 0;
    }

    public void buscar() {

        if (!validarParametros()) {
            return;
        }
        cargarFiltroBusqueda();
        movimientos = mantenimientoRN.getListaByBusqueda(filtro, cantidadRegistros);

        if (movimientos == null || movimientos.isEmpty()) {
            JsfUtil.addWarningMessage("No se han encontrado movimientos");
        }

        modoVista = "B";
    }

    public void buscarPendientes() {

        cargarFiltroPendientes();
        pendientes = mantenimientoRN.getListaByBusqueda(filtro, 100);

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

        // Filtramos movimientos de acuerdo a la sucursal asignada al usuario
        String sFiltro = usuarioSessionBean.getStringInSucursal();

        if (sFiltro != null && !sFiltro.isEmpty()) {
            filtro.put("sucursal.codigo IN", "(" + sFiltro + ")");
        }

        if (!mostrarDebaja) {
            filtro.put("movimientoReversion", " IS NULL");
        }

        if (comprobante != null) {

            filtro.put("comprobante.modulo = ", "'" + comprobante.getModulo() + "'");
            filtro.put("comprobante.codigo = ", "'" + comprobante.getCodigo() + "'");
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

        if (bienUso != null) {
            filtro.put("bienUso.codigo = ", "'" + bienUso.getCodigo() + "'");
        }
    }

    public void cargarFiltroPendientes() {

        filtro.clear();

        // Filtramos movimientos de acuerdo a la sucursal asignada al usuario
        String sFiltro = usuarioSessionBean.getStringInSucursal();

        if (sFiltro != null && !sFiltro.isEmpty()) {
            filtro.put("sucursal.codigo IN", "(" + sFiltro + ")");
        }

        filtro.put("movimientoReversion", " IS NULL");
        filtro.put("estado.codigo = ", "'A'");
        //filtro.put("NOT EXISTS ", "(SELECT a FROM MovimientoEducacion a WHERE a.movimientoAplicado.id = e.id) ");

        if (m.getComprobante().getTipoComprobante().equals("MC")) {
            filtro.put("comprobante.tipoComprobante = ", "'SM'");
        }

        if (bienUso != null) {
            filtro.put("bienUso.codigo = ", "'" + bienUso.getCodigo() + "'");
        }

    }

    public void seleccionar(MovimientoMantenimiento mSel) {

        try {
            if (mSel == null) {
                return;
            }

            m = mSel;
            modoVista = "D";

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible seleccionar movimiento " + ex);
        }
    }

    public void seleccionarMovimientoPendiente(MovimientoMantenimiento mSel) {

        try {

            if (mSel != null) {

                m.setBienUso(mSel.getBienUso());
                m.setDefecto(mSel.getDefecto());
                m.setDetalleDefecto(mSel.getDetalleDefecto());
                m.setObservaciones(mSel.getObservaciones());

            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible seleccionar movimiento " + ex);
        }
    }

    public void procesarFormulario() {

        if (formularioMantenimientoBean.getFormulario() != null) {
            formulario = formularioMantenimientoBean.getFormulario();
        }
    }

    public void procesarBienUso() {

        if (m != null && m.getBienUso() != null) {

            try {
                mantenimientoRN.asignarBienUso(m, m.getBienUso());

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar producto " + ex);
            }
        }
    }

    public void procesarPlanActividad() {

        if (m != null && m.getPlanActividad() != null) {

            try {
                mantenimientoRN.asignarPlanActividad(m, m.getPlanActividad());

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar el Plan de Actividad " + ex);
            }
        }
    }

    /*
    public void procesarCondicionesPago() {

        if (m != null && m.getCurso() != null && m.getGrupo() != null) {

            try {
                educacionRN.asignarCondicionesPago(m, m.getCurso(), m.getGrupo());

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar curso " + ex);
            }
        }
    }

    public void procesarLocalidad() {

        if (m != null && m.getLocalidad() != null) {

            m.setProvincia(m.getLocalidad().getProvincia());
        }
    }
     */
    /**
     * Cargamos en la lista de formularios disponibles los que tienen relación
     * con el circuito que estamos ejecutando.
     */
    private void cargarFormulariosBusqueda() {

        if (m != null) {
            formularioMantenimientoBean.setLista(formularioPorSituacionIVARN.getFormularioByComprobante(m.getComprobante()));
        }
    }

    public List<Producto> completeBienUso(String query) {
        try {
            Map<String, String> filtroProducto = new HashMap<String, String>();

            filtroProducto.put("bienDeUso = ", "'S'");

            return productoRN.getListaByBusqueda(filtroProducto, query, false, 10);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Producto>();
        }
    }

    public List<Usuario> completeResponsable(String query) {
        try {

            return usuarioRN.getUsuarioByBusqueda(query, false);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Usuario>();
        }
    }

    public List<Producto> completeProducto(String query) {
        try {
            Map<String, String> filtroProducto = new HashMap<String, String>();

            // Agregar validación de fechas
            return productoRN.getListaByBusqueda(filtroProducto, query, false, 10);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Producto>();
        }
    }

    public List<Formulario> completeFormulario(String query) {
        try {

//            if (circuito == null) {
//                return null;
//            }
            Comprobante comprobante = null;
            String puntoVenta = PTOMT;

//            if (circuito.getActualizaFacturacion().equals("S") && CODFC != null) {
//
//                comprobante = circuito.getComprobanteFacturacion();
//                puntoVenta = SUCFC;
//
//            } else if (circuito.getActualizaVenta().equals("S") && CODVT != null) {
//
//                comprobante = circuito.getComprobanteVenta();
//                puntoVenta = SUCFC;
//
//            } else if (circuito.getActualizaStock().equals("S") && CODST != null) {
//
//                comprobante = circuito.getComprobanteStock();
//                puntoVenta = SUCST;
//            }
            return formularioPorSituacionIVARN.getFormularioByComprobantePuntoVenta(comprobante, puntoVenta);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Formulario>();
        }
    }

    /*
    public void calcularImportes() {

        educacionRN.calcularImportes(m);
    }
     */
 /*
    public void onMascaraSelect(SelectEvent event) {

        try {
            MascaraContabilidad mascara = (MascaraContabilidad) event.getObject();

            educacionRN.asignarMascara(m, mascara);
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + ex);
        }
    }
     */
 /*
    public void onFechaSelect(SelectEvent event) {

        try {

            educacionRN.asignarPeriodoContable(e);

            if (m.getPeriodoContable() == null) {
                JsfUtil.addErrorMessage("No existe un período contable activo para la fecha seleccionada, no podrá guardar el comprobante");
            }

        } catch (ExcepcionGeneralSistema ex) {
            Logger.getLogger(MovimientoEducacionBean.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("Problemas para asignar período contable");
        }

    }

    public void onRowToggle(ToggleEvent event) {

        centroCosto = ((ItemMovimientoContabilidadCentroCosto) event.getData()).getCentroCosto();
    }

    public List<SubCuenta> completeSubCuenta(String query) {

        try {
            return subCuentaRN.getListaByBusqueda(centroCosto, query, false, 10);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<SubCuenta>();
        }
    }

    public void nuevoItemSubCuenta(ItemMovimientoContabilidadCentroCosto cc) {

        try {
            contabilidadRN.nuevoItemMovimientoSubCuenta(cc);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item Sub Cuenta " + ex);
        }
    }

    public void eliminarItemSubCuenta(ItemMovimientoContabilidadCentroCosto itemCentroCosto, ItemMovimientoContabilidadSubcuenta itemSubCuenta) {

        try {
            contabilidadRN.eliminarItemSubCuenta(itemCentroCosto, itemSubCuenta);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible quitar item Sub Cuenta " + ex);
        }
    }

     */
    //------------------------------------------------------------------------------------------------------------
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

    public FormularioMantenimientoBean getFormularioMantenimientoBean() {
        return formularioMantenimientoBean;
    }

    public void setFormularioMantenimientoBean(FormularioMantenimientoBean formularioMantenimientoBean) {
        this.formularioMantenimientoBean = formularioMantenimientoBean;
    }

    public MovimientoMantenimiento getM() {
        return m;
    }

    public void setM(MovimientoMantenimiento m) {
        this.m = m;
    }

    //public ItemMovimientoContabilidad getItem() {
    //     return item;
    // }
    // public void setItem(ItemMovimientoContabilidad item) {
    //     this.item = item;
    // }
    public MovimientoMantenimiento getmReversion() {
        return mReversion;
    }

    public void setmReversion(MovimientoMantenimiento mReversion) {
        this.mReversion = mReversion;
    }

    public MovimientoMantenimiento getmBusqueda() {
        return mBusqueda;
    }

    public void setmBusqueda(MovimientoMantenimiento mBusqueda) {
        this.mBusqueda = mBusqueda;
    }

    // public CentroCosto getCentroCosto() {
    //     return centroCosto;
    // }
    //  public void setCentroCosto(CentroCosto centroCosto) {
    //      this.centroCosto = centroCosto;
    //  }
    public Producto getBienUso() {
        return bienUso;
    }

    public void setBienUso(Producto bienUso) {
        this.bienUso = bienUso;
    }

    public List<MovimientoMantenimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoMantenimiento> movimientos) {
        this.movimientos = movimientos;
    }

    public String getPTOMT() {
        return PTOMT;
    }

    public void setPTOMT(String PTOMT) {
        this.PTOMT = PTOMT;
    }

    public String getCODMT() {
        return CODMT;
    }

    public void setCODMT(String CODMT) {
        this.CODMT = CODMT;
    }

    public ActividadBean getActividadBean() {
        return actividadBean;
    }

    public void setActividadBean(ActividadBean actividadBean) {
        this.actividadBean = actividadBean;
    }

    public ItemMovimientoMantenimientoActividad getItemActividad() {
        return itemActividad;
    }

    public void setItemActividad(ItemMovimientoMantenimientoActividad itemActividad) {
        this.itemActividad = itemActividad;
    }

    public ItemMovimientoMantenimientoRecurso getItemRecurso() {
        return itemRecurso;
    }

    public void setItemRecurso(ItemMovimientoMantenimientoRecurso itemRecurso) {
        this.itemRecurso = itemRecurso;
    }

    public List<MovimientoMantenimiento> getPendientes() {
        return pendientes;
    }

    public void setPendientes(List<MovimientoMantenimiento> pendientes) {
        this.pendientes = pendientes;
    }

    public MovimientoEducacion getmPendiente() {
        return mPendiente;
    }

    public void setmPendiente(MovimientoEducacion mPendiente) {
        this.mPendiente = mPendiente;
    }

}
