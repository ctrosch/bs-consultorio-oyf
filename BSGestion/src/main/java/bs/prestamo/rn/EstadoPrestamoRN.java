/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.prestamo.dao.EstadoPrestamoDAO;
import bs.prestamo.modelo.EstadoPrestamo;
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

public class EstadoPrestamoRN implements Serializable {

    @EJB
    private EstadoPrestamoDAO estadoPrestamoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(EstadoPrestamo estadoPrestamo, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (estadoPrestamoDAO.getObjeto(EstadoPrestamo.class, estadoPrestamo.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe " + estadoPrestamo.getClass().getSimpleName() + " con el código" + estadoPrestamo.getCodigo());
            }
            estadoPrestamoDAO.crear(estadoPrestamo);
        } else {
            estadoPrestamoDAO.editar(estadoPrestamo);
        }
    }

    public void eliminar(EstadoPrestamo estadoPrestamo) throws Exception {

        estadoPrestamoDAO.eliminar(estadoPrestamo);

    }

    public EstadoPrestamo getEstadoPrestamo(String cod) {
        return estadoPrestamoDAO.getEstadoPrestamo(cod);
    }

    public List<EstadoPrestamo> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return estadoPrestamoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<EstadoPrestamo> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return estadoPrestamoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
