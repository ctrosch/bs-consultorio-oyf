/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.rn;

import bs.administracion.rn.ParametrosRN;
import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.modelo.ImpuestoPorEntidad;
import bs.facturacion.dao.FacturacionDAO;
import bs.facturacion.dao.ItemFacturacionDAO;
import bs.facturacion.modelo.CircuitoFacturacion;
import bs.facturacion.modelo.ItemMovimientoFacturacion;
import bs.facturacion.modelo.ItemMovimientoFacturacionDetalle;
import bs.facturacion.modelo.ItemMovimientoFacturacionKit;
import bs.facturacion.modelo.MovimientoFacturacion;
import bs.facturacion.modelo.ParametroFacturacion;
import bs.facturacion.vista.PendienteFacturacionDetalle;
import bs.facturacion.vista.PendienteFacturacionGrupo;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Concepto;
import bs.global.modelo.Formulario;
import bs.global.modelo.ImpuestoPorConcepto;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.FormularioRN;
import bs.global.rn.MonedaRN;
import bs.global.rn.PuntoVentaRN;
import bs.stock.modelo.ComposicionFormula;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.ItemComposicionFormulaComponente;
import bs.stock.modelo.ItemComposicionFormulaProceso;
import bs.stock.modelo.MovimientoStock;
import bs.stock.modelo.ParametroStock;
import bs.stock.modelo.Producto;
import bs.stock.modelo.Stock;
import bs.stock.rn.ComposicionFormulaRN;
import bs.stock.rn.MovimientoStockRN;
import bs.stock.rn.ParametroStockRN;
import bs.stock.rn.ProductoRN;
import bs.stock.rn.StockRN;
import bs.taller.modelo.ItemProductoTaller;
import bs.taller.modelo.ItemServicioTaller;
import bs.taller.modelo.MovimientoTaller;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.tesoreria.rn.MovimientoTesoreriaRN;
import bs.ventas.modelo.ItemImpuestoVenta;
import bs.ventas.modelo.ItemPercepcionVenta;
import bs.ventas.modelo.ItemProductoVenta;
import bs.ventas.modelo.ItemTotalVenta;
import bs.ventas.modelo.MovimientoVenta;
import bs.ventas.rn.CondicionPagoVentaRN;
import bs.ventas.rn.CuentaCorrienteRN;
import bs.ventas.rn.ListaPrecioVentaRN;
import bs.ventas.rn.MovimientoVentaRN;
import bs.wsafip.dao.ParametroWSDAO;
import bs.wsafip.modelo.ParametroWS;
import bs.wsafip.rn.WSFEv1;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
public class FacturacionRN implements Serializable {

    @EJB
    private ParametroWSDAO parametroWSDAO;
    @EJB
    private ParametroStockRN parametroStockRN;
    @EJB
    private MonedaRN monedaRN;
    @EJB
    private FacturacionDAO facturacionDAO;
    @EJB
    private ItemFacturacionDAO itemFacturacionDAO;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private MovimientoVentaRN ventaRN;
    @EJB
    private MovimientoTesoreriaRN tesoreriaRN;
    @EJB
    private MovimientoStockRN movimientoStockRN;
    @EJB
    private ParametroFacturacionRN parametroFacturacionRN;
    @EJB
    private ParametrosRN parametrosRN;
    @EJB
    private ProductoRN productoRN;
    @EJB
    private StockRN stockRN;
    @EJB
    private ListaPrecioVentaRN listaPrecioRN;
    @EJB
    private CircuitoFacturacionRN circuitoRN;
    protected @EJB
    PuntoVentaRN puntoVentaRN;
    protected @EJB
    CondicionPagoVentaRN condicionDePagoVentaRN;
    @EJB
    protected ComposicionFormulaRN composicionFormulaRN;

    @EJB
    private CuentaCorrienteRN cuentaCorrienteRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized MovimientoFacturacion guardar(MovimientoFacturacion movimiento) throws Exception {

        preSave(movimiento);
        calcularImportes(movimiento, "U", false);
        control(movimiento);

        if (movimiento.getId() == null) {

            WSFEv1 wsfe = null;
            ParametroWS p = parametroWSDAO.getObjeto("01");

            generarMovimientosAdicionales(movimiento);

            if (movimiento.getComprobanteVenta() != null
                    && movimiento.getCircuito().getActualizaVenta().equals("S")
                    && movimiento.getMovimientoVenta().getComprobante().getComprobanteInterno().equals("N")
                    && movimiento.getMovimientoVenta().getPuntoVenta().getImplementaFE().equals("S")
                    && p != null
                    && p.getTipoAutorizacion().equals("I")) {

                wsfe = new WSFEv1(p, movimiento.getMovimientoVenta().getPuntoVenta().getWebservice());
            }

            tomarNumeroFormulario(movimiento, wsfe);
            facturacionDAO.crear(movimiento);

            //Si es distinto de null significa que es un comprobante de venta
            if (wsfe != null) {

                try {
                    wsfe.autorizar(movimiento.getMovimientoVenta());
                    movimiento = facturacionDAO.editar(movimiento);
                } catch (Exception ex) {

                    facturacionDAO.eliminar(movimiento);
                    throw new ExcepcionGeneralSistema("Error autorizando comprobante: " + ex.getMessage() + ". Intente nuevamente");
                }
            }
        } else {
            movimiento = facturacionDAO.editar(movimiento);
        }

        actualizarCantidadesPendientes(movimiento);
        movimientoStockRN.generarStock(movimiento.getMovimientoStock());
        return movimiento;
    }

    public MovimientoFacturacion nuevoMovimiento() {
        return new MovimientoFacturacion();
    }

    public MovimientoFacturacion nuevoMovimiento(CircuitoFacturacion circuito,
            String codFC, String sucFC,
            String codVT, String sucVT,
            String codST, String sucST,
            String codCJ, String sucCJ) throws ExcepcionGeneralSistema, Exception {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("El circuito no puede ser nulo");
        }

        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(sucFC);
        PuntoVenta puntoVentaStock = puntoVentaRN.getPuntoVenta(sucST);

        if (puntoVentaStock == null) {
            puntoVentaStock = puntoVenta;
        }

        if (puntoVentaStock != null && puntoVenta == null) {
            puntoVenta = puntoVentaStock;
        }

        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No se encontró punto de venta de facturación " + sucFC);
        }

        Comprobante comprobante = null;
        Comprobante comprobanteCJ = null;
        Comprobante comprobanteST = null;

        //Siempre tiene que haber un comprobante de venta para los cálculos
        Comprobante comprobanteVT = circuito.getComprobanteVenta();

        if (circuito.getItemCircuitoVenta() == null || circuito.getItemCircuitoVenta().isEmpty()) {
            throw new ExcepcionGeneralSistema("El circuito debe tener configurado al menos un comprobante de venta");
        } else if (comprobanteVT == null) {
            comprobanteVT = circuito.getItemCircuitoVenta().get(0).getComprobante();
        }

        if (circuito.getActualizaFacturacion().equals("S")) {

            comprobante = circuito.getComprobanteFacturacion();

        } else if (circuito.getActualizaVenta().equals("S")) {

            comprobante = circuito.getComprobanteVenta();

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

        MovimientoFacturacion m = crearMovimiento(circuito, comprobante, puntoVenta, puntoVentaStock);

        if (comprobanteCJ != null) {
            m.setComprobanteTesoreria(comprobanteCJ);
            MovimientoTesoreria mt = tesoreriaRN.generarComprobante(m);
            m.setMovimientoTesoreria(mt);
        }

        if (comprobanteVT != null) {
            m.setComprobanteVenta(comprobanteVT);
            ventaRN.generarItemsMovimientoVenta(m);
        }

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

    public MovimientoFacturacion nuevoMovimiento(CircuitoFacturacion circuito,
            String codFC, String sucFC,
            String codVT, String sucVT,
            String codST, String sucST,
            String codCJ, String sucCJ,
            PendienteFacturacionGrupo pendienteGrupo,
            List<PendienteFacturacionDetalle> itemsPendientes,
            boolean congelaCotizacion) throws ExcepcionGeneralSistema, Exception {

        if (!tengoItemsSeleccionados(itemsPendientes)) {
            throw new ExcepcionGeneralSistema("No existen items seleccionados para generar el movimiento");
        }

        MovimientoFacturacion m = nuevoMovimiento(circuito, codFC, sucFC, codVT, sucVT, codST, sucST, codCJ, sucCJ);

        m.setCanalVenta(pendienteGrupo.getCanalVenta());

        m.setCliente(pendienteGrupo.getCliente());
        m.setClienteCuentaCorriente(pendienteGrupo.getCliente());
        m.setCondicionDeIva(pendienteGrupo.getCliente().getCondicionDeIva());

        m.setRazonSocial(pendienteGrupo.getRazonSocial());
        m.setNrodoc(pendienteGrupo.getNrodoc());
        m.setTipoDocumento(pendienteGrupo.getTipoDocumento());
        m.setProvincia(pendienteGrupo.getLocalidad().getProvincia());
        m.setLocalidad(pendienteGrupo.getLocalidad());

        m.setBarrio(pendienteGrupo.getCliente().getBarrio());
        m.setDireccion(pendienteGrupo.getDireccion());

        m.setDepartamentoPiso(pendienteGrupo.getCliente().getDepartamentoPiso());
        m.setDepartamentoNumero(pendienteGrupo.getCliente().getDepartamentoNumero());

        m.setCondicionDePago(pendienteGrupo.getCondicionDePago());
        m.setListaDePrecio(pendienteGrupo.getListaDePrecio());
        m.setVendedor(pendienteGrupo.getVendedor());
        m.setRepartidor(pendienteGrupo.getRepartidor());

        m.setMonedaListaPrecio(pendienteGrupo.getListaDePrecio().getMoneda());
        m.setMonedaRegistracion(pendienteGrupo.getMonedaRegistracion());

        if (congelaCotizacion) {
            m.setCotizacion(pendienteGrupo.getCotizacion());
        }

        if (pendienteGrupo.getDeposito() != null) {
            m.setDeposito(pendienteGrupo.getDeposito());
        }

        if (pendienteGrupo.getDepositoTransferencia() != null) {
            m.setDeposito(pendienteGrupo.getDepositoTransferencia());
        }

        if (pendienteGrupo.getTransporte() != null) {
            m.setTransporte(pendienteGrupo.getTransporte());
        }

        m.setMovimientoTaller(pendienteGrupo.getMovimientoTaller());

        generarItems(m, itemsPendientes);

        if (pendienteGrupo.getModulo() != null) {

            if (!pendienteGrupo.getModulo().equals("TL")) {
                buscarObservacionesMovimientosAplicados(m, itemsPendientes);
            } else {
                //copiar obsrvaciones de taller
            }
        }

        asignarFormulario(m);

        return m;
    }

    public boolean tengoItemsSeleccionados(List<PendienteFacturacionDetalle> itemsPendientes) {

        if (itemsPendientes == null || itemsPendientes.isEmpty()) {
            return false;
        }

        for (PendienteFacturacionDetalle p : itemsPendientes) {

            if (p.isSeleccionado()) {
                return true;
            }
        }

        return false;
    }

    private MovimientoFacturacion crearMovimiento(CircuitoFacturacion circuito, Comprobante comprobante, PuntoVenta puntoVenta, PuntoVenta puntoVentaStock) throws ExcepcionGeneralSistema, Exception {

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("El comprobante no puede ser nulo");
        }

        MovimientoFacturacion movimiento = new MovimientoFacturacion();
        ParametroFacturacion pf = parametroFacturacionRN.getParametro();
        BigDecimal cotizacion = monedaRN.getCotizacionDia(pf.getMonedaSecundaria().getCodigo());

        movimiento.setEmpresa(puntoVenta.getEmpresa());
        movimiento.setSucursal(puntoVenta.getSucursal());
        movimiento.setPuntoVenta(puntoVenta);
        movimiento.setUnidadNegocio(puntoVenta.getUnidadNegocio());

        movimiento.setCircuito(circuito);
        movimiento.setPuntoVentaStock(puntoVentaStock);
        movimiento.setCongelaBonificacion(circuito.getCongelaBonificacion());
        movimiento.setCongelaPrecio(circuito.getCongelaPrecio());

        movimiento.setComprobante(comprobante);
        movimiento.setFechaMovimiento(new Date());
        movimiento.setFechaVencimiento(new Date());
        movimiento.setMonedaSecundaria(pf.getMonedaSecundaria());
        movimiento.setMonedaRegistracion(comprobante.getMonedaRegistracion());
        movimiento.setCotizacion(cotizacion);

        if (pf.getClientePredeterminado() != null) {
            asignarCliente(movimiento, pf.getClientePredeterminado());
        }

        if (comprobante.getDeposito() != null) {
            movimiento.setDeposito(comprobante.getDeposito());
        }

        if (comprobante.getDepositoTransferencia() != null) {
            movimiento.setDepositoTransferencia(comprobante.getDepositoTransferencia());
        }

        asignarFormulario(movimiento);
        return movimiento;
    }

    public ItemMovimientoFacturacion nuevoItemProducto(MovimientoFacturacion movimiento) throws ExcepcionGeneralSistema {

        ItemMovimientoFacturacion nItem = new ItemMovimientoFacturacion();
//        nItem.setIdItemAplicacion(nItem.getId());
        nItem.setNroitm(movimiento.getItemsProducto().size() + 1);
        nItem.setCotizacion(movimiento.getCotizacion());

        nItem.setMovimiento(movimiento);
        //nItem.setMovimientoAplicacion(movimiento);
        nItem.setMovimientoInicial(movimiento.getId());
        nItem.setMovimientoOriginal(movimiento.getId());

        nItem.setDeposito(movimiento.getDeposito());

        reorganizarNroItem(movimiento);

        return nItem;
    }

    public void agregarItem(MovimientoFacturacion m) throws ExcepcionGeneralSistema {

        puedoAgregarItem(m);
        m.getItemsProducto().add(nuevoItemProducto(m));
    }

    public ItemMovimientoFacturacionDetalle nuevoItemDetalle(ItemMovimientoFacturacion itemProducto) {

        if (itemProducto.getItemsDetalle() == null) {
            itemProducto.setItemsDetalle(new ArrayList<>());
        }

        ItemMovimientoFacturacionDetalle itemDetalle = new ItemMovimientoFacturacionDetalle();

        itemDetalle.setNroitm(itemProducto.getItemsDetalle().size() + 1);
        itemDetalle.setItemProducto(itemProducto);
        itemDetalle.setProducto(itemProducto.getProducto());
        itemDetalle.setDescripcion(itemProducto.getDescripcion());
        itemDetalle.setDeposito(itemProducto.getDeposito());
        itemDetalle.setUnidadMedida(itemProducto.getUnidadMedida());

        itemProducto.getItemsDetalle().add(itemDetalle);

        return itemDetalle;
    }

    public ItemMovimientoFacturacionKit nuevoItemKit(ItemMovimientoFacturacion itemProducto) {

        if (itemProducto.getItemsKit() == null) {
            itemProducto.setItemsKit(new ArrayList<ItemMovimientoFacturacionKit>());
        }

        ItemMovimientoFacturacionKit itemKit = new ItemMovimientoFacturacionKit();

        itemKit.setNroitm(itemProducto.getItemsKit().size() + 1);
        itemKit.setItemProducto(itemProducto);
        //itemKit.setProducto(itemProducto.getProducto());
        //itemKit.setDescripcion(itemProducto.getDescripcion());
        itemKit.setDeposito(itemProducto.getDeposito());
        //itemKit.setUnidadMedida(itemProducto.getUnidadMedida());

        itemProducto.getItemsKit().add(itemKit);

        return itemKit;
    }

    public void eliminarItemProducto(MovimientoFacturacion movimiento, ItemMovimientoFacturacion nItem) throws Exception {

        if (movimiento == null) {
            throw new ExcepcionGeneralSistema("Movimiento vacío, nada para eliminar");
        }

        if (movimiento.getId() != null && !movimiento.getFormulario().getModfor().equals("FC")) {
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

        for (ItemMovimientoFacturacion ip : movimiento.getItemsProducto()) {

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
            ItemMovimientoFacturacion remove = movimiento.getItemsProducto().remove(indiceItemProducto);
            if (remove != null) {

                if (movimiento.getId() != null && remove.getId() != null) {
                    facturacionDAO.eliminar(ItemMovimientoFacturacion.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(movimiento);
        calcularImportes(movimiento, false);
    }

    public void eliminarItemDetalle(MovimientoFacturacion movimiento, ItemMovimientoFacturacion itemProducto, ItemMovimientoFacturacionDetalle itemDetalle) throws Exception {

        if (movimiento == null) {
            throw new ExcepcionGeneralSistema("Movimiento vacío, nada para eliminar");
        }

        if (itemDetalle == null) {
            throw new ExcepcionGeneralSistema("Item detalle vacío, nada para eliminar");
        }

        if (movimiento.getId() != null && !movimiento.getFormulario().getModfor().equals("FC")) {
            throw new ExcepcionGeneralSistema("Solo se puede eliminar detalles de atributos de comprobantes de facturación");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemProducto = -1;

        for (ItemMovimientoFacturacionDetalle id : itemProducto.getItemsDetalle()) {

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
            ItemMovimientoFacturacionDetalle remove = itemProducto.getItemsDetalle().remove(indiceItemProducto);
            if (remove != null) {
                if (movimiento.getId() != null && itemProducto.getId() != null && remove.getId() != null) {
                    facturacionDAO.eliminar(ItemMovimientoFacturacionDetalle.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(movimiento);
        calcularImportes(movimiento, false);
    }

    public void eliminarItemKit(ItemMovimientoFacturacionKit itemKit) throws Exception {

        if (itemKit == null) {
            throw new ExcepcionGeneralSistema("Item kit vacío, nada para eliminar");
        }

        if (itemKit.getItemProducto() == null) {
            throw new ExcepcionGeneralSistema("Item producto vacío, nada para eliminar");
        }

        if (itemKit.getItemProducto().getMovimiento() == null) {
            throw new ExcepcionGeneralSistema("Movimiento vacío, nada para eliminar");
        }

        if (itemKit.getItemProducto().getMovimiento().getId() != null && !itemKit.getItemProducto().getMovimiento().getFormulario().getModfor().equals("FC")) {
            throw new ExcepcionGeneralSistema("El comprobante ha sido guardado, no es posible modificar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemProducto = -1;

        for (ItemMovimientoFacturacionKit id : itemKit.getItemProducto().getItemsKit()) {

            if (id.getNroitm() >= 0) {

                if (id.getNroitm() == itemKit.getNroitm()) {
                    indiceItemProducto = i;
                }
            }
            i++;
        }
        i = 0;

        //Borramos los items productos
        if (indiceItemProducto >= 0) {
            ItemMovimientoFacturacionKit remove = itemKit.getItemProducto().getItemsKit().remove(indiceItemProducto);
            if (remove != null) {
                if (itemKit.getItemProducto().getMovimiento().getId() != null && itemKit.getItemProducto().getId() != null && remove.getId() != null) {
                    facturacionDAO.eliminar(ItemMovimientoFacturacionKit.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(itemKit.getItemProducto().getMovimiento());
        asignarPrecioItem(itemKit.getItemProducto());
        calcularImportes(itemKit.getItemProducto().getMovimiento(), false);
    }

    public void puedoAgregarItem(MovimientoFacturacion movimiento) throws ExcepcionGeneralSistema {

        if (movimiento.getCircuito().getPermiteAgregarItems().equals("N")) {
            throw new ExcepcionGeneralSistema("El circuito no permite agregar items");
        }

        if (movimiento.getListaDePrecio() == null) {
            throw new ExcepcionGeneralSistema("Seleccione una lista de precios");
        }

        if (movimiento.getMonedaSecundaria() == null) {
            throw new ExcepcionGeneralSistema("La moneda secundaria no puede estar vacía");
        }

        if (movimiento.getCotizacion() == null || movimiento.getCotizacion().compareTo(BigDecimal.ONE) < 0) {
            throw new ExcepcionGeneralSistema("La cotización del comprobante no puede se nula o menor a 1");
        }
    }

    public void reorganizarNroItem(MovimientoFacturacion movimiento) {

        //Reorganizamos los números de items
        int i = 1;
        for (ItemMovimientoFacturacion ip : movimiento.getItemsProducto()) {
            ip.setNroitm(i);
            i++;
            int d = 0;

            for (ItemMovimientoFacturacionDetalle itemDetalle : ip.getItemsDetalle()) {
                itemDetalle.setNroitm(d);
                d++;
            }

            d = 0;
            for (ItemMovimientoFacturacionKit itemKit : ip.getItemsKit()) {
                itemKit.setNroitm(d);
                d++;
            }
        }

//        i = 1;
//        for (ItemMovimientoFacturacion ip : movimiento.getItemsAplicados()) {
//            ip.setNroitm(i);
//            i++;
//        }
    }

    public void asignarFormulario(MovimientoFacturacion m) throws ExcepcionGeneralSistema {

        if (m.getComprobante() == null || m.getPuntoVenta() == null) {
            return;
        }

        Formulario formulario = null;
        Formulario formularioCJ = null;

        if (m.getCircuito().getActualizaVenta().equals("S")) {

            if (m.getCondicionDeIva() != null) {
                formulario = formularioRN.getFormulario(m.getComprobante(), m.getPuntoVenta(), m.getCondicionDeIva().getCodigo());

                if (m.getMovimientoTesoreria() != null) {
                    formularioCJ = formularioRN.getFormulario(m.getMovimientoTesoreria().getComprobante(), m.getMovimientoTesoreria().getPuntoVenta(), m.getCondicionDeIva().getCodigo());
                    m.getMovimientoTesoreria().setFormulario(formularioCJ);
                }
            }

        } else if (m.getCircuito().getActualizaFacturacion().equals("S")) {

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

    public void seleccionarTodo(List<PendienteFacturacionDetalle> itemsPendiente, boolean seleccionarTodo) {

        if (itemsPendiente == null) {
            return;
        }

        for (PendienteFacturacionDetalle i : itemsPendiente) {
            i.setSeleccionado(seleccionarTodo);

            if (i.isSeleccionado()) {
                i.setCantidadSeleccionada(i.getCantidad());
            } else {
                i.setCantidadSeleccionada(0);
            }
        }
    }

    public List<Object[]> getPendienteGrupoNew(String consultaGrupo) throws SQLException {
        return facturacionDAO.getPendienteGrupoNew(consultaGrupo);
    }

    public List<Object[]> getPendienteDetalleNew(String consultaDetalle) {
        return facturacionDAO.getPendienteDetalleNew(consultaDetalle);
    }

    public List<PendienteFacturacionGrupo> getPendienteGrupo(String consultaGrupo) {
        return facturacionDAO.getPendienteGrupo(consultaGrupo);
    }

    public List<PendienteFacturacionDetalle> getPendienteDetalle(String consultaDetalle) {
        return facturacionDAO.getPendienteDetalle(consultaDetalle);
    }

    public void generarItems(MovimientoFacturacion movimiento, List<PendienteFacturacionDetalle> itemsPendiente) throws ExcepcionGeneralSistema {

        if (itemsPendiente.isEmpty()) {
            return;
        }

        //Tomo el numero y fecha del comprobante asociado para la NC de factura de crédito
        //Como solo el metodo de AgregarCmpAsoc de WSFEV1 un solo comprobante tomo el primero de la lista
        for (PendienteFacturacionDetalle itemPendiente : itemsPendiente) {
            if (itemPendiente.isSeleccionado()) {

                movimiento.setFechaCpbteAsociado(itemPendiente.getFchmov());
                movimiento.setNumeroCpbteAsociado(itemPendiente.getNrofor());
                break;
            }

        }

        //Vaciamos todos los items del movimiento.
        movimiento.getItemsProducto().clear();

        int cantSel = 0;
        for (PendienteFacturacionDetalle itemPendiente : itemsPendiente) {

            if (itemPendiente.isSeleccionado()) {

                cantSel++;
                ItemMovimientoFacturacion itemProducto = nuevoItemProducto(movimiento);

                itemProducto.setProducto(itemPendiente.getProducto());
                itemProducto.setProductoOriginal(itemPendiente.getProducto());
                itemProducto.setDescripcion(itemPendiente.getDescripcion());
                itemProducto.setObservaciones(itemPendiente.getObservacion());
                itemProducto.setConcepto(itemPendiente.getProducto().getConceptoVenta());
                itemProducto.setUnidadMedida(itemPendiente.getUnidadMedida());
                itemProducto.setCantidad(new BigDecimal(itemPendiente.getCantidadSeleccionada()));
                itemProducto.setCantidadOriginal(new BigDecimal(itemPendiente.getCantidad()));
                itemProducto.setPorcentajeBonificacion1(itemPendiente.getPorcentajeBonificacion1());
                itemProducto.setPorcentajeBonificacion2(itemPendiente.getPorcentajeBonificacion2());
                itemProducto.setPrecio(itemPendiente.getPrecio());
                itemProducto.setPrecioSecundario(itemPendiente.getPrecioSecundario());

                itemProducto.setCostoManual(itemPendiente.getItemProducto().isCostoManual());

                //Si el item está marcado para que tenga costo manual no actualiza y toma el costo del item aplicado
                if (!itemProducto.isCostoManual()) {

                    if (itemPendiente.getProducto().getMonedaReposicion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
                        itemProducto.setPrecioCosto(itemPendiente.getProducto().getPrecioReposicion().multiply(movimiento.getCotizacion()));
                        itemProducto.setPrecioCostoSecundario(itemPendiente.getProducto().getPrecioReposicion());
                    } else {
                        itemProducto.setPrecioCosto(itemPendiente.getProducto().getPrecioReposicion());
                        itemProducto.setPrecioCostoSecundario(itemPendiente.getProducto().getPrecioReposicion().divide(movimiento.getCotizacion(), 2, RoundingMode.HALF_UP));
                    }

                } else {

                    itemProducto.setPrecioCosto(itemPendiente.getItemProducto().getPrecioCosto());
                    itemProducto.setPrecioCostoSecundario(itemPendiente.getItemProducto().getPrecioCostoSecundario());

                }

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
                if (itemProducto.getMovimiento().getCircuito().getActualizaFacturacion().equals("N")
                        || itemProducto.getMovimiento().getCircuito().getComprometeStock().equals("S")) {

                    if (itemProducto.getItemAplicado() != null && itemProducto.getItemAplicado().getItemsDetalle() != null) {
                        asignarProductoItemDetalleFromItemAplicado(itemProducto);
                    } else {
                        asignarProductoItemDetalle(itemProducto);
                    }

                    if (itemProducto.getItemsDetalle() == null || itemProducto.getItemsDetalle().isEmpty()) {
                        asignarProductoItemDetalle(itemProducto);
                    }
                }

                /**
                 * COPIAR LOS ITEMS KITS DEL ITEM APLICADO *
                 */
                if (itemPendiente.getItemProducto().getItemsKit() != null) {

                    for (ItemMovimientoFacturacionKit ika : itemPendiente.getItemProducto().getItemsKit()) {

//                        System.err.println("copiar items kit");
                        ItemMovimientoFacturacionKit ikn = nuevoItemKit(itemProducto);
                        ikn.setProducto(ika.getProducto());
                        ikn.setDescripcion(ika.getDescripcion());
                        ikn.setDeposito(ika.getDeposito());
                        ikn.setCantidad(ika.getCantidad());
                        ikn.setCantidadNominal(ika.getCantidadNominal());
                        ikn.setUnidadMedida(ika.getUnidadMedida());
                        ikn.setPrecio(ika.getPrecio());
                        ikn.setPrecioSecundario(ika.getPrecioSecundario());

                        ikn.setCostoManual(ika.isCostoManual());
                        ikn.setPrecioCosto(ika.getPrecioCosto());
                        ikn.setPrecioCostoSecundario(ika.getPrecioCostoSecundario());

                        ikn.setTotalLinea(ika.getTotalLinea());
                        ikn.setTotalLineaSecundario(ika.getTotalLineaSecundario());
                    }
                }

                if (movimiento.getCircuito().getCongelaPrecio().equals("N")
                        && itemProducto.getProducto().getCongelaPrecioEnFacturacion().equals("N")) {
                    asignarPrecioItem(itemProducto);
                }

                //Agregamos el item a la lista
                movimiento.getItemsProducto().add(itemProducto);

            }
        }
        if (cantSel == 0) {
            throw new ExcepcionGeneralSistema("No ha seleccionado ningún producto");
        }
    }

    public void calcularImportesLineaByPrecio(ItemMovimientoFacturacion itemProducto, boolean precioConImpuesto) {

        if (itemProducto.getConcepto() == null) {
            return;
        }

        if (itemProducto.getCantidad() == null) {
            itemProducto.setCantidad(BigDecimal.ZERO);
        }

        if (itemProducto.getPrecio() == null) {
            itemProducto.setPrecio(BigDecimal.ZERO);
        }

        if (itemProducto.getPrecioConImpuesto() == null) {
            itemProducto.setPrecioConImpuesto(BigDecimal.ZERO);
        }

        if (itemProducto.getPorcentajeBonificacion1() == null) {
            itemProducto.setPorcentajeBonificacion1(BigDecimal.ZERO);
        }

        if (itemProducto.getPorcentajeBonificacion2() == null) {
            itemProducto.setPorcentajeBonificacion2(BigDecimal.ZERO);
        }

        //Si la moneda de registración del movimiento, es la moneda secundaria, calculamos el importe en moneda primaria
        if (itemProducto.getMovimiento().getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
            itemProducto.setPrecio(itemProducto.getPrecioSecundario().multiply(itemProducto.getCotizacion()).setScale(4, RoundingMode.HALF_UP));
        }

        //Buscamos el impuesto iva asociado al concepto para asignarle el procentaje de impuesto al item.
        for (ImpuestoPorConcepto impuestoConcepto : itemProducto.getConcepto().getImpuestos()) {

            /* Buscamos el impuesto iva para asignarle el porcentaje de tasa al
             * item y verificamos que el comprobante tenga asignado items de
             * impuesto */
            if (impuestoConcepto.getCodimp().equals("IVA")
                    && itemProducto.getMovimiento().getItemsImpuestoVenta() != null
                    && !itemProducto.getMovimiento().getItemsImpuestoVenta().isEmpty()) {
                itemProducto.setPorcentajeImpuesto(impuestoConcepto.getTasa());
                break;
            }
        }

        BigDecimal cien = new BigDecimal(100);

        itemProducto.setPrecio(itemProducto.getPrecio().setScale(4, RoundingMode.HALF_UP));
        itemProducto.setPrecioConImpuesto(itemProducto.getPrecioConImpuesto().setScale(4, RoundingMode.HALF_UP));

        if (precioConImpuesto && itemProducto.getPrecioConImpuesto().compareTo(BigDecimal.ZERO) > 0) {

            itemProducto.setPrecio(itemProducto.getPrecioConImpuesto()
                    .divide(itemProducto.getPorcentajeImpuesto().add(cien)
                            .divide(cien, 4, RoundingMode.HALF_UP), 4, RoundingMode.HALF_UP));

        } else {

            itemProducto.setPrecioConImpuesto(itemProducto.getPrecio()
                    .multiply(itemProducto.getPorcentajeImpuesto().add(cien).divide(cien, 4, RoundingMode.HALF_UP)));

        }

        BigDecimal porcentTotalBonif = itemProducto.getPorcentajeBonificacion1().add(itemProducto.getPorcentajeBonificacion2()).add(itemProducto.getPorcentajeBonificacion3()).add(itemProducto.getPorcentajeBonificacion4()).add(itemProducto.getPorcentajeBonificacion5()).add(itemProducto.getPorcentajeBonificacion6());

        itemProducto.setPorcentaTotalBonificacion(porcentTotalBonif.setScale(2, RoundingMode.HALF_UP));

        itemProducto.setPrecioSecundario(itemProducto.getPrecio().divide(itemProducto.getCotizacion(), 2, RoundingMode.HALF_UP));

        BigDecimal bonifUnitaria = itemProducto.getPrecio().multiply(itemProducto.getPorcentaTotalBonificacion()).divide(cien, 2, RoundingMode.HALF_UP).negate();

        BigDecimal totalLinea = itemProducto.getCantidad().multiply(itemProducto.getPrecio().add(bonifUnitaria)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal bonifSecundarioUnit = itemProducto.getPrecioSecundario().multiply(itemProducto.getPorcentaTotalBonificacion()).divide(cien, 4, RoundingMode.HALF_UP).negate();
        BigDecimal totalLineaSecundario = itemProducto.getCantidad().multiply(itemProducto.getPrecioSecundario().add(bonifSecundarioUnit)).setScale(2, RoundingMode.HALF_UP);

        itemProducto.setTotalLinea(totalLinea);
        itemProducto.setTotalLineaSecundario(totalLineaSecundario);

        itemProducto.setTotalLineaConImpuesto(itemProducto.getTotalLinea()
                .multiply(itemProducto.getPorcentajeImpuesto().add(cien).divide(cien, 4, RoundingMode.HALF_UP)));

        if (itemProducto.isCostoManual()) {
            itemProducto.getMovimiento().setPideCostoItem(true);
        }

    }

    @Deprecated
    public void calcularImportesLineaByTotal(ItemMovimientoFacturacion item, boolean precioConImpuesto) {

        BigDecimal cien = new BigDecimal(100);

        BigDecimal porcentTotalBonif = item.getPorcentajeBonificacion1().add(item.getPorcentajeBonificacion2()).add(item.getPorcentajeBonificacion3()).add(item.getPorcentajeBonificacion4()).add(item.getPorcentajeBonificacion5()).add(item.getPorcentajeBonificacion6());
        item.setPorcentaTotalBonificacion(porcentTotalBonif);

        if (item.getCantidad() == null) {
            item.setCantidad(BigDecimal.ZERO);
        }

        if (item.getTotalLinea() == null) {
            item.setTotalLinea(BigDecimal.ZERO);
        }

        BigDecimal bonifCalc = BigDecimal.ZERO;

        if (porcentTotalBonif.compareTo(BigDecimal.ZERO) != 0) {

            bonifCalc = porcentTotalBonif.divide(cien, 2, RoundingMode.HALF_UP).negate();
        }

        if (item.getMovimiento().getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
            item.setTotalLinea(item.getTotalLineaSecundario().multiply(item.getCotizacion()).setScale(2, RoundingMode.HALF_UP));
        }

        item.setTotalLinea(item.getTotalLinea().setScale(2, RoundingMode.HALF_UP));
        BigDecimal precio = item.getTotalLinea().divide(item.getCantidad().multiply((BigDecimal.ONE).add(bonifCalc)), 2, RoundingMode.HALF_UP);

        item.setPrecio(precio);
        item.setPrecioSecundario(item.getPrecio().divide(item.getCotizacion(), 2, RoundingMode.HALF_UP));

        BigDecimal bonifSecundario = item.getPrecioSecundario().multiply(
                item.getPorcentaTotalBonificacion().divide(cien, 4, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalLineaSecundario = item.getCantidad().multiply(item.getPrecioSecundario().add(bonifSecundario)).setScale(2, RoundingMode.HALF_UP);
        item.setTotalLineaSecundario(totalLineaSecundario);
    }

    private void preSave(MovimientoFacturacion m) {

        for (ItemMovimientoFacturacion ip : m.getItemsProducto()) {

            if (ip.getCantidad() == null || ip.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            if (m.getCircuito().getEsAnulacion().equals("S")) {
                ip.setCantidadPendiente(BigDecimal.ZERO);
            } else {
                ip.setCantidadPendiente(ip.getCantidad());
            }

            if (m.getDeposito() != null) {

                ip.setDeposito(m.getDeposito());

                if (ip.getItemsDetalle() != null) {

                    for (ItemMovimientoFacturacionDetalle id : ip.getItemsDetalle()) {
                        id.setDeposito(m.getDeposito());
                    }
                }

            }
        }
    }

    private void actualizarCantidadesPendientes(MovimientoFacturacion m) {

        for (ItemMovimientoFacturacion ip : m.getItemsProducto()) {

            if (ip.getCantidad() == null || ip.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            if (ip.getItemAplicado() != null) {

                BigDecimal aplicadoActual = facturacionDAO.getCantidadAplicadaItem(ip.getItemAplicado().getId());
                BigDecimal pendiente = ip.getItemAplicado().getCantidad().add(aplicadoActual.negate());
                ip.getItemAplicado().setCantidadPendiente(pendiente);

                facturacionDAO.editar(ip.getItemAplicado());
            }

            if (ip.getItemReversion() != null) {

                BigDecimal aplicadoActual = facturacionDAO.getCantidadAplicadaItem(ip.getItemReversion().getId());
                BigDecimal pendiente = ip.getItemReversion().getCantidad().add(aplicadoActual.negate());
                ip.getItemReversion().setCantidadPendiente(pendiente);

                facturacionDAO.editar(ip.getItemReversion());
            }
        }
    }

    private void control(MovimientoFacturacion movimiento) throws ExcepcionGeneralSistema, Exception {

        ParametroStock parametro = parametroStockRN.getParametro();
        String sErrores = "";

        if (movimiento.getId() != null) {
            if (movimiento.getFormulario().getModfor().equals("VT")) {
                sErrores += "- No es posible modificar un comprobante de venta \n";
            }

            if (movimiento.getFormulario().getModfor().equals("ST")) {
                sErrores += "- No es posible modificar un comprobante de stock \n";
            }
        }

        if (movimiento.getCanalVenta() == null) {
            sErrores += "- El canal de venta no puede estar vacío \n";
        }

        if (movimiento.getCliente() == null) {
            sErrores += "- El cliente no puede estar vacío \n";
        }

        if (movimiento.getCondicionDeIva() == null) {
            sErrores += "- La condición de iva no puede estar vacía \n";
        }

        if (movimiento.getLocalidad() == null) {
            sErrores += "- La localidad no puede estar vacía \n";
        }

        if (movimiento.getDireccion() == null || movimiento.getDireccion().isEmpty()) {
            sErrores += "- La dirección no puede estar vacía \n";
        }

        if (movimiento.getCircuito().getActualizaVenta().equals("S")) {

            if (movimiento.getCliente().getEstado().getCodigo().equals("P")) {
                sErrores += "- Cliente potencial, debe actualizar estado del para poder guardar el comprobante de venta \n";
            }

            if (movimiento.getCondicionDePago().getImputaCuentaCorriente().equals("N") && movimiento.getComprobanteVenta().getTipoImputacion().equals("CC")) {
                sErrores += "- La condición de pago seleccionada del comprobante, no permite generar comprobantes en cuenta corriente \n";
            }

            if (movimiento.getCondicionDePago().getImputaCuentaCorriente().equals("S") && movimiento.getComprobanteVenta().getTipoImputacion().equals("CO")) {
                sErrores += "- La condición de pago asignada al comprobante tiene que ser contado, modifique y vuelva a intentar \n";
            }

            if (movimiento.getCliente().getSoloContado().equals("S") && movimiento.getComprobanteVenta().getTipoImputacion().equals("CC")) {
                sErrores += "- El cliente seleccionado está marcado para realizarle solo ventas de contado \n";
            }
        }

        if (movimiento.getCircuito().getActualizaStock().equals("S")
                || movimiento.getCircuito().getTransporteObligatorio().equals("S")) {

            if (movimiento.getTransporte() == null) {
                sErrores += "- El transporte no puede estar en blanco para este comprobante \n";
            }
        }

        if (movimiento.getCircuito().getComprometeStock().equals("S") && movimiento.getDeposito() == null) {

            sErrores += "- El circuito compromete stock, por lo tanto debe seleccionar un depósito para el comprobante \n";
        }

        if (movimiento.getItemsProducto().isEmpty()) {
            sErrores += "- El detalle de productos está vacío, no es posible guardar el comprobante \n";
        }

        if (movimiento.getImpBonificacion4().compareTo(BigDecimal.ZERO) < 0) {
            sErrores += " - El porcentaje de bonificación no puede ser negativo \n";
        }

        if (movimiento.getImpBonificacion5().compareTo(BigDecimal.ZERO) < 0) {
            sErrores += "- El porcentaje de bonificación no puede ser negativo \n";
        }

        if (movimiento.getImpBonificacion6().compareTo(BigDecimal.ZERO) < 0) {
            sErrores += "- El porcentaje de bonificación no puede ser negativo \n";
        }

        if (movimiento.getImporteTotal().compareTo(BigDecimal.ZERO) <= 0) {
            sErrores += "- El importe total no puede ser menor a cero \n";
        }

        //CONTROLES GENERALES PARA LOS ITEMS
        for (ItemMovimientoFacturacion itemProducto : movimiento.getItemsProducto()) {

            itemProducto.setConError(false);

            if (itemProducto.getProducto() == null) {
                itemProducto.setConError(true);
                sErrores += "- El producto no puede estar vacío en item " + itemProducto.getNroitm() + " \n";
                break;
            }

            if (movimiento.getCircuito().getPermiteCantidadCero().equals("N")) {
                if (itemProducto.getCantidad() == null || itemProducto.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                    itemProducto.setConError(true);
                    sErrores += "- Ingrese un valor de cantidad válido. Mayor a 0 para el item " + itemProducto.getNroitm() + " \n";
                }
            }

            if (movimiento.getCircuito().getPermiteProductosConPrecioCero().equals("N")) {
                if (itemProducto.getPrecio() == null || itemProducto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                    itemProducto.setConError(true);
                    sErrores += "- No está permitido productos con precio en cero. Ingrese un valor de precio válido. Mayor a 0 para el item " + itemProducto.getNroitm() + " \n";
                }
            }

            if (movimiento.getCircuito().getVerificaPendiente().equals("S")) {

            }

            if (movimiento.getCircuito().getPidePrecioCostoSiEsCero().equals("S")
                    && itemProducto.getProducto().getPidePrecioCosto().equals("S")
                    && (itemProducto.getPrecioCosto() == null || itemProducto.getPrecioCosto().compareTo(BigDecimal.ZERO) <= 0)) {

                itemProducto.setConError(true);
                sErrores += "- Ingrese un valor de precio de costo válido. Mayor a 0 para el item " + itemProducto.getNroitm() + " \n";
            }

            if (movimiento.getCircuito().getAdministraAtributo1().equals("S")
                    || movimiento.getCircuito().getAdministraAtributo2().equals("S")
                    || movimiento.getCircuito().getAdministraAtributo3().equals("S")
                    || movimiento.getCircuito().getAdministraAtributo4().equals("S")
                    || movimiento.getCircuito().getAdministraAtributo5().equals("S")
                    || movimiento.getCircuito().getAdministraAtributo6().equals("S")
                    || movimiento.getCircuito().getAdministraAtributo7().equals("S")) {

                for (ItemMovimientoFacturacionDetalle itemDetalle : itemProducto.getItemsDetalle()) {

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

            if (movimiento.getCircuito().getActualizaStock().equals("S")
                    || movimiento.getCircuito().getComprometeStock().equals("S")) {

                BigDecimal cantidadTotalItems = BigDecimal.ZERO;

                for (ItemMovimientoFacturacionDetalle itemDetalle : itemProducto.getItemsDetalle()) {
                    if (itemDetalle.getCantidad() != null) {
                        cantidadTotalItems = cantidadTotalItems.add(itemDetalle.getCantidad());
                    }
                }

                if (itemProducto.getCantidad().compareTo(cantidadTotalItems) != 0) {
                    itemProducto.setConError(true);
                    sErrores += "- La cantidad en el item " + itemProducto.getNroitm() + " no coincide con las cantidades del detalle de atributos \n";
                }

                for (ItemMovimientoFacturacionDetalle itemDetalle : itemProducto.getItemsDetalle()) {

                    //Si el circuito compromete o mueve stock debemos asegurarnos que haya stock para comprometer
                    if (movimiento.getDeposito() != null
                            && itemProducto.getProducto().getGestionaStock().equals("S")
                            && movimiento.getDeposito().getSigno().equals("+")) {

                        if (movimiento.getCircuito().getComprometeStock().equals("S")) {
                            itemDetalle.setStockComprometer(itemDetalle.getCantidad().doubleValue());
                        }

                        //Verificamos que el item que se aplica comprometió stock para ahora descomprometerlo
                        if (itemProducto.getItemAplicado() != null
                                && itemProducto.getItemAplicado().getMovimiento().getCircuito().getComprometeStock().equals("S")) {

                            if (itemDetalle.getCantidad().doubleValue() > itemProducto.getItemAplicado().getCantidad().doubleValue()) {
                                itemDetalle.setStockDescomprometer(itemProducto.getItemAplicado().getCantidad().doubleValue());
                            } else {
                                itemDetalle.setStockDescomprometer(itemDetalle.getCantidad().doubleValue());
                            }
                        }

                        //Si el circuito actualiza stock y es egreso
                        if (movimiento.getCircuito().getActualizaStock().equals("S")
                                && movimiento.getComprobanteStock() != null
                                && "E,T".contains(movimiento.getComprobanteStock().getTipoMovimiento())) {

                            itemDetalle.setStockDescontar(itemDetalle.getCantidad().doubleValue());

                        }

                        if (itemDetalle.getDeposito().getSigno().equals("+")) {

                            Stock s = new Stock(itemDetalle);

                            String mensaje = stockRN.isProductoDisponible(s);

                            if (mensaje != null && !mensaje.isEmpty()) {
                                sErrores += "| " + mensaje + " ";
                            }
                        }
                    }
                }
            }

            if (itemProducto.getItemsKit() != null) {

                for (ItemMovimientoFacturacionKit itemKit : itemProducto.getItemsKit()) {

                    if (itemKit.getProducto() == null) {
                        itemKit.setConError(true);
                        sErrores += "- Tiene un item sin producto asignado en el kit " + itemProducto.getProducto().getDescripcion() + " \n";
                        break;
                    }

                    if (itemKit.getCantidad() == null || itemKit.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                        itemKit.setConError(true);
                        sErrores += "- Ingrese un valor de cantidad válido. Mayor a 0 para el producto " + itemKit.getProducto().getDescripcion() + " en el kit " + itemProducto.getProducto().getDescripcion() + " \n";
                    }

                    if (movimiento.getCircuito().getPidePrecioCostoSiEsCero().equals("S")
                            && itemKit.getProducto().getPidePrecioCosto().equals("S")
                            && (itemKit.getPrecioCosto() == null || itemKit.getPrecioCosto().compareTo(BigDecimal.ZERO) <= 0)) {

                        itemKit.setConError(true);
                        sErrores += "- Ingrese un valor de precio de costo válido. Mayor a 0 para el producto " + itemKit.getProducto().getDescripcion() + " en el kit " + itemProducto.getProducto().getDescripcion() + " \n";

                    }
                }
            }

//            if (i.getCantidadOriginal() != null && i.getCantidad().compareTo(i.getCantidadOriginal()) > 0) {
//                throw new ExcepcionGeneralSistema("La cantidad no puede ser mayor a la cantidad pendiente original ("
//                        + i.getCantidadOriginal().setScale(2)
//                        + ") para el item " + i.getNroitm() + " - " + i.getDescripcion());
//            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void calcularImportes(MovimientoFacturacion m, boolean precioConImpuesto) {
        calcularImportes(m, "U", precioConImpuesto);
    }

    /**
     *
     * @param m
     * @param modo U=Por precio unitario, calcula total | T = Por total, calcula
     * precio
     */
    public void calcularImportes(MovimientoFacturacion m, String modo, boolean precioConImpuesto) {

        if (m.getCliente() == null) {
            return;
        }

        if (m.getItemsProducto() == null) {
            return;
        }

        m.setPideCostoItem(false);

        ponerImportesCero(m);

        for (ItemMovimientoFacturacion ip : m.getItemsProducto()) {

            ip.setCotizacion(m.getCotizacion());

            ip.setPorcentajeBonificacion4(m.getPorcentajeBonificacion4());
            ip.setPorcentajeBonificacion5(m.getPorcentajeBonificacion5());
            ip.setPorcentajeBonificacion6(m.getPorcentajeBonificacion6());

            if (modo.equals("U")) {
                calcularImportesLineaByPrecio(ip, precioConImpuesto);
            }

            if (modo.equals("T")) {
                calcularImportesLineaByTotal(ip, precioConImpuesto);
            }

        }

        BigDecimal cien = new BigDecimal(100);

        //Recorremos cada item
        for (ItemProductoVenta itemProductoVenta : m.getItemsProductoVenta()) {

            if (itemProductoVenta.getConcepto() == null) {
                continue;
            }

            itemProductoVenta.setImporte(BigDecimal.ZERO);

            //Cargamos la sumatoria por conceptos tipo A
            for (ItemMovimientoFacturacion itemProductoFacturacion : m.getItemsProducto()) {

                if (itemProductoFacturacion.getConcepto() == null) {
                    continue;
                }

                if (itemProductoFacturacion.getConcepto().equals(itemProductoVenta.getConcepto())) {

                    itemProductoVenta.setImporte(itemProductoVenta.getImporte().add(itemProductoFacturacion.getTotalLinea()));
                    itemProductoVenta.setImporteSecundario(itemProductoVenta.getImporte().divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));
                }
            }

            //Calculamos los impuestos y percepciones por cada concepto tipo A y vamos acumulando
            for (ImpuestoPorConcepto impuestoConcepto : itemProductoVenta.getConcepto().getImpuestos()) {

                //Calculo de IVA
                for (ItemImpuestoVenta itemImpuesto : m.getItemsImpuestoVenta()) {

                    if (itemImpuesto.getTipoImpuesto() == null
                            || itemProductoVenta.getConcepto().getImpuestos() == null
                            || itemProductoVenta.getConceptoAsociado() == null) {
                        continue;
                    }

//                    System.err.println("itemImpuesto.getEditaImporte() " + itemImpuesto.getEditaImporte());
                    if (itemImpuesto.getEditaImporte().equals("N")) {

                        //El impuesto concepto e impuesto tienen que ser iguales para que pueda sumar
                        if (itemProductoVenta.getImporte().compareTo(BigDecimal.ZERO) > 0
                                && itemImpuesto.getTipoImpuesto().getCodigo().equals(impuestoConcepto.getCodimp())
                                && itemProductoVenta.getConceptoAsociado().equals(itemImpuesto.getConcepto())) {

                            itemImpuesto.setBaseImponible(itemImpuesto.getBaseImponible().add(itemProductoVenta.getImporte()).setScale(2, RoundingMode.HALF_UP));
                            itemImpuesto.setAlicuota(impuestoConcepto.getTasa());
                            itemImpuesto.setImporte(itemImpuesto.getImporte().add(itemProductoVenta.getImporte().multiply(impuestoConcepto.getTasa()).divide(cien, 2, RoundingMode.HALF_UP)));
                            itemImpuesto.setImporteSecundario(itemImpuesto.getImporte().divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));

                            break;
                        }
                    }
                }

                //Calculo de percepciones, si es marcado como bien de uso no se calcula
                if (m.getBienDeUso().equals("N")) {

                    for (ItemPercepcionVenta itemPercepcion : m.getItemsPercepcionVenta()) {

                        if (itemPercepcion.getTipoImpuesto() == null
                                || itemProductoVenta.getConcepto().getImpuestos() == null
                                || itemPercepcion.getEditaImporte().equals("S")) {
                            continue;
                        }

                        if (itemPercepcion.getImporte() == null) {
                            itemPercepcion.setImporte(BigDecimal.ZERO);
                        }

                        boolean correspondePercepcion = correspondePercepcion(impuestoConcepto.getCodimp(), m.getCliente());

                        //El concepto y impuesto tienen que ser iguales para que pueda sumar
                        if (itemProductoVenta.getImporte().compareTo(BigDecimal.ZERO) > 0
                                && itemPercepcion.getTipoImpuesto().getCodigo().equals(impuestoConcepto.getCodimp())
                                && correspondePercepcion) {

                            itemPercepcion.setBaseImponible(itemPercepcion.getBaseImponible().add(itemProductoVenta.getImporte()));
                            itemPercepcion.setAlicuota(impuestoConcepto.getTasa());
                            itemPercepcion.setImporte(itemPercepcion.getImporte().add(itemProductoVenta.getImporte().multiply(impuestoConcepto.getTasa()).divide(cien, 2, RoundingMode.HALF_UP)));
                            itemPercepcion.setImporteSecundario(itemPercepcion.getImporte().divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));

                            break;
                        }

                    }
                }
            }
        }

        //Validamos los mínimos de percepción
        for (ItemPercepcionVenta itemPercepcion : m.getItemsPercepcionVenta()) {

            if (itemPercepcion.getImporte() != null && itemPercepcion.getImporte().compareTo(BigDecimal.ZERO) > 0) {

                if (itemPercepcion.getImporte().compareTo(itemPercepcion.getTipoImpuesto().getValorMinimo()) < 0) {

                    itemPercepcion.setImporte(BigDecimal.ZERO);
                    itemPercepcion.setImporteSecundario(BigDecimal.ZERO);
                }
            }
        }
        sumarImportes(m);
        try {
            cuentaCorrienteRN.generarItemCuentaCorriente(m);
        } catch (ExcepcionGeneralSistema ex) {
            Logger.getLogger(FacturacionRN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sumarImportes(MovimientoFacturacion m) {

        m.setImporteTotal(BigDecimal.ZERO);

        for (ItemProductoVenta itemProdVenta : m.getItemsProductoVenta()) {
            m.setImporteTotal(m.getImporteTotal().add(itemProdVenta.getImporte()).setScale(2, RoundingMode.HALF_UP));
        }

        //Calculo de IVA
        for (ItemImpuestoVenta itemImpuesto : m.getItemsImpuestoVenta()) {
            m.setImporteTotal(m.getImporteTotal().add(itemImpuesto.getImporte()).setScale(2, RoundingMode.HALF_UP));
        }

        //Calculo de percepciones
        for (ItemPercepcionVenta itemPercepcion : m.getItemsPercepcionVenta()) {
            m.setImporteTotal(m.getImporteTotal().add(itemPercepcion.getImporte()).setScale(2, RoundingMode.HALF_UP));
        }

        for (ItemTotalVenta itp : m.getItemsTotalVenta()) {

            itp.setImporte(m.getImporteTotal());
            itp.setImporteSecundario(itp.getImporte().divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));
        }
    }

    public void ponerImportesCero(MovimientoFacturacion m) {

        for (ItemImpuestoVenta i : m.getItemsImpuestoVenta()) {

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

        for (ItemPercepcionVenta i : m.getItemsPercepcionVenta()) {

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

        for (ItemTotalVenta i : m.getItemsTotalVenta()) {
            i.setImporte(BigDecimal.ZERO);
        }
    }

    private boolean correspondePercepcion(String codImpuesto, EntidadComercial e) {

        for (ImpuestoPorEntidad ic : e.getImpuestos()) {

            if (codImpuesto.equals(ic.getTipoImpuesto().getCodigo())) {
                return true;
            }
        }
        return false;
    }

    public List<MovimientoFacturacion> getListaByBusqueda(Map<String, String> filtro, boolean soloPendientes, int cantMax) {

        return facturacionDAO.getListaByBusqueda(filtro, soloPendientes, cantMax);
    }

    private void generarMovimientosAdicionales(MovimientoFacturacion m) throws ExcepcionGeneralSistema, Exception {

        if (m.getCircuito().getActualizaVenta().equals("S") && m.getComprobanteVenta() != null) {
            MovimientoVenta mv = ventaRN.generarComprobante(m);
            m.setMovimientoVenta(mv);
        }

        if (m.getComprobanteStock() != null) {

            MovimientoStock ms = movimientoStockRN.generarComprobante(m);
            m.setMovimientoStock(ms);
        }

        if (m.getComprobanteTesoreria() != null) {

            m.getMovimientoTesoreria().setFechaMovimiento(m.getFechaMovimiento());
            m.getMovimientoTesoreria().setEntidad(m.getCliente());
            m.getMovimientoTesoreria().setNombreEntidad(m.getCliente().getRazonSocial());

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

    private void tomarNumeroFormulario(MovimientoFacturacion m, WSFEv1 wsfe) throws ExcepcionGeneralSistema, Exception {

        if (m.getCircuito().getActualizaVenta().equals("S") && m.getComprobanteVenta() != null) {

            if (wsfe != null) {

                int proxNum = wsfe.tomarProximoNumero(m.getMovimientoVenta());

                m.getMovimientoVenta().setNumeroFormulario(proxNum);
                m.getMovimientoVenta().setInstanciaCAE("B");

                m.getMovimientoVenta().getFormulario().setUltimoNumero(proxNum);
                formularioRN.guardar(m.getMovimientoVenta().getFormulario());

            } else if (m.getMovimientoVenta().getFormulario().getTipoNumeracion().equals("A")) {
                m.getMovimientoVenta().setNumeroFormulario(formularioRN.tomarProximoNumero(m.getMovimientoVenta().getFormulario()));
            }

            m.setNumeroFormulario(m.getMovimientoVenta().getNumeroFormulario());

            if (m.getComprobanteTesoreria() != null && m.getMovimientoTesoreria() != null) {

                if (!m.getMovimientoTesoreria().getFormulario().equals(m.getMovimientoTesoreria().getFormulario())) {
                    if (m.getMovimientoTesoreria().getFormulario().getTipoNumeracion().equals("A")) {
                        m.getMovimientoTesoreria().setNumeroFormulario(formularioRN.tomarProximoNumero(m.getMovimientoTesoreria().getFormulario()));
                    }
                } else {
                    m.getMovimientoTesoreria().setNumeroFormulario(m.getMovimientoVenta().getNumeroFormulario());
                }
            }

            if (m.getComprobanteStock() != null && m.getMovimientoStock() != null) {
                if (!m.getFormulario().equals(m.getMovimientoStock().getFormulario())) {
                    if (m.getMovimientoStock().getFormulario().getTipoNumeracion().equals("A")) {
                        m.getMovimientoStock().setNumeroFormulario(formularioRN.tomarProximoNumero(m.getMovimientoStock().getFormulario()));
                    }
                } else {
                    m.getMovimientoStock().setNumeroFormulario(m.getMovimientoVenta().getNumeroFormulario());
                }
            }

        } else if (m.getComprobanteStock() != null && m.getMovimientoStock() != null) {

            if (m.getMovimientoStock().getFormulario().getTipoNumeracion().equals("A")) {
                m.getMovimientoStock().setNumeroFormulario(formularioRN.tomarProximoNumero(m.getMovimientoStock().getFormulario()));
            }
            m.setNumeroFormulario(m.getMovimientoStock().getNumeroFormulario());

        } else //Si no actualiza ventas ni stock, entonces es facturación
        {
            if (m.getFormulario().getTipoNumeracion().equals("A")) {
                m.setNumeroFormulario(formularioRN.tomarProximoNumero(m.getFormulario()));
            }
        }
    }

    public MovimientoFacturacion generarSeguimiento(MovimientoFacturacion movimiento) {

        if (movimiento == null) {
            return null;
        }

        if (movimiento.getItemsProducto() == null || movimiento.getItemsProducto().isEmpty()) {
            return movimiento;
        }

//        for (ItemMovimientoFacturacion ip : movimiento.getItemsProducto()) {
//
//            buscarAplicaciones(ip);
//        }
        return movimiento;
    }

    /**
     *
     * @param item itemProductoFacturacion
     */
    public void buscarAplicaciones(ItemMovimientoFacturacion item) {

        List<ItemMovimientoFacturacion> itemsAplicaciones = itemFacturacionDAO.getAplicacionesByItem(item.getId());
        item.setItemsAplicacion(itemsAplicaciones);
        buscarItemProducto(itemsAplicaciones);

    }

    private void buscarItemProducto(List<ItemMovimientoFacturacion> items) {

        if (items == null || items.isEmpty()) {
            return;
        }

        for (ItemMovimientoFacturacion ia : items) {

            ItemMovimientoFacturacion ip = itemFacturacionDAO.getItemProductoByItemAplicacion(ia.getMovimiento().getId(), ia.getNroitm(), ia.getProducto().getCodigo());
            buscarAplicaciones(ip);
            ia.setItemsAplicacion(ip.getItemsAplicacion());
        }
    }

    private void buscarObservacionesMovimientosAplicados(MovimientoFacturacion m, List<PendienteFacturacionDetalle> itemsPendiente) {

        if (itemsPendiente.isEmpty()) {
            return;
        }

        String observaciones = "";
        List<String> movimientosProcesados = new ArrayList<String>();

        String codFor = "";
        Integer nroFor = 0;

        Map<String, Integer> comprobantes = new HashMap<String, Integer>();

        for (PendienteFacturacionDetalle i : itemsPendiente) {

            if (i.isSeleccionado()
                    && i.getMovimientoAplicacion().getObservaciones() != null
                    && !i.getMovimientoAplicacion().getObservaciones().isEmpty()
                    && !movimientosProcesados.contains(i.getCodfor() + "-" + i.getNrofor())) {

                observaciones = observaciones + i.getMovimientoAplicacion().getObservaciones() + "\n";
                movimientosProcesados.add(i.getCodfor() + "-" + i.getNrofor());
            }
        }

        if (!observaciones.isEmpty()) {
            m.setObservaciones(observaciones);
        }
    }

    public void generarItemsParaPickingDetalle(MovimientoFacturacion m) throws ExcepcionGeneralSistema {

        if (m.getListaDePrecio() == null) {
            throw new ExcepcionGeneralSistema("El comprobante no tiene una lista de precio asignada");
        }

        if (m.getMonedaSecundaria() == null) {
            throw new ExcepcionGeneralSistema("El comprobante no tiene una moneda secundaria asignada");
        }

        m.getItemsProducto().clear();

        Map<String, String> filtro = new HashMap<String, String>();
        filtro.put("disponibleParaPickingFacturacion = ", "'S'");

        List<Producto> productos = productoRN.getListaByBusqueda("", filtro, "", true, 200);

        if (productos == null || productos.isEmpty()) {
            return;
        }

        for (Producto p : productos) {

            ItemMovimientoFacturacion ip = nuevoItemProducto(m);

            ip.setProducto(p);
            ip.setDescripcion(p.getDescripcion());
            ip.setProductoOriginal(p);
            ip.setUnidadMedida(p.getUnidadDeMedida());
            ip.setConcepto(p.getConceptoVenta());

            if (p.getMonedaReposicion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
                ip.setPrecioCosto(p.getPrecioReposicion().multiply(m.getCotizacion()).setScale(2, RoundingMode.HALF_UP));
                ip.setPrecioCostoSecundario(p.getPrecioReposicion());
            } else {
                ip.setPrecioCosto(p.getPrecioReposicion());
                ip.setPrecioCostoSecundario(p.getPrecioReposicion().divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));
            }

            asignarPrecioItem(ip);

            m.getItemsProducto().add(ip);
        }

        calcularImportes(m, false);
    }

    public boolean existeProducto(MovimientoFacturacion movimiento, Producto producto) {

        if (movimiento.getItemsProducto() == null || movimiento.getItemsProducto().isEmpty()) {
            return false;
        }

        if (producto == null) {
            return false;
        }

        for (ItemMovimientoFacturacion item : movimiento.getItemsProducto()) {

            if (item.getProducto() != null && item.getProducto().equals(producto)) {
                return true;
            }
        }

        return false;
    }

    public MovimientoFacturacion seleccionarMovimiento(MovimientoFacturacion movimientoSeleccionado, CircuitoFacturacion circuito) throws ExcepcionGeneralSistema {

        MovimientoFacturacion movimiento = facturacionDAO.getMovimientoFacturacionById(movimientoSeleccionado.getId());

        movimiento.setComprobanteVenta(circuito.getComprobanteVenta());
        ventaRN.generarItemsMovimientoVenta(movimiento);
        tesoreriaRN.cargarItemsMovimiento(movimiento.getMovimientoTesoreria());
        calcularImportes(movimiento, "U", false);

        return movimiento;
    }

    public void actualizarCantidades(ItemMovimientoFacturacion itemProducto) {

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

        if (itemProducto.getItemsKit() != null && !itemProducto.getItemsKit().isEmpty()) {
            for (ItemMovimientoFacturacionKit ik : itemProducto.getItemsKit()) {

//                System.err.println(ik.getProducto().getDescripcion()+" "+ ik.getCantidadNominal());
                ik.setCantidad(itemProducto.getCantidad().multiply(ik.getCantidadNominal()));
            }
            asignarPrecioItem(itemProducto);
        }

        calcularImportes(itemProducto.getMovimiento(), "U", false);
    }

    public void asignarProducto(ItemMovimientoFacturacion itemProducto, Producto producto) throws ExcepcionGeneralSistema {

        String sErrores = "";

        if (itemProducto.getMovimiento().getListaDePrecio() == null) {
            sErrores += "- El comprobante no tiene una lista de precio asignada \n";
        }

        if (itemProducto.getMovimiento().getMonedaSecundaria() == null) {
            sErrores += "- El comprobante no tiene una moneda secundaria asignada \n";
        }

        if (itemProducto.getMovimiento().getCircuito().getPermiteProductosDuplicados().equals("N")
                && producto.getValidaDuplicidad().equals("S")) {
            if (existeProducto(itemProducto.getMovimiento(), producto)) {
                sErrores += "- El producto que intenta agregar ya existe en el comprobante " + itemProducto.getMovimiento().getComprobante().getDescripcion() + " y no está permitido agregar items duplicados \n";
            }
        }

        if (itemProducto.getItemsAplicacion() != null) {
            if (!itemProducto.getItemsAplicacion().isEmpty()) {
                sErrores += "- Este items tiene aplicaciones, no es posible modificarlo \n";
            }
        }

        if (producto.getCuentaContableVenta() == null) {
            sErrores += "- El producto no tiene una cuenta contable de venta asignada \n";
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }

        itemProducto.setProducto(producto);
        itemProducto.setDescripcion(producto.getDescripcion());
        itemProducto.setProductoOriginal(producto);
        itemProducto.setUnidadMedida(producto.getUnidadDeMedida());
        itemProducto.setConcepto(producto.getConceptoVenta());
        itemProducto.setAtributo1("");
        itemProducto.setAtributo2("");
        itemProducto.setAtributo3("");
        itemProducto.setAtributo4("");
        itemProducto.setAtributo5("");
        itemProducto.setAtributo6("");
        itemProducto.setAtributo7("");

        if (itemProducto.getMovimiento().getCircuito().getPidePrecioCostoSiEsCero().equals("S")
                && itemProducto.getProducto().getPidePrecioCosto().equals("S")) {
            itemProducto.setCostoManual(true);
        }

        if (itemProducto.getMovimiento().getCircuito().getCantidadPorDefectoEnItems() > 0) {
            itemProducto.setCantidad(new BigDecimal(itemProducto.getMovimiento().getCircuito().getCantidadPorDefectoEnItems()));
            itemProducto.setCantidadPendiente(itemProducto.getCantidad());
            itemProducto.setCantidadOriginal(itemProducto.getCantidad());
        }

        asignarItemsKit(itemProducto);
        asignarPrecioItem(itemProducto);
        asignarProductoItemDetalle(itemProducto);
        calcularImportes(itemProducto.getMovimiento(), itemProducto.getMovimiento().getListaDePrecio().getIncluyeImpuestos().equals("S"));

    }

    public void asignarProductoItemDetalle(ItemMovimientoFacturacion itemProducto) {

        if (itemProducto.getMovimiento().getCircuito().getActualizaFacturacion().equals("S")
                && itemProducto.getMovimiento().getCircuito().getComprometeStock().equals("N")) {
            return;
        }

        if (itemProducto.getItemsDetalle() == null || itemProducto.getItemsDetalle().isEmpty()) {

            List<Stock> stocks = stockRN.getStockByProductoDeposito(itemProducto.getProducto(), itemProducto.getDeposito());

            if (stocks == null || stocks.isEmpty()) {

                ItemMovimientoFacturacionDetalle itemDetalle = nuevoItemDetalle(itemProducto);

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

            } else {

                double cantidadPendiente = itemProducto.getCantidad().doubleValue();

                for (Stock s : stocks) {

                    if (cantidadPendiente > 0) {

                        ItemMovimientoFacturacionDetalle itemDetalle = nuevoItemDetalle(itemProducto);

                        itemDetalle.setAtributo1(s.getAtributo1());
                        itemDetalle.setAtributo2(s.getAtributo2());
                        itemDetalle.setAtributo3(s.getAtributo3());
                        itemDetalle.setAtributo4(s.getAtributo4());
                        itemDetalle.setAtributo5(s.getAtributo5());
                        itemDetalle.setAtributo6(s.getAtributo6());
                        itemDetalle.setAtributo7(s.getAtributo7());

                        if (s.getStocks() > cantidadPendiente) {
                            itemDetalle.setCantidad(new BigDecimal(cantidadPendiente));
                        } else {
                            itemDetalle.setCantidad(new BigDecimal(s.getStocks()));
                        }

                        cantidadPendiente = cantidadPendiente - s.getStocks();
                    }
                }
            }

        } else {

            if (itemProducto.getItemsDetalle().size() == 1) {

                if (itemProducto.getCantidad() != null) {

                    itemProducto.getItemsDetalle().get(0).setCantidad(itemProducto.getCantidad());
                    itemProducto.getItemsDetalle().get(0).setProducto(itemProducto.getProducto());
                }
            } else {

                for (ItemMovimientoFacturacionDetalle d : itemProducto.getItemsDetalle()) {
                    d.setProducto(itemProducto.getProducto());
                    d.setDescripcion(itemProducto.getDescripcion());
                    d.setDeposito(itemProducto.getDeposito());
                    d.setUnidadMedida(itemProducto.getUnidadMedida());
                }
            }
        }

    }

    public void asignarItemsKit(ItemMovimientoFacturacion itemProducto) throws ExcepcionGeneralSistema {

        if (itemProducto.getProducto() == null) {
            return;
        }

        if (itemProducto.getItemsKit() == null) {
            itemProducto.setItemsKit(new ArrayList<ItemMovimientoFacturacionKit>());
        }

        itemProducto.getItemsKit().clear();

        if (itemProducto.getProducto().getEsKitVenta().equals("N")) {
            return;
        }

        ComposicionFormula composicionFormula = composicionFormulaRN.getComprosicionFormula(itemProducto.getProducto().getCodigo(), itemProducto.getProducto().getFormulaComposicionVenta().getCodigo());

        if (composicionFormula != null) {

            if (composicionFormula.getItemsComponente() == null && composicionFormula.getItemsProceso() == null) {

                throw new ExcepcionGeneralSistema("La formula del producto seleccionado no contiene componentes ni procesos ");

            } else {

                if (composicionFormula.getItemsComponente() != null) {

                    for (ItemComposicionFormulaComponente i : composicionFormula.getItemsComponente()) {

                        ItemMovimientoFacturacionKit itemKit = nuevoItemKit(itemProducto);

                        itemKit.setProducto(i.getProductoComponente());
                        itemKit.setDescripcion(i.getProductoComponente().getDescripcion());
                        itemKit.setUnidadMedida(i.getUnidadMedidaItem());
                        itemKit.setCantidadNominal(i.getCantidadNominal());
                        itemKit.setCantidad(itemProducto.getCantidad().multiply(i.getCantidadNominal()));
                        itemKit.setUnidadMedida(i.getProductoComponente().getUnidadDeMedida());

                        if (itemProducto.getMovimiento().getCircuito().getPidePrecioCostoSiEsCero().equals("S")
                                && itemKit.getProducto().getPidePrecioCosto().equals("S")) {
                            itemKit.setCostoManual(true);
                        }

                    }
                }

                if (composicionFormula.getItemsProceso() != null) {

                    for (ItemComposicionFormulaProceso i : composicionFormula.getItemsProceso()) {

                        ItemMovimientoFacturacionKit itemKit = nuevoItemKit(itemProducto);

                        itemKit.setProducto(i.getProductoComponente());
                        itemKit.setDescripcion(i.getProductoComponente().getDescripcion());
                        itemKit.setUnidadMedida(i.getUnidadMedidaItem());
                        itemKit.setCantidadNominal(i.getCantidadNominal());
                        itemKit.setCantidad(itemProducto.getCantidad().multiply(i.getCantidadNominal()));
                        itemKit.setUnidadMedida(i.getProductoComponente().getUnidadDeMedida());

                        if (itemProducto.getMovimiento().getCircuito().getPidePrecioCostoSiEsCero().equals("S")
                                && itemKit.getProducto().getPidePrecioCosto().equals("S")) {
                            itemKit.setCostoManual(true);
                        }
                    }
                }

            }

        } else {
            throw new ExcepcionGeneralSistema("El producto (" + itemProducto.getProducto().getCodigo() + "-" + itemProducto.getProducto().getDescripcion() + ") seleccionado no tiene una fórmula de composición kit definida");
        }

    }

    public void procesarProductoKit(ItemMovimientoFacturacion itemProducto) throws ExcepcionGeneralSistema {

        if (itemProducto.getItemsKit() == null) {
            return;
        }

        for (ItemMovimientoFacturacionKit itemKit : itemProducto.getItemsKit()) {

            itemKit.setCostoManual(false);

            if (itemKit.getProducto() != null) {

                itemKit.setDescripcion(itemKit.getProducto().getDescripcion());
                itemKit.setUnidadMedida(itemKit.getProducto().getUnidadDeMedida());

                if (itemProducto.getMovimiento().getCircuito().getPidePrecioCostoSiEsCero().equals("S")
                        && itemKit.getProducto().getPidePrecioCosto().equals("S")) {
                    itemKit.setCostoManual(true);
                }

            } else {

                itemKit.setDescripcion(null);
                itemKit.setUnidadMedida(null);

            }

            itemKit.setPrecioCosto(BigDecimal.ZERO);
            itemKit.setPrecioCostoSecundario(BigDecimal.ZERO);

            if (itemKit.getCantidadNominal() == null) {
                itemKit.setCantidadNominal(BigDecimal.ZERO);
            }

            if (itemKit.getPrecio() == null
                    || itemKit.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                asignarPrecioItemKit(itemKit);
            }
        }
        asignarPrecioItem(itemProducto);
        calcularImportes(itemProducto.getMovimiento(), false);
    }

    public void asignarProductoItemDetalleFromItemAplicado(ItemMovimientoFacturacion itemProducto) {

        if (itemProducto.getItemsDetalle() != null) {
            itemProducto.getItemsDetalle().clear();
        }

        for (ItemMovimientoFacturacionDetalle itemDetalleAplicado : itemProducto.getItemAplicado().getItemsDetalle()) {

            ItemMovimientoFacturacionDetalle itemDetalle = nuevoItemDetalle(itemProducto);
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

    public void asignarCliente(MovimientoFacturacion m, EntidadComercial cliente) throws Exception {

        m.setCliente(cliente);
        m.setClienteCuentaCorriente(cliente);
        m.setRazonSocial(cliente.getRazonSocial());
        m.setNrodoc(cliente.getNrodoc().replace("-", ""));
        m.setTipoDocumento(cliente.getTipoDocumento());

        m.setProvincia(cliente.getProvincia());
        m.setLocalidad(cliente.getLocalidad());
        m.setBarrio(cliente.getBarrio());
        m.setDireccion(cliente.getDireccion());

        if (cliente.getDireccionesDeEntrega() != null && !cliente.getDireccionesDeEntrega().isEmpty()) {
            asignarDireccionEntrega(m, cliente.getDireccionesDeEntrega().get(0));
        }

        //movimiento.setDepartamentoPiso(cliente.getDepartamentoPiso());
        //movimiento.setDepartamentoNumero(cliente.getDepartamentoNumero());
        m.setCondicionDeIva(cliente.getCondicionDeIva());

        if (m.getComprobanteVenta() != null && m.getComprobanteVenta().getTipoImputacion().equals("CO")) {
            m.setCondicionDePago(condicionDePagoVentaRN.getPrimeraCondicionByImputacion("N"));
        } else {
            m.setCondicionDePago(cliente.getCondicionDePagoVentas());
        }

        m.setListaDePrecio(cliente.getListaDePrecioVenta());
        m.setMonedaListaPrecio(cliente.getListaDePrecioVenta().getMoneda());
        m.setVendedor(cliente.getVendedor());
        m.setRepartidor(cliente.getRepartidor());
        m.setCanalVenta(cliente.getCanalVenta());

        cliente.getTransportes().forEach(et -> {
            if (et.getSucursal().equals(m.getSucursal())) {
                m.setTransporte(et.getTransporte());
            }
        });

        asignarFormulario(m);

        if (m.getCircuito().getPickingDetalle().equals("S")) {
            generarItemsParaPickingDetalle(m);
        }

    }

    public void asignarDireccionEntrega(MovimientoFacturacion movimiento, DireccionEntregaEntidad direccionEntrega) {

        movimiento.setDireccionEntrega(direccionEntrega);
        movimiento.setProvincia(direccionEntrega.getProvincia());
        movimiento.setLocalidad(direccionEntrega.getLocalidad());
        movimiento.setBarrio(direccionEntrega.getBarrio());
        movimiento.setDireccion(direccionEntrega.getDireccion());
        //movimiento.setNumero(direccionEntrega.getNumero());
        movimiento.setDepartamentoPiso(direccionEntrega.getDepartamentoPiso());
        movimiento.setDepartamentoNumero(direccionEntrega.getDepartamentoNumero());
    }

    public void asignarDireccionEntregaRemitente(MovimientoFacturacion m, DireccionEntregaEntidad de) {

        m.setRemitenteProvincia(de.getProvincia());
        m.setRemitenteLocalidad(de.getLocalidad());
        m.setRemitenteDireccion(de.getDireccion());
        m.setRemitenteNumero(de.getNumero());

    }

    public void asignarDireccionEntregaDestinatario(MovimientoFacturacion m, DireccionEntregaEntidad de) {

        m.setDestinatarioProvincia(de.getProvincia());
        m.setDestinatarioLocalidad(de.getLocalidad());
        m.setDestinatarioDireccion(de.getDireccion());
        m.setDestinatarioNumero(de.getNumero());

    }

    /**
     * Se utiliza para transporte, donde se define a quien se le emite la
     * factura según origen flete
     *
     * @param movimiento
     * @param cliente
     */
    public void definirCliente(MovimientoFacturacion movimiento) throws ExcepcionGeneralSistema {

        EntidadComercial clienteAux = null;

        if (movimiento.getPagoFlete() == null) {
            return;
        }

        if (movimiento.getPagoFlete().equals("O")) {

            if (movimiento.getRemitenteCliente() == null) {
                return;
            }

            clienteAux = movimiento.getRemitenteCliente();

            movimiento.setCliente(movimiento.getRemitenteCliente());
            movimiento.setClienteCuentaCorriente(movimiento.getRemitenteCliente());
            movimiento.setRazonSocial(movimiento.getRemitenteRazonSocial());
            movimiento.setNrodoc(movimiento.getRemitenteNrodoc());
            movimiento.setTipoDocumento(movimiento.getRemitenteTipoDocumento());

            movimiento.setProvincia(movimiento.getRemitenteProvincia());
            movimiento.setLocalidad(movimiento.getRemitenteLocalidad());
            movimiento.setBarrio(clienteAux.getBarrio());
            movimiento.setDireccion(movimiento.getRemitenteDireccion());
            //movimiento.setNumero(movimiento.getRemitenteNumero());
            movimiento.setDepartamentoPiso(clienteAux.getDepartamentoPiso());
            movimiento.setDepartamentoNumero(clienteAux.getDepartamentoNumero());

            movimiento.setCondicionDeIva(movimiento.getRemitenteCondicionDeIva());
        }

        if (movimiento.getPagoFlete().equals("D")) {

            if (movimiento.getDestinatarioCliente() == null) {
                return;
            }

            clienteAux = movimiento.getDestinatarioCliente();

            movimiento.setCliente(movimiento.getDestinatarioCliente());
            movimiento.setClienteCuentaCorriente(movimiento.getDestinatarioCliente());
            movimiento.setRazonSocial(movimiento.getDestinatarioRazonSocial());
            movimiento.setNrodoc(movimiento.getDestinatarioNrodoc());
            movimiento.setTipoDocumento(movimiento.getDestinatarioTipoDocumento());

            movimiento.setProvincia(movimiento.getDestinatarioProvincia());
            movimiento.setLocalidad(movimiento.getDestinatarioLocalidad());
            movimiento.setBarrio(clienteAux.getBarrio());
            movimiento.setDireccion(movimiento.getDestinatarioDireccion());
            //movimiento.setNumero(movimiento.getDestinatarioNumero());
            movimiento.setDepartamentoPiso(clienteAux.getDepartamentoPiso());
            movimiento.setDepartamentoNumero(clienteAux.getDepartamentoNumero());

            movimiento.setCondicionDeIva(movimiento.getDestinatarioCondicionDeIva());
        }

        if (clienteAux == null) {
            return;
        }

        if (movimiento.getComprobanteVenta() != null && movimiento.getComprobanteVenta().getTipoImputacion().equals("CO")) {
            movimiento.setCondicionDePago(condicionDePagoVentaRN.getCondicionDePagoVenta("A"));
        } else {
            movimiento.setCondicionDePago(clienteAux.getCondicionDePagoVentas());
        }

        if (movimiento.getCliente().getSoloContado().equals("S")) {
            movimiento.setCondicionDePago(condicionDePagoVentaRN.getCondicionDePagoVenta("A"));
        }

        movimiento.setListaDePrecio(clienteAux.getListaDePrecioVenta());
        movimiento.setMonedaListaPrecio(clienteAux.getListaDePrecioVenta().getMoneda());
        movimiento.setVendedor(clienteAux.getVendedor());

        asignarFormulario(movimiento);

    }

    public void asignarRemitente(MovimientoFacturacion movimiento, EntidadComercial remitente) throws ExcepcionGeneralSistema {

        movimiento.setRemitenteRazonSocial(remitente.getRazonSocial());
        movimiento.setRemitenteNrodoc(remitente.getNrodoc());
        movimiento.setRemitenteTipoDocumento(remitente.getTipoDocumento());

        movimiento.setRemitenteProvincia(remitente.getProvincia());
        movimiento.setRemitenteLocalidad(remitente.getLocalidad());
        movimiento.setRemitenteDireccion(remitente.getDireccion());
        movimiento.setRemitenteNumero(remitente.getNumero());
        movimiento.setRemitenteCondicionDeIva(remitente.getCondicionDeIva());

        definirCliente(movimiento);
    }

    public void asignarDestinatario(MovimientoFacturacion movimiento, EntidadComercial destinatario) throws ExcepcionGeneralSistema {

        movimiento.setDestinatarioRazonSocial(destinatario.getRazonSocial());
        movimiento.setDestinatarioNrodoc(destinatario.getNrodoc());
        movimiento.setDestinatarioTipoDocumento(destinatario.getTipoDocumento());

        movimiento.setDestinatarioProvincia(destinatario.getProvincia());
        movimiento.setDestinatarioLocalidad(destinatario.getLocalidad());
        movimiento.setDestinatarioDireccion(destinatario.getDireccion());
        movimiento.setDestinatarioNumero(destinatario.getNumero());
        movimiento.setDestinatarioCondicionDeIva(destinatario.getCondicionDeIva());

        definirCliente(movimiento);
    }

    public void asignarListaPrecios(MovimientoFacturacion movimiento) {

        if (movimiento.getItemsProducto() != null && !movimiento.getItemsProducto().isEmpty()) {

            for (ItemMovimientoFacturacion i : movimiento.getItemsProducto()) {

                asignarPrecioItem(i);
            }
        }
        calcularImportes(movimiento, movimiento.getListaDePrecio().getIncluyeImpuestos().equals("S"));

    }

    public MovimientoFacturacion generaComprobante(MovimientoTaller mt) throws ExcepcionGeneralSistema, Exception {

        CircuitoFacturacion circuito = circuitoRN.iniciarCircuito(mt.getCircuito().getCircuitoComienzoFacturacion().getCodigo(),
                mt.getCircuito().getCircuitoAplicacionFacturacion().getCodigo(),
                mt.getComprobante().getCodigo());

        MovimientoFacturacion mf = nuevoMovimiento(circuito,
                mt.getComprobante().getCodigo(),
                mt.getPuntoVenta().getCodigo(),
                null, null,
                null, null,
                null, null);

        mf.setFechaMovimiento(mt.getFechaMovimiento());

        if (mt.getCliente() != null) {
            asignarCliente(mf, mt.getCliente());
        } else {
            //mf.setCliente(mt.getCliente());
        }

        mf.setDireccion(mt.getDireccion());
        mf.setBarrio(mt.getBarrio());
        mf.setLocalidad(mt.getLocalidad());
        mf.setProvincia(mt.getProvincia());
        mf.setMonedaRegistracion(mt.getMonedaRegistracion());
        mf.setMonedaSecundaria(mt.getMonedaSecundaria());
        mf.setCotizacion(mt.getCotizacion());
        mf.setListaDePrecio(mt.getListaPrecio());

        for (ItemServicioTaller itemServicio : mt.getItemsServicio()) {

            ItemMovimientoFacturacion itemFacturacion = nuevoItemProducto(mf);
            asignarProducto(itemFacturacion, itemServicio.getProducto());
            itemFacturacion.setCantidad(itemServicio.getCantidad());
            itemFacturacion.setPrecio(itemServicio.getPrecio());

            actualizarCantidades(itemFacturacion);
            mf.getItemsProducto().add(itemFacturacion);
        }

        for (ItemProductoTaller itemProducto : mt.getItemsProducto()) {

            ItemMovimientoFacturacion itemFacturacion = nuevoItemProducto(mf);
            asignarProducto(itemFacturacion, itemProducto.getProducto());
            itemFacturacion.setCantidad(itemProducto.getCantidad());
            itemFacturacion.setPrecio(itemProducto.getPrecio());

            actualizarCantidades(itemFacturacion);
            mf.getItemsProducto().add(itemFacturacion);
        }

        preSave(mf);
        calcularImportes(mf, "U", false);
        control(mf);

        return mf;
    }

    /**
     *
     * @param codFormulario
     * @param numeroFormulario
     * @return
     */
    public MovimientoFacturacion getMovimiento(String codFormulario, Integer numeroFormulario) {

        return facturacionDAO.getMovimiento(codFormulario, numeroFormulario);
    }

    public MovimientoFacturacion getMovimiento(Integer id) {

        return facturacionDAO.getMovimiento(id);
    }

    private void asignarPrecioItem(ItemMovimientoFacturacion itemProducto) {

        if (itemProducto.getProducto() == null) {
            return;
        }

        String monedaReposicion = itemProducto.getProducto().getMonedaReposicion().getCodigo();
        BigDecimal cotizacion = itemProducto.getMovimiento().getCotizacion();
        BigDecimal utilidadAdicional = BigDecimal.ZERO;

        if (itemProducto.getMovimiento().getComprobanteVenta().getCalculaUtilidadAdicional().equals("S")) {
            utilidadAdicional = obtenerImpuestoByConcepto(itemProducto.getConcepto());
        }

        if (itemProducto.getProducto().getEsKitVenta().equals("N")) {

            if (!itemProducto.isCostoManual()) {
                if (monedaReposicion.equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
                    itemProducto.setPrecioCosto(itemProducto.getProducto().getPrecioReposicion().multiply(cotizacion).setScale(2, RoundingMode.HALF_UP));
                    itemProducto.setPrecioCostoSecundario(itemProducto.getProducto().getPrecioReposicion());
                } else {
                    itemProducto.setPrecioCosto(itemProducto.getProducto().getPrecioReposicion());
                    itemProducto.setPrecioCostoSecundario(itemProducto.getProducto().getPrecioReposicion().divide(cotizacion, 2, RoundingMode.HALF_UP));
                }
            }

            if (itemProducto.getMovimiento().getListaDePrecio().getIncluyeImpuestos().equals("S")) {
                itemProducto.setPrecioConImpuesto(listaPrecioRN.getPrecioByProducto(itemProducto.getMovimiento().getListaDePrecio(), itemProducto.getProducto(), cotizacion, utilidadAdicional));
            } else {
                itemProducto.setPrecio(listaPrecioRN.getPrecioByProducto(itemProducto.getMovimiento().getListaDePrecio(), itemProducto.getProducto(), cotizacion, utilidadAdicional));
                itemProducto.setPrecioSecundario(itemProducto.getPrecio().divide(cotizacion, 2, RoundingMode.HALF_UP));
            }

        } else {
            //Es kit de venta
            itemProducto.setPrecioCosto(BigDecimal.ZERO);
            itemProducto.setPrecioCostoSecundario(BigDecimal.ZERO);

            for (ItemMovimientoFacturacionKit itemKit : itemProducto.getItemsKit()) {

                asignarPrecioItemKit(itemKit);

                //El precio de costo viene en pesos siempre
                itemProducto.setPrecioCosto(itemProducto.getPrecioCosto().add(itemKit.getPrecioCosto().multiply(itemKit.getCantidadNominal()).setScale(4, RoundingMode.HALF_UP)));
                itemProducto.setPrecioCostoSecundario(itemProducto.getPrecioCostoSecundario().add(itemKit.getPrecioCostoSecundario().multiply(itemKit.getCantidadNominal()).setScale(4, RoundingMode.HALF_UP)));

            }
//            System.err.println("itemProducto.getPrecioCosto() " + itemProducto.getPrecioCosto());

            if (monedaReposicion.equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
                itemProducto.getProducto().setPrecioReposicion(itemProducto.getPrecioCostoSecundario());
            } else {
                itemProducto.getProducto().setPrecioReposicion(itemProducto.getPrecioCosto());
            }

            itemProducto.setPrecio(listaPrecioRN.getPrecioByProducto(itemProducto.getMovimiento().getListaDePrecio(), itemProducto.getProducto(), cotizacion, utilidadAdicional));
            itemProducto.setPrecioSecundario(itemProducto.getPrecio().divide(cotizacion, 2, RoundingMode.HALF_UP));
        }
    }

    private void asignarPrecioItemKit(ItemMovimientoFacturacionKit itemKit) {

        if (itemKit.getProducto() == null) {
            return;
        }

        if (itemKit.getPrecioCosto() == null) {
            itemKit.setPrecioCosto(BigDecimal.ZERO);
        }

        if (itemKit.getPrecioCostoSecundario() == null) {
            itemKit.setPrecioCostoSecundario(BigDecimal.ZERO);
        }

        BigDecimal cotizacion = itemKit.getItemProducto().getMovimiento().getCotizacion();
        BigDecimal utilidadAdicional = BigDecimal.ZERO;

        if (itemKit.getItemProducto().getMovimiento().getComprobanteVenta().getCalculaUtilidadAdicional().equals("S")) {
            utilidadAdicional = obtenerImpuestoByConcepto(itemKit.getProducto().getConceptoVenta());
        }

        //SI EL PRECIO DE COSTO NO ES MANUAL, LO ACTUALIZA
        if (!itemKit.isCostoManual()) {

            //Si está en dolares, lo convierto a Pesos
            if (itemKit.getProducto().getMonedaReposicion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
                itemKit.setPrecioCosto(itemKit.getProducto().getPrecioReposicion().multiply(cotizacion).setScale(2, RoundingMode.HALF_UP));
                itemKit.setPrecioCostoSecundario(itemKit.getProducto().getPrecioReposicion());

            } else {
                itemKit.setPrecioCosto(itemKit.getProducto().getPrecioReposicion());
                itemKit.setPrecioCostoSecundario(itemKit.getProducto().getPrecioReposicion().divide(cotizacion, 2, RoundingMode.HALF_UP));
            }
        }

        //PRECIOS DE VENTA
        if (itemKit.getItemProducto().getMovimiento().getListaDePrecio().getIncluyeImpuestos().equals("S")) {

            itemKit.setPrecioConImpuesto(listaPrecioRN.getPrecioByProducto(itemKit.getItemProducto().getMovimiento().getListaDePrecio(), itemKit.getProducto(), cotizacion, utilidadAdicional));

        } else {
            itemKit.setPrecio(listaPrecioRN.getPrecioByProducto(itemKit.getItemProducto().getMovimiento().getListaDePrecio(), itemKit.getProducto(), cotizacion, utilidadAdicional));
            itemKit.setPrecioSecundario(itemKit.getPrecio().divide(cotizacion, 2, RoundingMode.HALF_UP));
        }

    }

    private BigDecimal obtenerImpuestoByConcepto(Concepto concepto) {

        if (concepto != null && concepto.getImpuestos() != null) {

            //Buscamos el impuesto iva asociado al concepto para asignarle el procentaje de impuesto al item.
            for (ImpuestoPorConcepto impuestoConcepto : concepto.getImpuestos()) {

                /**
                 * Buscamos el impuesto iva para asignarle el porcentaje de tasa
                 * al item y verificamos que el comprobante tenga asignado items
                 * de impuesto
                 */
                if (impuestoConcepto.getCodimp().equals("IVA")) {
                    return impuestoConcepto.getTasa();
                }
            }
        }

        return BigDecimal.ZERO;
    }

    public double getComprometidoByProducto(Producto producto, Deposito deposito) {

        return facturacionDAO.getComprometidoByProducto(producto, deposito);

    }

}
