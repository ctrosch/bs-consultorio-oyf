/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "gr_tipo_impuesto")
@EntityListeners(AuditoriaListener.class)
public class TipoImpuesto implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;
    @Column(name = "DESCRP", length = 60)
    private String descripcion;
    @Column(name = "MINIMO")
    private Character minimo;
    @Column(name = "VALORM", precision = 15, scale = 4)
    private BigDecimal valorMinimo;

    @Embedded
    private Auditoria auditoria;

    public TipoImpuesto() {
        this.auditoria = new Auditoria();
        this.valorMinimo = BigDecimal.ZERO;

    }

    public TipoImpuesto(String codigo) {
        this.codigo = codigo;
        this.auditoria = new Auditoria();
        this.valorMinimo = BigDecimal.ZERO;
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

    public Character getMinimo() {
        return minimo;
    }

    public void setMinimo(Character minimo) {
        this.minimo = minimo;
    }

    public BigDecimal getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(BigDecimal valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final TipoImpuesto other = (TipoImpuesto) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TipoDeImpuesto{" + "codigo=" + codigo + '}';
    }

}
