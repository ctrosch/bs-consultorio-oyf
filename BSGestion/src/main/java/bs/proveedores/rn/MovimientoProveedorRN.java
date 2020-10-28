/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.rn;

import bs.administracion.rn.ModuloRN;
import bs.administracion.rn.ParametrosRN;
import bs.compra.modelo.ItemMovimientoCompra;
import bs.compra.modelo.MovimientoCompra;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.modelo.RetencionPorEntidad;
import bs.global.dao.ComprobanteDAO;
import bs.global.dao.ConceptoDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.ConceptoComprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.Moneda;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.FormularioRN;
import bs.global.rn.MonedaRN;
import bs.global.rn.PuntoVentaRN;
import bs.proveedores.dao.ProveedorDAO;
import bs.proveedores.modelo.ConceptoRetencion;
import bs.proveedores.modelo.ConceptoRetencionValor;
import bs.proveedores.modelo.ItemImpuestoProveedor;
import bs.proveedores.modelo.ItemPendienteCuentaCorrienteProveedor;
import bs.proveedores.modelo.ItemPercepcionProveedor;
import bs.proveedores.modelo.ItemProductoProveedor;
import bs.proveedores.modelo.ItemRetencionProveedor;
import bs.proveedores.modelo.ItemTotalProveedor;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.tesoreria.rn.MovimientoTesoreriaRN;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class MovimientoProveedorRN implements Serializable {

    @EJB
    private ProveedorDAO proveedorDAO;
    @EJB
    private ConceptoDAO conceptoDAO;
    @EJB
    private ComprobanteDAO comprobanteDAO;
    @EJB
    private MovimientoTesoreriaRN tesoreriaRN;
    @EJB
    private CuentaCorrienteProveedorRN cuentaCorrienteRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private MonedaRN monedaRN;
    @EJB
    private ModuloRN moduloRN;
    @EJB
    private PuntoVentaRN puntoVentaRN;
    @EJB
    private ParametrosRN parametrosRN;
    @EJB
    CondicionPagoProveedorRN condicionPagoProveedorRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized MovimientoProveedor guardar(MovimientoProveedor m) throws Exception {

        calcularImportes(m, false);
        control(m);
        limpiarConceptoEnCero(m);

        if (m.getId() != null) {
            m = (MovimientoProveedor) proveedorDAO.editar(m);
        } else {

            if (m.getImputacionCuentaCorriente().equals("CC")) {

                if (m.getComprobante().getEsComprobanteReversion().equals("N")) {
                    cuentaCorrienteRN.generarItemCuentaCorriente(m);
                } else {
                    cuentaCorrienteRN.generarItemsCuentaCorrienteReversionMovimiento(m);
                }
            }

            if (m.getComprobanteTesoreria() != null) {

                m.getMovimientoTesoreria().setFechaMovimiento(m.getFechaMovimiento());
                m.getMovimientoTesoreria().setEntidad(m.getProveedor());
                m.getMovimientoTesoreria().setNombreEntidad(m.getProveedor().getRazonSocial());

                tesoreriaRN.calcularImportes(m.getMovimientoTesoreria());

                if (m.getMovimientoTesoreria().getComprobante().getTipoComprobante().equals("I")
                        && m.getMovimientoTesoreria().getImporteTotalDebe().compareTo(m.getImporteTotal()) != 0) {
                    throw new ExcepcionGeneralSistema("El importe total del comprobante (" + m.getImporteTotal() + ") "
                            + "no coincide con el importe total de los medios de pagos (" + m.getMovimientoTesoreria().getImporteTotalDebe() + ")");
                }

                if (m.getMovimientoTesoreria().getComprobante().getTipoComprobante().equals("E")
                        && m.getMovimientoTesoreria().getImporteTotalHaber().compareTo(m.getImporteTotal()) != 0) {
                    throw new ExcepcionGeneralSistema("El importe total del comprobante (" + m.getImporteTotal() + ") "
                            + "no coincide con el importe total de los medios de pagos (" + m.getMovimientoTesoreria().getImporteTotalHaber() + ")");
                }

                tesoreriaRN.controlComprobante(m.getMovimientoTesoreria());
                tesoreriaRN.limpiarConceptoEnCero(m.getMovimientoTesoreria());
            }

            tomarNumeroFormulario(m);
            proveedorDAO.crear(m);
        }

        return m;
    }

    public ItemTotalProveedor nuevoItemTotal(MovimientoProveedor mp) {

        ItemTotalProveedor itp = new ItemTotalProveedor();

        itp.setNroItem(mp.getItemTotal().size() + 1);
        itp.setMovimiento(mp);
        itp.setConcepto(conceptoDAO.getConcepto("PV", "T", "DVT001"));
        itp.setCuentaContable(itp.getConcepto().getCuentaContable());
        itp.setDebeHaber("D");
        itp.setCantidad(BigDecimal.ZERO);
        itp.setCotizacion(mp.getCotizacion());
        mp.getItemTotal().add(itp);

        return itp;
    }

    public MovimientoProveedor nuevoMovimiento(String CODPV, String SUCURS, String CODCJ, String SUCCJ) throws Exception {

        Comprobante comprobante = comprobanteDAO.getComprobante("PV", CODPV);
        Comprobante comprobanteCJ = comprobanteDAO.getComprobante("CJ", CODCJ);

        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(SUCURS);

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No se encontró comprobante de proveedor (" + CODPV + ")");
        }
        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No se encontró punto de venta (" + SUCURS + ")");
        }

        MovimientoProveedor m = new MovimientoProveedor();
        Moneda moneda = monedaRN.getMoneda(parametrosRN.getParametro().getCodigoMonedaSecundaria());
        BigDecimal cotizacion = monedaRN.getCotizacionDia(parametrosRN.getParametro().getCodigoMonedaSecundaria());

        m.setEmpresa(puntoVenta.getEmpresa());
        m.setSucursal(puntoVenta.getSucursal());
        m.setPuntoVenta(puntoVenta);
        m.setUnidadNegocio(puntoVenta.getUnidadNegocio());

        m.setComprobante(comprobante);

        m.setImputacionCuentaCorriente(comprobante.getTipoImputacion());
        m.setObservaciones("");
        m.setFechaMovimiento(new Date());
        m.setFechaVencimiento(new Date());
        m.setMonedaSecundaria(moneda);
        m.setMonedaRegistracion(comprobante.getMonedaRegistracion());
        m.setCotizacion(cotizacion);

        if (comprobanteCJ != null) {
            m.setComprobanteTesoreria(comprobanteCJ);
            MovimientoTesoreria mt = tesoreriaRN.generarComprobante(m);
            m.setMovimientoTesoreria(mt);
        }

        generarItemsMovimiento(m);
        return m;
    }

    public MovimientoProveedor nuevoMovimiento(String CODPV, String SUCURS, String CNDIVA) throws Exception {

        Comprobante comprobante = comprobanteDAO.getComprobante("PV", CODPV);
        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(SUCURS);

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No se encontró comprobante de proveedor (" + CODPV + ")");
        }
        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No se encontró punto de venta (" + SUCURS + ")");
        }

        Formulario formulario = formularioRN.getFormulario(comprobante, puntoVenta, CNDIVA);
        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de venta para el comprobante (" + CODPV + ") "
                    + "para El punto de venta (" + SUCURS + ") "
                    + "con la condición de iva (" + CNDIVA + ")");
        }

        MovimientoProveedor m = new MovimientoProveedor();
        Moneda moneda = monedaRN.getMoneda(parametrosRN.getParametro().getCodigoMonedaSecundaria());
        BigDecimal cotizacion = monedaRN.getCotizacionDia(parametrosRN.getParametro().getCodigoMonedaSecundaria());

        m.setEmpresa(puntoVenta.getEmpresa());
        m.setSucursal(puntoVenta.getSucursal());
        m.setPuntoVenta(puntoVenta);
        m.setUnidadNegocio(puntoVenta.getUnidadNegocio());

        m.setComprobante(comprobante);
        m.setFormulario(formulario);
        m.setNumeroFormulario(formulario.getUltimoNumero() + 1);

        m.setImputacionCuentaCorriente(comprobante.getTipoImputacion());
        m.setObservaciones("");
        m.setFechaMovimiento(new Date());
        m.setFechaVencimiento(new Date());
        m.setMonedaSecundaria(moneda);
        m.setMonedaRegistracion(comprobante.getMonedaRegistracion());
        m.setCotizacion(cotizacion);
        return m;
    }

    public MovimientoProveedor generarComprobante(MovimientoCompra mc) throws ExcepcionGeneralSistema {

        MovimientoProveedor m = new MovimientoProveedor();

        m.setEmpresa(mc.getEmpresa());
        m.setSucursal(mc.getSucursal());
        m.setPuntoVenta(mc.getPuntoVenta());
        m.setUnidadNegocio(mc.getUnidadNegocio());

        m.setComprobante(mc.getComprobante());
        m.setFormulario(mc.getFormulario());
        m.setNumeroFormulario(mc.getNumeroFormulario());

        m.setImputacionCuentaCorriente(mc.getComprobante().getTipoImputacion());
        m.setObservaciones(mc.getObservaciones());
        m.setFechaMovimiento(mc.getFechaMovimiento());
        m.setFechaEmision(mc.getFechaEmision());
        m.setFechaRecepcion(mc.getFechaRecepcion());
        m.setFechaVencimiento(mc.getFechaMovimiento());

        m.setPuntoVentaOriginal(mc.getPuntoVentaOriginal());
        m.setNumeroOriginal(mc.getNumeroOriginal());
        m.setCodigoAutorizacion(mc.getCodigoAutorizacion());

        m.setProveedor(mc.getProveedor());
        m.setProveedorCuentaCorriente(mc.getProveedorCuentaCorriente());
        m.setRazonSocial(mc.getRazonSocial());
        m.setTipoDocumento(mc.getTipoDocumento());
        m.setNrodoc(mc.getNrodoc());

        m.setProvincia(mc.getProvincia());
        m.setLocalidad(mc.getLocalidad());
        m.setDireccion(mc.getDireccion());
        m.setNumero(mc.getNumero());
        m.setDepartamentoPiso(mc.getDepartamentoPiso());
        m.setDepartamentoNumero(mc.getDepartamentoNumero());

        m.setCondicionDePago(mc.getCondicionDePago());
        m.setComprador(mc.getComprador());
        m.setListaCosto(mc.getListaCosto());
        m.setMonedaListaCosto(mc.getMonedaListaCosto());
        m.setMonedaSecundaria(mc.getMonedaSecundaria());
        m.setMonedaRegistracion(mc.getMonedaRegistracion());
        m.setCotizacion(mc.getCotizacion());
        m.setCondicionDeIva(mc.getCondicionDeIva());

        generarItemProducto(mc, m);
        generarItemImpuesto(mc, m);
        generarItemPercepcion(mc, m);
        generarItemTotal(mc, m);

        if (m.getComprobante().getTipoImputacion().equals("CC")) {

            cuentaCorrienteRN.generarItemCuentaCorriente(m);
        }

        return m;
    }

    /**
     * Generamos un comprobante de proveedor atravez de un comprobante de
     * tesorería.
     *
     * @param mt Movimiento de tesorería
     * @return Movimiento de Proveedor
     * @throws ExcepcionGeneralSistema
     */
    public MovimientoProveedor generarComprobante(MovimientoTesoreria mt) throws ExcepcionGeneralSistema {

        Formulario formulario = formularioRN.getFormulario(mt.getComprobanteProveedor(), mt.getPuntoVenta(), mt.getEntidad().getCondicionDeIva().getCodigo());

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de proveedor para el comprobante (" + mt.getComprobanteProveedor().getCodigo() + ") "
                    + "para El punto de venta (" + mt.getPuntoVenta() + ") "
                    + "con la condición de iva (" + mt.getEntidad().getCondicionDeIva().getCodigo() + ")");
        }

        MovimientoProveedor m;
        m = new MovimientoProveedor();

        m.setEmpresa(mt.getEmpresa());
        m.setSucursal(mt.getSucursal());
        m.setPuntoVenta(mt.getPuntoVenta());
        m.setUnidadNegocio(mt.getUnidadNegocio());

        m.setComprobante(mt.getComprobanteProveedor());
        m.setFormulario(formulario);
        m.setNumeroFormulario(mt.getNumeroFormulario());

        m.setImputacionCuentaCorriente(mt.getComprobanteProveedor().getTipoImputacion());
        m.setObservaciones(mt.getObservaciones());
        m.setFechaMovimiento(mt.getFechaMovimiento());
        m.setFechaVencimiento(mt.getFechaMovimiento());

        m.setProveedor(mt.getEntidad());
        m.setProveedorCuentaCorriente(mt.getEntidad());
        m.setRazonSocial(mt.getEntidad().getRazonSocial());

        m.setTipoDocumento(mt.getEntidad().getTipoDocumento());
        m.setNrodoc(mt.getEntidad().getNrodoc());
        m.setCondicionDePago(mt.getEntidad().getCondicionPagoProveedor());
        m.setCondicionDeIva(mt.getEntidad().getCondicionDeIva());
        m.setProvincia(mt.getEntidad().getProvincia());
        m.setLocalidad(mt.getEntidad().getLocalidad());
        m.setDireccion(mt.getEntidad().getDireccion());
        m.setNumero(mt.getEntidad().getNumero());
        m.setDepartamentoPiso(mt.getEntidad().getDepartamentoPiso());
        m.setDepartamentoNumero(mt.getEntidad().getDepartamentoNumero());

        m.setComprador(mt.getEntidad().getComprador());
        m.setListaCosto(mt.getEntidad().getListaCosto());
        m.setMonedaListaCosto(mt.getEntidad().getListaCosto().getMoneda());

        m.setMonedaSecundaria(mt.getMonedaSecundaria());
        m.setMonedaRegistracion(mt.getMonedaRegistracion());

        m.setCotizacion(mt.getCotizacion());

        m.setEsAnticipo(mt.isEsAnticipo());

        //Les paso las retenciones generadas para poder aplicarlas
        m.setRetenciones(mt.getRetenciones());
        asignarMovimientoRetencion(m);

        generarItemTotal(mt, m);

        /**
         * if (m.getComprobante().getEsComprobanteReversion().equals("S")) {
         * m.setMovimientoReversion(mt.getMovimientoReversion().getMovimientoProveedor());
         * }
         */
        if (m.getComprobante().getEsComprobanteReversion().equals("N")) {
            cuentaCorrienteRN.generarItemCuentaCorrienteOrdenPago(m, mt.getCreditos());
        } else {
            cuentaCorrienteRN.generarItemsCuentaCorrienteReversionMovimiento(m);
        }

        return m;
    }

    //if(mt.isEsAnticipo() || mt.getComprobante().getTipoComprobante().equals("R")){
//        if(mt.isEsAnticipo()){
//            cuentaCorrienteRN.generarItemCuentaCorrienteAnticipo(m);
//        }else{
//            if(!m.getComprobante().getEsComprobanteReversion().equals("S")){
//                cuentaCorrienteRN.generarItemCuentaCorrienteOrdenPago(m,mt.getCreditos());
//            }else{
//                cuentaCorrienteRN.generarItemsCuentaCorrienteReversionMovimiento(m);
//            }
//        }
    /**
     * Se implementó esta solución para salir del paso con metalúrgica
     * avellaneda
     *
     * @param mt
     * @param importeRetencion
     * @return
     * @throws ExcepcionGeneralSistema
     */
    public MovimientoProveedor generarComprobanteRetencion(MovimientoTesoreria mt, ConceptoRetencion conceptoRetencion, BigDecimal baseImponible, BigDecimal porcentaje, BigDecimal importeRetencion) throws ExcepcionGeneralSistema {

        if (conceptoRetencion == null) {
            throw new ExcepcionGeneralSistema("Concepto de retención nulo");
        }

        if (conceptoRetencion.getComprobanteCompra() == null) {
            throw new ExcepcionGeneralSistema("Concepto de retención sin comprobante de proveedor asociado");
        }

        Formulario formulario = formularioRN.getFormulario(conceptoRetencion.getComprobanteCompra(), mt.getPuntoVenta(), mt.getEntidad().getCondicionDeIva().getCodigo());

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de proveedor para el comprobante (" + mt.getComprobante().getCodigo() + ") "
                    + "para El punto de venta (" + mt.getPuntoVenta() + ") "
                    + "con la condición de iva (" + mt.getEntidad().getCondicionDeIva().getCodigo() + ")");
        }

        //Integer ultimoNumero = formularioRN.tomarProximoNumero(formulario);
        //m.setNumeroFormulario(ultimoNumero);
        MovimientoProveedor m;
        m = new MovimientoProveedor();

        m.setEmpresa(mt.getEmpresa());
        m.setSucursal(mt.getSucursal());
        m.setPuntoVenta(mt.getPuntoVenta());
        m.setUnidadNegocio(mt.getUnidadNegocio());

        m.setComprobante(conceptoRetencion.getComprobanteCompra());
        m.setFormulario(formulario);
        m.setNumeroFormulario(formulario.getUltimoNumero() + 1);

        m.setImputacionCuentaCorriente(conceptoRetencion.getComprobanteCompra().getTipoImputacion());
        m.setObservaciones(mt.getObservaciones());
        m.setFechaMovimiento(mt.getFechaMovimiento());
        m.setFechaVencimiento(mt.getFechaMovimiento());

        m.setProveedor(mt.getEntidad());
        m.setProveedorCuentaCorriente(mt.getEntidad());
        m.setRazonSocial(mt.getEntidad().getRazonSocial());

        m.setTipoDocumento(mt.getEntidad().getTipoDocumento());
        m.setNrodoc(mt.getEntidad().getNrodoc());
        m.setCondicionDePago(mt.getEntidad().getCondicionPagoProveedor());
        m.setCondicionDeIva(mt.getEntidad().getCondicionDeIva());
        m.setProvincia(mt.getEntidad().getProvincia());
        m.setLocalidad(mt.getEntidad().getLocalidad());
        m.setDireccion(mt.getEntidad().getDireccion());
        m.setNumero(mt.getEntidad().getNumero());
        m.setDepartamentoPiso(mt.getEntidad().getDepartamentoPiso());
        m.setDepartamentoNumero(mt.getEntidad().getDepartamentoNumero());

        m.setComprador(mt.getEntidad().getComprador());
        m.setListaCosto(mt.getEntidad().getListaCosto());
        m.setMonedaListaCosto(mt.getEntidad().getListaCosto().getMoneda());

        m.setMonedaSecundaria(mt.getMonedaSecundaria());
        m.setMonedaRegistracion(mt.getMonedaRegistracion());
        m.setCotizacion(mt.getCotizacion());

        m.setImporteTotal(importeRetencion);
        generarItemsComprobanteRetenciones(m, conceptoRetencion, baseImponible, porcentaje, importeRetencion);

        //cuentaCorrienteRN.generarItemCuentaCorrienteAnticipo(m);
        return m;
    }

    public void generarItemsMovimiento(MovimientoProveedor m) throws Exception {

        if (m.getComprobante() == null) {
            throw new ExcepcionGeneralSistema("El comprobante no puede estar vacío");
        }

        if (m.getComprobante().getConceptos() == null || m.getComprobante().getConceptos().isEmpty()) {
            throw new ExcepcionGeneralSistema("El comprobante no tienen conceptos asociados");
        }

        for (ConceptoComprobante cc : m.getComprobante().getConceptos()) {

            if (cc.getConcepto().getTipo().equals("A")) {

                ItemProductoProveedor ipv = new ItemProductoProveedor();

                ipv.setNroItem(m.getItemProducto().size() + 1);
                ipv.setMovimiento(m);
                ipv.setConcepto(cc.getConcepto());

                if (cc.getCuentaContable() != null) {
                    ipv.setCuentaContable(cc.getCuentaContable());
                } else {
                    ipv.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                ipv.setCotizacion(m.getCotizacion());
                ipv.setDebeHaber(cc.getDebeHaber());
                m.getItemProducto().add(ipv);
            }

            if (cc.getConcepto().getTipo().equals("I")) {

                ItemImpuestoProveedor ipv = new ItemImpuestoProveedor();

                ipv.setNroItem(m.getItemImpuesto().size() + 1);
                ipv.setMovimiento(m);
                ipv.setConcepto(cc.getConcepto());
                ipv.setEditaImporte(cc.getEditaImputacion());

                if (cc.getCuentaContable() != null) {
                    ipv.setCuentaContable(cc.getCuentaContable());
                } else {
                    ipv.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                ipv.setCotizacion(m.getCotizacion());
                ipv.setDebeHaber(cc.getDebeHaber());
                m.getItemImpuesto().add(ipv);
            }

            if (cc.getConcepto().getTipo().equals("P")) {

                ItemPercepcionProveedor ipv = new ItemPercepcionProveedor();

                ipv.setNroItem(m.getItemPercepcion().size() + 1);
                ipv.setMovimiento(m);
                ipv.setConcepto(cc.getConcepto());
                ipv.setEditaImporte(cc.getEditaImputacion());

                if (cc.getCuentaContable() != null) {
                    ipv.setCuentaContable(cc.getCuentaContable());
                } else {
                    ipv.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                ipv.setCotizacion(m.getCotizacion());
                ipv.setDebeHaber(cc.getDebeHaber());
                m.getItemPercepcion().add(ipv);
            }

            if (cc.getConcepto().getTipo().equals("R")) {

                ItemRetencionProveedor ipv = new ItemRetencionProveedor();

                ipv.setNroItem(m.getItemRetencion().size() + 1);
                ipv.setMovimiento(m);
                ipv.setConcepto(cc.getConcepto());
                ipv.setEditaImporte(cc.getEditaImputacion());

                if (cc.getCuentaContable() != null) {
                    ipv.setCuentaContable(cc.getCuentaContable());
                } else {
                    ipv.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                ipv.setCotizacion(m.getCotizacion());
                ipv.setDebeHaber(cc.getDebeHaber());
                m.getItemRetencion().add(ipv);
            }

            if (cc.getConcepto().getTipo().equals("T")) {

                ItemTotalProveedor ipv = new ItemTotalProveedor();

                ipv.setNroItem(m.getItemTotal().size() + 1);
                ipv.setMovimiento(m);
                ipv.setConcepto(cc.getConcepto());

                if (cc.getCuentaContable() != null) {
                    ipv.setCuentaContable(cc.getCuentaContable());
                } else {
                    ipv.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                ipv.setCotizacion(m.getCotizacion());
                ipv.setDebeHaber(cc.getDebeHaber());
                m.getItemTotal().add(ipv);

            }
        }
    }

    public void generarItemsMovimientoProveedor(MovimientoCompra m) throws ExcepcionGeneralSistema {

        if (m.getComprobanteProveedor() == null) {
            throw new ExcepcionGeneralSistema("El comprobante de proveedor no puede ser nulo, verifique la configuración del circuito");
        }

        if (m.getComprobanteProveedor().getConceptos() == null || m.getComprobanteProveedor().getConceptos().isEmpty()) {
            throw new ExcepcionGeneralSistema("El comprobante de proveedor no tiene conceptos asociados");
        }

        if (m.getItemsProductoProveedor() == null) {
            m.setItemsProductoProveedor(new ArrayList<ItemProductoProveedor>());
        } else {
            m.getItemsProductoProveedor().clear();
        }

        if (m.getItemsImpuestoProveedor() == null) {
            m.setItemsImpuestoProveedor(new ArrayList<ItemImpuestoProveedor>());
        } else {
            m.getItemsImpuestoProveedor().clear();
        }

        if (m.getItemsPercepcionProveedor() == null) {
            m.setItemsPercepcionProveedor(new ArrayList<ItemPercepcionProveedor>());
        } else {
            m.getItemsPercepcionProveedor().clear();
        }

        if (m.getItemsRetencionProveedor() == null) {
            m.setItemsRetencionProveedor(new ArrayList<ItemRetencionProveedor>());
        } else {
            m.getItemsRetencionProveedor().clear();
        }

        if (m.getItemsTotalProveedor() == null) {
            m.setItemsTotalProveedor(new ArrayList<ItemTotalProveedor>());
        } else {
            m.getItemsTotalProveedor().clear();
        }

        for (ConceptoComprobante cc : m.getComprobanteProveedor().getConceptos()) {

//            System.out.println("cc" + cc);
            if (cc.getConcepto().getTipo().equals("A")) {

                ItemProductoProveedor id = new ItemProductoProveedor();
                id.setNroItem(m.getItemsProductoProveedor().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (cc.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                m.getItemsProductoProveedor().add(id);
            }

            if (cc.getConcepto().getTipo().equals("I")) {

                ItemImpuestoProveedor id = new ItemImpuestoProveedor();
                id.setNroItem(m.getItemsImpuestoProveedor().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());
                if (cc.getTipoImpuesto() != null) {
                    id.setCodigoTipoImpuesto(cc.getTipoImpuesto().getCodigo());
                }
                id.setEditaImporte(cc.getEditaSiCero());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (cc.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                m.getItemsImpuestoProveedor().add(id);
            }

            if (cc.getConcepto().getTipo().equals("P")) {

                ItemPercepcionProveedor id = new ItemPercepcionProveedor();
                id.setNroItem(m.getItemsPercepcionProveedor().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());

                if (cc.getTipoImpuesto() != null) {
                    id.setCodigoTipoImpuesto(cc.getTipoImpuesto().getCodigo());
                }

                id.setEditaImporte(cc.getEditaSiCero());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (cc.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                m.getItemsPercepcionProveedor().add(id);
            }

            if (cc.getConcepto().getTipo().equals("R")) {

                ItemRetencionProveedor id = new ItemRetencionProveedor();
                id.setNroItem(m.getItemsRetencionProveedor().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());
                if (cc.getTipoImpuesto() != null) {
                    id.setCodigoTipoImpuesto(cc.getTipoImpuesto().getCodigo());
                }
                id.setEditaImporte(cc.getEditaSiCero());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (cc.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                m.getItemsRetencionProveedor().add(id);
            }

            if (cc.getConcepto().getTipo().equals("T")) {

                ItemTotalProveedor id = new ItemTotalProveedor();
                id.setNroItem(m.getItemsTotalProveedor().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (cc.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                m.getItemsTotalProveedor().add(id);
            }
        }
    }

    private void generarItemProducto(MovimientoCompra mc, MovimientoProveedor mp) throws ExcepcionGeneralSistema {

        if (mc.getItemsProducto() == null) {
            return;
        }

        for (ItemMovimientoCompra ipc : mc.getItemsProducto()) {

            ItemProductoProveedor ipp = new ItemProductoProveedor();

            ipp.setNroItem(ipc.getNroitm());
            ipp.setMovimiento(mp);

            ipp.setProducto(ipc.getProducto());
            ipp.setDescripcion(ipc.getDescripcion());
            ipp.setDeposito(ipc.getDeposito());
            ipp.setKilogramosAforado(ipc.getKilogramosAforado());
            ipp.setKilogramosEfectivo(ipc.getKilogramosEfectivo());

            ipp.setConcepto(ipc.getConcepto());
            ipp.setCuentaContable(ipp.getProducto().getCuentaContableVenta());

            ipp.setUnidadMedida(ipc.getUnidadMedida());
            ipp.setCantidad(ipc.getCantidad());
            ipp.setValorDeclarado(ipc.getValorDeclarado());
            ipp.setCotizacion(ipc.getCotizacion());
            ipp.setObservaciones(ipc.getObservaciones());

            ipp.setPrecio(ipc.getPrecio());
            ipp.setPrecioSecundario(ipc.getPrecioSecundario());

            ipp.setPorcentajeBonificacion1(ipc.getPorcentajeBonificacion1());
            ipp.setPorcentajeBonificacion2(ipc.getPorcentajeBonificacion2());
            ipp.setPorcentajeBonificacion3(ipc.getPorcentajeBonificacion3());
            ipp.setPorcentajeBonificacion4(ipc.getPorcentajeBonificacion4());
            ipp.setPorcentajeBonificacion5(ipc.getPorcentajeBonificacion5());
            ipp.setPorcentajeBonificacion6(ipc.getPorcentajeBonificacion6());
            ipp.setPorcentaTotalBonificacion(ipc.getPorcentaTotalBonificacion());

            ipp.setImporte(ipc.getTotalLinea());
            ipp.setImporteSecundario(ipc.getTotalLineaSecundario());

            //Si el comprobante de compras es Debe, los productos van al Haber
            if (mp.getComprobante().getDebeHaber().equals("D")) {
                ipp.setDebeHaber("H");
                ipp.setImporteHaber(ipc.getTotalLinea());
                ipp.setImporteHaberSecundario(ipc.getTotalLineaSecundario());
            } else {
                ipp.setDebeHaber("D");
                ipp.setImporteDebe(ipc.getTotalLinea());
                ipp.setImporteDebeSecundario(ipc.getTotalLineaSecundario());
            }

            mp.getItemProducto().add(ipp);
        }
    }

    private void generarItemImpuesto(MovimientoCompra mc, MovimientoProveedor mp) {

        mp.getItemImpuesto().clear();

        for (ItemImpuestoProveedor iip : mc.getItemsImpuestoProveedor()) {

            if (iip.getImporte().compareTo(BigDecimal.ZERO) != 0) {

                iip.setNroItem(mp.getItemImpuesto().size() + 1);
                iip.setMovimiento(mp);
                iip.setCantidad(BigDecimal.ZERO);
                iip.setCotizacion(mp.getCotizacion());

                //Si el comprobante de ventas es Debe, los impuestos van al Haber
                if (iip.getDebeHaber().equals("H")) {
                    iip.setImporteHaber(iip.getImporte());
                    iip.setImporteHaberSecundario(iip.getImporteSecundario());
                } else {
                    iip.setImporteDebe(iip.getImporte());
                    iip.setImporteDebeSecundario(iip.getImporteSecundario());
                }
                mp.getItemImpuesto().add(iip);
            }
        }
    }

    private void generarItemPercepcion(MovimientoCompra mc, MovimientoProveedor mp) {

        mp.getItemPercepcion().clear();

        for (ItemPercepcionProveedor ipp : mc.getItemsPercepcionProveedor()) {

            if (ipp.getImporte().compareTo(BigDecimal.ZERO) != 0) {

                ipp.setNroItem(mp.getItemPercepcion().size() + 1);
                ipp.setMovimiento(mp);
                ipp.setCantidad(BigDecimal.ZERO);
                ipp.setCotizacion(mp.getCotizacion());

                //Si el comprobante de ventas es Debe, los impuestos van al Haber
                if (ipp.getDebeHaber().equals("H")) {
                    ipp.setImporteHaber(ipp.getImporte());
                    ipp.setImporteHaberSecundario(ipp.getImporteSecundario());
                } else {
                    ipp.setImporteDebe(ipp.getImporte());
                    ipp.setImporteDebeSecundario(ipp.getImporteSecundario());
                }
                mp.getItemPercepcion().add(ipp);
            }
        }
    }

    /**
     * Genera item total desde un movimiento de facturación
     */
    private void generarItemTotal(MovimientoCompra mc, MovimientoProveedor mp) {

        mp.getItemTotal().clear();

        for (ItemTotalProveedor itv : mc.getItemsTotalProveedor()) {

            if (itv.getImporte().compareTo(BigDecimal.ZERO) != 0) {

                itv.setNroItem(mp.getItemImpuesto().size() + 1);
                itv.setMovimiento(mp);
                itv.setCantidad(BigDecimal.ZERO);
                itv.setCotizacion(mp.getCotizacion());

                //Si el comprobante de ventas es Debe, los impuestos van al Haber
                if (itv.getDebeHaber().equals("H")) {
                    itv.setImporteHaber(itv.getImporte());
                    itv.setImporteHaberSecundario(itv.getImporteSecundario());
                } else {
                    itv.setImporteDebe(itv.getImporte());
                    itv.setImporteDebeSecundario(itv.getImporteSecundario());
                }
                mp.getItemTotal().add(itv);
            }
        }
    }

    private void generarItemTotal(MovimientoTesoreria mt, MovimientoProveedor mp) {

        mp.getItemTotal().clear();

        BigDecimal totalRetenciones = cuentaCorrienteRN.sumarSaldosRetenciones(mt.getCreditos());

        mp.setImporteRetenciones(totalRetenciones);
        mp.setImporteTotal(mt.getImporteTotalHaber().add(totalRetenciones));

        //Item Debe
        ItemTotalProveedor itvd = new ItemTotalProveedor();
        itvd.setNroItem(mp.getItemTotal().size() + 1);
        itvd.setMovimiento(mp);
        itvd.setDebeHaber("D");
        itvd.setConcepto(conceptoDAO.getConcepto("PV", "T", "PVR001"));
        itvd.setCuentaContable(itvd.getConcepto().getCuentaContable());
        itvd.setCantidad(BigDecimal.ZERO);
        itvd.setCotizacion(mp.getCotizacion());

        // Item Haber
        ItemTotalProveedor itvh = new ItemTotalProveedor();
        itvh.setNroItem(mp.getItemTotal().size() + 1);
        itvh.setMovimiento(mp);
        itvh.setDebeHaber("H");
        itvh.setConcepto(conceptoDAO.getConcepto("PV", "T", "CTR001"));
        itvh.setCuentaContable(itvh.getConcepto().getCuentaContable());
        itvh.setCantidad(BigDecimal.ZERO);
        itvh.setCotizacion(mp.getCotizacion());

        BigDecimal importeDebe = BigDecimal.ZERO;
        BigDecimal importeHaber = BigDecimal.ZERO;

        importeDebe = mp.getImporteTotal();
        importeHaber = mp.getImporteTotal();

//        if (mt.getComprobante().getTipoComprobante().equals("T")) {
//
//            importeDebe = mt.getImporteTotalDebe();
//            importeHaber = mt.getImporteTotalHaber();
//
//        }
//
//        if (mt.getComprobante().getTipoComprobante().equals("E")) {
//
//            importeDebe = mt.getImporteTotalHaber().add(totalRetenciones);
//            importeHaber = mt.getImporteTotalHaber().add(totalRetenciones);
//
//        }
//
//        if (mt.getComprobante().getTipoComprobante().equals("I")) {
//
//            importeDebe = mt.getImporteTotalDebe();
//            importeHaber = mt.getImporteTotalDebe();
//
//        }
        //Movimento de reversión tiene que intercambiar los conceptos
        if (mt.getComprobante().getTipoComprobante().equals("R")) {

            if (mt.getImporteTotalDebe().compareTo(BigDecimal.ZERO) > 0) {
                importeDebe = mt.getImporteTotalDebe();
                importeHaber = mt.getImporteTotalDebe();
            } else {
                importeDebe = mt.getImporteTotalHaber();
                importeHaber = mt.getImporteTotalHaber();
            }

            itvd.setConcepto(conceptoDAO.getConcepto("PV", "T", "PVR001"));
            itvd.setCuentaContable(itvd.getConcepto().getCuentaContable());
            itvh.setConcepto(conceptoDAO.getConcepto("PV", "T", "CTR001"));
            itvh.setCuentaContable(itvh.getConcepto().getCuentaContable());

        }

        itvd.setImporte(importeDebe);
        itvd.setImporteSecundario(importeDebe.divide(mt.getCotizacion(), 2, RoundingMode.HALF_UP));
        itvd.setImporteDebe(importeDebe);
        itvd.setImporteDebeSecundario(importeDebe.divide(mt.getCotizacion(), 2, RoundingMode.HALF_UP));

        itvh.setImporte(importeHaber);
        itvh.setImporteSecundario(importeHaber.divide(mt.getCotizacion(), 2, RoundingMode.HALF_UP));
        itvh.setImporteHaber(importeHaber);
        itvh.setImporteHaberSecundario(importeHaber.divide(mt.getCotizacion(), 2, RoundingMode.HALF_UP));

        //Agrego los items generados
        mp.getItemTotal().add(itvd);
        mp.getItemTotal().add(itvh);

    }

    public void control(MovimientoProveedor m) throws ExcepcionGeneralSistema {

        String sErrores = "";

        sErrores += moduloRN.canSaveModulo("PV", m.getFechaMovimiento());

        if (m.getMovimientoContabilidad() != null) {
            throw new ExcepcionGeneralSistema("El comprobante fue pasado a la contabilidad, no es posible guardar");
        }

        if (m.getProveedor() == null) {
            sErrores += "| El proveedor no puede estar vacío ";
        }

        if (m.getDireccion() == null || m.getDireccion().isEmpty()) {
            sErrores += "| Ingrese una dirección válida ";
        }

        if (m.getLocalidad() == null) {
            sErrores += "| Ingrese una localidad válida ";
        }

        if (m.getNrodoc() == null || m.getNrodoc().isEmpty()) {
            sErrores += "| Ingrese el número de documento del proveedor ";
        }

        if (m.getCondicionDePago().getImputaCuentaCorriente().equals("N") && m.getImputacionCuentaCorriente().equals("CC")) {
            sErrores += "| La condición de pago asignada al comprobante no permite generar comprobantes en cuenta corriente. Por favor modifique la condición de pago ";
        }

        if (m.getComprobante().getComprobanteInterno().equals("N")
                && m.getProveedor().getPideCodigoAutorizacion().equals("S")
                && m.getComprobante().getSeIncluyeEnSubDiario().equals("S")
                && ((m.getNrocae() == null || m.getNrocae().isEmpty()) || (m.getVencae() == null))) {
            sErrores += "| Ingrese el número de CAE/CAI y su fecha de vencimiento. ";
        }

        if (m.getComprobante().getEsComprobanteReversion().equals("S") && m.getMovimientoReversion() == null) {
            sErrores += "| El movimiento es de reversión y no tienen asignado el movimiento a revertir ";
        }

        if (m.getImporteTotalDebe().compareTo(BigDecimal.ZERO) < 0 || m.getImporteTotalHaber().compareTo(BigDecimal.ZERO) < 0) {

            sErrores += "| Los importes \"Debe\" y \"Haber\" tienen que ser mayor a cero ";
        }

        if (m.getImporteTotalDebe().compareTo(m.getImporteTotalHaber()) != 0) {
            sErrores += "| Los importes \"Debe\" y \"Haber\" en el comprobante de proveedor tienen que ser iguales ";
        }

        if (m.getId() == null
                && m.getPuntoVentaOriginal() != null
                && m.getNumeroOriginal() != null
                && m.getComprobante().getEsComprobanteReversion().equals("N")) {

            m.setPuntoVentaOriginal("0000" + m.getPuntoVentaOriginal());
            m.setPuntoVentaOriginal(m.getPuntoVentaOriginal().substring(m.getPuntoVentaOriginal().length() - 4, m.getPuntoVentaOriginal().length()));

            m.setNumeroOriginal("00000000" + m.getNumeroOriginal());
            m.setNumeroOriginal(m.getNumeroOriginal().substring(m.getNumeroOriginal().length() - 8, m.getNumeroOriginal().length()));

            if (proveedorDAO.getMovimientoByPuntoVentaNumeroOriginal(m.getProveedor().getNrocta(), m.getPuntoVentaOriginal(), m.getNumeroOriginal()) != null) {
                sErrores += "| El punto de venta y número de comprobante ingresados, han sido cargados en otro comprobante ";
            }
        }

        for (ItemProductoProveedor ip : m.getItemProducto()) {

            ip.setConError(false);

            if (ip.getConcepto() == null) {
                ip.setConError(true);
                sErrores += "| El item " + ip.getNroItem() + " no tienen asignado el concepto asociado ";
            }

            if (ip.getImporte() != null && ip.getImporte().compareTo(BigDecimal.ZERO) > 0) {

                if (ip.getCuentaContable() == null) {
                    ip.setConError(true);
                    sErrores += "| El item producto " + ip.getNroItem() + " no tienen asignada la cuenta contable ";
                } else {

                    if (ip.getCuentaContable().getImputable().equals("N")) {

                        ip.setConError(true);
                        sErrores += "| La cuenta contable asignadas al item producto " + ip.getNroItem() + ", no es una cuenta imputable";
                    }
                }
            }
        }

        for (ItemImpuestoProveedor ii : m.getItemImpuesto()) {

            ii.setConError(false);

            if (ii.getConcepto() == null) {
                ii.setConError(true);
                sErrores += "| El item impuesto " + ii.getNroItem() + " no tienen asignado el concepto ";
            }

            if (ii.getCuentaContable() == null) {
                ii.setConError(true);
                sErrores += "| El item impuesto " + ii.getNroItem() + " no tienen asignada la cuenta contable ";
            }
        }

        for (ItemPercepcionProveedor ip : m.getItemPercepcion()) {

            ip.setConError(false);

            if (ip.getConcepto() == null) {
                ip.setConError(true);
                sErrores += "| El item percepción " + ip.getNroItem() + " no tienen asignado el concepto ";
            }

            if (ip.getCuentaContable() == null) {
                ip.setConError(true);
                sErrores += "| El item percepción " + ip.getNroItem() + " no tienen asignada la cuenta contable ";
            }
        }

        for (ItemTotalProveedor it : m.getItemTotal()) {

            it.setConError(false);

            if (it.getConcepto() == null) {
                it.setConError(true);
                sErrores += "| El item total " + it.getNroItem() + " no tienen asignado el concepto ";
            }

            if (it.getCuentaContable() == null) {
                it.setConError(true);
                sErrores += "| El item total " + it.getNroItem() + " no tienen asignada la cuenta contable ";
            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    private void generarItemsComprobanteRetenciones(MovimientoProveedor mp, ConceptoRetencion conceptoRetencion, BigDecimal baseImponible, BigDecimal porcentaje, BigDecimal impRetencion) throws ExcepcionGeneralSistema {

        mp.getItemTotal().clear();
        mp.getItemProducto().clear();

        if (mp.getComprobante().getConceptos() == null || mp.getComprobante().getConceptos().isEmpty()) {
            throw new ExcepcionGeneralSistema("El comprobante de retenciones no tienen conceptos configurados");
        }

        //Item Debe
        ItemTotalProveedor itvd = new ItemTotalProveedor();

        // Item Haber
        ItemProductoProveedor itvh = new ItemProductoProveedor();

        for (ConceptoComprobante cc : mp.getComprobante().getConceptos()) {

            if (cc.getTipcpt().equals("A")) {

                itvh.setNroItem(mp.getItemProducto().size() + 1);
                itvh.setMovimiento(mp);
                itvh.setDebeHaber("H");
                itvh.setConcepto(cc.getConcepto());
                itvh.setConceptoRetencion(conceptoRetencion);

                if (cc.getCuentaContable() != null) {

                    itvh.setCuentaContable(cc.getCuentaContable());

                } else if (cc.getConcepto().getCuentaContable() != null) {

                    itvh.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                itvh.setCantidad(BigDecimal.ZERO);
                itvh.setCotizacion(mp.getCotizacion());

                itvh.setBaseImponible(baseImponible);
                itvh.setAlicuota(porcentaje);
                itvh.setImporte(impRetencion);
                itvh.setImporteSecundario(impRetencion.divide(mp.getCotizacion(), 2, RoundingMode.HALF_UP));
                itvh.setImporteHaber(impRetencion);
                itvh.setImporteHaberSecundario(impRetencion.divide(mp.getCotizacion(), 2, RoundingMode.HALF_UP));

            }

            if (cc.getTipcpt().equals("T")) {

                itvd.setNroItem(mp.getItemTotal().size() + 1);
                itvd.setMovimiento(mp);
                itvd.setDebeHaber("D");
                itvd.setConcepto(cc.getConcepto());

                if (cc.getCuentaContable() != null) {

                    itvd.setCuentaContable(cc.getCuentaContable());

                } else if (cc.getConcepto().getCuentaContable() != null) {

                    itvd.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                itvd.setCantidad(BigDecimal.ZERO);
                itvd.setCotizacion(mp.getCotizacion());

                itvd.setImporte(impRetencion);
                itvd.setImporteSecundario(impRetencion.divide(mp.getCotizacion(), 2, RoundingMode.HALF_UP));
                itvd.setImporteDebe(impRetencion);
                itvd.setImporteDebeSecundario(impRetencion.divide(mp.getCotizacion(), 2, RoundingMode.HALF_UP));
            }
        }

        //Agrego los items generados
        mp.getItemTotal().add(itvd);
        mp.getItemProducto().add(itvh);

    }

    //---------------------------------------------------------------------------
    public MovimientoProveedor getMovimiento(Integer id) {

        return proveedorDAO.getObjeto(MovimientoProveedor.class, id);
    }

    public MovimientoProveedor getMovimiento(String codFormulario, Integer numeroFormulario) {
        return proveedorDAO.getMovimiento(codFormulario, numeroFormulario);
    }

    public List<MovimientoProveedor> getComprobantesByBusqueda(Map parameters) {

        return proveedorDAO.getComprobantesByBusqueda(parameters);
    }

    public void generarRetenciones(MovimientoTesoreria m,
            List<ItemPendienteCuentaCorrienteProveedor> saldosPendientes) throws ExcepcionGeneralSistema {

//        System.err.println("generar Retenciones");
        if (m.getComprobante().getComprobanteInterno().equals("S")) {
            return;
        }

        BigDecimal baseImponible = null;
        BigDecimal totalBaseImponible = BigDecimal.ZERO;
        BigDecimal porcRetencion = BigDecimal.ZERO;
        BigDecimal totalRetencionConcepto = BigDecimal.ZERO;
        BigDecimal exedenteAdescontarPorItem = BigDecimal.ZERO;
        int cantidadItemCancelar = 0;

        m.getRetenciones().clear();

        //Primero ponemos en cero el importe retenciones de los saldos
        for (ItemPendienteCuentaCorrienteProveedor ic : saldosPendientes) {
            ic.setImporteRetencion(BigDecimal.ZERO);
        }

        for (RetencionPorEntidad rpe : m.getEntidad().getRetenciones()) {

            ConceptoRetencion conceptoRet = rpe.getConceptoRetencion();

            if (conceptoRet == null) {
                continue;
            }

            //CALCULAMOS LA BASE IMPONIBLE
            totalBaseImponible = BigDecimal.ZERO;
            totalRetencionConcepto = BigDecimal.ZERO;

            for (ItemPendienteCuentaCorrienteProveedor ic : saldosPendientes) {

                if (ic.isSeleccionado()) {

                    if (m.isEsAnticipo()) {
//                        baseImponible = (new BigDecimal("100"))
//                                .multiply(ic.getImporteAplicar())
//                                .divide((new BigDecimal("100").add(porcRetencion.negate())), 2, RoundingMode.HALF_UP);
                        baseImponible = ic.getImporteAplicar();
                    } else {

                        if (conceptoRet.getTipoCalculo().getCodigo().equals("B")) {
                            baseImponible = ic.getPendienteNeto();
                        }

                        if (conceptoRet.getTipoCalculo().getCodigo().equals("T")) {
                            baseImponible = ic.getImporteAplicar();
                        }

                        if (conceptoRet.getTipoCalculo().getCodigo().equals("I")) {
                            baseImponible = ic.getImporteAplicar().add(ic.getPendienteNeto().negate());
                        }

                    }
                    totalBaseImponible = totalBaseImponible.add(baseImponible);
                    cantidadItemCancelar++;
                }
            }

            //DEFINIMOS EL PORCENTAJE DE RETENCIÓN
//             System.out.println("conceptoRet " + conceptoRet);
            if (conceptoRet.getPorcentajeRetener() != null
                    && conceptoRet.getPorcentajeRetener().compareTo(BigDecimal.ZERO) > 0
                    && (conceptoRet.getValores() == null || conceptoRet.getValores().isEmpty())) {
                porcRetencion = conceptoRet.getPorcentajeRetener();
            } else if ((conceptoRet.getValores() != null && !conceptoRet.getValores().isEmpty())) {

                for (ConceptoRetencionValor rv : conceptoRet.getValores()) {

                    if (rv.getImporteDesde().compareTo(totalBaseImponible) <= 0 && rv.getImporteHasta().compareTo(totalBaseImponible) > 0) {

                        porcRetencion = rv.getPorcentajeRetener();
                        break;
                    }
                }
            }

//            System.out.println("conceptoRet " + conceptoRet);
//            System.out.println("BaseImponible " + totalBaseImponible);
//            System.out.println("porcRetencion " + porcRetencion);
            if (porcRetencion == null || porcRetencion.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

//            System.err.println("conceptoRet.getSobreExedenteDe() " + conceptoRet.getSobreExedenteDe());
//            System.err.println("totalBaseImponible " + totalBaseImponible);
            if (conceptoRet.getSobreExedenteDe() != null && conceptoRet.getSobreExedenteDe().compareTo(totalBaseImponible) > 0) {
                return;
            }

            exedenteAdescontarPorItem = conceptoRet.getSobreExedenteDe().divide(new BigDecimal(cantidadItemCancelar), 2, RoundingMode.HALF_UP);

//            System.err.println("exedenteAdescontarPorItem " + exedenteAdescontarPorItem);
            for (ItemPendienteCuentaCorrienteProveedor ic : saldosPendientes) {

                if (ic.isSeleccionado()) {

                    if (m.isEsAnticipo()) {
//                        baseImponible = (new BigDecimal("100"))
//                                .multiply(ic.getImporteAplicar())
//                                .divide((new BigDecimal("100").add(porcRetencion.negate())), 2, RoundingMode.HALF_UP);

                        baseImponible = ic.getImporteAplicar();

                    } else {

                        if (conceptoRet.getTipoCalculo().getCodigo().equals("B")) {
                            baseImponible = ic.getPendienteNeto();
                        }

                        if (conceptoRet.getTipoCalculo().getCodigo().equals("T")) {
                            baseImponible = ic.getImporteAplicar();
                        }

                        if (conceptoRet.getTipoCalculo().getCodigo().equals("I")) {
                            baseImponible = ic.getImporteAplicar().add(ic.getPendienteNeto().negate());
                        }
                    }

                    baseImponible = baseImponible.add(exedenteAdescontarPorItem.negate());

                    BigDecimal impRetencionItem = baseImponible.multiply(porcRetencion).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                    //Acumulamos el importe en el saldo
                    ic.setImporteRetencion(ic.getImporteRetencion().add(impRetencionItem));
                    //Guardamos el importe de la retencion para este concepto para luego analizar el mínimo
                    ic.setImporteRetencionConcepto(impRetencionItem);

//                    System.err.println("baseImponible " + baseImponible);
//                    System.err.println("impRetencionItem " + impRetencionItem);
                    totalRetencionConcepto = totalRetencionConcepto.add(impRetencionItem);
                }
            }

//            System.err.println("totalRetencion " + totalRetencion);
//            System.err.println("conceptoRet.getImporteMinimo() " + conceptoRet.getImporteMinimo());
//            System.err.println("conceptoRet.getImporteMinimo().compareTo(totalRetencion) " + conceptoRet.getImporteMinimo().compareTo(totalRetencion));
            if (conceptoRet.getImporteMinimo() != null && conceptoRet.getImporteMinimo().compareTo(totalRetencionConcepto) < 0) {

//                System.err.println("Agrega retención");
                m.getRetenciones().add(generarComprobanteRetencion(m, conceptoRet, totalBaseImponible, porcRetencion, totalRetencionConcepto));
            } else {

                //Si el importe de retencion no supera el mínimo, debemos restar los importes asignado previamente
                for (ItemPendienteCuentaCorrienteProveedor ic : saldosPendientes) {

                    if (ic.isSeleccionado()) {

                        ic.setImporteRetencion(ic.getImporteRetencion().add(ic.getImporteRetencionConcepto().negate()));

                    }
                }
//                System.err.println("No Agrega retención");
            }
        }
    }

    /**
     * Asigno el objeto movmiento proveedor a los comprobantes de retenciones
     * generados en caso de que existan.
     *
     * @param mp Movimiento Proveedor
     */
    private void asignarMovimientoRetencion(MovimientoProveedor mp) {

        if (mp.getRetenciones() == null || mp.getRetenciones().isEmpty()) {
            return;
        }

        for (MovimientoProveedor mret : mp.getRetenciones()) {

            mret.setMovimientoProveedor(mp);
        }
    }

    private void tomarNumeroFormulario(MovimientoProveedor m) throws ExcepcionGeneralSistema, Exception {

        if (m == null) {
            return;
        }

        if (m.getFormulario().getTipoNumeracion() == null || m.getFormulario().getTipoNumeracion().isEmpty()) {
            m.getFormulario().setTipoNumeracion("A");
        }

        if (m.getFormulario().getTipoNumeracion().equals("A")) {

            m.setNumeroFormulario(formularioRN.tomarProximoNumero(m.getFormulario()));

        } else if (m.getFormulario().getTipoNumeracion().equals("I") || m.getFormulario().getTipoNumeracion().equals("S")) {

            m.getFormulario().setUltimoNumero(m.getNumeroFormulario());

        } else {

            m.setNumeroFormulario(formularioRN.tomarProximoNumero(m.getFormulario()));
        }

        if (m.getMovimientoTesoreria() != null) {
            m.getMovimientoTesoreria().setNumeroFormulario(m.getNumeroFormulario());
        }

        if (m.getFechaMovimiento() != null) {
            m.getFormulario().setUltimaFecha(m.getFechaMovimiento());
        }

        formularioRN.guardar(m.getFormulario());
    }

    public void asignarFormulario(MovimientoProveedor m) throws ExcepcionGeneralSistema {

        if (m.getComprobante() == null || m.getPuntoVenta() == null || m.getProveedor() == null) {
            throw new ExcepcionGeneralSistema("El comprobante no tiene asignado el proveedor");
        }

        Formulario formulario = formularioRN.getFormulario(m.getComprobante(), m.getPuntoVenta(), m.getCondicionDeIva().getCodigo());

        //Este número es temporal y puede cambiar al guardar el objeto.
        m.setNumeroFormulario(formulario.getUltimoNumero() + 1);
        m.setFormulario(formulario);

        if (m.getComprobanteTesoreria() != null) {
            Formulario formularioCJ = formularioRN.getFormulario(m.getComprobanteTesoreria(), m.getPuntoVenta(), m.getCondicionDeIva().getCodigo());
            m.getMovimientoTesoreria().setNumeroFormulario(m.getNumeroFormulario());
            m.getMovimientoTesoreria().setFormulario(formularioCJ);
        }

    }

    public void calcularImportes(MovimientoProveedor m, boolean calculaIva) {

        //Si el movimiento está guardado, no calcula nada
        if (m.getId() != null) {

            if (m.getItemTotal() != null && !m.getItemTotal().isEmpty()) {
                m.setImporteTotal(m.getItemTotal().get(0).getImporte());
            }
            return;
        }

        BigDecimal gravado1050 = BigDecimal.ZERO;
        BigDecimal gravado2100 = BigDecimal.ZERO;
        BigDecimal gravado2700 = BigDecimal.ZERO;
        BigDecimal iva1050;
        BigDecimal iva2100;
        BigDecimal iva2700;
        BigDecimal impTotal = BigDecimal.ZERO;

        BigDecimal importeTotalDebe = BigDecimal.ZERO;
        BigDecimal importeTotalHaber = BigDecimal.ZERO;

        for (ItemProductoProveedor i : m.getItemProducto()) {

            i.setCotizacion(m.getCotizacion());

            if (i.getDebeHaber().equals("D")) {
                importeTotalDebe = importeTotalDebe.add(i.getImporte());
                i.setImporteDebe(i.getImporte());
                i.setImporteDebeSecundario(i.getImporte().divide(i.getCotizacion(), 2, RoundingMode.HALF_UP));
            } else {
                importeTotalHaber = importeTotalHaber.add(i.getImporte());
                i.setImporteHaber(i.getImporte());
                i.setImporteHaberSecundario(i.getImporte().divide(i.getCotizacion(), 2, RoundingMode.HALF_UP));
            }

            if (i.getConcepto().getCodigo().equals("C001")) {
                gravado2100 = gravado2100.add(i.getImporte());
            }

            if (i.getConcepto().getCodigo().equals("C002")) {
                gravado1050 = gravado1050.add(i.getImporte());
            }

            if (i.getConcepto().getCodigo().equals("C003")) {
                gravado2700 = gravado2700.add(i.getImporte());
            }

            if (i.getConcepto().getCodigo().equals("S001")) {
                gravado2100 = gravado2100.add(i.getImporte());
            }

            if (i.getConcepto().getCodigo().equals("S002")) {
                gravado1050 = gravado1050.add(i.getImporte());
            }

            if (i.getConcepto().getCodigo().equals("S003")) {
                gravado2700 = gravado2700.add(i.getImporte());
            }

            impTotal = impTotal.add(i.getImporte());
        }

        if (m.getCondicionDeIva().getCodigo().equals("I")) {

            //calculamos los importes de iva
            iva1050 = gravado1050.multiply(new BigDecimal("0.105")).setScale(2, RoundingMode.HALF_UP);
            iva2100 = gravado2100.multiply(new BigDecimal("0.21")).setScale(2, RoundingMode.HALF_UP);
            iva2700 = gravado2700.multiply(new BigDecimal("0.27")).setScale(2, RoundingMode.HALF_UP);

            for (ItemImpuestoProveedor i : m.getItemImpuesto()) {

                i.setCotizacion(m.getCotizacion());

                if (i.getEditaImporte() == null) {
                    i.setEditaImporte("N");
                }

                if (i.getEditaImporte().equals("N") || calculaIva) {

                    if (i.getConcepto().getCodigo().equals("IVA001")) {
                        i.setImporte(iva2100);
                    }

                    if (i.getConcepto().getCodigo().equals("IVA002")) {
                        i.setImporte(iva1050);
                    }

                    if (i.getConcepto().getCodigo().equals("IVA003")) {
                        i.setImporte(iva2700);
                    }
                }

                if (i.getDebeHaber().equals("D")) {
                    importeTotalDebe = importeTotalDebe.add(i.getImporte());
                    i.setImporteDebe(i.getImporte());
                    i.setImporteDebeSecundario(i.getImporte().divide(i.getCotizacion(), 2, RoundingMode.HALF_UP));
                } else {
                    importeTotalHaber = importeTotalHaber.add(i.getImporte());
                    i.setImporteHaber(i.getImporte());
                    i.setImporteHaberSecundario(i.getImporte().divide(i.getCotizacion(), 2, RoundingMode.HALF_UP));
                }

                impTotal = impTotal.add(i.getImporte());
            }
        }

        for (ItemPercepcionProveedor i : m.getItemPercepcion()) {

            i.setCotizacion(m.getCotizacion());

            if (i.getDebeHaber().equals("D")) {
                importeTotalDebe = importeTotalDebe.add(i.getImporte());
                i.setImporteDebe(i.getImporte());
                i.setImporteDebeSecundario(i.getImporte().divide(i.getCotizacion(), 2, RoundingMode.HALF_UP));
            } else {
                importeTotalHaber = importeTotalHaber.add(i.getImporte());
                i.setImporteHaber(i.getImporte());
                i.setImporteHaberSecundario(i.getImporte().divide(i.getCotizacion(), 2, RoundingMode.HALF_UP));
            }

            impTotal = impTotal.add(i.getImporte());
        }

        for (ItemRetencionProveedor i : m.getItemRetencion()) {

            i.setCotizacion(m.getCotizacion());

            if (i.getDebeHaber().equals("D")) {
                importeTotalDebe = importeTotalDebe.add(i.getImporte());
                i.setImporteDebe(i.getImporte());
                i.setImporteDebeSecundario(i.getImporte().divide(i.getCotizacion(), 2, RoundingMode.HALF_UP));
            } else {
                importeTotalHaber = importeTotalHaber.add(i.getImporte());
                i.setImporteHaber(i.getImporte());
                i.setImporteHaberSecundario(i.getImporte().divide(i.getCotizacion(), 2, RoundingMode.HALF_UP));
            }

            impTotal = impTotal.add(i.getImporte());
        }

        for (ItemTotalProveedor i : m.getItemTotal()) {

            i.setCotizacion(m.getCotizacion());

            i.setImporte(impTotal);

            if (i.getDebeHaber().equals("D")) {
                importeTotalDebe = importeTotalDebe.add(i.getImporte());
                i.setImporteDebe(i.getImporte());
                i.setImporteDebeSecundario(i.getImporte().divide(i.getCotizacion(), 2, RoundingMode.HALF_UP));
            } else {
                importeTotalHaber = importeTotalHaber.add(i.getImporte());
                i.setImporteHaber(i.getImporte());
                i.setImporteHaberSecundario(i.getImporte().divide(i.getCotizacion(), 2, RoundingMode.HALF_UP));
            }
        }

        m.setImporteTotalDebe(importeTotalDebe);
        m.setImporteTotalHaber(importeTotalHaber);

        if (m.getComprobante().getDebeHaber().equals("D")) {
            m.setImporteTotal(importeTotalDebe);
        } else {
            m.setImporteTotal(importeTotalHaber);
        }

        try {
            if (m.getComprobante().getTipoImputacion().equals("CC")) {
                cuentaCorrienteRN.generarItemCuentaCorriente(m);
            }
        } catch (ExcepcionGeneralSistema ex) {
            Logger.getLogger(MovimientoProveedorRN.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void limpiarConceptoEnCero(MovimientoProveedor m) {

        List<ItemProductoProveedor> itemProductoAux = m.getItemProducto();
        List<ItemImpuestoProveedor> itemImpuestoAux = m.getItemImpuesto();
        List<ItemPercepcionProveedor> itemPercepcionAux = m.getItemPercepcion();
        List<ItemRetencionProveedor> itemRetencionAux = m.getItemRetencion();
        List<ItemTotalProveedor> itemTotalAux = m.getItemTotal();

        m.setItemProducto(new ArrayList<ItemProductoProveedor>());
        m.setItemImpuesto(new ArrayList<ItemImpuestoProveedor>());
        m.setItemPercepcion(new ArrayList<ItemPercepcionProveedor>());
        m.setItemRetencion(new ArrayList<ItemRetencionProveedor>());
        m.setItemTotal(new ArrayList<ItemTotalProveedor>());

        for (ItemProductoProveedor im : itemProductoAux) {
            if (im.getImporte() != null && im.getImporte().compareTo(BigDecimal.ZERO) > 0) {
                m.getItemProducto().add(im);
            }
        }

        for (ItemImpuestoProveedor im : itemImpuestoAux) {
            if (im.getImporte() != null && im.getImporte().compareTo(BigDecimal.ZERO) > 0) {
                m.getItemImpuesto().add(im);
            }
        }

        for (ItemPercepcionProveedor im : itemPercepcionAux) {
            if (im.getImporte() != null && im.getImporte().compareTo(BigDecimal.ZERO) > 0) {
                m.getItemPercepcion().add(im);
            }
        }

        for (ItemRetencionProveedor im : itemRetencionAux) {
            if (im.getImporte() != null && im.getImporte().compareTo(BigDecimal.ZERO) > 0) {
                m.getItemRetencion().add(im);
            }
        }

        for (ItemTotalProveedor im : itemTotalAux) {
            if (im.getImporte() != null && im.getImporte().compareTo(BigDecimal.ZERO) > 0) {
                m.getItemTotal().add(im);
            }
        }
    }

    public List<MovimientoProveedor> getListaByBusqueda(Map<String, String> filtro, int cantMax) {

        return proveedorDAO.getListaByBusqueda(filtro, null, cantMax);
    }

    public List<MovimientoProveedor> getListaByBusqueda(Map<String, String> filtro, List<String> orderBy, int cantMax) {

        return proveedorDAO.getListaByBusqueda(filtro, orderBy, cantMax);
    }

    public MovimientoProveedor revertirMovimiento(MovimientoProveedor mRevertir) throws Exception {

        MovimientoProveedor mr = generarMovimientoReversion(mRevertir);
        mr = guardar(mr);

        mRevertir.setMovimientoReversion(mr);
        if (mRevertir.getMovimientoTesoreria() != null) {
            mRevertir.getMovimientoTesoreria().setMovimientoReversion(mr.getMovimientoTesoreria());
        }
        mRevertir = (MovimientoProveedor) proveedorDAO.editar(mRevertir);

        return mr;
    }

    public MovimientoProveedor generarMovimientoReversion(MovimientoProveedor m) throws ExcepcionGeneralSistema, Exception {

        if (m == null) {
            throw new ExcepcionGeneralSistema("Movimiento de proveedor nulo");
        }

        if (m.getMovimientoReversion() != null) {
            throw new ExcepcionGeneralSistema("El comprobante ya fué anulado o revertido");
        }

        if (m.getMovimientoContabilidad() != null) {
            throw new ExcepcionGeneralSistema("El comprobante fue pasado a la contabilidad, no es posible revertirlo");
        }

        if (m.getComprobante().getComprobanteReversion() == null) {
            throw new ExcepcionGeneralSistema("El comprobante " + m.getComprobante().getDescripcion() + " no tienen comprobante de reversión asociado");
        }

        MovimientoProveedor mr = new MovimientoProveedor();

        mr.setEmpresa(m.getEmpresa());
        mr.setSucursal(m.getSucursal());
        mr.setPuntoVenta(m.getPuntoVenta());
        mr.setUnidadNegocio(m.getUnidadNegocio());

        mr.setComprobante(m.getComprobante().getComprobanteReversion());

        Formulario formulario = formularioRN.getFormulario(mr.getComprobante(), mr.getPuntoVenta(), m.getCondicionDeIva().getCodigo());

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de tesorería para el comprobante (" + mr.getComprobante().getCodigo() + ") "
                    + "para El punto de venta (" + mr.getPuntoVenta().getCodigo() + ") "
                    + "con la condición de iva (" + m.getCondicionDeIva().getCodigo() + ")");
        }

        mr.setFormulario(formulario);
        mr.setImputacionCuentaCorriente(m.getImputacionCuentaCorriente());
        mr.setFechaMovimiento(m.getFechaMovimiento());
        mr.setFechaEmision(m.getFechaEmision());
        mr.setFechaRecepcion(m.getFechaRecepcion());
        mr.setFechaVencimiento(m.getFechaVencimiento());

        mr.setPuntoVentaOriginal(m.getPuntoVentaOriginal());
        mr.setNumeroOriginal(m.getNumeroOriginal());
        mr.setNrocae(m.getNrocae());
        mr.setVencae(m.getVencae());

        mr.setObservaciones("");
        mr.setMonedaSecundaria(m.getMonedaSecundaria());
        mr.setMonedaRegistracion(m.getMonedaRegistracion());
        mr.setCotizacion(m.getCotizacion());

        mr.setProveedor(m.getProveedor());
        mr.setProveedorCuentaCorriente(m.getProveedorCuentaCorriente());
        mr.setRazonSocial(m.getRazonSocial());
        mr.setNrodoc(m.getNrodoc());
        mr.setTipoDocumento(m.getTipoDocumento());
        mr.setProvincia(m.getProvincia());
        mr.setLocalidad(m.getLocalidad());
        mr.setBarrio(m.getBarrio());
        mr.setDireccion(m.getDireccion());
        mr.setNumero(m.getNumero());
        mr.setDepartamentoPiso(m.getDepartamentoPiso());
        mr.setDepartamentoNumero(m.getDepartamentoNumero());

        mr.setCondicionDeIva(m.getCondicionDeIva());
        mr.setCondicionDePago(m.getCondicionDePago());

        mr.setListaCosto(m.getListaCosto());
        mr.setMonedaListaCosto(m.getMonedaListaCosto());
        mr.setComprador(m.getComprador());

        mr.setMovimientoReversion(m);
        m.setMovimientoReversion(mr);

        if (m.getFormulario().getTipoNumeracion().equals("I")) {
            m.setNumeroFormulario(m.getNumeroFormulario() * -1);
        }

        asignarFormulario(mr);
        tomarNumeroFormulario(mr);

        for (ItemProductoProveedor i : m.getItemProducto()) {

            ItemProductoProveedor ir = new ItemProductoProveedor();

            ir.setMovimiento(mr);
            ir.setNroItem(mr.getItemProducto().size() + 1);
            ir.setMovimiento(mr);
            ir.setConcepto(i.getConcepto());
            ir.setCuentaContable(i.getCuentaContable());
            ir.setCotizacion(i.getCotizacion());

            ir.setObservaciones(i.getObservaciones());

            ir.setDebeHaber((i.getDebeHaber().equals("D") ? "H" : "D"));

            ir.setImporte(i.getImporte());
            ir.setImporteSecundario(i.getImporteSecundario());

            ir.setImporteDebe(i.getImporteHaber());
            ir.setImporteHaberSecundario(i.getImporteHaberSecundario());

            ir.setImporteHaber(i.getImporteDebe());
            ir.setImporteDebeSecundario(i.getImporteDebeSecundario());

            mr.getItemProducto().add(ir);
        }

        for (ItemImpuestoProveedor i : m.getItemImpuesto()) {

            ItemImpuestoProveedor ir = new ItemImpuestoProveedor();

            ir.setMovimiento(mr);
            ir.setNroItem(mr.getItemProducto().size() + 1);
            ir.setMovimiento(mr);
            ir.setConcepto(i.getConcepto());
            ir.setCuentaContable(i.getCuentaContable());
            ir.setCotizacion(i.getCotizacion());

            ir.setObservaciones(i.getObservaciones());

            ir.setDebeHaber((i.getDebeHaber().equals("D") ? "H" : "D"));

            ir.setImporte(i.getImporte());
            ir.setImporteSecundario(i.getImporteSecundario());

            ir.setImporteDebe(i.getImporteHaber());
            ir.setImporteHaberSecundario(i.getImporteHaberSecundario());

            ir.setImporteHaber(i.getImporteDebe());
            ir.setImporteDebeSecundario(i.getImporteDebeSecundario());

            mr.getItemImpuesto().add(ir);
        }

        for (ItemPercepcionProveedor i : m.getItemPercepcion()) {

            ItemPercepcionProveedor ir = new ItemPercepcionProveedor();

            ir.setMovimiento(mr);
            ir.setNroItem(mr.getItemProducto().size() + 1);
            ir.setMovimiento(mr);
            ir.setConcepto(i.getConcepto());
            ir.setCuentaContable(i.getCuentaContable());
            ir.setCotizacion(i.getCotizacion());

            ir.setObservaciones(i.getObservaciones());

            ir.setDebeHaber((i.getDebeHaber().equals("D") ? "H" : "D"));

            ir.setImporte(i.getImporte());
            ir.setImporteSecundario(i.getImporteSecundario());

            ir.setImporteDebe(i.getImporteHaber());
            ir.setImporteHaberSecundario(i.getImporteHaberSecundario());

            ir.setImporteHaber(i.getImporteDebe());
            ir.setImporteDebeSecundario(i.getImporteDebeSecundario());

            mr.getItemPercepcion().add(ir);
        }

        for (ItemRetencionProveedor i : m.getItemRetencion()) {

            ItemRetencionProveedor ir = new ItemRetencionProveedor();

            ir.setMovimiento(mr);
            ir.setNroItem(mr.getItemProducto().size() + 1);
            ir.setMovimiento(mr);
            ir.setConcepto(i.getConcepto());
            ir.setCuentaContable(i.getCuentaContable());
            ir.setCotizacion(i.getCotizacion());

            ir.setObservaciones(i.getObservaciones());

            ir.setDebeHaber((i.getDebeHaber().equals("D") ? "H" : "D"));

            ir.setImporte(i.getImporte());
            ir.setImporteSecundario(i.getImporteSecundario());

            ir.setImporteDebe(i.getImporteHaber());
            ir.setImporteHaberSecundario(i.getImporteHaberSecundario());

            ir.setImporteHaber(i.getImporteDebe());
            ir.setImporteDebeSecundario(i.getImporteDebeSecundario());

            mr.getItemRetencion().add(ir);
        }

        for (ItemTotalProveedor i : m.getItemTotal()) {

            ItemTotalProveedor ir = new ItemTotalProveedor();

            ir.setMovimiento(mr);
            ir.setNroItem(mr.getItemProducto().size() + 1);
            ir.setMovimiento(mr);
            ir.setConcepto(i.getConcepto());
            ir.setCuentaContable(i.getCuentaContable());
            ir.setCotizacion(i.getCotizacion());

            ir.setObservaciones(i.getObservaciones());

            ir.setDebeHaber((i.getDebeHaber().equals("D") ? "H" : "D"));

            ir.setImporte(i.getImporte());
            ir.setImporteSecundario(i.getImporteSecundario());

            ir.setImporteDebe(i.getImporteHaber());
            ir.setImporteHaberSecundario(i.getImporteHaberSecundario());

            ir.setImporteHaber(i.getImporteDebe());
            ir.setImporteDebeSecundario(i.getImporteDebeSecundario());

            mr.getItemTotal().add(ir);
        }

        cuentaCorrienteRN.generarItemReversion(mr, m);

        if (m.getMovimientoTesoreria() != null) {
            MovimientoTesoreria mTesoreriaR = tesoreriaRN.generarMovimientoReversion(m.getMovimientoTesoreria());
            mr.setMovimientoTesoreria(mTesoreriaR);
        }

        /**
         * Si el movimiento tiene retenciones, las recorremos, vamos anulando y
         * agregando al movimiento de reversión.
         */
        if (m.getRetenciones() != null) {
            for (MovimientoProveedor mRet : m.getRetenciones()) {
                mr.getRetenciones().add(revertirMovimiento(mRet));
            }
        }

        return mr;
    }

    public void asignarProveedor(MovimientoProveedor m, EntidadComercial proveedor) throws ExcepcionGeneralSistema {

        m.setProveedor(proveedor);
        m.setProveedorCuentaCorriente(proveedor);
        m.setRazonSocial(proveedor.getRazonSocial());
        m.setTipoDocumento(proveedor.getTipoDocumento());
        m.setNrodoc(proveedor.getNrodoc());

        m.setProvincia(proveedor.getProvincia());
        m.setLocalidad(proveedor.getLocalidad());
        m.setDireccion(proveedor.getDireccion());

        m.setCondicionDeIva(proveedor.getCondicionDeIva());

        if (m.getComprobante() != null && m.getComprobante().getTipoImputacion().equals("CO")) {
            m.setCondicionDePago(condicionPagoProveedorRN.getPrimeraCondicionByImputacion("N"));
        } else {
            m.setCondicionDePago(proveedor.getCondicionPagoProveedor());
        }

        if (proveedor.getEntidadComodin().equals("S")) {

            m.setRazonSocial(null);
            m.setProvincia(null);
            m.setLocalidad(null);
            m.setDireccion(null);
            m.setNrodoc(null);
        }

        m.setListaCosto(proveedor.getListaCosto());
        m.setMonedaListaCosto(proveedor.getListaCosto().getMoneda());
        m.setComprador(proveedor.getComprador());

        asignarFormulario(m);
    }

    public void changeCotizacion(MovimientoProveedor m, BigDecimal cotizacion) {

    }

}
