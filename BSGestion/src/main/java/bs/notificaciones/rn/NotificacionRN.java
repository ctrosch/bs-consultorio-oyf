/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.notificaciones.rn;

import bs.educacion.modelo.Materia;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.notificaciones.dao.NotificacionDAO;
import bs.notificaciones.modelo.ItemNotificacionGrupo;
import bs.notificaciones.modelo.ItemNotificacionUsuario;
import bs.notificaciones.modelo.Notificacion;
import java.io.Serializable;
import java.util.ArrayList;
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
public class NotificacionRN implements Serializable {

    @EJB
    private NotificacionDAO notificacionDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Notificacion guardar(Notificacion notificacion, boolean esNuevo) throws Exception {

        reorganizarNroItem(notificacion);
        preSave(notificacion);
        control(notificacion);

        if (esNuevo) {

            if (notificacionDAO.getObjeto(Materia.class, notificacion.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una Notificación con el código " + notificacion.getCodigo());
            }
            notificacionDAO.crear(notificacion);

        } else {
            notificacion = (Notificacion) notificacionDAO.editar(notificacion);
        }

        return notificacion;

    }

    private void preSave(Notificacion notificacion) throws ExcepcionGeneralSistema, Exception {

        if (notificacion.getUsuarios() != null) {

            for (ItemNotificacionUsuario item : notificacion.getUsuarios()) {

                item.setNotificacion(notificacion);
            }
        }

        if (notificacion.getGrupos() != null) {

            for (ItemNotificacionGrupo item : notificacion.getGrupos()) {

                item.setNotificacion(notificacion);
            }
        }
    }

    private void control(Notificacion notificacion) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (notificacion.getCodigo() == null || notificacion.getCodigo().isEmpty()) {
            sErrores += "- El código es obligatorio \n ";
        }

        if (notificacion.getDescripcion() == null || notificacion.getDescripcion().isEmpty()) {
            sErrores += "- La descripción es obligatoria \n ";
        }

        if (notificacion.getModulo() == null) {
            sErrores += "- El módulo es obligatorio \n ";
        }
        if (notificacion.getReporte() == null) {
            sErrores += "- El reporte es obligatorio \n ";
        }

        if (notificacion.getMensajePush() == null || notificacion.getMensajePush().isEmpty()) {
            sErrores += "- Envia por mensaje Push es obligatorio  \n ";
        }

        if (notificacion.getEmail() == null || notificacion.getEmail().isEmpty()) {
            sErrores += "- Envia por email es obligatorio  \n ";
        }

        if (notificacion.getWhatsapp() == null || notificacion.getWhatsapp().isEmpty()) {
            sErrores += "- Envia por whatsApp es obligatorio  \n ";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(Notificacion notificacion) throws Exception {

        notificacionDAO.eliminar(notificacion);

    }

    public Notificacion duplicar(Notificacion n) throws Exception {

        if (n == null) {
            throw new ExcepcionGeneralSistema("Notificación nula, nada para duplicar");
        }

        Notificacion notificacion = n.clone();
        notificacion.setCodigo(n.getCodigo());
        notificacion.setDescripcion(n.getDescripcion() + "( Duplicado)");
        notificacion.setEmail("N");
        notificacion.setWhatsapp("N");
        notificacion.setMensajePush("N");
        notificacion.setGrupos(new ArrayList<ItemNotificacionGrupo>());
        notificacion.setUsuarios(new ArrayList<ItemNotificacionUsuario>());

        return notificacion;
    }

    public Notificacion getNotificacion(String codigo) {

        return notificacionDAO.getNotificacion(codigo);
    }

    public List<Notificacion> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return notificacionDAO.getNotificacionByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<Notificacion> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return notificacionDAO.getNotificacionByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

    public String asignarCodigoModulo(Notificacion notificacion) {

        if (notificacion.getModulo() != null) {
            return notificacion.getModulo().getCodigo();
        }
        return "";
    }

    public Notificacion asignarModulo(Notificacion notificacion) {

        notificacion.setReporte(null);
        return notificacion;
    }

    public void nuevoItemUsuario(Notificacion notificacion) throws ExcepcionGeneralSistema {

        if (notificacion == null) {
            throw new ExcepcionGeneralSistema("No existe una Notificación seleccionada para agregar un Usuario");
        }

        if (notificacion.getUsuarios() == null) {
            notificacion.setUsuarios(new ArrayList<ItemNotificacionUsuario>());

        }

        ItemNotificacionUsuario item = new ItemNotificacionUsuario();
        item.setNroitm(notificacion.getUsuarios().size() + 1);
        item.setNotificacion(notificacion);

        notificacion.getUsuarios().add(item);

    }

    public void eliminarItemUsuario(Notificacion notificacion, ItemNotificacionUsuario item) throws ExcepcionGeneralSistema {

        if (notificacion == null) {
            throw new ExcepcionGeneralSistema("No existe una Notificación seleccionada para quitar un Usuario");
        }

        if (item == null) {
            throw new ExcepcionGeneralSistema("No tiene ningún item seleccionado a quitar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemNotificacionUsuario u : notificacion.getUsuarios()) {

            if (u.getNroitm() >= 0) {

                if (u.getNroitm() == item.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemNotificacionUsuario remove = notificacion.getUsuarios().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    notificacionDAO.eliminar(ItemNotificacionUsuario.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(notificacion);

    }

    public void nuevoItemGrupo(Notificacion notificacion) throws ExcepcionGeneralSistema {

        if (notificacion == null) {
            throw new ExcepcionGeneralSistema("No existe una Notificación seleccionada para agregar un Grupo");
        }

        if (notificacion.getGrupos() == null) {
            notificacion.setGrupos(new ArrayList<ItemNotificacionGrupo>());

        }

        ItemNotificacionGrupo item = new ItemNotificacionGrupo();
        item.setNroitm(notificacion.getGrupos().size() + 1);
        item.setNotificacion(notificacion);

        notificacion.getGrupos().add(item);

    }

    public void eliminarItemGrupo(Notificacion notificacion, ItemNotificacionGrupo item) throws ExcepcionGeneralSistema {

        if (notificacion == null) {
            throw new ExcepcionGeneralSistema("No existe una Notificación seleccionada para quitar un Grupo");
        }

        if (item == null) {
            throw new ExcepcionGeneralSistema("No tiene ningún item seleccionado a quitar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemNotificacionGrupo g : notificacion.getGrupos()) {

            if (g.getNroitm() >= 0) {

                if (g.getNroitm() == item.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemNotificacionGrupo remove = notificacion.getGrupos().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    notificacionDAO.eliminar(ItemNotificacionGrupo.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(notificacion);

    }

    public void reorganizarNroItem(Notificacion notificacion) {

        //Reorganizamos los números de items
        int i;

        if (notificacion.getUsuarios() != null) {

            i = 1;
            for (ItemNotificacionUsuario item : notificacion.getUsuarios()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (notificacion.getGrupos() != null) {

            i = 1;
            for (ItemNotificacionGrupo item : notificacion.getGrupos()) {
                item.setNroitm(i);
                i++;
            }
        }

    }
}
