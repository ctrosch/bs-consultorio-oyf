/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.dao;

import bs.facturacion.modelo.ItemMovimientoFacturacion;
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
public class ItemFacturacionDAO extends BaseDAO {

    public ItemMovimientoFacturacion getItemProducto(Integer id) {

        return getObjeto(ItemMovimientoFacturacion.class, id);
    }

    public List<ItemMovimientoFacturacion> getAplicacionesByItem(Integer idItem) {

        try {
            String sQuery = "SELECT i FROM ItemMovimientoFacturacion i "
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
            System.err.println("Error al obtener getAplicacionesByItem de facturacion");
            return null;
        }
    }

    public ItemMovimientoFacturacion getItemProductoByItemAplicacion(Integer idMovimiento, Integer nroItem, String artcod) {

        try {
            String sQuery = "SELECT i FROM ItemMovimientoFacturacion i "
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

            return (ItemMovimientoFacturacion) q.getSingleResult();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener getItemProductoByItemAplicacion de facturacion");
            return null;
        }

    }
}
