/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Concepto;
import bs.global.modelo.Moneda;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.ItemDetalleModelo;
import bs.stock.modelo.Producto;
import bs.stock.modelo.UnidadMedida;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
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
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "tl_movimiento_item")
@EntityListeners(AuditoriaListener.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPITM", discriminatorType = DiscriminatorType.STRING, length = 1)
public abstract class ItemMovimientoTaller implements Serializable, ItemDetalleModelo, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @Column(name = "ID_IAPL", nullable = true)
    private Integer idItemAplicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MCAB", referencedColumnName = "ID")
    private MovimientoTaller movimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MAPL", referencedColumnName = "ID")
    private MovimientoTaller movimientoAplicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_IAPL", referencedColumnName = "ID", insertable = false, updatable = false)
    private ItemProductoTaller itemAplicado;

    @Column(name = "ID_MSTO")
    private Integer idMovimientoStock;

    /**
     * Movimiento original
     */
    @Column(name = "ID_MORIG", nullable = true)
    private Integer movimientoOriginal;

    /**
     * Movimiento inicial
     */
    @Column(name = "ID_MINIC", nullable = false)
    private Integer movimientoInicial;

    @JoinColumns({
        @JoinColumn(name = "MODCPT", referencedColumnName = "MODULO"),
        @JoinColumn(name = "TIPCPT", referencedColumnName = "TIPCPT"),
        @JoinColumn(name = "CODCPT", referencedColumnName = "CODIGO")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Concepto concepto;

    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto producto;

    @Column(name = "DESCRP", length = 200)
    private String descripcion;

    @JoinColumn(name = "ARTORI", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto productoOriginal;

    @Column(name = "natri1", length = 30)
    private String atributo1;

    @Column(name = "natri2", length = 30)
    private String atributo2;

    @Column(name = "natri3", length = 30)
    private String atributo3;

    @Column(name = "natri4", length = 30)
    private String atributo4;

    @Column(name = "natri5", length = 30)
    private String atributo5;

    @Column(name = "natri6", length = 30)
    private String atributo6;

    @Column(name = "natri7", length = 30)
    private String atributo7;

    //Deposito
    @JoinColumn(name = "DEPOSI", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito deposito;

    @Column(name = "CANTID", precision = 15, scale = 4)
    private BigDecimal cantidad;
    @Column(name = "CNTPEN", precision = 15, scale = 4)
    private BigDecimal cantidadPendiente;
    @Column(name = "CNTORI", precision = 15, scale = 4)
    private BigDecimal cantidadOriginal;

    @Column(name = "PRECIO", precision = 15, scale = 4)
    private BigDecimal precio;
    @Column(name = "PRESEC", precision = 15, scale = 4)
    private BigDecimal precioSecundario;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private BigDecimal cotizacion;

    @Column(name = "porimp", precision = 15, scale = 4)
    private BigDecimal porcentajeImpuesto;

    //Total de linea
    @Column(name = "TOTLIN", precision = 15, scale = 4)
    private BigDecimal totalLinea;

    @Column(name = "TOTSEC", precision = 15, scale = 4)
    private BigDecimal totalLineaSecundario;

    @JoinColumn(name = "COFLIS", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaListaPrecio;

    @JoinColumn(name = "UNIMED", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadMedida;

    @Column(name = "PCTBF1", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion1;
    @Column(name = "PCTBF2", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion2;

    @Column(name = "PCTBFN", precision = 15, scale = 4)
    private BigDecimal porcentaTotalBonificacion;

    @Column(name = "CNTBON", precision = 15, scale = 4)
    private BigDecimal cantidadBonificada;

    @Lob
    @Column(name = "OBSERVA", length = 2147483647)
    private String observaciones;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private List<ItemAplicacionTaller> itemsAplicacion;

    @Transient
    private BigDecimal precioConImpuesto;

    @Transient
    private BigDecimal importeBonificacionSinImpuesto;

    @Transient
    private BigDecimal totalLineaConImpuesto;

    @Transient
    private boolean conError;

    @Transient
    private double stockComprometer;

    @Transient
    private double stockDescomprometer;

    @Transient
    private double stockDescontar;

    public ItemMovimientoTaller() {

        precio = BigDecimal.ZERO;
        precioSecundario = BigDecimal.ZERO;
        cantidad = BigDecimal.ZERO;
        cantidadBonificada = BigDecimal.ZERO;

        totalLinea = BigDecimal.ZERO;
        totalLineaSecundario = BigDecimal.ZERO;

        porcentajeBonificacion1 = BigDecimal.ZERO;
        porcentajeBonificacion2 = BigDecimal.ZERO;
        porcentaTotalBonificacion = BigDecimal.ZERO;

        cotizacion = BigDecimal.ONE;
        auditoria = new Auditoria();

    }

    public ItemMovimientoTaller(Producto producto) {

        precio = BigDecimal.ZERO;
        precioSecundario = BigDecimal.ZERO;
        cantidad = BigDecimal.ZERO;
        cantidadBonificada = BigDecimal.ZERO;

        totalLinea = BigDecimal.ZERO;
        totalLineaSecundario = BigDecimal.ZERO;

        porcentajeBonificacion1 = BigDecimal.ZERO;
        porcentajeBonificacion2 = BigDecimal.ZERO;
        porcentaTotalBonificacion = BigDecimal.ZERO;

        cotizacion = BigDecimal.ONE;
        auditoria = new Auditoria();
        this.producto = producto;

        if (producto != null) {
            productoOriginal = producto;
            concepto = producto.getConceptoVenta();
            //cuenta = producto.getCuentaVenta();
        }
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int getNroitm() {
        return nroitm;
    }

    @Override
    public void setNroitm(int nroitm) {
        this.nroitm = nroitm;
    }

    public Integer getIdItemAplicacion() {
        return idItemAplicacion;
    }

    public void setIdItemAplicacion(Integer idItemAplicacion) {
        this.idItemAplicacion = idItemAplicacion;
    }

    @Override
    public Producto getProducto() {
        return producto;
    }

    @Override
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Producto getProductoOriginal() {
        return productoOriginal;
    }

    public void setProductoOriginal(Producto productoOriginal) {
        this.productoOriginal = productoOriginal;
    }

    @Override
    public String getAtributo1() {
        return atributo1;
    }

    @Override
    public void setAtributo1(String atributo1) {
        this.atributo1 = atributo1;
    }

    @Override
    public String getAtributo2() {
        return atributo2;
    }

    @Override
    public void setAtributo2(String atributo2) {
        this.atributo2 = atributo2;
    }

    @Override
    public String getAtributo3() {
        return atributo3;
    }

    @Override
    public void setAtributo3(String atributo3) {
        this.atributo3 = atributo3;
    }

    @Override
    public String getAtributo4() {
        return atributo4;
    }

    @Override
    public void setAtributo4(String atributo4) {
        this.atributo4 = atributo4;
    }

    @Override
    public String getAtributo5() {
        return atributo5;
    }

    @Override
    public void setAtributo5(String atributo5) {
        this.atributo5 = atributo5;
    }

    @Override
    public String getAtributo6() {
        return atributo6;
    }

    @Override
    public void setAtributo6(String atributo6) {
        this.atributo6 = atributo6;
    }

    @Override
    public String getAtributo7() {
        return atributo7;
    }

    @Override
    public void setAtributo7(String atributo7) {
        this.atributo7 = atributo7;
    }

    @Override
    public Deposito getDeposito() {
        return deposito;
    }

    @Override
    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    @Override
    public BigDecimal getCantidad() {
        return cantidad;
    }

    @Override
    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    @Override
    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public MovimientoTaller getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoTaller movimiento) {
        this.movimiento = movimiento;
    }

    public MovimientoTaller getMovimientoAplicacion() {
        return movimientoAplicacion;
    }

    public void setMovimientoAplicacion(MovimientoTaller movimientoAplicacion) {
        this.movimientoAplicacion = movimientoAplicacion;
    }

    public ItemProductoTaller getItemAplicado() {
        return itemAplicado;
    }

    public void setItemAplicado(ItemProductoTaller itemAplicado) {
        this.itemAplicado = itemAplicado;
    }

    public Integer getIdMovimientoStock() {
        return idMovimientoStock;
    }

    public void setIdMovimientoStock(Integer idMovimientoStock) {
        this.idMovimientoStock = idMovimientoStock;
    }

    public Integer getMovimientoOriginal() {
        return movimientoOriginal;
    }

    public void setMovimientoOriginal(Integer movimientoOriginal) {
        this.movimientoOriginal = movimientoOriginal;
    }

    public Integer getMovimientoInicial() {
        return movimientoInicial;
    }

    public void setMovimientoInicial(Integer movimientoInicial) {
        this.movimientoInicial = movimientoInicial;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getCantidadPendiente() {
        return cantidadPendiente;
    }

    public void setCantidadPendiente(BigDecimal cantidadPendiente) {
        this.cantidadPendiente = cantidadPendiente;
    }

    public BigDecimal getCantidadOriginal() {
        return cantidadOriginal;
    }

    public void setCantidadOriginal(BigDecimal cantidadOriginal) {
        this.cantidadOriginal = cantidadOriginal;
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

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }

    public BigDecimal getPorcentajeImpuesto() {
        return porcentajeImpuesto;
    }

    public void setPorcentajeImpuesto(BigDecimal porcentajeImpuesto) {
        this.porcentajeImpuesto = porcentajeImpuesto;
    }

    public BigDecimal getTotalLinea() {
        return totalLinea;
    }

    public void setTotalLinea(BigDecimal totalLinea) {
        this.totalLinea = totalLinea;
    }

    public BigDecimal getTotalLineaSecundario() {
        return totalLineaSecundario;
    }

    public void setTotalLineaSecundario(BigDecimal totalLineaSecundario) {
        this.totalLineaSecundario = totalLineaSecundario;
    }

    public Moneda getMonedaListaPrecio() {
        return monedaListaPrecio;
    }

    public void setMonedaListaPrecio(Moneda monedaListaPrecio) {
        this.monedaListaPrecio = monedaListaPrecio;
    }

    public BigDecimal getPorcentajeBonificacion1() {
        return porcentajeBonificacion1;
    }

    public void setPorcentajeBonificacion1(BigDecimal porcentajeBonificacion1) {
        this.porcentajeBonificacion1 = porcentajeBonificacion1;
    }

    public BigDecimal getPorcentajeBonificacion2() {
        return porcentajeBonificacion2;
    }

    public void setPorcentajeBonificacion2(BigDecimal porcentajeBonificacion2) {
        this.porcentajeBonificacion2 = porcentajeBonificacion2;
    }

    public BigDecimal getPorcentaTotalBonificacion() {
        return porcentaTotalBonificacion;
    }

    public void setPorcentaTotalBonificacion(BigDecimal porcentaTotalBonificacion) {
        this.porcentaTotalBonificacion = porcentaTotalBonificacion;
    }

    public BigDecimal getCantidadBonificada() {
        return cantidadBonificada;
    }

    public void setCantidadBonificada(BigDecimal cantidadBonificada) {
        this.cantidadBonificada = cantidadBonificada;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public List<ItemAplicacionTaller> getItemsAplicacion() {
        return itemsAplicacion;
    }

    public void setItemsAplicacion(List<ItemAplicacionTaller> itemsAplicacion) {
        this.itemsAplicacion = itemsAplicacion;
    }

    public BigDecimal getPrecioConImpuesto() {
        return precioConImpuesto;
    }

    public void setPrecioConImpuesto(BigDecimal precioConImpuesto) {
        this.precioConImpuesto = precioConImpuesto;
    }

    public BigDecimal getImporteBonificacionSinImpuesto() {
        return importeBonificacionSinImpuesto;
    }

    public void setImporteBonificacionSinImpuesto(BigDecimal importeBonificacionSinImpuesto) {
        this.importeBonificacionSinImpuesto = importeBonificacionSinImpuesto;
    }

    public BigDecimal getTotalLineaConImpuesto() {
        return totalLineaConImpuesto;
    }

    public void setTotalLineaConImpuesto(BigDecimal totalLineaConImpuesto) {
        this.totalLineaConImpuesto = totalLineaConImpuesto;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    @Override
    public double getStockComprometer() {
        return stockComprometer;
    }

    @Override
    public void setStockComprometer(double stockComprometer) {
        this.stockComprometer = stockComprometer;
    }

    @Override
    public double getStockDescomprometer() {
        return stockDescomprometer;
    }

    @Override
    public void setStockDescomprometer(double stockDescomprometer) {
        this.stockDescomprometer = stockDescomprometer;
    }

    @Override
    public double getStockDescontar() {
        return stockDescontar;
    }

    @Override
    public void setStockDescontar(double stockDescontar) {
        this.stockDescontar = stockDescontar;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final ItemMovimientoTaller other = (ItemMovimientoTaller) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemMovimientoTaller{" + "id=" + this.id + '}';
    }
}
