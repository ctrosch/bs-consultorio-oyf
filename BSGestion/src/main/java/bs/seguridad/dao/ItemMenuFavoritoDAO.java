/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.seguridad.dao;

import bs.global.dao.BaseDAO;
import bs.seguridad.modelo.ItemMenuFavorito;
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
public class ItemMenuFavoritoDAO extends BaseDAO {

    
    public ItemMenuFavorito getItemMenuReciente(int id){
        return getObjeto(ItemMenuFavorito.class, id);
    }

    public ItemMenuFavorito getItemMenuRecienteByNombre(String value) {
        return getObjeto(ItemMenuFavorito.class,"nombre",value);

    }
   
    public List<ItemMenuFavorito> getItemMenuFavoritoByUsuario(Usuario usuario) {

        try {
            String sQuery = "SELECT m FROM ItemMenuFavorito m "
                    + " WHERE m.idUsuario = :idUsuario "
                    + " AND m.menu.activo = :activo "
                    + " AND m.menu.modulo.activo = 'S' "        
                    + " ORDER BY m.orden ";
            
            Query q = (Query) getEm().createQuery(sQuery);

            q.setParameter("idUsuario", usuario.getId());
            q.setParameter("activo", "S");

            return q.getResultList();
        } catch (Exception e) {
            System.out.println("getItemMenuFavoritoByUsuario" + e.getMessage());
            return new ArrayList<ItemMenuFavorito>();
        }        
    }

 
}
