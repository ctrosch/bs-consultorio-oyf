package bs.produccion.vista;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author ctrosch
 */
@Embeddable
public class PendienteProduccionGrupoPK implements Serializable {

    private int id;
    private String tipitm;
    private String grupo;

    public PendienteProduccionGrupoPK() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipitm() {
        return tipitm;
    }

    public void setTipitm(String tipitm) {
        this.tipitm = tipitm;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.id;
        hash = 97 * hash + (this.tipitm != null ? this.tipitm.hashCode() : 0);
        hash = 97 * hash + (this.grupo != null ? this.grupo.hashCode() : 0);
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
        final PendienteProduccionGrupoPK other = (PendienteProduccionGrupoPK) obj;
        if (this.id != other.id) {
            return false;
        }
        if ((this.tipitm == null) ? (other.tipitm != null) : !this.tipitm.equals(other.tipitm)) {
            return false;
        }
        if ((this.grupo == null) ? (other.grupo != null) : !this.grupo.equals(other.grupo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PendienteProduccionGrupoPK{" + "id=" + id + ", tipitm=" + tipitm + ", grupo=" + grupo + '}';
    }

}
