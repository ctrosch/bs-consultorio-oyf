/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.proveedores.modelo.CondicionPagoProveedor;
import bs.proveedores.modelo.ItemCondicionPagoProveedor;
import bs.proveedores.rn.CondicionPagoProveedorRN;
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
public class CondicionPagoProveedorBean extends GenericBean implements Serializable {

    private CondicionPagoProveedor condicion;
    private List<CondicionPagoProveedor> lista;
    private String codigo;

    @EJB
    private CondicionPagoProveedorRN condicionPagoRN;

    // Variables para filtros
    private String imputaCuentaCorriente;

    /**
     * Creates a new instance of CondicionPagoProveedorBean
     */
    public CondicionPagoProveedorBean() {

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
                    seleccionar(condicionPagoRN.getCondicionPagoProveedor(codigo));
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
        condicion = new CondicionPagoProveedor();
    }

    public void guardar(boolean nuevo) {
        try {
            if (condicion != null) {

                condicion = condicionPagoRN.guardar(condicion, esNuevo);
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
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = condicionPagoRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (imputaCuentaCorriente != null && !imputaCuentaCorriente.isEmpty()) {

            filtro.put("imputaCuentaCorriente = ", "'" + imputaCuentaCorriente + "'");
        }

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        imputaCuentaCorriente = "";

        buscar();

    }

    public List<CondicionPagoProveedor> complete(String query) {
        try {
            lista = condicionPagoRN.getListaByBusqueda(query, mostrarDebaja);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CondicionPagoProveedor>();
        }
    }

    public void onSelect(SelectEvent event) {
        condicion = (CondicionPagoProveedor) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(CondicionPagoProveedor c) {

        if (c == null) {
            return;
        }

        condicion = c;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (condicion == null) {
            JsfUtil.addErrorMessage("No hay condicion seleccionada, nada para imprimir");
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            condicion.getAuditoria().setDebaja(habDes);
            condicion = condicionPagoRN.guardar(condicion, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void nuevoItemCondicionPagoProveedor() {

        try {

            condicionPagoRN.nuevoItemCondicionPagoProveedor(condicion);

        } catch (Exception ex) {

            ex.printStackTrace();

            JsfUtil.addErrorMessage("No es posible agregar un nuevo item " + ex);
        }
    }
    
    public void eliminarItemCondicionPago(ItemCondicionPagoProveedor item) {

        try {

            condicionPagoRN.eliminarItemCondicionPago(condicion, item);

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("No es posible eliminar item " + ex);
        }
    }

    public CondicionPagoProveedor getCondicion() {
        return condicion;
    }

    public void setCondicion(CondicionPagoProveedor condicion) {
        this.condicion = condicion;
    }

    public List<CondicionPagoProveedor> getLista() {
        return lista;
    }

    public void setLista(List<CondicionPagoProveedor> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getImputaCuentaCorriente() {
        return imputaCuentaCorriente;
    }

    public void setImputaCuentaCorriente(String imputaCuentaCorriente) {
        this.imputaCuentaCorriente = imputaCuentaCorriente;
    }

}
