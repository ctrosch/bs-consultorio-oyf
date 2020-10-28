/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.proveedores.modelo.TipoRetencion;
import bs.proveedores.rn.TipoRetencionRN;
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
@ViewScoped
@ManagedBean

public class TipoRetencionBean extends GenericBean implements Serializable {

    private TipoRetencion tipoRetencion;
    private List<TipoRetencion> lista;
    private String codigo;

    @EJB
    private TipoRetencionRN tipoRetencionRN;

    public TipoRetencionBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(tipoRetencionRN.getTipoRetencion(codigo));
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
        tipoRetencion = new TipoRetencion();
        modoVista = "D";
    }

    public void guardar(boolean nuevo) {

        try {
            if (tipoRetencion != null) {

                tipoRetencionRN.guardar(tipoRetencion, esNuevo);
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
            tipoRetencion.getAuditoria().setDebaja(habDes);
            tipoRetencionRN.guardar(tipoRetencion, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            tipoRetencionRN.eliminar(tipoRetencion);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = tipoRetencionRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<TipoRetencion> complete(String query) {
        try {
            lista = tipoRetencionRN.getListaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TipoRetencion>();
        }
    }

    public void onSelect(SelectEvent event) {
        tipoRetencion = (TipoRetencion) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(TipoRetencion s) {

        if (s == null) {
            return;
        }

        tipoRetencion = s;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (tipoRetencion == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public TipoRetencion getTipoRetencion() {
        return tipoRetencion;
    }

    public void setTipoRetencion(TipoRetencion tipoRetencion) {
        this.tipoRetencion = tipoRetencion;
    }

    public List<TipoRetencion> getLista() {
        return lista;
    }

    public void setLista(List<TipoRetencion> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
