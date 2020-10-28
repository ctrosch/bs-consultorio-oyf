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
public class CircuitoTallerPK implements Serializable {

    private String circom;
    private String cirapl;

    public CircuitoTallerPK() {
    }

    public CircuitoTallerPK(String circom, String cirapl) {
        this.circom = circom;
        this.cirapl = cirapl;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (circom != null ? circom.hashCode() : 0);
        hash += (cirapl != null ? cirapl.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CircuitoTallerPK)) {
            return false;
        }
        CircuitoTallerPK other = (CircuitoTallerPK) object;
        if ((this.circom == null && other.circom != null) || (this.circom != null && !this.circom.equals(other.circom))) {
            return false;
        }
        if ((this.cirapl == null && other.cirapl != null) || (this.cirapl != null && !this.cirapl.equals(other.cirapl))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.service.modelo.CircuitoPK[ circom=" + circom + ", cirapl=" + cirapl + " ]";
    }
    
}
