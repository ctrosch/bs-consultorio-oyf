/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.dao;


import bs.global.dao.BaseDAO;
import bs.taller.modelo.ItemAplicacionTaller;
import bs.taller.modelo.ItemProductoTaller;
import bs.taller.modelo.MovimientoTaller;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class TallerDAO extends BaseDAO {

    public MovimientoTaller editar(MovimientoTaller m) {
        return (MovimientoTaller) super.editar(m);
    }

    public List<MovimientoTaller> getListaByBusqueda(Map<String, String> filtro, boolean soloPendientes, int cantMax) {

        try {
            String sQuery = "SELECT m FROM MovimientoTaller m ";
            if (soloPendientes) {
                sQuery += "WHERE m.movimientoAplicacion IS NULL ";
//                        + "AND NOT EXISTS(SELECT mf FROM MovimientoFacturacion WHERE mf.movimientoTaller.id = m.id) ";
            }
            sQuery += generarStringFiltro(filtro, "m", !soloPendientes);
            sQuery += " ORDER BY m.fechaMovimiento DESC, m.numeroFormulario DESC";

            Query q = getEm().createQuery(sQuery);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de facturacion");
            return new ArrayList<MovimientoTaller>();
        }
    }

    public MovimientoTaller getMovimientoTallerById(Integer id) {
        try {
            return getEm().find(MovimientoTaller.class, id);
        } catch (Exception e) {
            System.out.println("No se encontró movimiento: " + id);
            return null;
        }
    }


    public ItemAplicacionTaller getItemAplicacion(Integer id) {

        return getObjeto(ItemAplicacionTaller.class, id);
    }

    public MovimientoTaller getMovimiento(String codFormulario, Integer numeroFormulario) {

        try {
            String sQuery = "SELECT m FROM MovimientoTaller m "
                    + "WHERE m.formulario.codigo = :codFormulario "
                    + "AND m.numeroFormulario = :numeroFormulario";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codFormulario", codFormulario);
            q.setParameter("numeroFormulario", numeroFormulario);

            q.setMaxResults(1);
            return (MovimientoTaller) q.getSingleResult();

        } catch (NoResultException e) {

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de facturacion");
            return null;
        }

    }

    public List<ItemAplicacionTaller> getAplicacionesByItem(Integer idItem) {

        try {
            String sQuery = "SELECT i FROM ItemAplicacionTaller i "
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
            System.err.println("Error al obtener ItemAplicacionTaller de facturacion");
            return null;
        }
    }

    public ItemProductoTaller getItemProductoByItemAplicacion(Integer idMovimiento, Integer nroItem, String artcod) {

        try {
            String sQuery = "SELECT i FROM ItemProductoTaller i "
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

            return (ItemProductoTaller) q.getSingleResult();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener ItemAplicacionTaller de facturacion");
            return null;
        }

    }
}
