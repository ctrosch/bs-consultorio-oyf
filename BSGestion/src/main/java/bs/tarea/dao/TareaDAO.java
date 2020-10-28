/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tarea.dao;

import bs.global.dao.BaseDAO;
import bs.produccion.modelo.Operario;
import bs.tarea.modelo.Tarea;
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
public class TareaDAO extends BaseDAO {

    public Tarea getTarea(Integer id) {
        return getObjeto(Tarea.class, id);
    }

    public List<Tarea> getListaByBusqueda(Operario operario, Map<String, String> filtro, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM Tarea e "
                    + "WHERE 1=1 "
                    + generarStringFiltro(filtro, "e", false)
                    + (operario == null ? " " : " AND EXISTS(SELECT u FROM TareaOperario u WHERE u.tarea.id = e.id and u.operario.codigo =:codope) ")
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + "ORDER BY e.fechaMovimiento DESC, e.numeroFormulario DESC ";

            Query q = getEm().createQuery(sQuery);

            if (operario != null) {
                q.setParameter("codope", operario.getCodigo());
            }
//            q.setParameter("nombre", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getTareaByBusqueda", e.getMessage());
            return new ArrayList<Tarea>();
        }
    }

    public List<Tarea> getListaByBusqueda(Operario operario, String estado, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM Tarea e "
                    + "WHERE 1=1 "
                    + (estado == null || estado.isEmpty() ? " " : " AND e.estado= :estado ")
                    + (operario == null ? " " : " AND EXISTS(SELECT u FROM TareaOperario u WHERE u.tarea.id = e.id and u.operario.codigo =:codope) ")
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + "ORDER BY e.horaInicio DESC, e.fechaMovimiento DESC,  e.numeroFormulario DESC ";

            Query q = getEm().createQuery(sQuery);

            if (estado != null && !estado.isEmpty()) {
                q.setParameter("estado", estado);
            }

            if (operario != null) {
                q.setParameter("codope", operario.getCodigo());
            }

//            q.setParameter("nombre", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getTareaByBusqueda", e.getMessage());
            return new ArrayList<Tarea>();
        }
    }

}
