/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.prestamo.modelo;

import bs.contabilidad.modelo.MovimientoContabilidad;
import bs.entidad.modelo.EntidadComercial;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Comprobante;
import bs.global.modelo.CondicionDeIva;
import bs.global.modelo.Empresa;
import bs.global.modelo.Formulario;
import bs.global.modelo.Localidad;
import bs.global.modelo.Moneda;
import bs.global.modelo.Provincia;
import bs.global.modelo.PuntoVenta;
import bs.global.modelo.Sucursal;
import bs.global.modelo.TipoDocumento;
import bs.global.modelo.UnidadNegocio;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.ventas.modelo.Cobrador;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author "Claudio Trosch"
 */
@Entity
@Table(name = "pr_movimiento")
@XmlRootElement
public class MovimientoPrestamo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;

    //Comprobante
    @JoinColumns({
        @JoinColumn(name = "MODCOM", referencedColumnName = "modcom"),
        @JoinColumn(name = "CODCOM", referencedColumnName = "codcom")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobante;

    @JoinColumns({
        @JoinColumn(name = "MODFOR", referencedColumnName = "modfor"),
        @JoinColumn(name = "CODFOR", referencedColumnName = "codfor")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Formulario formulario;

    @Column(name = "NROFOR")
    private int numeroFormulario;

    //Punto de Venta
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

    @Basic(optional = false)
    @Column(name = "IMPTCN", length = 6)
    private String imputacionCuentaCorriente;

    //Fecha del movimiento
    @Column(name = "FCHMOV")
    @Temporal(TemporalType.DATE)
    private Date fechaMovimiento;

    @Column(name = "FCHVNC")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @JoinColumn(name = "ID_PRES", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Prestamo prestamo;

    @JoinColumn(name = "ID_PRESR", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Prestamo prestamoReprogramado;

    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial destinatario;

    @Column(name = "BENEFI", nullable = false, length = 200)
    private String nombre;

    @JoinColumn(name = "TIPDOC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento tipoDocumento;

    @Column(name = "NRODOC", length = 50)
    private String nrodoc;

    //Condicion de iva
    @JoinColumn(name = "CNDIVA", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDeIva condicionDeIva;

    @Column(name = "DIRECC", length = 60)
    private String direccion;
    @Column(name = "BARRIO", length = 6)
    private String barrio;

    //Codigo postal entrega
    @JoinColumn(name = "CODLOC", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Localidad localidad;

    @JoinColumns({
        @JoinColumn(name = "CODPRO", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "CODPAI", referencedColumnName = "CODPAI", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Provincia provincia;

    @JoinColumn(name = "COBRAD", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Cobrador cobrador;

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

    //Observaciones textos
    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @JoinColumn(name = "ID_MREV", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoPrestamo movimientoReversion;

    @JoinColumn(name = "ID_MCJ", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private MovimientoTesoreria movimientoTesoreria;

    @JoinColumn(name = "ID_MRED", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private MovimientoPrestamo movimientoReprogramacion;

    @JoinColumn(name = "ID_MCG", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private MovimientoContabilidad movimientoContabilidad;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemCapitalPrestamo> itemCapital;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemInteresPrestamo> itemIntereses;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemInteresMoraPrestamo> itemInteresesMora;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemDescuentoPrestamo> itemDescuento;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemGastoOtorgamientoPrestamo> itemGastoOtorgamiento;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemMicroseguroPrestamo> itemMicroseguro;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemTotalPrestamo> itemTotal;

    @OrderBy(value = "cuota")
    @OneToMany(mappedBy = "movimiento", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<AplicacionCuentaCorrientePrestamo> itemCuentaCorriente;

    @Embedded
    private Auditoria auditoria;

    @Transient
    double importeCapital = 0;

    @Transient
    double importeInteres = 0;

    @Transient
    double importeInteresMora = 0;

    @Transient
    double importeDescuento = 0;

    @Transient
    double importeTotal = 0;

    @Transient
    double importeGastos = 0;

    @Transient
    double importeMicroseguro = 0;

    @Transient
    private List<ItemPendienteCuentaCorrientePrestamo> debitosPrestamo;

    @Transient
    private boolean persistido;

    @Transient
    private boolean seleccionado;

    public MovimientoPrestamo() {

        this.cotizacion = 1;
        itemCapital = new ArrayList<>();
        itemIntereses = new ArrayList<>();
        itemInteresesMora = new ArrayList<>();
        itemDescuento = new ArrayList<>();
        itemGastoOtorgamiento = new ArrayList<>();
        itemMicroseguro = new ArrayList<>();
        itemTotal = new ArrayList<>();
        itemCuentaCorriente = new ArrayList<>();

        auditoria = new Auditoria();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public PuntoVenta getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(PuntoVenta puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public String getImputacionCuentaCorriente() {
        return imputacionCuentaCorriente;
    }

    public void setImputacionCuentaCorriente(String imputacionCuentaCorriente) {
        this.imputacionCuentaCorriente = imputacionCuentaCorriente;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public EntidadComercial getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(EntidadComercial destinatario) {
        this.destinatario = destinatario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNrodoc() {
        return nrodoc;
    }

    public void setNrodoc(String nrodoc) {
        this.nrodoc = nrodoc;
    }

    public CondicionDeIva getCondicionDeIva() {
        return condicionDeIva;
    }

    public void setCondicionDeIva(CondicionDeIva condicionDeIva) {
        this.condicionDeIva = condicionDeIva;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public Cobrador getCobrador() {
        return cobrador;
    }

    public void setCobrador(Cobrador cobrador) {
        this.cobrador = cobrador;
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

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }

    public boolean isPersistido() {
        return persistido;
    }

    public void setPersistido(boolean persistido) {
        this.persistido = persistido;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public List<ItemCapitalPrestamo> getItemCapital() {
        return itemCapital;
    }

    public void setItemCapital(List<ItemCapitalPrestamo> itemCapital) {
        this.itemCapital = itemCapital;
    }

    public List<ItemInteresPrestamo> getItemIntereses() {
        return itemIntereses;
    }

    public void setItemIntereses(List<ItemInteresPrestamo> itemIntereses) {
        this.itemIntereses = itemIntereses;
    }

    public List<ItemTotalPrestamo> getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(List<ItemTotalPrestamo> itemTotal) {
        this.itemTotal = itemTotal;
    }

    public List<AplicacionCuentaCorrientePrestamo> getItemCuentaCorriente() {
        return itemCuentaCorriente;
    }

    public void setItemCuentaCorriente(List<AplicacionCuentaCorrientePrestamo> itemCuentaCorriente) {
        this.itemCuentaCorriente = itemCuentaCorriente;
    }

    public MovimientoPrestamo getMovimientoReversion() {
        return movimientoReversion;
    }

    public void setMovimientoReversion(MovimientoPrestamo movimientoReversion) {
        this.movimientoReversion = movimientoReversion;
    }

    public MovimientoTesoreria getMovimientoTesoreria() {
        return movimientoTesoreria;
    }

    public void setMovimientoTesoreria(MovimientoTesoreria movimientoTesoreria) {
        this.movimientoTesoreria = movimientoTesoreria;
    }

    public double getImporteCapital() {
        return importeCapital;
    }

    public void setImporteCapital(double importeCapital) {
        this.importeCapital = importeCapital;
    }

    public double getImporteInteres() {
        return importeInteres;
    }

    public void setImporteInteres(double importeInteres) {
        this.importeInteres = importeInteres;
    }

    public List<ItemInteresMoraPrestamo> getItemInteresesMora() {
        return itemInteresesMora;
    }

    public void setItemInteresesMora(List<ItemInteresMoraPrestamo> itemInteresesMora) {
        this.itemInteresesMora = itemInteresesMora;
    }

    public List<ItemDescuentoPrestamo> getItemDescuento() {
        return itemDescuento;
    }

    public void setItemDescuento(List<ItemDescuentoPrestamo> itemDescuento) {
        this.itemDescuento = itemDescuento;
    }

    public double getImporteInteresMora() {
        return importeInteresMora;
    }

    public void setImporteInteresMora(double importeInteresMora) {
        this.importeInteresMora = importeInteresMora;
    }

    public double getImporteDescuento() {
        return importeDescuento;
    }

    public void setImporteDescuento(double importeDescuento) {
        this.importeDescuento = importeDescuento;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public List<ItemGastoOtorgamientoPrestamo> getItemGastoOtorgamiento() {
        return itemGastoOtorgamiento;
    }

    public void setItemGastoOtorgamiento(List<ItemGastoOtorgamientoPrestamo> itemGastoOtorgamiento) {
        this.itemGastoOtorgamiento = itemGastoOtorgamiento;
    }

    public List<ItemMicroseguroPrestamo> getItemMicroseguro() {
        return itemMicroseguro;
    }

    public void setItemMicroseguro(List<ItemMicroseguroPrestamo> itemMicroseguro) {
        this.itemMicroseguro = itemMicroseguro;
    }

    public double getImporteMicroseguro() {
        return importeMicroseguro;
    }

    public void setImporteMicroseguro(double importeMicroseguro) {
        this.importeMicroseguro = importeMicroseguro;
    }

    public List<ItemPendienteCuentaCorrientePrestamo> getDebitosPrestamo() {
        return debitosPrestamo;
    }

    public void setDebitosPrestamo(List<ItemPendienteCuentaCorrientePrestamo> debitosPrestamo) {
        this.debitosPrestamo = debitosPrestamo;
    }

    public Prestamo getPrestamoReprogramado() {
        return prestamoReprogramado;
    }

    public void setPrestamoReprogramado(Prestamo prestamoReprogramado) {
        this.prestamoReprogramado = prestamoReprogramado;
    }

    public MovimientoPrestamo getMovimientoReprogramacion() {
        return movimientoReprogramacion;
    }

    public void setMovimientoReprogramacion(MovimientoPrestamo movimientoReprogramacion) {
        this.movimientoReprogramacion = movimientoReprogramacion;
    }

    public double getImporteGastos() {
        return importeGastos;
    }

    public void setImporteGastos(double importeGastos) {
        this.importeGastos = importeGastos;
    }

    public MovimientoContabilidad getMovimientoContabilidad() {
        return movimientoContabilidad;
    }

    public void setMovimientoContabilidad(MovimientoContabilidad movimientoContabilidad) {
        this.movimientoContabilidad = movimientoContabilidad;
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
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MovimientoPrestamo)) {
            return false;
        }
        MovimientoPrestamo other = (MovimientoPrestamo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.prestamo.modelo.MovimientoPrestamo[ id=" + id + " ]";
    }

}
