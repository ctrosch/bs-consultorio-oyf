/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.produccion.modelo.TipoOperario;
import bs.produccion.rn.TipoOperarioRN;
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
public class TipoOperarioBean extends GenericBean implements Serializable {

    private TipoOperario operario;
    private List<TipoOperario> lista;

    @EJB
    private TipoOperarioRN operarioRN;

    public TipoOperarioBean() {

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
        operario = new TipoOperario();
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

    public List<TipoOperario> complete(String query) {
        try {
            lista = operarioRN.getListaByBusqueda(query, false, cantidadRegistros);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TipoOperario>();
        }
    }

    public void onSelect(SelectEvent event) {
        operario = (TipoOperario) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(TipoOperario u) {

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

    public TipoOperario getTipoOperario() {
        return operario;
    }

    public void setTipoOperario(TipoOperario unidadMedida) {
        this.operario = unidadMedida;
    }

    public List<TipoOperario> getLista() {
        return lista;
    }

    public void setLista(List<TipoOperario> lista) {
        this.lista = lista;
    }
}
