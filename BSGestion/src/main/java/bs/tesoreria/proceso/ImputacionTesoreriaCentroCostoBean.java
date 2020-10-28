/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.proceso;

import bs.contabilidad.modelo.CentroCosto;
import bs.contabilidad.modelo.SubCuenta;
import bs.contabilidad.rn.SubCuentaRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.UnidadNegocio;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.tesoreria.modelo.ItemMovimientoTesoreriaCentroCosto;
import bs.tesoreria.modelo.ItemMovimientoTesoreriaSubcuenta;
import bs.tesoreria.rn.ItemMovimientoTesoreriaRN;
import bs.tesoreria.rn.MovimientoTesoreriaRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;

/**
 *
 * @author Ctrosch
 */
@ManagedBean
@ViewScoped
public class ImputacionTesoreriaCentroCostoBean extends GenericBean implements Serializable {

    private ItemMovimientoTesoreria itemMovimiento;
    private CentroCosto centroCosto;
    private List<ItemMovimientoTesoreria> lista;

    private UnidadNegocio unidadNegocio;

    @EJB
    private ItemMovimientoTesoreriaRN itemMovimientoRN;
    @EJB
    private SubCuentaRN subCuentaRN;
    @EJB
    MovimientoTesoreriaRN tesoreriaRN;

    public ImputacionTesoreriaCentroCostoBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                super.iniciar();
                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

//    public void nuevo(){
//
//        try {
//            centroCosto = new CentroCosto();
//            esNuevo = true;
//            modoVista = "D";
//        } catch (Exception ex) {
//            JsfUtil.addErrorMessage("No es posible crear una nueva cuenta " + ex);
//        }
//    }
    public void guardar(boolean nuevo) {

        try {
            if (itemMovimiento != null) {

                itemMovimiento = itemMovimientoRN.guardar(itemMovimiento);
                esNuevo = false;
                JsfUtil.addInfoMessage("Los datos se guardaron correctamente");
            } else {
                JsfUtil.addInfoMessage("No hay datos para guardar");
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("No es posible guardar los datos " + ex);
        }
    }

    public void buscar() {

        if (!validarParametros()) {
            return;
        }
        cargarFiltroBusqueda();

        lista = itemMovimientoRN.getListaByBusqueda(filtro);
        itemMovimientoRN.validarImputacionCentroCostoLista(lista);
        modoVista = "B";
    }

    public boolean validarParametros() {

        if (fechaMovimientoDesde != null && fechaMovimientoHasta != null) {
            if (fechaMovimientoHasta.before(fechaMovimientoDesde)) {
                JsfUtil.addWarningMessage("La fecha de desde, no puede ser mayor a la fecha hasta.");
                return false;
            }
        }

        if (numeroFormularioDesde != null && numeroFormularioHasta != null) {
            if (numeroFormularioDesde > numeroFormularioHasta) {
                JsfUtil.addWarningMessage("Número de formulario desde es mayor al número de formulario hasta");
                return false;
            }
        }

        return true;
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        if (formulario != null) {
            filtro.put("movimiento.formulario.codigo = ", "'" + formulario.getCodigo() + "'");
        }

        if (numeroFormularioDesde != null) {

            filtro.put("movimiento.numeroFormulario >=", String.valueOf(numeroFormularioDesde));
        }

        if (numeroFormularioHasta != null) {

            filtro.put("movimiento.numeroFormulario <=", String.valueOf(numeroFormularioHasta));
        }

        if (fechaMovimientoDesde != null) {

            filtro.put("movimiento.fechaMovimiento >= ", JsfUtil.getFechaSQL(fechaMovimientoDesde));
        }

        if (fechaMovimientoHasta != null) {

            filtro.put("movimiento.fechaMovimiento <= ", JsfUtil.getFechaSQL(fechaMovimientoHasta));
        }

        if (unidadNegocio != null) {

            filtro.put("movimiento.unidadNegocio.codigo = ", "'" + unidadNegocio.getCodigo() + "'");
        }

        if (seleccionaPendiente) {
            //filtro.put("itemsProducto.")
        }

//        System.err.println("filtro busqueda " + filtro);
    }

    public void resetParametros() {
        formulario = null;
        unidadNegocio = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
    }

    public void onSelect(SelectEvent event) {
        itemMovimiento = (ItemMovimientoTesoreria) event.getObject();
        esNuevo = false;
        modoVista = "D";
    }

    public void seleccionar(ItemMovimientoTesoreria d) {

        if (d == null) {
            return;
        }

        itemMovimiento = d;
        esNuevo = false;
        modoVista = "D";
    }

    public void calcularImportes(ItemMovimientoTesoreria item) {
        try {
            itemMovimientoRN.calcularImportes(item);
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para calcular totales del comprobante");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void calcularImportes() {
        try {
            itemMovimientoRN.calcularImportes(itemMovimiento);
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para calcular totales del comprobante");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onRowToggle(ToggleEvent event) {
        centroCosto = ((ItemMovimientoTesoreriaCentroCosto) event.getData()).getCentroCosto();
    }

    public List<SubCuenta> completeSubCuenta(String query) {

        try {
            return subCuentaRN.getListaByBusqueda(centroCosto, query, false, 10);

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<SubCuenta>();
        }
    }

    public void nuevoItemSubCuenta(ItemMovimientoTesoreriaCentroCosto cc) {

        try {
            tesoreriaRN.nuevoItemMovimientoSubCuenta(cc);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible agregar item Sub Cuenta " + ex);
        }
    }

    public void eliminarItemSubCuenta(ItemMovimientoTesoreriaCentroCosto itemCentroCosto, ItemMovimientoTesoreriaSubcuenta itemSubCuenta) {

        try {
            tesoreriaRN.eliminarItemSubCuenta(itemCentroCosto, itemSubCuenta);
        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("No es posible quitar item Sub Cuenta " + ex);
        }
    }

//    public void nuevoItemSubCuenta() {
//
//        try {
//            itemCentroCostoRN.nuevoItemSubCuenta(itemCentroCosto);
//        } catch (ExcepcionGeneralSistema ex) {
//            JsfUtil.addErrorMessage("No es posible agregar la subcuenta: " + ex);
//        }
//    }
//
//    public void eliminarItemSubCuenta(SubCuenta subCuenta) {
//
//        try {
//            itemCentroCostoRN.eliminarItemSubCuenta(itemCentroCosto, subCuenta);
//            JsfUtil.addWarningMessage("Se ha borrado la subcuenta " + (subCuenta.getCentroCosto()!= null ? subCuenta.getCentroCosto().getDescripcion() : "") + "");
//        } catch (Exception ex) {
//            JsfUtil.addErrorMessage("Error " + (subCuenta.getCentroCosto()!= null ? subCuenta.getCentroCosto().getDescripcion():"") + " " + ex);
//        }
//    }
    public ItemMovimientoTesoreria getItemMovimiento() {
        return itemMovimiento;
    }

    public void setItemMovimiento(ItemMovimientoTesoreria itemMovimiento) {
        this.itemMovimiento = itemMovimiento;
    }

    public List<ItemMovimientoTesoreria> getLista() {
        return lista;
    }

    public void setLista(List<ItemMovimientoTesoreria> lista) {
        this.lista = lista;
    }

    public ItemMovimientoTesoreriaRN getItemCentroCostoRN() {
        return itemMovimientoRN;
    }

    public void setItemCentroCostoRN(ItemMovimientoTesoreriaRN itemCentroCostoRN) {
        this.itemMovimientoRN = itemCentroCostoRN;
    }

    public CentroCosto getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(CentroCosto centroCosto) {
        this.centroCosto = centroCosto;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

}
