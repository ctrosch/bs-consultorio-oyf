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
@Table(name = "ws_parametro")
@EntityListeners(AuditoriaListener.class)
public class ParametroWS implements Serializable, IAuditableEntity {
    private static final long serialVersionUID = 1L;
    
    @Id    
    @Column(name = "CODIGO", nullable = false, length = 6)
    private String codigo;    
    
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;   
    
    @Column(name = "MODPRB", nullable = false, length = 1)
    private String modoPrueba;   
    
    // I-Individual - G-General
    @Column(name = "TIPAUT", nullable = false, length = 1)
    private String tipoAutorizacion;   
    
    @Column(name = "CUITWS", nullable = false, length = 11)
    private String cuitWS; 
    
    @Lob
    @Column(name = "PATHCE",length = 2147483647)
    private String pathCertificado;    
    
    @Lob
    @Column(name = "PATHKY",length = 2147483647)
    private String pathKey;
    
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
    
    public ParametroWS(){
        
        codigo = "01";
        descripcion = "Parametros WSAA";
        modoPrueba = "S";
        tipoAutorizacion = "G";        
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

    public String getTipoAutorizacion() {
        return tipoAutorizacion;
    }

    public void setTipoAutorizacion(String tipoAutorizacion) {
        this.tipoAutorizacion = tipoAutorizacion;
    }
    
    public String getPathCertificado() {
        return pathCertificado;
    }

    public void setPathCertificado(String pathCertificado) {
        this.pathCertificado = pathCertificado;
    }

    public String getPathKey() {
        return pathKey;
    }

    public void setPathKey(String pathKey) {
        this.pathKey = pathKey;
    }

    public String getModoPrueba() {
        return modoPrueba;
    }

    public void setModoPrueba(String modoPrueba) {
        this.modoPrueba = modoPrueba;
    }

    public String getCuitWS() {
        return cuitWS;
    }

    public void setCuitWS(String cuitWS) {
        this.cuitWS = cuitWS;
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
        final ParametroWS other = (ParametroWS) obj;
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
