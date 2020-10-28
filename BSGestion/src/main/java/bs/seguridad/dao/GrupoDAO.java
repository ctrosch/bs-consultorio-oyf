/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.seguridad.dao;

import bs.global.dao.BaseDAO;
import bs.seguridad.modelo.Grupo;
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
public class GrupoDAO extends BaseDAO {

    public void borrar(Grupo m) throws Exception {
        eliminar(getObjeto(Grupo.class, m.getCodigo()));
    }

    public Grupo getGrupo(String id) {
        return getObjeto(Grupo.class, id);
    }

    public List<Grupo> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {

            String sQuery = "SELECT e FROM Grupo e "
                    + " WHERE (e.codigo LIKE :codigo OR e.descripcion LIKE :descrp) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.codigo";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descrp", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e.getCause());
            return new ArrayList<Grupo>();
        }
    }

}
