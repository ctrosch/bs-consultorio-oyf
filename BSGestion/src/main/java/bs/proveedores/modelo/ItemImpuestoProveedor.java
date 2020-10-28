/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.proveedores.modelo;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@DiscriminatorValue("I")
public class ItemImpuestoProveedor extends ItemMovimientoProveedor {
    
    @Column(name = "BASIMP", precision = 15, scale = 4)
    private BigDecimal baseImponible;
    
    @Column(name = "ALICUO", precision = 15, scale = 4)
    private BigDecimal alicuota;
        
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

    public BigDecimal getBaseImponible() {
        return baseImponible;
    }

    public void setBaseImponible(BigDecimal baseImponible) {
        this.baseImponible = baseImponible;
    }

    public BigDecimal getAlicuota() {
        return alicuota;
    }

    public void setAlicuota(BigDecimal alicuota) {
        this.alicuota = alicuota;
    }
    
}
