/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.educacion.dao.PlanEstudioDAO;
import bs.educacion.modelo.ItemPlanEstudioMateria;
import bs.educacion.modelo.PlanEstudio;
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
public class PlanEstudioRN implements Serializable {

    @EJB
    private PlanEstudioDAO planEstudioDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PlanEstudio guardar(PlanEstudio planEstudio, boolean esNuevo) throws Exception {

        reorganizarNroItem(planEstudio);
        preSave(planEstudio);
        control(planEstudio);

        if (esNuevo) {

            if (planEstudioDAO.getObjeto(PlanEstudio.class, planEstudio.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe una con ese codigo " + planEstudio.getCodigo());
            }

            planEstudioDAO.crear(planEstudio);

        } else {

            planEstudio = (PlanEstudio) planEstudioDAO.editar(planEstudio);

        }

        return planEstudio;

    }

    private void preSave(PlanEstudio planEstudio) throws ExcepcionGeneralSistema, Exception {

        if (planEstudio.getMaterias() != null) {

            for (ItemPlanEstudioMateria item : planEstudio.getMaterias()) {

                item.setPlanEstudio(planEstudio);
            }
        }
    }

    private void control(PlanEstudio planEstudio) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (planEstudio.getCarrera() == null) {
            sErrores += "- La Carrera es Obligatoria \n ";
        }

        if (planEstudio.getAnio() == null || planEstudio.getAnio() == 0) {

            sErrores += "- El año no puede estar vacío o ser igual a 0 \n ";
        }

        if (planEstudio.getCodigo() == null || planEstudio.getCodigo().isEmpty()) {

            sErrores += "- El Código es Obligatorio \n ";
        }

        if (planEstudio.getTitulo() == null || planEstudio.getTitulo().isEmpty()) {
            sErrores += "- El Título es Obligatorio \n ";
        }

        if (planEstudio.getModalidadCursado() == null) {
            sErrores += "- La Modalidad de Cursado es obligatoria \n ";
        }

        if (planEstudio.getTipoCertificacion() == null) {
            sErrores += "- El Tipo de Certificación es obligatorio \n ";
        }

        if (planEstudio.getReglamento() == null) {
            sErrores += "- El Reglamento es obligatorio \n ";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void eliminar(PlanEstudio planEstudio) throws Exception {

        planEstudioDAO.eliminar(planEstudio);

    }

    public PlanEstudio duplicar(PlanEstudio a) throws Exception {

        if (a == null) {
            throw new ExcepcionGeneralSistema("PlanEstudio nulo, nada para duplicar");
        }

        PlanEstudio planEstudio = a.clone();
        planEstudio.setCodigo("");
        planEstudio.setTitulo(a.getTitulo() + "( Duplicado)");

        return planEstudio;
    }

    public PlanEstudio getPlanEstudio(String id) {

        return planEstudioDAO.getPlanEstudio(id);
    }

    public List<PlanEstudio> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja) {

        return planEstudioDAO.getListaByBusqueda(null, txtBusqueda, mostrarDebaja, 10);
    }

    public List<PlanEstudio> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return planEstudioDAO.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public String getProximoCodigo(String codCarrera) {
        int codigo = planEstudioDAO.getMaxCodigo(codCarrera);

        String nroCodigo = "00000000" + String.valueOf(codigo);
        return codCarrera + nroCodigo.substring(nroCodigo.length() - 2, nroCodigo.length());

    }

    public void asignarCodigo(PlanEstudio planEstudio) throws Exception {

        if (planEstudio.getCarrera() == null) {
            planEstudio.setCodigo("");
            return;
        }

        if (planEstudio.getModalidadCursado() == null) {
            planEstudio.setCodigo("");
            return;
        }

        if (planEstudio.getAnio() == null || planEstudio.getAnio() <= 0) {
            planEstudio.setCodigo("");
            return;
        }

        String codigo = planEstudio.getCarrera().getCodigo() + "-" + planEstudio.getAnio() + "-" + planEstudio.getModalidadCursado().getCodigo();

        planEstudio.setCodigo(codigo);
        planEstudio.setTitulo(planEstudio.getCarrera().getTitulo() + " " + planEstudio.getAnio() + "-" + planEstudio.getModalidadCursado().getDescripcion());

    }

    public void reorganizarNroItem(PlanEstudio planEstudio) {

        //Reorganizamos los números de items
        int i;

        if (planEstudio.getMaterias() != null) {

            i = 1;
            for (ItemPlanEstudioMateria item : planEstudio.getMaterias()) {
                item.setNroitm(i);
                i++;
            }
        }

    }

    public void nuevoItemMateria(PlanEstudio planEstudio) throws ExcepcionGeneralSistema {

        if (planEstudio == null) {
            throw new ExcepcionGeneralSistema("No existe un PlanEstudio seleccionado para agregarle una Materia");
        }

        if (planEstudio.getMaterias() == null) {
            planEstudio.setMaterias(new ArrayList<ItemPlanEstudioMateria>());
        }

        ItemPlanEstudioMateria itemMateria = new ItemPlanEstudioMateria();
        itemMateria.setNroitm(planEstudio.getMaterias().size() + 1);
        itemMateria.setPlanEstudio(planEstudio);

        planEstudio.getMaterias().add(itemMateria);

    }

    public void eliminarItemMateria(PlanEstudio planEstudio, ItemPlanEstudioMateria item) throws ExcepcionGeneralSistema {

        if (planEstudio == null) {
            throw new ExcepcionGeneralSistema("No existe un PlanEstudio seleccionado para quitar una Materia");
        }

        if (item == null) {
            throw new ExcepcionGeneralSistema("No tiene ningún item seleccionado a quitar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemPlanEstudioMateria e : planEstudio.getMaterias()) {

            if (e.getNroitm() >= 0) {

                if (e.getNroitm() == item.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemPlanEstudioMateria remove = planEstudio.getMaterias().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    planEstudioDAO.eliminar(ItemPlanEstudioMateria.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(planEstudio);

    }

    public int getCantidadRegistros() {

        return planEstudioDAO.getCantidadRegistros();
    }

}
