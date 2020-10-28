/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.FormulaDAO;
import bs.stock.modelo.Formula;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author ctrosch
 */
@Stateless
public class FormulaRN implements Serializable {

    @EJB
    private FormulaDAO formulaDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Formula e, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (formulaDAO.getObjeto(Formula.class, e.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una fórmula con el código " + e.getCodigo());
            }
            formulaDAO.crear(e);

        } else {

            formulaDAO.editar(e);
        }
    }

    public void eliminar(Formula deposito) throws Exception {

        formulaDAO.eliminar(deposito);

    }

    public List<Formula> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {

        return formulaDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDeBaja, cantMax);
    }

    public List<Formula> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja) {

        return formulaDAO.getListaByBusqueda(null, txtBusqueda, mostrarDeBaja, 15);
    }

    public Formula getFormula(String value) {

        return formulaDAO.getFormula(value);
    }

}
