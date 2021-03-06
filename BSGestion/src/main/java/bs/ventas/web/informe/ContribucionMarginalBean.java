/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.ventas.web.informe;

import bs.administracion.rn.ReporteRN;
import bs.entidad.modelo.EntidadComercial;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import bs.stock.modelo.Producto;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class ContribucionMarginalBean extends InformeBase implements Serializable{
    
    private Date fechaDesde;
    private Date fechaHasta;    
    private Producto producto;
    private EntidadComercial cliente;
        
    public ContribucionMarginalBean() {
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
        
        nombreArchivo = "CONTRIBUCION_MARGINAL";
        reporte = reporteRN.getReporte(codigoReporte);
        //pathReporte = "venta/informe/estadistica/VTA_CONTRIBUCION_MARGINAL.jasper";               
    }
    
    @Override
    public void resetParametros(){
        
        fechaDesde = new Date();
        fechaHasta = new Date();        
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
