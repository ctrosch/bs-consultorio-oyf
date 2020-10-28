/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Comprobante;
import bs.global.modelo.TipoImpuesto;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
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
@Table(name = "pv_retencion_concepto")
@XmlRootElement
@IdClass(ConceptoRetencionPK.class)
@EntityListeners(AuditoriaListener.class)
public class ConceptoRetencion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "TIPRET")
    private String tipo;
    @Id
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "CODRET")
    private String codigo;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "DESCRIP")
    private String descripcion;

    @JoinColumn(name = "TIPRET", referencedColumnName = "CODIGO", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TipoRetencion tipoRetencion;
    
    @Column(name = "PORCEN", precision = 10, scale = 2)
    private BigDecimal porcentajeRetener;
    @Column(name = "EXCEDE", precision = 10, scale = 2)
    private BigDecimal sobreExedenteDe;
    @Size(max = 6)
    @Column(name = "CODDGI")
    private String coddgi;
    @Column(name = "IMPMIN", precision = 10, scale = 2)
    private BigDecimal importeMinimo;
    @Column(name = "PREMIN", precision = 10, scale = 2)
    private BigDecimal precioMinimo;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "CONMIN")
    private String consideraMinimoParaTodosLosPagos;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "MINIMO")
    private String trabajaConMinimo;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ESCALA")
    private String trabajaConEscalaDeValores;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ACUMUL")
    private String trabajaConAcumuladoPorPeriodo;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ACUCAL")
    private String acumuladoPorPago;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ACUMOP")
    private String acumulaEnPrimerPago;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "ACUPAG")
    private String acumuladoPorMonto;

    @JoinColumns({
        @JoinColumn(name = "MODVT", referencedColumnName = "MODCOM"),
        @JoinColumn(name = "CODVT", referencedColumnName = "CODCOM")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobanteVenta;

    @JoinColumns({
        @JoinColumn(name = "MODPV", referencedColumnName = "MODCOM"),
        @JoinColumn(name = "CODPV", referencedColumnName = "CODCOM")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobanteCompra;

    @NotNull    
    @Column(name = "PORTOT", precision = 2, scale = 10)
    private BigDecimal porcentajeDelTotal;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "PORDEF")
    private String porcentajeDefecto;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "RECTAS")
    private String recuperaTasaTablas;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "RECCTE")
    private String recuperaSaldosPorEstructura;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "VERPRE")
    private String verificaMininoEnPrecio;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "RETACE")
    private String aplicaTasaTablaCero;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "RETTOT")
    private String SiExcedeRetienePorcentajeTotalFactura;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "RECIMP")
    private String recuperaImporteDeTabla;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "EXIRET")
    private String verificaRetencionesAnteriores;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "RETEID")
    private String grupoDeRetenciones;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "APLCDI")
    private String aplicaCDI;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "APLACR")
    private String aplicaAcrecentamiento;

    @JoinColumn(name = "TIPIMP", referencedColumnName = "CODIGO")
    @ManyToOne
    private TipoImpuesto tipoImpuesto;

    @JoinColumn(name = "TIPCAL", referencedColumnName = "CODIGO")
    @ManyToOne
    private TipoCalculoRetenciones tipoCalculo;

    @JoinColumn(name = "TIPCOM", referencedColumnName = "CODIGO")
    @ManyToOne
    private TipoCalculoRetenciones tipoCalculoComparacion;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "concepto")
    private List<ConceptoRetencionValor> valores;

    @Embedded
    private Auditoria auditoria;

    public ConceptoRetencion() {
        this.auditoria = new Auditoria();
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getPorcentajeRetener() {
        return porcentajeRetener;
    }

    public void setPorcentajeRetener(BigDecimal porcentajeRetener) {
        this.porcentajeRetener = porcentajeRetener;
    }

    public BigDecimal getSobreExedenteDe() {
        return sobreExedenteDe;
    }

    public void setSobreExedenteDe(BigDecimal sobreExedenteDe) {
        this.sobreExedenteDe = sobreExedenteDe;
    }

    public String getCoddgi() {
        return coddgi;
    }

    public void setCoddgi(String coddgi) {
        this.coddgi = coddgi;
    }

    public BigDecimal getImporteMinimo() {
        return importeMinimo;
    }

    public void setImporteMinimo(BigDecimal importeMinimo) {
        this.importeMinimo = importeMinimo;
    }

    public BigDecimal getPrecioMinimo() {
        return precioMinimo;
    }

    public void setPrecioMinimo(BigDecimal precioMinimo) {
        this.precioMinimo = precioMinimo;
    }

    public String getTrabajaConMinimo() {
        return trabajaConMinimo;
    }

    public void setTrabajaConMinimo(String trabajaConMinimo) {
        this.trabajaConMinimo = trabajaConMinimo;
    }

    public String getTrabajaConEscalaDeValores() {
        return trabajaConEscalaDeValores;
    }

    public void setTrabajaConEscalaDeValores(String trabajaConEscalaDeValores) {
        this.trabajaConEscalaDeValores = trabajaConEscalaDeValores;
    }

    public String getTrabajaConAcumuladoPorPeriodo() {
        return trabajaConAcumuladoPorPeriodo;
    }

    public void setTrabajaConAcumuladoPorPeriodo(String trabajaConAcumuladoPorPeriodo) {
        this.trabajaConAcumuladoPorPeriodo = trabajaConAcumuladoPorPeriodo;
    }

    public String getAcumuladoPorPago() {
        return acumuladoPorPago;
    }

    public void setAcumuladoPorPago(String acumuladoPorPago) {
        this.acumuladoPorPago = acumuladoPorPago;
    }

    public String getAcumulaEnPrimerPago() {
        return acumulaEnPrimerPago;
    }

    public void setAcumulaEnPrimerPago(String acumulaEnPrimerPago) {
        this.acumulaEnPrimerPago = acumulaEnPrimerPago;
    }

    public String getAcumuladoPorMonto() {
        return acumuladoPorMonto;
    }

    public void setAcumuladoPorMonto(String acumuladoPorMonto) {
        this.acumuladoPorMonto = acumuladoPorMonto;
    }

    public Comprobante getComprobanteVenta() {
        return comprobanteVenta;
    }

    public void setComprobanteVenta(Comprobante comprobanteVenta) {
        this.comprobanteVenta = comprobanteVenta;
    }

    public Comprobante getComprobanteCompra() {
        return comprobanteCompra;
    }

    public void setComprobanteCompra(Comprobante comprobanteCompra) {
        this.comprobanteCompra = comprobanteCompra;
    }

    public String getConsideraMinimoParaTodosLosPagos() {
        return consideraMinimoParaTodosLosPagos;
    }

    public void setConsideraMinimoParaTodosLosPagos(String consideraMinimoParaTodosLosPagos) {
        this.consideraMinimoParaTodosLosPagos = consideraMinimoParaTodosLosPagos;
    }

    public BigDecimal getPorcentajeDelTotal() {
        return porcentajeDelTotal;
    }

    public void setPorcentajeDelTotal(BigDecimal porcentajeDelTotal) {
        this.porcentajeDelTotal = porcentajeDelTotal;
    }

    public String getRecuperaImporteDeTabla() {
        return recuperaImporteDeTabla;
    }

    public void setRecuperaImporteDeTabla(String recuperaImporteDeTabla) {
        this.recuperaImporteDeTabla = recuperaImporteDeTabla;
    }

    public String getVerificaRetencionesAnteriores() {
        return verificaRetencionesAnteriores;
    }

    public void setVerificaRetencionesAnteriores(String verificaRetencionesAnteriores) {
        this.verificaRetencionesAnteriores = verificaRetencionesAnteriores;
    }

    public String getGrupoDeRetenciones() {
        return grupoDeRetenciones;
    }

    public void setGrupoDeRetenciones(String grupoDeRetenciones) {
        this.grupoDeRetenciones = grupoDeRetenciones;
    }

    public String getAplicaCDI() {
        return aplicaCDI;
    }

    public void setAplicaCDI(String aplicaCDI) {
        this.aplicaCDI = aplicaCDI;
    }

    public String getAplicaAcrecentamiento() {
        return aplicaAcrecentamiento;
    }

    public void setAplicaAcrecentamiento(String aplicaAcrecentamiento) {
        this.aplicaAcrecentamiento = aplicaAcrecentamiento;
    }

    public TipoImpuesto getTipoImpuesto() {
        return tipoImpuesto;
    }

    public void setTipoImpuesto(TipoImpuesto tipoImpuesto) {
        this.tipoImpuesto = tipoImpuesto;
    }

    public TipoCalculoRetenciones getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(TipoCalculoRetenciones tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public List<ConceptoRetencionValor> getValores() {
        return valores;
    }

    public void setValores(List<ConceptoRetencionValor> valores) {
        this.valores = valores;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoRetencion getTipoRetencion() {
        return tipoRetencion;
    }

    public void setTipoRetencion(TipoRetencion tipoRetencion) {
        this.tipoRetencion = tipoRetencion;
    }

    public String getPorcentajeDefecto() {
        return porcentajeDefecto;
    }

    public void setPorcentajeDefecto(String porcentajeDefecto) {
        this.porcentajeDefecto = porcentajeDefecto;
    }

    public String getRecuperaTasaTablas() {
        return recuperaTasaTablas;
    }

    public void setRecuperaTasaTablas(String recuperaTasaTablas) {
        this.recuperaTasaTablas = recuperaTasaTablas;
    }

    public String getRecuperaSaldosPorEstructura() {
        return recuperaSaldosPorEstructura;
    }

    public void setRecuperaSaldosPorEstructura(String recuperaSaldosPorEstructura) {
        this.recuperaSaldosPorEstructura = recuperaSaldosPorEstructura;
    }

    public String getVerificaMininoEnPrecio() {
        return verificaMininoEnPrecio;
    }

    public void setVerificaMininoEnPrecio(String verificaMininoEnPrecio) {
        this.verificaMininoEnPrecio = verificaMininoEnPrecio;
    }

    public String getAplicaTasaTablaCero() {
        return aplicaTasaTablaCero;
    }

    public void setAplicaTasaTablaCero(String aplicaTasaTablaCero) {
        this.aplicaTasaTablaCero = aplicaTasaTablaCero;
    }

    public String getSiExcedeRetienePorcentajeTotalFactura() {
        return SiExcedeRetienePorcentajeTotalFactura;
    }

    public void setSiExcedeRetienePorcentajeTotalFactura(String SiExcedeRetienePorcentajeTotalFactura) {
        this.SiExcedeRetienePorcentajeTotalFactura = SiExcedeRetienePorcentajeTotalFactura;
    }

    public TipoCalculoRetenciones getTipoCalculoComparacion() {
        return tipoCalculoComparacion;
    }

    public void setTipoCalculoComparacion(TipoCalculoRetenciones tipoCalculoComparacion) {
        this.tipoCalculoComparacion = tipoCalculoComparacion;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.tipo != null ? this.tipo.hashCode() : 0);
        hash = 13 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
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
        final ConceptoRetencion other = (ConceptoRetencion) obj;
        if ((this.tipo == null) ? (other.tipo != null) : !this.tipo.equals(other.tipo)) {
            return false;
        }
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tipo=" + tipo + ",codigo=" + codigo;
    }

}
