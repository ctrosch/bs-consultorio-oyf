/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.ventas.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.stock.modelo.Producto;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "vt_lista_precio_item")
@EntityListeners(AuditoriaListener.class)
public class ItemListaPrecioVenta implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)    
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "CODLIS", nullable = false, length = 10)
    private String codlis;    
    
    @Column(name = "ARTCOD", nullable = false, length = 30)
    private String artcod;
    
    @Column(name = "FECLIS")
    @Temporal(TemporalType.DATE)
    private Date fechaVigencia;

    @JoinColumn(name = "CODLIS", referencedColumnName = "CODIGO", nullable = false, insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ListaPrecioVenta listaDePrecio;

    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO", nullable = false, insertable=false, updatable=false)    
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto producto;

    @Column(name = "PRECIO", precision = 15, scale = 4)
    private BigDecimal precio;
    
    @Column(name = "MONREG", nullable = false, length = 30)    
    private String monedaRegistracion;
        
    @Transient
    private boolean modificado;
    
    @Transient
    private BigDecimal precioBase;
    
    @Transient
    private BigDecimal precioActual;
    
    @Embedded
    private Auditoria auditoria;
    
    
    public ItemListaPrecioVenta() {
        
        this.fechaVigencia = new Date();
        this.precio = BigDecimal.ZERO;
        this.auditoria = new Auditoria();        
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getCodlis() {
        return codlis;
    }

    public void setCodlis(String codlis) {
        this.codlis = codlis;
    }

    public String getArtcod() {
        return artcod;
    }

    public void setArtcod(String artcod) {
        this.artcod = artcod;
    }

    public Date getFechaVigencia() {
        return fechaVigencia;
    }

    public void setFechaVigencia(Date fechaVigencia) {
        this.fechaVigencia = fechaVigencia;
    }
    
    public ListaPrecioVenta getListaDePrecio() {
        return listaDePrecio;
    }

    public void setListaDePrecio(ListaPrecioVenta listaDePrecio) {
        this.listaDePrecio = listaDePrecio;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    public boolean isModificado() {
        return modificado;
    }

    public void setModificado(boolean modificado) {
        this.modificado = modificado;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public BigDecimal getPrecioActual() {
        return precioActual;
    }

    public void setPrecioActual(BigDecimal precioActual) {
        this.precioActual = precioActual;
    }
    
    public String getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(String monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.codlis != null ? this.codlis.hashCode() : 0);
        hash = 23 * hash + (this.artcod != null ? this.artcod.hashCode() : 0);
        hash = 23 * hash + (this.fechaVigencia != null ? this.fechaVigencia.hashCode() : 0);
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
        final ItemListaPrecioVenta other = (ItemListaPrecioVenta) obj;
        if ((this.codlis == null) ? (other.codlis != null) : !this.codlis.equals(other.codlis)) {
            return false;
        }
        if ((this.artcod == null) ? (other.artcod != null) : !this.artcod.equals(other.artcod)) {
            return false;
        }
        if (this.fechaVigencia != other.fechaVigencia && (this.fechaVigencia == null || !this.fechaVigencia.equals(other.fechaVigencia))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemListaPrecio{" + "codlis=" + codlis + ", artcod=" + artcod + ", fechaVigencia=" + fechaVigencia + '}';
    }

    
    
}
