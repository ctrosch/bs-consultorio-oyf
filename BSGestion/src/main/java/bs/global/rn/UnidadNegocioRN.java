/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.rn;

import bs.global.dao.UnidadNegocioDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.UnidadNegocio;
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
public class UnidadNegocioRN implements Serializable {

    @EJB
    private UnidadNegocioDAO unidadNegocioDAO;

    public UnidadNegocio getUnidadNegocio(String value) {
        return unidadNegocioDAO.getObjeto(UnidadNegocio.class, value);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public UnidadNegocio guardar(UnidadNegocio unidadNegocio, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (unidadNegocioDAO.getObjeto(UnidadNegocio.class, unidadNegocio.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe unidad de negocio con el código" + unidadNegocio.getCodigo());
            }
            unidadNegocioDAO.crear(unidadNegocio);
        } else {
            unidadNegocio = (UnidadNegocio) unidadNegocioDAO.editar(unidadNegocio);
        }

        return unidadNegocio;
    }

    public List<UnidadNegocio> getLista() {
        return unidadNegocioDAO.getLista();
    }

    public List<UnidadNegocio> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja) {

        return unidadNegocioDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, 15);
    }

    public List<UnidadNegocio> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return unidadNegocioDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<UnidadNegocio> getLista(boolean mostrarDebaja) {

        return unidadNegocioDAO.getLista(mostrarDebaja);

    }

    public void eliminar(UnidadNegocio s) throws Exception {

        unidadNegocioDAO.eliminar(s);

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
