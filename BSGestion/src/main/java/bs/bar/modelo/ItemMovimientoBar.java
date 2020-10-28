/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Concepto;
import bs.stock.modelo.Producto;
import bs.stock.modelo.UnidadMedida;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "br_movimiento_item")
@EntityListeners(AuditoriaListener.class)
@XmlRootElement
public class ItemMovimientoBar implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @JoinColumn(name = "ID_MCAB", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private MovimientoBar movimiento;

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

    @Column(name = "CANTID", precision = 15, scale = 4)
    private double cantidad;
    @Column(name = "CNTPEN", precision = 15, scale = 4)
    private double cantidadPendiente;
    @Column(name = "CNTORI", precision = 15, scale = 4)
    private double cantidadOriginal;

    @Column(name = "PRECIO", precision = 15, scale = 4)
    private double precio;
    @Column(name = "PRESEC", precision = 15, scale = 4)
    private double precioSecundario;

    @Column(name = "TOTLIN", precision = 15, scale = 4)
    private double totalLinea;

    @Column(name = "TOTSEC", precision = 15, scale = 4)
    private double totalLineaSecundario;

    @Column(name = "PCTBF1", precision = 15, scale = 4)
    private double porcentajeBonificacion1;
    @Column(name = "PCTBF2", precision = 15, scale = 4)
    private double porcentajeBonificacion2;

    @Column(name = "PCTBFN", precision = 15, scale = 4)
    private double porcentaTotalBonificacion;

    @Column(name = "CNTBON")
    private double cantidadBonificada;

    @Column(name = "PORIMP", precision = 15, scale = 4)
    private double porcentajeImpuesto;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private double cotizacion;

    @JoinColumn(name = "UNIMED", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadMedida;

    @Lob
    @Column(name = "OBSERVA", length = 2147483647)
    private String observaciones;

    @Column(name = "PCOSTO", precision = 15, scale = 4)
    private double precioCosto;

    @Column(name = "COSSEC", precision = 15, scale = 4)
    private double precioCostoSecundario;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private BigDecimal precioConImpuesto;

    public ItemMovimientoBar() {

        this.cantidad = 0;
        this.cantidadBonificada = 0;

        this.auditoria = new Auditoria();
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

    public MovimientoBar getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoBar movimiento) {
        this.movimiento = movimiento;
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

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getCantidadPendiente() {
        return cantidadPendiente;
    }

    public void setCantidadPendiente(double cantidadPendiente) {
        this.cantidadPendiente = cantidadPendiente;
    }

    public double getCantidadOriginal() {
        return cantidadOriginal;
    }

    public void setCantidadOriginal(double cantidadOriginal) {
        this.cantidadOriginal = cantidadOriginal;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getPrecioSecundario() {
        return precioSecundario;
    }

    public void setPrecioSecundario(double precioSecundario) {
        this.precioSecundario = precioSecundario;
    }

    public double getTotalLinea() {
        return totalLinea;
    }

    public void setTotalLinea(double totalLinea) {
        this.totalLinea = totalLinea;
    }

    public double getTotalLineaSecundario() {
        return totalLineaSecundario;
    }

    public void setTotalLineaSecundario(double totalLineaSecundario) {
        this.totalLineaSecundario = totalLineaSecundario;
    }

    public double getPorcentajeBonificacion1() {
        return porcentajeBonificacion1;
    }

    public void setPorcentajeBonificacion1(double porcentajeBonificacion1) {
        this.porcentajeBonificacion1 = porcentajeBonificacion1;
    }

    public double getPorcentajeBonificacion2() {
        return porcentajeBonificacion2;
    }

    public void setPorcentajeBonificacion2(double porcentajeBonificacion2) {
        this.porcentajeBonificacion2 = porcentajeBonificacion2;
    }

    public double getPorcentaTotalBonificacion() {
        return porcentaTotalBonificacion;
    }

    public void setPorcentaTotalBonificacion(double porcentaTotalBonificacion) {
        this.porcentaTotalBonificacion = porcentaTotalBonificacion;
    }

    public double getCantidadBonificada() {
        return cantidadBonificada;
    }

    public void setCantidadBonificada(double cantidadBonificada) {
        this.cantidadBonificada = cantidadBonificada;
    }

    public double getPorcentajeImpuesto() {
        return porcentajeImpuesto;
    }

    public void setPorcentajeImpuesto(double porcentajeImpuesto) {
        this.porcentajeImpuesto = porcentajeImpuesto;
    }

    public double getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(double cotizacion) {
        this.cotizacion = cotizacion;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public double getPrecioCosto() {
        return precioCosto;
    }

    public void setPrecioCosto(double precioCosto) {
        this.precioCosto = precioCosto;
    }

    public double getPrecioCostoSecundario() {
        return precioCostoSecundario;
    }

    public void setPrecioCostoSecundario(double precioCostoSecundario) {
        this.precioCostoSecundario = precioCostoSecundario;
    }

    public BigDecimal getPrecioConImpuesto() {
        return precioConImpuesto;
    }

    public void setPrecioConImpuesto(BigDecimal precioConImpuesto) {
        this.precioConImpuesto = precioConImpuesto;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemMovimientoBar other = (ItemMovimientoBar) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
