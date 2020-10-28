/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.web.informe;

import bs.entidad.modelo.EntidadComercial;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.stock.modelo.Producto;
import bs.stock.modelo.Rubro01;
import bs.stock.modelo.Rubro02;
import bs.stock.modelo.Rubro03;
import bs.stock.modelo.Stock;
import bs.stock.modelo.TipoProducto;
import bs.stock.rn.ProductoRN;
import bs.stock.rn.StockRN;
import bs.stock.rn.TipoProductoRN;
import bs.ventas.modelo.ItemListaPrecioVenta;
import bs.ventas.rn.ItemListaPrecioVentaRN;
import java.io.Serializable;
import java.util.List;
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
public class ConsultaProducto extends GenericBean implements Serializable {

    @EJB
    private ProductoRN productoRN;
    @EJB
    private TipoProductoRN tipoProductoRN;
    @EJB
    private StockRN stockRN;
    @EJB
    private ItemListaPrecioVentaRN itemListaPrecioVentaRN;

    private Producto producto;
    private List<Producto> lista;

    //Filtros
    private TipoProducto tipoProducto;
    public Producto productoDesde;
    public Producto productoHasta;
    private Rubro01 rubro01;
    private Rubro02 rubro02;
    private Rubro03 rubro03;
    private EntidadComercial proveedorHabitual;

    // para consulta completa de productos
    private List<ItemListaPrecioVenta> precios;
    private List<Stock> stock;

    /**
     * Creates a new instance of ProductoBean
     */
    public ConsultaProducto() {

    }

    @PostConstruct
    public void init() {

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

    public void setearTipo(String codTipo) {

        if (codTipo != null) {
            tipoProducto = tipoProductoRN.getTipoProducto(codTipo);
        }
    }

    public void buscarBytipo() {

        limpiarFiltro();
        cargarFiltro();

        String codTipo = "";

        if (tipoProducto != null) {
            codTipo = tipoProducto.getCodigo();
        }

        lista = productoRN.getListaByBusqueda(codTipo, filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public void buscar() {

        cargarFiltro();
        producto = null;

        String codTipo = "";

        if (tipoProducto != null) {
            codTipo = tipoProducto.getCodigo();
        }

        lista = productoRN.getListaByBusqueda(codTipo, filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public void onSelect(SelectEvent event) {

        producto = (Producto) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void onSelectCompleto(SelectEvent event) {

        producto = (Producto) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;

        stock = stockRN.getStockByProducto(producto, true);
        precios = itemListaPrecioVentaRN.getPreciosByProducto(producto);
    }

    public void seleccionar(Producto e) {

        if (e == null) {
            return;
        }

        producto = e;
        esNuevo = false;
        buscaMovimiento = false;

    }

    public void seleccionarCompleto(Producto e) {

        producto = e;
        esNuevo = false;
        buscaMovimiento = false;
        stock = stockRN.getStockByProducto(producto, true);
        precios = itemListaPrecioVentaRN.getPreciosByProducto(producto);
    }

    public boolean validarParametros() {

        if (formulario == null) {
            JsfUtil.addWarningMessage("Seleccione un formulario");
            return false;
        }

        if (fechaMovimientoDesde != null && fechaMovimientoHasta != null) {
            if (fechaMovimientoHasta.before(fechaMovimientoDesde)) {
                JsfUtil.addWarningMessage("La fecha de desde, no puede ser mayor a la fecha hasta.");
                return false;
            }
        }

        if (numeroFormularioDesde != null && numeroFormularioHasta != null) {
            if (numeroFormularioDesde > numeroFormularioHasta) {
                JsfUtil.addWarningMessage("Número de formulario desde es mayor al número de formulario hasta");
                return false;
            }
        }
        return true;
    }

    public void cargarFiltro() {

        filtro.clear();

        if (productoDesde != null) {
            filtro.put("codigo >=", "'" + productoDesde.getCodigo() + "'");
        }

        if (productoHasta != null) {
            filtro.put("codigo <=", "'" + productoHasta.getCodigo() + "'");
        }

        if (rubro01 != null) {
            filtro.put("rubr01.codigo >=", "'" + rubro01.getCodigo() + "'");
        }

        if (rubro02 != null) {
            filtro.put("rubr02.codigo >=", "'" + rubro02.getCodigo() + "'");
        }

        if (rubro03 != null) {
            filtro.put("rubr03.codigo <=", "'" + rubro03.getCodigo() + "'");
        }

        if (proveedorHabitual != null) {
            filtro.put("proveedorHabitual.nrocta =", "'" + proveedorHabitual.getNrocta() + "'");
        }
    }

    public void limpiarFiltro() {

        producto = null;
        productoDesde = null;
        productoHasta = null;
        rubro01 = null;
        rubro02 = null;
        rubro03 = null;
        proveedorHabitual = null;

    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public List<Producto> getLista() {
        return lista;
    }

    public void setLista(List<Producto> lista) {
        this.lista = lista;
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

    public Producto getProductoDesde() {
        return productoDesde;
    }

    public void setProductoDesde(Producto productoDesde) {
        this.productoDesde = productoDesde;
    }

    public Producto getProductoHasta() {
        return productoHasta;
    }

    public void setProductoHasta(Producto productoHasta) {
        this.productoHasta = productoHasta;
    }

    public Rubro01 getRubro01() {
        return rubro01;
    }

    public void setRubro01(Rubro01 rubro01) {
        this.rubro01 = rubro01;
    }

    public Rubro02 getRubro02() {
        return rubro02;
    }

    public void setRubro02(Rubro02 rubro02) {
        this.rubro02 = rubro02;
    }

    public Rubro03 getRubro03() {
        return rubro03;
    }

    public void setRubro03(Rubro03 rubro03) {
        this.rubro03 = rubro03;
    }

    public EntidadComercial getProveedorHabitual() {
        return proveedorHabitual;
    }

    public void setProveedorHabitual(EntidadComercial proveedorHabitual) {
        this.proveedorHabitual = proveedorHabitual;
    }

}
