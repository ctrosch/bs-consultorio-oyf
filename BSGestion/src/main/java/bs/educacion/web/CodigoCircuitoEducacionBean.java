/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.web;

import bs.educacion.modelo.CodigoCircuitoEducacion;
import bs.educacion.rn.CodigoCircuitoEducacionRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
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
public class CodigoCircuitoEducacionBean extends GenericBean implements Serializable {

    @EJB
    private CodigoCircuitoEducacionRN codigoCircuitoEducacionRN;

    private CodigoCircuitoEducacion codigoCircuitoEducacion;
    private List<CodigoCircuitoEducacion> lista;

    private String CODIGO;

    // Variables para filtros
    ///
    public CodigoCircuitoEducacionBean() {

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
                    seleccionar(codigoCircuitoEducacionRN.getCodigoCircuitoEducacion(CODIGO));
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
        codigoCircuitoEducacion = new CodigoCircuitoEducacion();
    }

    public void guardar(boolean nuevo) {

        try {
            if (codigoCircuitoEducacion != null) {
                codigoCircuitoEducacionRN.guardar(codigoCircuitoEducacion, esNuevo);
                esNuevo = false;
                modoVista = "D";
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            codigoCircuitoEducacion.getAuditoria().setDebaja(habDes);
            codigoCircuitoEducacionRN.guardar(codigoCircuitoEducacion, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizaron los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            codigoCircuitoEducacionRN.eliminar(codigoCircuitoEducacion);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = codigoCircuitoEducacionRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<CodigoCircuitoEducacion> complete(String query) {
        try {
            lista = codigoCircuitoEducacionRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CodigoCircuitoEducacion>();
        }
    }

    public void onSelect(SelectEvent event) {
        codigoCircuitoEducacion = (CodigoCircuitoEducacion) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(CodigoCircuitoEducacion c) {

        if (c == null) {
            return;
        }

        codigoCircuitoEducacion = c;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (codigoCircuitoEducacion == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public CodigoCircuitoEducacion getCodigoCircuitoEducacion() {
        return codigoCircuitoEducacion;
    }

    public void setCodigoCircuitoEducacion(CodigoCircuitoEducacion codigoCircuitoEducacion) {
        this.codigoCircuitoEducacion = codigoCircuitoEducacion;
    }

    public List<CodigoCircuitoEducacion> getLista() {
        return lista;
    }

    public void setLista(List<CodigoCircuitoEducacion> lista) {
        this.lista = lista;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

}
