/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.dao;

import bs.global.dao.BaseDAO;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.ItemMovimientoStock;
import bs.stock.modelo.Producto;
import bs.stock.modelo.Stock;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Stateless
public class StockDAO extends BaseDAO {

    @EJB
    ProductoDAO productoDAO;
    @EJB
    DepositoDAO depositoDAO;

    public double getCantidadStockByProducto(Stock s) {

        try {

            Query q = (Query) getEm().createQuery("SELECT SUM(s.stocks) "
                    + "FROM Stock s "
                    + "WHERE s.artcod = :artcod "
                    + "AND s.deposi = :deposi "
                    + "AND s.atributo1 = :atributo1 "
                    + "AND s.atributo2 = :atributo2 "
                    + "AND s.atributo3 = :atributo3 "
                    + "AND s.atributo4 = :atributo4 "
                    + "AND s.atributo5 = :atributo5 "
                    + "AND s.atributo6 = :atributo6 "
                    + "AND s.atributo7 = :atributo7 "
                    + " ");

            q.setParameter("artcod", s.getCodigo());
            q.setParameter("deposi", s.getDeposi());
            q.setParameter("atributo1", s.getAtributo1());
            q.setParameter("atributo2", s.getAtributo2());
            q.setParameter("atributo3", s.getAtributo3());
            q.setParameter("atributo4", s.getAtributo4());
            q.setParameter("atributo5", s.getAtributo5());
            q.setParameter("atributo6", s.getAtributo6());
            q.setParameter("atributo7", s.getAtributo7());

            Double cantidad = (Double) q.getSingleResult();

            if (cantidad == null) {
                return 0;
            }
            return cantidad.doubleValue();

        } catch (NoResultException e) {

            return 0;

        } catch (Exception e) {
            System.out.println("No se puede obtener disponible en stock" + e);
            return 0;
        }
    }

    public double getCantidadStockTotalByProducto(Producto p, Deposito d) {

        try {

            Query q = (Query) getEm().createQuery("SELECT SUM(s.stocks) "
                    + "FROM Stock s "
                    + "WHERE s.artcod = :artcod "
                    + "AND s.deposi = :deposi ");

            q.setParameter("artcod", p.getCodigo());
            q.setParameter("deposi", d.getCodigo());

            Double cantidad = (Double) q.getSingleResult();

            if (cantidad == null) {
                return 0;
            }
            return cantidad.doubleValue();

        } catch (NoResultException e) {

            return 0;

        } catch (Exception e) {
            System.out.println("No se puede obtener disponible en stock" + e);
            return 0;
        }
    }

    public double getCantidadStockAFecha(Producto p, Deposito d, Date fecha) {

        try {

            Query q = getEm().createNativeQuery(" SELECT ifnull(sum(st_movimiento_item.STOCKS) ,0) "
                    + " FROM st_movimiento INNER JOIN st_movimiento_item "
                    + " ON   st_movimiento.ID = st_movimiento_item.ID_MCAB "
                    + " WHERE st_movimiento_item.ARTCOD = ?1 "
                    + " AND st_movimiento_item.DEPOSI = ?2 "
                    + " AND st_movimiento.FCHMOV <= ?3 ");

            q.setParameter("1", p.getCodigo());
            q.setParameter("2", d.getCodigo());
            q.setParameter("3", fecha);

            Double cantidad = (Double) q.getSingleResult();

            if (cantidad == null) {
                return 0;
            }
            return cantidad.doubleValue();

        } catch (Exception e) {
            System.out.println("No se puede obtener stock por producto a fecha para el producto " + p + ", depósito " + d + ", fecha " + fecha + " Causa: " + e);
            return 0;
        }

    }

    public List<Stock> getStockByDeposito(String codDep) {
        try {
            Query q = getEm().createQuery("SELECT s FROM Stock s "
                    + "WHERE s.deposi = :deposi "
                    + "ORDER BY s.artcod ");

            q.setParameter("deposi", codDep);

            return q.getResultList();

        } catch (Exception e) {
            System.out.println("getStockByDeposito" + e);
            return new ArrayList<Stock>();
        }
    }

    public List<Stock> getStockByDepositoSinAtributos(String codDep) {
        try {

            Query q = getEm().createNativeQuery("SELECT ARTCOD, DEPOSI, '' AS NATRI1,'' AS NATRI2,'' AS NATRI3,'' AS NATRI4,'' AS NATRI5,'' AS NATRI6,'' AS NATRI7, SUM(STOCKS) AS STOCKS "
                    + " FROM st_stock "
                    + " WHERE DEPOSI = '" + codDep + "' "
                    + " GROUP BY ARTCOD,DEPOSI "
                    + " ORDER BY ARTCOD ", Stock.class);

            return q.getResultList();

        } catch (Exception e) {
            System.out.println("getStockByDeposito" + e);
            return new ArrayList<Stock>();
        }
    }

    public List<Stock> getStockByProductoSinAtributos(String artcod) {
        try {

            Query q = getEm().createNativeQuery("SELECT ARTCOD, DEPOSI, '' AS NATRI1,'' AS NATRI2,'' AS NATRI3,'' AS NATRI4,'' AS NATRI5,'' AS NATRI6,'' AS NATRI7, SUM(STOCKS) AS STOCKS "
                    + " FROM st_stock "
                    + " WHERE ARTCOD = '" + artcod + "' "
                    + " GROUP BY ARTCOD,DEPOSI "
                    + " ORDER BY ARTCOD ", Stock.class);

            return q.getResultList();

        } catch (Exception e) {
            System.out.println("getStockByDeposito" + e);
            return new ArrayList<Stock>();
        }
    }

    public List<Stock> getStockByProducto(String artCod) {
        try {

            Query q = getEm().createQuery("SELECT s FROM Stock s "
                    + "WHERE s.artcod = :codigo "
                    + "ORDER BY s.deposi ");

            q.setParameter("codigo", artCod);

            return q.getResultList();

        } catch (Exception e) {
            System.out.println("getStockByProducto" + e.getCause());
            return new ArrayList<Stock>();
        }
    }

    public List<Stock> getStockByProductoDeposito(String artcod, String deposi) {
        try {

            Query q = getEm().createQuery(" SELECT s FROM Stock s "
                    + " WHERE s.artcod = :codigo "
                    + " AND s.deposi = :deposito "
                    + " AND s.stocks <> 0 "
                    + " ");

            q.setParameter("codigo", artcod);
            q.setParameter("deposito", deposi);

            return q.getResultList();

        } catch (Exception e) {
            System.out.println("No se puede obtener stock por producto y deposito" + e);
            return new ArrayList<Stock>();
        }
    }

    public List<ItemMovimientoStock> getMovimientosEntreFechas(Producto p, Deposito d, Date fDesde, Date fHasta) {
        try {

            Query q = getEm().createQuery("SELECT i FROM ItemMovimientoStock i "
                    + "WHERE i.producto.codigo = :codigo "
                    + "AND i.deposito.codigo = :deposi "
                    + "AND i.movimiento.fechaMovimiento BETWEEN :fDesde AND :fHasta ");

            q.setParameter("codigo", p.getCodigo());
            q.setParameter("deposi", d.getCodigo());
            q.setParameter("fDesde", fDesde);
            q.setParameter("fHasta", fHasta);

            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No se puede obtener stock entre fechas" + e.getMessage());
            return new ArrayList<ItemMovimientoStock>();
        }
    }

    public void recalcularStock() {

        try {
            Query q1 = getEm().createNativeQuery("DELETE FROM st_stock");

            q1.executeUpdate();

            Query q2 = getEm().createNativeQuery("INSERT INTO `st_stock` (`artcod`, `deposi`, `natri1`, `natri2`, `natri3`, `natri4`, `natri5`, `natri6`, `natri7`, "
                    + " `stocks`, `DEBAJA`, `FECALT`, `FECMOD`, `ULTOPR`) "
                    + " SELECT i.artcod, i.deposi,i.natri1,i.natri2,i.natri3,i.natri4,i.natri5,i.natri6,i.natri7, SUM(i.stocks) as stocks,'N' as DEBAJA,CURDATE() AS FECALT, "
                    + " CURDATE() AS FECMOD,'A' AS ULTOPR"
                    + " FROM st_movimiento_item i inner JOIN st_movimiento m on m.id = i.ID_MCAB"
                    + " GROUP BY i.artcod, i.deposi,"
                    + " i.natri1,i.natri2,i.natri3,i.natri4,i.natri5,i.natri6,i.natri7");

            q2.executeUpdate();
        } catch (Exception e) {

            System.err.println("recalcularStock " + e);
        }
    }

}
