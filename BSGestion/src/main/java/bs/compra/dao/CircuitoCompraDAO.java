/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.compra.dao;

import bs.compra.modelo.CircuitoCompra;
import bs.compra.modelo.CircuitoCompraPK;
import bs.global.dao.BaseDAO;
import bs.global.modelo.Comprobante;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class CircuitoCompraDAO extends BaseDAO {

    public CircuitoCompra getCircuito(CircuitoCompraPK idPK) {
        return getEm().find(CircuitoCompra.class, idPK);
    }

    public Comprobante getComprobanteCompra(String circom, String cirapl, String codcom) {

        try {

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoCompraCompra i "
                    + "WHERE i.cirapl = '" + cirapl + "' "
                    + "AND i.circom = '" + circom + "' "
                    + "AND i.modulo = 'CO' "
                    + "AND i.codcom = '" + codcom + "' ";
            return queryObject(Comprobante.class, sQuery);

        } catch (Exception e) {
            return null;
        }

    }

    public Comprobante getComprobanteProveedor(String circom, String cirapl, String codcom) {

        try {

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoCompraProveedor i "
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

    public Comprobante getComprobanteTesoreria(String circom, String cirapl, String codcom) {

        try {

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoCompraTesoreria i "
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

    public Comprobante getComprobanteStock(String circom, String cirapl, String codcom) {
        try {

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoCompraStock i "
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

    public List<CircuitoCompra> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {

            String sQuery = "SELECT e FROM CircuitoCompra e "
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
            return new ArrayList<CircuitoCompra>();
        }
    }

}
