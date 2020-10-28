/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.mantenimiento.dao.EstadoDAO;
import bs.mantenimiento.modelo.Estado;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author GUILLERMO
 */
@Stateless
public class EstadoRN implements Serializable {

    @EJB
    private EstadoDAO estadoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Estado guardar(Estado estado, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (estadoDAO.getObjeto(Estado.class, estado.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un Estado con el código " + estado.getCodigo());
            }
            estadoDAO.crear(estado);

        } else {
            estado = (Estado) estadoDAO.editar(estado);
        }

        return estado;

    }

    public void eliminar(Estado estado) throws Exception {

        estadoDAO.eliminar(estado);

    }

    public Estado duplicar(Estado e) throws Exception {

        if (e == null) {
            throw new ExcepcionGeneralSistema("Estado nulo, nada para duplicar");
        }

        Estado estado = e.clone();
        estado.setCodigo(e.getCodigo());
        estado.setDescripcion(e.getDescripcion() + "( Duplicado)");

        return estado;
    }

    public Estado getEstado(String codigo) {

        return estadoDAO.getEstado(codigo);
    }

    public List<Estado> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return estadoDAO.getEstadoByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<Estado> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return estadoDAO.getEstadoByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

}
