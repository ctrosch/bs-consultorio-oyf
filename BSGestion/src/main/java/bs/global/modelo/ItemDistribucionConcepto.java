/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.modelo;

import bs.contabilidad.modelo.*;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import java.io.Serializable;
import javax.persistence.Basic;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Claudio
 */
@Entity
@EntityListeners(AuditoriaListener.class)
@Table(name = "gr_concepto_distribucion_centro_costo")
@XmlRootElement
public class ItemDistribucionConcepto implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "NROITM", nullable = false)
    private int nroItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_IDET", referencedColumnName = "ID", nullable = false)
    private ConceptoComprobante conceptoComprobante;

    @NotNull
    @JoinColumn(name = "CNTCOS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(optional = false)
    private CentroCosto centroCosto;

    @JoinColumn(name = "CODDIS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(optional = false)
    private Distribucion distribucion;

    @NotNull
    @Column(name = "IMPAUT", nullable = false, length = 1)
    private String imputacionAutomatica;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean conError;

    public ItemDistribucionConcepto() {

        this.auditoria = new Auditoria();
        this.imputacionAutomatica = "N";
    }

    public ItemDistribucionConcepto(Integer id) {
        this.id = id;
        this.auditoria = new Auditoria();
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

    public CentroCosto getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(CentroCosto centroCosto) {
        this.centroCosto = centroCosto;
    }

    public Distribucion getDistribucion() {
        return distribucion;
    }

    public void setDistribucion(Distribucion distribucion) {
        this.distribucion = distribucion;
    }

    public ConceptoComprobante getConceptoComprobante() {
        return conceptoComprobante;
    }

    public void setConceptoComprobante(ConceptoComprobante conceptoComprobante) {
        this.conceptoComprobante = conceptoComprobante;
    }

    public String getImputacionAutomatica() {
        return imputacionAutomatica;
    }

    public void setImputacionAutomatica(String imputacionAutomatica) {
        this.imputacionAutomatica = imputacionAutomatica;
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
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemDistribucionConcepto)) {
            return false;
        }
        ItemDistribucionConcepto other = (ItemDistribucionConcepto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.contabilidad.modelo.CuentaContableCentroCosto[ id=" + id + " ]";
    }

}
