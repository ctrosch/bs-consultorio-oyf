/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.rn;

import bs.administracion.rn.ParametrosRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.salud.dao.ParametroSaludDAO;
import bs.salud.modelo.ParametroSalud;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Guillermo
 */
@Stateless
public class ParametroSaludRN implements Serializable {

    @EJB
    ParametroSaludDAO parametroDAO;
    @EJB
    EstadoSaludRN estadoRN;
    @EJB
    protected ParametrosRN parametrosRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(ParametroSalud p) throws ExcepcionGeneralSistema, Exception {

        if (p.getId().equals("01")) {

            if (parametroDAO.getParametros() == null) {
                parametroDAO.crear(p);
            } else {
                parametroDAO.editar(p);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ParametroSalud getParametro() {

        ParametroSalud p = parametroDAO.getParametros();
        return p;
    }
}
