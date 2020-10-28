/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.dao;

import bs.global.dao.BaseDAO;
import bs.salud.modelo.EstadoSalud;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author Guillermo
 */
@Stateless
public class EstadoSaludDAO extends BaseDAO {

    public EstadoSalud getEstadoSalud(String cod) {
        return getObjeto(EstadoSalud.class, cod);
    }

    public List<EstadoSalud> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM EstadoSalud e "
                    + "WHERE 1=1 "
                    + " AND ((e.codigo LIKE :codigo) "
                    + "  OR  (e.descripcion LIKE :descripcion) "
                    + "     ) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getEstadoSaludByBusqueda", e.getMessage());
            return new ArrayList<EstadoSalud>();
        }

    }

}
