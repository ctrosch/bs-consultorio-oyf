/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.rn;

import bs.administracion.rn.ModuloRN;
import bs.administracion.rn.ParametrosRN;
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
import bs.global.util.Numeros;
import bs.prestamo.dao.MovimientoPrestamoDAO;
import bs.prestamo.modelo.ItemCapitalPrestamo;
import bs.prestamo.modelo.ItemDescuentoPrestamo;
import bs.prestamo.modelo.ItemGastoOtorgamientoPrestamo;
import bs.prestamo.modelo.ItemInteresMoraPrestamo;
import bs.prestamo.modelo.ItemInteresPrestamo;
import bs.prestamo.modelo.ItemMicroseguroPrestamo;
import bs.prestamo.modelo.ItemPendienteCuentaCorrientePrestamo;
import bs.prestamo.modelo.ItemTotalPrestamo;
import bs.prestamo.modelo.MovimientoPrestamo;
import bs.prestamo.modelo.Prestamo;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.tesoreria.rn.MovimientoTesoreriaRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Claudio
 */
@Stateless
public class MovimientoPrestamoRN implements Serializable {

    @EJB
    private MovimientoPrestamoDAO movimientoPrestamoDAO;
    @EJB
    private ConceptoDAO conceptoDAO;
    @EJB
    private ComprobanteDAO comprobantePrestamoDAO;
    @EJB
    private CuentaCorrientePrestamoRN cuentaCorrienteRN;
    @EJB
    private PrestamoRN prestamoRN;
    @EJB
    private EstadoPrestamoRN estadoPrestamoRN;
    @EJB
    private MovimientoTesoreriaRN tesoreriaRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private MonedaRN monedaRN;
    @EJB
    private ModuloRN moduloRN;
    @EJB
    private PuntoVentaRN puntoVentaRN;
    @EJB
    protected ParametrosRN parametrosRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized MovimientoPrestamo guardar(MovimientoPrestamo m) throws Exception {

        if (m.getId() == null && m.getMovimientoTesoreria() != null) {

            tesoreriaRN.sincronizarDatos(m);
            tesoreriaRN.calcularImportes(m.getMovimientoTesoreria());
            tesoreriaRN.controlComprobante(m.getMovimientoTesoreria());
            tesoreriaRN.limpiarConceptoEnCero(m.getMovimientoTesoreria());
        }

        //calcularImportes(m);
        controlComprobante(m);
        limpiarConceptoEnCero(m);

        if (m.getId() == null) {

            tomarNumeroFormulario(m);

            // Reprogramación de deuda
            if ((m.getComprobante().getTipoComprobante().equals("CR") || m.getComprobante().getTipoComprobante().equals("CJ"))
                    && m.getComprobante().getComprobanteReprogramacion() != null
                    && m.getPrestamoReprogramado() != null) {

                generarComprobanteReprogramacion(m);
                tomarNumeroFormulario(m.getMovimientoReprogramacion());

                m.getPrestamoReprogramado().setEstado(estadoPrestamoRN.getEstadoPrestamo("C"));

                ajustarImportesCancelarPorReprogramacion(m);
                cuentaCorrienteRN.generarItemAplicacionCuentaCorriente(m, null, m.getDebitosPrestamo());
            } else {
                cuentaCorrienteRN.generarItemCuentaCorriente(m);
            }

            movimientoPrestamoDAO.crear(m);
            cambiarEstadoPrestamo(m);
        } else {

        }
        return m;
    }

    public MovimientoPrestamo nuevoMovimiento(String CODPR, String SUCPR, String CODCJ, String SUCCJ) throws Exception {

        Comprobante comprobante = comprobantePrestamoDAO.getComprobante("PR", CODPR);
        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(SUCPR);
        Formulario formulario = formularioRN.getFormulario(comprobante, puntoVenta, "X");

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No se encontró comprobante de prestamo (" + CODPR + ")");
        }
        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No encontró punto de venta (" + SUCPR + ")");
        }

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de venta para el comprobante (" + CODPR + ") "
                    + "para El punto de venta (" + SUCPR + ") "
                    + "con la condición de iva (X)");
        }

        MovimientoPrestamo m = new MovimientoPrestamo();
        Moneda moneda = monedaRN.getMoneda(parametrosRN.getParametro().getCodigoMonedaSecundaria());
        double cotizacion = monedaRN.getCotizacionDia(parametrosRN.getParametro().getCodigoMonedaSecundaria()).doubleValue();

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

        if (m.getComprobante().getEsComprobanteReversion().equals("N")) {
            generarItemsMovimiento(m);
        }

        if (CODCJ != null && !CODCJ.isEmpty() && SUCCJ != null && !SUCCJ.isEmpty()) {
            MovimientoTesoreria movimientoTesoreria = tesoreriaRN.nuevoMovimiento(CODCJ, SUCCJ);
            m.setMovimientoTesoreria(movimientoTesoreria);
        }

        return m;
    }

    public MovimientoPrestamo nuevoMovimiento(Comprobante comprobante, PuntoVenta puntoVenta) throws Exception {

        return nuevoMovimiento(comprobante.getCodigo(), puntoVenta.getCodigo(), "", "");

    }

    public void controlComprobante(MovimientoPrestamo m) throws ExcepcionGeneralSistema {

        String sErrores = "";

        sErrores += moduloRN.canSaveModulo("PR", m.getFechaMovimiento());

        if (m.getMovimientoContabilidad() != null) {
            throw new ExcepcionGeneralSistema("El comprobante fue pasado a la contabilidad, no es posible guardar");
        }

        if (m.getDestinatario() == null) {
            sErrores += " El destinatario no puede estar vacío";
        }

        if (m.getPrestamo() == null) {
            sErrores += "| El préstamo no puede estar vacío";
        }

        //Control items
        if (m.getComprobante().getEsComprobanteReversion().equals("S") && m.getMovimientoReversion() == null) {
            sErrores += "| El movimiento es de reversión y no tienen asignado el movimiento a revertir ";
        }

        for (ItemCapitalPrestamo ip : m.getItemCapital()) {
            if (ip.getConcepto() == null) {
                sErrores += "| El item amortización " + ip.getNroItem() + " no tienen asignado el concepto ";
            }

            if (ip.getCuentaContable() == null) {
                sErrores += "| El item amortización " + ip.getNroItem() + " no tienen asignada la cuenta contable ";
            }
        }

        for (ItemInteresPrestamo ii : m.getItemIntereses()) {
            if (ii.getConcepto() == null) {
                sErrores += "| El item interés " + ii.getNroItem() + " no tienen asignado el concepto ";
            }

            if (ii.getCuentaContable() == null) {
                sErrores += "| El item interés " + ii.getNroItem() + " no tienen asignada la cuenta contable ";
            }
        }

//        if(m.getItemTotal()==null || m.getItemTotal().isEmpty()){
//            throw new ExcepcionGeneralSistema("No existe item total, no es posible guardar");
//        }
        for (ItemTotalPrestamo it : m.getItemTotal()) {
            if (it.getConcepto() == null) {
                sErrores += "| El item total " + it.getNroItem() + " no tienen asignado el concepto ";
            }

            if (it.getCuentaContable() == null) {
                sErrores += "| El item total " + it.getNroItem() + " no tienen asignada la cuenta contable ";
            }
        }

//        if(m.getComprobante().getTipoComprobante().equals("RD")){
//
//            if(m.getDebitosPrestamo()!=null){
//
//                for(ItemPendienteCuentaCorrientePrestamo i:m.getDebitosPrestamo()){
//
//                    if(m.getFechaMovimiento().after(i.getFechaVencimiento())){
//                        throw new ExcepcionGeneralSistema("No es posible reprogramar un préstamos con cuotas vencidas.");
//                    }
//                }
//            }
//
//        }
        if (m.getComprobante().getTipoComprobante().equals("LQ")) {

            if (m.getMovimientoTesoreria() != null && Numeros.redondear(m.getImporteCapital(), 2) != Numeros.redondear(m.getMovimientoTesoreria().getImporteTotal(), 2).doubleValue()) {

                sErrores += "| El importe a liquidar ($ " + Numeros.redondear(m.getImporteCapital(), 2) + ") no coincide con el importe total de los conceptos ingresados ($ " + m.getMovimientoTesoreria().getImporteTotal() + ") ";
            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public MovimientoPrestamo generarItemsCuentaCorriente(MovimientoPrestamo m) throws ExcepcionGeneralSistema {

        if (m == null) {
            throw new ExcepcionGeneralSistema("El movimiento es nulo, nada para generar");
        }

        if (m.getItemCuentaCorriente() == null || m.getItemCuentaCorriente().isEmpty()) {
            cuentaCorrienteRN.generarItemCuentaCorriente(m);
            m = (MovimientoPrestamo) movimientoPrestamoDAO.editar(m);
        } else {
            throw new ExcepcionGeneralSistema("El movimiento ya tiene generado los items en cuenta corriente");
        }

        return m;
    }

    public MovimientoPrestamo generarItemsMovimientoMantenimiento(MovimientoPrestamo m) throws ExcepcionGeneralSistema, Exception {

        if (m == null) {
            throw new ExcepcionGeneralSistema("El movimiento es nulo, nada para generar");
        }

        generarItemsMovimiento(m);
        asignarPrestamo(m, m.getPrestamo());

        m = (MovimientoPrestamo) movimientoPrestamoDAO.editar(m);

        return m;
    }

    private void limpiarConceptoEnCero(MovimientoPrestamo m) {

        List<ItemCapitalPrestamo> itemCapitalAux = m.getItemCapital();
        List<ItemInteresPrestamo> itemInteresAux = m.getItemIntereses();
        List<ItemInteresMoraPrestamo> itemItemInteresesMoraAux = m.getItemInteresesMora();
        List<ItemDescuentoPrestamo> itemDescuentoAux = m.getItemDescuento();
        List<ItemGastoOtorgamientoPrestamo> itemGastoOtorgamientoAux = m.getItemGastoOtorgamiento();
        List<ItemMicroseguroPrestamo> itemMicroseguroAux = m.getItemMicroseguro();
        List<ItemTotalPrestamo> itemTotalAux = m.getItemTotal();

        m.setItemCapital(new ArrayList<>());
        m.setItemIntereses(new ArrayList<>());
        m.setItemInteresesMora(new ArrayList<>());
        m.setItemDescuento(new ArrayList<>());
        m.setItemGastoOtorgamiento(new ArrayList<>());
        m.setItemMicroseguro(new ArrayList<>());
        m.setItemTotal(new ArrayList<>());

        for (ItemCapitalPrestamo im : itemCapitalAux) {
            if (im.getImporte() > 0) {
                m.getItemCapital().add(im);
            }
        }

        for (ItemInteresPrestamo im : itemInteresAux) {
            if (im.getImporte() > 0) {
                m.getItemIntereses().add(im);
            }
        }

        for (ItemInteresMoraPrestamo im : itemItemInteresesMoraAux) {
            if (im.getImporte() > 0) {
                m.getItemInteresesMora().add(im);
            }
        }

        for (ItemDescuentoPrestamo im : itemDescuentoAux) {
            if (im.getImporte() > 0) {
                m.getItemDescuento().add(im);
            }
        }

        for (ItemGastoOtorgamientoPrestamo im : itemGastoOtorgamientoAux) {
            if (im.getImporte() > 0) {
                m.getItemGastoOtorgamiento().add(im);
            }
        }

        for (ItemMicroseguroPrestamo im : itemMicroseguroAux) {
            if (im.getImporte() > 0) {
                m.getItemMicroseguro().add(im);
            }
        }

        for (ItemTotalPrestamo im : itemTotalAux) {
            if (im.getImporte() > 0) {
                m.getItemTotal().add(im);
            }
        }
    }

    public void cambiarEstadoPrestamo(MovimientoPrestamo m) throws Exception {
//
//        if (m.getComprobante().getComprobanteLiquidacion().equals("S")) {
//
//            if (m.getComprobante().getEsComprobanteReversion().equals("S")) {
//                m.getPrestamo().setEstado(estadoPrestamoRN.getEstadoPrestamo("B"));
//            } else {
//                m.getPrestamo().setEstado(estadoPrestamoRN.getEstadoPrestamo("C"));
//            }
//        }

        m.getPrestamo().setEstado(estadoPrestamoRN.getEstadoPrestamo(m.getComprobante().getEstadoPrestamo()));

        prestamoRN.guardar(m.getPrestamo());

    }

    private void generarItemsMovimiento(MovimientoPrestamo m) throws ExcepcionGeneralSistema {

        if (m.getComprobante().getConceptos() == null || m.getComprobante().getConceptos().isEmpty()) {
            throw new ExcepcionGeneralSistema("El comprobante de préstamo no tienen conceptos configurados");
        }

        if (m.getItemCapital() != null) {
            m.getItemCapital().clear();
        }

        if (m.getItemIntereses() != null) {
            m.getItemIntereses().clear();
        }

        if (m.getItemInteresesMora() != null) {
            m.getItemInteresesMora().clear();
        }

        if (m.getItemDescuento() != null) {
            m.getItemDescuento().clear();
        }

        if (m.getItemGastoOtorgamiento() != null) {
            m.getItemGastoOtorgamiento().clear();
        }

        if (m.getItemMicroseguro() != null) {
            m.getItemMicroseguro().clear();
        }

        if (m.getItemTotal() != null) {
            m.getItemTotal().clear();
        }

        for (ConceptoComprobante cc : m.getComprobante().getConceptos()) {

            if (cc.getConcepto().getTipo().equals("A")) {

                ItemCapitalPrestamo ia = new ItemCapitalPrestamo();
                ia.setNroItem(m.getItemCapital().size() + 1);
                ia.setDebeHaber(cc.getDebeHaber());
                ia.setMovimiento(m);
                ia.setConcepto(cc.getConcepto());
                ia.setCotizacion(m.getCotizacion());

                if (cc.getCuentaContable() != null) {

                    ia.setCuentaContable(cc.getCuentaContable());

                } else if (ia.getConcepto().getCuentaContable() != null) {

                    ia.setCuentaContable(ia.getConcepto().getCuentaContable());
                }

                m.getItemCapital().add(ia);
            }

            if (cc.getConcepto().getTipo().equals("I")) {

                ItemInteresPrestamo ii = new ItemInteresPrestamo();
                ii.setNroItem(m.getItemIntereses().size() + 1);
                ii.setDebeHaber(cc.getDebeHaber());
                ii.setMovimiento(m);
                ii.setConcepto(cc.getConcepto());
                ii.setCotizacion(m.getCotizacion());

                if (cc.getCuentaContable() != null) {

                    ii.setCuentaContable(cc.getCuentaContable());

                } else if (ii.getConcepto().getCuentaContable() != null) {

                    ii.setCuentaContable(ii.getConcepto().getCuentaContable());
                }

                m.getItemIntereses().add(ii);
            }

            if (cc.getConcepto().getTipo().equals("M")) {

                ItemInteresMoraPrestamo ii = new ItemInteresMoraPrestamo();
                ii.setNroItem(m.getItemInteresesMora().size() + 1);
                ii.setDebeHaber(cc.getDebeHaber());
                ii.setMovimiento(m);
                ii.setConcepto(cc.getConcepto());
                ii.setCotizacion(m.getCotizacion());

                if (cc.getCuentaContable() != null) {

                    ii.setCuentaContable(cc.getCuentaContable());

                } else if (ii.getConcepto().getCuentaContable() != null) {

                    ii.setCuentaContable(ii.getConcepto().getCuentaContable());
                }

                m.getItemInteresesMora().add(ii);

            }

            if (cc.getConcepto().getTipo().equals("D")) {

                ItemDescuentoPrestamo id = new ItemDescuentoPrestamo();
                id.setNroItem(m.getItemDescuento().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setMovimiento(m);
                id.setConcepto(cc.getConcepto());
                id.setCotizacion(m.getCotizacion());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (id.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(id.getConcepto().getCuentaContable());
                }

                m.getItemDescuento().add(id);

            }

            if (cc.getConcepto().getTipo().equals("G")) {

                ItemGastoOtorgamientoPrestamo id = new ItemGastoOtorgamientoPrestamo();
                id.setNroItem(m.getItemGastoOtorgamiento().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setMovimiento(m);
                id.setConcepto(cc.getConcepto());
                id.setCotizacion(m.getCotizacion());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (id.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(id.getConcepto().getCuentaContable());
                }

                m.getItemGastoOtorgamiento().add(id);

            }

            if (cc.getConcepto().getTipo().equals("H")) {

                ItemMicroseguroPrestamo id = new ItemMicroseguroPrestamo();
                id.setNroItem(m.getItemMicroseguro().size() + 1);
                id.setDebeHaber(cc.getDebeHaber());
                id.setMovimiento(m);
                id.setConcepto(cc.getConcepto());
                id.setCotizacion(m.getCotizacion());

                if (cc.getCuentaContable() != null) {

                    id.setCuentaContable(cc.getCuentaContable());

                } else if (id.getConcepto().getCuentaContable() != null) {

                    id.setCuentaContable(id.getConcepto().getCuentaContable());
                }

                m.getItemMicroseguro().add(id);

            }

            if (cc.getConcepto().getTipo().equals("T")) {

                ItemTotalPrestamo it = new ItemTotalPrestamo();
                it.setNroItem(m.getItemTotal().size() + 1);
                it.setDebeHaber(cc.getDebeHaber());
                it.setMovimiento(m);
                it.setConcepto(cc.getConcepto());
                it.setCotizacion(m.getCotizacion());

                if (cc.getCuentaContable() != null) {

                    it.setCuentaContable(cc.getCuentaContable());

                } else if (it.getConcepto().getCuentaContable() != null) {

                    it.setCuentaContable(it.getConcepto().getCuentaContable());
                }
                m.getItemTotal().add(it);
            }
        }
    }

    public ItemTotalPrestamo nuevoItemTotal(MovimientoPrestamo mv) {

        ItemTotalPrestamo itv = new ItemTotalPrestamo();

        itv.setNroItem(mv.getItemTotal().size() + 1);
        itv.setMovimiento(mv);
        itv.setConcepto(conceptoDAO.getConcepto("PR", "T", "DPT001"));
        itv.setCuentaContable(itv.getConcepto().getCuentaContable());
        itv.setDebeHaber("D");
        itv.setCotizacion(mv.getCotizacion());
        mv.getItemTotal().add(itv);

        return itv;
    }

    public MovimientoPrestamo generarComprobante(MovimientoTesoreria mt) throws ExcepcionGeneralSistema, Exception {

        MovimientoPrestamo m = nuevoMovimiento(mt.getComprobantePrestamo(), mt.getPuntoVenta());

        m.setEmpresa(mt.getEmpresa());
        m.setSucursal(mt.getSucursal());
        m.setPuntoVenta(mt.getPuntoVenta());
        m.setUnidadNegocio(mt.getUnidadNegocio());

        m.setNumeroFormulario(mt.getNumeroFormulario());

        m.setObservaciones(mt.getObservaciones());
        m.setFechaMovimiento(mt.getFechaMovimiento());
        m.setFechaVencimiento(mt.getFechaMovimiento());

        m.setPrestamo(mt.getPrestamo());
        m.setDestinatario(mt.getEntidad());
        m.setNombre(mt.getEntidad().getRazonSocial());
        m.setTipoDocumento(mt.getEntidad().getTipoDocumento());
        m.setNrodoc(mt.getEntidad().getNrodoc());
        m.setCondicionDeIva(mt.getEntidad().getCondicionDeIva());
        m.setProvincia(mt.getEntidad().getProvincia());
        m.setLocalidad(mt.getEntidad().getLocalidad());
        m.setDireccion(mt.getEntidad().getDireccion());

        m.setCobrador(mt.getCobrador());
        m.setMonedaSecundaria(mt.getMonedaSecundaria());
        m.setMonedaRegistracion(mt.getMonedaRegistracion());
        m.setCotizacion(mt.getCotizacion().doubleValue());

        asignarImportesRecibo(m, mt);

        controlComprobante(m);
        limpiarConceptoEnCero(m);

        cuentaCorrienteRN.generarItemCuentaCorrienteRecibo(m, mt.getDebitosPrestamo());

        return m;
    }

    public MovimientoPrestamo getMovimiento(Integer id) {

        return movimientoPrestamoDAO.getObjeto(MovimientoPrestamo.class, id);
    }

    public MovimientoPrestamo getMovimiento(String codFormulario, Integer numeroFormulario) {
        return movimientoPrestamoDAO.getMovimiento(codFormulario, numeroFormulario);
    }

    public List<MovimientoPrestamo> getListaByBusqueda(Map<String, String> filtro, int cantMax) {

        return movimientoPrestamoDAO.getListaByBusqueda(filtro, null, cantMax);
    }

    public List<MovimientoPrestamo> getListaByBusqueda(Map<String, String> filtro, List<String> orden, int cantMax) {

        return movimientoPrestamoDAO.getListaByBusqueda(filtro, null, cantMax);
    }

    public MovimientoPrestamo revertirMovimiento(MovimientoPrestamo mRevertir) throws Exception {

        MovimientoPrestamo mr = generarMovimientoReversion(mRevertir);
        mr = guardar(mr);
        mRevertir.setMovimientoReversion(mr);
        guardar(mRevertir);

        return mr;
    }

    public MovimientoPrestamo generarMovimientoReversion(MovimientoPrestamo m) throws ExcepcionGeneralSistema {

        if (m == null) {
            throw new ExcepcionGeneralSistema("Movimiento nulo");
        }

        if (m.getComprobante().getComprobanteReversion() == null) {
            throw new ExcepcionGeneralSistema("El comprobante de préstamo " + m.getComprobante().getDescripcion() + " no tiene comprobante de reversión asociado");
        }

        if (m.getMovimientoContabilidad() != null) {
            throw new ExcepcionGeneralSistema("El comprobante fue pasado a la contabilidad, no es posible revertirlo");
        }

        Formulario formulario = formularioRN.getFormulario(m.getComprobante().getComprobanteReversion(), m.getPuntoVenta(), "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de tesorería para el comprobante (" + m.getComprobante().getComprobanteReversion().getCodigo() + ") "
                    + "para El punto de venta (" + m.getPuntoVenta().getCodigo() + ") "
                    + "con la condición de iva (X)");
        }

        MovimientoPrestamo mr = new MovimientoPrestamo();

        mr.setComprobante(m.getComprobante().getComprobanteReversion());
        mr.setFormulario(formulario);

        mr.setEmpresa(m.getEmpresa());
        mr.setSucursal(m.getSucursal());
        mr.setPuntoVenta(m.getPuntoVenta());
        mr.setUnidadNegocio(m.getUnidadNegocio());

        mr.setImputacionCuentaCorriente(m.getImputacionCuentaCorriente());
        mr.setFechaMovimiento(m.getFechaMovimiento());
        mr.setFechaVencimiento(m.getFechaVencimiento());

        mr.setObservaciones("");
        mr.setMonedaSecundaria(m.getMonedaSecundaria());
        mr.setMonedaRegistracion(m.getMonedaRegistracion());
        mr.setCotizacion(m.getCotizacion());

        mr.setPrestamo(m.getPrestamo());
        mr.setDestinatario(m.getDestinatario());
        mr.setNombre(m.getNombre());
        mr.setNrodoc(m.getNrodoc());
        mr.setTipoDocumento(m.getTipoDocumento());
        mr.setProvincia(m.getProvincia());
        mr.setLocalidad(m.getLocalidad());
        mr.setBarrio(m.getBarrio());
        mr.setDireccion(m.getDireccion());

        mr.setCondicionDeIva(m.getCondicionDeIva());

        mr.setMonedaRegistracion(m.getMonedaSecundaria());
        mr.setMonedaSecundaria(m.getMonedaSecundaria());

        mr.setCobrador(m.getCobrador());

        mr.setMovimientoReversion(m);
        m.setMovimientoReversion(mr);

        if (m.getFormulario().getTipoNumeracion().equals("I")) {
            m.setNumeroFormulario(m.getNumeroFormulario() * -1);
        }

        for (ItemCapitalPrestamo i : m.getItemCapital()) {

            ItemCapitalPrestamo ir = new ItemCapitalPrestamo();

            ir.setMovimiento(mr);
            ir.setNroItem(mr.getItemCapital().size() + 1);
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

            mr.getItemCapital().add(ir);
        }

        for (ItemInteresPrestamo i : m.getItemIntereses()) {

            ItemInteresPrestamo ir = new ItemInteresPrestamo();

            ir.setMovimiento(mr);
            ir.setNroItem(mr.getItemIntereses().size() + 1);
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

            mr.getItemIntereses().add(ir);
        }

        for (ItemInteresMoraPrestamo i : m.getItemInteresesMora()) {

            ItemInteresMoraPrestamo ir = new ItemInteresMoraPrestamo();

            ir.setMovimiento(mr);
            ir.setNroItem(mr.getItemInteresesMora().size() + 1);
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

            mr.getItemInteresesMora().add(ir);
        }

        for (ItemDescuentoPrestamo i : m.getItemDescuento()) {

            ItemDescuentoPrestamo ir = new ItemDescuentoPrestamo();

            ir.setMovimiento(mr);
            ir.setNroItem(mr.getItemDescuento().size() + 1);
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

            mr.getItemDescuento().add(ir);
        }

        for (ItemGastoOtorgamientoPrestamo i : m.getItemGastoOtorgamiento()) {

            ItemGastoOtorgamientoPrestamo ir = new ItemGastoOtorgamientoPrestamo();

            ir.setMovimiento(mr);
            ir.setNroItem(mr.getItemGastoOtorgamiento().size() + 1);
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

            mr.getItemGastoOtorgamiento().add(ir);
        }

        for (ItemMicroseguroPrestamo i : m.getItemMicroseguro()) {

            ItemMicroseguroPrestamo ir = new ItemMicroseguroPrestamo();

            ir.setMovimiento(mr);
            ir.setNroItem(mr.getItemMicroseguro().size() + 1);
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

            mr.getItemMicroseguro().add(ir);
        }

        for (ItemTotalPrestamo i : m.getItemTotal()) {

            ItemTotalPrestamo ir = new ItemTotalPrestamo();

            ir.setMovimiento(mr);
            ir.setNroItem(mr.getItemTotal().size() + 1);
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

        return mr;
    }

    public void asignarPrestamo(MovimientoPrestamo m, Prestamo p) throws Exception {

        m.setPrestamo(p);
        m.setDestinatario(p.getDestinatario());
        asignarDestinatario(m);

        //Comprobantes de cancelación por reprogramacion judicial
        boolean calculaMora = !(m.getComprobante().getTipoComprobante().equals("CJ"));

        asignarImportes(m, calculaMora, true);

        if (m.getComprobante().getTipoComprobante().equals("CR")) {
            prestamoRN.reprogramar(m);
        }

        if (m.getComprobante().getTipoComprobante().equals("CJ")) {
            prestamoRN.reprogramarJudicial(m);
        }
    }

    public void asignarDestinatario(MovimientoPrestamo m) throws Exception {

        //movimiento.setDestinatario(destinatario);
        m.setNombre(m.getDestinatario().getRazonSocial());
        m.setCondicionDeIva(m.getDestinatario().getCondicionDeIva());
        m.setTipoDocumento(m.getDestinatario().getTipoDocumento());
        m.setNrodoc(m.getDestinatario().getNrodoc());
        m.setDireccion(m.getDestinatario().getDireccion());
        m.setBarrio(m.getDestinatario().getBarrio());
        m.setLocalidad(m.getDestinatario().getLocalidad());
        m.setProvincia(m.getDestinatario().getProvincia());
    }

    private void asignarImportesRecibo(MovimientoPrestamo mp, MovimientoTesoreria mt) {

        if (mp == null || mt == null || mt.getDebitosPrestamo() == null || mt.getDebitosPrestamo().isEmpty()) {
            return;
        }

        double totalCapital = 0;
        double totalIntereses = 0;
        double totalInteresMora = 0;
        double totalDescuento = 0;
        double totalGastos = 0;
        double totalMicroseguro = 0;
        double total = 0;

        for (ItemPendienteCuentaCorrientePrestamo ip : mt.getDebitosPrestamo()) {

            if (ip.isSeleccionado()) {

                totalCapital = totalCapital + ip.getCapital();
                totalIntereses = totalIntereses + ip.getInteres();
                totalInteresMora = totalInteresMora + ip.getInteresMora();
                totalDescuento = totalDescuento + ip.getDescuentoInteres();
                totalGastos = totalGastos + ip.getGastosOtorgamiento();
                totalMicroseguro = totalMicroseguro + ip.getImporteMicroseguros();
                total = total + ip.getImporteAplicar();
            }
        }

        calcularImportes(mp, totalCapital, totalIntereses, totalInteresMora, totalDescuento, totalGastos, totalMicroseguro, total);

    }

    public void asignarImportes(MovimientoPrestamo m, boolean calculaMora, boolean calculaDescuento) {

        if (m.getPrestamo() == null) {
            return;
        }

        //Por defecto trabajamos con el pendiente del prestamo
        m.setDebitosPrestamo(cuentaCorrienteRN.getDebitosPendientes(m.getPrestamo(),
                m.getMonedaRegistracion().getCodigo(),
                m.getFechaMovimiento(),
                calculaMora,
                calculaDescuento));

        double totalCapital = 0;
        double totalIntereses = 0;
        double totalInteresMora = 0;
        double totalDescuento = 0;
        double totalGastos = 0;
        double totalMicroseguro = 0;
        double total = 0;

        if (m.getDebitosPrestamo() == null || m.getDebitosPrestamo().isEmpty()) {

            prestamoRN.calcularTotales(m.getPrestamo());
            totalCapital = m.getPrestamo().getImporteCapital();
            totalIntereses = m.getPrestamo().getImporteInteres();
            totalInteresMora = m.getPrestamo().getImporteMora();
            totalDescuento = m.getPrestamo().getImporteDescuento();
            totalGastos = m.getPrestamo().getGastosOtorgamiento();
            totalMicroseguro = m.getPrestamo().getImporteMicroseguros();

        } else {

            for (ItemPendienteCuentaCorrientePrestamo item : m.getDebitosPrestamo()) {

                totalCapital = totalCapital + item.getCapital();
                totalIntereses = totalIntereses + item.getInteres();
                totalInteresMora = totalInteresMora + item.getInteresMora();
                totalDescuento = totalDescuento + item.getDescuentoInteres();
                totalGastos = totalGastos + item.getGastosOtorgamiento();
                totalMicroseguro = totalMicroseguro + item.getImporteMicroseguros();
            }
        }

        total = totalCapital + totalIntereses + totalInteresMora + totalGastos + totalMicroseguro - totalDescuento;

        calcularImportes(m,
                totalCapital,
                totalIntereses,
                totalInteresMora,
                totalDescuento,
                totalGastos,
                totalMicroseguro,
                total);

    }

    public void calcularImportes(MovimientoPrestamo m,
            double totalCapital,
            double totalIntereses,
            double totalInteresMora,
            double totalDescuento,
            double totalGastos,
            double totalMicroseguro,
            double total) {

//        System.err.println("totalCapital" + totalCapital);
//        System.err.println("totalIntereses" + totalIntereses);
//        System.err.println("totalInteresMora" + totalInteresMora);
//        System.err.println("totalDescuento" + totalDescuento);
//        System.err.println("totalGastos" + totalGastos);
//        System.err.println("total" + total);
        if (m.getItemCapital() != null) {

            for (ItemCapitalPrestamo i : m.getItemCapital()) {

                i.setImporte(totalCapital);
                i.setImporteSecundario(Numeros.redondear(totalCapital / m.getCotizacion()));

                if (i.getDebeHaber().equals("D")) {
                    i.setImporteDebe(totalCapital);
                    i.setImporteDebeSecundario(i.getImporteSecundario());
                }

                if (i.getDebeHaber().equals("H")) {
                    i.setImporteHaber(totalCapital);
                    i.setImporteHaberSecundario(i.getImporteSecundario());
                }
            }
        }

        if (m.getItemIntereses() != null) {

            for (ItemInteresPrestamo i : m.getItemIntereses()) {

                i.setImporte(totalIntereses);
                i.setImporteSecundario(Numeros.redondear(totalIntereses / m.getCotizacion()));

                if (i.getDebeHaber().equals("D")) {
                    i.setImporteDebe(totalIntereses);
                    i.setImporteDebeSecundario(i.getImporteSecundario());
                }

                if (i.getDebeHaber().equals("H")) {
                    i.setImporteHaber(totalIntereses);
                    i.setImporteHaberSecundario(i.getImporteSecundario());
                }
            }
        }

        if (m.getItemInteresesMora() != null) {

            for (ItemInteresMoraPrestamo i : m.getItemInteresesMora()) {

                i.setImporte(totalInteresMora);
                i.setImporteSecundario(Numeros.redondear(totalInteresMora / m.getCotizacion()));

                if (i.getDebeHaber().equals("D")) {
                    i.setImporteDebe(totalInteresMora);
                    i.setImporteDebeSecundario(i.getImporteSecundario());
                }

                if (i.getDebeHaber().equals("H")) {
                    i.setImporteHaber(totalInteresMora);
                    i.setImporteHaberSecundario(i.getImporteSecundario());
                }
            }
        }

        if (m.getItemDescuento() != null) {

            for (ItemDescuentoPrestamo i : m.getItemDescuento()) {

                i.setImporte(totalDescuento);
                i.setImporteSecundario(Numeros.redondear(totalDescuento / m.getCotizacion()));

                if (i.getDebeHaber().equals("D")) {
                    i.setImporteDebe(totalDescuento);
                    i.setImporteDebeSecundario(i.getImporteSecundario());
                }

                if (i.getDebeHaber().equals("H")) {
                    i.setImporteHaber(totalDescuento);
                    i.setImporteHaberSecundario(i.getImporteSecundario());
                }
            }
        }

        if (m.getItemGastoOtorgamiento() != null) {

            for (ItemGastoOtorgamientoPrestamo i : m.getItemGastoOtorgamiento()) {

                i.setImporte(totalGastos);
                i.setImporteSecundario(Numeros.redondear(totalGastos / m.getCotizacion()));

                if (i.getDebeHaber().equals("D")) {
                    i.setImporteDebe(totalGastos);
                    i.setImporteDebeSecundario(i.getImporteSecundario());
                }

                if (i.getDebeHaber().equals("H")) {
                    i.setImporteHaber(totalGastos);
                    i.setImporteHaberSecundario(i.getImporteSecundario());
                }
            }
        }

        if (m.getItemMicroseguro() != null) {

            for (ItemMicroseguroPrestamo i : m.getItemMicroseguro()) {

                i.setImporte(totalMicroseguro);
                i.setImporteSecundario(Numeros.redondear(totalMicroseguro / m.getCotizacion()));

                if (i.getDebeHaber().equals("D")) {
                    i.setImporteDebe(totalMicroseguro);
                    i.setImporteDebeSecundario(i.getImporteSecundario());
                }

                if (i.getDebeHaber().equals("H")) {
                    i.setImporteHaber(totalMicroseguro);
                    i.setImporteHaberSecundario(i.getImporteSecundario());
                }
            }
        }

        if (m.getItemTotal() != null) {

            for (ItemTotalPrestamo i : m.getItemTotal()) {

                i.setImporte(total);
                i.setImporteSecundario(Numeros.redondear(total / m.getCotizacion()));

                if (i.getDebeHaber().equals("D")) {
                    i.setImporteDebe(total);
                    i.setImporteDebeSecundario(i.getImporteSecundario());
                }

                if (i.getDebeHaber().equals("H")) {
                    i.setImporteHaber(total);
                    i.setImporteHaberSecundario(i.getImporteSecundario());
                }
            }
        }
//
//        System.err.println("totalCapital " + totalCapital);
//        System.err.println("totalIntereses " + totalIntereses);
//        System.err.println("totalInteresMora " + totalInteresMora);
//        System.err.println("totalDescuento " + totalDescuento);
//        System.err.println("total " + total);
        m.setImporteCapital(totalCapital);
        m.setImporteInteres(totalIntereses);
        m.setImporteInteresMora(totalInteresMora);
        m.setImporteDescuento(totalDescuento);
        m.setImporteGastos(totalGastos);
        m.setImporteMicroseguro(totalMicroseguro);
        m.setImporteTotal(total);
    }

    private void generarComprobanteReprogramacion(MovimientoPrestamo m) throws Exception {

        if (m == null
                || m.getComprobante() == null
                || m.getComprobante().getComprobanteReprogramacion() == null) {
            throw new ExcepcionGeneralSistema("No es posible avanzar con la reprogramación ");
        }

        MovimientoPrestamo mrep = nuevoMovimiento(m.getComprobante().getComprobanteReprogramacion().getCodigo(), m.getPuntoVenta().getCodigo(), "", "");
        asignarPrestamo(mrep, m.getPrestamoReprogramado());
        cuentaCorrienteRN.generarItemCuentaCorriente(mrep);

        m.setMovimientoReprogramacion(mrep);
    }

    private void ajustarImportesCancelarPorReprogramacion(MovimientoPrestamo m) {

//        System.err.println("ajustarImportesCancelarPorReprogramacion");
        if (m.getDebitosPrestamo() == null) {
            return;
        }

        for (ItemPendienteCuentaCorrientePrestamo cuota : m.getDebitosPrestamo()) {

//            System.err.println("cuota " + cuota);
            cuota.setSeleccionado(true);

            long startTime = m.getFechaMovimiento().getTime();
            long endTime = cuota.getFechaVencimiento().getTime();
            long diffTime = endTime - startTime;
            long diffDays = diffTime / (1000 * 60 * 60 * 24);

            int diasAdelanto = (int) diffDays;

            if (m.getComprobante().getTipoComprobante().equals("CR") && diasAdelanto < 30) {
                cuota.setInteres(0);
            }

            cuota.setDescuentoInteres(0);
            cuota.setPendiente(cuota.getCapital() + cuota.getInteres() + cuota.getInteresMora() - cuota.getDescuentoInteres());
            cuentaCorrienteRN.marcarDebito(cuota, m.getCotizacion());
        }
    }

    private void tomarNumeroFormulario(MovimientoPrestamo m) throws ExcepcionGeneralSistema, Exception {

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

        if (m.getFechaMovimiento() != null) {
            m.getFormulario().setUltimaFecha(m.getFechaMovimiento());
        }

        formularioRN.guardar(m.getFormulario());
    }

}
