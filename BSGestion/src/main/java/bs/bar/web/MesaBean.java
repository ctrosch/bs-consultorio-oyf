/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.web;

import bs.bar.modelo.Mesa;
import bs.bar.rn.MesaRN;
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
public class MesaBean extends GenericBean implements Serializable {

    private Mesa mesa;
    private List<Mesa> lista;
    private String CODIGO;

    @EJB
    private MesaRN mesaRN;

    public MesaBean() {

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
                    seleccionar(mesaRN.getMesa(CODIGO));
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
        mesa = new Mesa();
    }

    public void guardar(boolean nuevo) {

        try {
            if (mesa != null) {

                mesaRN.guardar(mesa, esNuevo);
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
            mesa.getAuditoria().setDebaja(habDes);
            mesaRN.guardar(mesa, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = mesaRN.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<Mesa> complete(String query) {
        try {
            lista = mesaRN.getListaByBusqueda(query, false, 0);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Mesa>();
        }
    }

    public void onSelect(SelectEvent event) {
        mesa = (Mesa) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(Mesa d) {

        if (d == null) {
            return;
        }

        mesa = d;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (mesa == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public List<Mesa> getLista() {
        return lista;
    }

    public void setLista(List<Mesa> lista) {
        this.lista = lista;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

}
