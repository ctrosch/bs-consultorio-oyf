/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.facturacion.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Comprobante;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 *
 * @author Claudio
 */
@Entity
@DiscriminatorValue("FC")
@EntityListeners(AuditoriaListener.class)
public class ItemCircuitoFacturacion extends ItemCircuito {

    @JoinColumns({
    @JoinColumn(name = "MODULO", referencedColumnName = "MODCOM", nullable = false,insertable=false, updatable=false),
    @JoinColumn(name = "CODCOM", referencedColumnName = "CODCOM", nullable = false,insertable=false, updatable=false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobante;

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

}
