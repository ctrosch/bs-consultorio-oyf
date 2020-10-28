/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tarea.web;

import bs.administracion.modelo.CorreoElectronico;
import bs.estadistica.web.DashboardBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.web.AplicacionBean;
import bs.global.web.GenericBean;
import bs.global.web.MailFactory;
import bs.inversiones.modelo.Proyecto;
import bs.inversiones.web.ProyectoBean;
import bs.produccion.modelo.ItemDetalleMovimientoProduccion;
import bs.produccion.modelo.ItemMovimientoProduccion;
import bs.produccion.modelo.MovimientoProduccion;
import bs.produccion.modelo.Operario;
import bs.produccion.modelo.Planta;
import bs.produccion.modelo.TipoMovimientoProduccion;
import bs.produccion.rn.ProduccionRN;
import bs.produccion.vista.PendienteProduccionGrupo;
import bs.produccion.web.MovimientoProduccionBean;
import bs.seguridad.web.UsuarioSessionBean;
import bs.stock.modelo.Producto;
import bs.stock.rn.ProductoRN;
import bs.stock.rn.TipoProductoRN;
import bs.stock.web.ProductoBean;
import bs.tarea.modelo.Area;
import bs.tarea.modelo.EstadoTarea;
import bs.tarea.modelo.Tarea;
import bs.tarea.modelo.TareaOperario;
import bs.tarea.rn.NotificacionesTareaRN;
import bs.tarea.rn.TareaRN;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class TareaBean extends GenericBean implements Serializable {

    @EJB
    protected TareaRN tareaRN;
    @EJB
    protected ProduccionRN produccionRN;
    @EJB
    protected ProductoRN productoRN;
    @EJB
    protected TipoProductoRN tipoProductoRN;
    @EJB
    protected NotificacionesTareaRN notificacionesTareaRN;
    @EJB
    protected MailFactory mailFactoryBean;

    protected Tarea tarea;
    protected TareaOperario tareaOperario;
    protected List<Tarea> lista;
    protected Operario operarioAgregar;

    //DATS PARA FILTRO
    private Operario operario;
    private Area area;
    private Proyecto proyecto;
    private Planta planta;
    private String estado;
    private Integer numeroOrdenProduccionDesde;
    private Integer numeroOrdenProduccionHasta;

    protected String CIRCOM = "";
    protected String CIRAPL = "";

    protected String MODTA = "TA";
    protected String CODTA = "TAR";
    protected String SUCTA = "0001";

    protected String SUCPR = "0001";
    protected String MODPR = "PD";
    protected String CODPR = "PR";

    protected String SUCPP = "0001";
    protected String MODPP = "ST";
    protected String CODPP = "PP";

    protected String SUCVC = "0001";
    protected String MODVC = "ST";
    protected String CODVC = "VC";

    protected String SUCPH = "0001";
    protected String MODPH = "PD";
    protected String CODPH = "PH";

    protected List<PendienteProduccionGrupo> pendienteProduccion;
    protected PendienteProduccionGrupo movimientoPendiente;

    protected ItemMovimientoProduccion itemPendiente;

    protected List<ItemMovimientoProduccion> procesosPendientes;
    protected List<ItemMovimientoProduccion> horariosPendientes;
    protected List<ItemMovimientoProduccion> productosPendientes;
    protected List<ItemMovimientoProduccion> componentesPendientes;

    @ManagedProperty(value = "#{aplicacionBean}")
    protected AplicacionBean aplicacionBean;

    @ManagedProperty(value = "#{movimientoProduccionBean}")
    protected MovimientoProduccionBean movimientoProduccionBean;

    @ManagedProperty(value = "#{proyectoBean}")
    protected ProyectoBean proyectoBean;

    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;

    @ManagedProperty(value = "#{usuarioSessionBean}")
    protected UsuarioSessionBean usuarioSessionBean;

    @ManagedProperty(value = "#{dashboardBean}")
    protected DashboardBean dashboardBean;

    private boolean imprimeEtiqueta = false;
    private String accion = "";

    public TareaBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                nombreArchivo = "";
                muestraReporte = false;

                productoBean.setTipoProducto(tipoProductoRN.getTipoProducto("99"));
                productoBean.buscar();

                nuevoMovimiento();
                beanIniciado = true;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + e.getMessage());
        }
    }

    public void iniciarVariablesMantenimiento() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                nombreArchivo = "";
                muestraReporte = false;

                productoBean.setTipoProducto(tipoProductoRN.getTipoProducto("99"));
                productoBean.buscar();

                modoVista = "B";
                buscar();
                beanIniciado = true;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + e.getMessage());
        }
    }

    public void nuevoMovimiento() {

        super.iniciar();

        try {

            nombreArchivo = "";
            modoVista = "D";
            muestraReporte = false;

            if (usuarioSessionBean.getUsuario() != null) {

                if (usuarioSessionBean.getUsuario().getOperario() == null) {
                    JsfUtil.addErrorMessage("Usuario sin operario asignado, no tiene permitido generar tareas");
                    return;
                }

                buscarTareaActiva();

                if (lista == null || lista.isEmpty()) {
                    nuevo();
                } else {

                    tarea = lista.get(0);
                    controlTiempo();
                    esNuevo = false;
                    JsfUtil.addInfoMessage("Se ha recuperado su tarea activa");
                }
            } else {
                JsfUtil.addErrorMessage("No se encontró usuario");
            }

            if (tarea.getMovimientoProduccion() != null) {
                cargarDatosProduccion();
            }

        } catch (ExcepcionGeneralSistema egs) {
            JsfUtil.addErrorMessage("nuevoMovimiento" + egs);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, egs);
        }
    }

    public void nuevoMovimientoMantenimiento() {

        super.iniciar();

        try {

            nombreArchivo = "";
            modoVista = "D";
            muestraReporte = false;
            tarea = null;
            nuevo();
            tarea.setEstado("C");
            tarea.setFechaMovimiento(null);
            tarea.getOperarios().clear();

        } catch (ExcepcionGeneralSistema egs) {
            JsfUtil.addErrorMessage("nuevoMovimiento" + egs);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, egs);
        }
    }

    public void nuevo() throws ExcepcionGeneralSistema {

        esNuevo = true;
        Tarea tareaAux = null;

        if (tarea != null) {
            tareaAux = tarea;
        }

        tarea = tareaRN.nuevoMovimiento(CODTA, SUCTA, "", "");

        if (tareaAux == null) {

            tareaOperario = new TareaOperario();
            tareaOperario.setTarea(tarea);
            tareaOperario.setOperario(usuarioSessionBean.getUsuario().getOperario());
            tarea.getOperarios().add(tareaOperario);

        } else {

            tarea.setArea(tareaAux.getArea());
            tarea.setProyecto(tareaAux.getProyecto());
            tarea.setTipoMantenimiento(tareaAux.getTipoMantenimiento());
            tarea.setGrupoProduccion(tareaAux.getGrupoProduccion());

            if (tareaAux.getMovimientoProduccion() != null) {
                tarea.setMovimientoProduccion(produccionRN.getMovimiento(tareaAux.getMovimientoProduccion().getId()));
            }

            tarea.setProceso(tareaAux.getProceso());
            tarea.setBienUso(tareaAux.getBienUso());

            for (TareaOperario tareaO : tareaAux.getOperarios()) {

                tareaOperario = new TareaOperario();
                tareaOperario.setTarea(tarea);
                tareaOperario.setOperario(tareaO.getOperario());
                tarea.getOperarios().add(tareaOperario);
            }
        }

        modoVista = "D";
    }

    public void iniciarTarea(boolean continua) {

        try {
            if (tarea != null) {

                tarea.setHoraInicio(new Date());
                tarea.setEstado(EstadoTarea.B.name());

                if (continua) {
                    tarea.setIniciaTareaIndividual(true);
                }

                tareaRN.guardar(tarea);
                esNuevo = false;
                modoVista = "D";

                if (!continua) {
                    mostrarLogin();
                }

                dashboardBean.actualizarTareas();
                JsfUtil.addInfoMessage("La tarea se ha iniciado con éxito");

            } else {
                JsfUtil.addInfoMessage("No hay datos para iniciar tarea");
            }
        } catch (Exception ex) {
//            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible iniciar la tarea " + ex);

            tarea.setHoraFin(null);
            tarea.setEstado(EstadoTarea.A.name());
//            usuarioSessionBean.desIdentificar();
//            ocultarLogin();
        }
    }

    public void finalizarTarea(boolean nueva, boolean continua) {

        try {
            if (tarea != null) {

                imprimeEtiqueta = false;

                controlTiempo();

                if (!tarea.isIngresaHoraFinManualmente()) {
                    tarea.setHoraFin(new Date());
                }

                if (tarea.getHoraFin() == null) {
                    return;
                }

                tarea.setTiempo(Integer.valueOf(String.valueOf(JsfUtil.cuantosMinutos(tarea.getHoraInicio(), tarea.getHoraFin()))));
                tarea.setEstado(EstadoTarea.Z.name());

                if (tarea.getArea().getCodigo().equals("PRD")) {

                    tarea.getMovimientosProduccion().clear();
//                    generarMovimientoHorario();
                    generarMovimientoProceso();
                    generarMovimientoConsumo();
                    generarMovimientoProduccion();
                }

                tarea = tareaRN.guardar(tarea);
                esNuevo = false;
                modoVista = "D";
                enviarNotificaciones();

                JsfUtil.addInfoMessage("La tarea anterior finalizó con éxito");

                if (imprimeEtiqueta) {
                    imprimirParteProduccion();;
                } else {

                    if (!nueva) {
                        mostrarLogin();
                        return;
                    }

                    if (!continua) {
                        JsfUtil.addInfoMessage("Ingrese los datos de su nueva tarea");
                    }

                    nuevoMovimiento();

                    if (continua) {
                        iniciarTarea(continua);
                    }
                }

                dashboardBean.actualizarTareas();

            } else {
                JsfUtil.addInfoMessage("No hay datos para finalizar tarea");
            }
        } catch (Exception ex) {
            //Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible finalizar la tarea" + ex);

            tarea.setHoraFin(null);
            tarea.setEstado(EstadoTarea.B.name());
            tarea.getMovimientosProduccion().clear();
//            ocultarLogin();
        }
    }

    public void controlTiempo() {

        if (tarea != null) {

            Integer tiempoActual = Integer.valueOf(String.valueOf(JsfUtil.cuantosMinutos(tarea.getHoraInicio(), new Date())));

            if (tiempoActual > 480) {
                tarea.setIngresaHoraFinManualmente(true);
                JsfUtil.addWarningMessage("Esta tarea tiene ha pasado el límite de tiempo standar, por favor, ingrese fecha y hora de finalización manualmente");
            } else {
                tarea.setIngresaHoraFinManualmente(false);
            }
        }
    }

    public void guardar(boolean nuevo) {

        try {
            if (tarea != null) {

                tarea.setTiempo(Integer.valueOf(String.valueOf(JsfUtil.cuantosMinutos(tarea.getHoraInicio(), tarea.getHoraFin()))));
                tarea.setEstado(EstadoTarea.Z.name());

                if (tarea.getArea().getCodigo().equals("PRD")) {

                    tarea.getMovimientosProduccion().clear();
                    generarMovimientoProceso();
                    generarMovimientoConsumo();
                    generarMovimientoProduccion();
                }

                tareaRN.guardarModificada(tarea);
                esNuevo = false;
                modoVista = "D";
                enviarNotificaciones();
                tarea = null;
                JsfUtil.addInfoMessage("La tarea se ha guardado correctamente");

            } else {
                JsfUtil.addInfoMessage("No hay datos para finalizar tarea");
            }
        } catch (Exception ex) {

            JsfUtil.addErrorMessage("No es posible finalizar la tarea" + ex);
            tarea.setEstado(EstadoTarea.C.name());
            tarea.getMovimientosProduccion().clear();
            ex.printStackTrace();
        }
    }

    public void anular(Tarea t) {

        try {
            tareaRN.anularMovimiento(t);
            JsfUtil.addInfoMessage("La tarea ha sido anulada correctamente");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible anular la tarea " + ex);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void copiar(Tarea t) {

        try {

            System.err.println("Tarea " + t.getFormulario().getCodigo() + " " + t.getNumeroFormulario());

            tarea = tareaRN.copiarMovimiento(t);
            cargarDatosProduccion();
            esNuevo = false;
            modoVista = "D";
            JsfUtil.addInfoMessage("La tarea ha sido copiada correctamente");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible copiar la tarea " + ex);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void borrar(Tarea t) {

        try {
            tareaRN.eliminar(t);
            JsfUtil.addInfoMessage("La tarea ha sido eliminada correctamente");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible eliminar la tarea " + ex);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generarMovimientoConsumo() throws Exception {

        if (!produccionRN.tengoItemsConCantidad(componentesPendientes)) {
            return;
        }

        movimientoProduccionBean.setCIRCOM("0250");
        movimientoProduccionBean.setCIRAPL("0200");
        movimientoProduccionBean.setSUCST(SUCVC);
        movimientoProduccionBean.setMODST(MODVC);
        movimientoProduccionBean.setCODST(CODVC);

        movimientoProduccionBean.iniciarMovimiento();
        movimientoProduccionBean.generarMovimientoFromItems(componentesPendientes);

        movimientoProduccionBean.getM().setFechaMovimiento(tarea.getFechaMovimiento());
        movimientoProduccionBean.getM().setTarea(tarea);
        movimientoProduccionBean.getM().setPlanta(usuarioSessionBean.getUsuario().getOperario().getPlanta());
        tarea.getMovimientosProduccion().add(movimientoProduccionBean.getM());

    }

    public void generarMovimientoProduccion() throws Exception {

        if (!produccionRN.tengoItemsConCantidad(productosPendientes)) {
            return;
        }

        movimientoProduccionBean.setCIRCOM("0300");
        movimientoProduccionBean.setCIRAPL("0200");
        movimientoProduccionBean.setSUCST(SUCPP);
        movimientoProduccionBean.setMODST(MODPP);
        movimientoProduccionBean.setCODST(CODPP);

        movimientoProduccionBean.iniciarMovimiento();
        movimientoProduccionBean.generarMovimientoFromItems(productosPendientes);

        movimientoProduccionBean.getM().setFechaMovimiento(tarea.getFechaMovimiento());
        movimientoProduccionBean.getM().setTarea(tarea);
        movimientoProduccionBean.getM().setPlanta(usuarioSessionBean.getUsuario().getOperario().getPlanta());

        tarea.getMovimientosProduccion().add(movimientoProduccionBean.getM());

        imprimeEtiqueta = true;

    }

    public void generarMovimientoProceso() throws Exception {

        for (TareaOperario itemOperario : tarea.getOperarios()) {

            movimientoProduccionBean.setCIRCOM("0350");
            movimientoProduccionBean.setCIRAPL("0200");
            movimientoProduccionBean.setSUCPD(SUCPR);
            movimientoProduccionBean.setMODPD(MODPR);
            movimientoProduccionBean.setCODPD(CODPR);

            movimientoProduccionBean.iniciarMovimiento();

            for (ItemMovimientoProduccion itemProceso : procesosPendientes) {

                if (!itemProceso.getTipitm().equals("R")) {
                    continue;
                }

                if (itemProceso.getProducto().equals(tarea.getProceso()) && tarea.getGrupoProduccion().equals(itemProceso.getGrupo())) {

                    if (itemOperario.getOperario() != null) {

                        itemProceso.setOperario(itemOperario.getOperario());
                        itemProceso.setPrecio(itemOperario.getOperario().getTipo().getValorHora());
                    }
                    itemProceso.setProduccion(new BigDecimal(tarea.getTiempo()));
                    itemProceso.setCantidadSecundaria(tarea.getCantidad());
                }
            }

            movimientoProduccionBean.generarMovimientoFromItems(procesosPendientes);
            movimientoProduccionBean.getM().setFechaMovimiento(tarea.getFechaMovimiento());
            movimientoProduccionBean.getM().setTarea(tarea);
            movimientoProduccionBean.getM().setPlanta(itemOperario.getOperario().getPlanta());
            tarea.getMovimientosProduccion().add(movimientoProduccionBean.getM());
        }
    }

    /**
     * public void generarMovimientoHorario() throws Exception {
     *
     * for (TareaOperario itemOperario : tarea.getOperarios()) {
     *
     * movimientoProduccionBean.setCIRCOM("0450");
     * movimientoProduccionBean.setCIRAPL("0200");
     * movimientoProduccionBean.setSUCPD(SUCPH);
     * movimientoProduccionBean.setMODPD(MODPH);
     * movimientoProduccionBean.setCODPD(CODPH);
     *
     * movimientoProduccionBean.iniciarMovimiento();
     *
     * for (ItemHorarioProduccion itemHorario : horariosPendientes) {
     *
     * if (itemHorario.getProducto().equals(tarea.getProducto()) &&
     * tarea.getGrupoProduccion().equals(itemHorario.getGrupo())) {
     *
     * if (itemOperario.getOperario() != null) {
     *
     * itemHorario.setOperario(itemOperario.getOperario());
     * itemHorario.setPrecio(itemOperario.getOperario().getTipo().getValorHora());
     * }
     *
     * itemHorario.setProduccion(new BigDecimal(tarea.getTiempo())); } }
     *
     * movimientoProduccionBean.generarMovimientoFromItems(horariosPendientes);
     * movimientoProduccionBean.getM().setFechaMovimiento(tarea.getFechaMovimiento());
     * movimientoProduccionBean.getM().setTarea(tarea);
     * movimientoProduccionBean.getM().setPlanta(itemOperario.getOperario().getPlanta());
     * tarea.getMovimientosProduccion().add(movimientoProduccionBean.getM()); }
     * }
     */
    public void buscarOrdenesProduccionPendientes() {

        filtro.clear();
        filtro.put("circom = ", "'0200'");
        filtro.put("estado IN", "('1','2') ");
        filtro.put("codpla = ", "'" + usuarioSessionBean.getUsuario().getOperario().getPlanta().getCodigo() + "'");
        pendienteProduccion = produccionRN.getMovimientosPendiente(filtro);
    }

//    public void seleccionarOrdenProduccion(MovimientoProduccion op) {
//
//        try {
//            op = produccionRN.getMovimiento(op.getId(), true, true);
//            tarea.setMovimientoProduccion(op);
//        } catch (ExcepcionGeneralSistema ex) {
//            JsfUtil.addErrorMessage("No es posible seleccionar orden de producción");
//            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public void seleccionarProcesoProduccion(SelectEvent event) {

        tarea.setProceso(((ItemMovimientoProduccion) event.getObject()).getProducto());
    }

    public void seleccionarOrdenProduccion(SelectEvent event) {

        PendienteProduccionGrupo pend = ((PendienteProduccionGrupo) event.getObject());
        MovimientoProduccion op = produccionRN.getMovimiento(pend.getId());

        procesosPendientes = produccionRN.getItemByGrupoSector(op, "R", pend.getGrupo(), usuarioSessionBean.getUsuario().getOperario().getSector());
        tarea.setGrupoProduccion(pend.getGrupo());
        tarea.setMovimientoProduccion(op);

    }

    public void agregarOperario() {

        if (!tarea.getEstado().equals("A") && !tarea.getEstado().equals("C")) {

            JsfUtil.addErrorMessage("No es posible agregar operarios a una tarea iniciada. Finalice e inicie una tarea nueva por favor");
            return;
        }

        if (operarioAgregar != null) {

            TareaOperario tu = new TareaOperario();
            tu.setTarea(tarea);
            tu.setOperario(operarioAgregar);

            for (TareaOperario to : tarea.getOperarios()) {

                if (to.getOperario().equals(tu.getOperario())) {
                    JsfUtil.addErrorMessage("El operario " + operarioAgregar.getNombre() + " ya está asignado a esta tarea");
                    return;
                }
            }

            if (tarea.getEstado().equals("A")) {

                List<Tarea> otrasTareasAsignadas = tareaRN.getListaByBusqueda(operarioAgregar, EstadoTarea.B.name(), mostrarDebaja, cantidadRegistros);

                if (otrasTareasAsignadas != null && !otrasTareasAsignadas.isEmpty()) {
                    JsfUtil.addErrorMessage("El operario " + operarioAgregar.getNombre() + " está asignado a otra tarea en estos momentos, no es posible agregarlo.");
                    return;
                }
            }

            tarea.getOperarios().add(tu);
            operarioAgregar = null;
        }
    }

    public String mostrarProductoProducir() {

        if (tarea == null
                || tarea.getMovimientoProduccion() == null
                || tarea.getMovimientoProduccion().getItemsMovimiento() == null) {
            return "";
        }

        for (ItemMovimientoProduccion ip : tarea.getMovimientoProduccion().getItemsMovimiento().stream().filter(i -> i.getTipitm().equals("P")).collect(Collectors.toList())) {

            if (tarea.getGrupoProduccion().equals(ip.getGrupo())) {
                return ip.getProducto().getDescripcion();
            }
        }

        return "";
    }

    public void buscarTareaActiva() {

        lista = tareaRN.getListaByBusqueda(usuarioSessionBean.getUsuario().getOperario(), EstadoTarea.B.name(), mostrarDebaja, cantidadRegistros);

    }

    public void onSelect(SelectEvent event) {
        tarea = (Tarea) event.getObject();
        cargarDatosProduccion();
        esNuevo = false;
        modoVista = "D";

    }

    public void seleccionar(Tarea u) {

        if (u == null) {
            return;
        }

        tarea = u;
        cargarDatosProduccion();
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (tarea == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void imprimirParteProduccion() {

        if (tarea == null) {
            return;
        }

        if (tarea.getMovimientosProduccion() != null) {

            for (MovimientoProduccion m : tarea.getMovimientosProduccion()) {

                if (m.getTipoMovimiento().equals(TipoMovimientoProduccion.PP)) {

                    movimientoProduccionBean.setM(m);
                    movimientoProduccionBean.generarReporteEtiquetas();

                    nombreArchivo = movimientoProduccionBean.getNombreArchivo();
                    muestraReporte = movimientoProduccionBean.isMuestraReporte();

                    PrimeFaces.current().ajax().update("formulario");

                    if (muestraReporte) {
                        PrimeFaces.current().executeScript("PF('dlg_reporte').show()");
                    }
                }
            }
        }
    }

    public void onSelectArea() {

        if (tarea.getArea() != null) {

            tarea.setMovimientoProduccion(null);
            tarea.setProceso(null);
            tarea.setBienUso(null);
            tarea.setProyecto(null);
            tarea.setTipoMantenimiento(null);

            if (tarea.getArea().getCodigo().equals("MAN")) {
                productoBean.setearTipo("03");
            } else {
                productoBean.setTipoProducto(null);
            }
            productoBean.buscar();
        }
    }

    public void procesarProducto() {

        if (productoBean.getProducto() != null) {

            if (tarea.getArea().getCodigo().equals("MAN")) {
                tarea.setBienUso(productoBean.getProducto());
            }

            if (tarea.getArea().getCodigo().equals("PRD")) {

                ItemMovimientoProduccion itemComponente = new ItemMovimientoProduccion();
                itemComponente.setTipitm("C");
                itemComponente.setProducto(productoBean.getProducto());
                itemComponente.setProductoOriginal(productoBean.getProducto());
                itemComponente.setUnidadMedida(productoBean.getProducto().getUnidadDeMedida());
                itemComponente.setActualizaStock(productoBean.getProducto().getGestionaStock());

                itemComponente.setItemDetalleTemporal(new ArrayList<ItemDetalleMovimientoProduccion>());
                ItemDetalleMovimientoProduccion itemDetalle = produccionRN.nuevoItemDetalle(itemComponente);
                itemComponente.getItemDetalleTemporal().add(itemDetalle);

                componentesPendientes.add(itemComponente);

            }
        }
    }

    private void mostrarLogin() {

        tarea = null;
        PrimeFaces.current().executeScript("PF('dlgLogin').show()");
        try {
            usuarioSessionBean.logout();
        } catch (IOException ex) {
            Logger.getLogger(TareaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void enviarNotificaciones() {

        try {

            if (aplicacionBean.getParametro().getEnviaNotificaciones() == 'S') {

                muestraReporte = false;
                CorreoElectronico ce = null;

                //ce = notificacionesTareaRN.generarCorreoElectronico(tarea, "clauditrosch@gmail.com");
                //ce = notificacionesTareaRN.generarCorreoElectronico(tarea, "hormitec.produccion@hormitec-srl.com");
                ce = notificacionesTareaRN.generarCorreoElectronico(tarea, aplicacionBean.getParametro().getEmailRecepcionConsulta());

                if (ce == null) {
                    JsfUtil.addWarningMessage("No se ha generado el correo electrónico");
                    return;
                }
                List<CorreoElectronico> correos = new ArrayList<CorreoElectronico>();
                correos.add(ce);

                mailFactoryBean.enviarCorreosElectronicos(correos);
            }

        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "enviarNotificaciones", ex);
        }
    }

    public void agregarItemDetalleTemporal(ItemMovimientoProduccion nItem) {
        try {

            produccionRN.agregarItemDetalleTemporalProducto(nItem);

        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage("No es posible agregar el item detalle: " + ex.getMessage());
        }
    }

    public void eliminarItemDetalleTemporal(ItemMovimientoProduccion ip, ItemDetalleMovimientoProduccion nItem) {

        try {
            if (produccionRN.eliminarItemDetalleTemporal(ip, nItem)) {

                JsfUtil.addWarningMessage("Se ha borrado el item " + nItem.getAtributo1() + "");
            } else {

                JsfUtil.addInfoMessage("No se encontró el item a borrar");
            }
        } catch (ExcepcionGeneralSistema e) {
            JsfUtil.addErrorMessage(e.getMessage());
        }
    }

    public void buscar() {

        if (!validarParametros()) {
            return;
        }
        cargarFiltroBusqueda();

        lista = tareaRN.getListaByBusqueda(operario, filtro, mostrarDebaja, cantidadRegistros);

        if (lista == null || lista.isEmpty()) {
            JsfUtil.addWarningMessage("No se han encontrado tareas");
        }

        modoVista = "B";

    }

    public boolean validarParametros() {

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

        if (numeroOrdenProduccionDesde != null && numeroOrdenProduccionHasta != null) {
            if (numeroOrdenProduccionDesde > numeroOrdenProduccionHasta) {
                JsfUtil.addWarningMessage("Número de orden de producción desde es mayor al número de orden de producción hasta");
                return false;
            }
        }

        return true;
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (formulario != null) {
            filtro.put("formulario.codigo = ", "'" + formulario.getCodigo() + "'");
        }

        if (estado != null && !estado.isEmpty()) {
            filtro.put("estado =", "'" + estado + "'");
        }

        if (area != null) {
            filtro.put("area.codigo =", "'" + area.getCodigo() + "'");
        }

        if (proyecto != null) {
            filtro.put("proyecto.codigo =", "'" + proyecto.getCodigo() + "'");
        }

        if (planta != null) {
            filtro.put("movimientoProduccion.planta.codigo =", "'" + planta.getCodigo() + "'");
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

        if (numeroOrdenProduccionDesde != null) {

            filtro.put("movimientoProduccion.numeroFormulario >=", String.valueOf(numeroOrdenProduccionDesde));
        }

        if (numeroOrdenProduccionHasta != null) {

            filtro.put("movimientoProduccion.numeroFormulario <=", String.valueOf(numeroOrdenProduccionHasta));
        }

    }

    public void nuevaBusqueda() {

        if (tarea != null && tarea.getFormulario() != null) {
            formulario = tarea.getFormulario();
        }
        modoVista = "B";
    }

    public void resetParametros() {

        formulario = null;
        operario = null;
        area = null;
        proyecto = null;
        estado = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        numeroOrdenProduccionDesde = null;
        numeroOrdenProduccionHasta = null;
        muestraReporte = false;
        solicitaEmail = false;
        lista = null;

    }

    public void calcularTiempo() {

        if (tarea != null) {

            tarea.setTiempo(Integer.valueOf(String.valueOf(JsfUtil.cuantosMinutos(tarea.getHoraInicio(), tarea.getHoraFin()))));

        }
    }

    public void onFechaSelect(SelectEvent event) {
        tarea.setHoraInicio(tarea.getFechaMovimiento());
    }

    public void cargarDatosProduccion() {

        if (tarea.getMovimientoProduccion() != null && tarea.getGrupoProduccion() != null) {

            try {
                productosPendientes = produccionRN.getItemByGrupo(tarea.getMovimientoProduccion(), "P", tarea.getGrupoProduccion());
                componentesPendientes = produccionRN.getItemByGrupo(tarea.getMovimientoProduccion(), "C", tarea.getGrupoProduccion());
                procesosPendientes = produccionRN.getItemByGrupo(tarea.getMovimientoProduccion(), "R", tarea.getGrupoProduccion());

                if (tarea.getTareaAnular() != null) {

                    System.err.println("cargar cantidades guardadas");

                    produccionRN.cargarCantidadesGuardadas(tarea, "P", productosPendientes);
                    produccionRN.cargarCantidadesGuardadas(tarea, "C", componentesPendientes);
                }

            } catch (ExcepcionGeneralSistema ex) {

                JsfUtil.addErrorMessage("Problemas para cargar datos producción");
            }
        }
    }

    public void sincronizarCantidadesProduccion(ItemMovimientoProduccion item) {

        produccionRN.sincronizarCantidadesItemDetalleTemporal(item);

    }

    public List<Producto> completeBienUso(String query) {
        try {
            filtro.clear();
            filtro.put("bienDeUso = ", "'S'");

            return productoRN.getListaByBusqueda(filtro, query, false, 10);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Producto>();
        }
    }

    public List<Producto> completeProceso(String query) {
        try {
            filtro.clear();
            filtro.put("tipoProducto.codigo = ", "'02'");

            return productoRN.getListaByBusqueda(filtro, query, false, 10);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Producto>();
        }
    }

//    private void ocultarLogin() {
//
//        RequestContext context = RequestContext.getCurrentInstance();
//        context.execute("PF('dlgLogin').hide()");
////        usuarioSessionBean.desIdentificar();
//    }
    //--------------------------------------------------------------------------
    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea unidadMedida) {
        this.tarea = unidadMedida;
    }

    public List<Tarea> getLista() {
        return lista;
    }

    public void setLista(List<Tarea> lista) {
        this.lista = lista;
    }

    public TareaOperario getTareaOperario() {
        return tareaOperario;
    }

    public void setTareaOperario(TareaOperario tareaOperario) {
        this.tareaOperario = tareaOperario;
    }

    public List<PendienteProduccionGrupo> getPendienteProduccion() {
        return pendienteProduccion;
    }

    public void setPendienteProduccion(List<PendienteProduccionGrupo> pendienteProduccion) {
        this.pendienteProduccion = pendienteProduccion;
    }

    public Operario getOperarioAgregar() {
        return operarioAgregar;
    }

    public void setOperarioAgregar(Operario operarioAgregar) {
        this.operarioAgregar = operarioAgregar;
    }

    public String getMODTA() {
        return MODTA;
    }

    public void setMODTA(String MODTA) {
        this.MODTA = MODTA;
    }

    public String getCODTA() {
        return CODTA;
    }

    public void setCODTA(String CODTA) {
        this.CODTA = CODTA;
    }

    public String getSUCTA() {
        return SUCTA;
    }

    public void setSUCTA(String SUCTA) {
        this.SUCTA = SUCTA;
    }

    public String getSUCPR() {
        return SUCPR;
    }

    public void setSUCPR(String SUCPR) {
        this.SUCPR = SUCPR;
    }

    public String getMODPR() {
        return MODPR;
    }

    public void setMODPR(String MODPR) {
        this.MODPR = MODPR;
    }

    public String getCODPR() {
        return CODPR;
    }

    public void setCODPR(String CODPR) {
        this.CODPR = CODPR;
    }

    public String getSUCPP() {
        return SUCPP;
    }

    public void setSUCPP(String SUCPP) {
        this.SUCPP = SUCPP;
    }

    public String getMODPP() {
        return MODPP;
    }

    public void setMODPP(String MODPP) {
        this.MODPP = MODPP;
    }

    public String getCODPP() {
        return CODPP;
    }

    public void setCODPP(String CODPP) {
        this.CODPP = CODPP;
    }

    public String getSUCVC() {
        return SUCVC;
    }

    public void setSUCVC(String SUCVC) {
        this.SUCVC = SUCVC;
    }

    public String getMODVC() {
        return MODVC;
    }

    public void setMODVC(String MODVC) {
        this.MODVC = MODVC;
    }

    public String getCODVC() {
        return CODVC;
    }

    public void setCODVC(String CODVC) {
        this.CODVC = CODVC;
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

    public MovimientoProduccionBean getMovimientoProduccionBean() {
        return movimientoProduccionBean;
    }

    public void setMovimientoProduccionBean(MovimientoProduccionBean movimientoProduccionBean) {
        this.movimientoProduccionBean = movimientoProduccionBean;
    }

    public PendienteProduccionGrupo getMovimientoPendiente() {
        return movimientoPendiente;
    }

    public void setMovimientoPendiente(PendienteProduccionGrupo movimientoPendiente) {
        this.movimientoPendiente = movimientoPendiente;
    }

    public ProyectoBean getProyectoBean() {
        return proyectoBean;
    }

    public void setProyectoBean(ProyectoBean proyectoBean) {
        this.proyectoBean = proyectoBean;
    }

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

    public DashboardBean getDashboardBean() {
        return dashboardBean;
    }

    public void setDashboardBean(DashboardBean dashboardBean) {
        this.dashboardBean = dashboardBean;
    }

    public AplicacionBean getAplicacionBean() {
        return aplicacionBean;
    }

    public void setAplicacionBean(AplicacionBean aplicacionBean) {
        this.aplicacionBean = aplicacionBean;
    }

    public Operario getOperario() {
        return operario;
    }

    public void setOperario(Operario operario) {
        this.operario = operario;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public Integer getNumeroOrdenProduccionDesde() {
        return numeroOrdenProduccionDesde;
    }

    public void setNumeroOrdenProduccionDesde(Integer numeroOrdenProduccionDesde) {
        this.numeroOrdenProduccionDesde = numeroOrdenProduccionDesde;
    }

    public Integer getNumeroOrdenProduccionHasta() {
        return numeroOrdenProduccionHasta;
    }

    public void setNumeroOrdenProduccionHasta(Integer numeroOrdenProduccionHasta) {
        this.numeroOrdenProduccionHasta = numeroOrdenProduccionHasta;
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getSUCPH() {
        return SUCPH;
    }

    public void setSUCPH(String SUCPH) {
        this.SUCPH = SUCPH;
    }

    public String getMODPH() {
        return MODPH;
    }

    public void setMODPH(String MODPH) {
        this.MODPH = MODPH;
    }

    public String getCODPH() {
        return CODPH;
    }

    public void setCODPH(String CODPH) {
        this.CODPH = CODPH;
    }

    public ItemMovimientoProduccion getItemPendiente() {
        return itemPendiente;
    }

    public void setItemPendiente(ItemMovimientoProduccion itemPendiente) {
        this.itemPendiente = itemPendiente;
    }

    public List<ItemMovimientoProduccion> getProcesosPendientes() {
        return procesosPendientes;
    }

    public void setProcesosPendientes(List<ItemMovimientoProduccion> procesosPendientes) {
        this.procesosPendientes = procesosPendientes;
    }

    public List<ItemMovimientoProduccion> getHorariosPendientes() {
        return horariosPendientes;
    }

    public void setHorariosPendientes(List<ItemMovimientoProduccion> horariosPendientes) {
        this.horariosPendientes = horariosPendientes;
    }

    public List<ItemMovimientoProduccion> getProductosPendientes() {
        return productosPendientes;
    }

    public void setProductosPendientes(List<ItemMovimientoProduccion> productosPendientes) {
        this.productosPendientes = productosPendientes;
    }

    public List<ItemMovimientoProduccion> getComponentesPendientes() {
        return componentesPendientes;
    }

    public void setComponentesPendientes(List<ItemMovimientoProduccion> componentesPendientes) {
        this.componentesPendientes = componentesPendientes;
    }

    public boolean isImprimeEtiqueta() {
        return imprimeEtiqueta;
    }

    public void setImprimeEtiqueta(boolean imprimeEtiqueta) {
        this.imprimeEtiqueta = imprimeEtiqueta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
