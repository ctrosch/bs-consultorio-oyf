/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.educacion.dao.CursoDAO;
import bs.educacion.modelo.Curso;
import bs.educacion.modelo.ItemCursoMateria;
import bs.educacion.modelo.MovimientoEducacion;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
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
public class CursoRN implements Serializable {

    @EJB
    private CursoDAO cursoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Curso guardar(Curso curso, boolean esNuevo) throws Exception {
        preSave(curso, esNuevo);
        control(curso, esNuevo);
        reorganizarNroItem(curso);
        if (esNuevo) {

            if (cursoDAO.getObjeto(Curso.class, curso.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un curso con ese codigo " + curso.getCodigo());
            }

            cursoDAO.crear(curso);

        } else {

            curso = (Curso) cursoDAO.editar(curso);

        }

        return curso;

    }

    public void preSave(Curso curso, boolean esNuevo) throws Exception {

        if (esNuevo) {

        }

        if (curso.getMaterias() != null) {

            curso.getMaterias().forEach(i -> {
                i.setCurso(curso);
            });

        }

        if (curso.getInscripciones() != null) {

            curso.getInscripciones().forEach(i -> {
                i.setCurso(curso);
            });
        }

    }

    public void control(Curso curso, boolean esNuevo) throws ExcepcionGeneralSistema {

        String sError = "";

        if (curso.getCodigo() == null || curso.getCodigo().isEmpty()) {
            sError += "El código es obligatorio | ";
        }

        if (curso.getSucursal() == null) {
            sError += "La Sucursal es obligatoria | ";
        }

        if (curso.getPlanEstudio() == null) {
            sError += "El Plan de Estudio es obligatorio | ";
        }

        if (curso.getFechaInicio() == null) {
            sError += "La Fecha de Inicio es obligatoria | ";
        }

        if (curso.getFechaFinalizacion() == null) {
            sError += "La Fecha de Finalización es obligatoria | ";

        }

        if (curso.getFechaFinalizacion().before(curso.getFechaInicio())) {
            sError += "La Fecha de Finalización debe ser mayor o igual a la Fecha de Inicio | ";
        }

        if (curso.getDescripcion() == null || curso.getDescripcion().isEmpty()) {
            sError += "La descripción es obligatoria | ";
        }

        if (!sError.isEmpty()) {
            throw new ExcepcionGeneralSistema(sError);
        }

    }

    public void eliminar(Curso curso) throws Exception {

        cursoDAO.eliminar(curso);

    }

    public Curso duplicar(Curso a) throws Exception {

        if (a == null) {
            throw new ExcepcionGeneralSistema("Curso nulo, nada para duplicar");
        }

        Curso curso = a.clone();
        curso.setCodigo("");
        asignarCodigo(curso);
        //curso.setDescripcion(a.getDescripcion() + "( Duplicado)");
        curso.setInscripciones(new ArrayList<MovimientoEducacion>());

        return curso;
    }

    public Curso getCurso(String id) {

        return cursoDAO.getCurso(id);
    }

    public List<Curso> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return cursoDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<Curso> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return cursoDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

    public String getProximoCodigo(String codSucursal) {
        int codigo = cursoDAO.getMaxCodigo(codSucursal);

        String nroCodigo = "00000000" + String.valueOf(codigo);
        return nroCodigo.substring(nroCodigo.length() - 3, nroCodigo.length());

    }

    public void reorganizarNroItem(Curso curso) {

        //Reorganizamos los números de items
        int i;

        if (curso.getMaterias() != null) {

            i = 1;
            for (ItemCursoMateria item : curso.getMaterias()) {
                item.setNroitm(i);
                i++;
            }
        }
    }

    public void nuevoItemMateria(Curso curso) throws ExcepcionGeneralSistema {

        if (curso == null) {
            throw new ExcepcionGeneralSistema("No existe un Curso seleccionado para agregarle Materias");
        }

        if (curso.getMaterias() == null) {
            curso.setMaterias(new ArrayList<ItemCursoMateria>());
        }

        ItemCursoMateria itemMateria = new ItemCursoMateria();
        itemMateria.setNroitm(curso.getMaterias().size() + 1);
        itemMateria.setCurso(curso);

        curso.getMaterias().add(itemMateria);

    }

    public void eliminarItemMateria(Curso curso, ItemCursoMateria item) throws ExcepcionGeneralSistema {

        if (curso == null) {
            throw new ExcepcionGeneralSistema("No existe un Curso seleccionado para quitar materia ");
        }

        if (item == null) {
            throw new ExcepcionGeneralSistema("No tiene ningún item seleccionado a quitar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemCursoMateria e : curso.getMaterias()) {

            if (e.getNroitm() >= 0) {

                if (e.getNroitm() == e.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemCursoMateria remove = curso.getMaterias().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    cursoDAO.eliminar(ItemCursoMateria.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(curso);

    }

    public void asignarCodigo(Curso curso) throws Exception {

        if (curso.getSucursal() == null) {
            curso.setCodigo("");
            return;
        }

        if (curso.getPlanEstudio() == null) {
            curso.setCodigo("");
            return;
        }

        if (curso.getFechaInicio() == null) {
            curso.setCodigo("");
            return;
        }

        String codSucursal = curso.getSucursal() != null ? curso.getSucursal().getCodigo() : "";
        String codigo = getProximoCodigo(codSucursal);

        System.out.println(codigo);
        Calendar c = Calendar.getInstance();
        c.setTime(curso.getFechaInicio());

        String periodo = JsfUtil.getMeses().get(String.valueOf((c.get(Calendar.MONTH) + 1))) + " " + c.get(Calendar.YEAR);

        curso.setCodigo(curso.getSucursal().getCodigo() + "-" + curso.getPlanEstudio().getCarrera().getCodigo() + "-" + codigo);
        curso.setDescripcion(curso.getPlanEstudio().getCarrera().getTitulo() + " " + curso.getSucursal().getNombre() + " " + periodo);
    }

    public int getCantidadRegistros() {
        return cursoDAO.getCantidadRegistros();
    }
}
