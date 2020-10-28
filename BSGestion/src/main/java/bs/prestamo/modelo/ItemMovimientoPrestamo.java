/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.modelo;

import bs.contabilidad.modelo.CuentaContable;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Concepto;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
@Entity
@Table(name = "pr_movimiento_item")
@EntityListeners(AuditoriaListener.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPCPT", discriminatorType = DiscriminatorType.STRING, length = 1)
public abstract class ItemMovimientoPrestamo implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NROITM", nullable = false)
    private int nroItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MCAB", referencedColumnName = "ID", nullable = false)
    MovimientoPrestamo movimiento;

    @JoinColumn(name = "ID_IPRES", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private ItemPrestamo itemPrestamo;

    @JoinColumns({
        @JoinColumn(name = "MODCPT", referencedColumnName = "MODULO", nullable = false),
        @JoinColumn(name = "TIPCPT", referencedColumnName = "TIPCPT", nullable = false),
        @JoinColumn(name = "CODCPT", referencedColumnName = "CODIGO", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Concepto concepto;

    @JoinColumn(name = "CTACON", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContable;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private double cotizacion;

    //Importe nacional
    @Column(name = "IMPNAC", precision = 15, scale = 4)
    private double importe;

    @Column(name = "IMPSEC", precision = 15, scale = 4)
    private double importeSecundario;

    @Column(name = "DEBHAB", length = 1)
    private String debeHaber;

    //Importe debe
    @Column(name = "IMPDEB", precision = 15, scale = 4)
    private double importeDebe;

    //Importe haber
    @Column(name = "IMPHAB", precision = 15, scale = 4)
    private double importeHaber;

    //Importe debe
    @Column(name = "DEBSEC", precision = 15, scale = 4)
    private double importeDebeSecundario;

    //Importe haber
    @Column(name = "HABSEC", precision = 15, scale = 4)
    private double importeHaberSecundario;

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private Concepto conceptoAsociado;

    public ItemMovimientoPrestamo() {

        importe = 0;
        importeSecundario = 0;

        importeDebe = 0;
        importeDebeSecundario = 0;
        importeHaber = 0;
        importeHaberSecundario = 0;
        cotizacion = 1;
        auditoria = new Auditoria();

    }

    public ItemMovimientoPrestamo(Integer id) {
        this.id = id;

        importe = 0;
        importeSecundario = 0;

        importeDebe = 0;
        importeDebeSecundario = 0;
        importeHaber = 0;
        importeHaberSecundario = 0;

        cotizacion = 1;
        auditoria = new Auditoria();
    }

    public ItemMovimientoPrestamo(Integer id, Concepto concepto) {
        this.id = id;
        this.concepto = concepto;

        cotizacion = 1;
        auditoria = new Auditoria();
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

    public MovimientoPrestamo getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoPrestamo movimiento) {
        this.movimiento = movimiento;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

    public double getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(double cotizacion) {
        this.cotizacion = cotizacion;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public double getImporteSecundario() {
        return importeSecundario;
    }

    public void setImporteSecundario(double importeSecundario) {
        this.importeSecundario = importeSecundario;
    }

    public double getImporteDebe() {
        return importeDebe;
    }

    public void setImporteDebe(double importeDebe) {
        this.importeDebe = importeDebe;
    }

    public double getImporteHaber() {
        return importeHaber;
    }

    public void setImporteHaber(double importeHaber) {
        this.importeHaber = importeHaber;
    }

    public double getImporteDebeSecundario() {
        return importeDebeSecundario;
    }

    public void setImporteDebeSecundario(double importeDebeSecundario) {
        this.importeDebeSecundario = importeDebeSecundario;
    }

    public double getImporteHaberSecundario() {
        return importeHaberSecundario;
    }

    public void setImporteHaberSecundario(double importeHaberSecundario) {
        this.importeHaberSecundario = importeHaberSecundario;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public String getDebeHaber() {
        return debeHaber;
    }

    public void setDebeHaber(String debeHaber) {
        this.debeHaber = debeHaber;
    }

    public Concepto getConceptoAsociado() {
        return conceptoAsociado;
    }

    public void setConceptoAsociado(Concepto conceptoAsociado) {
        this.conceptoAsociado = conceptoAsociado;
    }

    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public ItemPrestamo getItemPrestamo() {
        return itemPrestamo;
    }

    public void setItemPrestamo(ItemPrestamo itemPrestamo) {
        this.itemPrestamo = itemPrestamo;
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
        if (!(object instanceof ItemMovimientoPrestamo)) {
            return false;
        }
        ItemMovimientoPrestamo other = (ItemMovimientoPrestamo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.prestamo.modelo.ItemMovimientoPrestamo[id=" + id + "]";
    }

}
