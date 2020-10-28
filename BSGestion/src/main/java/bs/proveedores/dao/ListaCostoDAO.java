/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.proveedores.dao;

import bs.global.dao.BaseDAO;
import bs.proveedores.modelo.ItemListaPrecioCosto;
import bs.proveedores.modelo.ListaCosto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;


/**
 *
 * @author ctrosch
 */
@Stateless
public class ListaCostoDAO extends BaseDAO{
    
    public List<ListaCosto> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax){
        
        try {
            String sQuery = "SELECT e FROM ListaCosto e "
                    + "WHERE "
                    + "      ((e.codigo LIKE :codigo) "
                    + "  OR  (e.descripcion LIKE :descripcion ) "                    
                    + "     ) "                    
                    + (mostrarDebaja ? " ": " AND e.auditoria.debaja = 'N' ")
                    + generarStringFiltro(filtro,"e", false)
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
            log.log(Level.SEVERE, "getListaByBusqueda", e.getMessage());
            return new ArrayList<ListaCosto>();
        }        
    }
    
    public List<ItemListaPrecioCosto> getItemsPrecioByFiltro(Map<String, String> filtro) {
        try {

            String sQuery = "SELECT p FROM ItemListaPrecioCosto p ";
            sQuery += generarStringFiltro(filtro,"p", true);
            sQuery += " ORDER BY p.artcod";

//            System.err.println(sQuery);
            Query q = getEm().createQuery(sQuery);            
            q.setMaxResults(20);
            
            return q.getResultList();

        } catch (Exception e) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "getItemsPrecioByFiltro", e);  
            return new ArrayList<ItemListaPrecioCosto>();
        }
    }
    
    public BigDecimal getPrecioByProducto(String codLis,String codigo) {
        try {

            String sQuery = "SELECT p.precio FROM ItemListaPrecioCosto p "
                    + " WHERE p.auditoria.debaja = 'N' "
                    + " AND p.codlis = :codLis "
                    + " AND p.fechaVigencia <= :fechaVigencia "
                    + " AND p.producto.codigo =:codigo "                    
                    + " ORDER BY p.fechaVigencia DESC";

            Query q = getEm().createQuery(sQuery);            

            q.setMaxResults(1);
            
            q.setParameter("codLis", codLis);
            q.setParameter("fechaVigencia", new Date());            
            q.setParameter("codigo", codigo);            
            
            return (BigDecimal) q.getSingleResult();

        }catch (NoResultException nre){ 
            return BigDecimal.ZERO;
        }catch (Exception e) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "getPrecioByProducto", e);  
            return BigDecimal.ZERO;
        }
    }
    
    public ItemListaPrecioCosto getItemsPrecioByProducto(String codLis,String codigo) {
        try {

            String sQuery = "SELECT p FROM ItemListaPrecioCosto p "
                    + " WHERE p.auditoria.debaja = 'N' "
                    + " AND p.codlis = :codLis "
                    + " AND p.fechaVigencia <= :fechaVigencia "
                    + " AND p.producto.codigo =:codigo "                    
                    + " ORDER BY p.fechaVigencia DESC";

            Query q = getEm().createQuery(sQuery);    
            
            q.setMaxResults(1);

            q.setParameter("codLis", codLis);
            q.setParameter("fechaVigencia", new Date());            
            q.setParameter("codigo", codigo);            
            
            return (ItemListaPrecioCosto) q.getSingleResult();

        }catch (javax.persistence.NoResultException nre){    
            return null;
        }catch (Exception e) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "getItemsPrecioByProducto", e);  
            return null;
        }
    }
    
}
