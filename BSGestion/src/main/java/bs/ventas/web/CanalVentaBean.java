/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.ventas.modelo.CanalVenta;
import bs.ventas.rn.CanalVentaRN;
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
public class CanalVentaBean extends GenericBean implements Serializable {

    private String codigo;
    private CanalVenta canalVenta;
    private List<CanalVenta> lista;

    @EJB
    private CanalVentaRN canalVentaRN;

    public CanalVentaBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (codigo != null && !codigo.isEmpty()) {
                    seleccionar(canalVentaRN.getCanalVenta(codigo));
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
        canalVenta = new CanalVenta();
    }

    public void guardar(boolean nuevo) {

        try {
            if (canalVenta != null) {

                canalVenta = canalVentaRN.guardar(canalVenta, esNuevo);
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
            canalVenta.getAuditoria().setDebaja(habDes);
            canalVentaRN.guardar(canalVenta, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            canalVentaRN.eliminar(canalVenta);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();
        lista = canalVentaRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<CanalVenta> complete(String query) {
        try {

            lista = canalVentaRN.getListaByBusqueda(query, false, 10);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CanalVenta>();
        }
    }

    public void onSelect(SelectEvent event) {
        canalVenta = (CanalVenta) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(CanalVenta c) {

        if (c == null) {
            return;
        }

        canalVenta = c;
        esNuevo = false;
        modoVista = "D";
    }

    public void duplicar() throws Exception {

        try {
            if (canalVenta == null) {
                JsfUtil.addErrorMessage("No hay Canal de Venta activo");
                return;
            }

            canalVenta = canalVentaRN.duplicar(canalVenta);
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (canalVenta == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public CanalVenta getCanalVenta() {
        return canalVenta;
    }

    public void setCanalVenta(CanalVenta canalVenta) {
        this.canalVenta = canalVenta;
    }

    public List<CanalVenta> getLista() {
        return lista;
    }

    public void setLista(List<CanalVenta> lista) {
        this.lista = lista;
    }

}
