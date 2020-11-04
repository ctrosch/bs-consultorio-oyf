/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.web;

import bs.global.util.JsfUtil;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.ventas.modelo.ItemPendienteCuentaCorriente;
import bs.ventas.rn.CuentaCorrienteRN;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class ReciboBean extends MovimientoTesoreriaBean {

    @EJB
    private CuentaCorrienteRN cuentaCorrienteRN;

    private List<ItemPendienteCuentaCorriente> debitos;
    private BigDecimal totalDebe;
    private BigDecimal totalDebeSecundario;

    /**
     * Creates a new instance of CajaBean
     */
    public ReciboBean() {

    }

    @Override
    public void iniciarVariables() {

        if (!beanIniciado) {
            totalDebe = BigDecimal.ZERO;
            totalDebeSecundario = BigDecimal.ZERO;
            super.iniciarVariables();
        }
    }

    @Override
    public void guardar(boolean nuevo) {

        if (m.isEsAnticipo()) {
            m.setDebitos(null);
        } else {

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
                totalDebe = cuentaCorrienteRN.sumarSaldos(debitos, m.getMonedaRegistracion().getCodigo());
            }

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
                totalDebeSecundario = cuentaCorrienteRN.sumarSaldos(debitos, m.getMonedaRegistracion().getCodigo());
            }

        }

        if (!puedoGuardar()) {
            return;
        }

        super.guardar(nuevo);

        if (m.getId() != null) {
            totalDebe = BigDecimal.ZERO;
            totalDebeSecundario = BigDecimal.ZERO;
            debitos = new ArrayList<>();
        }
    }

    @Override
    public boolean puedoGuardar() {

        boolean todoOK = true;

        if (!super.puedoGuardar()) {
            todoOK = false;
        }

        if (m.getComprobanteVenta() == null) {
            JsfUtil.addErrorMessage("El comprobante de ventas no puede estar vacío");
            todoOK = false;
        }

        if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {

            if (!m.isEsAnticipo() && totalDebe.compareTo(m.getImporteTotalDebe()) != 0) {
                JsfUtil.addErrorMessage("El importe a aplicar es diferente a la suma de los conceptos ingresados");
                todoOK = false;
            }
        }

        if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
            if (!m.isEsAnticipo() && totalDebeSecundario.compareTo(m.getImporteTotalDebeSecundario()) != 0) {
                JsfUtil.addErrorMessage("El importe a aplicar es diferente a la suma de los conceptos ingresados");
                todoOK = false;
            }
        }

        return todoOK;
    }

    public void modificarCotizacion() {

    }

    public void procesarCliente() {

        super.procesarEntidad();
        m.setEsAnticipo(false);
        debitos = cuentaCorrienteRN.getDebitosPendientes(m.getEntidad().getNrocta(), m.getMonedaRegistracion().getCodigo(), m.getComprobante().getComprobanteInterno());
        m.setDebitos(debitos);
    }

    public void procesarDebito() {

    }

    public void modificaDebito(ItemPendienteCuentaCorriente i) {

        if (i.getImporteAplicar() == null) {
            i.setImporteAplicar(BigDecimal.ZERO);
        }

        if (i.isSeleccionado()) {

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {

                if (i.getImporteAplicar().compareTo(i.getPendiente()) > 0) {
                    i.setImporteAplicar(i.getPendiente());
                    JsfUtil.addErrorMessage("El importe a aplicar es mayor al pendiente");
                }
            }

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {

                if (i.getImporteAplicarSecundario().compareTo(i.getPendienteSecundario()) > 0) {
                    i.setImporteAplicarSecundario(i.getPendienteSecundario());
                    JsfUtil.addErrorMessage("El importe a aplicar es mayor al pendiente");
                }

                i.setImporteAplicar(i.getImporteAplicarSecundario().multiply(m.getCotizacion()).setScale(2, RoundingMode.HALF_UP));
            }

        } else {
            i.setImporteAplicar(BigDecimal.ZERO);
            i.setImporteAplicarSecundario(BigDecimal.ZERO);
            JsfUtil.addErrorMessage("El saldo pendiente que está modificando no está seleccionado");
        }

        calcularTotal();

    }

    public void marcarDebito(ItemPendienteCuentaCorriente i) {

        if (i.isSeleccionado()) {

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {

                i.setImporteAplicar(i.getPendiente());
                i.setImporteAplicarSecundario(i.getImporteAplicar().divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));
            }

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {

                i.setImporteAplicarSecundario(i.getPendienteSecundario());
                i.setImporteAplicar(i.getImporteAplicarSecundario().multiply(m.getCotizacion()).setScale(2, RoundingMode.HALF_UP));
            }

        } else {
            i.setImporteAplicar(BigDecimal.ZERO);
            i.setImporteAplicarSecundario(BigDecimal.ZERO);
        }

        calcularTotal();
    }

    public void calcularTotal() {

        if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
            totalDebe = cuentaCorrienteRN.sumarSaldos(debitos, m.getMonedaRegistracion().getCodigo());
        }

        if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
            totalDebeSecundario = cuentaCorrienteRN.sumarSaldos(debitos, m.getMonedaRegistracion().getCodigo());
        }
    }

    @Override
    public void asignarTotal(ItemMovimientoTesoreria item) {

        if (item.getDebeHaber().equals("D")) {
            m.getItemsDebe().forEach(i -> i.setImporte(BigDecimal.ZERO));
        } else {
            m.getItemsHaber().forEach(i -> i.setImporte(BigDecimal.ZERO));
        }

        if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
            item.setImporte(totalDebe);
        }

        if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
            item.setImporte(totalDebeSecundario);
        }

        calcularImportes();
    }

    //--------------------------------------------------------------------------
    public List<ItemPendienteCuentaCorriente> getDebitos() {
        return debitos;
    }

    public void setDebitos(List<ItemPendienteCuentaCorriente> debitos) {
        this.debitos = debitos;
    }

    public BigDecimal getTotalDebe() {
        return totalDebe;
    }

    public void setTotalDebe(BigDecimal totalDebe) {
        this.totalDebe = totalDebe;
    }

    public BigDecimal getTotalDebeSecundario() {
        return totalDebeSecundario;
    }

    public void setTotalDebeSecundario(BigDecimal totalDebeSecundario) {
        this.totalDebeSecundario = totalDebeSecundario;
    }

}
