/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.rn;

import bs.prestamo.dao.LineaCreditoDAO;
import bs.prestamo.modelo.LineaCredito;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless

public class LineaCreditoRN implements Serializable {

    @EJB
    private LineaCreditoDAO lineaCreditoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(LineaCredito lineaCredito, boolean esNuevo) throws Exception {

        if (lineaCredito.getId() == null) {
            lineaCreditoDAO.crear(lineaCredito);
        } else {
            lineaCreditoDAO.editar(lineaCredito);
        }
    }

    public void eliminar(LineaCredito lineaCredito) throws Exception {

        lineaCreditoDAO.eliminar(lineaCredito);

    }

    public LineaCredito getLineaCredito(Integer id) {
        return lineaCreditoDAO.getLineaCredito(id);
    }

    public List<LineaCredito> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return lineaCreditoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<LineaCredito> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return lineaCreditoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
