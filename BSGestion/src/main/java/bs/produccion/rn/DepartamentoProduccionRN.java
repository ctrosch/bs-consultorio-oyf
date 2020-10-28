/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.produccion.dao.DepartamentoProduccionDAO;
import bs.produccion.modelo.DepartamentoProduccion;
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
public class DepartamentoProduccionRN implements Serializable {

    @EJB
    private DepartamentoProduccionDAO departamentoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(DepartamentoProduccion d, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (departamentoDAO.getObjeto(DepartamentoProduccion.class, d.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una entidad con el código" + d.getCodigo());
            }
            departamentoDAO.crear(d);

        } else {
            departamentoDAO.editar(d);
        }
    }

    public void eliminar(DepartamentoProduccion deposito) throws Exception {

        departamentoDAO.eliminar(deposito);
    }

    public List<DepartamentoProduccion> getLista(boolean mostrarDebaja) {

        return departamentoDAO.getListaByBusqueda("", mostrarDebaja, 0);
    }

    public DepartamentoProduccion getDepartamentoProduccion(String codigo) {
        return departamentoDAO.getDepartamentoProduccion(codigo);
    }

    public List<DepartamentoProduccion> getLista() {
        return departamentoDAO.getListaByBusqueda("", false, 0);
    }

    public List<DepartamentoProduccion> getListaByBusqueda(String txtBusqueda, boolean mostrarDeBaja, int cantMax) {

        return departamentoDAO.getListaByBusqueda(txtBusqueda, mostrarDeBaja, cantMax);
    }
}
