/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.stock.modelo.ComposicionFormula;
import bs.stock.modelo.Formula;
import bs.stock.modelo.ItemComposicionFormulaComponente;
import bs.stock.modelo.ItemComposicionFormulaProceso;
import bs.stock.modelo.Producto;
import bs.stock.rn.ComposicionFormulaRN;
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
public class ComposicionFormulaBean extends GenericBean implements Serializable {

    private ComposicionFormula composicionFormula;

    private List<ComposicionFormula> lista;

    @EJB
    private ComposicionFormulaRN composicionFormulaRN;

    // Variables para filtros
    private Producto producto;
    private Formula formula;

    public ComposicionFormulaBean() {

    }

    @PostConstruct
    public void init() {
        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

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
        composicionFormula = composicionFormulaRN.nuevo();

    }

    public void guardar(boolean nuevo) {

        try {
            if (composicionFormula != null) {

                composicionFormula = composicionFormulaRN.guardar(composicionFormula, esNuevo);
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
            composicionFormula.getAuditoria().setDebaja(habDes);
            composicionFormulaRN.guardar(composicionFormula, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            composicionFormulaRN.eliminar(composicionFormula);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        //String artcod = "";
        //String codfor = "";
//        if (composicionFormula.getProducto() != null) {
//            artcod = composicionFormula.getProducto().getCodigo();
//        }
//        if (composicionFormula.getFormula() != null) {
//            codfor = composicionFormula.getFormula().getCodigo();
//        }
        lista = composicionFormulaRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (formula != null) {
            filtro.put("formula.codigo = ", "'" + formula.getCodigo() + "'");
        }

        if (producto != null) {
            filtro.put("producto.codigo=", "'" + producto.getCodigo() + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        formula = null;
        producto = null;

        buscar();

    }

    public List<ComposicionFormula> complete(String query) {
        try {

            //String artcod = "";
            // String codfor = "";
//            if (composicionFormula.getProducto() != null) {
//                artcod = composicionFormula.getProducto().getCodigo();
//            }
//            if (composicionFormula.getFormula() != null) {
//                codfor = composicionFormula.getFormula().getCodigo();
//            }
            lista = composicionFormulaRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<ComposicionFormula>();
        }
    }

    public void onSelect(SelectEvent event) {
        composicionFormula = (ComposicionFormula) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(ComposicionFormula d) {

        if (d == null) {
            return;
        }

        composicionFormula = d;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (composicionFormula == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void procesarProducto() {

        if (composicionFormula.getProducto() != null) {
            composicionFormula.setArtcod(composicionFormula.getProducto().getCodigo());
        }
    }

    public void procesarFormula() {

        if (composicionFormula.getFormula() != null) {
            composicionFormula.setCodfor(composicionFormula.getFormula().getCodigo());
        }
    }

    public void nuevoItemComponente() {

        try {
            composicionFormulaRN.nuevoItemComponente(composicionFormula);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item componente: " + ex);
        }
    }

    public void nuevoItemProceso() {

        try {
            composicionFormulaRN.nuevoItemProceso(composicionFormula);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item proceso: " + ex);
        }
    }

    public void eliminarItemComponente(ItemComposicionFormulaComponente item) {

        try {
            composicionFormulaRN.eliminarItemComponente(composicionFormula, item);
            JsfUtil.addWarningMessage("Se ha borrado el item componente " + (item.getProductoComponente() != null ? item.getProductoComponente().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (item.getProductoComponente() != null ? item.getProductoComponente().getDescripcion() : "") + " " + ex);
        }
    }

    public void eliminarItemProceso(ItemComposicionFormulaProceso item) {

        try {
            composicionFormulaRN.eliminarItemProceso(composicionFormula, item);
            JsfUtil.addWarningMessage("Se ha borrado el item proceso " + (item.getProductoComponente() != null ? item.getProductoComponente().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (item.getProductoComponente() != null ? item.getProductoComponente().getDescripcion() : "") + " " + ex);
        }
    }

    //-----------------------------------------------------------------------------------
    public ComposicionFormula getComposicionFormula() {
        return composicionFormula;
    }

    public void setComposicionFormula(ComposicionFormula composicionFormula) {
        this.composicionFormula = composicionFormula;
    }

    public List<ComposicionFormula> getLista() {
        return lista;
    }

    public void setLista(List<ComposicionFormula> lista) {
        this.lista = lista;
    }

    public Formula getFormula() {
        return formula;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

}
