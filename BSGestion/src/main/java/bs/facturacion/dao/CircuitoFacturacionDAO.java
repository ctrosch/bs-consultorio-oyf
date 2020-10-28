/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.dao;

import bs.facturacion.modelo.CircuitoFacturacion;
import bs.facturacion.modelo.CircuitoFacturacionPK;
import bs.facturacion.modelo.CodigoCircuitoFacturacion;
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
public class CircuitoFacturacionDAO extends BaseDAO {

    public CircuitoFacturacion getCircuito(CircuitoFacturacionPK idPK) {
        return getEm().find(CircuitoFacturacion.class, idPK);
    }

    public List<CircuitoFacturacion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {

            String sQuery = "SELECT e FROM CircuitoFacturacion e "
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
            return new ArrayList<CircuitoFacturacion>();
        }
    }

    public Comprobante getComprobanteFacturacion(String circom, String cirapl, String codcom) {

        try {

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoFacturacion i "
                    + "WHERE i.cirapl = '" + cirapl + "' "
                    + "AND i.circom = '" + circom + "' "
                    + "AND i.modulo = 'FC' "
                    + "AND i.codcom = '" + codcom + "' ";
            return queryObject(Comprobante.class, sQuery);

        } catch (Exception e) {
            return null;
        }

    }

    public Comprobante getComprobanteVenta(String circom, String cirapl, String codcom) {

        try {

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoVenta i "
                    + "WHERE i.cirapl = '" + cirapl + "' "
                    + "AND i.circom = '" + circom + "' "
                    + "AND i.modulo = 'VT' "
                    + "AND i.codcom = '" + codcom + "' ";
            return queryObject(Comprobante.class, sQuery);

        } catch (Exception e) {
            return null;
        }
    }

    public Comprobante getComprobanteTesoreria(String circom, String cirapl, String codcom) {

        try {

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoTesoreria i "
                    + "WHERE i.cirapl = '" + cirapl + "' "
                    + "AND i.circom = '" + circom + "' "
                    + "AND i.modulo = 'CJ' "
                    + "AND i.codcom = '" + codcom + "' ";
            return queryObject(Comprobante.class, sQuery);

        } catch (Exception e) {
            return null;
        }
    }

    public Comprobante getComprobanteStock(String circom, String cirapl, String codcom) {
        try {

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoStock i "
                    + "WHERE i.cirapl = '" + cirapl + "' "
                    + "AND i.circom = '" + circom + "' "
                    + "AND i.modulo = 'ST' "
                    + "AND i.codcom = '" + codcom + "' ";
            return queryObject(Comprobante.class, sQuery);

        } catch (Exception e) {
            return null;
        }
    }

    public CodigoCircuitoFacturacion getCodigoCircuito(String codigo) {

        return getEm().find(CodigoCircuitoFacturacion.class, codigo);

    }

    public List<CodigoCircuitoFacturacion> getListaCodigoCircuito() {

        return getLista(CodigoCircuitoFacturacion.class, true, -1, -1);
    }

}
