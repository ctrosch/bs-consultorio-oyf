/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Claudio
 */
@Entity
@EntityListeners(AuditoriaListener.class)
@Table(name = "cg_movimiento_item_subcuenta")
@XmlRootElement
public class ItemMovimientoContabilidadSubcuenta implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "NROITM", nullable = false)
    private int nroItem;

    @NotNull
    @JoinColumn(name = "ID_IDET", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ItemMovimientoContabilidadCentroCosto itemCentroCosto;

    @JoinColumn(name = "SUBCTA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(optional = false)
    private SubCuenta subCuenta;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "DEBHAB", nullable = false, length = 1)
    private String debhab;

    @NotNull
    @Column(name = "IMPORT", nullable = false, precision = 15, scale = 4)
    private double importe;

    @Column(name = "PORCEN", nullable = false, precision = 15, scale = 2)
    private double porcentaje;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean conError;

    public ItemMovimientoContabilidadSubcuenta() {
        this.auditoria = new Auditoria();
    }

    public ItemMovimientoContabilidadSubcuenta(Integer id) {
        this.id = id;
        this.auditoria = new Auditoria();

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDebhab() {
        return debhab;
    }

    public void setDebhab(String debhab) {
        this.debhab = debhab;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    public int getNroItem() {
        return nroItem;
    }

    public void setNroItem(int nroItem) {
        this.nroItem = nroItem;
    }

    public ItemMovimientoContabilidadCentroCosto getItemCentroCosto() {
        return itemCentroCosto;
    }

    public void setItemCentroCosto(ItemMovimientoContabilidadCentroCosto itemCentroCosto) {
        this.itemCentroCosto = itemCentroCosto;
    }

    public SubCuenta getSubCuenta() {
        return subCuenta;
    }

    public void setSubCuenta(SubCuenta subCuenta) {
        this.subCuenta = subCuenta;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemMovimientoContabilidadSubcuenta)) {
            return false;
        }
        ItemMovimientoContabilidadSubcuenta other = (ItemMovimientoContabilidadSubcuenta) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.contabilidad.modelo.ItemContabilidadSubcuenta[ id=" + id + " ]";
    }

}
