/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.taller.modelo;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author Claudio
 */
@Embeddable
public class ItemCircuitoTallerPK implements Serializable {

    private String circom;
    private String cirapl;    
    private String modulo;    
    private String codcom;

    public ItemCircuitoTallerPK() {
    }

    public ItemCircuitoTallerPK(String circom, String cirapl, String modulo, String codcom) {
        this.circom = circom;
        this.cirapl = cirapl;
        this.modulo = modulo;
        this.codcom = codcom;
    }

    public String getCircom() {
        return circom;
    }

    public void setCircom(String circom) {
        this.circom = circom;
    }

    public String getCirapl() {
        return cirapl;
    }

    public void setCirapl(String cirapl) {
        this.cirapl = cirapl;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getCodcom() {
        return codcom;
    }

    public void setCodcom(String codcom) {
        this.codcom = codcom;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (circom != null ? circom.hashCode() : 0);
        hash += (cirapl != null ? cirapl.hashCode() : 0);
        hash += (modulo != null ? modulo.hashCode() : 0);
        hash += (codcom != null ? codcom.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemCircuitoTallerPK)) {
            return false;
        }
        ItemCircuitoTallerPK other = (ItemCircuitoTallerPK) object;
        if ((this.circom == null && other.circom != null) || (this.circom != null && !this.circom.equals(other.circom))) {
            return false;
        }
        if ((this.cirapl == null && other.cirapl != null) || (this.cirapl != null && !this.cirapl.equals(other.cirapl))) {
            return false;
        }
        if ((this.modulo == null && other.modulo != null) || (this.modulo != null && !this.modulo.equals(other.modulo))) {
            return false;
        }
        if ((this.codcom == null && other.codcom != null) || (this.codcom != null && !this.codcom.equals(other.codcom))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemCircuitoServicePK{" + "circom=" + circom + ", cirapl=" + cirapl + ", modulo=" + modulo + ", codcom=" + codcom + '}';
    }

}
