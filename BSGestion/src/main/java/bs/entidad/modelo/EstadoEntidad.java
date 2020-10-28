/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.entidad.modelo;

import bs.global.auditoria.AuditoriaListener;
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
 * @author ctrosch
 * Tabla: VTTVND
 *
 */
@Entity
@Table(name="en_estado")
@EntityListeners(AuditoriaListener.class)
public class EstadoEntidad implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id    
    @Column(name="codigo", length = 1)
    private String codigo;
    
    @Column(name="DESCRP", length=60)
    private String descripcion;
    
    @Embedded
    private Auditoria auditoria;

    public EstadoEntidad() {
        this.auditoria = new Auditoria();
    }

    public EstadoEntidad(String codigo) {
        this.codigo = codigo;        
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
        int hash = 5;
        hash = 89 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final EstadoEntidad other = (EstadoEntidad) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EstadoEntidad{" + "codigo=" + codigo + '}';
    }
   
}
