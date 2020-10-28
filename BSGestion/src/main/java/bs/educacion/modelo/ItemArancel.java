/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Concepto;
import bs.global.modelo.Periodo;
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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 *
 * @author GUILLERMO
 */
@Entity
@Table(name = "ed_arancel_item")
@EntityListeners(AuditoriaListener.class)
public class ItemArancel implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @JoinColumn(name = "CODARA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Arancel arancel;

    @JoinColumns({
        @JoinColumn(name = "MODCPT", referencedColumnName = "MODULO"),
        @JoinColumn(name = "TIPCPT", referencedColumnName = "TIPCPT"),
        @JoinColumn(name = "CODCPT", referencedColumnName = "CODIGO")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Concepto concepto;

    @Column(name = "CANTID")
    private int cantidad;

    @Column(name = "CUODES", nullable = false)
    private int cuotaDesde;

    @Column(name = "CUOHAS", nullable = false)
    private int cuotaHasta;

    @Column(name = "IMPORT", precision = 15, scale = 4)
    private double importe;

    @JoinColumn(name = "PERIOD", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Periodo periodo;

    @Column(name = "VTODIP")
    private int diaVencimientoSegunPeriodo;

    @Column(name = "OFCVTO")
    private String origenFechaCalculoVencimiento;

    @Column(name = "CONVIA", length = 1)
    private String continuaVencimientoItemAnterior;

    @Column(name = "EDIIMP", length = 1)
    private String editaImporte;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean conError;

    public ItemArancel() {
        this.auditoria = new Auditoria();
        this.continuaVencimientoItemAnterior = "N";
        this.editaImporte = "N";
        this.origenFechaCalculoVencimiento = "M";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNroitm() {
        return nroitm;
    }

    public void setNroitm(int nroitm) {
        this.nroitm = nroitm;
    }

    public Arancel getArancel() {
        return arancel;
    }

    public void setArancel(Arancel arancel) {
        this.arancel = arancel;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public int getDiaVencimientoSegunPeriodo() {
        return diaVencimientoSegunPeriodo;
    }

    public void setDiaVencimientoSegunPeriodo(int diaVencimientoSegunPeriodo) {
        this.diaVencimientoSegunPeriodo = diaVencimientoSegunPeriodo;
    }

    public String getOrigenFechaCalculoVencimiento() {
        return origenFechaCalculoVencimiento;
    }

    public void setOrigenFechaCalculoVencimiento(String origenFechaCalculoVencimiento) {
        this.origenFechaCalculoVencimiento = origenFechaCalculoVencimiento;
    }

    public int getCuotaDesde() {
        return cuotaDesde;
    }

    public void setCuotaDesde(int cuotaDesde) {
        this.cuotaDesde = cuotaDesde;
    }

    public int getCuotaHasta() {
        return cuotaHasta;
    }

    public void setCuotaHasta(int cuotaHasta) {
        this.cuotaHasta = cuotaHasta;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    public String getContinuaVencimientoItemAnterior() {
        return continuaVencimientoItemAnterior;
    }

    public void setContinuaVencimientoItemAnterior(String continuaVencimientoItemAnterior) {
        this.continuaVencimientoItemAnterior = continuaVencimientoItemAnterior;
    }

    public String getEditaImporte() {
        return editaImporte;
    }

    public void setEditaImporte(String editaImporte) {
        this.editaImporte = editaImporte;
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
        if (!(object instanceof ItemArancel)) {
            return false;
        }
        ItemArancel other = (ItemArancel) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.educacion.modelo.ItemArancel[ id=" + id + " ]";
    }

}
