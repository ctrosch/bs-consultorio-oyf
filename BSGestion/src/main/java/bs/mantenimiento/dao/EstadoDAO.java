/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.dao;

import bs.global.dao.BaseDAO;
import bs.mantenimiento.modelo.Estado;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author GUILLERMO
 */
@Stateless
public class EstadoDAO extends BaseDAO {

    public Estado getEstado(String codigo) {
        return getObjeto(Estado.class, codigo);
    }

    public List<Estado> getLista() {
        return getLista(Estado.class, true, -1, -1);
    }

    public List<Estado> getEstadoByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {
            String sQuery = "SELECT e FROM Estado e "
                    + " WHERE (e.descripcion LIKE :descripcion "
                    + " OR e.codigo LIKE :codigo) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getEstadoByBusqueda", e);
            return new ArrayList<Estado>();
        }
    }
}
