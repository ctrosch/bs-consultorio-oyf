/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.ventas.modelo.CondicionDePagoVenta;
import bs.ventas.modelo.ItemCondicionPagoVenta;
import bs.ventas.rn.CondicionPagoVentaRN;
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
public class CondicionPagoVentaBean extends GenericBean implements Serializable {

    private CondicionDePagoVenta condicion;
    private List<CondicionDePagoVenta> lista;
    private String codigo;

    // Variables para filtros
    private String imputaCuentaCorriente;

    @EJB
    private CondicionPagoVentaRN condicionDePagoVentaRN;

    /**
     * Creates a new instance of CondicionDePagoVentaBean
     */
    public CondicionPagoVentaBean() {

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
                    seleccionar(condicionDePagoVentaRN.getCondicionDePagoVenta(codigo));
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
        condicion = new CondicionDePagoVenta();
    }

    public void guardar(boolean nuevo) {
        try {
            if (condicion != null) {

                condicion = condicionDePagoVentaRN.guardar(condicion, esNuevo);
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
//            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = condicionDePagoVentaRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<CondicionDePagoVenta> complete(String query) {
        try {

            lista = condicionDePagoVentaRN.getListaByBusqueda(query, mostrarDebaja, 10);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CondicionDePagoVenta>();
        }
    }

    public void onSelect(SelectEvent event) {
        condicion = (CondicionDePagoVenta) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(CondicionDePagoVenta c) {

        if (c == null) {
            return;
        }

        condicion = c;
        esNuevo = false;
        modoVista = "D";
    }

    /*
    public void nuevoItemCondicionPagoVenta() {

        try {
            cursoRN.nuevoItemMateria(curso);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item materia: " + ex);
        }
    }

    public void eliminarItemCondicionPago(ItemCursoMateria itemMateria) {

        try {
            cursoRN.eliminarItemMateria(curso, itemMateria);
            JsfUtil.addWarningMessage("Se ha borrado el item materia " + (itemMateria.getMateria() != null ? itemMateria.getMateria().getNombre() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemMateria.getMateria() != null ? itemMateria.getMateria().getNombre() : "") + " " + ex);
        }
    }
     */
    public void imprimir() {

        if (condicion == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            condicion.getAuditoria().setDebaja(habDes);
            condicion = condicionDePagoVentaRN.guardar(condicion, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void nuevoItemCondicionPagoVenta() {

        try {

            condicionDePagoVentaRN.nuevoItemCondicionPagoVenta(condicion);

        } catch (Exception ex) {

            ex.printStackTrace();

            JsfUtil.addErrorMessage("No es posible agregar un nuevo item " + ex);
        }
    }

    public void eliminarItemCondicionPago(ItemCondicionPagoVenta item) {

        try {

            condicionDePagoVentaRN.eliminarItemCondicionPagoVenta(condicion, item);

        } catch (Exception ex) {

            JsfUtil.addErrorMessage("No es posible eliminar item " + ex);
        }
    }

    public CondicionDePagoVenta getCondicion() {
        return condicion;
    }

    public void setCondicion(CondicionDePagoVenta condicion) {
        this.condicion = condicion;
    }

    public List<CondicionDePagoVenta> getLista() {
        return lista;
    }

    public void setLista(List<CondicionDePagoVenta> lista) {
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
