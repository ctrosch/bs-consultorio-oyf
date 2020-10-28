/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.modelo;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

/**
 *
 * @author ctrosch
 */
@Embeddable
public class ItemCondicionPagoVentaPK implements Serializable {

    private String cndpag;

    private Integer cuotas;

    public ItemCondicionPagoVentaPK() {

    }

    public ItemCondicionPagoVentaPK(String cndpag, Integer cuotas) {
        this.cndpag = cndpag;
        this.cuotas = cuotas;
    }

    public String getCndpag() {
        return cndpag;
    }

    public void setCndpag(String cndpag) {
        this.cndpag = cndpag;
    }

    public Integer getCuotas() {
        return cuotas;
    }

    public void setCuotas(Integer cuotas) {
        this.cuotas = cuotas;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final ItemCondicionPagoVentaPK other = (ItemCondicionPagoVentaPK) obj;
        if (!Objects.equals(this.cndpag, other.cndpag)) {
            return false;
        }
        if (!Objects.equals(this.cuotas, other.cuotas)) {
            return false;
        }
        return true;
    }

}
