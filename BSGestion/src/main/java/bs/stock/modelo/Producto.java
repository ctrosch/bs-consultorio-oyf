/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.modelo;

import bs.contabilidad.modelo.CuentaContable;
import bs.entidad.modelo.EntidadComercial;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Concepto;
import bs.global.modelo.Moneda;
import bs.produccion.modelo.Sector;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "st_producto")
@EntityListeners(AuditoriaListener.class)
public class Producto implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CODIGO", nullable = false, length = 30)
    private String codigo;

    @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoProducto tipoProducto;

    @Column(name = "DESCRP", nullable = false, length = 120)
    private String descripcion;

    @Column(name = "DESALT", nullable = false, length = 120)
    private String descripcionAlternativa;

    @Lob
    @Column(name = "DETALLE", length = 65535)
    private String detalle;

    //Unidad de medida
    @JoinColumn(name = "UNIMED", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadDeMedida;

    @Column(name = "NROPAR", nullable = false, length = 40)
    private String numeroParte;

    @Column(name = "CODREF", nullable = false, length = 40)
    private String codigoReferencia;

    @Column(name = "PESMIN", precision = 15, scale = 2)
    private double pesoMinimo;

    @Column(name = "PESMAX", precision = 15, scale = 2)
    private double pesosMaximo;

    @Column(name = "PESONT", precision = 15, scale = 2)
    private double pesoNeto;

    @Column(name = "VOLUME", precision = 15, scale = 2)
    private double volumen;

    @JoinColumn(name = "UNIPES", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadDePeso;

//    @Basic(fetch=FetchType.LAZY)
    @Column(name = "STKMIN", precision = 15, scale = 4)
    private BigDecimal stockMinimo;

    //Stock maximo
//    @Basic(fetch=FetchType.LAZY)
    @Column(name = "STKMAX", precision = 15, scale = 4)
    private BigDecimal stockMaximo;

    //Punto de pedido
//    @Basic(fetch=FetchType.LAZY)
    @Column(name = "PTOPED", precision = 15, scale = 4)
    private BigDecimal puntoDePedido;

    //Dias de entrega
    @Column(name = "DIAENT")
    private Short diasEntrega;

    @Column(name = "COSOBL")
    private String pidePrecioCosto;

    //Precio de reposición
    @Column(name = "PREREP", precision = 15, scale = 4)
    private BigDecimal precioReposicion;
    @JoinColumn(name = "MONREP", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaReposicion;
    @Column(name = "FECREP")
    @Temporal(TemporalType.DATE)
    private Date fechaReposicion;

    //Precio ultima compra
    @Column(name = "PREUCO", precision = 15, scale = 4)
    private BigDecimal precioUltimaCompra;
    //GR_Moneda ultima compra
    @JoinColumn(name = "MONUCO", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaUltimaCompra;
    //Fecha ultima compra
    @Column(name = "FECUCO")
    @Temporal(TemporalType.DATE)
    private Date fechaUltimaCompra;

    //Precio de produccion
    @Column(name = "PREPRD", precision = 15, scale = 4)
    private BigDecimal precioProduccion;
    //Modeda de produccion)
    @JoinColumn(name = "MONPRD", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaDeProduccion;
    //Fecha de produccion)
    @Column(name = "FECPRD")
    @Temporal(TemporalType.DATE)
    private Date fechaProduccion;

    //% Utilidad
    @Column(name = "UTILID", precision = 15, scale = 2)
    private BigDecimal utilidad;

    @Column(name = "UTILI2", precision = 15, scale = 2)
    private BigDecimal utilidad2;

    @Column(name = "UTILI3", precision = 15, scale = 2)
    private BigDecimal utilidad3;

    @Column(name = "UTILI4", precision = 15, scale = 2)
    private BigDecimal utilidad4;

    @Column(name = "UTILI5", precision = 15, scale = 2)
    private BigDecimal utilidad5;

    @JoinColumn(name = "PROHAB", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private EntidadComercial proveedorHabitual;

    @Column(name = "CODPRO", nullable = false, length = 25)
    private String codigoProveedor;

    @JoinColumns({
        @JoinColumn(name = "RUBR01", referencedColumnName = "CODIGO", nullable = false, insertable = true, updatable = true),
        @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Rubro01 rubr01;

    @JoinColumns({
        @JoinColumn(name = "RUBR02", referencedColumnName = "CODIGO", nullable = false, insertable = true, updatable = true),
        @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Rubro02 rubr02;

    @JoinColumns({
        @JoinColumn(name = "RUBR03", referencedColumnName = "CODIGO", nullable = false, insertable = true, updatable = true),
        @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Rubro03 rubr03;

    @JoinColumns({
        @JoinColumn(name = "RUBR04", referencedColumnName = "CODIGO", nullable = true, insertable = true, updatable = true),
        @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Rubro04 rubr04;

    @JoinColumns({
        @JoinColumn(name = "RUBR05", referencedColumnName = "CODIGO", nullable = true, insertable = true, updatable = true),
        @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Rubro05 rubr05;

    @JoinColumns({
        @JoinColumn(name = "RUBR06", referencedColumnName = "CODIGO", nullable = true, insertable = true, updatable = true),
        @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Rubro06 rubr06;

    @JoinColumns({
        @JoinColumn(name = "RUBR07", referencedColumnName = "CODIGO", nullable = true, insertable = true, updatable = true),
        @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Rubro07 rubr07;

    @JoinColumns({
        @JoinColumn(name = "RUBR08", referencedColumnName = "CODIGO", nullable = true, insertable = true, updatable = true),
        @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Rubro08 rubr08;

    @JoinColumns({
        @JoinColumn(name = "RUBR09", referencedColumnName = "CODIGO", nullable = true, insertable = true, updatable = true),
        @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO", nullable = false, insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Rubro09 rubr09;

    @Column(name = "STOCKS", length = 1)
    private String gestionaStock;

    @Column(name = "ADATR1", length = 1)
    private String administraAtributo1;

    @Column(name = "ADATR2", length = 1)
    private String administraAtributo2;
    //Numero de envase
    @Column(name = "ADATR3", length = 1)
    private String administraAtributo3;
    //Numero otros
    @Column(name = "ADATR4", length = 1)
    private String administraAtributo4;
    //Numero de atributo
    @Column(name = "ADATR5", length = 1)
    private String administraAtributo5;
    //Numero de estante
    @Column(name = "ADATR6", length = 1)
    private String administraAtributo6;

    @Column(name = "ADATR7", length = 1)
    private String administraAtributo7;

    //Indica si es considerado un bien de uso
    @Column(name = "BIEUSO")
    private String bienDeUso;

    @JoinColumns({
        @JoinColumn(name = "MODCPT", referencedColumnName = "MODULO"),
        @JoinColumn(name = "TIPCPT", referencedColumnName = "TIPCPT"),
        @JoinColumn(name = "CODCPT", referencedColumnName = "CODIGO")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Concepto conceptoVenta;

    @JoinColumns({
        @JoinColumn(name = "MODCPC", referencedColumnName = "MODULO"),
        @JoinColumn(name = "TIPCPC", referencedColumnName = "TIPCPT"),
        @JoinColumn(name = "CODCPC", referencedColumnName = "CODIGO")
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Concepto conceptoProveedor;

    //Cuenta contable de venta
    @JoinColumn(name = "CUENVT", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContableVenta;

    //Cuenta contable de venta
    @JoinColumn(name = "CUENPV", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContableCompra;

    @Column(name = "PNUEVO", length = 1)
    private String productoNuevo;
    //Imagen chica
    @Column(name = "IMGCHI", length = 40)
    private String imagenChica;
    //Imagen grande
    @Column(name = "IMGGRA", length = 40)
    private String imagenGrande;

    @Column(name = "KITSFC", length = 1)
    private String esKitVenta;

    @JoinColumn(name = "KITFOR", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Formula formulaComposicionVenta;

    @Column(name = "IMPCNT", length = 1)
    private String imprimeCantidad;

    @Column(name = "PICKFC", length = 1)
    private String disponibleParaPickingFacturacion;

    @Column(name = "PICKCO", length = 1)
    private String disponibleParaPickingCompras;

    @Column(name = "CODBAR", length = 60)
    private String codigoBarra;

    @Column(name = "NROSER", length = 60)
    private String numeroSerie;

    @Column(name = "GARAN")
    private int garantia;

    @JoinColumn(name = "UNIGAR", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadDeGarantia;

    @Column(name = "PESABL", length = 1)
    private String pesable;

    @Column(name = "CONGEL", length = 1)
    private String congelaPrecioEnFacturacion;

    @Column(name = "VALDUP", length = 1)
    private String validaDuplicidad;

    @Column(name = "EDIDES", length = 1)
    private String editaDescripcion;

    @JoinColumn(name = "CODSEC", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne
    private Sector sectorProduccion;

    @Lob
    @Column(name = "OBSERV", length = 2147483647)
    private String observaciones;

    @OrderBy("nroitm")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto", fetch = FetchType.LAZY)
    private List<EquivalenciaProveedor> equivalenciaProveedor;

    @OrderBy("nroitm")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto", fetch = FetchType.LAZY)
    private List<AplicacionProducto> aplicaciones;

    @OrderBy("nroitm")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto", fetch = FetchType.LAZY)
    private List<AtributoProducto> atributos;

    @OrderBy("nroitm")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto", fetch = FetchType.LAZY)
    private List<ProductoSugerido> sugeridos;

    @OrderBy("nroitm")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto", fetch = FetchType.LAZY)
    private List<ProductoSustituto> sustitutos;

    @OrderBy("nroitm")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto", fetch = FetchType.LAZY)
    private List<GrupoProducto> grupos;

    @OrderBy("nroitm")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto", fetch = FetchType.LAZY)
    private List<ImagenProducto> imagenes;

    @OrderBy("nroitm")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto", fetch = FetchType.LAZY)
    private List<AtributoDefecto> atributosPorDefecto;

    @OrderBy("nroitm")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto", fetch = FetchType.LAZY)
    private List<ProductoPresentacion> presentaciones;

    @Embedded
    private Auditoria auditoria;

    public Producto() {

        this.auditoria = new Auditoria();

        this.pesoMinimo = 0;
        this.pesosMaximo = 0;

        this.stockMinimo = BigDecimal.ZERO;
        this.stockMaximo = BigDecimal.ZERO;
        this.puntoDePedido = BigDecimal.ZERO;
        this.diasEntrega = 0;

        administraAtributo1 = "N";
        administraAtributo2 = "N";
        administraAtributo3 = "N";
        administraAtributo4 = "N";
        administraAtributo5 = "N";
        administraAtributo6 = "N";
        administraAtributo7 = "N";
        bienDeUso = "N";
        imprimeCantidad = "S";

        this.precioProduccion = BigDecimal.ZERO;
//        this.precioReferencia = BigDecimal.ZERO;
        this.precioReposicion = BigDecimal.ZERO;
        this.precioUltimaCompra = BigDecimal.ZERO;

//        this.fechaReferencia = new Date();
        this.fechaReposicion = new Date();
        this.fechaProduccion = new Date();
        this.fechaUltimaCompra = new Date();

        this.esKitVenta = "N";
        this.disponibleParaPickingFacturacion = "N";
        this.disponibleParaPickingCompras = "N";
        this.pidePrecioCosto = "N";
        this.pesable = "N";
        this.congelaPrecioEnFacturacion = "N";
        this.validaDuplicidad = "S";
        this.editaDescripcion = "N";

        this.utilidad = BigDecimal.ZERO;
        this.utilidad2 = BigDecimal.ZERO;
        this.utilidad3 = BigDecimal.ZERO;
        this.utilidad4 = BigDecimal.ZERO;
        this.utilidad5 = BigDecimal.ZERO;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPesosMaximo() {
        return pesosMaximo;
    }

    public void setPesosMaximo(double pesosMaximo) {
        this.pesosMaximo = pesosMaximo;
    }

    public double getPesoMinimo() {
        return pesoMinimo;
    }

    public void setPesoMinimo(double pesoMinimo) {
        this.pesoMinimo = pesoMinimo;
    }

    public UnidadMedida getUnidadDePeso() {
        return unidadDePeso;
    }

    public void setUnidadDePeso(UnidadMedida unidadDePeso) {
        this.unidadDePeso = unidadDePeso;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public UnidadMedida getUnidadDeMedida() {
        return unidadDeMedida;
    }

    public void setUnidadDeMedida(UnidadMedida unidadDeMedida) {
        this.unidadDeMedida = unidadDeMedida;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(BigDecimal stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public BigDecimal getStockMaximo() {
        return stockMaximo;
    }

    public void setStockMaximo(BigDecimal stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    public BigDecimal getPuntoDePedido() {
        return puntoDePedido;
    }

    public void setPuntoDePedido(BigDecimal puntoDePedido) {
        this.puntoDePedido = puntoDePedido;
    }

    public Short getDiasEntrega() {
        return diasEntrega;
    }

    public void setDiasEntrega(Short diasEntrega) {
        this.diasEntrega = diasEntrega;
    }
//
//    public BigDecimal getPrecioReferencia() {
//        return precioReferencia;
//    }
//
//    public void setPrecioReferencia(BigDecimal precioReferencia) {
//        this.precioReferencia = precioReferencia;
//    }
//
//    public Moneda getMonedaDeReferencia() {
//        return monedaDeReferencia;
//    }
//
//    public void setMonedaDeReferencia(Moneda monedaDeReferencia) {
//        this.monedaDeReferencia = monedaDeReferencia;
//    }
//
//    public Date getFechaReferencia() {
//        return fechaReferencia;
//    }
//
//    public void setFechaReferencia(Date fechaReferencia) {
//        this.fechaReferencia = fechaReferencia;
//    }

    public BigDecimal getPrecioReposicion() {
        return precioReposicion;
    }

    public void setPrecioReposicion(BigDecimal precioReposicion) {
        this.precioReposicion = precioReposicion;
    }

    public Moneda getMonedaReposicion() {
        return monedaReposicion;
    }

    public void setMonedaReposicion(Moneda monedaReposicion) {
        this.monedaReposicion = monedaReposicion;
    }

    public Date getFechaReposicion() {
        return fechaReposicion;
    }

    public void setFechaReposicion(Date fechaReposicion) {
        this.fechaReposicion = fechaReposicion;
    }

    public BigDecimal getPrecioUltimaCompra() {
        return precioUltimaCompra;
    }

    public void setPrecioUltimaCompra(BigDecimal precioUltimaCompra) {
        this.precioUltimaCompra = precioUltimaCompra;
    }

    public Moneda getMonedaUltimaCompra() {
        return monedaUltimaCompra;
    }

    public void setMonedaUltimaCompra(Moneda monedaUltimaCompra) {
        this.monedaUltimaCompra = monedaUltimaCompra;
    }

    public Date getFechaUltimaCompra() {
        return fechaUltimaCompra;
    }

    public void setFechaUltimaCompra(Date fechaUltimaCompra) {
        this.fechaUltimaCompra = fechaUltimaCompra;
    }

    public BigDecimal getPrecioProduccion() {
        return precioProduccion;
    }

    public void setPrecioProduccion(BigDecimal precioProduccion) {
        this.precioProduccion = precioProduccion;
    }

    public Moneda getMonedaDeProduccion() {
        return monedaDeProduccion;
    }

    public void setMonedaDeProduccion(Moneda monedaDeProduccion) {
        this.monedaDeProduccion = monedaDeProduccion;
    }

    public Date getFechaProduccion() {
        return fechaProduccion;
    }

    public void setFechaProduccion(Date fechaProduccion) {
        this.fechaProduccion = fechaProduccion;
    }

    public Rubro01 getRubr01() {
        return rubr01;
    }

    public void setRubr01(Rubro01 rubr01) {
        this.rubr01 = rubr01;
    }

    public Rubro02 getRubr02() {
        return rubr02;
    }

    public void setRubr02(Rubro02 rubr02) {
        this.rubr02 = rubr02;
    }

    public Rubro03 getRubr03() {
        return rubr03;
    }

    public void setRubr03(Rubro03 rubr03) {
        this.rubr03 = rubr03;
    }

    public Rubro04 getRubr04() {
        return rubr04;
    }

    public void setRubr04(Rubro04 rubr04) {
        this.rubr04 = rubr04;
    }

    public Rubro05 getRubr05() {
        return rubr05;
    }

    public void setRubr05(Rubro05 rubr05) {
        this.rubr05 = rubr05;
    }

    public Rubro06 getRubr06() {
        return rubr06;
    }

    public void setRubr06(Rubro06 rubr06) {
        this.rubr06 = rubr06;
    }

    public Rubro07 getRubr07() {
        return rubr07;
    }

    public void setRubr07(Rubro07 rubr07) {
        this.rubr07 = rubr07;
    }

    public Rubro08 getRubr08() {
        return rubr08;
    }

    public void setRubr08(Rubro08 rubr08) {
        this.rubr08 = rubr08;
    }

    public Rubro09 getRubr09() {
        return rubr09;
    }

    public void setRubr09(Rubro09 rubr09) {
        this.rubr09 = rubr09;
    }

    public String getAdministraAtributo1() {
        return administraAtributo1;
    }

    public void setAdministraAtributo1(String administraAtributo1) {
        this.administraAtributo1 = administraAtributo1;
    }

    public String getAdministraAtributo2() {
        return administraAtributo2;
    }

    public void setAdministraAtributo2(String administraAtributo2) {
        this.administraAtributo2 = administraAtributo2;
    }

    public String getAdministraAtributo3() {
        return administraAtributo3;
    }

    public void setAdministraAtributo3(String administraAtributo3) {
        this.administraAtributo3 = administraAtributo3;
    }

    public String getAdministraAtributo4() {
        return administraAtributo4;
    }

    public void setAdministraAtributo4(String administraAtributo4) {
        this.administraAtributo4 = administraAtributo4;
    }

    public String getAdministraAtributo5() {
        return administraAtributo5;
    }

    public void setAdministraAtributo5(String administraAtributo5) {
        this.administraAtributo5 = administraAtributo5;
    }

    public String getAdministraAtributo6() {
        return administraAtributo6;
    }

    public void setAdministraAtributo6(String administraAtributo6) {
        this.administraAtributo6 = administraAtributo6;
    }

    public String getAdministraAtributo7() {
        return administraAtributo7;
    }

    public void setAdministraAtributo7(String administraAtributo7) {
        this.administraAtributo7 = administraAtributo7;
    }

    public String getBienDeUso() {
        return bienDeUso;
    }

    public void setBienDeUso(String bienDeUso) {
        this.bienDeUso = bienDeUso;
    }

    public Concepto getConceptoVenta() {
        return conceptoVenta;
    }

    public void setConceptoVenta(Concepto conceptoVenta) {
        this.conceptoVenta = conceptoVenta;
    }

    public CuentaContable getCuentaContableVenta() {
        return cuentaContableVenta;
    }

    public void setCuentaContableVenta(CuentaContable cuentaContableVenta) {
        this.cuentaContableVenta = cuentaContableVenta;
    }

    public CuentaContable getCuentaContableCompra() {
        return cuentaContableCompra;
    }

    public void setCuentaContableCompra(CuentaContable cuentaContableCompra) {
        this.cuentaContableCompra = cuentaContableCompra;
    }

    public String getProductoNuevo() {
        return productoNuevo;
    }

    public void setProductoNuevo(String productoNuevo) {
        this.productoNuevo = productoNuevo;
    }

    public String getImagenChica() {
        return imagenChica;
    }

    public void setImagenChica(String imagenChica) {
        this.imagenChica = imagenChica;
    }

    public String getImagenGrande() {
        return imagenGrande;
    }

    public void setImagenGrande(String imagenGrande) {
        this.imagenGrande = imagenGrande;
    }

    public String getEsKitVenta() {
        return esKitVenta;
    }

    public void setEsKitVenta(String esKitVenta) {
        this.esKitVenta = esKitVenta;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public String getGestionaStock() {
        return gestionaStock;
    }

    public void setGestionaStock(String gestionaStock) {
        this.gestionaStock = gestionaStock;
    }

    public Concepto getConceptoProveedor() {
        return conceptoProveedor;
    }

    public void setConceptoProveedor(Concepto conceptoProveedor) {
        this.conceptoProveedor = conceptoProveedor;
    }

    public String getImprimeCantidad() {
        return imprimeCantidad;
    }

    public void setImprimeCantidad(String imprimeCantidad) {
        this.imprimeCantidad = imprimeCantidad;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public Formula getFormulaComposicionVenta() {
        return formulaComposicionVenta;
    }

    public void setFormulaComposicionVenta(Formula fcv) {
        this.formulaComposicionVenta = fcv;
    }

    public BigDecimal getUtilidad() {
        return utilidad;
    }

    public void setUtilidad(BigDecimal utilidad) {
        this.utilidad = utilidad;
    }

    public EntidadComercial getProveedorHabitual() {
        return proveedorHabitual;
    }

    public void setProveedorHabitual(EntidadComercial proveedorHabitual) {
        this.proveedorHabitual = proveedorHabitual;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getNumeroParte() {
        return numeroParte;
    }

    public void setNumeroParte(String numeroParte) {
        this.numeroParte = numeroParte;
    }

    public String getCodigoReferencia() {
        return codigoReferencia;
    }

    public void setCodigoReferencia(String codigoReferencia) {
        this.codigoReferencia = codigoReferencia;
    }

    public double getPesoNeto() {
        return pesoNeto;
    }

    public void setPesoNeto(double pesoNeto) {
        this.pesoNeto = pesoNeto;
    }

    public double getVolumen() {
        return volumen;
    }

    public void setVolumen(double volumen) {
        this.volumen = volumen;
    }

    public int getGarantia() {
        return garantia;
    }

    public void setGarantia(int garantia) {
        this.garantia = garantia;
    }

    public UnidadMedida getUnidadDeGarantia() {
        return unidadDeGarantia;
    }

    public void setUnidadDeGarantia(UnidadMedida unidadDeGarantia) {
        this.unidadDeGarantia = unidadDeGarantia;
    }

    public List<EquivalenciaProveedor> getEquivalenciaProveedor() {
        return equivalenciaProveedor;
    }

    public void setEquivalenciaProveedor(List<EquivalenciaProveedor> equivalenciaProveedor) {
        this.equivalenciaProveedor = equivalenciaProveedor;
    }

    public String getDescripcionAlternativa() {
        return descripcionAlternativa;
    }

    public void setDescripcionAlternativa(String descripcionAlternativa) {
        this.descripcionAlternativa = descripcionAlternativa;
    }

    public List<AplicacionProducto> getAplicaciones() {
        return aplicaciones;
    }

    public void setAplicaciones(List<AplicacionProducto> aplicaciones) {
        this.aplicaciones = aplicaciones;
    }

    public List<ProductoSugerido> getSugeridos() {
        return sugeridos;
    }

    public void setSugeridos(List<ProductoSugerido> sugeridos) {
        this.sugeridos = sugeridos;
    }

    public List<ProductoSustituto> getSustitutos() {
        return sustitutos;
    }

    public void setSustitutos(List<ProductoSustituto> sustitutos) {
        this.sustitutos = sustitutos;
    }

    public List<AtributoProducto> getAtributos() {
        return atributos;
    }

    public void setAtributos(List<AtributoProducto> atributos) {
        this.atributos = atributos;
    }

    public String getDisponibleParaPickingFacturacion() {
        return disponibleParaPickingFacturacion;
    }

    public void setDisponibleParaPickingFacturacion(String disponibleParaPickingFacturacion) {
        this.disponibleParaPickingFacturacion = disponibleParaPickingFacturacion;
    }

    public String getDisponibleParaPickingCompras() {
        return disponibleParaPickingCompras;
    }

    public void setDisponibleParaPickingCompras(String disponibleParaPickingCompras) {
        this.disponibleParaPickingCompras = disponibleParaPickingCompras;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public Sector getSectorProduccion() {
        return sectorProduccion;
    }

    public void setSectorProduccion(Sector sectorProduccion) {
        this.sectorProduccion = sectorProduccion;
    }

    public List<GrupoProducto> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<GrupoProducto> grupos) {
        this.grupos = grupos;
    }

    public List<ImagenProducto> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ImagenProducto> imagenes) {
        this.imagenes = imagenes;
    }

    public BigDecimal getUtilidad2() {
        return utilidad2;
    }

    public void setUtilidad2(BigDecimal utilidad2) {
        this.utilidad2 = utilidad2;
    }

    public BigDecimal getUtilidad3() {
        return utilidad3;
    }

    public void setUtilidad3(BigDecimal utilidad3) {
        this.utilidad3 = utilidad3;
    }

    public String getPidePrecioCosto() {
        return pidePrecioCosto;
    }

    public void setPidePrecioCosto(String pidePrecioCosto) {
        this.pidePrecioCosto = pidePrecioCosto;
    }

    public String getCodigoProveedor() {
        return codigoProveedor;
    }

    public void setCodigoProveedor(String codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }

    public BigDecimal getUtilidad4() {
        return utilidad4;
    }

    public void setUtilidad4(BigDecimal utilidad4) {
        this.utilidad4 = utilidad4;
    }

    public BigDecimal getUtilidad5() {
        return utilidad5;
    }

    public void setUtilidad5(BigDecimal utilidad5) {
        this.utilidad5 = utilidad5;
    }

    public List<AtributoDefecto> getAtributosPorDefecto() {
        return atributosPorDefecto;
    }

    public void setAtributosPorDefecto(List<AtributoDefecto> atributosPorDefecto) {
        this.atributosPorDefecto = atributosPorDefecto;
    }

    public List<ProductoPresentacion> getPresentaciones() {
        return presentaciones;
    }

    public void setPresentaciones(List<ProductoPresentacion> presentaciones) {
        this.presentaciones = presentaciones;
    }

    public String getPesable() {
        return pesable;
    }

    public void setPesable(String pesable) {
        this.pesable = pesable;
    }

    public String getCongelaPrecioEnFacturacion() {
        return congelaPrecioEnFacturacion;
    }

    public void setCongelaPrecioEnFacturacion(String congelaPrecioEnFacturacion) {
        this.congelaPrecioEnFacturacion = congelaPrecioEnFacturacion;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        return hash;
    }

    public String getValidaDuplicidad() {
        return validaDuplicidad;
    }

    public void setValidaDuplicidad(String validaDuplicidad) {
        this.validaDuplicidad = validaDuplicidad;
    }

    public String getEditaDescripcion() {
        return editaDescripcion;
    }

    public void setEditaDescripcion(String editaDescripcion) {
        this.editaDescripcion = editaDescripcion;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Producto other = (Producto) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Producto{" + "codigo=" + codigo + "}";
    }

}
