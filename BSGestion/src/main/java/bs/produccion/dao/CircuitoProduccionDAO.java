/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.dao;

import bs.global.dao.BaseDAO;
import bs.global.modelo.Comprobante;
import bs.produccion.modelo.CircuitoProduccion;
import bs.produccion.modelo.CircuitoProduccionPK;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author ctrosch
 */
@Stateless
public class CircuitoProduccionDAO extends BaseDAO {

    public CircuitoProduccion getCircuito(CircuitoProduccionPK idPK) {
        return getEm().find(CircuitoProduccion.class, idPK);
    }

    public Comprobante getComprobanteProduccion(String circom, String cirapl, String modcom, String codcom) {

        try {

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoProduccionProduccion i "
                    + "WHERE i.cirapl = '" + cirapl + "' "
                    + "AND i.circom = '" + circom + "' "
                    + "AND i.modulo = 'PD' "
                    + "AND i.codcom = '" + codcom + "' ";
            return queryObject(Comprobante.class, sQuery);

        } catch (Exception e) {
            return null;
        }

    }

    public Comprobante getComprobanteProveedor(String circom, String cirapl, String modcom, String codcom) {

        try {

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoProduccionProveedor i "
                    + "WHERE i.cirapl = '" + cirapl + "' "
                    + "AND i.circom = '" + circom + "' "
                    + "AND i.modulo = 'PV' "
                    + "AND i.codcom = '" + codcom + "' ";
            return queryObject(Comprobante.class, sQuery);

        } catch (Exception e) {
            System.err.println("Error getComprobanteProveedor " + e);
            return null;
        }
    }

    public Comprobante getComprobanteTesoreria(String circom, String cirapl, String modcom, String codcom) {

        try {

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoProduccionTesoreria i "
                    + "WHERE i.cirapl = '" + cirapl + "' "
                    + "AND i.circom = '" + circom + "' "
                    + "AND i.modulo = 'CJ' "
                    + "AND i.codcom = '" + codcom + "' ";
            return queryObject(Comprobante.class, sQuery);

        } catch (Exception e) {
            System.err.println("Error getComprobanteTesoreria " + e);
            return null;
        }
    }

    public Comprobante getComprobanteStock(String circom, String cirapl, String modcom, String codcom) {
        try {

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoProduccionStock i "
                    + "WHERE i.cirapl = '" + cirapl + "' "
                    + "AND i.circom = '" + circom + "' "
                    + "AND i.modulo = 'ST' "
                    + "AND i.codcom = '" + codcom + "' ";
            return queryObject(Comprobante.class, sQuery);

        } catch (Exception e) {
            System.err.println("Error getComprobanteStock " + e);
            return null;
        }
    }

    public List<CircuitoProduccion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {

            String sQuery = "SELECT e FROM CircuitoProduccion e "
                    + " WHERE (e.circom LIKE :circom OR e.cirapl LIKE :cirapl OR e.descripcion LIKE :descripcion ) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.circom, e.cirapl ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("circom", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("cirapl", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e);
            return new ArrayList<CircuitoProduccion>();
        }
    }

}
