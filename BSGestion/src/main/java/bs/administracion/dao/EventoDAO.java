/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.dao;

import bs.administracion.modelo.Evento;
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
public class EventoDAO extends BaseDAO {

    public Evento getEvento(Integer id) {
        return getObjeto(Evento.class, id);
    }

    public List<Evento> getLista() {
        return getLista(Evento.class, true, -1, -1);
    }

    public List<Evento> getEventoByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {
            String sQuery = "SELECT e FROM Evento e "
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
            log.log(Level.SEVERE, "getEventoByBusqueda", e);
            return new ArrayList<Evento>();
        }
    }

    public int getMaxCodigo() {

        try {
            String sQuery = "select CAST(MAX(ad_evento.id) AS SIGNED)+1  from ad_evento where id < 90000 ";

            Query q = getEm().createNativeQuery(sQuery);

            return ((Long) q.getSingleResult()).intValue();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getMaxCodigo", e.getMessage());
            return 0;
        }
    }
}
