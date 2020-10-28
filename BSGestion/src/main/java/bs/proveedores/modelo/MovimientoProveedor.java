/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.proveedores.modelo;

import bs.contabilidad.modelo.MovimientoContabilidad;
import bs.entidad.modelo.EntidadComercial;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
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
import javax.validation.constraints.Size;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "pv_movimiento")
@EntityListeners(AuditoriaListener.class)
public class MovimientoProveedor implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;

    @JoinColumns({
        @JoinColumn(name = "MODCOM", referencedColumnName = "MODCOM"),
        @JoinColumn(name = "CODCOM", referencedColumnName = "CODCOM")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobante;

    @JoinColumns({
        @JoinColumn(name = "MODFOR", referencedColumnName = "MODFOR"),
        @JoinColumn(name = "CODFOR", referencedColumnName = "CODFOR")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Formulario formulario;

    @Column(name = "NROFOR")
    private int numeroFormulario;

    //Punto de venta
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

    @Column(name = "FCHEMI", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaEmision;

    @Column(name = "FCHREC", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaRecepcion;

    @Column(name = "SUCORI", length = 6)
    private String puntoVentaOriginal;

    @Column(name = "NROORI", length = 6)
    private String numeroOriginal;

    @Column(name = "CODAUT", length = 6)
    private String codigoAutorizacion;

    @Column(name = "FCHVEN")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    //Proveedor
    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial proveedor;

    //EntidadComercial
    @JoinColumn(name = "NROSUB", referencedColumnName = "NROCTA", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial proveedorCuentaCorriente;

    @Column(name = "RAZONS", nullable = false, length = 200)
    private String razonSocial;

    @JoinColumn(name = "TIPDOC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento tipoDocumento;

    @Column(name = "NRODOC", length = 50)
    private String nrodoc;

    @JoinColumns({
        @JoinColumn(name = "CODPRO", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "CODPAI", referencedColumnName = "CODPAI", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Provincia provincia;

    //Codigo postal entrega
    @JoinColumn(name = "CODLOC", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Localidad localidad;

    @Column(name = "DIRECC", length = 60)
    private String direccion;
    @Column(name = "NUMERO", length = 20)
    private String numero;
    @Column(name = "DPPISO", length = 6)
    private String departamentoPiso;
    @Column(name = "DEPTOS", length = 6)
    private String departamentoNumero;
    @Column(name = "BARRIO", length = 6)
    private String barrio;

    //Condicion de pago
    @JoinColumn(name = "CNDPAG", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionPagoProveedor condicionDePago;

    @JoinColumn(name = "COMDOR", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Comprador comprador;

    //Lista de precio
    @JoinColumn(name = "CODLIS", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private ListaCosto listaCosto;

    //Moneda lista de precios
    @JoinColumn(name = "COFLIS", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaListaCosto;

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

    //Condicion de iva
    @JoinColumn(name = "CNDIVA", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDeIva condicionDeIva;

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;
    @Basic(optional = false)

    @Size(max = 20)
    @Column(name = "NROCAE")
    private String nrocae;

    @Column(name = "VENCAE")
    @Temporal(TemporalType.DATE)
    private Date vencae;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemProductoProveedor> itemProducto;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemImpuestoProveedor> itemImpuesto;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemRetencionProveedor> itemRetencion;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemPercepcionProveedor> itemPercepcion;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemTotalProveedor> itemTotal;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AplicacionCuentaCorrienteProveedor> itemCuentaCorriente;

    @OneToMany(mappedBy = "movimientoAplicacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AplicacionCuentaCorrienteProveedor> itemCuentaCorrienteAplicacion;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MPV", referencedColumnName = "ID")
    MovimientoProveedor movimientoProveedor;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MCJ", referencedColumnName = "ID")
    MovimientoTesoreria movimientoTesoreria;

    @JoinColumn(name = "ID_MREV", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoProveedor movimientoReversion;

    @OneToMany(mappedBy = "movimientoProveedor", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MovimientoProveedor> retenciones;

    @JoinColumn(name = "ID_MCG", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private MovimientoContabilidad movimientoContabilidad;

    @Embedded
    private Auditoria auditoria;

    @Transient
    BigDecimal importeTotal = BigDecimal.ZERO;

    @Transient
    BigDecimal importeRetenciones = BigDecimal.ZERO;

    @Transient
    private String enLetras;

    @Transient
    BigDecimal importeTotalDebe;

    @Transient
    BigDecimal importeTotalHaber;

    @Transient
    private boolean persistido;

    @Transient
    private boolean seleccionado;

    @Transient
    private boolean esAnticipo;

    @Transient
    private Comprobante comprobanteTesoreria;

    public MovimientoProveedor() {

        this.cotizacion = BigDecimal.ONE;

        this.fechaRecepcion = new Date();

        itemProducto = new ArrayList<ItemProductoProveedor>();
        itemImpuesto = new ArrayList<ItemImpuestoProveedor>();
        itemRetencion = new ArrayList<ItemRetencionProveedor>();
        itemPercepcion = new ArrayList<ItemPercepcionProveedor>();
        itemTotal = new ArrayList<ItemTotalProveedor>();
        itemCuentaCorriente = new ArrayList<AplicacionCuentaCorrienteProveedor>();

        retenciones = new ArrayList<MovimientoProveedor>();

        importeTotalDebe = BigDecimal.ZERO;
        importeTotalHaber = BigDecimal.ZERO;

        this.auditoria = new Auditoria();
    }

    public MovimientoProveedor(Integer id) {
        this.id = id;

        this.cotizacion = BigDecimal.ONE;

        itemProducto = new ArrayList<ItemProductoProveedor>();
        itemImpuesto = new ArrayList<ItemImpuestoProveedor>();
        itemRetencion = new ArrayList<ItemRetencionProveedor>();
        itemPercepcion = new ArrayList<ItemPercepcionProveedor>();
        itemTotal = new ArrayList<ItemTotalProveedor>();
        itemCuentaCorriente = new ArrayList<AplicacionCuentaCorrienteProveedor>();

        retenciones = new ArrayList<MovimientoProveedor>();

        importeTotalDebe = BigDecimal.ZERO;
        importeTotalHaber = BigDecimal.ZERO;

        this.auditoria = new Auditoria();
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

    public EntidadComercial getProveedor() {
        return proveedor;
    }

    public void setProveedor(EntidadComercial proveedor) {
        this.proveedor = proveedor;
    }

    public EntidadComercial getProveedorCuentaCorriente() {
        return proveedorCuentaCorriente;
    }

    public void setProveedorCuentaCorriente(EntidadComercial proveedorCuentaCorriente) {
        this.proveedorCuentaCorriente = proveedorCuentaCorriente;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
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

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDepartamentoPiso() {
        return departamentoPiso;
    }

    public void setDepartamentoPiso(String departamentoPiso) {
        this.departamentoPiso = departamentoPiso;
    }

    public String getDepartamentoNumero() {
        return departamentoNumero;
    }

    public void setDepartamentoNumero(String departamentoNumero) {
        this.departamentoNumero = departamentoNumero;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public CondicionPagoProveedor getCondicionDePago() {
        return condicionDePago;
    }

    public void setCondicionDePago(CondicionPagoProveedor condicionDePago) {
        this.condicionDePago = condicionDePago;
    }

    public Comprador getComprador() {
        return comprador;
    }

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
    }

    public ListaCosto getListaCosto() {
        return listaCosto;
    }

    public void setListaCosto(ListaCosto listaCosto) {
        this.listaCosto = listaCosto;
    }

    public Moneda getMonedaListaCosto() {
        return monedaListaCosto;
    }

    public void setMonedaListaCosto(Moneda monedaListaCosto) {
        this.monedaListaCosto = monedaListaCosto;
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

    public CondicionDeIva getCondicionDeIva() {
        return condicionDeIva;
    }

    public void setCondicionDeIva(CondicionDeIva condicionDeIva) {
        this.condicionDeIva = condicionDeIva;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getNrocae() {
        return nrocae;
    }

    public void setNrocae(String nrocae) {
        this.nrocae = nrocae;
    }

    public Date getVencae() {
        return vencae;
    }

    public void setVencae(Date vencae) {
        this.vencae = vencae;
    }

    public List<ItemProductoProveedor> getItemProducto() {
        return itemProducto;
    }

    public void setItemProducto(List<ItemProductoProveedor> itemProducto) {
        this.itemProducto = itemProducto;
    }

    public List<ItemImpuestoProveedor> getItemImpuesto() {
        return itemImpuesto;
    }

    public void setItemImpuesto(List<ItemImpuestoProveedor> itemImpuesto) {
        this.itemImpuesto = itemImpuesto;
    }

    public List<ItemRetencionProveedor> getItemRetencion() {
        return itemRetencion;
    }

    public void setItemRetencion(List<ItemRetencionProveedor> itemRetencion) {
        this.itemRetencion = itemRetencion;
    }

    public List<ItemTotalProveedor> getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(List<ItemTotalProveedor> itemTotal) {
        this.itemTotal = itemTotal;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public String getEnLetras() {
        return enLetras;
    }

    public void setEnLetras(String enLetras) {
        this.enLetras = enLetras;
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

    public List<AplicacionCuentaCorrienteProveedor> getItemCuentaCorriente() {
        return itemCuentaCorriente;
    }

    public void setItemCuentaCorriente(List<AplicacionCuentaCorrienteProveedor> itemCuentaCorriente) {
        this.itemCuentaCorriente = itemCuentaCorriente;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Date getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(Date fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public String getPuntoVentaOriginal() {
        return puntoVentaOriginal;
    }

    public void setPuntoVentaOriginal(String puntoVentaOriginal) {
        this.puntoVentaOriginal = puntoVentaOriginal;
    }

    public String getNumeroOriginal() {
        return numeroOriginal;
    }

    public void setNumeroOriginal(String numeroOriginal) {
        this.numeroOriginal = numeroOriginal;
    }

    public String getCodigoAutorizacion() {
        return codigoAutorizacion;
    }

    public void setCodigoAutorizacion(String codigoAutorizacion) {
        this.codigoAutorizacion = codigoAutorizacion;
    }

    public List<ItemPercepcionProveedor> getItemPercepcion() {
        return itemPercepcion;
    }

    public void setItemPercepcion(List<ItemPercepcionProveedor> itemPercepcion) {
        this.itemPercepcion = itemPercepcion;
    }

    public List<MovimientoProveedor> getRetenciones() {
        return retenciones;
    }

    public void setRetenciones(List<MovimientoProveedor> retenciones) {
        this.retenciones = retenciones;
    }

    public MovimientoProveedor getMovimientoProveedor() {
        return movimientoProveedor;
    }

    public void setMovimientoProveedor(MovimientoProveedor movimientoProveedor) {
        this.movimientoProveedor = movimientoProveedor;
    }

    public BigDecimal getImporteTotalDebe() {
        return importeTotalDebe;
    }

    public void setImporteTotalDebe(BigDecimal importeTotalDebe) {
        this.importeTotalDebe = importeTotalDebe;
    }

    public BigDecimal getImporteTotalHaber() {
        return importeTotalHaber;
    }

    public void setImporteTotalHaber(BigDecimal importeTotalHaber) {
        this.importeTotalHaber = importeTotalHaber;
    }

    public MovimientoProveedor getMovimientoReversion() {
        return movimientoReversion;
    }

    public void setMovimientoReversion(MovimientoProveedor movimientoReversion) {
        this.movimientoReversion = movimientoReversion;
    }

    public boolean isEsAnticipo() {
        return esAnticipo;
    }

    public void setEsAnticipo(boolean esAnticipo) {
        this.esAnticipo = esAnticipo;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public MovimientoTesoreria getMovimientoTesoreria() {
        return movimientoTesoreria;
    }

    public void setMovimientoTesoreria(MovimientoTesoreria movimientoTesoreria) {
        this.movimientoTesoreria = movimientoTesoreria;
    }

    public Comprobante getComprobanteTesoreria() {
        return comprobanteTesoreria;
    }

    public void setComprobanteTesoreria(Comprobante comprobanteTesoreria) {
        this.comprobanteTesoreria = comprobanteTesoreria;
    }

    public MovimientoContabilidad getMovimientoContabilidad() {
        return movimientoContabilidad;
    }

    public void setMovimientoContabilidad(MovimientoContabilidad movimientoContabilidad) {
        this.movimientoContabilidad = movimientoContabilidad;
    }

    public BigDecimal getImporteRetenciones() {
        return importeRetenciones;
    }

    public void setImporteRetenciones(BigDecimal importeRetenciones) {
        this.importeRetenciones = importeRetenciones;
    }

    public List<AplicacionCuentaCorrienteProveedor> getItemCuentaCorrienteAplicacion() {
        return itemCuentaCorrienteAplicacion;
    }

    public void setItemCuentaCorrienteAplicacion(List<AplicacionCuentaCorrienteProveedor> itemCuentaCorrienteAplicacion) {
        this.itemCuentaCorrienteAplicacion = itemCuentaCorrienteAplicacion;
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
        if (!(object instanceof MovimientoProveedor)) {
            return false;
        }
        MovimientoProveedor other = (MovimientoProveedor) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bs.compras.modelo.MovimientoProveedor[ id=" + id + " ]";
    }

}
