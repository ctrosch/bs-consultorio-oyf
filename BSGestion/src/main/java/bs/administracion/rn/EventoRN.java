/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.rn;

import bs.administracion.dao.EventoDAO;
import bs.administracion.modelo.Evento;
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
 * @author GUILLERMO
 */
@Stateless
public class EventoRN implements Serializable {

    @EJB
    private EventoDAO eventoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Evento guardar(Evento evento, boolean esNuevo) throws Exception {

        control(evento);

        if (esNuevo) {

            if (eventoDAO.getObjeto(Evento.class, evento.getId()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un Evento con el id " + evento.getId());
            }
            eventoDAO.crear(evento);

        } else {
            evento = (Evento) eventoDAO.editar(evento);
        }

        return evento;

    }

    private void control(Evento evento) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (evento.getNombre() == null || evento.getNombre().isEmpty()) {
            sErrores += "- La descripción es Obligatoria \n ";
        }

        if (evento.getModulo() == null) {
            sErrores += "- El Módulo es Obligatorio \n ";
        }

        if (evento.getFecha() == null) {
            sErrores += "- La fecha es Obligatoria \n ";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(Evento evento) throws Exception {

        eventoDAO.eliminar(evento);

    }

    public Evento duplicar(Evento e) throws Exception {

        if (e == null) {
            throw new ExcepcionGeneralSistema(" nulo, nada para duplicar");
        }

        Evento evento = e.clone();
        evento.setId(eventoDAO.getMaxCodigo());

        evento.setNombre(e.getNombre() + "( Duplicado)");

        return evento;
    }

    public Evento getEvento(Integer id) {

        return eventoDAO.getEvento(id);
    }

    public List<Evento> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return eventoDAO.getEventoByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<Evento> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return eventoDAO.getEventoByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

    public Integer getProximoCodigo() {
        int nroId = eventoDAO.getMaxCodigo();
        return nroId;

    }
}
