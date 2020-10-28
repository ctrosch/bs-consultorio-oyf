/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bs.contrato.modelo;

import bs.contabilidad.modelo.CuentaContable;
import bs.facturacion.modelo.ItemMovimientoFacturacion;
import bs.global.auditoria.AuditoriaListener;
import bs.global.modelo.Auditoria;
import bs.global.modelo.Concepto;
import bs.global.modelo.Moneda;
import bs.stock.modelo.Producto;
import bs.stock.modelo.UnidadMedida;
import bs.ventas.modelo.CondicionDePagoVenta;
import bs.ventas.modelo.ListaPrecioVenta;
import bs.ventas.modelo.Vendedor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 *
 * @author ctrosch
 */
//@Entity
//@Table(name = "cv_liquidacion_item")
@EntityListeners(AuditoriaListener.class)
public abstract class ItemMovimientoLiquidacion implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Column(name = "ID_IAPL", nullable = true)
    private Integer idItemAplicacion;    
    
    @Column(name = "NROITM", nullable = false)
    private int nroitm;    
        
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MCAB", referencedColumnName = "ID", nullable = false)        
    private MovimientoLiquidacion movimiento;
       
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MAPL", referencedColumnName = "ID", nullable = false)        
    private MovimientoLiquidacion movimientoAplicacion;
    
    @Column(name = "ITMCON", nullable = false)
    private int itemContrato; 
        
    @Column(name = "DEBHAB", length = 1)
    private String debeHaber;
    
    //Nro cta cliente    
    @Column(name = "NROCTA", length = 13)
    private String cliente;
    
    @JoinColumns({
        @JoinColumn(name = "MODCPT", referencedColumnName = "MODULO", nullable = false),
        @JoinColumn(name = "TIPCPT", referencedColumnName = "TIPCPT", nullable = false),
        @JoinColumn(name = "CODCPT", referencedColumnName = "CODIGO", nullable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Concepto concepto;
        
    @JoinColumn(name = "CUENTA", referencedColumnName = "NROCTA", nullable = false)    
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaContable cuentaContable;
        
    @JoinColumn(name = "ARTCOD", referencedColumnName = "CODIGO", nullable = false)    
    @ManyToOne(fetch = FetchType.LAZY)
    private Producto producto;
    
    @Column(name = "DESCRP", length = 200)
    private String descripcion;
    
    @JoinColumn(name = "CNDPAG", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CondicionDePagoVenta condicionDePago;
        
    @JoinColumn(name = "VNDDOR", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Vendedor vendedor;
        
    @JoinColumn(name = "CODLIS", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ListaPrecioVenta listaDePrecio;
    
    @Column(name = "CANTID", precision = 15, scale = 4)    
    private BigDecimal cantidad;
        
    @JoinColumn(name = "UNIMED", referencedColumnName = "CODIGO", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadMedida unidadMedida;
    
    @Column(name = "PRECIO", precision = 15, scale = 4)
    private BigDecimal precio;   
    @Column(name = "PRESEC", precision = 15, scale = 4)
    private BigDecimal precioSecundario;
    
    @Column(name = "PRCIMP", precision = 15, scale = 4)
    private BigDecimal precioConImpuesto;   
    @Column(name = "PRCIMS", precision = 15, scale = 4)
    private BigDecimal precioConImpuestoSecundario;
    
    @Column(name = "TOTLIN", precision = 15, scale = 4)
    private BigDecimal totalLinea;    
    @Column(name = "TOTSEC", precision = 15, scale = 4)
    private BigDecimal totalLineaSecundario;
        
    @Column(name = "TOTINC", precision = 15, scale = 4)
    private BigDecimal totalLineaConImpuesto;    
    @Column(name = "TOTINS", precision = 15, scale = 4)
    private BigDecimal totalLineaConImpuestoSecundario;
    
    @Column(name = "IMPDEB", precision = 15, scale = 4)
    private BigDecimal importeDebe;
    @Column(name = "IMPHAB", precision = 15, scale = 4)
    private BigDecimal importeHaber;
    
    @Column(name = "DEBSEC", precision = 15, scale = 4)
    private BigDecimal importeDebeSecundario;
    @Column(name = "HABSEC", precision = 15, scale = 4)
    private BigDecimal importeHaberSecundario;
        
    @Column(name = "TASA01", precision = 15, scale = 4)
    private BigDecimal tasaImpositiva01;
    
    @Column(name = "TASA02", precision = 15, scale = 4)
    private BigDecimal tasaImpositiva02;
    
    @Column(name = "TASA03", precision = 15, scale = 4)
    private BigDecimal tasaImpositiva03;
    
    @Column(name = "TASA04", precision = 15, scale = 4)
    private BigDecimal tasaImpositiva04;
    
    @Column(name = "TASA05", precision = 15, scale = 4)
    private BigDecimal tasaImpositiva05;
    
    @Column(name = "IMPU01", precision = 15, scale = 4)
    private BigDecimal impuesto01;
    
    @Column(name = "IMPU02", precision = 15, scale = 4)
    private BigDecimal impuesto02;
    
    @Column(name = "IMPU03", precision = 15, scale = 4)
    private BigDecimal impuesto03;
    
    @Column(name = "IMPU04", precision = 15, scale = 4)
    private BigDecimal impuesto04;
    
    @Column(name = "IMPU05", precision = 15, scale = 4)
    private BigDecimal impuesto05;
        
    //Numero de Serie
    @Column(name = "NSERIE", length = 30)
    private String nserie;
    //Numero de despacho
    @Column(name = "NDESPA", length = 30)
    private String ndespa;
    //Numero de envase
    @Column(name = "ENVASE", length = 30)
    private String envase;
    //Numero otros
    @Column(name = "NOTROS", length = 30)
    private String notros;
    //Numero de atributo
    @Column(name = "NATRIB", length = 30)
    private String natrib;
    //Numero de estante
    @Column(name = "NESTAN", length = 30)
    private String nestan;
    //Numero de ubicacion
    @Column(name = "NUBICA", length = 30)
    private String nubica;
       
    @JoinColumn(name = "COFLIS", referencedColumnName = "CODIGO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Moneda monedaListaPrecio;
    
    @Column(name = "COTIZA", precision = 15, scale = 4)
    private BigDecimal cotizacion;
   
    @Column(name = "PCTBF1", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion1;
    @Column(name = "PCTBF2", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion2;
    @Column(name = "PCTBF3", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion3;
    @Column(name = "PCTBF4", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion4;
    @Column(name = "PCTBF5", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion5;
    @Column(name = "PCTBF6", precision = 15, scale = 4)
    private BigDecimal porcentajeBonificacion6;    
    @Column(name = "PCTBFN", precision = 15, scale = 4)
    private BigDecimal porcentaTotalBonificacion;
    
    @Column(name = "CNTBON", precision = 15, scale = 4)
    private BigDecimal cantidadBonificada;
   
    @Lob
    @Column(name = "OBSERVA", length = 2147483647)
    private String observaciones;
       
    @Embedded
    private Auditoria auditoria;
    
    @Transient
    private List<ItemMovimientoFacturacion> itemsAplicacion;
    
    @Transient    
    private boolean todoOk;
    

    public ItemMovimientoLiquidacion() {
           
        precio = BigDecimal.ZERO;
        precioSecundario = BigDecimal.ZERO;
        cantidad = BigDecimal.ZERO;                
        cantidadBonificada = BigDecimal.ZERO; 
        
        totalLinea = BigDecimal.ZERO;
        totalLineaSecundario = BigDecimal.ZERO;
        
        
        porcentajeBonificacion1 = BigDecimal.ZERO;
        porcentajeBonificacion2 = BigDecimal.ZERO;
        porcentajeBonificacion3 = BigDecimal.ZERO;
        porcentajeBonificacion4 = BigDecimal.ZERO;
        porcentajeBonificacion5 = BigDecimal.ZERO;
        porcentajeBonificacion6 = BigDecimal.ZERO;
        porcentaTotalBonificacion = BigDecimal.ZERO;
        
        cotizacion = BigDecimal.ONE;        
        auditoria = new Auditoria();

        
    }

    public ItemMovimientoLiquidacion(Producto producto) {

        precio = BigDecimal.ZERO;
        precioSecundario = BigDecimal.ZERO;
        cantidad = BigDecimal.ZERO;                
        cantidadBonificada = BigDecimal.ZERO;   
        
        totalLinea = BigDecimal.ZERO;
        totalLineaSecundario = BigDecimal.ZERO;
        
        porcentajeBonificacion1 = BigDecimal.ZERO;
        porcentajeBonificacion2 = BigDecimal.ZERO;
        porcentajeBonificacion3 = BigDecimal.ZERO;
        porcentajeBonificacion4 = BigDecimal.ZERO;
        porcentajeBonificacion5 = BigDecimal.ZERO;
        porcentajeBonificacion6 = BigDecimal.ZERO;
        porcentaTotalBonificacion = BigDecimal.ZERO;
        
        cotizacion = BigDecimal.ONE;        
        auditoria = new Auditoria();
        this.producto = producto;
        
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getEnvase() {
        return envase;
    }

    public Integer getIdItemAplicacion() {
        return idItemAplicacion;
    }

    public void setIdItemAplicacion(Integer idItemAplicacion) {
        this.idItemAplicacion = idItemAplicacion;
    }

    public void setEnvase(String envase) {
        this.envase = envase;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public BigDecimal getPrecioSecundario() {
        return precioSecundario;
    }

    public void setPrecioSecundario(BigDecimal precioSecundario) {
        this.precioSecundario = precioSecundario;
    }

    public BigDecimal getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(BigDecimal cotizacion) {
        this.cotizacion = cotizacion;
    }
    
    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

   
    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
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

    public BigDecimal getPorcentaTotalBonificacion() {
        return porcentaTotalBonificacion;
    }

    public void setPorcentaTotalBonificacion(BigDecimal porcentaTotalBonificacion) {
        this.porcentaTotalBonificacion = porcentaTotalBonificacion;
    }

    public BigDecimal getCantidadBonificada() {
        return cantidadBonificada;
    }

    public void setCantidadBonificada(BigDecimal cantidadBonificada) {
        this.cantidadBonificada = cantidadBonificada;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public MovimientoLiquidacion getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoLiquidacion movimiento) {
        this.movimiento = movimiento;
    }

    public String getNatrib() {
        return natrib;
    }

    public void setNatrib(String natrib) {
        this.natrib = natrib;
    }

    public String getNdespa() {
        return ndespa;
    }

    public void setNdespa(String ndespa) {
        this.ndespa = ndespa;
    }

    public String getNestan() {
        return nestan;
    }

    public void setNestan(String nestan) {
        this.nestan = nestan;
    }

    public String getNotros() {
        return notros;
    }

    public void setNotros(String notros) {
        this.notros = notros;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
    
    public String getNserie() {
        return nserie;
    }

    public void setNserie(String nserie) {
        this.nserie = nserie;
    }

    public String getNubica() {
        return nubica;
    }

    public void setNubica(String nubica) {
        this.nubica = nubica;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public Moneda getMonedaListaPrecio() {
        return monedaListaPrecio;
    }

    public void setMonedaListaPrecio(Moneda monedaListaPrecio) {
        this.monedaListaPrecio = monedaListaPrecio;
    }
   
    public MovimientoLiquidacion getMovimientoAplicacion() {
        return movimientoAplicacion;
    }

    public void setMovimientoAplicacion(MovimientoLiquidacion movimientoAplicacion) {
        this.movimientoAplicacion = movimientoAplicacion;
    }

    public int getNroitm() {
        return nroitm;
    }

    public void setNroitm(int nroitm) {
        this.nroitm = nroitm;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }
    
    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public BigDecimal getTotalLinea() {
        return totalLinea;
    }

    public void setTotalLinea(BigDecimal totalLinea) {
        this.totalLinea = totalLinea;
    }

    public boolean isTodoOk() {
        
        
        if(id!=null){
            todoOk = true;
        }
        
        return todoOk;
    }

    public void setTodoOk(boolean todoOk) {
        this.todoOk = todoOk;
    }

    public BigDecimal getTotalLineaSecundario() {
        return totalLineaSecundario;
    }

    public void setTotalLineaSecundario(BigDecimal totalLineaSecundario) {
        this.totalLineaSecundario = totalLineaSecundario;
    }   
    
    public List<ItemMovimientoFacturacion> getItemsAplicacion() {
        return itemsAplicacion;
    }

    public void setItemsAplicacion(List<ItemMovimientoFacturacion> itemsAplicacion) {
        this.itemsAplicacion = itemsAplicacion;
    }

    public int getItemContrato() {
        return itemContrato;
    }

    public void setItemContrato(int itemContrato) {
        this.itemContrato = itemContrato;
    }

    public String getDebeHaber() {
        return debeHaber;
    }

    public void setDebeHaber(String debeHaber) {
        this.debeHaber = debeHaber;
    }

    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
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

    public ListaPrecioVenta getListaDePrecio() {
        return listaDePrecio;
    }

    public void setListaDePrecio(ListaPrecioVenta listaDePrecio) {
        this.listaDePrecio = listaDePrecio;
    }

    public BigDecimal getPrecioConImpuesto() {
        return precioConImpuesto;
    }

    public void setPrecioConImpuesto(BigDecimal precioConImpuesto) {
        this.precioConImpuesto = precioConImpuesto;
    }

    public BigDecimal getPrecioConImpuestoSecundario() {
        return precioConImpuestoSecundario;
    }

    public void setPrecioConImpuestoSecundario(BigDecimal precioConImpuestoSecundario) {
        this.precioConImpuestoSecundario = precioConImpuestoSecundario;
    }

    public BigDecimal getTotalLineaConImpuesto() {
        return totalLineaConImpuesto;
    }

    public void setTotalLineaConImpuesto(BigDecimal totalLineaConImpuesto) {
        this.totalLineaConImpuesto = totalLineaConImpuesto;
    }

    public BigDecimal getTotalLineaConImpuestoSecundario() {
        return totalLineaConImpuestoSecundario;
    }

    public void setTotalLineaConImpuestoSecundario(BigDecimal totalLineaConImpuestoSecundario) {
        this.totalLineaConImpuestoSecundario = totalLineaConImpuestoSecundario;
    }

    public BigDecimal getImporteDebe() {
        return importeDebe;
    }

    public void setImporteDebe(BigDecimal importeDebe) {
        this.importeDebe = importeDebe;
    }

    public BigDecimal getImporteHaber() {
        return importeHaber;
    }

    public void setImporteHaber(BigDecimal importeHaber) {
        this.importeHaber = importeHaber;
    }

    public BigDecimal getImporteDebeSecundario() {
        return importeDebeSecundario;
    }

    public void setImporteDebeSecundario(BigDecimal importeDebeSecundario) {
        this.importeDebeSecundario = importeDebeSecundario;
    }

    public BigDecimal getImporteHaberSecundario() {
        return importeHaberSecundario;
    }

    public void setImporteHaberSecundario(BigDecimal importeHaberSecundario) {
        this.importeHaberSecundario = importeHaberSecundario;
    }

    public BigDecimal getTasaImpositiva01() {
        return tasaImpositiva01;
    }

    public void setTasaImpositiva01(BigDecimal tasaImpositiva01) {
        this.tasaImpositiva01 = tasaImpositiva01;
    }

    public BigDecimal getTasaImpositiva02() {
        return tasaImpositiva02;
    }

    public void setTasaImpositiva02(BigDecimal tasaImpositiva02) {
        this.tasaImpositiva02 = tasaImpositiva02;
    }

    public BigDecimal getTasaImpositiva03() {
        return tasaImpositiva03;
    }

    public void setTasaImpositiva03(BigDecimal tasaImpositiva03) {
        this.tasaImpositiva03 = tasaImpositiva03;
    }

    public BigDecimal getTasaImpositiva04() {
        return tasaImpositiva04;
    }

    public void setTasaImpositiva04(BigDecimal tasaImpositiva04) {
        this.tasaImpositiva04 = tasaImpositiva04;
    }

    public BigDecimal getTasaImpositiva05() {
        return tasaImpositiva05;
    }

    public void setTasaImpositiva05(BigDecimal tasaImpositiva05) {
        this.tasaImpositiva05 = tasaImpositiva05;
    }

    public BigDecimal getImpuesto01() {
        return impuesto01;
    }

    public void setImpuesto01(BigDecimal impuesto01) {
        this.impuesto01 = impuesto01;
    }

    public BigDecimal getImpuesto02() {
        return impuesto02;
    }

    public void setImpuesto02(BigDecimal impuesto02) {
        this.impuesto02 = impuesto02;
    }

    public BigDecimal getImpuesto03() {
        return impuesto03;
    }

    public void setImpuesto03(BigDecimal impuesto03) {
        this.impuesto03 = impuesto03;
    }

    public BigDecimal getImpuesto04() {
        return impuesto04;
    }

    public void setImpuesto04(BigDecimal impuesto04) {
        this.impuesto04 = impuesto04;
    }

    public BigDecimal getImpuesto05() {
        return impuesto05;
    }

    public void setImpuesto05(BigDecimal impuesto05) {
        this.impuesto05 = impuesto05;
    }
    
    
        
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final ItemMovimientoLiquidacion other = (ItemMovimientoLiquidacion) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "ItemMovimientoLiquidacion{" + "id=" + this.id  + '}';
    }
}
