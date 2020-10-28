/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.dao;

import bs.bar.modelo.EstadoBar;
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
public class EstadoBarDAO extends BaseDAO {

    public EstadoBar getEstadoBar(String cod) {
        return getObjeto(EstadoBar.class, cod);
    }

    public List<EstadoBar> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {

            String sQuery = "SELECT e FROM EstadoBar e "
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
            return new ArrayList<EstadoBar>();
        }
    }
}
