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
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author "Claudio Trosch"
 */
@Entity
@Table(name = "st_mascara")
@EntityListeners(AuditoriaListener.class)
@XmlRootElement
public class MascaraStock implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    
    @Id    
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name="codigo",nullable = false, length = 20)
    private String codigo;
    
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name="descrp",nullable = false, length = 150)
    private String descripcion;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mascara")
    private List<ItemMascaraStock> items;
    
    @Embedded
    private Auditoria auditoria;

    public MascaraStock(){
        this.auditoria = new Auditoria();
    }

    public MascaraStock(String codigo) {
        this.codigo = codigo;
        this.auditoria = new  Auditoria();
    }

    public MascaraStock(String codigo, String descrp, String debaja) {
        this.codigo = codigo;
        this.descripcion = descrp;
        this.auditoria = new  Auditoria();
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

    @XmlTransient
    public List<ItemMascaraStock> getItems() {
        return items;
    }

    public void setItems(List<ItemMascaraStock> items) {
        this.items = items;
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
        if (!(object instanceof MascaraStock)) {
            return false;
        }
        MascaraStock other = (MascaraStock) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.stock.modelo.MascaraStock[ codigo=" + codigo + " ]";
    }
    
}
