/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.proceso;

import bs.contabilidad.modelo.CentroCosto;
import bs.contabilidad.modelo.SubCuenta;
import bs.contabilidad.rn.CentroCostoRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
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
 * @author Ctrosch
 */
@ManagedBean
@ViewScoped
public class ImputacionCentroCostoBean extends GenericBean implements Serializable {

    private CentroCosto centroCosto;
    private List<CentroCosto> lista;
    private String imputable = "";

    @EJB
    private CentroCostoRN centroCostoRN;

    public ImputacionCentroCostoBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();
                buscar();
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        try {
            centroCosto = new CentroCosto();
            esNuevo = true;
            modoVista = "D";
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible crear una nueva cuenta " + ex);
        }
    }

    public void guardar(boolean nuevo) {

        try {
            if (centroCosto != null) {

                centroCosto = centroCostoRN.guardar(centroCosto, esNuevo);
                esNuevo = false;
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
            centroCosto.getAuditoria().setDebaja(habDes);
            centroCosto = centroCostoRN.guardar(centroCosto, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            centroCostoRN.eliminar(centroCosto);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        lista = centroCostoRN.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public List<CentroCosto> complete(String query) {
        try {
            lista = centroCostoRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CentroCosto>();
        }
    }

    public void onSelect(SelectEvent event) {
        centroCosto = (CentroCosto) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(CentroCosto d) {

        if (d == null) {
            return;
        }

        centroCosto = d;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (centroCosto == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void nuevoItemSubCuenta() {

        try {
            centroCostoRN.nuevoItemSubCuenta(centroCosto);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar la subcuenta: " + ex);
        }
    }

    public void eliminarItemSubCuenta(SubCuenta subCuenta) {

        try {
            centroCostoRN.eliminarItemSubCuenta(centroCosto, subCuenta);
            JsfUtil.addWarningMessage("Se ha borrado la subcuenta " + (subCuenta.getCentroCosto() != null ? subCuenta.getCentroCosto().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (subCuenta.getCentroCosto() != null ? subCuenta.getCentroCosto().getDescripcion() : "") + " " + ex);
        }
    }

    public CentroCosto getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(CentroCosto centroCosto) {
        this.centroCosto = centroCosto;
    }

    public List<CentroCosto> getLista() {
        return lista;
    }

    public void setLista(List<CentroCosto> lista) {
        this.lista = lista;
    }

    public String getImputable() {
        return imputable;
    }

    public void setImputable(String imputable) {
        this.imputable = imputable;
    }

}
