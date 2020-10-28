/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.ventas.web.informe;

import bs.entidad.modelo.EntidadComercial;
import bs.entidad.web.EntidadComercialBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import bs.stock.modelo.Producto;
import bs.stock.web.ProductoBean;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class VentaComprobanteClienteProducto extends InformeBase implements Serializable{
    
    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;
    
    @ManagedProperty(value = "#{entidadComercialBean}")
    protected EntidadComercialBean entidadComercialBean;
    
    private Date fechaDesde;
    private Date fechaHasta;    
    private Producto producto;
    private EntidadComercial cliente;
    
    /**
     * Creates a new instance of ImpresionComprobanteFacturacionBean
     */
    public VentaComprobanteClienteProducto() {
    }
    
    @PostConstruct
    @Override
    public void init(){
        
        super.init();
        fechaDesde = new Date();
        fechaHasta = new Date();
             
    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {
        
        String mensaje = "";
        todoOk = true;
       
        if(fechaDesde==null){
            mensaje ="Fecha desde no puede estar en blanco";
        }
        
        if(fechaHasta==null){
            mensaje ="Fecha hasta no puede estar en blanco";
        }
        
        if(fechaHasta.before(fechaDesde)){
            mensaje ="Fecha desde es mayor a fecha hasta";
        }
        
        if(!mensaje.isEmpty()){
            todoOk = false;
            throw new ExcepcionGeneralSistema(mensaje);
        }        
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {
                
        parameters.put("FCHMOV_DESDE", fechaDesde);
        parameters.put("FCHMOV_HASTA", fechaHasta);
        
        if(cliente!=null){ 
            parameters.put("NROCTA", cliente.getNrocta());        
        }else{
            parameters.put("NROCTA", "");        
        }
        
        if(producto!=null){
            parameters.put("ARTCOD", producto.getCodigo());           
        }else{
            parameters.put("ARTCOD", "");           
        }
        
        nombreArchivo = "VTA_COMPROBANTE_PRODUCTO_CLIENTE";
        reporte = reporteRN.getReporte(codigoReporte);        
               
    }
    
    public void procesarCliente(){
        
        if(entidadComercialBean.getEntidad()!=null){            
            
            cliente = entidadComercialBean.getEntidad();                  
            todoOk = false;                        
        }
    }
    
    public void procesarProducto(){
        
        if(productoBean.getProducto()!=null){            
            
            producto = productoBean.getProducto();
            todoOk = false;
                        
        }
    }
    
    @Override
    public void resetParametros(){
        
        fechaDesde = new Date();
        fechaHasta = new Date(); 
        cliente = null;
        producto = null; 
        todoOk = false;
        muestraReporte = false;
                
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public ProductoBean getProductoBean() {
        return productoBean;
    }

    public void setProductoBean(ProductoBean productoBean) {
        this.productoBean = productoBean;
    }

    public EntidadComercialBean getEntidadComercialBean() {
        return entidadComercialBean;
    }

    public void setEntidadComercialBean(EntidadComercialBean entidadComercialBean) {
        this.entidadComercialBean = entidadComercialBean;
    }
    
    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
    }
    
    
    
}
