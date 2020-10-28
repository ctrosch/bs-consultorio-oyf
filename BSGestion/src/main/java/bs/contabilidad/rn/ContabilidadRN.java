/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.rn;

import bs.administracion.modelo.Parametro;
import bs.administracion.rn.ModuloRN;
import bs.administracion.rn.ParametrosRN;
import bs.contabilidad.dao.ContabilidadDAO;
import bs.contabilidad.modelo.CuentaContable;
import bs.contabilidad.modelo.CuentaContableCentroCosto;
import bs.contabilidad.modelo.ItemDistribucion;
import bs.contabilidad.modelo.ItemMascaraContabilidad;
import bs.contabilidad.modelo.ItemMovimientoContabilidad;
import bs.contabilidad.modelo.ItemMovimientoContabilidadCentroCosto;
import bs.contabilidad.modelo.ItemMovimientoContabilidadSubcuenta;
import bs.contabilidad.modelo.MascaraContabilidad;
import bs.contabilidad.modelo.MovimientoContabilidad;
import bs.contabilidad.modelo.PeriodoContable;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.Moneda;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.ComprobanteRN;
import bs.global.rn.FormularioRN;
import bs.global.rn.MonedaRN;
import bs.global.rn.PuntoVentaRN;
import bs.global.util.Numeros;
import bs.prestamo.modelo.ItemCapitalPrestamo;
import bs.prestamo.modelo.ItemDescuentoPrestamo;
import bs.prestamo.modelo.ItemGastoOtorgamientoPrestamo;
import bs.prestamo.modelo.ItemInteresMoraPrestamo;
import bs.prestamo.modelo.ItemInteresPrestamo;
import bs.prestamo.modelo.ItemTotalPrestamo;
import bs.prestamo.modelo.MovimientoPrestamo;
import bs.proveedores.modelo.ItemImpuestoProveedor;
import bs.proveedores.modelo.ItemPercepcionProveedor;
import bs.proveedores.modelo.ItemProductoProveedor;
import bs.proveedores.modelo.ItemRetencionProveedor;
import bs.proveedores.modelo.ItemTotalProveedor;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.tesoreria.modelo.ItemMovimientoTesoreriaCentroCosto;
import bs.tesoreria.modelo.ItemMovimientoTesoreriaSubcuenta;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.ventas.modelo.ItemBonificacionVenta;
import bs.ventas.modelo.ItemImpuestoVenta;
import bs.ventas.modelo.ItemPercepcionVenta;
import bs.ventas.modelo.ItemProductoVenta;
import bs.ventas.modelo.ItemTotalVenta;
import bs.ventas.modelo.MovimientoVenta;
import bs.ventas.rn.CondicionPagoVentaRN;
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
 *
 * @author Claudio
 */
@Stateless
public class ContabilidadRN implements Serializable {

    @EJB
    private MonedaRN monedaRN;
    @EJB
    private ModuloRN moduloRN;
    @EJB
    private ContabilidadDAO contabilidadDAO;
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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized MovimientoContabilidad guardar(MovimientoContabilidad m) throws Exception {

        reorganizarNroItem(m);
        calcularImportes(m);
        control(m);

        if (m.getId() == null) {

            tomarNumeroFormulario(m);
            contabilidadDAO.crear(m);

        } else {
            m = contabilidadDAO.editar(m);
        }

        return m;
    }

//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardarPasajeContable(List<MovimientoContabilidad> movimientosContabilidad, List<String> resultadoOk, List<String> resultadoError) {

        if (resultadoOk == null) {
            resultadoOk = new ArrayList<>();
        } else {
            resultadoOk.clear();
        }

        if (resultadoError == null) {
            resultadoError = new ArrayList<>();
        } else {
            resultadoError.clear();
        }

        int movimientosOk = 0;
        int movimientosError = 0;

        for (MovimientoContabilidad m : movimientosContabilidad) {

            try {
                reorganizarNroItem(m);
                calcularImportes(m);
                control(m);
                tomarNumeroFormulario(m);
                contabilidadDAO.crear(m);

                if (m.getMovimientoTesoreria() != null) {
                    contabilidadDAO.editar(m.getMovimientoTesoreria());
                }

                if (m.getMovimientoPrestamo() != null) {
                    contabilidadDAO.editar(m.getMovimientoPrestamo());
                }

                if (m.getMovimientoProveedor() != null) {
                    contabilidadDAO.editar(m.getMovimientoProveedor());
                }

                if (m.getMovimientoVenta() != null) {
                    contabilidadDAO.editar(m.getMovimientoVenta());
                }

                movimientosOk++;
            } catch (Exception e) {

                movimientosError++;
                resultadoError.add(m.getReferencia() + ": " + e);
            }
        }

        resultadoOk.add(0, "Movimientos sin errores : " + movimientosOk);
        resultadoError.add(0, "Movimientos con errores: " + movimientosError);

    }

    public MovimientoContabilidad nuevoMovimiento() {
        return new MovimientoContabilidad();
    }

    public MovimientoContabilidad nuevoMovimiento(String codCG, String sucCG) throws ExcepcionGeneralSistema, Exception {

        return nuevoMovimiento(codCG, sucCG, new Date());
    }

    public MovimientoContabilidad nuevoMovimiento(String codCG, String sucCG, Date fechaMovimiento) throws ExcepcionGeneralSistema, Exception {

        Comprobante comprobante = comprobanteRN.getComprobante("CG", codCG);
        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(sucCG);
        PeriodoContable periodo = periodoRN.getPeriodoByFecha(comprobante.getComprobanteInterno(), fechaMovimiento);
        Parametro p = parametrosRN.getParametro();
        double cotizacion = monedaRN.getCotizacionDia(p.getCodigoMonedaSecundaria()).doubleValue();
        Moneda monedaSec = monedaRN.getMoneda(p.getCodigoMonedaSecundaria());

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No se encontró comprobante de contabilidad " + codCG);
        }
        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No se encontró punto de venta " + sucCG);
        }

        //Buscamos el formulario correspondiente
        Formulario formulario = formularioRN.getFormulario(comprobante, puntoVenta, "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de contabilidad para el comprobante (" + codCG + ") "
                    + "para El punto de venta (" + sucCG + ") "
                    + "con la condición de iva (X) ");
        }

        MovimientoContabilidad movimiento = new MovimientoContabilidad();

        movimiento.setEmpresa(puntoVenta.getEmpresa());
        movimiento.setSucursal(puntoVenta.getSucursal());
        movimiento.setPuntoVenta(puntoVenta);
        movimiento.setUnidadNegocio(puntoVenta.getUnidadNegocio());

        movimiento.setPeriodoContable(periodo);

        movimiento.setComprobante(comprobante);
        movimiento.setFechaMovimiento(fechaMovimiento);
        movimiento.setMonedaSecundaria(monedaSec);
        movimiento.setMonedaRegistracion(comprobante.getMonedaRegistracion());
        movimiento.setCotizacion(cotizacion);

        asignarFormulario(movimiento);

        return movimiento;
    }

    public ItemMovimientoContabilidad nuevoItemMovimientoContabilidad(MovimientoContabilidad movimiento) throws ExcepcionGeneralSistema {

        puedoAgregarItem(movimiento);

        ItemMovimientoContabilidad nItem = new ItemMovimientoContabilidad();

        nItem.setNroitm(movimiento.getItemsDetalle().size() + 1);

        nItem.setMovimiento(movimiento);
        reorganizarNroItem(movimiento);

        return nItem;
    }

    public void eliminarItemMovimientoContabilidad(MovimientoContabilidad movimiento, ItemMovimientoContabilidad nItem) throws Exception {

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

        for (ItemMovimientoContabilidad ip : movimiento.getItemsDetalle()) {

            if (ip.getNroitm() >= 0) {

                if (ip.getNroitm() == nItem.getNroitm()) {
                    indiceItemCuentaContable = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemCuentaContable >= 0) {
            ItemMovimientoContabilidad remove = movimiento.getItemsDetalle().remove(indiceItemCuentaContable);
            if (remove != null) {

                if (movimiento.getId() != null && remove.getId() != null) {
                    contabilidadDAO.eliminar(ItemMovimientoContabilidad.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(movimiento);
    }

    public void puedoAgregarItem(MovimientoContabilidad movimiento) throws ExcepcionGeneralSistema {

//        if (movimiento.getId() != null) {
//            throw new ExcepcionGeneralSistema("El asiento generado, no puede agregar items");
//        }
        if (movimiento.getMonedaSecundaria() == null) {
            throw new ExcepcionGeneralSistema("La moneda secundaria no puede estar vacía");
        }

        if (movimiento.getCotizacion() < 1) {
            throw new ExcepcionGeneralSistema("La cotización del comprobante no puede se nula o menor a 1");
        }
    }

    public void reorganizarNroItem(MovimientoContabilidad movimiento) {

        //Reorganizamos los números de items
        int i = 1;
        for (ItemMovimientoContabilidad ip : movimiento.getItemsDetalle()) {
            ip.setNroitm(i);
            i++;
            int d = 0;
        }
    }

    public void asignarFormulario(MovimientoContabilidad m) throws ExcepcionGeneralSistema {

        Formulario formulario;
        formulario = formularioRN.getFormulario(m.getComprobante(), m.getPuntoVenta(), "X");

        //Este número es temporal y puede cambiar al guardar el objeto.
        m.setNumeroFormulario(formulario.getUltimoNumero() + 1);
        m.setFormulario(formulario);
    }

    public void calcularImportes(MovimientoContabilidad movimiento) {

        double importeTotalDebe = 0;
        double importeTotalHaber = 0;
        double importeTotalDebeSecundario = 0;
        double importeTotalHaberSecundario = 0;

        for (ItemMovimientoContabilidad item : movimiento.getItemsDetalle()) {

            if (item.getImporteDebe() > 0) {

                item.setDebeHaber("D");
                importeTotalDebe = importeTotalDebe + item.getImporteDebe();
                importeTotalDebeSecundario = importeTotalDebeSecundario + item.getImporteDebeMonedaSecundaria();

            }

            if (item.getImporteHaber() > 0) {

                item.setDebeHaber("H");
                importeTotalHaber = importeTotalHaber + item.getImporteHaber();
                importeTotalHaberSecundario = importeTotalHaberSecundario + item.getImporteHaberMonedaSecundaria();
            }

            //Calculamos la imputación en dimensiones
            if (item.getItemsCentroCosto() != null) {
                for (ItemMovimientoContabilidadCentroCosto cc : item.getItemsCentroCosto()) {

                    if (cc.getSubCuentas() != null) {

                        for (ItemMovimientoContabilidadSubcuenta is : cc.getSubCuentas()) {

                            is.setDebhab(item.getDebeHaber());

                            if (is.getPorcentaje() > 0) {

                                is.setImporte(Numeros.redondear((item.getImporteDebe() > 0 ? item.getImporteDebe() : item.getImporteHaber()) * is.getPorcentaje() / 100, 6));
                            }
                        }
                    }
                }
            }

            movimiento.setImporteTotalDebe(importeTotalDebe);
            movimiento.setImporteTotalHaber(importeTotalHaber);

            movimiento.setImporteTotalDebeSecundario(importeTotalDebeSecundario);
            movimiento.setImporteTotalHaberSecundario(importeTotalHaberSecundario);

        }
    }

    private void control(MovimientoContabilidad m) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        sErrores += moduloRN.canSaveModulo("CG", m.getFechaMovimiento());

        if (m.getPeriodoContable() == null) {
            sErrores += "- El comprobante no tienen un período contable asignado \n";
        } else if (!m.getPeriodoContable().getEstado().equals("A")) {
            sErrores += "- El periodo asignado al comprobante no está activo \n";
        }

        if (m.getItemsDetalle().isEmpty()) {
            sErrores += "- El detalle está vacío, no es posible guardar el comprobante \n";
        }

        if (m.getImporteTotalDebe() != m.getImporteTotalHaber()) {
            sErrores += "- El asiento no está balanceado. La suma de la columna debe tiene que se igual a la suma de la columna haber \n";
        }

        if (m.getPeriodoContable() != null && m.getFechaMovimiento().before(m.getPeriodoContable().getFechaInicio())
                || m.getFechaMovimiento().after(m.getPeriodoContable().getFechaFin())) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            sErrores += "- La fecha del comprobante (" + sdf.format(m.getFechaMovimiento()) + ") no se encuentra dentro del rango de fechas de período seleccionado " + m.getPeriodoContable().getDescripcion() + " \n";
        }

        //CONTROLES GENERALES PARA LOS ITEMS
        for (ItemMovimientoContabilidad i : m.getItemsDetalle()) {

            i.setConError(false);

            if (i.getCuentaContable() == null) {

                i.setConError(true);
                sErrores += "- Ingrese una cuenta contable para el item " + i.getNroitm() + " \n";

            }

            if (i.getImporteDebe() < 0) {
                i.setConError(true);
                sErrores += "- El importe debe en el item " + i.getNroitm() + " no puede ser negativo \n";
            }

            if (i.getImporteHaber() < 0) {
                i.setConError(true);
                sErrores += "- El importe haber en el item " + i.getNroitm() + " no puede ser negativo \n";
            }

            if (i.getImporteDebe() == 0 && i.getImporteHaber() == 0) {
                i.setConError(true);
                sErrores += "- El item " + i.getNroitm() + " debe tener al menos un importe mayor a cero \n";
            }

            if (i.getImporteDebe() != 0 && i.getImporteHaber() != 0) {
                i.setConError(true);
                sErrores += "- El item " + i.getNroitm() + " tiene que tener debe o haber igual a cero  \n";
            }

            //En el módulo contable la imputación por centro de costo es siempre obligatoria.
            if (i.isImputaCentroCosto() && i.getItemsCentroCosto() != null) {

                double acumulado;

                for (ItemMovimientoContabilidadCentroCosto cc : i.getItemsCentroCosto()) {

                    acumulado = 0;

                    if (cc.getSubCuentas() != null) {

                        for (ItemMovimientoContabilidadSubcuenta is : cc.getSubCuentas()) {

                            acumulado = acumulado + is.getImporte();
                        }
                    }

                    if ((i.getImporteDebe() + i.getImporteHaber()) != acumulado) {
                        i.setConError(true);
                        sErrores += "|El total imputado (" + acumulado + ") para el centro de costo " + cc.getCentroCosto().getDescripcion()
                                + " no coincide con el importe (" + i.getImporteDebe() + i.getImporteHaber() + ") de la cuenta contable " + i.getCuentaContable().getDescripcion();
                    }
                }
            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public List<MovimientoContabilidad> getListaByBusqueda(Map<String, String> filtro, int cantMax) {

        return contabilidadDAO.getListaByBusqueda(filtro, cantMax);
    }

    private void tomarNumeroFormulario(MovimientoContabilidad m) throws ExcepcionGeneralSistema, Exception {

        if (m.getFormulario().getTipoNumeracion().equals("A")) {
            m.setNumeroFormulario(formularioRN.tomarProximoNumero(m.getFormulario()));
        }

        formularioRN.guardar(m.getFormulario());

    }

    public void asignarCuentaContable(ItemMovimientoContabilidad itemMovimiento, CuentaContable cuentaContable) throws ExcepcionGeneralSistema {

        if (itemMovimiento.getMovimiento().getMonedaSecundaria() == null) {
            throw new ExcepcionGeneralSistema("El comprobante no tiene una moneda secundaria asignada");
        }

        if (cuentaContable.getImputable().equals("N")) {
            throw new ExcepcionGeneralSistema("La cuenta contable seleccionada no es imputable");
        }

        itemMovimiento.setCuentaContable(cuentaContable);
        cargarImputacionCentroCosto(itemMovimiento);
    }

    public MovimientoContabilidad generaComprobante(MovimientoVenta mv) throws ExcepcionGeneralSistema, Exception {

        return null;
    }

    public List<MovimientoContabilidad> generaPasajeContableTesoreria(List<MovimientoTesoreria> movimientos, Date fechaPasaje) throws ExcepcionGeneralSistema, Exception {

        List<MovimientoContabilidad> movimientosContabilidad = new ArrayList<>();

        for (MovimientoTesoreria mt : movimientos) {

            MovimientoContabilidad m = nuevoMovimiento(mt.getComprobante().getComprobanteAsiento().getCodigo(), "0001", fechaPasaje);

            m.setFechaMovimiento(fechaPasaje);
            m.setReferencia(mt.getFormulario().getCodigo() + " " + mt.getNumeroFormulario()
                    + (mt.getNombreEntidad() == null || mt.getNombreEntidad().isEmpty() ? "" : " | " + mt.getNombreEntidad())
                    + (mt.getReferencia() == null || mt.getReferencia().isEmpty() ? "" : " | " + mt.getReferencia()));
            m.setCotizacion(mt.getCotizacion().doubleValue());

            m.setMovimientoTesoreria(mt);
            mt.setMovimientoContabilidad(m);

            for (ItemMovimientoTesoreria it : mt.getItemsDetalle()) {

                ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe().doubleValue());
                i.setImporteHaber(it.getImporteHaber().doubleValue());
                i.setImporteDebeMonedaSecundaria(it.getImporteDebeMonedaSecundaria().doubleValue());
                i.setImporteHaberMonedaSecundaria(it.getImporteHaberMonedaSecundaria().doubleValue());

                m.getItemsDetalle().add(i);

                if (it.getItemsCentroCosto() != null && !it.getItemsCentroCosto().isEmpty()) {

                    for (ItemMovimientoTesoreriaCentroCosto it_cc : it.getItemsCentroCosto()) {

                        i.setImputaCentroCosto(true);

                        ItemMovimientoContabilidadCentroCosto itemCentroCosto = new ItemMovimientoContabilidadCentroCosto();
                        itemCentroCosto.setNroItem(it_cc.getNroItem());
                        itemCentroCosto.setItemContabilidad(i);
                        itemCentroCosto.setCentroCosto(it_cc.getCentroCosto());
                        itemCentroCosto.setDistribucion(it_cc.getDistribucion());

                        i.getItemsCentroCosto().add(itemCentroCosto);

                        if (it_cc.getSubCuentas() != null) {

                            for (ItemMovimientoTesoreriaSubcuenta it_s : it_cc.getSubCuentas()) {

                                ItemMovimientoContabilidadSubcuenta itemSubCuenta = nuevoItemMovimientoSubCuenta(itemCentroCosto);

                                if (itemSubCuenta != null) {

                                    itemSubCuenta.setDebhab(it_s.getDebhab());
                                    itemSubCuenta.setSubCuenta(it_s.getSubCuenta());
                                    itemSubCuenta.setPorcentaje(it_s.getPorcentaje().doubleValue());
                                    itemSubCuenta.setImporte(it_s.getImporte().doubleValue());
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

    public List<MovimientoContabilidad> generaPasajeContablePrestamo(List<MovimientoPrestamo> movimientos, Date fechaPasaje) throws ExcepcionGeneralSistema, Exception {

        List<MovimientoContabilidad> movimientosContabilidad = new ArrayList<>();

        for (MovimientoPrestamo mp : movimientos) {

            MovimientoContabilidad m = nuevoMovimiento(mp.getComprobante().getComprobanteAsiento().getCodigo(), "0001", fechaPasaje);

            m.setFechaMovimiento(fechaPasaje);
            m.setReferencia(mp.getFormulario().getCodigo() + " " + mp.getNumeroFormulario()
                    + (mp.getPrestamo() == null ? "" : " | " + mp.getPrestamo().getCodigo())
                    + (mp.getDestinatario() == null ? "" : " | " + mp.getDestinatario().getRazonSocial()));
            m.setCotizacion(mp.getCotizacion());

            m.setMovimientoPrestamo(mp);
            mp.setMovimientoContabilidad(m);

            for (ItemCapitalPrestamo it : mp.getItemCapital()) {

                ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());

                m.getItemsDetalle().add(i);
            }

            for (ItemInteresPrestamo it : mp.getItemIntereses()) {

                ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());

                m.getItemsDetalle().add(i);
            }

            for (ItemInteresMoraPrestamo it : mp.getItemInteresesMora()) {

                ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());

                m.getItemsDetalle().add(i);
            }

            for (ItemDescuentoPrestamo it : mp.getItemDescuento()) {

                ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());

                m.getItemsDetalle().add(i);
            }

            for (ItemGastoOtorgamientoPrestamo it : mp.getItemGastoOtorgamiento()) {

                ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());

                m.getItemsDetalle().add(i);
            }

            for (ItemTotalPrestamo it : mp.getItemTotal()) {

                ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe());
                i.setImporteHaber(it.getImporteHaber());

                m.getItemsDetalle().add(i);
            }

            movimientosContabilidad.add(m);
        }

        return movimientosContabilidad;
    }

    public List<MovimientoContabilidad> generaPasajeContableProveedores(List<MovimientoProveedor> movimientos, Date fechaPasaje) throws ExcepcionGeneralSistema, Exception {

        List<MovimientoContabilidad> movimientosContabilidad = new ArrayList<>();

        for (MovimientoProveedor mp : movimientos) {

            MovimientoContabilidad m = nuevoMovimiento(mp.getComprobante().getComprobanteAsiento().getCodigo(), "0001", fechaPasaje);

            m.setFechaMovimiento(fechaPasaje);
            m.setReferencia(mp.getFormulario().getCodigo() + " " + mp.getNumeroFormulario()
                    + (mp.getProveedor() == null ? "" : " | " + mp.getProveedor().getNrocta() + "-" + mp.getProveedor().getRazonSocial()));

            m.setCotizacion(mp.getCotizacion().doubleValue());

            m.setMovimientoProveedor(mp);
            mp.setMovimientoContabilidad(m);

            for (ItemProductoProveedor it : mp.getItemProducto()) {

                ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe().doubleValue());
                i.setImporteHaber(it.getImporteHaber().doubleValue());

                m.getItemsDetalle().add(i);
            }

            for (ItemImpuestoProveedor it : mp.getItemImpuesto()) {

                ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe().doubleValue());
                i.setImporteHaber(it.getImporteHaber().doubleValue());

                m.getItemsDetalle().add(i);
            }

            for (ItemRetencionProveedor it : mp.getItemRetencion()) {

                ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe().doubleValue());
                i.setImporteHaber(it.getImporteHaber().doubleValue());

                m.getItemsDetalle().add(i);
            }

            for (ItemPercepcionProveedor it : mp.getItemPercepcion()) {

                ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe().doubleValue());
                i.setImporteHaber(it.getImporteHaber().doubleValue());

                m.getItemsDetalle().add(i);
            }

            for (ItemTotalProveedor it : mp.getItemTotal()) {

                ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                i.setCuentaContable(it.getCuentaContable());
                i.setDebeHaber(it.getDebeHaber());
                i.setImporteDebe(it.getImporteDebe().doubleValue());
                i.setImporteHaber(it.getImporteHaber().doubleValue());

                m.getItemsDetalle().add(i);
            }

            movimientosContabilidad.add(m);
        }

        return movimientosContabilidad;
    }

    public List<MovimientoContabilidad> generaPasajeContableVenta(List<MovimientoVenta> movimientos, Date fechaPasaje) throws ExcepcionGeneralSistema, Exception {

        List<MovimientoContabilidad> movimientosContabilidad = new ArrayList<>();

        for (MovimientoVenta mp : movimientos) {

            MovimientoContabilidad m = nuevoMovimiento(mp.getComprobante().getComprobanteAsiento().getCodigo(), "0001", fechaPasaje);

            m.setFechaMovimiento(fechaPasaje);
            m.setReferencia(mp.getFormulario().getCodigo() + " " + mp.getNumeroFormulario()
                    + (mp.getCliente() == null ? "" : " | " + mp.getCliente().getNrocta() + "-" + mp.getCliente().getRazonSocial()));

            m.setCotizacion(mp.getCotizacion().doubleValue());

            m.setMovimientoVenta(mp);
            mp.setMovimientoContabilidad(m);

            for (ItemProductoVenta it : mp.getItemProducto()) {

                if (it.getImporte().compareTo(BigDecimal.ZERO) != 0) {

                    ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                    i.setCuentaContable(it.getCuentaContable());
                    i.setDebeHaber(it.getDebeHaber());
                    i.setImporteDebe(it.getImporteDebe().doubleValue());
                    i.setImporteHaber(it.getImporteHaber().doubleValue());

                    m.getItemsDetalle().add(i);
                }

            }

            for (ItemImpuestoVenta it : mp.getItemImpuesto()) {

                if (it.getImporte().compareTo(BigDecimal.ZERO) != 0) {

                    ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                    i.setCuentaContable(it.getCuentaContable());
                    i.setDebeHaber(it.getDebeHaber());
                    i.setImporteDebe(it.getImporteDebe().doubleValue());
                    i.setImporteHaber(it.getImporteHaber().doubleValue());

                    m.getItemsDetalle().add(i);
                }

            }

            for (ItemBonificacionVenta it : mp.getItemBonificacion()) {

                if (it.getImporte().compareTo(BigDecimal.ZERO) != 0) {

                    ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                    i.setCuentaContable(it.getCuentaContable());
                    i.setDebeHaber(it.getDebeHaber());
                    i.setImporteDebe(it.getImporteDebe().doubleValue());
                    i.setImporteHaber(it.getImporteHaber().doubleValue());

                    m.getItemsDetalle().add(i);
                }
            }

            for (ItemPercepcionVenta it : mp.getItemPercepcion()) {

                if (it.getImporte().compareTo(BigDecimal.ZERO) != 0) {

                    ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                    i.setCuentaContable(it.getCuentaContable());
                    i.setDebeHaber(it.getDebeHaber());
                    i.setImporteDebe(it.getImporteDebe().doubleValue());
                    i.setImporteHaber(it.getImporteHaber().doubleValue());

                    m.getItemsDetalle().add(i);
                }

            }

            for (ItemTotalVenta it : mp.getItemTotal()) {

                if (it.getImporte().compareTo(BigDecimal.ZERO) != 0) {

                    ItemMovimientoContabilidad i = nuevoItemMovimientoContabilidad(m);
                    i.setCuentaContable(it.getCuentaContable());
                    i.setDebeHaber(it.getDebeHaber());
                    i.setImporteDebe(it.getImporteDebe().doubleValue());
                    i.setImporteHaber(it.getImporteHaber().doubleValue());

                    m.getItemsDetalle().add(i);
                }
            }

            movimientosContabilidad.add(m);
        }

        return movimientosContabilidad;
    }

    /**
     *
     * @param codFormulario
     * @param numeroFormulario
     * @return
     */
    public MovimientoContabilidad getMovimiento(String codFormulario, Integer numeroFormulario) {

        return contabilidadDAO.getMovimiento(codFormulario, numeroFormulario);
    }

    public MovimientoContabilidad getMovimiento(Integer id) {

        return contabilidadDAO.getMovimiento(id);
    }

    public void asignarMascara(MovimientoContabilidad m, MascaraContabilidad mascara) throws ExcepcionGeneralSistema {

        if (mascara.getItems() == null || mascara.getItems().isEmpty()) {

            throw new ExcepcionGeneralSistema("La máscara seleccionada no tienen cuentas contables configuradas");

        } else {

            for (ItemMascaraContabilidad itemMascara : mascara.getItems()) {

                ItemMovimientoContabilidad itemMovimiento = nuevoItemMovimientoContabilidad(m);

                asignarCuentaContable(itemMovimiento, itemMascara.getCuentaContable());
                itemMovimiento.setDebeHaber(itemMascara.getDebeHaber());
                m.getItemsDetalle().add(itemMovimiento);

            }

        }

    }

    public void asignarPeriodoContable(MovimientoContabilidad movimiento) throws ExcepcionGeneralSistema {

        PeriodoContable periodo = periodoRN.getPeriodoByFecha(movimiento.getComprobante().getComprobanteInterno(), movimiento.getFechaMovimiento());

        if (periodo == null) {
            throw new ExcepcionGeneralSistema("No existe un periodo contable activo para la fecha seleccionada");
        }

        movimiento.setPeriodoContable(periodo);

    }

    public MovimientoContabilidad revertirMovimiento(MovimientoContabilidad mRevertir) throws Exception {

        MovimientoContabilidad mr = generarMovimientoReversion(mRevertir);
        mr = guardar(mr);
        mRevertir.setMovimientoReversion(mr);

        guardar(mRevertir);

        return mr;
    }

    private MovimientoContabilidad generarMovimientoReversion(MovimientoContabilidad m) throws ExcepcionGeneralSistema {

        if (m == null) {
            throw new ExcepcionGeneralSistema("Movimiento de contabilidad nulo");
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

        MovimientoContabilidad mr = new MovimientoContabilidad();

        mr.setPeriodoContable(m.getPeriodoContable());
        mr.setComprobante(m.getComprobante().getComprobanteReversion());
        mr.setFormulario(formulario);

        mr.setEmpresa(m.getEmpresa());
        mr.setSucursal(m.getSucursal());
        mr.setPuntoVenta(m.getPuntoVenta());
        mr.setUnidadNegocio(m.getUnidadNegocio());

        mr.setFechaMovimiento(m.getFechaMovimiento());

        mr.setMascaraContabilidad(m.getMascaraContabilidad());
        mr.setReferencia(m.getReferencia());

        mr.setObservaciones("");
        mr.setMonedaSecundaria(m.getMonedaSecundaria());
        mr.setMonedaRegistracion(m.getMonedaRegistracion());
        mr.setCotizacion(m.getCotizacion());

        mr.setMovimientoReversion(m);
        m.setNumeroFormulario(m.getNumeroFormulario() * -1);

        mr.getItemsDetalle().clear();

        for (ItemMovimientoContabilidad i : m.getItemsDetalle()) {

            ItemMovimientoContabilidad ir = new ItemMovimientoContabilidad();

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

        return mr;
    }

    public void cargarImputacionCentroCosto(MovimientoContabilidad m) throws ExcepcionGeneralSistema {

        if (m.getItemsDetalle() != null) {
            for (ItemMovimientoContabilidad i : m.getItemsDetalle()) {

                if (i.getItemsCentroCosto() == null || i.getItemsCentroCosto().isEmpty()) {
                    cargarImputacionCentroCosto(i);
                }
            }
        }
    }

    private void cargarImputacionCentroCosto(ItemMovimientoContabilidad item) throws ExcepcionGeneralSistema {

        if (item.getCuentaContable() == null) {
            return;
        }

        List<CuentaContableCentroCosto> centrosCosto = cuentaContableRN.getCentroCostoByCuenta(item.getCuentaContable());

        if (centrosCosto == null || centrosCosto.isEmpty()) {
            return;
        }

        item.setImputaCentroCosto(true);

        if (item.getItemsCentroCosto() == null) {
            item.setItemsCentroCosto(new ArrayList<>());
        }

        for (CuentaContableCentroCosto d : centrosCosto) {

            ItemMovimientoContabilidadCentroCosto itemCentroCosto = new ItemMovimientoContabilidadCentroCosto();
            itemCentroCosto.setNroItem(item.getItemsCentroCosto().size() + 1);
            itemCentroCosto.setCentroCosto(d.getCentroCosto());
            itemCentroCosto.setDistribucion(d.getDistribucion());

            itemCentroCosto.setItemContabilidad(item);

            if (d.getDistribucion() != null && d.getDistribucion().getItemsDistribucion() != null) {

                for (ItemDistribucion itemDistribucion : d.getDistribucion().getItemsDistribucion()) {

                    ItemMovimientoContabilidadSubcuenta itemSubCuenta = nuevoItemMovimientoSubCuenta(itemCentroCosto);

                    if (itemSubCuenta != null) {

                        itemSubCuenta.setDebhab(item.getDebeHaber());
                        itemSubCuenta.setSubCuenta(itemDistribucion.getSubCuenta());
                        itemSubCuenta.setPorcentaje(itemDistribucion.getPorcentaje().doubleValue());

                        itemSubCuenta.setImporte((item.getImporteDebe() > 0 ? item.getImporteDebe() : item.getImporteHaber()) * itemSubCuenta.getPorcentaje() / 100);
                    }
                }
            }
            item.getItemsCentroCosto().add(itemCentroCosto);
        }
    }

    public ItemMovimientoContabilidadSubcuenta nuevoItemMovimientoSubCuenta(ItemMovimientoContabilidadCentroCosto itemCentroCosto) throws ExcepcionGeneralSistema {

        if (itemCentroCosto == null || itemCentroCosto.getCentroCosto() == null) {
            throw new ExcepcionGeneralSistema("El centro de costo no puede ser nulo");
        }

        if (itemCentroCosto.getSubCuentas() == null) {
            itemCentroCosto.setSubCuentas(new ArrayList<>());
        }

        ItemMovimientoContabilidadSubcuenta itemSubCuenta = new ItemMovimientoContabilidadSubcuenta();
        itemSubCuenta.setNroItem(itemCentroCosto.getSubCuentas().size() + 1);

        itemSubCuenta.setItemCentroCosto(itemCentroCosto);
        itemCentroCosto.getSubCuentas().add(itemSubCuenta);

        return itemSubCuenta;

    }

    public void eliminarItemSubCuenta(ItemMovimientoContabilidadCentroCosto itemCentroCosto, ItemMovimientoContabilidadSubcuenta itemSubCuenta) throws ExcepcionGeneralSistema {

        if (itemCentroCosto == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        if (itemSubCuenta == null) {
            throw new ExcepcionGeneralSistema("Item Sub Cuenta vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemMovimientoContabilidadSubcuenta item : itemCentroCosto.getSubCuentas()) {

            if (item.getNroItem() >= 0) {

                if (item.getNroItem() == itemSubCuenta.getNroItem()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemMovimientoContabilidadSubcuenta remove = itemCentroCosto.getSubCuentas().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    contabilidadDAO.eliminar(ItemMovimientoContabilidadSubcuenta.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

    public List<ItemMovimientoContabilidad> getItemsMovimientoByCuentaContable(CuentaContable cuentaContable, PeriodoContable periodo, Date fechaMovimientoDesde, Date fechaMovimientoHasta) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
