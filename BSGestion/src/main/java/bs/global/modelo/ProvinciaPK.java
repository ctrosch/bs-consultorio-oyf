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

public class ProvinciaPK implements Serializable{

    
    private String codpai;    
    private String codigo;

    public ProvinciaPK() {

    }

    public ProvinciaPK(String codpai, String codigo) {
        this.codpai = codpai;
        this.codigo = codigo;
    }
    
    public String getCodpai() {
        return codpai;
    }

    public void setCodpai(String codpai) {
        this.codpai = codpai;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.codpai != null ? this.codpai.hashCode() : 0);
        hash = 61 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final ProvinciaPK other = (ProvinciaPK) obj;
        if ((this.codpai == null) ? (other.codpai != null) : !this.codpai.equals(other.codpai)) {
            return false;
        }
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ProvinciaPK{" + "codpai=" + codpai + ", codigo=" + codigo + '}';
    }    
    
}
