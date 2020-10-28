/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.seguridad.dao;
import bs.global.dao.BaseDAO;
import bs.seguridad.modelo.EstadoUsuario;
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
public class EstadoUsuarioDAO extends BaseDAO{
    
    public EstadoUsuario getEstado(Integer id) {
        return getObjeto(EstadoUsuario.class, id);
    }   
    
    public List<EstadoUsuario> getLista() {
        return getLista(EstadoUsuario.class, true, -1, -1);
    }

    public List<EstadoUsuario> getLista(int maxResults, int firstResult) {
        return getLista(EstadoUsuario.class, false, maxResults, firstResult);
    }

    public int getCantidadRegistros(){
        return (int) getCantidadRegistros(EstadoUsuario.class);
    }

    public EstadoUsuario getEstadoByDescripcion(String descripcion) {

        return getObjeto(EstadoUsuario.class, "descripcion", descripcion);
        
    }
    
    public List<EstadoUsuario> getLista(boolean mostrarDebaja) {
        
        try {
            String sQuery = "SELECT e FROM EstadoUsuario e "
                    + "WHERE 1=1"
                    + (mostrarDebaja ? " ": " e.auditoria.debaja = 'N' ")
                    + "ORDER BY e.id ";

            Query q = getEm().createQuery(sQuery);
            return q.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, "getLista", e.getCause());
            return new ArrayList<EstadoUsuario>();
        } 
    }
    
    public List<EstadoUsuario> getEstadoByBusqueda(String txtBusqueda, boolean mostrarDeBaja,int cantMax) {
        try {
            
            String sQuery = "SELECT e FROM EstadoUsuario e "
                    + " WHERE e.descripcion LIKE :descripcion "
                    + (mostrarDeBaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + " ORDER BY e.id";
            
            Query q = getEm().createQuery(sQuery);            
            //q.setParameter("id", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("descripcion", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
          
            return q.getResultList();            
            
        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "ERROR:getEstadoByBusqueda", e);
            return new ArrayList<EstadoUsuario>();
        }  
    }
  
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
 
}
