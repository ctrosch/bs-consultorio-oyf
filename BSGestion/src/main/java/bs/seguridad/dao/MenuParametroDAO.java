/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.seguridad.dao;


import bs.global.dao.BaseDAO;
import bs.seguridad.modelo.Menu;
import bs.seguridad.modelo.MenuParametro;
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
public class MenuParametroDAO extends BaseDAO{

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    
    public MenuParametro getMenuParametro(String id) {
        return getObjeto(MenuParametro.class, id);
    }

    public List<MenuParametro> getListaByBusqueda(String txtBusqueda, String codMenu, boolean mostrarDebaja, int cantMax) {
            
        try {
            String sQuery = "SELECT e FROM MenuParametro e "
                    + "WHERE 1=1 "
                    + " AND ((e.nombre LIKE :nombre) "
                    + "  OR  (e.valor LIKE :valor) "                    
                    + "     ) "
                    + (codMenu==null ? " ": " AND e.menu.codigo = :codMenu ")
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + "ORDER BY e.id ";

            Query q = getEm().createQuery(sQuery);
            
            if (codMenu!=null){
                q.setParameter("codMenu", codMenu);
            }
            
            q.setParameter("nombre", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("valor", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
                        
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }
            
            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<MenuParametro>();
        }        
    
    }
}
