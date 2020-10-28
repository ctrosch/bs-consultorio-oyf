/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.ventas.modelo;

import bs.contabilidad.modelo.MovimientoContabilidad;
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
import bs.global.modelo.Zona;
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
@Table(name = "vt_movimiento")
@EntityListeners(AuditoriaListener.class)
public class MovimientoVenta implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    //Comprobante de Venta
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

    //Observaciones textos
    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;
    @Basic(optional = false)

    //Fecha del movimiento
    @Column(name = "FCHMOV")
    @Temporal(TemporalType.DATE)
    private Date fechaMovimiento;

    @Column(name = "FCHVEN")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    //Cliente
    @JoinColumn(name = "NROCTA", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial cliente;

    @Column(name = "RAZONS", nullable = false, length = 200)
    private String razonSocial;

    @JoinColumn(name = "TIPDOC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoDocumento tipoDocumento;

    @Column(name = "NRODOC", length = 50)
    private String nrodoc;

    //Direccion de entrega
    @JoinColumns({
        @JoinColumn(name = "CODENT", referencedColumnName = "CODIGO", nullable = false),
        @JoinColumn(name = "CTAENT", referencedColumnName = "NROCTA", nullable = false, insertable = false, updatable = false)
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

    @JoinColumn(name = "IDCAMI", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadCamion camion;

    @JoinColumn(name = "IDCHOF", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadChofer chofer;

    @Column(name = "DIRECC", length = 60)
    private String direccion;
//    @Column(name = "NUMERO", length = 20)
//    private String numero;
//    @Column(name = "DPPISO", length = 6)
//    private String departamentoPiso;
//    @Column(name = "DEPTOS", length = 6)
//    private String departamentoNumero;
    @Column(name = "BARRIO", length = 6)
    private String barrio;

    //EntidadComercial
    @JoinColumn(name = "NROSUB", referencedColumnName = "NROCTA", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial clienteCuentaCorriente;

    //Condicion de pago
    @JoinColumn(name = "CNDPAG", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDePagoVenta condicionDePago;

    //VT_Vendedor
    @JoinColumn(name = "VNDDOR", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Vendedor vendedor;

    @JoinColumn(name = "COBRAD", referencedColumnName = "CODIGO", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Cobrador cobrador;

    @JoinColumn(name = "REPDOR", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Repartidor repartidor;

    //Lista de precio
    @JoinColumn(name = "CODLIS", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private ListaPrecioVenta listaDePrecio;

    //Moneda lista de precios
    @JoinColumn(name = "COFLIS", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaListaPrecio;

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

    @JoinColumn(name = "CODZON", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Zona zona;

    //Condicion de iva
    @JoinColumn(name = "CNDIVA", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDeIva condicionDeIva;

    @Column(name = "INSCAE", length = 20)
    private String instanciaCAE;
    @Column(name = "NROCAE", length = 20)
    private String numeroCAE;
    @Column(name = "VENCAE")
    @Temporal(TemporalType.DATE)
    private Date vencimientoCAE;
    @Column(name = "TIPEXP", length = 6)
    private String tipoExportacion;
    @Column(name = "BIENUSO", length = 1)
    private String bienDeUso;

    @JoinColumn(name = "ID_MREV", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoVenta movimientoReversion;

    @JoinColumn(name = "ID_MCG", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private MovimientoContabilidad movimientoContabilidad;

    @JoinColumn(name = "ID_MREN", referencedColumnName = "ID")
    @OneToOne(fetch = FetchType.LAZY)
    private MovimientoVenta movimientoRendicion;

    //Canal de Venta
    @JoinColumn(name = "CANVTA", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CanalVenta canalVenta;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemProductoVenta> itemProducto;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemImpuestoVenta> itemImpuesto;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemBonificacionVenta> itemBonificacion;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemPercepcionVenta> itemPercepcion;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemTotalVenta> itemTotal;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy(value = "importe")
    private List<AplicacionCuentaCorrienteVenta> itemCuentaCorriente;

    @OneToMany(mappedBy = "movimientoAplicacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy(value = "importe")
    private List<AplicacionCuentaCorrienteVenta> itemCuentaCorrienteAplicacion;

    @Embedded
    private Auditoria auditoria;

    @Transient
    BigDecimal importeTotal = BigDecimal.ZERO;

    @Transient
    private String codigoBarra;

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
    private int numeroCpbteAsociado;

    @Transient
    private Date fechaCpbteAsociado;

    public MovimientoVenta() {

        this.cotizacion = BigDecimal.ONE;
        this.bienDeUso = "N";

        itemProducto = new ArrayList<ItemProductoVenta>();
        itemImpuesto = new ArrayList<ItemImpuestoVenta>();
        itemPercepcion = new ArrayList<ItemPercepcionVenta>();
        itemBonificacion = new ArrayList<ItemBonificacionVenta>();
        itemTotal = new ArrayList<ItemTotalVenta>();
        itemCuentaCorriente = new ArrayList<AplicacionCuentaCorrienteVenta>();

        auditoria = new Auditoria();
    }

    public MovimientoVenta(Integer id) {

        this.id = id;
        this.cotizacion = BigDecimal.ONE;
        this.bienDeUso = "N";

        itemProducto = new ArrayList<ItemProductoVenta>();
        itemImpuesto = new ArrayList<ItemImpuestoVenta>();
        itemPercepcion = new ArrayList<ItemPercepcionVenta>();
        itemBonificacion = new ArrayList<ItemBonificacionVenta>();
        itemTotal = new ArrayList<ItemTotalVenta>();
        itemCuentaCorriente = new ArrayList<AplicacionCuentaCorrienteVenta>();

        auditoria = new Auditoria();
    }

    public MovimientoVenta(Integer id, String codcom, Date fechaMovimiento, EntidadComercial cliente) {

        this.id = id;
        this.fechaMovimiento = fechaMovimiento;
        this.cliente = cliente;

        this.cotizacion = BigDecimal.ONE;
        this.bienDeUso = "N";

        itemProducto = new ArrayList<ItemProductoVenta>();
        itemImpuesto = new ArrayList<ItemImpuestoVenta>();
        itemPercepcion = new ArrayList<ItemPercepcionVenta>();
        itemBonificacion = new ArrayList<ItemBonificacionVenta>();
        itemTotal = new ArrayList<ItemTotalVenta>();
        itemCuentaCorriente = new ArrayList<AplicacionCuentaCorrienteVenta>();

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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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

    public EntidadComercial getCliente() {
        return cliente;
    }

    public void setCliente(EntidadComercial cliente) {
        this.cliente = cliente;
    }

    public EntidadComercial getClienteCuentaCorriente() {
        return clienteCuentaCorriente;
    }

    public void setClienteCuentaCorriente(EntidadComercial clienteCuentaCorriente) {
        this.clienteCuentaCorriente = clienteCuentaCorriente;
    }

    public CondicionDePagoVenta getCondicionDePago() {
        return condicionDePago;
    }

    public void setCondicionDePago(CondicionDePagoVenta condicionDePago) {
        this.condicionDePago = condicionDePago;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public Cobrador getCobrador() {
        return cobrador;
    }

    public void setCobrador(Cobrador cobrador) {
        this.cobrador = cobrador;
    }

    public ListaPrecioVenta getListaDePrecio() {
        return listaDePrecio;
    }

    public void setListaDePrecio(ListaPrecioVenta listaDePrecio) {
        this.listaDePrecio = listaDePrecio;
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

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public CondicionDeIva getCondicionDeIva() {
        return condicionDeIva;
    }

    public void setCondicionDeIva(CondicionDeIva condicionDeIva) {
        this.condicionDeIva = condicionDeIva;
    }

    public String getNumeroCAE() {
        return numeroCAE;
    }

    public void setNumeroCAE(String numeroCAE) {
        this.numeroCAE = numeroCAE;
    }

    public Date getVencimientoCAE() {
        return vencimientoCAE;
    }

    public void setVencimientoCAE(Date vencimientoCAE) {
        this.vencimientoCAE = vencimientoCAE;
    }

    public String getTipoExportacion() {
        return tipoExportacion;
    }

    public void setTipoExportacion(String tipoExportacion) {
        this.tipoExportacion = tipoExportacion;
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

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public List<ItemProductoVenta> getItemProducto() {
        return itemProducto;
    }

    public void setItemProducto(List<ItemProductoVenta> itemProducto) {
        this.itemProducto = itemProducto;
    }

    public List<ItemImpuestoVenta> getItemImpuesto() {
        return itemImpuesto;
    }

    public void setItemImpuesto(List<ItemImpuestoVenta> itemImpuesto) {
        this.itemImpuesto = itemImpuesto;
    }

    public List<ItemBonificacionVenta> getItemBonificacion() {
        return itemBonificacion;
    }

    public void setItemBonificacion(List<ItemBonificacionVenta> itemBonificacion) {
        this.itemBonificacion = itemBonificacion;
    }

    public List<ItemPercepcionVenta> getItemPercepcion() {
        return itemPercepcion;
    }

    public void setItemPercepcion(List<ItemPercepcionVenta> itemPercepcion) {
        this.itemPercepcion = itemPercepcion;
    }

    public List<ItemTotalVenta> getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(List<ItemTotalVenta> itemTotal) {
        this.itemTotal = itemTotal;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public List<AplicacionCuentaCorrienteVenta> getItemCuentaCorriente() {
        return itemCuentaCorriente;
    }

    public void setItemCuentaCorriente(List<AplicacionCuentaCorrienteVenta> itemCuentaCorriente) {
        this.itemCuentaCorriente = itemCuentaCorriente;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
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

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public String getEnLetras() {
        return enLetras;
    }

    public void setEnLetras(String enLetras) {
        this.enLetras = enLetras;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getInstanciaCAE() {
        return instanciaCAE;
    }

    public void setInstanciaCAE(String instanciaCAE) {
        this.instanciaCAE = instanciaCAE;
    }

    public MovimientoVenta getMovimientoReversion() {
        return movimientoReversion;
    }

    public void setMovimientoReversion(MovimientoVenta movimientoReversion) {
        this.movimientoReversion = movimientoReversion;
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

    public Moneda getMonedaRegistracion() {
        return monedaRegistracion;
    }

    public void setMonedaRegistracion(Moneda monedaRegistracion) {
        this.monedaRegistracion = monedaRegistracion;
    }

    public String getBienDeUso() {
        return bienDeUso;
    }

    public void setBienDeUso(String bienDeUso) {
        this.bienDeUso = bienDeUso;
    }

    public UnidadNegocio getUnidadNegocio() {
        return unidadNegocio;
    }

    public void setUnidadNegocio(UnidadNegocio unidadNegocio) {
        this.unidadNegocio = unidadNegocio;
    }

    public MovimientoContabilidad getMovimientoContabilidad() {
        return movimientoContabilidad;
    }

    public void setMovimientoContabilidad(MovimientoContabilidad movimientoContabilidad) {
        this.movimientoContabilidad = movimientoContabilidad;
    }

    public MovimientoVenta getMovimientoRendicion() {
        return movimientoRendicion;
    }

    public void setMovimientoRendicion(MovimientoVenta movimientoRendicion) {
        this.movimientoRendicion = movimientoRendicion;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
    }

    public List<AplicacionCuentaCorrienteVenta> getItemCuentaCorrienteAplicacion() {
        return itemCuentaCorrienteAplicacion;
    }

    public void setItemCuentaCorrienteAplicacion(List<AplicacionCuentaCorrienteVenta> itemCuentaCorrienteAplicacion) {
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

    public CanalVenta getCanalVenta() {
        return canalVenta;
    }

    public void setCanalVenta(CanalVenta canalVenta) {
        this.canalVenta = canalVenta;
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
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MovimientoVenta)) {
            return false;
        }
        MovimientoVenta other = (MovimientoVenta) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tv.venta.modelo.MovimientoVenta[id=" + id + "]";
    }

}
