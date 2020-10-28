/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.rn;

import bs.administracion.rn.ParametrosRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.Moneda;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.FormularioRN;
import bs.global.rn.MonedaRN;
import bs.global.rn.PuntoVentaRN;
import bs.global.util.JsfUtil;
import bs.produccion.dao.ProduccionDAO;
import bs.produccion.modelo.CircuitoProduccion;
import bs.produccion.modelo.ItemDetalleMovimientoProduccion;
import bs.produccion.modelo.ItemMovimientoProduccion;
import bs.produccion.modelo.MovimientoProduccion;
import bs.produccion.modelo.Sector;
import bs.produccion.modelo.TipoMovimientoProduccion;
import bs.produccion.vista.PendienteProduccionDetalle;
import bs.produccion.vista.PendienteProduccionGrupo;
import bs.stock.modelo.ComposicionFormula;
import bs.stock.modelo.Formula;
import bs.stock.modelo.ItemComposicionFormula;
import bs.stock.modelo.ItemComposicionFormulaComponente;
import bs.stock.modelo.ItemComposicionFormulaProceso;
import bs.stock.modelo.MovimientoStock;
import bs.stock.modelo.ParametroStock;
import bs.stock.modelo.Producto;
import bs.stock.rn.ComposicionFormulaRN;
import bs.stock.rn.MovimientoStockRN;
import bs.stock.rn.ParametroStockRN;
import bs.stock.rn.ProductoRN;
import bs.stock.rn.StockRN;
import bs.tarea.modelo.Tarea;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * @author ctrosch
 */
@Stateless
public class ProduccionRN implements Serializable {

    @EJB
    private MonedaRN monedaRN;
    @EJB
    protected ParametrosRN parametrosRN;
    @EJB
    private ProduccionDAO produccionDAO;
    @EJB
    private ParametroStockRN parametroStockRN;
    @EJB
    protected ComposicionFormulaRN composicionFormulaRN;
    @EJB
    private CircuitoProduccionRN circuitoRN;
    @EJB
    private MovimientoStockRN movimientoStockRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private PuntoVentaRN puntoVentaRN;
    @EJB
    private ProductoRN productoRN;
    @EJB
    private StockRN stockRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized MovimientoProduccion guardar(MovimientoProduccion movimiento) throws Exception {

        sincronizarCantidades(movimiento);
        reorganizarNroItem(movimiento);
        generarDatosAdicionales(movimiento);
        controlComprobante(movimiento);

        if (movimiento.getId() == null) {

            generarMovimientosAdicionales(movimiento);

            if (!movimiento.isNoSincronizaNumeroFormulario() && movimiento.getNumeroFormulario() > 0) {
                tomarNumeroFormulario(movimiento);
            }

            produccionDAO.crear(movimiento);

        } else {
            movimiento = produccionDAO.editar(movimiento);
        }
        actualizarCantidadesPendientes(movimiento);
        return movimiento;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public MovimientoProduccion editar(MovimientoProduccion movimiento) {

        return produccionDAO.editar(movimiento);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized MovimientoProduccion guardarSincronizacion(MovimientoProduccion movimiento) throws Exception {

        if (movimiento.getId() == null) {

            reorganizarNroItem(movimiento);
            generarDatosAdicionales(movimiento);
            controlComprobante(movimiento);

            generarMovimientosAdicionales(movimiento);

            if (!movimiento.isNoSincronizaNumeroFormulario() && movimiento.getNumeroFormulario() > 0) {
                tomarNumeroFormulario(movimiento);
            }
            produccionDAO.crear(movimiento);
        } else {
            movimiento = produccionDAO.editar(movimiento);
        }

        actualizarCantidadesPendientes(movimiento);
        return movimiento;
    }

    public void prepararMovimiento(MovimientoProduccion m) throws Exception {

        sincronizarCantidades(m);
        reorganizarNroItem(m);
        generarDatosAdicionales(m);
        controlComprobante(m);
        generarMovimientosAdicionales(m);

    }

    public void guardarComprobanteStock(MovimientoStock m) throws Exception {

        if (m != null) {
            movimientoStockRN.guardar(m);
        }
    }

    public void generarMovimientosAdicionales(MovimientoProduccion m) throws ExcepcionGeneralSistema, Exception {

        if (m.getComprobanteStock() != null) {
            MovimientoStock ms = movimientoStockRN.generarComprobante(m);
            m.setMovimientoStock(ms);
        }
    }

    /**
     *
     * @param circom circuito inicial
     * @param cirapl circuito aplicación
     * @param modPD modulo comprobante de producción
     * @param codPD código comprobante de producción
     * @param sucPD sucursal comprobante producción
     * @return Movimiento de producción generado
     * @throws ExcepcionGeneralSistema
     */
    public MovimientoProduccion nuevoMovimiento(
            String circom, String cirapl,
            String modPD, String codPD, String sucPD) throws ExcepcionGeneralSistema {

        CircuitoProduccion circuito = circuitoRN.iniciarCircuito(circom, cirapl, modPD, codPD, sucPD);
        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(sucPD);
        PuntoVenta puntoVentaStock = puntoVentaRN.getPuntoVenta(sucPD);

        return nuevoMovimiento(circuito, puntoVenta, puntoVentaStock);

    }

    /**
     *
     * @param circom circuito inicial
     * @param cirapl circuito aplicación
     * @param modPD modulo comprobante de producción
     * @param codPD código comprobante de producción
     * @param sucPD sucursal comprobante producción
     * @param modST modulo comprobante stock
     * @param codST código comprobante stock
     * @param sucST sucursal comprobante stock
     * @return Movimiento de producción generado
     * @throws ExcepcionGeneralSistema
     */
    public MovimientoProduccion nuevoMovimiento(String circom, String cirapl,
            String modPD, String codPD, String sucPD,
            String modST, String codST, String sucST) throws ExcepcionGeneralSistema {

        CircuitoProduccion circuito = circuitoRN.iniciarCircuito(circom, cirapl, modPD, codPD, sucPD, modST, codST, sucST);
        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(sucPD);
        PuntoVenta puntoVentaStock = puntoVentaRN.getPuntoVenta(sucPD);

        return nuevoMovimiento(circuito, puntoVenta, puntoVentaStock);

    }

    public MovimientoProduccion nuevoMovimiento(CircuitoProduccion circuito, PuntoVenta puntoVenta, PuntoVenta puntoVentaStock) throws ExcepcionGeneralSistema {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("No es posible crear un nuevo movimiento si el circuito es nulo");
        }

        Comprobante comprobante = null;
        Comprobante comprobanteST = null;

        if (circuito.getActualizaProduccion().equals("S")) {

            comprobante = circuito.getComprobanteProduccion();

        } else if (circuito.getActualizaStock().equals("S")) {

            comprobante = circuito.getComprobanteStock();
        }

        MovimientoProduccion m = crearMovimiento(circuito, comprobante, circuito.getTipoMovimiento(), puntoVenta, puntoVentaStock);

        comprobanteST = circuito.getComprobanteStock();

        if (comprobanteST != null) {
            m.setComprobanteStock(comprobanteST);

            if (comprobanteST.getDeposito() != null) {
                m.setDeposito(comprobanteST.getDeposito());
            }

            if (comprobanteST.getDepositoTransferencia() != null) {
                m.setDepositoTransferencia(comprobanteST.getDepositoTransferencia());
            }
        }

        return m;
    }

    public MovimientoProduccion nuevoMovimientoFromPendiente(CircuitoProduccion circuito, PuntoVenta puntoVenta, PuntoVenta puntoVentaStock,
            PendienteProduccionGrupo pendienteGrupo,
            List<PendienteProduccionDetalle> itemsPendientes) throws ExcepcionGeneralSistema, Exception {

        if (!tengoItemsSeleccionados(itemsPendientes)) {
            throw new ExcepcionGeneralSistema("No existen items seleccionados para generar el movimiento");
        }

        MovimientoProduccion m = nuevoMovimiento(circuito, puntoVenta, puntoVentaStock);

        m.setPlanta(pendienteGrupo.getPlanta());

        generarItemsFromPendiente(m, itemsPendientes);
        asignarFormulario(m);

        return m;
    }

    public MovimientoProduccion nuevoMovimientoFromItems(CircuitoProduccion circuito, PuntoVenta puntoVenta, PuntoVenta puntoVentaStock,
            List<ItemMovimientoProduccion> itemsMovimiento) throws ExcepcionGeneralSistema, Exception {

        MovimientoProduccion m = nuevoMovimiento(circuito, puntoVenta, puntoVentaStock);

        generarItemsFromItemMovimiento(m, itemsMovimiento);
        asignarFormulario(m);

        return m;
    }

    private MovimientoProduccion crearMovimiento(CircuitoProduccion circuito, Comprobante comprobante, TipoMovimientoProduccion tm,
            PuntoVenta puntoVenta, PuntoVenta puntoVentaStock) throws ExcepcionGeneralSistema {

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("El comprobante no puede ser nulo");
        }

        MovimientoProduccion m = new MovimientoProduccion();
        Moneda moneda = monedaRN.getMoneda(parametrosRN.getParametro().getCodigoMonedaSecundaria());
        BigDecimal cotizacion = monedaRN.getCotizacionDia(parametrosRN.getParametro().getCodigoMonedaSecundaria());

        m.setEmpresa(puntoVenta.getEmpresa());
        m.setSucursal(puntoVenta.getSucursal());
        m.setPuntoVenta(puntoVenta);

        m.setPersistido(false);
        m.setCircuito(circuito);
        m.setComprobante(comprobante);
        m.setPuntoVenta(puntoVenta);
        m.setPuntoVentaStock(puntoVentaStock);
        m.setTipoMovimiento(tm);
        m.setMonedaRegistracion(comprobante.getMonedaRegistracion());
        m.setMonedaSecundaria(moneda);
        m.setCotizacion(cotizacion);

        asignarFormulario(m);

        if (comprobante.getDeposito() != null) {
            m.setDeposito(comprobante.getDeposito());
        }

        if (comprobante.getDepositoTransferencia() != null) {
            m.setDepositoTransferencia(comprobante.getDepositoTransferencia());
        }

//        if (circuito.getCircuitoComienzo().equals(circuito.getCircuitoAplicacion())) {
//            m.getItemsMovimiento().add((ItemMovimientoProduccion) nuevoItem(m, "P"));
//        }
        return m;
    }

    public ItemMovimientoProduccion nuevoItem(MovimientoProduccion m, String tipitm) {

        ItemMovimientoProduccion nItem = new ItemMovimientoProduccion();

        nItem.setNroitm(m.getItemsMovimiento().size() + 1);
        nItem.setTipitm(tipitm);
        nItem.setMovimiento(m);
        nItem.setMovimientoOriginal(m);

        return nItem;
    }

    public ItemDetalleMovimientoProduccion nuevoItemDetalle(ItemMovimientoProduccion itemProducto) {

        ItemDetalleMovimientoProduccion itemDetalle = new ItemDetalleMovimientoProduccion();

        itemDetalle.setAtributo1(itemProducto.getAtributo1());
        itemDetalle.setAtributo2(itemProducto.getAtributo2());
        itemDetalle.setAtributo3(itemProducto.getAtributo3());
        itemDetalle.setAtributo4(itemProducto.getAtributo4());
        itemDetalle.setAtributo5(itemProducto.getAtributo5());
        itemDetalle.setAtributo6(itemProducto.getAtributo6());
        itemDetalle.setAtributo7(itemProducto.getAtributo7());

        itemDetalle.setProducto(itemProducto.getProducto());
        itemDetalle.setCantidad(BigDecimal.ZERO);
        itemDetalle.setUnidadMedida(itemProducto.getUnidadMedida());

        itemDetalle.setItemProducto(itemProducto);
        return itemDetalle;
    }

    public ItemDetalleMovimientoProduccion nuevoItemDetalleFromItem(ItemMovimientoProduccion itemProducto, ItemDetalleMovimientoProduccion itemDetalleCopiar) {

        ItemDetalleMovimientoProduccion itemDetalle = new ItemDetalleMovimientoProduccion();

        itemDetalle.setAtributo1(itemDetalleCopiar.getAtributo1());
        itemDetalle.setAtributo2(itemDetalleCopiar.getAtributo2());
        itemDetalle.setAtributo3(itemDetalleCopiar.getAtributo3());
        itemDetalle.setAtributo4(itemDetalleCopiar.getAtributo4());
        itemDetalle.setAtributo5(itemDetalleCopiar.getAtributo5());
        itemDetalle.setAtributo6(itemDetalleCopiar.getAtributo6());
        itemDetalle.setAtributo7(itemDetalleCopiar.getAtributo7());

        itemDetalle.setProducto(itemDetalleCopiar.getProducto());
        itemDetalle.setCantidad(itemDetalleCopiar.getCantidad());
        itemDetalle.setUnidadMedida(itemDetalleCopiar.getUnidadMedida());

        itemDetalle.setItemProducto(itemProducto);
        return itemDetalle;
    }

    //-------------------------------------------------------------------------------------------
    public void generarItemFromItemMovimiento(MovimientoProduccion movimiento, ItemMovimientoProduccion itemCopiar, ItemMovimientoProduccion itemNuevo) throws ExcepcionGeneralSistema {

        itemNuevo.setProducto(itemCopiar.getProducto());
        itemNuevo.setProductoOriginal(itemCopiar.getProducto());
        itemNuevo.setUnidadMedida(itemCopiar.getUnidadMedida());
        itemNuevo.setOperario(itemCopiar.getOperario());
        itemNuevo.setPrecio(itemCopiar.getPrecio());

        itemNuevo.setAtributo1(itemCopiar.getAtributo1());
        itemNuevo.setAtributo2(itemCopiar.getAtributo2());
        itemNuevo.setAtributo3(itemCopiar.getAtributo3());
        itemNuevo.setAtributo4(itemCopiar.getAtributo4());
        itemNuevo.setAtributo5(itemCopiar.getAtributo5());
        itemNuevo.setAtributo6(itemCopiar.getAtributo6());
        itemNuevo.setAtributo7(itemCopiar.getAtributo7());

        if (itemCopiar.getProduccion() != null && itemCopiar.getProduccion().compareTo(BigDecimal.ZERO) > 0) {
            itemNuevo.setCantidad(itemCopiar.getProduccion());
            itemNuevo.setCantidadPendiente(itemCopiar.getCantidad());
            itemNuevo.setCantidadOriginal(itemCopiar.getCantidad());
            itemNuevo.setCantidadStock(itemCopiar.getProduccion());
            itemNuevo.setCantidadSecundaria(itemCopiar.getCantidadSecundaria());
            itemNuevo.setCantidadNominal(itemCopiar.getCantidadNominal());
        } else {
            itemNuevo.setCantidad(BigDecimal.ZERO);
            itemNuevo.setCantidadPendiente(BigDecimal.ZERO);
            itemNuevo.setCantidadOriginal(BigDecimal.ZERO);
            itemNuevo.setCantidadStock(BigDecimal.ZERO);
            itemNuevo.setCantidadSecundaria(BigDecimal.ZERO);
            itemNuevo.setCantidadNominal(BigDecimal.ZERO);

        }

        itemNuevo.setActualizaStock(itemCopiar.getActualizaStock());
        itemNuevo.setGrupo(itemCopiar.getGrupo());

        if (itemCopiar.getComposicionFormula() != null) {
            itemNuevo.setComposicionFormula(itemCopiar.getComposicionFormula());
        }

        //Si tiene asignado la toma de numero de serie se lo asignamos
        //Ahora toma nro de serie de hora de ruta
        if (movimiento.getTipoMovimiento().equals(TipoMovimientoProduccion.PP)) {

            SimpleDateFormat sdfAnio = new SimpleDateFormat("yy");
            SimpleDateFormat sdfSemana = new SimpleDateFormat("ww");
            SimpleDateFormat sdfDiaSemana = new SimpleDateFormat("uu");

            String nroLote = String.valueOf(itemCopiar.getMovimiento().getNumeroFormulario()) + "-";
            nroLote = nroLote + sdfAnio.format(new Date());
            nroLote = nroLote + sdfSemana.format(new Date());
            nroLote = nroLote + sdfDiaSemana.format(new Date());

            itemNuevo.setAtributo3(nroLote);
        }

        itemNuevo.setTodoOk(true);

        //Verificamos si el circuito aplica a items pendientes y si no es temporal
        if (movimiento.getCircuito().getNoCancelaPendiente().equals("N")
                && itemCopiar.getMovimiento() != null) {
            itemNuevo.setItemAplicado(itemCopiar);
        }

        if ((itemNuevo.getTipitm().equals("P") || itemNuevo.getTipitm().equals("C"))
                && movimiento.getCircuito().getActualizaStock().equals("S")) {
            generarItemDetalle(itemNuevo, itemCopiar);
        }

        movimiento.getItemsMovimiento().add((ItemMovimientoProduccion) itemNuevo);

    }

    public void generarItemFromItemPendiente(MovimientoProduccion movimiento, PendienteProduccionDetalle itemPendiente, ItemMovimientoProduccion itemNuevo) throws ExcepcionGeneralSistema {

        itemNuevo.setProducto(itemPendiente.getProducto());
        itemNuevo.setProductoOriginal(itemPendiente.getProducto());
        itemNuevo.setUnidadMedida(itemPendiente.getUnidadMedida());

        if (itemPendiente.getCantidad() != null && itemPendiente.getCantidad().compareTo(BigDecimal.ZERO) > 0) {
            itemNuevo.setCantidad(itemPendiente.getCantidad());
            itemNuevo.setCantidadPendiente(itemPendiente.getCantidad());
            itemNuevo.setCantidadOriginal(itemPendiente.getCantidad());
            itemNuevo.setCantidadStock(itemPendiente.getCantidad());
        } else {
            itemNuevo.setCantidad(itemPendiente.getPendiente());
            itemNuevo.setCantidadOriginal(itemPendiente.getPendiente());
            itemNuevo.setCantidadStock(itemPendiente.getPendiente());
        }

        itemNuevo.setActualizaStock(itemPendiente.getStocks());
        itemNuevo.setGrupo(itemPendiente.getGrupo());

        if (itemNuevo.getTipitm().equals("P") && itemPendiente.getFormul() != null && !itemPendiente.getFormul().isEmpty()) {

            ComposicionFormula composicionFormula = composicionFormulaRN.getComprosicionFormula(itemPendiente.getArtcod(), itemPendiente.getFormul());
            itemNuevo.setComposicionFormula(composicionFormula);

        }

        itemNuevo.setTodoOk(true);

        //Verificamos si el circuito aplica a items pendientes
        if (movimiento.getCircuito().getNoCancelaPendiente().equals("N")) {
            ItemMovimientoProduccion itemAplicado = produccionDAO.getItem(itemPendiente.getIdIapl());
            ((ItemMovimientoProduccion) itemNuevo).setItemAplicado(itemAplicado);
        }

        if ((itemNuevo.getTipitm().equals("P") || itemNuevo.getTipitm().equals("C"))
                && movimiento.getCircuito().getActualizaStock().equals("S")) {
            generarItemDetalle((ItemMovimientoProduccion) itemNuevo, null);
        }

        movimiento.getItemsMovimiento().add((ItemMovimientoProduccion) itemNuevo);

    }

    /**
     * Generamos los items producto del movimientos en base a los items
     * pendientes
     *
     * @param m Movimiento de produccción
     * @param itemsPendiente items pendientes necesarios para generar el
     * movimiento
     * @throws ExcepcionGeneralSistema
     *
     */
    public void generarItemsFromPendiente(MovimientoProduccion m, List<PendienteProduccionDetalle> itemsPendiente) throws ExcepcionGeneralSistema {

        if (itemsPendiente.isEmpty()) {
            return;
        }

        int cantSel = 0;
        for (PendienteProduccionDetalle itemPendiente : itemsPendiente) {

            if (itemPendiente.isSeleccionado()) {

                cantSel++;
                ItemMovimientoProduccion nItem = nuevoItem(m, itemPendiente.getTipitm());
                generarItemFromItemPendiente(m, itemPendiente, nItem);
            }
        }
        if (cantSel == 0) {
            throw new ExcepcionGeneralSistema("No ha seleccionado ningún producto");
        }
    }

    /**
     * Generamos los items producto del movimientos en base a los items del
     * movmiento original
     *
     * @param movimiento Movimiento de produccción
     * @param itemsMovimiento items pendientes necesarios para generar el
     * movimiento
     * @throws ExcepcionGeneralSistema
     *
     */
    public void generarItemsFromItemMovimiento(MovimientoProduccion movimiento, List<ItemMovimientoProduccion> itemsMovimiento) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (itemsMovimiento.isEmpty()) {
            return;
        }

        int cantSel = 0;
        for (ItemMovimientoProduccion item : itemsMovimiento) {

            //Si tieen cantidad cero no genera el item
            if (item.getProduccion() != null && item.getProduccion().compareTo(BigDecimal.ZERO) > 0) {

                if (item.getTipitm().equals("P") || item.getTipitm().equals("C")) {
                    sErrores += controlCantidadesItemsDetalle(item, item.getItemDetalleTemporal());
                }

                cantSel++;
                ItemMovimientoProduccion nItem = nuevoItem(movimiento, item.getTipitm());
                generarItemFromItemMovimiento(movimiento, item, nItem);
            }
        }

        if (cantSel == 0) {
            sErrores += "No ha seleccionado ningún producto para el comprobante " + movimiento.getFormulario().getDescripcion() + "\n";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void generarItemDetalle(ItemMovimientoProduccion item, ItemMovimientoProduccion itemCopiar) throws ExcepcionGeneralSistema {

        if (item.getTipitm().equals("R") || item.getTipitm().equals("H")) {
            return;
        }

        if (item.getItemDetalle() == null) {
            item.setItemDetalle(new ArrayList<ItemDetalleMovimientoProduccion>());
        }

        if (itemCopiar == null || itemCopiar.getItemDetalleTemporal() == null || itemCopiar.getItemDetalleTemporal().isEmpty()) {

            ItemDetalleMovimientoProduccion itemDetalle = nuevoItemDetalle(item);
            item.getItemDetalle().add(itemDetalle);

        } else {

            for (ItemDetalleMovimientoProduccion itemDetalleCopiar : itemCopiar.getItemDetalleTemporal()) {

                ItemDetalleMovimientoProduccion itemDetalle = nuevoItemDetalleFromItem(item, itemDetalleCopiar);
                item.getItemDetalle().add(itemDetalle);
            }
        }
    }

    //-------------------------------------------------------------------------------------------
    public void agregarItemDetalle(ItemMovimientoProduccion nItem) {

        ItemDetalleMovimientoProduccion nItemD = nuevoItemDetalle(nItem);
        nItemD.setCantidad(BigDecimal.ZERO);
        nItem.getItemDetalle().add(nItemD);

    }

    public void agregarItemDetalleTemporalProducto(ItemMovimientoProduccion nItem) {

        ItemDetalleMovimientoProduccion nItemD = nuevoItemDetalle(nItem);
        nItemD.setCantidad(BigDecimal.ZERO);
        nItem.getItemDetalleTemporal().add(nItemD);

    }

    //------------------------------------------------------------------------------------------------------------------
    public void agregarItem(MovimientoProduccion movimiento, String tipitm, Producto producto, BigDecimal cantidad) throws ExcepcionGeneralSistema {
        try {

            String sError = puedoAgregarItem(movimiento, producto, cantidad);

            if (!sError.isEmpty()) {
                throw new ExcepcionGeneralSistema(sError);
            }

            ItemMovimientoProduccion item = movimiento.getItemsMovimiento().get(movimiento.getItemsMovimiento().size() - 1);

            if (item == null) {
                item = nuevoItem(movimiento, tipitm);
            }

            item.setProducto(producto);
            item.setProductoOriginal(producto);
            item.setUnidadMedida(producto.getUnidadDeMedida());
            item.setActualizaStock(producto.getGestionaStock());
            item.setCantidad(cantidad);
            item.setCantidadStock(cantidad);
            item.setCantidadOriginal(cantidad);

            for (ItemDetalleMovimientoProduccion id : item.getItemDetalle()) {

                id.setCantidad(item.getCantidad());
                id.setUnidadMedida(item.getUnidadMedida());

                id.setAtributo1(item.getAtributo1());
                id.setAtributo2(item.getAtributo2());
                id.setAtributo3(item.getAtributo3());
                id.setAtributo4(item.getAtributo4());
                id.setAtributo5(item.getAtributo5());
                id.setAtributo6(item.getAtributo6());
                id.setAtributo7(item.getAtributo7());
            }

            //Si es comprobante de Orden de producción, agregamos los componentes
            if (movimiento.getCircuito().getTipoMovimiento() == TipoMovimientoProduccion.OP) {

                agregarComponentesYProcesos(movimiento, item);
            }

            //Cargarmos un nuevo item en blanco
            item.setTodoOk(true);
            movimiento.getItemsMovimiento().add(nuevoItem(movimiento, tipitm));

        } catch (Exception ex) {
            throw new ExcepcionGeneralSistema("No es posible agregar item " + ex);
        }
    }

    public void asignarProducto(ItemMovimientoProduccion itemProducto, Producto producto) throws ExcepcionGeneralSistema {

        if (itemProducto.getMovimiento().getMonedaSecundaria() == null) {
            JsfUtil.addWarningMessage("El comprobante no tiene una moneda secundaria asignada");
            return;
        }

        itemProducto.setProducto(producto);
        itemProducto.setProductoOriginal(producto);
        itemProducto.setGrupo(producto.getCodigo());
        itemProducto.setUnidadMedida(producto.getUnidadDeMedida());
        itemProducto.setActualizaStock(producto.getGestionaStock());

        //Si es comprobante de Orden de producción, agregamos los componentes
        if (itemProducto.getTipitm().equals("P")
                && itemProducto.getMovimiento().getCircuito().getTipoMovimiento() == TipoMovimientoProduccion.OP) {

            agregarComponentesYProcesos(itemProducto.getMovimiento(), itemProducto);
        }
    }

    public void asignarFormula(ItemMovimientoProduccion itemProducto, Formula formula) throws ExcepcionGeneralSistema {

        itemProducto.setFormula(formula);

        //Si es comprobante de Orden de producción, agregamos los componentes
        if (itemProducto.getMovimiento().getCircuito().getTipoMovimiento() == TipoMovimientoProduccion.OP) {

            agregarComponentesYProcesos(itemProducto.getMovimiento(), itemProducto);
        }
    }

    public String puedoAgregarItem(MovimientoProduccion movimiento, Producto producto, BigDecimal cantidad) {

        String sError = "";

        if ((movimiento.getCircuito().getItemUnico().equals("S")) && (movimiento.getItemsMovimiento().size() > 1)) {
            sError += "Ha superado la cantidad máxima de items, no puede continuar agregando\n";

        }

        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            sError += "Ingrese un valor de cantidad válido. Mayor a 0\n";

        }

        if (producto == null) {
            sError += "Seleccione un producto para agregar al comprobante\n";

        }

        return sError;
    }

    public void agregarComponentesYProcesos(MovimientoProduccion movimiento, ItemMovimientoProduccion itemProducto) throws ExcepcionGeneralSistema {

        if (itemProducto.getFormula() == null) {
            return;
        }

        if (itemProducto.getProducto() == null) {
            return;
        }

        ComposicionFormula composicionFormula = composicionFormulaRN.getComprosicionFormula(itemProducto.getProducto().getCodigo(), itemProducto.getFormula().getCodigo());
        itemProducto.setComposicionFormula(composicionFormula);

        if (composicionFormula != null) {

            if (composicionFormula.getItemsComponente() == null && composicionFormula.getItemsProceso() == null) {

                throw new ExcepcionGeneralSistema("La formula del producto seleccionado no contiene componentes ni procesos ");

            } else {

                if (composicionFormula.getItemsComponente() != null) {

                    movimiento.getItemsMovimiento().removeIf(i -> (i.getTipitm().equals("C")));

                    for (ItemComposicionFormulaComponente i : composicionFormula.getItemsComponente()) {

                        ItemMovimientoProduccion itmComp = nuevoItem(movimiento, "C");
                        BigDecimal cntNominal = i.getCantidadNominal();

                        itmComp.setTipitm("C");
                        itmComp.setGrupo(itemProducto.getGrupo());
                        itmComp.setProducto(i.getProductoComponente());
                        itmComp.setProductoOriginal(i.getProductoComponente());

                        itmComp.setCantidadNominal(cntNominal);
                        itmComp.setCantidad(itemProducto.getCantidad().multiply(cntNominal));
                        itmComp.setCantidadStock(itemProducto.getCantidad().multiply(cntNominal));
                        itmComp.setCantidadOriginal(itemProducto.getCantidad().multiply(cntNominal));

                        itmComp.setUnidadMedida(i.getProductoComponente().getUnidadDeMedida());
                        itmComp.setActualizaStock(i.getProductoComponente().getGestionaStock());

                        movimiento.getItemsMovimiento().add(itmComp);
                    }
                }

                if (composicionFormula.getItemsProceso() != null) {

                    movimiento.getItemsMovimiento().removeIf(i -> (i.getTipitm().equals("R")));

                    for (ItemComposicionFormulaProceso i : composicionFormula.getItemsProceso()) {

                        ItemMovimientoProduccion itmComp = nuevoItem(movimiento, "R");
                        BigDecimal cntNominal = i.getCantidadNominal();

                        itmComp.setTipitm("R");
                        itmComp.setGrupo(itemProducto.getGrupo());
                        itmComp.setProducto(i.getProductoComponente());
                        itmComp.setProductoOriginal(i.getProductoComponente());

                        itmComp.setCantidadNominal(cntNominal);
                        itmComp.setCantidad(itemProducto.getCantidad().multiply(cntNominal));
                        itmComp.setCantidadStock(itemProducto.getCantidad().multiply(cntNominal));
                        itmComp.setCantidadOriginal(itemProducto.getCantidad().multiply(cntNominal));

                        itmComp.setUnidadMedida(i.getProductoComponente().getUnidadDeMedida());
                        itmComp.setActualizaStock(i.getProductoComponente().getGestionaStock());

                        movimiento.getItemsMovimiento().add(itmComp);
                    }
                }
            }

        } else {
            throw new ExcepcionGeneralSistema("El producto (" + itemProducto.getProducto().getCodigo() + "-" + itemProducto.getProducto().getCodigo() + ") seleccionado no tiene una fórmula de producción definida");
        }
    }

    public void generarDatosAdicionales(MovimientoProduccion m) throws ExcepcionGeneralSistema, Exception {

        sincronizarCantidades(m);
        //generarItemsDetallesVacio(m);

    }

    public void controlComprobante(MovimientoProduccion movimiento) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        if (movimiento.getId() != null) {
            if (movimiento.getFormulario().getModfor().equals("PD")) {
                sErrores += "- No es posible modificar un comprobante de producción \n";
            }

            if (movimiento.getFormulario().getModfor().equals("ST")) {
                sErrores += "- No es posible modificar un comprobante de stock \n";
            }
        }

        if (movimiento.getTipoMovimiento().equals(TipoMovimientoProduccion.OP)) {

            if (movimiento.getItemsMovimiento().isEmpty()) {
                sErrores += "- El detalle de productos en " + movimiento.getFormulario().getDescripcion() + ", está vacío, no se puede guardar\n";
            }
        }

        if (movimiento.getTipoMovimiento().equals(TipoMovimientoProduccion.PP)) {

            if (movimiento.getItemsMovimiento().isEmpty()) {
                sErrores += "- El detalle de productos en " + movimiento.getFormulario().getDescripcion() + ", está vacío, no se puede guardar\n";
            }
        }

        if (movimiento.getPlanta() == null) {
            sErrores += "- No se definió la planta de producción en " + movimiento.getFormulario().getDescripcion() + "\n";
        }

        // Controlamos los items producto
        if (movimiento.getItemsMovimiento() != null && !movimiento.getItemsMovimiento().isEmpty()) {

            for (ItemMovimientoProduccion i : movimiento.getItemsMovimiento()) {

                i.setConError(false);

                if (i.getCantidad() == null || i.getCantidad().compareTo(BigDecimal.ZERO) == 0) {
                    i.setConError(true);
                    sErrores += "- El item " + i.getProducto().getDescripcion() + " no tiene un dato válido en cantidad\n";
                }

                if (i.getActualizaStock() == null) {
                    i.setConError(true);
                    sErrores += "- El item " + i.getProducto().getDescripcion() + " no tiene definido la actualización de stock\n";
                }

                if ((i.getTipitm().equals("P") || i.getTipitm().equals("C"))
                        && movimiento.getCircuito().getActualizaStock().equals("S")) {

                    if (i.getItemDetalle() == null || i.getItemDetalle().isEmpty()) {
                        i.setConError(true);
                        sErrores += "- El item " + i.getProducto().getDescripcion() + " no tiene items de apertura\n";
                    }

                    sErrores += controlItemsDetalle(i);
                }

                if ((i.getTipitm().equals("R") || i.getTipitm().equals("H"))
                        && !movimiento.getTipoMovimiento().equals(TipoMovimientoProduccion.OP)) {
                    if (i.getOperario() == null) {
                        i.setConError(true);
                        sErrores += "- No ingresó operario en item " + i.getProducto().getDescripcion() + "\n";
                    }
                }

            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void actualizarCantidades(MovimientoProduccion movimiento) throws ExcepcionGeneralSistema {

        if (movimiento.getItemsProducto() != null && !movimiento.getItemsProducto().isEmpty()) {

            for (ItemMovimientoProduccion item : movimiento.getItemsProducto()) {

                item.setCantidadStock(item.getCantidad());
                //Actualizamos la cantidad original solo si es un movimiento directo
                if (movimiento.getCircuito().getCircom().equals(movimiento.getCircuito().getCirapl())) {
                    item.setCantidadOriginal(item.getCantidad());
                }

                //Si es una hoja de ruta vaciamos y volvemos a cargar los componentes
                if (movimiento.getCircuito().getTipoMovimiento() == TipoMovimientoProduccion.OP) {

                    movimiento.getItemsComponente().clear();
                    agregarComponentesYProcesos(movimiento, item);
                }

                sincronizarCantidadesItemDetalle(item);
            }
        }

        if (movimiento.getItemsComponente() != null && !movimiento.getItemsComponente().isEmpty()) {

            for (ItemMovimientoProduccion item : movimiento.getItemsComponente()) {

                item.setCantidadStock(item.getCantidad());
                //Actualizamos la cantidad original solo si es un movimiento directo
                if (movimiento.getCircuito().getCircom().equals(movimiento.getCircuito().getCirapl())) {
                    item.setCantidadOriginal(item.getCantidad());
                }

                sincronizarCantidadesItemDetalle(item);
            }
        }
    }

    public void sincronizarCantidadesItemDetalle(ItemMovimientoProduccion i) {

        if (i.getItemDetalle() != null) {
            if (i.getItemDetalle().size() == 1) {
                i.getItemDetalle().get(0).setCantidad(i.getCantidad());
            }
        }
    }

    public void actualizarCantidades(MovimientoProduccion movimiento, ItemMovimientoProduccion nItem) throws ExcepcionGeneralSistema {

        nItem.setCantidadStock(nItem.getCantidad());

        //Actualizamos la cantidad original solo si es un movimiento directo
        if (movimiento.getCircuito().getCircom().equals(movimiento.getCircuito().getCirapl())) {
            nItem.setCantidadOriginal(nItem.getCantidad());
        }

        //Si es una orden de produccion de ruta vaciamos y volvemos a cargar los componentes
        if (movimiento.getCircuito().getTipoMovimiento() == TipoMovimientoProduccion.OP) {

            agregarComponentesYProcesos(movimiento, nItem);
        }

        if (movimiento.getCircuito().getAutomatizaParteProduccion().equals("S")) {

            ComposicionFormula composicionFormula = composicionFormulaRN.getComprosicionFormula(nItem.getProducto().getCodigo(), nItem.getFormula().getCodigo());

            if (composicionFormula != null) {
                if (composicionFormula.getItemsComponente() == null) {
                    JsfUtil.addWarningMessage("La formula del producto seleccionado no contiene componentes");

                } else {
                    for (ItemComposicionFormula i : composicionFormula.getItemsComponente()) {

                        if (movimiento.getValeConsumo() != null) {

                            //Actualizamos la cantidad para los items materia prima
                            for (ItemMovimientoProduccion ivc : movimiento.getValeConsumo().getItemsMovimiento()) {

                                if (ivc.getProducto().equals(i.getProductoComponente())) {

                                    ivc.setCantidad(nItem.getCantidad().multiply(i.getCantidadNominal()));
                                    ivc.setCantidadStock(nItem.getCantidad().multiply(i.getCantidadNominal()));

//                                    actualizarAtributos(ivc);
                                }
                            }
                        }

                        //Actualizamos la cantidad para los items proceso
                        if (movimiento.getParteProceso() != null) {

                            for (ItemMovimientoProduccion ipp : movimiento.getParteProceso().getItemsMovimiento()) {

                                if (ipp.getProducto().equals(i.getProductoComponente())) {

                                    ipp.setCantidad(nItem.getCantidad().multiply(i.getCantidadNominal()));
                                    ipp.setCantidadStock(nItem.getCantidad().multiply(i.getCantidadNominal()));

//                                    actualizarAtributos(ipp);
                                }
                            }
                        }
                    }
                }
            } else {
                JsfUtil.addWarningMessage("El producto seleccionado no tiene una fórmula de producción definida");
            }
        }

//        actualizarAtributos(nItem);
    }

    /**
     * Actualizamos los atributos de stock, por el momento solo maneja nro de
     * serie
     *
     * @param item
     */
    public void actualizarAtributosItems(ItemMovimientoProduccion item) {

        if (!item.getTipitm().equals("P") && !item.getTipitm().equals("C")) {
            return;
        }

        if (item.getItemDetalle() == null || item.getItemDetalle().isEmpty()) {
            ItemDetalleMovimientoProduccion nuevoItemDetalle = nuevoItemDetalle(item);
        }

        for (ItemDetalleMovimientoProduccion id : item.getItemDetalle()) {

            id.setCantidad(item.getCantidad());

            if (item.getAtributo1() != null && !item.getAtributo1().isEmpty()) {
                id.setAtributo1(item.getAtributo1());
            }
            if (item.getAtributo2() != null && !item.getAtributo2().isEmpty()) {
                id.setAtributo2(item.getAtributo2());
            }
            if (item.getAtributo3() != null && !item.getAtributo3().isEmpty()) {
                id.setAtributo3(item.getAtributo3());
            }
            if (item.getAtributo3() != null && !item.getAtributo3().isEmpty()) {
                id.setAtributo3(item.getAtributo3());
            }
            if (item.getAtributo4() != null && !item.getAtributo4().isEmpty()) {
                id.setAtributo4(item.getAtributo4());

            }
            if (item.getAtributo5() != null && !item.getAtributo5().isEmpty()) {
                id.setAtributo5(item.getAtributo5());
            }
            if (item.getAtributo6() != null && !item.getAtributo6().isEmpty()) {
                id.setAtributo6(item.getAtributo6());
            }
            if (item.getAtributo7() != null && !item.getAtributo7().isEmpty()) {
                id.setAtributo7(item.getAtributo7());
            }
        }

    }

    public void sincronizarCantidades(MovimientoProduccion m) {

        if (m.getItemsMovimiento() != null) {

            for (ItemMovimientoProduccion i : m.getItemsMovimiento()) {

                if (i.getCantidad() == null || i.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }
                i.setCantidadPendiente(i.getCantidad());
                i.setCantidadStock(i.getCantidad());
                i.setCantidadOriginal(i.getCantidad());
            }
        }
    }

    public void actualizarCantidadesPendientesFromTarea(Tarea tarea) {

        if (tarea.getMovimientosProduccion() != null) {

            for (MovimientoProduccion m : tarea.getMovimientosProduccion()) {
                if (m.getId() == null) {
                    //tareaDAO.crear(m);
                    actualizarCantidadesPendientes(m);
                }
            }
        }
    }

    public void actualizarCantidadesPendientes(MovimientoProduccion m) {

        if (m.getItemsMovimiento() != null) {

            for (ItemMovimientoProduccion i : m.getItemsMovimiento()) {

                if (i.getCantidad() == null || i.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }
                if (i.getItemAplicado() != null) {
                    BigDecimal aplicadoActual = produccionDAO.getCantidadAplicadaItem(i.getItemAplicado().getId());
                    BigDecimal pendiente = i.getItemAplicado().getCantidad().add(aplicadoActual.negate());
                    i.getItemAplicado().setCantidadPendiente(pendiente);
                    produccionDAO.editar(i.getItemAplicado());
                }
            }
        }
    }

    public void reorganizarNroItem(MovimientoProduccion movimiento) {

        //Reorganizamos los números de items
        int i = 1;

        if (movimiento.getItemsMovimiento() != null) {
            for (ItemMovimientoProduccion ip : movimiento.getItemsMovimiento()) {
                ip.setNroitm(i);
                i++;
                int d = 0;

                if (ip.getItemDetalle() != null) {
                    for (ItemDetalleMovimientoProduccion itemDetalle : ip.getItemDetalle()) {
                        itemDetalle.setNroitm(d);
                        d++;
                    }
                }
            }
        }
    }

    public boolean tengoItemsSeleccionados(List<PendienteProduccionDetalle> itemsPendientes) {

        if (itemsPendientes == null || itemsPendientes.isEmpty()) {
            return false;
        }

        for (PendienteProduccionDetalle p : itemsPendientes) {

            if (p.isSeleccionado()) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public boolean tengoItemsConCantidad(CircuitoProduccion circuito, Object itemsPendientes) {

        if (itemsPendientes == null || ((List<ItemMovimientoProduccion>) itemsPendientes).isEmpty()) {
            return false;
        }

        if (circuito.getNoControlaPendiente().equals("N")) {
            return true;
        }

        for (ItemMovimientoProduccion i : (List<ItemMovimientoProduccion>) itemsPendientes) {

            if (i.getCantidadPendiente() != null && i.getCantidadPendiente().compareTo(BigDecimal.ZERO) > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean tengoItemsConCantidad(Object itemsPendientes) {

        if (itemsPendientes == null || ((List<ItemMovimientoProduccion>) itemsPendientes).isEmpty()) {
            return false;
        }

        for (ItemMovimientoProduccion i : (List<ItemMovimientoProduccion>) itemsPendientes) {

            if (i.getProduccion() != null && i.getProduccion().compareTo(BigDecimal.ZERO) > 0) {
                return true;
            }

            if (i.getTipitm().equals("P") || i.getTipitm().equals("C")) {

                for (ItemDetalleMovimientoProduccion d : i.getItemDetalleTemporal()) {

                    if (d.getCantidad().compareTo(BigDecimal.ZERO) > 0) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public void seleccionarTodo(List<PendienteProduccionDetalle> itemsPendiente, boolean seleccionarTodo) {

        if (itemsPendiente == null) {
            return;
        }

        itemsPendiente.forEach(i -> i.setSeleccionado(seleccionarTodo));

    }

    public void ponerItemsDetalleEnCero(MovimientoProduccion movimiento) {

        if (movimiento.getItemsMovimiento() != null) {

            for (ItemMovimientoProduccion i : movimiento.getItemsMovimiento()) {

                if (i.getTipitm().equals("P") || i.getTipitm().equals("C")) {

                    if (i.getItemDetalle() != null) {

                        for (ItemDetalleMovimientoProduccion item : i.getItemDetalle()) {
                            item.setCantidad(BigDecimal.ZERO);
                        }
                    }
                }

            }
        }
    }

    public void asignarFormulario(MovimientoProduccion m) throws ExcepcionGeneralSistema {

        Formulario formulario = null;
        //Buscamos el formulario correspondiente
        if (m.getCircuito().getActualizaProduccion().equals("S")) {

            formulario = formularioRN.getFormulario(m.getComprobante(), m.getPuntoVenta(), "X");

        } else if (m.getCircuito().getActualizaStock().equals("S")) {

            formulario = formularioRN.getFormulario(m.getComprobante(), m.getPuntoVentaStock(), "X");
        }

        if (formulario != null) {

            m.setNumeroFormulario(formulario.getUltimoNumero() + 1);
            m.setFormulario(formulario);
        }
    }

    public void tomarNumeroFormulario(MovimientoProduccion m) throws ExcepcionGeneralSistema {

        if (m.getComprobanteStock() != null && m.getMovimientoStock() != null) {

            if (m.getMovimientoStock().getFormulario().getTipoNumeracion().equals("A")) {
                m.getMovimientoStock().setNumeroFormulario(formularioRN.tomarProximoNumero(m.getMovimientoStock().getFormulario()));
            }
            m.setNumeroFormulario(m.getMovimientoStock().getNumeroFormulario());

        } else {
            m.setNumeroFormulario(formularioRN.tomarProximoNumero(m.getFormulario()));
        }
    }

    public String controlItemsDetalle(ItemMovimientoProduccion item) throws Exception {

        if (!item.getTipitm().equals("P") && !item.getTipitm().equals("C")) {
            return "";
        }

        String sErrores = "";

        ParametroStock parametroStock = parametroStockRN.getParametro();
        //variable temporal para comparar la suma de los items
        BigDecimal cantItems = BigDecimal.ZERO;

        for (ItemDetalleMovimientoProduccion itemDetalle : item.getItemDetalle()) {

            //Acumulamos las cantidades
            cantItems = cantItems.add(itemDetalle.getCantidad().setScale(2, BigDecimal.ROUND_HALF_DOWN));

            if (itemDetalle.getCantidad().equals(BigDecimal.ZERO)) {
                item.setConError(true);
                sErrores += "- No puede tener un item de apertura con cantidad cero en " + item.getProducto().getDescripcion() + "\n";
            }

            //Si el circuito define que controla atributos los validamos
            if (item.getMovimiento().getCircuito().getAdministraAtributo1().equals("S")
                    && itemDetalle.getProducto().getAdministraAtributo1().equals("S")) {

                if (itemDetalle.getAtributo1() == null || itemDetalle.getAtributo1().isEmpty()) {
                    item.setConError(true);
                    sErrores += "- Ingrese el atributo 1 (" + parametroStock.getDescripcionAtributo1() + ") para el producto " + itemDetalle.getProducto().getDescripcion() + "\n";
                }
            }

            if (item.getMovimiento().getCircuito().getAdministraAtributo2().equals("S")
                    && itemDetalle.getProducto().getAdministraAtributo2().equals("S")) {
                if (itemDetalle.getAtributo2() == null || itemDetalle.getAtributo2().isEmpty()) {
                    item.setConError(true);
                    sErrores += "- Ingrese el atributo 2 (" + parametroStock.getDescripcionAtributo2() + ") para el producto " + itemDetalle.getProducto().getDescripcion() + "\n";
                }
            }

            if (item.getMovimiento().getCircuito().getAdministraAtributo3().equals("S")
                    && itemDetalle.getProducto().getAdministraAtributo3().equals("S")) {
                if (itemDetalle.getAtributo3() == null || itemDetalle.getAtributo3().isEmpty()) {
                    item.setConError(true);
                    sErrores += "- Ingrese el atributo 3 (" + parametroStock.getDescripcionAtributo3() + ") para el producto " + itemDetalle.getProducto().getDescripcion() + "\n";
                }
            }

            if (item.getMovimiento().getCircuito().getAdministraAtributo4().equals("S")
                    && itemDetalle.getProducto().getAdministraAtributo4().equals("S")) {
                if (itemDetalle.getAtributo4() == null || itemDetalle.getAtributo4().isEmpty()) {
                    item.setConError(true);
                    sErrores += "- Ingrese el atributo 4 (" + parametroStock.getDescripcionAtributo4() + ") para el producto " + itemDetalle.getProducto().getDescripcion() + "\n";
                }
            }
        }

        if (!item.getCantidad().setScale(2, BigDecimal.ROUND_FLOOR).equals(cantItems.setScale(2, BigDecimal.ROUND_FLOOR))) {

            item.setConError(true);
            sErrores += "- No coincide la cantidad (" + item.getCantidad() + ") de " + item.getProducto().getDescripcion()
                    + " con la sumatoria en de los items de apertura (" + cantItems + ")\n";
        }

        return sErrores;

    }

    /**
     * Se controla que la cantidad ingresada en el campo producción y la
     * cantidad de los items de apertura coincidan.
     *
     * @param itemOrden
     * @param itemsDetalle
     * @return
     * @throws Exception
     */
    public String controlCantidadesItemsDetalle(ItemMovimientoProduccion itemOrden,
            List<ItemDetalleMovimientoProduccion> itemsDetalle) throws Exception {

        String sErrores = "";
        ParametroStock parametroStock = parametroStockRN.getParametro();
        //variable temporal para comparar la suma de los items
        BigDecimal cantItems = BigDecimal.ZERO;

        for (ItemDetalleMovimientoProduccion itemDetalle : itemsDetalle) {

            //Acumulamos las cantidades
            cantItems = cantItems.add(itemDetalle.getCantidad().setScale(2, BigDecimal.ROUND_HALF_DOWN));

        }

        if (!itemOrden.getProduccion().setScale(2, BigDecimal.ROUND_FLOOR).equals(cantItems.setScale(2, BigDecimal.ROUND_FLOOR))) {

            itemOrden.setConError(true);
            sErrores += "- No coincide la cantidad (" + itemOrden.getProduccion() + ") de " + itemOrden.getProducto().getDescripcion()
                    + " con la sumatoria en de los items de apertura (" + cantItems + ")\n";
        }

        return sErrores;

    }

    //-------------------------------------------------------------------------------------------
    /**
     * Eliminar un item de un movimiento
     *
     * @param movimiento movimiento del cual se eliminará el item
     * @param nItem item a eliminar
     */
    public boolean eliminarItem(MovimientoProduccion movimiento, ItemMovimientoProduccion nItem) {

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemProducto = -1;
        int indiceItemAplicacion = -1;

        for (ItemMovimientoProduccion ip : movimiento.getItemsMovimiento()) {

            if (ip.getNroitm() >= 0) {

                if (ip.getNroitm() == nItem.getNroitm()) {
                    indiceItemProducto = i;
                }
            }
        }
        i = 0;

        //Borramos los items productos
        if (indiceItemProducto >= 0) {
            ItemMovimientoProduccion remove = movimiento.getItemsMovimiento().remove(indiceItemProducto);
            if (remove != null) {

                if (movimiento.getId() != null && remove.getId() != null) {
                    produccionDAO.eliminar(ItemMovimientoProduccion.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        reorganizarNroItem(movimiento);
        return fItemBorrado;
    }

    public void eliminarMovimiento(MovimientoProduccion movimientoProduccion) throws Exception {

        produccionDAO.eliminar(MovimientoProduccion.class, movimientoProduccion.getId());
        stockRN.recalcularStock();
        //if(movimientoProduccion.getMovimientoStock()!=null){
        //    movimientoStockRN.eliminarMovimiento(movimientoProduccion.getMovimientoStock());
        //}
    }

    public boolean eliminarItemDetalle(ItemMovimientoProduccion ip, ItemDetalleMovimientoProduccion nItem) throws ExcepcionGeneralSistema {

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemProducto = -1;

        //Verificamos que siempre quede 1 items detalle
        if (ip.getItemDetalle().size() == 1) {

            throw new ExcepcionGeneralSistema("No es posible eliminar item de apertura, la cantidad mínima es un item");
        }

        //Buscamos el indice del item a borrar
        for (ItemDetalleMovimientoProduccion id : ip.getItemDetalle()) {

            if (id.getId() == null) {
                if (id.getNroitm() == nItem.getNroitm()) {
                    indiceItemProducto = i;
                }
            } else if (id.getId().equals(nItem.getId())) {
                indiceItemProducto = i;
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemProducto >= 0) {
            ItemDetalleMovimientoProduccion remove = ip.getItemDetalle().remove(indiceItemProducto);

            if (remove != null) {
                fItemBorrado = true;
            }
        }
        return fItemBorrado;
    }

    public boolean eliminarItemDetalleTemporal(ItemMovimientoProduccion ip, ItemDetalleMovimientoProduccion nItem) throws ExcepcionGeneralSistema {

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemProducto = -1;

        //Verificamos que siempre quede 1 items detalle
        if (ip.getItemDetalleTemporal().size() == 1) {

            throw new ExcepcionGeneralSistema("No es posible eliminar item de apertura, la cantidad mínima es un item");
        }

        //Buscamos el indice del item a borrar
        for (ItemDetalleMovimientoProduccion id : ip.getItemDetalleTemporal()) {

            if (id.getId() == null) {
                if (id.getNroitm() == nItem.getNroitm()) {
                    indiceItemProducto = i;
                }
            } else if (id.getId().equals(nItem.getId())) {
                indiceItemProducto = i;
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemProducto >= 0) {
            ItemDetalleMovimientoProduccion remove = ip.getItemDetalleTemporal().remove(indiceItemProducto);

            if (remove != null) {
                fItemBorrado = true;
            }
        }
        return fItemBorrado;
    }

    //-------------------------------------------------------------------------------------------
    // CONSULTAS
    //-------------------------------------------------------------------------------------------
    public MovimientoProduccion getMovimiento(Integer id) {

        return produccionDAO.getMovimiento(id);
    }

    public MovimientoProduccion getMovimiento(String codFormulario, Integer numeroFormulario) {

        return produccionDAO.getMovimiento(codFormulario, numeroFormulario);
    }

    public List<MovimientoProduccion> getListaByBusqueda(Map<String, String> filtro, boolean soloPendientes, int cantMax) {

        return produccionDAO.getListaByBusqueda(filtro, soloPendientes, cantMax);
    }

    public List<ItemMovimientoProduccion> getItemByGrupoSector(MovimientoProduccion op, String tipitm,
            String grupo, Sector sector) {

        List<ItemMovimientoProduccion> procesosSeleccionados = new ArrayList<>();

        for (ItemMovimientoProduccion iProceso : op.getItemsMovimiento()) {

            if (iProceso.getTipitm().equals(tipitm) && iProceso.getGrupo().equals(grupo)) {

                //Filtramos los proceso con cantidad menos o igual a 0.01
                if (iProceso.getCantidad().doubleValue() <= 0.01) {
                    continue;
                }

                if (iProceso.getProducto().getSectorProduccion() == null || sector == null) {
                    procesosSeleccionados.add(iProceso);
                } else if (iProceso.getProducto().getSectorProduccion().getCodigo().equals(sector.getCodigo())) {
                    procesosSeleccionados.add(iProceso);
                }
            }
        }
        return procesosSeleccionados;
    }

    //Cargamos y mostramos solo los productos asociados al producto a producir seleccionado.
    public List<ItemMovimientoProduccion> getItemByGrupo(MovimientoProduccion op, String tipitm, String grupo) throws ExcepcionGeneralSistema {

        List<ItemMovimientoProduccion> productoSeleccionados = new ArrayList<ItemMovimientoProduccion>();

        for (ItemMovimientoProduccion item : op.getItemsMovimiento()) {

            if (item.getTipitm().equals(tipitm) && item.getGrupo().equals(grupo)) {

                if (item.getTipitm().equals("P") || item.getTipitm().equals("C")) {

                    if (item.getItemDetalleTemporal() == null) {
                        item.setItemDetalleTemporal(new ArrayList<ItemDetalleMovimientoProduccion>());
                    }

                    ItemDetalleMovimientoProduccion itemDetalle = nuevoItemDetalle(item);
                    item.getItemDetalleTemporal().add(itemDetalle);
                }

                productoSeleccionados.add(item);
            }
        }
        return productoSeleccionados;
    }

    public PendienteProduccionGrupo getMovimientoPendiente(Map<String, String> filtro) {

        return produccionDAO.getPendienteGrupo(filtro);
    }

    public List<PendienteProduccionGrupo> getMovimientosPendiente(Map<String, String> filtro) {

        return produccionDAO.getPendientesGrupo(filtro);
    }

    public List<PendienteProduccionDetalle> getItemsPendiente(Map<String, String> filtro) {

        return produccionDAO.getPendientesDetalle(filtro);
    }

    public PendienteProduccionDetalle getItemPendiente(Map<String, String> filtro) {

        return produccionDAO.getPendienteDetalle(filtro);
    }

    public ItemMovimientoProduccion getItem(Integer id) {
        return produccionDAO.getItem(id);
    }

    public ItemMovimientoProduccion getItemByTarea(Tarea tarea, Producto producto) {

        return produccionDAO.getItemByTarea(tarea, null, producto);
    }

    public void sincronizarCantidadesItemDetalleTemporal(ItemMovimientoProduccion item) {

        if (item.getTipitm().equals("P") || item.getTipitm().equals("C")) {

            if (item.getItemDetalleTemporal() == null) {
                item.setItemDetalleTemporal(new ArrayList<ItemDetalleMovimientoProduccion>());
                ItemDetalleMovimientoProduccion itemDetalle = nuevoItemDetalle(item);
                item.getItemDetalleTemporal().add(itemDetalle);
            }

            if (item.getItemDetalleTemporal().size() == 1) {
                item.getItemDetalleTemporal().get(0).setCantidad(item.getProduccion());
            }
        }

    }

    public BigDecimal getCantidadRegistradaByTarea(Tarea tarea, Producto producto) {

        return produccionDAO.getCantidadRegistradaByTarea(tarea, producto);

    }

    //Cargamos las cantidades guardadas en la tarea a anular y las asinamos al los pendientes
    public void cargarCantidadesGuardadas(Tarea tarea, String tipitm, List<ItemMovimientoProduccion> itemsPendientes) {

        if (tarea.getTareaAnular() == null || tarea.getTareaAnular().getMovimientosProduccion() == null) {
            return;
        }

        for (MovimientoProduccion mp : tarea.getTareaAnular().getMovimientosProduccion()) {

            //System.err.println("mp " + mp.getFormulario().getCodigo() + " " + mp.getNumeroFormulario());
            for (ItemMovimientoProduccion im : mp.getItemsMovimiento()) {

                for (ItemMovimientoProduccion ip : itemsPendientes) {

//                    ip.setProduccion(BigDecimal.ZERO);
                    if (im.getTipitm().equals(ip.getTipitm()) && im.getProducto().equals(ip.getProducto()) && im.getGrupo().equals(ip.getGrupo())) {

//                        System.err.println("Producto " + im.getProducto().getDescripcion() + " " + im.getCantidad());
//                        System.err.println("Pendiente " + ip.getProducto().getDescripcion() + " " + ip.getCantidad());
                        ip.setProduccion(im.getCantidad());

                        for (ItemDetalleMovimientoProduccion id : im.getItemDetalle()) {

                            ip.setItemDetalleTemporal(new ArrayList<ItemDetalleMovimientoProduccion>());
                            ItemDetalleMovimientoProduccion itemDetalle = nuevoItemDetalle(ip);

                            itemDetalle.setAtributo1(id.getAtributo1());
                            itemDetalle.setAtributo2(id.getAtributo2());
                            itemDetalle.setAtributo3(id.getAtributo3());
                            itemDetalle.setCantidad(id.getCantidad());

                            ip.getItemDetalleTemporal().add(itemDetalle);
                        }
                    }
                }

                //Este item no estaba programado y fué agregado en la tarea
                if (mp.getTipoMovimiento().equals(TipoMovimientoProduccion.VC)
                        && tipitm.equals("C")
                        && im.getTipitm().equals("C")
                        && im.getItemAplicado() == null) {

                    ItemMovimientoProduccion itemComponente = new ItemMovimientoProduccion();
                    itemComponente.setTipitm("C");
                    itemComponente.setProducto(im.getProducto());
                    itemComponente.setProductoOriginal(im.getProducto());
                    itemComponente.setUnidadMedida(im.getProducto().getUnidadDeMedida());
                    itemComponente.setActualizaStock(im.getProducto().getGestionaStock());

                    itemComponente.setProduccion(im.getCantidad());

                    for (ItemDetalleMovimientoProduccion id : im.getItemDetalle()) {

                        itemComponente.setItemDetalleTemporal(new ArrayList<ItemDetalleMovimientoProduccion>());
                        ItemDetalleMovimientoProduccion itemDetalle = nuevoItemDetalle(itemComponente);

                        itemDetalle.setAtributo1(id.getAtributo1());
                        itemDetalle.setAtributo2(id.getAtributo2());
                        itemDetalle.setAtributo3(id.getAtributo3());
                        itemDetalle.setCantidad(id.getCantidad());

                        itemComponente.getItemDetalleTemporal().add(itemDetalle);
                    }

                    itemsPendientes.add(itemComponente);
                }

            }

        }

    }

}
