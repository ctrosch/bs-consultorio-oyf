/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.tesoreria.web.informe;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
import bs.tesoreria.modelo.CuentaTesoreria;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.tesoreria.rn.MovimientoTesoreriaRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class ConsultaMovimientoTesoreriaBean extends InformeBase implements Serializable{
        
    @EJB private MovimientoTesoreriaRN movimientoRN;
        
    private Comprobante comprobante;
    private Integer numeroComprobante;
    private Date fechaDesde;
    private Date fechaHasta;
    
    
    private MovimientoTesoreria movimiento;
    private List<MovimientoTesoreria> movimientos;

    /**
     * Creates a new instance of CajaBean
     */
    public ConsultaMovimientoTesoreriaBean() {        
        
    }
    
    @PostConstruct
    @Override
    public void init(){
        
        filtro = new HashMap<String,String>();
        fechaDesde = new Date();
        fechaHasta = new Date();  
        movimientos = new ArrayList<MovimientoTesoreria>();        
    }
    
    public void verMovimientos(CuentaTesoreria c){   
        
        consultarDatos();        
    }    
    
    public void onSelect(SelectEvent event) {   
        
        consultarDatos();
    }
    
    public void onDateSelect(SelectEvent event) {
        
        consultarDatos();
    }
    
    public void cargarFiltro(){
        
        filtro.clear();
        
        if(fechaDesde!=null){
            filtro.put("fechaMovimiento = ", "'"+fechaDesde+"'"); 
        }
        
        if(fechaHasta!=null){
            filtro.put("fechaMovimiento = ", "'"+fechaHasta+"'"); 
        }
    }
    
    public void consultarDatos(){
        
        cargarFiltro(); 
        
        movimientos = movimientoRN.getMovimientosByFiltro(filtro);
        
        if(movimientos.isEmpty()){            
            JsfUtil.addWarningMessage("No se han encontrado movimientos");
        }
        
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public Integer getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(Integer numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
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

    public Map<String, String> getFiltro() {
        return filtro;
    }

    public void setFiltro(Map<String, String> filtro) {
        this.filtro = filtro;
    }

    public MovimientoTesoreria getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoTesoreria movimiento) {
        this.movimiento = movimiento;
    }

    public List<MovimientoTesoreria> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoTesoreria> movimientos) {
        this.movimientos = movimientos;
    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetParametros() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
   
}
