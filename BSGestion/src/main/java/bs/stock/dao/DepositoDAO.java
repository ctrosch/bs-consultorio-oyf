/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.dao;

import bs.global.dao.BaseDAO;
import bs.stock.modelo.Deposito;
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
public class DepositoDAO extends BaseDAO {

    public Deposito getDeposito(String id) {
        return getObjeto(Deposito.class, id);
    }

    public List<Deposito> getLista() {
        return getLista(Deposito.class, true, -1, -1);
    }

    public List<Deposito> getLista(int maxResults, int firstResult) {
        return getLista(Deposito.class, false, maxResults, firstResult);
    }

    public List<Deposito> getLista(boolean mostrarDebaja) {

        try {
            String sQuery = "SELECT e FROM Deposito e "
                    + "WHERE 1=1 "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            return q.getResultList();

        } catch (Exception e) {

            log.log(Level.SEVERE, "getLista", e.getCause());

            return new ArrayList<Deposito>();
        }
    }

    public List<Deposito> getDepositoByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {

            String sQuery = "SELECT e FROM Deposito e "
                    + " WHERE (e.codigo LIKE :codigo OR e.descripcion LIKE :descripcion) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.codigo";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getDepositoByBusqueda", e.getCause());
            return new ArrayList<Deposito>();
        }
    }

}
