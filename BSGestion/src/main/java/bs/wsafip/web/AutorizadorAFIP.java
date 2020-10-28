/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.wsafip.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.ventas.modelo.MovimientoVenta;
import bs.ventas.rn.MovimientoVentaRN;
import bs.wsafip.dao.ParametroWSDAO;
import bs.wsafip.dao.WebServiceDAO;
import bs.wsafip.rn.WSFEv1;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class AutorizadorAFIP extends GenericBean implements Serializable{
    
    @EJB private MovimientoVentaRN movimientoRN;
    @EJB private WebServiceDAO webServiceDAO;
    @EJB private ParametroWSDAO parametroWSDAO;
    
    public List<MovimientoVenta> lista;
    public WSFEv1 wsfev1;

    /**
     * Creates a new instance of AutorizadorAFIP
     */
    public AutorizadorAFIP() {
        
    }
    
    @PostConstruct
    private void init(){
        
        lista = new ArrayList<MovimientoVenta>();
        
    }
    
    public void verComprobantes(){
        lista = movimientoRN.getComprobantesPendienteAutorizar();
    }
    
    public void autorizarComprobante(MovimientoVenta m){
        
        try {
            wsfev1.autorizar(m);
            movimientoRN.guardar(m);
            JsfUtil.addInfoMessage("El comprobante " 
                    + m.getFormulario().getCodigo()+" " 
                    + m.getPuntoVenta().getCodigo()+" "
                    + m.getNumeroFormulario()+" "
                    + "Fue autorizado correctamente - CAE " + m.getNumeroCAE() );
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage(ex.getMessage());
        } catch (Exception ex) {
            JsfUtil.addErrorMessage(ex.getMessage());
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }        
    }            
    
    public void autorizarComprobantes(){
        
        if(wsfev1==null){
            
            wsfev1 = new WSFEv1(parametroWSDAO.getObjeto("01"), webServiceDAO.getObjeto("WSFE"));
        }
        
        for(MovimientoVenta m: lista){
            
            if(m.isSeleccionado()){
                try {
                    wsfev1.autorizar(m);
                    movimientoRN.guardar(m);
                    
                    JsfUtil.addInfoMessage("El comprobante " 
                    + m.getFormulario().getCodigo()+" " 
                    + m.getPuntoVenta().getCodigo()+" "
                    + m.getNumeroFormulario()+" "
                    + "Fue autorizado correctamente - CAE " + m.getNumeroCAE() );
                    
                } catch (ExcepcionGeneralSistema ex) {
                    JsfUtil.addErrorMessage(ex.getMessage());
                } catch (Exception ex) {
                    JsfUtil.addErrorMessage(ex.getMessage());
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }            
        }
    }
    
    
    public void recuperarCAE(){
        
        if(wsfev1==null){
            
            wsfev1 = new WSFEv1(parametroWSDAO.getObjeto("01"), webServiceDAO.getObjeto("WSFE"));
        }
        
        for(MovimientoVenta m: lista){
            
            if(m.isSeleccionado()){
                try {
                    wsfev1.recuperarCAE(m);
                    m = movimientoRN.guardar(m);
                    
                    JsfUtil.addInfoMessage("El comprobante " 
                    + m.getFormulario().getCodigo()+" " 
                    + m.getPuntoVenta().getCodigo()+" "
                    + m.getNumeroFormulario()+" "
                    + "Fue recuperado correctamente - CAE " + m.getNumeroCAE() );
                    
                } catch (ExcepcionGeneralSistema ex) {
                    JsfUtil.addErrorMessage(ex.getMessage());
                } catch (Exception ex) {
                    JsfUtil.addErrorMessage(ex.getMessage());
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }            
        }
    }

    public List<MovimientoVenta> getLista() {
        return lista;
    }

    public void setLista(List<MovimientoVenta> lista) {
        this.lista = lista;
    }

    public WSFEv1 getWsfev1() {
        return wsfev1;
    }

    public void setWsfev1(WSFEv1 wsfev1) {
        this.wsfev1 = wsfev1;
    }
    
    
    
}
