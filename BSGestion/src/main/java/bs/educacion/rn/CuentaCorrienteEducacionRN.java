/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.administracion.rn.ParametrosRN;
import bs.educacion.dao.CuentaCorrienteEducacionDAO;
import bs.educacion.modelo.AplicacionCuentaCorrienteEducacion;
import bs.educacion.modelo.ItemHistoricoCuentaCorrienteEducacion;
import bs.educacion.modelo.ItemMovimientoEducacion;
import bs.educacion.modelo.ItemPendienteCuentaCorrienteEducacion;
import bs.educacion.modelo.MovimientoEducacion;
import bs.educacion.modelo.ParametroEducacion;
import bs.entidad.modelo.EntidadComercial;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Periodo;
import bs.global.util.Numeros;
import bs.global.util.UtilFechas;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Claudio
 */
@Stateless
public class CuentaCorrienteEducacionRN implements Serializable {

    @EJB
    private CuentaCorrienteEducacionDAO cuentaCorrienteDAO;
    @EJB
    private EducacionRN movimientoEducacionRN;
    @EJB
    private ParametrosRN parametrosRN;
    @EJB
    private ParametroEducacionRN parametrosEducacionRN;

    public void generarItemCuentaCorriente(MovimientoEducacion m) throws ExcepcionGeneralSistema {
        generarItemCuentaCorriente(m, null);
    }

    /**
     * Pasamos por parámetro un movimiento de educación y de acuerdo a los
     * itemsPendientes generamos la cuenta corriente
     *
     * @param m Movimiento de Educación
     * @param itemPendiente Item al que se aplica
     * @throws ExcepcionGeneralSistema
     */
    public void generarItemCuentaCorriente(MovimientoEducacion m, ItemPendienteCuentaCorrienteEducacion itemPendiente) throws ExcepcionGeneralSistema {

        if (!m.getComprobante().getTipoImputacion().equals("CC")) {
            return;
        }

        if (m.getItemsMovimiento() == null) {
            throw new ExcepcionGeneralSistema("Los items de condiciones de pago no puede estar vacío");
        }

        if (m.getItemsCuentaCorriente() != null) {
            m.getItemsCuentaCorriente().clear();
        }

        Calendar fechaVencimiento = Calendar.getInstance();

        for (ItemMovimientoEducacion item : m.getItemsMovimiento()) {

            double importeCuota = Numeros.redondear(item.getPrecio() - item.getPrecio() * item.getPorcentaTotalBonificacion() / 100);

            //Buscamos la fecha a partir de la cual calculamos los vencimientos
            // M = Fecha de movimiento I = Fecha inicio curso
            if (item.getOrigenFechaCalculoVencimiento().equals("M") || m.getCurso() == null) {
                fechaVencimiento.setTime(m.getFechaMovimiento());
            } else {
                fechaVencimiento.setTime(m.getCurso().getFechaInicio());
                fechaVencimiento.set(Calendar.DAY_OF_MONTH, item.getDiaVencimientoSegunPeriodo());

                //Seteamos el mes del vencimiento de acuerdo a la cuota desde
                fechaVencimiento.add(Calendar.MONTH, item.getCuotaDesde() - 1);
            }

            for (int i = item.getCuotaDesde(); i <= item.getCuotaHasta(); ++i) {

                AplicacionCuentaCorrienteEducacion icc = new AplicacionCuentaCorrienteEducacion();
                icc.setMovimiento(m);
                icc.setItemArancel(item.getItemArancel());
                icc.setItemMovimiento(item);
                icc.setMovimientoAplicado(m);
                icc.setItemMovimientoAplicado(item);

                if (itemPendiente != null) {

                    icc.setFechaVencimiento(itemPendiente.getFechaVencimiento());
                    icc.setCuota(itemPendiente.getCuotas());

                } else {
                    icc.setFechaVencimiento(fechaVencimiento.getTime());
                    icc.setCuota(i);
                }

                icc.setFechaAplicacion(m.getFechaMovimiento());

                icc.setObservaciones(item.getConcepto().getDescripcion());
                icc.setAlumno(m.getAlumno());
                icc.setCodigoImputacion("CC");
                icc.setMonedaRegistracion(m.getMonedaRegistracion());
                icc.setCotizacion(m.getCotizacion());
                icc.setImporte(importeCuota);

                if (m.getComprobante().getSignoAplicacionCuentaCorriente().equals("-")) {

                    icc.setImporte(importeCuota * - 1);
                }

                icc.setImporteSecundario(icc.getImporte() / m.getCotizacion());
                icc.setImportePendiente(icc.getImporte());
                icc.setImporteOriginal(icc.getImporte());

                if (icc.getImporte() != 0) {
                    m.getItemsCuentaCorriente().add(icc);
                }

                fechaVencimiento = calcularProximoVencimiento(item.getPeriodo(), fechaVencimiento);
            }
        }
    }

    /**
     * Generamos los items de aplicación a la cuenta corriente
     *
     * @param m
     * @param creditos
     * @param debitos
     */
    public void generarItemAplicacionCuentaCorriente(MovimientoEducacion m,
            List<ItemPendienteCuentaCorrienteEducacion> creditos,
            List<ItemPendienteCuentaCorrienteEducacion> debitos) {

        if (creditos != null) {

            for (ItemPendienteCuentaCorrienteEducacion ic : creditos) {

                if (ic.isSeleccionado()) {

                    ItemMovimientoEducacion iAplicacion = movimientoEducacionRN.getItemMovimiento(ic.getId());

                    double importe = ic.getImporteAplicar() - ic.getImporteInteres();

                    AplicacionCuentaCorrienteEducacion icc = new AplicacionCuentaCorrienteEducacion();

                    icc.setMovimiento(m);
                    icc.setMovimientoAplicado(iAplicacion.getMovimiento());
                    icc.setItemMovimientoAplicado(iAplicacion);
                    icc.setObservaciones(ic.getObservaciones());
                    icc.setAlumno(m.getAlumno());
                    icc.setCuota(ic.getCuotas());
                    icc.setCodigoImputacion("CC");
                    icc.setMonedaRegistracion(m.getMonedaRegistracion());
                    icc.setCotizacion(m.getCotizacion());

                    icc.setImporte(Numeros.redondear(importe));
                    icc.setImportePendiente(icc.getImporte());
                    icc.setImporteOriginal(icc.getImporte());
                    icc.setImporteSecundario(Numeros.redondear(importe / m.getCotizacion()));

                    icc.setFechaAplicacion(m.getFechaMovimiento());
                    icc.setFechaVencimiento(ic.getFechaVencimiento());

                    m.getItemsCuentaCorriente().add(icc);
                }
            }
        }

        if (debitos != null) {

            for (ItemPendienteCuentaCorrienteEducacion id : debitos) {
                if (id.isSeleccionado()) {

                    ItemMovimientoEducacion iAplicacion = movimientoEducacionRN.getItemMovimiento(id.getId());

                    double importe = (id.getImporteAplicar() - id.getImporteInteres()) * - 1;

                    AplicacionCuentaCorrienteEducacion icc = new AplicacionCuentaCorrienteEducacion();
                    icc.setMovimiento(m);
                    icc.setMovimientoAplicado(iAplicacion.getMovimiento());
                    icc.setItemMovimientoAplicado(iAplicacion);
                    icc.setObservaciones(id.getObservaciones());
                    icc.setAlumno(m.getAlumno());
                    icc.setCuota(id.getCuotas());
                    icc.setCodigoImputacion("CC");
                    icc.setMonedaRegistracion(m.getMonedaRegistracion());
                    icc.setCotizacion(m.getCotizacion());

                    icc.setImporte(Numeros.redondear(importe));
                    icc.setImportePendiente(icc.getImporte());
                    icc.setImporteOriginal(icc.getImporte());
                    icc.setImporteSecundario(Numeros.redondear(importe / m.getCotizacion()));

                    icc.setFechaAplicacion(m.getFechaMovimiento());
                    icc.setFechaVencimiento(id.getFechaVencimiento());

                    m.getItemsCuentaCorriente().add(icc);

                }
            }
        }
    }

    /**
     * Se utiliza para generar cuenta corriente cuando el recibo o comprobante
     * que se ingresa no aplica a ningún movimiento.
     *
     * @param m Movimiento de venta
     * @throws ExcepcionGeneralSistema
     */
    public void generarItemCuentaCorrienteAnticipo(MovimientoEducacion m) throws ExcepcionGeneralSistema {

        if (m.getImporteTotal() > 0) {
            throw new ExcepcionGeneralSistema("Importe total no fue generado, nada para agregar a la cuenta corriente");
        }
        m.getItemsCuentaCorriente().clear();

        AplicacionCuentaCorrienteEducacion icc = new AplicacionCuentaCorrienteEducacion();
        icc.setMovimiento(m);
        icc.setMovimientoAplicado(m);
        icc.setAlumno(m.getAlumno());
        icc.setCuota(1);
        icc.setCodigoImputacion("CC");
        icc.setMonedaRegistracion(m.getMonedaRegistracion());
        icc.setCotizacion(m.getCotizacion());

        icc.setImporte(m.getImporteTotal());

        if (m.getComprobante().getSignoAplicacionCuentaCorriente().equals("-")) {

            icc.setImporte(m.getImporteTotal() * -1);
        }

        icc.setImportePendiente(icc.getImporte());
        icc.setImporteOriginal(icc.getImporte());
        icc.setImporteSecundario(Numeros.redondear(icc.getImporte() / m.getCotizacion()));

        icc.setFechaAplicacion(m.getFechaMovimiento());
        icc.setFechaVencimiento(m.getFechaMovimiento());

        m.getItemsCuentaCorriente().add(icc);

    }

    public void generarItemCuentaCorrienteRecibo(MovimientoEducacion m, List<ItemPendienteCuentaCorrienteEducacion> debitos) throws ExcepcionGeneralSistema {

//        System.err.println("m.getImporteTotal() " + m.getImporteTotal());
        if (m.getImporteTotal() <= 0) {
            throw new ExcepcionGeneralSistema("Importe total no fue generado, nada para agregar a la cuenta corriente");
        }

        if (debitos == null || debitos.isEmpty()) {
            throw new ExcepcionGeneralSistema("No existen débitos para aplicar en cuenta corriente");
        }

        double importeAplicar = 0;

        for (ItemPendienteCuentaCorrienteEducacion i : debitos) {

            if (i.isSeleccionado()) {
                importeAplicar = importeAplicar + i.getImporteAplicar() - i.getImporteInteres();
            }
        }

        if (Numeros.redondear(m.getImporteTotal()) != Numeros.redondear(importeAplicar)) {
            throw new ExcepcionGeneralSistema("El importe total del recibo (" + Numeros.redondear(m.getImporteTotal()) + ") no coincide con el importe total a aplicar (" + Numeros.redondear(importeAplicar) + ")");
        }
        generarItemAplicacionCuentaCorriente(m, null, debitos);
    }

    /**
     * Revertimos los items de cuenta corriente del movimiento original y los
     * asignamos al movimiento de reversión.
     *
     * @param mr
     * @param m
     */
    public void generarItemReversion(MovimientoEducacion mr, MovimientoEducacion m) throws ExcepcionGeneralSistema {

        if (m.getImporteTotal() <= 0) {
            throw new ExcepcionGeneralSistema("Importe total no fue generado, nada para agregar a la cuenta corriente");
        }

        if (m.getItemsCuentaCorriente() == null || m.getItemsCuentaCorriente().isEmpty()) {
            throw new ExcepcionGeneralSistema("No existen items en cuenta corriente para anular");
        }

        m.getItemsCuentaCorriente().forEach(iccr -> {

            try {
                AplicacionCuentaCorrienteEducacion icc = iccr.clone();
                icc.setId(null);
                icc.setMovimiento(mr);
                icc.setFechaAplicacion(mr.getFechaMovimiento());

                icc.setImporte(iccr.getImporte() * -1);

                icc.setImporteSecundario(icc.getImporte() / m.getCotizacion());
                icc.setImportePendiente(icc.getImporte());
                icc.setImporteOriginal(icc.getImporte());

                if (icc.getImporte() != 0) {
                    mr.getItemsCuentaCorriente().add(icc);
                }
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(CuentaCorrienteEducacionRN.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    /**
     *
     * @param saldos lista de saldos de cuenta corriente
     * @return La suma de los saldos pendientes
     */
    public double sumarSaldos(List<ItemPendienteCuentaCorrienteEducacion> saldos, String monedaRegistracion) {

        if (saldos == null) {
            return 0;
        }

        double importeAplicar = 0;
        double importeAplicarSecundario = 0;

        for (ItemPendienteCuentaCorrienteEducacion ic : saldos) {

            if (ic.isSeleccionado()) {
                importeAplicar = importeAplicar + ic.getImporteAplicar() - ic.getImporteInteres();
                importeAplicarSecundario = importeAplicarSecundario + ic.getImporteAplicarSecundario();
            } else {
                ic.setImporteAplicar(0);
                ic.setImporteAplicarSecundario(0);
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

    public double sumarIntereses(List<ItemPendienteCuentaCorrienteEducacion> saldos, String monedaRegistracion) {

        if (saldos == null) {
            return 0;
        }

        double importeIntereses = 0;

        for (ItemPendienteCuentaCorrienteEducacion ic : saldos) {

            if (ic.isSeleccionado()) {
                importeIntereses = importeIntereses + ic.getImporteInteres();
            } else {
                ic.setImporteAplicar(0);
                ic.setImporteAplicarSecundario(0);
            }
        }
        return importeIntereses;

    }

    public List<ItemHistoricoCuentaCorrienteEducacion> getHistoricoMovimientos(String nroCuenta, Date fDesde, Date fHasta, String monReg) {

        return cuentaCorrienteDAO.getHistoricoMovimientos(nroCuenta, monReg, fDesde, fHasta);
    }

    public List<ItemPendienteCuentaCorrienteEducacion> getDebitosPendientes(String nroCuenta, Date fechaMovimiento, String monReg, String comprobanteInterno, Integer idMovimiento) {

        List<ItemPendienteCuentaCorrienteEducacion> pendientes = cuentaCorrienteDAO.getPendientesByNroCuenta(nroCuenta, monReg, comprobanteInterno, "D", idMovimiento);

        pendientes.forEach(i -> {
            i.setDiasVencidos(UtilFechas.daysDifference(i.getFechaVencimiento(), fechaMovimiento));
        });
        return pendientes;
    }

    public List<EntidadComercial> getAlumnosConSaldos(Map<String, String> filtro, String monReg, String comprobanteInterno) {

        return cuentaCorrienteDAO.getAlumnosConSaldos(filtro, monReg, comprobanteInterno);
    }

    public List<ItemPendienteCuentaCorrienteEducacion> getCreditosPendientes(String nroCuenta, String monReg, String incEst, Integer idMovimiento) {

        return cuentaCorrienteDAO.getPendientesByNroCuenta(nroCuenta, monReg, incEst, "H", idMovimiento);
    }

    public double getSaldoByMovimiento(Integer idMovimiento) {

        return cuentaCorrienteDAO.getSaldoByMovimiento(idMovimiento);
    }

    public double getSaldoActual(String nrocta, String monedaRegistracion) {

        return cuentaCorrienteDAO.getSaldoAFecha(nrocta, monedaRegistracion, new Date());
    }

    public double getSaldoSecundarioActual(String nrocta, String monedaRegistracion) {

        return cuentaCorrienteDAO.getSaldoSecundarioAFecha(nrocta, monedaRegistracion, new Date());
    }

    public ItemHistoricoCuentaCorrienteEducacion getSaldoAnterior(String nrocta, String monedaRegistracion, Date fDesde) {

        ItemHistoricoCuentaCorrienteEducacion itemSA = new ItemHistoricoCuentaCorrienteEducacion();
        itemSA.setCodfor("SA");
        itemSA.setComprobante("Saldo Anterior");
        itemSA.setNrocta(nrocta);
        itemSA.setFchmov(fDesde);
        itemSA.setPuntoVenta("0000");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fDesde); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, -1);  // numero de días a añadir o restar

        double saldo = cuentaCorrienteDAO.getSaldoAFecha(nrocta, monedaRegistracion, calendar.getTime());
        double saldoSecundario = cuentaCorrienteDAO.getSaldoSecundarioAFecha(nrocta, monedaRegistracion, calendar.getTime());

        itemSA.setSaldo(saldo);
        itemSA.setSaldoSecundario(saldoSecundario);

        return itemSA;
    }

    public String conDeuda(String nroCuenta, String monReg) {

        return cuentaCorrienteDAO.conDeuda(nroCuenta, monReg);

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public void invertirImporteCreditos(List<ItemPendienteCuentaCorrienteEducacion> creditos) {

        if (creditos == null) {
            return;
        }

        creditos.stream().forEach(cr -> {

            cr.setPendiente(cr.getPendiente() * -1);
            // c.setPendienteSecundario(c.getPendienteSecundario() * -1);

        });
    }

    public Calendar calcularProximoVencimiento(Periodo periodo, Calendar fechaVencimiento) {

        if (periodo.getId() == 1) {
            fechaVencimiento.add(Calendar.YEAR, 1);
        }

        if (periodo.getId() == 2) {
            fechaVencimiento.add(Calendar.MONTH, 6);
        }

        if (periodo.getId() == 3) {
            fechaVencimiento.add(Calendar.MONTH, 4);
        }

        if (periodo.getId() == 4) {
            fechaVencimiento.add(Calendar.MONTH, 3);
        }

        if (periodo.getId() == 5) {
            fechaVencimiento.add(Calendar.MONTH, 2);
        }

        if (periodo.getId() == 6) {
            fechaVencimiento.add(Calendar.MONTH, 1);
        }

        if (periodo.getId() == 7) {
            fechaVencimiento.add(Calendar.DAY_OF_YEAR, 7);
        }

        if (periodo.getId() == 8) {
            fechaVencimiento.add(Calendar.DAY_OF_YEAR, 1);
        }

        return fechaVencimiento;
    }

    public void calcularIntereses(Date fechaMovimiento, List<ItemPendienteCuentaCorrienteEducacion> debitos) {

        if (debitos == null || fechaMovimiento == null) {
            return;
        }

        ParametroEducacion pe = parametrosEducacionRN.getParametro();

        for (ItemPendienteCuentaCorrienteEducacion i : debitos) {
            if (i.isCalculaInteres()
                    && i.getFechaVencimiento().before(fechaMovimiento)
                    && i.getObservaciones().contains("Cuota Mensual")) {

                Calendar cal = Calendar.getInstance();

                cal.setTime(fechaMovimiento);
                int mesMov = cal.get(Calendar.MONTH);

                cal.setTime(i.getFechaVencimiento());
                int mesVto = cal.get(Calendar.MONTH);

                if (i.getDiasVencidos() > 0 && mesMov == mesVto) {
                    i.setImporteInteres(150);
                }

                if (i.getDiasVencidos() > 20 && mesMov > mesVto) {
                    i.setImporteInteres(300);
                }

//                i.setImporteInteres(i.getPendiente() * (pe.getPorcentajeInteres() / 100) * i.getDiasVencidos());
            } else {
                i.setImporteInteres(0);
            }
        }

    }

    /**
     * Contamos la cantidad de cuotas vencidas a un fecha determinada
     *
     * @param fechaMovimiento
     * @param debitos
     * @return
     */
    public int calcularCantidadCuotasVencidas(Date fechaMovimiento, List<ItemPendienteCuentaCorrienteEducacion> debitos) {

        if (debitos == null || fechaMovimiento == null) {
            return 0;
        }

        int cantidad = 0;

        for (ItemPendienteCuentaCorrienteEducacion i : debitos) {
            if (i.getFechaVencimiento().before(fechaMovimiento)) {
                cantidad++;
            }
        }

        return cantidad;
    }

}
