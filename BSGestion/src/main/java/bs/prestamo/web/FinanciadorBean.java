/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.prestamo.modelo.Financiador;
import bs.prestamo.modelo.ItemCuentaContableFinanciador;
import bs.prestamo.rn.FinanciadorRN;
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
public class FinanciadorBean extends GenericBean implements Serializable {

    private Financiador financiador;
    private List<Financiador> lista;

    private Integer ID;

    @EJB
    private FinanciadorRN financiadorRN;

    // Variables para filtros
    ///
    public FinanciadorBean() {

    }

    @PostConstruct
    public void init() {
        buscar();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (ID != null) {
                    seleccionar(financiadorRN.getFinanciador(ID));
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
        financiador = new Financiador();
    }

    public void guardar(boolean nuevo) {

        try {
            if (financiador != null) {

                financiadorRN.guardar(financiador, esNuevo);
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
            financiador.getAuditoria().setDebaja(habDes);
            financiadorRN.guardar(financiador, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            financiadorRN.eliminar(financiador);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = financiadorRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<Financiador> complete(String query) {
        try {
            lista = financiadorRN.getListaByBusqueda(query, false, 5);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Financiador>();
        }
    }

    public void onSelect(SelectEvent event) {
        financiador = (Financiador) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(Financiador u) {

        if (u == null) {
            return;
        }

        financiador = u;
        esNuevo = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (financiador == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void nuevoItemCuentaContable() {

        try {
            financiadorRN.nuevoItemCuentaContable(financiador);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item cuenta contable: " + ex);
        }
    }

    public void eliminarItemCuentaContable(ItemCuentaContableFinanciador itemCuentaContable) {

        try {
            financiadorRN.eliminarItemCuentaContable(financiador, itemCuentaContable);
            JsfUtil.addWarningMessage("Se ha borrado el item cuenta contable " + (itemCuentaContable.getCuentaContable() != null ? itemCuentaContable.getCuentaContable().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (itemCuentaContable.getCuentaContable() != null ? itemCuentaContable.getCuentaContable().getDescripcion() : "") + " " + ex);
        }
    }

    //--------------------------------------------------------------------
    public Financiador getFinanciador() {
        return financiador;
    }

    public void setFinanciador(Financiador unidadMedida) {
        this.financiador = unidadMedida;
    }

    public List<Financiador> getLista() {
        return lista;
    }

    public void setLista(List<Financiador> lista) {
        this.lista = lista;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

}
