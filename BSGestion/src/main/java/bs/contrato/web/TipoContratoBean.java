/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contrato.web;

import bs.contrato.modelo.TipoContrato;
import bs.contrato.rn.TipoContratoRN;
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
 * @author GUILLERMO
 */
@ManagedBean
@ViewScoped
public class TipoContratoBean extends GenericBean implements Serializable {

    private String codigo;
    private TipoContrato tipoContrato;
    private List<TipoContrato> lista;

    @EJB
    private TipoContratoRN tipoContratoRN;

    public TipoContratoBean() {

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
                    seleccionar(tipoContratoRN.getTipoContrato(codigo));
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
        tipoContrato = new TipoContrato();
    }

    public void guardar(boolean nuevo) {

        try {
            if (tipoContrato != null) {

                tipoContrato = tipoContratoRN.guardar(tipoContrato, esNuevo);
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
            tipoContrato.getAuditoria().setDebaja(habDes);
            tipoContratoRN.guardar(tipoContrato, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            tipoContratoRN.eliminar(tipoContrato);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = tipoContratoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<TipoContrato> complete(String query) {
        try {

            lista = tipoContratoRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TipoContrato>();
        }
    }

    public void onSelect(SelectEvent event) {
        tipoContrato = (TipoContrato) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(TipoContrato t) {

        if (t == null) {
            return;
        }

        tipoContrato = t;
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (tipoContrato == null) {
                JsfUtil.addErrorMessage("No hay Tipo de Contrato activa");
                return;
            }

            tipoContrato = tipoContratoRN.duplicar(tipoContrato);
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (tipoContrato == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public List<TipoContrato> getLista() {
        return lista;
    }

    public void setLista(List<TipoContrato> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoContrato getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(TipoContrato tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

}
