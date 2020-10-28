/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.web;

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
public class FormularioVentaBean extends GenericBean implements Serializable {

    @EJB
    private FormularioRN formularioRN;

    private List<Formulario> lista;
    private final String MODULO = "VT";
    private String CODIGO;

    @ManagedProperty(value = "#{reporteBean}")
    protected ReporteBean reporteBean;

    // Variables para filtros
    private Reporte reporte;

    /**
     * Creates a new instance of FormularioVentaBean
     */
    public FormularioVentaBean() {

    }

    @PostConstruct
    public void init() {

        filtro = new LinkedHashMap<String, String>();
        filtro.put("modfor", " = '" + MODULO + "'");

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (CODIGO != null && !CODIGO.isEmpty()) {
                    seleccionar(formularioRN.getFormulario(MODULO, CODIGO));
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
        formulario = new Formulario();
        formulario.setModfor(MODULO);
    }

    public void guardar(boolean nuevo) {

        try {
            if (formulario != null) {

                formularioRN.guardar(formulario, esNuevo);
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
        cargarFiltroBusqueda();

        lista = formularioRN.getFormularioByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        filtro.put("modfor = ", "'" + MODULO + "'");

        if (reporte != null) {

            filtro.put("reporte.codigo = ", "'" + reporte.getCodigo() + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        reporte = null;

        buscar();

    }

    public List<Formulario> complete(String query) {
        try {

            lista = formularioRN.getFormularioByBusqueda(MODULO, query, false);
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
        modoVista = "D";
    }

    public void seleccionar(Formulario f) {

        if (f == null) {
            return;
        }

        formulario = f;
        esNuevo = false;
        modoVista = "D";

    }

    public void duplicar() {

        try {
            if (formulario == null) {
                JsfUtil.addErrorMessage("No hay comprobante activo");
                return;
            }

            formulario = formularioRN.duplicar(formulario);
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
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

    public String getModulo() {
        return MODULO;
    }

    public ReporteBean getReporteBean() {
        return reporteBean;
    }

    public void setReporteBean(ReporteBean reporteBean) {
        this.reporteBean = reporteBean;
    }

    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

}
