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

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "cg_movimiento_item")
@EntityListeners(AuditoriaListener.class)
public class ItemMovimientoContabilidad implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @JoinColumn(name = "id_mcab", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private MovimientoContabilidad movimiento;

    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @Column(name = "debhab", length = 1)
    private String debeHaber;

    @JoinColumn(name = "CTACON", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContable;

    @Column(name = "IMPDEB", precision = 18, scale = 2)
    private double importeDebe;
    @Column(name = "IMPHAB", precision = 18, scale = 2)
    private double importeHaber;

    @Column(name = "SECDEB", precision = 15, scale = 4)
    private double importeDebeMonedaSecundaria;
    @Column(name = "SECHAB", precision = 15, scale = 4)
    private double importeHaberMonedaSecundaria;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "itemContabilidad")
    private List<ItemMovimientoContabilidadCentroCosto> itemsCentroCosto;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean conError;

    @Transient
    private boolean imputaCentroCosto;

    @Transient
    private double importe;

    public ItemMovimientoContabilidad() {

        this.auditoria = new Auditoria();
        this.itemsCentroCosto = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MovimientoContabilidad getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoContabilidad movimiento) {
        this.movimiento = movimiento;
    }

    public int getNroitm() {
        return nroitm;
    }

    public void setNroitm(int nroitm) {
        this.nroitm = nroitm;
    }

    public String getDebeHaber() {
        return debeHaber;
    }

    public void setDebeHaber(String debeHaber) {
        this.debeHaber = debeHaber;
    }

    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public double getImporteDebe() {
        return importeDebe;
    }

    public void setImporteDebe(double importeDebe) {
        this.importeDebe = importeDebe;
    }

    public double getImporteHaber() {
        return importeHaber;
    }

    public void setImporteHaber(double importeHaber) {
        this.importeHaber = importeHaber;
    }

    public double getImporteDebeMonedaSecundaria() {
        return importeDebeMonedaSecundaria;
    }

    public void setImporteDebeMonedaSecundaria(double importeDebeMonedaSecundaria) {
        this.importeDebeMonedaSecundaria = importeDebeMonedaSecundaria;
    }

    public double getImporteHaberMonedaSecundaria() {
        return importeHaberMonedaSecundaria;
    }

    public void setImporteHaberMonedaSecundaria(double importeHaberMonedaSecundaria) {
        this.importeHaberMonedaSecundaria = importeHaberMonedaSecundaria;
    }

    public List<ItemMovimientoContabilidadCentroCosto> getItemsCentroCosto() {
        return itemsCentroCosto;
    }

    public void setItemsCentroCosto(List<ItemMovimientoContabilidadCentroCosto> itemsCentroCosto) {
        this.itemsCentroCosto = itemsCentroCosto;
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

    public boolean isImputaCentroCosto() {
        return imputaCentroCosto;
    }

    public void setImputaCentroCosto(boolean imputaCentroCosto) {
        this.imputaCentroCosto = imputaCentroCosto;
    }

    public double getImporte() {
        return Math.abs(importeDebe - importeHaber);
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final ItemMovimientoContabilidad other = (ItemMovimientoContabilidad) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ItemMovimientoContabilidad{" + "id=" + id + '}';
    }
}
