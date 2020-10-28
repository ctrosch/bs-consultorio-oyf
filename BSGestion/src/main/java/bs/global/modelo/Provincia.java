/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.global.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name="gr_provincia")
@EntityListeners(AuditoriaListener.class)
@IdClass(ProvinciaPK.class)
@XmlRootElement
public class Provincia implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;

    @Id    
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo; 
    
    @Id        
    @Column(name = "CODPAI", nullable = false, length = 6)
    private String codpai;
    
    @Column(name="DESCRP", length=60)
    private String descripcion;
    
    @JoinColumn(name = "CODPAI", referencedColumnName = "CODIGO", nullable = false, insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Pais pais;
        
    @Embedded
    private Auditoria auditoria;

    public Provincia() {
        
        this.auditoria = new Auditoria();        
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getCodpai() {
        return codpai;
    }

    public void setCodpai(String codpai) {
        this.codpai = codpai;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
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
        hash = 71 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        hash = 71 * hash + (this.codpai != null ? this.codpai.hashCode() : 0);
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
        final Provincia other = (Provincia) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        if ((this.codpai == null) ? (other.codpai != null) : !this.codpai.equals(other.codpai)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "codpai=" + codpai +",codigo=" + codigo ;
    }
    
}
