/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.prestamo.dao.ParametroPrestamoDAO;
import bs.prestamo.modelo.ParametroPrestamo;
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
public class ParametroPrestamoRN implements Serializable {

    @EJB
    ParametroPrestamoDAO parametroDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(ParametroPrestamo p) throws ExcepcionGeneralSistema, Exception {

        if (p.getId().equals("01")) {

            if (parametroDAO.getParametros() == null) {
                parametroDAO.crear(p);
            } else {
                parametroDAO.editar(p);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ParametroPrestamo getParametro() throws Exception {

        ParametroPrestamo p = parametroDAO.getParametros();

        if (p == null) {
            p = new ParametroPrestamo();
            p.setId("01");
            parametroDAO.crear(p);
        }
        return p;
    }
}
