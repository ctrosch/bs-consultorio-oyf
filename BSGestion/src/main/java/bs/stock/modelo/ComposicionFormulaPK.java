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
//@Embeddable
public class ComposicionFormulaPK implements Serializable {
    
    private String artcod;    
    private String codfor;

    public ComposicionFormulaPK() {
    }

    public ComposicionFormulaPK(String artcod, String codfor) {
        
        this.artcod = artcod;
        this.codfor = codfor;
    }

    public String getArtcod() {
        return artcod;
    }

    public void setArtcod(String artcod) {
        this.artcod = artcod;
    }

    public String getCodfor() {
        return codfor;
    }

    public void setCodfor(String codfor) {
        this.codfor = codfor;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.artcod != null ? this.artcod.hashCode() : 0);
        hash = 79 * hash + (this.codfor != null ? this.codfor.hashCode() : 0);
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
        final ComposicionFormulaPK other = (ComposicionFormulaPK) obj;
        if ((this.artcod == null) ? (other.artcod != null) : !this.artcod.equals(other.artcod)) {
            return false;
        }
        if ((this.codfor == null) ? (other.codfor != null) : !this.codfor.equals(other.codfor)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ComprosicionFormulaPK{" + "artcod=" + artcod + ", codfor=" + codfor + '}';
    }
    
    

    
}
