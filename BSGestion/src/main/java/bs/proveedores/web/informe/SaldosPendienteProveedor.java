/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.proveedores.web.informe;


import bs.entidad.modelo.EntidadComercial;
import bs.entidad.web.EntidadComercialBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import java.io.Serializable;
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
public class SaldosPendienteProveedor extends InformeBase implements Serializable{
    
    
    @ManagedProperty(value = "#{entidadComercialBean}")
    protected EntidadComercialBean entidadComercialBean;
    
    private EntidadComercial proveedor;
    private String codigoMonedaRegistracion;
    private String codigoMonedaVisualizacion;
    
    /**
     * Creates a new instance of ImpresionComprobanteFacturacionBean
     */
    public SaldosPendienteProveedor() {
    }
    
    @PostConstruct
    @Override
    public void init(){      
        
        super.init();
        codigoMonedaRegistracion = parametrosRN.getParametro().getCodigoMonedaPrimaria();
        codigoMonedaVisualizacion = parametrosRN.getParametro().getCodigoMonedaPrimaria();              
    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {
        
        String mensaje = "";
        todoOk = true;
       
        if(!mensaje.isEmpty()){
            todoOk = false;
            throw new ExcepcionGeneralSistema(mensaje);
        }        
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {
                
        if(proveedor!=null){ 
            parameters.put("NROCTA", proveedor.getNrocta());        
        }else{
            parameters.put("NROCTA", "");        
        }
        
        if(codigoMonedaRegistracion!=null && !codigoMonedaRegistracion.isEmpty()){
            parameters.put("MONREG", codigoMonedaRegistracion);        
        }
        
        if(codigoMonedaVisualizacion!=null && !codigoMonedaVisualizacion.isEmpty()){
            parameters.put("MONVIS", codigoMonedaVisualizacion);        
        }
        
        nombreArchivo = "VT_SALDOS_PENDIENTES";
        reporte = reporteRN.getReporte(codigoReporte);
        //pathReporte = "proveedor/informe/cuentaCorriente/PEN_CTA_CTE.jasper";
               
    }
    
    @Override
    public void resetParametros(){
         
        proveedor = null;
        todoOk = false;
        muestraReporte = false;
        
    }
    
    public void procesarProveedor(){
        
        if(entidadComercialBean.getEntidad()!=null){            
            
            proveedor = entidadComercialBean.getEntidad();                  
            todoOk = false;                        
        }
    }

    public EntidadComercial getProveedor() {
        return proveedor;
    }

    public void setProveedor(EntidadComercial proveedor) {
        this.proveedor = proveedor;
    }
        

    public EntidadComercialBean getEntidadComercialBean() {
        return entidadComercialBean;
    }

    public void setEntidadComercialBean(EntidadComercialBean entidadComercialBean) {
        this.entidadComercialBean = entidadComercialBean;
    }

    public String getCodigoMonedaRegistracion() {
        return codigoMonedaRegistracion;
    }

    public void setCodigoMonedaRegistracion(String codigoMonedaRegistracion) {
        this.codigoMonedaRegistracion = codigoMonedaRegistracion;
    }

    public String getCodigoMonedaVisualizacion() {
        return codigoMonedaVisualizacion;
    }

    public void setCodigoMonedaVisualizacion(String codigoMonedaVisualizacion) {
        this.codigoMonedaVisualizacion = codigoMonedaVisualizacion;
    }
    
    
    
}
