/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.modelo;

import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author "Claudio Trosch"
 */
@Entity
@Table(name = "pr_amortizacion")
@XmlRootElement
public class AmortizacionPrestamo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id    
    @Column(name = "CODIGO", nullable = false)
    private String codigo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "DESCRP", nullable = false, length = 100)
    private String descripcion;

    @Embedded
    private Auditoria auditoria;

    public AmortizacionPrestamo() {
        
        this.auditoria = new Auditoria();
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

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final AmortizacionPrestamo other = (AmortizacionPrestamo) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AmortizacionPrestamo{" + "codigo=" + codigo + '}';
    }
    

}
