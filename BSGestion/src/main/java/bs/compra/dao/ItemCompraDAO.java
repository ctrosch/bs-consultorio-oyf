/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.compra.dao;

import bs.compra.modelo.ItemMovimientoCompra;
import bs.global.dao.BaseDAO;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class ItemCompraDAO extends BaseDAO {

    public ItemMovimientoCompra getItemProducto(Integer id) {

        return getObjeto(ItemMovimientoCompra.class, id);
    }

    public List<ItemMovimientoCompra> getAplicacionesByItem(Integer idItem) {

        try {
            String sQuery = "SELECT i FROM ItemMovimientoCompra i "
                    + "WHERE i.idItemAplicacion = :idItem "
                    + "AND i.id <> i.idItemAplicacion "
                    + "AND i.auditoria.debaja = 'N' "
                    + "ORDER BY i.producto.codigo ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("idItem", idItem);

            return q.getResultList();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener ItemAplicacionCompra de facturacion");
            return null;
        }
    }

    public ItemMovimientoCompra getItemProductoByItemAplicacion(Integer idMovimiento, Integer nroItem, String artcod) {

        try {
            String sQuery = "SELECT i FROM ItemMovimientoCompra i "
                    + "WHERE i.movimiento.id = :idMovimiento "
                    + "AND i.nroitm =:nroItem "
                    + "AND i.producto.codigo =:artcod "
                    + "AND i.id = i.idItemAplicacion "
                    + "AND i.auditoria.debaja = 'N' "
                    + "ORDER BY i.producto.codigo ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("idMovimiento", idMovimiento);
            q.setParameter("nroItem", nroItem);
            q.setParameter("artcod", artcod);

            q.setMaxResults(1);

            return (ItemMovimientoCompra) q.getSingleResult();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener ItemAplicacionCompra de facturacion");
            return null;
        }

    }
}
