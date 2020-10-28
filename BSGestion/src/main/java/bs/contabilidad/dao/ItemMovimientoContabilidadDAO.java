/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.dao;

import bs.contabilidad.modelo.ItemMovimientoContabilidad;
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
public class ItemMovimientoContabilidadDAO extends BaseDAO {

    /**
     *
     * @param filtro
     * @param cantMax
     * @return
     */
    public List<ItemMovimientoContabilidad> getListaByBusqueda(Map<String, String> filtro, int cantMax) {

        try {
            String sQuery = "SELECT e FROM ItemMovimientoContabilidad e "
                    + " WHERE e.itemsCentroCosto IS NOT EMPTY "
                    + " AND e.movimiento.movimientoReversion IS NULL "
                    + generarStringFiltro(filtro, "e", false)
                    + " ORDER BY e.id ";

            Query q = getEm().createQuery(sQuery);

            if (cantMax > 0) {
                q.setMaxResults(cantMax);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<ItemMovimientoContabilidad>();
        }
    }
}
