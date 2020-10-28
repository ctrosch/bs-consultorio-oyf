/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.Producto;
import bs.stock.modelo.UnidadMedida;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "fc_movimiento_item_kit")
@EntityListeners(AuditoriaListener.class)
public class ItemMovimientoFacturacionKit implements IAuditableEntity, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_IDET", referencedColumnName = "ID", nullable = false)
    private ItemMovimientoFacturacion itemProducto;

    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto producto;

    @Column(name = "DESCRP", length = 200)
    private String descripcion;

    //Deposito
    @JoinColumn(name = "DEPOSI", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito deposito;

    @Column(name = "CNTNOM", precision = 15, scale = 4)
    private BigDecimal cantidadNominal;

    @Column(name = "CANTID", precision = 15, scale = 4)
    private BigDecimal cantidad;

    @JoinColumn(name = "UNIMED", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadMedida;

    @Column(name = "PRECIO", precision = 15, scale = 4)
    private BigDecimal precio;

    @Column(name = "PRESEC", precision = 15, scale = 4)
    private BigDecimal precioSecundario;

    @Column(name = "TOTLIN", precision = 15, scale = 4)
    private BigDecimal totalLinea;

    @Column(name = "TOTSEC", precision = 15, scale = 4)
    private BigDecimal totalLineaSecundario;

    @Column(name = "COSMAN", length = 1)
    private boolean costoManual;

    @Column(name = "PCOSTO", precision = 15, scale = 4)
    private BigDecimal precioCosto;

    @Column(name = "COSSEC", precision = 15, scale = 4)
    private BigDecimal precioCostoSecundario;

    @Transient
    private BigDecimal precioConImpuesto;

    @Transient
    private double stock;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean conError;

    public ItemMovimientoFacturacionKit() {

        cantidad = BigDecimal.ZERO;
        cantidadNominal = BigDecimal.ZERO;
        auditoria = new Auditoria();
        costoManual = false;

    }

    public ItemMovimientoFacturacionKit(Producto producto) {

        cantidad = BigDecimal.ZERO;
        cantidadNominal = BigDecimal.ZERO;
        auditoria = new Auditoria();
        costoManual = false;
        this.producto = producto;
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

    public ItemMovimientoFacturacion getItemProducto() {
        return itemProducto;
    }

    public void setItemProducto(ItemMovimientoFacturacion itemProducto) {
        this.itemProducto = itemProducto;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
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

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    public BigDecimal getCantidadNominal() {
        return cantidadNominal;
    }

    public void setCantidadNominal(BigDecimal cantidadNominal) {
        this.cantidadNominal = cantidadNominal;
    }

    public BigDecimal getPrecioConImpuesto() {
        return precioConImpuesto;
    }

    public void setPrecioConImpuesto(BigDecimal precioConImpuesto) {
        this.precioConImpuesto = precioConImpuesto;
    }

    public boolean isCostoManual() {
        return costoManual;
    }

    public void setCostoManual(boolean costoManual) {
        this.costoManual = costoManual;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
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
        final ItemMovimientoFacturacionKit other = (ItemMovimientoFacturacionKit) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemMovimientoFacturacionKit{" + "id=" + this.id + '}';
    }
}
