/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.proveedores.modelo;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@DiscriminatorValue("R")
public class ItemRetencionProveedor extends ItemMovimientoProveedor {
    
    @Transient
    private String codigoTipoImpuesto;
    
    @Transient
    private String editaImporte;

    public String getCodigoTipoImpuesto() {
        return codigoTipoImpuesto;
    }

    public void setCodigoTipoImpuesto(String codigoTipoImpuesto) {
        this.codigoTipoImpuesto = codigoTipoImpuesto;
    }

    public String getEditaImporte() {
        return editaImporte;
    }

    public void setEditaImporte(String editaImporte) {
        this.editaImporte = editaImporte;
    }

}
