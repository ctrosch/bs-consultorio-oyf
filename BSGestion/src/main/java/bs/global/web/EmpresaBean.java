/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Empresa;
import bs.global.modelo.Sucursal;
import bs.global.modelo.UnidadNegocio;
import bs.global.rn.EmpresaRN;
import bs.global.util.JsfUtil;
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
public class EmpresaBean extends GenericBean implements Serializable {

    private List<Empresa> lista;
    private Empresa empresa;
    private String ID;

    @EJB
    private EmpresaRN empresaRN;

    // Variables para filtros
    //
    //
    /**
     * Creates a new instance of EntidadComercialBean
     */
    public EmpresaBean() {

    }

    @PostConstruct
    public void init() {
        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (ID != null && !ID.isEmpty()) {
                    seleccionar(empresaRN.getEmpresa(ID));
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
        empresa = new Empresa();
        empresa.setCodigo(empresaRN.getProximoCodigo());

    }

    public void guardar(boolean nuevo) {
        try {
            if (empresa != null) {

                empresa = empresaRN.guardar(empresa, esNuevo);
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

    public void nuevoSucursal() {

        try {
            empresaRN.nuevoSucursal(empresa);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar sucursal: " + ex);
        }
    }

    public void eliminarSucursal(Sucursal sucursal) {

        try {
            empresaRN.eliminarSucursal(empresa, sucursal);
            JsfUtil.addWarningMessage("Se ha borrado el item " + (sucursal != null ? sucursal.getNombre() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (sucursal != null ? sucursal.getNombre() : "") + " " + ex);
        }
    }

    public void nuevoUnidadNegocio() {

        try {
            empresaRN.nuevoUnidadNegocio(empresa);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar unidad de negocio: " + ex);
        }
    }

    public void eliminarUnidadNegocio(UnidadNegocio unidadNegocio) {

        try {
            empresaRN.eliminarUnidadNegocio(empresa, unidadNegocio);
            JsfUtil.addWarningMessage("Se ha borrado el item " + (unidadNegocio != null ? unidadNegocio.getNombre() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (unidadNegocio != null ? unidadNegocio.getNombre() : "") + " " + ex);
        }
    }

    public void habilitaDeshabilita(String habDes) {

        try {
            empresa.getAuditoria().setDebaja(habDes);
            empresa = empresaRN.guardar(empresa, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            empresaRN.eliminar(empresa);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {

        cargarFiltroBusqueda();

        lista = empresaRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
        modoVista = "B";
    }

    public List<Empresa> complete(String query) {
        try {
            cargarFiltroBusqueda();

            lista = empresaRN.getListaByBusqueda(null, query, false, 15);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Empresa>();
        }
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

    }

    public void limpiarFiltroBusqueda() {
        txtBusqueda = "";
        mostrarDebaja = false;

        buscar();

    }

    public void onSelect(SelectEvent event) {

        empresa = (Empresa) event.getObject();
        esNuevo = false;
        modoVista = "D";

    }

    public void seleccionar(Empresa e) {

        if (e == null) {
            return;
        }

        empresa = e;
        esNuevo = false;
        modoVista = "D";

    }

    public void duplicar() throws Exception {

        try {
            if (empresa == null) {
                JsfUtil.addErrorMessage("No hay Empresa activo");
                return;
            }

            empresa = empresaRN.duplicar(empresa);
            empresa.setCodigo(empresaRN.getProximoCodigo());
            esNuevo = true;
            modoVista = "D";
        } catch (CloneNotSupportedException ex) {

            JsfUtil.addErrorMessage("No es posible duplicar el objeto");

        }
    }

    public void imprimir() {

        if (empresa == null) {
            JsfUtil.addErrorMessage("No hay Empresa seleccionado, nada para imprimir");
        }
    }

    public List<Empresa> getLista() {
        return lista;
    }

    public void setLista(List<Empresa> lista) {
        this.lista = lista;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getId() {
        return ID;
    }

    public void setId(String id) {
        this.ID = id;
    }

}
