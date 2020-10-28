/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.web;

import bs.educacion.modelo.ItemPendienteCuentaCorrienteEducacion;
import bs.educacion.modelo.ParametroEducacion;
import bs.educacion.rn.CuentaCorrienteEducacionRN;
import bs.educacion.rn.EducacionRN;
import bs.educacion.rn.ParametroEducacionRN;
import bs.entidad.modelo.EntidadComercial;
import bs.entidad.modelo.TipoEntidad;
import bs.entidad.rn.EntidadRN;
import bs.entidad.rn.TipoEntidadRN;
import bs.global.util.JsfUtil;
import bs.global.util.Numeros;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.ventas.rn.MovimientoVentaRN;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class ReciboEducacionBean extends MovimientoTesoreriaBean {

    @EJB
    private CuentaCorrienteEducacionRN cuentaCorrienteRN;
    @EJB
    private MovimientoVentaRN ventaRN;
    @EJB
    private ParametroEducacionRN parametrosEducacionRN;
    @EJB
    private EducacionRN educacionRN;
    @EJB
    private EntidadRN entidadRN;
    @EJB
    private TipoEntidadRN tipoEntidadRN;

    private List<ItemPendienteCuentaCorrienteEducacion> debitos;
    private double totalRecibo;
    protected double totalCancelar;

    private int cantidadCuotasVencidas;
    private ParametroEducacion parametrosEducacion;
    private TipoEntidad tipoEntidad;

    /**
     * Creates a new instance of CajaBean
     */
    public ReciboEducacionBean() {

    }

    @Override
    public void iniciarVariables() {

        if (!beanIniciado) {
            totalRecibo = 0;
            totalCancelar = 0;
            totalIntereses = 0;
            totalComision = 0;
            parametrosEducacion = parametrosEducacionRN.getParametro();
            tipoEntidad = tipoEntidadRN.getTipoEntidad("4");
            super.iniciarVariables();
        }
    }

    @Override
    public void guardar(boolean nuevo) {

        if (m.isEsAnticipo()) {
            m.setDebitos(null);
        } else {
            totalCancelar = cuentaCorrienteRN.sumarSaldos(debitos, m.getMonedaRegistracion().getCodigo());
            totalIntereses = cuentaCorrienteRN.sumarIntereses(debitos, m.getMonedaRegistracion().getCodigo());
            totalRecibo = totalCancelar + totalComision + totalIntereses;
        }

        m.setTotalComision(totalComision);
        m.setTotalIntereses(totalIntereses);

        if (!puedoGuardar()) {
            return;
        }

        super.guardar(nuevo);

        if (m.getId() != null) {
            totalRecibo = 0;
            totalCancelar = 0;
            totalComision = 0;
            totalIntereses = 0;
            debitos = new ArrayList<>();
        }

        educacionRN.validarEstado(debitos);

    }

    @Override
    public boolean puedoGuardar() {

        boolean todoOK = true;

        if (!super.puedoGuardar()) {
            todoOK = false;
        }

        if (m.getComprobanteEducacion() == null) {
            JsfUtil.addErrorMessage("El comprobante de educación no puede estar vacío");
            todoOK = false;
        }

        if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {

            if (!m.isEsAnticipo() && Numeros.redondear(totalRecibo, 4) > m.getImporteTotalDebe().setScale(4).doubleValue()) {
                JsfUtil.addErrorMessage("El importe a cancelar (" + Numeros.redondear(totalRecibo) + ") es diferente a la suma de los conceptos ingresados (" + m.getImporteTotalDebe().doubleValue() + ")");
                todoOK = false;
            }
        }

        if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
            if (!m.isEsAnticipo() && Numeros.redondear(totalRecibo, 4) > m.getImporteTotalDebeSecundario().setScale(4).doubleValue()) {
                JsfUtil.addErrorMessage("El importe a cancelar (" + Numeros.redondear(totalRecibo) + ") es diferente a la suma de los conceptos ingresados (" + m.getImporteTotalDebeSecundario().doubleValue() + ")");
                todoOK = false;
            }
        }

        //Buscamos si existen concepto de mora y que se lo aplique
//        if (debitos.stream()
//                .filter(d -> !d.isSeleccionado() && d.getItemMovimientoEducacion().getConcepto().getTipo().equals("M"))
//                .collect(Collectors.toList()).size() > 0) {
//
//            JsfUtil.addErrorMessage("El alumnos tiene recargos por mora pendiente de cobro, debe abonarlos a todos para habilitarse el cobro de las demás cuotas");
//            todoOK = false;
//        }
        //Buscamos si existe concepto de reinscripcion que se lo aplique
        if (debitos.stream()
                .filter(d -> !d.isSeleccionado() && d.getItemMovimientoEducacion().getConcepto().getTipo().equals("R"))
                .collect(Collectors.toList()).size() > 0) {

            JsfUtil.addErrorMessage("El alumnos tiene reinscripción pendiente de cobro, debe abonarla para habilitarse el cobro de las demás cuotas");
            todoOK = false;
        }

        return todoOK;
    }

    public void modificarCotizacion() {

    }

    public List<EntidadComercial> completeAlumno(String query) {
        try {
            filtro.clear();
            //filtro.put("sucursal.codigo = ", "'" + m.getSucursal().getCodigo() + "'");

            return entidadRN.getListaByBusqueda(filtro, query, tipoEntidad, mostrarDebaja, 10);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public void procesarAlumno() {

        super.procesarEntidad();
        m.setEsAnticipo(false);
        debitos = cuentaCorrienteRN.getDebitosPendientes(m.getEntidad().getNrocta(),
                m.getFechaMovimiento(),
                m.getMonedaRegistracion().getCodigo(),
                m.getComprobante().getComprobanteInterno(),
                null);

        cuentaCorrienteRN.calcularIntereses(m.getFechaMovimiento(), debitos);

        cantidadCuotasVencidas = cuentaCorrienteRN.calcularCantidadCuotasVencidas(m.getFechaMovimiento(), debitos);

//        try {
//            educacionRN.generarComprobanteIntereses(m, debitos);
//        } catch (Exception ex) {
//            Logger.getLogger(ReciboEducacionBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        if (m.getComprobanteInteresesGenerados() > 0) {
//            debitos = cuentaCorrienteRN.getDebitosPendientes(m.getEntidad().getNrocta(), m.getFechaMovimiento(), m.getMonedaRegistracion().getCodigo(), m.getComprobante().getComprobanteInterno());
//            JsfUtil.addInfoMessage("Se han generado los recargos de las cuotas vencidas");
//        }
        m.setDebitosEducacion(debitos);
    }

    public void procesarDebito() {

    }

    public void calcularComisiones(ItemMovimientoTesoreria item) {

        //Ponemos todas las comisiones en cero
        totalComision = 0;

        m.getItemsDebe().forEach(i -> {
            i.setImporteComision(0);
        });

        //Calculamos comisión de acuerdo a la cuenta que se cobra
        if (item.getConcepto().getPorcentajeComision() > 0) {

            totalComision = (totalCancelar + totalIntereses) * item.getConcepto().getPorcentajeComision() / 100;
            item.setImporteComision(totalComision);
        }
    }

    public void modificaDebito(ItemPendienteCuentaCorrienteEducacion i) {

        cuentaCorrienteRN.calcularIntereses(m.getFechaMovimiento(), debitos);

        if (i.isSeleccionado()) {

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {

                if (i.getImporteAplicar() > (i.getPendiente() + i.getImporteInteres())) {
                    i.setImporteAplicar(i.getPendiente() + i.getImporteInteres());
                    JsfUtil.addErrorMessage("El importe a aplicar es mayor al saldo pendiente");
                }

                if (i.getImporteAplicar() < i.getImporteInteres()) {
                    i.setImporteAplicar(i.getImporteInteres());
                    JsfUtil.addErrorMessage("El importe a aplicar no puede ser menor al importe de los intereses");
                }
            }

        } else {
            i.setImporteAplicar(0);
            i.setImporteAplicarSecundario(0);
//            JsfUtil.addErrorMessage("El saldo pendiente que está modificando no está seleccionado");
        }

        totalCancelar = cuentaCorrienteRN.sumarSaldos(debitos, m.getMonedaRegistracion().getCodigo());
        totalIntereses = cuentaCorrienteRN.sumarIntereses(debitos, m.getMonedaRegistracion().getCodigo());
        totalRecibo = totalCancelar + totalIntereses + totalComision;
    }

    public void marcarDebito(ItemPendienteCuentaCorrienteEducacion i) {

        if (i.isSeleccionado()) {

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaPrimaria())) {
                i.setImporteAplicar(i.getPendiente() + i.getImporteInteres());

//                BigDecimal disponible = m.getImporteTotalDebe().add(totalDebe.negate());
//
//                if (i.getImporteAplicar().compareTo(disponible) > 0) {
//                    i.setImporteAplicar(disponible);
////                    JsfUtil.addWarningMessage("El importe a aplicar es mayor al pendiente");
//                }
                i.setImporteAplicarSecundario(i.getImporteAplicar() / m.getCotizacion().doubleValue());
            }

            if (m.getMonedaRegistracion().getCodigo().equals(parametrosRN.getParametro().getCodigoMonedaSecundaria())) {
                i.setImporteAplicarSecundario(i.getImporteAplicar() / m.getCotizacion().doubleValue());

//                BigDecimal disponible = m.getImporteTotalDebeSecundario().add(totalDebe.negate());
//
//                if (i.getImporteAplicarSecundario().compareTo(disponible) > 0) {
//                    i.setImporteAplicarSecundario(disponible);
//                }
//                i.setImporteAplicar(i.getImporteAplicarSecundario().multiply(m.getCotizacion()).setScale(2, RoundingMode.HALF_UP));
            }

        } else {
            i.setImporteAplicar(0);
            i.setImporteAplicarSecundario(0);
        }

        totalCancelar = cuentaCorrienteRN.sumarSaldos(debitos, m.getMonedaRegistracion().getCodigo());
        totalIntereses = cuentaCorrienteRN.sumarIntereses(debitos, m.getMonedaRegistracion().getCodigo());
        totalRecibo = totalCancelar + totalIntereses + totalComision;
    }

    @Override
    public void asignarTotal(ItemMovimientoTesoreria item) {

        calcularComisiones(item);

        if (item.getDebeHaber().equals("D")) {
            m.getItemsDebe().forEach(i -> i.setImporte(BigDecimal.ZERO));
        } else {
            m.getItemsHaber().forEach(i -> i.setImporte(BigDecimal.ZERO));
        }

        totalRecibo = totalCancelar + totalIntereses + totalComision;
        item.setImporte(Numeros.toBigDecimal(totalCancelar + totalIntereses + totalComision));
        calcularImportes();
    }

    public void generarMovimientoVenta(MovimientoTesoreria mt) {
        try {

            ventaRN.generarComprobante(mt.getMovimientoEducacion());

            JsfUtil.addInfoMessage("El comprobante " + mt.getMovimientoEducacion().getMovimientoVenta().getFormulario().getDescripcion() + "-" + mt.getMovimientoEducacion().getMovimientoVenta().getNumeroFormulario() + " se guardó correctamente", "");
            buscar();

        } catch (Exception e) {
            JsfUtil.addErrorMessage("Problemas para generar movimiento de venta " + e);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    //--------------------------------------------------------------------------
    public List<ItemPendienteCuentaCorrienteEducacion> getDebitos() {
        return debitos;
    }

    public void setDebitos(List<ItemPendienteCuentaCorrienteEducacion> debitos) {
        this.debitos = debitos;
    }

    public double getTotalRecibo() {
        return totalRecibo;
    }

    public void setTotalRecibo(double totalRecibo) {
        this.totalRecibo = totalRecibo;
    }

    public double getTotalCancelar() {
        return totalCancelar;
    }

    public void setTotalCancelar(double totalCancelar) {
        this.totalCancelar = totalCancelar;
    }

    public int getCantidadCuotasVencidas() {
        return cantidadCuotasVencidas;
    }

    public void setCantidadCuotasVencidas(int cantidadCuotasVencidas) {
        this.cantidadCuotasVencidas = cantidadCuotasVencidas;
    }

}
