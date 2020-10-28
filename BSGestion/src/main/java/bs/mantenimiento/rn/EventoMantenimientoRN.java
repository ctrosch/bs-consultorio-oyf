/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.mantenimiento.dao.EventoMantenimientoDAO;
import bs.mantenimiento.modelo.EventoMantenimiento;
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
public class EventoMantenimientoRN implements Serializable {

    @EJB
    private EventoMantenimientoDAO tipoMantenimientoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EventoMantenimiento guardar(EventoMantenimiento tipoMantenimiento, boolean esNuevo) throws Exception {

        if (esNuevo) {

            if (tipoMantenimientoDAO.getObjeto(EventoMantenimiento.class, tipoMantenimiento.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un evento de mantenimiento con el código " + tipoMantenimiento.getCodigo());
            }
            tipoMantenimientoDAO.crear(tipoMantenimiento);

        } else {
            tipoMantenimiento = (EventoMantenimiento) tipoMantenimientoDAO.editar(tipoMantenimiento);
        }

        return tipoMantenimiento;

    }

    public EventoMantenimiento duplicar(EventoMantenimiento a) throws Exception {

        if (a == null) {
            throw new ExcepcionGeneralSistema("Evento de Mantenimiento nulo, nada para duplicar");
        }

        EventoMantenimiento tipoMantenimiento = a.clone();
        tipoMantenimiento.setCodigo(a.getCodigo());
        tipoMantenimiento.setDescripcion(a.getDescripcion() + "( Duplicado)");

        return tipoMantenimiento;
    }

    public EventoMantenimiento getEventoMantenimiento(String codigo) {

        return tipoMantenimientoDAO.getEventoMantenimiento(codigo);
    }

    public List<EventoMantenimiento> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoMantenimientoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<EventoMantenimiento> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return tipoMantenimientoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

}
