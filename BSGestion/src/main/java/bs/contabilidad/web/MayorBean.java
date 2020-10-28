/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.web;

import bs.contabilidad.modelo.CuentaContable;
import bs.contabilidad.modelo.ItemMovimientoContabilidad;
import bs.contabilidad.modelo.PeriodoContable;
import bs.contabilidad.rn.ContabilidadRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import java.io.Serializable;
import java.util.List;
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
public class MayorBean extends GenericBean implements Serializable {

    private CuentaContable cuentaContable;
    private List<ItemMovimientoContabilidad> lista;
    private PeriodoContable periodo;

    @EJB
    private ContabilidadRN contabilidadRN;

    public MayorBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();
                buscar();
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void buscar() {

        lista = contabilidadRN.getItemsMovimientoByCuentaContable(cuentaContable, periodo, fechaMovimientoDesde, fechaMovimientoHasta);
        modoVista = "B";
    }

    public void onSelect(SelectEvent event) {
        cuentaContable = (CuentaContable) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(CuentaContable d) {

        if (d == null) {
            return;
        }

        cuentaContable = d;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (cuentaContable == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public List<ItemMovimientoContabilidad> getLista() {
        return lista;
    }

    public void setLista(List<ItemMovimientoContabilidad> lista) {
        this.lista = lista;
    }

    public PeriodoContable getPeriodo() {
        return periodo;
    }

    public void setPeriodo(PeriodoContable periodo) {
        this.periodo = periodo;
    }

}
