/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "ed_circuito_codigo")
@EntityListeners(AuditoriaListener.class)
public class CodigoCircuitoEducacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;
    @Basic(optional = false)
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;

    @Embedded
    private Auditoria auditoria;

    public CodigoCircuitoEducacion() {

        this.auditoria = new Auditoria();
    }

    public CodigoCircuitoEducacion(String Circom) {
        this.codigo = Circom;
        this.auditoria = new Auditoria();
    }

    public CodigoCircuitoEducacion(String Circom, String Descrp) {
        this.codigo = Circom;
        this.descripcion = Descrp;
        this.auditoria = new Auditoria();
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
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
        if (!(object instanceof CodigoCircuitoEducacion)) {
            return false;
        }
        CodigoCircuitoEducacion other = (CodigoCircuitoEducacion) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CodigoCircuitoEducacion{" + "codigo=" + codigo + '}';
    }

}
