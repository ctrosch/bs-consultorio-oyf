/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.web;

import bs.entidad.modelo.EntidadComercial;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.salud.modelo.MovimientoSalud;
import bs.salud.modelo.ReservaTurno;
import bs.salud.rn.ReservaTurnoRN;
import bs.salud.rn.SaludRN;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author GUILLERMO
 */
@ManagedBean
@ViewScoped
public class GestionReservaTurnoBean extends GenericBean implements Serializable {

    protected MovimientoSalud m;
    protected MovimientoSalud mReversion;
    protected MovimientoSalud mBusqueda;

    private ReservaTurno reservaTurno;
    private List<ReservaTurno> lista;
    private EntidadComercial profesional;
    private Boolean isOK;
    private ScheduleModel eventModel;
    private ScheduleEvent event;

    //Datos inicializacion registracion
    protected String PTOSA = "";
    protected String CODSA = "";

    @EJB
    private ReservaTurnoRN reservaTurnoRN;

    @EJB
    private SaludRN saludRN;

    public GestionReservaTurnoBean() {

    }

    @PostConstruct
    public void init() {

        eventModel = new DefaultScheduleModel();
        event = new DefaultScheduleEvent();

    }

    public void nuevo() {

        try {
            esNuevo = true;
//            reservaTurno = reservaTurnoRN.nuevoMovimiento("SA", "TUR", "0001");
//            reservaTurno.setFechaMovimiento(null);

            mBusqueda = null;
            mReversion = null;

            m = saludRN.nuevoMovimiento(CODSA, PTOSA);

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible generar el turno");
        }
    }

    public void guardar(boolean nuevo) {

        try {
            if (reservaTurno != null) {

                isOK = reservaTurnoRN.controlTurno(lista, reservaTurno);

                if (isOK) {
                    reservaTurno = reservaTurnoRN.guardar(reservaTurno);
                    esNuevo = false;

                    JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                    if (nuevo) {
                        nuevo();
                    }
                    actualizarCalendario();
                } else {

                    JsfUtil.addErrorMessage("No es posible guardar los datos, ya hay un turno dado en esa fecha y hora ");
                }

            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            reservaTurno.getAuditoria().setDebaja(habDes);
            reservaTurno = reservaTurnoRN.guardar(reservaTurno);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");
            actualizarCalendario();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        lista = reservaTurnoRN.getListaByBusqueda(profesional, txtBusqueda, mostrarDebaja, 0);

    }

    public void actualizarCalendario() {
        try {
            buscar();
            eventModel = new DefaultScheduleModel();

            eventModel = reservaTurnoRN.actualizarCalendario(lista);

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar calendario" + ex);
        }
    }

    public void onEventSelect(SelectEvent selectEvent) {
        reservaTurno = reservaTurnoRN.getReservaTurno(reservaTurnoRN.getIdTurno(selectEvent));
        esNuevo = false;
    }

    public void onDateSelect(SelectEvent selectEvent) {

        nuevo();
        reservaTurno = reservaTurnoRN.nuevoTurno(profesional, reservaTurno, selectEvent);

    }

    //----------------------------------------------------------------------------------------------
    public ReservaTurno getReservaTurno() {
        return reservaTurno;
    }

    public void setReservaTurno(ReservaTurno reservaTurno) {
        this.reservaTurno = reservaTurno;
    }

    public List<ReservaTurno> getLista() {
        return lista;
    }

    public void setLista(List<ReservaTurno> lista) {
        this.lista = lista;
    }

    public EntidadComercial getProfesional() {
        return profesional;
    }

    public void setProfesional(EntidadComercial profesional) {
        this.profesional = profesional;
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

    public Boolean getIsOK() {
        return isOK;
    }

    public void setIsOK(Boolean isOK) {
        this.isOK = isOK;
    }

}
