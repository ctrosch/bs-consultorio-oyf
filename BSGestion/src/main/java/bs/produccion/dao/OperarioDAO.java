/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.produccion.dao;

import bs.global.dao.BaseDAO;
import bs.produccion.modelo.Operario;
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
public class OperarioDAO extends BaseDAO {

    public Operario getOperario(String cod) {
        return getObjeto(Operario.class, cod);
    }
    
    
    public List<Operario> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {
            
        try {
            String sQuery = "SELECT e FROM Operario e "
                    + "WHERE 1=1 "
                    + " AND ((e.codigo LIKE :codigo) "
                    + "  OR  (e.nombre LIKE :nombre) "                                      
                    + "     ) "
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + "ORDER BY e.codigo ";

            Query q = getEm().createQuery(sQuery);
            
            q.setParameter("codigo", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("nombre", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
                        
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getOperarioByBusqueda", e.getMessage());
            return new ArrayList<Operario>();
        }        
    
    }

}
