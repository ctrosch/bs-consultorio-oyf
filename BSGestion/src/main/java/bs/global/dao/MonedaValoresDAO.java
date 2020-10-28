/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.dao;

import bs.global.modelo.Moneda;
import bs.global.modelo.MonedaValores;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author ctrosch
 */
@Stateless
public class MonedaValoresDAO extends BaseDAO {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public Moneda getMoneda(String codcof) {

        return getObjeto(Moneda.class, codcof);
    }

    public List<MonedaValores> getListaByBusqueda(String codMoneda, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {

            String sQuery = "SELECT e FROM MonedaValores e "
                    + " WHERE (e.codcof LIKE :codigo ) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + " ORDER BY e.codcof, e.fechaAlta DESC ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e);
            return new ArrayList<MonedaValores>();
        }
    }

    public List<MonedaValores> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {

            String sQuery = "SELECT e FROM MonedaValores e "
                    + " WHERE (e.codcof LIKE :codigo ) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.codcof, e.fechaAlta DESC ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e);
            return new ArrayList<MonedaValores>();
        }
    }

    public MonedaValores getCotizacionDia(String codcof) {

        try {
            String sQuery = "SELECT m FROM MonedaValores m WHERE m.codcof = '" + codcof + "' "
                    + "ORDER BY m.fechaAlta desc";

            Query q = getEm().createQuery(sQuery);

//            log.log(Level.SEVERE, sQuery);
            q.setMaxResults(1);

            return (MonedaValores) q.getResultList().get(0);
        } catch (NoResultException nre) {

            return new MonedaValores();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getCotizacionDia", e);
            return new MonedaValores();
        }

    }

    public MonedaValores getMoneda(String codcof, String fecalt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
