/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.dao;

import bs.administracion.modelo.Vista;
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
public class VistaDAO extends BaseDAO {

    public List<Vista> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {

            String sQuery = "SELECT e FROM Vista e "
                    + " WHERE (e.codigo LIKE :codigo "
                    + " OR e.nombre LIKE :nombre "
                    + " OR e.path LIKE :path) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.nombre ";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("nombre", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("path", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e);
            return new ArrayList<Vista>();
        }
    }

    public String obtenerSiguienteCodigo(String origen) {

        try {
            String sQuery = "SELECT CONCAT(ORIGEN,'_',RIGHT(CONCAT('00000',IFNULL(max(RIGHT(codigo,5)) + 1,1)),5)) from ad_vista where ad_vista.ORIGEN = '" + origen.toUpperCase() + "' ";

            Query q = getEm().createNativeQuery(sQuery);

            return ((String) q.getSingleResult());

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "obtenerSiguienteCodigo", e.getMessage());
            return origen.toUpperCase() + "_000000";
        }
    }
}
