/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.wsafip.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * @author Claudio
 */
@Entity
@Table(name = "ws_web_service")
@EntityListeners(AuditoriaListener.class)
public class WebServices implements Serializable,IAuditableEntity {
    private static final long serialVersionUID = 1L;
    
    @Id    
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;    
    
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;   
  
    @Lob
    @Column(name = "WSDLPR",length = 2147483647)
    private String wsdlProduccion; 
    
    @Lob
    @Column(name = "WSDLHO",length = 2147483647)
    private String wsdlPrueba; 
    
    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones; 
        
    @Embedded
    private Auditoria auditoria;
    
    public WebServices(){
        
        auditoria = new Auditoria();
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

    public String getWsdlProduccion() {
        return wsdlProduccion;
    }

    public void setWsdlProduccion(String wsdlProduccion) {
        this.wsdlProduccion = wsdlProduccion;
    }

    public String getWsdlPrueba() {
        return wsdlPrueba;
    }

    public void setWsdlPrueba(String wsdlPrueba) {
        this.wsdlPrueba = wsdlPrueba;
    }
    
    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final WebServices other = (WebServices) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WebServices{" + "codigo=" + codigo + '}';
    }

    
}
