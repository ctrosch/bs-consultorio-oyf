/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.estadistica.web;

import bs.compra.rn.CompraRN;
import bs.compra.vista.PendienteCompraDetalle;
import bs.contabilidad.rn.CuentaContableRN;
import bs.educacion.rn.CarreraRN;
import bs.educacion.rn.CursoRN;
import bs.entidad.rn.EntidadRN;
import bs.estadistica.rn.DashboardRN;
import bs.global.util.JsfUtil;
import bs.global.web.GenericBean;
import bs.prestamo.rn.PrestamoRN;
import bs.produccion.modelo.MovimientoProduccion;
import bs.produccion.rn.ProduccionRN;
import bs.salud.rn.SaludRN;
import bs.stock.rn.ProductoRN;
import bs.tarea.modelo.Tarea;
import bs.tarea.rn.TareaRN;
import bs.ventas.modelo.MovimientoVenta;
import bs.ventas.rn.MovimientoVentaRN;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ApplicationScoped
public class DashboardBean extends GenericBean implements Serializable {

    @EJB
    private DashboardRN dashboardRN;
    @EJB
    private EntidadRN entidadRN;
    @EJB
    private ProductoRN productoRN;
    @EJB
    private CuentaContableRN cuentaContableRN;
    @EJB
    private CompraRN compraRN;
    @EJB
    private MovimientoVentaRN ventaRN;
    @EJB
    private CarreraRN carreraRN;
    @EJB
    private CursoRN cursoRN;
    @EJB
    private ProduccionRN produccionRN;
    @EJB
    private TareaRN tareaRN;
    @EJB
    private SaludRN saludRN;

    @EJB
    private PrestamoRN prestamoRN;

    private BarChartModel estadisticasVenta;
    private BarChartModel estadisticasSalud;
    private PieChartModel vencidoVencer;
    private int cantClientes;
    private int cantProveedores;
    private int cantBeneficiarios;
    private int cantAlumnos;
    private int cantProfesores;
    private int cantCarreras;
    private int cantCursos;
    private int cantProductos;
    private int cantPrestamos;
    private int cantCuentasContables;
    private int cantProfesionales;
    private int cantPacientes;
    private int cantPacientesEnTurnos;
    private int cantPacientesEnEspera;
    private int cantPacientesEnAtencion;

    private List<PendienteCompraDetalle> pendienteCompra;
    private List<MovimientoVenta> ultimasVentas;

    private List<Tarea> tareasActivas;
    private List<MovimientoProduccion> ordenesPendientes;

    private Number vencido = 0;
    private Number vencer = 0;

    private String datosVentas;
    private String labelsVentas;
    private String datosVencidoVencer;

    private String datosTurnos;
    private String datosAtencion;
    private String labelsSalud;

    /**
     * Creates a new instance of DashboardBean
     */
    public DashboardBean() {

    }

    @PostConstruct
    private void init() {

        if (isActiveModulo("VT") || isActiveModulo("FC")) {
            cantClientes = entidadRN.getCantidadByTipo("1");
            cargarVentasMensuales();
            cargarUltimasVentas();
            cargarVencidoVencer();
        }

        if (isActiveModulo("ST")) {
            cantProductos = productoRN.getCantidadRegistros();
        }

        if (isActiveModulo("CG")) {
            cantCuentasContables = cuentaContableRN.getCantidadRegistros();
        }

        if (isActiveModulo("ED")) {
            cantAlumnos = entidadRN.getCantidadByTipo("4");
            cantProfesores = entidadRN.getCantidadByTipo("5");
            cantCarreras = carreraRN.getCantidadRegistros();
            cantCursos = cursoRN.getCantidadRegistros();
        }

        if (isActiveModulo("PV") || isActiveModulo("CO")) {
            cantProveedores = entidadRN.getCantidadByTipo("2");
            cargarProximosIngresos();
        }

        if (isActiveModulo("PR")) {
            cantBeneficiarios = entidadRN.getCantidadByTipo("3");
            cantPrestamos = prestamoRN.getCantidadRegistrosByEstado("C");
        }

        if (isActiveModulo("TA")) {
            actualizarTareas();
        }

        if (isActiveModulo("PD")) {
            actualizarOrdenesProduccion();
        }

        if (isActiveModulo("SA")) {

            cargarEstadisticaSaludMensuales();

            cantPacientes = entidadRN.getCantidadByTipo("6");
            cantProfesionales = entidadRN.getCantidadByTipo("7");

            cantPacientesEnTurnos = saludRN.getCantidadPacientesByEstado("RT", "E", null, null);
            cantPacientesEnEspera = saludRN.getCantidadPacientesByEstado("OC", "R", null, null);
            cantPacientesEnAtencion = saludRN.getCantidadPacientesByEstado("IC", "A", null, null);

        }
//        cargarRankingProductos();
    }

    @Schedule(hour = "3,13", minute = "0", second = "0")
    public void actualizarTodo() {
        init();
    }

    public void actualizarTareas() {

        tareasActivas = tareaRN.getListaByBusqueda(null, "B", mostrarDebaja, 0);
    }

    public void actualizarOrdenesProduccion() {

        filtro.clear();
        filtro.put("estado IN ", "('1','2')");
        filtro.put("comprobante.codigo = ", "'OP'");
        ordenesPendientes = produccionRN.getListaByBusqueda(filtro, true, 0);

    }

    private void cargarVentasMensuales() {

        String sql = "Select "
                + " YEAR(vt_movimiento.FCHMOV) as ANIO, "
                + " MONTH(vt_movimiento.FCHMOV) as MES, "
                + " Sum(Case vt_movimiento_item.DEBHAB When 'H' Then vt_movimiento_item.CANTID  When 'D' Then (vt_movimiento_item.CANTID * -1) Else 0 End) As CANTIDAD, "
                + " (Sum(IfNull(vt_movimiento_item.IMPHAB, 0) - IfNull(vt_movimiento_item.IMPDEB,0))) As IMPORTE_NACIONAL  "
                + " From  vt_movimiento "
                + " Inner Join vt_movimiento_item On vt_movimiento_item.ID_MCAB = vt_movimiento.ID "
                + " Inner Join gr_comprobante On vt_movimiento.MODCOM = gr_comprobante.MODCOM AND vt_movimiento.CODCOM = gr_comprobante.CODCOM "
                + " Where vt_movimiento_item.ARTCOD Is Not Null "
                + " AND vt_movimiento.FCHMOV >= DATE_ADD(now(),INTERVAL -150 DAY)  "
                + " AND gr_comprobante.INCEST = 'S' "
                + " Group By MONTH(vt_movimiento.FCHMOV), YEAR(vt_movimiento.FCHMOV) "
                + " Order By 1 Asc, 2 Asc ";

        List<Object[]> resultado = dashboardRN.executeSQL(sql);

        estadisticasVenta = new BarChartModel();

        ChartSeries ventas = new ChartSeries();
        ventas.setLabel("Meses");
        //ChartSeries cantidades = new ChartSeries();
        //cantidades.setLabel("Cantidad");

        labelsVentas = "";
        datosVentas = "";

        if (resultado != null && !resultado.isEmpty()) {

            for (Object[] r : resultado) {

                String nombre = ((Integer) r[1]).toString();
                Number valor = (Number) r[3];
                Number valor1 = (Number) r[2];

                if (labelsVentas.isEmpty()) {
                    labelsVentas += "\"" + JsfUtil.getMeses().get(nombre) + "\"";
                } else {
                    labelsVentas += ", \"" + JsfUtil.getMeses().get(nombre) + "\"";
                }

                if (datosVentas.isEmpty()) {
                    datosVentas += valor.toString();
                } else {
                    datosVentas += "," + valor.toString();
                }

                ventas.set(JsfUtil.getMeses().get(nombre), valor);
            }

        } else {

            String nombre = "N/D";
            Number valor = 1;
            Number valor1 = 1;

            ventas.set("N/D", valor);
        }

        estadisticasVenta.addSeries(ventas);
//        estadisticasVenta.addSeries(cantidades);

        estadisticasVenta.setTitle("Ventas mensuales");
        estadisticasVenta.setLegendPosition("ne");
        estadisticasVenta.setSeriesColors("0277BD,43A047");

        Axis xAxis = estadisticasVenta.getAxis(AxisType.X);
        xAxis.setLabel("Meses");

        Axis yAxis = estadisticasVenta.getAxis(AxisType.Y);
        yAxis.setLabel("Ventas");
        yAxis.setMin(0);
        //yAxis.setMax(150000);
    }

    private void cargarVencidoVencer() {

        String sql = "Select\n"
                + "   Sum(Case When vt_cuenta_corriente.FCHVNC <= NOW() Then vt_cuenta_corriente.IMPNAC Else 0 End) As VENCIDO,\n"
                + "   Sum(Case When vt_cuenta_corriente.FCHVNC > NOW() Then vt_cuenta_corriente.IMPNAC Else 0 End) As VENCER,\n"
                + "	Sum(vt_cuenta_corriente.IMPNAC) As SALDO\n"
                + "From\n"
                + "  vt_cuenta_corriente Inner Join\n"
                + "  en_entidad On vt_cuenta_corriente.NROCTA = en_entidad.NROCTA Inner Join\n"
                + "  vt_movimiento vt_mov_apl On vt_cuenta_corriente.ID_APL = vt_mov_apl.ID\n"
                + "HAVING Sum(vt_cuenta_corriente.IMPNAC) <>0";

        List<Number> intervals = new ArrayList<Number>();

        List<Object[]> resultado = dashboardRN.executeSQL(sql);

        BigDecimal saldo = BigDecimal.ZERO;

        if (resultado != null && !resultado.isEmpty()) {

            for (Object[] r : resultado) {
                vencido = (Number) r[0];
                vencer = (Number) r[1];
                saldo = ((BigDecimal) r[2]).setScale(2);

                if (vencido.equals(0)) {
                    vencido = 0.1;
                }

                if (vencer.equals(0)) {
                    vencer = 0.1;
                }

                intervals.add(vencido);
                intervals.add(vencer);
            }

        } else {

            vencido = 1;
            vencer = 1;
            saldo = BigDecimal.ZERO;
            intervals.add(vencido);
            intervals.add(vencer);
        }

        vencidoVencer = new PieChartModel();
        vencidoVencer.set("Vencido", vencido);
        vencidoVencer.set("Vencer", vencer);
        vencidoVencer.setLegendPosition("e");
        vencidoVencer.setShowDataLabels(true);
        vencidoVencer.setSeriesColors("E53935,43A047");
        vencidoVencer.setDiameter(150);
        vencidoVencer.setTitle("Saldo clientes $ " + saldo);

        //vencidoVencer.setGaugeLabel("Pendientes Cta. Cte.");
    }

    private void cargarProximosIngresos() {

        Map<String, String> filtro = new HashMap<String, String>();
        filtro.put("circom =", "'0200'");
        //filtro.put("nrocta =", "'"+m.getNrocta()+"'");
//        pendienteCompra = compraRN.getPendienteDetalle(filtro);
    }

    private void cargarUltimasVentas() {

        Map<String, String> filtro = new HashMap<String, String>();
        filtro.put("itemProducto ", " IS NOT EMPTY");
        ultimasVentas = ventaRN.getListaByBusqueda(filtro, 20);
    }

    public String getStringFechaJS(Date fecha) {

        String s = "";
        Calendar f = Calendar.getInstance();
        f.setTime(fecha);

        s = s + String.valueOf(f.get(Calendar.YEAR)) + ",";
        s = s + String.valueOf(f.get(Calendar.MONTH)) + ",";
        s = s + String.valueOf(f.get(Calendar.DAY_OF_MONTH)) + ",";

        if (f.get(Calendar.AM_PM) == Calendar.AM) {
            s = s + String.valueOf(f.get(Calendar.HOUR)) + ",";
        } else {
            s = s + String.valueOf(12 + f.get(Calendar.HOUR)) + ",";
        }

        s = s + String.valueOf(f.get(Calendar.MINUTE)) + ",";
        s = s + String.valueOf(f.get(Calendar.SECOND));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");

        return sdf.format(fecha);
    }

    public BarChartModel getEstadisticasVenta() {
        return estadisticasVenta;
    }

    public void setEstadisticasVenta(BarChartModel estadisticasVenta) {
        this.estadisticasVenta = estadisticasVenta;
    }

    private void cargarEstadisticaSaludMensuales() {

        String sql = "Select "
                + " YEAR(sa_movimiento.FCHMOV) as ANIO, "
                + " MONTH(sa_movimiento.FCHMOV) as MES, "
                + " COUNT(CASE gr_comprobante.TIPCOM WHEN 'RT' THEN sa_movimiento.ID ELSE NULL END) As CANT_TURNOS, "
                + " COUNT(CASE gr_comprobante.TIPCOM WHEN 'IA' THEN sa_movimiento.ID ELSE NULL END) As CANT_ATENCION "
                + " From  sa_movimiento "
                + " Inner Join gr_comprobante On sa_movimiento.MODCOM = gr_comprobante.MODCOM AND sa_movimiento.CODCOM = gr_comprobante.CODCOM "
                + " Where sa_movimiento.FCHMOV >= DATE_ADD(now(),INTERVAL -150 DAY)  "
                + " AND gr_comprobante.TIPCOM = 'IA' "
                + " Group By MONTH(sa_movimiento.FCHMOV), YEAR(sa_movimiento.FCHMOV) "
                + " Order By 1 Asc, 2 Asc ";

        List<Object[]> resultado = dashboardRN.executeSQL(sql);

        //ChartSeries cantidades = new ChartSeries();
        //cantidades.setLabel("Cantidad");
        labelsSalud = "";
        datosTurnos = "";
        datosAtencion = "";

        if (resultado != null && !resultado.isEmpty()) {

            resultado.forEach((r) -> {

                String nombre = ((Integer) r[1]).toString();
                Number valor = (Number) r[2];
                Number valor1 = (Number) r[3];

                if (labelsSalud.isEmpty()) {
                    labelsSalud += "\"" + JsfUtil.getMeses().get(nombre) + "\"";
                } else {
                    labelsSalud += ", \"" + JsfUtil.getMeses().get(nombre) + "\"";
                }

                if (datosTurnos.isEmpty()) {
                    datosTurnos += valor.toString();
                } else {
                    datosTurnos += "," + valor.toString();
                }

                if (datosAtencion.isEmpty()) {
                    datosAtencion += valor1.toString();
                } else {
                    datosAtencion += "," + valor1.toString();
                }
            });

        } else {

            String nombre = "N/D";
            Number valor = 1;
            Number valor1 = 1;
        }

    }

    public int getCantClientes() {
        return cantClientes;
    }

    public void setCantClientes(int cantClientes) {
        this.cantClientes = cantClientes;
    }

    public int getCantProveedores() {
        return cantProveedores;
    }

    public void setCantProveedores(int cantProveedores) {
        this.cantProveedores = cantProveedores;
    }

    public int getCantProductos() {
        return cantProductos;
    }

    public void setCantProductos(int cantProductos) {
        this.cantProductos = cantProductos;
    }

    public PieChartModel getVencidoVencer() {
        return vencidoVencer;
    }

    public void setVencidoVencer(PieChartModel vencidoVencer) {
        this.vencidoVencer = vencidoVencer;
    }

    public List<PendienteCompraDetalle> getPendienteCompra() {
        return pendienteCompra;
    }

    public void setPendienteCompra(List<PendienteCompraDetalle> pendienteCompra) {
        this.pendienteCompra = pendienteCompra;
    }

    public List<MovimientoVenta> getUltimasVentas() {
        return ultimasVentas;
    }

    public void setUltimasVentas(List<MovimientoVenta> ultimasVentas) {
        this.ultimasVentas = ultimasVentas;
    }

    public Number getVencido() {
        return vencido;
    }

    public void setVencido(Number vencido) {
        this.vencido = vencido;
    }

    public Number getVencer() {
        return vencer;
    }

    public void setVencer(Number vencer) {
        this.vencer = vencer;
    }

    public String getDatosVentas() {
        return datosVentas;
    }

    public void setDatosVentas(String datosVentas) {
        this.datosVentas = datosVentas;
    }

    public String getDatosVencidoVencer() {
        return datosVencidoVencer;
    }

    public void setDatosVencidoVencer(String datosVencidoVencer) {
        this.datosVencidoVencer = datosVencidoVencer;
    }

    public String getLabelsVentas() {
        return labelsVentas;
    }

    public void setLabelsVentas(String labelsVentas) {
        this.labelsVentas = labelsVentas;
    }

    public int getCantAlumnos() {
        return cantAlumnos;
    }

    public void setCantAlumnos(int cantAlumnos) {
        this.cantAlumnos = cantAlumnos;
    }

    public int getCantProfesores() {
        return cantProfesores;
    }

    public void setCantProfesores(int cantProfesores) {
        this.cantProfesores = cantProfesores;
    }

    public int getCantCarreras() {
        return cantCarreras;
    }

    public void setCantCarreras(int cantCarreras) {
        this.cantCarreras = cantCarreras;
    }

    public int getCantBeneficiarios() {
        return cantBeneficiarios;
    }

    public void setCantBeneficiarios(int cantBeneficiarios) {
        this.cantBeneficiarios = cantBeneficiarios;
    }

    public int getCantPrestamos() {
        return cantPrestamos;
    }

    public void setCantPrestamos(int cantPrestamos) {
        this.cantPrestamos = cantPrestamos;
    }

    public int getCantCuentasContables() {
        return cantCuentasContables;
    }

    public void setCantCuentasContables(int cantCuentasContables) {
        this.cantCuentasContables = cantCuentasContables;
    }

    public List<Tarea> getTareasActivas() {
        return tareasActivas;
    }

    public void setTareasActivas(List<Tarea> tareasActivas) {
        this.tareasActivas = tareasActivas;
    }

    public List<MovimientoProduccion> getOrdenesPendientes() {
        return ordenesPendientes;
    }

    public void setOrdenesPendientes(List<MovimientoProduccion> ordenesPendientes) {
        this.ordenesPendientes = ordenesPendientes;
    }

    public int getCantCursos() {
        return cantCursos;
    }

    public void setCantCursos(int cantCursos) {
        this.cantCursos = cantCursos;
    }

    public int getCantProfesionales() {
        return cantProfesionales;
    }

    public void setCantProfesionales(int cantProfesionales) {
        this.cantProfesionales = cantProfesionales;
    }

    public int getCantPacientes() {
        return cantPacientes;
    }

    public void setCantPacientes(int cantPacientes) {
        this.cantPacientes = cantPacientes;
    }

    public int getCantPacientesEnTurnos() {
        return cantPacientesEnTurnos;
    }

    public void setCantPacientesEnTurnos(int cantPacientesEnTurnos) {
        this.cantPacientesEnTurnos = cantPacientesEnTurnos;
    }

    public int getCantPacientesEnEspera() {
        return cantPacientesEnEspera;
    }

    public void setCantPacientesEnEspera(int cantPacientesEnEspera) {
        this.cantPacientesEnEspera = cantPacientesEnEspera;
    }

    public int getCantPacientesEnAtencion() {
        return cantPacientesEnAtencion;
    }

    public void setCantPacientesEnAtencion(int cantPacientesEnAtencion) {
        this.cantPacientesEnAtencion = cantPacientesEnAtencion;
    }

    public BarChartModel getEstadisticasSalud() {
        return estadisticasSalud;
    }

    public void setEstadisticasSalud(BarChartModel estadisticasSalud) {
        this.estadisticasSalud = estadisticasSalud;
    }

    public String getDatosTurnos() {
        return datosTurnos;
    }

    public void setDatosTurnos(String datosTurnos) {
        this.datosTurnos = datosTurnos;
    }

    public String getDatosAtencion() {
        return datosAtencion;
    }

    public void setDatosAtencion(String datosAtencion) {
        this.datosAtencion = datosAtencion;
    }

    public String getLabelsSalud() {
        return labelsSalud;
    }

    public void setLabelsSalud(String labelsSalud) {
        this.labelsSalud = labelsSalud;
    }

}
