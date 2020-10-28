/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.web;

import bs.facturacion.modelo.CodigoCircuitoFacturacion;
import bs.facturacion.rn.CodigoCircuitoFacturacionRN;
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
public class CodigoCircuitoFacturacionBean extends GenericBean implements Serializable {

    private CodigoCircuitoFacturacion codigoCircuitoFacturacion;
    private List<CodigoCircuitoFacturacion> lista;
    private String CODIGO;

    @EJB
    private CodigoCircuitoFacturacionRN codigoCircuitoFacturacionRN;

    // Variables para filtros
    ////
    ///
    public CodigoCircuitoFacturacionBean() {

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
                    seleccionar(codigoCircuitoFacturacionRN.getCodigoCircuitoFacturacion(CODIGO));
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
        codigoCircuitoFacturacion = new CodigoCircuitoFacturacion();
    }

    public void guardar(boolean nuevo) {

        try {
            if (codigoCircuitoFacturacion != null) {
                codigoCircuitoFacturacion = codigoCircuitoFacturacionRN.guardar(codigoCircuitoFacturacion, esNuevo);
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
            codigoCircuitoFacturacion.getAuditoria().setDebaja(habDes);
            codigoCircuitoFacturacion = codigoCircuitoFacturacionRN.guardar(codigoCircuitoFacturacion, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizaron los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            codigoCircuitoFacturacionRN.eliminar(codigoCircuitoFacturacion);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = codigoCircuitoFacturacionRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<CodigoCircuitoFacturacion> complete(String query) {
        try {

            lista = codigoCircuitoFacturacionRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CodigoCircuitoFacturacion>();
        }
    }

    public void onSelect(SelectEvent event) {
        codigoCircuitoFacturacion = (CodigoCircuitoFacturacion) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(CodigoCircuitoFacturacion c) {

        if (c == null) {
            return;
        }

        codigoCircuitoFacturacion = c;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (codigoCircuitoFacturacion == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public CodigoCircuitoFacturacion getCodigoCircuitoFacturacion() {
        return codigoCircuitoFacturacion;
    }

    public void setCodigoCircuitoFacturacion(CodigoCircuitoFacturacion codigoCircuitoFacturacion) {
        this.codigoCircuitoFacturacion = codigoCircuitoFacturacion;
    }

    public List<CodigoCircuitoFacturacion> getLista() {
        return lista;
    }

    public void setLista(List<CodigoCircuitoFacturacion> lista) {
        this.lista = lista;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

}
