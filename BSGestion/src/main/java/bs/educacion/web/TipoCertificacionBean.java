/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.web;

import bs.educacion.modelo.TipoCertificacion;
import bs.educacion.rn.TipoCertificacionRN;
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
 * @author GUILLERMO
 */
@ManagedBean
@ViewScoped
public class TipoCertificacionBean extends GenericBean implements Serializable {

    private String codigo;
    private TipoCertificacion tipo;
    private List<TipoCertificacion> lista;

    @EJB
    private TipoCertificacionRN tipoCertificacionRN;

    public TipoCertificacionBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(tipoCertificacionRN.getTipoCertificacion(codigo));
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
        tipo = new TipoCertificacion();
    }

    public void guardar(boolean nuevo) {

        try {
            if (tipo != null) {

                tipo = tipoCertificacionRN.guardar(tipo, esNuevo);
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
            tipo.getAuditoria().setDebaja(habDes);
            tipoCertificacionRN.guardar(tipo, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            tipoCertificacionRN.eliminar(tipo);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = tipoCertificacionRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<TipoCertificacion> complete(String query) {
        try {

            lista = tipoCertificacionRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TipoCertificacion>();
        }
    }

    public void onSelect(SelectEvent event) {
        tipo = (TipoCertificacion) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(TipoCertificacion d) {

        if (d == null) {
            return;
        }

        tipo = d;
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (tipo == null) {
                JsfUtil.addErrorMessage("No hay Tipo de Certificación activa");
                return;
            }

            tipo = tipoCertificacionRN.duplicar(tipo);
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (tipo == null) {
            JsfUtil.addErrorMessage("No hay Tipo de Certificación seleccionada, nada para imprimir");
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoCertificacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoCertificacion tipo) {
        this.tipo = tipo;
    }

    public List<TipoCertificacion> getLista() {
        return lista;
    }

    public void setLista(List<TipoCertificacion> lista) {
        this.lista = lista;
    }

}
