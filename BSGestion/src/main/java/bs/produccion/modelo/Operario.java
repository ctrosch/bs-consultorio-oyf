/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.modelo;

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

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "pd_operario", schema = "")
public class Operario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codigo", nullable = false, length = 6)
    private String codigo;
    @Column(name = "nombre", length = 60)
    private String nombre;

    @JoinColumn(name = "codtip", referencedColumnName = "codigo", nullable = false)
    @ManyToOne
    private TipoOperario tipo;

    @JoinColumn(name = "codpla", referencedColumnName = "codigo", nullable = false)
    @ManyToOne
    private Planta planta;

    @JoinColumn(name = "codsec", referencedColumnName = "codigo", nullable = false)
    @ManyToOne
    private Sector sector;

    @Embedded
    private Auditoria auditoria;

    public Operario() {

        auditoria = new Auditoria();

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

    public TipoOperario getTipo() {
        return tipo;
    }

    public void setTipo(TipoOperario tipo) {
        this.tipo = tipo;
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
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
        hash += (codigo != null ? codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Operario)) {
            return false;
        }
        Operario other = (Operario) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "isd.produccion.modelo.Operario[codigo=" + codigo + "]";
    }

}
