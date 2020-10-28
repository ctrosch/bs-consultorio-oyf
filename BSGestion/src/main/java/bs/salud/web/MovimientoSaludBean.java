/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.web;

import bs.entidad.modelo.EntidadComercial;
import bs.entidad.modelo.TipoEntidad;
import bs.entidad.rn.EntidadRN;
import bs.entidad.rn.TipoEntidadRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.ArchivoAdjunto;
import bs.global.modelo.Formulario;
import bs.global.util.JsfUtil;
import bs.global.util.ReportFactory;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.MailFactory;
import bs.salud.modelo.EstadoSalud;
import bs.salud.modelo.MovimientoSalud;
import bs.salud.rn.EstadoSaludRN;
import bs.salud.rn.SaludRN;
import bs.seguridad.web.UsuarioSessionBean;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 * @author Guillermo
 */
@ManagedBean
@ViewScoped
public class MovimientoSaludBean extends GenericBean implements Serializable {

    @EJB
    private SaludRN saludRN;

    @EJB
    private EstadoSaludRN estadoSaludRN;

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

    @ManagedProperty(value = "#{formularioSaludBean}")
    protected FormularioSaludBean formularioSaludBean;

    protected MovimientoSalud m;
    protected List<MovimientoSalud> movimientos;
    protected List<MovimientoSalud> pendientes;
    protected MovimientoSalud mReversion;
    protected MovimientoSalud mBusqueda;
    protected MovimientoSalud mPendiente;
    protected MovimientoSalud item;
    private EntidadComercial profesional;
    private List<Date> listaDeTurnos;
    private List<MovimientoSalud> lista;
    private Date horaMovimiento;
    private boolean esPendiente;

    private ScheduleModel eventModel;
    private ScheduleEvent event;

    //Datos inicializacion registracion
    protected String PTOSA = "";
    protected String CODSA = "";

    //Atributos para filtros
    private EntidadComercial paciente;
    private EstadoSalud estado;
    private TipoEntidad tipoEntidad;
    private EntidadComercial profesionalFiltro;

    public MovimientoSaludBean() {

        titulo = "Movimiento de Salud";
        muestraReporte = false;
        numeroFormularioDesde = 1;
    }

    @PostConstruct
    public void init() {

        eventModel = new DefaultScheduleModel();
        event = new DefaultScheduleEvent();

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();
                tipoEntidad = tipoEntidadRN.getTipoEntidad("6");

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
            esNuevo = true;
            nombreArchivo = "";
            muestraReporte = false;
            mBusqueda = null;
            mReversion = null;

            m = saludRN.nuevoMovimiento(CODSA, PTOSA);

            comprobante = m.getComprobante();
            puntoVenta = m.getPuntoVenta();

            if (m.getComprobante().getFiltraPorProfesional().equals("S") && usuarioSessionBean.getUsuario().getProfesionalMedico() != null) {
                if (m.getComprobante().getTipoComprobante().equals("RT")) {
                    profesional = usuarioSessionBean.getUsuario().getProfesionalMedico();
                    procesarProfesionalCalendario();
                } else {

                    m.setProfesional(usuarioSessionBean.getUsuario().getProfesionalMedico());
                    procesarProfesional();
                }
            }

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
        esPendiente = false;
        modoVista = "D";

    }

    public void guardar(boolean nuevo) {

        try {

            m = saludRN.guardar(m);

            if (m.getComprobante().getTipoComprobante().equals("R")) {

                mBusqueda.setMovimientoReversion(m);
                saludRN.guardar(mBusqueda);
            }

            if (m.getComprobante().getTipoComprobante().equals("RT")) {
                actualizarCalendario();
            }
            JsfUtil.addInfoMessage("El documento " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
//            cuentaContableBean.setCuentaContable(null);
            muestraReporte = false;

            if (nuevo) {
                nuevo();
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al guardar: " + ex);
        }
    }

    public void seleccionar(MovimientoSalud m) {

        if (m == null) {
            return;
        }

        this.m = m;
        modoVista = "D";
        esNuevo = false;
    }

    public void imprimir() {

        generarReporte();

        PrimeFaces.current().ajax().update("formulario");

        if (muestraReporte) {
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
        }
    }

    public void imprimir(MovimientoSalud movimiento) {

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

    public void revertirMovimiento(MovimientoSalud mSel) {
        try {

            mReversion = mSel;
            m = saludRN.revertirMovimiento(mReversion);

            JsfUtil.addInfoMessage("El comprobante " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            JsfUtil.addInfoMessage("Se ha revertido el comprobante " + mReversion.getComprobante().getDescripcion() + "-" + mReversion.getPuntoVenta().getCodigo() + "-" + mReversion.getNumeroFormulario(), "");

            if (mReversion.getComprobante().getTipoComprobante().equals("RT")) {
                actualizarCalendario();
            } else {
                buscar();
            }

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
        paciente = null;
        profesionalFiltro = null;
        estado = null;
        indiceWizard = 0;

        buscar();

    }

    public void buscar() {

        if (!validarParametros()) {
            return;
        }
        cargarFiltroBusqueda();
        movimientos = saludRN.getListaByBusqueda(filtro, cantidadRegistros);

        if (movimientos == null || movimientos.isEmpty()) {
            JsfUtil.addWarningMessage("No se han encontrado movimientos");
        }
        modoVista = "B";
    }

    public void buscarPendientes() {

        cargarFiltroPendientes();
        pendientes = saludRN.getListaByBusqueda(filtro, 100);

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

        if (paciente != null) {

            filtro.put("paciente.nrocta = ", "'" + paciente.getNrocta() + "'");
        }

        if (m.getComprobante().getFiltraPorProfesional().equals("S") && usuarioSessionBean.getUsuario().getProfesionalMedico() != null) {
            filtro.put("profesional.nrocta = ", "'" + usuarioSessionBean.getUsuario().getProfesionalMedico().getNrocta() + "'");
        } else {
            if (profesionalFiltro != null) {

                filtro.put("profesional.nrocta = ", "'" + profesionalFiltro.getNrocta() + "'");
            }
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

        //filtro.put("NOT EXISTS ", "(SELECT a FROM MovimientoEducacion a WHERE a.movimientoAplicado.id = e.id) ");
        if (m.getComprobante().getTipoComprobante().equals("SA")) {
            filtro.put("comprobante.tipoComprobante = ", "'RT'");
            filtro.put("estado.codigo = ", "'R'");
        }

        if (m.getComprobante().getTipoComprobante().equals("IA")) {
            filtro.put("comprobante.tipoComprobante = ", "'SA'");
            filtro.put("estado.codigo = ", "'E'");
        }

        if (paciente != null) {

            filtro.put("paciente.nrocta = ", "'" + paciente.getNrocta() + "'");
        }

        if (estado != null) {

            filtro.put("estado.codigo = ", "'" + estado.getCodigo() + "'");
        }

        if (m.getComprobante().getFiltraPorProfesional().equals("S") && usuarioSessionBean.getUsuario().getProfesionalMedico() != null) {
            filtro.put("profesional.nrocta = ", "'" + usuarioSessionBean.getUsuario().getProfesionalMedico().getNrocta() + "'");
        } else {
            if (profesionalFiltro != null) {

                filtro.put("profesional.nrocta = ", "'" + profesionalFiltro.getNrocta() + "'");
            }
        }

    }

    public void seleccionarMovimiento(MovimientoSalud mSel) {

        try {
            m = mSel;
            modoVista = "D";
            esNuevo = false;

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible seleccionar movimiento " + ex);
        }

    }

    public List<EntidadComercial> completePaciente(String query) {
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

    public void completeHorario() {
        listaDeTurnos = new ArrayList<Date>();
        if (m.getFechaMovimiento() != null && m.getProfesional() != null) {

            try {

                listaDeTurnos = saludRN.completeHorario(m.getFechaMovimiento(), m.getProfesional());

            } catch (Exception ex) {

                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);

            }
        }
    }

    public String formatHora(Date hora, String pattern) {
        return (new SimpleDateFormat(pattern)).format(hora);
    }

    public void seleccionarMovimientoPendiente(MovimientoSalud mSel) {

        try {
            mPendiente = mSel;

            if (mPendiente != null) {
                m.setProfesional(mPendiente.getProfesional());
                m.setNombreProfesional(mPendiente.getNombreProfesional());

                m.setPaciente(mPendiente.getPaciente());
                procesarPaciente();

                m.setObraSocial(mPendiente.getObraSocial());
                m.setNombreObraSocial(mPendiente.getNombreObraSocial());
                m.setNumeroAfiliado(mPendiente.getNumeroAfiliado());

                m.setHoraMovimiento(mPendiente.getHoraMovimiento());

                m.setObservaciones(mPendiente.getObservaciones());

                m.setMovimientoAplicado(mPendiente);

                esPendiente = true;
                esNuevo = false;

            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible seleccionar movimiento " + ex);
        }
    }

    public void procesarFormulario() {

        if (formularioSaludBean.getFormulario() != null) {
            formulario = formularioSaludBean.getFormulario();
        }
    }

    public void procesarPaciente() {

        if (m != null && m.getPaciente() != null) {

            try {
                saludRN.asignarPaciente(m, m.getPaciente());

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar paciente " + ex);
            }
        }
    }

    public void procesarProfesional() {

        if (m != null && m.getProfesional() != null) {

            try {

                saludRN.asignarProfesional(m, m.getProfesional());

                if (!m.getComprobante().getTipoComprobante().equals("IA")) {
                    completeHorario();
                }

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar profesional" + ex);
            }
        }
    }

    public void procesarProfesionalCalendario() {

        if (m != null && profesional != null) {

            try {

                saludRN.asignarProfesional(m, profesional);
                actualizarCalendario();

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar profesional o completar horario" + ex);
            }
        }
    }

    public void actualizarCalendario() {
        try {
            lista = new ArrayList<>();
            lista = saludRN.getMovimientoSaludEspera(m.getProfesional());
            eventModel = new DefaultScheduleModel();
            eventModel = saludRN.actualizarCalendario(lista);

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar calendario" + ex);
        }
    }

    public void procesarObraSocial() {

        if (m != null && m.getObraSocial() != null) {

            try {
                saludRN.asignarObraSocial(m, m.getObraSocial(), m.getPaciente());

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar obra social " + ex);
            }
        }
    }

    public void procesarLocalidad() {

        if (m != null && m.getLocalidad() != null) {

            m.setProvincia(m.getLocalidad().getProvincia());
        }
    }

    public void onEventSelect(SelectEvent selectEvent) {
        m = saludRN.getMovimiento(saludRN.getIdTurno(selectEvent));
        horaMovimiento = null;
        esNuevo = false;
        completeHorario();
    }

    public void procesarHora() {
        if (horaMovimiento != null) {
            m.setHoraMovimiento(horaMovimiento);
        }
    }

    public void onDateSelect(SelectEvent selectEvent) {

        nuevo();
        m = saludRN.nuevoTurno(m, selectEvent);
        m.setProfesional(profesional);
        procesarProfesional();
        completeHorario();

    }

    @Override
    public void upload(FileUploadEvent event) {

        try {
            copiarArchivoAdjunto(event.getFile().getFileName(), event.getFile().getInputstream());
            JsfUtil.addInfoMessage("El archivo ha sido procesada con éxito");
        } catch (IOException e) {
            System.err.println("Error subiendo archivo " + e);
            JsfUtil.addErrorMessage("No es posible procesar el archivo");
        }
    }

    /**
     * Copiar un archivo a la carpeta temporales de la organizacion
     *
     * @param fileName
     * @param in
     * @return Path del archivo generado
     */
    public void copiarArchivoAdjunto(String fileName, InputStream in) {
        try {

            nuevoAdjunto();

            ArchivoAdjunto archivoAdjunto = m.getAdjuntos().get(m.getAdjuntos().size() - 1);

            SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");

            String[] split = fileName.split("\\.");
            String extension = split[split.length - 1].toLowerCase();
            String archivo = m.getPaciente().getNrocta() + "_" + sdf.format(new Date()) + "_" + JsfUtil.getCadenaAlfanumAleatoria(5) + "." + extension;

            File file = new File(aplicacionBean.getParametro().getPathCarpetaProductos() + archivo);

            // write the inputStream to a FileOutputStream
            OutputStream out = new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            archivoAdjunto.setPathArchivo(aplicacionBean.getParametro().getUrlCarpetaProductos() + archivo);

//            if (!esNuevo) {
//                saludRN.guardar(m);
//            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, "Error cargando archivo: " + e);
        }
    }

    public void nuevoAdjunto() {

        try {
            saludRN.nuevoAdjunto(m);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item: " + ex);
        }
    }

    public void eliminarAdjunto(ArchivoAdjunto archivoAdjunto) {

        try {
            saludRN.eliminarAdjunto(m, archivoAdjunto);
            JsfUtil.addWarningMessage("Se ha borrado el Adjunto ");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + ex);
        }
    }

    //------------------------------------------------------------------------------------------------------------
    public MailFactory getMailFactoryBean() {
        return mailFactoryBean;
    }

    public void setMailFactoryBean(MailFactory mailFactoryBean) {
        this.mailFactoryBean = mailFactoryBean;
    }

    public String getPTOSA() {
        return PTOSA;
    }

    public void setPTOSA(String PTOSA) {
        this.PTOSA = PTOSA;
    }

    public String getCODSA() {
        return CODSA;
    }

    public void setCODSA(String CODSA) {
        this.CODSA = CODSA;
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

    public FormularioSaludBean getFormularioSaludBean() {
        return formularioSaludBean;
    }

    public void setFormularioSaludBean(FormularioSaludBean formularioSaludBean) {
        this.formularioSaludBean = formularioSaludBean;
    }

    public MovimientoSalud getM() {
        return m;
    }

    public void setM(MovimientoSalud m) {
        this.m = m;
    }

    public MovimientoSalud getmReversion() {
        return mReversion;
    }

    public void setmReversion(MovimientoSalud mReversion) {
        this.mReversion = mReversion;
    }

    public MovimientoSalud getmBusqueda() {
        return mBusqueda;
    }

    public void setmBusqueda(MovimientoSalud mBusqueda) {
        this.mBusqueda = mBusqueda;
    }

    public EntidadComercial getPaciente() {
        return paciente;
    }

    public void setPaciente(EntidadComercial paciente) {
        this.paciente = paciente;
    }

    public EstadoSalud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSalud estado) {
        this.estado = estado;
    }

    public EntidadComercial getProfesional() {
        return profesional;
    }

    public void setProfesional(EntidadComercial profesional) {
        this.profesional = profesional;
    }

    public EntidadComercial getProfesionalFiltro() {
        return profesionalFiltro;
    }

    public void setProfesionalFiltro(EntidadComercial profesionalFiltro) {
        this.profesionalFiltro = profesionalFiltro;
    }

    public List<MovimientoSalud> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoSalud> movimientos) {
        this.movimientos = movimientos;
    }

    public List<MovimientoSalud> getPendientes() {
        return pendientes;
    }

    public void setPendientes(List<MovimientoSalud> pendientes) {
        this.pendientes = pendientes;
    }

    public MovimientoSalud getmPendiente() {
        return mPendiente;
    }

    public void setmPendiente(MovimientoSalud mPendiente) {
        this.mPendiente = mPendiente;
    }

    public MovimientoSalud getItem() {
        return item;
    }

    public void setItem(MovimientoSalud item) {
        this.item = item;
    }

    public List<Date> getListaDeTurnos() {
        return listaDeTurnos;
    }

    public void setListaDeTurnos(List<Date> listaDeTurnos) {
        this.listaDeTurnos = listaDeTurnos;
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public List<MovimientoSalud> getLista() {
        return lista;
    }

    public void setLista(List<MovimientoSalud> lista) {
        this.lista = lista;
    }

    public Date getHoraMovimiento() {
        return horaMovimiento;
    }

    public void setHoraMovimiento(Date horaMovimiento) {
        this.horaMovimiento = horaMovimiento;
    }

    public boolean isEsPendiente() {
        return esPendiente;
    }

    public void setEsPendiente(boolean esPendiente) {
        this.esPendiente = esPendiente;
    }

}
