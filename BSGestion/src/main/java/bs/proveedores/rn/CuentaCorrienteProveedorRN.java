/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.rn;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.proveedores.dao.CuentaCorrienteProveedorDAO;
import bs.proveedores.modelo.AplicacionCuentaCorrienteProveedor;
import bs.proveedores.modelo.ItemCondicionPagoProveedor;
import bs.proveedores.modelo.ItemHistoricoCuentaCorrienteProveedor;
import bs.proveedores.modelo.ItemPendienteCuentaCorrienteProveedor;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.tesoreria.modelo.MovimientoTesoreria;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

public class CuentaCorrienteProveedorRN implements Serializable {

    @EJB
    private CuentaCorrienteProveedorDAO cuentaCorrienteDAO;

    @EJB
    private MovimientoProveedorRN movimientoProveedorRN;

    /**
     * Generamos los items de aplicación a la cuenta corriente
     *
     * @param m
     * @param creditosPendientes
     * @param debitosPendientes
     */
    public void generarItemAplicacionCuentaCorriente(MovimientoProveedor m,
            List<ItemPendienteCuentaCorrienteProveedor> creditosPendientes,
            List<ItemPendienteCuentaCorrienteProveedor> debitosPendientes) {

        if (creditosPendientes != null) {
            for (ItemPendienteCuentaCorrienteProveedor ic : creditosPendientes) {

                if (ic.isSeleccionado()) {

                    MovimientoProveedor mAplicado;
                    BigDecimal importeAplicado;

                    /**
                     * ******************************************************************************************
                     * Si es una orden de pago con retenciones se hace lo
                     * siguiente: - Si es anticipo, sumamos las retenciones al
                     * importe total a pargar - Si no es anticipo restamos el
                     * importe a aplicar para luego aplicarlo con al retención
                     * *******************************************************************************************
                     */
                    if (m.isEsAnticipo()) {
                        mAplicado = m;
                        importeAplicado = ic.getImporteAplicar().add(ic.getImporteRetencion().negate());
                    } else {
                        mAplicado = movimientoProveedorRN.getMovimiento(ic.getId());
                        importeAplicado = ic.getImporteAplicar().add(ic.getImporteRetencion().negate());
                    }

                    BigDecimal importeRetencion = ic.getImporteRetencion();

                    AplicacionCuentaCorrienteProveedor icc = new AplicacionCuentaCorrienteProveedor();

                    icc.setMovimiento(m);
                    icc.setMovimientoAplicacion(mAplicado);
                    icc.setCuota(ic.getCuotas());
                    icc.setProveedor(m.getProveedor());
                    icc.setNroSubcuenta(m.getProveedorCuentaCorriente().getNrocta());
                    icc.setCodigoImputacion("CC");
                    icc.setMonedaSecundaria(m.getMonedaSecundaria());
                    icc.setMonedaRegistracion(m.getMonedaRegistracion());
                    icc.setCotizacion(m.getCotizacion());

                    icc.setImporte(importeAplicado);
                    icc.setImporteSecundario(importeAplicado.divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));
                    icc.setImporteRetencion(importeRetencion);

                    icc.setFechaAplicacion(m.getFechaMovimiento());
                    icc.setFechaVencimiento(ic.getFechaVencimiento());

                    m.getItemCuentaCorriente().add(icc);

                    /**
                     * Esto es provisorio, para metalurgica avellaneda
                     */
                    if (m.getRetenciones() == null || m.getRetenciones().isEmpty()) {
                        continue;
                    }

                    //Si existen retenciones, generamos los items cuenta corriente
                    for (MovimientoProveedor r : m.getRetenciones()) {

                        importeAplicado = ic.getImporteRetencion();
                        icc = new AplicacionCuentaCorrienteProveedor();

                        //Si es anticipo viene id de movimiento nulo, aplicamos el mismo comprobante
                        if (ic.getId() == null) {
                            mAplicado = r;
                        }

                        icc.setMovimiento(r);
                        icc.setMovimientoAplicacion(mAplicado);
                        icc.setCuota(1);
                        icc.setProveedor(r.getProveedor());
                        icc.setNroSubcuenta(r.getProveedorCuentaCorriente().getNrocta());
                        icc.setCodigoImputacion("CC");
                        icc.setMonedaSecundaria(r.getMonedaSecundaria());
                        icc.setMonedaRegistracion(r.getMonedaRegistracion());
                        icc.setCotizacion(r.getCotizacion());

                        icc.setImporte(importeAplicado);
                        icc.setImporteSecundario(importeAplicado.divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));

                        icc.setFechaAplicacion(m.getFechaMovimiento());
                        icc.setFechaVencimiento(ic.getFechaVencimiento());

                        r.getItemCuentaCorriente().add(icc);
                    }
                }
            }
        }

        if (debitosPendientes != null) {
            for (ItemPendienteCuentaCorrienteProveedor id : debitosPendientes) {
                if (id.isSeleccionado()) {
                    /*
                    Buscamos el movimiento que vamos a aplicar en cta cte.
                    Si el id del pendiente es null, el comprobante aplica a si mismo
                     */
                    MovimientoProveedor mAplicado;

                    if (id.getId() != null) {
                        mAplicado = movimientoProveedorRN.getMovimiento(id.getId());
                    } else {
                        mAplicado = m;
                    }

                    BigDecimal importe = id.getImporteAplicar().negate();

                    AplicacionCuentaCorrienteProveedor icc = new AplicacionCuentaCorrienteProveedor();
                    icc.setMovimiento(m);
                    icc.setMovimientoAplicacion(mAplicado);
                    icc.setCuota(id.getCuotas());
                    icc.setProveedor(m.getProveedor());
                    icc.setNroSubcuenta(m.getProveedorCuentaCorriente().getNrocta());
                    icc.setCodigoImputacion("CC");
                    icc.setMonedaSecundaria(m.getMonedaSecundaria());
                    icc.setMonedaRegistracion(m.getMonedaRegistracion());
                    icc.setCotizacion(m.getCotizacion());

                    icc.setImporte(importe);
                    icc.setImporteSecundario(importe.divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));

                    icc.setFechaAplicacion(new Date());
                    icc.setFechaVencimiento(id.getFechaVencimiento());

                    m.getItemCuentaCorriente().add(icc);
                }
            }
        }
    }

    public void generarItemCuentaCorriente(MovimientoProveedor m) throws ExcepcionGeneralSistema {

        if (m.getId() != null) {
            throw new ExcepcionGeneralSistema("No es posible editar los items en cuenta corriente");
        }

        if (m.getCondicionDePago() == null) {
            throw new ExcepcionGeneralSistema("La condición de pago no puede estar vacía");
        }

        if (m.getItemTotal() == null || m.getItemTotal().isEmpty()) {
            throw new ExcepcionGeneralSistema("Item total no fue generado, nada para agregar a la cuenta corriente");
        }

        m.getItemCuentaCorriente().clear();

        BigDecimal importeTotal = m.getItemTotal().get(0).getImporte();
//
//        System.err.println("importeTotal para cuenta corriente"+importeTotal);

        for (ItemCondicionPagoProveedor icp : m.getCondicionDePago().getCuotas()) {

            BigDecimal impoteCuota = importeTotal.multiply(new BigDecimal(icp.getPorcentaje())).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            Calendar fechaVencimiento = Calendar.getInstance();

            if (m.getFechaEmision() != null) {
                fechaVencimiento.setTime(m.getFechaEmision());
            } else {
                fechaVencimiento.setTime(m.getFechaMovimiento());
            }

            fechaVencimiento.add(Calendar.DAY_OF_YEAR, icp.getDiasDePago());

            AplicacionCuentaCorrienteProveedor icc = new AplicacionCuentaCorrienteProveedor();
            icc.setMovimiento(m);
            icc.setMovimientoAplicacion(m);
            icc.setCuota(icp.getCuotas());
            icc.setProveedor(m.getProveedor());
            icc.setNroSubcuenta(m.getProveedorCuentaCorriente().getNrocta());
            icc.setCodigoImputacion("CC");
            icc.setMonedaSecundaria(m.getMonedaSecundaria());
            icc.setMonedaRegistracion(m.getMonedaRegistracion());
            icc.setCotizacion(m.getCotizacion());

            if (m.getComprobante().getSignoAplicacionCuentaCorriente().equals("+")) {

                icc.setImporte(impoteCuota);
                icc.setImporteSecundario(impoteCuota.divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));

            } else {

                icc.setImporte(impoteCuota.negate());
                icc.setImporteSecundario((impoteCuota.divide(m.getCotizacion(), 2, RoundingMode.HALF_UP)).negate());
            }

            icc.setFechaAplicacion(new Date());
            icc.setFechaVencimiento(fechaVencimiento.getTime());

            m.getItemCuentaCorriente().add(icc);
        }
    }

    /**
     * Se utiliza para generar cuenta corriente cuando el comprobante qeu se
     * ingresa no aplica ningún movimiento.
     *
     * @param m Movimiento de proveedor
     * @throws ExcepcionGeneralSistema
     */
    @Deprecated
    public void generarItemCuentaCorrienteAnticipo(MovimientoProveedor m) throws ExcepcionGeneralSistema {

        if (m.getItemTotal() == null || m.getItemTotal().isEmpty()) {
            throw new ExcepcionGeneralSistema("Item total no fue generado, nada para agregar a la cuenta corriente");
        }
        m.getItemCuentaCorriente().clear();

        BigDecimal importeTotal = m.getItemTotal().get(0).getImporte();
        AplicacionCuentaCorrienteProveedor icc = new AplicacionCuentaCorrienteProveedor();
        icc.setMovimiento(m);
        icc.setMovimientoAplicacion(m);
        icc.setCuota(1);
        icc.setProveedor(m.getProveedor());
        icc.setNroSubcuenta(m.getProveedorCuentaCorriente().getNrocta());
        icc.setCodigoImputacion("CC");
        icc.setMonedaSecundaria(m.getMonedaSecundaria());
        icc.setMonedaRegistracion(m.getMonedaRegistracion());
        icc.setCotizacion(m.getCotizacion());

        if (m.getComprobante().getSignoAplicacionCuentaCorriente().equals("+")) {

            icc.setImporte(importeTotal);
            icc.setImporteSecundario(importeTotal.divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));

        } else {

            icc.setImporte(importeTotal.negate());
            icc.setImporteSecundario((importeTotal.divide(m.getCotizacion(), 2, RoundingMode.HALF_UP)).negate());
        }

        icc.setFechaAplicacion(new Date());
        icc.setFechaVencimiento(m.getFechaVencimiento());

        m.getItemCuentaCorriente().add(icc);

    }

    public void generarItemCuentaCorrienteOrdenPago(MovimientoProveedor m,
            List<ItemPendienteCuentaCorrienteProveedor> pendientes) throws ExcepcionGeneralSistema {

        if (m.getItemTotal() == null || m.getItemTotal().isEmpty()) {
            throw new ExcepcionGeneralSistema("Item total no fue generado, nada para agregar a la cuenta corriente");
        }

        if (pendientes == null || pendientes.isEmpty()) {
            throw new ExcepcionGeneralSistema("No existen saldos pendientes para aplicar en cuenta corriente");
        }

        m.getItemCuentaCorriente().clear();

        BigDecimal importeTotal = m.getImporteTotal().add(m.getImporteRetenciones().negate());
        BigDecimal importeAplicar = BigDecimal.ZERO;

        for (ItemPendienteCuentaCorrienteProveedor ipcc : pendientes) {

            importeAplicar = importeAplicar.add(ipcc.getImporteAplicar().add(ipcc.getImporteRetencion().negate())).setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        if (importeAplicar.compareTo(importeTotal) != 0) {

            throw new ExcepcionGeneralSistema("El importe total del comprobante (" + importeTotal + ") no coincide con el importe total a aplicar (" + importeAplicar + ")");
        }

        generarItemAplicacionCuentaCorriente(m, pendientes, null);
    }

    /**
     * Generamos items de aplicación en cuenta corriente a partir de un
     * movimiento ya generado o persistido.
     *
     * @param m Movimiento de proveedor a generar
     */
    public void generarItemsCuentaCorrienteReversionMovimiento(MovimientoProveedor m) {

        List<AplicacionCuentaCorrienteProveedor> itemsCuentaCorriente = m.getMovimientoReversion().getItemCuentaCorriente();

        m.getItemCuentaCorriente().clear();

        for (AplicacionCuentaCorrienteProveedor iacc : itemsCuentaCorriente) {

            BigDecimal importe = iacc.getImporte().add(iacc.getImporteRetencion()).negate();

            AplicacionCuentaCorrienteProveedor icc = new AplicacionCuentaCorrienteProveedor();
            icc.setMovimiento(m);
            icc.setMovimientoAplicacion(iacc.getMovimientoAplicacion());
            icc.setCuota(iacc.getCuota());
            icc.setProveedor(m.getProveedor());
            icc.setNroSubcuenta(m.getProveedorCuentaCorriente().getNrocta());
            icc.setCodigoImputacion("CC");
            icc.setMonedaSecundaria(m.getMonedaSecundaria());
            icc.setMonedaRegistracion(m.getMonedaRegistracion());
            icc.setCotizacion(m.getCotizacion());

            icc.setImporte(importe);
            icc.setImporteSecundario(importe.divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));

            icc.setFechaAplicacion(iacc.getFechaAplicacion());
            icc.setFechaVencimiento(iacc.getFechaVencimiento());

            m.getItemCuentaCorriente().add(icc);
        }
    }

    /**
     *
     * @param saldos lista de saldos de cuenta corriente
     * @return La suma de los saldos pendientes
     */
    public BigDecimal sumarSaldos(List<ItemPendienteCuentaCorrienteProveedor> saldos) {

        if (saldos == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalHaber = BigDecimal.ZERO;

        for (ItemPendienteCuentaCorrienteProveedor ic : saldos) {

            if (ic.isSeleccionado()) {
                //totalHaber = totalHaber.add(ic.getImporteAplicar().add(ic.getImporteRetencion().negate()));
                totalHaber = totalHaber.add(ic.getImporteAplicar().setScale(2, RoundingMode.HALF_UP));
            } else {
                ic.setImporteAplicar(BigDecimal.ZERO);
            }
        }
        return totalHaber;
    }

    public BigDecimal sumarSaldosRetenciones(List<ItemPendienteCuentaCorrienteProveedor> saldos) {

        if (saldos == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalRetenciones = BigDecimal.ZERO;

        for (ItemPendienteCuentaCorrienteProveedor ic : saldos) {

            if (ic.isSeleccionado()) {
                totalRetenciones = totalRetenciones.add(ic.getImporteRetencion().setScale(2, RoundingMode.HALF_UP));
            } else {
                ic.setImporteAplicar(BigDecimal.ZERO);
            }
        }

        return totalRetenciones;
    }

    public List<ItemHistoricoCuentaCorrienteProveedor> getHistoricoMovimientos(String nroCuenta, String monReg, String comprobanteInterno, Date fDesde, Date fHasta) {

        return cuentaCorrienteDAO.getHistoricoMovimientos(nroCuenta, monReg, comprobanteInterno, fDesde, fHasta);
    }

    public List<ItemPendienteCuentaCorrienteProveedor> getDebitosPendientes(String nroCuenta, String monReg, String comprobanteInterno) {

        return cuentaCorrienteDAO.getPendientesByNroCuenta(nroCuenta, monReg, comprobanteInterno, "D");
    }

    public List<ItemPendienteCuentaCorrienteProveedor> getCreditosPendientes(String nroCuenta, String monReg, String comprobanteInterno) {

        return cuentaCorrienteDAO.getPendientesByNroCuenta(nroCuenta, monReg, comprobanteInterno, "H");
    }

    public BigDecimal getSaldoActual(String nrocta, String monReg, String comprobanteInterno) {

        BigDecimal s = cuentaCorrienteDAO.getSaldoAFecha(nrocta, monReg, comprobanteInterno, new Date());
        if (s == null) {
            s = BigDecimal.ZERO;
        }
        return s;

    }

    public BigDecimal getSaldoSecundarioActual(String nrocta, String monReg, String comprobanteInterno) {

        BigDecimal s = cuentaCorrienteDAO.getSaldoSecundarioAFecha(nrocta, monReg, comprobanteInterno, new Date());
        if (s == null) {
            s = BigDecimal.ZERO;
        }
        return s;

    }

    public ItemHistoricoCuentaCorrienteProveedor getSaldoAnterior(String nrocta, String monReg, String comprobanteInterno, Date fDesde) {

        ItemHistoricoCuentaCorrienteProveedor itemSA = new ItemHistoricoCuentaCorrienteProveedor();
        itemSA.setCodfor("SA");
        itemSA.setComprobante("Saldo Anterior");
        itemSA.setNrocta(nrocta);
        itemSA.setFchmov(fDesde);
        itemSA.setPuntoVenta("0000");
        itemSA.setNumeroFormulario(0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fDesde); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, -1);  // numero de días a añadir o restar

        BigDecimal saldo = cuentaCorrienteDAO.getSaldoAFecha(nrocta, monReg, comprobanteInterno, calendar.getTime());
        BigDecimal saldoSecundario = cuentaCorrienteDAO.getSaldoSecundarioAFecha(nrocta, monReg, comprobanteInterno, calendar.getTime());

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

    public ItemPendienteCuentaCorrienteProveedor generarItemCuentaCorrienteAnticipo(MovimientoTesoreria m, BigDecimal importeAnticipo) throws ExcepcionGeneralSistema {

        if (importeAnticipo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ExcepcionGeneralSistema("El importe a anticipar debe ser mayor a cero");
        }

        ItemPendienteCuentaCorrienteProveedor icc = new ItemPendienteCuentaCorrienteProveedor();

        icc.setCuotas(1);
        icc.setNrocta(m.getEntidad().getNrocta());
        icc.setCodigoFormulario(m.getFormulario().getCodigo());
        icc.setNumeroFormulario(m.getNumeroFormulario());
        icc.setPuntoVenta(m.getPuntoVenta().getCodigo());
        icc.setComprobante(m.getComprobante().getDescripcion());
        icc.setCodigoMonedaRegistracion(m.getMonedaRegistracion().getCodigo());
        icc.setCotizacion(m.getCotizacion());
        icc.setFechaVencimiento(m.getFechaMovimiento());
        icc.setPendiente(importeAnticipo);
        icc.setSeleccionado(true);
        icc.setImporteAplicar(importeAnticipo);

        return icc;
    }

    public void generarItemReversion(MovimientoProveedor mr, MovimientoProveedor m) throws CloneNotSupportedException {

        if (mr.getItemCuentaCorriente() != null && !m.getItemCuentaCorriente().isEmpty()) {

            mr.getItemCuentaCorriente().clear();

            for (AplicacionCuentaCorrienteProveedor item : m.getItemCuentaCorriente()) {

                AplicacionCuentaCorrienteProveedor icc = item.clone();

                icc.setMovimiento(mr);
                icc.setMovimientoAplicacion(item.getMovimientoAplicacion());

                icc.setImporte(item.getImporte().negate());
                icc.setImporteSecundario(item.getImporteSecundario().negate());
                icc.setImporteRetencion(item.getImporteRetencion().negate());

                mr.getItemCuentaCorriente().add(icc);

            }
        }
    }
}
