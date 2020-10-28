/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.dao;

import bs.global.dao.BaseDAO;
import bs.ventas.modelo.MovimientoVenta;
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
public class MovimientoVentaDAO extends BaseDAO {

    public List<MovimientoVenta> getComprobantesPendienteAutorizarAFIP() {

        try {

            String sQuery = "SELECT e FROM MovimientoVenta e "
                    + " WHERE e.numeroCAE IS NULL "
                    + " AND e.tipoExportacion IS NOT NULL"
                    + " AND e.puntoVenta.implementaFE = 'S' ";

//            System.err.println(sQuery);
            Query q = getEm().createQuery(sQuery);
            return q.getResultList();

        } catch (Exception e) {

            e.printStackTrace();
            System.err.println("Error al consultar getComprobantesPendienteAutorizarAFIP" + e.getCause());
            return new ArrayList<MovimientoVenta>();
        }

    }

    public MovimientoVenta getMovimiento(String codFormulario, Integer numeroFormulario) {

        try {
            String sQuery = "SELECT m FROM MovimientoVenta m "
                    + "WHERE m.formulario.codigo = :codFormulario "
                    + "AND m.numeroFormulario = :numeroFormulario";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codFormulario", codFormulario);
            q.setParameter("numeroFormulario", numeroFormulario);

            q.setMaxResults(1);
            return (MovimientoVenta) q.getSingleResult();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de venta");
            return null;
        }
    }

    public List<MovimientoVenta> getListaByBusqueda(Map<String, String> filtro, List<String> orderBy, int cantMax) {

        try {
            String sQuery = "SELECT m FROM MovimientoVenta m ";
            sQuery += generarStringFiltro(filtro, "m", true);
            sQuery += (orderBy == null || orderBy.isEmpty() ? " ORDER BY m.fechaMovimiento DESC, m.numeroFormulario DESC" : generarStringOrden(orderBy, "m"));

            Query q = getEm().createQuery(sQuery);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            System.err.println("Error al obtener movimientos de venta" + e);
            return new ArrayList<MovimientoVenta>();
        }
    }
}
