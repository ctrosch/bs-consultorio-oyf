/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.web;

import bs.educacion.modelo.TipoCarrera;
import bs.educacion.rn.TipoCarreraRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import java.io.Serializable;
import java.util.ArrayList;
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
public class TipoCarreraBean extends GenericBean implements Serializable {

    private String CODIGO;
    private TipoCarrera tipoCarrera;
    private List<TipoCarrera> lista;

    @EJB
    private TipoCarreraRN tipoCarreraRN;

    public TipoCarreraBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (CODIGO != null && !CODIGO.isEmpty()) {
                    seleccionar(tipoCarreraRN.getTipoCarrera(CODIGO));
                } else {
                    buscar();
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
        tipoCarrera = new TipoCarrera();

    }

    public void guardar(boolean nuevo) {

        try {
            if (tipoCarrera != null) {

                tipoCarrera = tipoCarreraRN.guardar(tipoCarrera, esNuevo);
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
            tipoCarrera.getAuditoria().setDebaja(habDes);
            tipoCarreraRN.guardar(tipoCarrera, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            tipoCarreraRN.eliminar(tipoCarrera);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        lista = tipoCarreraRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<TipoCarrera> complete(String query) {
        try {

            lista = tipoCarreraRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TipoCarrera>();
        }
    }

    public void onSelect(SelectEvent event) {
        tipoCarrera = (TipoCarrera) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(TipoCarrera t) {

        if (t == null) {
            return;
        }

        tipoCarrera = t;
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (tipoCarrera == null) {
                JsfUtil.addErrorMessage("No hay Tipo de Carrera activo");
                return;
            }

            tipoCarrera = tipoCarreraRN.duplicar(tipoCarrera);
            esNuevo = true;
            modoVista = "D";

        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (tipoCarrera == null) {
            JsfUtil.addErrorMessage("No hay Tipo de Carrera seleccionado, nada para imprimir");
        }
    }

    public List<TipoCarrera> getLista() {
        return lista;
    }

    public void setLista(List<TipoCarrera> lista) {
        this.lista = lista;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

    public TipoCarrera getTipoCarrera() {
        return tipoCarrera;
    }

    public void setTipoCarrera(TipoCarrera tipoCarrera) {
        this.tipoCarrera = tipoCarrera;
    }

}
