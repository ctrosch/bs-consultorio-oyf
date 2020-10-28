/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.administracion.rn.ParametrosRN;
import bs.educacion.dao.ParametroEducacionDAO;
import bs.educacion.modelo.ParametroEducacion;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.rn.MonedaRN;
import bs.global.rn.PeriodoRN;
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
public class ParametroEducacionRN implements Serializable {

    @EJB
    ParametroEducacionDAO parametroDAO;
    @EJB
    MonedaRN monedaRN;
    @EJB
    PeriodoRN periodoRN;
    @EJB
    EstadoEducacionRN estadoRN;
    @EJB
    protected ParametrosRN parametrosRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(ParametroEducacion p) throws ExcepcionGeneralSistema, Exception {

        if (p.getId().equals("01")) {

            if (parametroDAO.getParametros() == null) {
                parametroDAO.crear(p);
            } else {
                parametroDAO.editar(p);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ParametroEducacion getParametro() {

        ParametroEducacion p = parametroDAO.getParametros();

        if (p == null) {
            p = new ParametroEducacion("01");
            p.setMonedaRegistracion(monedaRN.getMoneda(parametrosRN.getParametro().getCodigoMonedaPrimaria()));
            p.setMonedaSecundaria(monedaRN.getMoneda(parametrosRN.getParametro().getCodigoMonedaSecundaria()));
            p.setPeriodo(periodoRN.getPeriodo(6));
            p.setEstado(estadoRN.getEstadoEducacion("A"));
            parametroDAO.crear(p);
        }
        return p;
    }
}
