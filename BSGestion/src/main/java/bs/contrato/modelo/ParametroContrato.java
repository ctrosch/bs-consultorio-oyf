/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.contrato.modelo;

import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author ctrosch
 */
//@Entity
//@Table(name = "cv_parametro")
//@EntityListeners(AuditoriaListener.class)
public class ParametroContrato implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="codigo", length=6)
    private String codigo;

    @Column(name="FCHDES", length=60)
    @Temporal(TemporalType.DATE)
    private Date fechaDesde;
    
    @Column(name="FCHHAS", length=60)
    @Temporal(TemporalType.DATE)
    private Date fechaHasta;
    
    @Column(name="1QUINCE")
    private int diaPrimerQuincena;
    
    @Column(name="2QUINCE")
    private int diaSegundaQuincena;
    
    @Embedded
    private Auditoria auditoria;
    
    public ParametroContrato() {
        
        this.auditoria = new Auditoria();        
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public int getDiaPrimerQuincena() {
        return diaPrimerQuincena;
    }

    public void setDiaPrimerQuincena(int diaPrimerQuincena) {
        this.diaPrimerQuincena = diaPrimerQuincena;
    }

    public int getDiaSegundaQuincena() {
        return diaSegundaQuincena;
    }

    public void setDiaSegundaQuincena(int diaSegundaQuincena) {
        this.diaSegundaQuincena = diaSegundaQuincena;
    }
    
    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ParametroContrato other = (ParametroContrato) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }
    
    @Override
    public String toString() {
        return "tv.producto.modelo.TipoProducto[id=" + codigo + "]";
    }

}
