/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.ventas.modelo;

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
 * @author ctrosch
 * Tabla: VTTVND
 *
 */
@Entity
@Table(name="vt_vendedor")
@EntityListeners(AuditoriaListener.class)
public class Vendedor implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;

    @Id    
    @Column(name="codigo", length = 6)
    private String codigo;

    @Column(name="NOMBRE", length=60)
    private String descripcion;

    @Column(name="NROTEL", length=30)
    private String nroTelefono;

    @Column(name="CAMAIL", length=120)
    private String email;
    
    @Embedded
    private Auditoria auditoria;

    public Vendedor() {        
        
        auditoria = new Auditoria();
    }

    public Vendedor(String codigo) {
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

    public String getNroTelefono() {
        return nroTelefono;
    }

    public void setNroTelefono(String nroTelefono) {
        this.nroTelefono = nroTelefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final Vendedor other = (Vendedor) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Vendedor{" + "codigo=" + codigo + '}';
    }

}
