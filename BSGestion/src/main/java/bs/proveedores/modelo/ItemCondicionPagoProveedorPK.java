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
public class ItemCondicionPagoProveedorPK implements Serializable {

    private String cndpag;

    private Integer cuotas;

    public ItemCondicionPagoProveedorPK() {
    }

    public ItemCondicionPagoProveedorPK(int cuotas, String cndpag) {
        this.cuotas = cuotas;
        this.cndpag = cndpag;
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
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cndpag == null) ? 0 : cndpag.hashCode());
        result = prime * result + ((cuotas == null) ? 0 : cuotas.hashCode());
        return result;
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
        ItemCondicionPagoProveedorPK other = (ItemCondicionPagoProveedorPK) obj;
        if (cndpag == null) {
            if (other.cndpag != null) {
                return false;
            }
        } else if (!cndpag.equals(other.cndpag)) {
            return false;
        }
        if (cuotas == null) {
            if (other.cuotas != null) {
                return false;
            }
        } else if (!cuotas.equals(other.cuotas)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.compras.modelo.ItemCondicionPagoProveedorPK[ cuotas=" + cuotas + ", cndpag=" + cndpag + " ]";
    }

}
