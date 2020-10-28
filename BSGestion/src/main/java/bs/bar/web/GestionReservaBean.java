/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.web;

import bs.bar.modelo.Reserva;
import bs.bar.modelo.Salon;
import bs.bar.rn.ReservaRN;
import bs.bar.rn.SalonRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.chart.MeterGaugeChartModel;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class GestionReservaBean extends GenericBean implements Serializable {

    private Reserva reserva;
    private List<Reserva> lista;
    private List<Salon> salones;

    private int cantidadPersonas;
    private int cantidadReservas;
    private MeterGaugeChartModel medidor;

    @EJB
    private ReservaRN reservaRN;
    @EJB
    private SalonRN salonRN;

    public GestionReservaBean() {

    }

    @PostConstruct
    public void init() {

        txtBusqueda = "";
        mostrarDebaja = false;
        fechaMovimientoDesde = new Date();

        salones = salonRN.getListaByBusqueda("", false, 0);

        nuevo();
        buscar();
        actualizarTotales();
    }

    public void nuevo() {

        try {
            esNuevo = true;
            buscaMovimiento = false;
            reserva = reservaRN.nuevoMovimiento("BR", "RES", "0001");
            reserva.setFechaMovimiento(null);

            if (salones != null && !salones.isEmpty()) {
                reserva.setSalon(salones.get(0));
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible generar la reserva");
        }
    }

    public void guardar(boolean nuevo) {

        try {
            if (reserva != null) {

                reserva = reservaRN.guardar(reserva);
                esNuevo = false;
                buscaMovimiento = false;
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }

                actualizarTotales();
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
            reserva.getAuditoria().setDebaja(habDes);
            reserva = reservaRN.guardar(reserva);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

            actualizarTotales();

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void deshabilitar(Reserva r) {

        try {
            r.getAuditoria().setDebaja("S");
            reserva = reservaRN.guardar(r);
            buscar();
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");
            actualizarTotales();

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            reservaRN.eliminar(reserva);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        lista = reservaRN.getListaByBusqueda(fechaMovimientoDesde, txtBusqueda, mostrarDebaja, cantidadRegistros);

    }

    public List<String> completeContacto(String query) {
        try {

            List<String> lista = reservaRN.getContactos(query);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<String>();
        }
    }

    public void onSelect(SelectEvent event) {
        reserva = (Reserva) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        fechaMovimientoDesde = reserva.getFechaMovimiento();
        actualizarTotales();
    }

    public void seleccionar(Reserva u) {

        if (u == null) {
            return;
        }

        reserva = u;
        esNuevo = false;
        buscaMovimiento = false;
        fechaMovimientoDesde = u.getFechaMovimiento();
        actualizarTotales();
    }

    public void imprimir() {

        if (reserva == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void actualizarTotales() {

        Salon salon = null;

        if (reserva != null) {

            salon = reserva.getSalon();

            if (reserva.getFechaMovimiento() != null) {
                fechaMovimientoDesde = reserva.getFechaMovimiento();
            }
        }

        cantidadReservas = reservaRN.getTotalReservasByFecha(fechaMovimientoDesde, salon);
        cantidadPersonas = reservaRN.getTotalPersonasByFecha(fechaMovimientoDesde, salon);

        if (salon != null) {

            List<Number> intervals = new ArrayList<Number>();

            intervals.add(salon.getCapacidad() - (salon.getCapacidad() * 0.4));
            intervals.add(salon.getCapacidad() - (salon.getCapacidad() * 0.15));
            intervals.add(salon.getCapacidad());

            medidor = new MeterGaugeChartModel(cantidadPersonas, intervals);
            //medidor.setTitle("Nivel de Reservas");
            medidor.setSeriesColors("66cc66,E7E658,cc6666");
            medidor.setGaugeLabel("Personas");
            medidor.setGaugeLabelPosition("bottom");
            medidor.setShowTickLabels(false);
            medidor.setLabelHeightAdjust(110);
            medidor.setIntervalOuterRadius(100);

        }

    }

    //----------------------------------------------------------------------------------------------
    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public List<Reserva> getLista() {
        return lista;
    }

    public void setLista(List<Reserva> lista) {
        this.lista = lista;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(int cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public int getCantidadReservas() {
        return cantidadReservas;
    }

    public void setCantidadReservas(int cantidadReservas) {
        this.cantidadReservas = cantidadReservas;
    }

    public List<Salon> getSalones() {
        return salones;
    }

    public void setSalones(List<Salon> salones) {
        this.salones = salones;
    }

    public MeterGaugeChartModel getMedidor() {
        return medidor;
    }

    public void setMedidor(MeterGaugeChartModel medidor) {
        this.medidor = medidor;
    }

}
