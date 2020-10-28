/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.mantenimiento.modelo.ItemPlanActividad;
import bs.mantenimiento.modelo.PlanActividad;
import bs.mantenimiento.rn.PlanActividadRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author GUILLERMO
 */
@ManagedBean
@ViewScoped
public class PlanActividadBean extends GenericBean implements Serializable {

    private String codigo;
    private PlanActividad planActividad;
    private List<PlanActividad> lista;

    @EJB
    private PlanActividadRN planActividadRN;

    public PlanActividadBean() {

    }

    @PostConstruct
    public void init() {
        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(planActividadRN.getPlanActividad(codigo));
                }
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        esNuevo = true;
        modoVista = "D";
        planActividad = new PlanActividad();
    }

    public void guardar(boolean nuevo) {

        try {
            if (planActividad != null) {

                planActividad = planActividadRN.guardar(planActividad, esNuevo);
                esNuevo = false;
                modoVista = "D";
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            planActividad.getAuditoria().setDebaja(habDes);
            planActividadRN.guardar(planActividad, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            planActividadRN.eliminar(planActividad);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = planActividadRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();
    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        buscar();

    }

    public List<PlanActividad> complete(String query) {
        try {

            lista = planActividadRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<PlanActividad>();
        }
    }

    public void onSelect(SelectEvent event) {
        planActividad = (PlanActividad) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(PlanActividad a) {

        if (a == null) {
            return;
        }

        planActividad = a;
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (planActividad == null) {
                JsfUtil.addErrorMessage("No hay Plan de Actividad activo");
                return;
            }

            planActividad = planActividadRN.duplicar(planActividad);
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (planActividad == null) {
            JsfUtil.addErrorMessage("No hay Plan de Actividad seleccionado, nada para imprimir");
        }
    }

    public void nuevoItemPlanActividad() {

        try {
            planActividadRN.nuevoItemPlanActividad(planActividad);

        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item sugerido: " + ex);
        }
    }

    public void eliminarItemPlanActividad(ItemPlanActividad itemPlanActividad) {

        try {
            planActividadRN.eliminarItemPlanActividad(planActividad, itemPlanActividad);
            JsfUtil.addWarningMessage("Se ha borrado el item " + (itemPlanActividad.getActividad().getDescripcion() != null ? itemPlanActividad.getActividad().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemPlanActividad.getActividad().getDescripcion() != null ? itemPlanActividad.getActividad().getDescripcion() : "") + " " + ex);
        }
    }

//    public void procesarProducto(ActividadRecurso actividadRecurso) {
//
//        if (actividadRecurso != null && actividadRecurso.getProducto() != null) {
//
//            try {
//                actividadRN.asignarProducto(actividadRecurso, actividadRecurso.getProducto());
//
//            } catch (Exception ex) {
//                JsfUtil.addErrorMessage("No es posible asignar producto " + ex);
//            }
//        }
//    }
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public PlanActividad getPlanActividad() {
        return planActividad;
    }

    public void setPlanActividad(PlanActividad planActividad) {
        this.planActividad = planActividad;
    }

    public List<PlanActividad> getLista() {
        return lista;
    }

    public void setLista(List<PlanActividad> lista) {
        this.lista = lista;
    }

}
