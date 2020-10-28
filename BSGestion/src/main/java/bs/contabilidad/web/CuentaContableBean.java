/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.web;

import bs.contabilidad.modelo.CentroCosto;
import bs.contabilidad.modelo.CuentaContable;
import bs.contabilidad.modelo.CuentaContableCentroCosto;
import bs.contabilidad.modelo.Distribucion;
import bs.contabilidad.rn.CuentaContableRN;
import bs.contabilidad.rn.DistribucionRN;
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
public class CuentaContableBean extends GenericBean implements Serializable {

    private CuentaContable cuentaContable;
    private CentroCosto centroCosto;
    private List<CuentaContable> lista;
    private String imputable = "S";
    private String NROCTA = "";

    @EJB
    private CuentaContableRN cuentaContableRN;
    @EJB
    private DistribucionRN distribucionRN;

    public CuentaContableBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                imputable = "";
                cantidadRegistros = 300;
                super.iniciar();
                if (NROCTA != null && !NROCTA.isEmpty()) {
                    seleccionar(cuentaContableRN.getCuentaContable(NROCTA));
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

        try {
            cuentaContable = cuentaContableRN.nuevo(null);
            esNuevo = true;
            modoVista = "D";
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible crear una nueva cuenta " + ex);
        }
    }

    public void nuevoHijo(CuentaContable cuentaMadre) {

        try {
            cuentaContable = cuentaContableRN.nuevo(cuentaMadre);
            esNuevo = true;
            modoVista = "D";
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible crear una nueva cuenta " + ex);
        }
    }

    public void guardar(boolean nuevo) {

        try {
            if (cuentaContable != null) {

                cuentaContable = cuentaContableRN.guardar(cuentaContable, esNuevo);
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
            cuentaContable.getAuditoria().setDebaja(habDes);
            cuentaContable = cuentaContableRN.guardar(cuentaContable, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            cuentaContableRN.eliminar(cuentaContable);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        lista = cuentaContableRN.getListaByBusqueda(txtBusqueda, imputable, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void asignarNumeroCuenta() {

        if (cuentaContable.getCuentaContable() != null) {
            cuentaContable.setNrocta(cuentaContableRN.obtenerSiguienteCogido(cuentaContable.getCuentaContable()));
        }

    }

    public List<CuentaContable> completeImputable(String query) {
        try {
            return cuentaContableRN.getListaByBusqueda(query, "S", false, 10);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CuentaContable>();
        }
    }

    public List<CuentaContable> completeNoImputable(String query) {
        try {

            return cuentaContableRN.getListaByBusqueda(query, "N", false, 10);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CuentaContable>();
        }
    }

    public List<Distribucion> completeDistribucion(String query) {

        try {
            if (centroCosto == null) {
                return new ArrayList<Distribucion>();
            }

            return distribucionRN.getListaByBusqueda(centroCosto, query, false, 10);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Distribucion>();
        }
    }

    public void onCentroCostoSelect(SelectEvent event) {
        centroCosto = (CentroCosto) event.getObject();
    }

    public void onSelect(SelectEvent event) {

        cuentaContable = (CuentaContable) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(CuentaContable d) {

        if (d == null) {
            return;
        }

        cuentaContable = d;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (cuentaContable == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void nuevoItemCentroCosto() {

        try {
            cuentaContableRN.nuevoItemCentroCosto(cuentaContable);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item centro de costo: " + ex);
        }
    }

    public void eliminarItemCentroCosto(CuentaContableCentroCosto itemCentroCosto) {

        try {
            cuentaContableRN.eliminarItemCentroCosto(cuentaContable, itemCentroCosto);
            JsfUtil.addWarningMessage("Se ha borrado el item centro de costo " + (itemCentroCosto.getCentroCosto() != null ? itemCentroCosto.getCentroCosto().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemCentroCosto.getCentroCosto() != null ? itemCentroCosto.getCentroCosto().getDescripcion() : "") + " " + ex);
        }
    }

    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public List<CuentaContable> getLista() {
        return lista;
    }

    public void setLista(List<CuentaContable> lista) {
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

    public String getNROCTA() {
        return NROCTA;
    }

    public void setNROCTA(String NROCTA) {
        this.NROCTA = NROCTA;
    }

}
