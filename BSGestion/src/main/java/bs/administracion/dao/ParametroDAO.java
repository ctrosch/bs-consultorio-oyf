/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.administracion.dao;

import bs.administracion.modelo.Parametro;
import bs.global.dao.BaseDAO;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class ParametroDAO extends BaseDAO {

    @TransactionAttribute(TransactionAttributeType.REQUIRED) 
    public void guardar(Parametro parametros)throws Exception{
        getEm().persist(parametros);
    }
    
    public void editar(Parametro parametro)throws Exception{
        getEm().merge(parametro);
    }

    public Parametro getParametros(){

        return  getEm().find(Parametro.class, "01");

    }
    
}
