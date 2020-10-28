/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.dao;

import bs.educacion.modelo.EstadoEducacion;
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
public class EstadoEducacionDAO extends BaseDAO {

    public EstadoEducacion getEstadoEducacion(String cod) {
        return getObjeto(EstadoEducacion.class, cod);
    }

    public List<EstadoEducacion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM EstadoEducacion e "
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
            log.log(Level.SEVERE, "getEstadoEducacionByBusqueda", e.getMessage());
            return new ArrayList<EstadoEducacion>();
        }

    }

}
