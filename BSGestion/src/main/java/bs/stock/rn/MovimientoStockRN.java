/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.rn;

import bs.administracion.rn.ModuloRN;
import bs.administracion.rn.ParametrosRN;
import bs.compra.modelo.ItemDetalleItemMovimientoCompra;
import bs.compra.modelo.ItemMovimientoCompra;
import bs.compra.modelo.MovimientoCompra;
import bs.facturacion.modelo.ItemMovimientoFacturacion;
import bs.facturacion.modelo.ItemMovimientoFacturacionDetalle;
import bs.facturacion.modelo.ItemMovimientoFacturacionKit;
import bs.facturacion.modelo.MovimientoFacturacion;
import bs.global.dao.ComprobanteDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.Moneda;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.FormularioRN;
import bs.global.rn.MonedaRN;
import bs.global.rn.PuntoVentaRN;
import bs.produccion.modelo.ItemDetalleMovimientoProduccion;
import bs.produccion.modelo.ItemMovimientoProduccion;
import bs.produccion.modelo.MovimientoProduccion;
import bs.proveedores.rn.ItemListaPrecioCostoRN;
import bs.stock.dao.MovimientoStockDAO;
import bs.stock.modelo.ItemDetalleModelo;
import bs.stock.modelo.ItemMascaraStock;
import bs.stock.modelo.ItemMovimientoStock;
import bs.stock.modelo.ItemProductoStock;
import bs.stock.modelo.ItemTransferenciaStock;
import bs.stock.modelo.MascaraStock;
import bs.stock.modelo.MovimientoStock;
import bs.stock.modelo.ParametroStock;
import bs.stock.modelo.Producto;
import bs.stock.modelo.Stock;
import bs.stock.modelo.TipoItemMovimiento;
import bs.taller.modelo.ItemProductoTaller;
import bs.taller.modelo.MovimientoTaller;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class MovimientoStockRN implements Serializable {

    @EJB
    private MonedaRN monedaRN;
    @EJB
    private MovimientoStockDAO movimientoStockDAO;
    @EJB
    private ComprobanteDAO comprobanteDAO;
    @EJB
    private StockRN stockRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private PuntoVentaRN puntoVentaRN;
    @EJB
    protected ParametroStockRN parametroStockRN;
    @EJB
    protected ParametrosRN parametrosRN;
    @EJB
    private ModuloRN moduloRN;
    @EJB
    private ItemListaPrecioCostoRN itemListaPrecioCostoRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public MovimientoStock guardar(MovimientoStock movimiento) throws Exception {

        borrarItemsNoValidos(movimiento);
        generarItemTransferencia(movimiento);
        asignarDepositoItems(movimiento);
        asignarCantidadStock(movimiento);

        //Validamos que se pueda guardar el comprobante
        control(movimiento, false);

        if (movimiento.getId() == null) {

            generarStock(movimiento);

            if (movimiento.getFormulario().getTipoNumeracion().equals("A")) {
                movimiento.setNumeroFormulario(formularioRN.tomarProximoNumero(movimiento.getFormulario()));
            }

            movimientoStockDAO.crear(movimiento);
        } else {
            movimiento = (MovimientoStock) movimientoStockDAO.editar(movimiento);
            stockRN.recalcularStock();
        }

        actualizarPrecioReposicion(movimiento);

        movimiento.setPersistido(true);
        return movimiento;
    }

    public void actualizarPrecioReposicion(MovimientoStock movimiento) {

        if (movimiento.getItemsProducto() != null) {
            for (ItemProductoStock ip : movimiento.getItemsProducto()) {
                if (ip.getPrecio() != null && ip.getPrecio().compareTo(BigDecimal.ZERO) > 0) {
                    ip.getProducto().setPrecioReposicion(ip.getPrecio());
                }
            }
        }
    }

    /*
     * Se utiliza para generar movimientos desde el modulo de stock
     * este metodo incremenda el nro de formulario
     */
    public MovimientoStock nuevoMovimiento(String CODST, String SUCST) throws ExcepcionGeneralSistema {

        Comprobante comprobante = comprobanteDAO.getComprobante("ST", CODST);
        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(SUCST);

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No se encontró comprobante de stock " + "-" + CODST);
        }
        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No se encontró punto de venta " + SUCST);
        }

        //Buscamos el formulario correspondiente
        Formulario formulario = formularioRN.getFormulario(comprobante, puntoVenta, "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de stock para el comprobante (" + CODST + ") "
                    + "para El punto de venta (" + SUCST + ") "
                    + "con la condición de iva (X) ");
        }

        MovimientoStock m = crearMovimiento(comprobante, formulario, puntoVenta);

        return m;
    }

    /**
     * Se utiliza para generar movimientos de stock automáticos desde otros
     * módulos
     *
     * @param comprobante
     * @param formulario
     * @param puntoVenta
     * @return
     * @throws ExcepcionGeneralSistema
     */
    public MovimientoStock nuevoMovimientoAutomatico(Comprobante comprobante, Formulario formulario, PuntoVenta puntoVenta) throws ExcepcionGeneralSistema {

        return crearMovimiento(comprobante, formulario, puntoVenta);
    }

    /**
     * Se utiliza para generar movimientos de stock automáticos
     *
     * @param comprobante objeto comprobante a generar
     * @param formulario obejeto formulario a generar
     * @return movimiento de stock
     * @throws ExcepcionGeneralSistema
     */
    private MovimientoStock crearMovimiento(Comprobante comprobante, Formulario formulario, PuntoVenta puntoVenta) throws ExcepcionGeneralSistema {

        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("El punto de venta no puede ser nulo");
        }

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("El comprobante de stock no puede ser nulo");
        }

        if (comprobante.getTipoMovimiento() == null) {
            throw new ExcepcionGeneralSistema("El comprobante no tiene definido el tipo de movimiento de inventario");
        }

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("El formulario de stock no puede ser nulo");
        }

        MovimientoStock m = new MovimientoStock();
        Moneda moneda = monedaRN.getMoneda(parametrosRN.getParametro().getCodigoMonedaSecundaria());
        BigDecimal cotizacion = monedaRN.getCotizacionDia(parametrosRN.getParametro().getCodigoMonedaSecundaria());

        m.setEmpresa(puntoVenta.getEmpresa());
        m.setSucursal(puntoVenta.getSucursal());
        m.setPuntoVenta(puntoVenta);
        m.setUnidadNegocio(puntoVenta.getUnidadNegocio());

        m.setComprobante(comprobante);
        m.setFormulario(formulario);
        m.setNumeroFormulario(formulario.getUltimoNumero() + 1);
        m.setFechaMovimiento(new Date());

        m.setTipoMovimiento(comprobante.getTipoMovimiento());
        m.setMonedaSecundaria(moneda);
        m.setMonedaRegistracion(comprobante.getMonedaRegistracion());
        m.setCotizacion(cotizacion);

        if (comprobante.getDeposito() != null) {
            m.setDeposito(comprobante.getDeposito());
        }

        if (comprobante.getDepositoTransferencia() != null) {
            m.setDepositoTransferencia(comprobante.getDepositoTransferencia());
        }

        //m.getItemsProducto().add(nuevoItemProducto(m));
        return m;
    }

    public void puedoAgregarItem(MovimientoStock movimiento) throws ExcepcionGeneralSistema {

        if (movimiento.getMonedaSecundaria() == null) {
            throw new ExcepcionGeneralSistema("La moneda secundaria no puede estar vacía");
        }

        if (movimiento.getCotizacion() == null || movimiento.getCotizacion().compareTo(BigDecimal.ONE) < 0) {
            throw new ExcepcionGeneralSistema("La cotización del comprobante no puede se nula o menor a 1");
        }

        if (movimiento.getTipoMovimiento().equals("T") && movimiento.getDeposito() == null && movimiento.getDepositoTransferencia() == null) {
            throw new ExcepcionGeneralSistema("Debe seleccionar el depósito de ingreso y egreso");
        }

        if (!movimiento.getTipoMovimiento().equals("T") && movimiento.getDeposito() == null) {
            throw new ExcepcionGeneralSistema("Debe seleccionar el depósito");
        }
    }

    public ItemProductoStock nuevoItemProducto(MovimientoStock movimiento) throws ExcepcionGeneralSistema {

        puedoAgregarItem(movimiento);

        ItemProductoStock nItem = (ItemProductoStock) nuevoItemMovimiento(TipoItemMovimiento.P, movimiento);
        reorganizarNroItem(movimiento);

        return nItem;

    }

    public ItemTransferenciaStock nuevoItemTransferencia(MovimientoStock movimiento) throws ExcepcionGeneralSistema {

        puedoAgregarItem(movimiento);
        ItemTransferenciaStock nItem = (ItemTransferenciaStock) nuevoItemMovimiento(TipoItemMovimiento.T, movimiento);
        reorganizarNroItem(movimiento);

        return nItem;

    }

    private ItemMovimientoStock nuevoItemMovimiento(TipoItemMovimiento ti, MovimientoStock m) {

        ItemMovimientoStock nItem;

        if (ti.equals(TipoItemMovimiento.P)) {
            nItem = new ItemProductoStock();
            nItem.setNroitm(m.getItemsProducto().size() + 1);
        } else {
            nItem = new ItemTransferenciaStock();
            nItem.setNroitm(m.getItemTransferencia().size() + 1);
        }

        nItem.setPuntoVenta(m.getPuntoVenta().getCodigo());
        nItem.setFechaMovimiento(m.getFechaMovimiento());
        nItem.setMonedaSecundaria(m.getMonedaSecundaria());
        nItem.setCotizacion(m.getCotizacion());

        nItem.setAtributo1(m.getAtributo1() != null ? m.getAtributo1() : "");
        nItem.setAtributo2(m.getAtributo2() != null ? m.getAtributo2() : "");
        nItem.setAtributo3(m.getAtributo3() != null ? m.getAtributo3() : "");
        nItem.setAtributo4(m.getAtributo4() != null ? m.getAtributo4() : "");
        nItem.setAtributo5(m.getAtributo5() != null ? m.getAtributo5() : "");
        nItem.setAtributo6(m.getAtributo6() != null ? m.getAtributo6() : "");
        nItem.setAtributo7(m.getAtributo7() != null ? m.getAtributo7() : "");

        nItem.setMovimiento(m);

        return nItem;
    }

    /**
     * Validaciones previas a guardar el movimiento
     *
     * @param m Movimiento de stock
     * @param permiteVacio permite guardar comprobante vacío
     * @throws bs.global.excepciones.ExcepcionGeneralSistema
     */
    public void control(MovimientoStock m, boolean permiteVacio) throws Exception {

        String sErrores = "";

        sErrores += moduloRN.canSaveModulo("ST", m.getFechaMovimiento());

        if (m.getId() != null) {
            sErrores += "| No es posible modificar un comprobante de stock ";
        }

        if (!permiteVacio && m.getItemsProducto().isEmpty()) {
            sErrores += "| El detalle está vacío, no es posible guardar el comprobante de stock ";
        }

        //Verificamos que el deposito ingreso siempre esté cargado
        if (m.getDeposito() == null) {
            sErrores += "| El depósito no puede ser nulo ";
        }

        //Si es transferencia el deposito de egreso tiene que estar cargado
        if (m.getTipoMovimiento().equals("T")) {

            if (m.getDepositoTransferencia() == null) {
                sErrores += "| El depósito para transferencia no puede ser nulo ";
            }

            if (m.getDeposito().equals(m.getDepositoTransferencia())) {
                sErrores += "| El depósito de egreso y de ingreso no pueden ser iguales ";
            }
        }

        if (m.getFechaMovimiento() == null) {
            sErrores += "| La fecha no puede estar en blanco ";
        }

        if (m.getPuntoVenta() == null) {
            sErrores += "| El punto de venta no puede estar en blanco ";
        }

        sErrores += controlItemsProducto(m);

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }

    }

    public void puedoAgregarItem(MovimientoStock m, ItemMovimientoStock nItem) throws ExcepcionGeneralSistema {

        if (nItem == null) {
            throw new ExcepcionGeneralSistema("No se ha creado el item");
        }

        if (m.getDeposito() == null) {
            throw new ExcepcionGeneralSistema("Seleccione el depósito");
        }

        if (nItem.getProducto() == null) {
            throw new ExcepcionGeneralSistema("Seleccione un producto para agregar al comprobante");
        }

        if (m.getTipoMovimiento().equals("I")
                || m.getTipoMovimiento().equals("E")
                || m.getTipoMovimiento().equals("T")) {

            if (nItem.getCantidad() == null || nItem.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ExcepcionGeneralSistema("Ingrese un valor de cantidad mayor a 0 para " + nItem.getProducto().getDescripcion());
            }
        }

        //Si es ajuste solamente validamos que sea distinto de cero
        if (m.getTipoMovimiento().equals("A")) {

            if (nItem.getCantidad() == null || nItem.getCantidad().compareTo(BigDecimal.ZERO) == 0) {
                throw new ExcepcionGeneralSistema("Ingrese un valor de cantidad distinto a 0 para " + nItem.getProducto().getDescripcion());
            }
        }

        if (m.getTipoMovimiento().equals("T")) {

            if (m.getDepositoTransferencia() == null) {
                throw new ExcepcionGeneralSistema("Seleccione el depósito de egreso");
            }
        }

        //Control de atributos de stock
        if (nItem.getProducto() != null
                && nItem.getProducto().getAdministraAtributo1().equals("S")
                && nItem.getAtributo1().isEmpty()) {

            throw new ExcepcionGeneralSistema("Ingrese el atributo 1 para el producto " + nItem.getProducto().getDescripcion());
        }

        if (nItem.getProducto() != null
                && nItem.getProducto().getAdministraAtributo2().equals("S")
                && nItem.getAtributo2().isEmpty()) {

            throw new ExcepcionGeneralSistema("Ingrese el atributo 2 para el producto " + nItem.getProducto().getDescripcion());
        }

        if (nItem.getProducto() != null
                && nItem.getProducto().getAdministraAtributo3().equals("S")
                && nItem.getAtributo3().isEmpty()) {

            throw new ExcepcionGeneralSistema("Ingrese el atributo 3 para el producto " + nItem.getProducto().getDescripcion());
        }

        if (nItem.getProducto() != null
                && nItem.getProducto().getAdministraAtributo4().equals("S")
                && nItem.getAtributo4().isEmpty()) {

            throw new ExcepcionGeneralSistema("Ingrese el atributo 4 para el producto " + nItem.getProducto().getDescripcion());
        }

        if (nItem.getProducto() != null
                && nItem.getProducto().getAdministraAtributo5().equals("S")
                && nItem.getAtributo5().isEmpty()) {

            throw new ExcepcionGeneralSistema("Ingrese el atributo 5 para el producto " + nItem.getProducto().getDescripcion());
        }

        if (nItem.getProducto() != null
                && nItem.getProducto().getAdministraAtributo6().equals("S")
                && nItem.getAtributo6().isEmpty()) {

            throw new ExcepcionGeneralSistema("Ingrese el atributo 6 para el producto " + nItem.getProducto().getDescripcion());
        }

        if (nItem.getProducto() != null
                && nItem.getProducto().getAdministraAtributo7().equals("S")
                && nItem.getAtributo7().isEmpty()) {

            throw new ExcepcionGeneralSistema("Ingrese el atributo 7 para el producto " + nItem.getProducto().getDescripcion());
        }

        for (ItemProductoStock ip : m.getItemsProducto()) {

            if (ip.getProducto() != null
                    && ip.getProducto().equals(nItem.getProducto())
                    && ip.getAtributo1().equals(nItem.getAtributo1())
                    && ip.getAtributo2().equals(nItem.getAtributo2())
                    && ip.getAtributo3().equals(nItem.getAtributo3())
                    && ip.getAtributo4().equals(nItem.getAtributo4())
                    && ip.getAtributo5().equals(nItem.getAtributo5())
                    && ip.getAtributo6().equals(nItem.getAtributo6())
                    && ip.getAtributo7().equals(nItem.getAtributo7())
                    && ip.isTodoOk()) {

                String mensaje = "El producto "
                        + nItem.getProducto().getDescripcion()
                        + (nItem.getProducto().getAdministraAtributo1().equals("S") ? "| Atributo1 " + nItem.getAtributo1() : "")
                        + (nItem.getProducto().getAdministraAtributo2().equals("S") ? "| Atributo2 " + nItem.getAtributo2() : "")
                        + (nItem.getProducto().getAdministraAtributo3().equals("S") ? "| Atributo3 " + nItem.getAtributo3() : "")
                        + (nItem.getProducto().getAdministraAtributo4().equals("S") ? "| Atributo4 " + nItem.getAtributo4() : "")
                        + (nItem.getProducto().getAdministraAtributo5().equals("S") ? "| Atributo5 " + nItem.getAtributo5() : "")
                        + (nItem.getProducto().getAdministraAtributo6().equals("S") ? "| Atributo6 " + nItem.getAtributo6() : "")
                        + (nItem.getProducto().getAdministraAtributo7().equals("S") ? "| Atributo7 " + nItem.getAtributo7() : "")
                        + " ya existe en el comprobante";

                throw new ExcepcionGeneralSistema(mensaje);
            }

            if (!ip.getProducto().getGestionaStock().equals("S")) {
                throw new ExcepcionGeneralSistema("El producto " + nItem.getProducto().getDescripcion() + " no gestiona stock");
            }

            // 14/09/2020 - No hace falta este control en item nuevo
//            if (m.getTipoMovimiento().equals("E")) {
//
//                nItem.setStocks(nItem.getCantidad().negate());
//                Stock s = new Stock(nItem);
//                s.setStockDescontar(nItem.getCantidad().doubleValue());
//
//                if (s.getDeposito().getSigno().equals("+")) {
//
//                    if (!stockRN.isProductoDisponible(s)) {
//
//                        String mensaje = "Stock insuficiente. Hay " + s.getStockDisponible() + " " + s.getProducto().getUnidadDeMedida().getCodigo()
//                                + " disponible/s para " + s.getProducto().getDescripcion() + " en Deposito " + s.getDeposito().getDescripcion()
//                                + (nItem.getProducto().getAdministraAtributo1().equals("S") ? "| Atributo1 " + nItem.getAtributo1() : "")
//                                + (nItem.getProducto().getAdministraAtributo2().equals("S") ? "| Atributo2 " + nItem.getAtributo2() : "")
//                                + (nItem.getProducto().getAdministraAtributo3().equals("S") ? "| Atributo3 " + nItem.getAtributo3() : "")
//                                + (nItem.getProducto().getAdministraAtributo4().equals("S") ? "| Atributo4 " + nItem.getAtributo4() : "")
//                                + (nItem.getProducto().getAdministraAtributo5().equals("S") ? "| Atributo5 " + nItem.getAtributo5() : "")
//                                + (nItem.getProducto().getAdministraAtributo6().equals("S") ? "| Atributo6 " + nItem.getAtributo6() : "")
//                                + (nItem.getProducto().getAdministraAtributo7().equals("S") ? "| Atributo7 " + nItem.getAtributo7() : "");
//
//                        throw new ExcepcionGeneralSistema(mensaje);
//                    }
//                }
//
//            }
//            if (m.getTipoMovimiento().equals("T")) {
//
//                nItem.setStocks(nItem.getCantidad().negate());
//                Stock s = new Stock(nItem);
//
//                s.setStockDescontar(nItem.getCantidad().doubleValue());
//                s.setDeposi(m.getDepositoTransferencia().getCodigo());
//                s.setDeposito(m.getDepositoTransferencia());
//
//                if (s.getDeposito().getSigno().equals("+")) {
//
//                    if (!stockRN.isProductoDisponible(s)) {
//
//                        String mensaje = "Stock insuficiente. Hay " + s.getStockDisponible() + " " + s.getProducto().getUnidadDeMedida().getCodigo()
//                                + " disponible/s para " + s.getProducto().getDescripcion() + " en Deposito " + s.getDeposito().getDescripcion()
//                                + (nItem.getProducto().getAdministraAtributo1().equals("S") ? "| Atributo1 " + nItem.getAtributo1() : "")
//                                + (nItem.getProducto().getAdministraAtributo2().equals("S") ? "| Atributo2 " + nItem.getAtributo2() : "")
//                                + (nItem.getProducto().getAdministraAtributo3().equals("S") ? "| Atributo3 " + nItem.getAtributo3() : "")
//                                + (nItem.getProducto().getAdministraAtributo4().equals("S") ? "| Atributo4 " + nItem.getAtributo4() : "")
//                                + (nItem.getProducto().getAdministraAtributo5().equals("S") ? "| Atributo5 " + nItem.getAtributo5() : "")
//                                + (nItem.getProducto().getAdministraAtributo6().equals("S") ? "| Atributo6 " + nItem.getAtributo6() : "")
//                                + (nItem.getProducto().getAdministraAtributo7().equals("S") ? "| Atributo7 " + nItem.getAtributo7() : "");
//
//                        throw new ExcepcionGeneralSistema(mensaje);
//                    }
//                }
//            }
        }
    }

    public String controlItemsProducto(MovimientoStock m) throws Exception {

        String sErrores = "";

        ParametroStock parametro = parametroStockRN.getParametro();

        for (ItemProductoStock i : m.getItemsProducto()) {

            if (i.getCantidad() == null || i.getCantidad().equals(BigDecimal.ZERO)) {

                sErrores += "| Ingrese una valor diferente a cero en cantidad para el producto " + i.getProducto().getDescripcion();
            }

            if (!m.getTipoMovimiento().equals("A")) {

                if (i.getCantidad() != null && i.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {

                    sErrores += "| Ingrese una valor mayor a cero en cantidad para el producto " + i.getProducto().getDescripcion();
                }

            }

            // Controlamos el ingreso de los atributos de stock
            if (i.getProducto() != null
                    && i.getProducto().getAdministraAtributo1().equals("S")
                    && i.getAtributo1().isEmpty()) {

                sErrores += "| Ingrese el atributo 1 (" + parametro.getDescripcionAtributo1() + ") para el producto " + i.getProducto().getDescripcion();
            }

            if (i.getProducto() != null
                    && i.getProducto().getAdministraAtributo2().equals("S")
                    && i.getAtributo2().isEmpty()) {

                sErrores += "| Ingrese el atributo 2 (" + parametro.getDescripcionAtributo2() + ") para el producto " + i.getProducto().getDescripcion();
            }

            if (i.getProducto() != null
                    && i.getProducto().getAdministraAtributo3().equals("S")
                    && i.getAtributo3().isEmpty()) {

                sErrores += "| Ingrese el atributo 3 (" + parametro.getDescripcionAtributo3() + ") para el producto " + i.getProducto().getDescripcion();
            }

            if (i.getProducto() != null
                    && i.getProducto().getAdministraAtributo4().equals("S")
                    && i.getAtributo4().isEmpty()) {

                sErrores += "| Ingrese el atributo 4 (" + parametro.getDescripcionAtributo4() + ") para el producto " + i.getProducto().getDescripcion();
            }

            if (i.getProducto() != null
                    && i.getProducto().getAdministraAtributo5().equals("S")
                    && i.getAtributo5().isEmpty()) {

                sErrores += "| Ingrese el atributo 5 (" + parametro.getDescripcionAtributo5() + ") para el producto " + i.getProducto().getDescripcion();
            }

            if (i.getProducto() != null
                    && i.getProducto().getAdministraAtributo6().equals("S")
                    && i.getAtributo6().isEmpty()) {

                sErrores += "| Ingrese el atributo 6 (" + parametro.getDescripcionAtributo6() + ") para el producto " + i.getProducto().getDescripcion();
            }

            if (i.getProducto() != null
                    && i.getProducto().getAdministraAtributo7().equals("S")
                    && i.getAtributo7().isEmpty()) {

                sErrores += "| Ingrese el atributo 7 (" + parametro.getDescripcionAtributo7() + ") para el producto " + i.getProducto().getDescripcion();
            }

            if (m.getTipoMovimiento().equals("I")
                    && (i.getPrecio() == null || i.getPrecio().compareTo(BigDecimal.ZERO) <= 0)
                    && i.getProducto().getPidePrecioCosto().equals("S")) {

                sErrores += "| Ingrese el costo de referencia para el producto " + i.getProducto().getDescripcion();
            }

            if (m.getTipoMovimiento().equals("A") && i.getCantidad().compareTo(BigDecimal.ZERO) > 0
                    && (i.getPrecio() == null || i.getPrecio().compareTo(BigDecimal.ZERO) <= 0)
                    && i.getProducto().getPidePrecioCosto().equals("S")) {

                sErrores += "| Ingrese el costo de referencia para el producto " + i.getProducto().getDescripcion();
            }

            if (m.getTipoMovimiento().equals("E") && i.getDeposito().getSigno().equals("+")) {

                Stock s = new Stock(i);
                //Es un egreso de stock por lo tanto convertimos la cantidad a negativo
                s.setStockDescontar(i.getCantidad().doubleValue());

                String mensaje = stockRN.isProductoDisponible(s);

                if (mensaje != null && !mensaje.isEmpty()) {
                    sErrores += "| " + mensaje + " ";
                }
            }

        }

        if (m.getItemTransferencia() != null && m.getDepositoTransferencia() != null && m.getDepositoTransferencia().getSigno().equals("+")) {

            for (ItemTransferenciaStock i : m.getItemTransferencia()) {

                Stock s = new Stock(i);
                //Al ser transferencia viene la cantidad en negativo
                s.setStockDescontar(s.getStocks() * -1);

                String mensaje = stockRN.isProductoDisponible(s);

                if (mensaje != null && !mensaje.isEmpty()) {
                    sErrores += "| " + mensaje + " ";
                }

            }
        }

        return sErrores;
    }

    /**
     * Generamos los items de transferencia para registrar la salida del stock.
     *
     * @param m Movimiento de stock
     */
    private void generarItemTransferencia(MovimientoStock m) throws ExcepcionGeneralSistema {

        //Verificamos que se un movimiento de tipo transferencia
        if (m.getTipoMovimiento().equals("T")) {
            //Generamos la lista vacía
            m.setItemTransferencia(new ArrayList<ItemTransferenciaStock>());

            if (m.getId() != null) {
                movimientoStockDAO.borraItemsTransferencia(m.getId());
            }
        } else {
            return;
        }

        if (m.getItemsProducto() != null) {

            for (ItemProductoStock i : m.getItemsProducto()) {

                if (i.getProducto() != null) {
                    ItemTransferenciaStock t = nuevoItemTransferencia(m);
                    t.setProducto(i.getProducto());
                    t.setUnidadMedida(i.getUnidadMedida());
                    t.setPrecio(i.getPrecio());
                    t.setAtributo1(i.getAtributo1());
                    t.setAtributo2(i.getAtributo2());
                    t.setAtributo3(i.getAtributo3());
                    t.setAtributo4(i.getAtributo4());
                    t.setAtributo5(i.getAtributo5());
                    t.setAtributo6(i.getAtributo6());
                    t.setAtributo7(i.getAtributo7());

                    if (i.getCantidad() == null) {
                        throw new ExcepcionGeneralSistema("Cantidad en blanco");
                    }

                    t.setCantidad(i.getCantidad());
                    t.setStocks(i.getCantidad().negate());
                    m.getItemTransferencia().add(t);
                }
            }
        }
    }

    /**
     * Generar los objetos stock donde se almacena el stock de los productos por
     * deposito, fecha, etc
     *
     * @param m Movimiento de stock
     * @throws Exception
     */
    public void generarStock(MovimientoStock m) throws Exception {

        if (m == null) {
            return;
        }

        for (ItemProductoStock i : m.getItemsProducto()) {

            Stock nStock = new Stock(i);
            stockRN.guardar(nStock);
        }

        if (m.getItemTransferencia() != null) {

            for (ItemTransferenciaStock i : m.getItemTransferencia()) {

                Stock nStock = new Stock(i);
                stockRN.guardar(nStock);
            }
        }
    }

    private void asignarDepositoItems(MovimientoStock m) {

        if (m.getItemsProducto() != null) {

            for (ItemMovimientoStock i : m.getItemsProducto()) {

                i.setDeposito(m.getDeposito());
            }
        }

        //Aplicamos el deposito a los items de transferencia
        if (m.getTipoMovimiento().equals("T")) {

            if (m.getItemTransferencia() != null) {
                for (ItemMovimientoStock i : m.getItemTransferencia()) {
                    i.setDeposito(m.getDepositoTransferencia());
                }
            }
        }
    }

    private void asignarCantidadStock(MovimientoStock m) {

        if (m.getItemsProducto() == null) {
            return;
        }

        for (ItemMovimientoStock i : m.getItemsProducto()) {

            //Si es un egreso actualizamos el stock en negativo
            if (m.getTipoMovimiento().equals("E")) {
                i.setStocks(i.getCantidad().negate());
            } else {
                i.setStocks(i.getCantidad());
            }
        }

        if (m.getItemTransferencia() != null) {
            for (ItemMovimientoStock i : m.getItemTransferencia()) {
                i.setStocks(i.getCantidad().negate());
            }
        }
    }

    /**
     * Borramos de la lista los items que no son válidos para guardar y que
     * pudieran generar errores
     *
     * @param m Movimiento de Stock
     */
    private void borrarItemsNoValidos(MovimientoStock m) {

        if (m.getItemsProducto() == null) {
            return;
        }

        String indiceBorrar[] = new String[m.getItemsProducto().size()];

        //Seteamos los valores en -1
        for (int i = 0; i < indiceBorrar.length; i++) {
            indiceBorrar[i] = "N";
        }

        for (int i = 0; i < m.getItemsProducto().size(); i++) {

            ItemProductoStock im = m.getItemsProducto().get(i);

            if (im.getProducto() == null) {
                indiceBorrar[i] = "S";
                continue;
            }
//
//            if (!im.isTodoOk()) {
//                indiceBorrar[i] = "S";
//            }
        }

        for (int i = 0; i < indiceBorrar.length; i++) {
            if (indiceBorrar[i].equals("S")) {
                ItemProductoStock remove = m.getItemsProducto().remove(i);
            }
        }

    }

    /**
     * Eliminar un item de un movimiento
     *
     * @param m movimiento del cual se eliminará el item
     * @param nItem item a eliminar
     * @return éxito si la eliminación fue exitosa
     */
    public void eliminarItemProducto(MovimientoStock movimiento, ItemProductoStock nItem) throws Exception {

        if (movimiento == null) {
            throw new ExcepcionGeneralSistema("Movimiento vacío, nada para eliminar");
        }

        if (nItem == null) {
            throw new ExcepcionGeneralSistema("Item nulo, nada para eliminar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemProducto = -1;

        for (ItemProductoStock ip : movimiento.getItemsProducto()) {

            if (ip.getNroitm() >= 0) {

                if (ip.getNroitm() == nItem.getNroitm()) {
                    indiceItemProducto = i;
                }
            }
            i++;
        }
        i = 0;

        //Borramos los items productos
        if (indiceItemProducto >= 0) {
            ItemProductoStock remove = movimiento.getItemsProducto().remove(indiceItemProducto);
            if (remove != null) {

                if (movimiento.getId() != null && remove.getId() != null) {
                    movimientoStockDAO.eliminar(ItemProductoStock.class,
                            remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(movimiento);

    }

    public MovimientoStock generarComprobante(MovimientoFacturacion movimientoFacturacion) throws ExcepcionGeneralSistema, Exception {

        Formulario formulario = formularioRN.getFormulario(movimientoFacturacion.getComprobanteStock(), movimientoFacturacion.getPuntoVentaStock(), "X");
        ParametroStock parametro = parametroStockRN.getParametro();
        MovimientoStock movimientoStock = crearMovimiento(movimientoFacturacion.getComprobanteStock(), formulario, movimientoFacturacion.getPuntoVentaStock());

        movimientoStock.setFechaMovimiento(movimientoFacturacion.getFechaMovimiento());
        movimientoStock.setMonedaSecundaria(movimientoFacturacion.getMonedaSecundaria());
        movimientoStock.setMonedaRegistracion(movimientoFacturacion.getMonedaRegistracion());
        movimientoStock.setCotizacion(movimientoFacturacion.getCotizacion());
        movimientoStock.setEntidadComercial(movimientoFacturacion.getCliente());
        movimientoStock.setTransporte(movimientoFacturacion.getTransporte());

        movimientoStock.setDeposito(movimientoFacturacion.getDeposito());
        movimientoStock.setDepositoTransferencia(movimientoFacturacion.getDepositoTransferencia());
        movimientoStock.setObservaciones(movimientoFacturacion.getObservaciones());

        movimientoStock.getItemsProducto().clear();

        for (ItemMovimientoFacturacion itemProducto : movimientoFacturacion.getItemsProducto()) {

            if (itemProducto.getProducto() != null) {

//                System.err.println("Producto  " + itemProducto.getProducto().getDescripcion());
                if (itemProducto.getProducto().getTipoProducto().getGestionaStock().equals("S")
                        && itemProducto.getProducto().getGestionaStock().equals("S")) {

                    for (ItemMovimientoFacturacionDetalle itemDetalle : itemProducto.getItemsDetalle()) {

//                        System.err.println("Agregar item producto from itemdetalle  " + itemDetalle.getDescripcion());
                        ItemProductoStock itemProductoStock = nuevoItemProducto(movimientoStock);
                        itemProductoStock.setObservaciones(itemProducto.getObservaciones());

                        cargarAtributos(itemProductoStock, itemDetalle);

                        itemProductoStock.setPrecio(itemProducto.getPrecioCosto());
                        itemProductoStock.setPrecioSecundario(itemProducto.getPrecioCostoSecundario());

                        itemProductoStock.setMovimiento(movimientoStock);
                        movimientoStock.getItemsProducto().add(itemProductoStock);
                    }
                }

                if (itemProducto.getProducto().getEsKitVenta().equals("S") && itemProducto.getItemsKit() != null) {

                    for (ItemMovimientoFacturacionKit itemKit : itemProducto.getItemsKit()) {

                        if (itemKit.getProducto().getTipoProducto().getGestionaStock().equals("S")
                                && itemKit.getProducto().getGestionaStock().equals("S")) {

//                            System.err.println("Agregar item kit " + itemKit.getDescripcion());
                            ItemProductoStock itemProductoStock = nuevoItemProducto(movimientoStock);

                            asignarProducto(itemProductoStock, itemKit.getProducto());

                            itemProductoStock.setCantidad(itemKit.getCantidad());

                            itemProductoStock.setPrecio(itemKit.getPrecioCosto());
                            itemProductoStock.setPrecioSecundario(itemKit.getPrecioCostoSecundario());

                            itemProductoStock.setMovimiento(movimientoStock);
                            movimientoStock.getItemsProducto().add(itemProductoStock);
                        }
                    }
                }
            }
        }

//        System.err.println("items mov stock " + movimientoStock.getItemsProducto());
        borrarItemsNoValidos(movimientoStock);
        generarItemTransferencia(movimientoStock);
        asignarDepositoItems(movimientoStock);
        asignarCantidadStock(movimientoStock);
        control(movimientoStock, true);
//        generarStock(movimientoStock);

//        System.err.println("items mov stock " + movimientoStock.getItemsProducto());
//        if(ms.getItemsProducto().isEmpty()){
//            ms = null;
//        }
        return movimientoStock;

    }

    /**
     *
     * @param movimientoCompra Movimiento de producción a partir del cual se
     * genera el movimiento de stock
     * @return Movimiento de stock generado
     * @throws ExcepcionGeneralSistema
     * @throws Exception
     */
    public MovimientoStock generarComprobante(MovimientoCompra movimientoCompra) throws ExcepcionGeneralSistema, Exception {

        Formulario formulario = formularioRN.getFormulario(movimientoCompra.getComprobanteStock(), movimientoCompra.getPuntoVentaStock(), "X");

        MovimientoStock movimientoStock = crearMovimiento(movimientoCompra.getComprobanteStock(), formulario, movimientoCompra.getPuntoVentaStock());

        movimientoStock.setFechaMovimiento(movimientoCompra.getFechaMovimiento());
        movimientoStock.setMonedaSecundaria(movimientoCompra.getMonedaSecundaria());
        movimientoStock.setMonedaRegistracion(movimientoCompra.getMonedaRegistracion());
        movimientoStock.setCotizacion(movimientoCompra.getCotizacion());
        movimientoStock.setEntidadComercial(movimientoCompra.getProveedor());

        movimientoStock.setDeposito(movimientoCompra.getDeposito());
        movimientoStock.setDepositoTransferencia(movimientoCompra.getDepositoTransferencia());

        movimientoStock.setObservaciones(movimientoCompra.getObservaciones());

        movimientoStock.getItemsProducto().clear();

        for (ItemMovimientoCompra itemProducto : movimientoCompra.getItemsProducto()) {

            if (itemProducto.getProducto() != null
                    && itemProducto.getProducto().getTipoProducto().getGestionaStock().equals("S")
                    && itemProducto.getProducto().getGestionaStock().equals("S")) {

                for (ItemDetalleItemMovimientoCompra itemDetalle : itemProducto.getItemsDetalle()) {

                    ItemProductoStock itemProductoStock = nuevoItemProducto(movimientoStock);
                    itemProductoStock.setObservaciones(itemProducto.getObservaciones());

                    cargarAtributos(itemProductoStock, itemDetalle);

                    itemProductoStock.setPrecio(itemProducto.getPrecio());
                    itemProductoStock.setPrecioSecundario(itemProducto.getPrecioSecundario());
                    itemProductoStock.setMovimiento(movimientoStock);
                    movimientoStock.getItemsProducto().add(itemProductoStock);
                }
            }
        }

        borrarItemsNoValidos(movimientoStock);
        generarItemTransferencia(movimientoStock);
        asignarDepositoItems(movimientoStock);
        asignarCantidadStock(movimientoStock);
        control(movimientoStock, true);
//        generarStock(movimientoStock);

//        if (movimientoStock.getItemsProducto().isEmpty()) {
//            movimientoStock = null;
//        }
        return movimientoStock;

    }

    public MovimientoStock generarComprobante(MovimientoProduccion movimientoProduccion) throws ExcepcionGeneralSistema, Exception {

        Formulario formulario = formularioRN.getFormulario(movimientoProduccion.getComprobanteStock(), movimientoProduccion.getPuntoVenta(), "X");

        MovimientoStock movimientoStock = crearMovimiento(movimientoProduccion.getComprobanteStock(), formulario, movimientoProduccion.getPuntoVentaStock());

        movimientoStock.setFechaMovimiento(movimientoProduccion.getFechaMovimiento());
        movimientoStock.setMonedaSecundaria(movimientoProduccion.getMonedaSecundaria());
        movimientoStock.setMonedaRegistracion(movimientoProduccion.getMonedaRegistracion());
        movimientoStock.setCotizacion(movimientoProduccion.getCotizacion());

        movimientoStock.setDeposito(movimientoProduccion.getDeposito());
        movimientoStock.setDepositoTransferencia(movimientoProduccion.getDepositoTransferencia());

        movimientoStock.getItemsProducto().clear();

        if (movimientoProduccion.getItemsMovimiento() != null) {

            for (ItemMovimientoProduccion item : movimientoProduccion.getItemsMovimiento()) {

                if ((item.getTipitm().equals("P") || item.getTipitm().equals("C"))
                        && item.getProducto() != null
                        && item.getProducto().getTipoProducto().getGestionaStock().equals("S")
                        && item.getProducto().getGestionaStock().equals("S")) {

                    for (ItemDetalleMovimientoProduccion itemDetalleProduccion : item.getItemDetalle()) {

                        ItemProductoStock itemProductoStock = nuevoItemProducto(movimientoStock);
                        itemProductoStock.setObservaciones(item.getObservaciones());

                        cargarAtributos(itemProductoStock, itemDetalleProduccion);

                        itemProductoStock.setMovimiento(movimientoStock);
                        movimientoStock.getItemsProducto().add(itemProductoStock);
                    }
                }
            }
        }

        borrarItemsNoValidos(movimientoStock);
        generarItemTransferencia(movimientoStock);
        asignarDepositoItems(movimientoStock);
        asignarCantidadStock(movimientoStock);
        control(movimientoStock, true);
        generarStock(movimientoStock);

        if (movimientoStock.getItemsProducto().isEmpty()) {
            movimientoStock = null;
        }

        return movimientoStock;
    }

    public MovimientoStock generarComprobante(MovimientoTaller movimientoTaller) throws ExcepcionGeneralSistema, Exception {

        Formulario formulario = formularioRN.getFormulario(movimientoTaller.getComprobanteStock(), movimientoTaller.getPuntoVentaStock(), "X");

        MovimientoStock movimientoStock = crearMovimiento(movimientoTaller.getComprobanteStock(), formulario, movimientoTaller.getPuntoVentaStock());

        movimientoStock.setFechaMovimiento(movimientoTaller.getFechaMovimiento());
        movimientoStock.setMonedaSecundaria(movimientoTaller.getMonedaSecundaria());
        movimientoStock.setMonedaRegistracion(movimientoTaller.getMonedaRegistracion());
        movimientoStock.setCotizacion(movimientoTaller.getCotizacion());

        movimientoStock.setDeposito(movimientoTaller.getDeposito());
        movimientoStock.setDepositoTransferencia(movimientoTaller.getDepositoTransferencia());

        movimientoStock.setObservaciones(movimientoTaller.getObservaciones());

        movimientoStock.getItemsProducto().clear();

        for (ItemProductoTaller itemProducto : movimientoTaller.getItemsProducto()) {

            if (itemProducto.getProducto() != null
                    && itemProducto.getProducto().getTipoProducto().getGestionaStock().equals("S")
                    && itemProducto.getProducto().getGestionaStock().equals("S")) {

                ItemProductoStock itemProductoStock = nuevoItemProducto(movimientoStock);
                itemProductoStock.setObservaciones(itemProducto.getObservaciones());

                cargarAtributos(itemProductoStock, itemProducto);

                itemProductoStock.setMovimiento(movimientoStock);
                movimientoStock.getItemsProducto().add(itemProductoStock);

            }
        }

        borrarItemsNoValidos(movimientoStock);
        generarItemTransferencia(movimientoStock);
        asignarDepositoItems(movimientoStock);
        asignarCantidadStock(movimientoStock);
        control(movimientoStock, true);
//        generarStock(movimientoStock);

//        if (movimientoStock.getItemsProducto().isEmpty()) {
//            movimientoStock = null;
//        }
        return movimientoStock;

    }

    private void cargarAtributos(ItemProductoStock itemProductoStock, ItemDetalleModelo itemDetalle) {

        itemProductoStock.setProducto(itemDetalle.getProducto());
        itemProductoStock.setCantidad(itemDetalle.getCantidad());
        itemProductoStock.setStockComprometer(itemDetalle.getStockComprometer());
        itemProductoStock.setStockDescomprometer(itemDetalle.getStockDescomprometer());
        itemProductoStock.setStockDescontar(itemDetalle.getStockDescontar());

        itemProductoStock.setUnidadMedida(itemDetalle.getUnidadMedida());

        if (itemDetalle.getProducto().getMonedaReposicion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())
                && itemProductoStock.getCotizacion() != null) {
            itemProductoStock.setPrecio(itemDetalle.getProducto().getPrecioReposicion());
            itemProductoStock.setPrecioSecundario(itemDetalle.getProducto().getPrecioReposicion().multiply(itemProductoStock.getCotizacion()));
        } else {
            itemProductoStock.setPrecio(itemDetalle.getProducto().getPrecioReposicion().divide(itemProductoStock.getCotizacion(), 4, RoundingMode.HALF_UP));
            itemProductoStock.setPrecioSecundario(itemDetalle.getProducto().getPrecioReposicion());
        }

        if (itemDetalle.getProducto().getAdministraAtributo1().equals("S")
                && itemDetalle.getAtributo1() != null
                && !itemDetalle.getAtributo1().isEmpty()) {

            itemProductoStock.setAtributo1(itemDetalle.getAtributo1());
        }

        if (itemDetalle.getProducto().getAdministraAtributo2().equals("S")
                && itemDetalle.getAtributo2() != null
                && !itemDetalle.getAtributo2().isEmpty()) {

            itemProductoStock.setAtributo2(itemDetalle.getAtributo2());
        }

        if (itemDetalle.getProducto().getAdministraAtributo3().equals("S")
                && itemDetalle.getAtributo3() != null
                && !itemDetalle.getAtributo3().isEmpty()) {

            itemProductoStock.setAtributo3(itemDetalle.getAtributo3());
        }

        if (itemDetalle.getProducto().getAdministraAtributo4().equals("S")
                && itemDetalle.getAtributo4() != null
                && !itemDetalle.getAtributo4().isEmpty()) {

            itemProductoStock.setAtributo4(itemDetalle.getAtributo4());
        }

        if (itemDetalle.getProducto().getAdministraAtributo5().equals("S")
                && itemDetalle.getAtributo5() != null
                && !itemDetalle.getAtributo5().isEmpty()) {

            itemProductoStock.setAtributo5(itemDetalle.getAtributo5());
        }

        if (itemDetalle.getProducto().getAdministraAtributo6().equals("S")
                && itemDetalle.getAtributo6() != null
                && !itemDetalle.getAtributo6().isEmpty()) {

            itemProductoStock.setAtributo6(itemDetalle.getAtributo6());
        }

        if (itemDetalle.getProducto().getAdministraAtributo7().equals("S")
                && itemDetalle.getAtributo7() != null
                && !itemDetalle.getAtributo7().isEmpty()) {

            itemProductoStock.setAtributo7(itemDetalle.getAtributo7());
        }

        itemProductoStock.setTodoOk(true);

    }

    public void asignarProducto(ItemMovimientoStock itemProducto, Producto producto) throws ExcepcionGeneralSistema {

        String sErrores = "";

        if (itemProducto.getMovimiento().getMonedaSecundaria() == null) {
            sErrores += "- El comprobante no tiene una moneda secundaria asignada \n";
        }

        if (producto.getCuentaContableVenta() == null) {
            sErrores += "- El producto no tiene una cuenta contable de venta asignada \n";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }

        itemProducto.setProducto(producto);
        itemProducto.setPrecio(producto.getPrecioReposicion());
        itemProducto.setUnidadMedida(producto.getUnidadDeMedida());
        itemProducto.setAtributo1("");
        itemProducto.setAtributo2("");
        itemProducto.setAtributo3("");
        itemProducto.setAtributo4("");
        itemProducto.setAtributo5("");
        itemProducto.setAtributo6("");
        itemProducto.setAtributo7("");

    }

    public List<MovimientoStock> getListaByBusqueda(Map<String, String> filtro, int cantidadRegistros) {

        return movimientoStockDAO.getListaByBusqueda(filtro, cantidadRegistros);
    }

    public MovimientoStock getMovimiento(String codigo, Integer numeroFormulario) {

        return movimientoStockDAO.getMovimiento(codigo, numeroFormulario);
    }

    public void reorganizarNroItem(MovimientoStock movimiento) {

        //Reorganizamos los números de items
        int i = 1;

        if (movimiento.getItemsProducto() != null) {

            for (ItemProductoStock ip : movimiento.getItemsProducto()) {
                ip.setNroitm(i);
                i++;
            }
        }

        i = 1;
        if (movimiento.getItemTransferencia() != null) {

            for (ItemTransferenciaStock ip : movimiento.getItemTransferencia()) {
                ip.setNroitm(i);
                i++;
            }
        }
    }

    public void asignarMascaraStock(MovimientoStock m, MascaraStock mascara) throws ExcepcionGeneralSistema {

        m.setMascara(mascara);

        if (mascara.getItems() != null) {

            m.getItemsProducto().clear();

            for (ItemMascaraStock im : mascara.getItems()) {

                ItemProductoStock ip = nuevoItemProducto(m);
                asignarProducto(ip, im.getProducto());
                m.getItemsProducto().add(ip);
            }
        }

    }

    public MovimientoStock revertirMovimiento(MovimientoStock mReversion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
