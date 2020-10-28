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
 *
 */
   
@Entity
@Table(name="en_tipo")
@EntityListeners(AuditoriaListener.class)
public class TipoEntidad implements Serializable {
    
    //CLIENTE(1), PROVEEDOR(2), TRANSPORTE(3), BANCO(4),OTRO(9);
    
    @Id    
    @Column(name="codigo", length = 1)
    private String codigo;
    
    @Column(name="DESCRP", length=60)
    private String descripcion;
    
    @Embedded
    private Auditoria auditoria;

    public TipoEntidad() {
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
        int hash = 5;
        hash = 23 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final TipoEntidad other = (TipoEntidad) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TipoEntidad{" + "codigo=" + codigo + '}';
    }
    
}

