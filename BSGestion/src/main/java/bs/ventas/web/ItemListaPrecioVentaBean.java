/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.stock.modelo.Producto;
import bs.stock.web.ProductoBean;
import bs.ventas.modelo.ItemListaPrecioVenta;
import bs.ventas.modelo.ListaPrecioVenta;
import bs.ventas.rn.ItemListaPrecioVentaRN;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class ItemListaPrecioVentaBean extends GenericBean implements Serializable {

    private ItemListaPrecioVenta itemListaPrecio;
    private List<ItemListaPrecioVenta> lista;
    private Integer id;

    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;

    protected boolean soloVigentes;
    protected ListaPrecioVenta listaPrecio;

    protected @EJB
    ItemListaPrecioVentaRN itemListaPrecioRN;

    // Variables para filtros
    /**
     * Creates a new instance of ListaPrecioVentaBean
     */
    public ItemListaPrecioVentaBean() {

    }

    @PostConstruct
    public void init() {

        txtBusqueda = "";
        mostrarDebaja = false;
        soloVigentes = false;
        itemListaPrecio = new ItemListaPrecioVenta();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                if (id != null && id > 0) {
                    seleccionar(itemListaPrecioRN.getItemListaPrecioVenta(id));
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

        esNuevo = true;
        modoVista = "D";
        itemListaPrecio = new ItemListaPrecioVenta();
    }

    public void guardar(boolean nuevo) {

        try {
            if (itemListaPrecio != null) {

                if (!puedoGuardar()) {
                    return;
                }

                if (esNuevo) {
                    itemListaPrecio.setArtcod(itemListaPrecio.getProducto().getCodigo());
                    itemListaPrecio.setCodlis(itemListaPrecio.getListaDePrecio().getCodigo());
                }

                itemListaPrecioRN.guardar(itemListaPrecio, esNuevo);
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

        if (itemListaPrecio.getListaDePrecio() == null) {
            JsfUtil.addErrorMessage("No es posible guardar, debe seleccionar una lista de precios");
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
            itemListaPrecioRN.guardar(itemListaPrecio, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            itemListaPrecioRN.eliminar(itemListaPrecio);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        lista = itemListaPrecioRN.getListaByBusqueda(listaPrecio, filtro, txtBusqueda, mostrarDebaja, soloVigentes, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (listaPrecio != null) {

            filtro.put("listaPrecio.codigo = ", "'" + listaPrecio.getCodigo() + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        listaPrecio = null;

        buscar();

    }

    public List<ItemListaPrecioVenta> complete(String query) {
        try {

            lista = itemListaPrecioRN.getListaByBusqueda(listaPrecio, null, query, mostrarDebaja, soloVigentes, cantidadRegistros);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<ItemListaPrecioVenta>();
        }
    }

    public void onSelect(SelectEvent event) {

        itemListaPrecio = (ItemListaPrecioVenta) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(ItemListaPrecioVenta i) {

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

        if (itemListaPrecio.getListaDePrecio() == null) {
            JsfUtil.addWarningMessage("Por favor, primero seleccione la lista de costo");
            return;
        }

        if (productoBean.getProducto() != null && itemListaPrecio != null) {

            Producto p = productoBean.getProducto();

            itemListaPrecio.setArtcod(p.getCodigo());
            itemListaPrecio.setProducto(p);

            if (itemListaPrecio.getListaDePrecio().getPriorizaMonedaProducto().equals("S")) {
                itemListaPrecio.setMonedaRegistracion(p.getMonedaReposicion().getCodigo());
            } else {
                itemListaPrecio.setMonedaRegistracion(itemListaPrecio.getListaDePrecio().getMoneda().getCodigo());
            }
        }
    }

    public void imprimir() {

        if (itemListaPrecio == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public ItemListaPrecioVenta getItemListaPrecio() {
        return itemListaPrecio;
    }

    public void setItemListaPrecio(ItemListaPrecioVenta itemListaPrecio) {
        this.itemListaPrecio = itemListaPrecio;
    }

    public List<ItemListaPrecioVenta> getLista() {
        return lista;
    }

    public void setLista(List<ItemListaPrecioVenta> lista) {
        this.lista = lista;
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

    public ListaPrecioVenta getListaPrecio() {
        return listaPrecio;
    }

    public void setListaPrecio(ListaPrecioVenta listaPrecio) {
        this.listaPrecio = listaPrecio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
