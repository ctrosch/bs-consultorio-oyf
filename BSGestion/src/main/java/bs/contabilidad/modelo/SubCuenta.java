/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.modelo;

import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "cg_subcuenta")
@XmlRootElement
public class SubCuenta implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "CODIGO", nullable = false, length = 20)
    private String codigo;
    
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "DESCRP", nullable = false, length = 150)
    private String descripcion;

    @JoinColumn(name = "CNTCOS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(optional = false)
    private CentroCosto centroCosto;
    
    
    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean conError;

    public SubCuenta() {
        
        this.auditoria = new Auditoria();
    }

    public SubCuenta(String codigo) {
        this.codigo = codigo;
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

    public CentroCosto getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(CentroCosto centroCosto) {
        this.centroCosto = centroCosto;
    }
    
    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }
    
    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }
    
    public String getDescripcionComplete() {
        return (codigo != null ? codigo + " - " : "") + (descripcion != null ? descripcion : "");
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
        if (!(object instanceof SubCuenta)) {
            return false;
        }
        SubCuenta other = (SubCuenta) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.contabilidad.modelo.Subcuenta[ codigo=" + codigo + " ]";
    }
    
}
