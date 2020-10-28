/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.dao;

import bs.global.dao.BaseDAO;
import bs.salud.modelo.ParametroSalud;
import javax.ejb.Stateless;

/**
 *
 * @author Guillermo
 */
@Stateless
public class ParametroSaludDAO extends BaseDAO {

    public void editar(ParametroSalud parametro) throws Exception {
        getEm().merge(parametro);
    }

    public ParametroSalud getParametros() {

        return getEm().find(ParametroSalud.class, "01");

    }

}
