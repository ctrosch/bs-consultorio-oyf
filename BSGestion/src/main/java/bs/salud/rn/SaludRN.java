/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.rn;

import bs.administracion.modelo.Parametro;
import bs.administracion.rn.ModuloRN;
import bs.administracion.rn.ParametrosRN;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.modelo.EntidadHorario;
import bs.entidad.modelo.EntidadObraSocial;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.ArchivoAdjunto;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.ComprobanteRN;
import bs.global.rn.FormularioRN;
import bs.global.rn.PuntoVentaRN;
import bs.salud.dao.MovimientoSaludDAO;
import bs.salud.modelo.MovimientoSalud;
import bs.salud.modelo.ParametroSalud;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author Guillermo
 */
@Stateless
public class SaludRN {

    @EJB
    private ModuloRN moduloRN;
    @EJB
    private MovimientoSaludDAO saludDAO;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private ComprobanteRN comprobanteRN;
    @EJB
    private ParametrosRN parametrosRN;
    @EJB
    protected PuntoVentaRN puntoVentaRN;
    @EJB
    protected EstadoSaludRN estadoSaludRN;
    @EJB
    private ParametroSaludRN parametroSaludRN;

    private ScheduleModel eventModel;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized MovimientoSalud guardar(MovimientoSalud m) throws Exception {

        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        preSave(m);
        control(m);
        if (m.getId() == null) {
            if (m.getComprobante().getTipoComprobante().equals("RT")) {

                controlCantidadTurnos(m);
            }
            if (m.getComprobante().getTipoComprobante().equals("SA") && m.getMovimientoAplicado() == null) {

                controlCantidadTurnos(m);
            }

//            calcularImportes(m);
            tomarNumeroFormulario(m);
            saludDAO.crear(m);

            // Solo edito si el comprobante es una reserva de turno o un informe de atención
//        } else if (m.getComprobante().getTipoComprobante().equals("RT") || m.getComprobante().getTipoComprobante().equals("IA")) {
        } else {
            if (m.getComprobante().getTipoComprobante().equals("RT")) {
                Date fechaComprobanteAModificar = saludDAO.getMovimientoSalud(m.getId()).getFechaMovimiento();
                if (!fecha.format(fechaComprobanteAModificar).equals(fecha.format(m.getFechaMovimiento()))) {
                    controlRangoHorasEnOtraFecha(m);
                    controlCantidadTurnos(m);
                }
            }
            m = saludDAO.editar(m);
        }

        if (m.getMovimientoAplicado() != null) {
            m.getMovimientoAplicado().setEstado(estadoSaludRN.getEstadoSalud("Z"));
            saludDAO.editar(m.getMovimientoAplicado());
        }

        return m;
    }

    public void preSave(MovimientoSalud m) {

        if (m.getAdjuntos() != null && !m.getAdjuntos().isEmpty()) {

            m.getAdjuntos().forEach(i -> {
                i.setMovimientoSalud(m);
            });

        }
    }

    public MovimientoSalud nuevoMovimiento() {
        return new MovimientoSalud();
    }

    public MovimientoSalud nuevoMovimiento(String codSA, String ptoSA) throws ExcepcionGeneralSistema, Exception {

        if (codSA.equals("RT001")) {
            return nuevoMovimiento(codSA, ptoSA, null);
        }
        return nuevoMovimiento(codSA, ptoSA, new Date());
    }

    public MovimientoSalud nuevoMovimiento(String codSA, String ptoSA, Date fechaMovimiento) throws ExcepcionGeneralSistema, Exception {

        Comprobante comprobante = comprobanteRN.getComprobante("SA", codSA);
        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(ptoSA);
        Parametro p = parametrosRN.getParametro();
        ParametroSalud ps = parametroSaludRN.getParametro();
//        ParametroEducacion pe = parametroEducacionRN.getParametro();
//        double cotizacion = monedaRN.getCotizacionDia(p.getCodigoMonedaSecundaria()).doubleValue();
//        Moneda monedaSec = monedaRN.getMoneda(p.getCodigoMonedaSecundaria());

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No se encontró comprobante de Salud " + codSA);
        }
        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No se encontró punto de venta " + ptoSA);
        }

        //Buscamos el formulario correspondiente
        Formulario formulario = formularioRN.getFormulario(comprobante, puntoVenta, "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de Salud para el comprobante (" + codSA + ") "
                    + "para El punto de venta (" + ptoSA + ") "
                    + "con la condición de iva (X) ");
        }

        MovimientoSalud movimiento = new MovimientoSalud();

        if (comprobante.getTipoComprobante().equals("IA")) {
            movimiento.setEstado(estadoSaludRN.getEstadoSalud("H"));
        } else if (comprobante.getTipoComprobante().equals("RT")) {
            movimiento.setEstado(estadoSaludRN.getEstadoSalud("R"));
        } else if (comprobante.getTipoComprobante().equals("SA")) {
            movimiento.setEstado(estadoSaludRN.getEstadoSalud("E"));
        }

        movimiento.setEmpresa(puntoVenta.getEmpresa());
        movimiento.setSucursal(puntoVenta.getSucursal());
        movimiento.setPuntoVenta(puntoVenta);

        movimiento.setComprobante(comprobante);
        movimiento.setFechaMovimiento(fechaMovimiento);
//        movimiento.setMonedaSecundaria(monedaSec);
//        movimiento.setMonedaRegistracion(comprobante.getMonedaRegistracion());
//        movimiento.setCotizacion(cotizacion);

        asignarFormulario(movimiento);

        return movimiento;
    }

    public void asignarFormulario(MovimientoSalud m) throws ExcepcionGeneralSistema {

        Formulario formulario;
        formulario = formularioRN.getFormulario(m.getComprobante(), m.getPuntoVenta(), "X");

        //Este número es temporal y puede cambiar al guardar el objeto.
        m.setNumeroFormulario(formulario.getUltimoNumero() + 1);
        m.setFormulario(formulario);
    }

    private void control(MovimientoSalud m) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        sErrores += moduloRN.canSaveModulo("SA", m.getFechaMovimiento());

        if (m.getEmpresa() == null) {
            sErrores += "- El movimiento no tiene una empresa asignada\n";
        }

        if (m.getPaciente() == null) {
            sErrores += "- El Paciente está vacío, no es posible guardar el comprobante \n";
        }
        if (m.getProfesional() == null) {
            sErrores += "- El Profesional está vacío, no es posible guardar el comprobante \n";
        }

        if (m.getComprobante().getTipoComprobante().equals("RT")
                || m.getComprobante().getTipoComprobante().equals("SA")) {

            if (m.getHoraMovimiento() == null) {
                sErrores += "- El horario del turno está vacío, no es posible guardar el comprobante \n";
            }
        }

        if (m.getEstado().getCodigo().equals("R")) {
            if (controlarSiEstaTurnoDado(m)) {
                sErrores += "- El turno en esa fecha y horario ya fue otorgado \n";
            }
        }
//        else if (m.getEstado().getCodigo().equals("E") && m.getMovimientoAplicado() == null) {
//            if (controlarSiEstaTurnoDado(m)) {
//                sErrores += "- El turno en esa fecha y horario ya fue otorgado \n";
//            }
//        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }

    }

    public void controlCantidadTurnos(MovimientoSalud m) throws ExcepcionGeneralSistema, Exception {
        Integer cantidadTurno = saludDAO.controlarCantidadTurnosDado(m.getFechaMovimiento(), m.getProfesional());
        if (cantidadTurno >= m.getProfesional().getCantidadTurnosDiarios()) {
            throw new ExcepcionGeneralSistema("No se puede guardar . La cantidad de turnos diarios del profesional " + m.getNombreProfesional() + " ya fue completada.-");
        }
    }

    public void controlRangoHorasEnOtraFecha(MovimientoSalud m) throws ExcepcionGeneralSistema, Exception {
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");
        GregorianCalendar gcalendario = new GregorianCalendar();
        gcalendario.setTime(m.getFechaMovimiento());

        if (m.getProfesional().getHorarios() != null && !m.getProfesional().getHorarios().isEmpty()) {
            for (EntidadHorario horario : m.getProfesional().getHorarios()) {
                if (Integer.parseInt(horario.getDiaSemana().substring(0, 1).trim()) == gcalendario.get(Calendar.DAY_OF_WEEK)) {
                    if (horario.getAtiendeTurnoMañana().equals("S") && horario.getAtiendeTurnoTarde().equals("S")) {
                        if (!horario.getHoraInicioMañana().equals(m.getHoraMovimiento())) {
                            if (!horario.getHoraFinMañana().equals(horario.getHoraInicioTarde())) {
                                if (m.getHoraMovimiento().equals(horario.getHoraFinMañana())) {
                                    throw new ExcepcionGeneralSistema("No se puede guardar . La hora " + formatoHora.format(m.getHoraMovimiento()) + " no se encuentra en dicha grilla horario esa fecha .-");
                                }
                            }
                            if (m.getHoraMovimiento().equals(horario.getHoraFinTarde())) {
                                throw new ExcepcionGeneralSistema("No se puede guardar . La hora " + formatoHora.format(m.getHoraMovimiento()) + " no se encuentra en dicha grilla horario esa fecha .-");
                            }

                            if (!(horario.getHoraInicioMañana().before(m.getHoraMovimiento()) && m.getHoraMovimiento().before(horario.getHoraFinMañana()))) {
                                if (!(horario.getHoraInicioTarde().before(m.getHoraMovimiento()) && m.getHoraMovimiento().before(horario.getHoraFinTarde()))) {
                                    throw new ExcepcionGeneralSistema("No se puede guardar . La hora " + formatoHora.format(m.getHoraMovimiento()) + " no se encuentra en dicha grilla horario esa fecha .-");
                                }
                            }
                        }

                    } else if (horario.getAtiendeTurnoMañana().equals("S")) {

                        if (m.getHoraMovimiento().equals(horario.getHoraFinMañana())) {
                            throw new ExcepcionGeneralSistema("No se puede guardar . La hora " + formatoHora.format(m.getHoraMovimiento()) + " no se encuentra en dicha grilla horario esa fecha .-");
                        }
                        if (!horario.getHoraInicioMañana().equals(m.getHoraMovimiento())) {
                            if (!(horario.getHoraInicioMañana().before(m.getHoraMovimiento()) && m.getHoraMovimiento().before(horario.getHoraFinMañana()))) {
                                throw new ExcepcionGeneralSistema("No se puede guardar . La hora " + formatoHora.format(m.getHoraMovimiento()) + " no se encuentra en dicha grilla horario esa fecha .-");
                            }
                        }

                    } else if (horario.getAtiendeTurnoTarde().equals("S")) {

                        if (m.getHoraMovimiento().equals(horario.getHoraFinTarde())) {
                            throw new ExcepcionGeneralSistema("No se puede guardar . La hora " + formatoHora.format(m.getHoraMovimiento()) + " no se encuentra en dicha grilla horario esa fecha .-");
                        }
                        if (!horario.getHoraInicioTarde().equals(m.getHoraMovimiento())) {
                            if (!(horario.getHoraInicioTarde().before(m.getHoraMovimiento()) && m.getHoraMovimiento().before(horario.getHoraFinTarde()))) {
                                throw new ExcepcionGeneralSistema("No se puede guardar . La hora " + formatoHora.format(m.getHoraMovimiento()) + " no se encuentra en dicha grilla horario esa fecha .-");
                            }
                        }

                    }

                    break;
                }
            }

        }

    }

    public boolean controlarSiEstaTurnoDado(MovimientoSalud m) {

        return saludDAO.controlarSiEstaTurnoDado(m.getFechaMovimiento(), m.getHoraMovimiento(), m.getProfesional());

    }

    public List<MovimientoSalud> getListaByBusqueda(Map<String, String> filtro, int cantMax) {

        return saludDAO.getListaByBusqueda(filtro, cantMax);
    }

    public List<MovimientoSalud> getMovimientoSaludEspera(EntidadComercial profesional) {
//        System.out.println("profesional recibido del formulario al seleccionar el mismo = " + profesional.getNombreComplete());
        return saludDAO.getMovimientoSaludEspera(profesional);
    }

    public int getCantidadPacientesByEstado(String tipoComprobante, String estado, EntidadComercial profesional, Date fechaMovimiento) {

        return saludDAO.getCantidadPacientesByEstado(tipoComprobante, estado, profesional, fechaMovimiento);
    }

    public List<Date> completeHorario2(Date fechaMovimiento, EntidadComercial profesional) {

        List<MovimientoSalud> movimientos = new ArrayList<MovimientoSalud>();
        movimientos = saludDAO.getMovimientoSaludEspera(fechaMovimiento, profesional);
        Date hora = null;
        ParametroSalud ps = parametroSaludRN.getParametro();
        GregorianCalendar gc = new GregorianCalendar();

        Date horarioInicioMañana = ps.getHoraInicioMañana();
        Date horarioFinMañana = ps.getHoraFinMañana();
        Date horarioInicioTarde = ps.getHoraInicioTarde();
        Date horarioFinTarde = ps.getHoraFinTarde();

        Integer duracionTurno = ps.getDuracionTurno();

        List<Date> horarios = new ArrayList<>();

        //Turno mañana
        horarios.add(horarioInicioMañana);

        gc.setTime(horarioInicioMañana);
        gc.set(Calendar.MINUTE, gc.get(Calendar.MINUTE) + duracionTurno);
        hora = gc.getTime();
        while (hora.before(horarioFinMañana)) {
            horarios.add(hora);
            gc.setTime(hora);
            gc.set(Calendar.MINUTE, gc.get(Calendar.MINUTE) + duracionTurno);
            hora = gc.getTime();
        }

        //Turno Tarde
        horarios.add(horarioInicioTarde);

        gc.setTime(horarioInicioTarde);
        gc.set(Calendar.MINUTE, gc.get(Calendar.MINUTE) + duracionTurno);
        hora = gc.getTime();
        while (hora.before(horarioFinTarde)) {
            horarios.add(hora);
            gc.setTime(hora);
            gc.set(Calendar.MINUTE, gc.get(Calendar.MINUTE) + duracionTurno);
            hora = gc.getTime();
        }

        //ME DABA EJBEXCEPTION por usar el REMOVE
//        Iterator<Date> it = horarios.iterator();
//
//        //Eliminamos la hora ya tomada
//        if (movimientos != null && !movimientos.isEmpty()) {
//
//            while (it.hasNext()) {
//                Date horaAnalizada = it.next();
//                for (MovimientoSalud mov : movimientos) {
//                    if (horaAnalizada.equals(mov.getHoraMovimiento())) {
//                        it.remove();
//                    }
//                }
//            }
//        }
//
//        return horarios;
        List<Date> horas = new ArrayList<>();
        List<Date> horasMovimientos = new ArrayList<>();

        if (movimientos != null && !movimientos.isEmpty()) {
            for (MovimientoSalud mov : movimientos) {
                horasMovimientos.add(mov.getHoraMovimiento());
            }

            for (Date h : horarios) {
                if (!horasMovimientos.contains(h)) {
                    horas.add(h);
                }
            }

            return horas;
        } else {

            return horarios;
        }

    }

    public List<Date> completeHorario(Date fechaMovimiento, EntidadComercial profesional) {

        List<MovimientoSalud> movimientos = new ArrayList<MovimientoSalud>();
        movimientos = saludDAO.getMovimientoSaludEspera(fechaMovimiento, profesional);
        Date hora = null;
        Date horarioInicioMañana = null;
        Date horarioFinMañana = null;
        Date horarioInicioTarde = null;
        Date horarioFinTarde = null;
        Boolean flag = false;
        Boolean atiendeMañana = false;
        Boolean atiendeTarde = false;
        Integer duracionTurno = profesional.getDuracionTurno();

        GregorianCalendar gcalendario = new GregorianCalendar();
        gcalendario.setTime(fechaMovimiento);

        if (profesional.getHorarios() != null && !profesional.getHorarios().isEmpty()) {
            for (EntidadHorario horario : profesional.getHorarios()) {
                if (Integer.parseInt(horario.getDiaSemana().substring(0, 1).trim()) == gcalendario.get(Calendar.DAY_OF_WEEK)) {
                    horarioInicioMañana = horario.getHoraInicioMañana();
                    horarioFinMañana = horario.getHoraFinMañana();
                    horarioInicioTarde = horario.getHoraInicioTarde();
                    horarioFinTarde = horario.getHoraFinTarde();
                    atiendeMañana = horario.getAtiendeTurnoMañana().equals("S");
                    atiendeTarde = horario.getAtiendeTurnoTarde().equals("S");
                    flag = true;
                    break;  //En BD tenemos que colocar la opcion de que profesional y dia de la semana no se pueden repetir
                }
            }
        }

        if (flag) {
            GregorianCalendar gc = new GregorianCalendar();
            List<Date> horarios = new ArrayList<>();

            if (atiendeMañana) {

                //Turno mañana
                horarios.add(horarioInicioMañana);

                gc.setTime(horarioInicioMañana);
                gc.set(Calendar.MINUTE, gc.get(Calendar.MINUTE) + duracionTurno);
                hora = gc.getTime();
                while (hora.before(horarioFinMañana)) {
                    horarios.add(hora);
                    gc.setTime(hora);
                    gc.set(Calendar.MINUTE, gc.get(Calendar.MINUTE) + duracionTurno);
                    hora = gc.getTime();
                }
            }

            if (atiendeTarde) {

                //Turno Tarde
                horarios.add(horarioInicioTarde);

                gc.setTime(horarioInicioTarde);
                gc.set(Calendar.MINUTE, gc.get(Calendar.MINUTE) + duracionTurno);
                hora = gc.getTime();
                while (hora.before(horarioFinTarde)) {
                    horarios.add(hora);
                    gc.setTime(hora);
                    gc.set(Calendar.MINUTE, gc.get(Calendar.MINUTE) + duracionTurno);
                    hora = gc.getTime();
                }

            }

            List<Date> horas = new ArrayList<>();
            List<Date> horasMovimientos = new ArrayList<>();

            if (movimientos != null && !movimientos.isEmpty()) {
                for (MovimientoSalud mov : movimientos) {
                    horasMovimientos.add(mov.getHoraMovimiento());
                }

                for (Date h : horarios) {
                    if (!horasMovimientos.contains(h)) {
                        horas.add(h);
                    }
                }

                return horas;
            } else {

                return horarios;
            }

        } else {
            return new ArrayList<Date>();
        }

    }

    private void tomarNumeroFormulario(MovimientoSalud m) throws ExcepcionGeneralSistema, Exception {

        if (m.getFormulario().getTipoNumeracion().equals("A")) {
            m.setNumeroFormulario(formularioRN.tomarProximoNumero(m.getFormulario()));
        }

        formularioRN.guardar(m.getFormulario());

    }

    /**
     *
     * @param codFormulario
     * @param numeroFormulario
     * @return
     */
    public MovimientoSalud getMovimiento(String codFormulario, Integer numeroFormulario) {

        return saludDAO.getMovimientoSalud(codFormulario, numeroFormulario);
    }

    public MovimientoSalud getMovimiento(Integer id) {

        return saludDAO.getMovimientoSalud(id);
    }

    public MovimientoSalud revertirMovimiento(MovimientoSalud mRevertir) throws Exception {
//        System.out.println("mRevertir = " + mRevertir.getComprobante().getTipoComprobante());
        MovimientoSalud mr = generarMovimientoReversion(mRevertir);
        mr = guardar(mr);

        mRevertir.setEstado(estadoSaludRN.getEstadoSalud("X"));
        mRevertir.setMovimientoReversion(mr);

        guardar(mRevertir);

        return mr;
    }

    public MovimientoSalud generarMovimientoReversion(MovimientoSalud m) throws ExcepcionGeneralSistema {

        if (m == null) {
            throw new ExcepcionGeneralSistema("Movimiento de salud nulo");
        }

        if (m.getEstado().getCodigo().equals("Z") || m.getEstado().getCodigo().equals("H")) {
            throw new ExcepcionGeneralSistema("El estado actual del comprobante no permite anular el movimiento");
        }

        if (m.getMovimientoReversion() != null) {
            throw new ExcepcionGeneralSistema("El comprobante ya fue anulado con " + m.getMovimientoReversion().getFormulario().getDescripcion() + " - " + m.getMovimientoReversion().getNumeroFormulario());
        }

        if (m.getComprobante().getComprobanteReversion() == null) {
            throw new ExcepcionGeneralSistema("El comprobante " + m.getComprobante().getDescripcion() + " no tienen comprobante de reversión asociado");
        }

//        if (cuentaCorrienteRN.getSaldoByMovimiento(m.getId()) == 0) {
//            throw new ExcepcionGeneralSistema("El comprobante " + m.getComprobante().getDescripcion() + " ha sido cobrado, anule el/los recibo/s e intente de nuevo ");
//        }
        Formulario formulario = formularioRN.getFormulario(m.getComprobante().getComprobanteReversion(), m.getPuntoVenta(), "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de salud para el comprobante (" + m.getComprobante().getComprobanteReversion().getCodigo() + ") "
                    + "para El punto de venta (" + m.getPuntoVenta().getCodigo() + ") "
                    + "con la condición de iva (X)");
        }

        MovimientoSalud mr = new MovimientoSalud();

        mr.setEstado(estadoSaludRN.getEstadoSalud("A"));
        mr.setEmpresa(m.getEmpresa());
        mr.setSucursal(m.getSucursal());
        mr.setPuntoVenta(m.getPuntoVenta());

        mr.setComprobante(m.getComprobante().getComprobanteReversion());
        mr.setFormulario(formulario);

        mr.setFechaMovimiento(new Date());
        mr.setHoraMovimiento(m.getHoraMovimiento());

        asignarPaciente(mr, m.getPaciente());

        asignarProfesional(mr, m.getProfesional());

        asignarObraSocial(mr, m.getObraSocial(), m.getPaciente());

        mr.setNumeroAfiliado(m.getNumeroAfiliado());

        mr.setObservaciones("");
        mr.setMovimientoReversion(m);
        return mr;
    }

    public void asignarPaciente(MovimientoSalud m, EntidadComercial paciente) throws ExcepcionGeneralSistema {

        if (m == null || paciente == null) {
            return;
        }

        m.setPaciente(paciente);

        m.setNombrePaciente(paciente.getApellido() + " " + paciente.getNombre());
        m.setTipoDocumento(paciente.getTipoDocumento());
        m.setNrodoc(paciente.getNrodoc());

        m.setDireccion(paciente.getDireccion());

        m.setProvincia(paciente.getProvincia());
        m.setLocalidad(paciente.getLocalidad());
        m.setDireccion(paciente.getDireccion());

//        asignarFormulario(m);
    }

    public void asignarProfesional(MovimientoSalud m, EntidadComercial profesional) throws ExcepcionGeneralSistema {
        if (m == null || profesional == null) {
            return;
        }

        m.setProfesional(profesional);

        m.setNombreProfesional(profesional.getApellido() + "" + profesional.getNombre());

//        asignarFormulario(m);
    }

    public void asignarObraSocial(MovimientoSalud m, EntidadComercial obraSocial, EntidadComercial paciente) throws ExcepcionGeneralSistema {

        if (m == null || obraSocial == null) {
            return;
        }

        List<EntidadObraSocial> listaObrasSociales = paciente.getObraSociales();

        if (listaObrasSociales != null && !listaObrasSociales.isEmpty()) {
            for (EntidadObraSocial l : listaObrasSociales) {
                if (l.getObraSocial().equals(obraSocial) && l.getPaciente().equals(paciente)) {
                    m.setNumeroAfiliado(l.getNroAfiliado());
                }

            }
        }

        m.setObraSocial(obraSocial);

        m.setNombreObraSocial(obraSocial.getRazonSocial());

    }

    public Integer getIdTurno(SelectEvent selectEvent) {

        DefaultScheduleEvent evento = (DefaultScheduleEvent) selectEvent.getObject();
        String titulo[] = evento.getTitle().split("-");
        Integer idTurno = Integer.parseInt(titulo[0]);

        return idTurno;

    }

    public ScheduleModel actualizarCalendario(List<MovimientoSalud> lista) throws ParseException {
        eventModel = new DefaultScheduleModel();
        ParametroSalud ps = parametroSaludRN.getParametro();

        if (lista != null && !lista.isEmpty()) {

            for (MovimientoSalud t : lista) {

                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(t.getHoraMovimiento());
                int hora = gc.get(Calendar.HOUR_OF_DAY);
                int minuto = gc.get(Calendar.MINUTE);
                int segundo = gc.get(Calendar.SECOND);

                gc.setTime(t.getFechaMovimiento());
                int año = gc.get(Calendar.YEAR);
                int mes = gc.get(Calendar.MONTH) + 1;
                int dia = gc.get(Calendar.DAY_OF_MONTH);

                Formatter fmtDiaInicio = new Formatter();
                Formatter fmtMesInicio = new Formatter();
                Formatter fmtHoraInicio = new Formatter();
                Formatter fmtMinutoInicio = new Formatter();
                Formatter fmtSegundoInicio = new Formatter();

                String fechaStringInicio = "" + año + "-" + String.valueOf(fmtMesInicio.format("%02d", mes)) + "-" + String.valueOf(fmtDiaInicio.format("%02d", dia)) + " " + String.valueOf(fmtHoraInicio.format("%02d", hora)) + ": " + String.valueOf(fmtMinutoInicio.format("%02d", minuto)) + ": " + String.valueOf(fmtSegundoInicio.format("%02d", segundo)) + "";

                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date fechaDateInicio = formato.parse(fechaStringInicio);

                //Agregue 15 minutos de intervalo entre turno y turno
                gc.setTime(fechaDateInicio);
                gc.set(Calendar.MINUTE, gc.get(Calendar.MINUTE) + ps.getDuracionTurno());
                Date fechaDateFin = gc.getTime();

                DefaultScheduleEvent event = new DefaultScheduleEvent(t.getId() + "-" + t.getPaciente().getApellido() + " " + t.getPaciente().getNombre(), fechaDateInicio, fechaDateFin);
                eventModel.addEvent(event);
            }

            return eventModel;
        }

        return eventModel;
    }

    public void nuevoAdjunto(MovimientoSalud movimiento) throws ExcepcionGeneralSistema {

        if (movimiento == null) {
            throw new ExcepcionGeneralSistema("No existe un movimiento seleccionado para agregarle un Adjunto");
        }

        if (movimiento.getAdjuntos() == null) {
            movimiento.setAdjuntos(new ArrayList<ArchivoAdjunto>());
        }

        ArchivoAdjunto archivoAdjunto = new ArchivoAdjunto();

        archivoAdjunto.setMovimientoSalud(movimiento);

        movimiento.getAdjuntos().add(archivoAdjunto);

    }

    public void eliminarAdjunto(MovimientoSalud movimiento, ArchivoAdjunto archivoAdjunto) throws ExcepcionGeneralSistema, Exception {
        if (movimiento == null) {
            throw new ExcepcionGeneralSistema("movimiento vacío, nada para eliminar");
        }
        if (archivoAdjunto == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para eliminar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ArchivoAdjunto item : movimiento.getAdjuntos()) {

            if (item.getId() >= 0) {

                if (item.getId() == archivoAdjunto.getId()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }
        i = 0;

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ArchivoAdjunto remove = movimiento.getAdjuntos().remove(indiceItemBorrar);
            if (remove != null) {
                if (movimiento.getId() != null && remove.getId() != null) {

                    saludDAO.eliminar(ArchivoAdjunto.class, remove.getId());

                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public void cargarHistoriaClinica(MovimientoSalud m) {

        if (m.getPaciente() == null) {
            return;
        }

        Map<String, String> filtro = saludDAO.getFiltro();

        filtro.put("comprobante.tipoComprobante = ", "'IA'");
        filtro.put("paciente.nrocta = ", "'" + m.getPaciente().getNrocta() + "'");

        m.setHistoriaClinica(saludDAO.getListaByBusqueda(filtro, 100));

    }

}
