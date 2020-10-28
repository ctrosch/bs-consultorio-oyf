/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.dao;

import bs.global.dao.BaseDAO;
import bs.stock.modelo.GrupoStock;
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
public class GrupoStockDAO extends BaseDAO {

    public GrupoStock getFamilia(Integer id) {
        return getObjeto(GrupoStock.class, id);
    }

    public List<GrupoStock> getLista() {
        return getLista(GrupoStock.class, true, -1, -1);
    }

    public List<GrupoStock> getLista(int maxResults, int firstResult) {
        return getLista(GrupoStock.class, false, maxResults, firstResult);
    }

    public List<GrupoStock> getLista(boolean mostrarDebaja) {

        try {
            String sQuery = "SELECT e FROM GrupoStock e "
                    + "WHERE 1=1 "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + "ORDER BY e.descripcion ";

            Query q = getEm().createQuery(sQuery);
            return q.getResultList();

        } catch (Exception e) {

            log.log(Level.SEVERE, "getLista", e.getCause());

            return new ArrayList<GrupoStock>();
        }
    }

    public List<GrupoStock> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {

            String sQuery = "SELECT e FROM GrupoStock e "
                    + " WHERE (e.descripcion LIKE :descripcion) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.descripcion";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getFamiliaByBusqueda", e.getCause());
            return new ArrayList<GrupoStock>();
        }
    }
}
