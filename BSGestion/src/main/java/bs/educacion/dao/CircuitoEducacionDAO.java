/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.dao;

import bs.educacion.modelo.CircuitoEducacion;
import bs.educacion.modelo.CircuitoEducacionPK;
import bs.educacion.modelo.CodigoCircuitoEducacion;
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
public class CircuitoEducacionDAO extends BaseDAO {

    public CircuitoEducacion getCircuito(CircuitoEducacionPK idPK) {
        return getEm().find(CircuitoEducacion.class, idPK);
    }

    public List<CircuitoEducacion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {

            String sQuery = "SELECT e FROM CircuitoEducacion e "
                    + " WHERE (e.descripcion LIKE :descripcion )"
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.circom, e.cirapl ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e);
            return new ArrayList<CircuitoEducacion>();
        }
    }

    public Comprobante getComprobanteEducacion(String circom, String cirapl, String codcom) {

        try {

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoEducacion i "
                    + "WHERE i.cirapl = '" + cirapl + "' "
                    + "AND i.circom = '" + circom + "' "
                    + "AND i.modulo = 'ED' "
                    + "AND i.codcom = '" + codcom + "' ";
            return queryObject(Comprobante.class, sQuery);

        } catch (Exception e) {
            return null;
        }

    }

    public Comprobante getComprobanteVenta(String circom, String cirapl, String codcom) {

        try {

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoEducacionVenta i "
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

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoEducacionTesoreria i "
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

            String sQuery = "SELECT i.comprobante FROM ItemCircuitoEducacionStock i "
                    + "WHERE i.cirapl = '" + cirapl + "' "
                    + "AND i.circom = '" + circom + "' "
                    + "AND i.modulo = 'ST' "
                    + "AND i.codcom = '" + codcom + "' ";
            return queryObject(Comprobante.class, sQuery);

        } catch (Exception e) {
            return null;
        }
    }

    public CodigoCircuitoEducacion getCodigoCircuito(String codigo) {

        return getEm().find(CodigoCircuitoEducacion.class, codigo);

    }

    public List<CodigoCircuitoEducacion> getListaCodigoCircuito() {

        return getLista(CodigoCircuitoEducacion.class, true, -1, -1);
    }

}
