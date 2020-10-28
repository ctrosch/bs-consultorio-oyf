/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.dao;

import bs.bar.modelo.Salon;
import bs.global.dao.BaseDAO;
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
public class SalonDAO extends BaseDAO {

    public Salon getSalon(String cod) {
        return getObjeto(Salon.class, cod);
    }

    public List<Salon> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {

            String sQuery = "SELECT e FROM Salon e "
                    + " WHERE (e.codigo LIKE :codigo OR e.descripcion LIKE :descripcion) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + " ORDER BY e.codigo";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e.getCause());
            return new ArrayList<Salon>();
        }
    }
}
