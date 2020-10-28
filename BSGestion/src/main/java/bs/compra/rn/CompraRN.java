/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.compra.rn;

import bs.administracion.rn.ModuloRN;
import bs.administracion.rn.ParametrosRN;
import bs.compra.dao.CompraDAO;
import bs.compra.dao.ItemCompraDAO;
import bs.compra.modelo.CircuitoCompra;
import bs.compra.modelo.ItemDetalleItemMovimientoCompra;
import bs.compra.modelo.ItemMovimientoCompra;
import bs.compra.modelo.MovimientoCompra;
import bs.compra.vista.PendienteCompraDetalle;
import bs.compra.vista.PendienteCompraGrupo;
import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.EntidadComercial;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.ImpuestoPorConcepto;
import bs.global.modelo.Moneda;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.FormularioRN;
import bs.global.rn.MonedaRN;
import bs.global.rn.PuntoVentaRN;
import bs.proveedores.modelo.ItemImpuestoProveedor;
import bs.proveedores.modelo.ItemPercepcionProveedor;
import bs.proveedores.modelo.ItemProductoProveedor;
import bs.proveedores.modelo.ItemTotalProveedor;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.proveedores.rn.CondicionPagoProveedorRN;
import bs.proveedores.rn.ListaCostoRN;
import bs.proveedores.rn.MovimientoProveedorRN;
import bs.stock.dao.ProductoDAO;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.MovimientoStock;
import bs.stock.modelo.ParametroStock;
import bs.stock.modelo.Producto;
import bs.stock.rn.MovimientoStockRN;
import bs.stock.rn.ParametroStockRN;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.tesoreria.rn.MovimientoTesoreriaRN;
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
public class CompraRN implements Serializable {

    @EJB
    private ParametroStockRN parametroStockRN;
    @EJB
    private MonedaRN monedaRN;
    @EJB
    private CompraDAO compraDAO;
    @EJB
    private ItemCompraDAO itemCompraDAO;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private MovimientoProveedorRN proveedorRN;
    @EJB
    private MovimientoTesoreriaRN tesoreriaRN;
    @EJB
    private MovimientoStockRN movimientoStockRN;
    @EJB
    private ProductoDAO productoDAO;
    @EJB
    protected ParametrosRN parametrosRN;
    @EJB
    protected PuntoVentaRN puntoVentaRN;
    @EJB
    protected CondicionPagoProveedorRN condicionPagoProveedorRN;
    @EJB
    protected ListaCostoRN listaCostoRN;
    @EJB
    protected ModuloRN moduloRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized MovimientoCompra guardar(MovimientoCompra movimiento) throws Exception {

        sincronizarCantidades(movimiento);
        calcularImportes(movimiento, "U");
        controlComprobante(movimiento);

        if (movimiento.getId() == null) {

            generarMovimientosAdicionales(movimiento);
            tomarNumeroFormulario(movimiento);
            compraDAO.crear(movimiento);

        } else {
            movimiento = compraDAO.editar(movimiento);

        }

        actualizarPreciosUltimaCompra(movimiento);
        actualizarCantidadesPendientes(movimiento);
        movimientoStockRN.generarStock(movimiento.getMovimientoStock());
        return movimiento;
    }

    public MovimientoCompra nuevoMovimiento() {
        return new MovimientoCompra();
    }

    public MovimientoCompra nuevoMovimiento(CircuitoCompra circuito,
            String codCO, String sucCO,
            String codPV, String sucPV,
            String codST, String sucST,
            String codCJ, String sucCJ) throws ExcepcionGeneralSistema, Exception {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("El circuito no puede ser nulo");
        }

        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(sucCO);
        PuntoVenta puntoVentaStock = puntoVentaRN.getPuntoVenta(sucST);

        if (puntoVentaStock == null) {
            puntoVentaStock = puntoVenta;
        }

        if (puntoVentaStock != null && puntoVenta == null) {
            puntoVenta = puntoVentaStock;
        }

        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No se encontró punto de venta de compra " + sucCO);
        }

        Comprobante comprobante = null;
        Comprobante comprobanteCJ = null;
        Comprobante comprobanteST = null;

        //Siempre tiene que haber un comprobante de proveedor para los cálculos
        Comprobante comprobantePV = circuito.getComprobanteProveedor();

        if (circuito.getItemCircuitoProveedor() == null || circuito.getItemCircuitoProveedor().isEmpty()) {
            throw new ExcepcionGeneralSistema("El circuito debe tener configurado al menos un comprobante de proveedor");
        } else if (comprobantePV == null) {
            comprobantePV = circuito.getItemCircuitoProveedor().get(0).getComprobante();
        }

        if (circuito.getActualizaCompra().equals("S")) {

            comprobante = circuito.getComprobanteCompra();

        } else if (circuito.getActualizaProveedor().equals("S")) {

            comprobante = circuito.getComprobanteProveedor();

            if (circuito.getActualizaTesoreria().equals("S")) {

                comprobanteCJ = circuito.getComprobanteTesoreria();
            }

            if (circuito.getActualizaStock().equals("S")) {

                comprobanteST = circuito.getComprobanteStock();
            }

        } else if (circuito.getActualizaStock().equals("S")) {

            comprobante = circuito.getComprobanteStock();
            comprobanteST = circuito.getComprobanteStock();
        }

        MovimientoCompra movimiento = crearMovimiento(circuito, comprobante, puntoVenta, puntoVentaStock);

        if (comprobanteCJ != null) {
            movimiento.setComprobanteTesoreria(comprobanteCJ);
            MovimientoTesoreria mt = tesoreriaRN.generarComprobante(movimiento);
            movimiento.setMovimientoTesoreria(mt);
        }

        if (comprobantePV != null) {
            movimiento.setComprobanteProveedor(comprobantePV);
            proveedorRN.generarItemsMovimientoProveedor(movimiento);
        }

        if (comprobanteST != null) {
            movimiento.setComprobanteStock(comprobanteST);

            if (comprobanteST.getDeposito() != null) {
                movimiento.setDeposito(comprobanteST.getDeposito());
            }

            if (comprobanteST.getDepositoTransferencia() != null) {
                movimiento.setDepositoTransferencia(comprobanteST.getDepositoTransferencia());
            }
        }

        return movimiento;
    }

    public MovimientoCompra nuevoMovimiento(CircuitoCompra circuito,
            String codCO, String sucCO,
            String codPV, String sucPV,
            String codST, String sucST,
            String codCJ, String sucCJ,
            PendienteCompraGrupo pendienteGrupo,
            List<PendienteCompraDetalle> itemsPendientes) throws ExcepcionGeneralSistema, Exception {

        if (!tengoItemsSeleccionados(itemsPendientes)) {
            throw new ExcepcionGeneralSistema("No existen items seleccionados para generar el movimiento");
        }

        MovimientoCompra m = nuevoMovimiento(circuito, codCO, sucCO, codPV, sucPV, codST, sucST, codCJ, sucCJ);

        m.setProveedor(pendienteGrupo.getProveedor());
        m.setProveedorCuentaCorriente(pendienteGrupo.getProveedor());

        if (pendienteGrupo.getProveedor().getDireccionesDeEntrega() != null && !pendienteGrupo.getProveedor().getDireccionesDeEntrega().isEmpty()) {

            asignarDireccionEntrega(m, pendienteGrupo.getProveedor().getDireccionesDeEntrega().get(0));
        }

        m.setCondicionDeIva(pendienteGrupo.getProveedor().getCondicionDeIva());

        m.setRazonSocial(pendienteGrupo.getRazonSocial());
        m.setNrodoc(pendienteGrupo.getNrodoc());
        m.setTipoDocumento(pendienteGrupo.getTipoDocumento());
        m.setProvincia(pendienteGrupo.getLocalidad().getProvincia());
        m.setLocalidad(pendienteGrupo.getLocalidad());

        m.setBarrio(pendienteGrupo.getProveedor().getBarrio());
        m.setDireccion(pendienteGrupo.getDireccion());

        m.setDepartamentoPiso(pendienteGrupo.getProveedor().getDepartamentoPiso());
        m.setDepartamentoNumero(pendienteGrupo.getProveedor().getDepartamentoNumero());

        m.setCondicionDePago(pendienteGrupo.getCondicionDePago());
        m.setListaCosto(pendienteGrupo.getListaDePrecio());
        m.setMonedaListaCosto(pendienteGrupo.getListaDePrecio().getMoneda());

        m.setMonedaRegistracion(pendienteGrupo.getMonedaRegistracion());

        if (circuito.getCongelaCotizacion().equals("S")) {
            m.setCotizacion(pendienteGrupo.getCotizacion());
        }

        m.setComprador(pendienteGrupo.getComprador());

        if (m.getCircuito().getRecuperaDepositoPasoAnterior().equals("S")) {

            if (pendienteGrupo.getDeposito() != null) {
                m.setDeposito(pendienteGrupo.getDeposito());
            }

            if (pendienteGrupo.getDepositoTransferencia() != null) {
                m.setDeposito(pendienteGrupo.getDepositoTransferencia());
            }

        }

        generarItems(m, itemsPendientes);
        asignarFormulario(m);

        return m;
    }

    public boolean tengoItemsSeleccionados(List<PendienteCompraDetalle> itemsPendientes) {

        if (itemsPendientes == null || itemsPendientes.isEmpty()) {
            return false;
        }

        for (PendienteCompraDetalle p : itemsPendientes) {

            if (p.isSeleccionado()) {
                return true;
            }
        }

        return false;
    }

    private MovimientoCompra crearMovimiento(CircuitoCompra circuito, Comprobante comprobante, PuntoVenta puntoVenta, PuntoVenta puntoVentaStock) throws ExcepcionGeneralSistema {

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("El comprobante no puede ser nulo");
        }

        MovimientoCompra movimiento = new MovimientoCompra();
        Moneda moneda = monedaRN.getMoneda(parametrosRN.getParametro().getCodigoMonedaSecundaria());
        BigDecimal cotizacion = monedaRN.getCotizacionDia(parametrosRN.getParametro().getCodigoMonedaSecundaria());

        movimiento.setCircuito(circuito);

        movimiento.setEmpresa(puntoVenta.getEmpresa());
        movimiento.setSucursal(puntoVenta.getSucursal());
        movimiento.setPuntoVenta(puntoVenta);
        movimiento.setUnidadNegocio(puntoVenta.getUnidadNegocio());

        movimiento.setPuntoVentaStock(puntoVentaStock);
        movimiento.setCongelaPrecio(circuito.getCongelaPrecio());

        movimiento.setComprobante(comprobante);
        movimiento.setFechaMovimiento(new Date());
        movimiento.setMonedaSecundaria(moneda);
        movimiento.setMonedaRegistracion(comprobante.getMonedaRegistracion());
        movimiento.setCotizacion(cotizacion);

        if (comprobante.getDeposito() != null) {
            movimiento.setDeposito(comprobante.getDeposito());
        }

        if (comprobante.getDepositoTransferencia() != null) {
            movimiento.setDepositoTransferencia(comprobante.getDepositoTransferencia());
        }

        asignarFormulario(movimiento);
        return movimiento;
    }

    public ItemMovimientoCompra nuevoItemProducto(MovimientoCompra movimiento) throws ExcepcionGeneralSistema {

        ItemMovimientoCompra nItem = new ItemMovimientoCompra();
        nItem.setNroitm(movimiento.getItemsProducto().size() + 1);
        nItem.setCotizacion(movimiento.getCotizacion());

        nItem.setMovimiento(movimiento);
        nItem.setMovimientoInicial(movimiento.getId());
        nItem.setMovimientoOriginal(movimiento.getId());

//        if (movimiento.getCircuito().getActualizaStock().equals("S")) {
//            nItem = nuevoItemDetalle(nItem);
//        }
        reorganizarNroItem(movimiento);

        return nItem;
    }

    public void agregarItem(MovimientoCompra m) throws ExcepcionGeneralSistema {

        puedoAgregarItem(m);
        m.getItemsProducto().add(nuevoItemProducto(m));
    }

    public void eliminarItemProducto(MovimientoCompra movimiento, ItemMovimientoCompra nItem) throws Exception {

        if (movimiento == null) {
            throw new ExcepcionGeneralSistema("Movimiento vacío, nada para eliminar");
        }

        if (nItem == null) {
            throw new ExcepcionGeneralSistema("Item vacío, nada para eliminar");
        }

        if (movimiento.getId() != null && !movimiento.getFormulario().getModfor().equals("CO")) {
            throw new ExcepcionGeneralSistema("Solo se puede eliminar items de comprobantes de facturación");
        }

        if (nItem == null) {
            throw new ExcepcionGeneralSistema("Item nulo, nada para eliminar");
        }

        if (movimiento.getId() != null && nItem.getItemsAplicacion() != null) {

            if (!nItem.getItemsAplicacion().isEmpty()) {
                throw new ExcepcionGeneralSistema("Este items tiene aplicaciones, no es posible eliminarlo");
            }
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemProducto = -1;
        int indiceItemAplicacion = -1;
        int indiceItemAnulacion = -1;

        for (ItemMovimientoCompra ip : movimiento.getItemsProducto()) {

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
            ItemMovimientoCompra remove = movimiento.getItemsProducto().remove(indiceItemProducto);
            if (remove != null) {
                if (movimiento.getId() != null && remove.getId() != null) {
                    compraDAO.eliminar(ItemMovimientoCompra.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(movimiento);
        calcularImportes(movimiento, "U");
    }

    public void eliminarItemDetalle(MovimientoCompra movimiento, ItemMovimientoCompra itemProducto, ItemDetalleItemMovimientoCompra itemDetalle) throws ExcepcionGeneralSistema {

        if (movimiento == null) {
            throw new ExcepcionGeneralSistema("Movimiento vacío, nada para eliminar");
        }

        if (itemDetalle == null) {
            throw new ExcepcionGeneralSistema("Item detalle vacío, nada para eliminar");
        }

        if (movimiento.getId() != null && !movimiento.getFormulario().getModfor().equals("CO")) {
            throw new ExcepcionGeneralSistema("Solo se puede eliminar detalles de atributos de comprobantes de compras");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemProducto = -1;

        for (ItemDetalleItemMovimientoCompra id : itemProducto.getItemsDetalle()) {

            if (id.getNroitm() >= 0) {

                if (id.getNroitm() == itemDetalle.getNroitm()) {
                    indiceItemProducto = i;
                }
            }
            i++;
        }
        i = 0;

        //Borramos los items productos
        if (indiceItemProducto >= 0) {
            ItemDetalleItemMovimientoCompra remove = itemProducto.getItemsDetalle().remove(indiceItemProducto);
            if (remove != null) {
                if (movimiento.getId() != null && itemProducto.getId() != null && remove.getId() != null) {
                    compraDAO.eliminar(ItemDetalleItemMovimientoCompra.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(movimiento);
        calcularImportes(movimiento, "U");
    }

    public void puedoAgregarItem(MovimientoCompra movimiento) throws ExcepcionGeneralSistema {

        if (movimiento.getCircuito().getPermiteAgregarItems().equals("N")) {
            throw new ExcepcionGeneralSistema("El circuito no permite agregar items");
        }

        if (movimiento.getListaCosto() == null) {
            throw new ExcepcionGeneralSistema("Seleccione una lista de precios");
        }

        if (movimiento.getMonedaSecundaria() == null) {
            throw new ExcepcionGeneralSistema("La moneda secundaria no puede estar vacía");
        }

        if (movimiento.getCotizacion() == null || movimiento.getCotizacion().compareTo(BigDecimal.ONE) < 0) {
            throw new ExcepcionGeneralSistema("La cotización del comprobante no puede se nula o menor a 1");
        }
    }

    public void reorganizarNroItem(MovimientoCompra movimiento) {

        //Reorganizamos los números de items
        int i = 1;
        for (ItemMovimientoCompra ip : movimiento.getItemsProducto()) {
            ip.setNroitm(i);
            i++;
            int d = 0;

            for (ItemDetalleItemMovimientoCompra itemDetalle : ip.getItemsDetalle()) {
                itemDetalle.setNroitm(d);
                d++;
            }
        }

    }

    public void asignarFormulario(MovimientoCompra m) throws ExcepcionGeneralSistema {

        if (m.getComprobante() == null || m.getPuntoVenta() == null) {
            return;
        }

        Formulario formulario = null;
        Formulario formularioCJ = null;

        if (m.getCircuito().getActualizaProveedor().equals("S") && m.getCondicionDeIva() != null) {

            if (m.getCondicionDeIva() != null) {
                formulario = formularioRN.getFormulario(m.getComprobante(), m.getPuntoVenta(), m.getCondicionDeIva().getCodigo());

                if (m.getMovimientoTesoreria() != null) {
                    formularioCJ = formularioRN.getFormulario(m.getMovimientoTesoreria().getComprobante(), m.getMovimientoTesoreria().getPuntoVenta(), m.getCondicionDeIva().getCodigo());
                    m.getMovimientoTesoreria().setFormulario(formulario);
                }
            }

        } else if (m.getCircuito().getActualizaCompra().equals("S")) {

            formulario = formularioRN.getFormulario(m.getComprobante(), m.getPuntoVenta(), "X");

        } else if (m.getCircuito().getActualizaStock().equals("S")) {

            formulario = formularioRN.getFormulario(m.getComprobante(), m.getPuntoVentaStock(), "X");
        }

        if (formulario != null) {
            //Este número es temporal y puede cambiar al guardar el objeto.
            m.setNumeroFormulario(formulario.getUltimoNumero() + 1);
            m.setFormulario(formulario);
        }
    }

    public void seleccionarTodo(List<PendienteCompraDetalle> itemsPendiente, boolean seleccionarTodo) {

        if (itemsPendiente == null) {
            return;
        }

        for (PendienteCompraDetalle i : itemsPendiente) {
            i.setSeleccionado(seleccionarTodo);
        }
    }

    public List<PendienteCompraGrupo> getPendienteGrupo(String consultaGrupo) {
        return compraDAO.getPendienteGrupo(consultaGrupo);
    }

    public List<PendienteCompraDetalle> getPendienteDetalle(String consultaDetalle) {
        return compraDAO.getPendienteDetalle(consultaDetalle);
    }

    public void generarItems(MovimientoCompra movimiento, List<PendienteCompraDetalle> itemsPendiente) throws ExcepcionGeneralSistema {

        if (itemsPendiente.isEmpty()) {
            return;
        }

        //Vaciamos todos los items del movimiento.
        movimiento.getItemsProducto().clear();

        int cantSel = 0;
        for (PendienteCompraDetalle itemPendiente : itemsPendiente) {

            if (itemPendiente.isSeleccionado()) {

                cantSel++;
                ItemMovimientoCompra itemProducto = (ItemMovimientoCompra) nuevoItemProducto(movimiento);

                itemProducto.setProducto(itemPendiente.getProducto());
                itemProducto.setProductoOriginal(itemPendiente.getProducto());
                itemProducto.setDescripcion(itemPendiente.getDescripcion());
                itemProducto.setObservaciones(itemPendiente.getObservacion());
                itemProducto.setConcepto(itemPendiente.getProducto().getConceptoProveedor());
                itemProducto.setUnidadMedida(itemPendiente.getUnidadMedida());
                itemProducto.setCantidad(new BigDecimal(itemPendiente.getCantidad()));
                itemProducto.setCantidadOriginal(new BigDecimal(itemPendiente.getCantidad()));

                itemProducto.setPrecioSecundario(itemPendiente.getPrecioSecundario());
                itemProducto.setPrecio(itemPendiente.getPrecio());

                //Verificamos si el circuito aplica a items pendientes
                if (movimiento.getCircuito().getNoCancelaPendiente().equals("N")) {
                    itemProducto.setItemAplicado(itemPendiente.getItemProducto());
                }

                //Verificamos reversión de pendientes y guardamos relacionamos el item a revertir
                if (movimiento.getCircuito().getEsAnulacion().equals("S") && movimiento.getCircuito().getReviertePendiente().equals("S")) {

                    itemProducto.setItemReversion(itemPendiente.getItemProducto().getItemAplicado());
                }

                /**
                 * Buscamos el detalle de atributos del item que estamos
                 * aplicando para cargarlo al movimiento nuevo y arrastrar la
                 * información.
                 */
                if (itemProducto.getMovimiento().getCircuito().getActualizaCompra().equals("N")) {

                    ItemMovimientoCompra itemAplicado = itemCompraDAO.getItemProducto(itemPendiente.getIdItemAplicacion());

                    if (itemAplicado != null && itemAplicado.getItemsDetalle() != null) {
                        asignarProductoItemDetalleFromItemAplicado(itemProducto);
                    } else {
                        asignarProductoItemDetalle(itemProducto);
                    }

                    if (itemProducto.getItemsDetalle() == null || itemProducto.getItemsDetalle().isEmpty()) {
                        asignarProductoItemDetalle(itemProducto);
                    }
                }

                //Agregamos el item a la lista
                movimiento.getItemsProducto().add(itemProducto);
            }
        }
        if (cantSel == 0) {
            throw new ExcepcionGeneralSistema("No ha seleccionado ningún producto");
        }
    }

    public void calcularImportesLineaByPrecio(ItemMovimientoCompra item) {

        if (item.getPorcentajeBonificacion1() == null) {
            item.setPorcentajeBonificacion1(BigDecimal.ZERO);
        }

        if (item.getPorcentajeBonificacion2() == null) {
            item.setPorcentajeBonificacion2(BigDecimal.ZERO);
        }

        if (item.getPorcentajeBonificacion3() == null) {
            item.setPorcentajeBonificacion3(BigDecimal.ZERO);
        }

        if (item.getPorcentajeBonificacion4() == null) {
            item.setPorcentajeBonificacion4(BigDecimal.ZERO);
        }

        if (item.getPorcentajeBonificacion5() == null) {
            item.setPorcentajeBonificacion5(BigDecimal.ZERO);
        }

        if (item.getPorcentajeBonificacion6() == null) {
            item.setPorcentajeBonificacion6(BigDecimal.ZERO);
        }

        if (item.getCantidad() == null) {
            item.setCantidad(BigDecimal.ZERO);
        }

        if (item.getPrecio() == null) {
            item.setPrecio(BigDecimal.ZERO);
        }

        if (item.getPrecioSecundario() == null) {
            item.setPrecioSecundario(BigDecimal.ZERO);
        }

        if (item.getMovimiento().getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
            item.setPrecio(item.getPrecioSecundario().multiply(item.getCotizacion()).setScale(2, RoundingMode.HALF_UP));
        }

        BigDecimal porcentTotalBonif = item.getPorcentajeBonificacion1().add(item.getPorcentajeBonificacion2()).add(item.getPorcentajeBonificacion3()).add(item.getPorcentajeBonificacion4()).add(item.getPorcentajeBonificacion5()).add(item.getPorcentajeBonificacion6());
        item.setPorcentaTotalBonificacion(porcentTotalBonif);

        item.setPrecioSecundario(item.getPrecio().divide(item.getCotizacion(), 2, RoundingMode.HALF_UP));

        BigDecimal bonif = item.getPrecio().multiply(item.getPorcentaTotalBonificacion().divide(new BigDecimal(100), 4, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalLinea = item.getCantidad().multiply(item.getPrecio().add(bonif)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal bonifSecundario = item.getPrecioSecundario().multiply(item.getPorcentaTotalBonificacion().divide(new BigDecimal(100), 4, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalLineaSecundario = item.getCantidad().multiply(item.getPrecioSecundario().add(bonifSecundario)).setScale(2, RoundingMode.HALF_UP);

        item.setTotalLinea(totalLinea);
        item.setTotalLineaSecundario(totalLineaSecundario);
    }

    public void calcularImportesLineaByTotal(ItemMovimientoCompra item) {

        if (item.getPorcentajeBonificacion1() == null) {
            item.setPorcentajeBonificacion1(BigDecimal.ZERO);
        }

        if (item.getPorcentajeBonificacion2() == null) {
            item.setPorcentajeBonificacion2(BigDecimal.ZERO);
        }

        if (item.getPorcentajeBonificacion3() == null) {
            item.setPorcentajeBonificacion3(BigDecimal.ZERO);
        }

        if (item.getPorcentajeBonificacion4() == null) {
            item.setPorcentajeBonificacion4(BigDecimal.ZERO);
        }

        if (item.getPorcentajeBonificacion5() == null) {
            item.setPorcentajeBonificacion5(BigDecimal.ZERO);
        }

        if (item.getPorcentajeBonificacion6() == null) {
            item.setPorcentajeBonificacion6(BigDecimal.ZERO);
        }

        BigDecimal porcentTotalBonif = item.getPorcentajeBonificacion1().add(item.getPorcentajeBonificacion2()).add(item.getPorcentajeBonificacion3()).add(item.getPorcentajeBonificacion4()).add(item.getPorcentajeBonificacion5()).add(item.getPorcentajeBonificacion6());
        item.setPorcentaTotalBonificacion(porcentTotalBonif);

        if (item.getCantidad() == null) {
            item.setCantidad(BigDecimal.ZERO);
        }

        if (item.getTotalLinea() == null) {
            item.setTotalLinea(BigDecimal.ZERO);
        }

        if (item.getTotalLineaSecundario() == null) {
            item.setTotalLineaSecundario(BigDecimal.ZERO);
        }

        BigDecimal bonifCalc = BigDecimal.ZERO;

        if (porcentTotalBonif.compareTo(BigDecimal.ZERO) != 0) {

            bonifCalc = porcentTotalBonif.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP).negate();
        }

        if (item.getMovimiento().getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
            item.setTotalLinea(item.getTotalLineaSecundario().multiply(item.getCotizacion()).setScale(2, RoundingMode.HALF_UP));
        }

        BigDecimal precio = item.getTotalLinea().divide(item.getCantidad().multiply((BigDecimal.ONE).add(bonifCalc)), 2, RoundingMode.HALF_UP);

        item.setPrecio(precio);
        item.setPrecioSecundario(item.getPrecio().divide(item.getCotizacion(), 2, RoundingMode.HALF_UP));

        BigDecimal bonifSecundario = item.getPrecioSecundario().multiply(
                item.getPorcentaTotalBonificacion().divide(new BigDecimal(100), 4, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalLineaSecundario = item.getCantidad().multiply(item.getPrecioSecundario().add(bonifSecundario)).setScale(2, RoundingMode.HALF_UP);
        item.setTotalLineaSecundario(totalLineaSecundario);
    }
//
//    private void actualizarPendienteItemsAplicados(MovimientoCompra m) {
//
//        for (ItemMovimientoCompra ip : m.getItemsProducto()) {
//
//            if (ip.getCantidad() == null || ip.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
//                continue;
//            }
//
//            if (ip.getItemAplicado() != null) {
//
//                BigDecimal aplicadoActual = compraDAO.getCantidadAplicadaItem(ip.getItemAplicado().getId());
//                aplicadoActual = aplicadoActual.add(ip.getCantidad());
//                BigDecimal cantPendiente = ip.getItemAplicado().getCantidad().add(aplicadoActual.negate());
//                ip.getItemAplicado().setCantidadPendiente(cantPendiente);
//                compraDAO.editar(ip.getItemAplicado());
//            }
//        }
//    }

    private void sincronizarCantidades(MovimientoCompra m) {

        for (ItemMovimientoCompra ip : m.getItemsProducto()) {

            if (ip.getCantidad() == null || ip.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            ip.setCantidadPendiente(ip.getCantidad());
        }
    }

    private void actualizarCantidadesPendientes(MovimientoCompra m) {

        for (ItemMovimientoCompra ip : m.getItemsProducto()) {

            if (ip.getCantidad() == null || ip.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            if (ip.getItemAplicado() != null) {

                BigDecimal aplicadoActual = compraDAO.getCantidadAplicadaItem(ip.getItemAplicado().getId());
                BigDecimal pendiente = ip.getItemAplicado().getCantidad().add(aplicadoActual.negate());
                ip.getItemAplicado().setCantidadPendiente(pendiente);

                compraDAO.editar(ip.getItemAplicado());
            }

            if (ip.getItemReversion() != null) {

                BigDecimal aplicadoActual = compraDAO.getCantidadAplicadaItem(ip.getItemReversion().getId());
                BigDecimal pendiente = ip.getItemReversion().getCantidad().add(aplicadoActual.negate());
                ip.getItemReversion().setCantidadPendiente(pendiente);

                compraDAO.editar(ip.getItemReversion());
            }
        }
    }

    private void controlComprobante(MovimientoCompra m) throws ExcepcionGeneralSistema, Exception {

        ParametroStock parametro = parametroStockRN.getParametro();

        String sErrores = "";

        sErrores += moduloRN.canSaveModulo("CO", m.getFechaMovimiento());

        if (m.getId() != null) {
            if (m.getFormulario().getModfor().equals("PV")) {
                sErrores += "- No es posible modificar un comprobante de proveedor \n";
            }

            if (m.getFormulario().getModfor().equals("ST")) {
                sErrores += "- No es posible modificar un comprobante de stock \n";
            }
        }

        if (m.getProveedor() == null) {
            sErrores += "- El proveedor no puede estar vacío \n";
        }

        if (m.getCondicionDeIva() == null) {
            sErrores += "- La condición de iva no puede estar vacía \n";
        }

        if (m.getLocalidad() == null) {
            sErrores += "- La localidad no puede estar vacía \n";
        }

        if (m.getDireccion() == null || m.getDireccion().isEmpty()) {
            sErrores += "- La dirección no puede estar vacía \n";
        }

        if (m.getCircuito().getDepositoObligatorio().equals("S") && m.getDeposito() == null) {
            sErrores += "- El depósito es obligatorio \n";
        }

        if (m.getCircuito().getActualizaProveedor().equals("S")) {

            if (m.getCondicionDePago().getImputaCuentaCorriente().equals("N") && m.getComprobanteProveedor().getTipoImputacion().equals("CC")) {

                sErrores += "- La condición de pago del proveedor no permite generar comprobantes en cuenta corriente \n";
            }

            if (m.getCondicionDePago().getImputaCuentaCorriente().equals("S") && m.getComprobanteProveedor().getTipoImputacion().equals("CO")) {

                sErrores += "- La condición de pago asignada al comprobante tiene que ser contado, modifique y vuelva a intentar \n";
            }
        }

        if (m.getItemsProducto().isEmpty()) {
            sErrores += "- El detalle de productos está vacío, no es posible guardar el comprobante\n";
        }

        if (m.getCircuito().getPermiteComprobanteConPrecioCero().equals("N") && m.getImporteTotal().compareTo(BigDecimal.ZERO) <= 0) {
            sErrores += "- El importe total no puede ser menor o igual a cero \n";
        }

        if (m.getId() == null
                && m.getPuntoVentaOriginal() != null && m.getNumeroOriginal() != null
                && (m.getComprobante().getModulo().equals("PV") || m.getComprobante().getModulo().equals("ST"))) {

            m.setPuntoVentaOriginal("0000" + m.getPuntoVentaOriginal());
            m.setPuntoVentaOriginal(m.getPuntoVentaOriginal().substring(m.getPuntoVentaOriginal().length() - 4, m.getPuntoVentaOriginal().length()));

            m.setNumeroOriginal("00000000" + m.getNumeroOriginal());
            m.setNumeroOriginal(m.getNumeroOriginal().substring(m.getNumeroOriginal().length() - 8, m.getNumeroOriginal().length()));

            if (compraDAO.getMovimientoByPuntoVentaNumeroOriginal(m.getProveedor().getNrocta(), m.getPuntoVentaOriginal(), m.getNumeroOriginal()) != null) {
                sErrores += "El punto de venta y número de comprobante original ingresados, han sido cargados en otro comprobante ";
            }
        }

        // CONTROLES GENERALES PARA LOS ITEMS
        for (ItemMovimientoCompra itemProducto : m.getItemsProducto()) {

            itemProducto.setConError(false);

            if (m.getCircuito().getPermiteCantidadCero().equals("N")) {
                if (itemProducto.getCantidad() == null || itemProducto.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                    itemProducto.setConError(true);
                    sErrores += "- Ingrese un valor de cantidad válido. Mayor a 0 para el item " + itemProducto.getNroitm() + " \n";
                }
            }

            if (m.getCircuito().getPermiteProductosConPrecioCero().equals("N")) {
                if (itemProducto.getPrecio() == null || itemProducto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                    itemProducto.setConError(true);
                    sErrores += "- No está permitido productos con precio en cero. Ingrese un valor de precio válido. Mayor a 0 para el item " + itemProducto.getNroitm() + " \n";
                }
            }

            if (itemProducto.getProducto() == null) {
                itemProducto.setConError(true);
                sErrores += "- El producto no puede estar vacío en item " + itemProducto.getNroitm() + " \n";
            }

            if (m.getCircuito().getActualizaStock().equals("S")) {

                BigDecimal cantidadTotalItems = BigDecimal.ZERO;

                for (ItemDetalleItemMovimientoCompra itemDetalle : itemProducto.getItemsDetalle()) {
                    if (itemDetalle.getCantidad() != null) {
                        cantidadTotalItems = cantidadTotalItems.add(itemDetalle.getCantidad());
                    }
                }

                if (itemProducto.getCantidad().compareTo(cantidadTotalItems) != 0) {
                    itemProducto.setConError(true);
                    sErrores += "- La cantidad en el item " + itemProducto.getNroitm() + " no coincide con las cantidades del detalle de atributos \n";
                }
            }

            if (m.getCircuito().getAdministraAtributo1().equals("S")
                    || m.getCircuito().getAdministraAtributo2().equals("S")
                    || m.getCircuito().getAdministraAtributo3().equals("S")
                    || m.getCircuito().getAdministraAtributo4().equals("S")
                    || m.getCircuito().getAdministraAtributo5().equals("S")
                    || m.getCircuito().getAdministraAtributo6().equals("S")
                    || m.getCircuito().getAdministraAtributo7().equals("S")) {

                for (ItemDetalleItemMovimientoCompra itemDetalle : itemProducto.getItemsDetalle()) {

                    // Controlamos el ingreso de los atributos de stock
                    if (itemDetalle.getProducto() != null
                            && itemDetalle.getProducto().getAdministraAtributo1().equals("S")
                            && itemDetalle.getAtributo1().isEmpty()) {

                        itemProducto.setConError(true);
                        sErrores += "- Ingrese el atributo 1 (" + parametro.getDescripcionAtributo1() + ") para el producto " + itemDetalle.getProducto().getDescripcion() + "\n";
                    }

                    if (itemDetalle.getProducto() != null
                            && itemDetalle.getProducto().getAdministraAtributo2().equals("S")
                            && itemDetalle.getAtributo2().isEmpty()) {

                        itemProducto.setConError(true);
                        sErrores += "- Ingrese el atributo 2 (" + parametro.getDescripcionAtributo2() + ") para el producto " + itemDetalle.getProducto().getDescripcion() + "\n";
                    }

                    if (itemDetalle.getProducto() != null
                            && itemDetalle.getProducto().getAdministraAtributo3().equals("S")
                            && itemDetalle.getAtributo3().isEmpty()) {

                        itemProducto.setConError(true);
                        sErrores += "- Ingrese el atributo 3 (" + parametro.getDescripcionAtributo3() + ") para el producto " + itemDetalle.getProducto().getDescripcion() + "\n";
                    }

                    if (itemDetalle.getProducto() != null
                            && itemDetalle.getProducto().getAdministraAtributo4().equals("S")
                            && itemDetalle.getAtributo4().isEmpty()) {

                        itemProducto.setConError(true);
                        sErrores += "- Ingrese el atributo 4 (" + parametro.getDescripcionAtributo4() + ") para el producto " + itemDetalle.getProducto().getDescripcion() + "\n";
                    }

                    if (itemDetalle.getProducto() != null
                            && itemDetalle.getProducto().getAdministraAtributo5().equals("S")
                            && itemDetalle.getAtributo5().isEmpty()) {

                        itemProducto.setConError(true);
                        sErrores += "- Ingrese el atributo 5 (" + parametro.getDescripcionAtributo5() + ") para el producto " + itemDetalle.getProducto().getDescripcion() + "\n";
                    }

                    if (itemDetalle.getProducto() != null
                            && itemDetalle.getProducto().getAdministraAtributo6().equals("S")
                            && itemDetalle.getAtributo6().isEmpty()) {

                        itemProducto.setConError(true);
                        sErrores += "- Ingrese el atributo 6 (" + parametro.getDescripcionAtributo6() + ") para el producto " + itemDetalle.getProducto().getDescripcion() + "\n";
                    }

                    if (itemDetalle.getProducto() != null
                            && itemDetalle.getProducto().getAdministraAtributo7().equals("S")
                            && itemDetalle.getAtributo7().isEmpty()) {

                        itemProducto.setConError(true);
                        sErrores += "- Ingrese el atributo 7 (" + parametro.getDescripcionAtributo7() + ") para el producto " + itemDetalle.getProducto().getDescripcion() + "\n";
                    }
                }
            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    /**
     *
     * @param m
     * @param modo U=Por precio unitario, calcula total | T = Por total, calcula
     * precio
     */
    public void calcularImportesOld(MovimientoCompra m, String modo) {

        for (ItemMovimientoCompra ip : m.getItemsProducto()) {

            if (modo.equals("U")) {
                calcularImportesLineaByPrecio(ip);
            }

            if (modo.equals("T")) {
                calcularImportesLineaByTotal(ip);
            }
        }

        m.setImpGravado0000(BigDecimal.ZERO);
        m.setImpGravado1050(BigDecimal.ZERO);
        m.setImpGravado2100(BigDecimal.ZERO);
        m.setImpGravado2700(BigDecimal.ZERO);

        for (ItemMovimientoCompra ip : m.getItemsProducto()) {

            if (ip.getConcepto() == null) {
                continue;
            }

            if (ip.getConcepto().getCodigo().equals("C000")) {
                m.setImpGravado0000(m.getImpGravado0000().add(ip.getTotalLinea()));
            }

            if (ip.getConcepto().getCodigo().equals("C001")) {
                m.setImpGravado2100(m.getImpGravado2100().add(ip.getTotalLinea()));
            }

            if (ip.getConcepto().getCodigo().equals("C002")) {
                m.setImpGravado1050(m.getImpGravado1050().add(ip.getTotalLinea()));
            }

            if (ip.getConcepto().getCodigo().equals("C003")) {
                m.setImpGravado2700(m.getImpGravado2700().add(ip.getTotalLinea()));
            }
        }

        m.setImpIva1050(m.getImpGravado1050().multiply(new BigDecimal("0.105")).setScale(2, RoundingMode.HALF_UP));
        m.setImpIva2100(m.getImpGravado2100().multiply(new BigDecimal("0.21")).setScale(2, RoundingMode.HALF_UP));
        m.setImpIva2700(m.getImpGravado2700().multiply(new BigDecimal("0.27")).setScale(2, RoundingMode.HALF_UP));

        m.setImpBruto(m.getImpGravado0000().add(m.getImpGravado1050()).add(m.getImpGravado2100()).add(m.getImpGravado2700()));

        BigDecimal impPercepciones = m.getImpPercepcionIB().add(m.getImpPercepcionGAN()).add(m.getImpPercepcionITC()).add(m.getImpPercepcionIVA()).add(m.getImpPercepcionINT());

        m.setImporteTotal(m.getImpBruto().add(m.getImpIva1050()).add(m.getImpIva2100()).add(m.getImpIva2700()).add(impPercepciones));

    }

    /**
     *
     * @param m Movimiento de Compra
     * @param modo U=Por precio unitario, calcula total | T = Por total,
     * calcula*precio
     */
    public void calcularImportes(MovimientoCompra m, String modo) {

        m.setImporteTotal(BigDecimal.ZERO);

        //Calculamos los totales por cada item
        for (ItemMovimientoCompra ip : m.getItemsProducto()) {

            ip.setCotizacion(m.getCotizacion());

            if (modo.equals("U")) {
                calcularImportesLineaByPrecio(ip);
            }

            if (modo.equals("T")) {
                calcularImportesLineaByTotal(ip);
            }
        }

        for (ItemImpuestoProveedor i : m.getItemsImpuestoProveedor()) {

            if (i.getImporte() == null) {
                i.setImporte(BigDecimal.ZERO);
            } else if (i.getEditaImporte() != null && i.getEditaImporte().equals("N")) {
                i.setImporte(BigDecimal.ZERO);
            }
        }

        for (ItemPercepcionProveedor i : m.getItemsPercepcionProveedor()) {

            if (i.getImporte() == null) {
                i.setImporte(BigDecimal.ZERO);
            } else if (i.getEditaImporte() != null && i.getEditaImporte().equals("N")) {
                i.setImporte(BigDecimal.ZERO);
            }
        }

        for (ItemTotalProveedor i : m.getItemsTotalProveedor()) {
            i.setImporte(BigDecimal.ZERO);
        }

        for (ItemProductoProveedor itemProdProveedor : m.getItemsProductoProveedor()) {

            if (itemProdProveedor.getConcepto() == null) {
                continue;
            }

            itemProdProveedor.setImporte(BigDecimal.ZERO);

            //Cargamos todos los conceptos tipo A
            for (ItemMovimientoCompra ip : m.getItemsProducto()) {

                if (ip.getConcepto() == null) {
                    continue;
                }

                if (ip.getConcepto().equals(itemProdProveedor.getConcepto())) {

                    itemProdProveedor.setImporte(itemProdProveedor.getImporte().add(ip.getTotalLinea()));
                    itemProdProveedor.setImporteSecundario(itemProdProveedor.getImporte().divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));
                }
            }

            //Acumulamos en el total, la suma del concepto
            m.setImporteTotal(m.getImporteTotal().add(itemProdProveedor.getImporte()));

            //Calculamos los impuestos y percepciones por cada concepto tipo A y vamos acumulando
            for (ImpuestoPorConcepto impuestoConcepto : itemProdProveedor.getConcepto().getImpuestos()) {

                for (ItemImpuestoProveedor itemImpuesto : m.getItemsImpuestoProveedor()) {

                    if (itemImpuesto.getCodigoTipoImpuesto() == null || itemImpuesto.getCodigoTipoImpuesto().isEmpty()) {
                        continue;
                    }

                    if (itemProdProveedor.getConcepto().getImpuestos() == null) {
                        continue;
                    }

                    if (itemImpuesto.getEditaImporte().equals("N")) {

                        //El impuesto concepto e impuesto tienen que ser iguales para que pueda sumar
                        if (itemProdProveedor.getImporte().compareTo(BigDecimal.ZERO) > 0
                                && itemImpuesto.getCodigoTipoImpuesto().equals(impuestoConcepto.getCodimp())) {

//                        System.err.println("Econtró itemProdProveedor" + itemProdProveedor.getImporte());
//                        System.err.println("Concepto item impuesto: " + itemImpuesto.getConcepto() + " Concepto item proveedor: " + itemProdProveedor.getConcepto());
//                        System.err.println("itemImpuesto: " + itemImpuesto.getCodigoTipoImpuesto()+" impuestoConcepto: " + impuestoConcepto.getCodimp());
                            itemImpuesto.setImporte(itemImpuesto.getImporte().add(itemProdProveedor.getImporte().multiply(impuestoConcepto.getTasa()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)));
                            itemImpuesto.setImporteSecundario(itemImpuesto.getImporte().divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));

                            //Acumulamos en el total, la suma del impuesto
                            m.setImporteTotal(m.getImporteTotal().add(itemImpuesto.getImporte()));
                            break;
                        }
                    } else {
                        //Acumulamos en el total, la suma del impuesto
                        m.setImporteTotal(m.getImporteTotal().add(itemImpuesto.getImporte()));
                    }

                }

                for (ItemPercepcionProveedor itemPercepcion : m.getItemsPercepcionProveedor()) {

                    if (itemPercepcion.getEditaImporte().equals("N")) {

                        if (itemPercepcion.getCodigoTipoImpuesto() == null || itemPercepcion.getCodigoTipoImpuesto().isEmpty()) {
                            continue;
                        }

                        if (itemProdProveedor.getConcepto().getImpuestos() == null) {
                            continue;
                        }

                        if (itemPercepcion.getImporte() == null) {
                            itemPercepcion.setImporte(BigDecimal.ZERO);
                        }

                        //El concepto y impuesto tienen que ser iguales para que pueda sumar
                        if (itemProdProveedor.getImporte().compareTo(BigDecimal.ZERO) > 0
                                && itemPercepcion.getCodigoTipoImpuesto().equals(impuestoConcepto.getCodimp())) {

                            itemPercepcion.setImporte(itemPercepcion.getImporte().add(itemProdProveedor.getImporte().multiply(impuestoConcepto.getTasa()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)));
                            itemPercepcion.setImporteSecundario(itemPercepcion.getImporte().divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));

                            //Acumulamos en el total, la suma del impuesto
                            m.setImporteTotal(m.getImporteTotal().add(itemPercepcion.getImporte()));
                            break;
                        }

                    } else {
                        //Acumulamos en el total, la suma del impuesto
                        m.setImporteTotal(m.getImporteTotal().add(itemPercepcion.getImporte()));
                    }
                }
            }
        }

        for (ItemTotalProveedor itp : m.getItemsTotalProveedor()) {

            itp.setImporte(m.getImporteTotal());
            itp.setImporteSecundario(itp.getImporte().divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));
        }

//        m.setImpGravado0000(BigDecimal.ZERO);
//        m.setImpGravado1050(BigDecimal.ZERO);
//        m.setImpGravado2100(BigDecimal.ZERO);
//        m.setImpGravado2700(BigDecimal.ZERO);
//        m.setImpIva1050(m.getImpGravado1050().multiply(new BigDecimal("0.105")).setScale(2, RoundingMode.HALF_UP));
//        m.setImpIva2100(m.getImpGravado2100().multiply(new BigDecimal("0.21")).setScale(2, RoundingMode.HALF_UP));
//        m.setImpIva2700(m.getImpGravado2700().multiply(new BigDecimal("0.27")).setScale(2, RoundingMode.HALF_UP));
//
//        m.setImpBruto(m.getImpGravado0000().add(m.getImpGravado1050()).add(m.getImpGravado2100()).add(m.getImpGravado2700()));
//
//        BigDecimal impPercepciones = m.getImpPercepcionIB().add(m.getImpPercepcionGAN()).add(m.getImpPercepcionITC()).add(m.getImpPercepcionIVA()).add(m.getImpPercepcionINT());
//
//        m.setImporteTotal(m.getImpBruto().add(m.getImpIva1050()).add(m.getImpIva2100()).add(m.getImpIva2700()).add(impPercepciones));
    }

    public void sumarImportes(MovimientoCompra m) {

        m.setImporteTotal(BigDecimal.ZERO);

        for (ItemProductoProveedor itemProdProveedor : m.getItemsProductoProveedor()) {
            m.setImporteTotal(m.getImporteTotal().add(itemProdProveedor.getImporte()));
        }

        //Calculo de IVA
        for (ItemImpuestoProveedor itemImpuesto : m.getItemsImpuestoProveedor()) {
            m.setImporteTotal(m.getImporteTotal().add(itemImpuesto.getImporte()));
        }

        //Calculo de percepciones
        for (ItemPercepcionProveedor itemPercepcion : m.getItemsPercepcionProveedor()) {
            m.setImporteTotal(m.getImporteTotal().add(itemPercepcion.getImporte()));
        }

        for (ItemTotalProveedor itp : m.getItemsTotalProveedor()) {

            itp.setImporte(m.getImporteTotal());
            itp.setImporteSecundario(itp.getImporte().divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));
        }
    }

    public void ponerImportesCero(MovimientoCompra m) {

        for (ItemImpuestoProveedor i : m.getItemsImpuestoProveedor()) {

            if (i.getImporte() == null) {
                i.setImporte(BigDecimal.ZERO);
                i.setBaseImponible(BigDecimal.ZERO);
                i.setAlicuota(BigDecimal.ZERO);
            } else if (i.getEditaImporte() != null && i.getEditaImporte().equals("N")) {
                i.setImporte(BigDecimal.ZERO);
                i.setBaseImponible(BigDecimal.ZERO);
                i.setAlicuota(BigDecimal.ZERO);
            }
        }

        for (ItemPercepcionProveedor i : m.getItemsPercepcionProveedor()) {

            if (i.getImporte() == null) {
                i.setImporte(BigDecimal.ZERO);
                i.setBaseImponible(BigDecimal.ZERO);
                i.setAlicuota(BigDecimal.ZERO);
            } else if (i.getEditaImporte() != null && i.getEditaImporte().equals("N")) {
                i.setImporte(BigDecimal.ZERO);
                i.setBaseImponible(BigDecimal.ZERO);
                i.setAlicuota(BigDecimal.ZERO);
            }
        }

        for (ItemTotalProveedor i : m.getItemsTotalProveedor()) {
            i.setImporte(BigDecimal.ZERO);
        }
    }

    public MovimientoCompra getMovimiento(String codFormulario, Integer numeroFormulario) {

        return compraDAO.getMovimiento(codFormulario, numeroFormulario);
    }

    public List<MovimientoCompra> getListaByBusqueda(Map<String, String> filtro, boolean soloPendientes, int cantMax) {

        return compraDAO.getListaByBusqueda(filtro, soloPendientes, cantMax);
    }

    private void generarMovimientosAdicionales(MovimientoCompra m) throws ExcepcionGeneralSistema, Exception {

        if (m.getCircuito().getActualizaProveedor().equals("S") && m.getComprobanteProveedor() != null) {
            MovimientoProveedor mp = proveedorRN.generarComprobante(m);
            m.setMovimientoProveedor(mp);
        }

        if (m.getComprobanteStock() != null) {

            MovimientoStock ms = movimientoStockRN.generarComprobante(m);
            m.setMovimientoStock(ms);
        }

        if (m.getComprobanteTesoreria() != null) {

            m.getMovimientoTesoreria().setEntidad(m.getProveedor());
            m.getMovimientoTesoreria().setNombreEntidad(m.getProveedor().getRazonSocial());

            tesoreriaRN.calcularImportes(m.getMovimientoTesoreria());

            if (m.getCircuito().getValidaTotalContraTesoreria().equals("S")) {
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
            }

            tesoreriaRN.controlComprobante(m.getMovimientoTesoreria());
            tesoreriaRN.limpiarConceptoEnCero(m.getMovimientoTesoreria());
        }
    }

    private void tomarNumeroFormulario(MovimientoCompra m) throws ExcepcionGeneralSistema, Exception {

        if (m.getComprobanteProveedor() != null && m.getCircuito().getActualizaProveedor().equals("S")) {

            if (m.getMovimientoProveedor().getFormulario().getTipoNumeracion().equals("A")) {
                m.getMovimientoProveedor().setNumeroFormulario(formularioRN.tomarProximoNumero(m.getMovimientoProveedor().getFormulario()));
            }
            m.setNumeroFormulario(m.getMovimientoProveedor().getNumeroFormulario());

            if (m.getComprobanteTesoreria() != null && m.getMovimientoTesoreria() != null) {

                if (!m.getMovimientoTesoreria().getFormulario().equals(m.getMovimientoTesoreria().getFormulario())) {

                    if (m.getMovimientoTesoreria().getFormulario().getTipoNumeracion().equals("A")) {
                        m.getMovimientoTesoreria().setNumeroFormulario(formularioRN.tomarProximoNumero(m.getMovimientoTesoreria().getFormulario()));
                    }
                } else {
                    m.getMovimientoTesoreria().setNumeroFormulario(m.getMovimientoProveedor().getNumeroFormulario());
                }
            }

            if (m.getComprobanteStock() != null && m.getMovimientoStock() != null) {
                if (!m.getFormulario().equals(m.getMovimientoStock().getFormulario())) {
                    if (m.getMovimientoStock().getFormulario().getTipoNumeracion().equals("A")) {
                        m.getMovimientoStock().setNumeroFormulario(formularioRN.tomarProximoNumero(m.getMovimientoStock().getFormulario()));
                    }
                } else {
                    m.getMovimientoStock().setNumeroFormulario(m.getMovimientoProveedor().getNumeroFormulario());
                }
            }

        } else if (m.getComprobanteStock() != null && m.getMovimientoStock() != null) {

            if (m.getMovimientoStock().getFormulario().getTipoNumeracion().equals("A")) {
                m.getMovimientoStock().setNumeroFormulario(formularioRN.tomarProximoNumero(m.getMovimientoStock().getFormulario()));
            }

            m.setNumeroFormulario(m.getMovimientoStock().getNumeroFormulario());

        } else //Si no actualiza proveedores ni stock, entonces es facturación
        {
            if (m.getFormulario().getTipoNumeracion().equals("A")) {
                m.setNumeroFormulario(formularioRN.tomarProximoNumero(m.getFormulario()));
            }
        }

        formularioRN.guardar(m.getFormulario());
    }

    public void marcarItemsPersistidos(MovimientoCompra m) {

        if (m == null) {
            return;
        }
        if (m.getItemsProducto() == null) {
            return;
        }

        for (ItemMovimientoCompra ip : m.getItemsProducto()) {

//            ip.setTodoOk(true);
        }

    }

    private void actualizarPreciosUltimaCompra(MovimientoCompra movimiento) throws Exception {

        if (movimiento == null) {
            return;
        }
        if (movimiento.getItemsProducto() == null) {
            return;
        }

        if (movimiento.getCircuito().getActualizaProveedor().equals("S")
                && movimiento.getComprobanteProveedor() != null
                && movimiento.getComprobanteProveedor().getDebeHaber().equals("H")) {

            for (ItemMovimientoCompra itemProducto : movimiento.getItemsProducto()) {

                if (itemProducto.getProducto().getMonedaReposicion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
                    itemProducto.getProducto().setPrecioUltimaCompra(itemProducto.getPrecio());
                } else {
                    itemProducto.getProducto().setPrecioUltimaCompra(itemProducto.getPrecioSecundario());
                }
                itemProducto.getProducto().setFechaUltimaCompra(movimiento.getFechaEmision());
                productoDAO.editar(itemProducto.getProducto());
            }
        }
    }

    public MovimientoCompra generarSeguimiento(MovimientoCompra movimiento) {

        if (movimiento == null) {
            return null;
        }

        if (movimiento.getItemsProducto() == null || movimiento.getItemsProducto().isEmpty()) {
            return movimiento;
        }

        for (ItemMovimientoCompra ip : movimiento.getItemsProducto()) {

            buscarAplicaciones(ip);
        }

        return movimiento;
    }

    /**
     *
     * @param item itemProductoCompra
     * @return Lista de aplicaciones
     */
    public void buscarAplicaciones(ItemMovimientoCompra item) {

        List<ItemMovimientoCompra> itemsAplicaciones = compraDAO.getAplicacionesByItem(item.getId());
        item.setItemsAplicacion(itemsAplicaciones);
        buscarItemProducto(itemsAplicaciones);

    }

    private void buscarItemProducto(List<ItemMovimientoCompra> items) {

        if (items == null || items.isEmpty()) {
            return;
        }

        for (ItemMovimientoCompra ia : items) {

            ItemMovimientoCompra ip = compraDAO.getItemProductoByItemAplicacion(ia.getMovimiento().getId(), ia.getNroitm(), ia.getProducto().getCodigo());
            buscarAplicaciones(ip);
            ia.setItemsAplicacion(ip.getItemsAplicacion());
        }
    }

    public ItemDetalleItemMovimientoCompra nuevoItemDetalle(ItemMovimientoCompra itemProducto) {

        if (itemProducto.getItemsDetalle() == null) {
            itemProducto.setItemsDetalle(new ArrayList<ItemDetalleItemMovimientoCompra>());
        }
        ItemDetalleItemMovimientoCompra itemDetalle = new ItemDetalleItemMovimientoCompra();
        itemDetalle.setNroitm(itemProducto.getItemsDetalle().size() + 1);
        itemDetalle.setItemProducto(itemProducto);
        itemDetalle.setProducto(itemProducto.getProducto());
        itemDetalle.setDescripcion(itemProducto.getDescripcion());
        itemDetalle.setDeposito(itemProducto.getDeposito());
        itemDetalle.setUnidadMedida(itemProducto.getUnidadMedida());

        //Si el movimiento tiene cargadado el dato de algún atirbuto se lo asignamos
        itemDetalle.setAtributo1(itemProducto.getMovimiento().getAtributo1() == null ? "" : itemProducto.getMovimiento().getAtributo1());
        itemDetalle.setAtributo2(itemProducto.getMovimiento().getAtributo2() == null ? "" : itemProducto.getMovimiento().getAtributo2());
        itemDetalle.setAtributo3(itemProducto.getMovimiento().getAtributo3() == null ? "" : itemProducto.getMovimiento().getAtributo3());
        itemDetalle.setAtributo4(itemProducto.getMovimiento().getAtributo4() == null ? "" : itemProducto.getMovimiento().getAtributo4());
        itemDetalle.setAtributo5(itemProducto.getMovimiento().getAtributo5() == null ? "" : itemProducto.getMovimiento().getAtributo5());
        itemDetalle.setAtributo6(itemProducto.getMovimiento().getAtributo6() == null ? "" : itemProducto.getMovimiento().getAtributo6());
        itemDetalle.setAtributo7(itemProducto.getMovimiento().getAtributo7() == null ? "" : itemProducto.getMovimiento().getAtributo7());

        itemProducto.getItemsDetalle().add(itemDetalle);

        return itemDetalle;
    }

    public ItemMovimientoCompra procesarItemDetalle(ItemMovimientoCompra itemProducto) {

        if (itemProducto.getItemsDetalle() == null) {
            itemProducto.setItemsDetalle(new ArrayList<ItemDetalleItemMovimientoCompra>());
        }
        itemProducto.getItemsDetalle().clear();

        ItemDetalleItemMovimientoCompra itemDetalle = new ItemDetalleItemMovimientoCompra();
        itemDetalle.setNroitm(itemProducto.getItemsDetalle().size() + 1);
        itemDetalle.setProducto(itemProducto.getProducto());
        itemDetalle.setDescripcion(itemProducto.getDescripcion());
        itemDetalle.setAtributo1(itemProducto.getAtributo1());
        itemDetalle.setAtributo2(itemProducto.getAtributo2());
        itemDetalle.setAtributo3(itemProducto.getAtributo3());
        itemDetalle.setAtributo4(itemProducto.getAtributo4());
        itemDetalle.setAtributo5(itemProducto.getAtributo5());
        itemDetalle.setAtributo6(itemProducto.getAtributo6());
        itemDetalle.setAtributo7(itemProducto.getAtributo7());
        itemDetalle.setDeposito(itemProducto.getDeposito());
        itemDetalle.setCantidad(itemProducto.getCantidad());
        itemDetalle.setUnidadMedida(itemProducto.getUnidadMedida());

        itemProducto.getItemsDetalle().add(itemDetalle);

        return itemProducto;
    }

    public ItemMovimientoCompra agregarItemDetalle(ItemMovimientoCompra itemProducto) {

        if (itemProducto.getItemsDetalle() == null) {
            itemProducto.setItemsDetalle(new ArrayList<ItemDetalleItemMovimientoCompra>());
        }

        ItemDetalleItemMovimientoCompra itemDetalle = new ItemDetalleItemMovimientoCompra();
        itemDetalle.setNroitm(itemProducto.getItemsDetalle().size() + 1);
        itemDetalle.setProducto(itemProducto.getProducto());
        itemDetalle.setDescripcion(itemProducto.getDescripcion());
        itemDetalle.setAtributo1(itemProducto.getAtributo1());
        itemDetalle.setAtributo2(itemProducto.getAtributo2());
        itemDetalle.setAtributo3(itemProducto.getAtributo3());
        itemDetalle.setAtributo4(itemProducto.getAtributo4());
        itemDetalle.setAtributo5(itemProducto.getAtributo5());
        itemDetalle.setAtributo6(itemProducto.getAtributo6());
        itemDetalle.setAtributo7(itemProducto.getAtributo7());
        itemDetalle.setDeposito(itemProducto.getDeposito());
        itemDetalle.setCantidad(itemProducto.getCantidad());
        itemDetalle.setUnidadMedida(itemProducto.getUnidadMedida());

        itemProducto.getItemsDetalle().add(itemDetalle);
        return itemProducto;
    }

    public MovimientoCompra seleccionarMovimiento(MovimientoCompra movimientoSeleccionado, CircuitoCompra circuito) throws ExcepcionGeneralSistema {

        MovimientoCompra movimiento = compraDAO.getMovimientoCompraById(movimientoSeleccionado.getId());

        movimiento.setComprobanteProveedor(circuito.getComprobanteProveedor());
        proveedorRN.generarItemsMovimientoProveedor(movimiento);
        calcularImportes(movimiento, "U");

        return movimiento;
    }

    public void actualizarCantidades(ItemMovimientoCompra itemProducto) {

        if (itemProducto == null) {
            return;
        }
        if (itemProducto.getCantidad() == null) {
            return;
        }

        itemProducto.setCantidadPendiente(itemProducto.getCantidad());

        if (itemProducto.getMovimiento().getCircuito().getCircom().equals(itemProducto.getMovimiento().getCircuito().getCirapl())) {
            itemProducto.setCantidadOriginal(itemProducto.getCantidad());
        }

        asignarProductoItemDetalle(itemProducto);

    }

    public void asignarProducto(ItemMovimientoCompra itemProducto, Producto producto) throws ExcepcionGeneralSistema {

        String sErrores = "";

        if (itemProducto.getMovimiento().getListaCosto() == null) {
            sErrores += "- El comprobante no tiene una lista de costo asignada \n";
        }

        if (itemProducto.getMovimiento().getMonedaSecundaria() == null) {
            sErrores += "- El comprobante no tiene una moneda secundaria asignada \n";
        }

        if (itemProducto.getMovimiento().getCircuito().getPermiteProductosDuplicados().equals("N")) {
            if (existeProducto(itemProducto.getMovimiento(), producto)) {

                sErrores += "- El producto que intenta agregar ya existe en el comprobante " + itemProducto.getMovimiento().getComprobante().getDescripcion() + " y no está permitido agregar items duplicados \n";
            }
        }

        if (itemProducto.getItemsAplicacion() != null) {
            if (!itemProducto.getItemsAplicacion().isEmpty()) {
                sErrores += "- Este items tiene aplicaciones, no es posible modificarlo \n";
            }
        }

        if (producto.getCuentaContableCompra() == null) {
            sErrores += "- El producto no tiene una cuenta contable de compra asignada \n";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }

        itemProducto.setProducto(producto);
        itemProducto.setDescripcion(producto.getDescripcion());
        itemProducto.setCodigoProveedor(producto.getCodigoProveedor());
        itemProducto.setProductoOriginal(producto);
        itemProducto.setUnidadMedida(producto.getUnidadDeMedida());
        itemProducto.setConcepto(producto.getConceptoProveedor());
        itemProducto.setAtributo1("");
        itemProducto.setAtributo2("");
        itemProducto.setAtributo3("");
        itemProducto.setAtributo4("");
        itemProducto.setAtributo5("");
        itemProducto.setAtributo6("");
        itemProducto.setAtributo7("");

        asignarPrecioItem(itemProducto);
        asignarProductoItemDetalle(itemProducto);
        calcularImportes(itemProducto.getMovimiento(), "U");
    }

    public void asignarProductoItemDetalle(MovimientoCompra m) {

        if (m.getCircuito().getActualizaCompra().equals("S")) {
            return;
        }

        if (m.getItemsProducto() != null) {

            for (ItemMovimientoCompra i : m.getItemsProducto()) {

                i.setAtributo1(m.getAtributo1() == null ? "" : m.getAtributo1());
                i.setAtributo2(m.getAtributo2() == null ? "" : m.getAtributo2());
                i.setAtributo3(m.getAtributo3() == null ? "" : m.getAtributo3());
                i.setAtributo4(m.getAtributo4() == null ? "" : m.getAtributo4());
                i.setAtributo5(m.getAtributo5() == null ? "" : m.getAtributo5());
                i.setAtributo6(m.getAtributo6() == null ? "" : m.getAtributo6());
                i.setAtributo7(m.getAtributo7() == null ? "" : m.getAtributo7());

                asignarProductoItemDetalle(i);
            }
        }

    }

    public void asignarProductoItemDetalle(ItemMovimientoCompra itemProducto) {

        if (itemProducto.getMovimiento().getCircuito().getActualizaCompra().equals("S")) {
            return;
        }

        if (itemProducto.getItemsDetalle() != null) {
            itemProducto.getItemsDetalle().clear();
        }

        ItemDetalleItemMovimientoCompra itemDetalle = nuevoItemDetalle(itemProducto);
        itemDetalle.setAtributo1(itemProducto.getAtributo1());
        itemDetalle.setAtributo2(itemProducto.getAtributo2());
        itemDetalle.setAtributo3(itemProducto.getAtributo3());
        itemDetalle.setAtributo4(itemProducto.getAtributo4());
        itemDetalle.setAtributo5(itemProducto.getAtributo5());
        itemDetalle.setAtributo6(itemProducto.getAtributo6());
        itemDetalle.setAtributo7(itemProducto.getAtributo7());
        itemDetalle.setCantidad(itemProducto.getCantidad());

        if (itemProducto.getCantidad() != null) {
            itemDetalle.setCantidad(itemProducto.getCantidad());
        }
    }

    public void asignarProductoItemDetalleFromItemAplicado(ItemMovimientoCompra itemProducto) {

        if (itemProducto.getItemsDetalle() != null) {
            itemProducto.getItemsDetalle().clear();
        }

        for (ItemDetalleItemMovimientoCompra itemDetalleAplicado : itemProducto.getItemAplicado().getItemsDetalle()) {

            ItemDetalleItemMovimientoCompra itemDetalle = nuevoItemDetalle(itemProducto);
            itemDetalle.setAtributo1(itemDetalleAplicado.getAtributo1());
            itemDetalle.setAtributo2(itemDetalleAplicado.getAtributo2());
            itemDetalle.setAtributo3(itemDetalleAplicado.getAtributo3());
            itemDetalle.setAtributo4(itemDetalleAplicado.getAtributo4());
            itemDetalle.setAtributo5(itemDetalleAplicado.getAtributo5());
            itemDetalle.setAtributo6(itemDetalleAplicado.getAtributo6());
            itemDetalle.setAtributo7(itemDetalleAplicado.getAtributo7());
            itemDetalle.setCantidad(itemDetalleAplicado.getCantidad());

        }
    }

    private void asignarPrecioItem(ItemMovimientoCompra itemProducto) {

        if (itemProducto.getProducto() == null) {
            return;
        }

        itemProducto.setPrecio(listaCostoRN.getPrecioByProducto(itemProducto.getMovimiento().getListaCosto(), itemProducto.getProducto(), itemProducto.getMovimiento().getCotizacion()));
        itemProducto.setPrecioSecundario(itemProducto.getPrecio().divide(itemProducto.getMovimiento().getCotizacion(), 2, RoundingMode.HALF_UP));

    }

    public void asignarProveeedor(MovimientoCompra m, EntidadComercial proveedor) throws Exception {

        m.setProveedor(proveedor);
        m.setProveedorCuentaCorriente(proveedor);
        m.setRazonSocial(proveedor.getRazonSocial());
        m.setNrodoc(proveedor.getNrodoc());
        m.setTipoDocumento(proveedor.getTipoDocumento());

        m.setProvincia(proveedor.getProvincia());
        m.setLocalidad(proveedor.getLocalidad());
        m.setBarrio(proveedor.getBarrio());
        m.setDireccion(proveedor.getDireccion());

        if (proveedor.getDireccionesDeEntrega() != null && !proveedor.getDireccionesDeEntrega().isEmpty()) {

            asignarDireccionEntrega(m, proveedor.getDireccionesDeEntrega().get(0));
        }

        m.setCondicionDeIva(proveedor.getCondicionDeIva());
        m.setCondicionDePago(proveedor.getCondicionPagoProveedor());

        if (m.getComprobanteProveedor() == null) {
            m.setCondicionDePago(proveedor.getCondicionPagoProveedor());

        } else if (m.getComprobanteProveedor().getTipoImputacion().equals("CO")) {
            m.setCondicionDePago(condicionPagoProveedorRN.getCondicionPagoProveedor("A"));
        }

        m.setListaCosto(proveedor.getListaCosto());
        m.setMonedaListaCosto(proveedor.getListaCosto().getMoneda());
        m.setComprador(proveedor.getComprador());

        asignarFormulario(m);

    }

    public void asignarDireccionEntrega(MovimientoCompra movimiento, DireccionEntregaEntidad direccionEntrega) {

        movimiento.setDireccionEntrega(direccionEntrega);
        movimiento.setProvincia(direccionEntrega.getProvincia());
        movimiento.setLocalidad(direccionEntrega.getLocalidad());
        movimiento.setBarrio(direccionEntrega.getBarrio());
        movimiento.setDireccion(direccionEntrega.getDireccion());
//        movimiento.setNumero(direccionEntrega.getNumero());
        movimiento.setDepartamentoPiso(direccionEntrega.getDepartamentoPiso());
        movimiento.setDepartamentoNumero(direccionEntrega.getDepartamentoNumero());
    }

    public boolean existeProducto(MovimientoCompra movimiento, Producto producto) {

        if (movimiento.getItemsProducto() == null || movimiento.getItemsProducto().isEmpty()) {
            return false;
        }

        if (producto == null) {
            return false;
        }

        for (ItemMovimientoCompra item : movimiento.getItemsProducto()) {

            if (item.getProducto() != null && item.getProducto().equals(producto)) {
                return true;
            }
        }

        return false;
    }

    public double getPendienteIngresoByProducto(Producto producto, Deposito deposito) {

        return compraDAO.getPendienteIngresoByProducto(producto, deposito);

    }
}
