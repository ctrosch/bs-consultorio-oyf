/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.dao;

import bs.global.dao.BaseDAO;
import bs.proveedores.modelo.MovimientoProveedor;
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
public class ProveedorDAO extends BaseDAO {

    public List<MovimientoProveedor> getComprobantesPendienteAutorizarAFIP() {

        try {

            String sQuery = "SELECT e FROM MovimientoProveedor e "
                    + " WHERE e.numeroCAE IS NULL "
                    + " AND e.tipoExportacion IS NOT NULL"
                    + " AND e.puntoVenta.implementaFE = 'S' ";

//            System.err.println(sQuery);
            Query q = getEm().createQuery(sQuery);
            return q.getResultList();

        } catch (Exception e) {
            System.err.println("Error al consultar getComprobantesPendienteAutorizarAFIP" + e);
            return new ArrayList<MovimientoProveedor>();
        }

    }

    public MovimientoProveedor getMovimiento(String codFormulario, Integer numeroFormulario) {

        try {
            String sQuery = "SELECT m FROM MovimientoProveedor m "
                    + "WHERE m.formulario.codigo = :codFormulario "
                    + "AND m.numeroFormulario = :numeroFormulario";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codFormulario", codFormulario);
            q.setParameter("numeroFormulario", numeroFormulario);

            q.setMaxResults(1);
            return (MovimientoProveedor) q.getSingleResult();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.err.println("Error al obtener movimientos de proveedor " + e);
            return null;
        }
    }

    public MovimientoProveedor getMovimientoByPuntoVentaNumeroOriginal(String nrocta, String puntoVentaOriginal, String numeroOriginal) {

        try {

            String sQuery = "SELECT m FROM MovimientoProveedor m "
                    + " WHERE m.puntoVentaOriginal = :puntoVentaOriginal "
                    + " AND m.numeroOriginal = :numeroOriginal "
                    + " AND m.proveedor.nrocta = :nrocta "
                    + " AND m.movimientoReversion IS NULL ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("nrocta", nrocta);
            q.setParameter("puntoVentaOriginal", puntoVentaOriginal);
            q.setParameter("numeroOriginal", numeroOriginal);

            q.setMaxResults(1);
            return (MovimientoProveedor) q.getSingleResult();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.err.println("Error al obtener movimientos de proveedor " + e);
            return null;
        }
    }

    public List<MovimientoProveedor> getComprobantesByBusqueda(Map parameters) {

        try {
            String sQuery = "SELECT m FROM MovimientoProveedor m "
                    + "WHERE m.formulario.codigo = :codFormulario "
                    + "AND m.numeroFormulario = :numeroFormulario";

            Query q = getEm().createQuery(sQuery);

//            q.setParameter("codFormulario", codFormulario);
//            q.setParameter("numeroFormulario", numeroFormulario);
            q.setMaxResults(1);
            return q.getResultList();

        } catch (javax.persistence.NoResultException nre) {
            return new ArrayList<MovimientoProveedor>();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de proveedor");
            return new ArrayList<MovimientoProveedor>();
        }
    }

    public List<MovimientoProveedor> getListaByBusqueda(Map<String, String> filtro, List<String> orderBy, int cantMax) {

        try {
            String sQuery = "SELECT m FROM MovimientoProveedor m ";
            sQuery += generarStringFiltro(filtro, "m", true);
            sQuery += (orderBy == null || orderBy.isEmpty() ? " ORDER BY m.fechaMovimiento DESC, m.numeroFormulario DESC" : generarStringOrden(orderBy, "m"));

            Query q = getEm().createQuery(sQuery);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener movimientos de proveedor");
            return new ArrayList<MovimientoProveedor>();
        }
    }
}
