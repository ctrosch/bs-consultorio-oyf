/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bs.stock.modelo;

import bs.contabilidad.modelo.CuentaContable;
import bs.global.auditoria.AuditoriaListener;
import bs.global.auditoria.IAuditableEntity;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Concepto;
import bs.global.modelo.Moneda;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Claudio
 */
@Entity
@Table(name = "st_parametro")
@EntityListeners(AuditoriaListener.class)
public class ParametroStock implements Serializable, IAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private String id;

    @JoinColumn(name = "TIPPRO", referencedColumnName = "TIPPRO")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoProducto tipoProducto;

    //Unidad de medida
    @JoinColumn(name = "UNIMED", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadDeMedida;

    @Column(name = "STOCKS", length = 1)
    private String gestionaStock;

    @Column(name = "IMPCNT", length = 1)
    private String imprimeCantidad;

    @Column(name = "METGST", length = 1)
    private String metodoGestionStock;

    //Cuenta contable de venta
    @JoinColumn(name = "CUENVT", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContableVenta;

    //Cuenta contable de venta
    @JoinColumn(name = "CUENPV", referencedColumnName = "NROCTA")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContableCompra;

    @Column(name = "ADATR1", length = 1)
    private String administraAtributo1;
    @Column(name = "ADATR2", length = 1)
    private String administraAtributo2;
    @Column(name = "ADATR3", length = 1)
    private String administraAtributo3;
    @Column(name = "ADATR4", length = 1)
    private String administraAtributo4;
    @Column(name = "ADATR5", length = 1)
    private String administraAtributo5;
    @Column(name = "ADATR6", length = 1)
    private String administraAtributo6;
    @Column(name = "ADATR7", length = 1)
    private String administraAtributo7;

    @Column(name = "DESAT1", length = 80)
    private String descripcionAtributo1;
    @Column(name = "DESAT2", length = 80)
    private String descripcionAtributo2;
    @Column(name = "DESAT3", length = 80)
    private String descripcionAtributo3;
    @Column(name = "DESAT4", length = 80)
    private String descripcionAtributo4;
    @Column(name = "DESAT5", length = 80)
    private String descripcionAtributo5;
    @Column(name = "DESAT6", length = 80)
    private String descripcionAtributo6;
    @Column(name = "DESAT7", length = 80)
    private String descripcionAtributo7;

    @Column(name = "DESAP1", length = 80)
    private String descripcionAplicacion1;
    @Column(name = "DESAP2", length = 80)
    private String descripcionAplicacion2;
    @Column(name = "DESAP3", length = 80)
    private String descripcionAplicacion3;
    @Column(name = "DESAP4", length = 80)
    private String descripcionAplicacion4;
    @Column(name = "DESAP5", length = 80)
    private String descripcionAplicacion5;
    @Column(name = "DESAP6", length = 80)
    private String descripcionAplicacion6;
    @Column(name = "DESAP7", length = 80)
    private String descripcionAplicacion7;
    @Column(name = "DESAP8", length = 80)
    private String descripcionAplicacion8;
    @Column(name = "DESAP9", length = 80)
    private String descripcionAplicacion9;
    @Column(name = "DESAP10", length = 80)
    private String descripcionAplicacion10;
    @Column(name = "DESAP11", length = 80)
    private String descripcionAplicacion11;
    @Column(name = "DESAP12", length = 80)
    private String descripcionAplicacion12;

    @Column(name = "CODMAN")
    private String codigoManual;
    @Column(name = "GCCANC")
    private Integer caracteresParaGeneracionCodigo;
    @Column(name = "GCTIPP", length = 1)
    private String utilizaTipoProductoGeneracionCodigo;
    @Column(name = "GCRUB1", length = 1)
    private String utilizaRubro1GeneracionCodigo;
    @Column(name = "GCRUB2", length = 1)
    private String utilizaRubro2GeneracionCodigo;
    @Column(name = "GCRUB3", length = 1)
    private String utilizaRubro3GeneracionCodigo;

    @Column(name = "DIAENT")
    private Short diasEntrega;
    @Column(name = "KITSFC", length = 1)
    private String esKitVenta;
    @Column(name = "IMGCHI", length = 40)
    private String imagenChica;
    @Column(name = "IMGGRA", length = 40)
    private String imagenGrande;
    @Column(name = "PNUEVO", length = 1)
    private String productoNuevo;
    @Column(name = "PTOPED", precision = 15, scale = 4)
    private BigDecimal puntoDePedido;
    @Column(name = "STKMIN", precision = 15, scale = 4)
    private BigDecimal stockMinimo;
    @Column(name = "STKMAX", precision = 15, scale = 4)
    private BigDecimal stockMaximo;
    @JoinColumn(name = "UNIPES", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadDePeso;

    @Column(name = "GARAN")
    private int garantia;

    @JoinColumn(name = "UNIGAR", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadDeGarantia;

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
    private Concepto conceptoCompra;
    @JoinColumn(name = "MONPRD", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaDeProduccion;
    @JoinColumn(name = "MONREP", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaReposicion;
    @JoinColumn(name = "MONUCO", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaUltimaCompra;
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

    @Embedded
    private Auditoria auditoria;

    public ParametroStock() {

        this.auditoria = new Auditoria();

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
        imprimeCantidad = "S";
        metodoGestionStock = "F"; // FIFO o LIFO

        this.esKitVenta = "N";
    }

    public ParametroStock(String id) {

        this.id = id;
        this.auditoria = new Auditoria();

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
        imprimeCantidad = "S";
        metodoGestionStock = "F"; // FIFO o LIFO

        this.esKitVenta = "N";
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public UnidadMedida getUnidadDePeso() {
        return unidadDePeso;
    }

    public void setUnidadDePeso(UnidadMedida unidadDePeso) {
        this.unidadDePeso = unidadDePeso;
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

    public Moneda getMonedaReposicion() {
        return monedaReposicion;
    }

    public void setMonedaReposicion(Moneda monedaReposicion) {
        this.monedaReposicion = monedaReposicion;
    }

    public Moneda getMonedaUltimaCompra() {
        return monedaUltimaCompra;
    }

    public void setMonedaUltimaCompra(Moneda monedaUltimaCompra) {
        this.monedaUltimaCompra = monedaUltimaCompra;
    }

    public Moneda getMonedaDeProduccion() {
        return monedaDeProduccion;
    }

    public void setMonedaDeProduccion(Moneda monedaDeProduccion) {
        this.monedaDeProduccion = monedaDeProduccion;
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

    public Concepto getConceptoVenta() {
        return conceptoVenta;
    }

    public void setConceptoVenta(Concepto conceptoVenta) {
        this.conceptoVenta = conceptoVenta;
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

    public String getImprimeCantidad() {
        return imprimeCantidad;
    }

    public void setImprimeCantidad(String imprimeCantidad) {
        this.imprimeCantidad = imprimeCantidad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDescripcionAtributo1() {
        return descripcionAtributo1;
    }

    public void setDescripcionAtributo1(String descripcionAtributo1) {
        this.descripcionAtributo1 = descripcionAtributo1;
    }

    public String getDescripcionAtributo2() {
        return descripcionAtributo2;
    }

    public void setDescripcionAtributo2(String descripcionAtributo2) {
        this.descripcionAtributo2 = descripcionAtributo2;
    }

    public String getDescripcionAtributo3() {
        return descripcionAtributo3;
    }

    public void setDescripcionAtributo3(String descripcionAtributo3) {
        this.descripcionAtributo3 = descripcionAtributo3;
    }

    public String getDescripcionAtributo4() {
        return descripcionAtributo4;
    }

    public void setDescripcionAtributo4(String descripcionAtributo4) {
        this.descripcionAtributo4 = descripcionAtributo4;
    }

    public String getDescripcionAtributo5() {
        return descripcionAtributo5;
    }

    public void setDescripcionAtributo5(String descripcionAtributo5) {
        this.descripcionAtributo5 = descripcionAtributo5;
    }

    public String getDescripcionAtributo6() {
        return descripcionAtributo6;
    }

    public void setDescripcionAtributo6(String descripcionAtributo6) {
        this.descripcionAtributo6 = descripcionAtributo6;
    }

    public String getDescripcionAtributo7() {
        return descripcionAtributo7;
    }

    public void setDescripcionAtributo7(String descripcionAtributo7) {
        this.descripcionAtributo7 = descripcionAtributo7;
    }

    public Concepto getConceptoCompra() {
        return conceptoCompra;
    }

    public void setConceptoCompra(Concepto conceptoCompra) {
        this.conceptoCompra = conceptoCompra;
    }

    public String getDescripcionAplicacion1() {
        return descripcionAplicacion1;
    }

    public void setDescripcionAplicacion1(String descripcionAplicacion1) {
        this.descripcionAplicacion1 = descripcionAplicacion1;
    }

    public String getDescripcionAplicacion2() {
        return descripcionAplicacion2;
    }

    public void setDescripcionAplicacion2(String descripcionAplicacion2) {
        this.descripcionAplicacion2 = descripcionAplicacion2;
    }

    public String getDescripcionAplicacion3() {
        return descripcionAplicacion3;
    }

    public void setDescripcionAplicacion3(String descripcionAplicacion3) {
        this.descripcionAplicacion3 = descripcionAplicacion3;
    }

    public String getDescripcionAplicacion4() {
        return descripcionAplicacion4;
    }

    public void setDescripcionAplicacion4(String descripcionAplicacion4) {
        this.descripcionAplicacion4 = descripcionAplicacion4;
    }

    public String getDescripcionAplicacion5() {
        return descripcionAplicacion5;
    }

    public void setDescripcionAplicacion5(String descripcionAplicacion5) {
        this.descripcionAplicacion5 = descripcionAplicacion5;
    }

    public String getDescripcionAplicacion6() {
        return descripcionAplicacion6;
    }

    public void setDescripcionAplicacion6(String descripcionAplicacion6) {
        this.descripcionAplicacion6 = descripcionAplicacion6;
    }

    public String getDescripcionAplicacion7() {
        return descripcionAplicacion7;
    }

    public void setDescripcionAplicacion7(String descripcionAplicacion7) {
        this.descripcionAplicacion7 = descripcionAplicacion7;
    }

    public String getDescripcionAplicacion8() {
        return descripcionAplicacion8;
    }

    public void setDescripcionAplicacion8(String descripcionAplicacion8) {
        this.descripcionAplicacion8 = descripcionAplicacion8;
    }

    public String getDescripcionAplicacion9() {
        return descripcionAplicacion9;
    }

    public void setDescripcionAplicacion9(String descripcionAplicacion9) {
        this.descripcionAplicacion9 = descripcionAplicacion9;
    }

    public String getDescripcionAplicacion10() {
        return descripcionAplicacion10;
    }

    public void setDescripcionAplicacion10(String descripcionAplicacion10) {
        this.descripcionAplicacion10 = descripcionAplicacion10;
    }

    public String getDescripcionAplicacion11() {
        return descripcionAplicacion11;
    }

    public void setDescripcionAplicacion11(String descripcionAplicacion11) {
        this.descripcionAplicacion11 = descripcionAplicacion11;
    }

    public String getDescripcionAplicacion12() {
        return descripcionAplicacion12;
    }

    public void setDescripcionAplicacion12(String descripcionAplicacion12) {
        this.descripcionAplicacion12 = descripcionAplicacion12;
    }

    public Integer getCaracteresParaGeneracionCodigo() {
        return caracteresParaGeneracionCodigo;
    }

    public void setCaracteresParaGeneracionCodigo(Integer caracteresParaGeneracionCodigo) {
        this.caracteresParaGeneracionCodigo = caracteresParaGeneracionCodigo;
    }

    public String getUtilizaTipoProductoGeneracionCodigo() {
        return utilizaTipoProductoGeneracionCodigo;
    }

    public void setUtilizaTipoProductoGeneracionCodigo(String utilizaTipoProductoGeneracionCodigo) {
        this.utilizaTipoProductoGeneracionCodigo = utilizaTipoProductoGeneracionCodigo;
    }

    public String getUtilizaRubro1GeneracionCodigo() {
        return utilizaRubro1GeneracionCodigo;
    }

    public void setUtilizaRubro1GeneracionCodigo(String utilizaRubro1GeneracionCodigo) {
        this.utilizaRubro1GeneracionCodigo = utilizaRubro1GeneracionCodigo;
    }

    public String getUtilizaRubro2GeneracionCodigo() {
        return utilizaRubro2GeneracionCodigo;
    }

    public void setUtilizaRubro2GeneracionCodigo(String utilizaRubro2GeneracionCodigo) {
        this.utilizaRubro2GeneracionCodigo = utilizaRubro2GeneracionCodigo;
    }

    public String getUtilizaRubro3GeneracionCodigo() {
        return utilizaRubro3GeneracionCodigo;
    }

    public void setUtilizaRubro3GeneracionCodigo(String utilizaRubro3GeneracionCodigo) {
        this.utilizaRubro3GeneracionCodigo = utilizaRubro3GeneracionCodigo;
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

    public String getBienDeUso() {
        return bienDeUso;
    }

    public void setBienDeUso(String bienDeUso) {
        this.bienDeUso = bienDeUso;
    }

    public String getCodigoManual() {
        return codigoManual;
    }

    public void setCodigoManual(String codigoManual) {
        this.codigoManual = codigoManual;
    }

    public String getMetodoGestionStock() {
        return metodoGestionStock;
    }

    public void setMetodoGestionStock(String metodoGestionStock) {
        this.metodoGestionStock = metodoGestionStock;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final ParametroStock other = (ParametroStock) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Producto{" + "id=" + id + "}";
    }

}
