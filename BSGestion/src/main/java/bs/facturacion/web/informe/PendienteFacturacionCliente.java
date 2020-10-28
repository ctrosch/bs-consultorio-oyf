/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.facturacion.web.informe;

import bs.entidad.modelo.EntidadComercial;
import bs.entidad.rn.EntidadRN;
import bs.entidad.web.EntidadComercialBean;
import bs.facturacion.modelo.CodigoCircuitoFacturacion;
import bs.facturacion.rn.CodigoCircuitoFacturacionRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
import bs.stock.modelo.Producto;
import bs.stock.rn.ProductoRN;
import bs.stock.web.ProductoBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class PendienteFacturacionCliente extends InformeBase implements Serializable{
    
    @EJB CodigoCircuitoFacturacionRN codigoCircuitoFacturacionRN;
    @EJB EntidadRN entidadRN;
    @EJB ProductoRN productoRN;
    
    @ManagedProperty(value = "#{productoBean}")
    protected ProductoBean productoBean;
    
    @ManagedProperty(value = "#{entidadComercialBean}")
    protected EntidadComercialBean entidadComercialBean;
        
    private CodigoCircuitoFacturacion circuitoDesde;
    private CodigoCircuitoFacturacion circuitoHasta;
    private Date fechaDesde;
    private Date fechaHasta;    
    private Producto producto;
    private EntidadComercial cliente;

    
    /**
     * Creates a new instance of ImpresionComprobanteFacturacionBean
     */
    public PendienteFacturacionCliente() {
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
        
        nombreArchivo = "PendienteCliente_"+JsfUtil.getCadenaAlfanumAleatoria(6);
        reporte = reporteRN.getReporte(codigoReporte);
        //pathReporte = "facturacion/informe/PENCLI.jasper";
               
    }
    
    public List<CodigoCircuitoFacturacion> completeCircuitoFacturacion(String query) {
        try {            
            return codigoCircuitoFacturacionRN.getListaByBusqueda(filtro,query, false);
            
        } catch (Exception ex) {
            
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "complete", ex);
            return new ArrayList<CodigoCircuitoFacturacion>();
        }
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
        circuitoDesde = null;
        circuitoHasta = null;
        cliente = null;
        producto = null; 
        todoOk = false;
        muestraReporte = false;
        
    }
    
    
    
    public void onItemSelectCircuitoDesde(SelectEvent event) {
        circuitoHasta = (CodigoCircuitoFacturacion) event.getObject();  
    }
    
    public void onItemSelectCircuitoHasta(SelectEvent event) {
        circuitoHasta = (CodigoCircuitoFacturacion) event.getObject();  
    }
    
    public void onItemSelectCliente(SelectEvent event) {
        cliente = (EntidadComercial) event.getObject();  
    }
    
    public void onItemSelectProducto(SelectEvent event) {
        producto = (Producto) event.getObject();  
    }

    public CodigoCircuitoFacturacion getCircuitoDesde() {
        return circuitoDesde;
    }

    public void setCircuitoDesde(CodigoCircuitoFacturacion circuitoDesde) {
        this.circuitoDesde = circuitoDesde;
    }

    public CodigoCircuitoFacturacion getCircuitoHasta() {
        return circuitoHasta;
    }

    public void setCircuitoHasta(CodigoCircuitoFacturacion circuitoHasta) {
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

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
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
