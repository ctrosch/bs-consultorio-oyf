/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.facturacion.vista;

import java.io.Serializable;

/**
 *
 * @author ctrosch
 */
//@Embeddable
public class PendienteFacturacionDetallePK implements Serializable {

    private Integer idItemAplicacion;    
    private Integer idMovimientoAplicacion;

    public Integer getIdItemAplicacion() {
        return idItemAplicacion;
    }

    public void setIdItemAplicacion(Integer idItemAplicacion) {
        this.idItemAplicacion = idItemAplicacion;
    }

    public Integer getIdMovimientoAplicacion() {
        return idMovimientoAplicacion;
    }

    public void setIdMovimientoAplicacion(Integer idMovimientoAplicacion) {
        this.idMovimientoAplicacion = idMovimientoAplicacion;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.idItemAplicacion != null ? this.idItemAplicacion.hashCode() : 0);
        hash = 89 * hash + (this.idMovimientoAplicacion != null ? this.idMovimientoAplicacion.hashCode() : 0);
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
        final PendienteFacturacionDetallePK other = (PendienteFacturacionDetallePK) obj;
        if (this.idItemAplicacion != other.idItemAplicacion && (this.idItemAplicacion == null || !this.idItemAplicacion.equals(other.idItemAplicacion))) {
            return false;
        }
        if (this.idMovimientoAplicacion != other.idMovimientoAplicacion && (this.idMovimientoAplicacion == null || !this.idMovimientoAplicacion.equals(other.idMovimientoAplicacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemPendienteFacturacionPK{" + "idItemAplicacion=" + idItemAplicacion + ", idMovimientoAplicacion=" + idMovimientoAplicacion + '}';
    }
    
    
}
