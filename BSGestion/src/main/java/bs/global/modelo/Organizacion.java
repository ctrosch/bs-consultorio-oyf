/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.modelo;

import bs.global.auditoria.AuditoriaListener;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "gr_organizacion")
@EntityListeners(AuditoriaListener.class)
public class Organizacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CODIGO", length = 6)
    private String codigo;
    @Column(name = "NOMBRE_FANTASIA", length = 80)
    private String nombreFantasia;

    @Column(name = "LOGO", length = 80)
    private String logo;
    @Column(name = "EMAIL", length = 80)
    private String email;
    @Column(name = "TELEFONO", length = 80)
    private String telefono;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organizacion", fetch = FetchType.LAZY)
    private List<Empresa> empresas;

    public Organizacion() {
    }

    public Organizacion(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombreFantasia() {
        return nombreFantasia;
    }

    public void setNombreFantasia(String nombreFantasia) {
        this.nombreFantasia = nombreFantasia;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public List<Empresa> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(List<Empresa> empresas) {
        this.empresas = empresas;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Organizacion other = (Organizacion) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Organizacion{" + "codigo=" + codigo + "nombreFantasia=" + nombreFantasia + '}';
    }

}
