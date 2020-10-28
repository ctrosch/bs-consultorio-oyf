/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.web.informe;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import bs.stock.modelo.Deposito;
import bs.stock.rn.ProductoRN;
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
public class StockDepositoFechaBean extends InformeBase implements Serializable{
    
    @EJB ProductoRN productoRN;
    
    private Date fechaHasta;    
    private Deposito deposito;

            
    /**
     * Creates a new instance of ImpresionComprobanteFacturacionBean
     */
    public StockDepositoFechaBean() {
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
        
        if(deposito!=null){
            parameters.put("DEPOSI", deposito.getCodigo());           
        }else{
            parameters.put("DEPOSI", "");           
        }
        
        
        nombreArchivo = "ST_STOCK_DEPOSITO_FECHA";
        reporte = reporteRN.getReporte(codigoReporte);
        //pathReporte = "stock/informe/ST_STOCK_DEPOSITO_FECHA.jasper";
               
    }
           
    @Override
    public void resetParametros(){
        
        
        fechaHasta = new Date();        
        deposito = null; 
        todoOk = false;
        muestraReporte = false;
        
    }
  
    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }
    
    
       

    
}
