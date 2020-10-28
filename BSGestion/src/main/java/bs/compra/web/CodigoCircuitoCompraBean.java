/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.compra.web;

import bs.compra.modelo.CodigoCircuitoCompra;
import bs.compra.rn.CodigoCircuitoCompraRN;
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
public class CodigoCircuitoCompraBean extends GenericBean implements Serializable {

    @EJB
    private CodigoCircuitoCompraRN codigoCircuitoFacturacionRN;

    private CodigoCircuitoCompra codigoCircuitoCompra;
    private List<CodigoCircuitoCompra> lista;
    private String CODIGO;

    // Variables para filtros
    ///
    public CodigoCircuitoCompraBean() {

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
                    seleccionar(codigoCircuitoFacturacionRN.getCodigoCircuitoCompra(CODIGO));
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
        codigoCircuitoCompra = new CodigoCircuitoCompra();
    }

    public void guardar(boolean nuevo) {

        try {
            if (codigoCircuitoCompra != null) {
                codigoCircuitoFacturacionRN.guardar(codigoCircuitoCompra, esNuevo);
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
            codigoCircuitoCompra.getAuditoria().setDebaja(habDes);
            codigoCircuitoFacturacionRN.guardar(codigoCircuitoCompra, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizaron los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            codigoCircuitoFacturacionRN.eliminar(codigoCircuitoCompra);
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

    public List<CodigoCircuitoCompra> complete(String query) {
        try {
            lista = codigoCircuitoFacturacionRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CodigoCircuitoCompra>();
        }
    }

    public void onSelect(SelectEvent event) {
        codigoCircuitoCompra = (CodigoCircuitoCompra) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(CodigoCircuitoCompra c) {

        if (c == null) {
            return;
        }

        codigoCircuitoCompra = c;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (codigoCircuitoCompra == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public CodigoCircuitoCompra getCodigoCircuitoCompra() {
        return codigoCircuitoCompra;
    }

    public void setCodigoCircuitoCompra(CodigoCircuitoCompra codigoCircuitoCompra) {
        this.codigoCircuitoCompra = codigoCircuitoCompra;
    }

    public List<CodigoCircuitoCompra> getLista() {
        return lista;
    }

    public void setLista(List<CodigoCircuitoCompra> lista) {
        this.lista = lista;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

}
