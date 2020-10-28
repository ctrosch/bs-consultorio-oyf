/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.PeriodoDAO;
import bs.global.modelo.Periodo;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless

public class PeriodoRN implements Serializable {

    @EJB
    private PeriodoDAO periodoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Periodo periodo, boolean esNuevo) throws Exception {

        if (periodo.getId() == null) {
            periodoDAO.crear(periodo);
        } else {
            periodoDAO.editar(periodo);
        }
    }

    public void eliminar(Periodo periodo) throws Exception {

        periodoDAO.eliminar(periodo);

    }

    public Periodo getPeriodo(Integer id) {
        return periodoDAO.getPeriodo(id);
    }

    public List<Periodo> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return periodoDAO.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
