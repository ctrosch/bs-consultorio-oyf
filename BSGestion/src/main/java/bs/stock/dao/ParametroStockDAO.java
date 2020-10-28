/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.dao;

import bs.global.dao.BaseDAO;
import bs.stock.modelo.ParametroStock;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless

public class ParametroStockDAO extends BaseDAO {

    @TransactionAttribute(TransactionAttributeType.REQUIRED) 
    public void guardar(ParametroStock parametros)throws Exception{
        getEm().persist(parametros);
    }
    
    public void editar(ParametroStock parametro)throws Exception{
        getEm().merge(parametro);
    }

    public ParametroStock getParametros(){

        return  getEm().find(ParametroStock.class, "01");

    }
    
}
