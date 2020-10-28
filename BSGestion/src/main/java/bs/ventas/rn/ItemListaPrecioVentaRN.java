/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.modelo.Producto;
import bs.ventas.dao.ItemListaPrecioVentaDAO;
import bs.ventas.modelo.ItemListaPrecioVenta;
import bs.ventas.modelo.ListaPrecioVenta;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
public class ItemListaPrecioVentaRN implements Serializable {

    @EJB
    private ItemListaPrecioVentaDAO itemListaPrecioDAO;
    @EJB
    private ListaPrecioVentaRN listaPrecioRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(ItemListaPrecioVenta i, boolean esNuevo) throws ExcepcionGeneralSistema {
        if (esNuevo && i.getId() == null) {

//            if(itemListaPrecioDAO.getObjeto(ItemListaPrecioVenta.class, i.getId())!=null){
//                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código "+ i.getId());
//            }
            itemListaPrecioDAO.crear(i);
        } else {
            itemListaPrecioDAO.editar(i);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardarLista(List<ItemListaPrecioVenta> precios) throws ExcepcionGeneralSistema {

        for (ItemListaPrecioVenta ip : precios) {

            if (ip.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            //Si el precio es menor al precio mínimo, asignamos el precio mínimo.
            if (ip.getPrecio().compareTo(ip.getListaDePrecio().getPrecioMinimo()) < 0) {
                ip.setPrecio(ip.getListaDePrecio().getPrecioMinimo());
            }

            ItemListaPrecioVenta ic = getItemsListaPrecioByProducto(ip.getCodlis(), ip.getArtcod(), ip.getFechaVigencia());

            if (ic != null) {

                ic.setPrecio(ip.getPrecio());
                ic.setMonedaRegistracion(ip.getMonedaRegistracion());
                itemListaPrecioDAO.editar(ic);
            } else {
                itemListaPrecioDAO.crear(ip);
            }
        }
    }

    public List<ItemListaPrecioVenta> getListaByBusqueda(
            ListaPrecioVenta lista,
            Map<String, String> filtro,
            String txtBusqueda,
            boolean mostrarDeBaja,
            boolean soloVigentes,
            int cantRegistros) {

        String codLista = null;
        if (lista != null) {
            codLista = lista.getCodigo();
        }

        return itemListaPrecioDAO.getListaByBusqueda(codLista, filtro, txtBusqueda, mostrarDeBaja, soloVigentes, cantRegistros);
    }

    public List<ItemListaPrecioVenta> getListaByBusqueda(
            ListaPrecioVenta lista,
            String txtBusqueda,
            boolean mostrarDeBaja,
            boolean soloVigentes) {

        String codLista = null;
        if (lista != null) {
            codLista = lista.getCodigo();
        }

        return itemListaPrecioDAO.getListaByBusqueda(codLista, null, txtBusqueda, mostrarDeBaja, soloVigentes, 15);
    }

    public ItemListaPrecioVenta getItemListaPrecioVenta(Integer id) {
        return itemListaPrecioDAO.getObjeto(ItemListaPrecioVenta.class, id);
    }

    public BigDecimal getPrecioByProducto(String codLis, String codigo, Date fechaVigencia) {

        return itemListaPrecioDAO.getPrecioByProducto(codLis, codigo, fechaVigencia);
    }

    public List<ItemListaPrecioVenta> getPreciosByProducto(Producto producto) {

        if (producto == null) {
            return new ArrayList<ItemListaPrecioVenta>();
        }

        List<ListaPrecioVenta> listas = listaPrecioRN.getListaByBusqueda("", false);
        List<ItemListaPrecioVenta> precios = new ArrayList<>();

        if (listas != null || !listas.isEmpty()) {

            listas.forEach(l -> {

                ItemListaPrecioVenta i = new ItemListaPrecioVenta();
                i.setListaDePrecio(l);
                i.setPrecio(listaPrecioRN.getPrecioByProducto(l, producto, BigDecimal.ONE, BigDecimal.ZERO));
                i.setMonedaRegistracion(producto.getMonedaReposicion().getCodigo());
                precios.add(i);
            });
        }

//        List<ItemListaPrecioVenta> precios = itemListaPrecioDAO.getPreciosByProducto(producto.getCodigo());
//
//        if (precios == null || precios.isEmpty()) {
//
//            precios = new ArrayList<ItemListaPrecioVenta>();
//
//            if (listas != null) {
//                for (ListaPrecioVenta l : listas) {
//
//                    ItemListaPrecioVenta i = new ItemListaPrecioVenta();
//                    i.setListaDePrecio(l);
//                    i.setPrecio(listaPrecioRN.getPrecioByProducto(l, producto, BigDecimal.ONE, BigDecimal.ZERO));
//                    i.setMonedaRegistracion(producto.getMonedaReposicion().getCodigo());
//
//                    precios.add(i);
//                }
//            }
//        } else {
//            for (ItemListaPrecioVenta i : precios) {
//
//                if (i.getListaDePrecio().getCalculaPrecioDesdeUtilidad().equals("S")) {
//
//                }
//            }
//        }
        return precios;
    }

    public ItemListaPrecioVenta getItemsListaPrecioByProducto(String codLis, String codigo, Date fechaVigencia) {

        return itemListaPrecioDAO.getItemsPrecioByProducto(codLis, codigo, fechaVigencia);
    }

    public void eliminar(ItemListaPrecioVenta i) {
        itemListaPrecioDAO.eliminar(ItemListaPrecioVenta.class, i.getId());
    }

}
