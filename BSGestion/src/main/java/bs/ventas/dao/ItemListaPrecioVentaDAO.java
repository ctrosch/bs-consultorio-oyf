/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.dao;

import bs.global.dao.BaseDAO;
import bs.ventas.modelo.ItemListaPrecioVenta;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author ctrosch
 */
@Stateless
public class ItemListaPrecioVentaDAO extends BaseDAO {

    /**
     *
     * @param codigoLista Código de la lista de precios
     * @param filtro Filtro recibido para filtrar items
     * @param txtBusqueda Campo de búqueda
     * @param mostrarDebaja Mostrar items dados de baja
     * @param soloVigentes Mostrar solo los items con la mayor fecha de
     * vigencia.
     * @param cantMax Cantidad máxima de items a encontrar
     * @return
     */
    public List<ItemListaPrecioVenta> getListaByBusqueda(String codigoLista, Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, boolean soloVigentes, int cantMax) {

        try {
            String sQuery = "SELECT e FROM ItemListaPrecioVenta e "
                    + "WHERE "
                    + "      ((e.producto.codigo LIKE :codigo) "
                    + "  OR  (e.producto.descripcion LIKE :descripcion ) "
                    + "     ) "
                    + (!soloVigentes ? "" : " AND e.fechaVigencia = (SELECT MAX(e1.fechaVigencia) "
                            + "                        FROM ItemListaPrecioVenta e1"
                            + "                        WHERE e1.codlis = e.codlis "
                            + "                        AND e1.artcod = e.artcod"
                            + "                        AND e1.auditoria.debaja = 'N' )  ")
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + (codigoLista == null ? " " : " AND e.codlis = :codigoLista ")
                    + generarStringFiltro(filtro, "e", false)
                    + "ORDER BY e.producto.descripcion, e.fechaVigencia DESC ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (codigoLista != null) {
                q.setParameter("codigoLista", codigoLista);
            }

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return null;
        }
    }

    public List<ItemListaPrecioVenta> getPreciosByProducto(String codigo) {
        try {

            String sQuery = "SELECT e FROM ItemListaPrecioVenta e "
                    + "WHERE e.producto.codigo =:codigo "
                    + " AND e.fechaVigencia = (SELECT MAX(e1.fechaVigencia) "
                            + "                        FROM ItemListaPrecioVenta e1"
                            + "                        WHERE e1.codlis = e.codlis "
                            + "                        AND e1.artcod = e.artcod"
                            + "                        AND e1.auditoria.debaja = 'N' )  "
                    + "AND e.auditoria.debaja = 'N' "                                        
                    + "ORDER BY e.codlis ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("codigo", codigo);
            
            return q.getResultList();

        } catch (NoResultException nre) {
            return null;

        } catch (Exception e) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "getPreciosByProducto", e);
            return null;
        }
    }

    public BigDecimal getPrecioByProducto(String codLis, String codigo, Date fechaVigencia) {
        try {

            String sQuery = "SELECT p.precio FROM ItemListaPrecioVenta p "
                    + " WHERE p.auditoria.debaja = 'N' "
                    + " AND p.codlis = :codLis "
                    + " AND p.fechaVigencia <= :fechaVigencia "
                    + " AND p.producto.codigo =:codigo "
                    + " ORDER BY p.fechaVigencia desc ";

            Query q = getEm().createQuery(sQuery);

            q.setMaxResults(1);

            q.setParameter("codLis", codLis);
            q.setParameter("codigo", codigo);

            if (fechaVigencia == null) {
                q.setParameter("fechaVigencia", new Date());
            } else {
                q.setParameter("fechaVigencia", fechaVigencia);
            }

            return (BigDecimal) q.getSingleResult();

        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        } catch (Exception e) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "getPrecioByProducto", e);
            return BigDecimal.ZERO;
        }
    }

    public ItemListaPrecioVenta getItemsPrecioByProducto(String codLis, String codigo, Date fechaVigencia) {
        try {

            String sQuery = "SELECT p FROM ItemListaPrecioVenta p "
                    + " WHERE p.auditoria.debaja = 'N' "
                    + " AND p.codlis = :codLis "
                    + " AND p.fechaVigencia <= :fechaVigencia "
                    + " AND p.producto.codigo =:codigo "
                    + " ORDER BY p.fechaVigencia desc";

            Query q = getEm().createQuery(sQuery);

            q.setMaxResults(1);

            q.setParameter("codLis", codLis);
            q.setParameter("codigo", codigo);

            if (fechaVigencia == null) {
                q.setParameter("fechaVigencia", new Date());
            } else {
                q.setParameter("fechaVigencia", fechaVigencia);
            }

            return (ItemListaPrecioVenta) q.getSingleResult();

        } catch (javax.persistence.NoResultException nre) {
            return null;
        } catch (Exception e) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "getItemsPrecioByProducto", e);
            return null;
        }
    }

}
