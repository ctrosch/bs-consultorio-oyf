package bs.produccion.vista;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author ctrosch
 */
@Embeddable
public class PendienteProduccionDetallePK implements Serializable {
    
    private Integer idIapl;    
    private Integer idMapl;           
    

    public PendienteProduccionDetallePK() {
        
    }

    public Integer getIdIapl() {
        return idIapl;
    }

    public void setIdIapl(Integer idIapl) {
        this.idIapl = idIapl;
    }

    public Integer getIdMapl() {
        return idMapl;
    }

    public void setIdMapl(Integer idMapl) {
        this.idMapl = idMapl;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.idIapl != null ? this.idIapl.hashCode() : 0);
        hash = 97 * hash + (this.idMapl != null ? this.idMapl.hashCode() : 0);
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
        final PendienteProduccionDetallePK other = (PendienteProduccionDetallePK) obj;
        if (this.idIapl != other.idIapl && (this.idIapl == null || !this.idIapl.equals(other.idIapl))) {
            return false;
        }
        if (this.idMapl != other.idMapl && (this.idMapl == null || !this.idMapl.equals(other.idMapl))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PendienteProduccionDetallePK{" + "idIapl=" + idIapl + ", idMapl=" + idMapl + '}';
    }
        
}
