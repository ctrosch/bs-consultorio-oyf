/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.tesoreria.modelo;

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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "cj_sucursal_banco")
@EntityListeners(AuditoriaListener.class)
@IdClass(SucursalBancoPK.class)
@XmlRootElement
public class SucursalBanco implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "CODIGO", nullable = false, length = 10)
    private String codigo;    
    
    @Id
    @Column(name = "CODBCO", nullable = false, length = 30)
    private String codigoBanco;
    
    @JoinColumn(name = "CODBCO", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Banco banco;
    
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;
    
    @Column(name = "CLRING", length = 1)
    private String clearing;
        
    @Embedded
    private Auditoria auditoria;
    
    public SucursalBanco() {
        
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoBanco() {
        return codigoBanco;
    }

    public void setCodigoBanco(String codigoBanco) {
        this.codigoBanco = codigoBanco;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getClearing() {
        return clearing;
    }

    public void setClearing(String clearing) {
        this.clearing = clearing;
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
        hash = 29 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        hash = 29 * hash + (this.codigoBanco != null ? this.codigoBanco.hashCode() : 0);
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
        final SucursalBanco other = (SucursalBanco) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        if ((this.codigoBanco == null) ? (other.codigoBanco != null) : !this.codigoBanco.equals(other.codigoBanco)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SucursalBanco{" + "codigo=" + codigo + ", codigoBanco=" + codigoBanco + '}';
    }

    
}
