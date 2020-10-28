/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.modelo;

import bs.entidad.modelo.Contacto;
import bs.entidad.modelo.EntidadComercial;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
@Table(name = "tl_equipo")
@EntityListeners(AuditoriaListener.class)
@XmlRootElement
public class Equipo implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 13)
    @Column(nullable = false, length = 13)
    private String codigo;    
    
    @Size(max = 255)
    @Column(name = "DESCRP", nullable = false, length = 20)
    private String descripcion;
    @Size(max = 20)
    @Column(length = 20)
    private String nserie;
    
    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA")
    @ManyToOne
    private EntidadComercial entidadComercial;
    
    @JoinColumn(name = "CONTAC", referencedColumnName = "ID")
    @ManyToOne
    private Contacto contacto;
    
    @JoinColumn(name = "CODMOD", referencedColumnName = "CODIGO")
    @ManyToOne
    private EquipoModelo modelo;
    
    @JoinColumn(name = "CODMAR", referencedColumnName = "CODIGO")
    @ManyToOne
    private EquipoMarca marca;
    
    @JoinColumn(name = "CODTIP", referencedColumnName = "CODIGO")
    @ManyToOne
    private EquipoTipo tipo;
    
    @Embedded
    private Auditoria auditoria;

    public Equipo() {
        
        this.auditoria = new Auditoria();
    }

    public Equipo(String codigo) {
        this.codigo = codigo;
        this.auditoria = new Auditoria();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNserie() {
        return nserie;
    }

    public void setNserie(String nserie) {
        this.nserie = nserie;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EntidadComercial getEntidadComercial() {
        return entidadComercial;
    }

    public void setEntidadComercial(EntidadComercial entidadComercial) {
        this.entidadComercial = entidadComercial;
    }
    
    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    public EquipoModelo getModelo() {
        return modelo;
    }

    public void setModelo(EquipoModelo modelo) {
        this.modelo = modelo;
    }

    public EquipoMarca getMarca() {
        return marca;
    }

    public void setMarca(EquipoMarca marca) {
        this.marca = marca;
    }

    public EquipoTipo getTipo() {
        return tipo;
    }

    public void setTipo(EquipoTipo tipo) {
        this.tipo = tipo;
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
        
        return (codigo!=null?codigo:"")+" - "+(descripcion!=null?descripcion:"");
        
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
        if (!(object instanceof Equipo)) {
            return false;
        }
        Equipo other = (Equipo) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.taller.modelo.Equipo[ codigo=" + codigo + " ]";
    }
    
}
