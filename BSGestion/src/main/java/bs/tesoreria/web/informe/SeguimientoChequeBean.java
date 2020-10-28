/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.tesoreria.web.informe;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.InformeBase;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class SeguimientoChequeBean extends InformeBase implements Serializable{
    
    private String nroCheque;
    
            
    /**
     * Creates a new instance of ImpresionComprobanteFacturacionBean
     */
    public SeguimientoChequeBean() {
    }
    
    @PostConstruct
    public void init(){
    
        nroCheque = "";
        
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
        
        if(nroCheque!=null){
            parameters.put("CHEQUE", nroCheque);           
        }else{
            parameters.put("CHEQUE", "");           
        }
        
        
        nombreArchivo = "CJ_SEGCHQ";
        reporte = reporteRN.getReporte(codigoReporte);
        //pathReporte = "tesoreria/informe/CJ_SEGCHQ.jasper";
               
    }
           
    @Override
    public void resetParametros(){
                
        todoOk = false;
        muestraReporte = false;
        nroCheque = "";
        
    }

    public String getNroCheque() {
        return nroCheque;
    }

    public void setNroCheque(String nroCheque) {
        this.nroCheque = nroCheque;
    }
    
    
}
