/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.dao;

import bs.educacion.modelo.Reglamento;
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
public class ReglamentoDAO extends BaseDAO {

    public Reglamento getReglamento(Integer id) {
        return getObjeto(Reglamento.class, id);
    }

    public List<Reglamento> getLista() {
        return getLista(Reglamento.class, true, -1, -1);
    }

    public List<Reglamento> getReglamentoByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {
            String sQuery = "SELECT e FROM Reglamento e "
                    + " WHERE (e.nombre LIKE :nombre) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.id ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("nombre", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getReglamentoByBusqueda", e);
            return new ArrayList<Reglamento>();
        }
    }

    public int getMaxCodigo() {

        try {
            String sQuery = "select CAST(MAX(ed_reglamento.id) AS SIGNED)+1  from ed_reglamento where id < 90000 ";

            Query q = getEm().createNativeQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getMaxCodigo", e.getMessage());
            return 0;
        }
    }
}
