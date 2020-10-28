/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.web;

import bs.entidad.modelo.EntidadComercial;
import bs.entidad.web.EntidadComercialBean;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.util.JsfUtil;
import bs.global.util.ReportFactory;
import bs.global.web.GenericBean;
import bs.proveedores.modelo.ItemPendienteCuentaCorrienteProveedor;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.proveedores.rn.CuentaCorrienteProveedorRN;
import bs.proveedores.rn.MovimientoProveedorRN;
import java.io.Serializable;
import java.math.BigDecimal;
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
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class AplicacionCuentaCorrienteProveedorBean extends GenericBean implements Serializable {

    @EJB
    private MovimientoProveedorRN compraRN;
    @EJB
    private CuentaCorrienteProveedorRN cuentaCorrienteRN;

    @ManagedProperty(value = "#{entidadComercialBean}")
    protected EntidadComercialBean entidadComercialBean;

    @ManagedProperty(value = "#{reportFactory}")
    protected ReportFactory reportFactory;

    protected String SUCPV = "";
    protected String MODPV = "";
    protected String CODPV = "";

    private MovimientoProveedor m;
    protected List<MovimientoProveedor> movimientos;
    protected EntidadComercial proveedor;

    private BigDecimal saldoActual;
    private BigDecimal saldoActualSecundario;
    private BigDecimal totalDebe;
    private BigDecimal totalHaber;
    private List<ItemPendienteCuentaCorrienteProveedor> debitos;
    private List<ItemPendienteCuentaCorrienteProveedor> creditos;

    /**
     * Creates a new instance of CuentaCorrienteBean
     */
    public AplicacionCuentaCorrienteProveedorBean() {

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                nuevoMovimiento();
                beanIniciado = true;
            }
        } catch (Exception e) {
//            e.printStackTrace();
            JsfUtil.addErrorMessage("Error al iniciar el bean " + e.getMessage());
        }
    }

    public void nuevoMovimiento() {

        try {
            m = compraRN.nuevoMovimiento(CODPV, SUCPV, "X");
            saldoActual = BigDecimal.ZERO;
            saldoActualSecundario = BigDecimal.ZERO;

            totalDebe = BigDecimal.ZERO;
            totalHaber = BigDecimal.ZERO;

            debitos = new ArrayList<ItemPendienteCuentaCorrienteProveedor>();
            creditos = new ArrayList<ItemPendienteCuentaCorrienteProveedor>();

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
            compraRN.guardar(m);
            JsfUtil.addInfoMessage("El documento " + m.getComprobante().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");

            if (nuevo) {
                nuevoMovimiento();
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible guardar el comprobante: " + ex);
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

            if (m.getProveedor() == null) {
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

            totalHaber = cuentaCorrienteRN.sumarSaldos(creditos);

            if (totalHaber.compareTo(BigDecimal.ZERO) <= 0) {
                JsfUtil.addWarningMessage("Seleccione al menos un crédito pendiente para continuar");
                return event.getOldStep();
            }
        }

        if (event.getNewStep().equals("debitos")) {
            JsfUtil.addInfoMessage("Seleccione los saldos pendientes a aplicar");
        }

        if (event.getOldStep().equals("debitos") && event.getNewStep().equals("confirmacion")) {

            totalDebe = cuentaCorrienteRN.sumarSaldos(debitos);

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

    public void marcarCredito(ItemPendienteCuentaCorrienteProveedor i) {

        if (i.isSeleccionado()) {
            i.setImporteAplicar(i.getPendiente());
        } else {
            i.setImporteAplicar(BigDecimal.ZERO);
        }
        totalHaber = cuentaCorrienteRN.sumarSaldos(creditos);
    }

    public void modificaCredito(ItemPendienteCuentaCorrienteProveedor i) {

        if (i.getImporteAplicar() == null) {
            i.setImporteAplicar(BigDecimal.ZERO);
        }

        if (i.isSeleccionado()) {
            if (i.getImporteAplicar().compareTo(i.getPendiente()) > 0) {
                i.setImporteAplicar(i.getPendiente());
                JsfUtil.addWarningMessage("El importe a aplicar es mayor al pendiente");
            }
        } else {
            i.setImporteAplicar(BigDecimal.ZERO);
            JsfUtil.addWarningMessage("El saldo pendiente que está modificando no está seleccionado");
        }

        totalHaber = cuentaCorrienteRN.sumarSaldos(creditos);
    }

    public void modificaDebito(ItemPendienteCuentaCorrienteProveedor i) {

        if (i.getImporteAplicar() == null) {
            i.setImporteAplicar(BigDecimal.ZERO);
        }

        if (i.isSeleccionado()) {
            if (i.getPendiente().compareTo(i.getImporteAplicar()) < 0) {

                i.setImporteAplicar(i.getPendiente());

                BigDecimal disponible = totalHaber.add(totalDebe.negate());

                if (i.getImporteAplicar().compareTo(disponible) > 0) {
                    i.setImporteAplicar(disponible);
                }

                JsfUtil.addWarningMessage("El importe a aplicar es mayor al pendiente");
            }
        } else {
            i.setImporteAplicar(BigDecimal.ZERO);
            JsfUtil.addWarningMessage("El saldo pendiente que está modificando no está seleccionado");
        }

        totalDebe = cuentaCorrienteRN.sumarSaldos(debitos);
    }

    public void marcarDebito(ItemPendienteCuentaCorrienteProveedor i) {

        if (totalHaber.compareTo(BigDecimal.ZERO) <= 0) {
            JsfUtil.addWarningMessage("Primero debe seleccionar al ménos un crédito pendiente");
            i.setImporteAplicar(BigDecimal.ZERO);
            return;
        }

        if (i.isSeleccionado()) {

            BigDecimal disponible = totalHaber.add(totalDebe.negate());

            if (i.getPendiente().compareTo(disponible) > 0) {
                i.setImporteAplicar(disponible);
            } else {
                i.setImporteAplicar(i.getPendiente());
            }
        } else {
            i.setImporteAplicar(BigDecimal.ZERO);
        }
        totalDebe = cuentaCorrienteRN.sumarSaldos(debitos);
    }

    private void ajustarCreditoConDebito() {

        //Primero ponemos en cero todos los importes a aplicar de los créditos
        for (ItemPendienteCuentaCorrienteProveedor ic : creditos) {
            ic.setImporteAplicar(BigDecimal.ZERO);
        }

        BigDecimal disponible = totalDebe;

        for (ItemPendienteCuentaCorrienteProveedor ic : creditos) {
            if (ic.isSeleccionado()) {

                if (ic.getPendiente().compareTo(disponible) < 0) {
                    ic.setImporteAplicar(ic.getPendiente());
                } else {
                    ic.setImporteAplicar(disponible);
                }
                disponible = disponible.add(ic.getImporteAplicar().negate());
            }
        }
    }

    public void onChageFechaMovimiento(SelectEvent event) {
        m.setFechaVencimiento(m.getFechaMovimiento());
    }

    public void procesarProveedor() {

        if (m != null && m.getProveedor() != null) {

            EntidadComercial e = m.getProveedor();

            m.setProveedor(e);
            m.setProveedorCuentaCorriente(e);
            m.setRazonSocial(e.getRazonSocial());
            m.setNrodoc(e.getNrodoc());
            m.setTipoDocumento(e.getTipoDocumento());

            m.setProvincia(e.getProvincia());
            m.setLocalidad(e.getLocalidad());
            m.setBarrio(e.getBarrio());
            m.setDireccion(e.getDireccion());
            m.setNumero(e.getNumero());
            m.setDepartamentoPiso(e.getDepartamentoPiso());
            m.setDepartamentoNumero(e.getDepartamentoNumero());

            m.setCondicionDeIva(e.getCondicionDeIva());
            m.setCondicionDePago(e.getCondicionPagoProveedor());
            m.setListaCosto(e.getListaCosto());
            m.setMonedaListaCosto(e.getListaDePrecioVenta().getMoneda());
            m.setComprador(e.getComprador());

            saldoActual = cuentaCorrienteRN.getSaldoActual(m.getProveedor().getNrocta(), m.getMonedaRegistracion().getCodigo(), m.getComprobante().getComprobanteInterno());
            saldoActualSecundario = cuentaCorrienteRN.getSaldoSecundarioActual(m.getProveedor().getNrocta(), m.getMonedaRegistracion().getCodigo(), m.getComprobante().getComprobanteInterno());
            debitos = cuentaCorrienteRN.getDebitosPendientes(m.getProveedor().getNrocta(), m.getMonedaRegistracion().getCodigo(), m.getComprobante().getComprobanteInterno());
            creditos = cuentaCorrienteRN.getCreditosPendientes(m.getProveedor().getNrocta(), m.getMonedaRegistracion().getCodigo(), m.getComprobante().getComprobanteInterno());

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
        } catch (ExcepcionGeneralSistema e) {
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e);
            muestraReporte = false;
        } catch (JRException e) {
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e);
            muestraReporte = false;
        } catch (Exception e) {
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e);
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

        movimientos = compraRN.getListaByBusqueda(filtro, cantidadRegistros);

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

        if (proveedor != null) {

            filtro.put("cliente.nrocta =", "'" + proveedor.getNrocta() + "'");
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

    public void seleccionarMovimiento(MovimientoProveedor mSel) {

        if (m.getId() == mSel.getId()) {
            return;
        }

        m = mSel;
        buscaMovimiento = false;
    }

    public void resetParametros() {

        proveedor = null;
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

    public List<ItemPendienteCuentaCorrienteProveedor> getDebitos() {
        return debitos;
    }

    public void setDebitos(List<ItemPendienteCuentaCorrienteProveedor> debitos) {
        this.debitos = debitos;
    }

    public List<ItemPendienteCuentaCorrienteProveedor> getCreditos() {
        return creditos;
    }

    public void setCreditos(List<ItemPendienteCuentaCorrienteProveedor> creditos) {
        this.creditos = creditos;
    }

    public EntidadComercialBean getEntidadComercialBean() {
        return entidadComercialBean;
    }

    public void setEntidadComercialBean(EntidadComercialBean entidadComercialBean) {
        this.entidadComercialBean = entidadComercialBean;
    }

    public MovimientoProveedor getM() {
        return m;
    }

    public void setM(MovimientoProveedor m) {
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

    public String getSUCPV() {
        return SUCPV;
    }

    public void setSUCPV(String SUCPV) {
        this.SUCPV = SUCPV;
    }

    public String getMODPV() {
        return MODPV;
    }

    public void setMODPV(String MODPV) {
        this.MODPV = MODPV;
    }

    public String getCODPV() {
        return CODPV;
    }

    public void setCODPV(String CODPV) {
        this.CODPV = CODPV;
    }

    public List<MovimientoProveedor> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoProveedor> movimientos) {
        this.movimientos = movimientos;
    }

    public EntidadComercial getProveedor() {
        return proveedor;
    }

    public void setProveedor(EntidadComercial proveedor) {
        this.proveedor = proveedor;
    }

    public BigDecimal getSaldoActualSecundario() {
        return saldoActualSecundario;
    }

    public void setSaldoActualSecundario(BigDecimal saldoActualSecundario) {
        this.saldoActualSecundario = saldoActualSecundario;
    }

}
