/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.stock.modelo;

import java.io.Serializable;
import javax.persistence.Embeddable;


/**
 *
 * @author ctrosch
 */

//@Embeddable
public class RubroPK implements Serializable {

    private String codigo;        
    private String tippro;

    public RubroPK() {
        
    }

    public RubroPK(String tippro,String codigo) {
        this.codigo = codigo;
        this.tippro = tippro;
    }
    
    

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTippro() {
        return tippro;
    }

    public void setTippro(String tippro) {
        this.tippro = tippro;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        hash = 59 * hash + (this.tippro != null ? this.tippro.hashCode() : 0);
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
        final RubroPK other = (RubroPK) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        if ((this.tippro == null) ? (other.tippro != null) : !this.tippro.equals(other.tippro)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RubroPK{" + "codigo=" + codigo + ", tippro=" + tippro + '}';
    }
    
}
