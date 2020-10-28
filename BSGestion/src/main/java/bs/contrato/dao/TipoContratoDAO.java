/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contrato.dao;

import bs.contrato.modelo.TipoContrato;
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
public class TipoContratoDAO extends BaseDAO {

    public TipoContrato getTipoContrato(String codigo) {
        return getObjeto(TipoContrato.class, codigo);
    }

    public List<TipoContrato> getLista() {
        return getLista(TipoContrato.class, true, -1, -1);
    }

    public List<TipoContrato> getTipoContratoByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {
            String sQuery = "SELECT e FROM TipoContrato e "
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
            log.log(Level.SEVERE, "getTipoContratoByBusqueda", e);
            return new ArrayList<TipoContrato>();
        }
    }
}
