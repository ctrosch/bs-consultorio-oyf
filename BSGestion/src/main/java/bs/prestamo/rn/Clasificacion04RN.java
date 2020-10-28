/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.prestamo.dao.Clasificacion04DAO;
import bs.prestamo.modelo.Clasificacion04;
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

public class Clasificacion04RN implements Serializable {

    @EJB
    private Clasificacion04DAO clasificacion04DAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Clasificacion04 clasificacion04, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (clasificacion04DAO.getObjeto(Clasificacion04.class, clasificacion04.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe " + clasificacion04.getClass().getSimpleName() + " con el código" + clasificacion04.getCodigo());
            }
            clasificacion04DAO.crear(clasificacion04);
        } else {
            clasificacion04DAO.editar(clasificacion04);
        }
    }

    public void eliminar(Clasificacion04 clasificacion04) throws Exception {

        clasificacion04DAO.eliminar(clasificacion04);

    }

    public Clasificacion04 getClasificacion04(String cod) {
        return clasificacion04DAO.getClasificacion04(cod);
    }

    public List<Clasificacion04> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return clasificacion04DAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<Clasificacion04> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return clasificacion04DAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
