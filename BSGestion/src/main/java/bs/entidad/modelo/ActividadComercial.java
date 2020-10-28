/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author GUILLERMO
 *
 *
 */
@Entity
@Table(name = "en_actividad")
@IdClass(ActividadComercialPK.class)
@EntityListeners(AuditoriaListener.class)
public class ActividadComercial implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CODIGO", length = 6)
    private String codigo;

    @Id
    @Column(name = "CODTIP", length = 1)
    private String codtip;

    @Column(name = "DESCRP", length = 200)
    private String descripcion;

    @JoinColumn(name = "CODTIP", referencedColumnName = "CODIGO", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TipoEntidad tipoEntidad;

    @Embedded
    private Auditoria auditoria;

    public ActividadComercial() {
        this.auditoria = new Auditoria();
    }

    public ActividadComercial(String codigo, String codtip) {
        this.codigo = codigo;
        this.codtip = codtip;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodtip() {
        return codtip;
    }

    public void setCodtip(String codtip) {
        this.codtip = codtip;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoEntidad getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(TipoEntidad tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.codigo);
        hash = 47 * hash + Objects.hashCode(this.codtip);
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
        final ActividadComercial other = (ActividadComercial) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        if (!Objects.equals(this.codtip, other.codtip)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "codigo=" + codigo + ", codtip=" + codtip;
    }

}
