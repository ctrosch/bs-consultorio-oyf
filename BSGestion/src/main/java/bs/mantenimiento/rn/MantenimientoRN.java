/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.rn;

import bs.administracion.modelo.Parametro;
import bs.administracion.rn.ModuloRN;
import bs.administracion.rn.ParametrosRN;
import bs.contabilidad.modelo.PeriodoContable;
import bs.contabilidad.rn.*;
import bs.educacion.modelo.MovimientoEducacion;
import bs.educacion.rn.*;
import bs.global.dao.ConceptoDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.Moneda;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.ComprobanteRN;
import bs.global.rn.FormularioRN;
import bs.global.rn.MonedaRN;
import bs.global.rn.PuntoVentaRN;
import bs.mantenimiento.dao.MovimientoMantenimientoDAO;
import bs.mantenimiento.modelo.ItemMovimientoMantenimientoActividad;
import bs.mantenimiento.modelo.ItemMovimientoMantenimientoRecurso;
import bs.mantenimiento.modelo.MovimientoMantenimiento;
import bs.mantenimiento.modelo.PlanActividad;
import bs.stock.modelo.Producto;
import bs.ventas.rn.CondicionPagoVentaRN;
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
 * @author GUILLERMO
 */
@Stateless
public class MantenimientoRN {

    @EJB
    private MonedaRN monedaRN;
    @EJB
    private ModuloRN moduloRN;
    @EJB
    private MovimientoMantenimientoDAO movimientoMantenimientoDAO;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private ComprobanteRN comprobanteRN;
    @EJB
    private ParametrosRN parametrosRN;
    protected @EJB
    PuntoVentaRN puntoVentaRN;
    protected @EJB
    CondicionPagoVentaRN condicionDePagoVentaRN;
    private @EJB
    PeriodoContableRN periodoRN;
    @EJB
    protected CuentaContableRN cuentaContableRN;
    @EJB
    protected CuentaCorrienteEducacionRN cuentaCorrienteRN;
    @EJB
    private ConceptoDAO conceptoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized MovimientoMantenimiento guardar(MovimientoMantenimiento m) throws Exception {

        //reorganizarNroItem(movimiento);
        //calcularImportes(m);
        control(m);

        if (m.getId() == null) {

            tomarNumeroFormulario(m);
            movimientoMantenimientoDAO.crear(m);

        } else {
            m = movimientoMantenimientoDAO.editar(m);
        }

        if (m.getItemsMovimientoActividad() != null && !m.getItemsMovimientoActividad().isEmpty()) {
            reorganizarNroItemActividad(m);
        }

        if (m.getItemsMovimientoRecurso() != null && !m.getItemsMovimientoRecurso().isEmpty()) {
            reorganizarNroItemRecurso(m);
        }

        return m;
    }

    public MovimientoMantenimiento nuevoMovimiento() {
        return new MovimientoMantenimiento();
    }

    public MovimientoMantenimiento nuevoMovimiento(String codMT, String ptoMT) throws ExcepcionGeneralSistema, Exception {

        return nuevoMovimiento(codMT, ptoMT, new Date());
    }

    public MovimientoMantenimiento nuevoMovimiento(String codMT, String ptoMT, Date fechaMovimiento) throws ExcepcionGeneralSistema, Exception {

        Comprobante comprobante = comprobanteRN.getComprobante("MT", codMT);
        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(ptoMT);
        PeriodoContable periodo = periodoRN.getPeriodoByFecha(comprobante.getComprobanteInterno(), fechaMovimiento);
        Parametro p = parametrosRN.getParametro();
        double cotizacion = monedaRN.getCotizacionDia(p.getCodigoMonedaSecundaria()).doubleValue();
        Moneda monedaSec = monedaRN.getMoneda(p.getCodigoMonedaSecundaria());

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No se encontró comprobante de Mantenimiento " + codMT);
        }
        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No se encontró punto de venta " + ptoMT);
        }

        //Buscamos el formulario correspondiente
        Formulario formulario = formularioRN.getFormulario(comprobante, puntoVenta, "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de Mantenimiento para el comprobante (" + codMT + ") "
                    + "para El punto de venta (" + ptoMT + ") "
                    + "con la condición de iva (X) ");
        }

        MovimientoMantenimiento movimiento = new MovimientoMantenimiento();

        movimiento.setEmpresa(puntoVenta.getEmpresa());
        movimiento.setSucursal(puntoVenta.getSucursal());
        movimiento.setPuntoVenta(puntoVenta);

        movimiento.setComprobante(comprobante);
        movimiento.setFechaMovimiento(fechaMovimiento);
        // movimiento.setMonedaSecundaria(monedaSec);
        movimiento.setMonedaRegistracion(comprobante.getMonedaRegistracion());
        movimiento.setCotizacion(cotizacion);

        asignarFormulario(movimiento);

        return movimiento;
    }

    /*
    public ItemMovimientoMantenimientoActividad nuevoItemMovimientoMantenimiento(MovimientoMantenimiento m) throws ExcepcionGeneralSistema {

        if (m.getItemsMovimiento() == null) {
            m.setItemsMovimiento(new ArrayList<ItemMovimientoMantenimientoActividad>());
        }

        puedoAgregarItem(m);

        ItemMovimientoMantenimientoActividad nItem = new ItemMovimientoMantenimientoActividad();

        nItem.setNroitm(m.getItemsMovimiento().size() + 1);

        nItem.setMovimiento(m);
        reorganizarNroItem(m);

        return nItem;
    }

     */
 /*
    public void eliminarItemMovimientoMantenimiento(MovimientoMantenimiento movimiento, ItemMovimientoMantenimientoActividad nItem) throws Exception {

        if (movimiento == null) {
            throw new ExcepcionGeneralSistema("Movimiento vacío, nada para eliminar");
        }

        if (nItem == null) {
            throw new ExcepcionGeneralSistema("Item nulo, nada para eliminar");
        }

       boolean fItemBorrado = false;
        int i = 0;
        int indiceItemProducto = -1;
        for (ItemMovimientoMantenimientoActividad ip : movimiento.getItemsMovimiento()) {

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
           ItemMovimientoMantenimientoActividad remove = movimiento.getItemsMovimiento().remove(indiceItemProducto);
            if (remove != null) {

                if (movimiento.getId() != null && remove.getId() != null) {
                    movimientoMantenimientoDAO.eliminar(ItemMovimientoMantenimientoActividad.class, remove.getId());
                }
                fItemBorrado = true;
            }
       }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

       reorganizarNroItem(movimiento);

    }

     */
 /*
    public void eliminarItemMovimientoEducacion(MovimientoEducacion movimiento, ItemMovimientoEducacion nItem) throws Exception {

        if (movimiento == null) {
            throw new ExcepcionGeneralSistema("Movimiento vacío, nada para eliminar");
        }

        if (movimiento.getId() != null) {
            throw new ExcepcionGeneralSistema("El item ya fué guardado, no es posible eliminar");
        }

        if (nItem == null) {
            throw new ExcepcionGeneralSistema("Item nulo, nada para eliminar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemCuentaContable = -1;

        for (ItemMovimientoEducacion ip : movimiento.getItemsDetalle()) {

            if (ip.getNroitm() >= 0) {

                if (ip.getNroitm() == nItem.getNroitm()) {
                    indiceItemCuentaContable = i;
                }
            }
            i++;
        }
        i = 0;

        //Borramos los items productos
        if (indiceItemCuentaContable >= 0) {
            ItemMovimientoEducacion remove = movimiento.getItemsDetalle().remove(indiceItemCuentaContable);
            if (remove != null) {

                if (movimiento.getId() != null && remove.getId() != null) {
                    movimientoEducacionDAO.eliminar(ItemMovimientoEducacion.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(movimiento);
    }
     */
    public void puedoAgregarItem(MovimientoMantenimiento m) throws ExcepcionGeneralSistema {

//        if (movimiento.getId() != null) {
//            throw new ExcepcionGeneralSistema("El asiento generado, no puede agregar items");
//        }
        //  if (m.getMonedaSecundaria() == null) {
        //      throw new ExcepcionGeneralSistema("La moneda secundaria no puede estar vacía");
        //  }
        if (m.getCotizacion() < 0) {
            throw new ExcepcionGeneralSistema("La cotización del comprobante no puede se nula o menor a 1");
        }
    }

    public void reorganizarNroItemActividad(MovimientoMantenimiento m) {

        //Reorganizamos los números de items
        int i = 1;
        for (ItemMovimientoMantenimientoActividad ip : m.getItemsMovimientoActividad()) {
            ip.setNroitm(i);
            i++;
            int d = 0;
        }
    }

    public void reorganizarNroItemRecurso(MovimientoMantenimiento m) {

        //Reorganizamos los números de items
        int i = 1;
        for (ItemMovimientoMantenimientoRecurso ip : m.getItemsMovimientoRecurso()) {
            ip.setNroitm(i);
            i++;
            int d = 0;
        }
    }

    public void asignarFormulario(MovimientoMantenimiento m) throws ExcepcionGeneralSistema {

        Formulario formulario;
        formulario = formularioRN.getFormulario(m.getComprobante(), m.getPuntoVenta(), "X");

        //Este número es temporal y puede cambiar al guardar el objeto.
        m.setNumeroFormulario(formulario.getUltimoNumero() + 1);
        m.setFormulario(formulario);
    }

    /*
    public void calcularImportes(MovimientoMantenimiento movimiento) {

        double importeTotalDebe = 0;
        double importeTotalHaber = 0;

        for (ItemMovimientoMantenimientoActividad item : movimiento.getItemsMovimiento()) {

            if (item.getImporteDebe() > 0) {

                importeTotalDebe = importeTotalDebe + item.getImporteDebe();

            }

            if (item.getImporteHaber() > 0) {
                importeTotalHaber = importeTotalHaber + item.getImporteHaber();
            }

        }
    }
     */
    private void control(MovimientoMantenimiento m) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        sErrores += moduloRN.canSaveModulo("MT", m.getFechaMovimiento());

        if (!m.getComprobante().getTipoComprobante().equals("SM")) {

            if (m.getItemsMovimientoActividad().isEmpty()) {
                sErrores += "- El detalle está vacío, no es posible guardar el comprobante \n";
            }

//            if (m.getProducto() == null) {
//                sErrores += "- El Producto está vacío, no es posible guardar el comprobante \n";
//            }
        }

        //CONTROLES GENERALES PARA LOS ITEMS
        for (ItemMovimientoMantenimientoActividad i : m.getItemsMovimientoActividad()) {

            i.setConError(false);

            if (i.getActividad() == null) {

                i.setConError(true);
                sErrores += "- Ingrese una Actividad para el item " + i.getNroitm() + " \n";

            }

        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }

    }

    public List<MovimientoMantenimiento> getListaByBusqueda(Map<String, String> filtro, int cantMax) {

        return movimientoMantenimientoDAO.getListaByBusqueda(filtro, cantMax);
    }

    private void tomarNumeroFormulario(MovimientoMantenimiento m) throws ExcepcionGeneralSistema, Exception {

        if (m.getFormulario().getTipoNumeracion().equals("A")) {
            m.setNumeroFormulario(formularioRN.tomarProximoNumero(m.getFormulario()));
        }

        formularioRN.guardar(m.getFormulario());

    }

    /*
    public void asignarCuentaContable(ItemMovimientoEducacion itemMovimiento, CuentaContable cuentaContable) throws ExcepcionGeneralSistema {

        if (itemMovimiento.getMovimiento().getMonedaSecundaria() == null) {
            throw new ExcepcionGeneralSistema("El comprobante no tiene una moneda secundaria asignada");
        }

        if (cuentaContable.getImputable().equals("N")) {
            throw new ExcepcionGeneralSistema("La cuenta contable seleccionada no es imputable");
        }

        itemMovimiento.setCuentaContable(cuentaContable);
        cargarImputacionCentroCosto(itemMovimiento);
    }
     */
 /*
    public MovimientoEducacion generaComprobante(MovimientoVenta mv) throws ExcepcionGeneralSistema, Exception {

        return null;
    }

    public List<MovimientoEducacion> generaPasajeContableTesoreria(List<MovimientoTesoreria> movimientos, Date fechaPasaje) throws ExcepcionGeneralSistema, Exception {

        List<MovimientoEducacion> movimientosContabilidad = new ArrayList<MovimientoEducacion>();

        for (MovimientoTesoreria mt : movimientos) {

            MovimientoEducacion m = nuevoMovimiento(mt.getComprobante().getComprobanteAsiento().getCodigo(), "0001", fechaPasaje);

            m.setFechaMovimiento(fechaPasaje);
            m.setReferencia(mt.getFormulario().getCodigo() + " " + mt.getNumeroFormulario()
                    + (mt.getNombreEntidad() == null || mt.getNombreEntidad().isEmpty() ? "" : " | " + mt.getNombreEntidad())
                    + (mt.getReferencia() == null || mt.getReferencia().isEmpty() ? "" : " | " + mt.getReferencia()));
            m.setCotizacion(mt.getCotizacion());

            m.setMovimientoTesoreria(mt);
            mt.setMovimientoEducacion(m);

            for (ItemMovimientoTesoreria it : mt.getItemsDetalle()) {

                ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());
                i.setImporteDebeMonedaSecundaria(it.getImporteDebeMonedaSecundaria());
                i.setImporteHaberMonedaSecundaria(it.getImporteHaberMonedaSecundaria());

                m.getItemsDetalle().add(i);

                if (it.getItemsCentroCosto() != null && !it.getItemsCentroCosto().isEmpty()) {

                    for (ItemMovimientoTesoreriaCentroCosto it_cc : it.getItemsCentroCosto()) {

                        i.setImputaCentroCosto(true);

                        ItemMovimientoEducacionCentroCosto itemCentroCosto = new ItemMovimientoEducacionCentroCosto();
                        itemCentroCosto.setNroItem(it_cc.getNroItem());
                        itemCentroCosto.setItemContabilidad(i);
                        itemCentroCosto.setCentroCosto(it_cc.getCentroCosto());
                        itemCentroCosto.setDistribucion(it_cc.getDistribucion());

                        i.getItemsCentroCosto().add(itemCentroCosto);

                        if (it_cc.getSubCuentas() != null) {

                            for (ItemMovimientoTesoreriaSubcuenta it_s : it_cc.getSubCuentas()) {

                                ItemMovimientoEducacionSubcuenta itemSubCuenta = nuevoItemMovimientoSubCuenta(itemCentroCosto);

                                if (itemSubCuenta != null) {

                                    itemSubCuenta.setDebhab(it_s.getDebhab());
                                    itemSubCuenta.setSubCuenta(it_s.getSubCuenta());
                                    itemSubCuenta.setPorcentaje(it_s.getPorcentaje());
                                    itemSubCuenta.setImporte(it_s.getImporte());
                                }
                            }
                        }
                    }
                }
            }

            movimientosContabilidad.add(m);
        }

        return movimientosContabilidad;
    }

    public List<MovimientoEducacion> generaPasajeContablePrestamo(List<MovimientoPrestamo> movimientos, Date fechaPasaje) throws ExcepcionGeneralSistema, Exception {

        List<MovimientoEducacion> movimientosContabilidad = new ArrayList<MovimientoEducacion>();

        for (MovimientoPrestamo mp : movimientos) {

            MovimientoEducacion m = nuevoMovimiento(mp.getComprobante().getComprobanteAsiento().getCodigo(), "0001", fechaPasaje);

            m.setFechaMovimiento(fechaPasaje);
            m.setReferencia(mp.getFormulario().getCodigo() + " " + mp.getNumeroFormulario()
                    + (mp.getPrestamo() == null ? "" : " | " + mp.getPrestamo().getCodigo())
                    + (mp.getDestinatario() == null ? "" : " | " + mp.getDestinatario().getRazonSocial()));
            m.setCotizacion(mp.getCotizacion());

            m.setMovimientoPrestamo(mp);
            mp.setMovimientoEducacion(m);

            for (ItemCapitalPrestamo it : mp.getItemCapital()) {

                ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());
                i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                m.getItemsDetalle().add(i);
            }

            for (ItemInteresPrestamo it : mp.getItemIntereses()) {

                ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());
                i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                m.getItemsDetalle().add(i);
            }

            for (ItemInteresMoraPrestamo it : mp.getItemInteresesMora()) {

                ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());
                i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                m.getItemsDetalle().add(i);
            }

            for (ItemDescuentoPrestamo it : mp.getItemDescuento()) {

                ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());
                i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                m.getItemsDetalle().add(i);
            }

            for (ItemGastoOtorgamientoPrestamo it : mp.getItemGastoOtorgamiento()) {

                ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());
                i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                m.getItemsDetalle().add(i);
            }

            for (ItemTotalPrestamo it : mp.getItemTotal()) {

                ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());
                i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                m.getItemsDetalle().add(i);
            }

            movimientosContabilidad.add(m);
        }

        return movimientosContabilidad;
    }

    public List<MovimientoEducacion> generaPasajeContableProveedores(List<MovimientoProveedor> movimientos, Date fechaPasaje) throws ExcepcionGeneralSistema, Exception {

        List<MovimientoEducacion> movimientosContabilidad = new ArrayList<MovimientoEducacion>();

        for (MovimientoProveedor mp : movimientos) {

            MovimientoEducacion m = nuevoMovimiento(mp.getComprobante().getComprobanteAsiento().getCodigo(), "0001", fechaPasaje);

            m.setFechaMovimiento(fechaPasaje);
            m.setReferencia(mp.getFormulario().getCodigo() + " " + mp.getNumeroFormulario()
                    + (mp.getProveedor() == null ? "" : " | " + mp.getProveedor().getNrocta() + "-" + mp.getProveedor().getRazonSocial()));

            m.setCotizacion(mp.getCotizacion());

            m.setMovimientoProveedor(mp);
            mp.setMovimientoEducacion(m);

            for (ItemProductoProveedor it : mp.getItemProducto()) {

                ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());
                i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                m.getItemsDetalle().add(i);
            }

            for (ItemImpuestoProveedor it : mp.getItemImpuesto()) {

                ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());
                i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                m.getItemsDetalle().add(i);
            }

            for (ItemRetencionProveedor it : mp.getItemRetencion()) {

                ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());
                i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                m.getItemsDetalle().add(i);
            }

            for (ItemPercepcionProveedor it : mp.getItemPercepcion()) {

                ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());
                i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                m.getItemsDetalle().add(i);
            }

            for (ItemTotalProveedor it : mp.getItemTotal()) {

                ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());
                i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                m.getItemsDetalle().add(i);
            }

            movimientosContabilidad.add(m);
        }

        return movimientosContabilidad;
    }

    public List<MovimientoEducacion> generaPasajeContableVenta(List<MovimientoVenta> movimientos, Date fechaPasaje) throws ExcepcionGeneralSistema, Exception {

        List<MovimientoEducacion> movimientosContabilidad = new ArrayList<MovimientoEducacion>();

        for (MovimientoVenta mp : movimientos) {

            MovimientoEducacion m = nuevoMovimiento(mp.getComprobante().getComprobanteAsiento().getCodigo(), "0001", fechaPasaje);

            m.setFechaMovimiento(fechaPasaje);
            m.setReferencia(mp.getFormulario().getCodigo() + " " + mp.getNumeroFormulario()
                    + (mp.getCliente() == null ? "" : " | " + mp.getCliente().getNrocta() + "-" + mp.getCliente().getRazonSocial()));

            m.setCotizacion(mp.getCotizacion());

            m.setMovimientoVenta(mp);
            mp.setMovimientoEducacion(m);

            for (ItemProductoVenta it : mp.getItemProducto()) {

                if (it.getImporte().compareTo(BigDecimal.ZERO) != 0) {

                    ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                    i.setCuentaContable(it.getCuentaContable());
                    i.setDebeHaber(it.getDebeHaber());
                    i.setImporteDebe(it.getImporteDebe());
                    i.setImporteHaber(it.getImporteHaber());
                    i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                    i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                    m.getItemsDetalle().add(i);
                }

            }

            for (ItemImpuestoVenta it : mp.getItemImpuesto()) {

                if (it.getImporte().compareTo(BigDecimal.ZERO) != 0) {

                    ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                    i.setCuentaContable(it.getCuentaContable());
                    i.setDebeHaber(it.getDebeHaber());
                    i.setImporteDebe(it.getImporteDebe());
                    i.setImporteHaber(it.getImporteHaber());
                    i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                    i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                    m.getItemsDetalle().add(i);
                }

            }

            for (ItemBonificacionVenta it : mp.getItemBonificacion()) {

                if (it.getImporte().compareTo(BigDecimal.ZERO) != 0) {

                    ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                    i.setCuentaContable(it.getCuentaContable());
                    i.setDebeHaber(it.getDebeHaber());
                    i.setImporteDebe(it.getImporteDebe());
                    i.setImporteHaber(it.getImporteHaber());
                    i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                    i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                    m.getItemsDetalle().add(i);
                }
            }

            for (ItemPercepcionVenta it : mp.getItemPercepcion()) {

                if (it.getImporte().compareTo(BigDecimal.ZERO) != 0) {

                    ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                    i.setCuentaContable(it.getCuentaContable());
                    i.setDebeHaber(it.getDebeHaber());
                    i.setImporteDebe(it.getImporteDebe());
                    i.setImporteHaber(it.getImporteHaber());
                    i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                    i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                    m.getItemsDetalle().add(i);
                }

            }

            for (ItemTotalVenta it : mp.getItemTotal()) {

                if (it.getImporte().compareTo(BigDecimal.ZERO) != 0) {

                    ItemMovimientoEducacion i = nuevoItemMovimientoEducacion(m);
                    i.setCuentaContable(it.getCuentaContable());
                    i.setDebeHaber(it.getDebeHaber());
                    i.setImporteDebe(it.getImporteDebe());
                    i.setImporteHaber(it.getImporteHaber());
                    i.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                    i.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);

                    m.getItemsDetalle().add(i);
                }
            }

            movimientosContabilidad.add(m);
        }

        return movimientosContabilidad;
    }
     */
    /**
     *
     * @param codFormulario
     * @param numeroFormulario
     * @return
     */
    public MovimientoMantenimiento getMovimiento(String codFormulario, Integer numeroFormulario) {

        return movimientoMantenimientoDAO.getMovimientoMantenimiento(codFormulario, numeroFormulario);
    }

    public MovimientoMantenimiento getMovimiento(Integer id) {

        return movimientoMantenimientoDAO.getMovimientoMantenimiento(id);
    }

    public ItemMovimientoMantenimientoActividad getItemMovimiento(Integer id) {

        return movimientoMantenimientoDAO.getItemMovimientoMantenimiento(id);
    }

    /*
    public void asignarMascara(MovimientoEducacion m, MascaraContabilidad mascara) throws ExcepcionGeneralSistema {

        if (mascara.getItems() == null || mascara.getItems().isEmpty()) {

            throw new ExcepcionGeneralSistema("La máscara seleccionada no tienen cuentas contables configuradas");

        } else {

            for (ItemMascaraContabilidad itemMascara : mascara.getItems()) {

                ItemMovimientoEducacion itemMovimiento = nuevoItemMovimientoEducacion(m);

                asignarCuentaContable(itemMovimiento, itemMascara.getCuentaContable());
                itemMovimiento.setDebeHaber(itemMascara.getDebeHaber());
                m.getItemsDetalle().add(itemMovimiento);

            }

        }

    }

    public void asignarPeriodoContable(MovimientoEducacion movimiento) throws ExcepcionGeneralSistema {

        PeriodoContable periodo = periodoRN.getPeriodoByFecha(movimiento.getComprobante().getComprobanteInterno(), movimiento.getFechaMovimiento());

//        System.err.println("periodo " + periodo);
        movimiento.setPeriodoContable(periodo);

    }

     */
    public MovimientoMantenimiento revertirMovimiento(MovimientoMantenimiento mRevertir) throws Exception {

        MovimientoMantenimiento mr = generarMovimientoReversion(mRevertir);
        mr = guardar(mr);
        mRevertir.setMovimientoReversion(mr);

        guardar(mRevertir);

        return mr;
    }

    private MovimientoMantenimiento generarMovimientoReversion(MovimientoMantenimiento m) throws ExcepcionGeneralSistema {
        if (m == null) {
            throw new ExcepcionGeneralSistema("Movimiento de Mantenimiento nulo");
        }

        if (m.getMovimientoReversion() != null) {
            throw new ExcepcionGeneralSistema("El comprobante ya fue anulado con " + m.getMovimientoReversion().getFormulario().getDescripcion() + " - " + m.getMovimientoReversion().getNumeroFormulario());
        }

        if (m.getComprobante().getComprobanteReversion() == null) {
            throw new ExcepcionGeneralSistema("El comprobante " + m.getComprobante().getDescripcion() + " no tienen comprobante de reversión asociado");
        }

        Formulario formulario = formularioRN.getFormulario(m.getComprobante().getComprobanteReversion(), m.getPuntoVenta(), "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de tesorería para el comprobante (" + m.getComprobante().getComprobanteReversion().getCodigo() + ") "
                    + "para El punto de venta (" + m.getPuntoVenta().getCodigo() + ") "
                    + "con la condición de iva (X)");
        }

        MovimientoMantenimiento mr = new MovimientoMantenimiento();

        mr.setEmpresa(m.getEmpresa());
        mr.setPuntoVenta(m.getPuntoVenta());

        // mr.setPeriodoContable(m.getPeriodoContable());
        mr.setComprobante(m.getComprobante().getComprobanteReversion());
        mr.setFormulario(formulario);

        mr.setFechaMovimiento(m.getFechaMovimiento());

        // mr.setMascaraContabilidad(m.getMascaraContabilidad());
        // mr.setReferencia(m.getReferencia());
        mr.setObservaciones("");
        // mr.setMonedaSecundaria(m.getMonedaSecundaria());
        mr.setMonedaRegistracion(m.getMonedaRegistracion());
        mr.setCotizacion(m.getCotizacion());

        mr.setMovimientoReversion(m);
        m.setNumeroFormulario(m.getNumeroFormulario() * -1);

        // mr.getItemsDetalle().clear();
/*
        for (ItemMovimientoEducacion i : m.getItemsDetalle()) {

            ItemMovimientoEducacion ir = new ItemMovimientoEducacion();

            ir.setMovimiento(mr);
            ir.setNroitm(mr.getItemsDetalle().size() + 1);
            ir.setMovimiento(mr);
            ir.setCuentaContable(i.getCuentaContable());

            ir.setDebeHaber((i.getDebeHaber().equals("D") ? "H" : "D"));

            ir.setImporteDebe(i.getImporteHaber());
            ir.setImporteDebeMonedaSecundaria(i.getImporteHaberMonedaSecundaria());

            ir.setImporteHaber(i.getImporteDebe());
            ir.setImporteHaberMonedaSecundaria(i.getImporteDebeMonedaSecundaria());

            mr.getItemsDetalle().add(ir);
        }
         */
        return mr;
    }

    public void asignarBienUso(MovimientoMantenimiento m, Producto bienUso) throws ExcepcionGeneralSistema {

        if (m == null || bienUso == null) {
            return;
        }

        m.setBienUso(bienUso);

        asignarFormulario(m);
    }

    public void asignarPlanActividad(MovimientoMantenimiento m, PlanActividad planActividad) throws ExcepcionGeneralSistema {

        if (m == null || planActividad == null) {
            return;
        }

        m.setPlanActividad(planActividad);
        m.setItemsMovimientoActividad(new ArrayList<>());
        m.setItemsMovimientoRecurso(new ArrayList<>());

        if (planActividad.getItemsPlanActividad() != null && !planActividad.getItemsPlanActividad().isEmpty()) {

            planActividad.getItemsPlanActividad().forEach(iPlanActividad -> {

                //Proceso Actividades
                if (iPlanActividad.getActividad().getSubactividades() != null && !iPlanActividad.getActividad().getSubactividades().isEmpty()) {

                    iPlanActividad.getActividad().getSubactividades().forEach(iSubactividad -> {
                        ItemMovimientoMantenimientoActividad itemMov = new ItemMovimientoMantenimientoActividad();

                        itemMov.setMovimiento(m);
                        itemMov.setActividad(iPlanActividad.getActividad());
                        itemMov.setDesccripcionActividad(iPlanActividad.getActividad().getDescripcion());
                        itemMov.setNroOrdenActividad(iPlanActividad.getNroOrden());

                        itemMov.setCodigoSubactividad(iSubactividad.getId());
                        itemMov.setDesccripcionSubactividad(iSubactividad.getDescripcion());
                        itemMov.setNroOrdenSubactividad(iSubactividad.getNroOrden());
                        itemMov.setObligatorio(iSubactividad.getObliga());

                        m.getItemsMovimientoActividad().add(itemMov);

                    });
                }

                //Proceso Recursos
                if (iPlanActividad.getActividad().getRecursos() != null && !iPlanActividad.getActividad().getRecursos().isEmpty()) {

                    iPlanActividad.getActividad().getRecursos().forEach(iRecurso -> {
                        ItemMovimientoMantenimientoRecurso itemMovR = new ItemMovimientoMantenimientoRecurso();

                        itemMovR.setMovimiento(m);
                        itemMovR.setActividad(iPlanActividad.getActividad());
                        itemMovR.setDesccripcionActividad(iPlanActividad.getActividad().getDescripcion());
                        itemMovR.setNroOrdenActividad(iPlanActividad.getNroOrden());

                        itemMovR.setCodigoRecurso(iRecurso.getId());
                        itemMovR.setDesccripcionRecurso(iRecurso.getDescripcion());
                        itemMovR.setCantidad(iRecurso.getCantidad());
                        itemMovR.setUnidadMedida(iRecurso.getProducto().getUnidadDeMedida().getCodigo());

                        m.getItemsMovimientoRecurso().add(itemMovR);

                    });
                }
            });

        }

        asignarFormulario(m);
    }

    /*
    public void generarItemsMovimiento(MovimientoMantenimiento m) throws ExcepcionGeneralSistema {

        if (m == null || m.getProducto() == null) {
            return;
        }

        if (m.getItemsMovimiento() == null) {
            m.setItemsMovimiento(new ArrayList<ItemMovimientoMantenimientoActividad>());
        } else {
            m.getItemsMovimiento().clear();
        }

    }
     */
 /*

    public MovimientoEducacion generarComprobante(MovimientoTesoreria mt) throws ExcepcionGeneralSistema, Exception {

        MovimientoEducacion m = nuevoMovimiento(mt.getComprobanteEducacion().getCodigo(), mt.getPuntoVenta().getCodigo());

        m.setEmpresa(mt.getEmpresa());
        m.setSucursal(mt.getSucursal());
        m.setPuntoVenta(mt.getPuntoVenta());
        m.setUnidadNegocio(mt.getUnidadNegocio());

        m.setNumeroFormulario(mt.getNumeroFormulario());

        m.setObservaciones(mt.getObservaciones());
        m.setFechaMovimiento(mt.getFechaMovimiento());

        m.setAlumno(mt.getEntidad());
        m.setNombreAlumno(mt.getEntidad().getNombre() + mt.getEntidad().getApellido());
        m.setTipoDocumento(mt.getEntidad().getTipoDocumento());
        m.setNrodoc(mt.getEntidad().getNrodoc());
        m.setProvincia(mt.getEntidad().getProvincia());
        m.setLocalidad(mt.getEntidad().getLocalidad());
        m.setDireccion(mt.getEntidad().getDireccion());

        m.setMonedaSecundaria(mt.getMonedaSecundaria());
        m.setMonedaRegistracion(mt.getMonedaRegistracion());
        m.setCotizacion(mt.getCotizacion().doubleValue());

        generarItemTotal(mt, m);

        //if(mt.isEsAnticipo() || mt.getComprobante().getTipoComprobante().equals("R")){
        if (mt.isEsAnticipo() || mt.getDebitosEducacion() == null || mt.getComprobante().getTipoComprobante().equals("R")) {
            cuentaCorrienteRN.generarItemCuentaCorrienteAnticipo(m);
        } else {
            cuentaCorrienteRN.generarItemCuentaCorrienteRecibo(m, mt.getDebitosEducacion());
        }

        return m;
    }

    private void generarItemTotal(MovimientoTesoreria mt, MovimientoEducacion me) throws ExcepcionGeneralSistema {

        me.getItemsMovimiento().clear();

        //Item Debe
        ItemMovimientoEducacion itvd = nuevoItemMovimientoEducacion(me);

        itvd.setDebeHaber("D");
        itvd.setConcepto(conceptoDAO.getConcepto("ED", "T", "CTR001"));
        itvd.setCuentaContable(itvd.getConcepto().getCuentaContable());
        itvd.setCantidad(0);
        itvd.setCotizacion(me.getCotizacion());

        // Item Haber
        ItemMovimientoEducacion itvh = nuevoItemMovimientoEducacion(me);

        itvh.setDebeHaber("H");
        itvh.setConcepto(conceptoDAO.getConcepto("ED", "T", "DED001"));
        itvh.setCuentaContable(itvh.getConcepto().getCuentaContable());
        itvh.setCantidad(0);
        itvh.setCotizacion(me.getCotizacion());

        double importeDebe = 0;
        double importeHaber = 0;

        importeDebe = mt.getImporteTotalDebe().doubleValue();
        importeHaber = mt.getImporteTotalHaber().doubleValue();

        //Movimento de reversión tiene que intercambiar los conceptos
        if (mt.getComprobante().getTipoComprobante().equals("R")) {

            if (mt.getImporteTotalDebe().compareTo(BigDecimal.ZERO) > 0) {
                importeDebe = mt.getImporteTotalDebe().doubleValue();
                importeHaber = mt.getImporteTotalDebe().doubleValue();
            } else {
                importeDebe = mt.getImporteTotalHaber().doubleValue();
                importeHaber = mt.getImporteTotalHaber().doubleValue();
            }

            itvd.setConcepto(conceptoDAO.getConcepto("ED", "T", "DED001"));
            itvh.setConcepto(conceptoDAO.getConcepto("ED", "T", "CTR001"));

        }

//        System.err.println("importeDebe" + importeDebe);
//        System.err.println("importeHaber" + importeHaber);
        itvd.setImporte(importeDebe);
        itvd.setImporteSecundario(importeDebe / mt.getCotizacion().doubleValue());
        itvd.setImporteDebe(importeDebe);
        itvd.setImporteDebeSecundario(importeDebe / mt.getCotizacion().doubleValue());

        itvh.setImporte(importeHaber);
        itvh.setImporteSecundario(importeHaber / mt.getCotizacion().doubleValue());
        itvh.setImporteHaber(importeHaber);
        itvh.setImporteHaberSecundario(importeHaber / mt.getCotizacion().doubleValue());

        //Agrego los items generados
        me.getItemsMovimiento().add(itvd);
        me.getItemsMovimiento().add(itvh);

    }
     */
    public MovimientoMantenimiento duplicar(MovimientoEducacion mPendiente) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public MovimientoMantenimiento duplicar(MovimientoMantenimiento m) throws Exception {

        if (m == null) {
            throw new ExcepcionGeneralSistema("Defecto nulo, nada para duplicar");
        }

        MovimientoMantenimiento mNew = m.clone();
        mNew.setId(null);

        return mNew;
    }
}
