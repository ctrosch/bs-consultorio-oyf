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
public class FormularioPK implements Serializable {

    private String modfor;
    private String codigo;

    public FormularioPK() {

    }

    public FormularioPK(String modfor, String codfor) {
        this.modfor = modfor;
        this.codigo = codfor;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getModfor() {
        return modfor;
    }

    public void setModfor(String modfor) {
        this.modfor = modfor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FormularioPK other = (FormularioPK) obj;
        if ((this.modfor == null) ? (other.modfor != null) : !this.modfor.equals(other.modfor)) {
            return false;
        }
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.modfor != null ? this.modfor.hashCode() : 0);
        hash = 53 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "GR_FormularioPK{" + "modfor=" + modfor + "codfor=" + codigo + '}';
    }


}
