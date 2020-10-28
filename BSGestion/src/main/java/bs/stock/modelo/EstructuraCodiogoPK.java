/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.modelo;

import java.io.Serializable;

/**
 *
 * @author ctrosch
 */

public class EstructuraCodiogoPK implements Serializable {

    private String tippro;    
    private String prefijo;    
    private String nrorub;

    public EstructuraCodiogoPK() {

    }

    public EstructuraCodiogoPK(String tippro, String prefix, String nrorub) {
        this.tippro = tippro;
        this.prefijo = prefix;
        this.nrorub = nrorub;
    }

    public String getTippro() {
        return tippro;
    }

    public void setTippro(String tippro) {
        this.tippro = tippro;
    }

    public String getNrorub() {
        return nrorub;
    }

    public void setNrorub(String nrorub) {
        this.nrorub = nrorub;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }
    
    


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.tippro != null ? this.tippro.hashCode() : 0);
        hash = 89 * hash + (this.prefijo != null ? this.prefijo.hashCode() : 0);
        hash = 89 * hash + (this.nrorub != null ? this.nrorub.hashCode() : 0);
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
        final EstructuraCodiogoPK other = (EstructuraCodiogoPK) obj;
        if ((this.tippro == null) ? (other.tippro != null) : !this.tippro.equals(other.tippro)) {
            return false;
        }
        if ((this.prefijo == null) ? (other.prefijo != null) : !this.prefijo.equals(other.prefijo)) {
            return false;
        }
        if ((this.nrorub == null) ? (other.nrorub != null) : !this.nrorub.equals(other.nrorub)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EstructuraTipoProductoPK{" + "tippro=" + tippro + ", prefix=" + prefijo + ", nrorub=" + nrorub + '}';
    }
    
}
