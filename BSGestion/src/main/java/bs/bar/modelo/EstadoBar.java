/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.bar.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "br_estado")
@EntityListeners(AuditoriaListener.class)
@XmlRootElement
public class EstadoBar implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CODIGO", nullable = false, length = 2)
    private String codigo;
    @Basic(optional = false)
    @Column(name = "DESCRP", nullable = false, length = 50)
    private String descripcion;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "COLOR", nullable = false, length = 100)
    private String color;

    @Embedded
    private Auditoria auditoria;

    public EstadoBar() {

        this.auditoria = new Auditoria();
    }

    public EstadoBar(String codigo) {
        this.codigo = codigo;
    }

    public EstadoBar(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigo != null ? codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EstadoBar)) {
            return false;
        }
        EstadoBar other = (EstadoBar) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "isd.resto.modelo.RT_EstadoMovimiento[codigo=" + codigo + "]";
    }

}
