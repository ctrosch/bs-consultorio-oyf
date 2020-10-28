/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.web;

import bs.contabilidad.modelo.PeriodoContable;
import bs.contabilidad.rn.PeriodoContableRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
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
 * @author Ctrosch
 */
@ManagedBean
@ViewScoped
public class PeriodoContableBean extends GenericBean implements Serializable {

    private PeriodoContable periodo;
    private List<PeriodoContable> lista;

    private String CODIGO;

    @EJB
    private PeriodoContableRN periodoRN;

    public PeriodoContableBean() {

    }

    @PostConstruct
    public void init() {
        buscar();

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (CODIGO != null && !CODIGO.isEmpty()) {
                    seleccionar(periodoRN.getPeriodoContable(CODIGO));
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
        periodo = new PeriodoContable();
    }

    public void guardar(boolean nuevo) {

        try {
            if (periodo != null) {

                periodo = periodoRN.guardar(periodo, esNuevo);
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
            periodo.getAuditoria().setDebaja(habDes);
            periodo = periodoRN.guardar(periodo, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            periodoRN.eliminar(periodo);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();
        lista = periodoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<PeriodoContable> complete(String query) {
        try {
            lista = periodoRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<PeriodoContable>();
        }
    }

    public void onSelect(SelectEvent event) {
        periodo = (PeriodoContable) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(PeriodoContable d) {

        if (d == null) {
            return;
        }
        periodo = d;
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (periodo == null) {
                JsfUtil.addErrorMessage("No hay período activo");
                return;
            }

            periodo = periodoRN.duplicar(periodo);
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void onFechaDesdeSelect() {

        periodo = periodoRN.onFechaDesdeSelect(periodo);
    }

    public void imprimir() {

        if (periodo == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public PeriodoContable getEjercicio() {
        return periodo;
    }

    public void setEjercicio(PeriodoContable periodo) {
        this.periodo = periodo;
    }

    public List<PeriodoContable> getLista() {
        return lista;
    }

    public void setLista(List<PeriodoContable> lista) {
        this.lista = lista;
    }

    public PeriodoContable getPeriodo() {
        return periodo;
    }

    public void setPeriodo(PeriodoContable periodo) {
        this.periodo = periodo;
    }

    public String getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(String CODIGO) {
        this.CODIGO = CODIGO;
    }

}
