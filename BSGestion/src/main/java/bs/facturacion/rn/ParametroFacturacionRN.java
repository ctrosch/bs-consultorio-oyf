/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.rn;

import bs.administracion.rn.ParametrosRN;
import bs.facturacion.dao.ParametroFacturacionDAO;
import bs.facturacion.modelo.ParametroFacturacion;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.rn.MonedaRN;
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
public class ParametroFacturacionRN implements Serializable {

    @EJB
    ParametroFacturacionDAO parametroDAO;
    @EJB
    MonedaRN monedaRN;
    @EJB
    protected ParametrosRN parametrosRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(ParametroFacturacion p) throws ExcepcionGeneralSistema, Exception {

        if (p.getId().equals("01")) {

            if (parametroDAO.getParametros() == null) {
                parametroDAO.crear(p);
            } else {
                parametroDAO.editar(p);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ParametroFacturacion getParametro() {

        ParametroFacturacion p = parametroDAO.getParametros();

        if (p == null) {
            p = new ParametroFacturacion("01");
            p.setMonedaRegistracion(monedaRN.getMoneda(parametrosRN.getParametro().getCodigoMonedaPrimaria()));
            p.setMonedaSecundaria(monedaRN.getMoneda(parametrosRN.getParametro().getCodigoMonedaSecundaria()));
            parametroDAO.crear(p);
        }
        return p;
    }
}
