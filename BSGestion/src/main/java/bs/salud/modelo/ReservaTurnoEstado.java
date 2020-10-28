/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.salud.modelo;

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

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "sa_turno_estado")
@EntityListeners(AuditoriaListener.class)
public class ReservaTurnoEstado implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;

    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;

    @Embedded
    private Auditoria auditoria;

    public ReservaTurnoEstado() {

        this.auditoria = new Auditoria();
    }

    public ReservaTurnoEstado(String codigo) {
        this.codigo = codigo;
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
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.codigo);
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
        final ReservaTurnoEstado other = (ReservaTurnoEstado) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ReservaTurnoEstado{" + "codigo=" + codigo + '}';
    }

}
