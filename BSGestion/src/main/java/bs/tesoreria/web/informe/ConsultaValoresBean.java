/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.tesoreria.web.informe;

import bs.global.web.GenericBean;
import bs.tesoreria.modelo.CuentaTesoreria;
import bs.tesoreria.modelo.ItemSaldoPendienteCuentaTesoreria;
import bs.tesoreria.rn.CuentaTesoreriaRN;
import java.io.Serializable;
import java.util.List;
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
public class ConsultaValoresBean extends GenericBean implements Serializable {
        
    @EJB private CuentaTesoreriaRN cuentaTesoreriaRN;
    
    private CuentaTesoreria cuentaTesoreria;    
            
    private List<ItemSaldoPendienteCuentaTesoreria> pendientes;
    private ItemSaldoPendienteCuentaTesoreria itemPendiente;

    /**
     * Creates a new instance of CajaBean
     */
    public ConsultaValoresBean() {        
        
    }
    
//    @PostConstruct
//    private void init(){
//        
//        filtro = new HashMap<String,String>();
//        pendientes = new ArrayList<ItemSaldoPendienteCuentaTesoreria>();
//    }
//    
    public void init(String cuenta){
        
        if(!beanIniciado){
        
            cuentaTesoreria = cuentaTesoreriaRN.getCuentaTesoreria(cuenta);            
            verSaldosPendiente();            
            beanIniciado = true;
        }
    }
    
    public void verSaldosPendiente(){
        
        if(cuentaTesoreria==null){
//            JsfUtil.addWarningMessage("No se encontró la cuenta de tesorería");
            return;
        }
        
        pendientes = cuentaTesoreriaRN.getSaldosPendienteByCuenta(cuentaTesoreria.getCodigo());
            
        if(pendientes.isEmpty()){            
//            JsfUtil.addWarningMessage("No se han encontrado movimientos");
        }        
    }
    
    public void actualizarSaldosPendientes(){
        
        if(cuentaTesoreria==null){            
            return;
        }
        
        pendientes = cuentaTesoreriaRN.getSaldosPendienteByCuenta(cuentaTesoreria.getCodigo());
    }
    
    public void onSelect(SelectEvent event) {  
        
        itemPendiente =  (ItemSaldoPendienteCuentaTesoreria) event.getObject();        
    }

    public CuentaTesoreria getCuentaTesoreria() {
        return cuentaTesoreria;
    }

    public void setCuentaTesoreria(CuentaTesoreria cuentaTesoreria) {
        this.cuentaTesoreria = cuentaTesoreria;
    }

    public List<ItemSaldoPendienteCuentaTesoreria> getPendientes() {
        return pendientes;
    }

    public void setPendientes(List<ItemSaldoPendienteCuentaTesoreria> pendientes) {
        this.pendientes = pendientes;
    }

    public ItemSaldoPendienteCuentaTesoreria getItemPendiente() {
        return itemPendiente;
    }

    public void setItemPendiente(ItemSaldoPendienteCuentaTesoreria itemPendiente) {
        this.itemPendiente = itemPendiente;
    }
       
}
