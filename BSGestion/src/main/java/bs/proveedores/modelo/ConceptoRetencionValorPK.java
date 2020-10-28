/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.proveedores.modelo;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author Claudio
 */
@Embeddable
public class ConceptoRetencionValorPK implements Serializable {
        
    private String tipo;
    private String codigo;        
    private int nroitm;

    public ConceptoRetencionValorPK(){
        
    }

    public ConceptoRetencionValorPK(String tipo, String codigo, int nroitm) {
        this.tipo = tipo;
        this.codigo = codigo;        
        this.nroitm = nroitm;
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

    public int getNroitm() {
        return nroitm;
    }

    public void setNroitm(int nroitm) {
        this.nroitm = nroitm;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.tipo != null ? this.tipo.hashCode() : 0);
        hash = 61 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        hash = 61 * hash + this.nroitm;
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
        final ConceptoRetencionValorPK other = (ConceptoRetencionValorPK) obj;
        if ((this.tipo == null) ? (other.tipo != null) : !this.tipo.equals(other.tipo)) {
            return false;
        }
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        if (this.nroitm != other.nroitm) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConceptoRetencionValoresPK{" + "tipo=" + tipo + ", codigo=" + codigo + ", nroitm=" + nroitm + '}';
    }
    
    
    

}
