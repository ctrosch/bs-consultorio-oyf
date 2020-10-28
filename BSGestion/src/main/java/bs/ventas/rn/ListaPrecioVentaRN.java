/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.rn;

import bs.administracion.rn.ParametrosRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.modelo.Producto;
import bs.ventas.dao.ListaPrecioVentaDAO;
import bs.ventas.modelo.ItemListaPrecioVenta;
import bs.ventas.modelo.ListaPrecioVenta;
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
public class ListaPrecioVentaRN implements Serializable {

    @EJB
    private ListaPrecioVentaDAO listaPrecioDAO;
    @EJB
    protected ParametrosRN parametrosRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(ListaPrecioVenta l, boolean esNuevo) throws ExcepcionGeneralSistema {
        if (esNuevo) {
            if (listaPrecioDAO.getObjeto(ListaPrecioVenta.class, l.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código " + l.getCodigo());
            }
            listaPrecioDAO.crear(l);
        } else {
            listaPrecioDAO.editar(l);
        }
    }

    public ListaPrecioVenta getListaPrecio(String value) {
        return listaPrecioDAO.getObjeto(ListaPrecioVenta.class, value);
    }

    public List<ItemListaPrecioVenta> getItemsPrecioByLista(String codigo) {

        Map<String, String> filtro = new LinkedHashMap<String, String>();
        filtro.put("auditoria.debaja =", "'N'");
        filtro.put("codlis = ", "'" + codigo + "'");

        return listaPrecioDAO.getItemsPrecioByFiltro(filtro);
    }

    public BigDecimal getPrecioByProducto(ListaPrecioVenta listaPrecio, Producto producto, BigDecimal cotizacion, BigDecimal utilidadAdicional) {

        if (listaPrecio == null || producto == null) {
            return BigDecimal.ZERO;
        }

        if (cotizacion == null) {
            cotizacion = BigDecimal.ONE;
        }

        ItemListaPrecioVenta itemPrecio = null;

        if (listaPrecio.getCalculaPrecioDesdeUtilidad().equals("N")) {
            itemPrecio = listaPrecioDAO.getItemsPrecioByProducto(listaPrecio.getCodigo(), producto.getCodigo());
        } else {

            BigDecimal utilidad = null;

            switch (listaPrecio.getUtilidadParaCalcularPrecio().intValue()) {

                case 1:
                    utilidad = producto.getUtilidad();
                    break;

                case 2:
                    utilidad = producto.getUtilidad2();
                    break;

                case 3:
                    utilidad = producto.getUtilidad3();
                    break;
                case 4:
                    utilidad = producto.getUtilidad4();
                    break;

                case 5:
                    utilidad = producto.getUtilidad5();
                    break;

                default:
                    utilidad = BigDecimal.ZERO;
            }

            if (utilidad == null || utilidad.compareTo(BigDecimal.ZERO) <= 0) {
                return BigDecimal.ZERO;
            }

            if (producto.getPrecioReposicion() == null || producto.getPrecioReposicion().compareTo(BigDecimal.ZERO) <= 0) {
                return BigDecimal.ZERO;
            }

//            System.err.println("Precio de Costo " + producto.getPrecioReposicion());
//            System.err.println("Utilidad " + utilidad);
//            System.err.println("utilidadAdicional " + utilidadAdicional);
            BigDecimal nuevoPrecio = producto.getPrecioReposicion().add(producto.getPrecioReposicion().multiply(utilidad).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));

            if (utilidadAdicional != null && utilidadAdicional.compareTo(BigDecimal.ZERO) > 0) {

                nuevoPrecio = nuevoPrecio.add(nuevoPrecio.multiply(utilidadAdicional).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            }

            itemPrecio = new ItemListaPrecioVenta();
            itemPrecio.setPrecio(nuevoPrecio);
            itemPrecio.setMonedaRegistracion(producto.getMonedaReposicion().getCodigo());
        }

        if (itemPrecio == null) {
            return BigDecimal.ZERO;
        }

        if (itemPrecio.getMonedaRegistracion().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
            cotizacion = BigDecimal.ONE;
        }

        return itemPrecio.getPrecio().multiply(cotizacion).setScale(2, RoundingMode.HALF_UP);
    }

    public ItemListaPrecioVenta getItemsPrecioByProducto(String codLis, String codigo) {

        return listaPrecioDAO.getItemsPrecioByProducto(codLis, codigo);
    }

    public List<ListaPrecioVenta> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja) {

        return listaPrecioDAO.getEntidadByBusqueda(null, txtBusqueda, mostrarDeBaja, 0);
    }

    public List<ListaPrecioVenta> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return listaPrecioDAO.getEntidadByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public void eliminarItemListaPrecio(ItemListaPrecioVenta itemListaPrecio) {
        listaPrecioDAO.eliminar(ItemListaPrecioVenta.class, itemListaPrecio.getId());
    }

    public void guardar(ItemListaPrecioVenta itemListaPrecio) {

        listaPrecioDAO.crear(itemListaPrecio);
    }

    public void eliminar(ListaPrecioVenta listaDePrecio) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
