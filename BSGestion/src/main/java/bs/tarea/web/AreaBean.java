/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tarea.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.tarea.modelo.Area;
import bs.tarea.rn.AreaRN;
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
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class AreaBean extends GenericBean implements Serializable {

    private Area area;
    private List<Area> lista;

    @EJB
    private AreaRN areaRN;

    public AreaBean() {

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
        area = new Area();
    }

    public void guardar(boolean nuevo) {

        try {
            if (area != null) {

                areaRN.guardar(area, esNuevo);
                esNuevo = false;
                buscaMovimiento = false;
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
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
            area.getAuditoria().setDebaja(habDes);
            areaRN.guardar(area, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            areaRN.eliminar(area);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        lista = areaRN.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<Area> complete(String query) {
        try {
            lista = areaRN.getListaByBusqueda(query, false, cantidadRegistros);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Area>();
        }
    }

    public void onSelect(SelectEvent event) {
        area = (Area) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(Area u) {

        if (u == null) {
            return;
        }

        area = u;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void imprimir() {

        if (area == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area unidadMedida) {
        this.area = unidadMedida;
    }

    public List<Area> getLista() {
        return lista;
    }

    public void setLista(List<Area> lista) {
        this.lista = lista;
    }
}
