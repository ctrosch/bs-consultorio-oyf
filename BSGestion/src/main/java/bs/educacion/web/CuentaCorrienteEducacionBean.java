/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.web;

import bs.educacion.modelo.ItemHistoricoCuentaCorrienteEducacion;
import bs.educacion.modelo.ItemPendienteCuentaCorrienteEducacion;
import bs.educacion.rn.CuentaCorrienteEducacionRN;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.rn.EntidadRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.rn.FormularioRN;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
import bs.global.util.UtilFechas;
import bs.ventas.modelo.MovimientoVenta;
import bs.ventas.rn.MovimientoVentaRN;
import java.io.Serializable;
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
public class CuentaCorrienteEducacionBean extends InformeBase implements Serializable {

    @EJB
    private CuentaCorrienteEducacionRN cuentaCorrienteRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private MovimientoVentaRN ventaRN;
    @EJB
    private EntidadRN entidadRN;

    private Date fechaMinima;

    private EntidadComercial alumno;
    private String NROCTA;

    private double saldoActual;
    private double saldoActualSecundario;
    private int cantVencidos;
    private int cantVencer;
    private List<ItemPendienteCuentaCorrienteEducacion> debitos;
    private List<ItemPendienteCuentaCorrienteEducacion> creditos;
    private List<ItemHistoricoCuentaCorrienteEducacion> historial;
    private String codigoMonedaRegistracion;
    private String comprobanteInterno;

    /**
     * Creates a new instance of CuentaCorrienteBean
     */
    public CuentaCorrienteEducacionBean() {

    }

    @PostConstruct
    @Override
    public void init() {

        super.init();
        muestraReporte = false;

        fechaDesde = UtilFechas.sumar(new Date(), Calendar.YEAR, -1);
        fechaHasta = new Date();
        fechaMinima = getFechaMinima();

        saldoActual = 0;
        saldoActualSecundario = 0;
        debitos = new ArrayList<>();
        creditos = new ArrayList<>();
        historial = new ArrayList<>();
        codigoMonedaRegistracion = parametrosRN.getParametro().getCodigoMonedaPrimaria();
        marcarClientesConDeuda();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                super.iniciar();

                if (NROCTA != null && !NROCTA.isEmpty()) {
                    alumno = entidadRN.getEntidad(NROCTA);
                    procesarAlumno();
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

        if (alumno == null) {
            JsfUtil.addErrorMessage("Seleccione un alumno");
            return;
        }

        if (fechaHasta.before(fechaDesde)) {
            JsfUtil.addWarningMessage("tablaHistorial", "Atención", "Fecha desde tiene que ser mayor o igual a fecha Hasta");
            return;
        }

        historial = cuentaCorrienteRN.getHistoricoMovimientos(alumno.getNrocta(), fechaDesde, fechaHasta, codigoMonedaRegistracion);

        //Inicializamos el saldo anterior en cero
        ItemHistoricoCuentaCorrienteEducacion SaldoAnterior = cuentaCorrienteRN.getSaldoAnterior(alumno.getNrocta(), codigoMonedaRegistracion, fechaDesde);
        historial.add(0, SaldoAnterior);

        double saldo = SaldoAnterior.getSaldo();
        double saldoSecundario = SaldoAnterior.getSaldoSecundario();

        for (ItemHistoricoCuentaCorrienteEducacion i : historial) {

            if (!i.getCodfor().equals("SA")) {

                i.setSaldo(saldo + (i.getDebe() - i.getHaber()));
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

    public void procesarAlumno() {

        if (alumno != null) {

            saldoActual = cuentaCorrienteRN.getSaldoActual(alumno.getNrocta(), codigoMonedaRegistracion);
            saldoActualSecundario = cuentaCorrienteRN.getSaldoSecundarioActual(alumno.getNrocta(), codigoMonedaRegistracion);
            debitos = cuentaCorrienteRN.getDebitosPendientes(alumno.getNrocta(), new Date(), codigoMonedaRegistracion, comprobanteInterno, null);
            creditos = cuentaCorrienteRN.getCreditosPendientes(alumno.getNrocta(), codigoMonedaRegistracion, comprobanteInterno, null);

            calcularVencidos(debitos);

            muestraReporte = false;
            todoOk = false;

            consultarHistorial();
        }
    }

    public void cambiarMoneda() {

//        buscarCliente();
        procesarAlumno();
    }

    public void cambiarComprobanteInterno() {

//        buscarCliente();
        procesarAlumno();
    }

    private void calcularVencidos(List<ItemPendienteCuentaCorrienteEducacion> items) {

        cantVencer = 0;
        cantVencidos = 0;

        for (ItemPendienteCuentaCorrienteEducacion icc : items) {

            if (icc.getFechaVencimiento().before(new Date())) {
                cantVencidos++;
            } else {
                cantVencer++;
            }
        }
    }

    public void onClienteSelect(SelectEvent event) {

        alumno = (EntidadComercial) event.getObject();

        if (alumno == null) {
            return;
        }

        saldoActual = cuentaCorrienteRN.getSaldoActual(alumno.getNrocta(), codigoMonedaRegistracion);
        saldoActualSecundario = cuentaCorrienteRN.getSaldoSecundarioActual(alumno.getNrocta(), codigoMonedaRegistracion);
        debitos = cuentaCorrienteRN.getDebitosPendientes(alumno.getNrocta(), new Date(), codigoMonedaRegistracion, comprobanteInterno, null);
        creditos = cuentaCorrienteRN.getCreditosPendientes(alumno.getNrocta(), codigoMonedaRegistracion, comprobanteInterno, null);

        muestraReporte = false;

//        consultarHistorial();
    }

    private void marcarClientesConDeuda() {

//        if (entidadComercialBean.getLista() == null) {
//            return;
//        }
//
//        for (EntidadComercial c : entidadComercialBean.getLista()) {
//            c.setConDeuda(cuentaCorrienteRN.conDeuda(c.getNrocta(), codigoMonedaRegistracion));
//        }
    }

//    public void buscarCliente() {
//
//        entidadComercialBean.buscar();
//        for (EntidadComercial c : entidadComercialBean.getLista()) {
//            c.setConDeuda(cuentaCorrienteRN.conDeuda(c.getNrocta(), codigoMonedaRegistracion));
//        }
//    }
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

    public double getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(double saldoActual) {
        this.saldoActual = saldoActual;
    }

    public double getSaldoActualSecundario() {
        return saldoActualSecundario;
    }

    public void setSaldoActualSecundario(double saldoActualSecundario) {
        this.saldoActualSecundario = saldoActualSecundario;
    }

    public List<ItemPendienteCuentaCorrienteEducacion> getDebitos() {
        return debitos;
    }

    public void setDebitos(List<ItemPendienteCuentaCorrienteEducacion> debitos) {
        this.debitos = debitos;
    }

    public List<ItemPendienteCuentaCorrienteEducacion> getCreditos() {
        return creditos;
    }

    public void setCreditos(List<ItemPendienteCuentaCorrienteEducacion> creditos) {
        this.creditos = creditos;
    }

    public List<ItemHistoricoCuentaCorrienteEducacion> getHistorial() {
        return historial;
    }

    public void setHistorial(List<ItemHistoricoCuentaCorrienteEducacion> historial) {
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

    public String getComprobanteInterno() {
        return comprobanteInterno;
    }

    public void setComprobanteInterno(String comprobanteInterno) {
        this.comprobanteInterno = comprobanteInterno;
    }

    public EntidadComercial getAlumno() {
        return alumno;
    }

    public void setAlumno(EntidadComercial alumno) {
        this.alumno = alumno;
    }

    public String getNROCTA() {
        return NROCTA;
    }

    public void setNROCTA(String NROCTA) {
        this.NROCTA = NROCTA;
    }

}
