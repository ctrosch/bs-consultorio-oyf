/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.compra.web.informe;

import bs.compra.modelo.CodigoCircuitoCompra;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.rn.EntidadRN;
import bs.entidad.web.EntidadComercialBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
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
public class PendienteCompraComprobante extends InformeBase implements Serializable{
    
    @EJB EntidadRN entidadRN;
    @EJB ProductoRN productoRN;
            
    
    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;
    
    @ManagedProperty(value = "#{entidadComercialBean}")
    protected EntidadComercialBean entidadComercialBean;
    
    private CodigoCircuitoCompra circuitoDesde;
    private CodigoCircuitoCompra circuitoHasta;
    private Date fechaDesde;
    private Date fechaHasta;    
    private Producto producto;
    private EntidadComercial proveedor;
            
    /**
     * Creates a new instance of ImpresionComprobanteCompraBean
     */
    public PendienteCompraComprobante() {
    }
    
    @PostConstruct
    public void init(){
        
        fechaDesde = new Date();
        fechaHasta = new Date();
        
    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {
        
        String mensaje = "";
        todoOk = true;
        
        if(circuitoDesde==null){
            mensaje = "Seleccione el circuito desde";
        }
        
        if(circuitoHasta==null){
            mensaje = "Seleccione el circuito desde";
        }
        
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
        
        parameters.put("CIRDES", circuitoDesde.getCodigo());
        parameters.put("CIRHAS", circuitoHasta.getCodigo());
        parameters.put("FCHDES", fechaDesde);
        parameters.put("FCHHAS", fechaHasta);
        
        if(proveedor!=null){ 
            parameters.put("NROCTA", proveedor.getNrocta());        
        }else{
            parameters.put("NROCTA", "");        
        }
        
        if(producto!=null){
            parameters.put("ARTCOD", producto.getCodigo());           
        }else{
            parameters.put("ARTCOD", "");           
        }
        
        nombreArchivo = "PendienteComprobante_"+JsfUtil.getCadenaAlfanumAleatoria(6);
        reporte = reporteRN.getReporte(codigoReporte);
        //pathReporte = "/facturacion/informe/PENCOM.jasper";
               
    }
        
    public void procesarProveedor(){
        
        if(entidadComercialBean.getEntidad()!=null){            
            
            proveedor = entidadComercialBean.getEntidad();                  
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
        circuitoDesde = null;
        circuitoHasta = null;
        proveedor = null;
        producto = null; 
        todoOk = false;
        muestraReporte = false;
    }
  
    
    public void onItemSelectCircuitoDesde(SelectEvent event) {
        circuitoHasta = (CodigoCircuitoCompra) event.getObject();  
    }
    
    public void onItemSelectCircuitoHasta(SelectEvent event) {
        circuitoHasta = (CodigoCircuitoCompra) event.getObject();  
    }
    
    public void onItemSelectCliente(SelectEvent event) {
        proveedor = (EntidadComercial) event.getObject();  
    }
    
    public void onItemSelectProducto(SelectEvent event) {
        producto = (Producto) event.getObject();  
    }

    public CodigoCircuitoCompra getCircuitoDesde() {
        return circuitoDesde;
    }

    public void setCircuitoDesde(CodigoCircuitoCompra circuitoDesde) {
        this.circuitoDesde = circuitoDesde;
    }

    public CodigoCircuitoCompra getCircuitoHasta() {
        return circuitoHasta;
    }

    public void setCircuitoHasta(CodigoCircuitoCompra circuitoHasta) {
        this.circuitoHasta = circuitoHasta;
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

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public EntidadComercial getProveedor() {
        return proveedor;
    }

    public void setProveedor(EntidadComercial proveedor) {
        this.proveedor = proveedor;
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
    
}
