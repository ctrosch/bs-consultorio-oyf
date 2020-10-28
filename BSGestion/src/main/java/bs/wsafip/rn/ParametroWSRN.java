/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.wsafip.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.wsafip.dao.ParametroWSDAO;
import bs.wsafip.modelo.ParametroWS;
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
public class ParametroWSRN implements Serializable {

    @EJB
    private ParametroWSDAO parametroWSDAO;

    public ParametroWS getParametro(String codigo) {

        return parametroWSDAO.getObjeto(codigo);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(ParametroWS p) throws ExcepcionGeneralSistema {

        if (p.getCodigo().equals("01")) {

            if (parametroWSDAO.getObjeto(p.getCodigo()) == null) {
                parametroWSDAO.crear(p);
            } else {
                parametroWSDAO.editar(p);
            }
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
