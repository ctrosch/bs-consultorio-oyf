/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.rn;

import bs.administracion.rn.ParametrosRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.Numeros;
import bs.prestamo.dao.CuentaCorrientePrestamoDAO;
import bs.prestamo.modelo.AplicacionCuentaCorrientePrestamo;
import bs.prestamo.modelo.ItemHistoricoCuentaCorrientePrestamo;
import bs.prestamo.modelo.ItemPendienteCuentaCorrientePrestamo;
import bs.prestamo.modelo.ItemPrestamo;
import bs.prestamo.modelo.MovimientoPrestamo;
import bs.prestamo.modelo.Prestamo;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Claudio
 */
@Stateless

public class CuentaCorrientePrestamoRN implements Serializable {

    @EJB
    private CuentaCorrientePrestamoDAO cuentaCorrienteDAO;

    @EJB
    private MovimientoPrestamoRN movimientoPrestamoRN;
    @EJB
    private ParametrosRN parametrosRN;
    @EJB
    private PrestamoRN prestamoRN;

    public AplicacionCuentaCorrientePrestamo nuevoItemAplicacion(MovimientoPrestamo m, MovimientoPrestamo mAplicacion, ItemPendienteCuentaCorrientePrestamo id) {

        AplicacionCuentaCorrientePrestamo icc = new AplicacionCuentaCorrientePrestamo();
        icc.setPrestamo(m.getPrestamo());

        icc.setMovimiento(m);
        icc.setMovimientoAplicacion(mAplicacion);

        icc.setDestinatario(m.getDestinatario());
        icc.setCodigoImputacion("CC");
        icc.setMonedaSecundaria(m.getMonedaSecundaria());
        icc.setMonedaRegistracion(m.getMonedaRegistracion());
        icc.setCotizacion(m.getCotizacion());

        icc.setPrestamo(id.getItemPrestamo().getPrestamo());
        icc.setItemPrestamo(id.getItemPrestamo());
        icc.setCuota(id.getCuotas());

        icc.setFechaAplicacion(m.getFechaMovimiento());
        icc.setFechaVencimiento(id.getFechaVencimiento());

        m.getItemCuentaCorriente().add(icc);

        return icc;

    }

    /**
     * Se utiliza para generar items de cuenta corriente tomando los importes
     * pendientes de cada préstamo.
     *
     * @param m Movimiento de préstamo
     * @throws ExcepcionGeneralSistema
     */
    public void generarItemCuentaCorriente(MovimientoPrestamo m) throws ExcepcionGeneralSistema {

        if (!m.getComprobante().getTipoImputacion().equals("CC")) {
            return;
        }

        if (m.getPrestamo() == null) {
            throw new ExcepcionGeneralSistema("El préstamo no puede estar vacío");
        }

        m.getItemCuentaCorriente().clear();

        for (ItemPrestamo item : m.getPrestamo().getItemsPrestamo()) {

            AplicacionCuentaCorrientePrestamo icc = new AplicacionCuentaCorrientePrestamo();

            icc.setMovimiento(m);
            icc.setPrestamo(m.getPrestamo());
            icc.setItemPrestamo(item);
            icc.setMovimientoAplicacion(m);
            icc.setCuota(item.getCuota());
            icc.setDestinatario(m.getDestinatario());
            icc.setCodigoImputacion("CC");
            icc.setMonedaSecundaria(m.getMonedaSecundaria());
            icc.setMonedaRegistracion(m.getMonedaRegistracion());
            icc.setCotizacion(m.getCotizacion());
            icc.setObservaciones("Cuota " + item.getCuota());
            icc.setFechaAplicacion(m.getFechaMovimiento());
            icc.setFechaVencimiento(item.getFechaVencimiento());

            if (item.getCapital() - item.getCapitalCancelado() > 0) {
                icc.setCapital(item.getCapital() - item.getCapitalCancelado());
            }

            if (item.getInteres() - item.getInteresCancelado() > 0) {

                icc.setInteres(item.getInteres() - item.getInteresCancelado());
            }

            if (item.getInteresMora() - item.getInteresMoraCancelado() > 0) {

                icc.setInteresMora(item.getInteresMora() - item.getInteresMoraCancelado());
            }

            if (item.getDescuentoInteres() - item.getDescuentoInteresCancelado() > 0) {

                icc.setDescuentoInteres(item.getDescuentoInteres() - item.getDescuentoInteresCancelado());
            }

            if (item.getGastosOtorgamiento() - item.getGastosOtorgamientoCancelado() > 0) {

                icc.setGastosOtorgamiento(item.getGastosOtorgamiento() - item.getGastosOtorgamientoCancelado());
            }

            if (item.getImporteMicroseguros() - item.getImporteMicroseguroCancelado() > 0) {

                icc.setImporteMicroseguros(item.getImporteMicroseguros() - item.getImporteMicroseguroCancelado());
            }

            if (item.getTotalCuota() > 0) {
                icc.setImporte(item.getTotalCuota());
            }

            if (m.getComprobante().getSignoAplicacionCuentaCorriente().equals("-")) {

                icc.setImporte(icc.getImporte() * -1);
                icc.setCapital(icc.getCapital() * -1);
                icc.setInteres(icc.getInteres() * -1);
                icc.setInteresMora(icc.getInteresMora() * -1);
                icc.setDescuentoInteres(icc.getDescuentoInteres() * -1);
                icc.setGastosOtorgamiento(icc.getGastosOtorgamiento() * -1);
                icc.setImporteMicroseguros(icc.getImporteMicroseguros() * -1);
                icc.setImporteSecundario((icc.getImporte() / m.getCotizacion()) * -1);
            }

            m.getItemCuentaCorriente().add(icc);
        }
    }

    public void generarItemCuentaCorrienteRecibo(MovimientoPrestamo m, List<ItemPendienteCuentaCorrientePrestamo> debitos) throws ExcepcionGeneralSistema {

        if (m.getItemTotal() == null || m.getItemTotal().isEmpty()) {
            throw new ExcepcionGeneralSistema("Item total no fue generado, nada para agregar a la cuenta corriente");
        }

        if (debitos == null || debitos.isEmpty()) {
            throw new ExcepcionGeneralSistema("No existen débitos para aplicar en cuenta corriente");
        }
        m.getItemCuentaCorriente().clear();
        double importeTotal = m.getItemTotal().get(0).getImporte();
        double importeAplicar = 0;

        for (ItemPendienteCuentaCorrientePrestamo ipcc : debitos) {
            importeAplicar = importeAplicar + ipcc.getImporteAplicar();
        }

        if (Numeros.toBigDecimal(importeAplicar).compareTo(Numeros.toBigDecimal(importeTotal)) != 0) {
            throw new ExcepcionGeneralSistema("El importe total del recibo (" + importeTotal + ") no coincide con el importe total a aplicar (" + importeAplicar + ")");
        }

        generarItemAplicacionCuentaCorriente(m, null, debitos);
    }

    /**
     * Generamos los items de aplicación a la cuenta corriente desde pendiemtes
     *
     * @param m
     * @param creditos
     * @param debitos
     */
    public void generarItemAplicacionCuentaCorriente(MovimientoPrestamo m,
            List<ItemPendienteCuentaCorrientePrestamo> creditos,
            List<ItemPendienteCuentaCorrientePrestamo> debitos) {

        if (creditos != null) {
            for (ItemPendienteCuentaCorrientePrestamo ic : creditos) {

                if (ic.isSeleccionado()) {

                    MovimientoPrestamo mAplicacion = movimientoPrestamoRN.getMovimiento(ic.getId());
                    AplicacionCuentaCorrientePrestamo icc = null;

                    if (ic.getCapital() > 0) {
                        icc = nuevoItemAplicacion(m, mAplicacion, ic);
                        icc.setImporte(Numeros.redondear(ic.getCapital()));

                        icc.setCapital(icc.getImporte());
                        icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));
                        icc.setObservaciones("Capital Cuota Nro " + icc.getCuota());
                    }

                    if (ic.getInteres() > 0) {
                        icc = nuevoItemAplicacion(m, mAplicacion, ic);
                        icc.setImporte(Numeros.redondear(ic.getInteres()));

                        icc.setInteres(icc.getInteres());
                        icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));
                        icc.setObservaciones("Interés Cuota Nro " + icc.getCuota());
                    }

                    if (ic.getInteresMora() > 0) {

                        icc = nuevoItemAplicacion(m, mAplicacion, ic);
                        icc.setImporte(Numeros.redondear(ic.getInteresMora()) * -1);

                        icc.setInteresMora(icc.getImporte());
                        icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));
                        icc.setObservaciones("Interés Mora Cuota Nro " + icc.getCuota());

                        icc = nuevoItemAplicacion(m, mAplicacion, ic);
                        icc.setImporte(Numeros.redondear(ic.getInteresMora()));

                        icc.setInteresMora(icc.getImporte());
                        icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));
                        icc.setObservaciones("Interés Mora Cuota Nro " + icc.getCuota());
                    }

                    if (ic.getDescuentoInteres() > 0) {
                        icc = nuevoItemAplicacion(m, mAplicacion, ic);
                        icc.setImporte(Numeros.redondear(ic.getDescuentoInteres()));

                        icc.setDescuentoInteres(icc.getImporte());
                        icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));
                        icc.setObservaciones("Descuento Interés Cuota Nro " + icc.getCuota());
                    }

                    if (ic.getGastosOtorgamiento() > 0) {
                        icc = nuevoItemAplicacion(m, mAplicacion, ic);
                        icc.setImporte(Numeros.redondear(ic.getGastosOtorgamiento()));

                        icc.setGastosOtorgamiento(icc.getImporte());
                        icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));
                        icc.setObservaciones("Gastos otorgamiento Cuota Nro " + icc.getCuota());
                    }

                    if (ic.getImporteMicroseguros() > 0) {
                        icc = nuevoItemAplicacion(m, mAplicacion, ic);
                        icc.setImporte(Numeros.redondear(ic.getImporteMicroseguros()));

                        icc.setImporteMicroseguros(icc.getImporte());
                        icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));
                        icc.setObservaciones("Microseguro Cuota Nro " + icc.getCuota());
                    }
                }
            }
        }

        if (debitos != null) {
            for (ItemPendienteCuentaCorrientePrestamo id : debitos) {

                if (id.isSeleccionado()) {

//                    System.err.println("id " + id);
                    MovimientoPrestamo mAplicacion = movimientoPrestamoRN.getMovimiento(id.getId());
                    AplicacionCuentaCorrientePrestamo icc = null;

                    if (id.getCapital() > 0) {
                        icc = nuevoItemAplicacion(m, mAplicacion, id);
                        icc.setImporte(Numeros.redondear(id.getCapital() * -1));

                        icc.setCapital(icc.getImporte());
                        icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));
                        icc.setObservaciones("Capital Cuota Nro " + icc.getCuota());
                    }

                    if (id.getInteres() > 0) {
                        icc = nuevoItemAplicacion(m, mAplicacion, id);
                        icc.setImporte(id.getInteres() * -1);

                        icc.setInteres(icc.getImporte());
                        icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));
                        icc.setObservaciones("Interés Cuota Nro " + icc.getCuota());
                    }

                    if (id.getInteresMora() > 0) {

                        if (!id.isLiquidacionInteresMoraGenerada()) {

                            icc = nuevoItemAplicacion(m, mAplicacion, id);
                            icc.setImporte(Numeros.redondear(id.getInteresMora()));

                            icc.setInteresMora(icc.getImporte());
                            icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));
                            icc.setObservaciones("Interés Mora Cuota Nro " + icc.getCuota());
                        }

                        if (!m.getComprobante().getTipoComprobante().equals("RD")) {

                            icc = nuevoItemAplicacion(m, mAplicacion, id);
                            icc.setImporte(Numeros.redondear(id.getInteresMora() * -1));

                            icc.setInteresMora(icc.getImporte());
                            icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));
                            icc.setObservaciones("Interés Mora Cuota Nro " + icc.getCuota());
                        }
                    }

                    if (id.getDescuentoInteres() > 0) {

                        icc = nuevoItemAplicacion(m, mAplicacion, id);
                        icc.setImporte(Numeros.redondear(id.getDescuentoInteres() * -1));

                        icc.setDescuentoInteres(icc.getImporte());
                        icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));
                        icc.setObservaciones("Descuento Interés Cuota Nro " + icc.getCuota());

                        icc = nuevoItemAplicacion(m, mAplicacion, id);
                        icc.setImporte(Numeros.redondear(id.getDescuentoInteres()));

                        icc.setDescuentoInteres(icc.getImporte());
                        icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));
                        icc.setObservaciones("Descuento Interés Cuota Nro " + icc.getCuota());

                    }

                    if (id.getGastosOtorgamiento() > 0) {
                        icc = nuevoItemAplicacion(m, mAplicacion, id);
                        icc.setImporte(Numeros.redondear(id.getGastosOtorgamiento() * -1));

                        icc.setGastosOtorgamiento(icc.getImporte());
                        icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));
                        icc.setObservaciones("Gastos otorgamiento Cuota Nro " + icc.getCuota());
                    }

                    if (id.getImporteMicroseguros() > 0) {
                        icc = nuevoItemAplicacion(m, mAplicacion, id);
                        icc.setImporte(Numeros.redondear(id.getImporteMicroseguros() * -1));

                        icc.setImporteMicroseguros(icc.getImporte());
                        icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));
                        icc.setObservaciones("Microseguro Cuota Nro " + icc.getCuota());
                    }
                }
            }
        }
    }

    public void generarItemReversion(MovimientoPrestamo mr, MovimientoPrestamo m) {

        if (m.getItemCuentaCorriente() != null && !m.getItemCuentaCorriente().isEmpty()) {

            mr.getItemCuentaCorriente().clear();

            for (AplicacionCuentaCorrientePrestamo item : m.getItemCuentaCorriente()) {

                AplicacionCuentaCorrientePrestamo icc = new AplicacionCuentaCorrientePrestamo();

                icc.setMovimiento(mr);
                icc.setPrestamo(mr.getPrestamo());
                icc.setItemPrestamo(item.getItemPrestamo());
                icc.setMovimientoAplicacion(item.getMovimientoAplicacion());
                icc.setCuota(item.getCuota());
                icc.setDestinatario(m.getDestinatario());
                icc.setCodigoImputacion("CC");
                icc.setMonedaSecundaria(m.getMonedaSecundaria());
                icc.setMonedaRegistracion(m.getMonedaRegistracion());
                icc.setCotizacion(m.getCotizacion());
                icc.setObservaciones("Cuota " + item.getCuota());
                icc.setFechaAplicacion(m.getFechaMovimiento());
                icc.setFechaVencimiento(item.getFechaVencimiento());

                icc.setCapital(item.getCapital() * -1);
                icc.setInteres(item.getInteres() * -1);
                icc.setInteresMora(item.getInteresMora() * -1);
                icc.setDescuentoInteres(item.getDescuentoInteres() * -1);
                icc.setGastosOtorgamiento(item.getGastosOtorgamiento() * -1);
                icc.setImporteMicroseguros(item.getImporteMicroseguros() * -1);
                icc.setImporte(item.getImporte() * -1);
                icc.setImporteSecundario(item.getImporteSecundario() * -1);
                mr.getItemCuentaCorriente().add(icc);

            }
        }
    }

    /**
     *
     * @param saldos lista de saldos de cuenta corriente
     * @return La suma de los saldos pendientes
     */
    public double sumarSaldos(List<ItemPendienteCuentaCorrientePrestamo> saldos, String monedaRegistracion) {

        if (saldos == null) {
            return 0;
        }

        double importeAplicar = 0;
        double importeAplicarSecundario = 0;

        for (ItemPendienteCuentaCorrientePrestamo ic : saldos) {

            if (ic.isSeleccionado()) {
                importeAplicar = importeAplicar + ic.getImporteAplicar();
                importeAplicarSecundario = importeAplicarSecundario + ic.getImporteAplicarSecundario();
            } else {
                ic.setImporteAplicar(0);
                ic.setImporteAplicarSecundario(0);
            }
        }

        if (monedaRegistracion.equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
            return Numeros.redondear(importeAplicar);
        }

        if (monedaRegistracion.equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
            return Numeros.redondear(importeAplicarSecundario);
        } else {
            return Numeros.redondear(importeAplicar);
        }
    }

    public List<ItemHistoricoCuentaCorrientePrestamo> getHistoricoMovimientos(String nroCuenta, Date fDesde, Date fHasta, String monReg) {

        return cuentaCorrienteDAO.getHistoricoMovimientos(nroCuenta, monReg, fDesde, fHasta);
    }

    public List<ItemPendienteCuentaCorrientePrestamo> getDebitosPendientes(Prestamo prestamo,
            String monReg,
            Date fechaMovimiento,
            boolean calculaMora,
            boolean calculaDescuento) {

        if (prestamo == null) {
            return null;
        }

//        System.out.println("getDebitosPendientes");
        List<ItemPendienteCuentaCorrientePrestamo> cuotasPendiente = cuentaCorrienteDAO.getPendientesByNroCuenta(prestamo.getId(), monReg, "D");

        //Si la mora viene calculada por una reprogramación y está en la cuenta corriente
        //entonces no la recalculamos.
        for (ItemPendienteCuentaCorrientePrestamo cuota : cuotasPendiente) {

            if (cuota.getInteresMora() > 0) {
//                System.err.println("Cuota con mora " + cuota.getCuotas());
                cuota.setCalculaInteresMora(false);
            }

            if (cuota.getCapital() <= 0 && cuota.getInteresMora() > 0) {
                cuota.setLiquidacionInteresMoraGenerada(true);
            }
        }

        //for (ItemPendienteCuentaCorrientePrestamo cuota : cuotasPendiente) {
        prestamoRN.calcularMoraYDescuento(prestamo, cuotasPendiente, fechaMovimiento, calculaMora, calculaDescuento);
        //}

        return cuotasPendiente;
    }

    public List<ItemPendienteCuentaCorrientePrestamo> getCreditosPendientes(Prestamo prestamo, String monReg) {

        return cuentaCorrienteDAO.getPendientesByNroCuenta(prestamo.getId(), monReg, "H");
    }

    public double getSaldoActual(Integer idPrestamo, String monedaRegistracion) {

        return cuentaCorrienteDAO.getSaldoAFecha(idPrestamo, monedaRegistracion, new Date());
    }

    public double getSaldoSecundarioActual(Integer idPrestamo, String monedaRegistracion) {

        return cuentaCorrienteDAO.getSaldoSecundarioAFecha(idPrestamo, monedaRegistracion, new Date());
    }

    public ItemHistoricoCuentaCorrientePrestamo getSaldoAnterior(Integer idPrestamo, String monedaRegistracion, Date fDesde) {

        ItemHistoricoCuentaCorrientePrestamo itemSA = new ItemHistoricoCuentaCorrientePrestamo();
        itemSA.setCodfor("SA");
        itemSA.setComprobante("Saldo Anterior");
        itemSA.setNrocta("");
        itemSA.setFchmov(fDesde);
        itemSA.setSucurs("0000");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fDesde); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, -1);  // numero de días a añadir o restar

        double saldo = cuentaCorrienteDAO.getSaldoAFecha(idPrestamo, monedaRegistracion, calendar.getTime());
        double saldoSecundario = cuentaCorrienteDAO.getSaldoSecundarioAFecha(idPrestamo, monedaRegistracion, calendar.getTime());

        itemSA.setSaldo(saldo);
        itemSA.setSaldoSecundario(saldoSecundario);

        return itemSA;
    }

    public String conDeuda(String nroCuenta, String monReg) {

        return cuentaCorrienteDAO.conDeuda(nroCuenta, monReg);

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public void invertirImporteCreditos(List<ItemPendienteCuentaCorrientePrestamo> creditos) {

        if (creditos == null) {
            return;
        }

        for (ItemPendienteCuentaCorrientePrestamo c : creditos) {

            c.setPendiente(c.getPendiente() * -1);
            c.setPendienteSecundario(c.getPendienteSecundario() * -1);
        }
    }

    public void marcarDebito(ItemPendienteCuentaCorrientePrestamo cuota, double cotizacion) {

        if (cuota.isSeleccionado()) {

            cuota.setImporteAplicar(cuota.getPendiente());
            cuota.setImporteAplicarSecundario(cuota.getImporteAplicar() / cotizacion);

        } else {

            cuota.setImporteAplicar(0);
            cuota.setImporteAplicarSecundario(0);
        }
    }

}
