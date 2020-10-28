/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.modelo;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

/**
 *
 * @author GUILLERMO
 *
 */
@Embeddable
public class ActividadComercialPK implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codigo;
    private String codtip;

    public ActividadComercialPK() {
    }

    public ActividadComercialPK(String codigo, String codtip) {
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
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.codigo);
        hash = 83 * hash + Objects.hashCode(this.codtip);
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
        final ActividadComercialPK other = (ActividadComercialPK) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        if (!Objects.equals(this.codtip, other.codtip)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ActividadEntidadPK{" + "codigo=" + codigo + ", codtip=" + codtip + '}';
    }

}
