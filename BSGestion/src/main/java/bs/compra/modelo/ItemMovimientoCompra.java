/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.compra.modelo;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
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
 * @author Claudio
 */
@Entity
@Table(name = "co_movimiento_item")
@EntityListeners(AuditoriaListener.class)
public class ItemMovimientoCompra implements IAuditableEntity, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NROITM")
    private int nroitm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MCAB", referencedColumnName = "ID", nullable = false)
    private MovimientoCompra movimiento;

    @Column(name = "ID_MSTO", nullable = true)
    private Integer idMovimientoStock;

    @Column(name = "ID_MINIC")
    private Integer movimientoInicial;

    @Column(name = "ID_MORIG")
    private Integer movimientoOriginal;

    @JoinColumns({
        @JoinColumn(name = "MODCPT", referencedColumnName = "MODULO", nullable = false),
        @JoinColumn(name = "TIPCPT", referencedColumnName = "TIPCPT", nullable = false),
        @JoinColumn(name = "CODCPT", referencedColumnName = "CODIGO", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Concepto concepto;

    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto producto;

    @Column(name = "DESCRP", length = 200)
    private String descripcion;

    @Column(name = "CODPRO", length = 40)
    private String codigoProveedor;

    @JoinColumn(name = "ARTORI", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto productoOriginal;

    //Deposito
    @JoinColumn(name = "DEPOSI", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito deposito;

    //punto de venta
    @Column(name = "SUCURS", length = 6)
    private String puntoVenta;

    //Nro cta proveedor
    @Column(name = "NROCTA", length = 13)
    private String proveedor;

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

    @JoinColumn(name = "UNIMED", referencedColumnName = "CODIGO", nullable = false)
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

    @OneToMany(mappedBy = "itemProducto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemDetalleItemMovimientoCompra> itemsDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_IAPL", referencedColumnName = "ID")
    private ItemMovimientoCompra itemAplicado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_IREV", referencedColumnName = "ID")
    private ItemMovimientoCompra itemReversion;

    @OneToMany(mappedBy = "itemAplicado", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemMovimientoCompra> itemsAplicacion;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private BigDecimal pendiente;

    @Transient
    private BigDecimal precioListaConIVA;

    @Transient
    private BigDecimal precioFinalConIVA;

    @Transient
    private BigDecimal precioFinalSinIVA;

    @Transient
    private BigDecimal totalPendienteSinIVA;

    @Transient
    private BigDecimal totalPendienteConIVA;

    @Transient
    private BigDecimal totalLineaSinIVA;

    @Transient
    private BigDecimal totalLineaConIVA;

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

    public ItemMovimientoCompra() {

        fechaEntregaDesde = new Date();
        fechaEntregaHasta = new Date();

        precio = BigDecimal.ZERO;
        precioSecundario = BigDecimal.ZERO;
        cantidad = BigDecimal.ZERO;
        cantidadPendiente = BigDecimal.ZERO;
        cantidadBonificada = BigDecimal.ZERO;

        totalLinea = BigDecimal.ZERO;
        totalLineaSecundario = BigDecimal.ZERO;

        porcentajeBonificacion1 = BigDecimal.ZERO;
        porcentajeBonificacion2 = BigDecimal.ZERO;
        porcentajeBonificacion3 = BigDecimal.ZERO;
        porcentajeBonificacion4 = BigDecimal.ZERO;
        porcentajeBonificacion5 = BigDecimal.ZERO;
        porcentajeBonificacion6 = BigDecimal.ZERO;
        porcentaTotalBonificacion = BigDecimal.ZERO;

        atributo1 = "";
        atributo2 = "";
        atributo3 = "";
        atributo4 = "";
        atributo5 = "";
        atributo6 = "";
        atributo7 = "";

        itemsDetalle = new ArrayList<ItemDetalleItemMovimientoCompra>();

        cotizacion = BigDecimal.ONE;
        auditoria = new Auditoria();
    }

    public ItemMovimientoCompra(Producto producto) {

        fechaEntregaDesde = new Date();
        fechaEntregaHasta = new Date();

        precio = BigDecimal.ZERO;
        precioSecundario = BigDecimal.ZERO;
        cantidad = BigDecimal.ZERO;
        cantidadPendiente = BigDecimal.ZERO;
        cantidadBonificada = BigDecimal.ZERO;

        totalLinea = BigDecimal.ZERO;
        totalLineaSecundario = BigDecimal.ZERO;

        porcentajeBonificacion1 = BigDecimal.ZERO;
        porcentajeBonificacion2 = BigDecimal.ZERO;
        porcentajeBonificacion3 = BigDecimal.ZERO;
        porcentajeBonificacion4 = BigDecimal.ZERO;
        porcentajeBonificacion5 = BigDecimal.ZERO;
        porcentajeBonificacion6 = BigDecimal.ZERO;
        porcentaTotalBonificacion = BigDecimal.ZERO;

        atributo1 = "";
        atributo2 = "";
        atributo3 = "";
        atributo4 = "";
        atributo5 = "";
        atributo6 = "";
        atributo7 = "";

        itemsDetalle = new ArrayList<ItemDetalleItemMovimientoCompra>();

        cotizacion = BigDecimal.ONE;
        auditoria = new Auditoria();
        this.producto = producto;

        if (producto != null) {
            productoOriginal = producto;
            concepto = producto.getConceptoProveedor();
            //cuenta = producto.getCuentaVenta();
        }
    }

    public ItemMovimientoCompra(Integer id, int nroitm, String descrp) {

        this.id = id;
        this.nroitm = nroitm;
        this.descripcion = descrp;

        fechaEntregaDesde = new Date();
        fechaEntregaHasta = new Date();
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

    public MovimientoCompra getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoCompra movimiento) {
        this.movimiento = movimiento;
    }

    public Integer getIdMovimientoStock() {
        return idMovimientoStock;
    }

    public void setIdMovimientoStock(Integer idMovimientoStock) {
        this.idMovimientoStock = idMovimientoStock;
    }

    public Integer getMovimientoInicial() {
        return movimientoInicial;
    }

    public void setMovimientoInicial(Integer movimientoInicial) {
        this.movimientoInicial = movimientoInicial;
    }

    public Integer getMovimientoOriginal() {
        return movimientoOriginal;
    }

    public void setMovimientoOriginal(Integer movimientoOriginal) {
        this.movimientoOriginal = movimientoOriginal;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Producto getProductoOriginal() {
        return productoOriginal;
    }

    public void setProductoOriginal(Producto productoOriginal) {
        this.productoOriginal = productoOriginal;
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

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public String getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(String puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getNrosub() {
        return nrosub;
    }

    public void setNrosub(String nrosub) {
        this.nrosub = nrosub;
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

    public Moneda getMonedaListaPrecio() {
        return monedaListaPrecio;
    }

    public void setMonedaListaPrecio(Moneda monedaListaPrecio) {
        this.monedaListaPrecio = monedaListaPrecio;
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

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
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

    public void setFechaEntregaHasta(Date fechaEntregaHasta) {
        this.fechaEntregaHasta = fechaEntregaHasta;
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

    public BigDecimal getValorDeclarado() {
        return valorDeclarado;
    }

    public void setValorDeclarado(BigDecimal valorDeclarado) {
        this.valorDeclarado = valorDeclarado;
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

    public BigDecimal getPendiente() {
        return pendiente;
    }

    public void setPendiente(BigDecimal pendiente) {
        this.pendiente = pendiente;
    }

    public BigDecimal getPrecioListaConIVA() {
        return precioListaConIVA;
    }

    public void setPrecioListaConIVA(BigDecimal precioListaConIVA) {
        this.precioListaConIVA = precioListaConIVA;
    }

    public BigDecimal getPrecioFinalConIVA() {
        return precioFinalConIVA;
    }

    public void setPrecioFinalConIVA(BigDecimal precioFinalConIVA) {
        this.precioFinalConIVA = precioFinalConIVA;
    }

    public BigDecimal getPrecioFinalSinIVA() {
        return precioFinalSinIVA;
    }

    public void setPrecioFinalSinIVA(BigDecimal precioFinalSinIVA) {
        this.precioFinalSinIVA = precioFinalSinIVA;
    }

    public BigDecimal getTotalPendienteSinIVA() {
        return totalPendienteSinIVA;
    }

    public void setTotalPendienteSinIVA(BigDecimal totalPendienteSinIVA) {
        this.totalPendienteSinIVA = totalPendienteSinIVA;
    }

    public BigDecimal getTotalPendienteConIVA() {
        return totalPendienteConIVA;
    }

    public void setTotalPendienteConIVA(BigDecimal totalPendienteConIVA) {
        this.totalPendienteConIVA = totalPendienteConIVA;
    }

    public BigDecimal getTotalLineaSinIVA() {
        return totalLineaSinIVA;
    }

    public void setTotalLineaSinIVA(BigDecimal totalLineaSinIVA) {
        this.totalLineaSinIVA = totalLineaSinIVA;
    }

    public BigDecimal getTotalLineaConIVA() {
        return totalLineaConIVA;
    }

    public void setTotalLineaConIVA(BigDecimal totalLineaConIVA) {
        this.totalLineaConIVA = totalLineaConIVA;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    public List<ItemDetalleItemMovimientoCompra> getItemsDetalle() {
        return itemsDetalle;
    }

    public void setItemsDetalle(List<ItemDetalleItemMovimientoCompra> itemsDetalle) {
        this.itemsDetalle = itemsDetalle;
    }

    public ItemMovimientoCompra getItemAplicado() {
        return itemAplicado;
    }

    public void setItemAplicado(ItemMovimientoCompra itemAplicado) {
        this.itemAplicado = itemAplicado;
    }

    public List<ItemMovimientoCompra> getItemsAplicacion() {
        return itemsAplicacion;
    }

    public void setItemsAplicacion(List<ItemMovimientoCompra> itemsAplicacion) {
        this.itemsAplicacion = itemsAplicacion;
    }

    public String getCodigoProveedor() {
        return codigoProveedor;
    }

    public void setCodigoProveedor(String codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }

    public ItemMovimientoCompra getItemReversion() {
        return itemReversion;
    }

    public void setItemReversion(ItemMovimientoCompra itemReversion) {
        this.itemReversion = itemReversion;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final ItemMovimientoCompra other = (ItemMovimientoCompra) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemMovimientoCompra{" + "id=" + id + '}';
    }

}
