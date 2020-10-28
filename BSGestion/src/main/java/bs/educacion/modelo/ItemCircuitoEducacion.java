/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Comprobante;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
 * @author Claudio
 */
@Entity
@Table(name = "ed_circuito_item")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "MODULO", discriminatorType = DiscriminatorType.STRING, length = 2)
@IdClass(ItemCircuitoEducacionPK.class)
@EntityListeners(AuditoriaListener.class)
public abstract class ItemCircuitoEducacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CIRAPL", nullable = false, length = 6)
    private String cirapl;
    @Id
    @Column(name = "CIRCOM", nullable = false, length = 6)
    private String circom;
    @Id
    @Column(name = "MODULO", nullable = false, length = 2)
    private String modulo;
    @Id
    @Column(name = "CODCOM", nullable = false, length = 6)
    private String codcom;

    @JoinColumns({
        @JoinColumn(name = "CIRAPL", referencedColumnName = "CIRAPL", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "CIRCOM", referencedColumnName = "CIRCOM", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    CircuitoEducacion circuito;

    @JoinColumns({
        @JoinColumn(name = "MODULO", referencedColumnName = "MODCOM", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "CODCOM", referencedColumnName = "CODCOM", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobante;

    @Column(name = "OBSERVA", length = 60)
    private String observaciones;

    @Embedded
    private Auditoria auditoria;

    public ItemCircuitoEducacion() {

        this.auditoria = new Auditoria();

    }

    public ItemCircuitoEducacion(String cirapl, String circom, String modulo, String codcom) {
        this.cirapl = cirapl;
        this.circom = circom;
        this.modulo = modulo;
        this.codcom = codcom;
        this.auditoria = new Auditoria();
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

    public CircuitoEducacion getCircuito() {
        return circuito;
    }

    public void setCircuito(CircuitoEducacion circuito) {
        this.circuito = circuito;
    }

    public String getCodcom() {
        return codcom;
    }

    public void setCodcom(String codcom) {
        this.codcom = codcom;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.cirapl != null ? this.cirapl.hashCode() : 0);
        hash = 97 * hash + (this.circom != null ? this.circom.hashCode() : 0);
        hash = 97 * hash + (this.modulo != null ? this.modulo.hashCode() : 0);
        hash = 97 * hash + (this.codcom != null ? this.codcom.hashCode() : 0);
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
        final ItemCircuitoEducacion other = (ItemCircuitoEducacion) obj;
        if ((this.cirapl == null) ? (other.cirapl != null) : !this.cirapl.equals(other.cirapl)) {
            return false;
        }
        if ((this.circom == null) ? (other.circom != null) : !this.circom.equals(other.circom)) {
            return false;
        }
        if ((this.modulo == null) ? (other.modulo != null) : !this.modulo.equals(other.modulo)) {
            return false;
        }
        if ((this.codcom == null) ? (other.codcom != null) : !this.codcom.equals(other.codcom)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemCircuito{" + "cirapl=" + cirapl + ", circom=" + circom + ", modulo=" + modulo + ", codcom=" + codcom + '}';
    }

}
