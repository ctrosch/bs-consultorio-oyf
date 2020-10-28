/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.produccion.dao;

import bs.global.dao.BaseDAO;
import bs.produccion.modelo.DepartamentoProduccion;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class DepartamentoProduccionDAO extends BaseDAO {

    
    public DepartamentoProduccion getDepartamentoProduccion(String codigo) {
        return getObjeto(DepartamentoProduccion.class, codigo);
    }
 

    public List<DepartamentoProduccion> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {
            
        try {
            String sQuery = "SELECT e FROM DepartamentoProduccion e "
                    + "WHERE 1=1 "
                    + " AND ((e.codigo LIKE :codigo) "
                    + "  OR  (e.descripcion LIKE :descripcion) "                                      
                    + "     ) "
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            
            q.setParameter("codigo", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("descripcion", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
                        
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {            
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<DepartamentoProduccion>();
        }        
    
    }

}
