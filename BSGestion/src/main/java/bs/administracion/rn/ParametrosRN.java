/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.administracion.rn;

import bs.administracion.dao.ParametroDAO;
import bs.administracion.modelo.Parametro;
import bs.global.excepciones.ExcepcionGeneralSistema;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class ParametrosRN implements Serializable{

    @EJB ParametroDAO parametroDAO;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED) 
    public void guardar(Parametro p) throws ExcepcionGeneralSistema, Exception{
        
        if(p.getId().equals("01")){
            
            if(parametroDAO.getParametros()==null){
                parametroDAO.crear(p);
            }else{
                parametroDAO.editar(p);
            }
        }
    }
    
    public Parametro getParametro(){
                
        Parametro p = parametroDAO.getParametros();

        if (p==null){
            p= new Parametro("01");            
        }        
        return p;
    } 

    public Parametro getParametro(String codigo){
                
        Parametro p = parametroDAO.getParametros();
        
//        if (p==null){
//            p= new Parametro(codigo);            
//        }        
        return p;
    } 
}
