/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.ventas.modelo;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author ctrosch
 */

@Embeddable
public class ItemPendienteCuentaCorrientePK implements Serializable {
    
    private Integer id;    
    private int cuotas;

    public ItemPendienteCuentaCorrientePK() {
    }

    public ItemPendienteCuentaCorrientePK(Integer id, int cuotas) {
        this.id = id;
        this.cuotas = cuotas;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getCuotas() {
        return cuotas;
    }

    public void setCuotas(int cuotas) {
        this.cuotas = cuotas;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 17 * hash + this.cuotas;
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
        final ItemPendienteCuentaCorrientePK other = (ItemPendienteCuentaCorrientePK) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if (this.cuotas != other.cuotas) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemPendienteCuentaCorrientePK{" + "id=" + id + ", cuotas=" + cuotas + '}';
    }
    
}
