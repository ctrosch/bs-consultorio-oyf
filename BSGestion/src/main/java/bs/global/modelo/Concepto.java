/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.modelo;

import bs.contabilidad.modelo.CuentaContable;
import bs.global.auditoria.AuditoriaListener;
import bs.stock.modelo.Producto;
import bs.tesoreria.modelo.CuentaTesoreria;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "gr_concepto")
@IdClass(ConceptoPK.class)
@EntityListeners(AuditoriaListener.class)
public class Concepto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "MODULO", nullable = false, length = 2)
    private String modulo;

    @Id
    @Column(name = "TIPCPT", nullable = false, length = 1)
    private String tipo;

    @Id
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;

    @Basic(optional = false)
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;

    @Column(name = "MEDPAG", nullable = false, length = 60)
    private String medioPago;

    @Column(name = "PIDECH", nullable = false, length = 1)
    private String pideNumeroCheque;

    @Column(name = "PIDFCH", nullable = false, length = 1)
    private String pideFechaEmision;

    @Column(name = "PORCOM", precision = 15, scale = 4)
    private double porcentajeComision;

    @Column(name = "CONCOB", precision = 15, scale = 4)
    private String conceptoConbranza;

    @JoinColumns({
        @JoinColumn(name = "MODULO", referencedColumnName = "MODULO", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "TIPCPT", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoConcepto tipoConcepto;

    @JoinColumn(name = "CJTCTA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaTesoreria cuentaTesoreria;

    @JoinColumn(name = "CTACON", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContable;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "concepto", fetch = FetchType.LAZY)
    private List<ImpuestoPorConcepto> impuestos;

    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto producto;

    @Embedded
    private Auditoria auditoria;

//    @OneToMany
//    public List<ImpuestoPorConcepto> impuestos;
    public Concepto() {

        this.auditoria = new Auditoria();
        this.pideNumeroCheque = "N";
        this.pideFechaEmision = "N";
        this.conceptoConbranza = "N";

    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public List<ImpuestoPorConcepto> getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(List<ImpuestoPorConcepto> impuestos) {
        this.impuestos = impuestos;
    }

    public TipoConcepto getTipoConcepto() {
        return tipoConcepto;
    }

    public void setTipoConcepto(TipoConcepto tipoConcepto) {
        this.tipoConcepto = tipoConcepto;
    }

    public CuentaTesoreria getCuentaTesoreria() {
        return cuentaTesoreria;
    }

    public void setCuentaTesoreria(CuentaTesoreria cuentaTesoreriaDebe) {
        this.cuentaTesoreria = cuentaTesoreriaDebe;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public String getPideNumeroCheque() {
        return pideNumeroCheque;
    }

    public void setPideNumeroCheque(String pideNumeroCheque) {
        this.pideNumeroCheque = pideNumeroCheque;
    }

    public String getPideFechaEmision() {
        return pideFechaEmision;
    }

    public void setPideFechaEmision(String pideFechaEmision) {
        this.pideFechaEmision = pideFechaEmision;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public double getPorcentajeComision() {
        return porcentajeComision;
    }

    public void setPorcentajeComision(double porcentajeComision) {
        this.porcentajeComision = porcentajeComision;
    }

    public String getConceptoConbranza() {
        return conceptoConbranza;
    }

    public void setConceptoConbranza(String conceptoConbranza) {
        this.conceptoConbranza = conceptoConbranza;
    }

    @Override
    public Concepto clone() throws CloneNotSupportedException {
        return (Concepto) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.modulo != null ? this.modulo.hashCode() : 0);
        hash = 41 * hash + (this.tipo != null ? this.tipo.hashCode() : 0);
        hash = 41 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final Concepto other = (Concepto) obj;
        if ((this.modulo == null) ? (other.modulo != null) : !this.modulo.equals(other.modulo)) {
            return false;
        }
        if ((this.tipo == null) ? (other.tipo != null) : !this.tipo.equals(other.tipo)) {
            return false;
        }
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modulo=" + modulo + ",tipo=" + tipo + ",codigo=" + codigo;
    }

}
