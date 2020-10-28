/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.prestamo.dao.AmortizacionPrestamoDAO;
import bs.prestamo.modelo.AmortizacionPrestamo;
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

public class AmortizacionPrestamoRN implements Serializable {

    @EJB
    private AmortizacionPrestamoDAO amortizacionDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(AmortizacionPrestamo amortizacion, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (amortizacionDAO.getObjeto(AmortizacionPrestamo.class, amortizacion.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe " + amortizacion.getClass().getSimpleName() + " con el código" + amortizacion.getCodigo());
            }
            amortizacionDAO.crear(amortizacion);
        } else {
            amortizacionDAO.editar(amortizacion);
        }
    }

    public void eliminar(AmortizacionPrestamo amortizacion) throws Exception {

        amortizacionDAO.eliminar(amortizacion);

    }

    public AmortizacionPrestamo getAmortizacion(String cod) {
        return amortizacionDAO.getAmortizacion(cod);
    }

    public List<AmortizacionPrestamo> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return amortizacionDAO.getListaByBusqueda(txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
