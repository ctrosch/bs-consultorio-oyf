/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.dao;

import bs.global.modelo.Sucursal;
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
public class SucursalDAO extends BaseDAO {

    public Sucursal getSucursal(String codigo) {
        return getObjeto(Sucursal.class, codigo);
    }

    public List<Sucursal> getLista() {
        return getLista(Sucursal.class, true, -1, -1);
    }

    public List<Sucursal> getLista(boolean mostrarDebaja) {

        try {
            String sQuery = "SELECT e FROM Sucursal e "
                    + " WHERE "
                    + (mostrarDebaja ? " " : " e.auditoria.debaja = 'N' ")
                    + " ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getLista", e.getCause());
            return new ArrayList<Sucursal>();
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public List<Sucursal> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM Sucursal e "
                    + "WHERE 1=1 "
                    + " AND ((e.codigo LIKE :codigo) "
                    + "  OR  (e.nombre LIKE :nombre) "
                    + "     ) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("nombre", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<Sucursal>();
        }

    }
}
