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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Claudio
 */
@Entity
@Table(name = "gr_unidad_negocio")
@EntityListeners(AuditoriaListener.class)
public class UnidadNegocio implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;
    @Column(name = "NOMBRE", length = 80)
    private String nombre;
    @Column(name = "EMAIL", length = 80)
    private String email;
    @Column(name = "TELEFONO", length = 80)
    private String telefono;
    @Column(name = "LOGO", length = 200)
    private String logo;

    @JoinColumn(name = "CODEMP", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Empresa empresa;

    @Embedded
    private Auditoria auditoria;

    public UnidadNegocio() {

        this.auditoria = new Auditoria();
    }

    public UnidadNegocio(String sucurs) {
        this.codigo = sucurs;
        this.auditoria = new Auditoria();
    }

    public UnidadNegocio(String sucurs, String descrp) {
        this.codigo = sucurs;
        this.auditoria = new Auditoria();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final UnidadNegocio other = (UnidadNegocio) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UnidadNegocio{" + "codigo=" + codigo + '}';
    }

}
