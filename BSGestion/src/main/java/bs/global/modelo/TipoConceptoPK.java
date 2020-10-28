/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.global.modelo;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author Claudio
 */
@Embeddable
public class TipoConceptoPK implements Serializable {
    
    private String modulo;
    private String codigo;

    public TipoConceptoPK() {

    }

    public TipoConceptoPK(String modulo, String codigo) {
        this.modulo = modulo;
        this.codigo = codigo;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.modulo != null ? this.modulo.hashCode() : 0);
        hash = 97 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final TipoConceptoPK other = (TipoConceptoPK) obj;
        if ((this.modulo == null) ? (other.modulo != null) : !this.modulo.equals(other.modulo)) {
            return false;
        }
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modulo=" + modulo + ", codigo=" + codigo ;
    }
    
}
