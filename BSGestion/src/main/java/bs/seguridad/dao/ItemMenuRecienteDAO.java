/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.seguridad.dao;

import bs.global.dao.BaseDAO;
import bs.seguridad.modelo.ItemMenuReciente;
import bs.seguridad.modelo.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author Claudio
 */
@Stateless
public class ItemMenuRecienteDAO extends BaseDAO {

    
    public ItemMenuReciente getItemMenuReciente(int id){
        return getObjeto(ItemMenuReciente.class, id);
    }

    public ItemMenuReciente getItemMenuRecienteByNombre(String value) {
        return getObjeto(ItemMenuReciente.class,"nombre",value);

    }
    
    public List<ItemMenuReciente> getItemMenuRecienteByUsuario(Usuario usuario) {

        try {
            String sQuery = "SELECT m FROM ItemMenuReciente m "
                    + " WHERE m.idUsuario = :idUsuario "
                    + " AND m.menu.activo = :activo "
                    + " AND m.menu.modulo.activo = 'S' "        
                    + " ORDER BY m.orden ";
            
            Query q = (Query) getEm().createQuery(sQuery);

            q.setParameter("idUsuario", usuario.getId());
            q.setParameter("activo", "S");
            
            q.setMaxResults(15);

            return q.getResultList();
        } catch (Exception e) {
            System.out.println("getItemMenuRecienteByUsuario" + e.getMessage());
            return new ArrayList<ItemMenuReciente>();
        }        
    }
    
 
}
