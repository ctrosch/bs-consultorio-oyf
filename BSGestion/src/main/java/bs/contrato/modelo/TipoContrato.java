/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contrato.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "cv_tipo_contrato")
@EntityListeners(AuditoriaListener.class)
public class TipoContrato implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Column(name = "CODIGO", length = 6)
    private String codigo;

    @Column(name = "DESCRP", length = 60)
    private String descripcion;

    @Embedded
    private Auditoria auditoria;

    public TipoContrato() {

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

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public TipoContrato clone() throws CloneNotSupportedException {
        return (TipoContrato) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TipoContrato other = (TipoContrato) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public String toString() {
        return "TipoContrato{" + "codigo=" + codigo + '}';
    }

}
