/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.administracion.rn.ParametrosRN;
import bs.global.dao.MonedaDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Moneda;
import bs.global.modelo.MonedaValores;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author ctrosch
 */
@Stateless
public class MonedaRN implements Serializable {

    @EJB
    private MonedaDAO monedaDAO;
    @EJB
    protected ParametrosRN parametrosRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Moneda guardar(Moneda moneda, boolean esNuevo) throws Exception {

        if (esNuevo) {
            if (monedaDAO.getObjeto(Moneda.class, moneda.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya extiste una entidad con el código" + moneda.getCodigo());
            }
            monedaDAO.crear(moneda);
        } else {
            moneda = (Moneda) monedaDAO.editar(moneda);
        }
        return moneda;
    }

    public Moneda getMoneda(String codcof) {
        return monedaDAO.getMoneda(codcof);
    }

    public List<Moneda> getLista() {
        return monedaDAO.getListaByBusqueda("", false, 15);
    }

    public List<Moneda> getListaConValores() {

        List<Moneda> monedas = monedaDAO.getListaByBusqueda("", false, 15);

        for (Moneda m : monedas) {

            if (m.getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
                m.setCotizacion(new BigDecimal(BigInteger.ONE).setScale(2));
            } else {
                m.setCotizacion(monedaDAO.getCotizacionDia(m.getCodigo()).getCotizacion().setScale(2));
            }
        }
        return monedas;
    }

    public BigDecimal getCotizacionDia(String codCoeficiente) {

        if (codCoeficiente.equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
            return BigDecimal.ONE;
        } else {
            return monedaDAO.getCotizacionDia(codCoeficiente).getCotizacion().setScale(2);
        }
    }

    public MonedaValores getUltimaCotizacion(String codCoeficiente) {

        if (codCoeficiente.equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
            return new MonedaValores();
        } else {
            return monedaDAO.getCotizacionDia(codCoeficiente);
        }
    }

    public void eliminar(Moneda moneda) throws Exception {
        monedaDAO.eliminar(moneda);
    }

}
