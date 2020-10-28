/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.dao;

import bs.global.dao.BaseDAO;
import bs.ventas.modelo.CanalVenta;
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
public class CanalVentaDAO extends BaseDAO {

    public CanalVenta getCanalVenta(String codigo) {
        return getObjeto(CanalVenta.class, codigo);
    }

    public List<CanalVenta> getLista() {
        return getLista(CanalVenta.class, true, -1, -1);
    }

    public List<CanalVenta> getCanalVentaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {
            String sQuery = "SELECT e FROM CanalVenta e "
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
            log.log(Level.SEVERE, "getCanalVentaByBusqueda", e);
            return new ArrayList<CanalVenta>();
        }
    }
}
