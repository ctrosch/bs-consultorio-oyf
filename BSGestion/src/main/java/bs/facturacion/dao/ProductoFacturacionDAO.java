/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.dao;

import bs.facturacion.vista.ProductoFacturacion;
import bs.global.dao.BaseDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author lloubiere
 */
@Stateless
public class ProductoFacturacionDAO extends BaseDAO implements Serializable{
    
    public ProductoFacturacion getProductoFacturacion(String id) {
        return getObjeto(ProductoFacturacion.class, id);
    }

    public ProductoFacturacion getProductoFacturacion(ProductoFacturacion p){

        return getProductoFacturacion(p.getCodigo());
    }
    
    public ProductoFacturacion getProductoFacturacionByCodigoBarra(String codigoBarra) {
        
        try {            
            String sQuery = "SELECT e FROM ProductoFacturacion e WHERE e.codigoBarra = :codigoBarra ";                    
            
            Query q = getEm().createQuery(sQuery);            
                        
            return (ProductoFacturacion) q.getSingleResult();
            
        }catch (NoResultException e) {
            return null;            
        }catch (Exception e) {
            log.log(Level.SEVERE, "getProductoFacturacionByCodigoBarra", e);
            return null;
        }        
    }
    
    public List<ProductoFacturacion> getListaByBusqueda(Map<String, String> filtro, String codTipo, String txtBusqueda, int cantMax){
        
        try {            
            String sQuery = "SELECT e FROM ProductoFacturacion e "
                    + " WHERE (e.codigo LIKE :codigo "
                    + " OR e.descripcion LIKE :descripcion "
                    + " OR e.numeroParte LIKE :numeroParte"
                    + " OR e.codigoProveedor LIKE :codigoProveedor) "                    
                    + (codTipo==null || codTipo.isEmpty() ? " ": " AND e.tipoProducto.codigo = :tipoProducto ")
                    + generarStringFiltro(filtro,"e", false)
                    + " ORDER BY e.descripcion ";
            
            Query q = getEm().createQuery(sQuery);            
            q.setParameter("codigo", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("descripcion", "%"+txtBusqueda.replaceAll(" ", "%")+"%");
            q.setParameter("numeroParte", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
            q.setParameter("codigoProveedor", "%"+txtBusqueda.replaceAll(" ", "%")+"%");            
           
            if(codTipo!=null && !codTipo.isEmpty()){
                q.setParameter("tipoProducto", codTipo);
            }
            
            if(cantMax>0){
                q.setMaxResults(cantMax);
            }    
            
            return q.getResultList();            
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "getProductoFacturacionByBusqueda", e);
            return new ArrayList<ProductoFacturacion>();
        }        
    }
}
