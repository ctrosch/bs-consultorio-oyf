/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.web.informe;

import bs.facturacion.vista.ProductoFacturacion;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.Producto;
import bs.stock.rn.ProductoRN;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class ConsultaAtributosBean extends InformeBase implements Serializable {

    @EJB private ProductoRN productoRN;
    
    private Producto producto;
    
    /** Creates a new instance of StockPorProductoBean */
    public ConsultaAtributosBean() {

    }

    public void verStockPorDeposito(Deposito d){
        
    }
    
    public void verAtributosPorProducto(Producto p){
        
        if(p==null) return;                
        producto = p;        
    }    
    
    public void verAtributosPorProducto(ProductoFacturacion p){
        
        if(p==null) return;                
        producto = productoRN.getProducto(p.getCodigo());
    }    
    
    
    //_------------------------------------------------------------------------

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetParametros() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
