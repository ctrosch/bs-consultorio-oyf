/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.web.informe;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.Producto;
import bs.stock.modelo.Stock;
import bs.stock.rn.StockRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class ConsultaStock extends InformeBase implements Serializable {

    @EJB private StockRN stockRN;
    
    private Producto producto;
    private Deposito deposito;
    private List<Stock> stock;
    private Stock itemStock;
    
    /** Creates a new instance of StockPorProductoBean */
    public ConsultaStock() {

    }

    @PostConstruct
    public void init(){        
        stock = new ArrayList<Stock>();
    }

    public void verStockPorDeposito(Deposito d){
        stock = stockRN.getStockByDeposito(d);
    }
    
    public void onSelectDeposito(SelectEvent event) {  
        
        deposito = (Deposito) event.getObject();
        
        if(deposito==null){
           JsfUtil.addErrorMessage("Seleccione un deposito para ver el stock");       
        }
        stock = stockRN.getStockByDeposito(deposito,true);
    }
        
    public void onSelectProducto(SelectEvent event) {  
        
        producto = (Producto) event.getObject();
        
        if(producto==null){
           JsfUtil.addErrorMessage("Seleccione un producto para ver el stock");       
        }
        stock = stockRN.getStockByProducto(producto, true);
    }
    
    public void onSelectStock(SelectEvent event) {  
        
        itemStock = (Stock) event.getObject();
        
    }
    
    public void verStockPorProductoDeposito(Producto p, Deposito d){
        
        if(p==null || d==null) return;
                
        producto = p;
        deposito = d;        
        stock = stockRN.getStockByProductoDeposito(p, d);        
    }    
    
    public void verStockPorProducto(Producto p){
        
        if(p==null) return;
                
        producto = p;        
        stock = stockRN.getStockByProducto(p,true);
    }   
    
    //_------------------------------------------------------------------------

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public List<Stock> getStock() {
        return stock;
    }

    public void setStock(List<Stock> stock) {
        this.stock = stock;
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

    public Stock getItemStock() {
        return itemStock;
    }

    public void setItemStock(Stock itemStock) {
        this.itemStock = itemStock;
    }
    

    
    
}
