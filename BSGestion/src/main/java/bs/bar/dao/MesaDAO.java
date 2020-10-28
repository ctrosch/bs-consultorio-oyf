/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.dao;

import bs.bar.modelo.Mesa;
import bs.global.dao.BaseDAO;
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
public class MesaDAO extends BaseDAO {

    public Mesa getMesa(String cod) {
        return getObjeto(Mesa.class, cod);
    }

    public List<Mesa> getListaByBusqueda(String txtBusqueda, Map<String, String> filtro, boolean mostrarDeBaja, int cantMax) {
        try {

            String sQuery = "SELECT e FROM Mesa e "
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
            log.log(Level.SEVERE, "getListaByBusqueda", e.getCause());
            return new ArrayList<Mesa>();
        }
    }
}
