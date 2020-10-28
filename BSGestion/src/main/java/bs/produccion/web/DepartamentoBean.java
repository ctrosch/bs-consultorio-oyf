/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.produccion.modelo.DepartamentoProduccion;
import bs.produccion.rn.DepartamentoProduccionRN;
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
public class DepartamentoBean extends GenericBean implements Serializable {

    private DepartamentoProduccion departamento;
    private List<DepartamentoProduccion> lista;

    @EJB
    private DepartamentoProduccionRN departamentoRN;

    public DepartamentoBean() {

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
        departamento = new DepartamentoProduccion();
    }

    public void guardar(boolean nuevo) {

        try {
            if (departamento != null) {

                departamentoRN.guardar(departamento, esNuevo);
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
            departamento.getAuditoria().setDebaja(habDes);
            departamentoRN.guardar(departamento, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            departamentoRN.eliminar(departamento);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        lista = departamentoRN.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<DepartamentoProduccion> complete(String query) {
        try {
            lista = departamentoRN.getListaByBusqueda(query, false, cantidadRegistros);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<DepartamentoProduccion>();
        }
    }

    public void onSelect(SelectEvent event) {
        departamento = (DepartamentoProduccion) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(DepartamentoProduccion u) {

        if (u == null) {
            return;
        }

        departamento = u;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void imprimir() {

        if (departamento == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public DepartamentoProduccion getDepartamentoProduccion() {
        return departamento;
    }

    public void setDepartamentoProduccion(DepartamentoProduccion unidadMedida) {
        this.departamento = unidadMedida;
    }

    public List<DepartamentoProduccion> getLista() {
        return lista;
    }

    public void setLista(List<DepartamentoProduccion> lista) {
        this.lista = lista;
    }
}
