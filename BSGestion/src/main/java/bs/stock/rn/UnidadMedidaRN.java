/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.stock.dao.UnidadMedidaDAO;
import bs.stock.modelo.UnidadMedida;
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
public class UnidadMedidaRN implements Serializable {

    @EJB
    private UnidadMedidaDAO unidadMedidaDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(UnidadMedida unidadMedida, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (unidadMedidaDAO.getObjeto(UnidadMedida.class, unidadMedida.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe unidad de medida con el código" + unidadMedida.getCodigo());
            }
            unidadMedidaDAO.crear(unidadMedida);
        } else {
            unidadMedidaDAO.editar(unidadMedida);
        }
    }

    public void eliminar(UnidadMedida unidadMedida) throws Exception {

        unidadMedidaDAO.eliminar(unidadMedida);

    }

    public UnidadMedida getUnidadMedida(String cod) {
        return unidadMedidaDAO.getUnidadMedida(cod);
    }

    public List<UnidadMedida> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return unidadMedidaDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<UnidadMedida> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return unidadMedidaDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
