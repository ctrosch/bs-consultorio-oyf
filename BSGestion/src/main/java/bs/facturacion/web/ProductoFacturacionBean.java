/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.web;

import bs.facturacion.rn.ProductoFacturacionRN;
import bs.facturacion.vista.ProductoFacturacion;
import bs.global.web.GenericBean;
import bs.stock.modelo.Stock;
import bs.stock.modelo.TipoProducto;
import bs.stock.rn.AplicacionProductoRN;
import bs.stock.rn.ProductoRN;
import bs.stock.rn.StockRN;
import bs.stock.rn.TipoProductoRN;
import bs.ventas.modelo.ItemListaPrecioVenta;
import bs.ventas.rn.ItemListaPrecioVentaRN;
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
public class ProductoFacturacionBean extends GenericBean implements Serializable {

    @EJB
    private ProductoFacturacionRN productoFacturacionRN;
    @EJB
    private ProductoRN productoRN;
    @EJB
    private TipoProductoRN tipoProductoRN;
    @EJB
    private AplicacionProductoRN aplicacionProductoRN;
    @EJB
    private StockRN stockRN;
    @EJB
    private ItemListaPrecioVentaRN itemListaPrecioVentaRN;

    private ProductoFacturacion producto;
    private List<ProductoFacturacion> lista;

    private List<TipoProducto> tipos;
    private TipoProducto tipoProducto;

    // para consulta completa de productos
    private List<ItemListaPrecioVenta> precios;
    private List<Stock> stock;

    /**
     * Creates a new instance of ProductoBean
     */
    public ProductoFacturacionBean() {

    }

    @PostConstruct
    public void init() {

        super.iniciar();
        tipos = tipoProductoRN.getLista(false);
        cantidadRegistros = 25;
        buscar();
    }

    public void setearTipo(String codTipo) {

        if (codTipo != null) {
            tipoProducto = tipoProductoRN.getTipoProducto(codTipo);
        }
    }

    public void buscar() {

        String codTipo = "";

        if (tipoProducto != null) {
            codTipo = tipoProducto.getCodigo();
        }

        lista = productoFacturacionRN.getListaByBusqueda(codTipo, txtBusqueda, cantidadRegistros);
    }

    public void onSelect(SelectEvent event) {

        producto = (ProductoFacturacion) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        sincronizarAplicacion();
    }

    public void onSelectCompleto(SelectEvent event) {

        producto = (ProductoFacturacion) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        sincronizarAplicacion();

        stock = stockRN.getStockByProducto(productoRN.getProducto(producto.getCodigo()), true);
        precios = itemListaPrecioVentaRN.getPreciosByProducto(productoRN.getProducto(producto.getCodigo()));
    }

    public void seleccionar(ProductoFacturacion e) {

        if (e == null) {
            return;
        }

        producto = e;
        esNuevo = false;
        buscaMovimiento = false;
        sincronizarAplicacion();
    }

    public void seleccionarCompleto(ProductoFacturacion e) {

        producto = e;
        esNuevo = false;
        buscaMovimiento = false;
        sincronizarAplicacion();
        stock = stockRN.getStockByProducto(productoRN.getProducto(producto.getCodigo()), true);
        precios = itemListaPrecioVentaRN.getPreciosByProducto(productoRN.getProducto(producto.getCodigo()));
    }

    public List<ProductoFacturacion> complete(String query) {
        try {
            String codTipo = "";

            if (tipoProducto != null) {
                codTipo = tipoProducto.getCodigo();
            }

            return productoFacturacionRN.getListaByBusqueda(codTipo, query, 10);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<ProductoFacturacion>();
        }
    }

    public void sincronizarAplicacion() {

        if (producto != null) {
            producto.setAplicaciones(aplicacionProductoRN.getListaByCodigoProducto(producto.getCodigo(), false));
        }
    }

    public List<ProductoFacturacion> getLista() {
        return lista;
    }

    public void setLista(List<ProductoFacturacion> lista) {
        this.lista = lista;
    }

    public ProductoFacturacion getProducto() {
        return producto;
    }

    public void setProducto(ProductoFacturacion producto) {
        this.producto = producto;
    }

    public List<TipoProducto> getTipos() {
        return tipos;
    }

    public void setTipos(List<TipoProducto> tipos) {
        this.tipos = tipos;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public List<ItemListaPrecioVenta> getPrecios() {
        return precios;
    }

    public void setPrecios(List<ItemListaPrecioVenta> precios) {
        this.precios = precios;
    }

    public List<Stock> getStock() {
        return stock;
    }

    public void setStock(List<Stock> stock) {
        this.stock = stock;
    }

}
