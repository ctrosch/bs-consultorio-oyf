/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.web.informe;

import bs.global.excepciones.ExcepcionGeneralSistema;
import bs.global.modelo.Formulario;
import bs.global.util.InformeBase;
import bs.global.util.JsfUtil;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.ItemMovimientoStock;
import bs.stock.modelo.ItemProductoStock;
import bs.stock.modelo.MovimientoStock;
import bs.stock.modelo.Producto;
import bs.stock.modelo.TipoProducto;
import bs.stock.rn.ProductoRN;
import bs.stock.rn.StockRN;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Claudio
 */
@ManagedBean(name = "cronologiaBean")
@ViewScoped
public class CronologiaBean extends InformeBase implements Serializable {

    @EJB
    private StockRN stockRN;
    @EJB
    private ProductoRN productoRN;

    private double saldoInicial;
    private Producto producto;
    private String ARTCOD;
    private TipoProducto tipoProducto;
    private Deposito deposito;
    private Date fechaDesde;
    private Date fechaHasta;
    private Date fechaMaxima;
    private List<ItemMovimientoStock> movimientos;

    /**
     * Creates a new instance of CronologiaBean
     */
    public CronologiaBean() {

        fechaDesde = new Date();
        fechaHasta = new Date();
        fechaMaxima = new Date();
    }

    public void iniciarVariables() {

        try {
            if (!beanIniciado) {

                super.iniciar();

                if (ARTCOD != null && !ARTCOD.isEmpty()) {
                    producto = productoRN.getProducto(ARTCOD);
                    verCronologia();
                }

                beanIniciado = true;
            }
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Error al iniciar el bean " + ex);
        }
    }

    public void verCronologia() {

        if (fechaHasta.before(fechaDesde)) {
            JsfUtil.addErrorMessage("Fecha desde tiene que ser mayor o igual a fecha Hasta");
            return;
        }

        if (deposito == null) {
            JsfUtil.addErrorMessage("Seleccione un depósito");
            return;
        }

        if (producto == null) {
            JsfUtil.addErrorMessage("Seleccione un producto");
            return;
        }

        try {
            movimientos = stockRN.getMovimientosEntreFechas(producto, deposito, fechaDesde, fechaHasta);
            saldoInicial = stockRN.getStockAFecha(producto, deposito, fechaDesde);

            ItemMovimientoStock imi = new ItemProductoStock();

            Formulario f = new Formulario("ST", "SA");

            imi.setMovimiento(new MovimientoStock());
            imi.getMovimiento().setFormulario(f);
            imi.getMovimiento().setNumeroFormulario(0);
            imi.getMovimiento().setFechaMovimiento(fechaDesde);
            imi.setCantidad(BigDecimal.ZERO);
            imi.setStocks(BigDecimal.ZERO);
            imi.setSaldoInicial(saldoInicial);

            for (ItemMovimientoStock im : movimientos) {
                saldoInicial = saldoInicial + im.getStocks().doubleValue();
                im.setSaldoInicial(saldoInicial);
            }

            movimientos.add(0, imi);

            if (movimientos.size() == 1) {
                JsfUtil.addInfoMessage("No se encontraron movimientos en el período seleccionado");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("No es posible obtener los datos solicitados");
        }
    }

    public List<Producto> completeProducto(String query) {
        try {

            String codtip = "";

            if (tipoProducto != null) {
                codtip = tipoProducto.getCodigo();
            }

            return productoRN.getListaByBusqueda(codtip, query, false, 10);

        } catch (Exception ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Producto>();
        }
    }

    public void limpiarMovimientos() {

        if (movimientos != null) {
            movimientos.clear();
        }
    }

    public void limpiarProducto() {

        if (producto != null) {
            producto = null;
        }
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public double getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(double saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public List<ItemMovimientoStock> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<ItemMovimientoStock> movimientos) {
        this.movimientos = movimientos;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Date getFechaMaxima() {
        return fechaMaxima;
    }

    public void setFechaMaxima(Date fechaMaxima) {
        this.fechaMaxima = fechaMaxima;
    }

    @Override
    public void validarDatos() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cargarParametros() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetParametros() throws ExcepcionGeneralSistema {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getARTCOD() {
        return ARTCOD;
    }

    public void setARTCOD(String ARTCOD) {
        this.ARTCOD = ARTCOD;
    }

}
