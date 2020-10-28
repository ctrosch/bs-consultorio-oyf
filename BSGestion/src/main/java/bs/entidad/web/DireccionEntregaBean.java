/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.web;

import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.rn.DireccionEntregaRN;
import bs.global.util.JsfUtil;
import bs.global.web.LocalidadBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class DireccionEntregaBean implements Serializable {

    private DireccionEntregaEntidad direccionEntrega;
    private List<DireccionEntregaEntidad> lista;
    private EntidadComercial entidad;

    private boolean mostrarDebaja;
    private boolean esNuevo;
    private String txtBusqueda;

    @EJB
    private DireccionEntregaRN direccionEntregaRN;

    @ManagedProperty(value = "#{localidadBean}")
    protected LocalidadBean localidadBean;

    public DireccionEntregaBean() {

    }

    @PostConstruct
    public void init() {
        mostrarDebaja = false;
        txtBusqueda = "";
        nuevo();
        buscar();
    }

    public void nuevo() {
        esNuevo = true;
        direccionEntrega = new DireccionEntregaEntidad();
    }

    public void nuevo(DireccionEntregaEntidad de) {
        esNuevo = true;
        direccionEntrega = de;
    }

    public void guardar(boolean nuevo) {

        try {
            if (direccionEntrega != null) {
                direccionEntregaRN.guardar(direccionEntrega, esNuevo);

                esNuevo = false;
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
            direccionEntrega.getAuditoria().setDebaja(habDes);
            direccionEntregaRN.guardar(direccionEntrega, false);
            JsfUtil.addInfoMessage("Los datos se deshabilitaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible deshabilitar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            direccionEntregaRN.eliminar(direccionEntrega);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        String nroCuenta = "";
        if (entidad != null) {
            nroCuenta = entidad.getNrocta();
        }

        lista = direccionEntregaRN.getListaByBusqueda(txtBusqueda, nroCuenta, mostrarDebaja);
    }

    public List<DireccionEntregaEntidad> complete(String query) {
        try {

            String nroCuenta = "";
            if (entidad != null) {
                nroCuenta = entidad.getNrocta();
            }

            lista = direccionEntregaRN.getListaByBusqueda(query, nroCuenta, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<DireccionEntregaEntidad>();
        }
    }

    public void onSelect(SelectEvent event) {
        direccionEntrega = (DireccionEntregaEntidad) event.getObject();
        esNuevo = false;
    }

    public void seleccionar(DireccionEntregaEntidad c) {

        if (c == null) {
            return;
        }

        direccionEntrega = c;
        esNuevo = false;
    }

    public void imprimir() {

        if (direccionEntrega == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void procesarLocalidad() {

        if (localidadBean.getLocalidad() != null && direccionEntrega != null) {

            direccionEntrega.setLocalidad(localidadBean.getLocalidad());
            direccionEntrega.setProvincia(localidadBean.getLocalidad().getProvincia());

        }
    }

    public void procesarCliente() {

        if (localidadBean.getLocalidad() != null && direccionEntrega != null) {

            direccionEntrega.setLocalidad(localidadBean.getLocalidad());
            direccionEntrega.setProvincia(localidadBean.getLocalidad().getProvincia());
        }
    }

    //--------------------------------------------------------------------------------------
    public List<DireccionEntregaEntidad> getLista() {
        return lista;
    }

    public void setLista(List<DireccionEntregaEntidad> lista) {
        this.lista = lista;
    }

    public boolean isMostrarDebaja() {
        return mostrarDebaja;
    }

    public void setMostrarDebaja(boolean mostrarDebaja) {
        this.mostrarDebaja = mostrarDebaja;
    }

    public boolean isEsNuevo() {
        return esNuevo;
    }

    public void setEsNuevo(boolean esNuevo) {
        this.esNuevo = esNuevo;
    }

    public String getTxtBusqueda() {
        return txtBusqueda;
    }

    public void setTxtBusqueda(String txtBusqueda) {
        this.txtBusqueda = txtBusqueda;
    }

    public DireccionEntregaEntidad getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(DireccionEntregaEntidad direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public LocalidadBean getLocalidadBean() {
        return localidadBean;
    }

    public void setLocalidadBean(LocalidadBean localidadBean) {
        this.localidadBean = localidadBean;
    }

    public EntidadComercial getEntidad() {
        return entidad;
    }

    public void setEntidad(EntidadComercial entidad) {
        this.entidad = entidad;
    }

}
