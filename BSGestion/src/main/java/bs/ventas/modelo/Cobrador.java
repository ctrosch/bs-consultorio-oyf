/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.ventas.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Localidad;
import java.io.Serializable;
import java.math.BigDecimal;
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

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "vt_cobrador")
@EntityListeners(AuditoriaListener.class)
public class Cobrador implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;    
    @Column(name = "NOMBRE", nullable = false, length = 60)
    private String descripcion;
    @Column(name = "PORCEN", precision = 15, scale = 4)
    private BigDecimal porcentaje;
    @Column(name = "LEGAJO")
    private Integer nrolegajo;
        
    
    @JoinColumn(name = "CODPOS", referencedColumnName = "ID", nullable = false)        
    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    private Localidad localidad;
       
    @Embedded
    private Auditoria auditoria;


    public Cobrador() {
        this.auditoria = new Auditoria();
    }
    
    public Cobrador(String codigo) {
        this.codigo = codigo;
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
    
    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Integer getNrolegajo() {
        return nrolegajo;
    }

    public void setNrolegajo(Integer nrolegajo) {
        this.nrolegajo = nrolegajo;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
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
        hash = 83 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cobrador other = (Cobrador) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Cobrador{" + "codigo=" + codigo + '}';
    }
    
    
}
