/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.dao;

import bs.global.dao.BaseDAO;
import bs.taller.modelo.Tecnico;
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
public class TecnicoDAO extends BaseDAO {

    public Tecnico getTecnico(Integer cod) {
        return getObjeto(Tecnico.class, cod);
    }

    public List<Tecnico> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM Tecnico e "
                    + "WHERE 1=1 "
                    + " AND e.nombre LIKE :nombre "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);

//            q.setParameter("codigo", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("nombre", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getTecnicoByBusqueda", e.getMessage());
            return new ArrayList<Tecnico>();
        }

    }

}
