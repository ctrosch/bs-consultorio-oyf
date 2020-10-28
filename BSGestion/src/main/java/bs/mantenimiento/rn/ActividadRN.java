/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.mantenimiento.dao.ActividadDAO;
import bs.mantenimiento.modelo.Actividad;
import bs.mantenimiento.modelo.ActividadActivador;
import bs.mantenimiento.modelo.ActividadAdjunto;
import bs.mantenimiento.modelo.ActividadRecurso;
import bs.mantenimiento.modelo.Subactividad;
import bs.stock.modelo.Producto;
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
public class ActividadRN implements Serializable {

    @EJB
    private ActividadDAO actividadDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Actividad guardar(Actividad actividad, boolean esNuevo) throws Exception {

        preSave(actividad);

        control(actividad, esNuevo);

        if (esNuevo) {

            if (actividadDAO.getObjeto(Actividad.class, actividad.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una Actividad con el código " + actividad.getCodigo());
            }
            actividadDAO.crear(actividad);

        } else {
            actividad = (Actividad) actividadDAO.editar(actividad);
        }

        return actividad;

    }

    public void preSave(Actividad actividad) {

        if (actividad.getSubactividades() != null && !actividad.getSubactividades().isEmpty()) {

            actividad.getSubactividades().forEach(i -> {
                i.setActividad(actividad);
            });

            /**
             * for(Subactividad i: actividad.getSubactividades()){
             * i.setActividad(actividad); }
             */
        }

        if (actividad.getRecursos() != null && !actividad.getRecursos().isEmpty()) {

            actividad.getRecursos().forEach(i -> {
                i.setActividad(actividad);
            });
        }

        if (actividad.getAdjuntos() != null && !actividad.getAdjuntos().isEmpty()) {

            actividad.getAdjuntos().forEach(i -> {
                i.setActividad(actividad);
            });
        }

        if (actividad.getActivador() != null && !actividad.getActivador().isEmpty()) {

            actividad.getAdjuntos().forEach(i -> {
                i.setActividad(actividad);
            });
        }

    }

    public void control(Actividad actividad, boolean esNuevo) throws ExcepcionGeneralSistema {

        String sError = "";

        if (actividad.getCodigo() == null || actividad.getCodigo().isEmpty()) {
            sError += "El código es obligatorio | ";
        }

        if (actividad.getDescripcion() == null || actividad.getDescripcion().isEmpty()) {
            sError += "La descripción es obligatoria | ";
        }

        if (!sError.isEmpty()) {
            throw new ExcepcionGeneralSistema(sError);
        }

    }

    public void eliminar(Actividad actividad) throws Exception {

        actividadDAO.eliminar(actividad);

    }

    public Actividad duplicar(Actividad a) throws Exception {

        if (a == null) {
            throw new ExcepcionGeneralSistema("Tipo de Mantenimiento nulo, nada para duplicar");
        }

        Actividad actividad = a.clone();
        actividad.setCodigo(a.getCodigo());
        actividad.setDescripcion(a.getDescripcion() + "( Duplicado)");

        return actividad;
    }

    public void nuevoSubActividad(Actividad actividad) throws ExcepcionGeneralSistema {

        if (actividad == null) {
            throw new ExcepcionGeneralSistema("No existe una Actividad seleccionado para agregarle una SubActividad");
        }

        if (actividad.getSubactividades() == null) {
            actividad.setSubactividades(new ArrayList<Subactividad>());
        }

        Subactividad subactividad = new Subactividad();
        subactividad.setNroitm(actividad.getSubactividades().size() + 1);
        subactividad.setActividad(actividad);

        actividad.getSubactividades().add(subactividad);

        reorganizarNroItem(actividad);

    }

    public void eliminarSubActividad(Actividad actividad, Subactividad subactividad) throws ExcepcionGeneralSistema, Exception {
        if (actividad == null) {
            throw new ExcepcionGeneralSistema("Actividad vacía, nada para eliminar");
        }
        if (subactividad == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para eliminar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (Subactividad item : actividad.getSubactividades()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == subactividad.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }
        i = 0;

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            Subactividad remove = actividad.getSubactividades().remove(indiceItemBorrar);
            if (remove != null) {
                if (actividad.getCodigo() != null && remove.getId() != null) {

                    actividadDAO.eliminar(Subactividad.class, remove.getId());

                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(actividad);
    }

    public void nuevoRecurso(Actividad actividad) throws ExcepcionGeneralSistema {

        if (actividad == null) {
            throw new ExcepcionGeneralSistema("No existe una Actividad seleccionada para agregarle un Recurso");
        }

        if (actividad.getRecursos() == null) {
            actividad.setRecursos(new ArrayList<ActividadRecurso>());
        }

        ActividadRecurso actividadRecurso = new ActividadRecurso();
        actividadRecurso.setNroitm(actividad.getRecursos().size() + 1);
        actividadRecurso.setActividad(actividad);

        actividad.getRecursos().add(actividadRecurso);

        reorganizarNroItem(actividad);

    }

    public void eliminarRecurso(Actividad actividad, ActividadRecurso actividadRecurso) throws ExcepcionGeneralSistema, Exception {
        if (actividad == null) {
            throw new ExcepcionGeneralSistema("Actividad vacía, nada para eliminar");
        }
        if (actividadRecurso == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para eliminar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ActividadRecurso item : actividad.getRecursos()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == actividadRecurso.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }
        i = 0;

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ActividadRecurso remove = actividad.getRecursos().remove(indiceItemBorrar);
            if (remove != null) {
                if (actividad.getCodigo() != null && remove.getId() != null) {

                    actividadDAO.eliminar(ActividadRecurso.class, remove.getId());

                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(actividad);
    }

    public void nuevoAdjunto(Actividad actividad) throws ExcepcionGeneralSistema {

        if (actividad == null) {
            throw new ExcepcionGeneralSistema("No existe una Actividad seleccionada para agregarle un Adjunto");
        }

        if (actividad.getAdjuntos() == null) {
            actividad.setAdjuntos(new ArrayList<ActividadAdjunto>());
        }

        ActividadAdjunto actividadAdjunto = new ActividadAdjunto();
        actividadAdjunto.setNroitm(actividad.getAdjuntos().size() + 1);
        actividadAdjunto.setActividad(actividad);
        actividadAdjunto.setDescripcion("Archivo " + actividadAdjunto.getNroitm());

        actividad.getAdjuntos().add(actividadAdjunto);

        reorganizarNroItem(actividad);

    }

    public void eliminarAdjunto(Actividad actividad, ActividadAdjunto actividadAdjunto) throws ExcepcionGeneralSistema, Exception {
        if (actividad == null) {
            throw new ExcepcionGeneralSistema("Actividad vacía, nada para eliminar");
        }
        if (actividadAdjunto == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para eliminar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ActividadAdjunto item : actividad.getAdjuntos()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == actividadAdjunto.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }
        i = 0;

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ActividadAdjunto remove = actividad.getAdjuntos().remove(indiceItemBorrar);
            if (remove != null) {
                if (actividad.getCodigo() != null && remove.getId() != null) {

                    actividadDAO.eliminar(ActividadAdjunto.class, remove.getId());

                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(actividad);
    }

    public void nuevoActivador(Actividad actividad) throws ExcepcionGeneralSistema {

        if (actividad == null) {
            throw new ExcepcionGeneralSistema("No existe una Actividad seleccionada para agregarle un activador");
        }

        if (actividad.getActivador() == null) {
            actividad.setActivador(new ArrayList<>());
        }

        ActividadActivador activador = new ActividadActivador();
        activador.setNroitm(actividad.getActivador().size() + 1);
        activador.setActividad(actividad);

        actividad.getActivador().add(activador);

        reorganizarNroItem(actividad);

    }

    public void eliminarActivador(Actividad actividad, ActividadActivador activador) throws ExcepcionGeneralSistema, Exception {
        if (actividad == null) {
            throw new ExcepcionGeneralSistema("Actividad vacía, nada para eliminar");
        }
        if (activador == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para eliminar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ActividadAdjunto item : actividad.getAdjuntos()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == activador.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }
        i = 0;

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ActividadActivador remove = actividad.getActivador().remove(indiceItemBorrar);
            if (remove != null) {
                if (actividad.getCodigo() != null && remove.getId() != null) {

                    actividadDAO.eliminar(ActividadActivador.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(actividad);
    }

    public void reorganizarNroItem(Actividad actividad) {

        //Reorganizamos los números de items
        int i;

        if (actividad.getSubactividades() != null) {

            i = 1;
            for (Subactividad item : actividad.getSubactividades()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (actividad.getAdjuntos() != null) {

            i = 1;
            for (ActividadAdjunto item : actividad.getAdjuntos()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (actividad.getRecursos() != null) {

            i = 1;
            for (ActividadRecurso item : actividad.getRecursos()) {
                item.setNroitm(i);
                i++;
            }
        }

        if (actividad.getActivador() != null) {

            i = 1;
            for (ActividadActivador item : actividad.getActivador()) {
                item.setNroitm(i);
                i++;
            }
        }

    }

    public void asignarProducto(ActividadRecurso a, Producto producto) throws ExcepcionGeneralSistema {

        if (a == null || producto == null) {
            return;
        }

        a.setDescripcion(producto.getDescripcion());

    }

    public Actividad getActividad(String codigo) {

        return actividadDAO.getActividad(codigo);
    }

    public List<Actividad> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return actividadDAO.getActividadByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<Actividad> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return actividadDAO.getActividadByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

}
