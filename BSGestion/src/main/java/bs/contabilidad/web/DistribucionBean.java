/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.web;

import bs.contabilidad.modelo.CentroCosto;
import bs.contabilidad.modelo.Distribucion;
import bs.contabilidad.modelo.ItemDistribucion;
import bs.contabilidad.modelo.SubCuenta;
import bs.contabilidad.rn.DistribucionRN;
import bs.contabilidad.rn.SubCuentaRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
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
 * @author Ctrosch
 */
@ManagedBean
@ViewScoped
public class DistribucionBean extends GenericBean implements Serializable {

    private Distribucion distribucion;
    private CentroCosto centroCosto;
    private List<Distribucion> lista;
    private String codigo;

    @EJB
    private DistribucionRN distribucionRN;

    @EJB
    private SubCuentaRN subCuentaRN;

    // Variables para filtros
    ////
    public DistribucionBean() {

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
                    seleccionar(distribucionRN.getDistribucion(codigo));
                }

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        try {
            distribucion = new Distribucion();
            esNuevo = true;
            modoVista = "D";
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible crear una nueva cuenta " + ex);
        }
    }

    public void guardar(boolean nuevo) {

        try {
            if (distribucion != null) {

                distribucion = distribucionRN.guardar(distribucion, esNuevo);
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
            distribucion.getAuditoria().setDebaja(habDes);
            distribucion = distribucionRN.guardar(distribucion, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            distribucionRN.eliminar(distribucion);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = distribucionRN.getListaByBusqueda(filtro, centroCosto, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;
        centroCosto = null;

        buscar();
    }

    public List<Distribucion> complete(String query) {
        try {
            return distribucionRN.getListaByBusqueda(centroCosto, query, false, 10);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Distribucion>();
        }
    }

    public List<SubCuenta> completeSubCuenta(String query) {
        try {
            return subCuentaRN.getListaByBusqueda(distribucion.getCentroCosto(), query, false, 10);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<SubCuenta>();
        }
    }

    public void onSelect(SelectEvent event) {

        distribucion = (Distribucion) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Distribucion d) {

        if (d == null) {
            return;
        }

        distribucion = d;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (distribucion == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void nuevoItemDistribucion() {

        try {
            distribucionRN.nuevoItemDistribucion(distribucion);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar el item: " + ex);
        }
    }

    public void eliminarItemDistribucion(ItemDistribucion itemDistribucion) {

        try {
            distribucionRN.eliminarItemDistribucion(distribucion, itemDistribucion);
            JsfUtil.addWarningMessage("Se ha borrado el item " + (itemDistribucion.getSubCuenta() != null ? itemDistribucion.getSubCuenta().getDescripcionComplete() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemDistribucion.getSubCuenta() != null ? itemDistribucion.getSubCuenta().getDescripcionComplete() : "") + " " + ex);
        }
    }

    public Distribucion getDistribucion() {
        return distribucion;
    }

    public void setDistribucion(Distribucion distribucion) {
        this.distribucion = distribucion;
    }

    public List<Distribucion> getLista() {
        return lista;
    }

    public void setLista(List<Distribucion> lista) {
        this.lista = lista;
    }

    public CentroCosto getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(CentroCosto centroCosto) {
        this.centroCosto = centroCosto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
