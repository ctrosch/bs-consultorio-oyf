/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.tesoreria.dao;

import bs.global.dao.BaseDAO;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
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
public class ItemMovimientoTesoreriaDAO extends BaseDAO{

   
    /**
     *      
     * @param filtro     
     * @param cantMax
     * @return 
     */
    public List<ItemMovimientoTesoreria> getListaByBusqueda(Map<String, String> filtro, int cantMax){
        
        try {
            String sQuery = "SELECT e FROM ItemMovimientoTesoreria e "
                    + " WHERE e.itemsCentroCosto IS NOT EMPTY "
                    + " AND e.movimiento.movimientoReversion IS NULL "                    
                    + generarStringFiltro(filtro,"e", false)
                    + " ORDER BY e.id ";

            Query q = getEm().createQuery(sQuery);
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<ItemMovimientoTesoreria>();
        }        
    }
}
