/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.tesoreria.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Formulario;
import bs.global.rn.FormularioRN;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.tesoreria.rn.MovimientoTesoreriaRN;
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
public class ImpresionFormularioTesoreriaBean extends InformeBase implements Serializable{
    
    @EJB FormularioRN formularioRN;
    @EJB MovimientoTesoreriaRN tesoreriaRN;
    
    private Integer numeroFormulario;
    private MovimientoTesoreria movimiento;
    
    @ManagedProperty(value = "#{formularioTesoreriaBean}")
    protected FormularioTesoreriaBean formularioTesoreriaBean;
    
    /**
     * Creates a new instance of ImpresionComprobanteFacturacionBean
     */
    public ImpresionFormularioTesoreriaBean() {
    }
    
    @PostConstruct
    @Override
    public void init(){
        
        super.init();
        filtro = new LinkedHashMap<String, String>();
        filtro.put("modfor", " = 'CJ'");
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
            throw new ExcepcionGeneralSistema("Ingrese un número de formulario válido");
        }

        movimiento = tesoreriaRN.getMovimiento(formulario.getCodigo(), numeroFormulario);

        if(movimiento==null){
            throw new ExcepcionGeneralSistema("El formulario "+formulario.getCodigo()+" nro "+numeroFormulario+" no existe");
        }
        
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {
        
        parameters.put("ID", movimiento.getId());
        
        if(movimiento.getMovimientoVenta()!=null){
            parameters.put("EN_LETRAS", JsfUtil.generarEnLetras(movimiento.getImporteTotalDebe()));
        }
        
        if(copias!=null && copias>0){            
            if(copias>4){
                parameters.put("CANT_COPIAS", 4);
            }else{
                parameters.put("CANT_COPIAS", copias);
            }            
        }else{
            parameters.put("CANT_COPIAS", movimiento.getComprobante().getCopias());
        }
        
        //nombreArchivo = JsfUtil.getCadenaAlfanumAleatoria(6)+"_"+movimiento.getFormulario().getCodigo()+"-"+movimiento.getNumeroFormulario();        
        nombreArchivo = movimiento.getFormulario().getCodigo()+"-"+movimiento.getNumeroFormulario();        
        reporte = formulario.getReporte();
        
        //System.err.println("nombre archivo: " + nombreArchivo);               
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
        
        if(movimiento.getEntidad().getEmail()!=null){
            emailEnvioComprobante = movimiento.getEntidad().getEmail();
        }
        JsfUtil.addWarningMessage("Separe las direcciones de entrega con punto y coma(;) si desea enviar a más de un destinatario");
    }
    
    public void enviarNotificaciones(){

        try{            
            muestraReporte = false;
            
            //CorreoElectronico ce = notificacionesVentaRN.generarCorreoElectronicoCliente(movimiento, emailEnvioComprobante,informacionAdicional);            
            //reporteToEmail(ce);            
        }catch (Exception e){
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "enviarNotificaciones", e); 
            JsfUtil.addErrorMessage("No es posible enviar la notificación", (e.getMessage()==null?"Error desconocido":e.getMessage()));                        
            solicitaEmail = true;
        }
    }
    
    @Override
    public void resetParametros() throws ExcepcionGeneralSistema {
        formulario = null;
        numeroFormulario = null;
    }
    
    public List<Formulario> completeFormulario(String query) {
        try {            
            return formularioRN.getFormularioByBusqueda(filtro,query, false);
            
        } catch (Exception ex) {
            
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "complete", ex);
            return new ArrayList<Formulario>();
        }
    }
    
    public void procesarFormulario(){
      
        if(formularioTesoreriaBean.getFormulario()!=null){            
            formulario = formularioTesoreriaBean.getFormulario();
        }
    }   
    
    public void onItemSelect(SelectEvent event) {
        formulario = (Formulario) event.getObject(); 
        todoOk = false;
    }

    public Integer getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(Integer numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
    }

    public MovimientoTesoreria getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoTesoreria movimiento) {
        this.movimiento = movimiento;
    }

    public FormularioTesoreriaBean getFormularioTesoreriaBean() {
        return formularioTesoreriaBean;
    }

    public void setFormularioTesoreriaBean(FormularioTesoreriaBean formularioTesoreriaBean) {
        this.formularioTesoreriaBean = formularioTesoreriaBean;
    }

}
