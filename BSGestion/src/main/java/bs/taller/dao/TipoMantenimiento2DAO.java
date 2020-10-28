/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.taller.dao;

import bs.global.dao.BaseDAO;
import bs.taller.modelo.TipoMantenimiento2;
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
public class TipoMantenimiento2DAO extends BaseDAO {

    public TipoMantenimiento2 getTipoMantenimiento(String cod) {
        return getObjeto(TipoMantenimiento2.class, cod);
    }
    
    
    public List<TipoMantenimiento2> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {
            
        try {
            String sQuery = "SELECT e FROM TipoMantenimiento e "
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
            e.printStackTrace();
            log.log(Level.SEVERE, "getTipoMantenimientoByBusqueda", e.getMessage());
            return new ArrayList<TipoMantenimiento2>();
        }        
    
    }

}
