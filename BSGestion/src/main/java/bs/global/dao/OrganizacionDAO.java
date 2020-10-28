/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.dao;

import bs.global.modelo.Organizacion;
import bs.global.dao.BaseDAO;
import javax.ejb.Stateless;

/**
 *
 * @author ctrosch
 */
@Stateless
public class OrganizacionDAO extends BaseDAO {

    public Organizacion getOrganizacion(String id) {

        return super.getObjeto(Organizacion.class, id);
    }
}
