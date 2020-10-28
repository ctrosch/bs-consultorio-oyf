/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.global.util;

/**
 *
 * @author Claudio
 */
public class StringUtil {

        
    public String right(String txt, int cantDigitos){
                
        return txt.substring(txt.length() - cantDigitos);
    }
    
    public String right(String txt, int cantDigitos, String space){
        
        String sSoporte = "";
        
        for(int i= 0; i<=cantDigitos; i++){            
            sSoporte += space;            
        }
        
        String sFinal = sSoporte+txt;
        return sFinal.substring(sFinal.length() - cantDigitos);
    }
    
    public String left(String txt, int cantDigitos){
        
        return txt.substring(0, cantDigitos);
    }
    
    public String left(String txt, int cantDigitos, String space){
        
        String sSoporte = "";
        
        for(int i= 0; i<=cantDigitos; i++){            
            sSoporte += space;            
        }
        
        String sFinal = txt+sSoporte;
        return sFinal.substring(0, cantDigitos);
    }
    
}
