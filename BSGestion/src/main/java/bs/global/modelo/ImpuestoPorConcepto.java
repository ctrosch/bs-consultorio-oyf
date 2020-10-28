/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Table;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "gr_concepto_impuesto")
@EntityListeners(AuditoriaListener.class)
@IdClass(ImpuestoPorConceptoPK.class)
public class ImpuestoPorConcepto implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "MODCPT", nullable = false, length = 2)
    private String modulo;
    @Id
    @Column(name = "TIPCPT", nullable = false, length = 1)
    private String tipo;
    @Id
    @Column(name = "CODCPT", nullable = false, length = 6)
    private String codigo;
    @Id
    @Column(name = "CODIMP", nullable = false, length = 6)
    private String codimp;

    @JoinColumns({
        @JoinColumn(name = "MODCPT", referencedColumnName = "MODULO", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "TIPCPT", referencedColumnName = "TIPCPT", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "CODCPT", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Concepto concepto;

    @JoinColumn(name = "CODIMP", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoImpuesto tipoImpuesto;

    @Column(name = "TASAIM", nullable = false, precision = 15, scale = 5)
    private BigDecimal tasa;

    @Embedded
    private Auditoria auditoria;

    public ImpuestoPorConcepto() {
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

    public String getCodimp() {
        return codimp;
    }

    public void setCodimp(String codimp) {
        this.codimp = codimp;
    }

    public BigDecimal getTasa() {
        return tasa;
    }

    public void setTasa(BigDecimal tasa) {
        this.tasa = tasa;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

    public TipoImpuesto getTipoImpuesto() {
        return tipoImpuesto;
    }

    public void setTipoImpuesto(TipoImpuesto tipoImpuesto) {
        this.tipoImpuesto = tipoImpuesto;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.modulo != null ? this.modulo.hashCode() : 0);
        hash = 97 * hash + (this.tipo != null ? this.tipo.hashCode() : 0);
        hash = 97 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        hash = 97 * hash + (this.codimp != null ? this.codimp.hashCode() : 0);
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
        final ImpuestoPorConcepto other = (ImpuestoPorConcepto) obj;
        if ((this.modulo == null) ? (other.modulo != null) : !this.modulo.equals(other.modulo)) {
            return false;
        }
        if ((this.tipo == null) ? (other.tipo != null) : !this.tipo.equals(other.tipo)) {
            return false;
        }
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        if ((this.codimp == null) ? (other.codimp != null) : !this.codimp.equals(other.codimp)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ImpuestoPorConcepto{" + "modulo=" + modulo + ", tipo=" + tipo + ", codigo=" + codigo + ", codimp=" + codimp + '}';
    }

}
