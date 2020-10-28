/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.proveedores.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Formulario;
import bs.global.rn.FormularioRN;
import bs.global.util.InformeBase;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.proveedores.rn.MovimientoProveedorRN;
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
public class ImpresionFormularioProveedorBean extends InformeBase implements Serializable{
    
    @EJB FormularioRN formularioRN;
    @EJB MovimientoProveedorRN proveedorRN;
    
    private Integer numeroFormulario;
    private MovimientoProveedor movimiento;
    
    @ManagedProperty(value = "#{formularioProveedorBean}")
    protected FormularioProveedorBean formularioProveedorBean;
    
    /**
     * Creates a new instance of ImpresionComprobanteFacturacionBean
     */
    public ImpresionFormularioProveedorBean() {
    }
    
    @PostConstruct
    @Override
    public void init(){
        
        super.init();
        filtro = new LinkedHashMap<String, String>();
        filtro.put("modfor", " = 'PV'");
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

        movimiento = proveedorRN.getMovimiento(formulario.getCodigo(), numeroFormulario);

        if(movimiento==null){
            throw new ExcepcionGeneralSistema("El formulario "+formulario.getCodigo()+" nro "+numeroFormulario+" no existe");
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
        
        //nombreArchivo = JsfUtil.getCadenaAlfanumAleatoria(6)+"_"+movimiento.getFormulario().getCodigo()+"-"+movimiento.getNumeroFormulario();        
        nombreArchivo = movimiento.getFormulario().getCodigo()+"-"+movimiento.getNumeroFormulario();        
        reporte = formulario.getReporte();
        
        //System.err.println("nombre archivo: " + nombreArchivo);               
    }
    
    @Override
    public void resetParametros() throws ExcepcionGeneralSistema {
        formulario = null;
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
      
        if(formularioProveedorBean.getFormulario()!=null){            
            formulario = formularioProveedorBean.getFormulario();
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

    public MovimientoProveedor getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoProveedor movimiento) {
        this.movimiento = movimiento;
    }

    public FormularioProveedorBean getFormularioProveedorBean() {
        return formularioProveedorBean;
    }

    public void setFormularioProveedorBean(FormularioProveedorBean formularioProveedorBean) {
        this.formularioProveedorBean = formularioProveedorBean;
    }
    
    

    
}
