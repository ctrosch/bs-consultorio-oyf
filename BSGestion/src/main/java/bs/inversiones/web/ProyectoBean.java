/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.inversiones.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.inversiones.modelo.Proyecto;
import bs.inversiones.rn.ProyectoRN;
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
 * @author Agustin
 */
@ManagedBean
@ViewScoped
public class ProyectoBean extends GenericBean implements Serializable {

    private Proyecto proyecto;
    private List<Proyecto> lista;

    @EJB
    private ProyectoRN proyectoRN;

    /**
     * Creates a new instance of ProyectoBean
     */
    public ProyectoBean() {

    }

    @PostConstruct
    public void init() {

        txtBusqueda = "";
        mostrarDebaja = false;
        nuevo();
        buscar();
    }

    public void nuevo() {

        esNuevo = true;
        buscaMovimiento = false;
        proyecto = new Proyecto();
    }

    public void guardar(boolean nuevo) {

        try {
            if (proyecto != null) {

                proyectoRN.guardar(proyecto, esNuevo);
                esNuevo = false;
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ProyectoBean.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            proyecto.getAuditoria().setDebaja(habDes);
            proyectoRN.guardar(proyecto, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos" + ex);
        }
    }

    public void eliminar() {
        try {
            proyectoRN.eliminar(proyecto);
            nuevo();

            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(ProyectoBean.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos" + ex);
        }
    }

    public void buscar() {
        lista = proyectoRN.getListaByBusqueda(txtBusqueda, mostrarDebaja);
    }

    public List<Proyecto> complete(String query) {
        try {
            lista = proyectoRN.getListaByBusqueda(query, mostrarDebaja, 8);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Proyecto>();
        }
    }

    public void onSelect(SelectEvent event) {
        proyecto = (Proyecto) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(Proyecto c) {

        if (c == null) {
            return;
        }

        proyecto = c;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void imprimir() {

        if (proyecto == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public List<Proyecto> getLista() {
        return lista;
    }

    public void setLista(List<Proyecto> lista) {
        this.lista = lista;
    }

}
