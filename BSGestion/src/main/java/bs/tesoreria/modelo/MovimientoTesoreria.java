/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.tesoreria.modelo;

import bs.contabilidad.modelo.MovimientoContabilidad;
import bs.educacion.modelo.ItemPendienteCuentaCorrienteEducacion;
import bs.educacion.modelo.MovimientoEducacion;
import bs.entidad.modelo.EntidadComercial;
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
import bs.prestamo.modelo.ItemPendienteCuentaCorrientePrestamo;
import bs.prestamo.modelo.MovimientoPrestamo;
import bs.prestamo.modelo.Prestamo;
import bs.proveedores.modelo.ItemPendienteCuentaCorrienteProveedor;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.ventas.modelo.Cobrador;
import bs.ventas.modelo.ItemPendienteCuentaCorriente;
import bs.ventas.modelo.MovimientoVenta;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "cj_movimiento")
@EntityListeners(AuditoriaListener.class)
public class MovimientoTesoreria implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @JoinColumns({
        @JoinColumn(name = "MODCOM", referencedColumnName = "MODCOM"),
        @JoinColumn(name = "CODCOM", referencedColumnName = "CODCOM")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Comprobante comprobante;

    @JoinColumns({
        @JoinColumn(name = "MODFOR", referencedColumnName = "MODFOR"),
        @JoinColumn(name = "CODFOR", referencedColumnName = "CODFOR")
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

    @JoinColumn(name = "NROENT", referencedColumnName = "NROCTA", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial entidad;

    @Column(name = "NOMENT", length = 120)
    private String nombreEntidad;

    @JoinColumn(name = "ID_REV", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoTesoreria movimientoReversion;

    @JoinColumn(name = "ID_MCG", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoContabilidad movimientoContabilidad;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MREN", referencedColumnName = "ID")
    private MovimientoTesoreria movimientoRendicion;

    @OneToMany(mappedBy = "movimientoRendicion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MovimientoTesoreria> movimientosRendicion;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MVT", referencedColumnName = "ID")
    private MovimientoVenta movimientoVenta;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MPR", referencedColumnName = "ID")
    private MovimientoPrestamo movimientoPrestamo;

    @JoinColumn(name = "ID_PRES", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Prestamo prestamo;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MED", referencedColumnName = "ID")
    private MovimientoEducacion movimientoEducacion;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MPV", referencedColumnName = "ID")
    private MovimientoProveedor movimientoProveedor;

    @JoinColumn(name = "COBRAD", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Cobrador cobrador;

    //Moneda de registracion
    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    //Moneda secundaria
    @JoinColumn(name = "COFSEC", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaSecundaria;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private BigDecimal cotizacion;

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @Column(name = "REFERN", length = 100)
    private String referencia;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemMovimientoTesoreria> itemsDetalle;

    @Embedded
    private Auditoria auditoria;
    @Transient
    private boolean persistido;
    @Transient
    private boolean seleccionado;

    @Transient
    private List<ItemMovimientoTesoreria> itemsDebe;
    @Transient
    private List<ItemMovimientoTesoreria> itemsHaber;
    @Transient
    private List<ItemMovimientoTesoreria> itemsControl;

    @Transient
    private BigDecimal importeTotalDebe;

    @Transient
    private BigDecimal importeTotalHaber;

    @Transient
    private double totalComision;

    @Transient
    private double totalIntereses;

    @Transient
    private BigDecimal importeTotalDebeSecundario;

    @Transient
    private BigDecimal importeTotalHaberSecundario;

    @Transient
    private BigDecimal importeTotal;

    @Transient
    private Comprobante comprobanteVenta;

    @Transient
    private Comprobante comprobanteProveedor;

    @Transient
    private Comprobante comprobantePrestamo;

    @Transient
    private Comprobante comprobanteEducacion;

    @Transient
    private List<MovimientoProveedor> retenciones;

    @Transient
    private Comprobante comprobanteRetencion;

    @Transient
    private boolean esAnticipo;

    //Para clientes
    @Transient
    private List<ItemPendienteCuentaCorriente> debitos;

    @Transient
    private List<ItemPendienteCuentaCorrientePrestamo> debitosPrestamo;

    @Transient
    private List<ItemPendienteCuentaCorrienteEducacion> debitosEducacion;

    //Para proveedor
    @Transient
    private List<ItemPendienteCuentaCorrienteProveedor> creditos;

    public MovimientoTesoreria() {

        auditoria = new Auditoria();
        this.cotizacion = BigDecimal.ONE;
        this.persistido = false;

        importeTotal = BigDecimal.ZERO;
        importeTotalDebe = BigDecimal.ZERO;
        importeTotalHaber = BigDecimal.ZERO;
        importeTotalDebeSecundario = BigDecimal.ZERO;
        importeTotalHaberSecundario = BigDecimal.ZERO;

        itemsDetalle = new ArrayList<ItemMovimientoTesoreria>();

        itemsDebe = new ArrayList<ItemMovimientoTesoreria>();
        itemsHaber = new ArrayList<ItemMovimientoTesoreria>();
        itemsControl = new ArrayList<ItemMovimientoTesoreria>();

        retenciones = new ArrayList<MovimientoProveedor>();

        referencia = "";
        observaciones = "";

        esAnticipo = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public EntidadComercial getEntidad() {
        return entidad;
    }

    public void setEntidad(EntidadComercial entidad) {
        this.entidad = entidad;
    }

    public String getNombreEntidad() {
        return nombreEntidad;
    }

    public void setNombreEntidad(String nombreEntidad) {
        this.nombreEntidad = nombreEntidad;
    }

    public MovimientoTesoreria getMovimientoReversion() {
        return movimientoReversion;
    }

    public List<ItemMovimientoTesoreria> getItemsDetalle() {
        return itemsDetalle;
    }

    public void setItemsDetalle(List<ItemMovimientoTesoreria> itemDetalle) {
        this.itemsDetalle = itemDetalle;
    }

    public void setMovimientoReversion(MovimientoTesoreria movimientoReversion) {
        this.movimientoReversion = movimientoReversion;
    }

    public Cobrador getCobrador() {
        return cobrador;
    }

    public void setCobrador(Cobrador cobrador) {
        this.cobrador = cobrador;
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

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public Moneda getMonedaSecundaria() {
        return monedaSecundaria;
    }

    public void setMonedaSecundaria(Moneda monedaSecundaria) {
        this.monedaSecundaria = monedaSecundaria;
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }

    public boolean isPersistido() {
        return persistido;
    }

    public void setPersistido(boolean persistido) {
        this.persistido = persistido;
    }

    public BigDecimal getImporteTotalDebe() {
        return importeTotalDebe;
    }

    public void setImporteTotalDebe(BigDecimal importeTotalDebe) {
        this.importeTotalDebe = importeTotalDebe;
    }

    public MovimientoVenta getMovimientoVenta() {
        return movimientoVenta;
    }

    public void setMovimientoVenta(MovimientoVenta movimientoVenta) {
        this.movimientoVenta = movimientoVenta;
    }

    public Comprobante getComprobanteVenta() {
        return comprobanteVenta;
    }

    public void setComprobanteVenta(Comprobante comprobanteVenta) {
        this.comprobanteVenta = comprobanteVenta;
    }

    public BigDecimal getImporteTotalHaber() {
        return importeTotalHaber;
    }

    public void setImporteTotalHaber(BigDecimal importeTotalHaber) {
        this.importeTotalHaber = importeTotalHaber;
    }

    public List<ItemPendienteCuentaCorriente> getDebitos() {
        return debitos;
    }

    public void setDebitos(List<ItemPendienteCuentaCorriente> debitos) {
        this.debitos = debitos;
    }

    public List<ItemPendienteCuentaCorrientePrestamo> getDebitosPrestamo() {
        return debitosPrestamo;
    }

    public void setDebitosPrestamo(List<ItemPendienteCuentaCorrientePrestamo> debitosPrestamo) {
        this.debitosPrestamo = debitosPrestamo;
    }

    public boolean isEsAnticipo() {
        return esAnticipo;
    }

    public void setEsAnticipo(boolean esAnticipo) {
        this.esAnticipo = esAnticipo;
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

    public PuntoVenta getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(PuntoVenta puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public Comprobante getComprobanteProveedor() {
        return comprobanteProveedor;
    }

    public void setComprobanteProveedor(Comprobante comprobanteProveedor) {
        this.comprobanteProveedor = comprobanteProveedor;
    }

    public List<ItemPendienteCuentaCorrienteProveedor> getCreditos() {
        return creditos;
    }

    public void setCreditos(List<ItemPendienteCuentaCorrienteProveedor> creditos) {
        this.creditos = creditos;
    }

    public MovimientoProveedor getMovimientoProveedor() {
        return movimientoProveedor;
    }

    public void setMovimientoProveedor(MovimientoProveedor movimientoProveedor) {
        this.movimientoProveedor = movimientoProveedor;
    }

    public Comprobante getComprobanteRetencion() {
        return comprobanteRetencion;
    }

    public void setComprobanteRetencion(Comprobante comprobanteRetencion) {
        this.comprobanteRetencion = comprobanteRetencion;
    }

    public List<MovimientoProveedor> getRetenciones() {
        return retenciones;
    }

    public void setRetenciones(List<MovimientoProveedor> retenciones) {
        this.retenciones = retenciones;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public BigDecimal getImporteTotal() {

        importeTotal = BigDecimal.ZERO;

        if (itemsDetalle != null) {

            for (ItemMovimientoTesoreria i : itemsDetalle) {

                if (i.getDebeHaber().equals("D")) {
                    importeTotal = importeTotal.add(i.getImporte());
                }
            }
        }

        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public BigDecimal getImporteTotalDebeSecundario() {
        return importeTotalDebeSecundario;
    }

    public void setImporteTotalDebeSecundario(BigDecimal importeTotalDebeSecundario) {
        this.importeTotalDebeSecundario = importeTotalDebeSecundario;
    }

    public BigDecimal getImporteTotalHaberSecundario() {
        return importeTotalHaberSecundario;
    }

    public void setImporteTotalHaberSecundario(BigDecimal importeTotalHaberSecundario) {
        this.importeTotalHaberSecundario = importeTotalHaberSecundario;
    }

    public MovimientoPrestamo getMovimientoPrestamo() {
        return movimientoPrestamo;
    }

    public void setMovimientoPrestamo(MovimientoPrestamo movimientoPrestamo) {
        this.movimientoPrestamo = movimientoPrestamo;
    }

    public Comprobante getComprobantePrestamo() {
        return comprobantePrestamo;
    }

    public void setComprobantePrestamo(Comprobante comprobantePrestamo) {
        this.comprobantePrestamo = comprobantePrestamo;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public List<ItemMovimientoTesoreria> getItemsControl() {
        return itemsControl;
    }

    public void setItemsControl(List<ItemMovimientoTesoreria> itemsControl) {
        this.itemsControl = itemsControl;
    }

    public MovimientoContabilidad getMovimientoContabilidad() {
        return movimientoContabilidad;
    }

    public void setMovimientoContabilidad(MovimientoContabilidad movimientoContabilidad) {
        this.movimientoContabilidad = movimientoContabilidad;
    }

    public MovimientoTesoreria getMovimientoRendicion() {
        return movimientoRendicion;
    }

    public void setMovimientoRendicion(MovimientoTesoreria movimientoRendicion) {
        this.movimientoRendicion = movimientoRendicion;
    }

    public List<MovimientoTesoreria> getMovimientosRendicion() {
        return movimientosRendicion;
    }

    public void setMovimientosRendicion(List<MovimientoTesoreria> movimientosRendicion) {
        this.movimientosRendicion = movimientosRendicion;
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

    public List<ItemPendienteCuentaCorrienteEducacion> getDebitosEducacion() {
        return debitosEducacion;
    }

    public void setDebitosEducacion(List<ItemPendienteCuentaCorrienteEducacion> debitosEducacion) {
        this.debitosEducacion = debitosEducacion;
    }

    public Comprobante getComprobanteEducacion() {
        return comprobanteEducacion;
    }

    public void setComprobanteEducacion(Comprobante comprobanteEducacion) {
        this.comprobanteEducacion = comprobanteEducacion;
    }

    public MovimientoEducacion getMovimientoEducacion() {
        return movimientoEducacion;
    }

    public void setMovimientoEducacion(MovimientoEducacion movimientoEducacion) {
        this.movimientoEducacion = movimientoEducacion;
    }

    public double getTotalIntereses() {
        return totalIntereses;
    }

    public void setTotalIntereses(double totalIntereses) {
        this.totalIntereses = totalIntereses;
    }

    public double getTotalComision() {
        return totalComision;
    }

    public void setTotalComision(double totalComision) {
        this.totalComision = totalComision;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final MovimientoTesoreria other = (MovimientoTesoreria) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.tesoreria.modelo.MovimientoTesoreria[ id=" + id + " ]";
    }

}
