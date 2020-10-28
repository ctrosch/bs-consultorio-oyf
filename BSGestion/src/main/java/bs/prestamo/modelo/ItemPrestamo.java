/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.modelo;

import bs.global.modelo.Auditoria;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author "Claudio Trosch"
 */
@Entity
@Table(name = "pr_prestamo_item")
@XmlRootElement
public class ItemPrestamo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "NROITM", nullable = false)
    private int nroitm;

    @JoinColumn(name = "ID_PRES", referencedColumnName = "ID")
    @ManyToOne
    private Prestamo prestamo;

    @Column(name = "FCHVNC")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @Column(name = "FCHPAG")
    @Temporal(TemporalType.DATE)
    private Date fechaPago;

    @Column(name = "CUOTA")
    private Integer cuota;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "IMPCAP", precision = 15, scale = 4)
    private double capital;

    @Column(name = "IMPINT", precision = 15, scale = 4)
    private double interes;

    @Column(name = "INTMOR", precision = 15, scale = 4)
    private double interesMora;

    @Column(name = "CALMOR", precision = 15, scale = 4)
    private boolean calculaInteresMora;

    @Column(name = "DESINT", precision = 15, scale = 4)
    private double descuentoInteres;

    @Column(name = "CALDES", precision = 15, scale = 4)
    private boolean calculaDescuentoInteres;

    @Column(name = "IMPGOT", precision = 15, scale = 4)
    private double gastosOtorgamiento;

    @Column(name = "IMPMCS", precision = 15, scale = 4)
    private double importeMicroseguros;

    @Column(name = "IMPTOT", precision = 15, scale = 4)
    private double totalCuota;

    @Column(name = "CAPCAN", precision = 15, scale = 4)
    private double capitalCancelado;

    @Column(name = "INTCAN", precision = 15, scale = 4)
    private double interesCancelado;

    @Column(name = "DESCAN", precision = 15, scale = 4)
    private double descuentoInteresCancelado;

    @Column(name = "MORCAN", precision = 15, scale = 4)
    private double interesMoraCancelado;

    @Column(name = "GOTCAN", precision = 15, scale = 4)
    private double gastosOtorgamientoCancelado;

    @Column(name = "MCSCAN", precision = 15, scale = 4)
    private double importeMicroseguroCancelado;

    @Column(name = "IMPSEC", precision = 15, scale = 4)
    private double importeSecundario;
    @Column(name = "COTIZA", precision = 15, scale = 4)
    private double cotizacion;

    @Lob
    @Size(max = 2147483647)
    @Column(name = "observ", length = 2147483647)
    private String observaciones;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private boolean seleccionado;

    public ItemPrestamo() {

        this.auditoria = new Auditoria();
        capital = 0;
        interes = 0;
        totalCuota = 0;
        importeSecundario = 0;
        calculaInteresMora = true;
        calculaDescuentoInteres = true;
        gastosOtorgamiento = 0;
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

    public Integer getCuota() {
        return cuota;
    }

    public void setCuota(Integer cuota) {
        this.cuota = cuota;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public double getCapital() {
        return capital;
    }

    public void setCapital(double capital) {
        this.capital = capital;
    }

    public double getInteres() {
        return interes;
    }

    public void setInteres(double interes) {
        this.interes = interes;
    }

    public double getTotalCuota() {
        return totalCuota;
    }

    public void setTotalCuota(double totalCuota) {
        this.totalCuota = totalCuota;
    }

    public double getImporteSecundario() {
        return importeSecundario;
    }

    public void setImporteSecundario(double importeSecundario) {
        this.importeSecundario = importeSecundario;
    }

    public double getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(double cotizacion) {
        this.cotizacion = cotizacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public double getInteresMora() {
        return interesMora;
    }

    public void setInteresMora(double interesMora) {
        this.interesMora = interesMora;
    }

    public double getDescuentoInteres() {
        return descuentoInteres;
    }

    public void setDescuentoInteres(double descuentoInteres) {
        this.descuentoInteres = descuentoInteres;
    }

    public boolean isCalculaInteresMora() {
        return calculaInteresMora;
    }

    public void setCalculaInteresMora(boolean calculaInteresMora) {
        this.calculaInteresMora = calculaInteresMora;
    }

    public boolean isCalculaDescuentoInteres() {
        return calculaDescuentoInteres;
    }

    public void setCalculaDescuentoInteres(boolean calculaDescuentoInteres) {
        this.calculaDescuentoInteres = calculaDescuentoInteres;
    }

    public double getCapitalCancelado() {
        return capitalCancelado;
    }

    public void setCapitalCancelado(double capitalCancelado) {
        this.capitalCancelado = capitalCancelado;
    }

    public double getInteresCancelado() {
        return interesCancelado;
    }

    public void setInteresCancelado(double interesCancelado) {
        this.interesCancelado = interesCancelado;
    }

    public double getGastosOtorgamiento() {
        return gastosOtorgamiento;
    }

    public void setGastosOtorgamiento(double gastosOtorgamiento) {
        this.gastosOtorgamiento = gastosOtorgamiento;
    }

    public double getGastosOtorgamientoCancelado() {
        return gastosOtorgamientoCancelado;
    }

    public void setGastosOtorgamientoCancelado(double gastosOtorgamientoCancelado) {
        this.gastosOtorgamientoCancelado = gastosOtorgamientoCancelado;
    }

    public double getDescuentoInteresCancelado() {
        return descuentoInteresCancelado;
    }

    public void setDescuentoInteresCancelado(double descuentoInteresCancelado) {
        this.descuentoInteresCancelado = descuentoInteresCancelado;
    }

    public double getInteresMoraCancelado() {
        return interesMoraCancelado;
    }

    public void setInteresMoraCancelado(double interesMoraCancelado) {
        this.interesMoraCancelado = interesMoraCancelado;
    }

    public double getImporteMicroseguros() {
        return importeMicroseguros;
    }

    public void setImporteMicroseguros(double importeMicroseguros) {
        this.importeMicroseguros = importeMicroseguros;
    }

    public double getImporteMicroseguroCancelado() {
        return importeMicroseguroCancelado;
    }

    public void setImporteMicroseguroCancelado(double importeMicroseguroCancelado) {
        this.importeMicroseguroCancelado = importeMicroseguroCancelado;
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
        if (!(object instanceof ItemPrestamo)) {
            return false;
        }
        ItemPrestamo other = (ItemPrestamo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.presupuesto.modelo.ItemMovimientoPrestamo[ id=" + id + " ]";
    }

}
