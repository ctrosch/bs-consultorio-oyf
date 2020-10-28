/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.dao;

import bs.global.dao.BaseDAO;
import bs.prestamo.modelo.LineaCredito;
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
public class LineaCreditoDAO extends BaseDAO {

    public LineaCredito getLineaCredito(Integer cod) {
        return getObjeto(LineaCredito.class, cod);
    }

    public List<LineaCredito> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM LineaCredito e "
                    + "WHERE 1=1 "
                    + " AND e.descripcion LIKE :descripcion "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + "ORDER BY e.descripcion ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getLineaCreditoByBusqueda", e.getMessage());
            return new ArrayList<LineaCredito>();
        }

    }

}
