/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.seguridad.dao;
import bs.global.dao.BaseDAO;
import bs.seguridad.modelo.TipoUsuario;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.Query;



/**
 *
 * @author lucas
 */
@Stateless
public class TipoUsuarioDAO extends BaseDAO{

   
    public TipoUsuario getTipo(Integer id) {
        return getObjeto(TipoUsuario.class, id);
    }      
  
    public List<TipoUsuario> getLista() {
        return getLista(TipoUsuario.class, true, -1, -1);
    }
  
    public List<TipoUsuario> getLista(int maxResults, int firstResult) {
        return getLista(TipoUsuario.class, false, maxResults, firstResult);
    }

    public int getCantidadRegistros() {
        return (int) getCantidadRegistros(TipoUsuario.class);
    }

    public TipoUsuario getTipoByDescripcion(String descripcion) {

        return getObjeto(TipoUsuario.class, "descripcion", descripcion);
        
    }
    
    public List<TipoUsuario> getLista(boolean mostrarDebaja) {
        
        try {
            String sQuery = "SELECT e FROM TipoUsuario e "
                    + "WHERE 1=1"
                    + (mostrarDebaja ? " ": " e.auditoria.debaja = 'N' ")
                    + "ORDER BY e.id ";

            Query q = getEm().createQuery(sQuery);
            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getLista", e.getCause());
            return new ArrayList<TipoUsuario>();
        } 
    }
    
    public List<TipoUsuario> getTipoByBusqueda(String txtBusqueda, boolean mostrarDeBaja,int cantMax) {
        try {
            
            String sQuery = "SELECT e FROM TipoUsuario e "
                    + " WHERE e.descripcion LIKE :descripcion "
                    + (mostrarDeBaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + " ORDER BY e.id";
            
            Query q = getEm().createQuery(sQuery);            
//            q.setParameter("id", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("descripcion", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
          
            return q.getResultList();            
            
        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "ERROR:getTipoByBusqueda", e.getMessage());
            return new ArrayList<TipoUsuario>();
        }  
    }



  
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
 
}
