/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.salud.dao.EstadoSaludDAO;
import bs.salud.modelo.EstadoSalud;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Guillermo
 */
@Stateless

public class EstadoSaludRN implements Serializable {

    @EJB
    private EstadoSaludDAO estadoSaludDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardar(EstadoSalud estadoSalud, boolean esNuevo) throws Exception {

        control(estadoSalud);

        if (esNuevo) {

            if (estadoSaludDAO.getObjeto(EstadoSalud.class, estadoSalud.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe " + estadoSalud.getClass().getSimpleName() + " con el código" + estadoSalud.getCodigo());
            }
            estadoSaludDAO.crear(estadoSalud);
        } else {
            estadoSaludDAO.editar(estadoSalud);
        }
    }

    private void control(EstadoSalud estadoSalud) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (estadoSalud.getCodigo() == null || estadoSalud.getCodigo().isEmpty()) {
            sErrores += "- El Código es Obligatorio \n ";
        }

        if (estadoSalud.getDescripcion() == null || estadoSalud.getDescripcion().isEmpty()) {
            sErrores += "- La Descripción es Obligatoria \n ";
        }

        if (estadoSalud.getColor() == null || estadoSalud.getColor().isEmpty()) {
            sErrores += "- El Color es Obligatorio \n ";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(EstadoSalud estadoSalud) throws Exception {

        estadoSaludDAO.eliminar(estadoSalud);

    }

    public EstadoSalud getEstadoSalud(String cod) {
        return estadoSaludDAO.getEstadoSalud(cod);
    }

    public List<EstadoSalud> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return estadoSaludDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }

    public List<EstadoSalud> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantidadRegistros) {

        return estadoSaludDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
    }
}
