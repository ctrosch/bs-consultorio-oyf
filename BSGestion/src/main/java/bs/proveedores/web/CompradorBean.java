/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.web;

import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.global.web.LocalidadBean;
import bs.proveedores.modelo.Comprador;
import bs.proveedores.rn.CompradorRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class CompradorBean extends GenericBean implements Serializable {

    private String codigo;
    private Comprador comprador;
    private List<Comprador> lista;

    @EJB
    private CompradorRN compradorRN;

    @ManagedProperty(value = "#{localidadBean}")
    protected LocalidadBean localidadBean;

    /**
     * Creates a new instance of CompradorBean
     */
    public CompradorBean() {

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
                    seleccionar(compradorRN.getComprador(codigo));
                }
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        esNuevo = true;
        comprador = new Comprador();
        modoVista = "D";
    }

    public void guardar(boolean nuevo) {

        try {
            if (comprador != null) {

                compradorRN.guardar(comprador, esNuevo);
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
            comprador.getAuditoria().setDebaja(habDes);
            compradorRN.guardar(comprador, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            compradorRN.eliminar(comprador);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = compradorRN.getCompradorByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<Comprador> complete(String query) {
        try {
            lista = compradorRN.getCompradorByBusqueda(query, false);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Comprador>();
        }
    }

    public void onSelect(SelectEvent event) {
        comprador = (Comprador) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Comprador v) {

        if (v == null) {
            return;
        }

        comprador = v;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (comprador == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void procesarLocalidadDlg() {

        if (comprador != null && localidadBean.getLocalidad() != null) {

            comprador.setLocalidad(localidadBean.getLocalidad());

        }
    }

    public void duplicar() throws Exception {

        try {
            if (comprador == null) {
                JsfUtil.addErrorMessage("No hay comprador activo");
                return;
            }

            comprador = compradorRN.duplicar(comprador);
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public Comprador getComprador() {
        return comprador;
    }

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
    }

    public List<Comprador> getLista() {
        return lista;
    }

    public void setLista(List<Comprador> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalidadBean getLocalidadBean() {
        return localidadBean;
    }

    public void setLocalidadBean(LocalidadBean localidadBean) {
        this.localidadBean = localidadBean;
    }

}
