/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.facturacion.dao;


import bs.facturacion.modelo.ParametroFacturacion;
import bs.global.dao.BaseDAO;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author Claudio
 */
@Stateless

public class ParametroFacturacionDAO extends BaseDAO {

    
    public void editar(ParametroFacturacion parametro)throws Exception{
        getEm().merge(parametro);
    }

    public ParametroFacturacion getParametros(){

        return  getEm().find(ParametroFacturacion.class, "01");

    }
    
}
