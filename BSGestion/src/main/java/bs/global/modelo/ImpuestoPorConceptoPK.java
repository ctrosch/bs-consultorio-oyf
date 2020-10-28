/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.modelo;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author ctrosch
 */
@Embeddable
public class ImpuestoPorConceptoPK implements Serializable {

    private String modulo;
    private String tipo;
    private String codigo;
    private String codimp;

    public ImpuestoPorConceptoPK() {

    }

    public ImpuestoPorConceptoPK(String modulo, String tipo, String codigo, String codimp) {
        this.modulo = modulo;
        this.tipo = tipo;
        this.codigo = codigo;
        this.codimp = codimp;
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

    public String getCodimp() {
        return codimp;
    }

    public void setCodimp(String codimp) {
        this.codimp = codimp;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.modulo != null ? this.modulo.hashCode() : 0);
        hash = 97 * hash + (this.tipo != null ? this.tipo.hashCode() : 0);
        hash = 97 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        hash = 97 * hash + (this.codimp != null ? this.codimp.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ImpuestoPorConceptoPK other = (ImpuestoPorConceptoPK) obj;
        if ((this.modulo == null) ? (other.modulo != null) : !this.modulo.equals(other.modulo)) {
            return false;
        }
        if ((this.tipo == null) ? (other.tipo != null) : !this.tipo.equals(other.tipo)) {
            return false;
        }
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        if ((this.codimp == null) ? (other.codimp != null) : !this.codimp.equals(other.codimp)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ImpuestoPorConceptoPK{" + "modulo=" + modulo + ", tipo=" + tipo + ", codigo=" + codigo + ", tipimp=" + codimp + '}';
    }

}
