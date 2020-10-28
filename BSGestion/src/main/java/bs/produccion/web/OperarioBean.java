/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.produccion.modelo.Operario;
import bs.produccion.rn.OperarioRN;
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
public class OperarioBean extends GenericBean implements Serializable {

    private Operario operario;
    private List<Operario> lista;

    @EJB
    private OperarioRN operarioRN;

    public OperarioBean() {

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
        operario = new Operario();
    }

    public void guardar(boolean nuevo) {

        try {
            if (operario != null) {

                operarioRN.guardar(operario, esNuevo);
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
            operario.getAuditoria().setDebaja(habDes);
            operarioRN.guardar(operario, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            operarioRN.eliminar(operario);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        lista = operarioRN.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<Operario> complete(String query) {
        try {
            lista = operarioRN.getListaByBusqueda(query, false, 5);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Operario>();
        }
    }

    public void onSelect(SelectEvent event) {
        operario = (Operario) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(Operario u) {

        if (u == null) {
            return;
        }

        operario = u;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void imprimir() {

        if (operario == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Operario getOperario() {
        return operario;
    }

    public void setOperario(Operario unidadMedida) {
        this.operario = unidadMedida;
    }

    public List<Operario> getLista() {
        return lista;
    }

    public void setLista(List<Operario> lista) {
        this.lista = lista;
    }
}
