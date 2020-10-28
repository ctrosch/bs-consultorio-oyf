/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.dao;

import bs.educacion.modelo.ParametroEducacion;
import bs.global.dao.BaseDAO;
import javax.ejb.Stateless;

/**
 *
 * @author Claudio
 */
@Stateless
public class ParametroEducacionDAO extends BaseDAO {

    public void editar(ParametroEducacion parametro) throws Exception {
        getEm().merge(parametro);
    }

    public ParametroEducacion getParametros() {

        return getEm().find(ParametroEducacion.class, "01");

    }

}
