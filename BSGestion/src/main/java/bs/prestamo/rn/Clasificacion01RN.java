/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.prestamo.dao.Clasificacion01DAO;
import bs.prestamo.modelo.Clasificacion01;
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

public class Clasificacion01RN implements Serializable {

    @EJB
    private Clasificacion01DAO clasificacion01DAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(Clasificacion01 clasificacion01, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (clasificacion01DAO.getObjeto(Clasificacion01.class, clasificacion01.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe " + clasificacion01.getClass().getSimpleName() + " con el código" + clasificacion01.getCodigo());
            }
            clasificacion01DAO.crear(clasificacion01);
        } else {
            clasificacion01DAO.editar(clasificacion01);
        }
    }

    public void eliminar(Clasificacion01 clasificacion01) throws Exception {

        clasificacion01DAO.eliminar(clasificacion01);

    }

    public Clasificacion01 getClasificacion01(String cod) {
        return clasificacion01DAO.getClasificacion01(cod);
    }

    public List<Clasificacion01> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return clasificacion01DAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<Clasificacion01> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return clasificacion01DAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
