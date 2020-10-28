/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author "Claudio Trosch"
 */
@Entity
@Table(name = "cg_mascara_item")
@EntityListeners(AuditoriaListener.class)
@XmlRootElement
public class ItemMascaraContabilidad implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @NotNull
    @Column(name = "NROITM", nullable = false)
    private int nroitm; 
        
    @NotNull
    @JoinColumn(name = "CODMAS", referencedColumnName = "CODIGO")
    @ManyToOne(optional = false)
    private MascaraContabilidad mascara;
    
    @NotNull
    @JoinColumn(name = "cuenta", referencedColumnName = "NROCTA")
    @ManyToOne(optional = false)
    private CuentaContable cuentaContable;
    
    @NotNull
    @Column(name="DEBHAB", length = 1)
    private String debeHaber;
        
    @Embedded
    private Auditoria auditoria;
    
    @Transient
    private boolean conError;

    public ItemMascaraContabilidad() {
        
        this.auditoria = new Auditoria();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNroitm() {
        return nroitm;
    }

    public void setNroitm(int nroitm) {
        this.nroitm = nroitm;
    }

    public MascaraContabilidad getMascara() {
        return mascara;
    }

    public void setMascara(MascaraContabilidad mascara) {
        this.mascara = mascara;
    }

    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public String getDebeHaber() {
        return debeHaber;
    }

    public void setDebeHaber(String debeHaber) {
        this.debeHaber = debeHaber;
    }
    
    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final ItemMascaraContabilidad other = (ItemMascaraContabilidad) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemMascaraContabilidad{" + "id=" + id + '}';
    }

}
