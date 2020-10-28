/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.mantenimiento.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author GUILLERMO
 */
@Entity
@Table(name = "mt_tipo_mantenimiento")
@EntityListeners(AuditoriaListener.class)
public class TipoMantenimiento implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "CODIGO", nullable = false, length = 10)
    private String codigo;
    @Size(max = 120)
    @Column(name = "DESCRP", length = 120)
    private String descripcion;

    @Embedded
    private Auditoria auditoria;

    public TipoMantenimiento() {

        this.auditoria = new Auditoria();
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
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

    @Override
    public TipoMantenimiento clone() throws CloneNotSupportedException {
        return (TipoMantenimiento) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.codigo);
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
        final TipoMantenimiento other = (TipoMantenimiento) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.mantenimiento.modelo.TipoMantenimiento[ codigo=" + codigo + " ]";
    }

}
