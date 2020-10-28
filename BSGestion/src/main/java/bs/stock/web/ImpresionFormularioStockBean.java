/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Formulario;
import bs.global.rn.FormularioRN;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
import bs.stock.modelo.MovimientoStock;
import bs.stock.rn.MovimientoStockRN;
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
public class ImpresionFormularioStockBean extends InformeBase implements Serializable{
    
    @EJB private FormularioRN formularioRN;    
    @EJB private MovimientoStockRN movimientoStockRN;

    private Integer numeroFormulario;
    private MovimientoStock movimiento;
    
    @ManagedProperty(value = "#{formularioStockBean}")
    protected FormularioStockBean formularioStockBean;


    /** Creates a new instance of ImpresionComprobanteProduccionBean */
    public ImpresionFormularioStockBean() {
        
        titulo = "Impresión Comprobante Inventario";
    }

    @PostConstruct
    @Override
    public void init(){

        super.init();
        filtro = new LinkedHashMap<String, String>();
        filtro.put("modfor", " = 'ST'");

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

        movimiento = movimientoStockRN.getMovimiento(formulario.getCodigo(), numeroFormulario);

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
        nombreArchivo = JsfUtil.getCadenaAlfanumAleatoria(6)+"_"+movimiento.getFormulario().getCodigo()+"-"+movimiento.getNumeroFormulario();        
        reporte = formulario.getReporte();
        
        //System.err.println("nombre archivo: " + nombreArchivo);
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
      
        if(formularioStockBean.getFormulario()!=null){            
            formulario = formularioStockBean.getFormulario();
        }
    } 

    public Integer getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(Integer numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
    }

    public MovimientoStock getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoStock movimiento) {
        this.movimiento = movimiento;
    }

    @Override
    public void resetParametros() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public FormularioStockBean getFormularioStockBean() {
        return formularioStockBean;
    }

    public void setFormularioStockBean(FormularioStockBean formularioStockBean) {
        this.formularioStockBean = formularioStockBean;
    }

    
    
    
}
