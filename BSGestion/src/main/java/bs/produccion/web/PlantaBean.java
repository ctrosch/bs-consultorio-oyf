/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.produccion.modelo.Planta;
import bs.produccion.rn.PlantaRN;
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
public class PlantaBean extends GenericBean implements Serializable {

    private Planta planta;
    private List<Planta> lista;

    @EJB
    private PlantaRN plantaRN;

    public PlantaBean() {

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
        planta = new Planta();
    }

    public void guardar(boolean nuevo) {

        try {
            if (planta != null) {

                plantaRN.guardar(planta, esNuevo);
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
            planta.getAuditoria().setDebaja(habDes);
            plantaRN.guardar(planta, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            plantaRN.eliminar(planta);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        lista = plantaRN.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<Planta> complete(String query) {
        try {
            lista = plantaRN.getListaByBusqueda(query, false, cantidadRegistros);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Planta>();
        }
    }

    public void onSelect(SelectEvent event) {
        planta = (Planta) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(Planta u) {

        if (u == null) {
            return;
        }

        planta = u;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void imprimir() {

        if (planta == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public List<Planta> getLista() {
        return lista;
    }

    public void setLista(List<Planta> lista) {
        this.lista = lista;
    }
}
