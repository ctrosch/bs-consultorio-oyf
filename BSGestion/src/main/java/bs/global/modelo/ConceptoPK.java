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

public class ConceptoPK implements Serializable {
    
    private String modulo;    
    private String tipo;    
    private String codigo;

    public ConceptoPK() {
        
    }

    public ConceptoPK(String modulo, String tipo, String codigo) {
        this.modulo = modulo;
        this.tipo = tipo;
        this.codigo = codigo;
    }
    
    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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
        hash = 31 * hash + (this.modulo != null ? this.modulo.hashCode() : 0);
        hash = 31 * hash + (this.tipo != null ? this.tipo.hashCode() : 0);
        hash = 31 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final ConceptoPK other = (ConceptoPK) obj;
        if ((this.modulo == null) ? (other.modulo != null) : !this.modulo.equals(other.modulo)) {
            return false;
        }
        if ((this.tipo == null) ? (other.tipo != null) : !this.tipo.equals(other.tipo)) {
            return false;
        }
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }
    
    

    @Override
    public String toString() {
        return "tv.global.modelo.ConceptoPK[grccohModcpt=" + modulo + ", grccohTipcpt=" + tipo + ", grccohCodcpt=" + codigo + "]";
    }

}
