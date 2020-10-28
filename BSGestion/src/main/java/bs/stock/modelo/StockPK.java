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
public class StockPK implements Serializable {

    private String artcod;
    private String deposi;
    private String atributo1;
    private String atributo2;
    private String atributo3;
    private String atributo4;
    private String atributo5;
    private String atributo6;
    private String atributo7;    


    public StockPK() {

    }

    public StockPK(String artcod, String deposi,String atributo1, String atributo2,String atributo3,String atributo4,String atributo5,String atributo6,String atributo7) {
        
        this.artcod = artcod;
        this.deposi = deposi;
        this.atributo1 = atributo1;
        this.atributo2 = atributo2;
        this.atributo3 = atributo3;
        this.atributo4 = atributo4;
        this.atributo5 = atributo5;
        this.atributo6 = atributo6;
        this.atributo7 = atributo7;
        
    }

    public String getCodigo() {
        return artcod;
    }

    public void setArtcod(String artcod) {
        this.artcod = artcod;
    }

    public String getDeposi() {
        return deposi;
    }

    public void setDeposi(String deposi) {
        this.deposi = deposi;
    }

    public String getAtributo1() {
        return atributo1;
    }

    public void setAtributo1(String atributo1) {
        this.atributo1 = atributo1;
    }

    public String getAtributo2() {
        return atributo2;
    }

    public void setAtributo2(String atributo2) {
        this.atributo2 = atributo2;
    }

    public String getAtributo3() {
        return atributo3;
    }

    public void setAtributo3(String atributo3) {
        this.atributo3 = atributo3;
    }

    public String getAtributo4() {
        return atributo4;
    }

    public void setAtributo4(String atributo4) {
        this.atributo4 = atributo4;
    }

    public String getAtributo5() {
        return atributo5;
    }

    public void setAtributo5(String atributo5) {
        this.atributo5 = atributo5;
    }

    public String getAtributo6() {
        return atributo6;
    }

    public void setAtributo6(String atributo6) {
        this.atributo6 = atributo6;
    }

    public String getAtributo7() {
        return atributo7;
    }

    public void setAtributo7(String atributo7) {
        this.atributo7 = atributo7;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.artcod != null ? this.artcod.hashCode() : 0);
        hash = 71 * hash + (this.deposi != null ? this.deposi.hashCode() : 0);
        hash = 71 * hash + (this.atributo1 != null ? this.atributo1.hashCode() : 0);
        hash = 71 * hash + (this.atributo2 != null ? this.atributo2.hashCode() : 0);
        hash = 71 * hash + (this.atributo3 != null ? this.atributo3.hashCode() : 0);
        hash = 71 * hash + (this.atributo4 != null ? this.atributo4.hashCode() : 0);
        hash = 71 * hash + (this.atributo5 != null ? this.atributo5.hashCode() : 0);
        hash = 71 * hash + (this.atributo6 != null ? this.atributo6.hashCode() : 0);
        hash = 71 * hash + (this.atributo7 != null ? this.atributo7.hashCode() : 0);
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
        final StockPK other = (StockPK) obj;
        if ((this.artcod == null) ? (other.artcod != null) : !this.artcod.equals(other.artcod)) {
            return false;
        }
        if ((this.deposi == null) ? (other.deposi != null) : !this.deposi.equals(other.deposi)) {
            return false;
        }
        if ((this.atributo1 == null) ? (other.atributo1 != null) : !this.atributo1.equals(other.atributo1)) {
            return false;
        }
        if ((this.atributo2 == null) ? (other.atributo2 != null) : !this.atributo2.equals(other.atributo2)) {
            return false;
        }
        if ((this.atributo3 == null) ? (other.atributo3 != null) : !this.atributo3.equals(other.atributo3)) {
            return false;
        }
        if ((this.atributo4 == null) ? (other.atributo4 != null) : !this.atributo4.equals(other.atributo4)) {
            return false;
        }
        if ((this.atributo5 == null) ? (other.atributo5 != null) : !this.atributo5.equals(other.atributo5)) {
            return false;
        }
        if ((this.atributo6 == null) ? (other.atributo6 != null) : !this.atributo6.equals(other.atributo6)) {
            return false;
        }
        if ((this.atributo7 == null) ? (other.atributo7 != null) : !this.atributo7.equals(other.atributo7)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StockPK{" + "artcod=" + artcod + ", deposi=" + deposi + ", atributo1=" + atributo1 + ", atributo2=" + atributo2 + ", atributo3=" + atributo3 + ", atributo4=" + atributo4 + ", atributo5=" + atributo5 + ", atributo6=" + atributo6 + ", atributo7=" + atributo7 + '}';
    }

    

}
