/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.web;

import bs.educacion.modelo.Curso;
import bs.educacion.modelo.ItemCursoMateria;
import bs.educacion.modelo.PlanEstudio;
import bs.educacion.rn.CursoRN;
import bs.entidad.modelo.TipoEntidad;
import bs.entidad.rn.EntidadRN;
import bs.entidad.rn.TipoEntidadRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Sucursal;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
public class CursoBean extends GenericBean implements Serializable {

    private List<Curso> lista;
    private Curso curso;
    private String CODIGO;

    @EJB
    private EntidadRN entidadRN;
    @EJB
    private TipoEntidadRN tipoEntidadRN;

    // Variables para filtros
    private PlanEstudio planEstudio;
    private Sucursal sucursal;
    private TipoEntidad tipoEntidad;
    private Date fechaInicio;
    private Date fechaFin;

    @EJB
    private CursoRN cursoRN;

    /**
     * Creates a new instance of EntidadComercialBean
     */
    public CursoBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();
                tipoEntidad = tipoEntidadRN.getTipoEntidad("5");

                if (CODIGO != null && !CODIGO.isEmpty()) {
                    seleccionar(cursoRN.getCurso(CODIGO));
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
        curso = new Curso();

    }

    public void guardar(boolean nuevo) {
        try {
            if (curso != null) {

                curso = cursoRN.guardar(curso, esNuevo);
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
            cursoRN.nuevoItemMateria(curso);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item Materia: " + ex);
        }
    }

    public void eliminarItemMateria(ItemCursoMateria itemMateria) {

        try {
            cursoRN.eliminarItemMateria(curso, itemMateria);
            JsfUtil.addWarningMessage("Se ha borrado el item Materia " + (itemMateria.getMateria() != null ? itemMateria.getMateria().getNombre() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemMateria.getMateria() != null ? itemMateria.getMateria().getNombre() : "") + " " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            curso.getAuditoria().setDebaja(habDes);
            curso = cursoRN.guardar(curso, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            cursoRN.eliminar(curso);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        lista = cursoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";

    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (fechaInicio != null) {

            filtro.put("fechaInicio >= ", JsfUtil.getFechaSQL(fechaInicio));
        }

        if (fechaFin != null) {

            filtro.put("fechaFinalizacion <= ", JsfUtil.getFechaSQL(fechaFin));
        }

        if (sucursal != null) {

            filtro.put("sucursal.codigo = ", "'" + sucursal.getCodigo() + "'");
        }

        if (planEstudio != null) {

            filtro.put("planEstudio.codigo = ", "'" + planEstudio.getCodigo() + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        planEstudio = null;
        sucursal = null;
        buscar();

    }

    public List<Curso> complete(String query) {
        try {

            lista = cursoRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Curso>();
        }
    }

    public void onSelect(SelectEvent event) {

        curso = (Curso) event.getObject();
        esNuevo = false;
        modoVista = "D";

    }

    public void seleccionar(Curso e) {

        if (e == null) {
            return;
        }

        curso = e;
        esNuevo = false;
        modoVista = "D";

    }

    public void actualizarDatos() {

        curso = cursoRN.getCurso(curso.getCodigo());
    }

    public void asignarCodigo() {

        try {
            if (esNuevo) {
                cursoRN.asignarCodigo(curso);
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para generar código del curso " + ex);
        }
    }

    public void duplicar() throws Exception {

        try {
            if (curso == null) {
                JsfUtil.addErrorMessage("No hay división de Alumnos activo");
                return;
            }

            curso = cursoRN.duplicar(curso);

            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (curso == null) {
            JsfUtil.addErrorMessage("No hay Curso seleccionada, nada para imprimir");
        }
    }

    public List<Curso> getLista() {
        return lista;
    }

    public void setLista(List<Curso> lista) {
        this.lista = lista;
    }

    public PlanEstudio getPlanEstudio() {
        return planEstudio;
    }

    public void setPlanEstudio(PlanEstudio planEstudio) {
        this.planEstudio = planEstudio;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public TipoEntidad getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(TipoEntidad tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

}
