/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.dao;

import bs.global.modelo.TipoComprobante;
import bs.global.modelo.TipoComprobantePK;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author ctrosch
 */
@Stateless
public class TipoComprobanteDAO extends BaseDAO {

    public TipoComprobante getTipoComprobante(String modulo, String codigo) {

        TipoComprobantePK idPK = new TipoComprobantePK(modulo, codigo);
        return getEm().find(TipoComprobante.class, idPK);
    }

    public TipoComprobante getTipoComprobante(TipoComprobantePK idPK) {

        return getEm().find(TipoComprobante.class, idPK);
    }

    public List<TipoComprobante> getListaByBusqueda(String modulo, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        try {
            String sQuery = "SELECT e FROM TipoComprobante e "
                    + "WHERE ((e.codigo LIKE :codigo) OR  (e.descripcion LIKE :descripcion)) "
                    + (mostrarDebaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + (modulo == null || modulo.isEmpty() ? " " : " AND e.modulo = :modulo ")
                    + "ORDER BY e.modulo, e.codigo ";

            Query q = getEm().createQuery(sQuery);

            if (modulo != null && !modulo.isEmpty()) {
                q.setParameter("modulo", modulo);
            }

            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getTipoComprobanteByBusqueda", e.getCause());
            return new ArrayList<TipoComprobante>();
        }

    }

}
