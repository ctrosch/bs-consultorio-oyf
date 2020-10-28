/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.educacion.dao.EstadoEducacionDAO;
import bs.educacion.modelo.EstadoEducacion;
import bs.global.excepciones.ExcepcionGeneralSistema;
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

public class EstadoEducacionRN implements Serializable {

    @EJB
    private EstadoEducacionDAO estadoEducacionDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(EstadoEducacion estadoEducacion, boolean esNuevo) throws Exception {

        control(estadoEducacion);

        if (esNuevo) {

            if (estadoEducacionDAO.getObjeto(EstadoEducacion.class, estadoEducacion.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe " + estadoEducacion.getClass().getSimpleName() + " con el código" + estadoEducacion.getCodigo());
            }
            estadoEducacionDAO.crear(estadoEducacion);
        } else {
            estadoEducacionDAO.editar(estadoEducacion);
        }
    }

    private void control(EstadoEducacion estadoEducacion) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (estadoEducacion.getCodigo() == null || estadoEducacion.getCodigo().isEmpty()) {
            sErrores += "- El Código es Obligatorio \n ";
        }

        if (estadoEducacion.getDescripcion() == null || estadoEducacion.getDescripcion().isEmpty()) {
            sErrores += "- La Descripción es Obligatoria \n ";
        }

        if (estadoEducacion.getColor() == null || estadoEducacion.getColor().isEmpty()) {
            sErrores += "- El Color es Obligatorio \n ";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(EstadoEducacion estadoEducacion) throws Exception {

        estadoEducacionDAO.eliminar(estadoEducacion);

    }

    public EstadoEducacion getEstadoEducacion(String cod) {
        return estadoEducacionDAO.getEstadoEducacion(cod);
    }

    public List<EstadoEducacion> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return estadoEducacionDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<EstadoEducacion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return estadoEducacionDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
