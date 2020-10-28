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
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Claudio
 */
@Entity
@EntityListeners(AuditoriaListener.class)
@Table(name = "cg_movimiento_item_centro_costo")
@XmlRootElement
public class ItemMovimientoContabilidadCentroCosto implements Serializable, IAuditableEntity {

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
    private ItemMovimientoContabilidad itemContabilidad;

    @NotNull
    @JoinColumn(name = "CNTCOS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(optional = false)
    private CentroCosto centroCosto;

    @JoinColumn(name = "CODDIS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(optional = false)
    private Distribucion distribucion;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "itemCentroCosto")
    private List<ItemMovimientoContabilidadSubcuenta> subCuentas;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean conError;

    public ItemMovimientoContabilidadCentroCosto() {

        this.auditoria = new Auditoria();
        this.subCuentas = new ArrayList<ItemMovimientoContabilidadSubcuenta>();
    }

    public ItemMovimientoContabilidadCentroCosto(Integer id) {
        this.id = id;
        this.subCuentas = new ArrayList<ItemMovimientoContabilidadSubcuenta>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNroItem() {
        return nroItem;
    }

    public void setNroItem(int nroItem) {
        this.nroItem = nroItem;
    }

    public ItemMovimientoContabilidad getItemContabilidad() {
        return itemContabilidad;
    }

    public void setItemContabilidad(ItemMovimientoContabilidad itemContabilidad) {
        this.itemContabilidad = itemContabilidad;
    }

    public CentroCosto getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(CentroCosto centroCosto) {
        this.centroCosto = centroCosto;
    }

    public List<ItemMovimientoContabilidadSubcuenta> getSubCuentas() {
        return subCuentas;
    }

    public void setSubCuentas(List<ItemMovimientoContabilidadSubcuenta> subCuentas) {
        this.subCuentas = subCuentas;
    }

    public Distribucion getDistribucion() {
        return distribucion;
    }

    public void setDistribucion(Distribucion distribucion) {
        this.distribucion = distribucion;
    }
    
    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
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
        if (!(object instanceof ItemMovimientoContabilidadCentroCosto)) {
            return false;
        }
        ItemMovimientoContabilidadCentroCosto other = (ItemMovimientoContabilidadCentroCosto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.contabilidad.modelo.ItemContabilidadCentroCosto[ id=" + id + " ]";
    }

}
