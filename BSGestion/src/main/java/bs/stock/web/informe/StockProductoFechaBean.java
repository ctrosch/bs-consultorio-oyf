/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.web.informe;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import bs.stock.modelo.Producto;
import bs.stock.rn.ProductoRN;
import bs.stock.web.ProductoBean;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class StockProductoFechaBean extends InformeBase implements Serializable{
    
    @EJB ProductoRN productoRN;
    
    private Date fechaHasta;    
    private Producto producto;
    
    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;
            
    /**
     * Creates a new instance of ImpresionComprobanteFacturacionBean
     */
    public StockProductoFechaBean() {
    }
    
    @PostConstruct
    public void init(){
    
        fechaHasta = new Date();
        
    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {
        
        String mensaje = "";
        todoOk = true;
        
        if(fechaHasta==null){
            mensaje ="Fecha hasta no puede estar en blanco";
        }
                
        if(!mensaje.isEmpty()){
            todoOk = false;
            throw new ExcepcionGeneralSistema(mensaje);
        }        
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {
        
        parameters.put("FCHHAS", fechaHasta);
        
        if(producto!=null){
            parameters.put("ARTCOD", producto.getCodigo());           
        }else{
            parameters.put("ARTCOD", "");           
        }
                
        nombreArchivo = "ST_STOCK_PRODUCTO_FECHA";
        reporte = reporteRN.getReporte(codigoReporte);        
               
    }
        
    public void procesarProducto(){
        
        if(productoBean.getProducto()!=null){            
            
            producto = productoBean.getProducto();
            todoOk = false;
                        
        }
    }
    
    @Override
    public void resetParametros(){
        
        
        fechaHasta = new Date();        
        producto = null; 
        todoOk = false;
        muestraReporte = false;
        
    }
    
    public void onItemSelectProducto(SelectEvent event) {
        producto = (Producto) event.getObject();  
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public ProductoBean getProductoBean() {
        return productoBean;
    }

    public void setProductoBean(ProductoBean productoBean) {
        this.productoBean = productoBean;
    }
       

    
}
