package bs.produccion.modelo;

import bs.global.modelo.Comprobante;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 *
 * @author Claudio
 */
@Entity
@DiscriminatorValue("PR")
public class ItemCircuitoProduccionParteProceso extends ItemCircuitoProduccion {

    @JoinColumns({
        @JoinColumn(name = "modulo", referencedColumnName = "modcom", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "codcom", referencedColumnName = "codcom", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Comprobante comprobante;

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }
}
