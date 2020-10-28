/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.web;

import bs.global.util.JsfUtil;
import bs.global.util.Numeros;
import bs.prestamo.modelo.ItemPendienteCuentaCorrientePrestamo;
import bs.prestamo.modelo.Prestamo;
import bs.prestamo.rn.CuentaCorrientePrestamoRN;
import bs.prestamo.rn.PrestamoRN;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.tesoreria.modelo.MovimientoTesoreria;
import java.math.BigDecimal;
import java.util.ArrayList;
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
public class ReciboPrestamoBean extends MovimientoTesoreriaBean {

    @EJB
    private CuentaCorrientePrestamoRN cuentaCorrienteRN;
    @EJB
    protected PrestamoRN prestamoRN;

    private List<ItemPendienteCuentaCorrientePrestamo> debitos;
    private double totalDebe;

    /**
     * Creates a new instance of CajaBean
     */
    public ReciboPrestamoBean() {

    }

    @Override
    public void iniciarVariables() {

        if (!beanIniciado) {
            totalDebe = 0;
            super.iniciarVariables();
        }
    }

    @Override
    public void guardar(boolean nuevo) {

        try {
            totalDebe = cuentaCorrienteRN.sumarSaldos(debitos, m.getMonedaRegistracion().getCodigo());

            if (!puedoGuardar()) {
                return;
            }

            super.guardar(nuevo);

            if (m.getId() != null) {
                totalDebe = 0;
                debitos = new ArrayList<ItemPendienteCuentaCorrientePrestamo>();
            }

            prestamoRN.validarEstado(m.getPrestamo());

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("error al guardar" + ex);
            Logger.getLogger(ReciboPrestamoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean puedoGuardar() {

        boolean todoOK = true;

        if (!super.puedoGuardar()) {
            todoOK = false;
        }

        if (m.getComprobantePrestamo() == null) {
            JsfUtil.addErrorMessage("El comprobante de préstamo no puede estar vacío");
            todoOK = false;
        }

        if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {

            if (!m.isEsAnticipo() && totalDebe != m.getImporteTotalDebe().doubleValue()) {
                JsfUtil.addErrorMessage("El importe a aplicar es diferente a la suma de los conceptos ingresados");
                todoOK = false;
            }
        }

        if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
            if (!m.isEsAnticipo() && totalDebe != m.getImporteTotalDebeSecundario().doubleValue()) {
                JsfUtil.addErrorMessage("El importe a aplicar es diferente a la suma de los conceptos ingresados");
                todoOK = false;
            }
        }

        return todoOK;
    }

    @Override
    public void revertirMovimiento(MovimientoTesoreria mSel) {

        try {
            super.revertirMovimiento(mSel);
            prestamoRN.validarEstado(mSel.getPrestamo());
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible revertir el movimiento " + ex);
            Logger.getLogger(ReciboPrestamoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void modificarCotizacion() {

    }

    public void procesarPrestamo() {

        if (m != null && m.getPrestamo() != null) {

            try {
                tesoreriaRN.asignarPrestamo(m);
                debitos = cuentaCorrienteRN.getDebitosPendientes(m.getPrestamo(), m.getMonedaRegistracion().getCodigo(), m.getFechaMovimiento(), true, true);
                m.setDebitosPrestamo(debitos);

            } catch (Exception ex) {
                JsfUtil.addErrorMessage("No es posible asignar destinatario " + ex);
            }
        }
    }

    public void procesarDebito() {

    }

    public List<Prestamo> completePrestamo(String query) {
        try {
            filtro.clear();
            filtro.put("estado.codigo = ", "'C'");
            filtro.put("sucursal.codigo IN ", "(" + usuarioSessionBean.getStringInSucursal() + ")");

            return prestamoRN.getListaByBusqueda(query, filtro, 10);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Prestamo>();
        }
    }

    public void modificaDebito(ItemPendienteCuentaCorrientePrestamo i) {

        prestamoRN.calcularMoraYDescuento(m.getPrestamo(), m.getDebitosPrestamo(), m.getFechaMovimiento(), true, true);

        if (i.isSeleccionado()) {

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {

                if (i.getImporteAplicar() > i.getPendiente()) {
                    i.setImporteAplicar(i.getPendiente());
                    JsfUtil.addErrorMessage("El importe a aplicar es mayor al pendiente");
                }
            }

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {

                if (i.getImporteAplicarSecundario() > i.getPendienteSecundario()) {
                    i.setImporteAplicarSecundario(i.getPendienteSecundario());
                    JsfUtil.addErrorMessage("El importe a aplicar es mayor al pendiente");
                }
                i.setImporteAplicar(i.getImporteAplicarSecundario() * m.getCotizacion().doubleValue());
            }

        } else {
            i.setImporteAplicar(0);
            i.setImporteAplicarSecundario(0);
        }

        cuentaCorrienteRN.marcarDebito(i, m.getCotizacion().doubleValue());
        totalDebe = cuentaCorrienteRN.sumarSaldos(debitos, m.getMonedaRegistracion().getCodigo());
    }

    public void marcarDebito(ItemPendienteCuentaCorrientePrestamo cuota) {

        prestamoRN.calcularMoraYDescuento(m.getPrestamo(), m.getDebitosPrestamo(), m.getFechaMovimiento(), true, true);
        cuentaCorrienteRN.marcarDebito(cuota, m.getCotizacion().doubleValue());
        totalDebe = cuentaCorrienteRN.sumarSaldos(debitos, m.getMonedaRegistracion().getCodigo());
    }

    public void asignarTotal(ItemMovimientoTesoreria item) {

        if (item.getDebeHaber().equals("D")) {
            m.getItemsDebe().forEach(i -> i.setImporte(BigDecimal.ZERO));
        } else {
            m.getItemsHaber().forEach(i -> i.setImporte(BigDecimal.ZERO));
        }

        item.setImporte(Numeros.toBigDecimal(totalDebe));
        calcularImportes();
    }

    //--------------------------------------------------------------------------
    public List<ItemPendienteCuentaCorrientePrestamo> getDebitos() {
        return debitos;
    }

    public void setDebitos(List<ItemPendienteCuentaCorrientePrestamo> debitos) {
        this.debitos = debitos;
    }

    public double getTotalDebe() {
        return totalDebe;
    }

    public void setTotalDebe(double totalDebe) {
        this.totalDebe = totalDebe;
    }

}
