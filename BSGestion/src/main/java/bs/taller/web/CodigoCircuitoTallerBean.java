/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.taller.modelo.CodigoCircuitoTaller;
import bs.taller.rn.CodigoCircuitoTallerRN;
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
public class CodigoCircuitoTallerBean extends GenericBean implements Serializable {

    private CodigoCircuitoTaller codigoCircuito;
    private List<CodigoCircuitoTaller> lista;
    private String codigo;

    @EJB
    private CodigoCircuitoTallerRN codigoCircuitoServiceRN;

    // Variables para filtros
    ///
    public CodigoCircuitoTallerBean() {

    }

    @PostConstruct
    public void init() {

        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(codigoCircuitoServiceRN.getCodigoCircuitoService(codigo));
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
        codigoCircuito = new CodigoCircuitoTaller();
    }

    public void guardar(boolean nuevo) {

        try {
            if (codigoCircuito != null) {
                codigoCircuitoServiceRN.guardar(codigoCircuito, esNuevo);
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
            codigoCircuito.getAuditoria().setDebaja(habDes);
            codigoCircuitoServiceRN.guardar(codigoCircuito, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizaron los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            codigoCircuitoServiceRN.eliminar(codigoCircuito);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = codigoCircuitoServiceRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<CodigoCircuitoTaller> complete(String query) {
        try {
            lista = codigoCircuitoServiceRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CodigoCircuitoTaller>();
        }
    }

    public void onSelect(SelectEvent event) {
        codigoCircuito = (CodigoCircuitoTaller) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(CodigoCircuitoTaller c) {

        if (c == null) {
            return;
        }

        codigoCircuito = c;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (codigoCircuito == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public CodigoCircuitoTaller getCodigoCircuito() {
        return codigoCircuito;
    }

    public void setCodigoCircuito(CodigoCircuitoTaller codigoCircuito) {
        this.codigoCircuito = codigoCircuito;
    }

    public List<CodigoCircuitoTaller> getLista() {
        return lista;
    }

    public void setLista(List<CodigoCircuitoTaller> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
