/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.entidad.dao;

import bs.entidad.modelo.ParametroEntidad;
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

public class ParametroEntidadDAO extends BaseDAO {

    @TransactionAttribute(TransactionAttributeType.REQUIRED) 
    public void guardar(ParametroEntidad parametros)throws Exception{
        getEm().persist(parametros);
    }
    
    public void editar(ParametroEntidad parametro)throws Exception{
        getEm().merge(parametro);
    }

    public ParametroEntidad getParametros(String codtip){

        return  getEm().find(ParametroEntidad.class, codtip);

    }
    
}
