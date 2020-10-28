/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tarea.web;

import bs.administracion.modelo.Plantilla;
import bs.administracion.modelo.Reporte;
import bs.administracion.web.ReporteBean;
import bs.global.modelo.Formulario;
import bs.global.rn.FormularioRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class FormularioTareaBean extends GenericBean implements Serializable {

    @EJB
    private FormularioRN formularioRN;
    private List<Formulario> lista;
    private String MODULO;

    @ManagedProperty(value = "#{reporteBean}")
    protected ReporteBean reporteBean;

    /**
     * Creates a new instance of FormularioVentaBean
     */
    public FormularioTareaBean() {

    }

    @PostConstruct
    public void init() {
        MODULO = "TA";
        filtro = new LinkedHashMap<String, String>();
        filtro.put("modfor", " = '" + MODULO + "'");
        txtBusqueda = "";
        mostrarDebaja = false;
        nuevo();
        buscar();
    }

    public void nuevo() {

        esNuevo = true;
        buscaMovimiento = false;
        formulario = new Formulario();
        formulario.setModfor(MODULO);
    }

    public void guardar(boolean nuevo) {

        try {
            if (formulario != null) {

                formularioRN.guardar(formulario, esNuevo);
                esNuevo = false;
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");

                if (nuevo) {
                    nuevo();
                }
            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            formulario.getAuditoria().setDebaja(habDes);
            formularioRN.guardar(formulario, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            formularioRN.eliminar(formulario);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        lista = formularioRN.getFormularioByBusqueda(filtro, txtBusqueda, mostrarDebaja);
    }

    public List<Formulario> complete(String query) {
        try {
            lista = formularioRN.getFormularioByBusqueda(filtro, query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Formulario>();
        }
    }

    public List<Reporte> completeReporte(String query) {
        try {

            filtro.clear();
            filtro.put("modulo.codigo = ", "'" + MODULO + "'");

            return reporteRN.getListaByBusqueda(filtro, query, false);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public List<Plantilla> completePlantilla(String query) {
        try {

            filtro.clear();
            filtro.put("modulo.codigo = ", "'" + MODULO + "'");

            return plantillaRN.getListaByBusqueda(filtro, query, false);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public void onSelect(SelectEvent event) {
        formulario = (Formulario) event.getObject();
        esNuevo = false;
    }

    public void seleccionar(Formulario f) {

        if (f == null) {
            return;
        }

        formulario = f;
        esNuevo = false;
        buscaMovimiento = false;
    }

    public void imprimir() {

        if (formulario == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void procesarReporte() {

        if (reporteBean.getReporte() != null) {
            formulario.setReporte(reporteBean.getReporte());
        }
    }

    public List<Formulario> getLista() {
        return lista;
    }

    public void setLista(List<Formulario> lista) {
        this.lista = lista;
    }

    public String getMODULO() {
        return MODULO;
    }

    public void setMODULO(String MODULO) {
        this.MODULO = MODULO;
    }

    public ReporteBean getReporteBean() {
        return reporteBean;
    }

    public void setReporteBean(ReporteBean reporteBean) {
        this.reporteBean = reporteBean;
    }

}
