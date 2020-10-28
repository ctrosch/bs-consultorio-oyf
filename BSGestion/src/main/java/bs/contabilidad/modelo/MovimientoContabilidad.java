/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.contabilidad.modelo;

import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Comprobante;
import bs.global.modelo.Empresa;
import bs.global.modelo.Formulario;
import bs.global.modelo.Moneda;
import bs.global.modelo.PuntoVenta;
import bs.global.modelo.Sucursal;
import bs.global.modelo.UnidadNegocio;
import bs.prestamo.modelo.MovimientoPrestamo;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.tesoreria.modelo.*;
import bs.ventas.modelo.MovimientoVenta;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "cg_movimiento")
@EntityListeners(AuditoriaListener.class)
public class MovimientoContabilidad implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @JoinColumn(name = "PERIOD", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private PeriodoContable periodoContable;

    @JoinColumns({
        @JoinColumn(name = "MODCOM", referencedColumnName = "MODCOM", nullable = false),
        @JoinColumn(name = "CODCOM", referencedColumnName = "CODCOM", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Comprobante comprobante;

    @JoinColumns({
        @JoinColumn(name = "MODFOR", referencedColumnName = "MODFOR", nullable = false),
        @JoinColumn(name = "CODFOR", referencedColumnName = "CODFOR", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Formulario formulario;

    @JoinColumn(name = "SUCURS", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private PuntoVenta puntoVenta;

    @JoinColumn(name = "CODEMP", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Empresa empresa;

    @JoinColumn(name = "CODSUC", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Sucursal sucursal;

    @JoinColumn(name = "UNINEG", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadNegocio unidadNegocio;

    @Column(name = "NROFOR", nullable = false)
    private int numeroFormulario;

    @Column(name = "FCHMOV", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaMovimiento;

    @JoinColumn(name = "CODMAS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private MascaraContabilidad mascaraContabilidad;

    //Moneda de registracion
    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    //Moneda secundaria
    @JoinColumn(name = "MONSEC", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaSecundaria;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private double cotizacion;

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @Column(name = "REFERN", length = 100)
    private String referencia;

    @JoinColumn(name = "ID_REV", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoContabilidad movimientoReversion;

    @JoinColumn(name = "ID_MVT", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoVenta movimientoVenta;

    @JoinColumn(name = "ID_MPV", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoProveedor movimientoProveedor;

    @JoinColumn(name = "ID_MCJ", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoTesoreria movimientoTesoreria;

    @JoinColumn(name = "ID_MPR", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoPrestamo movimientoPrestamo;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("nroitm")
    private List<ItemMovimientoContabilidad> itemsDetalle;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private List<ItemMovimientoTesoreria> itemsDebe;
    @Transient
    private List<ItemMovimientoTesoreria> itemsHaber;

    @Transient
    private double importeTotalDebe;
    @Transient
    private double importeTotalHaber;
    @Transient
    private double importeTotalDebeSecundario;
    @Transient
    private double importeTotalHaberSecundario;

    public MovimientoContabilidad() {

        auditoria = new Auditoria();
        this.cotizacion = 1;

        itemsDetalle = new ArrayList<>();

        itemsDebe = new ArrayList<>();
        itemsHaber = new ArrayList<>();

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PeriodoContable getPeriodoContable() {
        return periodoContable;
    }

    public void setPeriodoContable(PeriodoContable periodoContable) {
        this.periodoContable = periodoContable;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public int getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(int numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public PuntoVenta getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(PuntoVenta puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public MascaraContabilidad getMascaraContabilidad() {
        return mascaraContabilidad;
    }

    public void setMascaraContabilidad(MascaraContabilidad mascaraContabilidad) {
        this.mascaraContabilidad = mascaraContabilidad;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public MovimientoContabilidad getMovimientoReversion() {
        return movimientoReversion;
    }

    public void setMovimientoReversion(MovimientoContabilidad movimientoReversion) {
        this.movimientoReversion = movimientoReversion;
    }

    public MovimientoVenta getMovimientoVenta() {
        return movimientoVenta;
    }

    public void setMovimientoVenta(MovimientoVenta movimientoVenta) {
        this.movimientoVenta = movimientoVenta;
    }

    public MovimientoProveedor getMovimientoProveedor() {
        return movimientoProveedor;
    }

    public void setMovimientoProveedor(MovimientoProveedor movimientoProveedor) {
        this.movimientoProveedor = movimientoProveedor;
    }

    public MovimientoTesoreria getMovimientoTesoreria() {
        return movimientoTesoreria;
    }

    public void setMovimientoTesoreria(MovimientoTesoreria movimientoTesoreria) {
        this.movimientoTesoreria = movimientoTesoreria;
    }

    public List<ItemMovimientoContabilidad> getItemsDetalle() {
        return itemsDetalle;
    }

    public void setItemsDetalle(List<ItemMovimientoContabilidad> itemsDetalle) {
        this.itemsDetalle = itemsDetalle;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public List<ItemMovimientoTesoreria> getItemsDebe() {
        return itemsDebe;
    }

    public void setItemsDebe(List<ItemMovimientoTesoreria> itemsDebe) {
        this.itemsDebe = itemsDebe;
    }

    public List<ItemMovimientoTesoreria> getItemsHaber() {
        return itemsHaber;
    }

    public void setItemsHaber(List<ItemMovimientoTesoreria> itemsHaber) {
        this.itemsHaber = itemsHaber;
    }

    public double getImporteTotalDebe() {
        return importeTotalDebe;
    }

    public void setImporteTotalDebe(double importeTotalDebe) {
        this.importeTotalDebe = importeTotalDebe;
    }

    public double getImporteTotalHaber() {
        return importeTotalHaber;
    }

    public void setImporteTotalHaber(double importeTotalHaber) {
        this.importeTotalHaber = importeTotalHaber;
    }

    public double getImporteTotalDebeSecundario() {
        return importeTotalDebeSecundario;
    }

    public void setImporteTotalDebeSecundario(double importeTotalDebeSecundario) {
        this.importeTotalDebeSecundario = importeTotalDebeSecundario;
    }

    public double getImporteTotalHaberSecundario() {
        return importeTotalHaberSecundario;
    }

    public void setImporteTotalHaberSecundario(double importeTotalHaberSecundario) {
        this.importeTotalHaberSecundario = importeTotalHaberSecundario;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public MovimientoPrestamo getMovimientoPrestamo() {
        return movimientoPrestamo;
    }

    public void setMovimientoPrestamo(MovimientoPrestamo movimientoPrestamo) {
        this.movimientoPrestamo = movimientoPrestamo;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final MovimientoContabilidad other = (MovimientoContabilidad) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MovimientoContabilidad{" + "id=" + id + '}';
    }

}
