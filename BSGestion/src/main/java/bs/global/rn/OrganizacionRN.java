/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.OrganizacionDAO;
import bs.global.modelo.Organizacion;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author ctrosch
 */
@Stateless
public class OrganizacionRN implements Serializable {

    @EJB
    private OrganizacionDAO organizacionDAO;

    public Organizacion getOrganizacion(String id) {
        return organizacionDAO.getOrganizacion(id);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Organizacion guardar(Organizacion e) throws Exception {

        if (e.getCodigo().equals("01")) {

            if (organizacionDAO.getOrganizacion(e.getCodigo()) == null) {
                organizacionDAO.crear(e);
            } else {
                e = (Organizacion) organizacionDAO.editar(e);
            }
        }

        return e;
    }

}
