/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.compra.modelo;

import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.EntidadCamion;
import bs.entidad.modelo.EntidadChofer;
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
import bs.proveedores.modelo.AplicacionCuentaCorrienteProveedor;
import bs.proveedores.modelo.Comprador;
import bs.proveedores.modelo.CondicionPagoProveedor;
import bs.proveedores.modelo.ItemImpuestoProveedor;
import bs.proveedores.modelo.ItemPercepcionProveedor;
import bs.proveedores.modelo.ItemProductoProveedor;
import bs.proveedores.modelo.ItemRetencionProveedor;
import bs.proveedores.modelo.ItemTotalProveedor;
import bs.proveedores.modelo.ListaCosto;
import bs.proveedores.modelo.MovimientoProveedor;
import bs.stock.modelo.Deposito;
import bs.stock.modelo.MovimientoStock;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
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
@Table(name = "co_movimiento")
@EntityListeners(AuditoriaListener.class)
public class MovimientoCompra implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;

    @JoinColumns({
        @JoinColumn(name = "CIRCOM", referencedColumnName = "CIRCOM", nullable = false),
        @JoinColumn(name = "CIRAPL", referencedColumnName = "CIRAPL", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    CircuitoCompra circuito;

    //Comprobante de compra
    @JoinColumns({
        @JoinColumn(name = "MODCOM", referencedColumnName = "MODCOM", nullable = false),
        @JoinColumn(name = "CODCOM", referencedColumnName = "CODCOM", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobante;

    //Comprobante de compra original
    @JoinColumns({
        @JoinColumn(name = "COMORI", referencedColumnName = "MODCOM", nullable = false),
        @JoinColumn(name = "CODORI", referencedColumnName = "CODCOM", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Comprobante comprobanteOriginal;

    @JoinColumns({
        @JoinColumn(name = "MODFOR", referencedColumnName = "MODFOR", nullable = false),
        @JoinColumn(name = "CODFOR", referencedColumnName = "CODFOR", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    Formulario formulario;

    @Column(name = "NROFOR", nullable = false)
    private int numeroFormulario;

    //Punto de venta
    @JoinColumn(name = "SUCURS", referencedColumnName = "CODIGO", nullable = false)
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

    //Fecha de movimiento
    @Basic(optional = false)
    @Column(name = "FCHMOV", nullable = false)
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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MPV", referencedColumnName = "ID")
    MovimientoProveedor movimientoProveedor;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MCJ", referencedColumnName = "ID")
    MovimientoTesoreria movimientoTesoreria;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MST", referencedColumnName = "ID")
    MovimientoStock movimientoStock;

    //EntidadComercial
    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial proveedor;

    @Column(name = "RAZONS", nullable = false, length = 200)
    private String razonSocial;

    //EntidadComercial
    @JoinColumn(name = "NROSUB", referencedColumnName = "NROCTA", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial proveedorCuentaCorriente;

    @JoinColumn(name = "TIPDOC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento tipoDocumento;

    @Column(name = "NRODOC", length = 50)
    private String nrodoc;

    @JoinColumns({
        @JoinColumn(name = "codent", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "ctaent", referencedColumnName = "NROCTA", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private DireccionEntregaEntidad direccionEntrega;

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

    //Transportista
    @JoinColumn(name = "CODTRA", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial transporte;

    @JoinColumn(name = "IDCAMI", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadCamion camion;

    @JoinColumn(name = "IDCHOF", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadChofer chofer;

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

    //Condicion de iva
    @JoinColumn(name = "CNDIVA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDeIva condicionDeIva;

    //Congela precio
    @Column(name = "CONGEL", length = 1)
    private String congelaPrecio;

    @Column(name = "FCHVEN")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    //VT_Vendedor
    @JoinColumn(name = "COMDOR", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Comprador comprador;

    //Condicion de pago
    @JoinColumn(name = "CNDPAG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionPagoProveedor condicionDePago;

    @JoinColumn(name = "DEPOSI", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito deposito;

    @JoinColumn(name = "DEPTRA", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito depositoTransferencia;

    //Lista de precio
    @JoinColumn(name = "CODLIS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ListaCosto listaCosto;

    //Moneda lista de precios
    @JoinColumn(name = "COFLIS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaListaCosto;

    //Moneda de registracion
    @JoinColumn(name = "MONREG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaRegistracion;

    //Moneda secundaria
    @JoinColumn(name = "COFSEC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaSecundaria;

    @Column(name = "COTIZA", precision = 15, scale = 4)
    private BigDecimal cotizacion;

    @Column(name = "DIAENT")
    private Short dias;
    @Column(name = "HORENT", length = 60)
    private String hora;
    @Column(name = "CONTAC", length = 60)
    private String contacto;
    @Column(name = "TELEFN", length = 30)
    private String telefono;

    @Column(name = "PAGFLT", length = 1)
    private String pagoFlete;

    //Observaciones
    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;
    @Column(name = "PCTBF1", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion1;
    @Column(name = "PCTBF2", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion2;
    @Column(name = "PCTBF3", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion3;

    @Column(name = "PCTFIN", precision = 15, scale = 4)
    private BigDecimal porcentajeFinanciacion;
    @Column(name = "TASAIN", precision = 15, scale = 4)
    private BigDecimal tasaInteres;

    //Estado de autorizacion
    @Column(name = "ESTAUT")
    private int estadoAutorizacion;
    //Usuario de autorizaicon
    @Column(name = "USRAUT", length = 36)
    private String usuarioAutorizacion;
    //Fecha de autorizacion
    @Column(name = "FCHAUT")
    @Temporal(TemporalType.DATE)
    private Date fechaAutorizacion;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("nroitm")
    private List<ItemMovimientoCompra> itemsProducto;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private PuntoVenta puntoVentaStock;

    @Transient
    private BigDecimal subTotalConIVA;
    @Transient
    private BigDecimal subTotalPendienteConIVA;
    @Transient
    private int cantidadItems;

    @Transient
    private boolean persistido;

    @Transient
    private Comprobante comprobanteProveedor;
    @Transient
    private Comprobante comprobanteTesoreria;
    @Transient
    private Comprobante comprobanteStock;

    @Transient
    BigDecimal impGravado0000 = BigDecimal.ZERO;
    @Transient
    BigDecimal impGravado2100 = BigDecimal.ZERO;
    @Transient
    BigDecimal impGravado1050 = BigDecimal.ZERO;
    @Transient
    BigDecimal impGravado2700 = BigDecimal.ZERO;
    @Transient
    BigDecimal impIva0250 = BigDecimal.ZERO;
    @Transient
    BigDecimal impIva1050 = BigDecimal.ZERO;
    @Transient
    BigDecimal impIva2100 = BigDecimal.ZERO;
    @Transient
    BigDecimal impIva2700 = BigDecimal.ZERO;
    ;
    @Transient
    BigDecimal impBruto = BigDecimal.ZERO;
    @Transient
    BigDecimal impPercepcionIB = BigDecimal.ZERO;
    @Transient
    BigDecimal impPercepcionGAN = BigDecimal.ZERO;
    @Transient
    BigDecimal impPercepcionITC = BigDecimal.ZERO;
    @Transient
    BigDecimal impPercepcionIVA = BigDecimal.ZERO;
    @Transient
    BigDecimal impPercepcionINT = BigDecimal.ZERO;
    @Transient
    BigDecimal importeTotal = BigDecimal.ZERO;

    /*
    * SOPORTE PARA GENERAR MOVIMIENTOS DE PROVEEDOR
     */
    @Transient
    private List<ItemProductoProveedor> itemsProductoProveedor;
    @Transient
    private List<ItemImpuestoProveedor> itemsImpuestoProveedor;
    @Transient
    private List<ItemRetencionProveedor> itemsRetencionProveedor;
    @Transient
    private List<ItemPercepcionProveedor> itemsPercepcionProveedor;
    @Transient
    private List<ItemTotalProveedor> itemsTotalProveedor;
    @Transient
    private List<AplicacionCuentaCorrienteProveedor> itemCuentaCorriente;

    /*
    * SOPORTE PARA GENERAR MOVIMIENTOS DE TESORERÍA
     */
    @Transient
    private List<ItemMovimientoTesoreria> itemsOtraCuentaDebe;
    @Transient
    private List<ItemMovimientoTesoreria> itemsValoresDebe;
    @Transient
    private List<ItemMovimientoTesoreria> itemsBancoDebe;
    @Transient
    private List<ItemMovimientoTesoreria> itemsDocumentoPagarDebe;
    @Transient
    private List<ItemMovimientoTesoreria> itemsDocumentoCobrarDebe;
    @Transient
    private List<ItemMovimientoTesoreria> itemsTarjetaDebe;

    @Transient
    private List<ItemMovimientoTesoreria> itemsOtraCuentaHaber;
    @Transient
    private List<ItemMovimientoTesoreria> itemsValoresHaber;
    @Transient
    private List<ItemMovimientoTesoreria> itemsBancoHaber;
    @Transient
    private List<ItemMovimientoTesoreria> itemsDocumentoPagarHaber;
    @Transient
    private List<ItemMovimientoTesoreria> itemsDocumentoCobrarHaber;
    @Transient
    private List<ItemMovimientoTesoreria> itemsTarjetaHaber;

    @Transient
    private String atributo1;
    @Transient
    private String atributo2;
    @Transient
    private String atributo3;
    @Transient
    private String atributo4;
    @Transient
    private String atributo5;
    @Transient
    private String atributo6;
    @Transient
    private String atributo7;

    @Transient
    private Date fechaEntrega;

    public MovimientoCompra() {

        this.auditoria = new Auditoria();
        this.cotizacion = BigDecimal.ONE;

        this.fechaRecepcion = new Date();

        itemsProducto = new ArrayList<ItemMovimientoCompra>();

        itemsProductoProveedor = new ArrayList<ItemProductoProveedor>();
        itemsImpuestoProveedor = new ArrayList<ItemImpuestoProveedor>();
        itemsRetencionProveedor = new ArrayList<ItemRetencionProveedor>();
        itemsPercepcionProveedor = new ArrayList<ItemPercepcionProveedor>();
        itemsTotalProveedor = new ArrayList<ItemTotalProveedor>();

        itemsOtraCuentaDebe = new ArrayList<ItemMovimientoTesoreria>();
        itemsValoresDebe = new ArrayList<ItemMovimientoTesoreria>();
        itemsBancoDebe = new ArrayList<ItemMovimientoTesoreria>();
        itemsDocumentoPagarDebe = new ArrayList<ItemMovimientoTesoreria>();
        itemsDocumentoCobrarDebe = new ArrayList<ItemMovimientoTesoreria>();
        itemsTarjetaDebe = new ArrayList<ItemMovimientoTesoreria>();

        itemsOtraCuentaHaber = new ArrayList<ItemMovimientoTesoreria>();
        itemsValoresHaber = new ArrayList<ItemMovimientoTesoreria>();
        itemsBancoHaber = new ArrayList<ItemMovimientoTesoreria>();
        itemsDocumentoPagarHaber = new ArrayList<ItemMovimientoTesoreria>();
        itemsDocumentoCobrarHaber = new ArrayList<ItemMovimientoTesoreria>();
        itemsTarjetaHaber = new ArrayList<ItemMovimientoTesoreria>();
    }

    public MovimientoCompra(Integer id) {

        this.id = id;
        this.auditoria = new Auditoria();
        this.cotizacion = BigDecimal.ONE;

        this.fechaRecepcion = new Date();

        itemsProducto = new ArrayList<ItemMovimientoCompra>();

        itemsProductoProveedor = new ArrayList<ItemProductoProveedor>();
        itemsImpuestoProveedor = new ArrayList<ItemImpuestoProveedor>();
        itemsRetencionProveedor = new ArrayList<ItemRetencionProveedor>();
        itemsPercepcionProveedor = new ArrayList<ItemPercepcionProveedor>();
        itemsTotalProveedor = new ArrayList<ItemTotalProveedor>();

        itemsOtraCuentaDebe = new ArrayList<ItemMovimientoTesoreria>();
        itemsValoresDebe = new ArrayList<ItemMovimientoTesoreria>();
        itemsBancoDebe = new ArrayList<ItemMovimientoTesoreria>();
        itemsDocumentoPagarDebe = new ArrayList<ItemMovimientoTesoreria>();
        itemsDocumentoCobrarDebe = new ArrayList<ItemMovimientoTesoreria>();
        itemsTarjetaDebe = new ArrayList<ItemMovimientoTesoreria>();

        itemsOtraCuentaHaber = new ArrayList<ItemMovimientoTesoreria>();
        itemsValoresHaber = new ArrayList<ItemMovimientoTesoreria>();
        itemsBancoHaber = new ArrayList<ItemMovimientoTesoreria>();
        itemsDocumentoPagarHaber = new ArrayList<ItemMovimientoTesoreria>();
        itemsDocumentoCobrarHaber = new ArrayList<ItemMovimientoTesoreria>();
        itemsTarjetaHaber = new ArrayList<ItemMovimientoTesoreria>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CircuitoCompra getCircuito() {
        return circuito;
    }

    public void setCircuito(CircuitoCompra circuito) {
        this.circuito = circuito;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public Comprobante getComprobanteOriginal() {
        return comprobanteOriginal;
    }

    public void setComprobanteOriginal(Comprobante comprobanteOriginal) {
        this.comprobanteOriginal = comprobanteOriginal;
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

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public MovimientoTesoreria getMovimientoTesoreria() {
        return movimientoTesoreria;
    }

    public void setMovimientoTesoreria(MovimientoTesoreria movimientoTesoreria) {
        this.movimientoTesoreria = movimientoTesoreria;
    }

    public MovimientoStock getMovimientoStock() {
        return movimientoStock;
    }

    public void setMovimientoStock(MovimientoStock movimientoStock) {
        this.movimientoStock = movimientoStock;
    }

    public EntidadComercial getProveedor() {
        return proveedor;
    }

    public void setProveedor(EntidadComercial proveedor) {
        this.proveedor = proveedor;
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

    public EntidadComercial getTransporte() {
        return transporte;
    }

    public void setTransporte(EntidadComercial transporte) {
        this.transporte = transporte;
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

    public CondicionDeIva getCondicionDeIva() {
        return condicionDeIva;
    }

    public void setCondicionDeIva(CondicionDeIva condicionDeIva) {
        this.condicionDeIva = condicionDeIva;
    }

    public String getCongelaPrecio() {
        return congelaPrecio;
    }

    public void setCongelaPrecio(String congelaPrecio) {
        this.congelaPrecio = congelaPrecio;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public CondicionPagoProveedor getCondicionDePago() {
        return condicionDePago;
    }

    public void setCondicionDePago(CondicionPagoProveedor condicionDePago) {
        this.condicionDePago = condicionDePago;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public Deposito getDepositoTransferencia() {
        return depositoTransferencia;
    }

    public void setDepositoTransferencia(Deposito depositoTransferencia) {
        this.depositoTransferencia = depositoTransferencia;
    }

    public EntidadComercial getProveedorCuentaCorriente() {
        return proveedorCuentaCorriente;
    }

    public void setProveedorCuentaCorriente(EntidadComercial proveedorCuentaCorriente) {
        this.proveedorCuentaCorriente = proveedorCuentaCorriente;
    }

    public Comprador getComprador() {
        return comprador;
    }

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
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

    public Short getDias() {
        return dias;
    }

    public void setDias(Short dias) {
        this.dias = dias;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPagoFlete() {
        return pagoFlete;
    }

    public void setPagoFlete(String pagoFlete) {
        this.pagoFlete = pagoFlete;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public BigDecimal getPorcentajeBonificacion1() {
        return porcentajeBonificacion1;
    }

    public void setPorcentajeBonificacion1(BigDecimal porcentajeBonificacion1) {
        this.porcentajeBonificacion1 = porcentajeBonificacion1;
    }

    public BigDecimal getPorcentajeBonificacion2() {
        return porcentajeBonificacion2;
    }

    public void setPorcentajeBonificacion2(BigDecimal porcentajeBonificacion2) {
        this.porcentajeBonificacion2 = porcentajeBonificacion2;
    }

    public BigDecimal getPorcentajeBonificacion3() {
        return porcentajeBonificacion3;
    }

    public void setPorcentajeBonificacion3(BigDecimal porcentajeBonificacion3) {
        this.porcentajeBonificacion3 = porcentajeBonificacion3;
    }

    public BigDecimal getPorcentajeFinanciacion() {
        return porcentajeFinanciacion;
    }

    public void setPorcentajeFinanciacion(BigDecimal porcentajeFinanciacion) {
        this.porcentajeFinanciacion = porcentajeFinanciacion;
    }

    public BigDecimal getTasaInteres() {
        return tasaInteres;
    }

    public void setTasaInteres(BigDecimal tasaInteres) {
        this.tasaInteres = tasaInteres;
    }

    public int getEstadoAutorizacion() {
        return estadoAutorizacion;
    }

    public void setEstadoAutorizacion(int estadoAutorizacion) {
        this.estadoAutorizacion = estadoAutorizacion;
    }

    public String getUsuarioAutorizacion() {
        return usuarioAutorizacion;
    }

    public void setUsuarioAutorizacion(String usuarioAutorizacion) {
        this.usuarioAutorizacion = usuarioAutorizacion;
    }

    public Date getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setFechaAutorizacion(Date fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    @Override
    public Auditoria getAuditoria() {
        return auditoria;
    }

    @Override
    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public BigDecimal getSubTotalConIVA() {
        return subTotalConIVA;
    }

    public void setSubTotalConIVA(BigDecimal subTotalConIVA) {
        this.subTotalConIVA = subTotalConIVA;
    }

    public BigDecimal getSubTotalPendienteConIVA() {
        return subTotalPendienteConIVA;
    }

    public void setSubTotalPendienteConIVA(BigDecimal subTotalPendienteConIVA) {
        this.subTotalPendienteConIVA = subTotalPendienteConIVA;
    }

    public int getCantidadItems() {
        return cantidadItems;
    }

    public void setCantidadItems(int cantidadItems) {
        this.cantidadItems = cantidadItems;
    }

    public boolean isPersistido() {
        return persistido;
    }

    public void setPersistido(boolean persistido) {
        this.persistido = persistido;
    }

    public Comprobante getComprobanteTesoreria() {
        return comprobanteTesoreria;
    }

    public void setComprobanteTesoreria(Comprobante Comprobante) {
        this.comprobanteTesoreria = Comprobante;
    }

    public Comprobante getComprobanteStock() {
        return comprobanteStock;
    }

    public void setComprobanteStock(Comprobante comprobanteStock) {
        this.comprobanteStock = comprobanteStock;
    }

    public BigDecimal getImpGravado0000() {
        return impGravado0000;
    }

    public void setImpGravado0000(BigDecimal impGravado0000) {
        this.impGravado0000 = impGravado0000;
    }

    public BigDecimal getImpGravado2100() {
        return impGravado2100;
    }

    public void setImpGravado2100(BigDecimal impGravado2100) {
        this.impGravado2100 = impGravado2100;
    }

    public BigDecimal getImpGravado1050() {
        return impGravado1050;
    }

    public void setImpGravado1050(BigDecimal impGravado1050) {
        this.impGravado1050 = impGravado1050;
    }

    public BigDecimal getImpGravado2700() {
        return impGravado2700;
    }

    public void setImpGravado2700(BigDecimal impGravado2700) {
        this.impGravado2700 = impGravado2700;
    }

    public BigDecimal getImpIva2100() {
        return impIva2100;
    }

    public void setImpIva2100(BigDecimal impIva2100) {
        this.impIva2100 = impIva2100;
    }

    public BigDecimal getImpIva1050() {
        return impIva1050;
    }

    public void setImpIva1050(BigDecimal impIva1050) {
        this.impIva1050 = impIva1050;
    }

    public BigDecimal getImpIva2700() {
        return impIva2700;
    }

    public void setImpIva2700(BigDecimal impIva2700) {
        this.impIva2700 = impIva2700;
    }

    public BigDecimal getImpBruto() {
        return impBruto;
    }

    public void setImpBruto(BigDecimal impBruto) {
        this.impBruto = impBruto;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public List<ItemProductoProveedor> getItemsProductoProveedor() {
        return itemsProductoProveedor;
    }

    public void setItemsProductoProveedor(List<ItemProductoProveedor> itemsProductoProveedor) {
        this.itemsProductoProveedor = itemsProductoProveedor;
    }

    public List<ItemImpuestoProveedor> getItemsImpuestoProveedor() {
        return itemsImpuestoProveedor;
    }

    public void setItemsImpuestoProveedor(List<ItemImpuestoProveedor> itemsImpuestoProveedor) {
        this.itemsImpuestoProveedor = itemsImpuestoProveedor;
    }

    public List<ItemRetencionProveedor> getItemsRetencionProveedor() {
        return itemsRetencionProveedor;
    }

    public void setItemsRetencionProveedor(List<ItemRetencionProveedor> itemsRetencionProveedor) {
        this.itemsRetencionProveedor = itemsRetencionProveedor;
    }

    public List<ItemPercepcionProveedor> getItemsPercepcionProveedor() {
        return itemsPercepcionProveedor;
    }

    public void setItemsPercepcionProveedor(List<ItemPercepcionProveedor> itemsPercepcionProveedor) {
        this.itemsPercepcionProveedor = itemsPercepcionProveedor;
    }

    public List<ItemTotalProveedor> getItemsTotalProveedor() {
        return itemsTotalProveedor;
    }

    public void setItemsTotalProveedor(List<ItemTotalProveedor> itemsTotalProveedor) {
        this.itemsTotalProveedor = itemsTotalProveedor;
    }

    public List<AplicacionCuentaCorrienteProveedor> getItemCuentaCorriente() {
        return itemCuentaCorriente;
    }

    public void setItemCuentaCorriente(List<AplicacionCuentaCorrienteProveedor> itemCuentaCorriente) {
        this.itemCuentaCorriente = itemCuentaCorriente;
    }

    public List<ItemMovimientoTesoreria> getItemsOtraCuentaDebe() {
        return itemsOtraCuentaDebe;
    }

    public void setItemsOtraCuentaDebe(List<ItemMovimientoTesoreria> itemsOtraCuentaDebe) {
        this.itemsOtraCuentaDebe = itemsOtraCuentaDebe;
    }

    public List<ItemMovimientoTesoreria> getItemsValoresDebe() {
        return itemsValoresDebe;
    }

    public void setItemsValoresDebe(List<ItemMovimientoTesoreria> itemsValoresDebe) {
        this.itemsValoresDebe = itemsValoresDebe;
    }

    public List<ItemMovimientoTesoreria> getItemsBancoDebe() {
        return itemsBancoDebe;
    }

    public void setItemsBancoDebe(List<ItemMovimientoTesoreria> itemsBancoDebe) {
        this.itemsBancoDebe = itemsBancoDebe;
    }

    public List<ItemMovimientoTesoreria> getItemsDocumentoPagarDebe() {
        return itemsDocumentoPagarDebe;
    }

    public void setItemsDocumentoPagarDebe(List<ItemMovimientoTesoreria> itemsDocumentoPagarDebe) {
        this.itemsDocumentoPagarDebe = itemsDocumentoPagarDebe;
    }

    public List<ItemMovimientoTesoreria> getItemsDocumentoCobrarDebe() {
        return itemsDocumentoCobrarDebe;
    }

    public void setItemsDocumentoCobrarDebe(List<ItemMovimientoTesoreria> itemsDocumentoCobrarDebe) {
        this.itemsDocumentoCobrarDebe = itemsDocumentoCobrarDebe;
    }

    public List<ItemMovimientoTesoreria> getItemsTarjetaDebe() {
        return itemsTarjetaDebe;
    }

    public void setItemsTarjetaDebe(List<ItemMovimientoTesoreria> itemsTarjetaDebe) {
        this.itemsTarjetaDebe = itemsTarjetaDebe;
    }

    public List<ItemMovimientoTesoreria> getItemsOtraCuentaHaber() {
        return itemsOtraCuentaHaber;
    }

    public void setItemsOtraCuentaHaber(List<ItemMovimientoTesoreria> itemsOtraCuentaHaber) {
        this.itemsOtraCuentaHaber = itemsOtraCuentaHaber;
    }

    public List<ItemMovimientoTesoreria> getItemsValoresHaber() {
        return itemsValoresHaber;
    }

    public void setItemsValoresHaber(List<ItemMovimientoTesoreria> itemsValoresHaber) {
        this.itemsValoresHaber = itemsValoresHaber;
    }

    public List<ItemMovimientoTesoreria> getItemsBancoHaber() {
        return itemsBancoHaber;
    }

    public void setItemsBancoHaber(List<ItemMovimientoTesoreria> itemsBancoHaber) {
        this.itemsBancoHaber = itemsBancoHaber;
    }

    public List<ItemMovimientoTesoreria> getItemsDocumentoPagarHaber() {
        return itemsDocumentoPagarHaber;
    }

    public void setItemsDocumentoPagarHaber(List<ItemMovimientoTesoreria> itemsDocumentoPagarHaber) {
        this.itemsDocumentoPagarHaber = itemsDocumentoPagarHaber;
    }

    public List<ItemMovimientoTesoreria> getItemsDocumentoCobrarHaber() {
        return itemsDocumentoCobrarHaber;
    }

    public void setItemsDocumentoCobrarHaber(List<ItemMovimientoTesoreria> itemsDocumentoCobrarHaber) {
        this.itemsDocumentoCobrarHaber = itemsDocumentoCobrarHaber;
    }

    public List<ItemMovimientoTesoreria> getItemsTarjetaHaber() {
        return itemsTarjetaHaber;
    }

    public void setItemsTarjetaHaber(List<ItemMovimientoTesoreria> itemsTarjetaHaber) {
        this.itemsTarjetaHaber = itemsTarjetaHaber;
    }

    public ListaCosto getListaCosto() {
        return listaCosto;
    }

    public void setListaCosto(ListaCosto listaCosto) {
        this.listaCosto = listaCosto;
    }

    public Comprobante getComprobanteProveedor() {
        return comprobanteProveedor;
    }

    public void setComprobanteProveedor(Comprobante comprobanteProveedor) {
        this.comprobanteProveedor = comprobanteProveedor;
    }

    public MovimientoProveedor getMovimientoProveedor() {
        return movimientoProveedor;
    }

    public void setMovimientoProveedor(MovimientoProveedor movimientoProveedor) {
        this.movimientoProveedor = movimientoProveedor;
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

    public BigDecimal getImpPercepcionIB() {
        return impPercepcionIB;
    }

    public void setImpPercepcionIB(BigDecimal impPercepcionIB) {
        this.impPercepcionIB = impPercepcionIB;
    }

    public BigDecimal getImpPercepcionGAN() {
        return impPercepcionGAN;
    }

    public void setImpPercepcionGAN(BigDecimal impPercepcionGAN) {
        this.impPercepcionGAN = impPercepcionGAN;
    }

    public BigDecimal getImpPercepcionIVA() {
        return impPercepcionIVA;
    }

    public void setImpPercepcionIVA(BigDecimal impPercepcionIVA) {
        this.impPercepcionIVA = impPercepcionIVA;
    }

    public BigDecimal getImpPercepcionITC() {
        return impPercepcionITC;
    }

    public void setImpPercepcionITC(BigDecimal impPercepcionITC) {
        this.impPercepcionITC = impPercepcionITC;
    }

    public BigDecimal getImpIva0250() {
        return impIva0250;
    }

    public void setImpIva0250(BigDecimal impIva0250) {
        this.impIva0250 = impIva0250;
    }

    public BigDecimal getImpPercepcionINT() {
        return impPercepcionINT;
    }

    public void setImpPercepcionINT(BigDecimal impPercepcionINT) {
        this.impPercepcionINT = impPercepcionINT;
    }

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    public PuntoVenta getPuntoVentaStock() {
        return puntoVentaStock;
    }

    public void setPuntoVentaStock(PuntoVenta puntoVentaStock) {
        this.puntoVentaStock = puntoVentaStock;
    }

    public List<ItemMovimientoCompra> getItemsProducto() {
        return itemsProducto;
    }

    public void setItemsProducto(List<ItemMovimientoCompra> itemsProducto) {
        this.itemsProducto = itemsProducto;
    }

    public DireccionEntregaEntidad getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(DireccionEntregaEntidad direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public String getAtributo1() {
        return atributo1;
    }

    public void setAtributo1(String atributo1) {
        this.atributo1 = atributo1;
    }

    public String getAtributo2() {
        return atributo2;
    }

    public void setAtributo2(String atributo2) {
        this.atributo2 = atributo2;
    }

    public String getAtributo3() {
        return atributo3;
    }

    public void setAtributo3(String atributo3) {
        this.atributo3 = atributo3;
    }

    public String getAtributo4() {
        return atributo4;
    }

    public void setAtributo4(String atributo4) {
        this.atributo4 = atributo4;
    }

    public String getAtributo5() {
        return atributo5;
    }

    public void setAtributo5(String atributo5) {
        this.atributo5 = atributo5;
    }

    public String getAtributo6() {
        return atributo6;
    }

    public void setAtributo6(String atributo6) {
        this.atributo6 = atributo6;
    }

    public String getAtributo7() {
        return atributo7;
    }

    public void setAtributo7(String atributo7) {
        this.atributo7 = atributo7;
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

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public EntidadCamion getCamion() {
        return camion;
    }

    public void setCamion(EntidadCamion camion) {
        this.camion = camion;
    }

    public EntidadChofer getChofer() {
        return chofer;
    }

    public void setChofer(EntidadChofer chofer) {
        this.chofer = chofer;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final MovimientoCompra other = (MovimientoCompra) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MovimientoCompra{" + "id=" + id + '}';
    }

}
