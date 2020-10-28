/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.web.informe;

import bs.entidad.modelo.EntidadComercial;
import bs.entidad.rn.EntidadRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.rn.FormularioRN;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
import bs.proveedores.modelo.ItemHistoricoCuentaCorrienteProveedor;
import bs.proveedores.modelo.ItemPendienteCuentaCorrienteProveedor;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.proveedores.rn.CuentaCorrienteProveedorRN;
import bs.proveedores.rn.MovimientoProveedorRN;
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

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class CuentaCorrienteProveedorBean extends InformeBase implements Serializable {

    @EJB
    private CuentaCorrienteProveedorRN cuentaCorrienteRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private MovimientoProveedorRN proveedorRN;
    @EJB
    private EntidadRN entidadRN;

    private EntidadComercial proveedor;
    private String NROCTA;

    private BigDecimal saldoActual;
    private BigDecimal saldoActualSecundario;
    private int cantVencidos;
    private int cantVencer;
    private List<ItemPendienteCuentaCorrienteProveedor> debitos;
    private List<ItemPendienteCuentaCorrienteProveedor> creditos;
    private List<ItemHistoricoCuentaCorrienteProveedor> historial;
    private String codigoMonedaRegistracion;
    private String comprobanteInterno;

    /**
     * Creates a new instance of CuentaCorrienteBean
     */
    public CuentaCorrienteProveedorBean() {

    }

    @PostConstruct
    @Override
    public void init() {

        muestraReporte = false;

        fechaDesde = new Date();
        fechaHasta = new Date();

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
                    proveedor = entidadRN.getEntidad(NROCTA);
                    procesarProveedor();
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

        if (proveedor == null) {
            JsfUtil.addErrorMessage("Seleccione un proveedor");
            return;
        }

        if (fechaHasta.before(fechaDesde)) {
            JsfUtil.addWarningMessage("tablaHistorial", "Atención", "Fecha desde tiene que ser mayor o igual a fecha Hasta");
            return;
        }

        historial = cuentaCorrienteRN.getHistoricoMovimientos(proveedor.getNrocta(), codigoMonedaRegistracion, comprobanteInterno, fechaDesde, fechaHasta);

        //Inicializamos el saldo anterior en cero
        ItemHistoricoCuentaCorrienteProveedor SaldoAnterior = cuentaCorrienteRN.getSaldoAnterior(proveedor.getNrocta(), codigoMonedaRegistracion, comprobanteInterno, fechaDesde);
        historial.add(0, SaldoAnterior);

        BigDecimal saldo = SaldoAnterior.getSaldo();
        for (ItemHistoricoCuentaCorrienteProveedor i : historial) {

            if (!i.getCodfor().equals("SA")) {
                i.setSaldo(saldo.add(i.getDebe().add(i.getHaber().negate())));
                saldo = i.getSaldo();
            }
        }

        if (historial.size() == 1) {
            JsfUtil.addWarningMessage("tablaHistorial", "Atención", "No se encontraron movimientos en el período seleccionado");
        }
    }

    public void imprimir(String codfor, int nrofor) {

        try {

            formulario = formularioRN.getFormulario("PV", codfor);

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

            MovimientoProveedor mp = proveedorRN.getMovimiento(codfor, nrofor);

            parameters.put("ID", mp.getId());
            parameters.put("CANT_COPIAS", 1);

            reportFactory.exportReportToPdfFile(formulario.getReporte(), nombreArchivo, parameters);
            muestraReporte = true;

        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage("No se encontró el archivo: " + nombreArchivo + ".jasper");
            muestraReporte = false;

        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("No se puede imprimir a pdf " + e.getMessage());
            muestraReporte = false;
        }
    }

    public void procesarProveedor() {

        if (proveedor != null) {

            saldoActual = cuentaCorrienteRN.getSaldoActual(proveedor.getNrocta(), codigoMonedaRegistracion, comprobanteInterno);
            saldoActualSecundario = cuentaCorrienteRN.getSaldoSecundarioActual(proveedor.getNrocta(), codigoMonedaRegistracion, comprobanteInterno);
            debitos = cuentaCorrienteRN.getDebitosPendientes(proveedor.getNrocta(), codigoMonedaRegistracion, comprobanteInterno);
            creditos = cuentaCorrienteRN.getCreditosPendientes(proveedor.getNrocta(), codigoMonedaRegistracion, comprobanteInterno);

            calcularVencidos(creditos);

            muestraReporte = false;
            todoOk = false;

            consultarHistorial();
        }
    }

    public void cambiarMoneda() {

        procesarProveedor();

    }

    public void cambiarComprobanteInterno() {

        procesarProveedor();

    }

    private void calcularVencidos(List<ItemPendienteCuentaCorrienteProveedor> items) {

        cantVencer = 0;
        cantVencidos = 0;

        for (ItemPendienteCuentaCorrienteProveedor icc : items) {

            if (icc.getFechaVencimiento().before(new Date())) {
                cantVencidos++;
            } else {
                cantVencer++;
            }
        }
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

    public BigDecimal getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(BigDecimal saldoActual) {
        this.saldoActual = saldoActual;
    }

    public EntidadComercial getProveedor() {
        return proveedor;
    }

    public void setProveedor(EntidadComercial proveedor) {
        this.proveedor = proveedor;
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

    public List<ItemHistoricoCuentaCorrienteProveedor> getHistorial() {
        return historial;
    }

    public void setHistorial(List<ItemHistoricoCuentaCorrienteProveedor> historial) {
        this.historial = historial;
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

    public BigDecimal getSaldoActualSecundario() {
        return saldoActualSecundario;
    }

    public void setSaldoActualSecundario(BigDecimal saldoActualSecundario) {
        this.saldoActualSecundario = saldoActualSecundario;
    }

    public String getCodigoMonedaRegistracion() {
        return codigoMonedaRegistracion;
    }

    public void setCodigoMonedaRegistracion(String codigoMonedaRegistracion) {
        this.codigoMonedaRegistracion = codigoMonedaRegistracion;
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
