/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.web;

import bs.global.modelo.Moneda;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.proveedores.modelo.ItemListaPrecioCosto;
import bs.proveedores.modelo.ListaCosto;
import bs.proveedores.rn.ListaCostoRN;
import bs.stock.modelo.Producto;
import bs.stock.web.ProductoBean;
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
public class ListaPrecioCostoBean extends GenericBean implements Serializable {

    private ListaCosto listaCosto;
    private List<ListaCosto> lista;
    private String codigo;

    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;

    private ItemListaPrecioCosto itemListaCosto;

    protected @EJB
    ListaCostoRN listaCostoRN;

    // Variables para filtros
    private Moneda moneda;
    private String incluyeImpuestos;
    private String priorizaMonedaProducto;

    /**
     * Creates a new instance of ListaPrecioVentaBean
     */
    public ListaPrecioCostoBean() {

    }

    @PostConstruct
    public void init() {
        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                itemListaCosto = new ItemListaPrecioCosto();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(listaCostoRN.getListaCosto(codigo));
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
        listaCosto = new ListaCosto();
        nuevoItemListaCosto();
    }

    public void nuevoItemListaCosto() {

        itemListaCosto = new ItemListaPrecioCosto();
        itemListaCosto.setCodlis(listaCosto.getCodigo());
        itemListaCosto.setListaCosto(listaCosto);
    }

    public void guardar(boolean nuevo) {

        try {
            if (listaCosto != null) {

                if (esNuevo) {
                    listaCosto.setMonedaRegistracion(listaCosto.getMoneda());
                }

                listaCostoRN.guardar(listaCosto, esNuevo);
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
            listaCosto.getAuditoria().setDebaja(habDes);
            listaCostoRN.guardar(listaCosto, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            listaCostoRN.eliminar(listaCosto);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = listaCostoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";

    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (moneda != null) {
            filtro.put("moneda.codigo = ", "'" + moneda.getCodigo() + "'");
        }

        if (incluyeImpuestos != null && !incluyeImpuestos.isEmpty()) {

            filtro.put("incluyeImpuestos = ", "'" + incluyeImpuestos + "'");
        }

        if (priorizaMonedaProducto != null && !priorizaMonedaProducto.isEmpty()) {

            filtro.put("priorizaMonedaProducto = ", "'" + priorizaMonedaProducto + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        moneda = null;
        incluyeImpuestos = "";
        priorizaMonedaProducto = "";

        buscar();

    }

    public List<ListaCosto> complete(String query) {
        try {
            lista = listaCostoRN.getListaByBusqueda(null, query, mostrarDebaja, 10);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<ListaCosto>();
        }
    }

    public void onSelect(SelectEvent event) {

        listaCosto = (ListaCosto) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(ListaCosto l) {

        if (l == null) {
            return;
        }

        listaCosto = l;
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionarItemListaCosto(ItemListaPrecioCosto i) {
        itemListaCosto = i;
    }

    public void agregarItem() {
        try {
            if (!puedoAgregarItem(itemListaCosto)) {
                return;
            }

            itemListaCosto.setCodlis(listaCosto.getCodigo());
            itemListaCosto.setListaCosto(listaCosto);

            listaCostoRN.guardar(itemListaCosto);
            nuevoItemListaCosto();
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage(ex.getMessage());
        }
    }

    public void eliminarItem() {

        try {

            if (itemListaCosto == null) {
                JsfUtil.addErrorMessage("Seleecione un item a borrar");
            }

            listaCostoRN.eliminarItemLista(itemListaCosto);
            nuevoItemListaCosto();

            JsfUtil.addWarningMessage("El fue eliminado correctamente");

        } catch (Exception ex) {

            ex.printStackTrace();
            JsfUtil.addErrorMessage("No es posible eliminar item: " + ex);
        }
    }

    public boolean puedoAgregarItem(ItemListaPrecioCosto item) {

        if (listaCosto == null) {
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

        if (listaCostoRN.getItemsPrecioByProducto(listaCosto.getCodigo(), item.getProducto().getCodigo()) != null) {
            JsfUtil.addErrorMessage("El producto " + item.getProducto().getDescripcion() + " ya existe en la lista de precios");
            return false;
        }

        return true;
    }

    public void procesarProducto() {

        if (listaCosto == null) {
            JsfUtil.addWarningMessage("Seleccione una lista de precios");
            return;
        }

        if (productoBean.getProducto() != null && itemListaCosto != null) {

            Producto p = productoBean.getProducto();

            itemListaCosto.setArtcod(p.getCodigo());
            itemListaCosto.setProducto(p);
        }
    }

    public void imprimir() {

        if (listaCosto == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public ListaCosto getListaCosto() {
        return listaCosto;
    }

    public void setListaCosto(ListaCosto listaDePrecio) {
        this.listaCosto = listaDePrecio;
    }

    public List<ListaCosto> getLista() {
        return lista;
    }

    public void setLista(List<ListaCosto> lista) {
        this.lista = lista;
    }

    public ItemListaPrecioCosto getItemListaCosto() {
        return itemListaCosto;
    }

    public void setItemListaCosto(ItemListaPrecioCosto itemListaCosto) {
        this.itemListaCosto = itemListaCosto;
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

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public String getIncluyeImpuestos() {
        return incluyeImpuestos;
    }

    public void setIncluyeImpuestos(String incluyeImpuestos) {
        this.incluyeImpuestos = incluyeImpuestos;
    }

    public String getPriorizaMonedaProducto() {
        return priorizaMonedaProducto;
    }

    public void setPriorizaMonedaProducto(String priorizaMonedaProducto) {
        this.priorizaMonedaProducto = priorizaMonedaProducto;
    }

}
