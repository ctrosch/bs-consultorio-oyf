/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author "Claudio Trosch"
 */
@Entity
@Table(name = "tl_equipo_modelo")
@EntityListeners(AuditoriaListener.class)
@XmlRootElement
public class EquipoModelo implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer codigo;    
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "DESCRP",length = 100)
    private String descripcion;
    
    @JoinColumn(name = "CODMAR", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(optional = false)
    private EquipoMarca marca;
    
    @Embedded
    private Auditoria auditoria;
    

    public EquipoModelo() {
        
        this.auditoria = new Auditoria();
    }

    public EquipoModelo(Integer codigo) {
        this.codigo = codigo;
        this.auditoria = new Auditoria();
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EquipoMarca getMarca() {
        return marca;
    }

    public void setMarca(EquipoMarca marca) {
        this.marca = marca;
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
        if (!(object instanceof EquipoModelo)) {
            return false;
        }
        EquipoModelo other = (EquipoModelo) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.taller.modelo.EquipoModelo[ codigo=" + codigo + " ]";
    }
    
}
