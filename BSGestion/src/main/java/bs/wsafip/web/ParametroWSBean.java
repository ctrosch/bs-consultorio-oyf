/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.wsafip.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.wsafip.modelo.ParametroWS;
import bs.wsafip.rn.ParametroWSRN;
import bs.wsafip.rn.WSAA;
import java.io.Serializable;
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
public class ParametroWSBean extends GenericBean implements Serializable {
    
    @EJB private ParametroWSRN parametroWSRN;
    
    private ParametroWS parametro;
    private WSAA wsaa;

    /**
     * Creates a new instance of TestWSAA
     */
    public ParametroWSBean() {
        
    }
    
    @PostConstruct
    private void init(){
        parametro = parametroWSRN.getParametro("01");
        
        if(parametro==null){
            parametro = new ParametroWS();
        }
    }
    
    public void guardar(){
        try {
            parametroWSRN.guardar(parametro);
            JsfUtil.addInfoMessage("Lo datos se ha guardado correctamente");
        } catch (ExcepcionGeneralSistema ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos");
        }        
    }
    
    public void comprobarConexion(){
        
        wsaa = new WSAA();
        try {
            wsaa.obtenerTicketAcceso(parametro);
            
            JsfUtil.addInfoMessage("Conexión exitosa!");
            JsfUtil.addInfoMessage("Certificado: " + parametro.getPathCertificado());
            JsfUtil.addInfoMessage("Key: " + parametro.getPathKey());
            JsfUtil.addInfoMessage("Token: " + wsaa.getToken());
            JsfUtil.addInfoMessage("Sing " + wsaa.getSign());
            
        } catch (ExcepcionGeneralSistema ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible conectarse " + ex);
        }
        
    }

    public ParametroWS getParametro() {
        return parametro;
    }

    public void setParametro(ParametroWS parametro) {
        this.parametro = parametro;
    }

    public WSAA getWsaa() {
        return wsaa;
    }

    public void setWsaa(WSAA wsaa) {
        this.wsaa = wsaa;
    }
    
    
}
