/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.dao;

import bs.global.modelo.Periodo;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class PeriodoDAO extends BaseDAO {

    public Periodo getPeriodo(Integer id) {
        return getObjeto(Periodo.class, id);
    }

    public List<Periodo> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM Periodo e "
                    + "WHERE 1=1 "
                    + " AND e.descripcion LIKE :descripcion "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + "ORDER BY e.id ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getPeriodoByBusqueda", e.getMessage());
            return new ArrayList<Periodo>();
        }

    }

}
