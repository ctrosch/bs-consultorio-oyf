/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.web;

import bs.contabilidad.web.*;
import bs.educacion.modelo.Arancel;
import bs.educacion.modelo.Carrera;
import bs.educacion.modelo.Curso;
import bs.educacion.modelo.EstadoEducacion;
import bs.educacion.modelo.MovimientoEducacion;
import bs.educacion.modelo.PlanEstudio;
import bs.educacion.rn.ArancelRN;
import bs.educacion.rn.CarreraRN;
import bs.educacion.rn.CursoRN;
import bs.educacion.rn.EducacionRN;
import bs.educacion.rn.PlanEstudioRN;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.modelo.TipoEntidad;
import bs.entidad.rn.EntidadRN;
import bs.entidad.rn.TipoEntidadRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Formulario;
import bs.global.rn.FormularioPorSituacionIVARN;
import bs.global.util.JsfUtil;
import bs.global.util.ReportFactory;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.MailFactory;
import bs.seguridad.web.UsuarioSessionBean;
import java.io.Serializable;
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
import javax.inject.Inject;
import org.primefaces.PrimeFaces;

/**
 * @author ctrosch
 */
@ManagedBean
@ViewScoped
public class MovimientoEducacionBean extends GenericBean implements Serializable {

    @EJB
    private EducacionRN educacionRN;
    @EJB
    private FormularioPorSituacionIVARN formularioPorSituacionIVARN;
    @EJB
    private CarreraRN carreraRN;
    @EJB
    private CursoRN cursoRN;
    @EJB
    private ArancelRN arancelRN;
    @EJB
    private PlanEstudioRN planEstudioRN;

    @EJB
    private MailFactory mailFactoryBean;
    @EJB
    private EntidadRN entidadRN;
    @EJB
    private TipoEntidadRN tipoEntidadRN;

    //--------------------------------------------------
    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    @Inject
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    @ManagedProperty(value = "#{cuentaContableBean}")
    protected CuentaContableBean cuentaContableBean;

    @ManagedProperty(value = "#{formularioEducacionBean}")
    protected FormularioEducacionBean formularioEducacionBean;

    protected MovimientoEducacion m;
    protected List<MovimientoEducacion> movimientos;
    protected List<MovimientoEducacion> pendientes;
    protected MovimientoEducacion mReversion;
    protected MovimientoEducacion mBusqueda;
    protected MovimientoEducacion mPendiente;
    protected MovimientoEducacion item;

    //Datos inicializacion registracion
    protected String PTOED = "";
    protected String CODED = "";

    //Atributos para filtros
    private EntidadComercial alumno;
    private Carrera carrera;
    private PlanEstudio planEstudio;
    private Arancel arancel;
    private Curso curso;
    private EstadoEducacion estado;
    private TipoEntidad tipoEntidad;

    public MovimientoEducacionBean() {

        titulo = "Movimiento de Educación";
        muestraReporte = false;
        numeroFormularioDesde = 1;
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();
                tipoEntidad = tipoEntidadRN.getTipoEntidad("4");

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

            m = educacionRN.nuevoMovimiento(CODED, PTOED);

            comprobante = m.getComprobante();
            puntoVenta = m.getPuntoVenta();

            /**
             * if (comprobante.getTipoComprobante().equals("SC") ||
             * comprobante.getTipoComprobante().equals("SM") ||
             * comprobante.getTipoComprobante().equals("EC") ||
             * comprobante.getTipoComprobante().equals("EM")) {
             *
             * buscarPendientes(); *
             * PrimeFaces.current().executeScript("PF('dlgPendiente').show()");
             * }*
             */
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
            m = educacionRN.guardar(m);

            if (m.getComprobante().getTipoComprobante().equals("R")) {

                mBusqueda.setMovimientoReversion(m);
                educacionRN.guardar(mBusqueda);
            }

            JsfUtil.addInfoMessage("El documento " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            cuentaContableBean.setCuentaContable(null);
            muestraReporte = false;

            if (nuevo) {
                nuevo();
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al guardar: " + ex);
        }
    }

    public void seleccionar(MovimientoEducacion m) {

        if (m == null) {
            return;
        }

        this.m = m;
        modoVista = "D";
    }

    public void imprimir() {

        generarReporte();

        PrimeFaces.current().ajax().update("formulario");

        if (muestraReporte) {
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
        }
    }

    public void imprimir(MovimientoEducacion movimiento) {

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

    public void revertirMovimiento(MovimientoEducacion mSel) {
        try {

            mReversion = mSel;
            m = educacionRN.revertirMovimiento(mReversion);

            JsfUtil.addInfoMessage("El comprobante " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            JsfUtil.addInfoMessage("Se ha revertido el comprobante " + mReversion.getComprobante().getDescripcion() + "-" + mReversion.getPuntoVenta().getCodigo() + "-" + mReversion.getNumeroFormulario(), "");

            buscar();

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
        alumno = null;
        curso = null;
        carrera = null;
        arancel = null;
        planEstudio = null;
        estado = null;
        indiceWizard = 0;

        buscar();

    }

    public void buscar() {

        if (!validarParametros()) {
            return;
        }
        cargarFiltroBusqueda();
        movimientos = educacionRN.getListaByBusqueda(filtro, cantidadRegistros);

        if (movimientos == null || movimientos.isEmpty()) {
            JsfUtil.addWarningMessage("No se han encontrado movimientos");
        }
        modoVista = "B";
    }

    public void buscarPendientes() {

        cargarFiltroPendientes();
        pendientes = educacionRN.getListaByBusqueda(filtro, 100);

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
        String sFiltro = usuarioSessionBean.getStringInSucursal();

        if (sFiltro != null && !sFiltro.isEmpty()) {
            filtro.put("sucursal.codigo IN", "(" + sFiltro + ")");
        }

//        if (!mostrarDebaja) {
//            filtro.put("movimientoReversion", " IS NULL");
//        }
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

        if (alumno != null) {

            filtro.put("alumno.nrocta = ", "'" + alumno.getNrocta() + "'");
        }

        if (carrera != null) {

            filtro.put("carrera.codigo = ", "'" + carrera.getCodigo() + "'");
        }

        if (planEstudio != null) {

            filtro.put("planEstudio.codigo = ", "'" + planEstudio.getCodigo() + "'");
        }

        if (arancel != null) {

            filtro.put("arancel.codigo = ", "'" + arancel.getCodigo() + "'");
        }

        if (curso != null) {

            filtro.put("curso.codigo = ", "'" + curso.getCodigo() + "'");
        }

        if (estado != null) {

            filtro.put("estado.codigo = ", "'" + estado.getCodigo() + "'");
        }

        if (seleccionaPendiente) {

        }
    }

    public void cargarFiltroPendientes() {

        filtro.clear();

        // Filtramos movimientos de acuerdo a la sucursal asignada al usuario
        String sFiltro = usuarioSessionBean.getStringInSucursal();

        filtro.put("movimientoReversion", " IS NULL");
        filtro.put("estado.codigo = ", "'A'");
        //filtro.put("NOT EXISTS ", "(SELECT a FROM MovimientoEducacion a WHERE a.movimientoAplicado.id = e.id) ");

        if (m.getComprobante().getTipoComprobante().equals("IN")) {
            filtro.put("comprobante.tipoComprobante = ", "'PR'");
        } else {
            //En inscripciones no filtramos por sucursal ya que se pre-inscribe cualquier
            //Lugar y se inscribe en otro
            if (sFiltro != null && !sFiltro.isEmpty()) {
                filtro.put("sucursal.codigo IN", "(" + sFiltro + ")");
            }
        }

        if (m.getComprobante().getTipoComprobante().equals("SC")) {
            filtro.put("comprobante.tipoComprobante = ", "'IN'");
        }

        if (m.getComprobante().getTipoComprobante().equals("SM")) {
            filtro.put("comprobante.tipoComprobante = ", "'SC'");
        }

        if (m.getComprobante().getTipoComprobante().equals("EC")) {
            filtro.put("comprobante.tipoComprobante = ", "'SC'");
        }

        if (m.getComprobante().getTipoComprobante().equals("EM")) {
            filtro.put("comprobante.tipoComprobante = ", "'SM'");
        }

        if (alumno != null) {

            filtro.put("alumno.nrocta = ", "'" + alumno.getNrocta() + "'");
        }

        if (carrera != null) {

            filtro.put("carrera.codigo = ", "'" + carrera.getCodigo() + "'");
        }

        if (curso != null) {

            filtro.put("curso.codigo = ", "'" + curso.getCodigo() + "'");
        }

        if (estado != null) {

            filtro.put("estado.codigo = ", "'" + estado.getCodigo() + "'");
        }
    }

    public void seleccionarMovimiento(MovimientoEducacion mSel) {

        try {
            m = mSel;
            // educacionRN.calcularImportes(mSel);
            modoVista = "D";

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible seleccionar movimiento " + ex);
        }
    }

    public void seleccionarMovimientoPendiente(MovimientoEducacion mSel) {

        try {
            mPendiente = mSel;

            if (mPendiente != null) {

                m.setAlumno(mPendiente.getAlumno());
                procesarAlumno();

                m.setCarrera(mPendiente.getCarrera());
                m.setPlanEstudio(mPendiente.getPlanEstudio());
                m.setArancel(mPendiente.getArancel());
                m.setCurso(mPendiente.getCurso());

                m.setPorcentajeBonificacion(mPendiente.getPorcentajeBonificacion());
                m.setObservaciones(mPendiente.getObservaciones());

                //El tipo de comprobantes varios de educación no aplica movimientos
                if (m.getComprobante().getTipoComprobante().equals("VS")) {
                    m.setMovimientoAplicado(mPendiente);
                }

                procesarAranceles();
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible seleccionar movimiento " + ex);
        }
    }

    public void procesarFormulario() {

        if (formularioEducacionBean.getFormulario() != null) {
            formulario = formularioEducacionBean.getFormulario();
        }
    }

    public void procesarAlumno() {

        if (m != null && m.getAlumno() != null) {

            try {
                educacionRN.asignarAlumno(m, m.getAlumno());

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar alumno " + ex);
            }
        }
    }

    public void procesarCarrera() {

        if (m != null && m.getCarrera() != null) {

            m.setPlanEstudio(null);
            m.setArancel(null);
            m.setCurso(null);

        }
    }

    public void procesarAranceles() {

        if (m != null && m.getCarrera() != null && m.getArancel() != null) {

            if (m.getCurso() != null && !m.getCarrera().equals(m.getCurso().getPlanEstudio().getCarrera())) {
                return;
            }

            try {
                educacionRN.asignarAranceles(m, m.getCarrera(), m.getCurso(), m.getArancel());

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar aranceles " + ex);
            }
        }
    }

    public void procesarLocalidad() {

        if (m != null && m.getLocalidad() != null) {

            m.setProvincia(m.getLocalidad().getProvincia());
        }
    }

    public void procesarBonificacion() {

        try {

            if (m.getItemsMovimiento() != null) {
                m.getItemsMovimiento().forEach(i -> {
                    i.setPorcentaTotalBonificacion(m.getPorcentajeBonificacion());
                });
            }

            educacionRN.calcularImportes(m);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("Error calculando importes " + ex);
        }

    }

    public void calcularImportes() {

        try {
            educacionRN.calcularImportes(m);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("Error calculando importes " + ex);
        }

    }

    public List<EntidadComercial> completeAlumno(String query) {
        try {
            filtro.clear();

            //Si es sucursal general, el sistema permite al usuario que elija
            //alumnos de todas las sucursales a la que pertenece. de lo contrario
            //solo busca alumnos de la sucursal del movimiento.
            if (m.getSucursal().getGeneral().equals("S")) {

                // Filtramos movimientos de acuerdo a la sucursal asignada al usuario
                String sFiltroSuc = usuarioSessionBean.getStringInSucursal();

                if (sFiltroSuc != null && !sFiltroSuc.isEmpty()) {
                    filtro.put("sucursal.codigo IN", "(" + sFiltroSuc + ")");
                }
            } else {
                filtro.put("sucursal.codigo = ", "'" + m.getSucursal().getCodigo() + "'");
            }

            return entidadRN.getListaByBusqueda(filtro, query, tipoEntidad, mostrarDebaja, 10);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public List<Carrera> completeCarrera(String query) {
        try {
            filtro.clear();

            List<Carrera> carreras = carreraRN.getListaByBusqueda(filtro, query, false, 0);
            List<Carrera> resultado = new ArrayList<>();

            if (carreras != null) {
                carreras.forEach(i -> {

                    i.getSucursales().forEach(s -> {

                        if (s.getSucursal().equals(m.getSucursal())) {
                            resultado.add(i);
                        };
                    });
                });
            }

            return resultado;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Carrera>();
        }
    }

    public List<PlanEstudio> completePlanEstudio(String query) {
        try {

            if (m.getCarrera() == null) {
                JsfUtil.addErrorMessage("Primero debe seleccionar una carrera");
                return null;
            }

            filtro.clear();
            filtro.put("carrera.codigo = ", "'" + m.getCarrera().getCodigo() + "'");

            return planEstudioRN.getListaByBusqueda(filtro, query, false, 0);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public List<PlanEstudio> completePlanEstudioBusqueda(String query) {
        try {

            if (carrera == null) {
                JsfUtil.addErrorMessage("Primero debe seleccionar una carrera");
                return null;
            }

            filtro.clear();
            filtro.put("carrera.codigo = ", "'" + carrera.getCodigo() + "'");

            return planEstudioRN.getListaByBusqueda(filtro, query, false, 0);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public List<Curso> completeCurso(String query) {
        try {

            if (m.getCarrera() == null) {
                JsfUtil.addErrorMessage("Primero debe seleccionar una carrera");
                return null;
            }

            filtro.clear();
            filtro.put("sucursal.codigo = ", "'" + m.getSucursal().getCodigo() + "'");
            filtro.put("planEstudio.codigo = ", "'" + m.getPlanEstudio().getCodigo() + "'");
            //filtro.put("fechaInicio > ", UtilFechas.getFechaSQL(UtilFechas.sumar(m.getFechaMovimiento(), Calendar.DAY_OF_MONTH, -90)));

            return cursoRN.getListaByBusqueda(filtro, query, false, 0);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public List<Arancel> completeArancel(String query) {
        try {
            if (m.getCarrera() == null) {
                JsfUtil.addErrorMessage("Primero debe seleccionar una carrera");
                return null;
            }

            filtro.clear();
            filtro.put("carrera.codigo =", "'" + m.getCarrera().getCodigo() + "'");

            return arancelRN.getListaByBusqueda(filtro, "", false, 0);

//            return m.getCarrera().getAranceles().stream().filter(i
//                    -> i.getAuditoria().getDebaja().equals("N")
//                    && m.getFechaMovimiento().after(i.getFechaDesde())
//                    && m.getFechaMovimiento().before(i.getFechaHasta())).collect(Collectors.toList());
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public List<Arancel> completeArancelBusqueda(String query) {
        try {
            if (carrera == null) {
                JsfUtil.addErrorMessage("Primero debe seleccionar una carrera");
                return null;
            }

            filtro.clear();
            filtro.put("carrera.codigo =", "'" + carrera.getCodigo() + "'");

            return arancelRN.getListaByBusqueda(filtro, "", false, 0);

//            return m.getCarrera().getAranceles().stream().filter(i
//                    -> i.getAuditoria().getDebaja().equals("N")
//                    && m.getFechaMovimiento().after(i.getFechaDesde())
//                    && m.getFechaMovimiento().before(i.getFechaHasta())).collect(Collectors.toList());
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
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

                if (accionEnLote.equals("R")) {
                    educacionRN.generarComprobanteReincripcion(me);
                }

                if (accionEnLote.equals("B")) {
                    educacionRN.revertirMovimiento(me);
                }

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("Problemas para generar reinscripción");
            }
        });

    }

    //------------------------------------------------------------------------------------------------------------
    public MailFactory getMailFactoryBean() {
        return mailFactoryBean;
    }

    public void setMailFactoryBean(MailFactory mailFactoryBean) {
        this.mailFactoryBean = mailFactoryBean;
    }

    public String getPTOED() {
        return PTOED;
    }

    public void setPTOED(String PTOED) {
        this.PTOED = PTOED;
    }

    public String getCODED() {
        return CODED;
    }

    public void setCODED(String CODED) {
        this.CODED = CODED;
    }

    public List<MovimientoEducacion> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoEducacion> movimientos) {
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

    public CuentaContableBean getCuentaContableBean() {
        return cuentaContableBean;
    }

    public void setCuentaContableBean(CuentaContableBean cuentaContableBean) {
        this.cuentaContableBean = cuentaContableBean;
    }

    public FormularioEducacionBean getFormularioEducacionBean() {
        return formularioEducacionBean;
    }

    public void setFormularioEducacionBean(FormularioEducacionBean formularioEducacionBean) {
        this.formularioEducacionBean = formularioEducacionBean;
    }

    public MovimientoEducacion getM() {
        return m;
    }

    public void setM(MovimientoEducacion m) {
        this.m = m;
    }

    //public ItemMovimientoContabilidad getItem() {
    //     return item;
    // }
    // public void setItem(ItemMovimientoContabilidad item) {
    //     this.item = item;
    // }
    public MovimientoEducacion getmReversion() {
        return mReversion;
    }

    public void setmReversion(MovimientoEducacion mReversion) {
        this.mReversion = mReversion;
    }

    public MovimientoEducacion getmBusqueda() {
        return mBusqueda;
    }

    public void setmBusqueda(MovimientoEducacion mBusqueda) {
        this.mBusqueda = mBusqueda;
    }

    // public CentroCosto getCentroCosto() {
    //     return centroCosto;
    // }
    //  public void setCentroCosto(CentroCosto centroCosto) {
    //      this.centroCosto = centroCosto;
    //  }
    public EntidadComercial getAlumno() {
        return alumno;
    }

    public void setAlumno(EntidadComercial alumno) {
        this.alumno = alumno;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public EstadoEducacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoEducacion estado) {
        this.estado = estado;
    }

    public List<MovimientoEducacion> getPendientes() {
        return pendientes;
    }

    public void setPendientes(List<MovimientoEducacion> pendientes) {
        this.pendientes = pendientes;
    }

    public PlanEstudio getPlanEstudio() {
        return planEstudio;
    }

    public void setPlanEstudio(PlanEstudio planEstudio) {
        this.planEstudio = planEstudio;
    }

    public Arancel getArancel() {
        return arancel;
    }

    public void setArancel(Arancel arancel) {
        this.arancel = arancel;
    }

}
