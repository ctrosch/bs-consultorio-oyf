/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.web;

import bs.global.modelo.Moneda;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.stock.modelo.Producto;
import bs.stock.web.ProductoBean;
import bs.ventas.modelo.ItemListaPrecioVenta;
import bs.ventas.modelo.ListaPrecioVenta;
import bs.ventas.rn.ListaPrecioVentaRN;
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
public class ListaPrecioVentaBean extends GenericBean implements Serializable {

    private ListaPrecioVenta listaDePrecio;
    private List<ListaPrecioVenta> lista;
    private String codigo;

    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;

    private ItemListaPrecioVenta itemListaPrecio;

    protected @EJB
    ListaPrecioVentaRN listaPrecioRN;

    //Atributos para filtro
    private Moneda monedaRegistracion;
    private String priorizaMonedaProducto;

    /**
     * Creates a new instance of ListaPrecioVentaBean
     */
    public ListaPrecioVentaBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo == null || codigo.isEmpty()) {
                    buscar();
                } else {
                    seleccionar(listaPrecioRN.getListaPrecio(codigo));
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
        listaDePrecio = new ListaPrecioVenta();
        nuevoItemListaPrecio();
    }

    public void nuevoItemListaPrecio() {

        itemListaPrecio = new ItemListaPrecioVenta();
        itemListaPrecio.setCodlis(listaDePrecio.getCodigo());
        itemListaPrecio.setListaDePrecio(listaDePrecio);
    }

    public void guardar(boolean nuevo) {

        try {
            if (listaDePrecio != null) {

                if (esNuevo) {
                    listaDePrecio.setMonedaRegistracion(listaDePrecio.getMoneda());
                }

                listaPrecioRN.guardar(listaDePrecio, esNuevo);
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

    public void habilitaDeshabilita(String habDes) {

        try {
            listaDePrecio.getAuditoria().setDebaja(habDes);
            listaPrecioRN.guardar(listaDePrecio, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            listaPrecioRN.eliminar(listaDePrecio);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        lista = listaPrecioRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (monedaRegistracion != null) {

            filtro.put("monedaRegistracion.codigo = ", "'" + monedaRegistracion.getCodigo() + "'");
        }

        if (priorizaMonedaProducto != null && !priorizaMonedaProducto.isEmpty()) {

            filtro.put("priorizaMonedaProducto = ", "'" + priorizaMonedaProducto + "'");
        }
    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        monedaRegistracion = null;
        priorizaMonedaProducto = "";

        buscar();

    }

    public List<ListaPrecioVenta> complete(String query) {
        try {

            lista = listaPrecioRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<ListaPrecioVenta>();
        }
    }

    public void onSelect(SelectEvent event) {

        listaDePrecio = (ListaPrecioVenta) event.getObject();
        esNuevo = false;
        modoVista = "D";
        nuevoItemListaPrecio();
    }

    public void seleccionar(ListaPrecioVenta l) {

        if (l == null) {
            return;
        }

        listaDePrecio = l;
        esNuevo = false;
        modoVista = "D";
        nuevoItemListaPrecio();
    }

    public void seleccionarItemListaPrecio(ItemListaPrecioVenta i) {
        itemListaPrecio = i;
    }

    public void agregarItem() {
        try {
            if (!puedoAgregarItem(itemListaPrecio)) {
                return;
            }

            itemListaPrecio.setCodlis(listaDePrecio.getCodigo());
            itemListaPrecio.setListaDePrecio(listaDePrecio);

            listaPrecioRN.guardar(itemListaPrecio);
//            listaDePrecio.getItemPrecios().add(itemListaPrecio);
            nuevoItemListaPrecio();
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage(ex.getMessage());
        }
    }

    public void eliminarItem(ItemListaPrecioVenta item) {

        try {

            if (item == null) {
                JsfUtil.addErrorMessage("Seleecione un item a borrar");
            }

            listaPrecioRN.eliminarItemListaPrecio(item);
//            listaDePrecio.getItemPrecios().remove(item);
            nuevoItemListaPrecio();

            JsfUtil.addWarningMessage("El fue eliminado correctamente");

        } catch (Exception ex) {

            ex.printStackTrace();
            JsfUtil.addErrorMessage("No es posible eliminar item: " + ex);
        }
    }

    public boolean puedoAgregarItem(ItemListaPrecioVenta item) {

        if (listaDePrecio == null) {
            JsfUtil.addErrorMessage("Seleccione una lista de precios");
            return false;
        }

        if (item.getProducto() == null) {
            JsfUtil.addErrorMessage("Seleccione un producto para asignarle el precio");
            return false;
        }

        if (item.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            JsfUtil.addErrorMessage("Ingrese el precio mayor a cero");
            return false;
        }

        if (listaPrecioRN.getItemsPrecioByProducto(listaDePrecio.getCodigo(), item.getProducto().getCodigo()) != null) {
            JsfUtil.addErrorMessage("El producto " + item.getProducto().getDescripcion() + " ya existe en la lista de precios");
            return false;
        }

        return true;
    }

    public void procesarProducto() {

        if (listaDePrecio == null) {
            JsfUtil.addWarningMessage("Seleccione una lista de precios");
            return;
        }

        if (productoBean.getProducto() != null && itemListaPrecio != null) {

            Producto p = productoBean.getProducto();

            itemListaPrecio.setArtcod(p.getCodigo());
            itemListaPrecio.setProducto(p);
        }
    }

    public void imprimir() {

        if (listaDePrecio == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public ListaPrecioVenta getListaDePrecio() {
        return listaDePrecio;
    }

    public void setListaDePrecio(ListaPrecioVenta listaDePrecio) {
        this.listaDePrecio = listaDePrecio;
    }

    public List<ListaPrecioVenta> getLista() {
        return lista;
    }

    public void setLista(List<ListaPrecioVenta> lista) {
        this.lista = lista;
    }

    public ItemListaPrecioVenta getItemListaPrecio() {
        return itemListaPrecio;
    }

    public void setItemListaPrecio(ItemListaPrecioVenta itemListaPrecio) {
        this.itemListaPrecio = itemListaPrecio;
    }

    public ProductoBean getProductoBean() {
        return productoBean;
    }

    public void setProductoBean(ProductoBean productoBean) {
        this.productoBean = productoBean;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    public String getPriorizaMonedaProducto() {
        return priorizaMonedaProducto;
    }

    public void setPriorizaMonedaProducto(String priorizaMonedaProducto) {
        this.priorizaMonedaProducto = priorizaMonedaProducto;
    }

}
