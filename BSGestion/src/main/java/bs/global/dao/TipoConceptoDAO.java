/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.dao;

import bs.global.modelo.TipoConcepto;
import bs.global.modelo.TipoConceptoPK;
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
public class TipoConceptoDAO extends BaseDAO {

    public TipoConcepto getTipoConcepto(String modulo, String codigo){
        
        TipoConceptoPK idPK = new TipoConceptoPK(modulo, codigo);        
        return getEm().find(TipoConcepto.class, idPK);
    }
    
    public TipoConcepto getTipoConcepto(TipoConceptoPK idPK){

        return getEm().find(TipoConcepto.class, idPK);
    }
    
           
    public List<TipoConcepto> getListaByBusqueda(String modulo,String txtBusqueda, boolean mostrarDebaja, int cantMax) {
            
        try {
            String sQuery = "SELECT e FROM TipoConcepto e "
                    + "WHERE ((e.codigo LIKE :codigo) OR  (e.descripcion LIKE :descripcion) "                                    
                    + "     ) "                   
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + (modulo==null || modulo.isEmpty() ? " ": " AND e.modulo = :modulo ")
                    + "ORDER BY e.modulo, e.orden, e.codigo ";

            Query q = getEm().createQuery(sQuery);
            
            if(modulo!=null && !modulo.isEmpty()){
                q.setParameter("modulo", modulo);
            }
            
            q.setParameter("codigo", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("descripcion", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
                                    
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getTipoConceptoByBusqueda", e.getMessage());
            return new ArrayList<TipoConcepto>();
        }        
    
    }

}
