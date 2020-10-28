/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Moneda;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "pv_lista_precio")
@EntityListeners(AuditoriaListener.class)
public class ListaCosto implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Size(min = 1, max = 10)
    @Column(name = "CODIGO")
    private String codigo;
    @Size(min = 1, max = 60)
    @Column(name = "DESCRP")
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "INCIMP")
    private String incluyeImpuestos;
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "TPRREP")
    private String tomaPrecioReposicion;

    //Prioriza moneda producto
    @Column(name = "PRIMPR", length = 1, nullable = false)
    private String priorizaMonedaProducto;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @JoinColumn(name = "CODCOF", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda moneda;

    @JoinColumn(name = "COFDEU", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    //Precio maximo
    @Column(name = "PRMXPR", precision = 15, scale = 4)
    private BigDecimal precioMaximo;
    //Precio minimo
    @Column(name = "PRMIPR", precision = 15, scale = 4)
    private BigDecimal precioMinimo;

    @Embedded
    private Auditoria auditoria;
//
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "listaCosto")
//    private List<ItemListaPrecioCosto> itemPrecios;

    public ListaCosto() {
        this.precioMaximo = BigDecimal.ZERO;
        this.precioMinimo = BigDecimal.ZERO;
        this.incluyeImpuestos = "N";
        this.tomaPrecioReposicion = "N";
        this.auditoria = new Auditoria();
    }

    public ListaCosto(String codigo) {
        this.precioMaximo = BigDecimal.ZERO;
        this.precioMinimo = BigDecimal.ZERO;
        this.codigo = codigo;
        this.incluyeImpuestos = "N";
        this.tomaPrecioReposicion = "N";
        this.auditoria = new Auditoria();

    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIncluyeImpuestos() {
        return incluyeImpuestos;
    }

    public void setIncluyeImpuestos(String incluyeImpuestos) {
        this.incluyeImpuestos = incluyeImpuestos;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    public BigDecimal getPrecioMaximo() {
        return precioMaximo;
    }

    public void setPrecioMaximo(BigDecimal precioMaximo) {
        this.precioMaximo = precioMaximo;
    }

    public BigDecimal getPrecioMinimo() {
        return precioMinimo;
    }

    public void setPrecioMinimo(BigDecimal precioMinimo) {
        this.precioMinimo = precioMinimo;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public String getPriorizaMonedaProducto() {
        return priorizaMonedaProducto;
    }

    public void setPriorizaMonedaProducto(String priorizaMonedaProducto) {
        this.priorizaMonedaProducto = priorizaMonedaProducto;
    }

    public String getTomaPrecioReposicion() {
        return tomaPrecioReposicion;
    }

    public void setTomaPrecioReposicion(String tomaPrecioReposicion) {
        this.tomaPrecioReposicion = tomaPrecioReposicion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigo != null ? codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ListaCosto)) {
            return false;
        }
        ListaCosto other = (ListaCosto) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.compras.modelo.ListaCosto[ codigo=" + codigo + " ]";
    }

}
