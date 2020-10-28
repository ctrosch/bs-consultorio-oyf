/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.impuesto.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class SubdiarioVentaBean extends InformeBase implements Serializable{
    
    private Date fechaDesde;
    private Date fechaHasta;  

    /**
     * Creates a new instance of CitiBean
     */
    public SubdiarioVentaBean() {
        
    }
    
    @PostConstruct
    @Override
    public void init(){
        
        fechaDesde = new Date();
        fechaHasta = new Date();
        
    }
   
    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {
        
        String mensaje = "";
        todoOk = true;
        
        if(codigoReporte==null || codigoReporte.isEmpty()){
            mensaje = "El código de reporte no puede ser nulo";            
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
                
        parameters.put("FCHMOV_DESDE", fechaDesde);
        parameters.put("FCHMOV_HASTA", fechaHasta);
        
        //nombreArchivo = "SUBDIARIO_VENTA";        
        reporte = reporteRN.getReporte(codigoReporte);
               
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
    
}
