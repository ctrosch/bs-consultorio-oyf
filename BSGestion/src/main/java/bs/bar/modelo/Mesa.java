/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "br_mesa")
@EntityListeners(AuditoriaListener.class)
@XmlRootElement
public class Mesa implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CODIGO", nullable = false, length = 3)
    private String codigo;
    @Column(name = "DESCRP", length = 50)
    private String descripcion;

    @JoinColumn(name = "CODSAL", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne
    private Salon salon;

    @JoinColumn(name = "CODMOZ", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne
    private Mozo mozo;

    @Embedded
    private Auditoria auditoria;

    @Transient
    MovimientoBar movimiento;

    public Mesa() {

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

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public Mozo getMozo() {
        return mozo;
    }

    public void setMozo(Mozo mozo) {
        this.mozo = mozo;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public MovimientoBar getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoBar movimiento) {
        this.movimiento = movimiento;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.codigo);
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
        final Mesa other = (Mesa) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Mesa{" + "codigo=" + codigo + '}';
    }

}
