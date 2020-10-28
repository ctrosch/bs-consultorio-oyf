/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.rn;

import bs.entidad.dao.ParametroEntidadDAO;
import bs.entidad.modelo.ParametroEntidad;
import bs.stock.rn.*;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.ParametroStockDAO;
import bs.stock.modelo.ParametroStock;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless

public class ParametroEntidadRN {

    @EJB
    ParametroEntidadDAO parametroDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(ParametroEntidad p) throws ExcepcionGeneralSistema, Exception {

        if (parametroDAO.getParametros(p.getId()) == null) {
            parametroDAO.crear(p);
        } else {
            parametroDAO.editar(p);
        }

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ParametroEntidad getParametro(String codtip) throws Exception {

        ParametroEntidad p = parametroDAO.getParametros(codtip);
       
        if (p == null) {
            p = new ParametroEntidad(codtip);
            parametroDAO.crear(p);
        }
        return p;
    }
}
