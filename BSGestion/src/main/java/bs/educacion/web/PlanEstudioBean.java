/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.web;

import bs.educacion.modelo.AreaEducacion;
import bs.educacion.modelo.Carrera;
import bs.educacion.modelo.ItemPlanEstudioMateria;
import bs.educacion.modelo.ModalidadCursado;
import bs.educacion.modelo.PlanEstudio;
import bs.educacion.modelo.Reglamento;
import bs.educacion.modelo.TipoCarrera;
import bs.educacion.rn.PlanEstudioRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.UnidadNegocio;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class PlanEstudioBean extends GenericBean implements Serializable {

    private List<PlanEstudio> lista;
    private PlanEstudio planEstudio;
    private String CODIGO;

    // Variables para filtros
    Carrera carrera;
    UnidadNegocio unidadNegocio;
    TipoCarrera tipoCarrera;
    ModalidadCursado modalidad;
    AreaEducacion area;

    @EJB
    private PlanEstudioRN planEstudioRN;

    /**
     * Creates a new instance of EntidadComercialBean
     */
    public PlanEstudioBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (CODIGO != null && !CODIGO.isEmpty()) {
                    seleccionar(planEstudioRN.getPlanEstudio(CODIGO));
                } else {
                    buscar();
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
        planEstudio = new PlanEstudio();

    }

    public void asignarCodigo() {

        try {
            if (esNuevo) {
                planEstudioRN.asignarCodigo(planEstudio);
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para generar código de planEstudio " + ex);
        }
    }

    public void guardar(boolean nuevo) {
        try {
            if (planEstudio != null) {

                planEstudio = planEstudioRN.guardar(planEstudio, esNuevo);
                esNuevo = false;
                modoVista = "D";
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            } else {
                JsfUtil.addErrorMessage("No hay datos para guardar");
            }

        } catch (Exception ex) {
//            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void nuevoItemMateria() {

        try {
            planEstudioRN.nuevoItemMateria(planEstudio);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item materia: " + ex);
        }
    }

    public void eliminarItemMateria(ItemPlanEstudioMateria itemMateria) {

        try {
            planEstudioRN.eliminarItemMateria(planEstudio, itemMateria);
            JsfUtil.addWarningMessage("Se ha borrado el item materia " + (itemMateria.getMateria() != null ? itemMateria.getMateria().getNombre() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemMateria.getMateria() != null ? itemMateria.getMateria().getNombre() : "") + " " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            planEstudio.getAuditoria().setDebaja(habDes);
            planEstudio = planEstudioRN.guardar(planEstudio, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            planEstudioRN.eliminar(planEstudio);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        lista = planEstudioRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public List<PlanEstudio> complete(String query) {
        try {

            lista = planEstudioRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (area != null) {

            filtro.put("area.codigo = ", "'" + area.getCodigo() + "'");
        }

        if (modalidad != null) {
            filtro.put("modalidadCursado.codigo = ", "'" + modalidad.getCodigo() + "'");
        }

        if (carrera != null) {
            filtro.put("carrera.codigo=", "'" + carrera.getCodigo() + "'");
        }

        if (tipoCarrera != null) {
            filtro.put("carrera.tipoCarrera.codigo=", "'" + tipoCarrera.getCodigo() + "'");
        }

        if (unidadNegocio != null) {
            filtro.put("carrera.unidadNegocio.codigo=", "'" + unidadNegocio.getCodigo() + "'");
        }
    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        area = null;
        modalidad = null;
        carrera = null;
        buscar();

    }

    public void onSelect(SelectEvent event) {

        planEstudio = (PlanEstudio) event.getObject();
        esNuevo = false;
        modoVista = "D";

    }

    public void seleccionar(PlanEstudio e) {

        if (e == null) {
            return;
        }

        planEstudio = e;
        esNuevo = false;
        modoVista = "D";

    }

    public void acturalizarUrl(Reglamento r) {

        planEstudio.setReglamento(r);

    }

    public void duplicar() throws Exception {

        try {
            if (planEstudio == null) {
                JsfUtil.addErrorMessage("No hay PlanEstudio activo");
                return;
            }

            planEstudio = planEstudioRN.duplicar(planEstudio);
            // planEstudio.setCodigo(planEstudioRN.getProximoCodigo());
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (planEstudio == null) {
            JsfUtil.addErrorMessage("No hay PlanEstudio seleccionado, nada para imprimir");
        }
    }

    public List<PlanEstudio> getLista() {
        return lista;
    }

    public void setLista(List<PlanEstudio> lista) {
        this.lista = lista;
    }

    public PlanEstudio getPlanEstudio() {
        return planEstudio;
    }

    public void setPlanEstudio(PlanEstudio planEstudio) {
        this.planEstudio = planEstudio;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

    public ModalidadCursado getModalidad() {
        return modalidad;
    }

    public void setModalidad(ModalidadCursado modalidad) {
        this.modalidad = modalidad;
    }

    public AreaEducacion getArea() {
        return area;
    }

    public void setArea(AreaEducacion area) {
        this.area = area;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public TipoCarrera getTipoCarrera() {
        return tipoCarrera;
    }

    public void setTipoCarrera(TipoCarrera tipoCarrera) {
        this.tipoCarrera = tipoCarrera;
    }

}
