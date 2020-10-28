/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.global.modelo;

import java.io.Serializable;

/**
 *
 * @author ctrosch
 */

public class MonedaValoresPK implements Serializable{
    
    private String codcof;    
    private String fechaAlta;

    public MonedaValoresPK() {

    }

    public MonedaValoresPK(String codcof, String feccal) {
        this.codcof = codcof;
        this.fechaAlta = feccal;
    }

    public String getCodcof() {
        return codcof;
    }

    public void setCodcof(String codcof) {
        this.codcof = codcof;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.codcof != null ? this.codcof.hashCode() : 0);
        hash = 19 * hash + (this.fechaAlta != null ? this.fechaAlta.hashCode() : 0);
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
        final MonedaValoresPK other = (MonedaValoresPK) obj;
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
        return "MonedaValoresPK{" + "codcof=" + codcof + ", feccal=" + fechaAlta + '}';
    }
    
    
}
