/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.facturacion.modelo;

import java.io.Serializable;

/**
 *
 * @author Claudio
 */

public class CircuitoFacturacionPK implements Serializable {
    
    private String circom;    
    private String cirapl;

    public CircuitoFacturacionPK() {
    }

    public CircuitoFacturacionPK(String fctcihCircom, String fctcihCirapl) {
        this.circom = fctcihCircom;
        this.cirapl = fctcihCirapl;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CircuitoFacturacionPK other = (CircuitoFacturacionPK) obj;
        if ((this.circom == null) ? (other.circom != null) : !this.circom.equals(other.circom)) {
            return false;
        }
        if ((this.cirapl == null) ? (other.cirapl != null) : !this.cirapl.equals(other.cirapl)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.circom != null ? this.circom.hashCode() : 0);
        hash = 29 * hash + (this.cirapl != null ? this.cirapl.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "FC_CircuitoPK{" + "circom=" + circom + "cirapl=" + cirapl + '}';
    }

    

}
