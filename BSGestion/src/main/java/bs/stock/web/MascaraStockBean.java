/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.stock.modelo.ItemMascaraStock;
import bs.stock.modelo.MascaraStock;
import bs.stock.rn.MascaraStockRN;
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
public class MascaraStockBean extends GenericBean implements Serializable {

    private MascaraStock mascara;
    private List<MascaraStock> lista;
    private String codigo;

    @EJB
    private MascaraStockRN mascaraStockRN;

    // Variables para filtros
    //
    //
    public MascaraStockBean() {

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
                    seleccionar(mascaraStockRN.getMascaraStock(codigo));
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
        mascara = new MascaraStock();
    }

    public void guardar(boolean nuevo) {

        try {
            if (mascara != null) {

                mascara = mascaraStockRN.guardar(mascara, esNuevo);
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
            mascara.getAuditoria().setDebaja(habDes);
            mascara = mascaraStockRN.guardar(mascara, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            mascaraStockRN.eliminar(mascara);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = mascaraStockRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<MascaraStock> complete(String query) {
        try {
            lista = mascaraStockRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<MascaraStock>();
        }
    }

    public void onSelect(SelectEvent event) {
        mascara = (MascaraStock) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(MascaraStock e) {

        if (e == null) {
            return;
        }

        mascara = e;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (mascara == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void nuevoItem() {

        try {
            mascaraStockRN.nuevoItem(mascara);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item equivalencia proveedor: " + ex);
        }
    }

    public void eliminarItem(ItemMascaraStock item) {

        try {
            mascaraStockRN.eliminarItem(mascara, item);
            JsfUtil.addWarningMessage("Se ha borrado el item " + (item.getProducto() != null ? item.getProducto().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (item.getProducto() != null ? item.getProducto().getDescripcion() : "") + " " + ex);
        }
    }

    public MascaraStock getMascara() {
        return mascara;
    }

    public void setMascara(MascaraStock mascara) {
        this.mascara = mascara;
    }

    public List<MascaraStock> getLista() {
        return lista;
    }

    public void setLista(List<MascaraStock> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
