/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.dao;

import bs.educacion.modelo.AreaEducacion;
import bs.educacion.modelo.ModalidadCursado;
import bs.global.dao.BaseDAO;
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
public class ModalidadCursadoDAO extends BaseDAO {

    public ModalidadCursado getModalidadCursado(String codigo) {
        return getObjeto(ModalidadCursado.class, codigo);
    }

    public List<ModalidadCursado> getLista() {
        return getLista(AreaEducacion.class, true, -1, -1);
    }

    public List<ModalidadCursado> getModalidadCursadoByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {
            String sQuery = "SELECT e FROM ModalidadCursado e "
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
            log.log(Level.SEVERE, "getModalidadCursadoByBusqueda", e);
            return new ArrayList<ModalidadCursado>();
        }
    }
}
