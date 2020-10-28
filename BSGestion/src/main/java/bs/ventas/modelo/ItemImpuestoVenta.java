/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.modelo;

import bs.global.modelo.TipoImpuesto;
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
public class ItemImpuestoVenta extends ItemMovimientoVenta {

    @Column(name = "BASIMP", precision = 15, scale = 4)
    private BigDecimal baseImponible;

    @Column(name = "ALICUO", precision = 15, scale = 4)
    private BigDecimal alicuota;

    @Transient
    private TipoImpuesto tipoImpuesto;

    @Transient
    private String editaImporte;

    public ItemImpuestoVenta() {

        super();
        alicuota = BigDecimal.ZERO;
        baseImponible = BigDecimal.ZERO;
    }

    public TipoImpuesto getTipoImpuesto() {
        return tipoImpuesto;
    }

    public void setTipoImpuesto(TipoImpuesto tipoImpuesto) {
        this.tipoImpuesto = tipoImpuesto;
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

    @Override
    public String toString() {
        return super.toString() + "ItemImpuestoVenta{" + "baseImponible=" + baseImponible + ", alicuota=" + alicuota + ", tipoImpuesto=" + tipoImpuesto + ", editaImporte=" + editaImporte + '}';
    }

}
