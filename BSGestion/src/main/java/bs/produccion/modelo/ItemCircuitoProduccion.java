/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.produccion.modelo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author ctrosch
 *
 * Circuito de producción
 *
 */
@Entity
@Table(name = "pd_circuito_item")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipcir", discriminatorType = DiscriminatorType.STRING, length = 2)
@IdClass(ItemCircuitoProduccionPK.class)
public abstract class ItemCircuitoProduccion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "circom", nullable = false, length = 6)
    private String circom;
    @Id
    @Column(name = "cirapl", nullable = false, length = 6)
    private String cirapl;
    @Id
    @Column(name = "modulo", nullable = false, length = 2)
    private String modulo;
    @Id
    @Column(name = "codcom", nullable = false, length = 6)
    private String codcom;

    @JoinColumns({
        @JoinColumn(name = "circom", referencedColumnName = "circom", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "cirapl", referencedColumnName = "cirapl", nullable = false, insertable = false, updatable = false)

    })
    @ManyToOne(fetch = FetchType.LAZY)
    CircuitoProduccion circuito;

    @Column(name = "observa", length = 60)
    private String observaciones;

    public ItemCircuitoProduccion() {

    }

    public String getCirapl() {
        return cirapl;
    }

    public void setCirapl(String cirapl) {
        this.cirapl = cirapl;
    }

    public String getCircom() {
        return circom;
    }

    public void setCircom(String circom) {
        this.circom = circom;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getCodcom() {
        return codcom;
    }

    public void setCodcom(String codcom) {
        this.codcom = codcom;
    }

    public CircuitoProduccion getCircuito() {
        return circuito;
    }

    public void setCircuito(CircuitoProduccion circuito) {
        this.circuito = circuito;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.cirapl != null ? this.cirapl.hashCode() : 0);
        hash = 73 * hash + (this.circom != null ? this.circom.hashCode() : 0);
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
        final ItemCircuitoProduccion other = (ItemCircuitoProduccion) obj;
        if ((this.cirapl == null) ? (other.cirapl != null) : !this.cirapl.equals(other.cirapl)) {
            return false;
        }
        if ((this.circom == null) ? (other.circom != null) : !this.circom.equals(other.circom)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemCircuitoProduccion{" + "cirapl=" + cirapl + ", circom=" + circom + '}';
    }

}
