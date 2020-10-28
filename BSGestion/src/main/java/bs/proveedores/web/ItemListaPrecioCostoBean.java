/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.proveedores.modelo.ItemListaPrecioCosto;
import bs.proveedores.modelo.ListaCosto;
import bs.proveedores.rn.ItemListaPrecioCostoRN;
import bs.stock.modelo.Producto;
import bs.stock.web.ProductoBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class ItemListaPrecioCostoBean extends GenericBean implements Serializable {

    private ItemListaPrecioCosto itemListaPrecio;
    private List<ItemListaPrecioCosto> lista;

    private ListaCosto listaCosto;

    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;

    @ManagedProperty(value = "#{listaPrecioCostoBean}")
    protected ListaPrecioCostoBean listaPrecioCostoBean;

    protected boolean soloVigentes;

    protected @EJB
    ItemListaPrecioCostoRN itemListaPrecioCostoRN;

    // Variables para filtros
    ///
    /**
     * Creates a new instance of ListaPrecioVentaBean
     */
    public ItemListaPrecioCostoBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                soloVigentes = false;
                itemListaPrecio = new ItemListaPrecioCosto();

                buscar();

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        esNuevo = true;
        modoVista = "D";
        itemListaPrecio = new ItemListaPrecioCosto();
    }

    public void guardar(boolean nuevo) {

        try {
            if (itemListaPrecio != null) {

                if (!puedoGuardar()) {
                    return;
                }

                if (esNuevo) {
                    itemListaPrecio.setArtcod(itemListaPrecio.getProducto().getCodigo());
                    itemListaPrecio.setCodlis(itemListaPrecio.getListaCosto().getCodigo());
                }

                itemListaPrecioCostoRN.guardar(itemListaPrecio, esNuevo);
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
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public boolean puedoGuardar() {

        if (itemListaPrecio.getProducto() == null) {
            JsfUtil.addErrorMessage("No es posible guardar, debe seleccionar un producto");
            return false;
        }

        if (itemListaPrecio.getListaCosto() == null) {
            JsfUtil.addErrorMessage("No es posible guardar, debe seleccionar una lista de costo");
            return false;
        }

        if (itemListaPrecio.getPrecio() == null) {
            JsfUtil.addErrorMessage("No es posible guardar, el precio no puede ser nulo");
            return false;
        }

        if (itemListaPrecio.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            JsfUtil.addErrorMessage("No es posible guardar, el precio debe ser mayor a cero");
            return false;
        }

        return true;
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            itemListaPrecio.getAuditoria().setDebaja(habDes);
            itemListaPrecioCostoRN.guardar(itemListaPrecio, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            itemListaPrecioCostoRN.eliminar(itemListaPrecio);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = itemListaPrecioCostoRN.getListaByBusqueda(listaCosto, filtro, txtBusqueda, mostrarDebaja, soloVigentes, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        listaCosto = null;
        buscar();

    }

    public List<ItemListaPrecioCosto> complete(String query) {
        try {
            lista = itemListaPrecioCostoRN.getListaByBusqueda(null, null, query, mostrarDebaja, false, 15);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<ItemListaPrecioCosto>();
        }
    }

    public void onSelect(SelectEvent event) {

        itemListaPrecio = (ItemListaPrecioCosto) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(ItemListaPrecioCosto i) {

        if (i == null) {
            return;
        }

        itemListaPrecio = i;
        esNuevo = false;
        modoVista = "D";
    }

    public void procesarProducto() {

        if (itemListaPrecio == null) {
            JsfUtil.addWarningMessage("Item de precio vacío");
            return;
        }

        if (itemListaPrecio.getListaCosto() == null) {
            JsfUtil.addWarningMessage("Por favor, primero seleccione la lista de costo");
            return;
        }

        if (productoBean.getProducto() != null && itemListaPrecio != null) {

            Producto p = productoBean.getProducto();
            itemListaPrecio.setArtcod(p.getCodigo());
            itemListaPrecio.setProducto(p);

            if (itemListaPrecio.getListaCosto().getPriorizaMonedaProducto().equals("S")) {
                itemListaPrecio.setMonedaRegistracion(p.getMonedaReposicion().getCodigo());
            } else {
                itemListaPrecio.setMonedaRegistracion(itemListaPrecio.getListaCosto().getMoneda().getCodigo());
            }
        }

    }

    public void imprimir() {

        if (itemListaPrecio == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public ItemListaPrecioCosto getItemListaPrecio() {
        return itemListaPrecio;
    }

    public void setItemListaPrecio(ItemListaPrecioCosto itemListaPrecio) {
        this.itemListaPrecio = itemListaPrecio;
    }

    public List<ItemListaPrecioCosto> getLista() {
        return lista;
    }

    public void setLista(List<ItemListaPrecioCosto> lista) {
        this.lista = lista;
    }

    public ItemListaPrecioCostoRN getItemListaPrecioCostoRN() {
        return itemListaPrecioCostoRN;
    }

    public void setItemListaPrecioCostoRN(ItemListaPrecioCostoRN itemListaPrecioCostoRN) {
        this.itemListaPrecioCostoRN = itemListaPrecioCostoRN;
    }

    public ProductoBean getProductoBean() {
        return productoBean;
    }

    public void setProductoBean(ProductoBean productoBean) {
        this.productoBean = productoBean;
    }

    public boolean isSoloVigentes() {
        return soloVigentes;
    }

    public void setSoloVigentes(boolean soloVigentes) {
        this.soloVigentes = soloVigentes;
    }

    public ListaCosto getListaCosto() {
        return listaCosto;
    }

    public void setListaCosto(ListaCosto listaCosto) {
        this.listaCosto = listaCosto;
    }

    public ListaPrecioCostoBean getListaPrecioCostoBean() {
        return listaPrecioCostoBean;
    }

    public void setListaPrecioCostoBean(ListaPrecioCostoBean listaPrecioCostoBean) {
        this.listaPrecioCostoBean = listaPrecioCostoBean;
    }

}
