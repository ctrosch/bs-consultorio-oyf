/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.dao;

import bs.contabilidad.modelo.CentroCosto;
import bs.contabilidad.modelo.SubCuenta;
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
public class SubCuentaDAO extends BaseDAO {

    public SubCuenta getSubCuenta(String id) {
        return getObjeto(SubCuenta.class, id);
    }

    public List<SubCuenta> getListaByBusqueda(Map<String, String> filtro, CentroCosto centroCosto, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {
        try {

            String sQuery = "SELECT e FROM SubCuenta e "
                    + " WHERE (e.codigo LIKE :codigo OR e.descripcion LIKE :descripcion) "
                    + (mostrarDeBaja ? " " : " AND e.auditoria.debaja = 'N' ")
                    + (centroCosto == null ? " " : " AND e.centroCosto.codigo = '" + centroCosto.getCodigo() + "' ")
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.centroCosto.codigo,e.codigo";

            Query q = getEm().createQuery(sQuery);
            q.setParameter("codigo", "%" + txtBusqueda.replaceAll(" ", "%") + "%");
            q.setParameter("descripcion", "%" + txtBusqueda.replaceAll(" ", "%") + "%");

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getListaByBusqueda", e.getCause());
            return new ArrayList<SubCuenta>();
        }
    }

}
