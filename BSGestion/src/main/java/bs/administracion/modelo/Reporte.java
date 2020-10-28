/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.administracion.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
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
@Table(name = "ad_reporte")
@XmlRootElement
@EntityListeners(AuditoriaListener.class)
public class Reporte implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CODIGO", nullable = false)
    private String codigo;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "GRUPO", nullable = false, length = 100)
    private String grupo;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "PATH", nullable = false, length = 255)
    private String path;

    @Size(min = 3, max = 3)
    @Column(name = "ORIGEN", nullable = false, length = 3)
    private String origen;

    @JoinColumn(name = "MODULO", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Modulo modulo;

    @Embedded
    private Auditoria auditoria;

    public Reporte() {
        this.auditoria = new Auditoria();
        this.origen = "USR";
    }

    public Reporte(String codigo) {
        this.codigo = codigo;
        this.auditoria = new Auditoria();
        this.origen = "USR";
    }

    public Reporte(String codigo, String nombre, String path, String debaja) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.path = path;
        this.auditoria = new Auditoria();
        this.origen = "USR";
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getDescripcionComplete() {
        return (codigo != null ? codigo + " - " : "") + (nombre != null ? nombre : "");
    }

    @Override
    public Reporte clone() throws CloneNotSupportedException {
        return (Reporte) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final Reporte other = (Reporte) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.administracion.modelo.Vista[ codigo=" + codigo + " ]";
    }

}
