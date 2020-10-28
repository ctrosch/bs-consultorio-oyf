/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.global.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */

@Entity
@Table(name="gr_moneda_valores")
@EntityListeners(AuditoriaListener.class)
@IdClass(MonedaValoresPK.class)
public class MonedaValores implements Serializable, IAuditableEntity {

    @Id        
    @Column(name = "CODCOF", nullable = false, length = 6)
    private String codcof;
        
    @Id            
    @Column(name = "FCHALT", length = 10)
    private String fechaAlta;

    @Column(name = "COTIZ", nullable = false, precision=10, scale=4)
    private BigDecimal cotizacion;
    
    @Transient 
    private Date fecha;
        
    @Embedded
    private Auditoria auditoria;

    public MonedaValores() {
        
        this.cotizacion = new BigDecimal(BigInteger.ZERO);
        this.auditoria = new Auditoria();
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public String getCodcof() {
        return codcof;
    }

    public void setCodcof(String codcof) {
        this.codcof = codcof;
    }

    public Date getFecha() {
        
        if(fechaAlta==null || fechaAlta.isEmpty()){
            return null;
        }
            
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {         
            fecha = format.parse(fechaAlta);
        } catch (ParseException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.codcof != null ? this.codcof.hashCode() : 0);
        hash = 71 * hash + (this.fechaAlta != null ? this.fechaAlta.hashCode() : 0);
        return hash;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MonedaValores other = (MonedaValores) obj;
        if ((this.codcof == null) ? (other.codcof != null) : !this.codcof.equals(other.codcof)) {
            return false;
        }
        if (this.fechaAlta != other.fechaAlta && (this.fechaAlta == null || !this.fechaAlta.equals(other.fechaAlta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MonedaValores{" + "codcof=" + codcof + ", fechaAlta=" + fechaAlta + '}';
    }
    
    


}
