/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.web;

import bs.contabilidad.modelo.CentroCosto;
import bs.contabilidad.modelo.SubCuenta;
import bs.contabilidad.rn.SubCuentaRN;
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
 * @author Ctrosch
 */
@ManagedBean
@ViewScoped
public class SubCuentaBean extends GenericBean implements Serializable {

    private SubCuenta subCuenta;
    private List<SubCuenta> lista;
    private String imputable = "";
    private String codigo;

    //Para filtro
    private CentroCosto centroCosto;

    @EJB
    private SubCuentaRN subCuentaRN;

    public SubCuentaBean() {

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
                    seleccionar(subCuentaRN.getSubCuenta(codigo));
                }

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        try {
            subCuenta = new SubCuenta();
            esNuevo = true;
            modoVista = "D";
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible crear una nueva cuenta " + ex);
        }
    }

    public void guardar(boolean nuevo) {

        try {
            if (subCuenta != null) {

                subCuenta = subCuentaRN.guardar(subCuenta, esNuevo);
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
            subCuenta.getAuditoria().setDebaja(habDes);
            subCuenta = subCuentaRN.guardar(subCuenta, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            subCuentaRN.eliminar(subCuenta);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = subCuentaRN.getListaByBusqueda(filtro, centroCosto, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        centroCosto = null;

        buscar();

    }

    public List<SubCuenta> complete(String query) {
        try {
            lista = subCuentaRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<SubCuenta>();
        }
    }

    public void onSelect(SelectEvent event) {
        subCuenta = (SubCuenta) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(SubCuenta d) {

        if (d == null) {
            return;
        }

        subCuenta = d;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (subCuenta == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public SubCuenta getSubCuenta() {
        return subCuenta;
    }

    public void setSubCuenta(SubCuenta subCuenta) {
        this.subCuenta = subCuenta;
    }

    public List<SubCuenta> getLista() {
        return lista;
    }

    public void setLista(List<SubCuenta> lista) {
        this.lista = lista;
    }

    public String getImputable() {
        return imputable;
    }

    public void setImputable(String imputable) {
        this.imputable = imputable;
    }

    public CentroCosto getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(CentroCosto centroCosto) {
        this.centroCosto = centroCosto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
