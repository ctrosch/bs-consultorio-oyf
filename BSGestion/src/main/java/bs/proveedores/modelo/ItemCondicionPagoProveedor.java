/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.modelo;

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
 * @author Claudio
 */
@Entity
@Table(name = "pv_condicion_pago_items")
@EntityListeners(AuditoriaListener.class)
@IdClass(ItemCondicionPagoProveedorPK.class)
public class ItemCondicionPagoProveedor implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CNDPAG", nullable = false, length = 6)
    private String cndpag;

    @Id
    @Column(name = "CUOTAS", nullable = false, scale = 10)
    private int cuotas;

    @JoinColumn(name = "CNDPAG", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionPagoProveedor condicionPago;

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

    public ItemCondicionPagoProveedor() {

        this.auditoria = new Auditoria();
        this.porcentaje = 0;
    }

    public String getCndpag() {
        return cndpag;
    }

    public void setCndpag(String cndpag) {
        this.cndpag = cndpag;
    }

    public CondicionPagoProveedor getCondicionPago() {
        return condicionPago;
    }

    public void setCondicionPago(CondicionPagoProveedor condicionPago) {
        this.condicionPago = condicionPago;
    }

    public int getCuotas() {
        return cuotas;
    }

    public void setCuotas(int cuotas) {
        this.cuotas = cuotas;
    }

    public int getDiasDePago() {
        return diasDePago;
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

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.condicionPago);
        hash = 37 * hash + this.diasDePago;
        hash = 37 * hash + Objects.hashCode(this.porcentaje);
        hash = 37 * hash + this.diasFijo;
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
        final ItemCondicionPagoProveedor other = (ItemCondicionPagoProveedor) obj;
        if (this.cuotas != other.cuotas) {
            return false;
        }
        if (this.diasDePago != other.diasDePago) {
            return false;
        }
        if (this.diasFijo != other.diasFijo) {
            return false;
        }
        if (!Objects.equals(this.condicionPago, other.condicionPago)) {
            return false;
        }
        if (!Objects.equals(this.porcentaje, other.porcentaje)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemCondicionPagoProveedor{" + "condicionPago=" + condicionPago + ", cuotas=" + cuotas + '}';
    }

}
