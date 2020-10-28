/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.seguridad.modelo;

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

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "sg_estado")
@EntityListeners(AuditoriaListener.class)
public class EstadoUsuario implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;
    @Id    
    @Column(name = "ID", nullable = false)    
    private Integer id;
    @Column(name = "DESCRP", length = 45)
    private String descripcion;
    
    @Embedded
    private Auditoria auditoria;

    public EstadoUsuario() {
        this.auditoria = new Auditoria();
    }

    public EstadoUsuario(Integer id) {
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
    
    
    public Auditoria getAuditoria() {
        return auditoria;
    }

   
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
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
        if (!(object instanceof EstadoUsuario)) {
            return false;
        }
        EstadoUsuario other = (EstadoUsuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "isd.seguridad.modelo.SecEstados[id=" + id + "]";
    }

   

}
