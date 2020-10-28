/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.entidad.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import bs.proveedores.modelo.ConceptoRetencion;
import bs.global.modelo.Provincia;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "en_entidad_retenciones")
@XmlRootElement
@EntityListeners(AuditoriaListener.class)
public class RetencionPorEntidad implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private EntidadComercial entidadComercial;

    @JoinColumns({
        @JoinColumn(name = "TIPRET", referencedColumnName = "TIPRET"),
        @JoinColumn(name = "CODRET", referencedColumnName = "CODRET")})
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ConceptoRetencion conceptoRetencion;

    @Size(max = 1)
    @Column(name = "VIGENC", length = 1)
    private String vigencia;

    @Column(name = "FECDES")
    @Temporal(TemporalType.DATE)
    private Date fechaVigenciaDesde;

    @Column(name = "FECHAS")
    @Temporal(TemporalType.DATE)
    private Date fechaVigenciaHasta;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation

    @Column(name = "PCTEXN", precision = 10, scale = 2)
    private BigDecimal porcentajeExencion;

    @Column(name = "PCTRED", precision = 10, scale = 2)
    private BigDecimal porcentajeReduccion;
    @Size(max = 20)
    @Column(name = "RESOLC", length = 20)
    private String resolucion;

    @Size(max = 20)
    @Column(name = "CODSIN", length = 20)
    private String codigoSoporteElectrico;

    @JoinColumns({
        @JoinColumn(name = "CODPRO", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "CODPAI", referencedColumnName = "CODPAI", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Provincia provincia;

    @Embedded
    private Auditoria auditoria;

    public RetencionPorEntidad() {

        this.auditoria = new Auditoria();
    }

    public RetencionPorEntidad(int nroitm, String nrocta) {
        this.nroitm = nroitm;
        this.auditoria = new Auditoria();
    }

    public int getNroitm() {
        return nroitm;
    }

    public void setNroitm(int nroitm) {
        this.nroitm = nroitm;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public Date getFechaVigenciaDesde() {
        return fechaVigenciaDesde;
    }

    public void setFechaVigenciaDesde(Date fechaVigenciaDesde) {
        this.fechaVigenciaDesde = fechaVigenciaDesde;
    }

    public Date getFechaVigenciaHasta() {
        return fechaVigenciaHasta;
    }

    public void setFechaVigenciaHasta(Date fechaVigenciaHasta) {
        this.fechaVigenciaHasta = fechaVigenciaHasta;
    }

    public BigDecimal getPorcentajeExencion() {
        return porcentajeExencion;
    }

    public void setPorcentajeExencion(BigDecimal porcentajeExencion) {
        this.porcentajeExencion = porcentajeExencion;
    }

    public BigDecimal getPorcentajeReduccion() {
        return porcentajeReduccion;
    }

    public void setPorcentajeReduccion(BigDecimal porcentajeReduccion) {
        this.porcentajeReduccion = porcentajeReduccion;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public String getCodigoSoporteElectrico() {
        return codigoSoporteElectrico;
    }

    public void setCodigoSoporteElectrico(String codigoSoporteElectrico) {
        this.codigoSoporteElectrico = codigoSoporteElectrico;
    }

    public ConceptoRetencion getConceptoRetencion() {
        return conceptoRetencion;
    }

    public void setConceptoRetencion(ConceptoRetencion conceptoRetencion) {
        this.conceptoRetencion = conceptoRetencion;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public EntidadComercial getEntidadComercial() {
        return entidadComercial;
    }

    public void setEntidadComercial(EntidadComercial entidadComercial) {
        this.entidadComercial = entidadComercial;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final RetencionPorEntidad other = (RetencionPorEntidad) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RetencionPorEntidad{" + "id=" + id + '}';
    }
    
}
