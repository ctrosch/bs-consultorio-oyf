/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.proceso;

import bs.administracion.modelo.Modulo;
import bs.administracion.rn.ModuloRN;
import bs.contabilidad.modelo.MovimientoContabilidad;
import bs.contabilidad.rn.ContabilidadRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.prestamo.modelo.MovimientoPrestamo;
import bs.prestamo.rn.MovimientoPrestamoRN;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.proveedores.rn.MovimientoProveedorRN;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.tesoreria.rn.MovimientoTesoreriaRN;
import bs.ventas.modelo.MovimientoVenta;
import bs.ventas.rn.MovimientoVentaRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FlowEvent;

/**
 *
 * @author Ctrosch
 */
@ManagedBean
@ViewScoped
public class PasajeContableBean extends GenericBean implements Serializable {

    private List<MovimientoContabilidad> movimientosContabilidad;

    private List<MovimientoPrestamo> movimientosPrestamo;
    private List<MovimientoProveedor> movimientosProveedores;
    private List<MovimientoTesoreria> movimientosTesoreria;
    private List<MovimientoVenta> movimientosVentas;

    private Modulo modulo;
    private List<Modulo> modulos;

    @EJB
    private ModuloRN moduloRN;
    @EJB
    private MovimientoTesoreriaRN tesoreriaRN;
    @EJB
    private MovimientoPrestamoRN prestamoRN;
    @EJB
    private MovimientoVentaRN ventaRN;
    @EJB
    private MovimientoProveedorRN proveedorRN;
    @EJB
    private ContabilidadRN contabilidadRN;

    public PasajeContableBean() {

    }

    @PostConstruct
    public void init() {

        Calendar cFechaDesde = Calendar.getInstance();
        Calendar cFechaHasta = Calendar.getInstance();
        cFechaDesde.add(Calendar.MONTH, -1);
        cFechaDesde.set(Calendar.DAY_OF_MONTH, 1);

        cFechaHasta.set(Calendar.DAY_OF_MONTH, 1);
        cFechaHasta.add(Calendar.DAY_OF_MONTH, -1);

        fechaMovimientoDesde = cFechaDesde.getTime();
        fechaMovimientoHasta = cFechaHasta.getTime();

        modulos = moduloRN.getModuloForPasajeContable();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                super.iniciar();
                beanIniciado = true;

            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void setRangoFechaMovimiento() {

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(fechaMovimientoDesde);

        gc.set(Calendar.DAY_OF_MONTH, gc.getActualMaximum(Calendar.DAY_OF_MONTH));
        gc.set(Calendar.HOUR_OF_DAY, gc.getActualMaximum(Calendar.HOUR_OF_DAY));
        gc.set(Calendar.MINUTE, gc.getActualMaximum(Calendar.MINUTE));
        gc.set(Calendar.SECOND, gc.getActualMaximum(Calendar.SECOND));

        fechaMovimientoHasta = gc.getTime();

    }

//    public void nuevo(){
//
//        try {
//            centroCosto = new CentroCosto();
//            esNuevo = true;
//            modoVista = "D";
//        } catch (Exception ex) {
//            JsfUtil.addErrorMessage("No es posible crear una nueva cuenta " + ex);
//        }
//    }
    public String procesoPasajeContable(FlowEvent event) {

        if (event.getNewStep().equals("inicio")) {
            JsfUtil.addInfoMessage("Seleccione el módulo y rango de fechas para el pasaje contable");
        }

        if (event.getOldStep().equals("inicio")) {

            if (!validarParametros()) {
                return event.getOldStep();
            }

            if (buscarMovimientos()) {
                JsfUtil.addInfoMessage("Haga clic en \"Siguiente\" para finalizar el pasaje contable.");
            }
        }

        if (event.getOldStep().equals("pendiente") && event.getNewStep().equals("resultado")) {
            try {
                procesarMovimientos();
            } catch (Exception ex) {
                ex.printStackTrace();
                JsfUtil.addErrorMessage("No es posible procesar movimientos: " + ex);
            }
        }

        if (event.getNewStep().equals("resultado")) {

        }

        return event.getNewStep();
    }

    public boolean buscarMovimientos() {

        cargarFiltroBusqueda();

        orderBy.add("fechaMovimiento");
        orderBy.add("numeroFormulario");

        if (modulo.getCodigo().equals("CJ")) {

            movimientosTesoreria = tesoreriaRN.getListaByBusqueda(filtro, orderBy, 0);

            if (movimientosTesoreria == null || movimientosTesoreria.isEmpty()) {
                JsfUtil.addWarningMessage("No se encontraron movimientos para el módulo " + modulo.getDescripcion() + " en el rango de fechas seleccionado ");
                return false;
            }

            JsfUtil.addInfoMessage("Se encontraron " + movimientosTesoreria.size() + " comprobantes a procesar");
        }

        if (modulo.getCodigo().equals("PR")) {

            movimientosPrestamo = prestamoRN.getListaByBusqueda(filtro, orderBy, 0);

            if (movimientosPrestamo == null || movimientosPrestamo.isEmpty()) {
                JsfUtil.addWarningMessage("No se encontraron movimientos para el módulo " + modulo.getDescripcion() + " en el rango de fechas seleccionado ");
                return false;
            }

            JsfUtil.addInfoMessage("Se encontraron " + movimientosPrestamo.size() + " comprobantes a procesar");
        }

        if (modulo.getCodigo().equals("PV")) {

            movimientosProveedores = proveedorRN.getListaByBusqueda(filtro, orderBy, 0);

            if (movimientosProveedores == null || movimientosProveedores.isEmpty()) {
                JsfUtil.addWarningMessage("No se encontraron movimientos para el módulo " + modulo.getDescripcion() + " en el rango de fechas seleccionado ");
                return false;
            }

            JsfUtil.addInfoMessage("Se encontraron " + movimientosProveedores.size() + " comprobantes a procesar");
        }

        if (modulo.getCodigo().equals("VT")) {

            movimientosVentas = ventaRN.getListaByBusqueda(filtro, orderBy, 0);

            if (movimientosVentas == null || movimientosVentas.isEmpty()) {
                JsfUtil.addWarningMessage("No se encontraron movimientos para el módulo " + modulo.getDescripcion() + " en el rango de fechas seleccionado ");
                return false;
            }

            JsfUtil.addInfoMessage("Se encontraron " + movimientosVentas.size() + " comprobantes a procesar");

        }
        return true;
    }

    public void procesarMovimientos() {

        GregorianCalendar fechaPasaje = new GregorianCalendar();
        fechaPasaje.setTime(fechaMovimientoHasta);

        fechaPasaje.set(Calendar.DAY_OF_MONTH, fechaPasaje.getActualMaximum(Calendar.DAY_OF_MONTH));
        fechaPasaje.set(Calendar.HOUR_OF_DAY, fechaPasaje.getActualMinimum(Calendar.HOUR_OF_DAY));
        fechaPasaje.set(Calendar.MINUTE, fechaPasaje.getActualMinimum(Calendar.MINUTE));
        fechaPasaje.set(Calendar.SECOND, fechaPasaje.getActualMinimum(Calendar.SECOND));

        if (modulo.getCodigo().equals("CJ")) {

            try {
                movimientosContabilidad = contabilidadRN.generaPasajeContableTesoreria(movimientosTesoreria, fechaPasaje.getTime());
            } catch (Exception ex) {
                JsfUtil.addErrorMessage("Problemas para generar asiento resumen " + ex);
                Logger.getLogger(PasajeContableBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        if (modulo.getCodigo().equals("PR")) {

            try {
                movimientosContabilidad = contabilidadRN.generaPasajeContablePrestamo(movimientosPrestamo, fechaPasaje.getTime());
            } catch (Exception ex) {
                JsfUtil.addErrorMessage("Problemas para generar asiento resumen " + ex);
                Logger.getLogger(PasajeContableBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (modulo.getCodigo().equals("PV")) {

            try {
                movimientosContabilidad = contabilidadRN.generaPasajeContableProveedores(movimientosProveedores, fechaPasaje.getTime());
            } catch (Exception ex) {
                JsfUtil.addErrorMessage("Problemas para generar asiento resumen " + ex);
                Logger.getLogger(PasajeContableBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (modulo.getCodigo().equals("VT")) {

            try {
                movimientosContabilidad = contabilidadRN.generaPasajeContableVenta(movimientosVentas, fechaPasaje.getTime());
            } catch (Exception ex) {
                JsfUtil.addErrorMessage("Problemas para generar asiento resumen " + ex);
                Logger.getLogger(PasajeContableBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (movimientosContabilidad == null || movimientosContabilidad.isEmpty()) {
            JsfUtil.addWarningMessage("No se generó ningún asiento contable");
            return;
        }

        List<String> resultadoOk = new ArrayList<String>();;
        List<String> resultadoError = new ArrayList<String>();;

        contabilidadRN.guardarPasajeContable(movimientosContabilidad, resultadoOk, resultadoError);

        JsfUtil.addErrorMessages(resultadoError);
        JsfUtil.addInfoMessages(resultadoOk);
    }

    public boolean validarParametros() {

        if (modulo == null) {
            JsfUtil.addWarningMessage("El módulo es obligatorio");
            return false;
        }

        if (fechaMovimientoDesde != null && fechaMovimientoHasta != null) {
            if (fechaMovimientoHasta.before(fechaMovimientoDesde)) {
                JsfUtil.addWarningMessage("La fecha de desde, no puede ser mayor a la fecha hasta.");
                return false;
            }
        }

//        if (numeroFormularioDesde != null && numeroFormularioHasta != null) {
//            if (numeroFormularioDesde > numeroFormularioHasta) {
//                JsfUtil.addWarningMessage("Número de formulario desde es mayor al número de formulario hasta");
//                return false;
//            }
//        }
        return true;
    }

    public void cargarFiltroBusqueda() {

        filtro.clear();

        filtro.put("movimientoContabilidad ", "IS NULL ");
        filtro.put("movimientoReversion ", "IS NULL ");
        filtro.put("numeroFormulario > ", "0");
        filtro.put("comprobante.comprobanteAsiento ", "IS NOT NULL ");

        if (fechaMovimientoDesde != null) {

            filtro.put("fechaMovimiento >= ", JsfUtil.getFechaSQL(fechaMovimientoDesde));
        }

        if (fechaMovimientoHasta != null) {

            filtro.put("fechaMovimiento <= ", JsfUtil.getFechaSQL(fechaMovimientoHasta));
        }
    }

    public void reiniciarProceso() {

    }

    public void resetParametros() {
        formulario = null;

        numeroFormularioDesde = null;
        numeroFormularioHasta = null;
    }

    public List<MovimientoPrestamo> getMovimientosPrestamo() {
        return movimientosPrestamo;
    }

    public void setMovimientosPrestamo(List<MovimientoPrestamo> movimientosPrestamo) {
        this.movimientosPrestamo = movimientosPrestamo;
    }

    public List<MovimientoProveedor> getMovimientosProveedores() {
        return movimientosProveedores;
    }

    public void setMovimientosProveedores(List<MovimientoProveedor> movimientosProveedores) {
        this.movimientosProveedores = movimientosProveedores;
    }

    public List<MovimientoTesoreria> getMovimientosTesoreria() {
        return movimientosTesoreria;
    }

    public void setMovimientosTesoreria(List<MovimientoTesoreria> movimientosTesoreria) {
        this.movimientosTesoreria = movimientosTesoreria;
    }

    public List<MovimientoVenta> getMovimientosVentas() {
        return movimientosVentas;
    }

    public void setMovimientosVentas(List<MovimientoVenta> movimientosVentas) {
        this.movimientosVentas = movimientosVentas;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public List<Modulo> getModulos() {
        return modulos;
    }

    public void setModulos(List<Modulo> modulos) {
        this.modulos = modulos;
    }

    public List<MovimientoContabilidad> getMovimientosContabilidad() {
        return movimientosContabilidad;
    }

    public void setMovimientosContabilidad(List<MovimientoContabilidad> movimientosContabilidad) {
        this.movimientosContabilidad = movimientosContabilidad;
    }

}
