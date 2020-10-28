/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
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
@Table(name = "st_unidad_medida")
@EntityListeners(AuditoriaListener.class)
public class UnidadMedida implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;
    
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;
    
    @Column(name = "APLDEC", length = 1)
    private String aplicaDecimanles;
    
    @Column(name = "CNTDEC")
    private Integer cantidadDecimales;
    
    @Column(name = "FORMAT", length = 1)
    private String formato;
    
    @Embedded
    private Auditoria auditoria;
    
    public UnidadMedida() {
        
        this.auditoria = new Auditoria();
    }

    public UnidadMedida(String codigo) {
        this.codigo = codigo;
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

    public String getAplicaDecimanles() {
        return aplicaDecimanles;
    }

    public void setAplicaDecimanles(String aplicaDecimanles) {
        this.aplicaDecimanles = aplicaDecimanles;
    }

    public Integer getCantidadDecimales() {
        return cantidadDecimales;
    }

    public void setCantidadDecimales(Integer cantidadDecimales) {
        this.cantidadDecimales = cantidadDecimales;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
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
        hash = 23 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final UnidadMedida other = (UnidadMedida) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UnidadMedida{" + "codigo=" + codigo + '}';
    }
    
    
}
