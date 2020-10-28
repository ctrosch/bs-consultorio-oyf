/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.web.informe;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.rn.FormularioRN;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
import bs.tesoreria.modelo.CuentaTesoreria;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.tesoreria.rn.CuentaTesoreriaRN;
import bs.tesoreria.rn.MovimientoTesoreriaRN;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class MovimientoPorFechaBean extends InformeBase implements Serializable {

    @EJB
    private CuentaTesoreriaRN cuentaTesoreriaRN;
    @EJB
    private MovimientoTesoreriaRN tesoreriaRN;
    @EJB
    private FormularioRN formularioRN;

    private Date fecha;
    private Date fechaDesde;
    private Date fechaHasta;
    private String sFiltroCuenta;

    private CuentaTesoreria cuentaTesoreria;
    private List<CuentaTesoreria> cuentas;
    private BigDecimal saldoAFecha;
    private List<ItemMovimientoTesoreria> movimientos;

    /**
     * Creates a new instance of CajaBean
     */
    public MovimientoPorFechaBean() {

    }

    @PostConstruct
    @Override
    public void init() {

        filtro = new HashMap<String, String>();
        sFiltroCuenta = "";
        fecha = new Date();
        fechaDesde = new Date();
        fechaHasta = new Date();
        saldoAFecha = BigDecimal.ZERO;
        cuentas = cuentaTesoreriaRN.getListaByBusqueda(null, sFiltroCuenta);
        movimientos = new ArrayList<ItemMovimientoTesoreria>();

    }

    public void verMovimientos(CuentaTesoreria c) {
//        System.err.println("verMovimientos");
        cuentaTesoreria = c;
        consultarDatos();
    }

    public void onSelect(SelectEvent event) {
//        System.err.println("onSelect");
        cuentaTesoreria = (CuentaTesoreria) event.getObject();
        consultarDatos();
    }

    public void onDateSelect(SelectEvent event) {
//        System.err.println("onDateSelect");
        consultarDatos();
    }

    public void cargarFiltro() {

        filtro.clear();

        if (cuentaTesoreria != null) {
            filtro.put("cuentaTesoreria.codigo = ", "'" + cuentaTesoreria.getCodigo() + "'");
        }

        if (fechaDesde != null) {
            filtro.put("movimiento.fechaMovimiento >= ", JsfUtil.getFechaSQL(fechaDesde));
        }

        if (fechaHasta != null) {
            filtro.put("movimiento.fechaMovimiento <= ", JsfUtil.getFechaSQL(fechaHasta));
        }
    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {

        String mensaje = "";
        todoOk = true;

        if (cuentaTesoreria == null) {
            mensaje = "Seleccione una cuenta para ver los movimientos en la fecha seleccionada";
            todoOk = false;
        }

        if (fechaDesde != null && fechaHasta != null && fechaHasta.before(fechaDesde)) {
            mensaje = "La fecha desde no puede ser mayor a fecha hasta";
            todoOk = false;
        }

        if (!mensaje.isEmpty()) {
            todoOk = false;
            throw new ExcepcionGeneralSistema(mensaje);
        }
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetParametros() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void consultarDatos() {

        try {
            validarDatos();
            cargarFiltro();

            movimientos = tesoreriaRN.getItemMovientoTesoreriaByFiltro(filtro, 500);

            if (movimientos.isEmpty()) {
                JsfUtil.addWarningMessage("No se han encontrado movimientos para la cuenta " + cuentaTesoreria.getDescripcion());
            }

            ItemMovimientoTesoreria itemSaldoAnterio = tesoreriaRN.getSaldoAnterior(cuentaTesoreria.getCodigo(), fechaDesde);
            movimientos.add(0, itemSaldoAnterio);

            BigDecimal saldo = itemSaldoAnterio.getSaldo();
            BigDecimal saldoMonedaSecundaria = itemSaldoAnterio.getSaldoMonedaSecundaria();

            for (ItemMovimientoTesoreria i : movimientos) {

                if (i.getMovimiento() != null) {

                    i.setSaldo(saldo.add(i.getImporteDebe().add(i.getImporteHaber().negate())));
                    saldo = i.getSaldo();

                    i.setSaldoMonedaSecundaria(saldoMonedaSecundaria.add(i.getImporteDebeMonedaSecundaria().add(i.getImporteHaberMonedaSecundaria().negate())));
                    saldoMonedaSecundaria = i.getSaldoMonedaSecundaria();
                }
            }
        } catch (Exception e) {

            JsfUtil.addErrorMessage("No es posible obtener datos " + e);
            todoOk = false;
            muestraReporte = false;
        }
    }

    public void imprimir(String codfor, int nrofor) {

        try {

            formulario = formularioRN.getFormulario("CJ", codfor);

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

            MovimientoTesoreria mt = tesoreriaRN.getMovimiento(codfor, nrofor);

            parameters.put("ID", mt.getId());
            parameters.put("EN_LETRAS", "Son pesos " + JsfUtil.generarEnLetras(mt.getImporteTotal()));
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public List<CuentaTesoreria> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<CuentaTesoreria> cuentas) {
        this.cuentas = cuentas;
    }

    public List<ItemMovimientoTesoreria> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<ItemMovimientoTesoreria> movimientos) {
        this.movimientos = movimientos;
    }

    public String getsFiltroCuenta() {
        return sFiltroCuenta;
    }

    public void setsFiltroCuenta(String sFiltroCuenta) {
        this.sFiltroCuenta = sFiltroCuenta;
    }

    public Map<String, String> getFiltro() {
        return filtro;
    }

    public void setFiltro(Map<String, String> filtro) {
        this.filtro = filtro;
    }

    public BigDecimal getSaldoAFecha() {
        return saldoAFecha;
    }

    public void setSaldoAFecha(BigDecimal saldoAFecha) {
        this.saldoAFecha = saldoAFecha;
    }

    public CuentaTesoreria getCuentaTesoreria() {
        return cuentaTesoreria;
    }

    public void setCuentaTesoreria(CuentaTesoreria cuentaTesoreria) {
        this.cuentaTesoreria = cuentaTesoreria;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

}
