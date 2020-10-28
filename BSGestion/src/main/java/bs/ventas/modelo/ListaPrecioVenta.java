/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.modelo;

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

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "vt_lista_precio")
@EntityListeners(AuditoriaListener.class)
public class ListaPrecioVenta implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    //Codigo de lista de precios
    @Column(name = "CODIGO", nullable = false, length = 10)
    private String codigo;

    @Basic(optional = false)
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;

    @JoinColumn(name = "CODCOF", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda moneda;

    @JoinColumn(name = "COFDEU", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    //Prioriza moneda producto
    @Column(name = "PRIMPR", length = 1, nullable = false)
    private String priorizaMonedaProducto;

    //Incluye impuestos
    @Column(name = "INCIMP", length = 1, nullable = false)
    private String incluyeImpuestos;

    @Column(name = "CAPRUT", length = 1)
    private String calculaPrecioDesdeUtilidad;

    //Incluye impuestos
    @Column(name = "UTILID", nullable = false)
    private Integer utilidadParaCalcularPrecio;

    //Precio maximo
    @Column(name = "PRMXPR", precision = 15, scale = 4)
    private BigDecimal precioMaximo;
    //Precio minimo
    @Column(name = "PRMIPR", precision = 15, scale = 4)
    private BigDecimal precioMinimo;

    @Embedded
    private Auditoria auditoria;

//    @OneToMany(mappedBy = "listaDePrecio", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<ItemListaPrecioVenta> itemPrecios;
    public ListaPrecioVenta() {

        this.incluyeImpuestos = "N";
        this.precioMinimo = BigDecimal.ZERO;
        this.precioMaximo = BigDecimal.ZERO;
        this.auditoria = new Auditoria();
        this.calculaPrecioDesdeUtilidad = "N";
        this.utilidadParaCalcularPrecio = 0;
    }

    public ListaPrecioVenta(String codigo) {

        this.codigo = codigo;
        this.incluyeImpuestos = "N";
        this.precioMinimo = BigDecimal.ZERO;
        this.precioMaximo = BigDecimal.ZERO;
        this.auditoria = new Auditoria();
        this.calculaPrecioDesdeUtilidad = "N";
        this.utilidadParaCalcularPrecio = 0;
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

    public String getIncluyeImpuestos() {
        return incluyeImpuestos;
    }

    public void setIncluyeImpuestos(String incluyeImpuestos) {
        this.incluyeImpuestos = incluyeImpuestos;
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

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

//    public List<ItemListaPrecioVenta> getItemPrecios() {
//        return itemPrecios;
//    }
//
//    public void setItemPrecios(List<ItemListaPrecioVenta> itemPrecios) {
//        this.itemPrecios = itemPrecios;
//    }
    public String getPriorizaMonedaProducto() {
        return priorizaMonedaProducto;
    }

    public void setPriorizaMonedaProducto(String priorizaMonedaProducto) {
        this.priorizaMonedaProducto = priorizaMonedaProducto;
    }

    public Integer getUtilidadParaCalcularPrecio() {
        return utilidadParaCalcularPrecio;
    }

    public void setUtilidadParaCalcularPrecio(Integer utilidadParaCalcularPrecio) {
        this.utilidadParaCalcularPrecio = utilidadParaCalcularPrecio;
    }

    public String getCalculaPrecioDesdeUtilidad() {
        return calculaPrecioDesdeUtilidad;
    }

    public void setCalculaPrecioDesdeUtilidad(String calculaPrecioDesdeUtilidad) {
        this.calculaPrecioDesdeUtilidad = calculaPrecioDesdeUtilidad;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final ListaPrecioVenta other = (ListaPrecioVenta) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ListaDePrecio{" + "codigo=" + codigo + '}';
    }

}
