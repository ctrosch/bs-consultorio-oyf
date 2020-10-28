/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.web;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.proveedores.modelo.ItemPendienteCuentaCorrienteProveedor;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.proveedores.rn.CuentaCorrienteProveedorRN;
import bs.proveedores.rn.MovimientoProveedorRN;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.tesoreria.modelo.MovimientoTesoreria;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class OrdenPagoBean extends MovimientoTesoreriaBean {

    @EJB
    private CuentaCorrienteProveedorRN cuentaCorrienteRN;
    @EJB
    private MovimientoProveedorRN proveedorRN;

    private List<ItemPendienteCuentaCorrienteProveedor> creditos;
    private BigDecimal totalHaber;
    private BigDecimal totalRetenciones;
    private BigDecimal totalAnticipo;

    private boolean facturasConfirmadas;

    /**
     * Creates a new instance of CajaBean
     */
    public OrdenPagoBean() {
        facturasConfirmadas = false;
    }

    @Override
    public void iniciarVariables() {

        if (!beanIniciado) {
            totalHaber = BigDecimal.ZERO;
            totalRetenciones = BigDecimal.ZERO;
            totalAnticipo = BigDecimal.ZERO;
            super.iniciarVariables();
        }
    }

    @Override
    public void guardar(boolean nuevo) {

        totalHaber = cuentaCorrienteRN.sumarSaldos(creditos);

        if (!puedoGuardar()) {
            return;
        }

        super.guardar(nuevo);

        if (m.getId() != null) {

            totalHaber = BigDecimal.ZERO;
            totalRetenciones = BigDecimal.ZERO;
            totalAnticipo = BigDecimal.ZERO;
            creditos = new ArrayList<ItemPendienteCuentaCorrienteProveedor>();
            facturasConfirmadas = false;
        }
    }

    @Override
    public boolean puedoGuardar() {

        boolean todoOK = true;

        if (!super.puedoGuardar()) {
            todoOK = false;
        }

        if (m.getComprobanteProveedor() == null) {
            JsfUtil.addErrorMessage("El comprobante de proveedor no puede estar vacío");
            todoOK = false;
        }

        if (totalHaber.add(totalRetenciones.negate()).compareTo(m.getImporteTotalHaber()) != 0) {
            JsfUtil.addErrorMessage("El importe a aplicar es diferente a la suma de los conceptos ingresados");
            todoOK = false;
        }

        return todoOK;
    }

    @Override
    public void nuevo() {

        super.nuevo();

        totalHaber = BigDecimal.ZERO;
        totalRetenciones = BigDecimal.ZERO;
        creditos = new ArrayList<ItemPendienteCuentaCorrienteProveedor>();
        facturasConfirmadas = false;
    }

    public void procesarProveedor() {

        super.procesarEntidad();
        creditos = cuentaCorrienteRN.getCreditosPendientes(m.getEntidad().getNrocta(), m.getMonedaRegistracion().getCodigo(), m.getComprobante().getComprobanteInterno());
        m.setCreditos(creditos);
    }

    @Override
    public void seleccionarMovimiento(MovimientoTesoreria mSel) {

        super.seleccionarMovimiento(mSel);

        if (mSel.getMovimientoProveedor() != null) {

            proveedorRN.calcularImportes(mSel.getMovimientoProveedor(), true);

            m.setRetenciones(mSel.getMovimientoProveedor().getRetenciones());

            for (MovimientoProveedor mr : m.getRetenciones()) {
                proveedorRN.calcularImportes(mr, true);
            }
        }
        facturasConfirmadas = true;
    }

    public void marcaAnticipo() {

        if (!m.isEsAnticipo()) {
            totalAnticipo = BigDecimal.ZERO;
        }

        m.getRetenciones().clear();
        totalHaber = totalAnticipo;
        facturasConfirmadas = false;
    }

    public void marcarCredito(ItemPendienteCuentaCorrienteProveedor i) {

        if (i.isSeleccionado()) {
            i.setImporteAplicar(i.getPendiente());
            i.setImporteRetencion(BigDecimal.ZERO);
        } else {
            i.setImporteAplicar(BigDecimal.ZERO);
            i.setImporteRetencion(BigDecimal.ZERO);
        }

        m.getRetenciones().clear();
        totalHaber = cuentaCorrienteRN.sumarSaldos(creditos);
        totalRetenciones = cuentaCorrienteRN.sumarSaldosRetenciones(creditos);
        facturasConfirmadas = false;
    }

    public void modificaCredito(ItemPendienteCuentaCorrienteProveedor i) {

        if (i.getImporteAplicar() == null) {
            i.setImporteAplicar(BigDecimal.ZERO);
        }

        if (i.isSeleccionado()) {

            if (i.getImporteAplicar() == null) {
                i.setImporteAplicar(BigDecimal.ZERO);
            }

            if (i.getImporteAplicar().compareTo(i.getPendiente()) > 0) {
                i.setImporteAplicar(i.getPendiente());
                i.setImporteRetencion(BigDecimal.ZERO);
                JsfUtil.addErrorMessage("El importe a aplicar es mayor al pendiente");
            }
        } else {
            i.setImporteAplicar(BigDecimal.ZERO);
            i.setImporteRetencion(BigDecimal.ZERO);
            JsfUtil.addErrorMessage("El saldo pendiente que está modificando no está seleccionado");
        }

        m.getRetenciones().clear();
        totalHaber = cuentaCorrienteRN.sumarSaldos(creditos);
        totalRetenciones = cuentaCorrienteRN.sumarSaldosRetenciones(creditos);
        facturasConfirmadas = false;
    }

    public void confirmarFacturasPagar() {

        try {

            if (m.isEsAnticipo()) {

                creditos.clear();
                creditos.add(cuentaCorrienteRN.generarItemCuentaCorrienteAnticipo(m, totalAnticipo));
            }

            proveedorRN.generarRetenciones(m, creditos);
            totalHaber = cuentaCorrienteRN.sumarSaldos(creditos);
            totalRetenciones = cuentaCorrienteRN.sumarSaldosRetenciones(creditos);
            facturasConfirmadas = true;

            if (!m.getRetenciones().isEmpty()) {
                JsfUtil.addInfoMessage("Se han generado retenciones para este pago");
            }

        } catch (ExcepcionGeneralSistema ex) {
            JsfUtil.addErrorMessage("Problemas para confirmar facturas a pagar " + ex);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nuevaSeleccionFacturas() {
        try {
            m.getRetenciones().clear();
            for (ItemPendienteCuentaCorrienteProveedor ip : creditos) {

                ip.setSeleccionado(false);
                marcarCredito(ip);
            }
            totalHaber = cuentaCorrienteRN.sumarSaldos(creditos);
            totalRetenciones = cuentaCorrienteRN.sumarSaldosRetenciones(creditos);
            tesoreriaRN.calcularImportes(m);
            totalAnticipo = BigDecimal.ZERO;
            facturasConfirmadas = false;

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Problemas para realizar nueva selección de factuas a pagar");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void imprimirRetención(MovimientoProveedor r) {

        try {

            if (r.getId() == null) {
                JsfUtil.addErrorMessage("No se puede imprimir - La retención no ha sido guardada");
                return;
            }

            formulario = r.getFormulario();

            if (formulario == null) {
                JsfUtil.addErrorMessage("No se puede imprimir - No se encontró formulario");
                return;
            }

            if (formulario.getReporte() == null) {
                JsfUtil.addErrorMessage("No se puede imprimir - El formulario no tienen reporte asociado");
                return;
            }
            HashMap parameters = new HashMap();

            nombreArchivo = formulario.getCodigo() + "-" + r.getNumeroFormulario();

            MovimientoProveedor mp = proveedorRN.getMovimiento(formulario.getCodigo(), r.getNumeroFormulario());

            parameters.put("ID", mp.getId());
            parameters.put("CANT_COPIAS", 1);

            reportFactory.exportReportToPdfFile(formulario.getReporte(), nombreArchivo, parameters);
            muestraReporte = true;

        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage("No se encontró el archivo: " + nombreArchivo + ".jasper");
            muestraReporte = false;

        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e.getMessage());
            muestraReporte = false;
        }
    }

    public void asignarTotal(ItemMovimientoTesoreria item) {

        if (item.getDebeHaber().equals("D")) {
            m.getItemsDebe().forEach(i -> i.setImporte(BigDecimal.ZERO));
        } else {
            m.getItemsHaber().forEach(i -> i.setImporte(BigDecimal.ZERO));
        }

        item.setImporte(totalHaber.add(totalRetenciones.negate()));
        calcularImportes();
    }

    //--------------------------------------------------------------------------
    public List<ItemPendienteCuentaCorrienteProveedor> getCreditos() {
        return creditos;
    }

    public void setCreditos(List<ItemPendienteCuentaCorrienteProveedor> creditos) {
        this.creditos = creditos;
    }

    public BigDecimal getTotalHaber() {
        return totalHaber;
    }

    public void setTotalHaber(BigDecimal totalHaber) {
        this.totalHaber = totalHaber;
    }

    public BigDecimal getTotalRetenciones() {
        return totalRetenciones;
    }

    public void setTotalRetenciones(BigDecimal totalRetenciones) {
        this.totalRetenciones = totalRetenciones;
    }

    public boolean isFacturasConfirmadas() {
        return facturasConfirmadas;
    }

    public void setFacturasConfirmadas(boolean facturasConfirmadas) {
        this.facturasConfirmadas = facturasConfirmadas;
    }

    public BigDecimal getTotalAnticipo() {
        return totalAnticipo;
    }

    public void setTotalAnticipo(BigDecimal totalAnticipo) {
        this.totalAnticipo = totalAnticipo;
    }
}
