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
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name="gr_localidad")
@EntityListeners(AuditoriaListener.class)
public class Localidad implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;
   
    @Id
    @Column(name = "ID", nullable = false)
    private Integer id;
        
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;
    
    @Column(name="DESCRP", length=60)
    private String descripcion;    
    
    @JoinColumn(name = "CODPAI", referencedColumnName = "CODIGO", nullable = false,insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Pais pais;
    
    @JoinColumns({
        @JoinColumn(name = "CODPRO", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "CODPAI", referencedColumnName = "CODPAI", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Provincia provincia;
        
    @Embedded
    private Auditoria auditoria;

    public Localidad() {
        
        this.auditoria = new Auditoria();
    }

    public Localidad(String codigo, String codpai) {
        this.codigo = codigo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
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

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }
    
    public String getDescripcionComplete(){        
        return (codigo!=null?codigo+" - ":"")+(descripcion!=null?descripcion+" - ":"")+(provincia!=null?provincia.getDescripcion():"");        
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final Localidad other = (Localidad) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Localidad{" + "id=" + id + '}';
    }
    
}
