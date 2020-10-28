/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.educacion.dao.MateriaDAO;
import bs.educacion.modelo.ItemMateriaProfesor;
import bs.educacion.modelo.Materia;
import bs.global.excepciones.ExcepcionGeneralSistema;
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
public class MateriaRN implements Serializable {

    @EJB
    private MateriaDAO materiaDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Materia guardar(Materia materia, boolean esNuevo) throws Exception {

        reorganizarNroItem(materia);
        preSave(materia);
        control(materia);

        if (esNuevo) {

            if (materiaDAO.getObjeto(Materia.class, materia.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una Materia con el código " + materia.getCodigo());
            }
            materiaDAO.crear(materia);

        } else {
            materia = (Materia) materiaDAO.editar(materia);
        }

        return materia;

    }

    private void preSave(Materia materia) throws ExcepcionGeneralSistema, Exception {

        if (materia.getProfesores() != null) {

            for (ItemMateriaProfesor item : materia.getProfesores()) {

                item.setMateria(materia);
            }
        }
    }

    private void control(Materia materia) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (materia.getCodigo() == null || materia.getCodigo().isEmpty()) {
            sErrores += "- El Código es Obligatorio \n ";
        }

        if (materia.getNombre() == null || materia.getNombre().isEmpty()) {
            sErrores += "- El Nombre es Obligatorio \n ";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(Materia materia) throws Exception {

        materiaDAO.eliminar(materia);

    }

    public Materia duplicar(Materia m) throws Exception {

        if (m == null) {
            throw new ExcepcionGeneralSistema("Materia nula, nada para duplicar");
        }

        Materia materia = m.clone();
        materia.setCodigo(m.getCodigo());
        materia.setNombre(m.getNombre() + "( Duplicado)");

        return materia;
    }

    public Materia getMateria(String codigo) {

        return materiaDAO.getMateria(codigo);
    }

    public List<Materia> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return materiaDAO.getMateriaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<Materia> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return materiaDAO.getMateriaByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

    public void nuevoItemProfesor(Materia materia) throws ExcepcionGeneralSistema {

        if (materia == null) {
            throw new ExcepcionGeneralSistema("No existe una Materia seleccionada para agregar un Profesor");
        }

        if (materia.getProfesores() == null) {
            materia.setProfesores(new ArrayList<ItemMateriaProfesor>());

        }

        ItemMateriaProfesor item = new ItemMateriaProfesor();
        item.setNroitm(materia.getProfesores().size() + 1);
        item.setMateria(materia);

        materia.getProfesores().add(item);

    }

    public void eliminarItemProfesor(Materia materia, ItemMateriaProfesor item) throws ExcepcionGeneralSistema {

        if (materia == null) {
            throw new ExcepcionGeneralSistema("No existe una Materia seleccionada para quitar un Profesor");
        }

        if (item == null) {
            throw new ExcepcionGeneralSistema("No tiene ningún item seleccionado a quitar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemMateriaProfesor e : materia.getProfesores()) {

            if (e.getNroitm() >= 0) {

                if (e.getNroitm() == item.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemMateriaProfesor remove = materia.getProfesores().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    materiaDAO.eliminar(ItemMateriaProfesor.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(materia);

    }

    public void reorganizarNroItem(Materia materia) {

        //Reorganizamos los números de items
        int i;

        if (materia.getProfesores() != null) {

            i = 1;
            for (ItemMateriaProfesor item : materia.getProfesores()) {
                item.setNroitm(i);
                i++;
            }
        }

    }

}
