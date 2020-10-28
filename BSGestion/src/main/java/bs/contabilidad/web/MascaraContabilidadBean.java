/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.web;

import bs.contabilidad.modelo.ItemMascaraContabilidad;
import bs.contabilidad.modelo.MascaraContabilidad;
import bs.contabilidad.rn.MascaraContabilidadRN;
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
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class MascaraContabilidadBean extends GenericBean implements Serializable {

    private MascaraContabilidad mascara;
    private List<MascaraContabilidad> lista;
    private String codigo;

    @EJB
    private MascaraContabilidadRN mascaraContabilidadRN;

    // Variables para filtros
    ///
    public MascaraContabilidadBean() {

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
                    seleccionar(mascaraContabilidadRN.getMascaraContabilidad(codigo));
                }
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void nuevo() {

        esNuevo = true;
        buscaMovimiento = false;
        modoVista = "D";
        mascara = new MascaraContabilidad();
    }

    public void guardar(boolean nuevo) {

        try {
            if (mascara != null) {

                mascara = mascaraContabilidadRN.guardar(mascara, esNuevo);
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
            mascara.getAuditoria().setDebaja(habDes);
            mascara = mascaraContabilidadRN.guardar(mascara, false);
            JsfUtil.addInfoMessage("Los datos se actualizaron correctamente");

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible actualizar los datos " + ex);
        }
    }

    public void eliminar() {
        try {
            mascaraContabilidadRN.eliminar(mascara);
            nuevo();
            JsfUtil.addInfoMessage("Los datos fueron borrados");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible borrar los datos " + ex);
        }
    }

    public void buscar() {
        cargarFiltroBusqueda();

        lista = mascaraContabilidadRN.getListaByBusqueda(filtro, txtBusqueda, mostrarDebaja, cantidadRegistros);
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

    public List<MascaraContabilidad> complete(String query) {
        try {
            lista = mascaraContabilidadRN.getListaByBusqueda(query, false, 15);
            return lista;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<MascaraContabilidad>();
        }
    }

    public void onSelect(SelectEvent event) {
        mascara = (MascaraContabilidad) event.getObject();
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void seleccionar(MascaraContabilidad e) {

        if (e == null) {
            return;
        }

        mascara = e;
        esNuevo = false;
        buscaMovimiento = false;
        modoVista = "D";
    }

    public void imprimir() {

        if (mascara == null) {
            JsfUtil.addErrorMessage("No hay entidad seleccionada, nada para imprimir");
        }
    }

    public void nuevoItem() {

        try {
            mascaraContabilidadRN.nuevoItem(mascara);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item equivalencia proveedor: " + ex);
        }
    }

    public void eliminarItem(ItemMascaraContabilidad item) {

        try {
            mascaraContabilidadRN.eliminarItem(mascara, item);
            JsfUtil.addWarningMessage("Se ha borrado el item " + (item.getCuentaContable() != null ? item.getCuentaContable().getDescripcion() : "") + "");
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error " + (item.getCuentaContable() != null ? item.getCuentaContable().getDescripcion() : "") + " " + ex);
        }
    }

    public MascaraContabilidad getMascara() {
        return mascara;
    }

    public void setMascara(MascaraContabilidad mascara) {
        this.mascara = mascara;
    }

    public List<MascaraContabilidad> getLista() {
        return lista;
    }

    public void setLista(List<MascaraContabilidad> lista) {
        this.lista = lista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
