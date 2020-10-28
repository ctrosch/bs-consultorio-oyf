/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.educacion.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author GUILLERMO
 */
@Entity
@Table(name = "ed_arancel")
@EntityListeners(AuditoriaListener.class)
public class Arancel implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "CODIGO", nullable = false, length = 20)
    private String codigo;

    @Size(max = 20)
    @Column(name = "REFERE")
    private String referencia;

    @Size(max = 200)
    @Column(name = "DESCRP")
    private String descripcion;

    @JoinColumn(name = "CODCAR", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Carrera carrera;

    @Column(name = "ANIOLE")
    private int anioLectivo;

    @Column(name = "FCHDES", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaDesde;

    @Column(name = "FCHHAS", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaHasta;

    @Column(name = "SINPEN", nullable = false, length = 1)
    private String sincronizacionPendiente;

    @Column(name = "SINFCH", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaSincronizacion;

    @OneToMany(mappedBy = "arancel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("nroitm")
    private List<ItemArancel> items;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean conError;

    public Arancel() {
        this.sincronizacionPendiente = "N";
        this.auditoria = new Auditoria();
        this.items = new ArrayList<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public int getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(int anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public List<ItemArancel> getItems() {
        return items;
    }

    public void setItems(List<ItemArancel> items) {
        this.items = items;
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

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public String getSincronizacionPendiente() {
        return sincronizacionPendiente;
    }

    public void setSincronizacionPendiente(String sincronizacionPendiente) {
        this.sincronizacionPendiente = sincronizacionPendiente;
    }

    public Date getFechaSincronizacion() {
        return fechaSincronizacion;
    }

    public void setFechaSincronizacion(Date fechaSincronizacion) {
        this.fechaSincronizacion = fechaSincronizacion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    @Override
    public Arancel clone() throws CloneNotSupportedException {
        return (Arancel) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.codigo);
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
        final Arancel other = (Arancel) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.educacion.modelo.Arancel[ codigo=" + codigo + " ]";
    }

}
