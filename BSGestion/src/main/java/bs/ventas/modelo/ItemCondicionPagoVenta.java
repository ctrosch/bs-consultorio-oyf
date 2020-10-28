/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "vt_condicion_pago_items")
@EntityListeners(AuditoriaListener.class)
@IdClass(ItemCondicionPagoVentaPK.class)
public class ItemCondicionPagoVenta implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CNDPAG", nullable = false, length = 6)
    private String cndpag;

    @Id
    @Column(name = "CUOTAS", nullable = false, scale = 10)
    private int cuotas;

    @JoinColumn(name = "CNDPAG", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDePagoVenta condicionPago;

    @Column(name = "DIAPAG")
    private int diasDePago;
    @Column(name = "PORCEN", scale = 4, precision = 6)
    private double porcentaje;
    @Column(name = "DIAFIJ")
    private int diasFijo;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean conError;

    public ItemCondicionPagoVenta() {

        this.auditoria = new Auditoria();
        this.porcentaje = 0;
    }

    public int getDiasDePago() {
        return diasDePago;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public void setDiasDePago(int diasDePago) {
        this.diasDePago = diasDePago;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public int getDiasFijo() {
        return diasFijo;
    }

    public void setDiasFijo(int diasFijo) {
        this.diasFijo = diasFijo;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    public String getCndpag() {
        return cndpag;
    }

    public void setCndpag(String cndpag) {
        this.cndpag = cndpag;
    }

    public CondicionDePagoVenta getCondicionPago() {
        return condicionPago;
    }

    public void setCondicionPago(CondicionDePagoVenta condicionPago) {
        this.condicionPago = condicionPago;
    }

    public int getCuotas() {
        return cuotas;
    }

    public void setCuotas(int cuotas) {
        this.cuotas = cuotas;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.cndpag);
        hash = 79 * hash + this.cuotas;
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
        final ItemCondicionPagoVenta other = (ItemCondicionPagoVenta) obj;
        if (this.cuotas != other.cuotas) {
            return false;
        }
        if (!Objects.equals(this.cndpag, other.cndpag)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemCondicionPagoVenta{" + "cndpag=" + cndpag + ", cuotas=" + cuotas + '}';
    }

}
