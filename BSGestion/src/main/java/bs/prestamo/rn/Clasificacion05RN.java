/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.prestamo.dao.Clasificacion05DAO;
import bs.prestamo.modelo.Clasificacion05;
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

public class Clasificacion05RN implements Serializable {

    @EJB
    private Clasificacion05DAO clasificacion05DAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Clasificacion05 clasificacion05, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (clasificacion05DAO.getObjeto(Clasificacion05.class, clasificacion05.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe " + clasificacion05.getClass().getSimpleName() + " con el código" + clasificacion05.getCodigo());
            }
            clasificacion05DAO.crear(clasificacion05);
        } else {
            clasificacion05DAO.editar(clasificacion05);
        }
    }

    public void eliminar(Clasificacion05 clasificacion05) throws Exception {

        clasificacion05DAO.eliminar(clasificacion05);

    }

    public Clasificacion05 getClasificacion05(String cod) {
        return clasificacion05DAO.getClasificacion05(cod);
    }

    public List<Clasificacion05> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return clasificacion05DAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<Clasificacion05> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return clasificacion05DAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
