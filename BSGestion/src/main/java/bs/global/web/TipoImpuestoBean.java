/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.global.modelo.TipoImpuesto;
import bs.global.rn.TipoImpuestoRN;
import bs.global.util.JsfUtil;
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
public class TipoImpuestoBean extends GenericBean implements Serializable {

    private TipoImpuesto tipoDeImpuesto;
    private List<TipoImpuesto> lista;
    private String CODIGO;

    @EJB
    private TipoImpuestoRN tipoDeImpuestoRN;

    // Variables para filtros
    //
    public TipoImpuestoBean() {

    }

    @PostConstruct
    public void init() {

        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (CODIGO != null && !CODIGO.isEmpty()) {
                    seleccionar((TipoImpuesto) tipoDeImpuestoRN.getTipoImpuesto(CODIGO));
                }

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {
        esNuevo = true;
        buscaMovimiento = false;
        modoVista = "D";
        tipoDeImpuesto = new TipoImpuesto();
    }

    public void guardar(boolean nuevo) {

        try {
            if (tipoDeImpuesto != null) {
                tipoDeImpuestoRN.guardar(tipoDeImpuesto, esNuevo);
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
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            tipoDeImpuesto.getAuditoria().setDebaja(habDes);
            tipoDeImpuestoRN.guardar(tipoDeImpuesto, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            tipoDeImpuestoRN.eliminar(tipoDeImpuesto);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = tipoDeImpuestoRN.getTipoByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<TipoImpuesto> complete(String query) {
        try {
            lista = tipoDeImpuestoRN.getTipoByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TipoImpuesto>();
        }
    }

    public void onSelect(SelectEvent event) {
        tipoDeImpuesto = (TipoImpuesto) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(TipoImpuesto t) {

        if (t == null) {
            return;
        }

        tipoDeImpuesto = t;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (tipoDeImpuesto == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public TipoImpuesto getTipoDeImpuesto() {
        return tipoDeImpuesto;
    }

    public void setTipoDeImpuesto(TipoImpuesto tipoDeImpuesto) {
        this.tipoDeImpuesto = tipoDeImpuesto;
    }

    public List<TipoImpuesto> getLista() {
        return lista;
    }

    public void setLista(List<TipoImpuesto> lista) {
        this.lista = lista;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

}
