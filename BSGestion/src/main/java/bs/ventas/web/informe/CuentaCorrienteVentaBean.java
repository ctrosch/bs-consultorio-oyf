/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.web.informe;

import bs.entidad.modelo.EntidadComercial;
import bs.entidad.rn.EntidadRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.rn.FormularioRN;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
import bs.ventas.modelo.ItemHistoricoCuentaCorriente;
import bs.ventas.modelo.ItemPendienteCuentaCorriente;
import bs.ventas.modelo.MovimientoVenta;
import bs.ventas.rn.CuentaCorrienteRN;
import bs.ventas.rn.MovimientoVentaRN;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class CuentaCorrienteVentaBean extends InformeBase implements Serializable {

    @EJB
    private CuentaCorrienteRN cuentaCorrienteRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private MovimientoVentaRN ventaRN;
    @EJB
    private EntidadRN entidadRN;

    private Date fechaMinima;

    private EntidadComercial cliente;
    private String NROCTA;

    private BigDecimal saldoActual;
    private BigDecimal saldoActualSecundario;
    private int cantVencidos;
    private int cantVencer;
    private List<ItemPendienteCuentaCorriente> debitos;
    private List<ItemPendienteCuentaCorriente> creditos;
    private List<ItemHistoricoCuentaCorriente> historial;
    private String codigoMonedaRegistracion;
    private String comprobanteInterno;

    /**
     * Creates a new instance of CuentaCorrienteBean
     */
    public CuentaCorrienteVentaBean() {

    }

    @PostConstruct
    @Override
    public void init() {

        muestraReporte = false;

        fechaDesde = new Date();
        fechaHasta = new Date();
        fechaMinima = getFechaMinima();

        saldoActual = BigDecimal.ZERO;
        saldoActualSecundario = BigDecimal.ZERO;
        debitos = new ArrayList<>();
        creditos = new ArrayList<>();
        historial = new ArrayList<>();

        codigoMonedaRegistracion = parametrosRN.getParametro().getCodigoMonedaPrimaria();

    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (NROCTA != null && !NROCTA.isEmpty()) {
                    cliente = entidadRN.getEntidad(NROCTA);
                    procesarCliente();
                }

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public Date getFechaMinima() {

        Calendar cal = Calendar.getInstance();
        cal.set(1900, 1, 1);
        return cal.getTime();
    }

    public Date getFechaMaxima() {

        return new Date();
    }

    public void consultarHistorial() {

        if (cliente == null) {
            JsfUtil.addErrorMessage("Seleccione un cliente");
            return;
        }

        if (fechaHasta.before(fechaDesde)) {
            JsfUtil.addWarningMessage("tablaHistorial", "Atención", "Fecha desde tiene que ser mayor o igual a fecha Hasta");
            return;
        }

        historial = cuentaCorrienteRN.getHistoricoMovimientos(cliente.getNrocta(), codigoMonedaRegistracion, comprobanteInterno, fechaDesde, fechaHasta);

        //Inicializamos el saldo anterior en cero
        ItemHistoricoCuentaCorriente SaldoAnterior = cuentaCorrienteRN.getSaldoAnterior(cliente.getNrocta(), codigoMonedaRegistracion, comprobanteInterno, fechaDesde);
        historial.add(0, SaldoAnterior);

        BigDecimal saldo = SaldoAnterior.getSaldo();
        BigDecimal saldoSecundario = SaldoAnterior.getSaldoSecundario();
        for (ItemHistoricoCuentaCorriente i : historial) {

            if (!i.getCodfor().equals("SA")) {

                i.setSaldo(saldo.add(i.getDebe().add(i.getHaber().negate())));
                i.setSaldoSecundario(saldoSecundario.add(i.getDebeSecundario().add(i.getHaberSecundario().negate())));
                saldo = i.getSaldo();
                saldoSecundario = i.getSaldoSecundario();
            }
        }

        if (historial.size() == 1) {
            JsfUtil.addWarningMessage("tablaHistorial", "Atención", "No se encontraron movimientos en el período seleccionado");
        }
    }

    public void imprimir(String codfor, int nrofor) {

        try {

            formulario = formularioRN.getFormulario("VT", codfor);

            if (formulario == null) {
                JsfUtil.addErrorMessage("No se puede imprimir - No se encontró formulario");
                return;
            }

            if (formulario.getReporte() == null) {
                JsfUtil.addErrorMessage("No se puede imprimir - El formulario no tienen reporte asociado");
                return;
            }
            parameters = new HashMap();
            nombreArchivo = codfor + "-" + nrofor;

            MovimientoVenta mv = ventaRN.getMovimiento(codfor, nrofor);

            parameters.put("ID", mv.getId());
            parameters.put("CODIGO_BARRA", ventaRN.generarCodigoBarra(mv));
            parameters.put("EN_LETRAS", "Son pesos " + JsfUtil.generarEnLetras(mv.getItemTotal().get(0).getImporte()));
            parameters.put("CANT_COPIAS", 1);

            reportFactory.exportReportToPdfFile(formulario.getReporte(), nombreArchivo, parameters);
            muestraReporte = true;

            PrimeFaces.current().ajax().update("formulario");
            PrimeFaces.current().executeScript("PF('dlg_reporte').show()");

        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage("No se encontró el archivo: " + nombreArchivo + ".jasper");
            muestraReporte = false;
            PrimeFaces.current().ajax().update("formulario");

        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e.getMessage());
            muestraReporte = false;
            PrimeFaces.current().ajax().update("formulario");
        }
    }

    public void procesarCliente() {

        if (cliente != null) {

            saldoActual = cuentaCorrienteRN.getSaldoActual(cliente.getNrocta(), codigoMonedaRegistracion, comprobanteInterno);
            saldoActualSecundario = cuentaCorrienteRN.getSaldoSecundarioActual(cliente.getNrocta(), codigoMonedaRegistracion, comprobanteInterno);
            debitos = cuentaCorrienteRN.getDebitosPendientes(cliente.getNrocta(), codigoMonedaRegistracion, comprobanteInterno);
            creditos = cuentaCorrienteRN.getCreditosPendientes(cliente.getNrocta(), codigoMonedaRegistracion, comprobanteInterno);

            calcularVencidos(debitos);

            muestraReporte = false;
            todoOk = false;

            consultarHistorial();
        }
    }

    public void cambiarMoneda() {

        procesarCliente();
    }

    public void cambiarComprobanteInterno() {

        procesarCliente();
    }

    private void calcularVencidos(List<ItemPendienteCuentaCorriente> items) {

        cantVencer = 0;
        cantVencidos = 0;

        for (ItemPendienteCuentaCorriente icc : items) {

            if (icc.getFechaVencimiento().before(new Date())) {
                cantVencidos++;
            } else {
                cantVencer++;
            }
        }
    }

    public void onClienteSelect(SelectEvent event) {

        cliente = (EntidadComercial) event.getObject();

        if (cliente == null) {
            return;
        }

        saldoActual = cuentaCorrienteRN.getSaldoActual(cliente.getNrocta(), codigoMonedaRegistracion, comprobanteInterno);
        saldoActualSecundario = cuentaCorrienteRN.getSaldoSecundarioActual(cliente.getNrocta(), codigoMonedaRegistracion, comprobanteInterno);
        debitos = cuentaCorrienteRN.getDebitosPendientes(cliente.getNrocta(), codigoMonedaRegistracion, null);
        creditos = cuentaCorrienteRN.getCreditosPendientes(cliente.getNrocta(), codigoMonedaRegistracion, null);

        muestraReporte = false;

//        consultarHistorial();
    }

    public void postProcessXLS(Object document) {

        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow header = sheet.getRow(0);

        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.ORANGE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
            HSSFCell cell = header.getCell(i);
            cell.setCellStyle(cellStyle);
        }
    }

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
    }

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

    public List<ItemHistoricoCuentaCorriente> getHistorial() {
        return historial;
    }

    public void setHistorial(List<ItemHistoricoCuentaCorriente> historial) {
        this.historial = historial;
    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetParametros() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getCantVencidos() {
        return cantVencidos;
    }

    public void setCantVencidos(int cantVencidos) {
        this.cantVencidos = cantVencidos;
    }

    public int getCantVencer() {
        return cantVencer;
    }

    public void setCantVencer(int cantVencer) {
        this.cantVencer = cantVencer;
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

    public String getComprobanteInterno() {
        return comprobanteInterno;
    }

    public void setComprobanteInterno(String comprobanteInterno) {
        this.comprobanteInterno = comprobanteInterno;
    }

    public String getNROCTA() {
        return NROCTA;
    }

    public void setNROCTA(String NROCTA) {
        this.NROCTA = NROCTA;
    }

}
