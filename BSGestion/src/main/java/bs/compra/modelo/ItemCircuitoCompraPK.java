/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.compra.modelo;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author Claudio
 */
@Embeddable
public class ItemCircuitoCompraPK implements Serializable {
        
    private String cirapl;    
    private String circom;    
    private String modulo;
    private String codcom;

    public ItemCircuitoCompraPK() {
        
    }

    public ItemCircuitoCompraPK(String fctciiCirapl, String fctciiCircom, String fctciiModulo, String fctciiCodcom) {
        this.cirapl = fctciiCirapl;
        this.circom = fctciiCircom;
        this.modulo = fctciiModulo;
        this.codcom = fctciiCodcom;
    }

    public String getCirapl() {
        return cirapl;
    }

    public void setCirapl(String cirapl) {
        this.cirapl = cirapl;
    }

    public String getCircom() {
        return circom;
    }

    public void setCircom(String circom) {
        this.circom = circom;
    }

    public String getCodcom() {
        return codcom;
    }

    public void setCodcom(String codcom) {
        this.codcom = codcom;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cirapl != null ? cirapl.hashCode() : 0);
        hash += (circom != null ? circom.hashCode() : 0);
        hash += (modulo != null ? modulo.hashCode() : 0);
        hash += (codcom != null ? codcom.hashCode() : 0);
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
        final ItemCircuitoCompraPK other = (ItemCircuitoCompraPK) obj;
        if ((this.cirapl == null) ? (other.cirapl != null) : !this.cirapl.equals(other.cirapl)) {
            return false;
        }
        if ((this.circom == null) ? (other.circom != null) : !this.circom.equals(other.circom)) {
            return false;
        }
        if ((this.modulo == null) ? (other.modulo != null) : !this.modulo.equals(other.modulo)) {
            return false;
        }
        if ((this.codcom == null) ? (other.codcom != null) : !this.codcom.equals(other.codcom)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemCircuitoCompraPK{" + "cirapl=" + cirapl + ", circom=" + circom + ", modulo=" + modulo + ", codcom=" + codcom + '}';
    }

}
