/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.tesoreria.web.informe;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import bs.tesoreria.modelo.CuentaTesoreria;
import bs.tesoreria.rn.CuentaTesoreriaRN;
import java.io.Serializable;
import java.util.List;
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
public class ValoresPendienteBean extends InformeBase implements Serializable{
    
    @EJB private CuentaTesoreriaRN cuentaTesoreriaRN;
    
    
    private CuentaTesoreria cuentaTesoreria;
    private List<CuentaTesoreria> cuentas;
    
            
    /**
     * Creates a new instance of ImpresionComprobanteFacturacionBean
     */
    public ValoresPendienteBean() {
        
    }
    
    @Override
    @PostConstruct
    public void init(){
        
        super.init();
        cuentas = cuentaTesoreriaRN.getListaByBusqueda("2",filtro);
        
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
        
        if (usuarioSessionBean.isEstaRegistrado()) {
            parameters.put("USUARIO", usuarioSessionBean.getUsuario().getUsuario());
        }
        
        if(cuentaTesoreria!=null){
            parameters.put("NROCTA_CJ", cuentaTesoreria.getCodigo());           
        }else{
            parameters.put("NROCTA_CJ", "");           
        }        
        
        nombreArchivo = "CJ_VALPEN";
        reporte = reporteRN.getReporte(codigoReporte);
        //pathReporte = "tesoreria/informe/CJ_VALPEN.jasper";
               
    }
           
    @Override
    public void resetParametros(){
                
        todoOk = false;
        muestraReporte = false;
        cuentaTesoreria = null;
        
    }

    public CuentaTesoreria getCuentaTesoreria() {
        return cuentaTesoreria;
    }

    public void setCuentaTesoreria(CuentaTesoreria cuentaTesoreria) {
        this.cuentaTesoreria = cuentaTesoreria;
    }

    public List<CuentaTesoreria> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<CuentaTesoreria> cuentas) {
        this.cuentas = cuentas;
    }
    
    
    
}
