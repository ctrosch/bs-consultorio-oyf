/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.dao;

import bs.administracion.modelo.CorreoElectronico;
import bs.global.dao.BaseDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class CorreoElectronicoDAO extends BaseDAO {

    public List<CorreoElectronico> getListaByBusqueda(String txtBusqueda, String enviado, int cantMax) {
        try {

            String sQuery = "SELECT e FROM CorreoElectronico e "
                    + " WHERE (e.destinatarios LIKE :destinatarios OR e.asunto LIKE :asunto) "
                    + (enviado.equals("S") ? " AND e.enviado = 'S' " : " ")
                    + " ORDER BY e.fechaEnvio DESC";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("destinatarios", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("asunto", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (NoResultException e) {
            return new ArrayList<CorreoElectronico>();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getCorreoElectronicoByBusqueda", e.getCause());
            return new ArrayList<CorreoElectronico>();
        }
    }

}
