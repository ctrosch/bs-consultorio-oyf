/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.rn;

import bs.administracion.rn.ParametrosRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.proveedores.dao.ListaCostoDAO;
import bs.proveedores.modelo.ItemListaPrecioCosto;
import bs.proveedores.modelo.ListaCosto;
import bs.stock.modelo.Producto;
import bs.ventas.modelo.ItemListaPrecioVenta;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class ListaCostoRN implements Serializable {

    @EJB
    private ListaCostoDAO listaCostoDAO;
    @EJB
    protected ParametrosRN parametrosRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(ListaCosto l, boolean esNuevo) throws ExcepcionGeneralSistema {
        if (esNuevo) {
            if (listaCostoDAO.getObjeto(ListaCosto.class, l.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código " + l.getCodigo());
            }
            listaCostoDAO.crear(l);
        } else {
            l = (ListaCosto) listaCostoDAO.editar(l);
        }
    }

    public ListaCosto getListaCosto(String value) {
        return listaCostoDAO.getObjeto(ListaCosto.class, value);
    }

    public List<ItemListaPrecioCosto> getItemsPrecioByLista(String codigo) {

        Map<String, String> filtro = new LinkedHashMap<String, String>();
        filtro.put("auditoria.debaja =", "'N'");
        filtro.put("codlis = ", "'" + codigo + "'");

        return listaCostoDAO.getItemsPrecioByFiltro(filtro);
    }

    public BigDecimal getPrecioByProducto(ListaCosto listaCosto, Producto producto, BigDecimal cotizacion) {

        if (listaCosto == null || producto == null) {
            return BigDecimal.ZERO;
        }

        if (cotizacion == null) {
            cotizacion = BigDecimal.ONE;
        }

        if (listaCosto.getPriorizaMonedaProducto().equals("S")
                && producto.getMonedaReposicion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {

            cotizacion = BigDecimal.ONE;
        }

        if (listaCosto.getTomaPrecioReposicion().equals("S") && producto.getPrecioReposicion() != null) {

            return producto.getPrecioReposicion().multiply(cotizacion).setScale(4, RoundingMode.HALF_UP);
        }

        ItemListaPrecioCosto itemPrecio = listaCostoDAO.getItemsPrecioByProducto(listaCosto.getCodigo(), producto.getCodigo());

        if (itemPrecio == null) {

            if (producto.getPrecioReposicion() != null) {

                return producto.getPrecioReposicion().multiply(cotizacion).setScale(4, RoundingMode.HALF_UP);

            }

            return BigDecimal.ZERO;
        }

        if (itemPrecio.getMonedaRegistracion().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
            cotizacion = BigDecimal.ONE;
        }

        return itemPrecio.getPrecio().multiply(cotizacion).setScale(4, RoundingMode.HALF_UP);
    }

    public ItemListaPrecioCosto getItemsPrecioByProducto(String codLis, String codigo) {

        return listaCostoDAO.getItemsPrecioByProducto(codLis, codigo);
    }

    public List<ListaCosto> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja) {

        return listaCostoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDeBaja, 0);
    }

    public List<ListaCosto> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {

        return listaCostoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDeBaja, cantMax);
    }

    public void eliminarItemLista(ItemListaPrecioCosto itemListaCosto) {
        listaCostoDAO.eliminar(ItemListaPrecioVenta.class, itemListaCosto.getId());
    }

    public void guardar(ItemListaPrecioCosto itemListaCosto) {

        listaCostoDAO.crear(itemListaCosto);
    }

    public void eliminar(ListaCosto listaCosto) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
