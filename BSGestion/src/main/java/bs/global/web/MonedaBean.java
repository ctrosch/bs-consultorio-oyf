/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.global.web;

import bs.global.modelo.Moneda;
import bs.global.rn.MonedaRN;
import bs.global.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class MonedaBean extends GenericBean implements Serializable {
    
    @EJB private MonedaRN monedaRN;
    
    private Moneda moneda;
    private List<Moneda> lista;
       
    
    public MonedaBean() {
        
    }
    
    @PostConstruct
    public void init(){
        
        txtBusqueda="";
        mostrarDebaja = false;
        nuevo();
        buscar();        
    }
    
    public void nuevo(){
        
        esNuevo = true;
        buscaMovimiento = false;
        moneda = new Moneda();
    }
    
     public void guardar(boolean nuevo){
        
        try {
            if (moneda != null) {
                moneda = monedaRN.guardar(moneda, esNuevo);
                esNuevo = false;                
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");
                
                if(nuevo){
                    nuevo();
                }
            }else{
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }
     
     public void habilitaDeshabilita(String habDes){
        
        try {
            moneda.getAuditoria().setDebaja(habDes);
            moneda = monedaRN.guardar(moneda, false);            
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");
            
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }
     
     public void eliminar(){
        try {
            monedaRN.eliminar(moneda);
            nuevo();            
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }        
    }
     
     public void buscar(){
         lista = monedaRN.getLista();
     }
     
     public List<Moneda> complete (String query) {
         try{
             lista = monedaRN.getLista();
             return lista;
         } catch (Exception ex) {
             
             Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
             return new ArrayList<Moneda>();
         }
     }
     
     public void onSelect(SelectEvent event) {
         moneda = (Moneda) event.getObject();
         esNuevo = false;
         buscaMovimiento = false;
     }
     
     public void seleccionar (Moneda z) {
         
         moneda = z;
         esNuevo = false;
         buscaMovimiento = false;
     }
     
     public void imprimir() {
         
         if(moneda==null){
             JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
         }
     }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public List<Moneda> getLista() {
        return lista;
    }

    public void setLista(List<Moneda> lista) {
        this.lista = lista;
    }

}
