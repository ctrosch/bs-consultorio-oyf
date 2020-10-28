/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.modelo;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author ctrosch Tabla: VTTVND
 *
 */
@Embeddable
public class CategoriaPK implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codigo;
    private String codtip;

    public CategoriaPK() {
    }

    public CategoriaPK(String codigo, String codtip) {
        this.codigo = codigo;
        this.codtip = codtip;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodtip() {
        return codtip;
    }

    public void setCodtip(String codtip) {
        this.codtip = codtip;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        hash = 13 * hash + (this.codtip != null ? this.codtip.hashCode() : 0);
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
        final CategoriaPK other = (CategoriaPK) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        if ((this.codtip == null) ? (other.codtip != null) : !this.codtip.equals(other.codtip)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CateroriaPK{" + "codigo=" + codigo + ", codtip=" + codtip + '}';
    }

}
