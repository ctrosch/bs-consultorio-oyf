/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.rn;

import bs.administracion.rn.ModuloRN;
import bs.administracion.rn.ParametrosRN;
import bs.educacion.modelo.AplicacionCuentaCorrienteEducacion;
import bs.educacion.modelo.MovimientoEducacion;
import bs.facturacion.modelo.ItemMovimientoFacturacion;
import bs.facturacion.modelo.MovimientoFacturacion;
import bs.global.dao.ComprobanteDAO;
import bs.global.dao.ConceptoDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.ConceptoComprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.ImpuestoPorConcepto;
import bs.global.modelo.Moneda;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.FormularioRN;
import bs.global.rn.MonedaRN;
import bs.global.rn.PuntoVentaRN;
import bs.global.util.Numeros;
import bs.global.web.PrincipalBean;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.ventas.dao.MovimientoVentaDAO;
import bs.ventas.modelo.ItemImpuestoVenta;
import bs.ventas.modelo.ItemPercepcionVenta;
import bs.ventas.modelo.ItemProductoVenta;
import bs.ventas.modelo.ItemTotalVenta;
import bs.ventas.modelo.MovimientoVenta;
import bs.wsafip.dao.ParametroWSDAO;
import bs.wsafip.modelo.ParametroWS;
import bs.wsafip.rn.WSFEv1;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

/**
 *
 * @author Claudio
 */
@Stateless
public class MovimientoVentaRN implements Serializable {

    @EJB
    private MovimientoVentaDAO ventaDAO;
    @EJB
    private ConceptoDAO conceptoDAO;
    @EJB
    private ComprobanteDAO comprobanteVentaDAO;
    @EJB
    private CuentaCorrienteRN cuentaCorrienteRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private MonedaRN monedaRN;
    @EJB
    private PuntoVentaRN puntoVentaRN;
    @EJB
    protected ParametrosRN parametrosRN;
    @EJB
    private ModuloRN moduloRN;

    @EJB
    private ParametroWSDAO parametroWSDAO;

    @Inject
    private PrincipalBean principalBean;

    //@EJB private GR_MailFactory mailFactory;
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized MovimientoVenta guardar(MovimientoVenta m) throws Exception {

        calcularImportes(m);
        controlComprobante(m);
        limpiarConceptoEnCero(m);

        if (m.getId() == null) {

            WSFEv1 wsfe = null;
            ParametroWS p = parametroWSDAO.getObjeto("01");

            if (m.getComprobante() != null
                    && m.getComprobante().getComprobanteInterno().equals("N")
                    && !m.getComprobante().getTipoImputacion().equals("AP")
                    && m.getPuntoVenta().getImplementaFE().equals("S")
                    && p != null
                    && p.getTipoAutorizacion().equals("I")) {

                wsfe = new WSFEv1(p, m.getPuntoVenta().getWebservice());
            }

            tomarNumeroFormulario(m, wsfe);
            ventaDAO.crear(m);

            //Si es distinto de null significa que es un comprobante de venta
            if (wsfe != null) {

                try {
                    wsfe.autorizar(m);
                    m = (MovimientoVenta) ventaDAO.editar(m);
                } catch (Exception ex) {

                    ventaDAO.eliminar(m);
                    throw new ExcepcionGeneralSistema("Error autorizando comprobante: " + ex.getMessage() + ". Intente nuevamente");
                }
            }

        } else {
            m = (MovimientoVenta) ventaDAO.editar(m);
        }
        return m;
    }

    public ItemTotalVenta nuevoItemTotal(MovimientoVenta mv) {

        ItemTotalVenta itv = new ItemTotalVenta();

        itv.setNroItem(mv.getItemTotal().size() + 1);
        itv.setMovimiento(mv);
        itv.setConcepto(conceptoDAO.getConcepto("VT", "T", "DVT001"));
        itv.setCuentaContable(itv.getConcepto().getCuentaContable());
        itv.setDebeHaber("D");
        itv.setCantidad(BigDecimal.ZERO);
        itv.setCotizacion(mv.getCotizacion());
        mv.getItemTotal().add(itv);

        return itv;
    }

    public MovimientoVenta nuevoMovimiento(String CODVT, String SUCURS) throws Exception {

        Comprobante comprobante = comprobanteVentaDAO.getComprobante("VT", CODVT);
        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(SUCURS);

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No se encontró comprobante de ventas (" + CODVT + ")");
        }
        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No se encontró punto de venta (" + SUCURS + ")");
        }

        MovimientoVenta m = new MovimientoVenta();
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

        return m;
    }

    public MovimientoVenta nuevoMovimiento(String CODVT, String SUCVT, String CNDIVA) throws Exception {

        Comprobante comprobante = comprobanteVentaDAO.getComprobante("VT", CODVT);
        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(SUCVT);

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No se encontró comprobante de ventas (" + CODVT + ")");
        }
        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No se encontró punto de venta (" + SUCVT + ")");
        }

        Formulario formulario = formularioRN.getFormulario(comprobante, puntoVenta, CNDIVA);
        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de venta para el comprobante (" + CODVT + ") "
                    + "para El punto de venta (" + SUCVT + ") "
                    + "con la condición de iva (" + CNDIVA + ")");
        }

        MovimientoVenta m = new MovimientoVenta();
        Moneda moneda = monedaRN.getMoneda(parametrosRN.getParametro().getCodigoMonedaSecundaria());
        BigDecimal cotizacion = monedaRN.getCotizacionDia(parametrosRN.getParametro().getCodigoMonedaSecundaria());

        m.setEmpresa(puntoVenta.getEmpresa());
        m.setSucursal(puntoVenta.getSucursal());
        m.setPuntoVenta(puntoVenta);
        m.setUnidadNegocio(puntoVenta.getUnidadNegocio());

        m.setComprobante(comprobante);
        m.setFormulario(formulario);

        tomarNumeroFormulario(m, null);

        m.setImputacionCuentaCorriente(comprobante.getTipoImputacion());
        m.setObservaciones("");
        m.setFechaMovimiento(new Date());
        m.setFechaVencimiento(new Date());
        m.setMonedaSecundaria(moneda);
        m.setMonedaRegistracion(comprobante.getMonedaRegistracion());
        m.setCotizacion(cotizacion);

        return m;
    }

    public MovimientoVenta generarComprobante(MovimientoFacturacion mf) throws ExcepcionGeneralSistema {

        MovimientoVenta m = new MovimientoVenta();

        m.setEmpresa(mf.getEmpresa());
        m.setSucursal(mf.getSucursal());
        m.setPuntoVenta(mf.getPuntoVenta());
        m.setUnidadNegocio(mf.getUnidadNegocio());

        m.setComprobante(mf.getComprobanteVenta());
        m.setFormulario(mf.getFormulario());
        m.setNumeroFormulario(mf.getNumeroFormulario());

        m.setImputacionCuentaCorriente(mf.getComprobanteVenta().getTipoImputacion());
        m.setObservaciones(mf.getObservaciones());
        m.setTipoExportacion(mf.getTipoExportacion());
        m.setFechaMovimiento(mf.getFechaMovimiento());
        m.setFechaVencimiento(mf.getFechaVencimiento());

        m.setCliente(mf.getCliente());
        m.setClienteCuentaCorriente(mf.getClienteCuentaCorriente());
        m.setRazonSocial(mf.getRazonSocial());
        m.setTipoDocumento(mf.getTipoDocumento());
        m.setDireccion(mf.getDireccion());
        m.setNrodoc(mf.getNrodoc());
        m.setDireccionEntrega(mf.getDireccionEntrega());

        m.setProvincia(mf.getProvincia());
        m.setLocalidad(mf.getLocalidad());
        m.setTransporte(mf.getTransporte());
        m.setCamion(mf.getCamion());
        m.setChofer(mf.getChofer());

//        m.setNumero(mf.getNumero());
//        m.setDepartamentoPiso(mf.getDepartamentoPiso());
//        m.setDepartamentoNumero(mf.getDepartamentoNumero());
        m.setCondicionDePago(mf.getCondicionDePago());
        m.setVendedor(mf.getVendedor());
        m.setCobrador(mf.getCliente().getCobrador());
        m.setRepartidor(mf.getRepartidor());
        m.setListaDePrecio(mf.getListaDePrecio());
        m.setMonedaListaPrecio(mf.getMonedaListaPrecio());
        m.setMonedaSecundaria(mf.getMonedaSecundaria());
        m.setMonedaRegistracion(mf.getMonedaRegistracion());
        m.setCotizacion(mf.getCotizacion());
        m.setZona(mf.getCliente().getZona());
        m.setCondicionDeIva(mf.getCondicionDeIva());
        m.setBienDeUso(m.getBienDeUso());
        m.setNumeroCpbteAsociado(mf.getNumeroCpbteAsociado());
        m.setFechaCpbteAsociado(mf.getFechaCpbteAsociado());
        m.setCanalVenta(mf.getCliente().getCanalVenta());

        generarItemProducto(mf, m);
        generarItemImpuesto(mf, m);
        generarItemPercepcion(mf, m);
        generarItemTotal(mf, m);

        cuentaCorrienteRN.generarItemCuentaCorriente(m);

        return m;
    }

    public MovimientoVenta generarComprobante(MovimientoTesoreria mt) throws ExcepcionGeneralSistema {

        Formulario formulario = formularioRN.getFormulario(mt.getComprobanteVenta(), mt.getPuntoVenta(), mt.getEntidad().getCondicionDeIva().getCodigo());

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de venta para el comprobante (" + mt.getComprobanteVenta().getCodigo() + ") "
                    + "para El punto de venta (" + mt.getPuntoVenta() + ") "
                    + "con la condición de iva (" + mt.getEntidad().getCondicionDeIva().getCodigo() + ")");
        }

        //Integer ultimoNumero = formularioRN.tomarProximoNumero(formulario);
        //m.setNumeroFormulario(ultimoNumero);
        //m.setNumeroFormulario(formulario.getUltimoNumero()+1);
        MovimientoVenta m;
        m = new MovimientoVenta();

        m.setEmpresa(mt.getEmpresa());
        m.setSucursal(mt.getSucursal());
        m.setCanalVenta(mt.getEntidad().getCanalVenta());
        m.setPuntoVenta(mt.getPuntoVenta());
        m.setUnidadNegocio(mt.getUnidadNegocio());

        m.setComprobante(mt.getComprobanteVenta());
        m.setFormulario(formulario);
        m.setNumeroFormulario(mt.getNumeroFormulario());
        m.setFechaMovimiento(mt.getFechaMovimiento());
        m.setFechaVencimiento(mt.getFechaMovimiento());

        m.setImputacionCuentaCorriente(mt.getComprobanteVenta().getTipoImputacion());
        m.setObservaciones(mt.getObservaciones());

        m.setCliente(mt.getEntidad());
        m.setClienteCuentaCorriente(mt.getEntidad());
        m.setRazonSocial(mt.getEntidad().getRazonSocial());
        m.setTipoDocumento(mt.getEntidad().getTipoDocumento());
        m.setNrodoc(mt.getEntidad().getNrodoc());
        m.setCondicionDePago(mt.getEntidad().getCondicionDePagoVentas());
        m.setCondicionDeIva(mt.getEntidad().getCondicionDeIva());
        m.setProvincia(mt.getEntidad().getProvincia());
        m.setLocalidad(mt.getEntidad().getLocalidad());
        m.setZona(mt.getEntidad().getZona());
        m.setDireccion(mt.getEntidad().getDireccion());
//        m.setNumero(mt.getEntidad().getNumero());
//        m.setDepartamentoPiso(mt.getEntidad().getDepartamentoPiso());
//        m.setDepartamentoNumero(mt.getEntidad().getDepartamentoNumero());

        m.setCobrador(mt.getCobrador());
        m.setVendedor(mt.getEntidad().getVendedor());
        m.setMonedaSecundaria(mt.getMonedaSecundaria());
        m.setMonedaRegistracion(mt.getMonedaRegistracion());
        m.setCotizacion(mt.getCotizacion());

        generarItemTotal(mt, m);

        if (mt.getComprobanteVenta().getTipoImputacion().equals("CC")) {

            if (mt.isEsAnticipo() || mt.getDebitos() == null || mt.getComprobante().getTipoComprobante().equals("R")) {
                cuentaCorrienteRN.generarItemCuentaCorrienteAnticipo(m);
            } else {
                cuentaCorrienteRN.generarItemCuentaCorrienteRecibo(m, mt.getDebitos());
            }
        }

        return m;
    }

    public MovimientoVenta generarComprobante(MovimientoEducacion me) throws ExcepcionGeneralSistema, Exception {

        Formulario formulario = formularioRN.getFormulario(me.getComprobante().getComprobanteVenta(), me.getPuntoVenta(), me.getAlumno().getCondicionDeIva().getCodigo());

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de venta para el comprobante (" + me.getComprobante().getComprobanteVenta().getCodigo() + ") "
                    + "para El punto de venta (" + me.getPuntoVenta() + ") "
                    + "con la condición de iva (" + me.getAlumno().getCondicionDeIva().getCodigo() + ")");
        }

        BigDecimal cotizacion = monedaRN.getCotizacionDia(parametrosRN.getParametro().getCodigoMonedaSecundaria());

        MovimientoVenta m = new MovimientoVenta();

        m.setEmpresa(me.getEmpresa());
        m.setSucursal(me.getSucursal());
        m.setPuntoVenta(me.getPuntoVenta());

        m.setComprobante(me.getComprobante().getComprobanteVenta());
        m.setFormulario(formulario);

        m.setImputacionCuentaCorriente(me.getComprobante().getComprobanteVenta().getTipoImputacion());
        m.setObservaciones(me.getObservaciones());
        m.setTipoExportacion("3");
        m.setFechaMovimiento(new Date());
        m.setFechaVencimiento(new Date());

        m.setCliente(me.getAlumno());
        m.setClienteCuentaCorriente(me.getAlumno());

        if (me.getAlumno().getRazonSocial() == null || me.getAlumno().getRazonSocial().isEmpty()) {

            m.setRazonSocial(me.getNombreAlumno());
            m.setTipoDocumento(me.getTipoDocumento());
            m.setNrodoc(me.getNrodoc());
            m.setDireccion(me.getDireccion());
            m.setLocalidad(me.getLocalidad());
            m.setProvincia(me.getProvincia());
            m.setCondicionDeIva(me.getAlumno().getCondicionDeIva());

            //m.setDireccionEntrega(me.getDireccion());
        } else {

            m.setRazonSocial(me.getAlumno().getRazonSocial());
            m.setTipoDocumento(me.getAlumno().getTipoDocumento1());
            m.setNrodoc(me.getAlumno().getNrodo1());
            m.setDireccion(me.getAlumno().getDireccionFactura());
            m.setLocalidad(me.getAlumno().getLocalidadFactura());
            m.setProvincia(me.getAlumno().getProvinciaFactura());
            m.setCondicionDeIva(me.getAlumno().getCondicionDeIva());

        }

        //m.setTransporte(me.getTransporte());
        //m.setEntidadTransporte(me.getEntidadTransporte());
        m.setCondicionDePago(me.getAlumno().getCondicionDePagoVentas());
        m.setVendedor(me.getAlumno().getVendedor());
        m.setCobrador(me.getAlumno().getCobrador());
        m.setRepartidor(me.getAlumno().getRepartidor());

        m.setMonedaSecundaria(me.getMonedaSecundaria());
        m.setMonedaRegistracion(me.getMonedaRegistracion());
        m.setCotizacion(cotizacion);
        m.setZona(me.getAlumno().getZona());
        m.setBienDeUso(m.getBienDeUso());

        generarItemsMovimiento(m);
        generarItemProducto(me, m);
        calcularImportes(m);
        limpiarConceptoEnCero(m);

        me.setMovimientoVenta(m);
//
//        if (m.getItemProducto() != null) {
//            m.getItemProducto().forEach(i -> System.err.println("p " + i));
//        }
//
//        if (m.getItemImpuesto() != null) {
//            m.getItemImpuesto().forEach(i -> System.err.println("i " + i));
//        }
//
//        if (m.getItemTotal() != null) {
//            m.getItemTotal().forEach(i -> System.err.println("t " + i));
//        }

        guardar(m);

        return m;
    }

    /**
     *
     * @param m
     * @throws ExcepcionGeneralSistema Generamos todos los items de acuerdo a
     * los conceptos configurados en el comprobante
     */
    public void generarItemsMovimiento(MovimientoVenta m) throws ExcepcionGeneralSistema {

        if (m.getComprobante() == null) {
            throw new ExcepcionGeneralSistema("El comprobante de venta no puede ser nulo, verifique la configuración del circuito");
        }

        if (m.getComprobante().getConceptos() == null || m.getComprobante().getConceptos().isEmpty()) {
            throw new ExcepcionGeneralSistema("El comprobante de venta no tienen conceptos configurados");
        }

        if (m.getItemProducto() == null) {
            m.setItemProducto(new ArrayList<>());
        } else {
            m.getItemProducto().clear();
        }

        if (m.getItemImpuesto() == null) {
            m.setItemImpuesto(new ArrayList<>());
        } else {
            m.getItemImpuesto().clear();
        }

        if (m.getItemPercepcion() == null) {
            m.setItemPercepcion(new ArrayList<>());
        } else {
            m.getItemPercepcion().clear();
        }

        if (m.getItemTotal() == null) {
            m.setItemTotal(new ArrayList<>());
        } else {
            m.getItemTotal().clear();
        }

        for (ConceptoComprobante cc : m.getComprobante().getConceptos()) {

            if (cc.getConcepto().getTipo().equals("A")) {

                ItemProductoVenta id = new ItemProductoVenta();
                id.setNroItem(m.getItemProducto().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());
                id.setConceptoAsociado(cc.getConceptoAsociado());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (cc.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                m.getItemProducto().add(id);
            }

            if (cc.getConcepto().getTipo().equals("I")) {

                ItemImpuestoVenta id = new ItemImpuestoVenta();
                id.setNroItem(m.getItemImpuesto().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());
                id.setTipoImpuesto(cc.getTipoImpuesto());
                id.setEditaImporte(cc.getEditaSiCero());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (cc.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                m.getItemImpuesto().add(id);
            }

            if (cc.getConcepto().getTipo().equals("P")) {

                ItemPercepcionVenta id = new ItemPercepcionVenta();
                id.setNroItem(m.getItemPercepcion().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());
                id.setTipoImpuesto(cc.getTipoImpuesto());
                id.setEditaImporte(cc.getEditaSiCero());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (cc.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                m.getItemPercepcion().add(id);
            }

            if (cc.getConcepto().getTipo().equals("T")) {

                ItemTotalVenta id = new ItemTotalVenta();
                id.setNroItem(m.getItemTotal().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (cc.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                m.getItemTotal().add(id);
            }
        }
    }

    public void generarItemsMovimientoVenta(MovimientoFacturacion m) throws ExcepcionGeneralSistema {

        if (m.getComprobanteVenta() == null) {
            throw new ExcepcionGeneralSistema("El comprobante de venta no puede ser nulo, verifique la configuración del circuito");
        }

        if (m.getComprobanteVenta().getConceptos() == null || m.getComprobanteVenta().getConceptos().isEmpty()) {
            throw new ExcepcionGeneralSistema("El comprobante de venta no tienen conceptos configurados");
        }

        if (m.getItemsProductoVenta() == null) {
            m.setItemsProductoVenta(new ArrayList<>());
        } else {
            m.getItemsProductoVenta().clear();
        }

        if (m.getItemsImpuestoVenta() == null) {
            m.setItemsImpuestoVenta(new ArrayList<>());
        } else {
            m.getItemsImpuestoVenta().clear();
        }

        if (m.getItemsPercepcionVenta() == null) {
            m.setItemsPercepcionVenta(new ArrayList<>());
        } else {
            m.getItemsPercepcionVenta().clear();
        }

        if (m.getItemsTotalVenta() == null) {
            m.setItemsTotalVenta(new ArrayList<>());
        } else {
            m.getItemsTotalVenta().clear();
        }

        for (ConceptoComprobante cc : m.getComprobanteVenta().getConceptos()) {

            if (cc.getConcepto().getTipo().equals("A")) {

                ItemProductoVenta id = new ItemProductoVenta();
                id.setNroItem(m.getItemsProductoVenta().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());
                id.setConceptoAsociado(cc.getConceptoAsociado());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (cc.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                m.getItemsProductoVenta().add(id);
            }

            if (cc.getConcepto().getTipo().equals("I")) {

                ItemImpuestoVenta id = new ItemImpuestoVenta();
                id.setNroItem(m.getItemsImpuestoVenta().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());
                id.setTipoImpuesto(cc.getTipoImpuesto());
                id.setEditaImporte(cc.getEditaSiCero());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (cc.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                m.getItemsImpuestoVenta().add(id);
            }

            if (cc.getConcepto().getTipo().equals("P")) {

                ItemPercepcionVenta id = new ItemPercepcionVenta();
                id.setNroItem(m.getItemsPercepcionVenta().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());
                id.setTipoImpuesto(cc.getTipoImpuesto());
                id.setEditaImporte(cc.getEditaSiCero());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (cc.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                m.getItemsPercepcionVenta().add(id);
            }

            if (cc.getConcepto().getTipo().equals("T")) {

                ItemTotalVenta id = new ItemTotalVenta();
                id.setNroItem(m.getItemsTotalVenta().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (cc.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(cc.getConcepto().getCuentaContable());
                }

                m.getItemsTotalVenta().add(id);
            }
        }
    }

    private void generarItemProducto(MovimientoFacturacion mf, MovimientoVenta mv) throws ExcepcionGeneralSistema {

        if (mf.getItemsProducto() == null) {
            return;
        }

        mv.getItemProducto().clear();

        for (ItemMovimientoFacturacion ipf : mf.getItemsProducto()) {

            ItemProductoVenta ipv = new ItemProductoVenta();

            ipv.setNroItem(ipf.getNroitm());
            ipv.setMovimiento(mv);

            ipv.setProducto(ipf.getProducto());
            ipv.setDescripcion(ipf.getDescripcion());
            ipv.setCuentaContable(ipf.getProducto().getCuentaContableVenta());
            ipv.setDeposito(ipf.getDeposito());
            ipv.setKilogramosAforado(ipf.getKilogramosAforado());
            ipv.setKilogramosEfectivo(ipf.getKilogramosEfectivo());

            ipv.setConcepto(ipf.getConcepto());
            ipv.setUnidadMedida(ipf.getUnidadMedida());
            ipv.setCantidad(ipf.getCantidad());
            ipv.setValorDeclarado(ipf.getValorDeclarado());
            ipv.setCotizacion(ipf.getCotizacion());
            ipv.setObservaciones(ipf.getObservaciones());

            ipv.setPrecio(ipf.getPrecio());
            ipv.setPrecioSecundario(ipf.getPrecioSecundario());

            ipv.setPorcentajeBonificacion1(ipf.getPorcentajeBonificacion1());
            ipv.setPorcentajeBonificacion2(ipf.getPorcentajeBonificacion2());
            ipv.setPorcentajeBonificacion3(ipf.getPorcentajeBonificacion3());
            ipv.setPorcentajeBonificacion4(ipf.getPorcentajeBonificacion4());
            ipv.setPorcentajeBonificacion5(ipf.getPorcentajeBonificacion5());
            ipv.setPorcentajeBonificacion6(ipf.getPorcentajeBonificacion6());
            ipv.setPorcentaTotalBonificacion(ipf.getPorcentaTotalBonificacion());

            ipv.setImporte(ipf.getTotalLinea());
            ipv.setImporteSecundario(ipf.getTotalLineaSecundario());

            //Si el comprobante de ventas es Debe, los productos van al Haber
            if (mv.getComprobante().getDebeHaber().equals("D")) {
                ipv.setDebeHaber("H");
                ipv.setImporteHaber(ipf.getTotalLinea());
                ipv.setImporteHaberSecundario(ipf.getTotalLineaSecundario());
            } else {
                ipv.setDebeHaber("D");
                ipv.setImporteDebe(ipf.getTotalLinea());
                ipv.setImporteDebeSecundario(ipf.getTotalLineaSecundario());
            }

            ipv.setPrecioCosto(ipf.getPrecioCosto());

            mv.getItemProducto().add(ipv);
        }
    }

    private void generarItemProducto(MovimientoEducacion me, MovimientoVenta mv) throws ExcepcionGeneralSistema {

        if (me.getItemsCuentaCorriente() == null) {
            return;
        }

        mv.getItemProducto().clear();

        for (AplicacionCuentaCorrienteEducacion icc : me.getItemsCuentaCorriente()) {

            if (icc.getItemMovimientoAplicado().getConcepto().getProducto() == null) {
                throw new ExcepcionGeneralSistema("El concepto " + icc.getItemMovimientoAplicado().getConcepto().getDescripcion() + " No tiene un producto asociado para facturar");
            }

            ItemProductoVenta ipv = new ItemProductoVenta();

            ipv.setNroItem(mv.getItemProducto().size() + 1);
            ipv.setMovimiento(mv);

            ipv.setProducto(icc.getItemMovimientoAplicado().getConcepto().getProducto());
            ipv.setDescripcion(icc.getItemMovimientoAplicado().getConcepto().getProducto().getDescripcion());
            ipv.setCuentaContable(icc.getItemMovimientoAplicado().getCuentaContable());

            ipv.setConcepto(icc.getItemMovimientoAplicado().getConcepto().getProducto().getConceptoVenta());
            ipv.setUnidadMedida(icc.getItemMovimientoAplicado().getConcepto().getProducto().getUnidadDeMedida());
            ipv.setCantidad(BigDecimal.ONE);
            ipv.setCotizacion(mv.getCotizacion());
            ipv.setObservaciones(icc.getObservaciones());

            double tasaIVA = 0;

            //Buscamos el impuesto iva asociado al concepto para descontarle al precio
            for (ImpuestoPorConcepto impuestoConcepto : ipv.getConcepto().getImpuestos()) {

                if (impuestoConcepto.getCodimp().equals("IVA")) {
                    tasaIVA = impuestoConcepto.getTasa().doubleValue();
                    break;
                }
            }

            tasaIVA = tasaIVA / 100 + 1;
            //Sacamos el iva al producto ya que en la cuenta corriente va el total
            double precio = icc.getImporte() / tasaIVA;
            double importe = precio * ipv.getCantidad().doubleValue();

            ipv.setPrecio(new BigDecimal(precio));
            ipv.setPrecioSecundario(new BigDecimal(precio / mv.getCotizacion().doubleValue()));

            ipv.setImporte(new BigDecimal(importe));
            ipv.setImporteSecundario(new BigDecimal(importe / mv.getCotizacion().doubleValue()));

            //Si el comprobante de ventas es Debe, los productos van al Haber
            if (mv.getComprobante().getDebeHaber().equals("D")) {
                ipv.setDebeHaber("H");
                ipv.setImporteHaber(ipv.getImporte());
                ipv.setImporteHaberSecundario(ipv.getImporteSecundario());
            } else {
                ipv.setDebeHaber("D");
                ipv.setImporteDebe(ipv.getImporte());
                ipv.setImporteDebeSecundario(ipv.getImporteSecundario());
            }

            mv.getItemProducto().add(ipv);
        }
    }

    private void generarItemImpuesto(MovimientoFacturacion mf, MovimientoVenta mv) {

        mv.getItemImpuesto().clear();

        for (ItemImpuestoVenta iiv : mf.getItemsImpuestoVenta()) {

            if (iiv.getImporte().compareTo(BigDecimal.ZERO) != 0) {

                iiv.setNroItem(mv.getItemImpuesto().size() + 1);
                iiv.setMovimiento(mv);
                iiv.setCantidad(BigDecimal.ZERO);
                iiv.setCotizacion(mv.getCotizacion());

                //Si el comprobante de ventas es Debe, los impuestos van al Haber
                if (iiv.getDebeHaber().equals("H")) {
                    iiv.setImporteHaber(iiv.getImporte());
                    iiv.setImporteHaberSecundario(iiv.getImporteSecundario());
                } else {
                    iiv.setImporteDebe(iiv.getImporte());
                    iiv.setImporteDebeSecundario(iiv.getImporteSecundario());
                }
                mv.getItemImpuesto().add(iiv);
            }
        }
    }

    private void generarItemPercepcion(MovimientoFacturacion mf, MovimientoVenta mv) {

        mv.getItemPercepcion().clear();

        for (ItemPercepcionVenta ipv : mf.getItemsPercepcionVenta()) {

            if (ipv.getImporte().compareTo(BigDecimal.ZERO) != 0) {

                ipv.setNroItem(mv.getItemImpuesto().size() + 1);
                ipv.setMovimiento(mv);
                ipv.setCantidad(BigDecimal.ZERO);
                ipv.setCotizacion(mv.getCotizacion());

                //Si el comprobante de ventas es Debe, los impuestos van al Haber
                if (ipv.getDebeHaber().equals("H")) {
                    ipv.setImporteHaber(ipv.getImporte());
                    ipv.setImporteHaberSecundario(ipv.getImporteSecundario());
                } else {
                    ipv.setImporteDebe(ipv.getImporte());
                    ipv.setImporteDebeSecundario(ipv.getImporteSecundario());
                }
                mv.getItemPercepcion().add(ipv);
            }
        }
    }

    /**
     * Genera item total desde un movimiento de facturación
     */
    private void generarItemTotal(MovimientoFacturacion mf, MovimientoVenta mv) {

        mv.getItemTotal().clear();

        for (ItemTotalVenta itv : mf.getItemsTotalVenta()) {

            if (itv.getImporte().compareTo(BigDecimal.ZERO) != 0) {

//                System.err.println("Item Total: " + itv.getImporte());
                itv.setNroItem(mv.getItemTotal().size() + 1);
                itv.setMovimiento(mv);
                itv.setCantidad(BigDecimal.ZERO);
                itv.setCotizacion(mv.getCotizacion());

                //Si el comprobante de ventas es Debe, los impuestos van al Haber
                if (itv.getDebeHaber().equals("H")) {
                    itv.setImporteHaber(itv.getImporte());
                    itv.setImporteHaberSecundario(itv.getImporteSecundario());
                } else {
                    itv.setImporteDebe(itv.getImporte());
                    itv.setImporteDebeSecundario(itv.getImporteSecundario());
                }
                mv.getItemTotal().add(itv);
            }
        }
    }

    private void generarItemTotal(MovimientoTesoreria mt, MovimientoVenta mv) {

        mv.getItemTotal().clear();

        //Item Debe
        ItemTotalVenta itvd = new ItemTotalVenta();
        itvd.setNroItem(mv.getItemTotal().size() + 1);
        itvd.setMovimiento(mv);
        itvd.setDebeHaber("D");
        itvd.setConcepto(conceptoDAO.getConcepto("VT", "T", "CTR001"));
        itvd.setCuentaContable(itvd.getConcepto().getCuentaContable());
        itvd.setCantidad(BigDecimal.ZERO);
        itvd.setCotizacion(mv.getCotizacion());

        // Item Haber
        ItemTotalVenta itvh = new ItemTotalVenta();
        itvh.setNroItem(mv.getItemTotal().size() + 1);
        itvh.setMovimiento(mv);
        itvh.setDebeHaber("H");
        itvh.setConcepto(conceptoDAO.getConcepto("VT", "T", "DVT001"));
        itvh.setCuentaContable(itvh.getConcepto().getCuentaContable());
        itvh.setCantidad(BigDecimal.ZERO);
        itvh.setCotizacion(mv.getCotizacion());

        BigDecimal importeDebe = BigDecimal.ZERO;
        BigDecimal importeHaber = BigDecimal.ZERO;

        if (mt.getComprobante().getTipoComprobante().equals("T")) {

            importeDebe = mt.getImporteTotalDebe();
            importeHaber = mt.getImporteTotalHaber();
        }

        if (mt.getComprobante().getTipoComprobante().equals("E")) {

            importeDebe = mt.getImporteTotalHaber();
            importeHaber = mt.getImporteTotalHaber();

        }

        if (mt.getComprobante().getTipoComprobante().equals("I")) {

            importeDebe = mt.getImporteTotalDebe();
            importeHaber = mt.getImporteTotalDebe();

        }

        //Movimento de reversión tiene que intercambiar los conceptos
        if (mt.getComprobante().getTipoComprobante().equals("R")) {

            if (mt.getImporteTotalDebe().compareTo(BigDecimal.ZERO) > 0) {
                importeDebe = mt.getImporteTotalDebe();
                importeHaber = mt.getImporteTotalDebe();
            } else {
                importeDebe = mt.getImporteTotalHaber();
                importeHaber = mt.getImporteTotalHaber();
            }

            itvd.setConcepto(conceptoDAO.getConcepto("VT", "T", "DVT001"));
            itvh.setConcepto(conceptoDAO.getConcepto("VT", "T", "CTR001"));

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
        mv.getItemTotal().add(itvd);
        mv.getItemTotal().add(itvh);

    }

    public void controlComprobante(MovimientoVenta m) throws ExcepcionGeneralSistema {

        String sErrores = "";

        sErrores += moduloRN.canSaveModulo("VT", m.getFechaMovimiento());

        if (m.getMovimientoContabilidad() != null) {
            throw new ExcepcionGeneralSistema("El comprobante fue pasado a la contabilidad, no es posible guardar");
        }

        if (m.getImporteTotalDebe().compareTo(BigDecimal.ZERO) < 0 || m.getImporteTotalHaber().compareTo(BigDecimal.ZERO) < 0) {

            sErrores += "| Los importes \"Debe\" y \"Haber\" tienen que ser mayor a cero";
        }

        if (m.getImporteTotalDebe().compareTo(m.getImporteTotalHaber()) != 0) {
            sErrores += "| Los importes \"Debe\" y \"Haber\" en el comprobante de ventas tienen que ser iguales ";
        }

        if (m.getCliente() == null) {
            sErrores += "| El cliente no puede estar vacío ";
        }

        if (m.getLocalidad() == null) {
            sErrores += "| La localidad no puede estar vacía ";
        }

        if (m.getProvincia() == null) {
            sErrores += "| La provincia no puede estar vacía ";
        }

        if (m.getDireccion() == null) {
            sErrores += "| La dirección no puede estar vacía ";
        }

        if (m.getCliente() == null) {
            sErrores += "| El cliente no puede estar vacío ";
        }

        if (m.getCondicionDePago().getImputaCuentaCorriente().equals("N") && m.getImputacionCuentaCorriente().equals("CC")) {
            sErrores += "| La condición de pago asignada al comprobante no permite generar comprobantes en cuenta corriente. Por favor modifique la condición de pago ";
        }

        //Control items
        if (m.getComprobante().getEsComprobanteReversion().equals("S") && m.getMovimientoReversion() == null) {
            sErrores += "| El movimiento es de reversión y no tienen asignado el movimiento a revertir ";
        }

        for (ItemProductoVenta ip : m.getItemProducto()) {
            if (ip.getConcepto() == null) {
                sErrores += "| El item producto " + ip.getNroItem() + " no tienen asignado el concepto de venta ";
            }

            if (ip.getCuentaContable() == null) {
                sErrores += "| El item producto " + ip.getNroItem() + " no tienen asignada la cuenta contable ";
            }
        }

        for (ItemImpuestoVenta ii : m.getItemImpuesto()) {
            if (ii.getConcepto() == null) {
                sErrores += "| El item impuesto " + ii.getNroItem() + " no tienen asignado el concepto de venta ";
            }

            if (ii.getCuentaContable() == null) {
                sErrores += "| El item impuesto " + ii.getNroItem() + " no tienen asignada la cuenta contable ";
            }
        }

        for (ItemPercepcionVenta ip : m.getItemPercepcion()) {
            if (ip.getConcepto() == null) {
                sErrores += "| El item percepción " + ip.getNroItem() + " no tienen asignado el concepto de venta ";
            }

            if (ip.getCuentaContable() == null) {
                sErrores += "| El item percepción " + ip.getNroItem() + " no tienen asignada la cuenta contable ";
            }
        }

        for (ItemTotalVenta it : m.getItemTotal()) {
            if (it.getConcepto() == null) {
                sErrores += "| El item total " + it.getNroItem() + " no tienen asignado el concepto de venta ";
            }

            if (it.getCuentaContable() == null) {
                sErrores += "| El item total " + it.getNroItem() + " no tienen asignada la cuenta contable ";
            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    private void limpiarConceptoEnCero(MovimientoVenta m) {

        List<ItemProductoVenta> itemProductoAux = m.getItemProducto();
        List<ItemImpuestoVenta> itemImpuestoAux = m.getItemImpuesto();
        List<ItemPercepcionVenta> itemPercepcionAux = m.getItemPercepcion();
        List<ItemTotalVenta> itemTotalAux = m.getItemTotal();

        m.setItemProducto(new ArrayList<ItemProductoVenta>());
        m.setItemImpuesto(new ArrayList<ItemImpuestoVenta>());
        m.setItemPercepcion(new ArrayList<ItemPercepcionVenta>());
        m.setItemTotal(new ArrayList<ItemTotalVenta>());

        for (ItemProductoVenta im : itemProductoAux) {
            if (im.getImporte() != null && im.getImporte().compareTo(BigDecimal.ZERO) > 0) {
                m.getItemProducto().add(im);
            }
        }

        for (ItemImpuestoVenta im : itemImpuestoAux) {
            if (im.getImporte() != null && im.getImporte().compareTo(BigDecimal.ZERO) > 0) {
                m.getItemImpuesto().add(im);
            }
        }

        for (ItemPercepcionVenta im : itemPercepcionAux) {
            if (im.getImporte() != null && im.getImporte().compareTo(BigDecimal.ZERO) > 0) {
                m.getItemPercepcion().add(im);
            }
        }

        for (ItemTotalVenta im : itemTotalAux) {
            if (im.getImporte() != null && im.getImporte().compareTo(BigDecimal.ZERO) > 0) {
                m.getItemTotal().add(im);
            }
        }
    }

    public MovimientoVenta getMovimiento(Integer id) {

        return ventaDAO.getObjeto(MovimientoVenta.class,
                id);
    }

    public List<MovimientoVenta> getComprobantesPendienteAutorizar() {

        return ventaDAO.getComprobantesPendienteAutorizarAFIP();
    }

    public String generarCodigoBarra(MovimientoVenta m) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        String cuit = principalBean.getEmpresa().getCuit().replace("-", "");
        String codComp = m.getFormulario().getCodigoDGI();
        String ptoVta = m.getPuntoVenta().getCodigo();
        String caeNum = (m.getNumeroCAE() == null ? "" : m.getNumeroCAE());
        String caeVto = sdf.format((m.getVencimientoCAE() == null ? new Date() : m.getVencimientoCAE()));

        String codigo = cuit + codComp + ptoVta + caeNum + caeVto;
        return codigo + Numeros.digitoVerificador(codigo);

        //sCUIT + sCODComp + sPtoVta + sCAE + sVtoCAE;
    }

    public void asignarFormulario(MovimientoVenta m) throws ExcepcionGeneralSistema {

        if (m.getCliente() == null) {
            return;
        }

        Formulario formulario = formularioRN.getFormulario(m.getComprobante(), m.getPuntoVenta(), m.getCondicionDeIva().getCodigo());

        //Este número es temporal y puede cambiar al guardar el objeto.
        m.setNumeroFormulario(formulario.getUltimoNumero() + 1);
        m.setFormulario(formulario);
    }

    public void calcularImportes(MovimientoVenta m) {

        BigDecimal gravado1050 = BigDecimal.ZERO;
        BigDecimal gravado2100 = BigDecimal.ZERO;
        BigDecimal gravado2700 = BigDecimal.ZERO;
        BigDecimal iva1050;
        BigDecimal iva2100;
        BigDecimal iva2700;
        BigDecimal impTotal = BigDecimal.ZERO;

        BigDecimal importeTotalDebe = BigDecimal.ZERO;
        BigDecimal importeTotalHaber = BigDecimal.ZERO;

        for (ItemProductoVenta i : m.getItemProducto()) {
            if (i.getDebeHaber().equals("D")) {
                importeTotalDebe = importeTotalDebe.add(i.getImporte());
            } else {
                importeTotalHaber = importeTotalHaber.add(i.getImporte());
            }

            if (i.getConcepto().getCodigo().equals("V001")) {
                gravado2100 = gravado2100.add(i.getImporte());
            }

            if (i.getConcepto().getCodigo().equals("V002")) {
                gravado1050 = gravado1050.add(i.getImporte());
            }

            if (i.getConcepto().getCodigo().equals("V003")) {
                gravado2700 = gravado2700.add(i.getImporte());
            }

            impTotal = impTotal.add(i.getImporte());
        }

        //calculamos los importes de iva
        iva1050 = gravado1050.multiply(new BigDecimal("0.105")).setScale(2, RoundingMode.HALF_UP);
        iva2100 = gravado2100.multiply(new BigDecimal("0.21")).setScale(2, RoundingMode.HALF_UP);
        iva2700 = gravado2700.multiply(new BigDecimal("0.27")).setScale(2, RoundingMode.HALF_UP);

        for (ItemImpuestoVenta i : m.getItemImpuesto()) {

            if (i.getConcepto().getCodigo().equals("IVA001")) {
                i.setImporte(iva2100);
            }

            if (i.getConcepto().getCodigo().equals("IVA002")) {
                i.setImporte(iva1050);
            }

            if (i.getConcepto().getCodigo().equals("IVA003")) {
                i.setImporte(iva2700);
            }

            if (i.getDebeHaber().equals("D")) {
                importeTotalDebe = importeTotalDebe.add(i.getImporte());
            } else {
                importeTotalHaber = importeTotalHaber.add(i.getImporte());
            }

            impTotal = impTotal.add(i.getImporte());
        }

        for (ItemTotalVenta i : m.getItemTotal()) {

            i.setImporte(impTotal);

            if (i.getDebeHaber().equals("D")) {
                importeTotalDebe = importeTotalDebe.add(i.getImporte());
            } else {
                importeTotalHaber = importeTotalHaber.add(i.getImporte());
            }
        }

        m.setImporteTotalDebe(importeTotalDebe);
        m.setImporteTotalHaber(importeTotalHaber);

//        System.out.println("importeTotalDebe" + importeTotalDebe);
//        System.out.println("importeTotalHaber" + importeTotalHaber);
    }

    public MovimientoVenta getMovimiento(String codFormulario, Integer numeroFormulario) {
        return ventaDAO.getMovimiento(codFormulario, numeroFormulario);
    }

    public List<MovimientoVenta> getListaByBusqueda(Map<String, String> filtro, int cantMax) {

        return ventaDAO.getListaByBusqueda(filtro, null, cantMax);
    }

    public List<MovimientoVenta> getListaByBusqueda(Map<String, String> filtro, List<String> orderBy, int cantMax) {

        return ventaDAO.getListaByBusqueda(filtro, orderBy, cantMax);
    }

    public MovimientoVenta revertirMovimiento(MovimientoVenta m) throws ExcepcionGeneralSistema {

        if (m == null) {
            throw new ExcepcionGeneralSistema("Movimiento de venta nulo");
        }

        if (m.getMovimientoContabilidad() != null) {
            throw new ExcepcionGeneralSistema("El comprobante fue pasado a la contabilidad, no es posible revertirlo");
        }

        if (m.getComprobante().getComprobanteReversion() == null) {
            throw new ExcepcionGeneralSistema("El comprobante " + m.getComprobante().getDescripcion() + " no tienen comprobante de reversión asociado");
        }

        MovimientoVenta mr = new MovimientoVenta();

        mr.setEmpresa(m.getEmpresa());
        mr.setSucursal(m.getSucursal());
        mr.setPuntoVenta(m.getPuntoVenta());
        mr.setUnidadNegocio(m.getUnidadNegocio());

        mr.setComprobante(m.getComprobante().getComprobanteReversion());
        mr.setFechaMovimiento(m.getFechaMovimiento());
        mr.setFechaVencimiento(m.getFechaVencimiento());

        mr.setImputacionCuentaCorriente(m.getImputacionCuentaCorriente());

        mr.setObservaciones("");
        mr.setMonedaSecundaria(m.getMonedaSecundaria());
        mr.setMonedaRegistracion(m.getMonedaRegistracion());
        mr.setCotizacion(m.getCotizacion());

        mr.setCliente(m.getCliente());
        mr.setClienteCuentaCorriente(m.getClienteCuentaCorriente());
        mr.setRazonSocial(m.getRazonSocial());
        mr.setNrodoc(m.getNrodoc());
        mr.setTipoDocumento(m.getTipoDocumento());
        mr.setProvincia(m.getProvincia());
        mr.setLocalidad(m.getLocalidad());
        mr.setBarrio(m.getBarrio());
        mr.setDireccion(m.getDireccion());
//        mr.setNumero(m.getNumero());
//        mr.setDepartamentoPiso(m.getDepartamentoPiso());
//        mr.setDepartamentoNumero(m.getDepartamentoNumero());

        mr.setCondicionDeIva(m.getCondicionDeIva());
        mr.setCondicionDePago(m.getCondicionDePago());

        mr.setListaDePrecio(m.getListaDePrecio());
        mr.setMonedaListaPrecio(m.getMonedaListaPrecio());
        mr.setVendedor(m.getVendedor());
        mr.setCobrador(m.getCobrador());
        mr.setMovimientoReversion(m);

        asignarFormulario(mr);

        for (ItemProductoVenta i : m.getItemProducto()) {

            ItemProductoVenta ir = new ItemProductoVenta();

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

        for (ItemImpuestoVenta i : m.getItemImpuesto()) {

            ItemImpuestoVenta ir = new ItemImpuestoVenta();

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

        for (ItemPercepcionVenta i : m.getItemPercepcion()) {

            ItemPercepcionVenta ir = new ItemPercepcionVenta();

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

        for (ItemTotalVenta i : m.getItemTotal()) {

            ItemTotalVenta ir = new ItemTotalVenta();

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

        return mr;
    }

    private void tomarNumeroFormulario(MovimientoVenta m, WSFEv1 wsfe) throws ExcepcionGeneralSistema, Exception {

        if (m == null) {
            return;
        }

        if (wsfe != null) {

            int proxNum = wsfe.tomarProximoNumero(m);

            m.setNumeroFormulario(proxNum);
            m.setInstanciaCAE("B");

            m.getFormulario().setUltimoNumero(proxNum);
            formularioRN.guardar(m.getFormulario());

        } else if (m.getFormulario().getTipoNumeracion().equals("A")) {
            m.setNumeroFormulario(formularioRN.tomarProximoNumero(m.getFormulario()));
        }

        m.setNumeroFormulario(formularioRN.tomarProximoNumero(m.getFormulario()));

        if (m.getFechaMovimiento() != null) {
            m.getFormulario().setUltimaFecha(m.getFechaMovimiento());
        }
    }

}
