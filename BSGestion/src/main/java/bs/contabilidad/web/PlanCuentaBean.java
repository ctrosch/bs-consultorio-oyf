/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.web;

import bs.contabilidad.modelo.CuentaContable;
import bs.contabilidad.modelo.CuentaContableCentroCosto;
import bs.contabilidad.rn.CuentaContableRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Ctrosch
 */
@ManagedBean
@ViewScoped
public class PlanCuentaBean extends GenericBean implements Serializable {

    private CuentaContable cuentaContable;
    private List<CuentaContable> lista;
    private String imputable = "";

    private TreeNode arbol;
    private TreeNode nodoSeleccionado;

    @EJB
    private CuentaContableRN cuentaContableRN;

    public PlanCuentaBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                nuevo();
                actualizarArbol();

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void actualizarArbol() {

        lista = cuentaContableRN.getListaByNivel(1, false);
        arbol = new DefaultTreeNode();
        generarArbol(arbol, lista);
    }

    private void generarArbol(TreeNode raiz, List<CuentaContable> items) {

        if (items == null) {
            return;
        }

        if (items.isEmpty()) {
            return;
        }
        //Recorremos menu principal
        for (CuentaContable cuenta : items) {

            TreeNode hoja = new DefaultTreeNode(cuenta, raiz);
            hoja.setExpanded(true);
            List<CuentaContable> subItems = cuentaContableRN.getCuentasByCuentaMadre(cuenta);
            generarArbol(hoja, subItems);
        }
    }

    public void onNodeSelect(NodeSelectEvent event) {

        nodoSeleccionado = event.getTreeNode();
        nodoSeleccionado.setExpanded(true);
        cuentaContable = (CuentaContable) event.getTreeNode().getData();
        esNuevo = false;
    }

    public void nuevo() {

        try {

            cuentaContable = cuentaContableRN.nuevo(null);
            esNuevo = true;
            buscaMovimiento = false;
            TreeNode hoja = new DefaultTreeNode(cuentaContable, nodoSeleccionado);
        } catch (Exception ex) {
            Logger.getLogger(PlanCuentaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nuevo(CuentaContable cuentaMadre) {

        if (cuentaMadre.getImputable().equals("S")) {
            JsfUtil.addErrorMessage("La cuenta principal de la nueva cuenta contable, no puede ser una cuenta imputable");
            return;
        }

        String codigo = cuentaContableRN.obtenerSiguienteCogido(cuentaMadre);

        cuentaContable = new CuentaContable();
        cuentaContable.setNrocta(codigo);
        cuentaContable.setNivel(cuentaMadre.getNivel() + 1);
        cuentaContable.setCuentaContable(cuentaMadre);
        esNuevo = true;
        TreeNode hoja = new DefaultTreeNode(cuentaContable, nodoSeleccionado);
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

    public void onSelect(SelectEvent event) {
        cuentaContable = (CuentaContable) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void seleccionar(CuentaContable d) {

        if (d == null) {
            return;
        }

        cuentaContable = d;
        esNuevo = false;
        buscaMovimiento = false;
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

    public void imprimir() {

        if (cuentaContable == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void asignarNumeroCuenta() {

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

    public TreeNode getArbol() {
        return arbol;
    }

    public void setArbol(TreeNode arbol) {
        this.arbol = arbol;
    }

    public TreeNode getNodoSeleccionado() {
        return nodoSeleccionado;
    }

    public void setNodoSeleccionado(TreeNode nodoSeleccionado) {
        this.nodoSeleccionado = nodoSeleccionado;
    }

}
