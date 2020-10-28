/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.modelo;

import bs.entidad.modelo.EntidadComercial;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Moneda;
import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "pr_cuenta_corriente")
@EntityListeners(AuditoriaListener.class)
public class AplicacionCuentaCorrientePrestamo implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MOV", referencedColumnName = "ID")
    MovimientoPrestamo movimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_APL", referencedColumnName = "ID")
    MovimientoPrestamo movimientoAplicacion;

    @JoinColumn(name = "ID_PRES", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Prestamo prestamo;

    @JoinColumn(name = "ID_IPR", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private ItemPrestamo itemPrestamo;

    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial destinatario;

    @Column(name = "CUOTAS", nullable = false)
    private int cuota;
    @Basic(optional = false)

    //Fecha del movimiento
    @Column(name = "FCHMOV")
    @Temporal(TemporalType.DATE)
    private Date fechaAplicacion;

    //Fecha de vencimiento de la cuota
    @Column(name = "FCHVNC")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    //Fecha de interés
    @Column(name = "FCHINT")
    @Temporal(TemporalType.DATE)
    private Date fechaInteres;

    //Código de imputación
    @Column(name = "IMPTCN", length = 6)
    private String codigoImputacion;

//Importe de la cuota en moneda nacional
    @Column(name = "IMPNAC", precision = 15, scale = 4)
    private double importe;

    //Importe en moneda secundaria
    @Column(name = "IMPSEC", precision = 15, scale = 4)
    private double importeSecundario;

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

    //Texto asociado
    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @JoinColumn(name = "MONSEC", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaSecundaria;

    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    //Cambio de la operación
    @Column(name = "COTIZA", precision = 15, scale = 4)
    private double cotizacion;

    @Embedded
    private Auditoria auditoria;

    public AplicacionCuentaCorrientePrestamo() {

        auditoria = new Auditoria();
        importe = 0;
        calculaDescuentoInteres = true;
        calculaInteresMora = true;
        importeSecundario = 0;
        cotizacion = 1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MovimientoPrestamo getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoPrestamo movimiento) {
        this.movimiento = movimiento;
    }

    public MovimientoPrestamo getMovimientoAplicacion() {
        return movimientoAplicacion;
    }

    public void setMovimientoAplicacion(MovimientoPrestamo movimientoAplicacion) {
        this.movimientoAplicacion = movimientoAplicacion;
    }

    public int getCuota() {
        return cuota;
    }

    public void setCuota(int cuota) {
        this.cuota = cuota;
    }

    public Date getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(Date fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public EntidadComercial getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(EntidadComercial destinatario) {
        this.destinatario = destinatario;
    }

    public String getCodigoImputacion() {
        return codigoImputacion;
    }

    public void setCodigoImputacion(String codigoImputacion) {
        this.codigoImputacion = codigoImputacion;
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

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Moneda getMonedaSecundaria() {
        return monedaSecundaria;
    }

    public void setMonedaSecundaria(Moneda monedaSecundaria) {
        this.monedaSecundaria = monedaSecundaria;
    }

    public double getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(double cotizacion) {
        this.cotizacion = cotizacion;
    }

    public Date getFechaInteres() {
        return fechaInteres;
    }

    public void setFechaInteres(Date fechaInteres) {
        this.fechaInteres = fechaInteres;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
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

    public double getInteresMora() {
        return interesMora;
    }

    public void setInteresMora(double interesMora) {
        this.interesMora = interesMora;
    }

    public boolean isCalculaInteresMora() {
        return calculaInteresMora;
    }

    public void setCalculaInteresMora(boolean calculaInteresMora) {
        this.calculaInteresMora = calculaInteresMora;
    }

    public double getDescuentoInteres() {
        return descuentoInteres;
    }

    public void setDescuentoInteres(double descuentoInteres) {
        this.descuentoInteres = descuentoInteres;
    }

    public boolean isCalculaDescuentoInteres() {
        return calculaDescuentoInteres;
    }

    public void setCalculaDescuentoInteres(boolean calculaDescuentoInteres) {
        this.calculaDescuentoInteres = calculaDescuentoInteres;
    }

    public double getGastosOtorgamiento() {
        return gastosOtorgamiento;
    }

    public void setGastosOtorgamiento(double gastosOtorgamiento) {
        this.gastosOtorgamiento = gastosOtorgamiento;
    }

    public ItemPrestamo getItemPrestamo() {
        return itemPrestamo;
    }

    public void setItemPrestamo(ItemPrestamo itemPrestamo) {
        this.itemPrestamo = itemPrestamo;
    }

    public double getImporteMicroseguros() {
        return importeMicroseguros;
    }

    public void setImporteMicroseguros(double importeMicroseguros) {
        this.importeMicroseguros = importeMicroseguros;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final AplicacionCuentaCorrientePrestamo other = (AplicacionCuentaCorrientePrestamo) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AplicacionCuentaCorrienteVenta{" + "id=" + id + '}';
    }

}
