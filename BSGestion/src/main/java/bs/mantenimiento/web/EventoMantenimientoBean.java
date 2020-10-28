/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.mantenimiento.modelo.EventoMantenimiento;
import bs.mantenimiento.rn.EventoMantenimientoRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author GUILLERMO
 */
@ManagedBean
@ViewScoped
public class EventoMantenimientoBean extends GenericBean implements Serializable {

    private String codigo;
    private EventoMantenimiento evento;
    private List<EventoMantenimiento> lista;

    @EJB
    private EventoMantenimientoRN eventoRN;

    public EventoMantenimientoBean() {

    }

    @PostConstruct
    public void init() {
        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(eventoRN.getEventoMantenimiento(codigo));
                }
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        esNuevo = true;
        modoVista = "D";
        evento = new EventoMantenimiento();
    }

    public void guardar(boolean nuevo) {

        try {
            if (evento != null) {

                evento = eventoRN.guardar(evento, esNuevo);
                esNuevo = false;
                modoVista = "D";
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            evento.getAuditoria().setDebaja(habDes);
            eventoRN.guardar(evento, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = eventoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();
    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        buscar();

    }

    public List<EventoMantenimiento> complete(String query) {
        try {

            lista = eventoRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<EventoMantenimiento>();
        }
    }

    public void onSelect(SelectEvent event) {
        evento = (EventoMantenimiento) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(EventoMantenimiento t) {

        if (t == null) {
            return;
        }

        evento = t;
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (evento == null) {
                JsfUtil.addErrorMessage("No hay Tipo de Mantenimiento activo");
                return;
            }

            evento = eventoRN.duplicar(evento);
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (evento == null) {
            JsfUtil.addErrorMessage("No hay Tipo de Mantenimiento seleccionado, nada para imprimir");
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public EventoMantenimiento getEventoMantenimiento() {
        return evento;
    }

    public void setEventoMantenimiento(EventoMantenimiento evento) {
        this.evento = evento;
    }

    public List<EventoMantenimiento> getLista() {
        return lista;
    }

    public void setLista(List<EventoMantenimiento> lista) {
        this.lista = lista;
    }

}
