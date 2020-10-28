/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.web;

import bs.administracion.modelo.Evento;
import bs.administracion.modelo.Modulo;
import bs.administracion.rn.EventoRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class EventoBean extends GenericBean implements Serializable {

    private Integer id;
    private Evento evento;
    private List<Evento> lista;

    //Variable para filtros
    private Modulo modulo;
    private Date fechaDesde;
    private Date fechaHasta;

    @EJB
    private EventoRN eventoRN;

    public EventoBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                buscar();

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {
        esNuevo = true;
        modoVista = "D";
        evento = new Evento();
        evento.setId(eventoRN.getProximoCodigo());
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

    public void eliminar() {
        try {
            eventoRN.eliminar(evento);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = eventoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (fechaDesde != null) {

            filtro.put("fecha >= ", JsfUtil.getFechaSQL(fechaDesde));
        }

        if (fechaHasta != null) {

            filtro.put("fecha <= ", JsfUtil.getFechaSQL(fechaHasta));
        }

        if (modulo != null) {

            filtro.put("modulo.codigo = ", "'" + modulo.getCodigo() + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        modulo = null;
        fechaDesde = null;
        fechaHasta = null;

        buscar();

    }

    public List<Evento> complete(String query) {
        try {

            lista = eventoRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Evento>();
        }
    }

    public void onSelect(SelectEvent event) {
        evento = (Evento) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Evento e) {

        if (e == null) {
            return;
        }

        evento = e;
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (evento == null) {
                JsfUtil.addErrorMessage("No hay area activa");
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
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public List<Evento> getLista() {
        return lista;
    }

    public void setLista(List<Evento> lista) {
        this.lista = lista;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

}
