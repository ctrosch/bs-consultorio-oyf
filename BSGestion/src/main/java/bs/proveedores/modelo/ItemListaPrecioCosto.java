/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.stock.modelo.Producto;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
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
@Table(name = "pv_lista_precio_item")
@EntityListeners(AuditoriaListener.class)
public class ItemListaPrecioCosto implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CODLIS", nullable = false, length = 10)
    private String codlis;

    @Column(name = "ARTCOD", nullable = false, length = 30)
    private String artcod;

    @Column(name = "FECLIS")
    @Temporal(TemporalType.DATE)
    private Date fechaVigencia;

    @JoinColumn(name = "CODLIS", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ListaCosto listaCosto;

    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
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

    public ItemListaPrecioCosto() {

        this.fechaVigencia = new Date();
        this.precio = BigDecimal.ZERO;

        this.auditoria = new Auditoria();
    }

    public ItemListaPrecioCosto(Integer id) {
        this.id = id;
        this.fechaVigencia = new Date();
        this.precio = BigDecimal.ZERO;

        this.auditoria = new Auditoria();
    }

    public ItemListaPrecioCosto(Integer id, Date feclis) {

        this.id = id;
        this.fechaVigencia = feclis;
        this.precio = BigDecimal.ZERO;

        this.auditoria = new Auditoria();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaVigencia() {
        return fechaVigencia;
    }

    public void setFechaVigencia(Date fechaVigencia) {
        this.fechaVigencia = fechaVigencia;
    }

    public ListaCosto getListaCosto() {
        return listaCosto;
    }

    public void setListaCosto(ListaCosto listaCosto) {
        this.listaCosto = listaCosto;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
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

    public boolean isModificado() {
        return modificado;
    }

    public void setModificado(boolean modificado) {
        this.modificado = modificado;
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

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemListaPrecioCosto)) {
            return false;
        }
        ItemListaPrecioCosto other = (ItemListaPrecioCosto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.compras.modelo.ListaCostoItem[ id=" + id + " ]";
    }

}
