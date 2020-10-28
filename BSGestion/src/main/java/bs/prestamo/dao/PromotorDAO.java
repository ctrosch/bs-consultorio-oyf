/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.dao;

import bs.global.dao.BaseDAO;
import bs.prestamo.modelo.Promotor;
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
public class PromotorDAO extends BaseDAO {

    public Promotor getPromotor(Integer id) {
        return getObjeto(Promotor.class, id);
    }

    public List<Promotor> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM Promotor e "
                    + "WHERE 1=1 "
                    + " AND (e.nombre LIKE :nombre) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + "ORDER BY e.id ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("nombre", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getPromotorByBusqueda", e.getMessage());
            return new ArrayList<Promotor>();
        }

    }

}
