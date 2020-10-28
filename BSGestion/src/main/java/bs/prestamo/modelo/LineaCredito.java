/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.modelo;

import bs.contabilidad.modelo.CuentaContable;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author "Claudio Trosch"
 */
@Entity
@Table(name = "pr_linea_credito")
@XmlRootElement
public class LineaCredito implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "DESCRP", nullable = false, length = 100)
    private String descripcion;
    
    @Column(name = "CODIGO", nullable = false, length = 5)
    private String codigo;

    @Column(name = "TNA", precision = 15, scale = 2)
    private double tasaNominalAnual;

    @Column(name = "TNAMOR", precision = 15, scale = 2)
    private double tasaNominalAnualMora;

    @JoinColumn(name = "CTACAP", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContableCapital;

    @JoinColumn(name = "CTAINT", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContableInteres;

    @JoinColumn(name = "CTAMOR", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContableInteresMora;

    @JoinColumn(name = "CTADES", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContableDescuentoInteres;

    @Embedded
    private Auditoria auditoria;

    public LineaCredito() {

        this.auditoria = new Auditoria();
    }

    public LineaCredito(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getTasaNominalAnual() {
        return tasaNominalAnual;
    }

    public void setTasaNominalAnual(double tasaNominalAnual) {
        this.tasaNominalAnual = tasaNominalAnual;
    }

    public double getTasaNominalAnualMora() {
        return tasaNominalAnualMora;
    }

    public void setTasaNominalAnualMora(double tasaNominalAnualMora) {
        this.tasaNominalAnualMora = tasaNominalAnualMora;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public CuentaContable getCuentaContableCapital() {
        return cuentaContableCapital;
    }

    public void setCuentaContableCapital(CuentaContable cuentaContableCapital) {
        this.cuentaContableCapital = cuentaContableCapital;
    }

    public CuentaContable getCuentaContableInteres() {
        return cuentaContableInteres;
    }

    public void setCuentaContableInteres(CuentaContable cuentaContableInteres) {
        this.cuentaContableInteres = cuentaContableInteres;
    }

    public CuentaContable getCuentaContableInteresMora() {
        return cuentaContableInteresMora;
    }

    public void setCuentaContableInteresMora(CuentaContable cuentaContableInteresMora) {
        this.cuentaContableInteresMora = cuentaContableInteresMora;
    }

    public CuentaContable getCuentaContableDescuentoInteres() {
        return cuentaContableDescuentoInteres;
    }

    public void setCuentaContableDescuentoInteres(CuentaContable cuentaContableDescuentoInteres) {
        this.cuentaContableDescuentoInteres = cuentaContableDescuentoInteres;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
        if (!(object instanceof LineaCredito)) {
            return false;
        }
        LineaCredito other = (LineaCredito) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.presupuesto.modelo.LineaCredito[ id=" + id + " ]";
    }

}
