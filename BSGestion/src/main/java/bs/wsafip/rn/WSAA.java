/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.wsafip.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.wsafip.modelo.ParametroWS;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import java.util.logging.Logger;

/**
 *
 * @author Claudio
 */
public class WSAA {
    
    private static final Logger LOG = Logger.getLogger(WSAA.class.getName());
    
    private String token = "";
    private String sign = "";
            
    
    public void obtenerTicketAcceso(ParametroWS p) throws ExcepcionGeneralSistema{
        
                
        /* Crear objeto WSAA: Web Service de Autenticación y Autorización */
        ActiveXComponent wsaa = new ActiveXComponent("WSAA");
        
        Dispatch.call(wsaa, "Autenticar",new Variant("wsfe"), 
                new Variant(p.getPathCertificado()), 
                new Variant(p.getPathKey()),
                new Variant((p.getModoPrueba().equals("S")? p.getWsdlPrueba():p.getWsdlProduccion())));

        String excepcion =  Dispatch.get(wsaa, "Excepcion").toString();
        
        //System.out.println("Excepcion WSAA: " + excepcion);
        
        if(!excepcion.isEmpty()){
            throw new ExcepcionGeneralSistema(excepcion);
        }
        
        token = Dispatch.get(wsaa, "Token").toString();
        sign = Dispatch.get(wsaa, "Sign").toString();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
        
}
