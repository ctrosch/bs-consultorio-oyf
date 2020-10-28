/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.modelo;

import bs.contabilidad.modelo.MascaraContabilidad;
import bs.global.auditoria.AuditoriaListener;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.OrigenRegistracionStock;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "gr_comprobante")
@IdClass(ComprobantePK.class)
@EntityListeners(AuditoriaListener.class)
public class Comprobante implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "MODCOM", nullable = false, length = 2)
    private String modulo;

    @Id
    @Column(name = "CODCOM", nullable = false, length = 6)
    private String codigo;

    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;

    @Column(name = "TITULO", length = 60)
    private String titulo;

    @Column(name = "TIPREG", length = 1)
    private String regisracionManual;

    @Column(name = "COPIAS")
    private Integer copias;

    @Column(name = "RECFEC", length = 1)
    private String recuperacionFecha;

    @Column(name = "COMINT", length = 1)
    private String comprobanteInterno;

    @Column(name = "CALUTA", length = 1)
    private String calculaUtilidadAdicional;

    @Column(name = "INCEST", length = 1)
    private String seIncluyeEnEstadisticas;

    @Column(name = "IMPTCN", length = 6)
    private String tipoImputacion;

    @Column(name = "SIGTOT", length = 1)
    private String signoAplicacionCuentaCorriente;

    @Column(name = "DEBHAB", length = 1)
    private String debeHaber;

    @Column(name = "SUBDIA", length = 1)
    private String seIncluyeEnSubDiario;

    @Column(name = "INCITI", length = 1)
    private String seIncluyeEnCiti;

    @Column(name = "TIPCOM", length = 2)
    private String tipoComprobante;

    @Column(name = "IMOBCC", length = 1)
    private String imputacionObligatoriaEnCentroCosto;

    /**
     * Deposito emisor
     */
    @JoinColumn(name = "DEPTRA", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito depositoTransferencia;

    /**
     * Deposito receptor
     */
    @JoinColumn(name = "DEPOSI", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito deposito;

    @Lob
    @Column(name = "TEXTOS", length = 2147483647)
    private String textos;

    @Column(name = "COMREV", length = 1)
    private String esComprobanteReversion;

    @Column(name = "FILPRF", length = 1)
    private String filtraPorProfesional;

    //Moneda de registracion
    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    @JoinColumns({
        @JoinColumn(name = "MODREV", referencedColumnName = "MODCOM"),
        @JoinColumn(name = "CODREV", referencedColumnName = "CODCOM")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobanteReversion;

    /**
     * ******************************************************
     * TESORERIA ******************************************************
     */
    @OrderBy("debeHaber, tipcpt, codcpt")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comprobante", fetch = FetchType.LAZY)
    private List<ConceptoComprobante> conceptos;

    /**
     * Mascara de stock
     */
    @Column(name = "STMASCAR", length = 6)
    private String stmascar;
    /*
     * Tipo de movimiento
     * A = Ajuste
     * I = Ingreso
     * E = Egreso
     * T = Transferencia
     */

    @Column(name = "STTIPMOV", length = 1)
    private String tipoMovimiento;
    /**
     * Registración desde
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STREGDES", length = 2)
    private OrigenRegistracionStock origenRegistracion;

    /**
     * *****************************************************
     * EDUCACION *****************************************************
     */
    @JoinColumns({
        @JoinColumn(name = "MODINT", referencedColumnName = "MODCOM"),
        @JoinColumn(name = "CODINT", referencedColumnName = "CODCOM")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobanteIntereses;

    @JoinColumns({
        @JoinColumn(name = "MODCCO", referencedColumnName = "MODCOM"),
        @JoinColumn(name = "CODCCO", referencedColumnName = "CODCOM")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobanteComisionCobranza;

    @JoinColumns({
        @JoinColumn(name = "MODREI", referencedColumnName = "MODCOM"),
        @JoinColumn(name = "CODREI", referencedColumnName = "CODCOM")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobanteReinscripcion;

    @JoinColumns({
        @JoinColumn(name = "MODVTA", referencedColumnName = "MODCOM"),
        @JoinColumn(name = "CODVTA", referencedColumnName = "CODCOM")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobanteVenta;

    /**
     * ******************************************************
     * CONTABILIDAD ******************************************************
     */
    @JoinColumn(name = "CGCODMAS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private MascaraContabilidad mascaraContabilidad;

    @JoinColumns({
        @JoinColumn(name = "MODASI", referencedColumnName = "MODCOM"),
        @JoinColumn(name = "CODASI", referencedColumnName = "CODCOM")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobanteAsiento;

    /**
     * ******************************************************
     * PRESTAMO *****************************************************
     */
    @Column(name = "PRESTPRE", length = 1)
    private String estadoPrestamo;

    @Column(name = "PRBUSEST", length = 1)
    private String estadoPrestamoBusqueda;

    @JoinColumns({
        @JoinColumn(name = "MODRED", referencedColumnName = "MODCOM"),
        @JoinColumn(name = "CODRED", referencedColumnName = "CODCOM")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobanteReprogramacion;

    @Embedded
    private Auditoria auditoria;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comprobante", fetch = FetchType.LAZY)
//    @OrderBy("sucurs ASC, formulario.codigo")
    private List<FormularioPorSituacionIVA> formulariosPorSituacionIVA;

    public Comprobante() {

        this.recuperacionFecha = "A";
        this.regisracionManual = "S";
        this.esComprobanteReversion = "N";
        this.seIncluyeEnCiti = "N";
        this.seIncluyeEnEstadisticas = "N";
        this.seIncluyeEnSubDiario = "N";
        this.tipoComprobante = "ND";
        this.estadoPrestamo = "B";
        this.estadoPrestamoBusqueda = "A";
        this.imputacionObligatoriaEnCentroCosto = "N";
        this.calculaUtilidadAdicional = "N";
        this.comprobanteInterno = "N";
        this.filtraPorProfesional = "N";
        this.auditoria = new Auditoria();
    }

    public Comprobante(ComprobantePK idPK) {

        this.modulo = idPK.getModulo();
        this.codigo = idPK.getCodigo();

        this.recuperacionFecha = "A";
        this.regisracionManual = "S";
        this.esComprobanteReversion = "N";
        this.seIncluyeEnCiti = "N";
        this.seIncluyeEnEstadisticas = "N";
        this.seIncluyeEnSubDiario = "N";
        this.tipoComprobante = "ND";
        this.estadoPrestamo = "B";
        this.estadoPrestamoBusqueda = "A";
        this.imputacionObligatoriaEnCentroCosto = "N";
        this.calculaUtilidadAdicional = "N";
        this.comprobanteInterno = "N";
        this.filtraPorProfesional = "N";
        this.auditoria = new Auditoria();

    }

    public Comprobante(ComprobantePK idPK, String Descrp) {
        this.modulo = idPK.getModulo();
        this.codigo = idPK.getCodigo();

        this.descripcion = Descrp;
        this.recuperacionFecha = "A";
        this.regisracionManual = "S";
        this.esComprobanteReversion = "N";
        this.seIncluyeEnCiti = "N";
        this.seIncluyeEnEstadisticas = "N";
        this.seIncluyeEnSubDiario = "N";
        this.tipoComprobante = "ND";
        this.estadoPrestamo = "B";
        this.estadoPrestamoBusqueda = "A";
        this.imputacionObligatoriaEnCentroCosto = "N";
        this.calculaUtilidadAdicional = "N";
        this.comprobanteInterno = "N";
        this.filtraPorProfesional = "N";
        this.auditoria = new Auditoria();
    }

    public Comprobante(String modulo, String codigo) {
        this.modulo = modulo;
        this.codigo = codigo;

        this.recuperacionFecha = "A";
        this.regisracionManual = "S";
        this.esComprobanteReversion = "N";
        this.seIncluyeEnCiti = "N";
        this.seIncluyeEnEstadisticas = "N";
        this.seIncluyeEnSubDiario = "N";
        this.tipoComprobante = "ND";
        this.estadoPrestamo = "B";
        this.estadoPrestamoBusqueda = "A";
        this.imputacionObligatoriaEnCentroCosto = "N";
        this.calculaUtilidadAdicional = "N";
        this.comprobanteInterno = "N";
        this.filtraPorProfesional = "N";
        this.auditoria = new Auditoria();
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getRegisracionManual() {
        return regisracionManual;
    }

    public void setRegisracionManual(String regisracionManual) {
        this.regisracionManual = regisracionManual;
    }

    public Integer getCopias() {
        return copias;
    }

    public void setCopias(Integer copias) {
        this.copias = copias;
    }

    public String getRecuperacionFecha() {
        return recuperacionFecha;
    }

    public void setRecuperacionFecha(String recuperacionFecha) {
        this.recuperacionFecha = recuperacionFecha;
    }

    public Deposito getDepositoTransferencia() {
        return depositoTransferencia;
    }

    public void setDepositoTransferencia(Deposito depositoTransferencia) {
        this.depositoTransferencia = depositoTransferencia;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public String getTextos() {
        return textos;
    }

    public void setTextos(String textos) {
        this.textos = textos;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public List<FormularioPorSituacionIVA> getFormulariosPorSituacionIVA() {
        return formulariosPorSituacionIVA;
    }

    public void setFormulariosPorSituacionIVA(List<FormularioPorSituacionIVA> formulariosPorSituacionIVA) {
        this.formulariosPorSituacionIVA = formulariosPorSituacionIVA;
    }

    public String getEsComprobanteReversion() {
        return esComprobanteReversion;
    }

    public void setEsComprobanteReversion(String esComprobanteReversion) {
        this.esComprobanteReversion = esComprobanteReversion;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    public List<ConceptoComprobante> getConceptos() {
        return conceptos;
    }

    public void setConceptos(List<ConceptoComprobante> conceptos) {
        this.conceptos = conceptos;
    }
//
//    public String getTipoComprobante() {
//        return tipoComprobante;
//    }
//
//    public void settipoComprobante(String tipoComprobante) {
//        this.tipoComprobante = tipoComprobante;
//    }

    public Comprobante getComprobanteReversion() {
        return comprobanteReversion;
    }

    public void setComprobanteReversion(Comprobante comprobanteReversion) {
        this.comprobanteReversion = comprobanteReversion;
    }

    public String getSeIncluyeEnEstadisticas() {
        return seIncluyeEnEstadisticas;
    }

    public void setSeIncluyeEnEstadisticas(String seIncluyeEnEstadisticas) {
        this.seIncluyeEnEstadisticas = seIncluyeEnEstadisticas;
    }

    public String getTipoImputacion() {
        return tipoImputacion;
    }

    public void setTipoImputacion(String tipoImputacion) {
        this.tipoImputacion = tipoImputacion;
    }

    public String getSignoAplicacionCuentaCorriente() {
        return signoAplicacionCuentaCorriente;
    }

    public void setSignoAplicacionCuentaCorriente(String signoAplicacionCuentaCorriente) {
        this.signoAplicacionCuentaCorriente = signoAplicacionCuentaCorriente;
    }

    public String getDebeHaber() {
        return debeHaber;
    }

    public void setDebeHaber(String debeHaber) {
        this.debeHaber = debeHaber;
    }

    public String getSeIncluyeEnCiti() {
        return seIncluyeEnCiti;
    }

    public void setSeIncluyeEnCiti(String seIncluyeEnCiti) {
        this.seIncluyeEnCiti = seIncluyeEnCiti;
    }

    public String getSeIncluyeEnSubDiario() {
        return seIncluyeEnSubDiario;
    }

    public void setSeIncluyeEnSubDiario(String seIncluyeEnSubDiario) {
        this.seIncluyeEnSubDiario = seIncluyeEnSubDiario;
    }

    public String getStmascar() {
        return stmascar;
    }

    public void setStmascar(String stmascar) {
        this.stmascar = stmascar;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public OrigenRegistracionStock getOrigenRegistracion() {
        return origenRegistracion;
    }

    public void setOrigenRegistracion(OrigenRegistracionStock origenRegistracion) {
        this.origenRegistracion = origenRegistracion;
    }

    public Comprobante getComprobanteReprogramacion() {
        return comprobanteReprogramacion;
    }

    public void setComprobanteReprogramacion(Comprobante comprobanteReprogramacion) {
        this.comprobanteReprogramacion = comprobanteReprogramacion;
    }

    public MascaraContabilidad getMascaraContabilidad() {
        return mascaraContabilidad;
    }

    public void setMascaraContabilidad(MascaraContabilidad mascaraContabilidad) {
        this.mascaraContabilidad = mascaraContabilidad;
    }

    public String getComprobanteInterno() {
        return comprobanteInterno;
    }

    public void setComprobanteInterno(String comprobanteInterno) {
        this.comprobanteInterno = comprobanteInterno;
    }

    public String getCalculaUtilidadAdicional() {
        return calculaUtilidadAdicional;
    }

    public void setCalculaUtilidadAdicional(String calculaUtilidadAdicional) {
        this.calculaUtilidadAdicional = calculaUtilidadAdicional;
    }

    public String getEstadoPrestamo() {
        return estadoPrestamo;
    }

    public void setEstadoPrestamo(String estadoPrestamo) {
        this.estadoPrestamo = estadoPrestamo;
    }

    public String getEstadoPrestamoBusqueda() {
        return estadoPrestamoBusqueda;
    }

    public void setEstadoPrestamoBusqueda(String estadoPrestamoBusqueda) {
        this.estadoPrestamoBusqueda = estadoPrestamoBusqueda;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getImputacionObligatoriaEnCentroCosto() {
        return imputacionObligatoriaEnCentroCosto;
    }

    public void setImputacionObligatoriaEnCentroCosto(String imputacionObligatoriaEnCentroCosto) {
        this.imputacionObligatoriaEnCentroCosto = imputacionObligatoriaEnCentroCosto;
    }

    public Comprobante getComprobanteAsiento() {
        return comprobanteAsiento;
    }

    public void setComprobanteAsiento(Comprobante comprobanteAsiento) {
        this.comprobanteAsiento = comprobanteAsiento;
    }

    public String getDescripcionComplete() {
        return (codigo != null ? codigo + " - " : "") + (descripcion != null ? descripcion : "");
    }

    public Comprobante getComprobanteIntereses() {
        return comprobanteIntereses;
    }

    public void setComprobanteIntereses(Comprobante comprobanteIntereses) {
        this.comprobanteIntereses = comprobanteIntereses;
    }

    public Comprobante getComprobanteReinscripcion() {
        return comprobanteReinscripcion;
    }

    public void setComprobanteReinscripcion(Comprobante comprobanteReinscripcion) {
        this.comprobanteReinscripcion = comprobanteReinscripcion;
    }

    public Comprobante getComprobanteComisionCobranza() {
        return comprobanteComisionCobranza;
    }

    public void setComprobanteComisionCobranza(Comprobante comprobanteComisionCobranza) {
        this.comprobanteComisionCobranza = comprobanteComisionCobranza;
    }

    public Comprobante getComprobanteVenta() {
        return comprobanteVenta;
    }

    public void setComprobanteVenta(Comprobante comprobanteVenta) {
        this.comprobanteVenta = comprobanteVenta;
    }

    public String getFiltraPorProfesional() {
        return filtraPorProfesional;
    }

    public void setFiltraPorProfesional(String filtraPorProfesional) {
        this.filtraPorProfesional = filtraPorProfesional;
    }

    @Override
    public Comprobante clone() throws CloneNotSupportedException {
        return (Comprobante) super.clone();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (modulo != null ? modulo.hashCode() : 0);
        hash += (codigo != null ? codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Comprobante other = (Comprobante) obj;
        if ((this.modulo == null) ? (other.modulo != null) : !this.modulo.equals(other.modulo)) {
            return false;
        }
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {

        return "modulo=" + modulo + ", codigo=" + codigo;
    }

}
