/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.web;

import bs.entidad.modelo.EntidadComercial;
import bs.global.util.JsfUtil;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.ventas.modelo.ItemPendienteCuentaCorriente;
import bs.ventas.modelo.Repartidor;
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
public class RendicionBean extends MovimientoTesoreriaBean {

    protected String SUCREN = "";
    protected String CODREN = "";

    @EJB
    private CuentaCorrienteRN cuentaCorrienteRN;

    private List<EntidadComercial> clientes;
    private List<ItemPendienteCuentaCorriente> debitos;
    private BigDecimal totalDebe;

    private Repartidor repartidor;

    /**
     * Creates a new instance of CajaBean
     */
    public RendicionBean() {

    }

    @Override
    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                totalDebe = BigDecimal.ZERO;

                super.iniciar();

                if (SUCCJ == null) {
                    SUCCJ = "";
                }

                if (CODCJ == null) {
                    CODCJ = "";
                }

                if (CODVT == null) {
                    CODVT = "";
                }

                if (CODPV == null) {
                    CODPV = "";
                }

                if (CODPR == null) {
                    CODPR = "";
                }

                if (CTAVAL == null) {
                    CTAVAL = "";
                }

                iniciarMovimiento();

                beanIniciado = true;

            }
        } catch (Exception e) {

            JsfUtil.addErrorMessage("Error al iniciar el bean " + e.getMessage());
        }
    }

    @Override
    public void iniciarMovimiento() {

        try {

            nombreArchivo = "";
            buscaMovimiento = false;
            muestraReporte = false;
            mBusqueda = null;
            mReversion = null;

            m = tesoreriaRN.nuevoMovimiento(CODREN, "", "", "", "", SUCREN);
            esNuevo = true;

            if (m.getComprobante().getTipoComprobante().equals("T") && m.getComprobante().getRegisracionManual().equals("A")) {
                seleccionaPendiente = true;
                JsfUtil.addInfoMessage("Seleccione la cuenta de tesoreria de egreso");
            }

            super.cargarFormulariosBusqueda();

        } catch (Exception ex) {
            ex.printStackTrace();
            JsfUtil.addErrorMessage("ERROR iniciarMovimiento" + ex);
        }
    }

    @Override
    public void nuevo() {

        m = null;
        iniciarMovimiento();
        clientes.clear();
        repartidor = null;
        esNuevo = true;
        modoVista = "D";
    }

    @Override
    public void guardar(boolean nuevo) {

        try {

            if (!puedoGuardar()) {
                return;
            }

            m = tesoreriaRN.generarMovimientosRendicion(m, clientes, CODCJ, CODVT, SUCCJ);
            m = tesoreriaRN.guardar(m);

            JsfUtil.addInfoMessage("El documento " + m.getFormulario().getDescripcion() + "-" + m.getNumeroFormulario() + " se guardó correctamente", "");
            muestraReporte = false;
            esNuevo = false;

            if (nuevo) {
                nuevo();
            }

        } catch (Exception ex) {
            JsfUtil.addErrorMessage("No es posible guardar el comprobante " + ex.getMessage());
        }

        if (m.getId() != null) {

            totalDebe = BigDecimal.ZERO;
            debitos = new ArrayList<ItemPendienteCuentaCorriente>();
        }
    }

    @Override
    public boolean puedoGuardar() {

        boolean todoOK = true;

        if (!super.puedoGuardar()) {
            todoOK = false;
        }

        if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {

            if (!m.isEsAnticipo() && totalDebe.compareTo(m.getImporteTotalDebe()) != 0) {
                JsfUtil.addErrorMessage("El importe a aplicar es diferente a la suma de los conceptos ingresados");
                todoOK = false;
            }
        }

        if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
            if (!m.isEsAnticipo() && totalDebe.compareTo(m.getImporteTotalDebeSecundario()) != 0) {
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

    public void buscarPendientes() {

        cargarFiltro();

        clientes = cuentaCorrienteRN.getClientesConSaldos(filtro, m.getMonedaRegistracion().getCodigo(), m.getComprobante().getComprobanteInterno());

        if (clientes != null) {
            for (EntidadComercial e : clientes) {
                e.setSaldos(cuentaCorrienteRN.getDebitosPendientes(e.getNrocta(), m.getMonedaRegistracion().getCodigo(), m.getComprobante().getComprobanteInterno()));
            }
        }

        sumarSaldos();
    }

    public void cargarFiltro() {

        filtro.clear();

        if (repartidor != null) {
            filtro.put("movimiento.repartidor.codigo = ", "'" + repartidor.getCodigo() + "'");
        }

    }

    public void modificaSaldo(ItemPendienteCuentaCorriente i) {

        if (i.getImporteAplicar() == null) {
            i.setImporteAplicar(BigDecimal.ZERO);
        }

        if (i.isSeleccionado()) {

            if (i.getImporteAplicar().compareTo(i.getPendiente()) > 0) {
                i.setImporteAplicar(i.getPendiente());
                JsfUtil.addErrorMessage("El importe a aplicar es mayor al pendiente");
            }

        } else {
            i.setImporteAplicar(BigDecimal.ZERO);
            i.setImporteAplicarSecundario(BigDecimal.ZERO);
            JsfUtil.addErrorMessage("El saldo pendiente que está modificando no está seleccionado");
        }

        sumarSaldos();
    }

    public void marcarSaldo(ItemPendienteCuentaCorriente i) {

        if (i.isSeleccionado()) {

            i.setImporteAplicar(i.getPendiente());
            i.setImporteAplicarSecundario(i.getImporteAplicar().divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));

        } else {

            i.setImporteAplicar(BigDecimal.ZERO);
            i.setImporteAplicarSecundario(BigDecimal.ZERO);
        }

        sumarSaldos();

    }

    public void sumarSaldos() {

        totalDebe = BigDecimal.ZERO;

        if (clientes != null) {
            for (EntidadComercial e : clientes) {
                totalDebe = totalDebe.add(cuentaCorrienteRN.sumarSaldos(e.getSaldos(), m.getMonedaRegistracion().getCodigo()));
            }
        }
    }

    @Override
    public void asignarTotal(ItemMovimientoTesoreria item) {

        if (item.getDebeHaber().equals("D")) {
            m.getItemsDebe().forEach(i -> i.setImporte(BigDecimal.ZERO));
        } else {
            m.getItemsHaber().forEach(i -> i.setImporte(BigDecimal.ZERO));
        }

        item.setImporte(totalDebe);
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

    public List<EntidadComercial> getClientes() {
        return clientes;
    }

    public void setClientes(List<EntidadComercial> clientes) {
        this.clientes = clientes;
    }

    public String getSUCREN() {
        return SUCREN;
    }

    public void setSUCREN(String SUCREN) {
        this.SUCREN = SUCREN;
    }

    public String getCODREN() {
        return CODREN;
    }

    public void setCODREN(String CODREN) {
        this.CODREN = CODREN;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
    }

}
