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
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "pv_retencion_tipo")
@EntityListeners(AuditoriaListener.class)
@XmlRootElement
public class TipoRetencion implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "CODIGO")
    private String codigo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "DESCRP")
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "APLIVT")
    private String aplicableVentas;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "APLIPV")
    private String aplicableProveedor;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "APLISJ")
    private String aplicableSueldo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "TIPPER")
    private String tipoRetencion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "RECUPV")
    private String recuperaMaestroProveedor;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "AGRUPA")
    private String agrupa;
    @Size(max = 1)
    @Column(name = "RETEID")
    private String grupoRetenciones;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoRetencion")
    private List<ConceptoRetencion> conceptos;

    @Embedded
    private Auditoria auditoria;

    public TipoRetencion() {
    }

    public TipoRetencion(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getAgrupa() {
        return agrupa;
    }

    public void setAgrupa(String agrupa) {
        this.agrupa = agrupa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAplicableVentas() {
        return aplicableVentas;
    }

    public void setAplicableVentas(String aplicableVentas) {
        this.aplicableVentas = aplicableVentas;
    }

    public String getAplicableProveedor() {
        return aplicableProveedor;
    }

    public void setAplicableProveedor(String aplicableProveedor) {
        this.aplicableProveedor = aplicableProveedor;
    }

    public String getAplicableSueldo() {
        return aplicableSueldo;
    }

    public void setAplicableSueldo(String aplicableSueldo) {
        this.aplicableSueldo = aplicableSueldo;
    }

    public String getTipoRetencion() {
        return tipoRetencion;
    }

    public void setTipoRetencion(String tipoRetencion) {
        this.tipoRetencion = tipoRetencion;
    }

    public String getRecuperaMaestroProveedor() {
        return recuperaMaestroProveedor;
    }

    public void setRecuperaMaestroProveedor(String recuperaMaestroProveedor) {
        this.recuperaMaestroProveedor = recuperaMaestroProveedor;
    }

    public String getGrupoRetenciones() {
        return grupoRetenciones;
    }

    public void setGrupoRetenciones(String grupoRetenciones) {
        this.grupoRetenciones = grupoRetenciones;
    }

    public List<ConceptoRetencion> getConceptos() {
        return conceptos;
    }

    public void setConceptos(List<ConceptoRetencion> conceptos) {
        this.conceptos = conceptos;
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
        hash += (codigo != null ? codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoRetencion)) {
            return false;
        }
        TipoRetencion other = (TipoRetencion) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.global.modelo.RetencionesTipo[ codigo=" + codigo + " ]";
    }


}
