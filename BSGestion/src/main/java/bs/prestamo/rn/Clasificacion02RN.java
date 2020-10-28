/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.prestamo.dao.Clasificacion02DAO;
import bs.prestamo.modelo.Clasificacion02;
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

public class Clasificacion02RN implements Serializable {

    @EJB
    private Clasificacion02DAO clasificacion02DAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Clasificacion02 clasificacion02, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (clasificacion02DAO.getObjeto(Clasificacion02.class, clasificacion02.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe " + clasificacion02.getClass().getSimpleName() + " con el código" + clasificacion02.getCodigo());
            }
            clasificacion02DAO.crear(clasificacion02);
        } else {
            clasificacion02DAO.editar(clasificacion02);
        }
    }

    public void eliminar(Clasificacion02 clasificacion02) throws Exception {

        clasificacion02DAO.eliminar(clasificacion02);

    }

    public Clasificacion02 getClasificacion02(String cod) {
        return clasificacion02DAO.getClasificacion02(cod);
    }

    public List<Clasificacion02> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return clasificacion02DAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<Clasificacion02> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return clasificacion02DAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
