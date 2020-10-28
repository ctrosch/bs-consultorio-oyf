/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.tesoreria.modelo.TipoCuentaTesoreria;
import bs.tesoreria.rn.TipoCuentaTesoreriaRN;
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
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class TipoCuentaTesoreriaBean extends GenericBean implements Serializable {

    private TipoCuentaTesoreria cuentaTesoreria;
    private List<TipoCuentaTesoreria> lista;
    private String codigo;

    @EJB
    private TipoCuentaTesoreriaRN tipoCuentaTesoreriaRN;

    /**
     * Creates a new instance of TipoCuentaTesoreriaBean
     */
    public TipoCuentaTesoreriaBean() {

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
                    seleccionar(tipoCuentaTesoreriaRN.getTipoCuentaTesoreria(codigo));
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
        cuentaTesoreria = new TipoCuentaTesoreria();
    }

    public void guardar(boolean nuevo) {

        try {
            if (cuentaTesoreria != null) {

                tipoCuentaTesoreriaRN.guardar(cuentaTesoreria, esNuevo);
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
            cuentaTesoreria.getAuditoria().setDebaja(habDes);
            tipoCuentaTesoreriaRN.guardar(cuentaTesoreria, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            tipoCuentaTesoreriaRN.eliminar(cuentaTesoreria);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = tipoCuentaTesoreriaRN.getTipoCuentaTesoreriaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<TipoCuentaTesoreria> complete(String query) {
        try {
            lista = tipoCuentaTesoreriaRN.getTipoCuentaTesoreriaByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TipoCuentaTesoreria>();
        }
    }

    public void onSelect(SelectEvent event) {
        cuentaTesoreria = (TipoCuentaTesoreria) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(TipoCuentaTesoreria v) {

        if (v == null) {
            return;
        }

        cuentaTesoreria = v;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (cuentaTesoreria == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public TipoCuentaTesoreria getTipoCuentaTesoreria() {
        return cuentaTesoreria;
    }

    public void setTipoCuentaTesoreria(TipoCuentaTesoreria o) {
        this.cuentaTesoreria = o;
    }

    public List<TipoCuentaTesoreria> getLista() {
        return lista;
    }

    public void setLista(List<TipoCuentaTesoreria> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
