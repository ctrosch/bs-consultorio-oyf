/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.rn;

import bs.administracion.rn.ModuloRN;
import bs.administracion.rn.ParametrosRN;
import bs.compra.modelo.MovimientoCompra;
import bs.contabilidad.modelo.CuentaContableCentroCosto;
import bs.contabilidad.modelo.Distribucion;
import bs.contabilidad.modelo.ItemDistribucion;
import bs.contabilidad.rn.CuentaContableRN;
import bs.educacion.modelo.MovimientoEducacion;
import bs.educacion.rn.EducacionRN;
import bs.entidad.modelo.EntidadComercial;
import bs.facturacion.modelo.MovimientoFacturacion;
import bs.global.dao.ComprobanteDAO;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Comprobante;
import bs.global.modelo.ConceptoComprobante;
import bs.global.modelo.Formulario;
import bs.global.modelo.ItemDistribucionConcepto;
import bs.global.modelo.Moneda;
import bs.global.modelo.PuntoVenta;
import bs.global.rn.FormularioRN;
import bs.global.rn.MonedaRN;
import bs.global.rn.PuntoVentaRN;
import bs.prestamo.modelo.MovimientoPrestamo;
import bs.prestamo.modelo.Prestamo;
import bs.prestamo.rn.MovimientoPrestamoRN;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.proveedores.rn.MovimientoProveedorRN;
import bs.tesoreria.dao.MovimientoTesoreriaDAO;
import bs.tesoreria.modelo.CuentaTesoreria;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.tesoreria.modelo.ItemMovimientoTesoreriaCentroCosto;
import bs.tesoreria.modelo.ItemMovimientoTesoreriaSubcuenta;
import bs.tesoreria.modelo.ItemSaldoPendienteCuentaTesoreria;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.ventas.modelo.MovimientoVenta;
import bs.ventas.rn.CuentaCorrienteRN;
import bs.ventas.rn.MovimientoVentaRN;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
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
public class MovimientoTesoreriaRN implements Serializable {

    @EJB
    private MovimientoTesoreriaDAO tesoreriaDAO;
    @EJB
    private MonedaRN monedaRN;
    @EJB
    private ModuloRN moduloRN;
    @EJB
    private FormularioRN formularioRN;
    @EJB
    private ComprobanteDAO comprobanteDAO;
    @EJB
    private MovimientoVentaRN ventaRN;
    @EJB
    private MovimientoPrestamoRN prestamoRN;
    @EJB
    private EducacionRN educacionRN;
    @EJB
    private MovimientoProveedorRN proveedorRN;
    @EJB
    private PuntoVentaRN puntoVentaRN;
    @EJB
    protected ParametrosRN parametrosRN;
    @EJB
    protected CuentaContableRN cuentaContableRN;

    @EJB
    private CuentaCorrienteRN cuentaCorrienteClienteRN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized MovimientoTesoreria guardar(MovimientoTesoreria m) throws Exception {

        if (m.getId() == null) {

            calcularImportes(m);
            controlComprobante(m);
            limpiarConceptoEnCero(m);
            generarComprobantesAdicionales(m);
            tomarNumeroFormulario(m);

            tesoreriaDAO.crear(m);

            // Solo edito si el comprobante está anulado.
        } else if (m.getMovimientoReversion() != null) {

            m = (MovimientoTesoreria) tesoreriaDAO.editar(m);
        }

        return m;
    }

    public void controlComprobante(MovimientoTesoreria m) throws ExcepcionGeneralSistema, Exception {

        String sErrores = "";

        sErrores += moduloRN.canSaveModulo("CJ", m.getFechaMovimiento());

        if (m.getMovimientoContabilidad() != null) {
            throw new ExcepcionGeneralSistema("El comprobante fue pasado a la contabilidad, no es posible guardar");
        }

        if (formularioRN.existeMovimiento(m.getFormulario(), m.getNumeroFormulario())) {

            if (m.getFormulario().getTipoNumeracion().equals("I") || m.getFormulario().getTipoNumeracion().equals("S")) {
                sErrores += "Ya existe un movimiento cargado con el número de formulario " + m.getNumeroFormulario();
            } else {
                tomarNumeroFormulario(m);
            }
        }

        if (m.getItemsDetalle().isEmpty()) {
            sErrores += "Detalle de conceptos vacío o importes negativos, no es posible guardar el comprobante";
        }

        if (m.getComprobante().getTipoComprobante().equals("I")
                && m.getImporteTotalDebe().compareTo(BigDecimal.ZERO) <= 0) {
            sErrores += "|El importe \"Debe\", tiene que se mayor a cero";
        }

        if (m.getComprobante().getTipoComprobante().equals("E")
                && m.getImporteTotalHaber().compareTo(BigDecimal.ZERO) <= 0) {
            sErrores += "|El importe \"Haber\", tiene que se mayor a cero";
        }

//        if (m.getComprobante().getTipoComprobante().equals("T")
//                && (m.getImporteTotalDebe().compareTo(BigDecimal.ZERO) < 0 || m.getImporteTotalHaber().compareTo(BigDecimal.ZERO) < 0)) {
//            throw new ExcepcionGeneralSistema("Los importes \"Debe\" y \"Haber\" tienen que ser mayor a cero");
//        }
//
//        if (m.getComprobante().getTipoComprobante().equals("T")
//                && m.getImporteTotalDebe().compareTo(m.getImporteTotalHaber()) != 0) {
//            throw new ExcepcionGeneralSistema("Los importes \"Debe\" y \"Haber\" tiene que ser iguales");
//        }
        if ((m.getImporteTotalDebe().compareTo(BigDecimal.ZERO) < 0 || m.getImporteTotalHaber().compareTo(BigDecimal.ZERO) < 0)) {
            sErrores += "|Los importes \"Debe\" y \"Haber\" en el comprobante de tesorería tienen que ser mayor a cero";
        }

        if (m.getImporteTotalDebe().compareTo(m.getImporteTotalHaber()) != 0) {
            sErrores += "|Los importes \"Debe\" y \"Haber\" en el comprobante de tesorería tienen que ser iguales";
        }

        if (m.getComprobanteVenta() != null || m.getComprobanteProveedor() != null) {

            if (m.getEntidad() == null) {
                sErrores += "|El cliente no puede estar vacío";
            }

            if (m.getCobrador() == null) {
                sErrores += "|Seleccione un cobrador";
            }
        }

        if (m.getComprobanteProveedor() != null) {

            if (m.getEntidad() == null) {
                sErrores += "|El proveedor no puede estar vacío";
            }
        }

        if (m.getComprobantePrestamo() != null) {

            if (m.getEntidad() == null) {
                sErrores += "|El destinatario no puede estar vacío";
            }
        }

        for (ItemMovimientoTesoreria i : m.getItemsDetalle()) {

            i.setConError(false);

            if (i.getConcepto() == null) {
                i.setConError(true);
                sErrores += "|El item " + i.getNroItem() + " no tienen asignado el concepto de tesorería";
            }

            if (i.getCuentaContable() == null) {
                i.setConError(true);
                sErrores += "|El item " + i.getNroItem() + " no tienen asignada la cuenta contable";
            } else {

                if (i.getImporte().compareTo(BigDecimal.ZERO) > 0 && i.getCuentaContable().getImputable().equals("N")) {
                    i.setConError(true);
                    sErrores += "| La cuenta contable asignada a el concepto " + i.getConcepto().getDescripcion() + " es no imputable. Modifique la cuenta desde el concepto de tesorería.";
                }
            }

            //Validamos las cuentas bancarias
            if (i.getConcepto().getPideFechaEmision().equals("S")
                    || (i.getCuentaTesoreria() != null
                    && i.getCuentaTesoreria().getTipoCuenta().getCodigo().equals("3"))) {

                if (i.getFechaCheque() == null) {
                    i.setConError(true);
                    sErrores += "|La fecha del cheque, transferencia o depósito no puede estar en blanco en " + i.getConcepto().getDescripcion();
                }

            }

            //Validamos los cheques
            if (i.getCuentaTesoreria() != null
                    && i.getCuentaTesoreria().getTipoCuenta().getCodigo().equals("2")) {

                //Se valida fecha  que sea ingreso.
//                if(i.getFechaVencimiento().before(i.getFechaCheque())
//                        && i.getDebeHaber().equals("D")){
//                    throw  new ExcepcionGeneralSistema("La fecha de vencimiento es menor a la fecha de emisión para "+ i.getConcepto().getDescripcion());
//                }
                if (i.getCheque() == null || i.getCheque().isEmpty()) {
                    i.setConError(true);
                    sErrores += "|Ingrese el número de cheque";
                }

                if (i.getBanco() == null) {
                    i.setConError(true);
                    sErrores += "|Seleccione el banco para el valor " + i.getCheque();
                }

//                if(i.getFirmanteDocumento()==null || i.getFirmanteDocumento().isEmpty()){
//                    throw  new ExcepcionGeneralSistema("El firmante del valor "+i.getCheque()+" no puede estar en blanco");
//                }
//
//                if(i.getNumeroDocumento()==null || i.getNumeroDocumento().isEmpty()){
//                    throw  new ExcepcionGeneralSistema("El cuit del valor "+i.getCheque()+" no puede estar en blanco");
//                }
                if (i.getCategoriaCheque() == null || i.getCategoriaCheque().isEmpty()) {
                    i.setConError(true);
                    sErrores += "|La categoría del valor " + i.getCheque() + " no puede estar en blanco";
                }
            }

//            System.err.println("i.getConcepto().getPideNumeroCheque() " + i.getConcepto().getPideNumeroCheque());
            if (i.getCuentaTesoreria() != null && i.getConcepto().getPideNumeroCheque().equals("S")) {

                if (i.getFechaCheque() == null) {
                    i.setConError(true);
                    sErrores += "|La fecha de emisión no puede estar en blanco para " + i.getConcepto().getDescripcion();
                }

                if (i.getFechaVencimiento() == null) {
                    i.setConError(true);
                    sErrores += "|La fecha de vencimiento no puede estar en blanco para " + i.getConcepto().getDescripcion();
                }

                if (i.getFechaCheque() != null
                        && i.getFechaVencimiento() != null
                        && i.getFechaVencimiento().before(i.getFechaCheque())) {
                    i.setConError(true);
                    sErrores += "|La fecha de vencimiento es menor a la fecha de emisión para " + i.getConcepto().getDescripcion();
                }

                if (i.getCheque() == null || i.getCheque().isEmpty()) {
                    i.setConError(true);
                    sErrores += "|Ingrese el número de cheque para el concepto " + i.getConcepto().getDescripcion();
                }
            }

            if (i.getCuentaTesoreria() != null && i.getConcepto().getPideFechaEmision().equals("S")) {

                if (i.getFechaCheque() == null) {
                    i.setConError(true);
                    sErrores += "|La fecha de emisión no puede estar en blanco para " + i.getConcepto().getDescripcion();
                }

            }

            if (i.isImputaCentroCosto()
                    && i.getItemsCentroCosto() != null
                    && m.getComprobante().getImputacionObligatoriaEnCentroCosto().equals("S")) {

                BigDecimal acumulado = null;

                for (ItemMovimientoTesoreriaCentroCosto cc : i.getItemsCentroCosto()) {

                    acumulado = BigDecimal.ZERO;

                    if (cc.getSubCuentas() != null) {

                        for (ItemMovimientoTesoreriaSubcuenta is : cc.getSubCuentas()) {

                            acumulado = acumulado.add(is.getImporte());
                        }
                    }

                    if (i.getImporte().compareTo(acumulado) != 0) {
                        i.setConError(true);
                        sErrores += "|El total imputado para el centro de costo " + cc.getCentroCosto().getDescripcion() + " no coincide "
                                + "con el importe del concepto " + i.getConcepto().getDescripcion();
                    }
                }
            }
        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public MovimientoTesoreria generarComprobante(MovimientoFacturacion mf) throws Exception {

        MovimientoTesoreria mt = new MovimientoTesoreria();
        Formulario formulario = formularioRN.getFormulario(mf.getComprobanteTesoreria(), mf.getPuntoVenta(), "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de tesorería para el comprobante (" + mf.getComprobanteTesoreria().getCodigo() + ") "
                    + "para El punto de venta (" + mf.getPuntoVenta() + ") "
                    + "con la condición de iva (X)");
        }

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("El formulario no puede ser nulo");
        }

        mt.setEmpresa(mf.getEmpresa());
        mt.setSucursal(mf.getSucursal());
        mt.setPuntoVenta(mf.getPuntoVenta());
        mt.setUnidadNegocio(mf.getUnidadNegocio());

        mt.setComprobante(mf.getComprobanteTesoreria());
        mt.setFormulario(formulario);
        mt.setNumeroFormulario(mf.getNumeroFormulario());
        mt.setFechaMovimiento(new Date());
        mt.setMonedaSecundaria(mf.getMonedaSecundaria());
        mt.setMonedaRegistracion(mf.getMonedaRegistracion());
        mt.setCotizacion(mf.getCotizacion());

        if (mf.getCliente() != null) {
            mt.setEntidad(mf.getCliente());
            mt.setNombreEntidad(mf.getCliente().getRazonSocial());
        }

        generarItemsMovimiento(mt);

        return mt;
    }

    /**
     * @param mc Movimiento de compras a partir del cual se genera el movimiento
     * de tesoreria
     * @return Movimiento de tesoreria generado
     * @throws ExcepcionGeneralSistema
     * @throws Exception
     */
    public MovimientoTesoreria generarComprobante(MovimientoCompra mc) throws Exception {

        MovimientoTesoreria mt = new MovimientoTesoreria();

        Formulario formulario = formularioRN.getFormulario(mc.getComprobanteTesoreria(), mc.getPuntoVenta(), "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de tesorería para el comprobante (" + mc.getComprobanteTesoreria().getCodigo() + ") "
                    + "para El punto de venta (" + mc.getPuntoVenta() + ") "
                    + "con la condición de iva (X)");
        }

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("El formulario no puede ser nulo");
        }

        mt.setEmpresa(mc.getEmpresa());
        mt.setSucursal(mc.getSucursal());
        mt.setPuntoVenta(mc.getPuntoVenta());
        mt.setUnidadNegocio(mc.getUnidadNegocio());

        mt.setComprobante(mc.getComprobanteTesoreria());
        mt.setFormulario(formulario);
        mt.setNumeroFormulario(formulario.getUltimoNumero() + 1);

        mt.setFechaMovimiento(new Date());
        mt.setMonedaSecundaria(mc.getMonedaSecundaria());
        mt.setMonedaRegistracion(mc.getMonedaRegistracion());
        mt.setCotizacion(mc.getCotizacion());

        if (mc.getProveedor() != null) {
            mt.setEntidad(mc.getProveedor());
            mt.setNombreEntidad(mc.getProveedor().getRazonSocial());
        }

        generarItemsMovimiento(mt);

        return mt;
    }

    /**
     * @param mp Movimiento de proveedor a partir del cual se genera el
     * movimiento de tesoreria
     * @return Movimiento de tesoreria generado
     * @throws ExcepcionGeneralSistema
     * @throws Exception
     */
    public MovimientoTesoreria generarComprobante(MovimientoProveedor mp) throws Exception {

        MovimientoTesoreria mt = new MovimientoTesoreria();

        Formulario formulario = formularioRN.getFormulario(mp.getComprobanteTesoreria(), mp.getPuntoVenta(), "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de tesorería para el comprobante (" + mp.getComprobanteTesoreria().getCodigo() + ") "
                    + "para El punto de venta (" + mp.getPuntoVenta() + ") "
                    + "con la condición de iva (X)");
        }

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("El formulario no puede ser nulo");
        }

        mt.setEmpresa(mp.getEmpresa());
        mt.setSucursal(mp.getSucursal());
        mt.setPuntoVenta(mp.getPuntoVenta());
        mt.setUnidadNegocio(mp.getUnidadNegocio());

        mt.setComprobante(mp.getComprobanteTesoreria());
        mt.setFormulario(formulario);
        mt.setNumeroFormulario(formulario.getUltimoNumero() + 1);
        mt.setFechaMovimiento(new Date());

        mt.setMonedaSecundaria(mp.getMonedaSecundaria());
        mt.setMonedaRegistracion(mp.getMonedaRegistracion());
        mt.setCotizacion(mp.getCotizacion());

        if (mp.getProveedor() != null) {
            mt.setEntidad(mp.getProveedor());
            mt.setNombreEntidad(mp.getProveedor().getRazonSocial());
        }

        generarItemsMovimiento(mt);

        return mt;
    }

    public MovimientoTesoreria generarComprobante(Prestamo m) {

        MovimientoTesoreria mt = new MovimientoTesoreria();

        return mt;
    }

    public MovimientoTesoreria nuevoMovimiento(String CODCJ, String SUCCJ) throws Exception {

        return nuevoMovimiento(CODCJ, "", "", "", "", SUCCJ);
    }

    public MovimientoTesoreria nuevoMovimiento(
            String CODCJ,
            String CODVT,
            String CODPV,
            String CODPR,
            String CODED,
            String SUCCJ) throws Exception {

        if (!CODVT.isEmpty() && !CODPV.isEmpty()) {
            throw new ExcepcionGeneralSistema("No es posible definir un comprobante de venta y proveedor al mismo tiempo");
        }

        Comprobante comprobante = comprobanteDAO.getComprobante("CJ", CODCJ);

        Comprobante comprobanteVenta = null;
        Comprobante comprobanteProveedor = null;
        Comprobante comprobantePrestamo = null;
        Comprobante comprobanteEducacion = null;

        if (!CODVT.isEmpty()) {
            comprobanteVenta = comprobanteDAO.getComprobante("VT", CODVT);
        }

        if (!CODPV.isEmpty()) {
            comprobanteProveedor = comprobanteDAO.getComprobante("PV", CODPV);
        }

        if (!CODPR.isEmpty()) {
            comprobantePrestamo = comprobanteDAO.getComprobante("PR", CODPR);
        }

        if (!CODED.isEmpty()) {
            comprobanteEducacion = comprobanteDAO.getComprobante("ED", CODED);
        }

        PuntoVenta puntoVenta = puntoVentaRN.getPuntoVenta(SUCCJ);

        if (comprobante == null) {
            throw new ExcepcionGeneralSistema("No se encontró comprobante de tesorería (" + CODCJ + ")");
        }
        if (puntoVenta == null) {
            throw new ExcepcionGeneralSistema("No se encontró punto de venta (" + SUCCJ + ")");
        }

        Formulario formulario = formularioRN.getFormulario(comprobante, puntoVenta, "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de tesorería para el comprobante (" + CODCJ + ") "
                    + "para El punto de venta (" + SUCCJ + ") "
                    + "con la condición de iva (X)");
        }

        MovimientoTesoreria m = new MovimientoTesoreria();
        Moneda moneda = monedaRN.getMoneda(parametrosRN.getParametro().getCodigoMonedaSecundaria());
        BigDecimal cotizacion = monedaRN.getCotizacionDia(parametrosRN.getParametro().getCodigoMonedaSecundaria());

        m.setEmpresa(puntoVenta.getEmpresa());
        m.setSucursal(puntoVenta.getSucursal());
        m.setPuntoVenta(puntoVenta);
        m.setUnidadNegocio(puntoVenta.getUnidadNegocio());

        m.setComprobante(comprobante);
        m.setFormulario(formulario);
        m.setNumeroFormulario(formulario.getUltimoNumero() + 1);

        if (m.getFormulario().getRecuperacionFecha().equals("U")) {
            m.setFechaMovimiento(m.getFormulario().getUltimaFecha());
        } else {
            m.setFechaMovimiento(new Date());
        }

        m.setMonedaSecundaria(moneda);
        m.setMonedaRegistracion(comprobante.getMonedaRegistracion());
        m.setCotizacion(cotizacion);
        m.setComprobanteVenta(comprobanteVenta);
        m.setComprobanteProveedor(comprobanteProveedor);
        m.setComprobantePrestamo(comprobantePrestamo);
        m.setComprobanteEducacion(comprobanteEducacion);

        /**
         * Si el no es un comprobante de reversión, entonces generamos los
         * items.
         */
        if (!m.getComprobante().getTipoComprobante().equals("R")) {
            generarItemsMovimiento(m);
        }

        return m;
    }

    public void limpiarConceptoEnCero(MovimientoTesoreria m) {

        for (int i = 0; i < m.getItemsDetalle().size(); i++) {

            if (m.getItemsDetalle().get(i).getImporte().compareTo(BigDecimal.ZERO) <= 0) {
//                System.err.println("menor remueve: " + m.getItemsDetalle().remove(i).getConcepto().getDescripcion());
                //m.getItemsDebe().remove(i);
            }
        }
    }

    /**
     * Procesa los items, unificando los datos en una sola lista itemDetalle que
     * es la lista que persiste
     *
     * @param m Movimiento de tesoreria
     * @throws Exception
     */
    public void procesarConceptos(MovimientoTesoreria m) throws Exception {

        //Si es comprobante de reversión no procesa conceptos
        if (m.getComprobante().getTipoComprobante().equals("R")) {
            return;
        }

        m.getItemsDetalle().clear();

        for (ItemMovimientoTesoreria i : m.getItemsDebe()) {

            i.setCotizacion(m.getCotizacion());

            if (i.getCuentaTesoreria() != null && !i.getCuentaTesoreria().getCodigoMoneda().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
                if (i.getImporteMonedaSecundaria() != null && i.getImporteMonedaSecundaria().compareTo(BigDecimal.ZERO) > 0) {
                    i.setImporte(i.getImporteMonedaSecundaria().multiply(i.getCotizacion()).setScale(4, RoundingMode.HALF_UP));
                } else {
                    i.setImporte(BigDecimal.ZERO);
                }
            }

            if (i.getImporte() == null) {
                i.setImporte(BigDecimal.ZERO);
            }
            if (i.getImporte().compareTo(BigDecimal.ZERO) > 0 || i.getConcepto().getTipo().equals("Z")) {
                m.getItemsDetalle().add(i);
            }
        }

        for (ItemMovimientoTesoreria i : m.getItemsHaber()) {

            i.setCotizacion(m.getCotizacion());
            if (i.getCuentaTesoreria() != null && !i.getCuentaTesoreria().getCodigoMoneda().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
                if (i.getImporteMonedaSecundaria() != null && i.getImporteMonedaSecundaria().compareTo(BigDecimal.ZERO) > 0) {
                    i.setImporte(i.getImporteMonedaSecundaria().multiply(i.getCotizacion()).setScale(4, RoundingMode.HALF_UP));
                } else {
                    i.setImporte(BigDecimal.ZERO);
                }
            }

            if (i.getImporte() == null) {
                i.setImporte(BigDecimal.ZERO);
            }
            if (i.getImporte().compareTo(BigDecimal.ZERO) > 0 || i.getConcepto().getTipo().equals("Z")) {
                m.getItemsDetalle().add(i);
            }
        }

        for (ItemMovimientoTesoreria i : m.getItemsControl()) {

            i.setCotizacion(m.getCotizacion());
            if (i.getCuentaTesoreria() != null && !i.getCuentaTesoreria().getCodigoMoneda().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
                if (i.getImporteMonedaSecundaria() != null && i.getImporteMonedaSecundaria().compareTo(BigDecimal.ZERO) > 0) {
                    i.setImporte(i.getImporteMonedaSecundaria().multiply(i.getCotizacion()).setScale(4, RoundingMode.HALF_UP));
                } else {
                    i.setImporte(BigDecimal.ZERO);
                }
            }

            if (i.getImporte() == null) {
                i.setImporte(BigDecimal.ZERO);
            }
            if (i.getConcepto().getTipo().equals("Z")) {
                m.getItemsDetalle().add(i);
            }
        }

        // Asignamos los importes
        for (ItemMovimientoTesoreria id : m.getItemsDetalle()) {

            if (id.getImporte().compareTo(BigDecimal.ZERO) > 0) {

                id.setImporteMonedaSecundaria(id.getImporte().divide(id.getCotizacion(), 4, RoundingMode.HALF_UP));

                if (id.getDebeHaber().equals("D")) {
                    id.setImporteDebe(id.getImporte());
                    id.setImporteDebeMonedaSecundaria(id.getImporteMonedaSecundaria());
                }

                if (id.getDebeHaber().equals("H")) {
                    id.setImporteHaber(id.getImporte());
                    id.setImporteHaberMonedaSecundaria(id.getImporteMonedaSecundaria());
                }
            } else {
                id.setImporteMonedaSecundaria(BigDecimal.ZERO);
                id.setImporteDebe(BigDecimal.ZERO);
                id.setImporteDebeMonedaSecundaria(BigDecimal.ZERO);
                id.setImporteHaber(BigDecimal.ZERO);
                id.setImporteHaberMonedaSecundaria(BigDecimal.ZERO);
            }
        }

    }

    /**
     * Cargamos el detalle del movimiento ya persistidos
     *
     * @param m
     */
    public void cargarItemsMovimiento(MovimientoTesoreria m) {

        if (m == null) {
            return;
        }

        m.getItemsDebe().clear();
        m.getItemsHaber().clear();
        m.getItemsControl().clear();

        for (ItemMovimientoTesoreria i : m.getItemsDetalle()) {

            if (i.getConcepto().getTipo().equals("Z")) {
                m.getItemsControl().add(i);
            } else {

                if (i.getDebeHaber().equals("D")) {
                    m.getItemsDebe().add(i);
                }

                if (i.getDebeHaber().equals("H")) {
                    m.getItemsHaber().add(i);
                }
            }
        }
    }

    private void generarItemsMovimiento(MovimientoTesoreria m) throws ExcepcionGeneralSistema {

        if (m.getComprobante().getConceptos() == null || m.getComprobante().getConceptos().isEmpty()) {
            throw new ExcepcionGeneralSistema("El comprobante de tesorería no tienen conceptos configurados");
        }

        for (ConceptoComprobante cc : m.getComprobante().getConceptos()) {

            ItemMovimientoTesoreria id = new ItemMovimientoTesoreria();
            id.setNroItem(m.getItemsDetalle().size() + 1);
            id.setDebeHaber(cc.getDebeHaber());
            id.setMovimiento(m);
            id.setConcepto(cc.getConcepto());
            id.setMonedaSecundaria(m.getMonedaSecundaria());
            id.setCotizacion(m.getCotizacion());
            id.setCuentaTesoreria(cc.getConcepto().getCuentaTesoreria());

            if (cc.getCuentaContable() != null) {

                id.setCuentaContable(cc.getCuentaContable());
            } else if (id.getConcepto().getCuentaContable() != null) {

                id.setCuentaContable(id.getConcepto().getCuentaContable());
            }

            cargarImputacionCentroCosto(id, cc.getItemsDistribucion());

            if (cc.getConcepto().getTipo().equals("Z")) {
                id.setNroItem(m.getItemsDebe().size() + 1);
                m.getItemsControl().add(id);

            } else {

                if (cc.getDebeHaber().equals("D")) {
                    id.setNroItem(m.getItemsDebe().size() + 1);
                    m.getItemsDebe().add(id);
                }

                if (cc.getDebeHaber().equals("H")) {
                    id.setNroItem(m.getItemsHaber().size() + 1);
                    m.getItemsHaber().add(id);
                }
            }
        }
    }

    /**
     * public void generarItemsMovimientoParaFacturacion(MovimientoFacturacion
     * m) {
     *
     * int nroItem = 0;
     *
     * for (ConceptoComprobante cc : m.getComprobanteTesoreria().getConceptos())
     * {
     *
     * ItemMovimientoTesoreria id = new ItemMovimientoTesoreria();
     * id.setNroItem(nroItem++); id.setDebeHaber(cc.getDebeHaber()); //
     * id.setMovimiento(m); id.setConcepto(cc.getConcepto());
     * id.setMonedaSecundaria(m.getMonedaSecundaria());
     * id.setCotizacion(m.getCotizacion());
     * id.setCuentaTesoreria(cc.getConcepto().getCuentaTesoreria());
     *
     * if (cc.getCuentaContable() != null) {
     *
     * id.setCuentaContable(cc.getCuentaContable());
     *
     * } else if (id.getConcepto().getCuentaContable() != null) {
     *
     * id.setCuentaContable(id.getConcepto().getCuentaContable()); }
     *
     * CuentaTesoreria ctaTeso = id.getCuentaTesoreria();
     *
     * //Los conceptos que no tengan cuenta asignada se cargarán. if (ctaTeso
     * != null) {
     *
     * int codCta = Integer.parseInt(ctaTeso.getTipoCuenta().getCodigo());
     *
     * switch (codCta) { case 1: //Otras cuentas - Efectivo if
     * (cc.getDebeHaber().equals("D")) { m.getItemsOtraCuentaDebe().add(id); }
     * if (cc.getDebeHaber().equals("H")) { m.getItemsOtraCuentaHaber().add(id);
     * } break; case 2: //Cuenta valores if (cc.getDebeHaber().equals("D")) {
     * m.getItemsValoresDebe().add(id); } if (cc.getDebeHaber().equals("H")) {
     * m.getItemsValoresHaber().add(id); } break; case 3: //Cuenta bancaria if
     * (cc.getDebeHaber().equals("D")) { m.getItemsBancoDebe().add(id); } if
     * (cc.getDebeHaber().equals("H")) { m.getItemsBancoHaber().add(id); }
     * break; case 4: //Tarjeta de crédito if (cc.getDebeHaber().equals("D")) {
     * m.getItemsTarjetaDebe().add(id); } if (cc.getDebeHaber().equals("H")) {
     * m.getItemsTarjetaHaber().add(id); } break; case 5: //Documentos a cobrar
     * if (cc.getDebeHaber().equals("D")) {
     * m.getItemsDocumentoCobrarDebe().add(id); } if
     * (cc.getDebeHaber().equals("H")) {
     * m.getItemsDocumentoCobrarHaber().add(id); } break; case 6: //Documentos a
     * pagar if (cc.getDebeHaber().equals("D")) {
     * m.getItemsDocumentoPagarDebe().add(id); } if
     * (cc.getDebeHaber().equals("H")) {
     * m.getItemsDocumentoPagarHaber().add(id); } break; } } else {
     *
     * if (cc.getDebeHaber().equals("D")) { m.getItemsOtraCuentaDebe().add(id);
     * } if (cc.getDebeHaber().equals("H")) {
     * m.getItemsOtraCuentaHaber().add(id); } } } }
     *
     */
    public void generarItemsMovimientoParaCompra(MovimientoCompra m) throws ExcepcionGeneralSistema {

        int nroItem = 0;

        if (m.getComprobanteTesoreria().getConceptos() == null || m.getComprobanteTesoreria().getConceptos().isEmpty()) {
            throw new ExcepcionGeneralSistema("El comprobante de tesorería no tienen conceptos configurados");
        }

        for (ConceptoComprobante cc : m.getComprobanteTesoreria().getConceptos()) {

            ItemMovimientoTesoreria id = new ItemMovimientoTesoreria();
            id.setNroItem(nroItem++);
            id.setDebeHaber(cc.getDebeHaber());
            id.setConcepto(cc.getConcepto());
            id.setMonedaSecundaria(m.getMonedaSecundaria());
            id.setCotizacion(m.getCotizacion());
            id.setCuentaTesoreria(cc.getConcepto().getCuentaTesoreria());

            if (cc.getCuentaContable() != null) {

                id.setCuentaContable(cc.getCuentaContable());

            } else if (id.getConcepto().getCuentaContable() != null) {

                id.setCuentaContable(id.getConcepto().getCuentaContable());
            }

            CuentaTesoreria ctaTeso = id.getCuentaTesoreria();

            if (ctaTeso != null) {

                int codCta = Integer.parseInt(ctaTeso.getTipoCuenta().getCodigo());

                switch (codCta) {
                    case 1: //Otras cuentas - Efectivo
                        if (cc.getDebeHaber().equals("D")) {
                            m.getItemsOtraCuentaDebe().add(id);
                        }
                        if (cc.getDebeHaber().equals("H")) {
                            m.getItemsOtraCuentaHaber().add(id);
                        }
                        break;
                    case 2: //Cuenta valores
                        if (cc.getDebeHaber().equals("D")) {
                            m.getItemsValoresDebe().add(id);
                        }
                        if (cc.getDebeHaber().equals("H")) {
                            m.getItemsValoresHaber().add(id);
                        }
                        break;
                    case 3: //Cuenta bancaria
                        if (cc.getDebeHaber().equals("D")) {
                            m.getItemsBancoDebe().add(id);
                        }
                        if (cc.getDebeHaber().equals("H")) {
                            m.getItemsBancoHaber().add(id);
                        }
                        break;
                    case 4: //Tarjeta de crédito
                        if (cc.getDebeHaber().equals("D")) {
                            m.getItemsTarjetaDebe().add(id);
                        }
                        if (cc.getDebeHaber().equals("H")) {
                            m.getItemsTarjetaHaber().add(id);
                        }
                        break;
                    case 5: //Documentos a cobrar
                        if (cc.getDebeHaber().equals("D")) {
                            m.getItemsDocumentoCobrarDebe().add(id);
                        }
                        if (cc.getDebeHaber().equals("H")) {
                            m.getItemsDocumentoCobrarHaber().add(id);
                        }
                        break;
                    case 6: //Documentos a pagar
                        if (cc.getDebeHaber().equals("D")) {
                            m.getItemsDocumentoPagarDebe().add(id);
                        }
                        if (cc.getDebeHaber().equals("H")) {
                            m.getItemsDocumentoPagarHaber().add(id);
                        }
                        break;
                }
            } else {

                if (cc.getDebeHaber().equals("D")) {
                    m.getItemsOtraCuentaDebe().add(id);
                }
                if (cc.getDebeHaber().equals("H")) {
                    m.getItemsOtraCuentaHaber().add(id);
                }
            }
        }
    }

    public void calcularImportes(MovimientoTesoreria m) throws Exception {

        procesarConceptos(m);

        BigDecimal importeTotalDebe = BigDecimal.ZERO;
        BigDecimal importeTotalHaber = BigDecimal.ZERO;
        BigDecimal importeTotalDebeSecundario = BigDecimal.ZERO;
        BigDecimal importeTotalHaberSecundario = BigDecimal.ZERO;

        for (ItemMovimientoTesoreria id : m.getItemsDetalle()) {

            if (id.getImporte() == null) {
                id.setImporte(BigDecimal.ZERO);
                id.setImporteMonedaSecundaria(BigDecimal.ZERO);
            }

            if (id.getDebeHaber().equals("D")) {
                importeTotalDebe = importeTotalDebe.add(id.getImporte());
                importeTotalDebeSecundario = importeTotalDebeSecundario.add(id.getImporteMonedaSecundaria());

            } else {
                importeTotalHaber = importeTotalHaber.add(id.getImporte());
                importeTotalHaberSecundario = importeTotalHaberSecundario.add(id.getImporteMonedaSecundaria());
            }

            //Calculamos la imputación en dimensiones
            if (id.getItemsCentroCosto() != null) {
                for (ItemMovimientoTesoreriaCentroCosto cc : id.getItemsCentroCosto()) {

                    if (cc.getSubCuentas() != null) {

                        for (ItemMovimientoTesoreriaSubcuenta is : cc.getSubCuentas()) {

                            is.setDebhab(id.getDebeHaber());

                            if (is.getPorcentaje() != null && is.getPorcentaje().compareTo(BigDecimal.ZERO) > 0) {
                                is.setImporte(id.getImporte().multiply(is.getPorcentaje()).setScale(4, RoundingMode.HALF_UP).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                            }
                        }
                    }
                }
            }
        }

        //Asignamos el importe para el concepto Z en ingresos y egresos
        for (ItemMovimientoTesoreria id : m.getItemsDetalle()) {

            if (m.getComprobante().getTipoComprobante().equals("I") && id.getConcepto().getTipo().equals("Z")) {

                id.setImporte(importeTotalDebe);
                id.setImporteHaber(importeTotalDebe);
                id.setImporteHaberMonedaSecundaria(importeTotalDebeSecundario);
                id.setImporteMonedaSecundaria(importeTotalDebeSecundario);

                importeTotalHaber = importeTotalDebe;
                importeTotalHaberSecundario = importeTotalDebeSecundario;

            }

            if (m.getComprobante().getTipoComprobante().equals("E") && id.getConcepto().getTipo().equals("Z")) {

                id.setImporte(importeTotalHaber);
                id.setImporteDebe(importeTotalHaber);
                id.setImporteDebeMonedaSecundaria(importeTotalHaberSecundario);
                id.setImporteMonedaSecundaria(importeTotalHaberSecundario);

                importeTotalDebe = importeTotalHaber;
                importeTotalDebeSecundario = importeTotalHaberSecundario;

            }
        }

        m.setImporteTotalDebe(importeTotalDebe);
        m.setImporteTotalHaber(importeTotalHaber);

        m.setImporteTotalDebeSecundario(importeTotalDebeSecundario);
        m.setImporteTotalHaberSecundario(importeTotalHaberSecundario);

    }

    public List<ItemMovimientoTesoreria> getItemMovientoTesoreriaByFiltro(Map<String, String> filtro, Date fechaMovimiento) {

        return tesoreriaDAO.getItemMovientoTesoreriaByFiltro(filtro, fechaMovimiento, 0);
    }

    public List<ItemMovimientoTesoreria> getItemMovientoTesoreriaByFiltro(Map<String, String> filtro, int cantRegistros) {

        return tesoreriaDAO.getItemMovientoTesoreriaByFiltro(filtro, cantRegistros);
    }

    public BigDecimal getSaldoAFecha(String nrocta, Date fHasta) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fHasta); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, -1);  // numero de días a añadir o restar

        return tesoreriaDAO.getSaldoAFecha(nrocta, calendar.getTime());
    }

    public ItemMovimientoTesoreria getSaldoAnterior(String nrocta, Date fHasta) {

        ItemMovimientoTesoreria itemSA = new ItemMovimientoTesoreria();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fHasta); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, -1);  // numero de días a añadir o restar

        BigDecimal saldoMonedaSistema = tesoreriaDAO.getSaldoAFecha(nrocta, calendar.getTime());
        BigDecimal saldoMonedaSecundaria = tesoreriaDAO.getSaldoMonedaSecundariaAFecha(nrocta, calendar.getTime());

        if (saldoMonedaSistema == null) {
            saldoMonedaSistema = BigDecimal.ZERO;
        }
        if (saldoMonedaSecundaria == null) {
            saldoMonedaSecundaria = BigDecimal.ZERO;
        }

        itemSA.setSaldo(saldoMonedaSistema);
        itemSA.setSaldoMonedaSecundaria(saldoMonedaSecundaria);

        return itemSA;
    }

    public List<MovimientoTesoreria> getMovimientosByFiltro(Map<String, String> filtro) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void asignarMovimientoItems(MovimientoTesoreria mt) {

        if (mt.getItemsHaber() != null) {
            for (ItemMovimientoTesoreria im : mt.getItemsHaber()) {
                im.setMovimiento(mt);
            }
        }

        if (mt.getItemsDebe() != null) {
            for (ItemMovimientoTesoreria im : mt.getItemsDebe()) {
                im.setMovimiento(mt);
            }
        }

        if (mt.getItemsControl() != null) {
            for (ItemMovimientoTesoreria im : mt.getItemsControl()) {
                im.setMovimiento(mt);
            }
        }
    }

    public MovimientoTesoreria getMovimiento(String codigo, Integer numeroFormulario) {

        return tesoreriaDAO.getMovimiento(codigo, numeroFormulario);
    }

    public List<MovimientoTesoreria> getListaByBusqueda(Map<String, String> filtro, int cantMax) {

        return tesoreriaDAO.getListaByBusqueda(filtro, null, cantMax);
    }

    public List<MovimientoTesoreria> getListaByBusqueda(Map<String, String> filtro, List<String> orderBy, int cantMax) {

        return tesoreriaDAO.getListaByBusqueda(filtro, orderBy, cantMax);
    }

    public MovimientoTesoreria revertirMovimiento(MovimientoTesoreria mRevertir) throws Exception {

        MovimientoTesoreria mr = generarMovimientoReversion(mRevertir);
        mr = guardar(mr);

        mRevertir.setMovimientoReversion(mr);
        if (mRevertir.getMovimientoProveedor() != null) {
            mRevertir.getMovimientoProveedor().setMovimientoReversion(mr.getMovimientoProveedor());
        }

        if (mRevertir.getMovimientoPrestamo() != null) {
            mRevertir.getMovimientoPrestamo().setMovimientoReversion(mr.getMovimientoPrestamo());
        }

        if (mRevertir.getMovimientoEducacion() != null) {
            mRevertir.getMovimientoEducacion().setMovimientoReversion(mr.getMovimientoEducacion());
        }

        mRevertir = (MovimientoTesoreria) tesoreriaDAO.editar(mRevertir);

        return mr;
    }

    public MovimientoTesoreria generarMovimientoReversion(MovimientoTesoreria m) throws ExcepcionGeneralSistema, Exception {

        if (m == null) {
            throw new ExcepcionGeneralSistema("Movimiento de tesorería nulo");
        }

        if (m.getMovimientoContabilidad() != null) {
            throw new ExcepcionGeneralSistema("El comprobante fue pasado a la contabilidad, no es posible revertirlo");
        }

        if (m.getMovimientoReversion() != null) {
            throw new ExcepcionGeneralSistema("El comprobante ya fue anulado con " + m.getMovimientoReversion().getFormulario().getDescripcion() + " - " + m.getMovimientoReversion().getNumeroFormulario());
        }

        if (m.getComprobante().getComprobanteReversion() == null) {
            throw new ExcepcionGeneralSistema("El comprobante " + m.getComprobante().getDescripcion() + " no tienen comprobante de reversión asociado");
        }

        if (m.getMovimientoProveedor() != null && m.getMovimientoProveedor().getComprobante().getComprobanteReversion() == null) {
            throw new ExcepcionGeneralSistema("El comprobante de proveedor asociado " + m.getComprobante().getDescripcion() + " no tiene comprobante de reversión asociado");
        }

        if (m.getMovimientoVenta() != null && m.getMovimientoVenta().getComprobante().getComprobanteReversion() == null) {
            throw new ExcepcionGeneralSistema("El comprobante de venta asociado " + m.getComprobante().getDescripcion() + " no tiene comprobante de reversión asociado");
        }

        if (m.getMovimientoPrestamo() != null && m.getMovimientoPrestamo().getComprobante().getComprobanteReversion() == null) {
            throw new ExcepcionGeneralSistema("El comprobante de préstamo asociado " + m.getComprobante().getDescripcion() + " no tiene comprobante de reversión asociado");
        }

        if (m.getMovimientoEducacion() != null && m.getMovimientoEducacion().getComprobante().getComprobanteReversion() == null) {
            throw new ExcepcionGeneralSistema("El comprobante de educación asociado " + m.getComprobante().getDescripcion() + " no tiene comprobante de reversión asociado");
        }

        Formulario formulario = formularioRN.getFormulario(m.getComprobante().getComprobanteReversion(), m.getPuntoVenta(), "X");

        if (formulario == null) {
            throw new ExcepcionGeneralSistema("No se encontró formulario de tesorería para el comprobante (" + m.getComprobante().getComprobanteReversion().getCodigo() + ") "
                    + "para El punto de venta (" + m.getPuntoVenta().getCodigo() + ") "
                    + "con la condición de iva (X)");
        }

        MovimientoTesoreria mr = new MovimientoTesoreria();

        mr.setEmpresa(m.getEmpresa());
        mr.setSucursal(m.getSucursal());
        mr.setPuntoVenta(m.getPuntoVenta());
        mr.setUnidadNegocio(m.getUnidadNegocio());

        mr.setComprobante(m.getComprobante().getComprobanteReversion());
        mr.setFormulario(formulario);

        mr.setFechaMovimiento(m.getFechaMovimiento());

        mr.setObservaciones("");
        mr.setMonedaSecundaria(m.getMonedaSecundaria());
        mr.setMonedaRegistracion(m.getMonedaRegistracion());
        mr.setCotizacion(m.getCotizacion());

        mr.setEntidad(m.getEntidad());
        mr.setNombreEntidad(m.getNombreEntidad());

        mr.setCobrador(m.getCobrador());

        mr.setMovimientoReversion(m);
        m.setMovimientoReversion(mr);

        if (m.getFormulario().getTipoNumeracion().equals("I")) {
            m.setNumeroFormulario(m.getNumeroFormulario() * -1);
        }

        mr.getItemsDetalle().clear();

        for (ItemMovimientoTesoreria i : m.getItemsDetalle()) {

            ItemMovimientoTesoreria ir = new ItemMovimientoTesoreria();

            ir.setMovimiento(mr);
            ir.setNroItem(mr.getItemsDetalle().size() + 1);
            ir.setMovimiento(mr);
            ir.setConcepto(i.getConcepto());
            ir.setCuentaContable(i.getCuentaContable());
            ir.setMonedaSecundaria(i.getMonedaSecundaria());
            ir.setCotizacion(i.getCotizacion());
            ir.setCuentaTesoreria(i.getConcepto().getCuentaTesoreria());

            ir.setEntidadComercial(i.getEntidadComercial());
            ir.setNombreEntidad(i.getNombreEntidad());
            ir.setNumeroDocumento(i.getNumeroDocumento());

            ir.setCheque(i.getCheque());
            ir.setFechaCheque(i.getFechaCheque());
            ir.setFechaVencimiento(i.getFechaVencimiento());

            ir.setBanco(i.getBanco());
            ir.setCategoriaCheque(i.getCategoriaCheque());
            ir.setFirmanteDocumento(i.getFirmanteDocumento());
            ir.setCuentaTesoreria(i.getCuentaTesoreria());

            ir.setObservaciones(i.getObservaciones());

            ir.setDebeHaber((i.getDebeHaber().equals("D") ? "H" : "D"));

            ir.setImporte(i.getImporte());
            ir.setImporteMonedaSecundaria(i.getImporteMonedaSecundaria());

            ir.setImporteDebe(i.getImporteHaber());
            ir.setImporteDebeMonedaSecundaria(i.getImporteHaberMonedaSecundaria());

            ir.setImporteHaber(i.getImporteDebe());
            ir.setImporteHaberMonedaSecundaria(i.getImporteDebeMonedaSecundaria());

            mr.getItemsDetalle().add(ir);
        }

        if (m.getMovimientoVenta() != null) {
            mr.setComprobanteVenta(m.getMovimientoVenta().getComprobante().getComprobanteReversion());
        }

        if (m.getMovimientoProveedor() != null) {
            mr.setComprobanteProveedor(m.getMovimientoProveedor().getComprobante().getComprobanteReversion());
            MovimientoProveedor mProveedor = proveedorRN.generarMovimientoReversion(m.getMovimientoProveedor());
            mr.setMovimientoProveedor(mProveedor);
        }

        if (m.getMovimientoPrestamo() != null) {
            mr.setComprobantePrestamo(m.getMovimientoPrestamo().getComprobante().getComprobanteReversion());
            MovimientoPrestamo mPrestamoR = prestamoRN.generarMovimientoReversion(m.getMovimientoPrestamo());
            mr.setMovimientoPrestamo(mPrestamoR);
        }

        if (m.getMovimientoEducacion() != null) {
            mr.setComprobanteEducacion(m.getMovimientoEducacion().getComprobante().getComprobanteReversion());
            MovimientoEducacion mEducacion = educacionRN.generarMovimientoReversion(m.getMovimientoEducacion());
            mr.setMovimientoEducacion(mEducacion);
        }

        return mr;
    }

    public void procesarEntidad(MovimientoTesoreria m) {

        if (m == null) {
            return;
        }

        if (m.getEntidad() == null) {
            return;
        }

        m.setNombreEntidad(m.getEntidad().getRazonSocial());
        m.setCobrador(m.getEntidad().getCobrador());

    }

    private void generarComprobantesAdicionales(MovimientoTesoreria m) throws ExcepcionGeneralSistema, Exception {
        /**
         * Si el movimiento de tesorería generar un comprobante de venta, este
         * último gestiona el número de comprobante
         */
        if (m.getComprobanteVenta() != null) {
            MovimientoVenta mv = ventaRN.generarComprobante(m);
            m.setMovimientoVenta(mv);
        }

        /**
         * Si el movimiento de tesorería generar un comprobante de proveedor,
         * este último gestiona el número de comprobante
         */
        //Si es comprobante de reversión no genero el movimiento ya que lo hago en el método revertirComprobante
        if (m.getComprobanteProveedor() != null && m.getComprobante().getEsComprobanteReversion().equals("N")) {

            MovimientoProveedor mp = proveedorRN.generarComprobante(m);
            m.setMovimientoProveedor(mp);

            if (mp.getRetenciones() != null) {
                for (MovimientoProveedor mret : m.getRetenciones()) {
                    if (mret.getFormulario().getTipoNumeracion().equals("A")) {
                        mret.setNumeroFormulario(formularioRN.tomarProximoNumero(mret.getFormulario()));
                    }
                }
            }
        }

        //Si es comprobante de reversión no genero el movimiento ya que lo hago en el método revertirComprobante
        if (m.getComprobantePrestamo() != null && m.getComprobante().getEsComprobanteReversion().equals("N")) {

            MovimientoPrestamo mp = prestamoRN.generarComprobante(m);
            m.setMovimientoPrestamo(mp);
        }

        //Si es comprobante de reversión no genero el movimiento ya que lo hago en el método revertirComprobante
        if (m.getComprobanteEducacion() != null && m.getComprobante().getEsComprobanteReversion().equals("N")) {

            MovimientoEducacion me = educacionRN.generarComprobante(m);
            m.setMovimientoEducacion(me);
        }

    }

    public MovimientoTesoreria generarMovimientosRendicion(MovimientoTesoreria mRendicion, List<EntidadComercial> clientes, String CODCJ, String CODVT, String PTOVTA) throws Exception {

        if (mRendicion == null) {
            return mRendicion;
        }

        if (clientes == null || clientes.isEmpty()) {
            return mRendicion;
        }

        mRendicion.setMovimientosRendicion(new ArrayList<>());
        int numeroRecibo = 1;

        for (EntidadComercial cliente : clientes) {

            if (cliente.getSaldos() == null || cliente.getSaldos().isEmpty()) {
                continue;
            }

            MovimientoTesoreria m = nuevoMovimiento(CODCJ, CODVT, "", "", "", PTOVTA);
            m.setEntidad(cliente);

            procesarEntidad(m);
            m.setEsAnticipo(false);

            m.setDebitos(cliente.getSaldos());

            BigDecimal totalDebe = cuentaCorrienteClienteRN.sumarSaldos(m.getDebitos(), m.getMonedaRegistracion().getCodigo());

            if (totalDebe.compareTo(BigDecimal.ZERO) > 0) {

                m.getItemsDebe().get(0).setImporte(totalDebe);
                m.setNumeroFormulario(mRendicion.getNumeroFormulario() * 1000 + numeroRecibo);

                calcularImportes(m);
                controlComprobante(m);
                limpiarConceptoEnCero(m);

                generarComprobantesAdicionales(m);
                m.setMovimientoRendicion(mRendicion);
                mRendicion.getMovimientosRendicion().add(m);

                numeroRecibo++;
            }
        }

        return mRendicion;

    }

    public void asignarPrestamo(MovimientoTesoreria m) throws Exception {

        m.setEntidad(m.getPrestamo().getDestinatario());
        procesarEntidad(m);

    }

    public ItemMovimientoTesoreria nuevoItemMovimiento(MovimientoTesoreria m, ItemMovimientoTesoreria itemCopiar) throws ExcepcionGeneralSistema {

        ItemMovimientoTesoreria item = new ItemMovimientoTesoreria();
        item.setMovimiento(m);
        item.setMonedaSecundaria(m.getMonedaSecundaria());
        item.setCotizacion(m.getCotizacion());

        if (itemCopiar != null) {

            item.setDebeHaber(itemCopiar.getDebeHaber());
            item.setConcepto(itemCopiar.getConcepto());
            item.setCuentaTesoreria(itemCopiar.getConcepto().getCuentaTesoreria());
            item.setCuentaContable(itemCopiar.getCuentaContable());
        }

        cargarImputacionCentroCosto(item, null);
        return item;

    }

    private void cargarImputacionCentroCosto(ItemMovimientoTesoreria itemTesoreria,
            List<ItemDistribucionConcepto> itemsDistribucion) throws ExcepcionGeneralSistema {

        if (itemTesoreria.getCuentaContable() == null) {
            return;
        }

        List<CuentaContableCentroCosto> centrosCosto = cuentaContableRN.getCentroCostoByCuenta(itemTesoreria.getCuentaContable());

        if (centrosCosto == null || centrosCosto.isEmpty()) {
            return;
        }

        itemTesoreria.setImputaCentroCosto(true);

        if (itemTesoreria.getItemsCentroCosto() == null) {
            itemTesoreria.setItemsCentroCosto(new ArrayList<ItemMovimientoTesoreriaCentroCosto>());
        }

        for (CuentaContableCentroCosto d : centrosCosto) {

            ItemMovimientoTesoreriaCentroCosto itemCentroCosto = new ItemMovimientoTesoreriaCentroCosto();
            itemCentroCosto.setNroItem(itemTesoreria.getItemsCentroCosto().size() + 1);
            itemCentroCosto.setCentroCosto(d.getCentroCosto());
            itemCentroCosto.setItemTesoreria(itemTesoreria);

            //En caso que exista, aplicamos la distribución automática por centro de costo
            //Según prioridad comprobante -> concepto -> cuenta contable.
            //Si el concepto asociado al comprobante tiene distribución la aplicamos.
            if (itemsDistribucion != null) {

                for (ItemDistribucionConcepto iDist : itemsDistribucion) {

                    if (iDist.getCentroCosto() != null
                            && iDist.getCentroCosto().equals(d.getCentroCosto())
                            && iDist.getImputacionAutomatica().equals("S")
                            && iDist.getDistribucion() != null) {

                        cargarDistribucionAutomatica(itemCentroCosto, iDist.getDistribucion(), itemTesoreria.getDebeHaber(), itemTesoreria.getImporte());
                    }
                }
            } else {

                if (d.getDistribucion() != null && d.getDistribucion().getItemsDistribucion() != null) {

                    cargarDistribucionAutomatica(itemCentroCosto, d.getDistribucion(), itemTesoreria.getDebeHaber(), itemTesoreria.getImporte());
                }
            }
            itemTesoreria.getItemsCentroCosto().add(itemCentroCosto);
        }
    }

    private void cargarDistribucionAutomatica(ItemMovimientoTesoreriaCentroCosto itemCentroCosto,
            Distribucion distribucion,
            String debeHaber,
            BigDecimal importe) throws ExcepcionGeneralSistema {

        itemCentroCosto.setDistribucion(distribucion);

        for (ItemDistribucion itemDistribucion : distribucion.getItemsDistribucion()) {

            ItemMovimientoTesoreriaSubcuenta itemSubCuenta = nuevoItemMovimientoSubCuenta(itemCentroCosto);

            if (itemSubCuenta != null) {

                itemSubCuenta.setDebhab(debeHaber);
                itemSubCuenta.setSubCuenta(itemDistribucion.getSubCuenta());
                itemSubCuenta.setPorcentaje(itemDistribucion.getPorcentaje());
                itemSubCuenta.setImporte(importe.multiply(itemSubCuenta.getPorcentaje()).setScale(4, RoundingMode.HALF_UP).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));

            }
        }

    }

    public ItemMovimientoTesoreriaSubcuenta nuevoItemMovimientoSubCuenta(ItemMovimientoTesoreriaCentroCosto itemCentroCosto) throws ExcepcionGeneralSistema {

        if (itemCentroCosto == null || itemCentroCosto.getCentroCosto() == null) {
            throw new ExcepcionGeneralSistema("El centro de costo no puede ser nulo");
        }

        if (itemCentroCosto.getSubCuentas() == null) {
            itemCentroCosto.setSubCuentas(new ArrayList<ItemMovimientoTesoreriaSubcuenta>());
        }

        ItemMovimientoTesoreriaSubcuenta itemSubCuenta = new ItemMovimientoTesoreriaSubcuenta();
        itemSubCuenta.setNroItem(itemCentroCosto.getSubCuentas().size() + 1);

        itemSubCuenta.setItemCentroCosto(itemCentroCosto);
        itemCentroCosto.getSubCuentas().add(itemSubCuenta);

        return itemSubCuenta;

    }

    public void eliminarItemSubCuenta(ItemMovimientoTesoreriaCentroCosto itemCentroCosto, ItemMovimientoTesoreriaSubcuenta itemSubCuenta) throws ExcepcionGeneralSistema {

        if (itemCentroCosto == null) {
            throw new ExcepcionGeneralSistema("Item vació, nada para anular");
        }

        if (itemSubCuenta == null) {
            throw new ExcepcionGeneralSistema("Item Sub Cuenta vació, nada para anular");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemBorrar = -1;

        for (ItemMovimientoTesoreriaSubcuenta item : itemCentroCosto.getSubCuentas()) {

            if (item.getNroItem() >= 0) {

                if (item.getNroItem() == itemSubCuenta.getNroItem()) {
                    indiceItemBorrar = i;
                }
            }
            i++;
        }

        //Borramos los items productos
        if (indiceItemBorrar >= 0) {
            ItemMovimientoTesoreriaSubcuenta remove = itemCentroCosto.getSubCuentas().remove(indiceItemBorrar);
            if (remove != null) {

                if (remove.getId() != null) {
                    tesoreriaDAO.eliminar(ItemMovimientoTesoreriaSubcuenta.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }
    }

    public void reorganizarNroItem(MovimientoTesoreria movimiento) {

        //Reorganizamos los números de items
        if (movimiento.getItemsDebe() != null) {

            int i = 1;
            for (ItemMovimientoTesoreria ip : movimiento.getItemsDebe()) {
                ip.setNroItem(i);
                i++;
                int d = 0;
            }
        }

        //Reorganizamos los números de items
        if (movimiento.getItemsHaber() != null) {

            int i = 1;
            for (ItemMovimientoTesoreria ip : movimiento.getItemsHaber()) {
                ip.setNroItem(i);
                i++;
                int d = 0;
            }
        }

    }

    public void sincronizarDatos(MovimientoPrestamo m) throws Exception {

        m.getMovimientoTesoreria().setFechaMovimiento(m.getFechaMovimiento());
        m.getMovimientoTesoreria().setNumeroFormulario(m.getNumeroFormulario());

        m.getMovimientoTesoreria().setPrestamo(m.getPrestamo());
        asignarPrestamo(m.getMovimientoTesoreria());

    }

    public void asignarItemsFromPendiente(MovimientoTesoreria m,
            ItemMovimientoTesoreria itemDebe,
            ItemMovimientoTesoreria itemHaber,
            List<ItemSaldoPendienteCuentaTesoreria> saldosPendiente) throws Exception {

        if (m == null || itemDebe == null || itemHaber == null || saldosPendiente == null) {
            throw new ExcepcionGeneralSistema("Movimiento, cuenta o saldos están vaciós, no es posible continuar");
        }

        boolean tengoSeleccionado = false;

        for (ItemSaldoPendienteCuentaTesoreria saldo : saldosPendiente) {
            if (saldo.isSeleccionado()) {
                tengoSeleccionado = true;
                break;
            }
        }

        if (!tengoSeleccionado) {
            throw new ExcepcionGeneralSistema("No se ha seleccionado ningún saldo pendiente");
        }

        for (ItemSaldoPendienteCuentaTesoreria saldo : saldosPendiente) {
            if (saldo.isSeleccionado()) {
                ItemMovimientoTesoreria item = nuevoItemMovimiento(m, itemDebe);
                m.getItemsDebe().add(item);
                procesarValor(item, saldo);

                item = nuevoItemMovimiento(m, itemHaber);
                m.getItemsHaber().add(item);
                procesarValor(item, saldo);
            }
        }
        reorganizarNroItem(m);
        limpiarConceptoEnCero(m);
        calcularImportes(m);
    }

    public void procesarValor(ItemMovimientoTesoreria i, ItemSaldoPendienteCuentaTesoreria v) {

        i.setCheque(v.getCheque());
        i.setFirmanteDocumento(v.getFirmante());
        i.setNombreEntidad(v.getNombre());
        i.setFechaCheque(v.getFechaCheque());
        i.setFechaVencimiento(v.getFechaVencimiento());
        i.setBanco(v.getBanco());
        i.setImporte(v.getImporte().abs());
        i.setNumeroDocumento(v.getNumeroDocumento());
        i.setCategoriaCheque(v.getCategoria());
    }

    private void tomarNumeroFormulario(MovimientoTesoreria m) throws ExcepcionGeneralSistema, Exception {

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

        if (m.getMovimientoVenta() != null) {
            m.getMovimientoVenta().setNumeroFormulario(m.getNumeroFormulario());
        }

        if (m.getMovimientoProveedor() != null) {
            m.getMovimientoProveedor().setNumeroFormulario(m.getNumeroFormulario());
        }

        if (m.getMovimientoPrestamo() != null) {
            m.getMovimientoPrestamo().setNumeroFormulario(m.getNumeroFormulario());
        }

        if (m.getMovimientoEducacion() != null) {
            m.getMovimientoEducacion().setNumeroFormulario(m.getNumeroFormulario());
        }

        if (m.getFechaMovimiento() != null) {
            m.getFormulario().setUltimaFecha(m.getFechaMovimiento());
        }

        formularioRN.guardar(m.getFormulario());
    }

    public void generarMovimientoVenta(MovimientoTesoreria mt) {

    }

}
