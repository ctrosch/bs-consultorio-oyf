/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.prestamo.dao.Clasificacion03DAO;
import bs.prestamo.modelo.Clasificacion03;
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
public class Clasificacion03RN implements Serializable {

    @EJB
    private Clasificacion03DAO clasificacion03DAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Clasificacion03 clasificacion03, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (clasificacion03DAO.getObjeto(Clasificacion03.class, clasificacion03.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe " + clasificacion03.getClass().getSimpleName() + " con el código" + clasificacion03.getCodigo());
            }
            clasificacion03DAO.crear(clasificacion03);
        } else {
            clasificacion03DAO.editar(clasificacion03);
        }
    }

    public void eliminar(Clasificacion03 clasificacion03) throws Exception {

        clasificacion03DAO.eliminar(clasificacion03);

    }

    public Clasificacion03 getClasificacion03(String cod) {
        return clasificacion03DAO.getClasificacion03(cod);
    }

    public List<Clasificacion03> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return clasificacion03DAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<Clasificacion03> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return clasificacion03DAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
