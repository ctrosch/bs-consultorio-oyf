/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.mantenimiento.modelo.TipoMantenimiento;
import bs.mantenimiento.rn.TipoMantenimientoRN;
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
public class TipoMantenimientoBean extends GenericBean implements Serializable {

    private String codigo;
    private TipoMantenimiento tipoMantenimiento;
    private List<TipoMantenimiento> lista;

    @EJB
    private TipoMantenimientoRN tipoMantenimientoRN;

    public TipoMantenimientoBean() {

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
                    seleccionar(tipoMantenimientoRN.getTipoMantenimiento(codigo));
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
        tipoMantenimiento = new TipoMantenimiento();
    }

    public void guardar(boolean nuevo) {

        try {
            if (tipoMantenimiento != null) {

                tipoMantenimiento = tipoMantenimientoRN.guardar(tipoMantenimiento, esNuevo);
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
            tipoMantenimiento.getAuditoria().setDebaja(habDes);
            tipoMantenimientoRN.guardar(tipoMantenimiento, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            tipoMantenimientoRN.eliminar(tipoMantenimiento);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = tipoMantenimientoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<TipoMantenimiento> complete(String query) {
        try {

            lista = tipoMantenimientoRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TipoMantenimiento>();
        }
    }

    public void onSelect(SelectEvent event) {
        tipoMantenimiento = (TipoMantenimiento) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(TipoMantenimiento t) {

        if (t == null) {
            return;
        }

        tipoMantenimiento = t;
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (tipoMantenimiento == null) {
                JsfUtil.addErrorMessage("No hay Tipo de Mantenimiento activo");
                return;
            }

            tipoMantenimiento = tipoMantenimientoRN.duplicar(tipoMantenimiento);
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (tipoMantenimiento == null) {
            JsfUtil.addErrorMessage("No hay Tipo de Mantenimiento seleccionado, nada para imprimir");
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoMantenimiento getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(TipoMantenimiento tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    public List<TipoMantenimiento> getLista() {
        return lista;
    }

    public void setLista(List<TipoMantenimiento> lista) {
        this.lista = lista;
    }

}
