/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.vista;

import bs.entidad.modelo.EntidadComercial;
import bs.stock.modelo.*;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "fc_producto")
public class ProductoFacturacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CODIGO", nullable = false, length = 30)
    private String codigo;

    @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoProducto tipoProducto;

    @Column(name = "DESCRP", nullable = false, length = 120)
    private String descripcion;
    
    @Column(name = "CODPRO", nullable = false, length = 40)
    private String codigoProveedor;

    @Column(name = "NROPAR", nullable = false, length = 40)
    private String numeroParte;

    //Unidad de medida
    @JoinColumn(name = "UNIMED", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadDeMedida;

    @JoinColumn(name = "PROHAB", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial proveedorHabitual;

    @JoinColumns({
        @JoinColumn(name = "RUBR01", referencedColumnName = "CODIGO", nullable = false, insertable = true, updatable = true),
        @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Rubro01 rubr01;

    @JoinColumns({
        @JoinColumn(name = "RUBR02", referencedColumnName = "CODIGO", nullable = false, insertable = true, updatable = true),
        @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Rubro02 rubr02;

    @Column(name = "CODCPT")
    private String codigoConceptoVenta;

    @Column(name = "CAMPO_1")
    private String campo_1;

    @Column(name = "CAMPO_2")
    private String campo_2;

    @Column(name = "CAMPO_3")
    private String campo_3;

    @Column(name = "CAMPO_4")
    private String campo_4;

    @Column(name = "CAMPO_5")
    private String campo_5;

    @Column(name = "CAMPO_6")
    private String campo_6;

    @Column(name = "CAMPO_7")
    private String campo_7;

    @Column(name = "CAMPO_8")
    private String campo_8;

    @Column(name = "CAMPO_9")
    private String campo_9;

    @Transient
    private List<AplicacionProducto> aplicaciones;

    @Transient
    private List<AtributoProducto> atributos;

   

    public ProductoFacturacion() {

    }

    public ProductoFacturacion(String artcod) {
        this.codigo = artcod;

    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public UnidadMedida getUnidadDeMedida() {
        return unidadDeMedida;
    }

    public void setUnidadDeMedida(UnidadMedida unidadDeMedida) {
        this.unidadDeMedida = unidadDeMedida;
    }

    public EntidadComercial getProveedorHabitual() {
        return proveedorHabitual;
    }

    public void setProveedorHabitual(EntidadComercial proveedorHabitual) {
        this.proveedorHabitual = proveedorHabitual;
    }

    public Rubro01 getRubr01() {
        return rubr01;
    }

    public void setRubr01(Rubro01 rubr01) {
        this.rubr01 = rubr01;
    }

    public Rubro02 getRubr02() {
        return rubr02;
    }

    public void setRubr02(Rubro02 rubr02) {
        this.rubr02 = rubr02;
    }

    public String getCampo_1() {
        return campo_1;
    }

    public void setCampo_1(String campo_1) {
        this.campo_1 = campo_1;
    }

    public String getCampo_2() {
        return campo_2;
    }

    public void setCampo_2(String campo_2) {
        this.campo_2 = campo_2;
    }

    public String getCampo_3() {
        return campo_3;
    }

    public void setCampo_3(String campo_3) {
        this.campo_3 = campo_3;
    }

    public String getCampo_5() {
        return campo_5;
    }

    public void setCampo_5(String campo_5) {
        this.campo_5 = campo_5;
    }

    public String getCampo_4() {
        return campo_4;
    }

    public void setCampo_4(String campo_4) {
        this.campo_4 = campo_4;
    }

    public String getCampo_6() {
        return campo_6;
    }

    public void setCampo_6(String campo_6) {
        this.campo_6 = campo_6;
    }

    public String getCampo_7() {
        return campo_7;
    }

    public void setCampo_7(String campo_7) {
        this.campo_7 = campo_7;
    }

    public String getCampo_8() {
        return campo_8;
    }

    public void setCampo_8(String campo_8) {
        this.campo_8 = campo_8;
    }

    public String getCampo_9() {
        return campo_9;
    }

    public void setCampo_9(String campo_9) {
        this.campo_9 = campo_9;
    }

    public List<AplicacionProducto> getAplicaciones() {
        return aplicaciones;
    }

    public void setAplicaciones(List<AplicacionProducto> aplicaciones) {
        this.aplicaciones = aplicaciones;
    }

    public String getNumeroParte() {
        return numeroParte;
    }

    public void setNumeroParte(String numeroParte) {
        this.numeroParte = numeroParte;
    }

    public String getCodigoConceptoVenta() {
        return codigoConceptoVenta;
    }

    public void setCodigoConceptoVenta(String codigoConceptoVenta) {
        this.codigoConceptoVenta = codigoConceptoVenta;
    }

    public String getCodigoProveedor() {
        return codigoProveedor;
    }

    public void setCodigoProveedor(String codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final ProductoFacturacion other = (ProductoFacturacion) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Producto{" + "codigo=" + codigo + "}";
    }

}
