/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.rn;

import bs.entidad.modelo.EntidadComercial;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.ComprobanteRN;
import bs.global.rn.FormularioRN;
import bs.global.rn.PuntoVentaRN;
import bs.salud.dao.ReservaTurnoDAO;
import bs.salud.modelo.ReservaTurno;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.List;
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
 * @author GUILLERMO
 */
@Stateless
public class ReservaTurnoRN implements Serializable {

    @EJB
    private ReservaTurnoDAO reservaTurnoDAO;

    @EJB
    protected PuntoVentaRN puntoVentaRN;
    @EJB
    protected ComprobanteRN comprobanteRN;

    @EJB
    private FormularioRN formularioRN;

    private ScheduleModel eventModel;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ReservaTurno guardar(ReservaTurno reservaTurno) throws Exception {

        if (reservaTurno.getId() == null) {
            reservaTurnoDAO.crear(reservaTurno);
        } else {
            reservaTurno = (ReservaTurno) reservaTurnoDAO.editar(reservaTurno);
        }

        return reservaTurno;
    }

    public ReservaTurno nuevoMovimiento(String modSA, String codSA, String sucSA) throws Exception {

        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(sucSA);
        Comprobante comprobante = comprobanteRN.getComprobante(modSA, codSA);
        Formulario formulario = formularioRN.getFormulario(comprobante, puntoVenta, "X");

        ReservaTurno reservaTurno = new ReservaTurno();
        reservaTurno.setComprobante(comprobante);
        reservaTurno.setPuntoVenta(puntoVenta);
        reservaTurno.setFormulario(formulario);
        reservaTurno.setFechaMovimiento(new Date());
        if (reservaTurno.getFormulario().getTipoNumeracion().equals("A")) {
            reservaTurno.setNumeroFormulario(formularioRN.tomarProximoNumero(reservaTurno.getFormulario()));
        }

        return reservaTurno;
    }

    public void eliminar(ReservaTurno reservaTurno) throws Exception {

        reservaTurnoDAO.eliminar(reservaTurno);

    }

    public ReservaTurno getReservaTurno(Integer cod) {
        return reservaTurnoDAO.getReservaTurno(cod);
    }

    public ScheduleModel actualizarCalendario(List<ReservaTurno> lista) throws ParseException {
        eventModel = new DefaultScheduleModel();

        if (lista != null && !lista.isEmpty()) {

            for (ReservaTurno t : lista) {

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
                Date fechaDateInicio = null;
                fechaDateInicio = formato.parse(fechaStringInicio);

                //Agregue 15 minutos de intervalo entre turno y turno
                gc.setTime(fechaDateInicio);
                gc.set(Calendar.MINUTE, gc.get(Calendar.MINUTE) + 15);
                Date fechaDateFin = null;
                fechaDateFin = gc.getTime();

                DefaultScheduleEvent event = new DefaultScheduleEvent(t.getId() + "-" + t.getPaciente().getApellido() + " " + t.getPaciente().getNombre(), fechaDateInicio, fechaDateFin);
                eventModel.addEvent(event);
            }

            return eventModel;
        }

        return eventModel;
    }

    public ReservaTurno nuevoTurno(EntidadComercial profesional, ReservaTurno reservaTurno, SelectEvent selectEvent) {
        Date fechaEvento = (Date) selectEvent.getObject();
        reservaTurno.setProfesional(profesional);
        reservaTurno.setFechaMovimiento(fechaEvento);

        return reservaTurno;
    }

    public Integer getIdTurno(SelectEvent selectEvent) {

        DefaultScheduleEvent evento = (DefaultScheduleEvent) selectEvent.getObject();
        String titulo[] = evento.getTitle().split("-");
        Integer idTurno = Integer.parseInt(titulo[0]);

        return idTurno;

    }

    public Boolean controlTurno(List<ReservaTurno> listaTurnos, ReservaTurno reservaTurno) {
        for (ReservaTurno turno : listaTurnos) {
            if (reservaTurno.getFechaMovimiento().equals(turno.getFechaMovimiento()) && reservaTurno.getHoraMovimiento().equals(turno.getHoraMovimiento()) && !reservaTurno.getPaciente().equals(turno.getPaciente())) {
                return false;
            }

        }

        return true;
    }

    public List<ReservaTurno> getListaByBusqueda(Date fechaMovimiento, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return reservaTurnoDAO.getListaByBusqueda(fechaMovimiento, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<ReservaTurno> getListaByBusqueda(EntidadComercial profesional, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return reservaTurnoDAO.getListaByBusqueda(profesional, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

}
