package bs.produccion.vista;

import bs.global.modelo.Formulario;
import bs.produccion.modelo.MovimientoProduccion;
import bs.produccion.modelo.Planta;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
@Table(name = "pd_pendiente_grupo")
@XmlRootElement
@IdClass(PendienteProduccionGrupoPK.class)
public class PendienteProduccionGrupo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Column(name = "id_mcab", nullable = false)
    private int id;
    @Id
    @NotNull
    @Size(min = 1, max = 1)
    @Column(nullable = false, length = 1)
    private String tipitm;
    @Id
    @NotNull
    @Column(nullable = false)
    private String grupo;

    @NotNull
    @Size(min = 1, max = 6)
    @Column(nullable = false, length = 6)
    private String circom;

    @Size(max = 8)
    @Column(length = 8)
    private String modfor;

    @Size(max = 8)
    @Column(length = 8)
    private String codfor;

    @NotNull
    @Column(nullable = false)
    private int nrofor;

    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fchmov;

    @Size(max = 50)
    @Column(length = 50)
    private String observ;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(precision = 10, scale = 2)
    private BigDecimal pendiente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mcab", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private MovimientoProduccion movimientoAplicacion;

    @JoinColumns({
        @JoinColumn(name = "modfor", referencedColumnName = "modfor", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "codfor", referencedColumnName = "codfor", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Formulario formulario;

    @JoinColumn(name = "codpla", referencedColumnName = "codigo", nullable = false, insertable = false, updatable = false)
    @ManyToOne
    private Planta planta;

    @Column(name = "descrp", length = 60)
    private String descripcion;

    public PendienteProduccionGrupo() {

    }

    public String getCircom() {
        return circom;
    }

    public void setCircom(String circom) {
        this.circom = circom;
    }

    public String getCodfor() {
        return codfor;
    }

    public void setCodfor(String codfor) {
        this.codfor = codfor;
    }

    public int getNrofor() {
        return nrofor;
    }

    public void setNrofor(int nrofor) {
        this.nrofor = nrofor;
    }

    public Date getFchmov() {
        return fchmov;
    }

    public void setFchmov(Date fchmov) {
        this.fchmov = fchmov;
    }

    public String getObserv() {
        return observ;
    }

    public void setObserv(String observ) {
        this.observ = observ;
    }

    public BigDecimal getPendiente() {
        return pendiente;
    }

    public void setPendiente(BigDecimal pendiente) {
        this.pendiente = pendiente;
    }

    public MovimientoProduccion getMovimientoAplicacion() {
        return movimientoAplicacion;
    }

    public void setMovimientoAplicacion(MovimientoProduccion movimientoAplicacion) {
        this.movimientoAplicacion = movimientoAplicacion;
    }

    public String getModfor() {
        return modfor;
    }

    public void setModfor(String modfor) {
        this.modfor = modfor;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getTipitm() {
        return tipitm;
    }

    public void setTipitm(String tipitm) {
        this.tipitm = tipitm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + this.id;
        hash = 17 * hash + (this.tipitm != null ? this.tipitm.hashCode() : 0);
        hash = 17 * hash + (this.grupo != null ? this.grupo.hashCode() : 0);
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
        final PendienteProduccionGrupo other = (PendienteProduccionGrupo) obj;
        if (this.id != other.id) {
            return false;
        }
        if ((this.tipitm == null) ? (other.tipitm != null) : !this.tipitm.equals(other.tipitm)) {
            return false;
        }
        if ((this.grupo == null) ? (other.grupo != null) : !this.grupo.equals(other.grupo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PendienteProduccionGrupo{" + "id=" + id + ", tipitm=" + tipitm + ", grupo=" + grupo + '}';
    }
}
