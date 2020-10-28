/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.stock.dao.GrupoStockDAO;
import bs.stock.modelo.GrupoStock;
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
public class GrupoStockRN implements Serializable {

    @EJB
    private GrupoStockDAO grupoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(GrupoStock familia, boolean esNuevo) throws Exception {

        if (familia.getId() == null) {
            grupoDAO.crear(familia);
        } else {
            grupoDAO.editar(familia);
        }
    }

    public void eliminar(GrupoStock familia) throws Exception {

        grupoDAO.eliminar(familia);

    }

    public List<GrupoStock> getLista(boolean mostrarDebaja) {

        return grupoDAO.getLista(mostrarDebaja);

    }

    public GrupoStock getFamilia(Integer id) {
        return grupoDAO.getFamilia(id);
    }

    public List<GrupoStock> getLista() {
        return grupoDAO.getLista();
    }

    public List<GrupoStock> getLista(int maxResults, int firstResult) {
        return grupoDAO.getLista(maxResults, firstResult);
    }

    public List<GrupoStock> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja) {

        return grupoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDeBaja, 15);
    }

    public List<GrupoStock> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDeBaja, int cantMax) {

        return grupoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDeBaja, cantMax);
    }

}
