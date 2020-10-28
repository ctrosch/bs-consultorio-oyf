/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.rn;

import bs.administracion.modelo.Parametro;
import bs.administracion.rn.ParametrosRN;
import bs.entidad.modelo.EntidadComercial;
import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Moneda;
import bs.global.rn.MonedaRN;
import bs.global.util.Numeros;
import bs.global.util.UtilFechas;
import bs.prestamo.dao.PrestamoDAO;
import bs.prestamo.modelo.ItemPendienteCuentaCorrientePrestamo;
import bs.prestamo.modelo.ItemPrestamo;
import bs.prestamo.modelo.MovimientoPrestamo;
import bs.prestamo.modelo.ParametroPrestamo;
import bs.prestamo.modelo.Prestamo;
import bs.ventas.rn.CondicionPagoVentaRN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
public class PrestamoRN implements Serializable {

    @EJB
    private MonedaRN monedaRN;
    @EJB
    private PrestamoDAO prestamoDAO;
    @EJB
    private ParametrosRN parametrosRN;
    @EJB
    private CuentaCorrientePrestamoRN cuentaCorrienteRN;
    @EJB
    protected ParametroPrestamoRN parametroPrestamoRN;
    @EJB
    protected EstadoPrestamoRN estadoRN;
    @EJB
    protected CondicionPagoVentaRN condicionDePagoVentaRN;
    @EJB
    protected AmortizacionPrestamoRN amortizacionPrestamoRN;

    @EJB
    protected Clasificacion02RN clasificacion02RN;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized Prestamo guardar(Prestamo prestamo) throws Exception {

        controlComprobante(prestamo);

        if (prestamo.getId() == null) {

            prestamoDAO.crear(prestamo);

        } else {

            prestamo = prestamoDAO.editar(prestamo);
        }

        return prestamo;
    }

    public Prestamo nuevo() throws ExcepcionGeneralSistema, Exception {

        ParametroPrestamo parametros = parametroPrestamoRN.getParametro();
        Parametro parametrosGlobales = parametrosRN.getParametro();

        double cotizacion = monedaRN.getCotizacionDia("USD").doubleValue();
        Moneda monedaRegistracion = monedaRN.getMoneda(parametrosGlobales.getCodigoMonedaPrimaria());
        Moneda monedaSecundaria = monedaRN.getMoneda(parametrosGlobales.getCodigoMonedaSecundaria());

        Prestamo prestamo = new Prestamo();

        prestamo.setEstado(parametros.getEstado());
        prestamo.setMonedaRegistracion(monedaRegistracion);
        prestamo.setMonedaSecundaria(monedaSecundaria);
        prestamo.setCotizacion(cotizacion);

        return prestamo;
    }

    public ItemPrestamo nuevoItemPrestamo(Prestamo prestamo) throws ExcepcionGeneralSistema {

        puedoAgregarItem(prestamo);

        ItemPrestamo nItem = new ItemPrestamo();
//        nItem.setIdItemAplicacion(nItem.getId());
        nItem.setNroitm(prestamo.getItemsPrestamo().size() + 1);
        nItem.setCotizacion(prestamo.getCotizacion());

        nItem.setPrestamo(prestamo);
        reorganizarNroItem(prestamo);

        return nItem;
    }

    public void eliminarItemPrestamo(Prestamo prestamo, ItemPrestamo nItem) throws Exception {

        if (prestamo == null) {
            throw new ExcepcionGeneralSistema("Prestamo vacío, nada para eliminar");
        }

        if (nItem == null) {
            throw new ExcepcionGeneralSistema("Item nulo, nada para eliminar");
        }

        boolean fItemBorrado = false;
        int i = 0;
        int indiceItemProducto = -1;
        int indiceItemAplicacion = -1;

        for (ItemPrestamo ip : prestamo.getItemsPrestamo()) {

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
            ItemPrestamo remove = prestamo.getItemsPrestamo().remove(indiceItemProducto);
            if (remove != null) {

                if (prestamo.getId() != null && remove.getId() != null) {
                    prestamoDAO.eliminar(ItemPrestamo.class, remove.getId());
                }
                fItemBorrado = true;
            }
        }

        if (!fItemBorrado) {
            throw new ExcepcionGeneralSistema("No es posible borrar el item");
        }

        reorganizarNroItem(prestamo);
    }

    public void puedoAgregarItem(Prestamo movimiento) throws ExcepcionGeneralSistema {

        if (movimiento.getMonedaSecundaria() == null) {
            throw new ExcepcionGeneralSistema("La moneda secundaria no puede estar vacía");
        }

        if (movimiento.getCotizacion() < 1) {
            throw new ExcepcionGeneralSistema("La cotización del comprobante no puede se nula o menor a 1");
        }
    }

    public void reorganizarNroItem(Prestamo movimiento) {

        //Reorganizamos los números de items
        int i = 1;
        for (ItemPrestamo ip : movimiento.getItemsPrestamo()) {
            ip.setNroitm(i);
            i++;
        }
    }

    private void controlComprobante(Prestamo prestamo) throws ExcepcionGeneralSistema, Exception {

//        ParametroStock parametro = parametroStockRN.getParametro();
        String sErrores = "";

        if (prestamo == null) {
            throw new ExcepcionGeneralSistema("Préstamo nulo, nada para controlar");
        }

        if (prestamo.getDestinatario() == null) {
            sErrores += "- El destinatario no puede estar vacío \n";
        }
        if (prestamo.getSucursal() == null) {
            sErrores += "- La sucursal del préstamo no puede estar en blanco \n";
        }

        if (prestamo.getEstado() == null) {
            sErrores += "- El estado del préstamo no puede estar en blanco \n";
        }

        if (prestamo.getFinanciador() == null) {
            sErrores += "- El financiador del préstamo no puede estar en blanco \n";
        }

        if (prestamo.getLineaCredito() == null) {
            sErrores += "- La línea de crédito del préstamo no puede estar en blanco \n";
        }

        if (prestamo.getPromotor() == null) {
            sErrores += "- El promotor del préstamo no puede estar en blanco \n";
        }

        if (prestamo.getAmortizacion() == null) {
            sErrores += "- El tipo de amortización aplicada al préstamo no puede estar en blanco \n";
        }

        if (prestamo.getEstado().getCodigo().equals("A")) {

        }

        if (prestamo.getEstado().getCodigo().equals("B")) {

            Map<String, String> filtro = new HashMap<String, String>();
            filtro.put("estado.codigo IN", "('B','C','D','E')");
            filtro.put("destinatario.nrocta = ", "'" + prestamo.getDestinatario().getNrocta() + "'");
            filtro.put("codigo <> ", "'" + prestamo.getCodigo() + "'");

            Prestamo presActivo = prestamoDAO.getObjeto(Prestamo.class, filtro);

        }

        //CONTROLES GENERALES PARA LOS ITEMS
        for (ItemPrestamo itemPrestamo : prestamo.getItemsPrestamo()) {

        }

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }
    }

    public void sumarImportes(Prestamo m) {

        m.setImporteTotal(0);

    }

    public void ponerImportesCero(Prestamo m) {

    }

    public List<Prestamo> getListaByBusqueda(String txtBusqueda, Map<String, String> filtro, int cantMax) {

        return prestamoDAO.getListaByBusqueda(txtBusqueda, filtro, cantMax);
    }

    public List<Prestamo> getListaByDestinatario(String nrocta, String codest) {

        if (nrocta == null || nrocta.isEmpty()) {
            return null;
        }

        Map<String, String> filtro = prestamoDAO.getFiltro();

        filtro.put("destinatario.nrocta = ", "'" + nrocta + "'");
        filtro.put("estado.codigo = ", "'" + codest + "'");
        return getListaByBusqueda("", filtro, 15);
    }

    public void asignarDestinatario(Prestamo prestamo, EntidadComercial destinatario) throws Exception {

        prestamo.setDestinatario(destinatario);

        prestamo.setEmpresa(destinatario.getEmpresa());
        prestamo.setSucursal(destinatario.getSucursal());

        prestamo.setNombreDestinatario(prestamo.getDestinatario().getRazonSocial());
        prestamo.setCondicionDeIva(prestamo.getDestinatario().getCondicionDeIva());
        prestamo.setTipoDocumento(prestamo.getDestinatario().getTipoDocumento());
        prestamo.setNrodoc(prestamo.getDestinatario().getNrodoc());
        prestamo.setDireccion(prestamo.getDestinatario().getDireccion());
        prestamo.setBarrio(prestamo.getDestinatario().getBarrio());
        prestamo.setLocalidad(prestamo.getDestinatario().getLocalidad());
        prestamo.setProvincia(prestamo.getDestinatario().getProvincia());

        prestamo.setClasificacion02(clasificacion02RN.getClasificacion02(prestamo.getDestinatario().getSexo()));
    }

    public void cambiarEstado(Prestamo prestamo, String estado) throws Exception {

        if (prestamo.getEstado().getCodigo().equals("A")) {
            prestamo.setEstado(estadoRN.getEstadoPrestamo(estado));
        }

        if (prestamo.getEstado().getCodigo().equals("B") && estado != null && estado.equals("A")) {
            prestamo.setEstado(estadoRN.getEstadoPrestamo(estado));
        }

        guardar(prestamo);
    }

    public Prestamo getPrestamo(String codigo) {

        return prestamoDAO.getPrestamo(codigo);
    }

    public Prestamo getPrestamo(Integer id) {

        return prestamoDAO.getPrestamo(id);
    }

    public void generarCuota(Prestamo prestamo) throws ExcepcionGeneralSistema, Exception {

        if (prestamo.getEstado() == null) {
            throw new ExcepcionGeneralSistema("El préstamo debe tener un estado asignado");
        }

        if (!prestamo.getEstado().getCodigo().equals("A")) {
            throw new ExcepcionGeneralSistema("No es posible generar cuotas en el estado actual del préstamo");
        }

        if (prestamo.getLineaCredito() == null
                || prestamo.getAmortizacion() == null
                || prestamo.getPeriodo() == null
                || prestamo.getCantidadCuotas() == null) {
            return;
        }

        if (prestamo.getItemsPrestamo() == null) {
            prestamo.setItemsPrestamo(new ArrayList<ItemPrestamo>());
        }

        if (prestamo.getAmortizacion().getCodigo().equals("DIR")) {
            calcularPrestamoDirecto(prestamo);
        }

        if (prestamo.getAmortizacion().getCodigo().equals("FRA")) {
            calcularPrestamoFrances(prestamo);
        }

        if (prestamo.getAmortizacion().getCodigo().equals("MAN")) {
            calcularPrestamoManual(prestamo);
        }

        ajustarFechasVecimiento(prestamo);
    }

    private void calcularPrestamoDirecto(Prestamo prestamo) throws Exception {

        vaciarDetalleCuotas(prestamo);

        int cuotas = prestamo.getCantidadCuotas();
        int periodos = prestamo.getCantidadCuotas() - prestamo.getPeriodosGracia();
        double TNA = prestamo.getLineaCredito().getTasaNominalAnual();
        double tasaPeriodica = TNA / prestamo.getPeriodo().getCuotasAnuales() / 100;

        double capital = Numeros.redondear(prestamo.getImporteCapital() / cuotas, 4);
        double gastosOtorgamiento = Numeros.redondear(prestamo.getGastosOtorgamiento() / cuotas, 4);
        double microSeguro = Numeros.redondear(prestamo.getImporteMicroseguros() / cuotas, 4);
        double interesPeriodico = Numeros.redondear(prestamo.getImporteCapital() * tasaPeriodica, 6);
        double cuota = capital + interesPeriodico;
        double total = cuota * cuotas + prestamo.getGastosOtorgamiento();
        double interes = Numeros.redondear(interesPeriodico * cuotas, 6);

        prestamo.setImporteInteres(interes);
        prestamo.setImporteTotal(total);

        Calendar fechaAuxiliar = Calendar.getInstance();
        fechaAuxiliar.setTime(prestamo.getFechaEntrega());
        Date fechaVencimiento = prestamo.getFechaEntrega();

        for (int i = 1; i <= cuotas; i++) {

            fechaVencimiento = calcularProximoVencimiento(prestamo, fechaAuxiliar);

            //System.err.println("Cuota " + i + ": " + cuota + " | Capital: " + capital + " | Interés " + interesPeriodico);
            ItemPrestamo item = nuevoItemPrestamo(prestamo);
            item.setCuota(i);
            item.setFechaVencimiento(fechaVencimiento);
            item.setCapital(Numeros.redondear(capital));
            item.setInteres(Numeros.redondear(interesPeriodico));
            item.setTotalCuota(Numeros.redondear(cuota));
            item.setGastosOtorgamiento(Numeros.redondear(gastosOtorgamiento));
            item.setImporteMicroseguros(Numeros.redondear(microSeguro));
            prestamo.getItemsPrestamo().add(item);

        }
    }

    private void calcularPrestamoFrances(Prestamo prestamo) throws Exception {

        vaciarDetalleCuotas(prestamo);

//        prestamo.setGastosOtorgamiento(0);
//        prestamo.setImporteCapital(0);
//        prestamo.setCantidadCuotas(0);
//        prestamo.setPeriodosGracia(0);
        int cantidad_cuotas = prestamo.getCantidadCuotas();
        int periodos = prestamo.getCantidadCuotas() - prestamo.getPeriodosGracia();
        double TNA = prestamo.getLineaCredito().getTasaNominalAnual();
        double tasaPeriodica = (TNA / prestamo.getPeriodo().getCuotasAnuales()) / 100;

        double capital = prestamo.getImporteCapital();

        double b = (1 + tasaPeriodica);
        int p = periodos * -1;
        double A = (1 - Math.pow(b, p)) / tasaPeriodica;

        double importeCuota = Numeros.redondear(capital / A, 4);

        double gastosOtorgamiento = Numeros.redondear(prestamo.getGastosOtorgamiento() / cantidad_cuotas, 4);
        double microSeguro = Numeros.redondear(prestamo.getImporteMicroseguros() / cantidad_cuotas, 4);

        double interesTotal = 0;
        double capitalRestante = capital;

//        System.err.println("capital " + capital);
//        System.err.println("tasaPeriodica " + tasaPeriodica);
//        System.err.println("importe cuota " + importeCuota);
        Calendar fechaAuxiliar = Calendar.getInstance();
        fechaAuxiliar.setTime(prestamo.getFechaEntrega());
        Date fechaVencimiento = prestamo.getFechaEntrega();

        for (int k = 1; k <= cantidad_cuotas; k++) {

            fechaVencimiento = calcularProximoVencimiento(prestamo, fechaAuxiliar);

            ItemPrestamo item = nuevoItemPrestamo(prestamo);
            item.setFechaVencimiento(fechaVencimiento);
            item.setCuota(k);

            double interes = capitalRestante * tasaPeriodica;

            if (k <= prestamo.getPeriodosGracia()) {

                item.setCapital(0);
                item.setGastosOtorgamiento(gastosOtorgamiento);
                item.setImporteMicroseguros(microSeguro);
                item.setInteres(interes);
                item.setTotalCuota(interes + gastosOtorgamiento + microSeguro);
            } else {

                if (k < cantidad_cuotas) {
                    item.setInteres((interes));
                    item.setCapital(importeCuota - interes);
                    capitalRestante = capitalRestante - item.getCapital();
                } else {
                    item.setCapital(capitalRestante);
                    item.setInteres((importeCuota - capitalRestante));
                }

                item.setGastosOtorgamiento(gastosOtorgamiento);
                item.setImporteMicroseguros(microSeguro);
                item.setTotalCuota(importeCuota + item.getGastosOtorgamiento() + item.getImporteMicroseguros());
            }

            prestamo.getItemsPrestamo().add(item);

            interesTotal = interesTotal + item.getInteres();

//            System.err.println("Cuota " + k + ": " + " | Capital: " + capitalEnk + " | Interés " + interesTotal);
        }

        prestamo.setImporteInteres(Numeros.redondear(interesTotal));
        prestamo.setImporteTotal(Numeros.redondear(capital + interesTotal + prestamo.getGastosOtorgamiento() + prestamo.getImporteMicroseguros()));

    }

    private void calcularPrestamoManual(Prestamo prestamo) throws Exception {

        vaciarDetalleCuotas(prestamo);

        int periodos = prestamo.getCantidadCuotas() - prestamo.getPeriodosGracia();

        double capital = prestamo.getImporteCapital();
        double interes = prestamo.getImporteInteres();
        double gastosOtorgamiento = prestamo.getGastosOtorgamiento();
        double importeMicroseguro = prestamo.getImporteMicroseguros();

        double total = Numeros.redondear(capital + interes + gastosOtorgamiento + importeMicroseguro);
        prestamo.setImporteTotal(total);

        double capitalCuota = Numeros.redondear(capital / periodos);
        double interesCuota = Numeros.redondear(interes / periodos);
        double gastosCuota = Numeros.redondear(gastosOtorgamiento / periodos);
        double microSeguro = Numeros.redondear(prestamo.getImporteMicroseguros() / periodos);
        double totalCuota = Numeros.redondear(total / periodos);

        Calendar fechaAuxiliar = Calendar.getInstance();
        fechaAuxiliar.setTime(prestamo.getFechaEntrega());
        Date fechaVencimiento = prestamo.getFechaEntrega();

        for (int i = 1; i <= prestamo.getCantidadCuotas(); i++) {

            fechaVencimiento = calcularProximoVencimiento(prestamo, fechaAuxiliar);

            ItemPrestamo item = nuevoItemPrestamo(prestamo);
            item.setCuota(i);
            item.setFechaVencimiento(fechaVencimiento);

            item.setCapital(capitalCuota);
            item.setInteres(interesCuota);
            item.setGastosOtorgamiento(gastosCuota);
            item.setImporteMicroseguros(microSeguro);
            item.setTotalCuota(totalCuota);
            prestamo.getItemsPrestamo().add(item);

        }
    }

    public void calcularMoraYDescuento(Prestamo prestamo,
            List<ItemPendienteCuentaCorrientePrestamo> cuotasPendiente,
            Date fechaMovimiento,
            boolean calculaMora,
            boolean calculaDescuento) {

//        System.out.println("fechaMovimiento " + fechaMovimiento);
        for (ItemPendienteCuentaCorrientePrestamo cuota : cuotasPendiente) {

//            long startTime = cuota.getFechaVencimiento().getTime();
//            long endTime = fechaMovimiento.getTime();
//            long diffTime = endTime - startTime;
//            long diffDays = diffTime / (1000 * 60 * 60 * 24);
            int diasVencido = UtilFechas.daysDifference(cuota.getFechaVencimiento(), fechaMovimiento);

            double capitalPendiente = 0;

            double TNA = prestamo.getLineaCredito().getTasaNominalAnualMora();
            double tasaDiaria = Numeros.redondear(TNA / 360 / 100, 6);

//          Ya no se necesita más el capital pendietne para el cálculo de la mora 24/07/2019
            for (ItemPendienteCuentaCorrientePrestamo i : cuotasPendiente) {

                if (i.getCuotas() >= cuota.getCuotas()) {
                    capitalPendiente = capitalPendiente + i.getCapital();
                }
            }

//            if (diasVencido > 10) {
//               cuota.setCalculaInteresMora(true);
//            }
            if (calculaMora && (diasVencido > 0) && cuota.isCalculaInteresMora()) {

//                System.err.println("cuota " + cuota.getCuotas()+ " fechaMovimiento " + fechaMovimiento + " días vencidos " + diasVencido + " cuota pend" + (cuota.getCapital() + cuota.getInteres()));
//                System.err.println("tasaDiaria " + tasaDiaria);
                //double interesMora = capitalPendiente * tasaDiaria.doubleValue() * diasVencido;
                double interesMora = Numeros.redondear((cuota.getCapital() + cuota.getInteres()) * tasaDiaria * diasVencido);
                cuota.setInteresMora(interesMora);

            } else {
//            cuota.setInteresMora(0);
            }

//            startTime = fechaMovimiento.getTime();
//            endTime = cuota.getFechaVencimiento().getTime();
//            diffTime = endTime - startTime;
//            diffDays = diffTime / (1000 * 60 * 60 * 24);
            int diasAdelanto = UtilFechas.daysDifference(fechaMovimiento, cuota.getFechaVencimiento());

            /**
             * Por pedido de Leonardo de Goya, adelantó un préstamos con periodo
             * de gracia por eso se quitó cuota.getCapital() > 0
             */
            //if ((diasAdelanto > 30) && cuota.isCalculaDescuentoInteres() && cuota.getCapital() > 0) {
            if (calculaDescuento && (diasAdelanto > 28) && cuota.isCalculaDescuentoInteres()) {

//                System.err.println("tasaDiaria " + tasaDiaria);
//                System.err.println("diasAdelanto " + diasAdelanto);
//                double descuentoIntereses = capitalPendiente * tasaDiaria.doubleValue() * diasAdelanto;
//
//                if (cuota.getInteres() > descuentoIntereses) {
//                    cuota.setDescuentoInteres(descuentoIntereses);
//                } else {
//                    cuota.setDescuentoInteres(cuota.getInteres());
//                }
                cuota.setDescuentoInteres(cuota.getInteres());

            } else {
                cuota.setDescuentoInteres(0);
            }

            cuota.setPendiente(cuota.getCapital() + cuota.getInteres() + cuota.getGastosOtorgamiento() + cuota.getImporteMicroseguros() + cuota.getInteresMora() - cuota.getDescuentoInteres());

        }
    }

    public void calcularTotales(Prestamo prestamo) {

        double totalCapital = 0;
        double totalIntereses = 0;
        double totalInteresMora = 0;
        double totalDescuento = 0;
        double totalGastos = 0;
        double totalMicroseguro = 0;

        for (ItemPrestamo item : prestamo.getItemsPrestamo()) {

            totalCapital = totalCapital + item.getCapital() - item.getCapitalCancelado();
            totalIntereses = totalIntereses + item.getInteres() - item.getInteresCancelado();
            totalInteresMora = totalInteresMora + item.getInteresMora() - item.getInteresMora();
            totalDescuento = totalDescuento + item.getDescuentoInteres();
            totalGastos = totalGastos + item.getGastosOtorgamiento() - item.getGastosOtorgamientoCancelado();
            totalMicroseguro = totalMicroseguro + item.getImporteMicroseguros() - item.getImporteMicroseguroCancelado();

        }

        System.out.println("totalMicroseguro " + totalMicroseguro);

        prestamo.setImporteCapital(Numeros.redondear(totalCapital));
        prestamo.setImporteInteres(Numeros.redondear(totalIntereses));
        prestamo.setImporteMora(Numeros.redondear(totalInteresMora));
        prestamo.setImporteDescuento(Numeros.redondear(totalDescuento));
        prestamo.setGastosOtorgamiento(Numeros.redondear(totalGastos));
        prestamo.setImporteMicroseguros(Numeros.redondear(totalMicroseguro));
        prestamo.setImporteTotal(Numeros.redondear(totalCapital + totalIntereses + totalGastos + totalMicroseguro));
    }

    public void asignarCodigo(Prestamo prestamo) throws Exception {

        if (prestamo.getSucursal() == null
                || prestamo.getLineaCredito() == null
                || prestamo.getDestinatario() == null) {
            return;
        }

        String sErrores = "";

        if (!sErrores.isEmpty()) {
            throw new ExcepcionGeneralSistema(sErrores);
        }

        //String codigo = getProximoCodigo(prestamo);
        String codigo = prestamo.getSucursal().getCodigo()
                + prestamo.getLineaCredito().getCodigo()
                + prestamo.getDestinatario().getNrocta();
        prestamo.setCodigo(codigo);
    }

    public String getProximoCodigo(Prestamo prestamo) throws Exception {

        ParametroPrestamo p = parametroPrestamoRN.getParametro();

        int cantCaracteres = 5;
        int nroPrestamo = prestamoDAO.getProximoCodigo();
        String codigo = "00000000000" + String.valueOf(nroPrestamo);

//        String clasi1 = prestamo.getClasificacion01() != null ? "-" + prestamo.getClasificacion01().getCodigo() : "";
//        String clasi2 = prestamo.getClasificacion02() != null ? "-" + prestamo.getClasificacion02().getCodigo() : "";
//        String clasi3 = prestamo.getClasificacion03() != null ? "-" + prestamo.getClasificacion03().getCodigo() : "";
//        String clasi4 = prestamo.getClasificacion04() != null ? "-" + prestamo.getClasificacion04().getCodigo() : "";
//        String clasi5 = prestamo.getClasificacion05() != null ? "-" + prestamo.getClasificacion05().getCodigo() : "";
//
//        if (p != null) {
//
//            if (!p.getUtilizaClasificacion01GeneracionCodigo().equals("S")) {
//                clasi1 = "";
//            }
//
//            if (!p.getUtilizaClasificacion02GeneracionCodigo().equals("S")) {
//                clasi2 = "";
//            }
//
//            if (!p.getUtilizaClasificacion03GeneracionCodigo().equals("S")) {
//                clasi3 = "";
//            }
//
//            if (!p.getUtilizaClasificacion04GeneracionCodigo().equals("S")) {
//                clasi4 = "";
//            }
//
//            if (!p.getUtilizaClasificacion05GeneracionCodigo().equals("S")) {
//                clasi5 = "";
//            }
//        } else {
//            clasi1 = "";
//            clasi2 = "";
//            clasi3 = "";
//            clasi4 = "";
//            clasi5 = "";
//        }
//        System.err.println("codigo " + codigo.substring(codigo.length() - cantCaracteres, codigo.length()) + clasi1 + clasi2 + clasi3 + clasi4 + clasi5);
        return codigo.substring(codigo.length() - cantCaracteres, codigo.length()) + "-" + prestamo.getDestinatario().getNrocta(); //   + clasi1 + clasi2 + clasi3 + clasi4 + clasi5;

    }

    public Date calcularProximoVencimiento(Prestamo prestamo, Calendar fechaVencimiento) {

        if (prestamo.getPeriodo().getId() == 1) {
            fechaVencimiento.add(Calendar.YEAR, 1);
        }

        if (prestamo.getPeriodo().getId() == 2) {
            fechaVencimiento.add(Calendar.MONTH, 6);
        }

        if (prestamo.getPeriodo().getId() == 3) {
            fechaVencimiento.add(Calendar.MONTH, 4);
        }

        if (prestamo.getPeriodo().getId() == 4) {
            fechaVencimiento.add(Calendar.MONTH, 3);
        }

        if (prestamo.getPeriodo().getId() == 5) {
            fechaVencimiento.add(Calendar.MONTH, 2);
        }

        if (prestamo.getPeriodo().getId() == 6) {
            fechaVencimiento.add(Calendar.MONTH, 1);
        }

        if (prestamo.getPeriodo().getId() == 7) {
            fechaVencimiento.add(Calendar.DAY_OF_YEAR, 7);
        }

        if (prestamo.getPeriodo().getId() == 8) {
            fechaVencimiento.add(Calendar.DAY_OF_YEAR, 1);
        }

        return fechaVencimiento.getTime();
    }

    public void ajustarFechasVecimiento(Prestamo prestamo) {

        Calendar fechaVencimiento = Calendar.getInstance();

        if (prestamo == null || prestamo.getItemsPrestamo() == null) {
            return;
        }

        for (ItemPrestamo i : prestamo.getItemsPrestamo()) {

            if (i.getFechaVencimiento() != null) {

                fechaVencimiento.setTime(i.getFechaVencimiento());

                //Primero Ajustamos navidad y año nuevo
                if (fechaVencimiento.get(Calendar.DAY_OF_MONTH) == 25
                        && fechaVencimiento.get(Calendar.MONTH) == Calendar.DECEMBER) {

                    fechaVencimiento.add(Calendar.DAY_OF_MONTH, 1);
                }

                if (fechaVencimiento.get(Calendar.DAY_OF_MONTH) == 1
                        && fechaVencimiento.get(Calendar.MONTH) == Calendar.JANUARY) {

                    fechaVencimiento.add(Calendar.DAY_OF_MONTH, 1);
                }

                if (fechaVencimiento.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    //System.err.println("Es sábado");
                    fechaVencimiento.add(Calendar.DAY_OF_MONTH, 2);
                }

                if (fechaVencimiento.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    //System.err.println("Es domingo");
                    fechaVencimiento.add(Calendar.DAY_OF_MONTH, 1);
                }

                i.setFechaVencimiento(fechaVencimiento.getTime());

            }
        }
    }

    private void vaciarDetalleCuotas(Prestamo prestamo) throws Exception {

        if (prestamo.getId() == null || prestamo.isVacioCuotas()) {
            prestamo.getItemsPrestamo().clear();
        } else {
            prestamo.getItemsPrestamo().clear();
            prestamo = prestamoDAO.vaciarDetalleCuotas(prestamo);
            prestamo.setVacioCuotas(true);
        }
    }

    public List<ItemPrestamo> getCuotasPendientes(Prestamo prestamo) {

        return prestamoDAO.getCuotasPendientes(prestamo);

    }

    public void reprogramar(MovimientoPrestamo m) throws Exception {

//        System.err.println("reprogramar");
        Prestamo pr = m.getPrestamo().clone();

        pr.setId(null);
        pr.setFechaCredito(m.getFechaMovimiento());
        pr.setFechaEntrega(m.getFechaMovimiento());
        pr.setCodigo(pr.getCodigo() + "-R");
        pr.setEstado(estadoRN.getEstadoPrestamo("A"));
        pr.setImporteCapital(m.getImporteCapital());
        pr.setGastosOtorgamiento(m.getImporteGastos());
        pr.setReprogramado("S");

        //pr.setFechaVencimientoPrimeraCuota(m.getFechaMovimiento());
        generarCuota(pr);

        m.setPrestamoReprogramado(pr);
    }

    public void reprogramarJudicial(MovimientoPrestamo m) throws Exception {

//        System.err.println("reprogramar");
        Prestamo pr = m.getPrestamo().clone();

        pr.setId(null);
        pr.setFechaCredito(m.getFechaMovimiento());
        pr.setFechaEntrega(m.getFechaMovimiento());
        pr.setCodigo(pr.getCodigo() + "-J");
        pr.setEstado(estadoRN.getEstadoPrestamo("A"));
        pr.setImporteCapital(m.getImporteCapital());
        pr.setImporteInteres(m.getImporteInteres());
        pr.setGastosOtorgamiento(m.getImporteGastos());
        pr.setReprogramado("S");

        pr.setAmortizacion(amortizacionPrestamoRN.getAmortizacion("MAN"));

        //pr.setFechaVencimientoPrimeraCuota(m.getFechaMovimiento());
        generarCuota(pr);
        m.setPrestamoReprogramado(pr);
    }

    public void validarEstado(Prestamo prestamo) throws Exception {

        if (cuentaCorrienteRN.getSaldoActual(prestamo.getId(), "ARS") <= 0) {
            prestamo.setEstado(estadoRN.getEstadoPrestamo("Z"));
            guardar(prestamo);
        } else {

            if (prestamo.getEstado().getCodigo().equals("Z")) {
                prestamo.setEstado(estadoRN.getEstadoPrestamo("C"));
                guardar(prestamo);
            }
        }
    }

    public int getCantidadRegistrosByEstado(String estado) {

        Map<String, String> filtro = prestamoDAO.getFiltro();

        filtro.put("estado.codigo = ", "'" + estado + "'");

        return prestamoDAO.getCantidadRegistros(filtro, Prestamo.class);
    }
}
