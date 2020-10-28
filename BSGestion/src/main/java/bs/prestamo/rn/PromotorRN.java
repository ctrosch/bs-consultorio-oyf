/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.rn;

import bs.prestamo.dao.PromotorDAO;
import bs.prestamo.modelo.Promotor;
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

public class PromotorRN implements Serializable {

    @EJB
    private PromotorDAO promotorDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Promotor clasificacion01, boolean esNuevo) throws Exception {

        if (clasificacion01.getId() == null) {
            promotorDAO.crear(clasificacion01);
        } else {
            promotorDAO.editar(clasificacion01);
        }
    }

    public void eliminar(Promotor clasificacion01) throws Exception {

        promotorDAO.eliminar(clasificacion01);

    }

    public Promotor getPromotor(Integer id) {
        return promotorDAO.getPromotor(id);
    }

    public List<Promotor> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return promotorDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<Promotor> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return promotorDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
