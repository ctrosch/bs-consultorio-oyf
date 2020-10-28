/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.prestamo.dao;


import bs.global.dao.BaseDAO;
import bs.prestamo.modelo.ParametroPrestamo;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless

public class ParametroPrestamoDAO extends BaseDAO {

    @TransactionAttribute(TransactionAttributeType.REQUIRED) 
    public void guardar(ParametroPrestamo parametros)throws Exception{
        getEm().persist(parametros);
    }
    
    public void editar(ParametroPrestamo parametro)throws Exception{
        getEm().merge(parametro);
    }

    public ParametroPrestamo getParametros(){

        return  getEm().find(ParametroPrestamo.class, "01");

    }
    
}
