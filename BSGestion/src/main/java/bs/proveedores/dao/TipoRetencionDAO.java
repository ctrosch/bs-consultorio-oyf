/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.dao;

import bs.global.dao.BaseDAO;
import bs.proveedores.modelo.TipoRetencion;
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
public class TipoRetencionDAO extends BaseDAO {

    public TipoRetencion getTipoRetencion(String codigo) {
        return getEm().find(TipoRetencion.class, codigo);
    }

    public List<TipoRetencion> getLista() {
        return getLista(TipoRetencion.class, true, -1, -1);
    }

    public List<TipoRetencion> getLista(boolean mostrarDebaja) {

        try {
            String sQuery = "SELECT e FROM TipoRetencion e "
                    + " WHERE "
                    + (mostrarDebaja ? " " : " e.auditoria.debaja = 'N' ")
                    + " ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getLista", e.getCause());
            return new ArrayList<TipoRetencion>();
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public List<TipoRetencion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM TipoRetencion e "
                    + "WHERE 1=1 "
                    + " AND ((e.codigo LIKE :codigo) "
                    + "  OR  (e.descripcion LIKE :descripcion) "
                    + "     ) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro, "e", false)
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);

            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<TipoRetencion>();
        }

    }
}
