/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.facturacion.modelo;

import bs.administracion.modelo.CorreoElectronico;
import bs.entidad.modelo.DireccionEntregaEntidad;
import bs.entidad.modelo.EntidadCamion;
import bs.entidad.modelo.EntidadChofer;
import bs.entidad.modelo.EntidadComercial;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.ArchivoAdjunto;
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
import bs.stock.modelo.Deposito;
import bs.stock.modelo.MovimientoStock;
import bs.stock.modelo.Producto;
import bs.taller.modelo.MovimientoTaller;
import bs.tesoreria.modelo.ItemMovimientoTesoreria;
import bs.tesoreria.modelo.MovimientoTesoreria;
import bs.ventas.modelo.AplicacionCuentaCorrienteVenta;
import bs.ventas.modelo.CanalVenta;
import bs.ventas.modelo.CondicionDePagoVenta;
import bs.ventas.modelo.ItemImpuestoVenta;
import bs.ventas.modelo.ItemPercepcionVenta;
import bs.ventas.modelo.ItemProductoVenta;
import bs.ventas.modelo.ItemTotalVenta;
import bs.ventas.modelo.ListaPrecioVenta;
import bs.ventas.modelo.MovimientoVenta;
import bs.ventas.modelo.Repartidor;
import bs.ventas.modelo.Vendedor;
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
 * @author ctrosch
 */
@Entity
@Table(name = "fc_movimiento")
@EntityListeners(AuditoriaListener.class)
public class MovimientoFacturacion implements IAuditableEntity, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @JoinColumns({
        @JoinColumn(name = "CIRCOM", referencedColumnName = "CIRCOM", nullable = false),
        @JoinColumn(name = "CIRAPL", referencedColumnName = "CIRAPL", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private CircuitoFacturacion circuito;

//    //Circuito inicio
//    @JoinColumn(name = "CIRINI", referencedColumnName = "CODIGO")
//    @ManyToOne(fetch = FetchType.LAZY)
//    private CodigoCircuitoFacturacion circuitoInicio;
    //Comprobante de Facturacion
    @JoinColumns({
        @JoinColumn(name = "MODCOM", referencedColumnName = "MODCOM", nullable = false),
        @JoinColumn(name = "CODCOM", referencedColumnName = "CODCOM", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Comprobante comprobante;

    //Comprobante de Facturacion original
    @JoinColumns({
        @JoinColumn(name = "COMORI", referencedColumnName = "MODCOM", nullable = false),
        @JoinColumn(name = "CODORI", referencedColumnName = "CODCOM", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Comprobante comprobanteOriginal;

    @JoinColumns({
        @JoinColumn(name = "MODFOR", referencedColumnName = "MODFOR", nullable = false),
        @JoinColumn(name = "CODFOR", referencedColumnName = "CODFOR", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Formulario formulario;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MVT", referencedColumnName = "ID")
    MovimientoVenta movimientoVenta;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MCJ", referencedColumnName = "ID")
    MovimientoTesoreria movimientoTesoreria;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_MST", referencedColumnName = "ID")
    MovimientoStock movimientoStock;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "ID_MTL", referencedColumnName = "ID")
    MovimientoTaller movimientoTaller;

    //EntidadComercial
    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial cliente;

    @Column(name = "RAZONS", nullable = false, length = 200)
    private String razonSocial;

    //EntidadComercial
    @JoinColumn(name = "NROSUB", referencedColumnName = "NROCTA", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial clienteCuentaCorriente;

    @JoinColumn(name = "TIPDOC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento tipoDocumento;

    @Column(name = "NRODOC", length = 50)
    private String nrodoc;

    //Direccion de entrega
    @JoinColumns({
        @JoinColumn(name = "CODENT", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "CTAENT", referencedColumnName = "NROCTA", nullable = false)
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
    @JoinColumn(name = "CODPOS", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Localidad localidad;

    //Transportista
    @JoinColumn(name = "CODTRA", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial transporte;

//    @JoinColumn(name = "CTATRA", referencedColumnName = "NROCTA", nullable = false)
//    @ManyToOne(fetch = FetchType.LAZY)
//    private EntidadComercial entidadTransporte;
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

    @JoinColumn(name = "REM_NROCTA", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial remitenteCliente;

    @Column(name = "REM_RAZONS", nullable = false, length = 200)
    private String remitenteRazonSocial;

    @JoinColumn(name = "REM_TIPDOC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento remitenteTipoDocumento;

    @Column(name = "REM_NRODOC", length = 50)
    private String remitenteNrodoc;

    @JoinColumn(name = "REM_CNDIVA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDeIva remitenteCondicionDeIva;

    @JoinColumns({
        @JoinColumn(name = "REM_CODPRO", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "REM_CODPAI", referencedColumnName = "CODPAI", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Provincia remitenteProvincia;

    //Codigo postal entrega
    @JoinColumn(name = "REM_CODPOS", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Localidad remitenteLocalidad;

    @Column(name = "REM_DIRECC", length = 60)
    private String remitenteDireccion;
    @Column(name = "REM_NUMERO", length = 20)
    private String remitenteNumero;

    @JoinColumn(name = "DES_NROCTA", referencedColumnName = "NROCTA", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial destinatarioCliente;

    @Column(name = "DES_RAZONS", nullable = false, length = 200)
    private String destinatarioRazonSocial;

    @JoinColumn(name = "DES_TIPDOC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento destinatarioTipoDocumento;

    @Column(name = "DES_NRODOC", length = 50)
    private String destinatarioNrodoc;

    @JoinColumn(name = "DES_CNDIVA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDeIva destinatarioCondicionDeIva;

    @JoinColumns({
        @JoinColumn(name = "DES_CODPRO", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "DES_CODPAI", referencedColumnName = "CODPAI", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Provincia destinatarioProvincia;

    //Codigo postal entrega
    @JoinColumn(name = "DES_CODPOS", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Localidad destinatarioLocalidad;

    @Column(name = "DES_DIRECC", length = 60)
    private String destinatarioDireccion;
    @Column(name = "DES_NUMERO", length = 20)
    private String destinatarioNumero;

    @JoinColumn(name = "VNDDOR", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Vendedor vendedor;

    @JoinColumn(name = "REPDOR", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Repartidor repartidor;

    //Condicion de pago
    @JoinColumn(name = "CNDPAG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDePagoVenta condicionDePago;

    @JoinColumn(name = "DEPOSI", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito deposito;

    @JoinColumn(name = "DEPTRA", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Deposito depositoTransferencia;

    //Lista de precio
    @JoinColumn(name = "CODLIS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ListaPrecioVenta listaDePrecio;

    //Moneda lista de precios
    @JoinColumn(name = "COFLIS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaListaPrecio;

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

    //Congela precio
    @Column(name = "CONGEL", length = 1)
    private String congelaPrecio;

    //Observaciones
    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    //Condicion de iva
    @JoinColumn(name = "CNDIVA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDeIva condicionDeIva;

    //Congela bonificacion
    @Column(name = "CONBON", length = 1)
    private String congelaBonificacion;

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

    //Fecha para calculo de vencimiento
    @Column(name = "FCHVEN")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @Column(name = "PCTFIN", precision = 15, scale = 4)
    private BigDecimal porcentajeFinanciacion;
    @Column(name = "TASAIN", precision = 15, scale = 4)
    private BigDecimal tasaInteres;

    @Column(name = "PCTBF1", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion4;
    @Column(name = "PCTBF2", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion5;
    @Column(name = "PCTBF3", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion6;

    @Column(name = "TIPEXP", length = 6)
    private String tipoExportacion;

    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto productoReferencia;

    //Canal de Venta
    @JoinColumn(name = "CANVTA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CanalVenta canalVenta;

    @Column(name = "BIENUSO", length = 1)
    private String bienDeUso;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("nroitm")
    private List<ItemMovimientoFacturacion> itemsProducto;

    @OneToMany(mappedBy = "movimientoFacturacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("fechaEnvio")
    private List<CorreoElectronico> correos;

    @OneToMany(mappedBy = "movimientoFacturacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("fechaAdjunto")
    private List<ArchivoAdjunto> archivosAdjuntos;

    @Embedded
    private Auditoria auditoria;

    @Transient
    private PuntoVenta puntoVentaStock;
    @Transient
    private boolean pideCostoItem;

    @Transient
    private BigDecimal subTotalConIVA;
    @Transient
    private BigDecimal subTotalPendienteConIVA;
    @Transient
    private int cantidadItems;

    @Transient
    private boolean persistido;

    @Transient
    private boolean seleccionado;

    @Transient
    private Comprobante comprobanteVenta;
    @Transient
    private Comprobante Comprobante;
    @Transient
    private Comprobante comprobanteStock;

    @Transient
    double contribucionMarginal = 0;

    @Transient
    BigDecimal impGravado0000 = BigDecimal.ZERO;
    @Transient
    BigDecimal impGravado2100 = BigDecimal.ZERO;
    @Transient
    BigDecimal impGravado1050 = BigDecimal.ZERO;
    @Transient
    BigDecimal impGravado2700 = BigDecimal.ZERO;
    @Transient
    BigDecimal impIva2100 = BigDecimal.ZERO;
    @Transient
    BigDecimal impIva1050 = BigDecimal.ZERO;
    @Transient
    BigDecimal impIva2700 = BigDecimal.ZERO;
    @Transient
    BigDecimal impBruto = BigDecimal.ZERO;
    @Transient
    BigDecimal impGravado = BigDecimal.ZERO;

    @Transient
    BigDecimal impBonificacion4 = BigDecimal.ZERO;
    @Transient
    BigDecimal impBonificacion5 = BigDecimal.ZERO;
    @Transient
    BigDecimal impBonificacion6 = BigDecimal.ZERO;

    @Transient
    BigDecimal importeTotal = BigDecimal.ZERO;

    @Transient
    BigDecimal importeRecibido = BigDecimal.ZERO;

    @Transient
    BigDecimal importeVuelto = BigDecimal.ZERO;

    /*
    * SOPORTE PARA GENERAR MOVIMIENTOS DE PROVEEDOR
     */
    @Transient
    private List<ItemProductoVenta> itemsProductoVenta;
    @Transient
    private List<ItemImpuestoVenta> itemsImpuestoVenta;
    @Transient
    private List<ItemPercepcionVenta> itemsPercepcionVenta;
    @Transient
    private List<ItemTotalVenta> itemsTotalVenta;
    @Transient
    private List<AplicacionCuentaCorrienteVenta> itemCuentaCorrienteVenta;


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
    private List<AplicacionCuentaCorrienteVenta> itemCuentaCorriente;

    @Transient
    private int numeroCpbteAsociado;

    @Transient
    private Date fechaCpbteAsociado;

    public MovimientoFacturacion() {

        auditoria = new Auditoria();
        this.cotizacion = BigDecimal.ONE;
        this.tipoExportacion = "3";
        this.bienDeUso = "N";

        porcentajeBonificacion4 = BigDecimal.ZERO;
        porcentajeBonificacion5 = BigDecimal.ZERO;
        porcentajeBonificacion6 = BigDecimal.ZERO;

        itemsProducto = new ArrayList<>();

        itemsProductoVenta = new ArrayList<>();
        itemsImpuestoVenta = new ArrayList<>();
        itemsPercepcionVenta = new ArrayList<>();
        itemsTotalVenta = new ArrayList<>();

        itemCuentaCorriente = new ArrayList<>();

        correos = new ArrayList<>();
    }

    public MovimientoFacturacion(Integer id) {

        this.id = id;
        auditoria = new Auditoria();
        this.cotizacion = BigDecimal.ONE;
        this.tipoExportacion = "3";
        this.bienDeUso = "N";

        porcentajeBonificacion4 = BigDecimal.ZERO;
        porcentajeBonificacion5 = BigDecimal.ZERO;
        porcentajeBonificacion6 = BigDecimal.ZERO;

        itemsProducto = new ArrayList<ItemMovimientoFacturacion>();

        itemsProductoVenta = new ArrayList<ItemProductoVenta>();
        itemsImpuestoVenta = new ArrayList<ItemImpuestoVenta>();
        itemsPercepcionVenta = new ArrayList<ItemPercepcionVenta>();
        itemsTotalVenta = new ArrayList<ItemTotalVenta>();

        itemCuentaCorriente = new ArrayList<AplicacionCuentaCorrienteVenta>();

    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public CircuitoFacturacion getCircuito() {
        return circuito;
    }

    public void setCircuito(CircuitoFacturacion circuito) {
        this.circuito = circuito;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
    }

    public CondicionDePagoVenta getCondicionDePago() {
        return condicionDePago;
    }

    public void setCondicionDePago(CondicionDePagoVenta condicionDePago) {
        this.condicionDePago = condicionDePago;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNrodoc() {
        return nrodoc;
    }

    public void setNrodoc(String nrodoc) {
        this.nrodoc = nrodoc;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
//
//    public CodigoCircuitoFacturacion getCircuitoInicio() {
//        return circuitoInicio;
//    }
//
//    public void setCircuitoInicio(CodigoCircuitoFacturacion circuitoInicio) {
//        this.circuitoInicio = circuitoInicio;
//    }

    public Comprobante getComprobanteOriginal() {
        return comprobanteOriginal;
    }

    public void setComprobanteOriginal(Comprobante comprobanteOriginal) {
        this.comprobanteOriginal = comprobanteOriginal;
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

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public ListaPrecioVenta getListaDePrecio() {
        return listaDePrecio;
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

    public void setListaDePrecio(ListaPrecioVenta listaDePrecio) {
        this.listaDePrecio = listaDePrecio;
    }

    public String getCongelaPrecio() {
        return congelaPrecio;
    }

    public void setCongelaPrecio(String congelaPrecio) {
        this.congelaPrecio = congelaPrecio;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Moneda getMonedaListaPrecio() {
        return monedaListaPrecio;
    }

    public void setMonedaListaPrecio(Moneda monedaListaPrecio) {
        this.monedaListaPrecio = monedaListaPrecio;
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

    public String getCongelaBonificacion() {
        return congelaBonificacion;
    }

    public void setCongelaBonificacion(String congelaBonificacion) {
        this.congelaBonificacion = congelaBonificacion;
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

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
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

    public BigDecimal getPorcentajeBonificacion4() {
        return porcentajeBonificacion4;
    }

    public void setPorcentajeBonificacion4(BigDecimal porcentajeBonificacion4) {
        this.porcentajeBonificacion4 = porcentajeBonificacion4;
    }

    public BigDecimal getPorcentajeBonificacion5() {
        return porcentajeBonificacion5;
    }

    public void setPorcentajeBonificacion5(BigDecimal porcentajeBonificacion5) {
        this.porcentajeBonificacion5 = porcentajeBonificacion5;
    }

    public BigDecimal getPorcentajeBonificacion6() {
        return porcentajeBonificacion6;
    }

    public void setPorcentajeBonificacion6(BigDecimal porcentajeBonificacion6) {
        this.porcentajeBonificacion6 = porcentajeBonificacion6;
    }

    public String getTipoExportacion() {
        return tipoExportacion;
    }

    public void setTipoExportacion(String tipoExportacion) {
        this.tipoExportacion = tipoExportacion;
    }

    public List<ItemMovimientoFacturacion> getItemsProducto() {
        return itemsProducto;
    }

    public void setItemsProducto(List<ItemMovimientoFacturacion> itemsProducto) {
        this.itemsProducto = itemsProducto;
    }

    public EntidadComercial getClienteCuentaCorriente() {
        return clienteCuentaCorriente;
    }

    public void setClienteCuentaCorriente(EntidadComercial clienteCuentaCorriente) {
        this.clienteCuentaCorriente = clienteCuentaCorriente;
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

    public MovimientoVenta getMovimientoVenta() {
        return movimientoVenta;
    }

    public void setMovimientoVenta(MovimientoVenta movimientoVenta) {
        this.movimientoVenta = movimientoVenta;
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

    public DireccionEntregaEntidad getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(DireccionEntregaEntidad direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
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

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public Comprobante getComprobanteVenta() {
        return comprobanteVenta;
    }

    public void setComprobanteVenta(Comprobante comprobanteVenta) {
        this.comprobanteVenta = comprobanteVenta;
    }

    public Comprobante getComprobanteTesoreria() {
        return Comprobante;
    }

    public void setComprobanteTesoreria(Comprobante Comprobante) {
        this.Comprobante = Comprobante;
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

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
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

    public EntidadComercial getRemitenteCliente() {
        return remitenteCliente;
    }

    public void setRemitenteCliente(EntidadComercial remitenteCliente) {
        this.remitenteCliente = remitenteCliente;
    }

    public String getRemitenteRazonSocial() {
        return remitenteRazonSocial;
    }

    public void setRemitenteRazonSocial(String remitenteRazonSocial) {
        this.remitenteRazonSocial = remitenteRazonSocial;
    }

    public TipoDocumento getRemitenteTipoDocumento() {
        return remitenteTipoDocumento;
    }

    public void setRemitenteTipoDocumento(TipoDocumento remitenteTipoDocumento) {
        this.remitenteTipoDocumento = remitenteTipoDocumento;
    }

    public String getRemitenteNrodoc() {
        return remitenteNrodoc;
    }

    public void setRemitenteNrodoc(String remitenteNrodoc) {
        this.remitenteNrodoc = remitenteNrodoc;
    }

    public CondicionDeIva getRemitenteCondicionDeIva() {
        return remitenteCondicionDeIva;
    }

    public void setRemitenteCondicionDeIva(CondicionDeIva remitenteCondicionDeIva) {
        this.remitenteCondicionDeIva = remitenteCondicionDeIva;
    }

    public Provincia getRemitenteProvincia() {
        return remitenteProvincia;
    }

    public void setRemitenteProvincia(Provincia remitenteProvincia) {
        this.remitenteProvincia = remitenteProvincia;
    }

    public String getRemitenteDireccion() {
        return remitenteDireccion;
    }

    public void setRemitenteDireccion(String remitenteDireccion) {
        this.remitenteDireccion = remitenteDireccion;
    }

    public String getRemitenteNumero() {
        return remitenteNumero;
    }

    public void setRemitenteNumero(String remitenteNumero) {
        this.remitenteNumero = remitenteNumero;
    }

    public EntidadComercial getDestinatarioCliente() {
        return destinatarioCliente;
    }

    public void setDestinatarioCliente(EntidadComercial destinatarioCliente) {
        this.destinatarioCliente = destinatarioCliente;
    }

    public String getDestinatarioRazonSocial() {
        return destinatarioRazonSocial;
    }

    public void setDestinatarioRazonSocial(String destinatarioRazonSocial) {
        this.destinatarioRazonSocial = destinatarioRazonSocial;
    }

    public TipoDocumento getDestinatarioTipoDocumento() {
        return destinatarioTipoDocumento;
    }

    public void setDestinatarioTipoDocumento(TipoDocumento destinatarioTipoDocumento) {
        this.destinatarioTipoDocumento = destinatarioTipoDocumento;
    }

    public String getDestinatarioNrodoc() {
        return destinatarioNrodoc;
    }

    public void setDestinatarioNrodoc(String destinatarioNrodoc) {
        this.destinatarioNrodoc = destinatarioNrodoc;
    }

    public CondicionDeIva getDestinatarioCondicionDeIva() {
        return destinatarioCondicionDeIva;
    }

    public void setDestinatarioCondicionDeIva(CondicionDeIva destinatarioCondicionDeIva) {
        this.destinatarioCondicionDeIva = destinatarioCondicionDeIva;
    }

    public Provincia getDestinatarioProvincia() {
        return destinatarioProvincia;
    }

    public void setDestinatarioProvincia(Provincia destinatarioProvincia) {
        this.destinatarioProvincia = destinatarioProvincia;
    }

    public Localidad getRemitenteLocalidad() {
        return remitenteLocalidad;
    }

    public void setRemitenteLocalidad(Localidad remitenteLocalidad) {
        this.remitenteLocalidad = remitenteLocalidad;
    }

    public Localidad getDestinatarioLocalidad() {
        return destinatarioLocalidad;
    }

    public void setDestinatarioLocalidad(Localidad destinatarioLocalidad) {
        this.destinatarioLocalidad = destinatarioLocalidad;
    }

    public String getDestinatarioDireccion() {
        return destinatarioDireccion;
    }

    public void setDestinatarioDireccion(String destinatarioDireccion) {
        this.destinatarioDireccion = destinatarioDireccion;
    }

    public String getDestinatarioNumero() {
        return destinatarioNumero;
    }

    public void setDestinatarioNumero(String destinatarioNumero) {
        this.destinatarioNumero = destinatarioNumero;
    }

    public String getPagoFlete() {
        return pagoFlete;
    }

    public void setPagoFlete(String pagoFlete) {
        this.pagoFlete = pagoFlete;
    }

    public BigDecimal getImpBonificacion4() {
        return impBonificacion4;
    }

    public void setImpBonificacion4(BigDecimal impBonificacion4) {
        this.impBonificacion4 = impBonificacion4;
    }

    public BigDecimal getImpBonificacion5() {
        return impBonificacion5;
    }

    public void setImpBonificacion5(BigDecimal impBonificacion5) {
        this.impBonificacion5 = impBonificacion5;
    }

    public BigDecimal getImpBonificacion6() {
        return impBonificacion6;
    }

    public void setImpBonificacion6(BigDecimal impBonificacion6) {
        this.impBonificacion6 = impBonificacion6;
    }

    public BigDecimal getImpGravado() {
        return impGravado;
    }

    public void setImpGravado(BigDecimal impGravado) {
        this.impGravado = impGravado;
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

    public List<ItemProductoVenta> getItemsProductoVenta() {
        return itemsProductoVenta;
    }

    public void setItemsProductoVenta(List<ItemProductoVenta> itemsProductoVenta) {
        this.itemsProductoVenta = itemsProductoVenta;
    }

    public List<ItemImpuestoVenta> getItemsImpuestoVenta() {
        return itemsImpuestoVenta;
    }

    public void setItemsImpuestoVenta(List<ItemImpuestoVenta> itemsImpuestoVenta) {
        this.itemsImpuestoVenta = itemsImpuestoVenta;
    }

    public List<ItemPercepcionVenta> getItemsPercepcionVenta() {
        return itemsPercepcionVenta;
    }

    public void setItemsPercepcionVenta(List<ItemPercepcionVenta> itemsPercepcionVenta) {
        this.itemsPercepcionVenta = itemsPercepcionVenta;
    }

    public List<ItemTotalVenta> getItemsTotalVenta() {
        return itemsTotalVenta;
    }

    public void setItemsTotalVenta(List<ItemTotalVenta> itemsTotalVenta) {
        this.itemsTotalVenta = itemsTotalVenta;
    }

    public List<AplicacionCuentaCorrienteVenta> getItemCuentaCorrienteVenta() {
        return itemCuentaCorrienteVenta;
    }

    public void setItemCuentaCorrienteVenta(List<AplicacionCuentaCorrienteVenta> itemCuentaCorrienteVenta) {
        this.itemCuentaCorrienteVenta = itemCuentaCorrienteVenta;
    }

    public Producto getProductoReferencia() {
        return productoReferencia;
    }

    public void setProductoReferencia(Producto productoReferencia) {
        this.productoReferencia = productoReferencia;
    }

    public CanalVenta getCanalVenta() {
        return canalVenta;
    }

    public void setCanalVenta(CanalVenta canalVenta) {
        this.canalVenta = canalVenta;
    }

    public String getBienDeUso() {
        return bienDeUso;
    }

    public void setBienDeUso(String bienDeUso) {
        this.bienDeUso = bienDeUso;
    }

    public MovimientoTaller getMovimientoTaller() {
        return movimientoTaller;
    }

    public void setMovimientoTaller(MovimientoTaller movimientoTaller) {
        this.movimientoTaller = movimientoTaller;
    }

    public BigDecimal getImporteRecibido() {
        return importeRecibido;
    }

    public void setImporteRecibido(BigDecimal importeRecibido) {
        this.importeRecibido = importeRecibido;
    }

    public BigDecimal getImporteVuelto() {
        return importeVuelto;
    }

    public void setImporteVuelto(BigDecimal importeVuelto) {
        this.importeVuelto = importeVuelto;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public boolean isPideCostoItem() {
        return pideCostoItem;
    }

    public void setPideCostoItem(boolean pideCostoItem) {
        this.pideCostoItem = pideCostoItem;
    }

    public List<AplicacionCuentaCorrienteVenta> getItemCuentaCorriente() {
        return itemCuentaCorriente;
    }

    public void setItemCuentaCorriente(List<AplicacionCuentaCorrienteVenta> itemCuentaCorriente) {
        this.itemCuentaCorriente = itemCuentaCorriente;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
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

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public List<CorreoElectronico> getCorreos() {
        return correos;
    }

    public void setCorreos(List<CorreoElectronico> correos) {
        this.correos = correos;
    }

    public List<ArchivoAdjunto> getArchivosAdjuntos() {
        return archivosAdjuntos;
    }

    public void setArchivosAdjuntos(List<ArchivoAdjunto> archivosAdjuntos) {
        this.archivosAdjuntos = archivosAdjuntos;
    }

    public int getNumeroCpbteAsociado() {
        return numeroCpbteAsociado;
    }

    public void setNumeroCpbteAsociado(int numeroCpbteAsociado) {
        this.numeroCpbteAsociado = numeroCpbteAsociado;
    }

    public Date getFechaCpbteAsociado() {
        return fechaCpbteAsociado;
    }

    public void setFechaCpbteAsociado(Date fechaCpbteAsociado) {
        this.fechaCpbteAsociado = fechaCpbteAsociado;
    }

    public double getContribucionMarginal() {

        contribucionMarginal = 0;

        double total = 0;
        double costo = 0;

        if (itemsProducto != null) {

            total = itemsProducto.stream()
                    .filter(f -> f.getCantidad() != null && f.getTotalLinea() != null)
                    .mapToDouble(i -> i.getCantidad().doubleValue() * i.getTotalLinea().doubleValue()).sum();
            costo = itemsProducto.stream()
                    .filter(f -> f.getCantidad() != null && f.getPrecioCosto() != null)
                    .mapToDouble(i -> i.getCantidad().doubleValue() * i.getPrecioCosto().doubleValue()).sum();

        }

        if (total == 0) {
            contribucionMarginal = 0;
        } else {
            contribucionMarginal = (total - costo) / total * 100;
        }

        return contribucionMarginal;
    }

    public void setContribucionMarginal(double contribucionMarginal) {
        this.contribucionMarginal = contribucionMarginal;
    }

    public EntidadComercial getTransporte() {
        return transporte;
    }

    public void setTransporte(EntidadComercial transporte) {
        this.transporte = transporte;
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
        final MovimientoFacturacion other = (MovimientoFacturacion) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MovimientoFacturacion{" + "id=" + id + '}';
    }
}
