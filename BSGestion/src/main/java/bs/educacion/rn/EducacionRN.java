/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.rn;

import bs.administracion.modelo.Parametro;
import bs.administracion.rn.ModuloRN;
import bs.administracion.rn.ParametrosRN;
import bs.contabilidad.rn.*;
import bs.educacion.dao.MovimientoEducacionDAO;
import bs.educacion.modelo.Arancel;
import bs.educacion.modelo.Carrera;
import bs.educacion.modelo.Curso;
import bs.educacion.modelo.ItemMovimientoEducacion;
import bs.educacion.modelo.ItemPendienteCuentaCorrienteEducacion;
import bs.educacion.modelo.MovimientoEducacion;
import bs.educacion.modelo.ParametroEducacion;
import bs.entidad.modelo.EntidadComercial;
import bs.global.dao.ConceptoDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.Moneda;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.ComprobanteRN;
import bs.global.rn.FormularioRN;
import bs.global.rn.MonedaRN;
import bs.global.rn.PeriodoRN;
import bs.global.rn.PuntoVentaRN;
import bs.global.util.Numeros;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.ventas.rn.CondicionPagoVentaRN;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author GUILLERMO
 */
@Stateless
public class EducacionRN {

    @EJB
    private MonedaRN monedaRN;
    @EJB
    private ModuloRN moduloRN;
    @EJB
    private MovimientoEducacionDAO educacionDAO;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private ComprobanteRN comprobanteRN;
    @EJB
    private ParametrosRN parametrosRN;
    @EJB
    private ParametroEducacionRN parametroEducacionRN;
    @EJB
    protected PuntoVentaRN puntoVentaRN;
    @EJB
    protected CondicionPagoVentaRN condicionDePagoVentaRN;
    @EJB
    private PeriodoRN periodoRN;
    @EJB
    protected CuentaContableRN cuentaContableRN;
    @EJB
    protected CuentaCorrienteEducacionRN cuentaCorrienteRN;
    @EJB
    protected EstadoEducacionRN estadoEducacionRN;
    @EJB
    private ConceptoDAO conceptoDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized MovimientoEducacion guardar(MovimientoEducacion m) throws Exception {

        if (m.getId() == null) {
            preSave(m);
            calcularImportes(m);
            control(m);
            limpiarConceptoEnCero(m);
            tomarNumeroFormulario(m);
            educacionDAO.crear(m);

            // Solo edito, si el comprobante está anulado.
        } else if (m.getMovimientoReversion() != null) {

            m = educacionDAO.editar(m);
        }

        if (m.getMovimientoAplicado() != null) {
            m.getMovimientoAplicado().setEstado(estadoEducacionRN.getEstadoEducacion("Z"));
            educacionDAO.editar(m.getMovimientoAplicado());
        }

        return m;
    }

    public void preSave(MovimientoEducacion m) throws Exception {

    }

    public void limpiarConceptoEnCero(MovimientoEducacion m) {

        m.setItemsMovimiento(m.getItemsMovimiento().stream().filter(i -> i.getImporte() > 0).collect(Collectors.toList()));

    }

    public MovimientoEducacion nuevoMovimiento() {
        return new MovimientoEducacion();
    }

    public MovimientoEducacion nuevoMovimiento(String codED, String ptoED) throws ExcepcionGeneralSistema, Exception {

        return nuevoMovimiento(codED, ptoED, new Date());
    }

    public MovimientoEducacion nuevoMovimiento(String codED, String ptoED, Date fechaMovimiento) throws ExcepcionGeneralSistema, Exception {

        Comprobante comprobante = comprobanteRN.getComprobante("ED", codED);
        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(ptoED);
        Parametro p = parametrosRN.getParametro();
        ParametroEducacion pe = parametroEducacionRN.getParametro();
        double cotizacion = monedaRN.getCotizacionDia(p.getCodigoMonedaSecundaria()).doubleValue();
        Moneda monedaSec = monedaRN.getMoneda(p.getCodigoMonedaSecundaria());

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No se encontró comprobante de Educación " + codED);
        }
        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No se encontró punto de venta " + ptoED);
        }

        //Buscamos el formulario correspondiente
        Formulario formulario = formularioRN.getFormulario(comprobante, puntoVenta, "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de Educación para el comprobante (" + codED + ") "
                    + "para El punto de venta (" + ptoED + ") "
                    + "con la condición de iva (X) ");
        }

        MovimientoEducacion movimiento = new MovimientoEducacion();

        movimiento.setEstado(pe.getEstado());

        movimiento.setEmpresa(puntoVenta.getEmpresa());
        movimiento.setSucursal(puntoVenta.getSucursal());
        movimiento.setPuntoVenta(puntoVenta);

        movimiento.setComprobante(comprobante);
        movimiento.setFechaMovimiento(fechaMovimiento);
        movimiento.setMonedaSecundaria(monedaSec);
        movimiento.setMonedaRegistracion(comprobante.getMonedaRegistracion());
        movimiento.setCotizacion(cotizacion);

        asignarFormulario(movimiento);

        return movimiento;
    }

    public ItemMovimientoEducacion nuevoItemMovimientoEducacion(MovimientoEducacion m) throws ExcepcionGeneralSistema {

        if (m.getItemsMovimiento() == null) {
            m.setItemsMovimiento(new ArrayList<ItemMovimientoEducacion>());
        }

        puedoAgregarItem(m);

        ItemMovimientoEducacion nItem = new ItemMovimientoEducacion();

        nItem.setNroitm(m.getItemsMovimiento().size() + 1);
        nItem.setMovimiento(m);
        nItem.setCotizacion(m.getCotizacion());
        reorganizarNroItem(m);

        return nItem;
    }

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
    public void puedoAgregarItem(MovimientoEducacion m) throws ExcepcionGeneralSistema {

//        if (movimiento.getId() != null) {
//            throw new ExcepcionGeneralSistema("El asiento generado, no puede agregar items");
//        }
        if (m.getMonedaSecundaria() == null) {
            throw new ExcepcionGeneralSistema("La moneda secundaria no puede estar vacía");
        }

        if (m.getCotizacion() < 0) {
            throw new ExcepcionGeneralSistema("La cotización del comprobante no puede se nula o menor a 1");
        }
    }

    public void reorganizarNroItem(MovimientoEducacion m) {

        //Reorganizamos los números de items
        int i = 1;
        for (ItemMovimientoEducacion ip : m.getItemsMovimiento()) {
            ip.setNroitm(i);
            i++;
            int d = 0;
        }
    }

    public void asignarFormulario(MovimientoEducacion m) throws ExcepcionGeneralSistema {

        Formulario formulario;
        formulario = formularioRN.getFormulario(m.getComprobante(), m.getPuntoVenta(), "X");

        //Este número es temporal y puede cambiar al guardar el objeto.
        m.setNumeroFormulario(formulario.getUltimoNumero() + 1);
        m.setFormulario(formulario);
    }

    public void calcularImportes(MovimientoEducacion m) throws ExcepcionGeneralSistema {

        double importeTotalDebe = 0;
        double importeTotalHaber = 0;

        for (ItemMovimientoEducacion item : m.getItemsMovimiento()) {

            if (item.getPorcentaTotalBonificacion() < 0) {
                item.setPorcentaTotalBonificacion(0);
            }

            item.setImporte(Numeros.redondear((item.getPrecio() - item.getPrecio() * item.getPorcentaTotalBonificacion() / 100) * item.getCantidad()));

            if (item.getDebeHaber().equals("D")) {

                item.setImporteDebe(item.getImporte());
                item.setImporteDebeSecundario(Numeros.redondear(item.getImporte() / item.getCotizacion()));
                importeTotalDebe = importeTotalDebe + item.getImporteDebe();

            }

            if (item.getDebeHaber().equals("H")) {

                item.setImporteHaber(item.getImporte());
                item.setImporteHaberSecundario(Numeros.redondear(item.getImporte() / item.getCotizacion()));
                importeTotalHaber = importeTotalHaber + item.getImporteHaber();
            }
        }

        if (m.getComprobante().getEsComprobanteReversion().equals("N")) {
            cuentaCorrienteRN.generarItemCuentaCorriente(m, null);
        }

    }

    private void control(MovimientoEducacion m) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        sErrores += moduloRN.canSaveModulo("ED", m.getFechaMovimiento());

//        if (m.getId() != null) {
//            sErrores += "- No es posible modificar un comprobante de educación \n";
//        }
        if (m.getEmpresa() == null) {
            sErrores += "- El movimiento no tiene una empresa asignada\n";
        }

        if (m.getId() == null
                && (m.getComprobante().getTipoComprobante().equals("IN") || m.getComprobante().getTipoComprobante().equals("PR"))
                && controlarAlumnoSiEstaInscripto(m)) {

            sErrores += "- El Alumno ya esta inscripto en esta carrera y curso \n";
        }

        if (m.getItemsMovimiento().isEmpty()) {
            sErrores += "- El detalle está vacío, no es posible guardar el comprobante \n";
        }

        if (m.getAlumno() == null) {
            sErrores += "- El Alumno está vacío, no es posible guardar el comprobante \n";
        }

        if (m.getComprobante().getTipoComprobante().equals("IN")
                || m.getComprobante().getTipoComprobante().equals("RE")) {

            if (m.getCarrera() == null) {
                sErrores += "- La Carrera está vacía, no es posible guardar el comprobante \n";
            }

            if (m.getCurso() == null) {
                sErrores += "- La curso está vacía, no es posible guardar el comprobante \n";
            }

        }

        //CONTROLES GENERALES PARA LOS ITEMS
        for (ItemMovimientoEducacion i : m.getItemsMovimiento()) {

            i.setConError(false);

            if (i.getCuentaContable() == null) {

                i.setConError(true);
                sErrores += "- Ingrese una cuenta contable para el item " + i.getNroitm() + " \n";
            }

//            if (i.getPrecio() <> 0) {
//
//                i.setConError(true);
//                sErrores += "- El item " + i.getNroitm() + " debe tener un valor mayor a cero en el campo importe \n";
//            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }

    }

    public boolean controlarAlumnoSiEstaInscripto(MovimientoEducacion m) {

        if (m.getComprobante().getEsComprobanteReversion().equals("S")) {
            return false;
        }

        if (m.getCurso() == null) {
            return false;
        }

        return educacionDAO.controlarAlumnoSiEstaInscripto(m.getAlumno().getNrocta(), m.getCarrera().getCodigo(), m.getCurso().getCodigo());
    }

    public List<MovimientoEducacion> getListaByBusqueda(Map<String, String> filtro, int cantMax) {

        return educacionDAO.getListaByBusqueda(filtro, cantMax);
    }

    private void tomarNumeroFormulario(MovimientoEducacion m) throws ExcepcionGeneralSistema, Exception {

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
    public MovimientoEducacion getMovimiento(String codFormulario, Integer numeroFormulario) {

        return educacionDAO.getMovimientoEducacion(codFormulario, numeroFormulario);
    }

    public MovimientoEducacion getMovimiento(Integer id) {

        return educacionDAO.getMovimientoEducacion(id);
    }

    public ItemMovimientoEducacion getItemMovimiento(Integer id) {

        return educacionDAO.getItemMovimientoEducacion(id);
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
    public MovimientoEducacion revertirMovimiento(MovimientoEducacion m) throws Exception {

        MovimientoEducacion mr = generarMovimientoReversion(m);
        mr = guardar(mr);

        m.setEstado(estadoEducacionRN.getEstadoEducacion("X"));
        m.setMovimientoReversion(mr);

        guardar(m);

        return mr;
    }

    public MovimientoEducacion generarMovimientoReversion(MovimientoEducacion m) throws ExcepcionGeneralSistema {

        if (m == null) {
            throw new ExcepcionGeneralSistema("Movimiento de Educación nulo");
        }

        if (m.getEstado().getCodigo().equals("Z")) {
            throw new ExcepcionGeneralSistema("El estado actual del comprobante no permite anular el movimiento");
        }

        if (m.getMovimientoReversion() != null) {
            throw new ExcepcionGeneralSistema("El comprobante ya fue anulado con " + m.getMovimientoReversion().getFormulario().getDescripcion() + " - " + m.getMovimientoReversion().getNumeroFormulario());
        }

        if (m.getComprobante().getComprobanteReversion() == null) {
            throw new ExcepcionGeneralSistema("El comprobante " + m.getComprobante().getDescripcion() + " no tienen comprobante de reversión asociado");
        }

        //System.err.println("Items en cuenta corriente " + (m.getItemsCuentaCorriente() == null ? "vacio" : m.getItemsCuentaCorriente().size()));
        //Lo recibos se deben anular sin restricciones de saldos en cuenta corriente
        //Si el movimiento esá 100 bonificado y no está aplicado también debería anularse, para eso
        //Verificamos que lo items de cuenta corriente estén vacíos
        if (!m.getComprobante().getTipoComprobante().equals("RC")
                && (m.getItemsCuentaCorriente() == null ? true : (m.getItemsCuentaCorriente().size() > 0))
                && cuentaCorrienteRN.getSaldoByMovimiento(m.getId()) == 0) {
            throw new ExcepcionGeneralSistema("El comprobante " + m.getComprobante().getDescripcion() + " ha sido cobrado, anule el/los recibo/s e intente de nuevo ");
        }

        Formulario formulario = formularioRN.getFormulario(m.getComprobante().getComprobanteReversion(), m.getPuntoVenta(), "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de educación para el comprobante (" + m.getComprobante().getComprobanteReversion().getCodigo() + ") "
                    + "para El punto de venta (" + m.getPuntoVenta().getCodigo() + ") "
                    + "con la condición de iva (X)");
        }

        MovimientoEducacion mr = new MovimientoEducacion();

        mr.setEstado(estadoEducacionRN.getEstadoEducacion("A"));
        mr.setEmpresa(m.getEmpresa());
        mr.setSucursal(m.getSucursal());
        mr.setPuntoVenta(m.getPuntoVenta());

        mr.setComprobante(m.getComprobante().getComprobanteReversion());
        mr.setFormulario(formulario);

        mr.setFechaMovimiento(new Date());

        asignarAlumno(mr, m.getAlumno());

        mr.setCarrera(m.getCarrera());
        mr.setPlanEstudio(m.getPlanEstudio());
        mr.setArancel(m.getArancel());
        mr.setCurso(m.getCurso());

        mr.setObservaciones("");
        mr.setMonedaSecundaria(m.getMonedaSecundaria());
        mr.setMonedaRegistracion(m.getMonedaRegistracion());
        mr.setCotizacion(m.getCotizacion());

        mr.setMovimientoReversion(m);
//        m.setNumeroFormulario(m.getNumeroFormulario() * -1);

        mr.getItemsMovimiento().clear();

        for (ItemMovimientoEducacion i : m.getItemsMovimiento()) {

            ItemMovimientoEducacion ir = new ItemMovimientoEducacion();

            ir.setMovimiento(mr);
            ir.setNroitm(mr.getItemsMovimiento().size() + 1);

            ir.setConcepto(i.getConcepto());
            ir.setCuentaContable(i.getCuentaContable());
            ir.setItemArancel(i.getItemArancel());

            ir.setCantidad(i.getCantidad());
            ir.setCuotaDesde(i.getCuotaDesde());
            ir.setCuotaHasta(i.getCuotaHasta());
            ir.setPeriodo(i.getPeriodo());
            ir.setDiaVencimientoSegunPeriodo(i.getDiaVencimientoSegunPeriodo());
            ir.setOrigenFechaCalculoVencimiento(i.getOrigenFechaCalculoVencimiento());

            ir.setCotizacion(i.getCotizacion());

            ir.setPrecio(i.getPrecio());
            ir.setPrecioOriginal(i.getPrecioOriginal());
            ir.setPorcentaTotalBonificacion(i.getPorcentaTotalBonificacion());
            ir.setPrecioSecundario(i.getPrecioSecundario());
            ir.setImporte(i.getImporte());
            ir.setImporteSecundario(i.getImporteSecundario());

            ir.setDebeHaber((i.getDebeHaber().equals("D") ? "H" : "D"));

            ir.setImporteDebe(i.getImporteHaber());
            ir.setImporteDebeSecundario(i.getImporteHaberSecundario());

            ir.setImporteHaber(i.getImporteDebe());
            ir.setImporteHaberSecundario(i.getImporteDebeSecundario());

            mr.getItemsMovimiento().add(ir);
        }

        //Si el comprobante a anular es diferente a un recibo, entonces controlamos los saldos en cuenta corriente.
        if (!m.getComprobante().getTipoComprobante().equals("RC")) {

            List<ItemPendienteCuentaCorrienteEducacion> itemsPendiente = cuentaCorrienteRN.getDebitosPendientes(mr.getAlumno().getNrocta(),
                    mr.getFechaMovimiento(),
                    mr.getMonedaRegistracion().getCodigo(),
                    mr.getComprobante().getComprobanteInterno(),
                    m.getId());

            if ((m.getItemsCuentaCorriente() == null ? true : (m.getItemsCuentaCorriente().size() > 0))
                    && (itemsPendiente == null || itemsPendiente.isEmpty())) {
                throw new ExcepcionGeneralSistema("El comprobante " + m.getComprobante().getDescripcion() + " no items pendientes en cuenta corriente ");
            }

            itemsPendiente.forEach(i -> {
                i.setSeleccionado(true);
                i.setImporteAplicar(i.getPendiente());
            });

            cuentaCorrienteRN.generarItemAplicacionCuentaCorriente(mr, null, itemsPendiente);
        } else {
            cuentaCorrienteRN.generarItemReversion(mr, m);
        }

        return mr;
    }

    public void asignarAlumno(MovimientoEducacion m, EntidadComercial alumno) throws ExcepcionGeneralSistema {

        if (m == null || alumno == null) {
            return;
        }

        m.setAlumno(alumno);

        m.setNombreAlumno(alumno.getApellido() + " " + alumno.getNombre());
        m.setTipoDocumento(alumno.getTipoDocumento());
        m.setNrodoc(alumno.getNrodoc());

        m.setProvincia(alumno.getProvincia());
        m.setLocalidad(alumno.getLocalidad());
        m.setDireccion(alumno.getDireccion());

        asignarFormulario(m);
    }

    public void generarItemsMovimiento(MovimientoEducacion m) throws ExcepcionGeneralSistema {

        if (m == null || m.getCarrera() == null || m.getArancel() == null) {
            return;
        }

        if (m.getArancel().getItems() == null || m.getArancel().getItems().isEmpty()) {
            throw new ExcepcionGeneralSistema("El arancel seleccionado no tiene concepto de inscripciones y cuotas asociados");
        }

        if (m.getItemsMovimiento() == null) {
            m.setItemsMovimiento(new ArrayList<>());
        } else {
            m.getItemsMovimiento().clear();
        }

        if (m.getComprobante().getTipoComprobante().equals("VS")) {

            m.getComprobante().getConceptos().forEach(i -> {

                if (i.getDebeHaber().equals("D")) {

                    try {
                        ItemMovimientoEducacion item = nuevoItemMovimientoEducacion(m);

                        item.setConcepto(i.getConcepto());
                        item.setCuentaContable(i.getConcepto().getCuentaContable());
                        item.setCantidad(1);
                        item.setCuotaDesde(1);
                        item.setCuotaHasta(1);
                        item.setPeriodo(periodoRN.getPeriodo(6));
                        item.setDiaVencimientoSegunPeriodo(0);
                        item.setOrigenFechaCalculoVencimiento("M");

                        item.setPrecio(0);
                        item.setPorcentaTotalBonificacion(0);
                        item.setPrecioOriginal(0);
                        item.setImporte(0);

                        item.setDebeHaber(i.getDebeHaber());
                        item.setImporteDebe(0);
                        item.setImporteDebeSecundario(0);

                        m.getItemsMovimiento().add(item);
                    } catch (ExcepcionGeneralSistema excepcionGeneralSistema) {
                    }

                }

            });
        } else {

            //Cargamos los items de acuerdo a los conceptos configurados en los comprobantes
            m.getComprobante().getConceptos().forEach(c -> {

                m.getArancel().getItems().forEach(i -> {

                    boolean agrego = true;

                    //Si el comprobante aplicado en pre-inscripción, no se agrega ese concepto
                    if (m.getComprobante().getTipoComprobante().equals("IN")
                            && m.getMovimientoAplicado() != null
                            && m.getMovimientoAplicado().getComprobante().getTipoComprobante().equals("PR")
                            && i.getConcepto().getCodigo().equals("C001")) {

                        agrego = false;
                    }

                    if (agrego && i.getConcepto().equals(c.getConcepto())) {

                        try {

                            ItemMovimientoEducacion item = nuevoItemMovimientoEducacion(m);

                            item.setConcepto(i.getConcepto());
                            item.setCuentaContable(i.getConcepto().getCuentaContable());
                            item.setItemArancel(i);
                            item.setCantidad(i.getCantidad());
                            item.setCuotaDesde(i.getCuotaDesde());
                            item.setCuotaHasta(i.getCuotaHasta());
                            item.setPeriodo(i.getPeriodo());
                            item.setDiaVencimientoSegunPeriodo(i.getDiaVencimientoSegunPeriodo());
                            item.setOrigenFechaCalculoVencimiento(i.getOrigenFechaCalculoVencimiento());

                            item.setPrecio(i.getImporte());
                            item.setPorcentaTotalBonificacion(m.getPorcentajeBonificacion());
                            item.setPrecioOriginal(i.getImporte());
                            item.setImporte(Numeros.redondear((item.getPrecio() - item.getPrecio() * item.getPorcentaTotalBonificacion() / 100) * item.getCantidad()));

                            item.setDebeHaber(c.getDebeHaber());
                            item.setImporteDebe(item.getImporte());
                            item.setImporteDebeSecundario(Numeros.redondear(item.getImporte() / item.getCotizacion()));

                            m.getItemsMovimiento().add(item);
                        } catch (ExcepcionGeneralSistema excepcionGeneralSistema) {
                        }
                    }
                });
            });

        }
    }

    public void asignarAranceles(MovimientoEducacion m, Carrera carrera, Curso curso, Arancel arancel) throws ExcepcionGeneralSistema {

        if (m == null || carrera == null) {
            return;
        }

        m.setCarrera(carrera);
        m.setCurso(curso);
        m.setArancel(arancel);
        generarItemsMovimiento(m);
        cuentaCorrienteRN.generarItemCuentaCorriente(m, null);
    }

    public MovimientoEducacion generarComprobante(MovimientoTesoreria mt) throws ExcepcionGeneralSistema, Exception {

        MovimientoEducacion m = nuevoMovimiento(mt.getComprobanteEducacion().getCodigo(), mt.getPuntoVenta().getCodigo());

        m.setEmpresa(mt.getEmpresa());
        m.setSucursal(mt.getSucursal());
        m.setPuntoVenta(mt.getPuntoVenta());

        m.setNumeroFormulario(mt.getNumeroFormulario());

        m.setObservaciones(mt.getObservaciones());
        m.setFechaMovimiento(mt.getFechaMovimiento());

        asignarAlumno(m, mt.getEntidad());

        m.setMonedaSecundaria(mt.getMonedaSecundaria());
        m.setMonedaRegistracion(mt.getMonedaRegistracion());
        m.setCotizacion(mt.getCotizacion().doubleValue());

        generarItemTotal(mt, m);

        generarComprobanteIntereses(m, mt.getTotalIntereses());
        generarComprobanteComisionCobranza(m, mt.getTotalComision());

        //if(mt.isEsAnticipo() || mt.getComprobante().getTipoComprobante().equals("R")){
        if (mt.isEsAnticipo() || mt.getDebitosEducacion() == null || mt.getComprobante().getTipoComprobante().equals("R")) {
            cuentaCorrienteRN.generarItemCuentaCorrienteAnticipo(m);
        } else {
            cuentaCorrienteRN.generarItemCuentaCorrienteRecibo(m, mt.getDebitosEducacion());
        }

        control(m);

        return m;
    }

    public void generarComprobanteReincripcion(MovimientoEducacion me) throws ExcepcionGeneralSistema, Exception {

        ParametroEducacion pe = parametroEducacionRN.getParametro();

        if (pe == null || pe.getImporteReinscripcion() <= 0) {
            throw new ExcepcionGeneralSistema("No hay configurado ningún importe de reinscripción en los parámetros");
        }

        if (me.getComprobante().getComprobanteReinscripcion() == null) {
            throw new ExcepcionGeneralSistema("El comprobante de reinscripción no tiene asociado ningún comprobante de reinscripción");
        }

        if (me.getComprobante().getComprobanteReinscripcion().getConceptos() == null
                || me.getComprobante().getComprobanteReinscripcion().getConceptos().isEmpty()) {
            throw new ExcepcionGeneralSistema("El comprobante de reinscripción no tienen conceptos configurados");
        }

        MovimientoEducacion mr = nuevoMovimiento(me.getComprobante().getComprobanteReinscripcion().getCodigo(), me.getPuntoVenta().getCodigo());

        mr.setEmpresa(me.getEmpresa());
        mr.setSucursal(me.getSucursal());
        mr.setPuntoVenta(me.getPuntoVenta());

        mr.setMonedaSecundaria(me.getMonedaSecundaria());
        mr.setMonedaRegistracion(me.getMonedaRegistracion());
        mr.setCotizacion(me.getCotizacion());

        mr.setObservaciones(me.getObservaciones());

        mr.setCarrera(me.getCarrera());
        mr.setCurso(me.getCurso());

        mr.setAlumno(me.getAlumno());
        mr.setNombreAlumno(me.getAlumno().getNombre() + me.getAlumno().getApellido());
        mr.setTipoDocumento(me.getAlumno().getTipoDocumento());
        mr.setNrodoc(me.getAlumno().getNrodoc());
        mr.setProvincia(me.getAlumno().getProvincia());
        mr.setLocalidad(me.getAlumno().getLocalidad());
        mr.setDireccion(me.getAlumno().getDireccion());

        mr.getComprobante().getConceptos().forEach(cc -> {

            try {
                ItemMovimientoEducacion id = nuevoItemMovimientoEducacion(mr);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());

                if (cc.getCuentaContable() != null) {
                    id.setCuentaContable(cc.getCuentaContable());
                } else if (id.getConcepto().getCuentaContable() != null) {
                    id.setCuentaContable(id.getConcepto().getCuentaContable());
                }

                id.setPeriodo(periodoRN.getPeriodo(6));
                id.setDiaVencimientoSegunPeriodo(0);
                id.setOrigenFechaCalculoVencimiento("M");

                id.setCantidad(1);
                id.setPrecio(pe.getImporteReinscripcion());
                id.setImporte(Numeros.redondear(id.getPrecio() * id.getCantidad()));

                if (id.getDebeHaber().equals("D")) {
                    id.setImporteDebe(id.getImporte());
                    id.setImporteDebeSecundario(Numeros.redondear(id.getImporte() / id.getCotizacion()));
                } else {
                    id.setImporteHaber(id.getImporte());
                    id.setImporteHaberSecundario(Numeros.redondear(id.getImporte() / id.getCotizacion()));
                }

                mr.getItemsMovimiento().add(id);

            } catch (ExcepcionGeneralSistema ex) {

                Logger.getLogger(EducacionRN.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        });

        cuentaCorrienteRN.generarItemCuentaCorriente(mr);

        mr.setMovimientoPrincipal(me);
        me.getMovimientosRelacionados().add(mr);

        guardar(mr);

    }

    public void generarComprobanteIntereses(MovimientoEducacion me, double totalIntereses) throws ExcepcionGeneralSistema, Exception {

        if (totalIntereses <= 0) {
            return;
        }

        if (me.getComprobante().getComprobanteIntereses() == null) {
            throw new ExcepcionGeneralSistema("El comprobante de educación no tiene asociado ningún comprobante de comisión por cobranza configurado");
        }

        if (me.getComprobante().getComprobanteIntereses().getConceptos() == null
                || me.getComprobante().getComprobanteIntereses().getConceptos().isEmpty()) {
            throw new ExcepcionGeneralSistema("El comprobante de comisión por cobranza no tiene conceptos configurados");
        }

        MovimientoEducacion mi = nuevoMovimiento(me.getComprobante().getComprobanteIntereses().getCodigo(), me.getPuntoVenta().getCodigo());

        mi.setEmpresa(me.getEmpresa());
        mi.setSucursal(me.getSucursal());
        mi.setPuntoVenta(me.getPuntoVenta());

        mi.setMonedaSecundaria(me.getMonedaSecundaria());
        mi.setMonedaRegistracion(me.getMonedaRegistracion());

        //mi.setCotizacion(mt.getCotizacion());
        mi.setObservaciones(me.getObservaciones());

//            mi.setCarrera(ii.getItemMovimientoEducacion().getMovimiento().getCarrera());
//            mi.setCurso(ii.getItemMovimientoEducacion().getMovimiento().getCurso());
        mi.setAlumno(me.getAlumno());
        mi.setNombreAlumno(me.getAlumno().getNombre() + me.getAlumno().getApellido());
        mi.setTipoDocumento(me.getAlumno().getTipoDocumento());
        mi.setNrodoc(me.getAlumno().getNrodoc());
        mi.setProvincia(me.getAlumno().getProvincia());
        mi.setLocalidad(me.getAlumno().getLocalidad());
        mi.setDireccion(me.getAlumno().getDireccion());

        mi.getComprobante().getConceptos().forEach(cc -> {

            try {
                ItemMovimientoEducacion id = nuevoItemMovimientoEducacion(mi);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());

                if (cc.getCuentaContable() != null) {
                    id.setCuentaContable(cc.getCuentaContable());
                } else if (id.getConcepto().getCuentaContable() != null) {
                    id.setCuentaContable(id.getConcepto().getCuentaContable());
                }

                id.setPeriodo(periodoRN.getPeriodo(6));
                id.setDiaVencimientoSegunPeriodo(0);
                id.setOrigenFechaCalculoVencimiento("M");

                id.setCantidad(1);
                id.setPrecio(totalIntereses);
                id.setImporte(Numeros.redondear(id.getPrecio() * id.getCantidad()));

                if (id.getDebeHaber().equals("D")) {
                    id.setImporteDebe(id.getImporte());
                    id.setImporteDebeSecundario(Numeros.redondear(id.getImporte() / id.getCotizacion()));
                } else {
                    id.setImporteHaber(id.getImporte());
                    id.setImporteHaberSecundario(Numeros.redondear(id.getImporte() / id.getCotizacion()));
                }

                id.setObservaciones("Intereses por mora");
                mi.getItemsMovimiento().add(id);

            } catch (ExcepcionGeneralSistema ex) {
                Logger.getLogger(EducacionRN.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        });

        tomarNumeroFormulario(mi);

        mi.setMovimientoPrincipal(me);

        control(mi);

        me.getMovimientosRelacionados().add(mi);

    }

    public void generarComprobanteComisionCobranza(MovimientoEducacion me, double totalComision) throws ExcepcionGeneralSistema, Exception {

        if (totalComision <= 0) {
            return;
        }

        if (me.getComprobante().getComprobanteComisionCobranza() == null) {
            throw new ExcepcionGeneralSistema("El comprobante educación no tiene asociado ningún comprobante de comisión por cobranza");
        }

        if (me.getComprobante().getComprobanteComisionCobranza().getConceptos() == null
                || me.getComprobante().getComprobanteComisionCobranza().getConceptos().isEmpty()) {
            throw new ExcepcionGeneralSistema("El comprobante de comisión por cobranza no tiene conceptos configurados");
        }

        MovimientoEducacion mc = nuevoMovimiento(me.getComprobante().getComprobanteComisionCobranza().getCodigo(), me.getPuntoVenta().getCodigo());

        mc.setEmpresa(me.getEmpresa());
        mc.setSucursal(me.getSucursal());
        mc.setPuntoVenta(me.getPuntoVenta());

        mc.setMonedaSecundaria(me.getMonedaSecundaria());
        mc.setMonedaRegistracion(me.getMonedaRegistracion());

        //mi.setCotizacion(mt.getCotizacion());
        mc.setObservaciones(me.getObservaciones());

//            mi.setCarrera(ii.getItemMovimientoEducacion().getMovimiento().getCarrera());
//            mi.setCurso(ii.getItemMovimientoEducacion().getMovimiento().getCurso());
        mc.setAlumno(me.getAlumno());
        mc.setNombreAlumno(me.getAlumno().getNombre() + me.getAlumno().getApellido());
        mc.setTipoDocumento(me.getAlumno().getTipoDocumento());
        mc.setNrodoc(me.getAlumno().getNrodoc());
        mc.setProvincia(me.getAlumno().getProvincia());
        mc.setLocalidad(me.getAlumno().getLocalidad());
        mc.setDireccion(me.getAlumno().getDireccion());

        mc.getComprobante().getConceptos().forEach(cc -> {

            try {
                ItemMovimientoEducacion id = nuevoItemMovimientoEducacion(mc);
                id.setDebeHaber(cc.getDebeHaber());
                id.setConcepto(cc.getConcepto());

                if (cc.getCuentaContable() != null) {
                    id.setCuentaContable(cc.getCuentaContable());
                } else if (id.getConcepto().getCuentaContable() != null) {
                    id.setCuentaContable(id.getConcepto().getCuentaContable());
                }

                id.setPeriodo(periodoRN.getPeriodo(6));
                id.setDiaVencimientoSegunPeriodo(0);
                id.setOrigenFechaCalculoVencimiento("M");

                id.setCantidad(1);
                id.setPrecio(totalComision);
                id.setImporte(Numeros.redondear(id.getPrecio() * id.getCantidad()));

                if (id.getDebeHaber().equals("D")) {
                    id.setImporteDebe(id.getImporte());
                    id.setImporteDebeSecundario(Numeros.redondear(id.getImporte() / id.getCotizacion()));
                } else {
                    id.setImporteHaber(id.getImporte());
                    id.setImporteHaberSecundario(Numeros.redondear(id.getImporte() / id.getCotizacion()));
                }

                id.setObservaciones("Comisión por cobranza");
                mc.getItemsMovimiento().add(id);

            } catch (ExcepcionGeneralSistema ex) {
                Logger.getLogger(EducacionRN.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        });

        tomarNumeroFormulario(mc);

        mc.setMovimientoPrincipal(me);
        control(mc);
        me.getMovimientosRelacionados().add(mc);

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

        double importeDebe = mt.getImporteTotalDebe().doubleValue() - mt.getTotalIntereses() - mt.getTotalComision();
        double importeHaber = mt.getImporteTotalHaber().doubleValue() - mt.getTotalIntereses() - mt.getTotalComision();

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

    public void validarEstado(List<ItemPendienteCuentaCorrienteEducacion> debitos) {

        debitos.forEach(i -> {

            if (cuentaCorrienteRN.getSaldoByMovimiento(i.getItemMovimientoEducacion().getMovimiento().getId()) == 0) {

                i.getItemMovimientoEducacion().getMovimiento().setEstado(estadoEducacionRN.getEstadoEducacion("Z"));
                educacionDAO.editar(i.getItemMovimientoEducacion().getMovimiento());

            }

        });

    }

}
