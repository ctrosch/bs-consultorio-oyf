/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.modelo;

import java.io.Serializable;

/**
 *
 * @author Claudio
 */
//@Embeddable
public class ItemComposicionFormulaPK implements Serializable {
    
    private String artcod;
    private String codigo;    
    private int nroitm;

    public ItemComposicionFormulaPK() {
    }

    public ItemComposicionFormulaPK(String sttfoiArtcod, String sttfoiFormul, int sttfoiNroitm) {
        
        this.artcod = sttfoiArtcod;
        this.codigo = sttfoiFormul;
        this.nroitm = sttfoiNroitm;
    }

    public String getArtcod() {
        return artcod;
    }

    public void setArtcod(String artcod) {
        this.artcod = artcod;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getNroitm() {
        return nroitm;
    }

    public void setNroitm(int nroitm) {
        this.nroitm = nroitm;
    }

    @Override
    public int hashCode() {
        int hash = 0;        
        hash += (artcod != null ? artcod.hashCode() : 0);
        hash += (codigo != null ? codigo.hashCode() : 0);
        hash += (int) nroitm;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemComposicionFormulaPK)) {
            return false;
        }
        ItemComposicionFormulaPK other = (ItemComposicionFormulaPK) object;
        
        if ((this.artcod == null && other.artcod != null) || (this.artcod != null && !this.artcod.equals(other.artcod))) {
            return false;
        }
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        if (this.nroitm != other.nroitm) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemComposicionFormulaPK{" + "artcod=" + artcod + ", codigo=" + codigo + ", nroitm=" + nroitm + '}';
    }


}
