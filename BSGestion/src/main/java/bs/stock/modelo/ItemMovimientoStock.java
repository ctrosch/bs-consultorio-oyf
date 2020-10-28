/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Moneda;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "st_movimiento_item")
@EntityListeners(AuditoriaListener.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPITM", discriminatorType = DiscriminatorType.STRING, length = 10)
public abstract class ItemMovimientoStock implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @Column(name = "SUCURS", nullable = false, length = 6)
    private String puntoVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mcab", referencedColumnName = "ID", nullable = false)
    private MovimientoStock movimiento;

    @Column(name = "natri1", nullable = false, length = 30)
    private String atributo1;
    @Column(name = "natri2", nullable = false, length = 30)
    private String atributo2;
    @Column(name = "natri3", nullable = false, length = 30)
    private String atributo3;
    @Column(name = "natri4", nullable = false, length = 30)
    private String atributo4;
    @Column(name = "natri5", length = 30)
    private String atributo5;
    @Column(name = "natri6", length = 30)
    private String atributo6;
    @Column(name = "natri7", length = 30)
    private String atributo7;

    @Column(name = "FCHMOV")
    @Temporal(TemporalType.DATE)
    private Date fechaMovimiento;

    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto producto;

    @JoinColumn(name = "DEPOSI", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito deposito;

    @Column(name = "CANTID", precision = 15, scale = 4)
    private BigDecimal cantidad;
    @Column(name = "CNTSEC", precision = 15, scale = 4)
    private BigDecimal cantidadSecundaria;

    @JoinColumn(name = "UNIMED", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadMedida;

    @Column(name = "STOCKS", precision = 15, scale = 4)
    private BigDecimal stocks;

    @Column(name = "PRECIO", precision = 15, scale = 4)
    private BigDecimal precio;
    @Column(name = "PRESEC", precision = 15, scale = 4)
    private BigDecimal precioSecundario;

    @JoinColumn(name = "COFLIS", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaSecundaria;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private BigDecimal cotizacion;

    /**
     * Observaciones
     */
    @Lob
    @Column(name = "OBSERV", length = 65535)
    private String observaciones;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean todoOk;

    @Transient
    private double saldoInicial;

    @Transient
    private double stockComprometer;

    @Transient
    private double stockDescomprometer;

    @Transient
    private double stockDescontar;

    @Transient
    private boolean conError;

    public ItemMovimientoStock() {

        this.atributo1 = "";
        this.atributo2 = "";
        this.atributo3 = "";
        this.atributo4 = "";
        this.atributo5 = "";
        this.atributo6 = "";
        this.atributo7 = "";

        auditoria = new Auditoria();
        cotizacion = BigDecimal.ZERO;

        cantidad = BigDecimal.ZERO;
        cantidadSecundaria = BigDecimal.ZERO;
        precio = BigDecimal.ZERO;
        precioSecundario = BigDecimal.ZERO;

        todoOk = false;
    }

    public ItemMovimientoStock(String ptoVta, int nroitm, String nserie, String ndespa, String notros) {

        this.puntoVenta = ptoVta;
        this.nroitm = nroitm;
        this.atributo1 = "";
        this.atributo2 = "";
        this.atributo3 = "";
        this.atributo4 = "";
        this.atributo5 = "";
        this.atributo6 = "";
        this.atributo7 = "";

        auditoria = new Auditoria();
        cotizacion = BigDecimal.ZERO;

        cantidad = BigDecimal.ZERO;
        cantidadSecundaria = BigDecimal.ZERO;
        precio = BigDecimal.ZERO;
        precioSecundario = BigDecimal.ZERO;

        todoOk = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNroitm() {
        return nroitm;
    }

    public void setNroitm(int nroitm) {
        this.nroitm = nroitm;
    }

    public String getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(String puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public MovimientoStock getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoStock movimiento) {
        this.movimiento = movimiento;
    }

    public Date getFchmov() {
        return fechaMovimiento;
    }

    public void setFchmov(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getAtributo1() {
        return atributo1;
    }

    public void setAtributo1(String atributo1) {
        this.atributo1 = atributo1;
    }

    public String getAtributo2() {
        return atributo2;
    }

    public void setAtributo2(String atributo2) {
        this.atributo2 = atributo2;
    }

    public String getAtributo3() {
        return atributo3;
    }

    public void setAtributo3(String atributo3) {
        this.atributo3 = atributo3;
    }

    public String getAtributo4() {
        return atributo4;
    }

    public void setAtributo4(String atributo4) {
        this.atributo4 = atributo4;
    }

    public String getAtributo5() {
        return atributo5;
    }

    public void setAtributo5(String atributo5) {
        this.atributo5 = atributo5;
    }

    public String getAtributo6() {
        return atributo6;
    }

    public void setAtributo6(String atributo6) {
        this.atributo6 = atributo6;
    }

    public String getAtributo7() {
        return atributo7;
    }

    public void setAtributo7(String atributo7) {
        this.atributo7 = atributo7;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCantidadSecundaria() {
        return cantidadSecundaria;
    }

    public void setCantidadSecundaria(BigDecimal cantidadSecundaria) {
        this.cantidadSecundaria = cantidadSecundaria;
    }

    public BigDecimal getStocks() {
        return stocks;
    }

    public void setStocks(BigDecimal stocks) {
        this.stocks = stocks;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getPrecioSecundario() {
        return precioSecundario;
    }

    public void setPrecioSecundario(BigDecimal precioSecundario) {
        this.precioSecundario = precioSecundario;
    }

    public Moneda getMonedaSecundaria() {
        return monedaSecundaria;
    }

    public void setMonedaSecundaria(Moneda monedaSecundaria) {
        this.monedaSecundaria = monedaSecundaria;
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public boolean isTodoOk() {
        return todoOk;
    }

    public void setTodoOk(boolean todoOk) {
        this.todoOk = todoOk;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public double getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(double saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    public double getStockComprometer() {
        return stockComprometer;
    }

    public void setStockComprometer(double stockComprometer) {
        this.stockComprometer = stockComprometer;
    }

    public double getStockDescomprometer() {
        return stockDescomprometer;
    }

    public void setStockDescomprometer(double stockDescomprometer) {
        this.stockDescomprometer = stockDescomprometer;
    }

    public double getStockDescontar() {
        return stockDescontar;
    }

    public void setStockDescontar(double stockDescontar) {
        this.stockDescontar = stockDescontar;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemMovimientoStock other = (ItemMovimientoStock) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemMovimientoStock{" + "id=" + id + '}';
    }

}
