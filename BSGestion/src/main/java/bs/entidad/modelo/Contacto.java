/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Localidad;
import bs.global.modelo.Provincia;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author "Claudio Trosch"
 */
@Entity
@Table(name = "en_contacto")
@EntityListeners(AuditoriaListener.class)
public class Contacto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NOMBRE", length = 120)
    private String nombre;

    @Column(name = "DIRECC", length = 120)
    private String direccion;

    @Column(name = "BARRIO", length = 100)
    private String barrio;

    @Column(name = "DIREML", length = 250)
    private String email;

    @Column(name = "TELEFN", length = 30)
    private String telefono;

    @Column(name = "CELULA", length = 30)
    private String celular;
    @Lob
    @Column(name = "NOTAS", length = 2147483647)
    private String notas;

    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial entidadComercial;

    @JoinColumns({
        @JoinColumn(name = "CODPRO", referencedColumnName = "CODIGO"),
        @JoinColumn(name = "CODPAI", referencedColumnName = "CODPAI")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Provincia provincia;

    @JoinColumn(name = "CODLOC", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Localidad localidad;

    @JoinColumn(name = "SECTOR", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private ContactoSector sector;

    @Embedded
    private Auditoria auditoria;

    public Contacto() {
        this.auditoria = new Auditoria();
    }

    public Contacto(Integer id) {
        this.id = id;
        this.auditoria = new Auditoria();
    }

    public Contacto(Integer id, String debaja) {
        this.id = id;
        this.auditoria = new Auditoria();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public EntidadComercial getEntidadComercial() {
        return entidadComercial;
    }

    public void setEntidadComercial(EntidadComercial entidadComercial) {
        this.entidadComercial = entidadComercial;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    public ContactoSector getSector() {
        return sector;
    }

    public void setSector(ContactoSector sector) {
        this.sector = sector;
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Contacto)) {
            return false;
        }
        Contacto other = (Contacto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.entidad.modelo.Contacto[ id=" + id + " ]";
    }

}
