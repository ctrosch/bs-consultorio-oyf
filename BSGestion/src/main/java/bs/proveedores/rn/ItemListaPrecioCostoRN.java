/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.proveedores.dao.ItemListaPrecioCostoDAO;
import bs.proveedores.modelo.ItemListaPrecioCosto;
import bs.proveedores.modelo.ListaCosto;
import bs.stock.dao.ProductoDAO;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class ItemListaPrecioCostoRN implements Serializable {

    @EJB
    private ItemListaPrecioCostoDAO itemListaPrecioDAO;
    @EJB
    private ProductoDAO productoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(ItemListaPrecioCosto i, boolean esNuevo) throws ExcepcionGeneralSistema {
        if (esNuevo && i.getId() == null) {

//            if(itemListaPrecioDAO.getObjeto(ItemListaPrecioCosto.class, i.getId())!=null){
//                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código "+ i.getId());
//            }
            itemListaPrecioDAO.crear(i);
        } else {
            itemListaPrecioDAO.editar(i);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardarLista(List<ItemListaPrecioCosto> precios, boolean actualizaPrecioReposicion) throws ExcepcionGeneralSistema {

        for (ItemListaPrecioCosto ip : precios) {

            if (ip.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            ItemListaPrecioCosto ic = getItemsListaPrecioByProducto(ip.getCodlis(), ip.getArtcod(), ip.getFechaVigencia());

            if (ic != null) {
                ic.setPrecio(ip.getPrecio());
                ic.setMonedaRegistracion(ip.getMonedaRegistracion());

                itemListaPrecioDAO.editar(ic);

            } else {

                itemListaPrecioDAO.crear(ip);
            }

            if (actualizaPrecioReposicion) {

                ip.getProducto().setPrecioReposicion(ip.getPrecio());
                productoDAO.editar(ip.getProducto());
            }

        }
    }

    public ItemListaPrecioCosto getItemListaPrecioCosto(Integer id) {
        return itemListaPrecioDAO.getObjeto(ItemListaPrecioCosto.class, id);
    }

    /**
     *
     * @param lista Lista de costo
     * @param filtro Filtro recibido para filtrar items
     * @param txtBusqueda Campo de búqueda
     * @param mostrarDebaja Mostrar items dados de baja
     * @param soloVigentes Mostrar solo los items con la mayor fecha de
     * vigencia.
     * @param cantMax Cantidad máxima de items a encontrar
     * @return
     */
    public List<ItemListaPrecioCosto> getListaByBusqueda(ListaCosto lista,
            Map<String, String> filtro,
            String txtBusqueda,
            boolean mostrarDebaja,
            boolean soloVigentes,
            int cantMax) {

        String codLista = null;
        if (lista != null) {
            codLista = lista.getCodigo();
        }

        return itemListaPrecioDAO.getListaByBusqueda(codLista, filtro, txtBusqueda, mostrarDebaja, soloVigentes, cantMax);
    }

    public List<ItemListaPrecioCosto> getListaByBusqueda(String codigo, boolean soloVigentes) {

        return itemListaPrecioDAO.getListaByBusqueda(codigo, null, "", false, soloVigentes, 20000);
    }

    public BigDecimal getPrecioByProducto(String codLis, String codigo, Date fechaVigencia) {

        return itemListaPrecioDAO.getPrecioByProducto(codLis, codigo, fechaVigencia);
    }

    public ItemListaPrecioCosto getItemsListaPrecioByProducto(String codigoLista, String codigoProducto, Date fechaVigencia) {

        return itemListaPrecioDAO.getItemsPrecioByProducto(codigoLista, codigoProducto, fechaVigencia);
    }

    public void eliminar(ItemListaPrecioCosto i) {
        itemListaPrecioDAO.eliminar(ItemListaPrecioCosto.class, i.getId());
    }

}
