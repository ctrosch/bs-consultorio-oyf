/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.dao;

import bs.global.modelo.Mensaje;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author Agustin
 */
@Stateless
public class MensajeDAO extends BaseDAO implements Serializable {

    public List<Mensaje> getLista(boolean mostrarDebaja) {

        try {
            String sQuery = "SELECT e FROM Mensaje e "
                    + "WHERE 1=1"
                    + (mostrarDebaja ? " " : " e.auditoria.debaja = 'N' ")
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getLista", e.getCause());
            return new ArrayList<Mensaje>();
        }
    }

    public List<Mensaje> getListaByBusqueda(String txtBusqueda, Map<String, String> filtro, List<String> orden, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM Mensaje e "
                    + "WHERE 1=1 "
                    + " AND ((e.remitente.nombre LIKE :idremi) "
                    + "  OR  (e.destinatario.nombre LIKE :iddest) "
                    + "     ) "
                    + generarStringFiltro(filtro, "e", false)
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ");
//                    + generarStringOrden(orden,"e", "fecha");

            Query q = getEm().createQuery(sQuery);

            q.setParameter("idremi", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("iddest", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getMensajeByBusqueda", e.getMessage());
            return new ArrayList<Mensaje>();
        }

    }
}
