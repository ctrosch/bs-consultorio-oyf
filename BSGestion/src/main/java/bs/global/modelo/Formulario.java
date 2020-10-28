/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.modelo;

import bs.administracion.modelo.Plantilla;
import bs.administracion.modelo.Reporte;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "gr_formulario")
@EntityListeners(AuditoriaListener.class)
@IdClass(FormularioPK.class)
public class Formulario implements IAuditableEntity, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Modulo de formulario
     */
    @Id
    @Column(name = "MODFOR", nullable = false, length = 2)
    private String modfor;
    /**
     * codigo de formulario
     */
    @Id
    @Basic(optional = false)
    @Column(name = "CODFOR", nullable = false, length = 6)
    private String codigo;
    /**
     * Descripción
     */
    @Basic(optional = false)
    @Column(name = "DESCRP", nullable = false, length = 60)
    private String descripcion;

    @Column(name = "TIPNUM", nullable = false, length = 1)
    private String tipoNumeracion;
    /**
     * Ultimo número grabado
     */
    @Column(name = "ULTNRO")
    private Integer ultimoNumero;
    /**
     * Ultima fecha grabada
     */
    @Basic(optional = false)
    @Column(name = "ULTFEC", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date ultimaFecha;

    /*
     * Punto de venta
     */
    @JoinColumn(name = "SUCURS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private PuntoVenta puntoVenta;

    /**
     * Letra
     */
    @Basic(optional = false)
    @Column(name = "LETRID", nullable = false, length = 1)
    private String letra;
    /**
     * número máximo de items
     */
    @Column(name = "MAXITM")
    private Short cantidadMaximaItems;
    /**
     * Nivel de impresión
     */
    @Column(name = "IMPRES")
    private Short nivelImpresion;

    @JoinColumn(name = "CODREP", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Reporte reporte;

    /**
     * Metodo de recuperación de fecha A = Fecha Actual U = Ultima fecha
     * guardada
     */
    @Column(name = "RECFEC", nullable = false, length = 1)
    private String recuperacionFecha;

    /**
     * Número de cai
     */
    @Column(name = "NROCAI", length = 20)
    private String numeroCAI;

    /**
     * Fecha de cai
     */
    @Column(name = "FCHCAI")
    @Temporal(TemporalType.DATE)
    private Date fechaCAI;
    /**
     * Corresponde factua credito
     */
    @Column(name = "TRAFCR")
    private Character correspondeFacturaCredito;
    /**
     * Cantidad de hojas ocupadas por el formulario en impresion
     */
    @Column(name = "HSTFOR")
    private Integer hojaFormulario;
    /**
     * Observaciones
     */
    @Lob
    @Column(name = "TEXTOS", length = 2147483647)
    private String Textos;
    /**
     * Numeracion personalizada
     */
    @Column(name = "PERSON")
    private Character numeracionPersonalizada;
    /**
     * Permite modificar numeracion
     */
    @Column(name = "MODNUM")
    private String modificaNumeracion;
    /**
     * Permite modificar fecha
     */
    @Column(name = "MODFEC")
    private String modificaFecha;
    /**
     * Controla secuencialidad de numeración
     */
    @Column(name = "SECNUM")
    private Character controlaSecuenciaNumeracion;
    /**
     * Controla secuencialidad de fecha
     */
    @Column(name = "SECFEC")
    private Character controlaSecuenciaFecha;
    /**
     * permite registrar en períodos cerrados
     */
    @Column(name = "REPECE")
    private Character registraPeriodosCerrados;
    /**
     * Código DGI
     */
    @Column(name = "CODDGI", length = 25)
    private String codigoDGI;

    @JoinColumn(name = "CODPLA", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Plantilla plantilla;

    @Embedded
    private Auditoria auditoria;

    public Formulario() {

        this.recuperacionFecha = "A";
        this.ultimaFecha = new Date();
        this.ultimoNumero = 0;
        this.tipoNumeracion = "A";
        this.modificaFecha = "N";
        this.modificaNumeracion = "N";
        this.auditoria = new Auditoria();
    }

    public Formulario(String modfor, String codfor) {
        this.modfor = modfor;
        this.codigo = codfor;
    }

    public PuntoVenta getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(PuntoVenta puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public String getTextos() {
        return Textos;
    }

    public void setTextos(String Textos) {
        this.Textos = Textos;
    }

    public Short getCantidadMaximaItems() {
        return cantidadMaximaItems;
    }

    public void setCantidadMaximaItems(Short cantidadMaximaItems) {
        this.cantidadMaximaItems = cantidadMaximaItems;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoDGI() {
        return codigoDGI;
    }

    public void setCodigoDGI(String codigoDGI) {
        this.codigoDGI = codigoDGI;
    }

    public Character getControlaSecuenciaFecha() {
        return controlaSecuenciaFecha;
    }

    public void setControlaSecuenciaFecha(Character controlaSecuenciaFecha) {
        this.controlaSecuenciaFecha = controlaSecuenciaFecha;
    }

    public Character getControlaSecuenciaNumeracion() {
        return controlaSecuenciaNumeracion;
    }

    public void setControlaSecuenciaNumeracion(Character controlaSecuenciaNumeracion) {
        this.controlaSecuenciaNumeracion = controlaSecuenciaNumeracion;
    }

    public Character getCorrespondeFacturaCredito() {
        return correspondeFacturaCredito;
    }

    public void setCorrespondeFacturaCredito(Character correspondeFacturaCredito) {
        this.correspondeFacturaCredito = correspondeFacturaCredito;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaCAI() {
        return fechaCAI;
    }

    public void setFechaCAI(Date fechaCAI) {
        this.fechaCAI = fechaCAI;
    }

    public Integer getHojaFormulario() {
        return hojaFormulario;
    }

    public void setHojaFormulario(Integer hojaFormulario) {
        this.hojaFormulario = hojaFormulario;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public String getModfor() {
        return modfor;
    }

    public void setModfor(String modfor) {
        this.modfor = modfor;
    }

    public String getModificaFecha() {
        return modificaFecha;
    }

    public void setModificaFecha(String modificaFecha) {
        this.modificaFecha = modificaFecha;
    }

    public String getModificaNumeracion() {
        return modificaNumeracion;
    }

    public void setModificaNumeracion(String modificaNumeracion) {
        this.modificaNumeracion = modificaNumeracion;
    }

    public Short getNivelImpresion() {
        return nivelImpresion;
    }

    public void setNivelImpresion(Short nivelImpresion) {
        this.nivelImpresion = nivelImpresion;
    }

    public Character getNumeracionPersonalizada() {
        return numeracionPersonalizada;
    }

    public void setNumeracionPersonalizada(Character numeracionPersonalizada) {
        this.numeracionPersonalizada = numeracionPersonalizada;
    }

    public String getNumeroCAI() {
        return numeroCAI;
    }

    public void setNumeroCAI(String numeroCAI) {
        this.numeroCAI = numeroCAI;
    }

    public String getRecuperacionFecha() {
        return recuperacionFecha;
    }

    public void setRecuperacionFecha(String recuperacionFecha) {
        this.recuperacionFecha = recuperacionFecha;
    }

    public Character getRegistraPeriodosCerrados() {
        return registraPeriodosCerrados;
    }

    public void setRegistraPeriodosCerrados(Character registraPeriodosCerrados) {
        this.registraPeriodosCerrados = registraPeriodosCerrados;
    }

    public String getTipoNumeracion() {
        return tipoNumeracion;
    }

    public void setTipoNumeracion(String tipoNumeracion) {
        this.tipoNumeracion = tipoNumeracion;
    }

    public Date getUltimaFecha() {
        return ultimaFecha;
    }

    public void setUltimaFecha(Date ultimaFecha) {
        this.ultimaFecha = ultimaFecha;
    }

    public Integer getUltimoNumero() {
        return ultimoNumero;
    }

    public void setUltimoNumero(Integer ultimoNumero) {
        this.ultimoNumero = ultimoNumero;
    }

    public Plantilla getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(Plantilla plantilla) {
        this.plantilla = plantilla;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public String getDescripcionComplete() {
        return (codigo != null ? codigo + " - " : "") + (descripcion != null ? descripcion + " - " : "") + (letra != null ? letra : "");
    }

    @Override
    public Formulario clone() throws CloneNotSupportedException {
        return (Formulario) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Formulario other = (Formulario) obj;
        if ((this.modfor == null) ? (other.modfor != null) : !this.modfor.equals(other.modfor)) {
            return false;
        }
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + (this.modfor != null ? this.modfor.hashCode() : 0);
        hash = 11 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "modulo=" + modfor + ", codigo=" + codigo;
    }

}
