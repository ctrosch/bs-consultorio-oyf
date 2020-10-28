/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.tesoreria.modelo;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author Claudio
 */
@Embeddable
public class SucursalBancoPK implements Serializable{
    
    private String codigo;    
    private String codigoBanco;

    public SucursalBancoPK() {
        
    }

    public SucursalBancoPK(String codigo, String codigoBanco) {
        this.codigo = codigo;
        this.codigoBanco = codigoBanco;
    }
    
    
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoBanco() {
        return codigoBanco;
    }

    public void setCodigoBanco(String codigoBanco) {
        this.codigoBanco = codigoBanco;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final SucursalBancoPK other = (SucursalBancoPK) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SucursalBancoPK{" + "codigo=" + codigo + ", codigoBanco=" + codigoBanco + '}';
    }

    
}
