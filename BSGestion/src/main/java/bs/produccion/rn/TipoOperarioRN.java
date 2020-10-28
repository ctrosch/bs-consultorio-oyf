/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.produccion.dao.TipoOperarioDAO;
import bs.produccion.modelo.TipoOperario;
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

public class TipoOperarioRN implements Serializable {

    @EJB
    private TipoOperarioDAO operarioDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(TipoOperario operario, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (operarioDAO.getObjeto(TipoOperario.class, operario.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe unidad de medida con el código" + operario.getCodigo());
            }
            operarioDAO.crear(operario);
        } else {
            operarioDAO.editar(operario);
        }
    }

    public void eliminar(TipoOperario operario) throws Exception {

        operarioDAO.eliminar(operario);

    }

    public TipoOperario getTipoOperario(String cod) {
        return operarioDAO.getTipoOperario(cod);
    }

    public List<TipoOperario> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return operarioDAO.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
