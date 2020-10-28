/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.web;

import bs.entidad.modelo.EntidadComercial;
import bs.entidad.web.EntidadComercialBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.util.ReportFactory;
import bs.global.web.GenericBean;
import bs.ventas.modelo.ItemPendienteCuentaCorriente;
import bs.ventas.modelo.MovimientoVenta;
import bs.ventas.rn.CuentaCorrienteRN;
import bs.ventas.rn.MovimientoVentaRN;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class AplicacionCuentaCorrienteBean extends GenericBean implements Serializable {

    @EJB
    private MovimientoVentaRN ventaRN;
    @EJB
    private CuentaCorrienteRN cuentaCorrienteRN;

    @ManagedProperty(value = "#{entidadComercialBean}")
    protected EntidadComercialBean entidadComercialBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    protected String SUCVT = "";
    protected String MODVT = "";
    protected String CODVT = "";

    private MovimientoVenta m;
    protected List<MovimientoVenta> movimientos;
    protected EntidadComercial cliente;
    protected String codigoMonedaRegistracion;

    private BigDecimal saldoActual;
    private BigDecimal saldoActualSecundario;
    private BigDecimal totalDebe;
    private BigDecimal totalHaber;
    private List<ItemPendienteCuentaCorriente> debitos;
    private List<ItemPendienteCuentaCorriente> creditos;

    /**
     * Creates a new instance of CuentaCorrienteBean
     */
    public AplicacionCuentaCorrienteBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                super.iniciar();
                nuevoMovimiento();
                beanIniciado = true;
            }
        } catch (Exception e) {
//            e.printStackTrace();
            JsfUtil.addErrorMessage("Error al iniciar el bean " + e);
        }
    }

    public void nuevoMovimiento() {

        try {
            m = ventaRN.nuevoMovimiento(CODVT, SUCVT, "X");
            saldoActual = BigDecimal.ZERO;
            saldoActualSecundario = BigDecimal.ZERO;
            totalDebe = BigDecimal.ZERO;
            totalHaber = BigDecimal.ZERO;

            debitos = new ArrayList<ItemPendienteCuentaCorriente>();
            creditos = new ArrayList<ItemPendienteCuentaCorriente>();

            buscaMovimiento = false;

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            m = null;
        }
    }

    public void guardar(boolean nuevo) {

        try {
            if (!puedoGuardar()) {
                return;
            }

            cuentaCorrienteRN.generarItemAplicacionCuentaCorriente(m, creditos, debitos);
            ventaRN.guardar(m);
            JsfUtil.addInfoMessage("El documento " + m.getComprobante().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");

            if (nuevo) {
                nuevoMovimiento();
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public boolean puedoGuardar() {

        if (totalHaber.add(totalDebe.negate()).compareTo(BigDecimal.ZERO) < 0) {
            JsfUtil.addErrorMessage("El importe total aplicado es mayor al importe disponible para aplicar");
            JsfUtil.addWarningMessage("El importe disponible debe quedar en cero");
            return false;
        }

        if (totalHaber.add(totalDebe.negate()).compareTo(BigDecimal.ZERO) > 0) {
            JsfUtil.addErrorMessage("El importe total aplicado es menor al importe disponible");
            JsfUtil.addWarningMessage("El importe disponible debe quedar en cero");
            return false;
        }

        return true;
    }

    public String procesoSeleccionDatos(FlowEvent event) {

        if (event.getOldStep().equals("datos")) {

            if (m.getCliente() == null) {
                JsfUtil.addWarningMessage("Seleccione un cliente para continuar");
                return event.getOldStep();
            }

            if (creditos.isEmpty()) {
                JsfUtil.addWarningMessage("El cliente no tiene créditos con saldo pendiente para aplicar ");
                return event.getOldStep();
            }

            if (debitos.isEmpty()) {
                JsfUtil.addWarningMessage("El cliente no tiene débitos con saldo pendiente para aplicar ");
                return event.getOldStep();
            }
        }

        if (event.getNewStep().equals("creditos")) {
            JsfUtil.addInfoMessage("Seleccione los saldos pendientes a aplicar");
        }

        if (event.getOldStep().equals("creditos") && event.getNewStep().equals("debitos")) {

            totalHaber = cuentaCorrienteRN.sumarSaldos(creditos, m.getMonedaRegistracion().getCodigo());

            if (totalHaber.compareTo(BigDecimal.ZERO) <= 0) {
                JsfUtil.addWarningMessage("Seleccione al menos un crédito pendiente para continuar");
                return event.getOldStep();
            }
        }

        if (event.getNewStep().equals("debitos")) {
            JsfUtil.addInfoMessage("Seleccione los saldos pendientes a aplicar");
        }

        if (event.getOldStep().equals("debitos") && event.getNewStep().equals("confirmacion")) {

            totalDebe = cuentaCorrienteRN.sumarSaldos(debitos, m.getMonedaRegistracion().getCodigo());

            if (totalDebe.compareTo(BigDecimal.ZERO) <= 0) {
                JsfUtil.addWarningMessage("Seleccione al menos un débito pendiente para aplicar");
                return event.getOldStep();
            }

            if (totalHaber.add(totalDebe.negate()).compareTo(BigDecimal.ZERO) > 0) {
                ajustarCreditoConDebito();
            }
        }

        if (event.getOldStep().equals("confirmacion")) {
//            guardar(false);
        }

        return event.getNewStep();
    }

    public void marcarCredito(ItemPendienteCuentaCorriente i) {

        if (i.isSeleccionado()) {

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
                i.setImporteAplicar(i.getPendiente());
            }

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
                i.setImporteAplicarSecundario(i.getPendienteSecundario());
                i.setImporteAplicar(i.getImporteAplicarSecundario().multiply(m.getCotizacion()).setScale(2, RoundingMode.HALF_UP));
            }

        } else {
            i.setImporteAplicar(BigDecimal.ZERO);
            i.setImporteAplicarSecundario(BigDecimal.ZERO);
        }
        totalHaber = cuentaCorrienteRN.sumarSaldos(creditos, m.getMonedaRegistracion().getCodigo());
    }

    public void modificaCredito(ItemPendienteCuentaCorriente i) {

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
            JsfUtil.addWarningMessage("El saldo pendiente que está modificando no está seleccionado");
        }

        totalHaber = cuentaCorrienteRN.sumarSaldos(creditos, m.getMonedaRegistracion().getCodigo());
    }

    public void modificaDebito(ItemPendienteCuentaCorriente i) {

        if (i.getImporteAplicar() == null) {
            i.setImporteAplicar(BigDecimal.ZERO);
        }

        if (i.isSeleccionado()) {

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {

                if (i.getPendiente().compareTo(i.getImporteAplicar()) < 0) {

                    i.setImporteAplicar(i.getPendiente());
                    BigDecimal disponible = totalHaber.add(totalDebe.negate());

                    if (i.getImporteAplicar().compareTo(disponible) > 0) {
                        i.setImporteAplicar(disponible);
                        JsfUtil.addWarningMessage("El importe a aplicar es mayor al pendiente");
                    }
                }
            }

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {

                i.setImporteAplicarSecundario(i.getPendienteSecundario());
                BigDecimal disponible = totalHaber.add(totalDebe.negate()).divide(m.getCotizacion(), 2, RoundingMode.HALF_UP);

                if (i.getImporteAplicarSecundario().compareTo(disponible) > 0) {
                    i.setImporteAplicarSecundario(disponible);
                    JsfUtil.addWarningMessage("El importe a aplicar es mayor al pendiente");
                }
                i.setImporteAplicar(i.getImporteAplicarSecundario().multiply(m.getCotizacion()).setScale(2, RoundingMode.HALF_UP));
            }

        } else {
            i.setImporteAplicar(BigDecimal.ZERO);
            i.setImporteAplicarSecundario(BigDecimal.ZERO);
            JsfUtil.addWarningMessage("El saldo pendiente que está modificando no está seleccionado");
        }

        totalDebe = cuentaCorrienteRN.sumarSaldos(debitos, m.getMonedaRegistracion().getCodigo());
    }

    public void marcarDebito(ItemPendienteCuentaCorriente i) {

        if (totalHaber.compareTo(BigDecimal.ZERO) <= 0) {
            JsfUtil.addWarningMessage("Primero debe seleccionar al ménos un crédito pendiente");
            i.setImporteAplicar(BigDecimal.ZERO);
            return;
        }

        if (i.isSeleccionado()) {

            BigDecimal disponible = totalHaber.add(totalDebe.negate());

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {

                if (i.getPendiente().compareTo(disponible) > 0) {
                    i.setImporteAplicar(disponible);
                } else {
                    i.setImporteAplicar(i.getPendiente());
                }
            }

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {

                if (i.getPendienteSecundario().compareTo(disponible) > 0) {
                    i.setImporteAplicarSecundario(disponible);
                } else {
                    i.setImporteAplicarSecundario(i.getPendienteSecundario());
                }
                i.setImporteAplicar(i.getImporteAplicarSecundario().multiply(m.getCotizacion()).setScale(2, RoundingMode.HALF_UP));
            }

        } else {
            i.setImporteAplicar(BigDecimal.ZERO);
            i.setImporteAplicarSecundario(BigDecimal.ZERO);
        }
        totalDebe = cuentaCorrienteRN.sumarSaldos(debitos, m.getMonedaRegistracion().getCodigo());
    }

    private void ajustarCreditoConDebito() {

        //Primero ponemos en cero todos los importes a aplicar de los créditos
        for (ItemPendienteCuentaCorriente ic : creditos) {
            ic.setImporteAplicar(BigDecimal.ZERO);
            ic.setImporteAplicarSecundario(BigDecimal.ZERO);
        }

        BigDecimal disponible = totalDebe;

        for (ItemPendienteCuentaCorriente ic : creditos) {
            if (ic.isSeleccionado()) {

                if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {

                    if (ic.getPendiente().compareTo(disponible) < 0) {
                        ic.setImporteAplicar(ic.getPendiente());
                    } else {
                        ic.setImporteAplicar(disponible);
                    }
                    disponible = disponible.add(ic.getImporteAplicar().negate());
                }

                if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {

                    if (ic.getPendienteSecundario().compareTo(disponible) < 0) {
                        ic.setImporteAplicarSecundario(ic.getPendienteSecundario());
                    } else {
                        ic.setImporteAplicarSecundario(disponible);
                    }
                    ic.setImporteAplicar(ic.getImporteAplicarSecundario().multiply(m.getCotizacion()).setScale(2, RoundingMode.HALF_UP));
                    disponible = disponible.add(ic.getImporteAplicarSecundario().negate());
                }
            }
        }
    }

    public void onChageFechaMovimiento(SelectEvent event) {
        m.setFechaVencimiento(m.getFechaMovimiento());
    }

    public void procesarCliente() {

        if (m != null && m.getCliente() != null) {

            EntidadComercial e = m.getCliente();

            m.setCliente(e);
            m.setClienteCuentaCorriente(e);
            m.setRazonSocial(e.getRazonSocial());
            m.setNrodoc(e.getNrodoc());
            m.setTipoDocumento(e.getTipoDocumento());

            m.setProvincia(e.getProvincia());
            m.setLocalidad(e.getLocalidad());
            m.setDireccion(e.getDireccion());

            m.setCondicionDeIva(e.getCondicionDeIva());
            m.setCondicionDePago(e.getCondicionDePagoVentas());
            m.setListaDePrecio(e.getListaDePrecioVenta());
            m.setMonedaListaPrecio(e.getListaDePrecioVenta().getMoneda());
            m.setVendedor(e.getVendedor());
            m.setCanalVenta(e.getCanalVenta());

            saldoActual = cuentaCorrienteRN.getSaldoActual(m.getCliente().getNrocta(), m.getMonedaRegistracion().getCodigo(), m.getComprobante().getComprobanteInterno());
            saldoActualSecundario = cuentaCorrienteRN.getSaldoSecundarioActual(m.getCliente().getNrocta(), m.getMonedaRegistracion().getCodigo(), m.getComprobante().getComprobanteInterno());

            debitos = cuentaCorrienteRN.getDebitosPendientes(m.getCliente().getNrocta(), m.getMonedaRegistracion().getCodigo(), m.getComprobante().getComprobanteInterno());
            creditos = cuentaCorrienteRN.getCreditosPendientes(m.getCliente().getNrocta(), m.getMonedaRegistracion().getCodigo(), m.getComprobante().getComprobanteInterno());

//            cuentaCorrienteRN.invertirImporteCreditos(creditos);
            if (creditos.isEmpty()) {
                JsfUtil.addWarningMessage("El cliente no tiene créditos con saldo pendiente para aplicar ");
            }

            if (debitos.isEmpty()) {
                JsfUtil.addWarningMessage("El cliente no tiene débitos con saldo pendiente para aplicar ");
            }
        }
    }

    public void imprimir() {

        try {

            if (m.getFormulario().getReporte() == null) {
                throw new ExcepcionGeneralSistema("No se puede imprimir - El formulario no tienen reporte asociado");
            }

            Map parameters = new HashMap();

            nombreArchivo = m.getComprobante().getCodigo() + "-" + m.getNumeroFormulario();

            parameters.put("ID", m.getId());
            parameters.put("CANT_COPIAS", m.getComprobante().getCopias());

            reportFactory.exportReportToPdfFile(m.getFormulario().getReporte(), nombreArchivo, parameters);
            muestraReporte = true;

        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage("No se encontró el archivo: " + m.getFormulario().getReporte().getPath());
            muestraReporte = false;
        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + (e.getMessage() == null ? e.getCause() : ""));
            muestraReporte = false;
        }
    }

    public void nuevaBusqueda() {

        if (m != null && m.getFormulario() != null) {
            formulario = m.getFormulario();
        }

        buscaMovimiento = true;
    }

    public void buscarMovimiento() {

        if (!validarParametros()) {
            return;
        }
        cargarFiltroBusqueda();

        movimientos = ventaRN.getListaByBusqueda(filtro, cantidadRegistros);

        if (movimientos == null || movimientos.isEmpty()) {
            JsfUtil.addWarningMessage("No se han encontrado movimientos");
        }
    }

    public boolean validarParametros() {

        if (formulario == null) {
            JsfUtil.addWarningMessage("Seleccione un formulario");
            return false;
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
            filtro.put("formulario.codigo = ", "'" + formulario.getCodigo() + "'");
        }

        if (cliente != null) {

            filtro.put("cliente.nrocta =", "'" + cliente.getNrocta() + "'");
        }

        if (numeroFormularioDesde != null) {

            filtro.put("numeroFormulario >=", String.valueOf(numeroFormularioDesde));
        }

        if (numeroFormularioHasta != null) {

            filtro.put("numeroFormulario <=", String.valueOf(numeroFormularioHasta));
        }

        if (fechaMovimientoDesde != null) {

            filtro.put("fechaMovimiento >= ", JsfUtil.getFechaSQL(fechaMovimientoDesde));
        }

        if (fechaMovimientoHasta != null) {

            filtro.put("fechaMovimiento <= ", JsfUtil.getFechaSQL(fechaMovimientoHasta));
        }
    }

    public void seleccionarMovimiento(MovimientoVenta mSel) {

        if (m.getId() == mSel.getId()) {
            return;
        }

        m = mSel;
        buscaMovimiento = false;
    }

    public void resetParametros() {

        cliente = null;
        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
        fechaMovimientoDesde = null;
        fechaMovimientoHasta = null;
        muestraReporte = false;
        solicitaEmail = false;
        movimientos = null;
    }

    //_---------------------------------------------------------------------------------
    public BigDecimal getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(BigDecimal saldoActual) {
        this.saldoActual = saldoActual;
    }

    public List<ItemPendienteCuentaCorriente> getDebitos() {
        return debitos;
    }

    public void setDebitos(List<ItemPendienteCuentaCorriente> debitos) {
        this.debitos = debitos;
    }

    public List<ItemPendienteCuentaCorriente> getCreditos() {
        return creditos;
    }

    public void setCreditos(List<ItemPendienteCuentaCorriente> creditos) {
        this.creditos = creditos;
    }

    public EntidadComercialBean getEntidadComercialBean() {
        return entidadComercialBean;
    }

    public void setEntidadComercialBean(EntidadComercialBean entidadComercialBean) {
        this.entidadComercialBean = entidadComercialBean;
    }

    public MovimientoVenta getM() {
        return m;
    }

    public void setM(MovimientoVenta m) {
        this.m = m;
    }

    public BigDecimal getTotalDebe() {
        return totalDebe;
    }

    public void setTotalDebe(BigDecimal totalDebe) {
        this.totalDebe = totalDebe;
    }

    public BigDecimal getTotalHaber() {
        return totalHaber;
    }

    public void setTotalHaber(BigDecimal totalHaber) {
        this.totalHaber = totalHaber;
    }

    public ReportFactory getReportFactory() {
        return reportFactory;
    }

    public void setReportFactory(ReportFactory reportFactory) {
        this.reportFactory = reportFactory;
    }

    public String getSUCVT() {
        return SUCVT;
    }

    public void setSUCVT(String SUCVT) {
        this.SUCVT = SUCVT;
    }

    public String getMODVT() {
        return MODVT;
    }

    public void setMODVT(String MODVT) {
        this.MODVT = MODVT;
    }

    public String getCODVT() {
        return CODVT;
    }

    public void setCODVT(String CODVT) {
        this.CODVT = CODVT;
    }

    public List<MovimientoVenta> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoVenta> movimientos) {
        this.movimientos = movimientos;
    }

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
    }

    public MovimientoVentaRN getVentaRN() {
        return ventaRN;
    }

    public void setVentaRN(MovimientoVentaRN ventaRN) {
        this.ventaRN = ventaRN;
    }

    public CuentaCorrienteRN getCuentaCorrienteRN() {
        return cuentaCorrienteRN;
    }

    public void setCuentaCorrienteRN(CuentaCorrienteRN cuentaCorrienteRN) {
        this.cuentaCorrienteRN = cuentaCorrienteRN;
    }

    public String getCodigoMonedaRegistracion() {
        return codigoMonedaRegistracion;
    }

    public void setCodigoMonedaRegistracion(String codigoMonedaRegistracion) {
        this.codigoMonedaRegistracion = codigoMonedaRegistracion;
    }

    public BigDecimal getSaldoActualSecundario() {
        return saldoActualSecundario;
    }

    public void setSaldoActualSecundario(BigDecimal saldoActualSecundario) {
        this.saldoActualSecundario = saldoActualSecundario;
    }
}
