/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.web;

import bs.administracion.modelo.VistaDetalle;
import bs.compra.modelo.CodigoCircuitoCompra;
import bs.contabilidad.modelo.CuentaContable;
import bs.contabilidad.modelo.PeriodoContable;
import bs.educacion.modelo.Carrera;
import bs.educacion.modelo.Curso;
import bs.educacion.modelo.EstadoEducacion;
import bs.entidad.modelo.EntidadComercial;
import bs.facturacion.modelo.CodigoCircuitoFacturacion;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Concepto;
import bs.global.modelo.Provincia;
import bs.global.modelo.PuntoVenta;
import bs.global.modelo.TipoComprobante;
import bs.global.modelo.TipoConcepto;
import bs.global.modelo.Zona;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
import bs.prestamo.modelo.Clasificacion01;
import bs.prestamo.modelo.Clasificacion02;
import bs.prestamo.modelo.Clasificacion03;
import bs.prestamo.modelo.EstadoPrestamo;
import bs.prestamo.modelo.Financiador;
import bs.prestamo.modelo.LineaCredito;
import bs.prestamo.modelo.Promotor;
import bs.produccion.modelo.Operario;
import bs.produccion.modelo.Planta;
import bs.proveedores.modelo.CondicionPagoProveedor;
import bs.proveedores.modelo.ListaCosto;
import bs.seguridad.modelo.MenuParametro;
import bs.stock.modelo.Atributo;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.Producto;
import bs.stock.modelo.Rubro01;
import bs.stock.modelo.Rubro02;
import bs.stock.modelo.Rubro03;
import bs.stock.modelo.TipoProducto;
import bs.stock.rn.AtributoValorRN;
import bs.tesoreria.modelo.CuentaTesoreria;
import bs.tesoreria.modelo.TipoCuentaTesoreria;
import bs.ventas.modelo.Cobrador;
import bs.ventas.modelo.CondicionDePagoVenta;
import bs.ventas.modelo.ListaPrecioVenta;
import bs.ventas.modelo.Repartidor;
import bs.ventas.modelo.Vendedor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class ReporteService extends InformeBase implements Serializable {

    @EJB
    private AtributoValorRN atributoValorRN;

    private boolean DEBUG = false;

    private Integer idMovimiento;
    private Integer numeroFormulario;

    //GLOBAL
    private PuntoVenta puntoVenta;
    private Provincia provincia;
    private Zona zona;

    private TipoComprobante tipoComprobante;
    private TipoConcepto tipoConcepto;
    private Concepto concepto;
    private Concepto conceptoDesde;
    private Concepto conceptoHasta;

    private PeriodoContable periodoContable;

    private CuentaContable cuentaContable;
    private CuentaContable cuentaContableDesde;
    private CuentaContable cuentaContableHasta;

    //FACTURACION Y VENTAS
    private Integer idMovimientoFacturacion;
    private CodigoCircuitoFacturacion circuitoDesde;
    private CodigoCircuitoFacturacion circuitoHasta;
    private String comprometeStock;
    private EntidadComercial cliente;
    private EntidadComercial cliente2;
    private EntidadComercial cliente3;
    private Vendedor vendedor;
    private Repartidor repartidor;
    private ListaPrecioVenta listaPrecioVenta;
    private CondicionDePagoVenta condicionDePagoVentas;
    private String incluyeEnSubdiario;

    //PROVEEDORES Y COMPRAS
    private EntidadComercial proveedor;
    private EntidadComercial proveedor2;
    private EntidadComercial proveedor3;
    private ListaCosto listaPrecioCosto;
    private CondicionPagoProveedor condicionPagoProveedor;
    private CodigoCircuitoCompra circuitoCompraDesde;
    private CodigoCircuitoCompra circuitoCompraHasta;

    //PRESTAMO
    private EntidadComercial destinatario;
    private EstadoPrestamo estadoPrestamo;
    private LineaCredito lineaCredito;
    private Financiador financiador;
    private Promotor promotor;
    private String reprogramado;
    private Clasificacion01 clasificacion01;
    private Clasificacion02 clasificacion02;
    private Clasificacion03 clasificacion03;

    //TESORERIA
    private Cobrador cobrador;
    private CuentaTesoreria cuentaTesoreria;
    private TipoCuentaTesoreria tipoCuentaTesoreria;

    //EDUCACION
    private EntidadComercial alumno;
    private EntidadComercial profesor;
    private Carrera carrera;
    private Curso curso;
    private EstadoEducacion estadoEducacion;

    //STOCK
    private Integer idMovimientoStock;
    private TipoProducto tipoProducto;
    private Rubro01 rubro01;
    private Rubro01 rubro01Desde;
    private Rubro01 rubro01Hasta;
    private Rubro02 rubro02;
    private Rubro02 rubro02Desde;
    private Rubro02 rubro02Hasta;
    private Rubro03 rubro03;
    private Rubro03 rubro03Desde;
    private Rubro03 rubro03Hasta;
    private String atributo1;
    private String atributo2;
    private String atributo3;
    private String atributo4;
    private String atributo5;
    private String atributo6;
    private String atributo7;
    private String conAtributosStock;
    private String exluirProductoStockCero;
    private String atributoStock1;
    private String atributoStock2;
    private String atributoStock3;
    private String atributoStock4;
    private String atributoStock5;
    private String atributoStock6;
    private String tipoMovimientoStock;
    private Producto producto;
    private Producto productoDesde;
    private Producto productoHasta;
    private Deposito deposito;
    private EntidadComercial proveedorHabitual;
    private String tipoCosto;

    //PRODUCCION
    private Planta planta;
    private String tipoItem;
    private Producto proceso;
    private Operario operario;

    private boolean soloPendiente;
    private boolean conProduccion;
    private String incluyeImpuesto;
    private String seIncluyeEnEstadisticas;
    private String comprobanteInterno;

    public ReporteService() {
        super();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {
                super.iniciar();

                cargarParametrosMenu();

                modoVista = "D";
                seIncluyeEnEstadisticas = "S";
                conAtributosStock = "N";
                exluirProductoStockCero = "N";
                monedaRegistracion = null;

                if (menuSeleccionado != null) {

                    if (menuSeleccionado.getModulo().getCodigo().equals("FC")
                            || menuSeleccionado.getModulo().getCodigo().equals("CO")
                            || menuSeleccionado.getModulo().getCodigo().equals("PD")) {

                        //Si es reporte de factruación, compras o producción, fecha desde es 2 años.
                        Calendar cFechaDesde = Calendar.getInstance();
                        cFechaDesde.add(Calendar.YEAR, -2);

                        fechaDesde = cFechaDesde.getTime();

                    }
                }

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    /**
     *
     * @param tipo
     * @param campo
     * @return Por defecto el campo no se muestra
     */
    @Override
    public boolean muestroCampo(String tipo, String campo) {

        if (campo == null) {
//            System.err.println("campo "+campo+" sale 1");
            return false;
        }
        if (vista == null) {
//            System.err.println("campo "+campo+" sale 2");
            return false;
        }
        if (vista.getDetalle() == null) {
//            System.err.println("campo "+campo+" sale 3");
            return false;
        }

        for (VistaDetalle i : vista.getDetalle()) {

            if (i.getTipo().equals(tipo) && i.getCampo().equals(campo)) {
                //System.err.println("campo "+campo+" sale 4");
                return i.isVisible();
            }
        }
        return false;
    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {

        String mensaje = "";
        todoOk = true;

        if (codigoReporte == null || codigoReporte.isEmpty()) {
            mensaje = "El código de reporte no puede ser nulo";
        }

        if (fechaDesde != null && fechaHasta != null && fechaHasta.before(fechaDesde)) {
            mensaje = "La fecha desde no puede ser mayor a fecha hasta";
        }

        if (fechaVencimientoDesde != null && fechaVencimientoHasta != null && fechaVencimientoHasta.before(fechaVencimientoDesde)) {
            mensaje = "La fecha de vencimiento desde no puede ser mayor a fecha de vencimiento hasta";
        }

        if (fechaPagoDesde != null && fechaPagoHasta != null && fechaPagoHasta.before(fechaPagoDesde)) {
            mensaje = "La fecha de pago desde no puede ser mayor a fecha de pago hasta";
        }

        if (numeroFormularioDesde != null && numeroFormularioHasta != null && numeroFormularioDesde.compareTo(numeroFormularioHasta) > 0) {
            mensaje = "La número de formulario desde no puede ser mayor al número hasta";
        }

        if (!mensaje.isEmpty()) {
            todoOk = false;
            throw new ExcepcionGeneralSistema(mensaje);
        }
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {

        reporte = reporteRN.getReporte(codigoReporte);

        if (usuarioSessionBean.isEstaRegistrado()) {
            parameters.put("USUARIO", usuarioSessionBean.getUsuario().getUsuario());
        } else {
            System.err.println("No hay usuario activo");
        }

        if (DEBUG) {
            parameters.put("DEBUG", DEBUG);
        }

        parameters.put("ID", 0);
        parameters.put("COPIAS", "1");
        parameters.put("CODSUC", "");
        parameters.put("UNINEG", "");
        parameters.put("PTOVTA", "");
        parameters.put("MONREG", "");

        parameters.put("TIPCOM", "");

        parameters.put("MODCPT", "");
        parameters.put("TIPCPT", "");
        parameters.put("CODCPT", "");

        parameters.put("MOCPTD", "");
        parameters.put("TICPTD", "");
        parameters.put("COCPTD", "");

        parameters.put("MOCPTH", "");
        parameters.put("TICPTH", "");
        parameters.put("COCPTH", "");

        //GLOBAL
        parameters.put("CODPRV", "");
        parameters.put("CODZON", "");
        parameters.put("FECHA", "");
        parameters.put("FCHMOV", "");
        parameters.put("FCHDES", "");
        parameters.put("FCHHAS", "");
        parameters.put("FCHVTD", "");
        parameters.put("FCHVTH", "");
        parameters.put("FCHPAD", "");
        parameters.put("FCHPAH", "");
        parameters.put("FCHMED", "");
        parameters.put("FCHMEH", "");
        parameters.put("CODFOR", "");
        parameters.put("NROFOR", "");
        parameters.put("NRODES", "0");
        parameters.put("NROHAS", "9999999");
        parameters.put("COTIZA", "");
        parameters.put("SOLPEN", "N");
        parameters.put("CONPRD", "N");
        parameters.put("COMINT", "");
        parameters.put("INCEST", "");
        parameters.put("INCSUB", "");

        //STOCK
        parameters.put("TIPMOV", "");
        parameters.put("INCIMP", "N");
        parameters.put("TIPPRO", "");
        parameters.put("ARTCOD", "");
        parameters.put("RUBR01", "");
        parameters.put("RUBR02", "");
        parameters.put("RUBR03", "");
        parameters.put("RUBR04", "");
        parameters.put("RUBR05", "");
        parameters.put("ARTDES", "");
        parameters.put("ARTHAS", "");
        parameters.put("RU1DES", "0");
        parameters.put("RU1HAS", "9999");
        parameters.put("RU2DES", "0");
        parameters.put("RU2HAS", "9999");
        parameters.put("PROHAB", "");
        parameters.put("DEPOSI", "");
        parameters.put("CODLVT", "");
        parameters.put("VATRI1", "");
        parameters.put("VATRI2", "");
        parameters.put("VATRI3", "");
        parameters.put("VATRI4", "");
        parameters.put("VATRI5", "");
        parameters.put("VATRI6", "");
        parameters.put("VATRI7", "");
        parameters.put("NATRI1", "");
        parameters.put("NATRI2", "");
        parameters.put("NATRI3", "");
        parameters.put("EXCCER", "N");
        parameters.put("CONATR", "N");
        parameters.put("NATRI1_ST", "");
        parameters.put("NATRI2_ST", "");
        parameters.put("NATRI3_ST", "");
        parameters.put("FCHENT", "");

        //PRODUCCION
        parameters.put("CODPLA", "");
        parameters.put("TIPITM", "");
        parameters.put("ARTCOD", "");
        parameters.put("PROCES", "");
        parameters.put("CODOPE", "");

        //FACTURACION
        parameters.put("CIRDES", "");
        parameters.put("CIRHAS", "");
        parameters.put("COMPST", "");
        parameters.put("NROCTA", "");
        parameters.put("NROCT2", "");
        parameters.put("NROCT3", "");
        parameters.put("VNDDOR", "");
        parameters.put("REPDOR", "");
        parameters.put("CNDPVT", "");
        parameters.put("TIPCOS", "");

        parameters.put("CODLPV", "");
        parameters.put("CNDPPV", "");

        parameters.put("NROCTA_CJ", "");
        parameters.put("COBRAD", "");
        parameters.put("TIPCTA", "");

        //PRESTAMO
        parameters.put("CODEST", "");
        parameters.put("CODFIN", "");
        parameters.put("CODLIN", "");
        parameters.put("CODPRO", "");
        parameters.put("REPROG", "");
        parameters.put("CLAS01", "");
        parameters.put("CLAS02", "");
        parameters.put("CLAS03", "");

        //EDUCACION
        parameters.put("NROCTAA", "");
        parameters.put("NROCTAP", "");
        parameters.put("CODCAR", "");
        parameters.put("CODCUR", "");
        parameters.put("CODARA", "");
        parameters.put("ESTADO", "");

        //CONTABILIDAD
        parameters.put("PERIOD", "");
        parameters.put("CTACON", "");
        parameters.put("CTCOD", "");
        parameters.put("CTCOH", "");

        if (copias != null) {
            parameters.put("COPIAS", copias);
        }

        if (sucursal != null) {
            parameters.put("CODSUC", sucursal.getCodigo());
        }

        if (unidadNegocio != null) {
            parameters.put("UNINEG", unidadNegocio.getCodigo());
        }

        if (provincia != null) {
            parameters.put("CODPRV", provincia.getCodigo());
        }

        if (zona != null) {
            parameters.put("CODZON", zona.getCodigo());
        }

        if (puntoVenta != null) {
            parameters.put("PTOVTA", puntoVenta.getCodigo());
        }

        if (monedaRegistracion != null) {
            parameters.put("MONREG", monedaRegistracion.getCodigo());
        }

        if (monedaVisualizacion != null) {
            parameters.put("MONVIS", monedaVisualizacion.getCodigo());
        }

        if (tipoComprobante != null) {
            parameters.put("TIPCOM", tipoComprobante.getCodigo());
        }

        if (tipoConcepto != null) {
            parameters.put("MODCPT", tipoConcepto.getModulo());
            parameters.put("TIPCPT", tipoConcepto.getCodigo());
        }

        if (concepto != null) {
            parameters.put("MODCPT", concepto.getModulo());
            parameters.put("TIPCPT", concepto.getTipo());
            parameters.put("CODCPT", concepto.getCodigo());
        }

        if (conceptoDesde != null) {
            parameters.put("MOCPTD", conceptoDesde.getModulo());
            parameters.put("TICPTD", conceptoDesde.getTipo());
            parameters.put("COCPTD", conceptoDesde.getCodigo());
        }

        if (conceptoHasta != null) {
            parameters.put("MOCPTH", conceptoHasta.getModulo());
            parameters.put("TICPTH", conceptoHasta.getTipo());
            parameters.put("COCPTH", conceptoHasta.getCodigo());
        }

        if (fecha != null) {
            parameters.put("FECHA", fecha);
        }

        if (fecha != null) {
            parameters.put("FCHMOV", fecha);
        }

        if (fechaDesde != null) {
            parameters.put("FCHDES", fechaDesde);
        }

        if (fechaHasta != null) {
            parameters.put("FCHHAS", fechaHasta);
        }

        if (fechaVencimientoDesde != null) {
            parameters.put("FCHVTD", fechaVencimientoDesde);
        }

        if (fechaVencimientoHasta != null) {
            parameters.put("FCHVTH", fechaVencimientoHasta);
        }

        if (fechaPagoDesde != null) {
            parameters.put("FCHPAD", fechaPagoDesde);
        }

        if (fechaPagoHasta != null) {
            parameters.put("FCHPAH", fechaPagoHasta);
        }

        if (fechaMediaDesde != null) {
            parameters.put("FCHMED", fechaMediaDesde);
        }

        if (fechaMediaHasta != null) {
            parameters.put("FCHMEH", fechaMediaHasta);
        }

        if (formulario != null) {
            parameters.put("CODFOR", formulario.getCodigo());
        }

        if (numeroFormularioDesde != null) {
            parameters.put("NRODES", numeroFormularioDesde);
        }

        if (numeroFormularioHasta != null) {
            parameters.put("NROHAS", numeroFormularioHasta);
        }

        if (numeroFormulario != null) {
            parameters.put("NROFOR", numeroFormulario);
        }

        if (cotizacion != null) {
            parameters.put("COTIZA", cotizacion);
        }

        if (soloPendiente) {
            parameters.put("SOLPEN", "S");
        }

        if (conProduccion) {
            parameters.put("CONPRD", "S");
        }

        if (seIncluyeEnEstadisticas != null && !seIncluyeEnEstadisticas.isEmpty()) {
            parameters.put("INCEST", seIncluyeEnEstadisticas);
        }

        if (comprobanteInterno != null && !comprobanteInterno.isEmpty()) {
            parameters.put("COMINT", comprobanteInterno);
        }

        //CONTABILIDAD
        if (periodoContable != null) {
            parameters.put("PERIOD", periodoContable.getCodigo());
        }

        if (cuentaContable != null) {
            parameters.put("CTACON", cuentaContable.getNrocta());
        }

        if (cuentaContableDesde != null) {
            parameters.put("CTACOD", cuentaContableDesde.getNrocta());
        }

        if (cuentaContableHasta != null) {
            parameters.put("CTACOD", cuentaContableHasta.getNrocta());
        }

        //FACTURACION
        if (circuitoDesde != null) {
            parameters.put("CIRDES", circuitoDesde.getCodigo());
        }

        if (circuitoHasta != null) {
            parameters.put("CIRHAS", circuitoHasta.getCodigo());
        }

        if (comprometeStock != null) {
            parameters.put("COMPST", comprometeStock);
        }

        //VENTAS
        if (cliente != null) {
            parameters.put("NROCTA", cliente.getNrocta());
        }

        if (cliente2 != null) {
            parameters.put("NROCT2", cliente2.getNrocta());
        }

        if (cliente3 != null) {
            parameters.put("NROCT3", cliente3.getNrocta());
        }

        if (vendedor != null) {
            parameters.put("VNDDOR", vendedor.getCodigo());
        }

        if (repartidor != null) {
            parameters.put("REPDOR", repartidor.getCodigo());
        }

        if (condicionDePagoVentas != null) {
            parameters.put("CNDPVT", condicionDePagoVentas.getCodigo());
        }

        if (listaPrecioVenta != null) {
            parameters.put("CODLVT", listaPrecioVenta.getCodigo());
        }

        if (proveedor != null) {
            parameters.put("NROCTA", proveedor.getNrocta());
        }

        if (proveedor2 != null) {
            parameters.put("NROCT2", proveedor2.getNrocta());
        }

        if (proveedor3 != null) {
            parameters.put("NROCT3", proveedor3.getNrocta());
        }

        if (tipoMovimientoStock != null) {
            parameters.put("TIPMOV", tipoMovimientoStock);
        }

        if (incluyeImpuesto != null) {
            parameters.put("INCIMP", incluyeImpuesto);
        }

        if (incluyeEnSubdiario != null) {
            parameters.put("INCSUB", incluyeEnSubdiario);
        }

        //STOCK
        if (tipoProducto != null) {
            parameters.put("TIPPRO", tipoProducto.getCodigo());

            if (tipoProducto.getAtributos() != null) {
                for (Atributo a : tipoProducto.getAtributos()) {
                    parameters.put("NATRI" + a.getNroitm(), a.getNombre());
                }
            }
        }

        if (producto != null) {
            parameters.put("ARTCOD", producto.getCodigo());
        }

        if (productoDesde != null) {
            parameters.put("ARTDES", productoDesde.getCodigo());
        }

        if (productoHasta != null) {
            parameters.put("ARTHAS", productoHasta.getCodigo());
        }

        if (rubro01 != null) {
            parameters.put("RUBR01", rubro01.getCodigo());
        }

        if (rubro01Desde != null) {
            parameters.put("RU1DES", rubro01Desde.getCodigo());
        }

        if (rubro01Hasta != null) {
            parameters.put("RU1HAS", rubro01Hasta.getCodigo());
        }

        if (rubro02 != null) {
            parameters.put("RUBR02", rubro02.getCodigo());
        }

        if (rubro02Desde != null) {
            parameters.put("RU2DES", rubro02Desde.getCodigo());
        }

        if (rubro02Hasta != null) {
            parameters.put("RU2HAS", rubro02Hasta.getCodigo());
        }

        if (rubro03 != null) {
            parameters.put("RUBR03", rubro03.getCodigo());
        }

        if (rubro03Desde != null) {
            parameters.put("RU3DES", rubro03Desde.getCodigo());
        }

        if (rubro03Hasta != null) {
            parameters.put("RU3HAS", rubro03Hasta.getCodigo());
        }

        if (proveedorHabitual != null) {
            parameters.put("PROHAB", proveedorHabitual.getNrocta());
        }

        if (deposito != null) {
            parameters.put("DEPOSI", deposito.getCodigo());
        }

        if (atributo1 != null) {
            parameters.put("VATRI1", atributo1);
        }

        if (atributo2 != null) {
            parameters.put("VATRI2", atributo2);
        }

        if (atributo3 != null) {
            parameters.put("VATRI3", atributo3);
        }

        if (atributo4 != null) {
            parameters.put("VATRI4", atributo4);
        }

        if (atributo5 != null) {
            parameters.put("VATRI5", atributo5);
        }

        if (atributo6 != null) {
            parameters.put("VATRI6", atributo6);
        }

        if (atributo7 != null) {
            parameters.put("VATRI7", atributo7);
        }

        if (exluirProductoStockCero != null) {
            parameters.put("EXCCER", exluirProductoStockCero);
        }

        if (conAtributosStock != null) {
            parameters.put("CONATR", conAtributosStock);
        }

        if (atributoStock1 != null) {
            parameters.put("NATRI1_ST", atributoStock1);
        }

        if (atributoStock2 != null) {
            parameters.put("NATRI2_ST", atributoStock2);
        }

        if (atributoStock3 != null) {
            parameters.put("NATRI3_ST", atributoStock3);
        }

        if (atributoStock4 != null) {
            parameters.put("NATRI4_ST", atributoStock4);
        }

        if (atributoStock5 != null) {
            parameters.put("NATRI5_ST", atributoStock5);
        }

        if (atributoStock6 != null) {
            parameters.put("NATRI6_ST", atributoStock6);
        }

        if (tipoCosto != null) {
            parameters.put("TIPCOS", tipoCosto);
        }

        //PRODUCCION
        if (planta != null) {
            parameters.put("CODPLA", planta.getCodigo());
        }

        if (tipoItem != null && !tipoItem.isEmpty()) {
            parameters.put("TIPITM", tipoItem);
        }

        if (proceso != null) {
            parameters.put("PROCES", proceso.getCodigo());
        }

        if (operario != null) {
            parameters.put("CODOPE", operario.getCodigo());
        }

        //COMPRAS
        if (circuitoCompraDesde != null) {
            parameters.put("CIRDES", circuitoCompraDesde.getCodigo());
        }

        if (circuitoCompraHasta != null) {
            parameters.put("CIRHAS", circuitoCompraHasta.getCodigo());
        }

        if (fechaEntregaHasta != null) {
            parameters.put("FCHENT", fechaEntregaHasta);
        }

        //PROVEEDOR
        if (listaPrecioCosto != null) {
            parameters.put("CODLPV", listaPrecioCosto.getCodigo());
        }

        if (condicionPagoProveedor != null) {
            parameters.put("CNDPPV", condicionPagoProveedor.getCodigo());
        }

        //TESORERIA
        if (cuentaTesoreria != null) {
            parameters.put("NROCTA_CJ", cuentaTesoreria.getCodigo());
        }

        if (tipoCuentaTesoreria != null) {
            parameters.put("TIPCTA", tipoCuentaTesoreria.getCodigo());
        }

        if (cobrador != null) {
            parameters.put("COBRAD", cobrador.getCodigo());
        }

        // PRESTAMO
        if (destinatario != null) {
            parameters.put("NROCTA", destinatario.getNrocta());
        }

        if (estadoPrestamo != null) {
            parameters.put("CODEST", estadoPrestamo.getCodigo());
        }

        if (lineaCredito != null) {
            parameters.put("CODLIN", String.valueOf(lineaCredito.getId()));
        }

        if (financiador != null) {
            parameters.put("CODFIN", String.valueOf(financiador.getId()));
        }

        if (promotor != null) {
            parameters.put("CODPRO", String.valueOf(promotor.getId()));
        }

        if (reprogramado != null) {
            parameters.put("REPROG", reprogramado);
        }

        if (clasificacion01 != null) {
            parameters.put("CLAS01", clasificacion01.getCodigo());
        }

        if (clasificacion02 != null) {
            parameters.put("CLAS02", clasificacion02.getCodigo());
        }

        if (clasificacion03 != null) {
            parameters.put("CLAS03", clasificacion03.getCodigo());
        }

        //EDUCACION
        if (alumno != null) {
            parameters.put("NROCTAA", alumno.getNrocta());
        }

        if (profesor != null) {
            parameters.put("NROCTAP", profesor.getNrocta());
        }

        if (carrera != null) {
            parameters.put("CODCAR", carrera.getCodigo());
        }

        if (curso != null) {
            parameters.put("CODCUR", curso.getCodigo());
        }

        if (estadoEducacion != null) {
            parameters.put("ESTADO", estadoEducacion.getCodigo());
        }
    }

    private void cargarParametrosMenu() {

        if (menuSeleccionado != null && menuSeleccionado.getParametros() != null) {

            for (MenuParametro p : menuSeleccionado.getParametros()) {

                if (p.getNombre().equals("FHCDES")) {

                }

            }
        }

    }

    public List<String> atributoLista(String nombre) {

        try {

            List<String> lista = atributoValorRN.getListaByBusqueda(nombre, "", mostrarDebaja, 100);
            return lista;

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<String>();
        }
    }

    public void limpiarFiltros() {

        puntoVenta = null;
        provincia = null;
        zona = null;

        tipoComprobante = null;
        tipoConcepto = null;
        concepto = null;
        conceptoDesde = null;
        conceptoHasta = null;
        monedaRegistracion = null;

        periodoContable = null;

        cuentaContable = null;
        cuentaContableDesde = null;
        cuentaContableHasta = null;

        //FACTURACION Y VENTAS
        idMovimientoFacturacion = null;
        circuitoDesde = null;
        circuitoHasta = null;
        comprometeStock = null;
        cliente = null;
        cliente2 = null;
        cliente3 = null;
        vendedor = null;
        repartidor = null;
        listaPrecioVenta = null;
        condicionDePagoVentas = null;
        incluyeEnSubdiario = null;

        //PROVEEDORES Y COMPRAS
        proveedor = null;
        proveedor2 = null;
        proveedor3 = null;
        listaPrecioCosto = null;
        condicionPagoProveedor = null;
        circuitoCompraDesde = null;
        circuitoCompraHasta = null;

        //PRESTAMO
        destinatario = null;
        estadoPrestamo = null;
        lineaCredito = null;
        financiador = null;
        promotor = null;
        reprogramado = null;
        clasificacion01 = null;
        clasificacion02 = null;
        clasificacion03 = null;

        //TESORERIA
        cobrador = null;
        cuentaTesoreria = null;
        tipoCuentaTesoreria = null;

        //STOCK
        idMovimientoStock = null;
        tipoProducto = null;
        rubro01 = null;
        rubro01Desde = null;
        rubro01Hasta = null;
        rubro02 = null;
        rubro02Desde = null;
        rubro02Hasta = null;
        rubro03 = null;
        rubro03Desde = null;
        rubro03Hasta = null;
        atributo1 = null;
        atributo2 = null;
        atributo3 = null;
        atributo4 = null;
        atributo5 = null;
        atributo6 = null;
        atributo7 = null;
        conAtributosStock = "N";
        exluirProductoStockCero = "N";
        atributoStock1 = null;
        atributoStock2 = null;
        atributoStock3 = null;
        tipoMovimientoStock = null;
        producto = null;
        productoDesde = null;
        productoHasta = null;
        deposito = null;
        proveedorHabitual = null;
        tipoCosto = null;
        fechaEntregaHasta = null;

        soloPendiente = false;
        conProduccion = false;
        incluyeImpuesto = null;

        comprobanteInterno = null;

        seIncluyeEnEstadisticas = "S";

    }

    @Override
    public void resetParametros() {

        todoOk = false;
        muestraReporte = false;

    }

    public void onPeriodoContableSelect(SelectEvent event) {

        periodoContable = (PeriodoContable) event.getObject();

        if (periodoContable != null) {
            fechaDesde = periodoContable.getFechaInicio();
            fechaHasta = periodoContable.getFechaFin();
        }
    }

    public void onCircuitoFacturacionDesdeSelect(SelectEvent event) {

        circuitoHasta = (CodigoCircuitoFacturacion) event.getObject();

    }

    public void onCircuitoCompraDesdeSelect(SelectEvent event) {

        circuitoCompraHasta = (CodigoCircuitoCompra) event.getObject();

    }

    public void onFechaDesdeSelect() {

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(fechaDesde);

        gc.set(Calendar.DAY_OF_MONTH, gc.getActualMaximum(Calendar.DAY_OF_MONTH));
        gc.set(Calendar.HOUR_OF_DAY, gc.getActualMaximum(Calendar.HOUR_OF_DAY));
        gc.set(Calendar.MINUTE, gc.getActualMaximum(Calendar.MINUTE));
        gc.set(Calendar.SECOND, gc.getActualMaximum(Calendar.SECOND));

        fechaHasta = gc.getTime();

    }

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
    }

    public EntidadComercial getProveedor() {
        return proveedor;
    }

    public void setProveedor(EntidadComercial proveedor) {
        this.proveedor = proveedor;
    }

    public ListaPrecioVenta getListaPrecioVenta() {
        return listaPrecioVenta;
    }

    public void setListaPrecioVenta(ListaPrecioVenta listaPrecioVenta) {
        this.listaPrecioVenta = listaPrecioVenta;
    }

    public ListaCosto getListaPrecioCosto() {
        return listaPrecioCosto;
    }

    public void setListaPrecioCosto(ListaCosto listaPrecioCosto) {
        this.listaPrecioCosto = listaPrecioCosto;
    }

    public CuentaTesoreria getCuentaTesoreria() {
        return cuentaTesoreria;
    }

    public void setCuentaTesoreria(CuentaTesoreria cuentaTesoreria) {
        this.cuentaTesoreria = cuentaTesoreria;
    }

    public TipoCuentaTesoreria getTipoCuentaTesoreria() {
        return tipoCuentaTesoreria;
    }

    public void setTipoCuentaTesoreria(TipoCuentaTesoreria tipoCuentaTesoreria) {
        this.tipoCuentaTesoreria = tipoCuentaTesoreria;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public Cobrador getCobrador() {
        return cobrador;
    }

    public void setCobrador(Cobrador cobrador) {
        this.cobrador = cobrador;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Producto getProductoDesde() {
        return productoDesde;
    }

    public void setProductoDesde(Producto productoDesde) {
        this.productoDesde = productoDesde;
    }

    public Producto getProductoHasta() {
        return productoHasta;
    }

    public void setProductoHasta(Producto productoHasta) {
        this.productoHasta = productoHasta;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public String getTipoMovimientoStock() {
        return tipoMovimientoStock;
    }

    public void setTipoMovimientoStock(String tipoMovimientoStock) {
        this.tipoMovimientoStock = tipoMovimientoStock;
    }

    public Rubro01 getRubro01Desde() {
        return rubro01Desde;
    }

    public void setRubro01Desde(Rubro01 rubro01Desde) {
        this.rubro01Desde = rubro01Desde;
    }

    public Rubro01 getRubro01Hasta() {
        return rubro01Hasta;
    }

    public void setRubro01Hasta(Rubro01 rubro01Hasta) {
        this.rubro01Hasta = rubro01Hasta;
    }

    public Rubro02 getRubro02Desde() {
        return rubro02Desde;
    }

    public void setRubro02Desde(Rubro02 rubro02Desde) {
        this.rubro02Desde = rubro02Desde;
    }

    public Rubro02 getRubro02Hasta() {
        return rubro02Hasta;
    }

    public void setRubro02Hasta(Rubro02 rubro02Hasta) {
        this.rubro02Hasta = rubro02Hasta;
    }

    public EntidadComercial getProveedorHabitual() {
        return proveedorHabitual;
    }

    public void setProveedorHabitual(EntidadComercial proveedorHabitual) {
        this.proveedorHabitual = proveedorHabitual;
    }

    public String getIncluyeImpuesto() {
        return incluyeImpuesto;
    }

    public void setIncluyeImpuesto(String incluyeImpuesto) {
        this.incluyeImpuesto = incluyeImpuesto;
    }

    public Integer getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(Integer numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
    }

    public boolean isSoloPendiente() {
        return soloPendiente;
    }

    public void setSoloPendiente(boolean soloPendiente) {
        this.soloPendiente = soloPendiente;
    }

    public boolean isConProduccion() {
        return conProduccion;
    }

    public void setConProduccion(boolean conProduccion) {
        this.conProduccion = conProduccion;
    }

    public Rubro01 getRubro01() {
        return rubro01;
    }

    public void setRubro01(Rubro01 rubro01) {
        this.rubro01 = rubro01;
    }

    public Rubro02 getRubro02() {
        return rubro02;
    }

    public void setRubro02(Rubro02 rubro02) {
        this.rubro02 = rubro02;
    }

    public Rubro03 getRubro03() {
        return rubro03;
    }

    public void setRubro03(Rubro03 rubro03) {
        this.rubro03 = rubro03;
    }

    public Rubro03 getRubro03Desde() {
        return rubro03Desde;
    }

    public void setRubro03Desde(Rubro03 rubro03Desde) {
        this.rubro03Desde = rubro03Desde;
    }

    public Rubro03 getRubro03Hasta() {
        return rubro03Hasta;
    }

    public void setRubro03Hasta(Rubro03 rubro03Hasta) {
        this.rubro03Hasta = rubro03Hasta;
    }

    public EntidadComercial getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(EntidadComercial destinatario) {
        this.destinatario = destinatario;
    }

    public String getSeIncluyeEnEstadisticas() {
        return seIncluyeEnEstadisticas;
    }

    public void setSeIncluyeEnEstadisticas(String seIncluyeEnEstadisticas) {
        this.seIncluyeEnEstadisticas = seIncluyeEnEstadisticas;
    }

    public String getComprobanteInterno() {
        return comprobanteInterno;
    }

    public void setComprobanteInterno(String comprobanteInterno) {
        this.comprobanteInterno = comprobanteInterno;
    }

    public CondicionDePagoVenta getCondicionDePagoVentas() {
        return condicionDePagoVentas;
    }

    public void setCondicionDePagoVentas(CondicionDePagoVenta condicionDePagoVentas) {
        this.condicionDePagoVentas = condicionDePagoVentas;
    }

    public CodigoCircuitoFacturacion getCircuitoDesde() {
        return circuitoDesde;
    }

    public void setCircuitoDesde(CodigoCircuitoFacturacion circuitoDesde) {
        this.circuitoDesde = circuitoDesde;
    }

    public CodigoCircuitoFacturacion getCircuitoHasta() {
        return circuitoHasta;
    }

    public void setCircuitoHasta(CodigoCircuitoFacturacion circuitoHasta) {
        this.circuitoHasta = circuitoHasta;
    }

    public EstadoPrestamo getEstadoPrestamo() {
        return estadoPrestamo;
    }

    public void setEstadoPrestamo(EstadoPrestamo estadoPrestamo) {
        this.estadoPrestamo = estadoPrestamo;
    }

    public LineaCredito getLineaCredito() {
        return lineaCredito;
    }

    public void setLineaCredito(LineaCredito lineaCredito) {
        this.lineaCredito = lineaCredito;
    }

    public Financiador getFinanciador() {
        return financiador;
    }

    public void setFinanciador(Financiador financiador) {
        this.financiador = financiador;
    }

    public Promotor getPromotor() {
        return promotor;
    }

    public void setPromotor(Promotor promotor) {
        this.promotor = promotor;
    }

    public String getAtributo1() {
        return atributo1;
    }

    public void setAtributo1(String atributo1) {
        this.atributo1 = atributo1;
    }

    public String getAtributo2() {
        return atributo2;
    }

    public void setAtributo2(String atributo2) {
        this.atributo2 = atributo2;
    }

    public String getAtributo3() {
        return atributo3;
    }

    public void setAtributo3(String atributo3) {
        this.atributo3 = atributo3;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public CondicionPagoProveedor getCondicionPagoProveedor() {
        return condicionPagoProveedor;
    }

    public void setCondicionPagoProveedor(CondicionPagoProveedor condicionPagoProveedor) {
        this.condicionPagoProveedor = condicionPagoProveedor;
    }

    public EntidadComercial getCliente2() {
        return cliente2;
    }

    public void setCliente2(EntidadComercial cliente2) {
        this.cliente2 = cliente2;
    }

    public EntidadComercial getCliente3() {
        return cliente3;
    }

    public void setCliente3(EntidadComercial cliente3) {
        this.cliente3 = cliente3;
    }

    public EntidadComercial getProveedor2() {
        return proveedor2;
    }

    public void setProveedor2(EntidadComercial proveedor2) {
        this.proveedor2 = proveedor2;
    }

    public EntidadComercial getProveedor3() {
        return proveedor3;
    }

    public void setProveedor3(EntidadComercial proveedor3) {
        this.proveedor3 = proveedor3;
    }

    public CodigoCircuitoCompra getCircuitoCompraDesde() {
        return circuitoCompraDesde;
    }

    public void setCircuitoCompraDesde(CodigoCircuitoCompra circuitoCompraDesde) {
        this.circuitoCompraDesde = circuitoCompraDesde;
    }

    public CodigoCircuitoCompra getCircuitoCompraHasta() {
        return circuitoCompraHasta;
    }

    public void setCircuitoCompraHasta(CodigoCircuitoCompra circuitoCompraHasta) {
        this.circuitoCompraHasta = circuitoCompraHasta;
    }

    public TipoConcepto getTipoConcepto() {
        return tipoConcepto;
    }

    public void setTipoConcepto(TipoConcepto tipoConcepto) {
        this.tipoConcepto = tipoConcepto;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

    public Concepto getConceptoDesde() {
        return conceptoDesde;
    }

    public void setConceptoDesde(Concepto conceptoDesde) {
        this.conceptoDesde = conceptoDesde;
    }

    public Concepto getConceptoHasta() {
        return conceptoHasta;
    }

    public void setConceptoHasta(Concepto conceptoHasta) {
        this.conceptoHasta = conceptoHasta;
    }

    public PeriodoContable getPeriodoContable() {
        return periodoContable;
    }

    public void setPeriodoContable(PeriodoContable periodoContable) {
        this.periodoContable = periodoContable;
    }

    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public CuentaContable getCuentaContableDesde() {
        return cuentaContableDesde;
    }

    public void setCuentaContableDesde(CuentaContable cuentaContableDesde) {
        this.cuentaContableDesde = cuentaContableDesde;
    }

    public CuentaContable getCuentaContableHasta() {
        return cuentaContableHasta;
    }

    public void setCuentaContableHasta(CuentaContable cuentaContableHasta) {
        this.cuentaContableHasta = cuentaContableHasta;
    }

    public PuntoVenta getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(PuntoVenta puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public String getTipoCosto() {
        return tipoCosto;
    }

    public void setTipoCosto(String tipoCosto) {
        this.tipoCosto = tipoCosto;
    }

    public String getComprometeStock() {
        return comprometeStock;
    }

    public void setComprometeStock(String comprometeStock) {
        this.comprometeStock = comprometeStock;
    }

    public Integer getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Integer idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Integer getIdMovimientoFacturacion() {
        return idMovimientoFacturacion;
    }

    public void setIdMovimientoFacturacion(Integer idMovimientoFacturacion) {
        this.idMovimientoFacturacion = idMovimientoFacturacion;
    }

    public Integer getIdMovimientoStock() {
        return idMovimientoStock;
    }

    public void setIdMovimientoStock(Integer idMovimientoStock) {
        this.idMovimientoStock = idMovimientoStock;
    }

    public String getAtributoStock1() {
        return atributoStock1;
    }

    public void setAtributoStock1(String atributoStock1) {
        this.atributoStock1 = atributoStock1;
    }

    public String getAtributoStock2() {
        return atributoStock2;
    }

    public void setAtributoStock2(String atributoStock2) {
        this.atributoStock2 = atributoStock2;
    }

    public String getAtributoStock3() {
        return atributoStock3;
    }

    public void setAtributoStock3(String atributoStock3) {
        this.atributoStock3 = atributoStock3;
    }

    public String getConAtributosStock() {
        return conAtributosStock;
    }

    public void setConAtributosStock(String conAtributosStock) {
        this.conAtributosStock = conAtributosStock;
    }

    public String getExluirProductoStockCero() {
        return exluirProductoStockCero;
    }

    public void setExluirProductoStockCero(String exluirProductoStockCero) {
        this.exluirProductoStockCero = exluirProductoStockCero;
    }

    public String getReprogramado() {
        return reprogramado;
    }

    public void setReprogramado(String reprogramado) {
        this.reprogramado = reprogramado;
    }

    public String getAtributo4() {
        return atributo4;
    }

    public void setAtributo4(String atributo4) {
        this.atributo4 = atributo4;
    }

    public String getAtributo5() {
        return atributo5;
    }

    public void setAtributo5(String atributo5) {
        this.atributo5 = atributo5;
    }

    public String getAtributo6() {
        return atributo6;
    }

    public void setAtributo6(String atributo6) {
        this.atributo6 = atributo6;
    }

    public String getAtributo7() {
        return atributo7;
    }

    public void setAtributo7(String atributo7) {
        this.atributo7 = atributo7;
    }

    public TipoComprobante getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(TipoComprobante tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
    }

    public boolean isDEBUG() {
        return DEBUG;
    }

    public void setDEBUG(boolean DEBUG) {
        this.DEBUG = DEBUG;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public Clasificacion01 getClasificacion01() {
        return clasificacion01;
    }

    public void setClasificacion01(Clasificacion01 clasificacion01) {
        this.clasificacion01 = clasificacion01;
    }

    public Clasificacion02 getClasificacion02() {
        return clasificacion02;
    }

    public void setClasificacion02(Clasificacion02 clasificacion02) {
        this.clasificacion02 = clasificacion02;
    }

    public Clasificacion03 getClasificacion03() {
        return clasificacion03;
    }

    public void setClasificacion03(Clasificacion03 clasificacion03) {
        this.clasificacion03 = clasificacion03;
    }

    public EntidadComercial getAlumno() {
        return alumno;
    }

    public void setAlumno(EntidadComercial alumno) {
        this.alumno = alumno;
    }

    public EntidadComercial getProfesor() {
        return profesor;
    }

    public void setProfesor(EntidadComercial profesor) {
        this.profesor = profesor;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public EstadoEducacion getEstadoEducacion() {
        return estadoEducacion;
    }

    public void setEstadoEducacion(EstadoEducacion estadoEducacion) {
        this.estadoEducacion = estadoEducacion;
    }

    public String getAtributoStock4() {
        return atributoStock4;
    }

    public void setAtributoStock4(String atributoStock4) {
        this.atributoStock4 = atributoStock4;
    }

    public String getAtributoStock5() {
        return atributoStock5;
    }

    public void setAtributoStock5(String atributoStock5) {
        this.atributoStock5 = atributoStock5;
    }

    public String getAtributoStock6() {
        return atributoStock6;
    }

    public void setAtributoStock6(String atributoStock6) {
        this.atributoStock6 = atributoStock6;
    }

    public String getIncluyeEnSubdiario() {
        return incluyeEnSubdiario;
    }

    public void setIncluyeEnSubdiario(String incluyeEnSubdiario) {
        this.incluyeEnSubdiario = incluyeEnSubdiario;
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public String getTipoItem() {
        return tipoItem;
    }

    public void setTipoItem(String tipoItem) {
        this.tipoItem = tipoItem;
    }

    public Producto getProceso() {
        return proceso;
    }

    public void setProceso(Producto proceso) {
        this.proceso = proceso;
    }

    public Operario getOperario() {
        return operario;
    }

    public void setOperario(Operario operario) {
        this.operario = operario;
    }

}
