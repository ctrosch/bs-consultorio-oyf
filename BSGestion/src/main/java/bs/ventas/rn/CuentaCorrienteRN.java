/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.rn;

import bs.administracion.rn.ParametrosRN;
import bs.entidad.modelo.EntidadComercial;
import bs.facturacion.modelo.MovimientoFacturacion;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.ventas.dao.CuentaCorrienteDAO;
import bs.ventas.modelo.AplicacionCuentaCorrienteVenta;
import bs.ventas.modelo.ItemCondicionPagoVenta;
import bs.ventas.modelo.ItemHistoricoCuentaCorriente;
import bs.ventas.modelo.ItemPendienteCuentaCorriente;
import bs.ventas.modelo.MovimientoVenta;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Claudio
 */
@Stateless

public class CuentaCorrienteRN implements Serializable {

    @EJB
    private CuentaCorrienteDAO cuentaCorrienteDAO;

    @EJB
    private MovimientoVentaRN movimientoVentaRN;
    @EJB
    private ParametrosRN parametrosRN;

    /**
     * Generamos los items de aplicación a la cuenta corriente
     *
     * @param m
     * @param creditos
     * @param debitos
     */
    public void generarItemAplicacionCuentaCorriente(MovimientoVenta m,
            List<ItemPendienteCuentaCorriente> creditos,
            List<ItemPendienteCuentaCorriente> debitos) {

        if (creditos != null) {
            for (ItemPendienteCuentaCorriente ic : creditos) {

                if (ic.isSeleccionado()) {

//                    ItemTotalVenta itv = movimientoVentaRN.nuevoItemTotal(m);
                    MovimientoVenta mAplicacion = movimientoVentaRN.getMovimiento(ic.getId());
//                    itv.setMovimientoAplicacion(mAplicacion);

                    BigDecimal importe = ic.getImporteAplicar();

//                    itv.setImporte(importe);
//                    itv.setImporteSecuntario(importe.divide(m.getCotizacion(),2,RoundingMode.HALF_UP));
//                    itv.setImporteHaber(importe);
//                    itv.setImporteHaberSecundario(importe.divide(m.getCotizacion(),2,RoundingMode.HALF_UP));
                    AplicacionCuentaCorrienteVenta icc = new AplicacionCuentaCorrienteVenta();

                    icc.setMovimiento(m);
                    icc.setMovimientoAplicacion(mAplicacion);
                    icc.setCuota(ic.getCuotas());
                    icc.setCliente(m.getCliente());
                    icc.setNroSubcuenta(m.getClienteCuentaCorriente().getNrocta());
                    icc.setCodigoImputacion("CC");
                    icc.setMonedaSecundaria(m.getMonedaSecundaria());
                    icc.setMonedaRegistracion(m.getMonedaRegistracion());
                    icc.setCotizacion(m.getCotizacion());

                    icc.setImporte(importe);
                    icc.setImporteSecundario(importe.divide(m.getCotizacion(), 4, RoundingMode.HALF_UP));

                    icc.setFechaAplicacion(m.getFechaMovimiento());
                    icc.setFechaVencimiento(ic.getFechaVencimiento());

                    m.getItemCuentaCorriente().add(icc);
                }
            }
        }

        if (debitos != null) {
            for (ItemPendienteCuentaCorriente id : debitos) {
                if (id.isSeleccionado()) {

//                    ItemTotalVenta itv = movimientoVentaRN.nuevoItemTotal(m);
                    MovimientoVenta mAplicacion = movimientoVentaRN.getMovimiento(id.getId());
//                    itv.setMovimientoAplicacion(mAplicacion);

                    BigDecimal importe = id.getImporteAplicar().negate();

//                    System.err.println("Importe aplicar: " + importe);
//                    itv.setImporte(importe);
//                    itv.setImporteSecuntario(importe.divide(m.getCotizacion(),2,RoundingMode.HALF_UP));
//                    itv.setImporteDebe(importe);
//                    itv.setImporteDebeSecundario(importe.divide(m.getCotizacion(),2,RoundingMode.HALF_UP));
                    AplicacionCuentaCorrienteVenta icc = new AplicacionCuentaCorrienteVenta();
                    icc.setMovimiento(m);
                    icc.setMovimientoAplicacion(mAplicacion);
                    icc.setCuota(id.getCuotas());
                    icc.setCliente(m.getCliente());
                    icc.setNroSubcuenta(m.getClienteCuentaCorriente().getNrocta());
                    icc.setCodigoImputacion("CC");
                    icc.setMonedaSecundaria(m.getMonedaSecundaria());
                    icc.setMonedaRegistracion(m.getMonedaRegistracion());
                    icc.setCotizacion(m.getCotizacion());

                    icc.setImporte(importe);
                    icc.setImporteSecundario(importe.divide(m.getCotizacion(), 4, RoundingMode.HALF_UP));

                    icc.setFechaAplicacion(m.getFechaMovimiento());
                    icc.setFechaVencimiento(id.getFechaVencimiento());

                    m.getItemCuentaCorriente().add(icc);
                }
            }
        }
    }

    public void generarItemCuentaCorriente(MovimientoVenta m) throws ExcepcionGeneralSistema {

        if (!m.getComprobante().getTipoImputacion().equals("CC")) {
            return;
        }

        if (m.getCondicionDePago() == null) {
            throw new ExcepcionGeneralSistema("La condición de pago no puede estar vacía");
        }

        if (m.getItemTotal() == null || m.getItemTotal().isEmpty()) {
            throw new ExcepcionGeneralSistema("Item total no fue generado, nada para agregar a la cuenta corriente");
        }

        m.getItemCuentaCorriente().clear();
        BigDecimal importeTotal = m.getItemTotal().get(0).getImporte();
        Calendar fechaVencimiento = Calendar.getInstance();

        switch (m.getCondicionDePago().getFechaOrigenCalculo()) {
            case FE:
                fechaVencimiento.setTime(m.getFechaMovimiento());
                break;
            case FV:
                fechaVencimiento.setTime(m.getFechaVencimiento());
                break;
            default:
                fechaVencimiento.setTime(m.getFechaMovimiento());
                break;
        }

        switch (m.getCondicionDePago().getTipoCalculo()) {
            case PER:

                for (ItemCondicionPagoVenta icp : m.getCondicionDePago().getCuotas()) {

                    BigDecimal impoteCuota = importeTotal.multiply(new BigDecimal(icp.getPorcentaje())).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);

                    fechaVencimiento.add(Calendar.DAY_OF_YEAR, icp.getDiasDePago());

                    AplicacionCuentaCorrienteVenta icc = new AplicacionCuentaCorrienteVenta();
                    icc.setMovimiento(m);
                    icc.setMovimientoAplicacion(m);
                    icc.setCuota(icp.getCuotas());
                    icc.setCliente(m.getCliente());
                    icc.setNroSubcuenta(m.getClienteCuentaCorriente().getNrocta());
                    icc.setCodigoImputacion("CC");
                    icc.setMonedaSecundaria(m.getMonedaSecundaria());
                    icc.setMonedaRegistracion(m.getMonedaRegistracion());
                    icc.setCotizacion(m.getCotizacion());

                    if (m.getComprobante().getSignoAplicacionCuentaCorriente().equals("+")) {

                        icc.setImporte(impoteCuota);
                        icc.setImporteSecundario(impoteCuota.divide(m.getCotizacion(), 4, RoundingMode.HALF_UP));

                    } else {

                        icc.setImporte(importeTotal.negate());
                        icc.setImporteSecundario((impoteCuota.divide(m.getCotizacion(), 4, RoundingMode.HALF_UP)).negate());
                    }

                    icc.setFechaAplicacion(m.getFechaMovimiento());
                    icc.setFechaVencimiento(fechaVencimiento.getTime());

                    m.getItemCuentaCorriente().add(icc);
                }
                break;

            case SEC:

                BigDecimal impoteCuota = importeTotal.divide(BigDecimal.valueOf(m.getCondicionDePago().getCantidadCuotas()), 2, RoundingMode.HALF_UP);

                BigDecimal acumulado = BigDecimal.ZERO;

                for (int nroCuota = 1; nroCuota <= m.getCondicionDePago().getCantidadCuotas(); nroCuota++) {

                    if (m.getCondicionDePago().getTomaFechaOrigenComoPrimerVencimiento().equals("N") || nroCuota > 1) {

                        switch (m.getCondicionDePago().getFrecuencia()) {
                            case D: //Diaria
                                fechaVencimiento.add(Calendar.DAY_OF_YEAR, 1);
                                break;

                            case S:  //Semanal
                                fechaVencimiento.add(Calendar.WEEK_OF_YEAR, 1);
                                break;

                            case M: // Mensual
                                fechaVencimiento.add(Calendar.MONTH, 1);
                                break;

                            case A: //Anual
                                fechaVencimiento.add(Calendar.YEAR, 1);
                                break;

                            default:
                                fechaVencimiento.add(Calendar.MONTH, 1);
                                break;
                        }
                    }

                    if (m.getCondicionDePago().getDiaSegunFrecuencia() != null && m.getCondicionDePago().getDiaSegunFrecuencia() > 0) {

                        switch (m.getCondicionDePago().getFrecuencia()) {

                            case S:  //Semanal
                                fechaVencimiento.set(Calendar.DAY_OF_WEEK, m.getCondicionDePago().getDiaSegunFrecuencia());
                                break;
                            case M: // Mensual
                                fechaVencimiento.set(Calendar.DAY_OF_MONTH, m.getCondicionDePago().getDiaSegunFrecuencia());
                                break;
                            case A: //Anual
                                fechaVencimiento.set(Calendar.DAY_OF_YEAR, m.getCondicionDePago().getDiaSegunFrecuencia());
                                break;
                            default:
                                break;
                        }
                    }

                    AplicacionCuentaCorrienteVenta icc = new AplicacionCuentaCorrienteVenta();
                    icc.setMovimiento(m);
                    icc.setMovimientoAplicacion(m);
                    icc.setCuota(nroCuota);
                    icc.setCliente(m.getCliente());
                    icc.setNroSubcuenta(m.getClienteCuentaCorriente().getNrocta());
                    icc.setCodigoImputacion("CC");
                    icc.setMonedaSecundaria(m.getMonedaSecundaria());
                    icc.setMonedaRegistracion(m.getMonedaRegistracion());
                    icc.setCotizacion(m.getCotizacion());

                    if (m.getComprobante().getSignoAplicacionCuentaCorriente().equals("+")) {

                        icc.setImporte(impoteCuota);
                        icc.setImporteSecundario(impoteCuota.divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));

                    } else {

                        icc.setImporte(impoteCuota.negate());
                        icc.setImporteSecundario((impoteCuota.negate().divide(m.getCotizacion(), 2, RoundingMode.HALF_UP)).negate());
                    }

                    icc.setFechaAplicacion(m.getFechaMovimiento());
                    icc.setFechaVencimiento(fechaVencimiento.getTime());

                    acumulado = acumulado.add(icc.getImporte());

                    //Realizamos ajuste en ultima cuota
                    if (nroCuota == m.getCondicionDePago().getCantidadCuotas()) {

                        if (m.getComprobante().getSignoAplicacionCuentaCorriente().equals("+")) {

                            if (acumulado.compareTo(importeTotal) < 0) {
                                icc.setImporte(icc.getImporte().add(importeTotal.add(acumulado.negate())));
                            }
                        } else {
                            if (acumulado.negate().compareTo(importeTotal) < 0) {
                                icc.setImporte(icc.getImporte().add(importeTotal.negate().add(acumulado.negate())));
                            }
                        }
                    }

                    m.getItemCuentaCorriente().add(icc);
                }

                break;
            default:
                break;
        }

    }

    // 3/6/2019 - Médoto temporal, habría que hacer que directamente calcule todo en el comprobante de venta y permita modificar los datos.
    // por ahora zafamos con esto.
    public void generarItemCuentaCorriente(MovimientoFacturacion m) throws ExcepcionGeneralSistema {

        if (m.getId() != null) {
            throw new ExcepcionGeneralSistema("No es posible editar los items en cuenta corriente");
        }

        if (m.getCondicionDePago() == null) {
            throw new ExcepcionGeneralSistema("La condición de pago no puede estar vacía");
        }

        if (m.getItemsTotalVenta() == null || m.getItemsTotalVenta().isEmpty()) {
            throw new ExcepcionGeneralSistema("Item total no fue generado, nada para agregar a la cuenta corriente");
        }

        m.getItemCuentaCorriente().clear();
        BigDecimal importeTotal = m.getItemsTotalVenta().get(0).getImporte();
        Calendar fechaVencimiento = Calendar.getInstance();

        switch (m.getCondicionDePago().getFechaOrigenCalculo()) {
            case FE:
                fechaVencimiento.setTime(m.getFechaMovimiento());
                break;
            case FV:
                fechaVencimiento.setTime(m.getFechaVencimiento());
                break;
            default:
                fechaVencimiento.setTime(m.getFechaMovimiento());
                break;
        }

        switch (m.getCondicionDePago().getTipoCalculo()) {
            case PER:

                for (ItemCondicionPagoVenta icp : m.getCondicionDePago().getCuotas()) {

                    BigDecimal impoteCuota = importeTotal.multiply(new BigDecimal(icp.getPorcentaje())).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);

                    fechaVencimiento.add(Calendar.DAY_OF_YEAR, icp.getDiasDePago());

                    AplicacionCuentaCorrienteVenta icc = new AplicacionCuentaCorrienteVenta();
                    icc.setCuota(icp.getCuotas());
                    icc.setCliente(m.getCliente());
                    icc.setNroSubcuenta(m.getClienteCuentaCorriente().getNrocta());
                    icc.setCodigoImputacion("CC");
                    icc.setMonedaSecundaria(m.getMonedaSecundaria());
                    icc.setMonedaRegistracion(m.getMonedaRegistracion());
                    icc.setCotizacion(m.getCotizacion());

                    if (m.getComprobante().getSignoAplicacionCuentaCorriente().equals("+")) {

                        icc.setImporte(impoteCuota);
                        icc.setImporteSecundario(impoteCuota.divide(m.getCotizacion(), 4, RoundingMode.HALF_UP));

                    } else {

                        icc.setImporte(importeTotal.negate());
                        icc.setImporteSecundario((impoteCuota.divide(m.getCotizacion(), 4, RoundingMode.HALF_UP)).negate());
                    }

                    icc.setFechaAplicacion(m.getFechaMovimiento());
                    icc.setFechaVencimiento(fechaVencimiento.getTime());

                    m.getItemCuentaCorriente().add(icc);
                }
                break;

            case SEC:

                BigDecimal impoteCuota = importeTotal.divide(BigDecimal.valueOf(m.getCondicionDePago().getCantidadCuotas()), 4, RoundingMode.HALF_UP);
                BigDecimal acumulado = BigDecimal.ZERO;

                for (int nroCuota = 1; nroCuota <= m.getCondicionDePago().getCantidadCuotas(); nroCuota++) {

                    if (m.getCondicionDePago().getTomaFechaOrigenComoPrimerVencimiento().equals("N") || nroCuota > 1) {

                        switch (m.getCondicionDePago().getFrecuencia()) {
                            case D: //Diaria
                                fechaVencimiento.add(Calendar.DAY_OF_YEAR, 1);
                                break;

                            case S:  //Semanal
                                fechaVencimiento.add(Calendar.WEEK_OF_YEAR, 1);
                                break;

                            case M: // Mensual
                                fechaVencimiento.add(Calendar.MONTH, 1);
                                break;

                            case A: //Anual
                                fechaVencimiento.add(Calendar.YEAR, 1);
                                break;

                            default:
                                fechaVencimiento.add(Calendar.MONTH, 1);
                                break;
                        }
                    }

                    if (m.getCondicionDePago().getDiaSegunFrecuencia() != null && m.getCondicionDePago().getDiaSegunFrecuencia() > 0) {

                        switch (m.getCondicionDePago().getFrecuencia()) {

                            case S:  //Semanal
                                fechaVencimiento.set(Calendar.DAY_OF_WEEK, m.getCondicionDePago().getDiaSegunFrecuencia());
                                break;
                            case M: // Mensual
                                fechaVencimiento.set(Calendar.DAY_OF_MONTH, m.getCondicionDePago().getDiaSegunFrecuencia());
                                break;
                            case A: //Anual
                                fechaVencimiento.set(Calendar.DAY_OF_YEAR, m.getCondicionDePago().getDiaSegunFrecuencia());
                                break;
                            default:
                                break;
                        }
                    }

                    AplicacionCuentaCorrienteVenta icc = new AplicacionCuentaCorrienteVenta();
                    icc.setCuota(nroCuota);
                    icc.setCliente(m.getCliente());
                    icc.setNroSubcuenta(m.getClienteCuentaCorriente().getNrocta());
                    icc.setCodigoImputacion("CC");
                    icc.setMonedaSecundaria(m.getMonedaSecundaria());
                    icc.setMonedaRegistracion(m.getMonedaRegistracion());
                    icc.setCotizacion(m.getCotizacion());

                    if (m.getComprobante().getSignoAplicacionCuentaCorriente().equals("+")) {

                        icc.setImporte(impoteCuota);
                        icc.setImporteSecundario(impoteCuota.divide(m.getCotizacion(), 4, RoundingMode.HALF_UP));

                    } else {

                        icc.setImporte(importeTotal.negate());
                        icc.setImporteSecundario((impoteCuota.divide(m.getCotizacion(), 4, RoundingMode.HALF_UP)).negate());
                    }

                    icc.setFechaAplicacion(m.getFechaMovimiento());
                    icc.setFechaVencimiento(fechaVencimiento.getTime());

                    acumulado = acumulado.add(icc.getImporte());

                    //Realizamos ajuste en ultima cuota
                    if (nroCuota == m.getCondicionDePago().getCantidadCuotas()) {

                        if (m.getComprobante().getSignoAplicacionCuentaCorriente().equals("+")) {

                            if (acumulado.compareTo(importeTotal) < 0) {
                                icc.setImporte(icc.getImporte().add(importeTotal.add(acumulado.negate())));
                            }
                        } else {
                            if (acumulado.negate().compareTo(importeTotal) < 0) {
                                icc.setImporte(icc.getImporte().add(importeTotal.negate().add(acumulado.negate())));
                            }
                        }
                    }

                    m.getItemCuentaCorriente().add(icc);
                }

                break;
            default:
                break;
        }

    }

    /**
     * Se utiliza para generar cuenta corriente cuando el recibo o comprobante
     * qeu se ingresa no aplica ningún movimiento.
     *
     * @param m Movimiento de venta
     * @throws ExcepcionGeneralSistema
     */
    public void generarItemCuentaCorrienteAnticipo(MovimientoVenta m) throws ExcepcionGeneralSistema {

        if (m.getItemTotal() == null || m.getItemTotal().isEmpty()) {
            throw new ExcepcionGeneralSistema("Item total no fue generado, nada para agregar a la cuenta corriente");
        }
        m.getItemCuentaCorriente().clear();

        BigDecimal importeTotal = m.getItemTotal().get(0).getImporte();
        AplicacionCuentaCorrienteVenta icc = new AplicacionCuentaCorrienteVenta();
        icc.setMovimiento(m);
        icc.setMovimientoAplicacion(m);
        icc.setCuota(1);
        icc.setCliente(m.getCliente());
        icc.setNroSubcuenta(m.getClienteCuentaCorriente().getNrocta());
        icc.setCodigoImputacion("CC");
        icc.setMonedaSecundaria(m.getMonedaSecundaria());
        icc.setMonedaRegistracion(m.getMonedaRegistracion());
        icc.setCotizacion(m.getCotizacion());

        if (m.getComprobante().getSignoAplicacionCuentaCorriente().equals("+")) {

            icc.setImporte(importeTotal);
            icc.setImporteSecundario(importeTotal.divide(m.getCotizacion(), 4, RoundingMode.HALF_UP));

        } else {

            icc.setImporte(importeTotal.negate());
            icc.setImporteSecundario((importeTotal.divide(m.getCotizacion(), 4, RoundingMode.HALF_UP)).negate());
        }

        icc.setFechaAplicacion(m.getFechaMovimiento());
        icc.setFechaVencimiento(m.getFechaVencimiento());

        m.getItemCuentaCorriente().add(icc);

    }

    public void generarItemCuentaCorrienteRecibo(MovimientoVenta m, List<ItemPendienteCuentaCorriente> debitos) throws ExcepcionGeneralSistema {

        if (m.getItemTotal() == null || m.getItemTotal().isEmpty()) {
            throw new ExcepcionGeneralSistema("Item total no fue generado, nada para agregar a la cuenta corriente");
        }

        if (debitos == null || debitos.isEmpty()) {
            throw new ExcepcionGeneralSistema("No existen débitos para aplicar en cuenta corriente");
        }
        m.getItemCuentaCorriente().clear();
        BigDecimal importeTotal = m.getItemTotal().get(0).getImporte();
        BigDecimal importeAplicar = BigDecimal.ZERO;

        for (ItemPendienteCuentaCorriente ipcc : debitos) {
            importeAplicar = importeAplicar.add(ipcc.getImporteAplicar());
        }

        if (importeAplicar.compareTo(importeTotal) != 0) {
            throw new ExcepcionGeneralSistema("El importe total del recibo no coincide con el importe total a aplicar");
        }

        generarItemAplicacionCuentaCorriente(m, null, debitos);
    }

    /**
     *
     * @param saldos lista de saldos de cuenta corriente
     * @return La suma de los saldos pendientes
     */
    public BigDecimal sumarSaldos(List<ItemPendienteCuentaCorriente> saldos, String monedaRegistracion) {

        if (saldos == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal importeAplicar = BigDecimal.ZERO;
        BigDecimal importeAplicarSecundario = BigDecimal.ZERO;

        for (ItemPendienteCuentaCorriente ic : saldos) {

            if (ic.isSeleccionado()) {
                importeAplicar = importeAplicar.add(ic.getImporteAplicar().setScale(4, RoundingMode.HALF_UP));
                importeAplicarSecundario = importeAplicarSecundario.add(ic.getImporteAplicarSecundario().setScale(4, RoundingMode.HALF_UP));
            } else {
                ic.setImporteAplicar(BigDecimal.ZERO);
                ic.setImporteAplicarSecundario(BigDecimal.ZERO);
            }
        }

        if (monedaRegistracion.equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
            return importeAplicar;
        }

        if (monedaRegistracion.equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
            return importeAplicarSecundario;
        } else {
            return importeAplicar;
        }
    }

    public List<ItemHistoricoCuentaCorriente> getHistoricoMovimientos(String nroCuenta, String monReg, String comprobanteInterno, Date fDesde, Date fHasta) {

        return cuentaCorrienteDAO.getHistoricoMovimientos(nroCuenta, monReg, comprobanteInterno, fDesde, fHasta);
    }

    public List<ItemPendienteCuentaCorriente> getDebitosPendientes(String nroCuenta, String monReg, String comprobanteInterno) {

        return cuentaCorrienteDAO.getPendientesByNroCuenta(nroCuenta, monReg, comprobanteInterno, "D");
    }

    public List<EntidadComercial> getClientesConSaldos(Map<String, String> filtro, String monReg, String comprobanteInterno) {

        return cuentaCorrienteDAO.getClientesConSaldos(filtro, monReg, comprobanteInterno);
    }

    public List<ItemPendienteCuentaCorriente> getCreditosPendientes(String nroCuenta, String monReg, String incEst) {

        return cuentaCorrienteDAO.getPendientesByNroCuenta(nroCuenta, monReg, incEst, "H");
    }

    public BigDecimal getSaldoActual(String nrocta, String monedaRegistracion, String comprobanteInterno) {

        BigDecimal s = cuentaCorrienteDAO.getSaldoAFecha(nrocta, monedaRegistracion, comprobanteInterno, new Date());
        if (s == null) {
            s = BigDecimal.ZERO;
        }
        return s;
    }

    public BigDecimal getSaldoSecundarioActual(String nrocta, String monedaRegistracion, String comprobanteInterno) {

        BigDecimal s = cuentaCorrienteDAO.getSaldoSecundarioAFecha(nrocta, monedaRegistracion, comprobanteInterno, new Date());
        if (s == null) {
            s = BigDecimal.ZERO;
        }
        return s;
    }

    public ItemHistoricoCuentaCorriente getSaldoAnterior(String nrocta, String monedaRegistracion, String comprobanteInterno, Date fDesde) {

        ItemHistoricoCuentaCorriente itemSA = new ItemHistoricoCuentaCorriente();
        itemSA.setCodfor("SA");
        itemSA.setComprobante("Saldo Anterior");
        itemSA.setNrocta(nrocta);
        itemSA.setFchmov(fDesde);
        itemSA.setSucurs("0000");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fDesde); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, -1);  // numero de días a añadir o restar

        BigDecimal saldo = cuentaCorrienteDAO.getSaldoAFecha(nrocta, monedaRegistracion, comprobanteInterno, calendar.getTime());
        BigDecimal saldoSecundario = cuentaCorrienteDAO.getSaldoSecundarioAFecha(nrocta, monedaRegistracion, comprobanteInterno, calendar.getTime());

        if (saldo == null) {
            saldo = BigDecimal.ZERO;
        }
        if (saldoSecundario == null) {
            saldoSecundario = BigDecimal.ZERO;
        }

        itemSA.setSaldo(saldo);
        itemSA.setSaldoSecundario(saldoSecundario);

        return itemSA;
    }

    public String conDeuda(String nroCuenta, String monReg, String comprobanteInterno) {

        return cuentaCorrienteDAO.conDeuda(nroCuenta, monReg, comprobanteInterno);

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public void invertirImporteCreditos(List<ItemPendienteCuentaCorriente> creditos) {

        if (creditos == null) {
            return;
        }

        for (ItemPendienteCuentaCorriente c : creditos) {

            c.setPendiente(c.getPendiente().negate());
            c.setPendienteSecundario(c.getPendienteSecundario().negate());
        }
    }

}
