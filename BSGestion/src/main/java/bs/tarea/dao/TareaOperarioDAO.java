/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tarea.dao;

import bs.global.dao.BaseDAO;
import bs.tarea.modelo.TareaOperario;
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
public class TareaOperarioDAO extends BaseDAO {

    public List<TareaOperario> getListaByBusqueda(Map<String, String> filtro, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM TareaOperario e "
                    + "WHERE 1 = 1 "                    
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro,"e", true)
                    + "ORDER BY e.tarea.fechaMovimiento DESC, e.tarea.horaInicio DESC, e.operario.nombre ";

            Query q = getEm().createQuery(sQuery);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<TareaOperario>();
        }

    }

}
