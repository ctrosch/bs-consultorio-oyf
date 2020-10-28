/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Concepto;
import bs.global.modelo.Moneda;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.Producto;
import bs.stock.modelo.UnidadMedida;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "fc_movimiento_item")
@EntityListeners(AuditoriaListener.class)
public class ItemMovimientoFacturacion implements IAuditableEntity, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MCAB", referencedColumnName = "ID")
    private MovimientoFacturacion movimiento;

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

    //Deposito
    @JoinColumn(name = "DEPOSI", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito deposito;

    //Punto de venta
    @Column(name = "SUCURS", length = 6)
    private String puntoVenta;

    //Nro cta cliente
    @Column(name = "NROCTA", length = 13)
    private String cliente;

    @Column(name = "NROSUB", length = 13)
    private String nrosub;

    @Column(name = "PRECIO", precision = 15, scale = 4)
    private BigDecimal precio;
    @Column(name = "PRESEC", precision = 15, scale = 4)
    private BigDecimal precioSecundario;

    @JoinColumn(name = "COFLIS", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaListaPrecio;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private BigDecimal cotizacion;

    @Column(name = "CANTID", precision = 15, scale = 4)
    private BigDecimal cantidad;
    @Column(name = "CNTPEN", precision = 15, scale = 4)
    private BigDecimal cantidadPendiente;
    @Column(name = "CNTORI", precision = 15, scale = 4)
    private BigDecimal cantidadOriginal;

    @Column(name = "KGAFOR", precision = 15, scale = 2)
    private BigDecimal kilogramosAforado;
    @Column(name = "KGEFEC", precision = 15, scale = 2)
    private BigDecimal kilogramosEfectivo;

    //Total de linea
    @Column(name = "TOTLIN", precision = 15, scale = 4)
    private BigDecimal totalLinea;

    @Column(name = "TOTSEC", precision = 15, scale = 4)
    private BigDecimal totalLineaSecundario;

    @Column(name = "PORIMP", precision = 15, scale = 4)
    private BigDecimal porcentajeImpuesto;

    @JoinColumn(name = "UNIMED", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadMedida;

    @Column(name = "FCHENT")
    @Temporal(TemporalType.DATE)
    private Date fechaEntregaDesde;
    @Column(name = "FCHHAS")
    @Temporal(TemporalType.DATE)
    private Date fechaEntregaHasta;

    @Column(name = "PCTBF1", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion1;
    @Column(name = "PCTBF2", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion2;
    @Column(name = "PCTBF3", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion3;
    @Column(name = "PCTBF4", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion4;
    @Column(name = "PCTBF5", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion5;
    @Column(name = "PCTBF6", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion6;
    @Column(name = "PCTBFN", precision = 15, scale = 4)
    private BigDecimal porcentaTotalBonificacion;

    @Column(name = "CNTBON", precision = 15, scale = 4)
    private BigDecimal cantidadBonificada;

    @Column(name = "VALDEC", precision = 15, scale = 2)
    private BigDecimal valorDeclarado;

    @Lob
    @Column(name = "OBSERVA", length = 2147483647)
    private String observaciones;

    @Column(name = "COSMAN", length = 1)
    private boolean costoManual;

    @Column(name = "PCOSTO", precision = 15, scale = 4)
    private BigDecimal precioCosto;

    @Column(name = "COSSEC", precision = 15, scale = 4)
    private BigDecimal precioCostoSecundario;

    @OneToMany(mappedBy = "itemProducto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemMovimientoFacturacionDetalle> itemsDetalle;

    @OneToMany(mappedBy = "itemProducto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemMovimientoFacturacionKit> itemsKit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_IAPL", referencedColumnName = "ID")
    private ItemMovimientoFacturacion itemAplicado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_IREV", referencedColumnName = "ID")
    private ItemMovimientoFacturacion itemReversion;

    @OneToMany(mappedBy = "itemAplicado", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemMovimientoFacturacion> itemsAplicacion;

    @Embedded
    private Auditoria auditoria;

    //Cantidad por precio unitario menos bonificaciones sin impuestos
    @Transient
    private BigDecimal precioUnitarioFinalSinImpuesto;

    @Transient
    private BigDecimal precioConImpuesto;

    @Transient
    private BigDecimal importeBonificacionSinImpuesto;

    @Transient
    private BigDecimal totalLineaConImpuesto;
    @Transient
    private String atributo1;
    @Transient
    private String atributo2;
    @Transient
    private String atributo3;
    @Transient
    private String atributo4;
    @Transient
    private String atributo5;
    @Transient
    private String atributo6;
    @Transient
    private String atributo7;
    @Transient
    private boolean conError;

    public ItemMovimientoFacturacion() {

        cantidad = BigDecimal.ZERO;
        cantidadBonificada = BigDecimal.ZERO;
        cantidadPendiente = BigDecimal.ZERO;

        precio = BigDecimal.ZERO;
        precioSecundario = BigDecimal.ZERO;
        precioConImpuesto = BigDecimal.ZERO;

        totalLinea = BigDecimal.ZERO;
        totalLineaSecundario = BigDecimal.ZERO;
        totalLineaConImpuesto = BigDecimal.ZERO;

        porcentajeBonificacion1 = BigDecimal.ZERO;
        porcentajeBonificacion2 = BigDecimal.ZERO;
        porcentajeBonificacion3 = BigDecimal.ZERO;
        porcentajeBonificacion4 = BigDecimal.ZERO;
        porcentajeBonificacion5 = BigDecimal.ZERO;
        porcentajeBonificacion6 = BigDecimal.ZERO;
        porcentaTotalBonificacion = BigDecimal.ZERO;

        porcentajeImpuesto = BigDecimal.ZERO;
        precioUnitarioFinalSinImpuesto = BigDecimal.ZERO;
        importeBonificacionSinImpuesto = BigDecimal.ZERO;

        itemsDetalle = new ArrayList<ItemMovimientoFacturacionDetalle>();
        itemsKit = new ArrayList<ItemMovimientoFacturacionKit>();

        cotizacion = BigDecimal.ONE;
        auditoria = new Auditoria();

        atributo1 = "";
        atributo2 = "";
        atributo3 = "";
        atributo4 = "";
        atributo5 = "";
        atributo6 = "";
        atributo7 = "";

    }

    public ItemMovimientoFacturacion(Producto producto) {

        cantidad = BigDecimal.ZERO;
        cantidadBonificada = BigDecimal.ZERO;
        cantidadPendiente = BigDecimal.ZERO;

        precio = BigDecimal.ZERO;
        precioSecundario = BigDecimal.ZERO;
        precioConImpuesto = BigDecimal.ZERO;

        totalLinea = BigDecimal.ZERO;
        totalLineaSecundario = BigDecimal.ZERO;
        totalLineaConImpuesto = BigDecimal.ZERO;

        porcentajeBonificacion1 = BigDecimal.ZERO;
        porcentajeBonificacion2 = BigDecimal.ZERO;
        porcentajeBonificacion3 = BigDecimal.ZERO;
        porcentajeBonificacion4 = BigDecimal.ZERO;
        porcentajeBonificacion5 = BigDecimal.ZERO;
        porcentajeBonificacion6 = BigDecimal.ZERO;
        porcentaTotalBonificacion = BigDecimal.ZERO;

        porcentajeImpuesto = BigDecimal.ZERO;
        precioUnitarioFinalSinImpuesto = BigDecimal.ZERO;
        importeBonificacionSinImpuesto = BigDecimal.ZERO;

        itemsDetalle = new ArrayList<ItemMovimientoFacturacionDetalle>();
        itemsKit = new ArrayList<ItemMovimientoFacturacionKit>();

        cotizacion = BigDecimal.ONE;
        auditoria = new Auditoria();
        this.producto = producto;

        if (producto != null) {
            productoOriginal = producto;
            concepto = producto.getConceptoVenta();
            //cuenta = producto.getCuentaVenta();
        }

        atributo1 = "";
        atributo2 = "";
        atributo3 = "";
        atributo4 = "";
        atributo5 = "";
        atributo6 = "";
        atributo7 = "";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

//    public Integer getIdItemAplicacion() {
//        return idItemAplicacion;
//    }
//
//    public void setIdItemAplicacion(Integer idItemAplicacion) {
//        this.idItemAplicacion = idItemAplicacion;
//    }
    public BigDecimal getCantidadPendiente() {
        return cantidadPendiente;
    }

    public void setCantidadPendiente(BigDecimal cantidadPendiente) {
        this.cantidadPendiente = cantidadPendiente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaEntregaDesde() {
        return fechaEntregaDesde;
    }

    public void setFechaEntregaDesde(Date fechaEntregaDesde) {
        this.fechaEntregaDesde = fechaEntregaDesde;
    }

    public Date getFechaEntregaHasta() {
        return fechaEntregaHasta;
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

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
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

    public BigDecimal getPorcentajeBonificacion3() {
        return porcentajeBonificacion3;
    }

    public void setPorcentajeBonificacion3(BigDecimal porcentajeBonificacion3) {
        this.porcentajeBonificacion3 = porcentajeBonificacion3;
    }

    public BigDecimal getPorcentajeBonificacion4() {
        return porcentajeBonificacion4;
    }

    public void setPorcentajeBonificacion4(BigDecimal porcentajeBonificacion4) {
        this.porcentajeBonificacion4 = porcentajeBonificacion4;
    }

    public BigDecimal getPorcentajeBonificacion5() {
        return porcentajeBonificacion5;
    }

    public void setPorcentajeBonificacion5(BigDecimal porcentajeBonificacion5) {
        this.porcentajeBonificacion5 = porcentajeBonificacion5;
    }

    public BigDecimal getPorcentajeBonificacion6() {
        return porcentajeBonificacion6;
    }

    public void setPorcentajeBonificacion6(BigDecimal porcentajeBonificacion6) {
        this.porcentajeBonificacion6 = porcentajeBonificacion6;
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

    public void setFechaEntregaHasta(Date fechaEntregaHasta) {
        this.fechaEntregaHasta = fechaEntregaHasta;
    }

    public MovimientoFacturacion getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoFacturacion movimiento) {
        this.movimiento = movimiento;
    }

    public String getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(String puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getNrosub() {
        return nrosub;
    }

    public void setNrosub(String nrosub) {
        this.nrosub = nrosub;
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public Moneda getMonedaListaPrecio() {
        return monedaListaPrecio;
    }

    public void setMonedaListaPrecio(Moneda monedaListaPrecio) {
        this.monedaListaPrecio = monedaListaPrecio;
    }

    public int getNroitm() {
        return nroitm;
    }

    public void setNroitm(int nroitm) {
        this.nroitm = nroitm;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getCantidadOriginal() {
        return cantidadOriginal;
    }

    public void setCantidadOriginal(BigDecimal cantidadOriginal) {
        this.cantidadOriginal = cantidadOriginal;
    }

    public BigDecimal getTotalLineaConIVA() {

        try {
            if (cantidad != null) {

                BigDecimal bonif = precio.multiply(porcentaTotalBonificacion.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP).negate()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal impUnit = precio.add(bonif);
                BigDecimal impTotal = cantidad.multiply(impUnit).setScale(2, RoundingMode.HALF_UP);
                totalLineaConImpuesto = impTotal.multiply(porcentajeImpuesto).setScale(2, RoundingMode.HALF_UP);
            }

            return totalLineaConImpuesto;

        } catch (Exception e) {

            return BigDecimal.ZERO;
        }
    }

    public Producto getProductoOriginal() {
        return productoOriginal;
    }

    public void setProductoOriginal(Producto productoOriginal) {
        this.productoOriginal = productoOriginal;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public BigDecimal getTotalLinea() {
        return totalLinea;
    }

    public void setTotalLinea(BigDecimal totalLinea) {
        this.totalLinea = totalLinea;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    public BigDecimal getTotalLineaSecundario() {
        return totalLineaSecundario;
    }

    public void setTotalLineaSecundario(BigDecimal totalLineaSecundario) {
        this.totalLineaSecundario = totalLineaSecundario;
    }

    public BigDecimal getValorDeclarado() {
        return valorDeclarado;
    }

    public void setValorDeclarado(BigDecimal valorDeclarado) {
        this.valorDeclarado = valorDeclarado;
    }

    public BigDecimal getKilogramosAforado() {
        return kilogramosAforado;
    }

    public void setKilogramosAforado(BigDecimal kilogramosAforado) {
        this.kilogramosAforado = kilogramosAforado;
    }

    public BigDecimal getKilogramosEfectivo() {
        return kilogramosEfectivo;
    }

    public void setKilogramosEfectivo(BigDecimal kilogramosEfectivo) {
        this.kilogramosEfectivo = kilogramosEfectivo;
    }

    public BigDecimal getPorcentajeImpuesto() {
        return porcentajeImpuesto;
    }

    public void setPorcentajeImpuesto(BigDecimal porcentajeImpuesto) {
        this.porcentajeImpuesto = porcentajeImpuesto;
    }

    public BigDecimal getPrecioUnitarioFinalSinImpuesto() {
        return precioUnitarioFinalSinImpuesto;
    }

    public void setPrecioUnitarioFinalSinImpuesto(BigDecimal precioUnitarioFinalSinImpuesto) {
        this.precioUnitarioFinalSinImpuesto = precioUnitarioFinalSinImpuesto;
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

    public BigDecimal getPrecioCosto() {
        return precioCosto;
    }

    public void setPrecioCosto(BigDecimal precioCosto) {
        this.precioCosto = precioCosto;
    }

    public BigDecimal getPrecioCostoSecundario() {
        return precioCostoSecundario;
    }

    public void setPrecioCostoSecundario(BigDecimal precioCostoSecundario) {
        this.precioCostoSecundario = precioCostoSecundario;
    }

    public List<ItemMovimientoFacturacionDetalle> getItemsDetalle() {
        return itemsDetalle;
    }

    public void setItemsDetalle(List<ItemMovimientoFacturacionDetalle> itemsDetalle) {
        this.itemsDetalle = itemsDetalle;
    }

    public ItemMovimientoFacturacion getItemAplicado() {
        return itemAplicado;
    }

    public void setItemAplicado(ItemMovimientoFacturacion itemAplicado) {
        this.itemAplicado = itemAplicado;
    }

    public List<ItemMovimientoFacturacion> getItemsAplicacion() {
        return itemsAplicacion;
    }

    public void setItemsAplicacion(List<ItemMovimientoFacturacion> itemsAplicacion) {
        this.itemsAplicacion = itemsAplicacion;
    }

    public List<ItemMovimientoFacturacionKit> getItemsKit() {
        return itemsKit;
    }

    public void setItemsKit(List<ItemMovimientoFacturacionKit> itemsKit) {
        this.itemsKit = itemsKit;
    }

    public boolean isCostoManual() {
        return costoManual;
    }

    public void setCostoManual(boolean costoManual) {
        this.costoManual = costoManual;
    }

    public ItemMovimientoFacturacion getItemReversion() {
        return itemReversion;
    }

    public void setItemReversion(ItemMovimientoFacturacion itemReversion) {
        this.itemReversion = itemReversion;
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
        final ItemMovimientoFacturacion other = (ItemMovimientoFacturacion) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemMovimientoFacturacion{" + "id=" + this.id + '}';
    }
}
