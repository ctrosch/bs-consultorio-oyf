/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.mantenimiento.dao.PlanActividadDAO;
import bs.mantenimiento.modelo.ItemPlanActividad;
import bs.mantenimiento.modelo.PlanActividad;
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
public class PlanActividadRN implements Serializable {

    @EJB
    private PlanActividadDAO planActividadDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PlanActividad guardar(PlanActividad planActividad, boolean esNuevo) throws Exception {

        preSave(planActividad);

        control(planActividad, esNuevo);

        if (esNuevo) {

            if (planActividadDAO.getObjeto(PlanActividad.class, planActividad.getCodigo()) != null) {
                throw new ExcepcionGeneralSistema("Ya existe un Plan de Actividad con el código " + planActividad.getCodigo());
            }
            planActividadDAO.crear(planActividad);

        } else {
            planActividad = (PlanActividad) planActividadDAO.editar(planActividad);
        }

        return planActividad;

    }

    public void preSave(PlanActividad planActividad) {

        if (planActividad.getItemsPlanActividad() != null && !planActividad.getItemsPlanActividad().isEmpty()) {

            planActividad.getItemsPlanActividad().forEach(i -> {
                i.setPlanActividad(planActividad);
            });

        }
    }

    public void control(PlanActividad planActividad, boolean esNuevo) throws ExcepcionGeneralSistema {

        String sError = "";

        if (planActividad.getCodigo() == null || planActividad.getCodigo().isEmpty()) {
            sError += "El código es obligatorio | ";
        }

        if (planActividad.getDescripcion() == null || planActividad.getDescripcion().isEmpty()) {
            sError += "La descripción es obligatoria | ";
        }

        if (!sError.isEmpty()) {
            throw new ExcepcionGeneralSistema(sError);
        }

    }

    public void eliminar(PlanActividad planActividad) throws Exception {

        planActividadDAO.eliminar(planActividad);

    }

    public PlanActividad duplicar(PlanActividad p) throws Exception {

        if (p == null) {
            throw new ExcepcionGeneralSistema("Plan de actividad Nulo, nada para duplicar");
        }

        PlanActividad planActividad = p.clone();
        planActividad.setCodigo(p.getCodigo());
        planActividad.setDescripcion(p.getDescripcion() + "( Duplicado)");

        return planActividad;
    }

    public void nuevoItemPlanActividad(PlanActividad planActividad) throws ExcepcionGeneralSistema {

        if (planActividad == null) {
            throw new ExcepcionGeneralSistema("No existe un Plan Actividad seleccionado para agregarle un Item");
        }

        if (planActividad.getItemsPlanActividad() == null) {
            planActividad.setItemsPlanActividad(new ArrayList<ItemPlanActividad>());
        }

        ItemPlanActividad itemPlanActividad = new ItemPlanActividad();
        itemPlanActividad.setNroitm(planActividad.getItemsPlanActividad().size() + 1);
        itemPlanActividad.setPlanActividad(planActividad);

        planActividad.getItemsPlanActividad().add(itemPlanActividad);

        reorganizarNroItem(planActividad);

    }

    public void eliminarItemPlanActividad(PlanActividad planActividad, ItemPlanActividad itemPlanActividad) throws ExcepcionGeneralSistema, Exception {
        if (planActividad == null) {
            throw new ExcepcionGeneralSistema("Plan de Actividad vacía, nada para eliminar");
        }
        if (itemPlanActividad == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para eliminar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemPlanActividad item : planActividad.getItemsPlanActividad()) {

            if (item.getNroitm() >= 0) {

                if (item.getNroitm() == itemPlanActividad.getNroitm()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }
        i = 0;

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemPlanActividad remove = planActividad.getItemsPlanActividad().remove(indiceItemBorrar);
            if (remove != null) {
                if (planActividad.getCodigo() != null && remove.getId() != null) {

                    planActividadDAO.eliminar(ItemPlanActividad.class, remove.getId());

                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(planActividad);
    }

    public void reorganizarNroItem(PlanActividad planActividad) {

        //Reorganizamos los números de items
        int i;

        if (planActividad.getItemsPlanActividad() != null) {

            i = 1;
            for (ItemPlanActividad item : planActividad.getItemsPlanActividad()) {
                item.setNroitm(i);
                i++;
            }
        }

    }

    public PlanActividad getPlanActividad(String codigo) {

        return planActividadDAO.getPlanActividad(codigo);
    }

    public List<PlanActividad> getListaByBusqueda(Map<String, String> filtro, String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return planActividadDAO.getPlanActividadByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantMax);
    }

    public List<PlanActividad> getListaByBusqueda(String txtBusqueda, boolean mostrarDebaja, int cantMax) {

        return planActividadDAO.getPlanActividadByBusqueda(null, txtBusqueda, mostrarDebaja, cantMax);
    }

}
