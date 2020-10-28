/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.ParametroStockDAO;
import bs.stock.modelo.ParametroStock;
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

public class ParametroStockRN implements Serializable {

    @EJB
    ParametroStockDAO parametroDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(ParametroStock p) throws ExcepcionGeneralSistema, Exception {

        if (p.getId().equals("01")) {

            if (parametroDAO.getParametros() == null) {
                parametroDAO.crear(p);
            } else {
                parametroDAO.editar(p);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ParametroStock getParametro() throws Exception {

        ParametroStock p = parametroDAO.getParametros();

        if (p == null) {
            p = new ParametroStock("01");
            parametroDAO.crear(p);
        }
        return p;
    }
}
