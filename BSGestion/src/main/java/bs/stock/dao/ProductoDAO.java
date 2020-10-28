/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.dao;

import bs.global.dao.BaseDAO;
import bs.stock.modelo.Producto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author lloubiere
 */
@Stateless
public class ProductoDAO extends BaseDAO implements Serializable {

    public Producto getProducto(String id) {
        return getObjeto(Producto.class, id);
    }

    public Producto getProducto(Producto p) {

        return getProducto(p.getCodigo());
    }

    public Producto getProductoByCodigoBarra(String codigoBarra) {

        try {
            String sQuery = "SELECT e FROM Producto e "
                    + " WHERE e.codigoBarra = :codigoBarra or e.codigo =:codigo "
                    + " AND e.auditoria.debaja = 'N' ";

            Query q = getEm().createQuery(sQuery);
            q.setMaxResults(1);
            q.setParameter("codigo", codigoBarra);
            q.setParameter("codigoBarra", codigoBarra);

            return (Producto) q.getSingleResult();

        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            log.log(Level.SEVERE, "getProductoByCodigoBarra", e);
            return null;
        }
    }

    public List<Producto> getListaByBusqueda(String codTipo, Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM Producto e "
                    + " WHERE (e.codigo LIKE :codigo "
                    + " OR e.descripcion LIKE :descripcion "
                    + " OR e.numeroParte LIKE :numeroParte"
                    + " OR e.codigoProveedor LIKE :codigoProveedor"
                    + " OR e.codigoReferencia LIKE :codigoReferencia) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + (codTipo == null || codTipo.isEmpty() ? " " : " AND e.tipoProducto.codigo = :tipoProducto ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.descripcion ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("numeroParte", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("codigoProveedor", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("codigoReferencia", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (codTipo != null && !codTipo.isEmpty()) {
                q.setParameter("tipoProducto", codTipo);
            }

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getProductoByBusqueda", e);
            return new ArrayList<Producto>();
        }
    }

    public int getProximoCodigoProducto(int cantCaracteres, String tippro, String rub01, String rub02) {

        try {
            String sQuery = "select IFNULL(CAST(RIGHT(MAX(st_producto.CODIGO)," + cantCaracteres + ") AS SIGNED),0) +1  "
                    + " from st_producto "
                    + " WHERE 1=1"
                    + (tippro == null || tippro.isEmpty() ? "" : " and st_producto.TIPPRO = '" + tippro + "' ")
                    + (rub01 == null || rub01.isEmpty() ? "" : " and st_producto.RUBR01 = '" + rub01 + "' ")
                    + (rub02 == null || rub02.isEmpty() ? "" : " and st_producto.RUBR02 = '" + rub02 + "' ");

            Query q = getEm().createNativeQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getProximoCodigoProducto", e.getMessage());
            return 0;
        }
    }

    public int getCantidadRegistros() {

        try {
            String sQuery = "SELECT COUNT(e) FROM Producto e WHERE e.auditoria.debaja = 'N' ";

            Query q = getEm().createQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getProductoByBusqueda", e);
            return 0;
        }

    }

    public void setDeBajaAll(String codTip) {

        try {
            String sQuery = "UPDATE Producto e SET e.auditoria.debaja = 'S' "
                    + " WHERE e.tipoProducto.codigo = '" + codTip + "'";

            int count = getEm().createQuery(sQuery).executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "setDeBajaAll", e.getMessage());

        }
    }

}
