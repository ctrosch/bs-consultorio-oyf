/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
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

/**
 *
 * @author Claudio
 */
@Entity
@Table(name="st_rubro04")
@EntityListeners(AuditoriaListener.class)
@IdClass(RubroPK.class)
public class Rubro04 implements Serializable, IAuditableEntity{
    
    @Id
    @Column(name = "CODIGO", nullable = false, length = 30)
    private String codigo;
        
    @Id
    @Column(name="TIPPRO", length=6)
    private String tippro;
    
    @Column(name="DESCRP", length=60)
    private String descripcion;
    
    @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO", nullable = false, insertable=false, updatable=false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoProducto tipoProducto;
    
    @Embedded
    private Auditoria auditoria;    

    public Rubro04() {
    }

    public Rubro04(String codigo, String tippro) {
        this.codigo = codigo;
        this.tippro = tippro;
    }
 
    
    public String getTippro() {
        return tippro;
    }

    public void setTippro(String tippro) {
        this.tippro = tippro;
    }
   
    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }  
    
    public Auditoria getAuditoria() {
        return auditoria;
    }

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
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        hash = 11 * hash + (this.tippro != null ? this.tippro.hashCode() : 0);
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
        final Rubro04 other = (Rubro04) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        if ((this.tippro == null) ? (other.tippro != null) : !this.tippro.equals(other.tippro)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tippro=" + tippro + ",codigo=" + codigo;
    }
    
}