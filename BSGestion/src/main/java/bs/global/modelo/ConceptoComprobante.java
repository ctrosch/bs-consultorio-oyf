/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.global.modelo;

import bs.contabilidad.modelo.CuentaContable;
import bs.global.auditoria.AuditoriaListener;
import java.io.Serializable;
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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "gr_concepto_comprobante")
@EntityListeners(AuditoriaListener.class)
public class ConceptoComprobante implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @Column(name = "DEBHAB", nullable = false, length = 1)
    private String debeHaber;

    @Column(name = "TIPCPT", nullable = false, length = 1, updatable = false, insertable = false)
    private String tipcpt;

    @Column(name = "CODCPT", nullable = false, length = 6, updatable = false, insertable = false)
    private String codcpt;

    @JoinColumns({
        @JoinColumn(name = "MODCOM", referencedColumnName = "modcom", nullable = false),
        @JoinColumn(name = "CODCOM", referencedColumnName = "codcom", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobante;

    @JoinColumns({
        @JoinColumn(name = "MODCPT", referencedColumnName = "modulo", nullable = false),
        @JoinColumn(name = "TIPCPT", referencedColumnName = "tipcpt", nullable = false),
        @JoinColumn(name = "CODCPT", referencedColumnName = "CODIGO", nullable = false),})
    @ManyToOne(fetch = FetchType.LAZY)
    private Concepto concepto;

    @JoinColumn(name = "CTACON", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContable;

    @JoinColumns({
        @JoinColumn(name = "MODCAS", referencedColumnName = "MODULO", nullable = false),
        @JoinColumn(name = "TIPCAS", referencedColumnName = "TIPCPT", nullable = false),
        @JoinColumn(name = "CODCAS", referencedColumnName = "CODIGO", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Concepto conceptoAsociado;

    @Column(name = "IMPUES", nullable = false, length = 1)
    private String impuesto;

    @JoinColumn(name = "TIPIMP", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoImpuesto tipoImpuesto;

    @Column(name = "EDIIMP", nullable = false, length = 1)
    private String editaImputacion;

    @Column(name = "EDICPT", nullable = false, length = 1)
    private String editaSiCero;

    @OneToMany(mappedBy = "conceptoComprobante", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemDistribucionConcepto> itemsDistribucion;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean conError;

    /**
     * EDITXT	Edita textos MODEST	Módulo de estructura COMEST	Comprobante de
     * estructura CODEST	Código de estructura CJCNTITM	Cantidad de items	*
     * CJCOLSUB	Columna del Subdiario CUENTA	Cuenta Contable
     *
     * MODCPD	Módulo TIPCPD	Tipo de concepto para devengamiento CODCPD	Código de
     * concepto para devengamiento FORMUL	Fórmula de usuario FORMU1	Fórmula
     * Estadística 1 FORMU2	Fórmula Estadística 2 COLIMP	Columna para Impuesto
     * MODCOL	Módulo ESTCOL	Estructura de Columnas NINTOT	No se incluye en el
     * total NOINDE	Excluir en cálculo de Descuento EXCPTA	Aplica detalle según
     * concepto primario
     */
    public ConceptoComprobante() {

        this.editaImputacion = "S";
        this.editaSiCero = "S";
        this.impuesto = "N";
        this.auditoria = new Auditoria();
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

    public String getDebeHaber() {
        return debeHaber;
    }

    public void setDebeHaber(String debeHaber) {
        this.debeHaber = debeHaber;
    }

    public String getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(String impuesto) {
        this.impuesto = impuesto;
    }

    public TipoImpuesto getTipoImpuesto() {
        return tipoImpuesto;
    }

    public void setTipoImpuesto(TipoImpuesto tipoImpuesto) {
        this.tipoImpuesto = tipoImpuesto;
    }

    public String getEditaImputacion() {
        return editaImputacion;
    }

    public void setEditaImputacion(String editaImputacion) {
        this.editaImputacion = editaImputacion;
    }

    public String getEditaSiCero() {
        return editaSiCero;
    }

    public void setEditaSiCero(String editaSiCero) {
        this.editaSiCero = editaSiCero;
    }

    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

    public Concepto getConceptoAsociado() {
        return conceptoAsociado;
    }

    public void setConceptoAsociado(Concepto conceptoAsociado) {
        this.conceptoAsociado = conceptoAsociado;
    }

    public String getTipcpt() {
        return tipcpt;
    }

    public void setTipcpt(String tipcpt) {
        this.tipcpt = tipcpt;
    }

    public String getCodcpt() {
        return codcpt;
    }

    public void setCodcpt(String codcpt) {
        this.codcpt = codcpt;
    }

    public List<ItemDistribucionConcepto> getItemsDistribucion() {
        return itemsDistribucion;
    }

    public void setItemsDistribucion(List<ItemDistribucionConcepto> itemsDistribucion) {
        this.itemsDistribucion = itemsDistribucion;
    }

    public boolean isConError() {
        return conError;
    }

    public void setConError(boolean conError) {
        this.conError = conError;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final ConceptoComprobante other = (ConceptoComprobante) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConceptoComprobante{" + "id=" + id + ", nroitm=" + nroitm + '}';
    }

}
