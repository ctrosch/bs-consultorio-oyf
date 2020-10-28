/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.facturacion.web.informe;

import bs.administracion.modelo.CorreoElectronico;
import bs.facturacion.modelo.MovimientoFacturacion;
import bs.facturacion.rn.FacturacionRN;
import bs.facturacion.rn.NotificacionesFacturacionRN;
import bs.facturacion.web.FormularioFacturacionBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Formulario;
import bs.global.rn.FormularioRN;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
public class ImpresionFormularioFacturacionBean extends InformeBase implements Serializable{
    
    @EJB FormularioRN formularioRN;
    @EJB FacturacionRN facturacionRN;
    @EJB NotificacionesFacturacionRN notificacionesFacturacionRN;
    
    private Integer numeroFormulario;
    private MovimientoFacturacion movimiento;
    
    
    @ManagedProperty(value = "#{formularioFacturacionBean}")
    protected FormularioFacturacionBean formularioFacturacionBean;
  
    
    /**
     * Creates a new instance of ImpresionComprobanteFacturacionBean
     */
    public ImpresionFormularioFacturacionBean() {
    }
    
    @PostConstruct
    @Override
    public void init(){
        
        super.init();        
        filtro = new LinkedHashMap<String, String>();
        filtro.put("modfor", " = 'FC'");
    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {
                
        if(formulario==null){
            throw new ExcepcionGeneralSistema("Seleccione un formulario");
        }
        
        if(formulario.getReporte()==null){
            throw new ExcepcionGeneralSistema("El formulario seleccionado no tiene asignado reporte");
        }

        if(numeroFormulario==null || numeroFormulario<=0){
            throw new ExcepcionGeneralSistema("Ingrese un número de comprobante válido");
        }

        movimiento = facturacionRN.getMovimiento(formulario.getCodigo(), numeroFormulario);

        if(movimiento==null){
            throw new ExcepcionGeneralSistema("El comprobante "+formulario.getCodigo()+" nro "+numeroFormulario+" no existe");
        }        
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {
        
        parameters.put("ID", movimiento.getId());
        
        if(copias!=null && copias>0){
            
            if(copias>4){
                parameters.put("CANT_COPIAS", 4);
            }else{
                parameters.put("CANT_COPIAS", copias);
            }            
        }else{
            parameters.put("CANT_COPIAS", movimiento.getComprobante().getCopias());
        }
        
        nombreArchivo = movimiento.getFormulario().getCodigo()+"-"+movimiento.getNumeroFormulario();        
        reporte = formulario.getReporte();
               
    }
    
    public List<Formulario> completeFormulario(String query) {
        try {            
            return formularioRN.getFormularioByBusqueda(filtro,query, false);
            
        } catch (Exception ex) {
            
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "complete", ex);
            return new ArrayList<Formulario>();
        }
    }
        
    public void onItemSelect(SelectEvent event) {
        formulario = (Formulario) event.getObject();  
    }
    
    public void procesarFormulario(){
      
        if(formularioFacturacionBean.getFormulario()!=null){            
            formulario = formularioFacturacionBean.getFormulario();
        }
    }   
    
    public void preparoEnvioNotificaciones(){
        
        reporteToPdf();
        
        if(movimiento==null){
            JsfUtil.addErrorMessage("Para enviar el correo electrónico, antes debe ejecutar el reporte");
            return;
        }
        
        solicitaEmail = true;
        emailEnvioComprobante = "";
        informacionAdicional = "";
        
        if(movimiento !=null && movimiento.getCliente().getEmail()!=null){
            emailEnvioComprobante = movimiento.getCliente().getEmail();
        }
        JsfUtil.addWarningMessage("Separe las direcciones de entrega con punto y coma(;) si desea enviar a más de un destinatario");
    }
    
    public void enviarNotificaciones(){

        try {
            muestraReporte = false;            
            CorreoElectronico ce = notificacionesFacturacionRN.generarCorreoElectronicoCliente(movimiento, emailEnvioComprobante,informacionAdicional);
            reporteToEmail(ce);
            
        } catch (Exception ex){
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "enviarNotificaciones", ex); 
            JsfUtil.addErrorMessage("No es posible enviar la notificación", "enviarNotificaciones: "+ ex);                        
            solicitaEmail = true;
        }
    }
    
    @Override
    public void resetParametros() throws ExcepcionGeneralSistema {
        
        todoOk = false;
        muestraReporte = false;
        solicitaEmail = false;
        emailEnvioComprobante = "";
        formulario = null;
        numeroFormulario = null;
        movimiento = null;
        
    }

    //--------------------------------------------------------------------------
    
    public Integer getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(Integer numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
    }

    public MovimientoFacturacion getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoFacturacion movimiento) {
        this.movimiento = movimiento;
    }

    public FormularioRN getFormularioRN() {
        return formularioRN;
    }

    public void setFormularioRN(FormularioRN formularioRN) {
        this.formularioRN = formularioRN;
    }

    public FacturacionRN getFacturacionRN() {
        return facturacionRN;
    }

    public void setFacturacionRN(FacturacionRN facturacionRN) {
        this.facturacionRN = facturacionRN;
    }

    public FormularioFacturacionBean getFormularioFacturacionBean() {
        return formularioFacturacionBean;
    }

    public void setFormularioFacturacionBean(FormularioFacturacionBean formularioFacturacionBean) {
        this.formularioFacturacionBean = formularioFacturacionBean;
    }
    
}
