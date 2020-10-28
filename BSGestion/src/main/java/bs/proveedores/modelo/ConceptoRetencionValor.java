/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.proveedores.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "pv_retencion_concepto_valor")
@EntityListeners(AuditoriaListener.class)
@IdClass(ConceptoRetencionValorPK.class)
@XmlRootElement
public class ConceptoRetencionValor implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "TIPRET")
    private String tipo;
    
    @Id
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "CODRET")
    private String codigo;
       
    @Id
    @NotNull
    @Column(name = "NROITM")
    private int nroitm;
        
    @Column(name = "IMPDES", precision = 10,scale = 2)
    private BigDecimal importeDesde;
    @Column(name = "IMPHAS", precision = 10,scale = 2)
    private BigDecimal importeHasta;
    @Column(name = "IMPORT", precision = 10,scale = 2)
    private BigDecimal importeRetener;
    @Column(name = "PORCEN", precision = 10,scale = 2)
    private BigDecimal porcentajeRetener;
    @Column(name = "EXCEDE", precision = 10,scale = 2)
    private BigDecimal sobreExedenteDe;
    
    @JoinColumns({
        @JoinColumn(name = "TIPRET", referencedColumnName = "TIPRET", insertable = false, updatable = false),
        @JoinColumn(name = "CODRET", referencedColumnName = "CODRET", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private ConceptoRetencion concepto;
    
    @Embedded
    private Auditoria auditoria;

    public ConceptoRetencionValor() {
        this.auditoria = new Auditoria();
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

    public int getNroitm() {
        return nroitm;
    }

    public void setNroitm(int nroitm) {
        this.nroitm = nroitm;
    }

    public BigDecimal getImporteDesde() {
        return importeDesde;
    }

    public void setImporteDesde(BigDecimal importeDesde) {
        this.importeDesde = importeDesde;
    }

    public BigDecimal getImporteHasta() {
        return importeHasta;
    }

    public void setImporteHasta(BigDecimal importeHasta) {
        this.importeHasta = importeHasta;
    }

    public BigDecimal getImporteRetener() {
        return importeRetener;
    }

    public void setImporteRetener(BigDecimal importeRetener) {
        this.importeRetener = importeRetener;
    }

    public BigDecimal getPorcentajeRetener() {
        return porcentajeRetener;
    }

    public void setPorcentajeRetener(BigDecimal porcentajeRetener) {
        this.porcentajeRetener = porcentajeRetener;
    }

    public BigDecimal getSobreExedenteDe() {
        return sobreExedenteDe;
    }

    public void setSobreExedenteDe(BigDecimal sobreExedenteDe) {
        this.sobreExedenteDe = sobreExedenteDe;
    }

    public ConceptoRetencion getConcepto() {
        return concepto;
    }

    public void setConcepto(ConceptoRetencion concepto) {
        this.concepto = concepto;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.tipo != null ? this.tipo.hashCode() : 0);
        hash = 97 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        hash = 97 * hash + this.nroitm;
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
        final ConceptoRetencionValor other = (ConceptoRetencionValor) obj;
        if ((this.tipo == null) ? (other.tipo != null) : !this.tipo.equals(other.tipo)) {
            return false;
        }
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        if (this.nroitm != other.nroitm) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConceptoRetencionValores{" + "tipo=" + tipo + ", codigo=" + codigo + ", nroitm=" + nroitm + '}';
    }
    
}
