/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.facturacion.rn;

import bs.facturacion.dao.ProductoFacturacionDAO;
import bs.facturacion.vista.ProductoFacturacion;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Claudio
 */
@Stateless
public class ProductoFacturacionRN implements Serializable {

    @EJB private ProductoFacturacionDAO productoDAO;
    Map<String,String> filtro = new HashMap<String,String>();
    
    public ProductoFacturacion getProductoFacturacion(String codigo){
    
        return productoDAO.getProductoFacturacion(codigo);        
    }
    
    public ProductoFacturacion getProductoFacturacionByCodigoBarra(String codigoBarra){
    
        return productoDAO.getProductoFacturacionByCodigoBarra(codigoBarra);        
    }
    
    public List<ProductoFacturacion> getListaByBusqueda(String codTipo , String txtBusqueda){
        
        return productoDAO.getListaByBusqueda(null, codTipo, txtBusqueda,  15);
    }
    
    public List<ProductoFacturacion> getListaByBusqueda(String codTipo , String txtBusqueda, int cantMaxima){
        
        return productoDAO.getListaByBusqueda(null, codTipo, txtBusqueda,  cantMaxima);
    }
    
    public List<ProductoFacturacion> getListaByBusqueda(Map<String, String> filtro, String codTipo, String txtBusqueda){
        
        return productoDAO.getListaByBusqueda(filtro, codTipo, txtBusqueda,  50);        
    }

    public List<ProductoFacturacion> getListaByBusqueda(Map<String, String> filtro,String codTipo, String txtBusqueda, int cantMaxima){
        
        return productoDAO.getListaByBusqueda(filtro,codTipo, txtBusqueda,  cantMaxima);        
    }    
}
