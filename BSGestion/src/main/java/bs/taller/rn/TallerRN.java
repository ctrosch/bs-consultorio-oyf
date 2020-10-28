/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.rn;

import bs.administracion.modelo.Parametro;
import bs.administracion.rn.ParametrosRN;
import bs.entidad.modelo.Contacto;
import bs.entidad.modelo.EntidadComercial;
import bs.facturacion.modelo.MovimientoFacturacion;
import bs.facturacion.rn.FacturacionRN;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.ImpuestoPorConcepto;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.FormularioRN;
import bs.global.rn.MonedaRN;
import bs.global.rn.PuntoVentaRN;
import bs.stock.modelo.MovimientoStock;
import bs.stock.modelo.Producto;
import bs.stock.rn.MovimientoStockRN;
import bs.taller.dao.TallerDAO;
import bs.taller.modelo.CircuitoTaller;
import bs.taller.modelo.Equipo;
import bs.taller.modelo.ItemAplicacionTaller;
import bs.taller.modelo.ItemMovimientoTaller;
import bs.taller.modelo.ItemProductoTaller;
import bs.taller.modelo.ItemServicioTaller;
import bs.taller.modelo.MovimientoTaller;
import bs.ventas.rn.CondicionPagoVentaRN;
import bs.ventas.rn.ListaPrecioVentaRN;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class TallerRN implements Serializable {

    @EJB
    private MonedaRN monedaRN;
    @EJB
    private TallerDAO tallerDAO;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private MovimientoStockRN movimientoStockRN;
    @EJB
    private FacturacionRN facturacionRN;
    @EJB
    private ParametrosRN parametrosRN;
    @EJB
    private ListaPrecioVentaRN listaPrecioRN;
    @EJB
    private CircuitoTallerRN circuitoRN;
    protected @EJB
    PuntoVentaRN puntoVentaRN;
    protected @EJB
    CondicionPagoVentaRN condicionDePagoVentaRN;

//    @EJB private GR_MailFactory mailFactory;
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized MovimientoTaller guardar(MovimientoTaller movimiento) throws Exception {

        //borrarItemsNoValidos(m);
        sincronizarCantidades(movimiento);
        controlComprobante(movimiento);

        if (movimiento.getId() == null) {

            generarMovimientosAdicionales(movimiento);
            tomarNumeroFormulario(movimiento);
            tallerDAO.crear(movimiento);

            actualizarMovimientoAplicado(movimiento);

            if (sincronizarIdAplicacion(movimiento)) {
                movimiento = tallerDAO.editar(movimiento);
            }

        } else {
            movimiento = tallerDAO.editar(movimiento);

            if (sincronizarIdAplicacion(movimiento)) {
                movimiento = tallerDAO.editar(movimiento);
            }
        }

        movimientoStockRN.generarStock(movimiento.getMovimientoStock());
        return movimiento;
    }

    public MovimientoTaller nuevoMovimiento() {
        return new MovimientoTaller();
    }

    public MovimientoTaller nuevoMovimiento(CircuitoTaller circuito,
            String codTL, String sucTL,
            String codFC, String sucFC,
            String codST, String sucST) throws ExcepcionGeneralSistema, Exception {

        if (circuito == null) {
            throw new ExcepcionGeneralSistema("El circuito no puede ser nulo");
        }

        //Punto de Venta taller y facturación nunca pueden ir juntas, o una u otra
        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta((sucFC != null && !sucFC.isEmpty() ? sucFC : sucTL));
        PuntoVenta puntoVentaStock = puntoVentaRN.getPuntoVenta(sucST);

        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No se encontró punto de venta de facturación " + sucTL);
        }

        if (puntoVentaStock == null) {
            puntoVentaStock = puntoVenta;
        }

        Comprobante comprobante = null;
        Comprobante comprobanteST = null;

        if (circuito.getActualizaTaller().equals("S")) {

            comprobante = circuito.getComprobanteTaller();

        } else if (circuito.getActualizaFacturacion().equals("S")) {

            comprobante = circuito.getComprobanteFacturacion();

        } else if (circuito.getActualizaStock().equals("S")) {

            comprobante = circuito.getComprobanteStock();
            comprobanteST = circuito.getComprobanteStock();
        }

        MovimientoTaller m = crearMovimiento(circuito, comprobante, puntoVenta, puntoVentaStock);

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

    public MovimientoTaller nuevoMovimiento(CircuitoTaller circuito, String codTL, String sucTL, String codFC, String sucFC, String codST, String sucST,
            MovimientoTaller movimientoPendiente) throws ExcepcionGeneralSistema, Exception {

        MovimientoTaller movimiento = nuevoMovimiento(circuito, codTL, sucTL, codFC, sucFC, codST, sucST);

        movimiento.setMovimientoAplicado(movimientoPendiente);

        movimiento.setFechaIngreso(movimientoPendiente.getFechaIngreso());
        movimiento.setFechaRequerida(movimientoPendiente.getFechaRequerida());

        movimiento.setCliente(movimientoPendiente.getCliente());
        movimiento.setContacto(movimientoPendiente.getContacto());
        movimiento.setNombreContacto(movimientoPendiente.getNombreContacto());

        movimiento.setProvincia(movimientoPendiente.getLocalidad().getProvincia());
        movimiento.setLocalidad(movimientoPendiente.getLocalidad());
        movimiento.setBarrio(movimientoPendiente.getBarrio());
        movimiento.setDireccion(movimientoPendiente.getDireccion());

        movimiento.setMonedaRegistracion(movimientoPendiente.getMonedaRegistracion());
        movimiento.setCotizacion(movimientoPendiente.getCotizacion());

        if (movimientoPendiente.getDeposito() != null) {
            movimiento.setDeposito(movimientoPendiente.getDeposito());
        }

        if (movimientoPendiente.getDepositoTransferencia() != null) {
            movimiento.setDeposito(movimientoPendiente.getDepositoTransferencia());
        }

        movimiento.setTecnico(movimientoPendiente.getTecnico());
        movimiento.setEquipo(movimientoPendiente.getEquipo());
        movimiento.setMotivo(movimientoPendiente.getMotivo());
        movimiento.setDiagnostico(movimientoPendiente.getDiagnostico());
        movimiento.setSolucion(movimientoPendiente.getSolucion());
        movimiento.setObservaciones(movimientoPendiente.getObservaciones());

        generarItems(movimiento, movimientoPendiente);
        asignarFormulario(movimiento);

        return movimiento;
    }

    private MovimientoTaller crearMovimiento(CircuitoTaller circuito, Comprobante comprobante, PuntoVenta puntoVenta, PuntoVenta puntoVentaStock) throws ExcepcionGeneralSistema {

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("El comprobante no puede ser nulo");
        }

        MovimientoTaller movimiento = new MovimientoTaller();
        Parametro parametros = parametrosRN.getParametro();
        BigDecimal cotizacion = monedaRN.getCotizacionDia("USD");

        movimiento.setEmpresa(puntoVenta.getEmpresa());
        movimiento.setSucursal(puntoVenta.getSucursal());
        movimiento.setPuntoVenta(puntoVenta);

        movimiento.setCircuito(circuito);
        movimiento.setPuntoVentaStock(puntoVentaStock);

        movimiento.setComprobante(comprobante);
        movimiento.setFechaMovimiento(new Date());
        movimiento.setMonedaSecundaria(monedaRN.getMoneda(parametros.getCodigoMonedaSecundaria()));
        movimiento.setMonedaRegistracion(comprobante.getMonedaRegistracion());
        movimiento.setCotizacion(cotizacion);
        movimiento.setListaPrecio(circuito.getListaPrecio());

        asignarFormulario(movimiento);

        if (comprobante.getDeposito() != null) {
            movimiento.setDeposito(comprobante.getDeposito());
        }

        if (comprobante.getDepositoTransferencia() != null) {
            movimiento.setDepositoTransferencia(comprobante.getDepositoTransferencia());
        }

//        if ((circuito.getCircuitoComienzo().equals(circuito.getCircuitoAplicacion())
//                || movimiento.getCircuito().getPermiteAgregarItems().equals("S"))) {
//
//            movimiento.getItemsProducto().add((ItemProductoTaller) nuevoItemProducto(movimiento));
//        }
        return movimiento;
    }

    public ItemProductoTaller nuevoItemProducto(MovimientoTaller movimiento) {

        ItemProductoTaller nItem = new ItemProductoTaller();
        nItem.setIdItemAplicacion(nItem.getId());
        nItem.setNroitm(movimiento.getItemsProducto().size() + 1);
        nItem.setCotizacion(movimiento.getCotizacion());

        nItem.setMovimiento(movimiento);
        nItem.setMovimientoAplicacion(movimiento);
        nItem.setMovimientoInicial(movimiento.getId());
        nItem.setMovimientoOriginal(movimiento.getId());

        return nItem;
    }

    public ItemServicioTaller nuevoItemServicio(MovimientoTaller movimiento) {

        ItemServicioTaller nItem = new ItemServicioTaller();
        nItem.setIdItemAplicacion(nItem.getId());
        nItem.setNroitm(movimiento.getItemsProducto().size() + 1);
        nItem.setCotizacion(movimiento.getCotizacion());

        nItem.setMovimiento(movimiento);
        nItem.setMovimientoAplicacion(movimiento);
        nItem.setMovimientoInicial(movimiento.getId());
        nItem.setMovimientoOriginal(movimiento.getId());

        return nItem;
    }

    public ItemAplicacionTaller getItemAplicacion(Integer id) {

        return tallerDAO.getItemAplicacion(id);
    }

    public void eliminarItemProducto(MovimientoTaller m, ItemProductoTaller nItem) throws Exception {

        if (!m.getFormulario().getModfor().equals("FC")) {
            throw new ExcepcionGeneralSistema("Solo se puede eliminar items de comprobantes de facturación");
        }

        if (nItem == null) {
            throw new ExcepcionGeneralSistema("Item nulo, nada para eliminar");
        }

        if (nItem.getItemsAplicacion() != null) {

            if (!nItem.getItemsAplicacion().isEmpty()) {
                throw new ExcepcionGeneralSistema("Este items tiene aplicaciones, no es posible eliminarlo");
            }
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemProducto = -1;
        int indiceItemAplicacion = -1;
        int indiceItemAnulacion = -1;

        for (ItemProductoTaller ip : m.getItemsProducto()) {

            if (ip.getProducto() != null) {

                if (ip.getProducto().equals(nItem.getProducto())) {
                    indiceItemProducto = i;
                }
            }
            i++;
        }

        i = 0;

        //Buscamos indice de item aplicación
        if (m.getItemsAplicacion() != null) {
            for (ItemAplicacionTaller a : m.getItemsAplicacion()) {

                if (a.getProducto().equals(nItem.getProducto())) {
                    indiceItemAplicacion = i;
                }
                i++;
            }
        }

        //Borramos los items productos
        if (indiceItemProducto >= 0) {
            ItemProductoTaller remove = m.getItemsProducto().remove(indiceItemProducto);
            if (remove != null) {

                if (m.getId() != null && remove.getId() != null) {
                    tallerDAO.eliminar(ItemProductoTaller.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        //Borramos los items aplicación si existen
        if (indiceItemAplicacion >= 0) {
            ItemAplicacionTaller remove = m.getItemsAplicacion().remove(indiceItemAplicacion);
            if (remove != null) {

                if (m.getId() != null && remove.getId() != null) {
                    tallerDAO.eliminar(ItemAplicacionTaller.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

    }

    public void asignarFormulario(MovimientoTaller m) throws ExcepcionGeneralSistema {

        Formulario formulario;
        //Buscamos el formulario correspondiente
        formulario = formularioRN.getFormulario(m.getComprobante(), m.getPuntoVenta(), "X");

        //Este número es temporal y puede cambiar al guardar el objeto.
        m.setNumeroFormulario(formulario.getUltimoNumero() + 1);
        m.setFormulario(formulario);
    }

    private void borrarItemsNoValidos(MovimientoTaller m) {

        if (m.getItemsProducto() == null) {
            return;
        }

        String indiceBorrar[] = new String[m.getItemsProducto().size()];

        //Seteamos los valores en -1
        for (int i = 0; i < indiceBorrar.length; i++) {
            indiceBorrar[i] = "N";
        }

        for (int i = 0; i < m.getItemsProducto().size(); i++) {

            ItemMovimientoTaller im = m.getItemsProducto().get(i);

            if (im.getProducto() == null) {
                indiceBorrar[i] = "S";
                continue;
            }

//            if (im.getId() == null && !im.isTodoOk()) {
//                indiceBorrar[i] = "S";
//            }
        }

        for (int i = 0; i < indiceBorrar.length; i++) {

            if (indiceBorrar[i].equals("S")) {
                ItemProductoTaller remove = m.getItemsProducto().remove(i);
            }
        }
    }

    public void generarItems(MovimientoTaller movimiento, MovimientoTaller movimientoPendiente) throws ExcepcionGeneralSistema {

        if (movimiento == null || movimientoPendiente == null) {
            return;
        }

        if (movimientoPendiente.getItemsServicio() != null) {

            movimiento.getItemsServicio().clear();

            for (ItemServicioTaller i : movimientoPendiente.getItemsServicio()) {

                ItemServicioTaller nItem = (ItemServicioTaller) nuevoItemServicio(movimiento);

                nItem.setProducto(i.getProducto());
                nItem.setProductoOriginal(i.getProducto());
                nItem.setDescripcion(i.getDescripcion());
                nItem.setObservaciones(i.getObservaciones());
                nItem.setConcepto(i.getProducto().getConceptoVenta());
                nItem.setUnidadMedida(i.getUnidadMedida());
                nItem.setCantidad(i.getCantidad());
                nItem.setCantidadOriginal(i.getCantidad());
                nItem.setPorcentajeBonificacion1(i.getPorcentajeBonificacion1());
                nItem.setPorcentajeBonificacion2(i.getPorcentajeBonificacion2());
                nItem.setPrecio(i.getPrecio());
                nItem.setPrecioSecundario(i.getPrecioSecundario());

//                if (i.getProducto().getMonedaDeReferencia().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
//                    nItem.setPrecioCosto(i.getProducto().getPrecioReposicion().multiply(m.getCotizacion()));
//                    nItem.setPrecioCostoSecundario(i.getProducto().getPrecioReposicion());
//                } else {
//                    nItem.setPrecioCosto(i.getProducto().getPrecioReposicion());
//                    nItem.setPrecioCostoSecundario(i.getProducto().getPrecioReposicion().divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));
//                }
                //Agregamos el item a la lista
                movimiento.getItemsServicio().add(nItem);
                calcularImportesLineaByPrecio(nItem, true);
            }
        }

        if (movimientoPendiente.getItemsProducto() != null) {

            movimiento.getItemsProducto().clear();

            for (ItemProductoTaller i : movimientoPendiente.getItemsProducto()) {

                ItemProductoTaller nItem = (ItemProductoTaller) nuevoItemProducto(movimiento);

                nItem.setProducto(i.getProducto());
                nItem.setProductoOriginal(i.getProducto());
                nItem.setDescripcion(i.getDescripcion());
                nItem.setObservaciones(i.getObservaciones());
                nItem.setConcepto(i.getProducto().getConceptoVenta());
                nItem.setUnidadMedida(i.getUnidadMedida());
                nItem.setCantidad(i.getCantidad());
                nItem.setCantidadOriginal(i.getCantidad());
                nItem.setPorcentajeBonificacion1(i.getPorcentajeBonificacion1());
                nItem.setPorcentajeBonificacion2(i.getPorcentajeBonificacion2());
                nItem.setPrecio(i.getPrecio());
                nItem.setPrecioSecundario(i.getPrecioSecundario());

//                if (i.getProducto().getMonedaDeReferencia().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
//                    nItem.setPrecioCosto(i.getProducto().getPrecioReposicion().multiply(m.getCotizacion()));
//                    nItem.setPrecioCostoSecundario(i.getProducto().getPrecioReposicion());
//                } else {
//                    nItem.setPrecioCosto(i.getProducto().getPrecioReposicion());
//                    nItem.setPrecioCostoSecundario(i.getProducto().getPrecioReposicion().divide(m.getCotizacion(), 2, RoundingMode.HALF_UP));
//                }
                //Agregamos el item a la lista
                movimiento.getItemsProducto().add(nItem);
                calcularImportesLineaByPrecio(nItem, true);
            }
        }
    }

    public void calcularImportesLineaByPrecio(ItemMovimientoTaller itemMovimiento, boolean precioConImpuesto) {

        if (itemMovimiento.getConcepto() == null) {
            return;
        }

        if (itemMovimiento.getCantidad() == null) {
            itemMovimiento.setCantidad(BigDecimal.ZERO);
        }

        if (itemMovimiento.getPrecio() == null) {
            itemMovimiento.setPrecio(BigDecimal.ZERO);
        }

        if (itemMovimiento.getPrecioConImpuesto() == null) {
            itemMovimiento.setPrecioConImpuesto(BigDecimal.ZERO);
        }

        //Si la moneda de registración del movimiento, es la moneda secundaria, calculamos el importe en moneda primaria
        if (itemMovimiento.getMovimiento().getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
            itemMovimiento.setPrecio(itemMovimiento.getPrecioSecundario().multiply(itemMovimiento.getCotizacion()).setScale(4, RoundingMode.HALF_UP));
        }

        //Buscamos el impuesto iva asociado al concepto para asignarle el procentaje de impuesto al item.
        for (ImpuestoPorConcepto impuestoConcepto : itemMovimiento.getConcepto().getImpuestos()) {

            //Buscamos el impuesto iva para asignarle el porcentaje de tasa al item.
            if (impuestoConcepto.getCodimp().equals("IVA")) {
                itemMovimiento.setPorcentajeImpuesto(impuestoConcepto.getTasa());
                break;
            }
        }

        BigDecimal cien = new BigDecimal(100);

        itemMovimiento.setPrecio(itemMovimiento.getPrecio().setScale(4, RoundingMode.HALF_UP));
        itemMovimiento.setPrecioConImpuesto(itemMovimiento.getPrecioConImpuesto().setScale(4, RoundingMode.HALF_UP));

        if (precioConImpuesto && itemMovimiento.getPrecioConImpuesto().compareTo(BigDecimal.ZERO) > 0) {

            itemMovimiento.setPrecio(itemMovimiento.getPrecioConImpuesto()
                    .divide(itemMovimiento.getPorcentajeImpuesto().add(cien)
                            .divide(cien, 4, RoundingMode.HALF_UP), 4, RoundingMode.HALF_UP));

        } else {

            itemMovimiento.setPrecioConImpuesto(itemMovimiento.getPrecio()
                    .multiply(itemMovimiento.getPorcentajeImpuesto().add(cien).divide(cien, 4, RoundingMode.HALF_UP)));

        }

        BigDecimal porcentTotalBonif = itemMovimiento.getPorcentajeBonificacion1().add(itemMovimiento.getPorcentajeBonificacion2());

        itemMovimiento.setPorcentaTotalBonificacion(porcentTotalBonif.setScale(2, RoundingMode.HALF_UP));

        itemMovimiento.setPrecioSecundario(itemMovimiento.getPrecio().divide(itemMovimiento.getCotizacion(), 2, RoundingMode.HALF_UP));

        BigDecimal bonifUnitaria = itemMovimiento.getPrecio().multiply(itemMovimiento.getPorcentaTotalBonificacion()).divide(cien, 2, RoundingMode.HALF_UP).negate();

        BigDecimal totalLinea = itemMovimiento.getCantidad().multiply(itemMovimiento.getPrecio().add(bonifUnitaria)).setScale(4, RoundingMode.HALF_UP);

        BigDecimal bonifSecundarioUnit = itemMovimiento.getPrecioSecundario().multiply(itemMovimiento.getPorcentaTotalBonificacion()).divide(cien, 4, RoundingMode.HALF_UP).negate();
        BigDecimal totalLineaSecundario = itemMovimiento.getCantidad().multiply(itemMovimiento.getPrecioSecundario().add(bonifSecundarioUnit));

        itemMovimiento.setTotalLinea(totalLinea);
        itemMovimiento.setTotalLineaSecundario(totalLineaSecundario);

        itemMovimiento.setTotalLineaConImpuesto(itemMovimiento.getTotalLinea()
                .multiply(itemMovimiento.getPorcentajeImpuesto().add(cien).divide(cien, 4, RoundingMode.HALF_UP)));

    }

    public void sincronizarCantidades(MovimientoTaller m) {

        for (ItemProductoTaller i : m.getItemsProducto()) {

            if (i.getCantidad() == null || i.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
//                JsfUtil.addErrorMessage("Ingrese un valor de cantidad válido. Mayor a 0");
                return;
            }

            if (m.getId() == null) {
                if (m.getItemsAplicacion() != null) {
                    for (ItemAplicacionTaller a : m.getItemsAplicacion()) {

                        if (i.getProducto().equals(a.getProducto())
                                && i.getNroitm() == a.getNroitm()
                                && i.getCantidad().compareTo(i.getCantidadOriginal()) <= 0) {

                            //Items aplicación van con valores negativos y no deben ser mayor al pendiente original
                            a.setCantidad(i.getCantidad().negate());
                        }
                    }
                }
            }
        }
    }

    private boolean sincronizarIdAplicacion(MovimientoTaller m) {

        boolean sincronizar = false;

        for (ItemProductoTaller ip : m.getItemsProducto()) {

            if (ip.getIdItemAplicacion() == null) {
                ip.setIdItemAplicacion(ip.getId());
                sincronizar = true;
            }
        }

        for (ItemServicioTaller ip : m.getItemsServicio()) {

            if (ip.getIdItemAplicacion() == null) {
                ip.setIdItemAplicacion(ip.getId());
                sincronizar = true;
            }
        }

        return sincronizar;
    }

    private void actualizarMovimientoAplicado(MovimientoTaller movimiento) {

        if (movimiento.getMovimientoAplicado() != null) {

            if (movimiento.getMovimientoAplicado().getMovimientoAplicacion() == null) {
                movimiento.getMovimientoAplicado().setMovimientoAplicacion(movimiento);
                tallerDAO.editar(movimiento.getMovimientoAplicado());
            }
        }
    }

    private void controlComprobante(MovimientoTaller movimiento) throws ExcepcionGeneralSistema {

        String sErrores = "";

        if (movimiento.getId() != null) {
            if (movimiento.getFormulario().getModfor().equals("FC")) {
                sErrores += "No es posible modificar un comprobante de facturación \n";
            }

            if (movimiento.getFormulario().getModfor().equals("ST")) {
                sErrores += "No es posible modificar un comprobante de stock \n";
            }
        }

        if (movimiento.getCircuito().getPermiteClienteEnBlanco().equals("N") && movimiento.getCliente() == null) {
            sErrores += "El cliente no puede estar en blanco \n";
        }

        if (movimiento.getNombreContacto() == null || movimiento.getNombreContacto().isEmpty()) {
            sErrores += "El nombre del contacto no puede estar en blanco \n";
        }

        if (movimiento.getDireccion() == null || movimiento.getDireccion().isEmpty()) {
            sErrores += "Ingrese la dirección \n";
        }

        if (movimiento.getLocalidad() == null) {
            sErrores += "Seleccione la localidad \n";
        }

        if (movimiento.getListaPrecio() == null) {
            sErrores += "El circuito debe tener definida una lista de precios por defecto \n";
        }

        if (movimiento.getCircuito().getPermiteTecnicoEnBlanco().equals("N") && movimiento.getTecnico() == null) {
            sErrores += "Seleccione el técnico asignado \n";
        }

        if (movimiento.getCircuito().getPermiteDiagnosticoEnBlanco().equals("N")
                && (movimiento.getDiagnostico() == null || movimiento.getDiagnostico().isEmpty())) {
            sErrores += "Debe completar el campo diagnóstico \n";
        }

        if (movimiento.getCircuito().getPermiteSolucionEnBlanco().equals("N")
                && (movimiento.getSolucion() == null || movimiento.getSolucion().isEmpty())) {
            sErrores += "Debe completar el campo solución \n";
        }

        if (movimiento.getEquipo() == null) {
            sErrores += "Seleccione un equipo \n";
        }

        // CONTROLES GENERALES PARA LOS ITEMS
        for (ItemProductoTaller i : movimiento.getItemsProducto()) {

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

    /**
     *
     * @param codFormulario
     * @param numeroFormulario
     * @return
     */
    public MovimientoTaller getMovimiento(String codFormulario, Integer numeroFormulario) {

        return tallerDAO.getMovimiento(codFormulario, numeroFormulario);
    }

    public List<MovimientoTaller> getListaByBusqueda(Map<String, String> filtro, boolean soloPendientes, int cantMax) {

        return tallerDAO.getListaByBusqueda(filtro, soloPendientes, cantMax);
    }

    private void generarMovimientosAdicionales(MovimientoTaller mt) throws ExcepcionGeneralSistema, Exception {

        if (mt.getCircuito().getActualizaFacturacion().equals("S")) {

            MovimientoFacturacion mf = facturacionRN.generaComprobante(mt);
            mt.setMovimientoFacturacion(mf);
        }

        if (mt.getCircuito().getActualizaStock().equals("S") && mt.getComprobanteStock() != null) {

            MovimientoStock ms = movimientoStockRN.generarComprobante(mt);
            mt.setMovimientoStock(ms);
        }
    }

    private void tomarNumeroFormulario(MovimientoTaller m) throws ExcepcionGeneralSistema, Exception {

        if (m.getComprobanteStock() != null && m.getMovimientoStock() != null) {

            if (m.getMovimientoStock().getFormulario().getTipoNumeracion().equals("A")) {
                m.getMovimientoStock().setNumeroFormulario(formularioRN.tomarProximoNumero(m.getMovimientoStock().getFormulario()));
            }

            m.setNumeroFormulario(m.getMovimientoStock().getNumeroFormulario());

        } else //Si no actualiza stock, entonces es taller o facturación
        if (m.getFormulario().getTipoNumeracion().equals("A")) {
            m.setNumeroFormulario(formularioRN.tomarProximoNumero(m.getFormulario()));
        }
    }

    public MovimientoTaller generarSeguimiento(MovimientoTaller movimiento) {

        if (movimiento == null) {
            return null;
        }

        if (movimiento.getItemsProducto() == null || movimiento.getItemsProducto().isEmpty()) {
            return movimiento;
        }

        for (ItemProductoTaller ip : movimiento.getItemsProducto()) {

            buscarAplicaciones(ip);
        }

        return movimiento;
    }

    /**
     *
     * @param item itemProductoTaller
     */
    public void buscarAplicaciones(ItemProductoTaller item) {

        List<ItemAplicacionTaller> itemsAplicaciones = tallerDAO.getAplicacionesByItem(item.getId());
        item.setItemsAplicacion(itemsAplicaciones);
        buscarItemProducto(itemsAplicaciones);

    }

    private void buscarItemProducto(List<ItemAplicacionTaller> items) {

        if (items == null || items.isEmpty()) {
            return;
        }

        for (ItemAplicacionTaller ia : items) {

            ItemProductoTaller ip = tallerDAO.getItemProductoByItemAplicacion(ia.getMovimiento().getId(), ia.getNroitm(), ia.getProducto().getCodigo());
            buscarAplicaciones(ip);
            ia.setItemsAplicacion(ip.getItemsAplicacion());
        }
    }

    public boolean existeProducto(MovimientoTaller movimiento, Producto producto) {

        if (movimiento.getItemsProducto() == null || movimiento.getItemsProducto().isEmpty()) {
            return false;
        }

        if (producto == null) {
            return false;
        }

        for (ItemProductoTaller item : movimiento.getItemsProducto()) {

            if (item.getProducto() != null && item.getProducto().equals(producto)) {
                return true;
            }
        }

        return false;
    }

    public MovimientoTaller seleccionarMovimiento(MovimientoTaller movimientoSeleccionado, CircuitoTaller circuito) throws ExcepcionGeneralSistema {

        MovimientoTaller movimiento = tallerDAO.getMovimientoTallerById(movimientoSeleccionado.getId());
        return movimiento;
    }

    public void actualizarCantidades(MovimientoTaller movimiento, ItemProductoTaller item) {

        if (movimiento.getCircuito().getCircom().equals(movimiento.getCircuito().getCirapl())) {
            item.setCantidadOriginal(item.getCantidad());
        }
        actualizarAtributos(item);
    }

    /**
     * Actualizamos los atributos de stock, por el momento solo maneja nro de
     * serie
     *
     * @param item
     */
    public void actualizarAtributos(ItemProductoTaller item) {

        //item.getItemDetalle().clear();
        //FC_ItemDetalle nuevoItemDetalle = facturacionRN.nuevoItemDetalle(item);
    }

    public void asignarProducto(ItemProductoTaller item, Producto producto) throws ExcepcionGeneralSistema {

        if (item.getMovimiento().getMonedaSecundaria() == null) {
            throw new ExcepcionGeneralSistema("El comprobante no tiene una moneda secundaria asignada");
        }

//        if (item.getMovimiento().getCircuito().getPermiteProductosDuplicados().equals("N")) {
//            if (existeProducto(item.getMovimiento(), producto)) {
//                throw new ExcepcionGeneralSistema("El producto que intenta agregar ya existe en el comprobante " + item.getMovimiento().getComprobante().getDescripcion() + " y no está permitido agregar items duplicados");
//            }
//        }
        if (item.getItemsAplicacion() != null) {
            if (!item.getItemsAplicacion().isEmpty()) {
                throw new ExcepcionGeneralSistema("Este items tiene aplicaciones, no es posible modificarlo");
            }
        }

//        if (item.getItemAplicado() != null) {
//            throw new ExcepcionGeneralSistema("Este items aplica a otro producto, no es posible modificarlo");
//        }
        item.setProducto(producto);
        item.setDescripcion(producto.getDescripcion());
        item.setProductoOriginal(producto);
        item.setUnidadMedida(producto.getUnidadDeMedida());
        item.setConcepto(producto.getConceptoVenta());
        item.setAtributo1("");
        item.setAtributo2("");
        item.setAtributo3("");
        item.setAtributo4("");
        item.setAtributo5("");
        item.setAtributo6("");
        item.setAtributo7("");

        BigDecimal utilidadAdicional = item.getMovimiento().getComprobante().getSeIncluyeEnEstadisticas().equals("N") ? new BigDecimal(21) : new BigDecimal(0);

        item.setPrecio(listaPrecioRN.getPrecioByProducto(item.getMovimiento().getListaPrecio(), producto, item.getMovimiento().getCotizacion(), utilidadAdicional));
        item.setPrecioSecundario(item.getPrecio().divide(item.getMovimiento().getCotizacion(), 2, RoundingMode.HALF_UP));
    }

    public void asignarServicio(ItemServicioTaller item, Producto producto) throws ExcepcionGeneralSistema {

        if (item.getMovimiento().getMonedaSecundaria() == null) {
            throw new ExcepcionGeneralSistema("El comprobante no tiene una moneda secundaria asignada");
        }

//        if (item.getMovimiento().getCircuito().getPermiteProductosDuplicados().equals("N")) {
//            if (existeProducto(item.getMovimiento(), producto)) {
//                throw new ExcepcionGeneralSistema("El producto que intenta agregar ya existe en el comprobante " + item.getMovimiento().getComprobante().getDescripcion() + " y no está permitido agregar items duplicados");
//            }
//        }
        if (item.getItemsAplicacion() != null) {
            if (!item.getItemsAplicacion().isEmpty()) {
                throw new ExcepcionGeneralSistema("Este items tiene aplicaciones, no es posible modificarlo");
            }
        }

//        if (item.getItemAplicado() != null) {
//            throw new ExcepcionGeneralSistema("Este items aplica a otro producto, no es posible modificarlo");
//        }
        item.setProducto(producto);
        item.setDescripcion(producto.getDescripcion());
        item.setProductoOriginal(producto);
        item.setUnidadMedida(producto.getUnidadDeMedida());
        item.setConcepto(producto.getConceptoVenta());
        item.setAtributo1("");
        item.setAtributo2("");
        item.setAtributo3("");
        item.setAtributo4("");
        item.setAtributo5("");
        item.setAtributo6("");
        item.setAtributo7("");

        BigDecimal utilidadAdicional = item.getMovimiento().getComprobante().getSeIncluyeEnEstadisticas().equals("N") ? new BigDecimal(21) : new BigDecimal(0);

        item.setPrecio(listaPrecioRN.getPrecioByProducto(item.getMovimiento().getListaPrecio(), producto, item.getMovimiento().getCotizacion(), utilidadAdicional));
        item.setPrecioSecundario(item.getPrecio().divide(item.getMovimiento().getCotizacion(), 2, RoundingMode.HALF_UP));
    }

    public void asignarCliente(MovimientoTaller m, EntidadComercial cliente) throws ExcepcionGeneralSistema {

        m.setCliente(cliente);
//        m.setClienteCuentaCorriente(cliente);
//        m.setRazonSocial(cliente.getRazonSocial());
//        m.setNrodoc(cliente.getNrodoc());
//        m.setTipoDocumento(cliente.getTipoDocumento());
//
//        m.setProvincia(cliente.getProvincia());
//        m.setLocalidad(cliente.getLocalidad());
//        m.setBarrio(cliente.getBarrio());
//        m.setDireccion(cliente.getDireccion());
//        m.setNumero(cliente.getNumero());
//        m.setDepartamentoPiso(cliente.getDepartamentoPiso());
//        m.setDepartamentoNumero(cliente.getDepartamentoNumero());

        //asignarFormulario(m);
    }

    public void asignarContacto(MovimientoTaller m, Contacto contacto) throws ExcepcionGeneralSistema {

        m.setContacto(contacto);
        m.setNombreContacto(contacto.getNombre());

        m.setProvincia(contacto.getProvincia());
        m.setLocalidad(contacto.getLocalidad());
        m.setBarrio(contacto.getBarrio());
        m.setDireccion(contacto.getDireccion());

        asignarFormulario(m);

    }

    public void asignarEquipo(MovimientoTaller m, Equipo equipo) throws ExcepcionGeneralSistema {

        if (equipo.getEntidadComercial() != null) {

            m.setCliente(equipo.getEntidadComercial());
//                m.setCalle(equipoBean.getEquipo().getEntidadComercial().getDireccion());
//                m.setNumero(equipoBean.getEquipo().getEntidadComercial().getNumero());
//                m.setBarrio(equipoBean.getEquipo().getEntidadComercial().getBarrio());
//                m.setLocalidad(equipoBean.getEquipo().getEntidadComercial().getLocalidad());

//                //Filtramos las direcciones de entrega del cliente
//                direccionEntregaBean.setEntidad(m.getCliente());
//                direccionEntregaBean.buscar();
        }

        if (m.getEquipo().getContacto() != null) {
            asignarContacto(m, equipo.getContacto());
        }

    }

}
